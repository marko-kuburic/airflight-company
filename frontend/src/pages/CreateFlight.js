import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { DispatcherLayout } from '../components/Layout/DispatcherLayout';
import { DispatcherSidebar } from '../components/Layout/DispatcherSidebar';
import { routeAPI, aircraftAPI, maintenanceAPI, flightAPI, segmentAPI } from '../services/api';
import toast from 'react-hot-toast';

export default function CreateFlight() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [aircraftAvailability, setAircraftAvailability] = useState(null);

  // Form state
  const generateDefaultFlightNumber = () => {
    const stamp = Date.now().toString().slice(-6);
    return `FD-${stamp}-TEST`;
  };

  const [formData, setFormData] = useState({
    flightNumber: generateDefaultFlightNumber(),
    routeId: '',
    departureDate: '',
    departureTime: '12:00',
    status: 'SCHEDULED',
    aircraftId: ''
  });

  // Calculated arrival date/time based on route segments
  const [calculatedArrival, setCalculatedArrival] = useState({
    date: '',
    time: '',
    dateTime: ''
  });

  // Data state
  const [routes, setRoutes] = useState([]);
  const [aircraft, setAircraft] = useState([]);
  const [selectedRoute, setSelectedRoute] = useState(null);

  // Load routes and aircraft on component mount
  useEffect(() => {
    const loadData = async () => {
      try {
        setLoading(true);
        const [routesResponse, aircraftResponse] = await Promise.all([
          routeAPI.getAllRoutes(),
          aircraftAPI.getAllAircraft()
        ]);
        
        const routesData = routesResponse.data;

        // Load segments for each route
        const routesWithSegments = await Promise.all(
          routesData.map(async (route) => {
            try {
              const segmentsResponse = await segmentAPI.getSegmentsByRouteOrdered(route.id);
              return {
                ...route,
                segments: segmentsResponse.data || []
              };
            } catch (error) {
              console.error(`Error loading segments for route ${route.id}:`, error);
              return {
                ...route,
                segments: []
              };
            }
          })
        );
        
        setRoutes(routesWithSegments);
        setAircraft(aircraftResponse.data);
      } catch (error) {
        console.error('Error loading data:', error);
        toast.error('Failed to load data');
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, []);

  // Update selected route when route is selected
  useEffect(() => {
    if (formData.routeId && routes.length > 0) {
      const route = routes.find(r => r.id === parseInt(formData.routeId));
      if (route) {
        setSelectedRoute(route);
      }
    } else {
      setSelectedRoute(null);
    }
  }, [formData.routeId, routes]);

  // Calculate arrival time whenever departure date/time or route changes
  useEffect(() => {
    if (formData.departureDate && formData.departureTime && selectedRoute) {
      const arrival = calculateArrivalTime(formData.departureDate, formData.departureTime, selectedRoute);
      setCalculatedArrival(arrival);
    } else {
      setCalculatedArrival({ date: '', time: '', dateTime: '' });
    }
  }, [formData.departureDate, formData.departureTime, selectedRoute]);

  const calculateArrivalTime = (departureDate, departureTime, route) => {
    if (!departureDate || !departureTime || !route || !route.segments || route.segments.length === 0) {
      return { date: '', time: '', dateTime: '' };
    }

    try {
      // Calculate total route duration from segments (durationMinutes + layoverMinutes)
      let totalDurationMinutes = 0;
      route.segments.forEach(segment => {
        if (segment.durationMinutes) {
          totalDurationMinutes += segment.durationMinutes;
        }
        // Add layover time (waiting time at destination before next segment)
        if (segment.layoverMinutes) {
          totalDurationMinutes += segment.layoverMinutes;
        }
      });

      // If no valid segments, return empty
      if (totalDurationMinutes === 0) {
        return { date: '', time: '', dateTime: '' };
      }

      // Calculate arrival datetime
      const departureDateTime = new Date(`${departureDate}T${departureTime}`);
      const arrivalDateTime = new Date(departureDateTime.getTime() + totalDurationMinutes * 60 * 1000);

      const arrivalDate = arrivalDateTime.toISOString().split('T')[0];
      const arrivalTime = arrivalDateTime.toTimeString().substring(0, 5);
      const arrivalDateTimeStr = `${arrivalDate}T${arrivalTime}:00`;

      return {
        date: arrivalDate,
        time: arrivalTime,
        dateTime: arrivalDateTimeStr
      };
    } catch (error) {
      console.error('Error calculating arrival time:', error);
      return { date: '', time: '', dateTime: '' };
    }
  };

  const handleInputChange = (field, value) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));
    
    // Clear aircraft availability when route or dates/times change
    if (field === 'routeId' || field === 'departureDate' || field === 'departureTime') {
      setAircraftAvailability(null);
    }
  };

  const checkAircraftAvailability = async () => {
    if (!formData.aircraftId || !formData.departureDate || !calculatedArrival.dateTime) {
      toast.error('Please select aircraft, departure date/time, and route first');
      return;
    }

    try {
      setLoading(true);
      
      // Use actual departure and calculated arrival times
      const departureDateTime = `${formData.departureDate}T${formData.departureTime}:00`;
      const arrivalDateTime = calculatedArrival.dateTime;

      // Check for maintenance conflicts
      const maintenanceResponse = await maintenanceAPI.getServicesByAircraft(formData.aircraftId);
      const maintenanceServices = maintenanceResponse.data || [];

      // Check if there are any maintenance services that conflict with the flight dates
      const conflictingMaintenance = maintenanceServices.filter(service => {
        const serviceStart = new Date(service.startDate);
        const serviceEnd = new Date(service.endDate);
        const flightStart = new Date(departureDateTime);
        const flightEnd = new Date(arrivalDateTime);

        // Check for overlap
        return (serviceStart <= flightEnd && serviceEnd >= flightStart);
      });

      if (conflictingMaintenance.length > 0) {
        setAircraftAvailability({
          available: false,
          message: `Aircraft has ${conflictingMaintenance.length} maintenance service(s) scheduled during this period`,
          conflictingServices: conflictingMaintenance
        });
        toast.error('Aircraft not available - maintenance scheduled');
      } else {
        setAircraftAvailability({
          available: true,
          message: 'Aircraft is available for this flight'
        });
        toast.success('Aircraft is available');
      }
    } catch (error) {
      console.error('Error checking aircraft availability:', error);
      toast.error('Failed to check aircraft availability');
      setAircraftAvailability({
        available: false,
        message: 'Error checking availability'
      });
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async () => {
    // Validation
    if (!formData.flightNumber.trim()) {
      toast.error('Please enter a flight number');
      return;
    }

    if (!formData.routeId) {
      toast.error('Please select a route');
      return;
    }

    if (!formData.departureDate || !formData.departureTime) {
      toast.error('Please select departure date and time');
      return;
    }

    if (!calculatedArrival.dateTime) {
      toast.error('Arrival time could not be calculated. Please check route segments');
      return;
    }

    if (!formData.aircraftId) {
      toast.error('Please select an aircraft');
      return;
    }

    // Check aircraft availability if not already checked
    if (!aircraftAvailability) {
      toast.error('Please check aircraft availability first');
      return;
    }

    if (!aircraftAvailability.available) {
      toast.error('Cannot create flight with unavailable aircraft');
      return;
    }

    setIsSubmitting(true);
    try {
      const departureDateTime = `${formData.departureDate}T${formData.departureTime}:00`;
      const arrivalDateTime = calculatedArrival.dateTime;

      const flightData = {
        flightNumber: formData.flightNumber,
        depTime: departureDateTime,
        arrTime: arrivalDateTime,
        status: formData.status,
        aircraftId: parseInt(formData.aircraftId),
        routeId: parseInt(formData.routeId)
      };

      console.log('Creating flight with data:', flightData);

      // Make API call to create flight
      const response = await flightAPI.createFlight(flightData);
      toast.success('Flight created successfully!');
      navigate('/dispatcher/flights');
    } catch (error) {
      console.error('Error creating flight:', error);
      const msg = error?.response?.data?.error || error?.response?.data?.message || error?.message || 'Failed to create flight';
      toast.error(msg);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleCancel = () => {
    navigate('/dispatcher/flights');
  };

  const formatRoutePath = (route) => {
    if (!route || !route.segments || route.segments.length === 0) {
      return 'No segments';
    }

    const segments = route.segments;
    const originCodes = segments.map(segment => segment.originAirportCode || 'N/A');
    const lastDestination = segments[segments.length - 1].destinationAirportCode || 'N/A';
    
    return originCodes.concat([lastDestination]).join(' → ');
  };

  const formatDuration = (minutes) => {
    if (!minutes || minutes === 0) return '0m';
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    return hours > 0 ? `${hours}h ${mins}m` : `${mins}m`;
  };

  if (loading && routes.length === 0) {
    return (
      <DispatcherLayout>
        <DispatcherSidebar />
        <div style={{
          flex: 1,
          padding: '24px',
          backgroundColor: '#F8FAFC',
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center'
        }}>
          <div style={{
            fontSize: '18px',
            color: '#4A5568',
            fontFamily: 'Inter, sans-serif'
          }}>
            Loading...
          </div>
        </div>
      </DispatcherLayout>
    );
  }

  return (
    <DispatcherLayout>
      <DispatcherSidebar />
      <div style={{
        flex: 1,
        padding: '24px',
        backgroundColor: '#F8FAFC',
        overflowY: 'auto'
      }}>
        <h1 style={{
          fontSize: '28px',
          fontWeight: 'bold',
          color: '#1A202C',
          marginBottom: '24px',
          fontFamily: 'Inter, sans-serif'
        }}>
          Create Flight
        </h1>

        {/* Information Banner */}
        <div style={{
          backgroundColor: '#F0FDF4',
          border: '1px solid #BBF7D0',
          borderRadius: '8px',
          padding: '16px',
          marginBottom: '24px'
        }}>
          <p style={{
            color: '#166534',
            fontSize: '14px',
            margin: 0,
            fontFamily: 'Inter, sans-serif'
          }}>
            System checks aircraft availability, maintenance, and segment consistency automatically.
          </p>
        </div>

        {/* Form */}
        <div style={{
          backgroundColor: 'white',
          borderRadius: '12px',
          padding: '24px',
          boxShadow: '0 1px 3px rgba(0, 0, 0, 0.1)'
        }}>
          <div style={{
            display: 'grid',
            gridTemplateColumns: '1fr 1fr',
            gap: '24px',
            marginBottom: '24px'
          }}>
            {/* Left Column */}
            <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
              {/* Flight ID */}
              <div>
                <label style={{
                  display: 'block',
                  fontSize: '12px',
                  fontWeight: '500',
                  color: '#4A5568',
                  marginBottom: '4px',
                  fontFamily: 'Inter, sans-serif'
                }}>
                  Flight ID
                </label>
                <input
                  type="text"
                  value={formData.flightNumber}
                  onChange={(e) => handleInputChange('flightNumber', e.target.value)}
                  style={{
                    width: '100%',
                    height: '40px',
                    padding: '8px 12px',
                    border: '1px solid #D1D5DB',
                    borderRadius: '6px',
                    fontSize: '14px',
                    fontFamily: 'Inter, sans-serif'
                  }}
                />
              </div>

              {/* Route Selection */}
              <div>
                <label style={{
                  display: 'block',
                  fontSize: '12px',
                  fontWeight: '500',
                  color: '#4A5568',
                  marginBottom: '4px',
                  fontFamily: 'Inter, sans-serif'
                }}>
                  Route (From → To)
                </label>
                <select
                  value={formData.routeId}
                  onChange={(e) => handleInputChange('routeId', e.target.value)}
                  style={{
                    width: '100%',
                    height: '40px',
                    padding: '8px 12px',
                    border: '1px solid #D1D5DB',
                    borderRadius: '6px',
                    fontSize: '14px',
                    fontFamily: 'Inter, sans-serif',
                    backgroundColor: 'white'
                  }}
                >
                  <option value="">Select route</option>
                  {routes.map(route => (
                    <option key={route.id} value={route.id}>
                      {route.name} - {formatRoutePath(route)}
                    </option>
                  ))}
                </select>
              </div>

              {/* Departure Date */}
              <div>
                <label style={{
                  display: 'block',
                  fontSize: '12px',
                  fontWeight: '500',
                  color: '#4A5568',
                  marginBottom: '4px',
                  fontFamily: 'Inter, sans-serif'
                }}>
                  Departure Date
                </label>
                <input
                  type="date"
                  value={formData.departureDate}
                  onChange={(e) => handleInputChange('departureDate', e.target.value)}
                  style={{
                    width: '100%',
                    height: '40px',
                    padding: '8px 12px',
                    border: '1px solid #D1D5DB',
                    borderRadius: '6px',
                    fontSize: '14px',
                    fontFamily: 'Inter, sans-serif'
                  }}
                />
              </div>

              {/* Departure Time */}
              <div>
                <label style={{
                  display: 'block',
                  fontSize: '12px',
                  fontWeight: '500',
                  color: '#4A5568',
                  marginBottom: '4px',
                  fontFamily: 'Inter, sans-serif'
                }}>
                  Departure Time
                </label>
                <input
                  type="time"
                  value={formData.departureTime}
                  onChange={(e) => handleInputChange('departureTime', e.target.value)}
                  style={{
                    width: '100%',
                    height: '40px',
                    padding: '8px 12px',
                    border: '1px solid #D1D5DB',
                    borderRadius: '6px',
                    fontSize: '14px',
                    fontFamily: 'Inter, sans-serif'
                  }}
                />
              </div>

              {/* Calculated Arrival (Read-only display) */}
              <div>
                <label style={{
                  display: 'block',
                  fontSize: '12px',
                  fontWeight: '500',
                  color: '#4A5568',
                  marginBottom: '4px',
                  fontFamily: 'Inter, sans-serif'
                }}>
                  Calculated Arrival
                </label>
                <div style={{
                  width: '100%',
                  height: '40px',
                  padding: '8px 12px',
                  border: '1px solid #D1D5DB',
                  borderRadius: '6px',
                  fontSize: '14px',
                  fontFamily: 'Inter, sans-serif',
                  backgroundColor: '#F9FAFB',
                  display: 'flex',
                  alignItems: 'center',
                  color: calculatedArrival.dateTime ? '#1F2937' : '#9CA3AF'
                }}>
                  {calculatedArrival.dateTime 
                    ? `${calculatedArrival.date} at ${calculatedArrival.time}` 
                    : 'Select route and departure to calculate'}
                </div>
              </div>

            </div>

            {/* Right Column */}
            <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
              {/* Assigned Aircraft */}
              <div>
                <label style={{
                  display: 'block',
                  fontSize: '12px',
                  fontWeight: '500',
                  color: '#4A5568',
                  marginBottom: '4px',
                  fontFamily: 'Inter, sans-serif'
                }}>
                  Assigned Aircraft
                </label>
                <div style={{ display: 'flex', gap: '8px', alignItems: 'end' }}>
                  <select
                    value={formData.aircraftId}
                    onChange={(e) => handleInputChange('aircraftId', e.target.value)}
                    style={{
                      flex: 1,
                      height: '40px',
                      padding: '8px 12px',
                      border: '1px solid #D1D5DB',
                      borderRadius: '6px',
                      fontSize: '14px',
                      fontFamily: 'Inter, sans-serif',
                      backgroundColor: 'white'
                    }}
                  >
                    <option value="">Select aircraft</option>
                    {aircraft.map(ac => (
                      <option key={ac.id} value={ac.id}>
                        {ac.registration} - {ac.model}
                      </option>
                    ))}
                  </select>
                  <button
                    onClick={checkAircraftAvailability}
                    disabled={loading || !formData.aircraftId || !formData.departureDate || !calculatedArrival.dateTime}
                    style={{
                      height: '40px',
                      padding: '0 12px',
                      backgroundColor: '#3182CE',
                      color: 'white',
                      border: 'none',
                      borderRadius: '6px',
                      fontSize: '12px',
                      fontWeight: '500',
                      cursor: (loading || !formData.aircraftId || !formData.departureDate || !calculatedArrival.dateTime) ? 'not-allowed' : 'pointer',
                      fontFamily: 'Inter, sans-serif',
                      opacity: (loading || !formData.aircraftId || !formData.departureDate || !calculatedArrival.dateTime) ? 0.6 : 1
                    }}
                  >
                    Check Availability
                  </button>
                </div>
                
                {/* Aircraft Availability Status */}
                {aircraftAvailability && (
                  <div style={{
                    marginTop: '8px',
                    padding: '8px 12px',
                    borderRadius: '6px',
                    fontSize: '12px',
                    fontFamily: 'Inter, sans-serif',
                    backgroundColor: aircraftAvailability.available ? '#F0FDF4' : '#FEF2F2',
                    color: aircraftAvailability.available ? '#166534' : '#DC2626',
                    border: `1px solid ${aircraftAvailability.available ? '#BBF7D0' : '#FECACA'}`
                  }}>
                    {aircraftAvailability.message}
                  </div>
                )}
              </div>

              {/* Segments Display */}
              {selectedRoute && (
                <div>
                  <label style={{
                    display: 'block',
                    fontSize: '12px',
                    fontWeight: '500',
                    color: '#4A5568',
                    marginBottom: '8px',
                    fontFamily: 'Inter, sans-serif'
                  }}>
                    Segments (optional for connections)
                  </label>
                  <div style={{
                    border: '1px solid #E2E8F0',
                    borderRadius: '8px',
                    overflow: 'hidden'
                  }}>
                    <table style={{
                      width: '100%',
                      borderCollapse: 'collapse',
                      fontFamily: 'Inter, sans-serif'
                    }}>
                      <thead>
                        <tr style={{ backgroundColor: '#F7FAFC' }}>
                          <th style={{
                            padding: '8px',
                            textAlign: 'left',
                            fontSize: '11px',
                            fontWeight: '600',
                            color: '#4A5568',
                            borderBottom: '1px solid #E2E8F0'
                          }}>#</th>
                          <th style={{
                            padding: '8px',
                            textAlign: 'left',
                            fontSize: '11px',
                            fontWeight: '600',
                            color: '#4A5568',
                            borderBottom: '1px solid #E2E8F0'
                          }}>From</th>
                          <th style={{
                            padding: '8px',
                            textAlign: 'left',
                            fontSize: '11px',
                            fontWeight: '600',
                            color: '#4A5568',
                            borderBottom: '1px solid #E2E8F0'
                          }}>To</th>
                          <th style={{
                            padding: '8px',
                            textAlign: 'left',
                            fontSize: '11px',
                            fontWeight: '600',
                            color: '#4A5568',
                            borderBottom: '1px solid #E2E8F0'
                          }}>Duration</th>
                          <th style={{
                            padding: '8px',
                            textAlign: 'left',
                            fontSize: '11px',
                            fontWeight: '600',
                            color: '#4A5568',
                            borderBottom: '1px solid #E2E8F0'
                          }}>Layover</th>
                        </tr>
                      </thead>
                      <tbody>
                        {selectedRoute.segments && selectedRoute.segments.map((segment, index) => (
                          <tr key={segment.id}>
                            <td style={{
                              padding: '8px',
                              fontSize: '12px',
                              color: '#2D3748',
                              borderBottom: '1px solid #E2E8F0'
                            }}>{index + 1}</td>
                            <td style={{
                              padding: '8px',
                              fontSize: '12px',
                              color: '#2D3748',
                              borderBottom: '1px solid #E2E8F0'
                            }}>{segment.originAirportCode || 'N/A'}</td>
                            <td style={{
                              padding: '8px',
                              fontSize: '12px',
                              color: '#2D3748',
                              borderBottom: '1px solid #E2E8F0'
                            }}>{segment.destinationAirportCode || 'N/A'}</td>
                            <td style={{
                              padding: '8px',
                              fontSize: '12px',
                              color: '#2D3748',
                              borderBottom: '1px solid #E2E8F0'
                            }}>{formatDuration(segment.durationMinutes || 0)}</td>
                            <td style={{
                              padding: '8px',
                              fontSize: '12px',
                              color: '#2D3748',
                              borderBottom: '1px solid #E2E8F0'
                            }}>{index === 0 ? 'N/A' : formatDuration(segment.layoverMinutes || 0)}</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </div>
              )}
            </div>
          </div>

          {/* Footer */}
          <div style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            marginTop: '32px',
            paddingTop: '24px',
            borderTop: '1px solid #E2E8F0'
          }}>
            <p style={{
              fontSize: '12px',
              color: '#718096',
              margin: 0,
              fontFamily: 'Inter, sans-serif'
            }}>
              Initial status is "Scheduled". All changes and notifications are recorded in audit log.
            </p>

            {/* Action Buttons */}
            <div style={{ display: 'flex', gap: '12px' }}>
              <button
                onClick={handleCancel}
                style={{
                  height: '40px',
                  padding: '0 24px',
                  backgroundColor: '#E2E8F0',
                  color: '#4A5568',
                  border: 'none',
                  borderRadius: '6px',
                  fontSize: '14px',
                  fontWeight: '500',
                  cursor: 'pointer',
                  fontFamily: 'Inter, sans-serif'
                }}
              >
                Cancel
              </button>
              <button
                onClick={handleSubmit}
                disabled={isSubmitting || !aircraftAvailability?.available}
                style={{
                  height: '40px',
                  padding: '0 24px',
                  backgroundColor: '#38A169',
                  color: 'white',
                  border: 'none',
                  borderRadius: '6px',
                  fontSize: '14px',
                  fontWeight: '500',
                  cursor: (!aircraftAvailability?.available || isSubmitting) ? 'not-allowed' : 'pointer',
                  fontFamily: 'Inter, sans-serif',
                  opacity: (!aircraftAvailability?.available || isSubmitting) ? 0.6 : 1
                }}
              >
                {isSubmitting ? 'Creating...' : 'Create Flight'}
              </button>
            </div>
          </div>
        </div>
      </div>
    </DispatcherLayout>
  );
}
