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
    arrivalDate: '',
    status: 'SCHEDULED',
    aircraftId: ''
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

  const handleInputChange = (field, value) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));
    
    // Clear aircraft availability when route or dates change
    if (field === 'routeId' || field === 'departureDate' || field === 'arrivalDate') {
      setAircraftAvailability(null);
    }
  };

  const checkAircraftAvailability = async () => {
    if (!formData.aircraftId || !formData.departureDate || !formData.arrivalDate) {
      toast.error('Please select aircraft and dates first');
      return;
    }

    try {
      setLoading(true);
      
      // Convert dates to LocalDateTime format (add default times)
      const departureDateTime = `${formData.departureDate}T00:00:00`;
      const arrivalDateTime = `${formData.arrivalDate}T23:59:59`;

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

    if (!formData.departureDate || !formData.arrivalDate) {
      toast.error('Please select departure and arrival dates');
      return;
    }

    if (!formData.aircraftId) {
      toast.error('Please select an aircraft');
      return;
    }

    if (new Date(formData.departureDate) >= new Date(formData.arrivalDate)) {
      toast.error('Arrival date must be after departure date');
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
      // Get selected route to extract time information
      const selectedRouteData = routes.find(r => r.id === parseInt(formData.routeId));
      if (!selectedRouteData) {
        toast.error('Selected route not found');
        return;
      }

      // For now, we'll use the first and last segments for times
      // In a real implementation, you'd calculate this based on route segments
      const departureDateTime = `${formData.departureDate}T12:00:00`; // Default time
      const arrivalDateTime = `${formData.arrivalDate}T13:25:00`; // Default time

      const flightData = {
        flightNumber: formData.flightNumber,
        depTime: departureDateTime,
        arrTime: arrivalDateTime,
        status: formData.status,
        aircraftId: parseInt(formData.aircraftId),
        routeId: parseInt(formData.routeId)
      };

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

              {/* Arrival Date */}
              <div>
                <label style={{
                  display: 'block',
                  fontSize: '12px',
                  fontWeight: '500',
                  color: '#4A5568',
                  marginBottom: '4px',
                  fontFamily: 'Inter, sans-serif'
                }}>
                  Arrival Date
                </label>
                <input
                  type="date"
                  value={formData.arrivalDate}
                  onChange={(e) => handleInputChange('arrivalDate', e.target.value)}
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
                    disabled={loading || !formData.aircraftId || !formData.departureDate || !formData.arrivalDate}
                    style={{
                      height: '40px',
                      padding: '0 12px',
                      backgroundColor: '#3182CE',
                      color: 'white',
                      border: 'none',
                      borderRadius: '6px',
                      fontSize: '12px',
                      fontWeight: '500',
                      cursor: (loading || !formData.aircraftId || !formData.departureDate || !formData.arrivalDate) ? 'not-allowed' : 'pointer',
                      fontFamily: 'Inter, sans-serif',
                      opacity: (loading || !formData.aircraftId || !formData.departureDate || !formData.arrivalDate) ? 0.6 : 1
                    }}
                  >
                    Available
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
                          }}>Dep</th>
                          <th style={{
                            padding: '8px',
                            textAlign: 'left',
                            fontSize: '11px',
                            fontWeight: '600',
                            color: '#4A5568',
                            borderBottom: '1px solid #E2E8F0'
                          }}>Arr</th>
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
                            }}>{segment.departureTime ? (typeof segment.departureTime === 'string' ? segment.departureTime.substring(0, 5) : segment.departureTime) : 'N/A'}</td>
                            <td style={{
                              padding: '8px',
                              fontSize: '12px',
                              color: '#2D3748',
                              borderBottom: '1px solid #E2E8F0'
                            }}>{segment.arrivalTime ? (typeof segment.arrivalTime === 'string' ? segment.arrivalTime.substring(0, 5) : segment.arrivalTime) : 'N/A'}</td>
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
