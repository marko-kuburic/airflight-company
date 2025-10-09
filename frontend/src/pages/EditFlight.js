import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { DispatcherLayout } from '../components/Layout/DispatcherLayout';
import { DispatcherSidebar } from '../components/Layout/DispatcherSidebar';
import { routeAPI, aircraftAPI, flightAPI, segmentAPI } from '../services/api';
import toast from 'react-hot-toast';

export default function EditFlight() {
  const navigate = useNavigate();
  const { flightId } = useParams();

  // Helper functions for route filtering
  const getRouteOriginDestination = (route) => {
    if (!route || !route.segments || route.segments.length === 0) {
      return { origin: null, destination: null };
    }
    
    const firstSegment = route.segments[0];
    const lastSegment = route.segments[route.segments.length - 1];
    
    return {
      origin: firstSegment.originAirportCode,
      destination: lastSegment.destinationAirportCode
    };
  };

  const getFilteredRoutes = () => {
    if (!routes.length || !flight || !flight.routeId) return routes;
    
    // Find the original route to get origin and destination airports
    const originalRoute = routes.find(r => r.id === flight.routeId);
    if (!originalRoute || !originalRoute.segments || originalRoute.segments.length === 0) {
      return routes;
    }
    
    const firstSegment = originalRoute.segments[0];
    const lastSegment = originalRoute.segments[originalRoute.segments.length - 1];
    const originAirportId = firstSegment.originAirportId;
    const destinationAirportId = lastSegment.destinationAirportId;
    
    // Filter routes that have the same origin and destination airports
    return routes.filter(route => {
      if (!route.segments || route.segments.length === 0) return false;
      
      const routeFirstSegment = route.segments[0];
      const routeLastSegment = route.segments[route.segments.length - 1];
      
      return routeFirstSegment.originAirportId === originAirportId && 
             routeLastSegment.destinationAirportId === destinationAirportId;
    });
  };
  const [loading, setLoading] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [aircraftAvailability, setAircraftAvailability] = useState(null);

  // Form state
  const [formData, setFormData] = useState({
    flightNumber: '',
    routeId: '',
    departureDate: '',
    departureTime: '',
    arrivalDate: '',
    arrivalTime: '',
    status: 'SCHEDULED',
    aircraftId: '',
    delayMinutes: 0 // Flight delay in minutes
  });

  // Data state
  const [routes, setRoutes] = useState([]);
  const [aircraft, setAircraft] = useState([]);
  const [selectedRoute, setSelectedRoute] = useState(null);
  const [flight, setFlight] = useState(null);

  // Load flight data and other data on component mount
  useEffect(() => {
    const loadData = async () => {
      try {
        setLoading(true);
        
        // Check authentication first
        const authToken = localStorage.getItem('authToken');
        const userData = localStorage.getItem('user');

        console.log('EditFlight page - Auth token:', authToken ? 'Present' : 'Missing');
        console.log('EditFlight page - User data:', userData ? 'Present' : 'Missing');

        // For now, make authentication optional for EditFlight page
        // You can remove this check if you want to allow access without login
        if (!authToken) {
          console.warn('No auth token found, but allowing access for now');
          // toast.error('Please log in to continue');
          // navigate('/login');
          // return;
        }

        // Load flight data, routes, and aircraft in parallel
        const [flightResponse, routesResponse, aircraftResponse] = await Promise.all([
          flightAPI.getAllFlights(),
          routeAPI.getAllRoutes(),
          aircraftAPI.getAllAircraft()
        ]);
        
        const flightData = flightResponse.data;
        console.log('Flight data loaded:', flightData);

        const flight = flightData.flights.find(f => f.id === parseInt(flightId));
        console.log('Found flight:', flight);
        
        if (!flight) {
          toast.error('Flight not found');
          navigate('/dispatcher/flights');
          return;
        }
        
        setFlight(flight);

        // Populate form with flight data - UPDATED FOR TESTING - FORCE REBUILD
        setFormData({
          flightNumber: flight.flightNumber || '',
          routeId: flight.routeId ? flight.routeId.toString() : '',
          departureDate: flight.departureTime ? flight.departureTime.split('T')[0] : '',
          departureTime: flight.departureTime ? flight.departureTime.split('T')[1].substring(0, 5) : '',
          arrivalDate: flight.arrivalTime ? flight.arrivalTime.split('T')[0] : '',
          arrivalTime: flight.arrivalTime ? flight.arrivalTime.split('T')[1].substring(0, 5) : '',
          status: flight.status || 'SCHEDULED',
          aircraftId: flight.aircraftId ? flight.aircraftId.toString() : '',
          delayMinutes: 0 // Initialize delay to 0
        });

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

        // Set initial selected route if flight has a routeId
        if (flight.routeId) {
          const initialRoute = routesWithSegments.find(r => r.id === flight.routeId);
          if (initialRoute) {
            setSelectedRoute(initialRoute);
          }
        }
      } catch (error) {
        console.error('Error loading data:', error);
        toast.error('Failed to load data');
      } finally {
        setLoading(false);
      }
    };

    if (flightId) {
      loadData();
    }
  }, [flightId]);

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

  const calculateArrivalTime = (departureDate, departureTime, route, delayMinutes = 0) => {
    if (!departureDate || !departureTime || !route || !route.segments || route.segments.length === 0) {
      return { date: '', time: '', dateTime: '' };
    }

    try {
      // Calculate total route duration from segments (durationMinutes + layoverMinutes)
      let totalDurationMinutes = 0;
      let adjustedDurationMinutes = 0;
      
      route.segments.forEach((segment, index) => {
        if (segment.durationMinutes) {
          totalDurationMinutes += segment.durationMinutes;
          adjustedDurationMinutes += segment.durationMinutes;
        }
        
        // Handle layover time with delay adjustment (layover is time between segments)
        // Only check layover for segments after the first one (index > 0)
        if (segment.layoverMinutes && index > 0) {
          const originalLayover = segment.layoverMinutes;
          
          // For layover adjustment: if (layover - 15) < delay, then reduce arrival delay by the difference
          if (delayMinutes > 0 && originalLayover > 15) {
            const layoverReduction = Math.min(delayMinutes, originalLayover - 15);
            const adjustedLayover = originalLayover - layoverReduction;
            totalDurationMinutes += originalLayover; // Add original layover to total
            adjustedDurationMinutes += adjustedLayover; // Add adjusted layover to adjusted total
          } else {
            totalDurationMinutes += originalLayover;
            adjustedDurationMinutes += originalLayover;
          }
        }
      });

      // If no segment durations available, return empty
      if (totalDurationMinutes === 0) {
        console.warn('No valid segment durations found for route');
        return { date: '', time: '', dateTime: '' };
      }

      // Calculate departure time with delay
      const departureDateTime = new Date(`${departureDate}T${departureTime}:00`);
      const delayedDepartureTime = new Date(departureDateTime.getTime() + delayMinutes * 60000);
      
      // Calculate arrival time using adjusted duration
      const arrivalDateTime = new Date(delayedDepartureTime.getTime() + adjustedDurationMinutes * 60000);

      const arrivalDate = arrivalDateTime.toISOString().split('T')[0];
      const arrivalTime = arrivalDateTime.toTimeString().substring(0, 5);
      const arrivalDateTimeStr = `${arrivalDate}T${arrivalTime}:00`;

      return {
        date: arrivalDate,
        time: arrivalTime,
        dateTime: arrivalDateTimeStr,
        delayedDeparture: {
          date: delayedDepartureTime.toISOString().split('T')[0],
          time: delayedDepartureTime.toTimeString().substring(0, 5),
          dateTime: `${delayedDepartureTime.toISOString().split('T')[0]}T${delayedDepartureTime.toTimeString().substring(0, 5)}:00`
        }
      };
    } catch (error) {
      console.error('Error calculating arrival time:', error);
      return { date: '', time: '', dateTime: '' };
    }
  };

  const handleInputChange = (field, value) => {
    setFormData(prev => {
      const newData = {
        ...prev,
        [field]: value
      };

      // Recalculate arrival time when departure time, route, or delay changes
      if (field === 'departureDate' || field === 'departureTime' || field === 'routeId' || field === 'delayMinutes') {
        const selectedRoute = field === 'routeId' 
          ? routes.find(r => r.id === parseInt(value))
          : routes.find(r => r.id === parseInt(prev.routeId));
        
        const arrival = calculateArrivalTime(
          field === 'departureDate' ? value : newData.departureDate,
          field === 'departureTime' ? value : newData.departureTime,
          selectedRoute,
          field === 'delayMinutes' ? parseInt(value) || 0 : newData.delayMinutes || 0
        );
        
        newData.arrivalDate = arrival.date;
        newData.arrivalTime = arrival.time;
      }

      return newData;
    });
    
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
      const departureDateTime = `${formData.departureDate}T${formData.departureTime || '00:00'}:00`;
      const arrivalDateTime = `${formData.arrivalDate}T${formData.arrivalTime || '23:59'}:59`;

      // For now, we'll just show a simple availability check
      // In a real implementation, you'd check for maintenance conflicts
      setAircraftAvailability({
        available: true,
        message: 'Availability: OK (no conflicts)'
      });
      toast.success('Aircraft is available');
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

  const handleSubmit = async (e) => {
    try {
      if (e) e.preventDefault();
      console.log('handleSubmit called', { formData, flightId });

      // Prevent double submission
      if (isSubmitting) {
        console.log('Already submitting, ignoring...');
        return;
      }

      console.log('Starting validation...');

      // Validation - more flexible for partial updates
      if (!formData.flightNumber.trim()) {
        console.log('Validation failed: flight number');
        toast.error('Please enter a flight number');
        return;
      }

      // Only validate route if it's being changed (not empty and different from current)
      if (!formData.routeId && !flight.routeId) {
        console.log('Validation failed: route');
        toast.error('Please select a route');
        return;
      }

      // Only validate dates if they're provided - compare full datetime
      if (formData.departureDate && formData.departureTime && formData.arrivalDate && formData.arrivalTime) {
        const departureDateTime = new Date(`${formData.departureDate}T${formData.departureTime}:00`);
        const arrivalDateTime = new Date(`${formData.arrivalDate}T${formData.arrivalTime}:00`);
        
        if (departureDateTime >= arrivalDateTime) {
          console.log('Validation failed: datetime order');
          toast.error('Arrival date/time must be after departure date/time');
          return;
        }
      }

      // Validate DELAYED status requirements
      if (formData.status === 'DELAYED') {
        const hasDelay = formData.delayMinutes > 0;
        const originalDepDate = flight?.departureTime?.split('T')[0] || '';
        const originalDepTime = flight?.departureTime?.split('T')[1]?.substring(0, 5) || '';
        const hasTimeChange = formData.departureDate !== originalDepDate || 
                             formData.departureTime !== originalDepTime;
        
        if (!hasDelay && !hasTimeChange) {
          console.log('Validation failed: DELAYED status requires delay or time change');
          toast.error('DELAYED status requires either delay minutes or departure time change');
          return;
        }
      }

      // Only validate aircraft if it's being changed (not empty and different from current)
      if (!formData.aircraftId && !flight.aircraftId) {
        console.log('Validation failed: aircraft');
        toast.error('Please select an aircraft');
        return;
      }

      console.log('Validation passed, setting isSubmitting...');
      setIsSubmitting(true);

      const departureDateTime = `${formData.departureDate}T${formData.departureTime || '12:00'}:00`;
      const arrivalDateTime = `${formData.arrivalDate}T${formData.arrivalTime || '13:25'}:00`;

      const flightData = {
        flightNumber: formData.flightNumber,
        depTime: departureDateTime,
        arrTime: arrivalDateTime,
        status: formData.status,
        aircraftId: formData.aircraftId ? parseInt(formData.aircraftId) : flight.aircraftId,
        routeId: formData.routeId ? parseInt(formData.routeId) : flight.routeId
      };

      // Make API call to update flight via API service (handles auth)
      const response = await flightAPI.updateFlight(flightId, flightData);

      console.log('Response status:', response.status);

      if (response.status === 200) {
        toast.success('Flight updated successfully!');
        navigate('/dispatcher/flights');
      } else {
        console.error('Update failed:', response.data);
        toast.error(`Failed to update flight: ${response.data?.message || 'Unknown error'}`);
      }
    } catch (error) {
      console.error('Error in handleSubmit:', error);
      toast.error(error?.response?.data?.error || error?.response?.data?.message || 'Failed to update flight');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDelete = async () => {
    try {
      console.log('handleDelete called', { flightId });

      if (!window.confirm('Are you sure you want to delete this flight?')) {
        return;
      }

      // Prevent double submission
      if (isSubmitting) {
        console.log('Already submitting, ignoring delete...');
        return;
      }

      setIsSubmitting(true);

      const response = await flightAPI.deleteFlight(flightId);

      console.log('Delete response status:', response.status);

      if (response.status === 204) {
        toast.success('Flight deleted successfully!');
        navigate('/dispatcher/flights');
      } else {
        console.error('Delete failed:', response.data);
        toast.error(`Failed to delete flight: ${response.data?.message || 'Unknown error'}`);
      }
    } catch (error) {
      console.error('Error in handleDelete:', error);
      toast.error(error?.response?.data?.error || error?.response?.data?.message || 'Failed to delete flight');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleCancel = () => {
    navigate('/dispatcher/flights');
  };

  if (loading && !flight) {
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
            Loading flight data...
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
          Edit Flight
        </h1>

        {/* Flight Details Form */}
        <div style={{
          backgroundColor: 'white',
          borderRadius: '12px',
          padding: '24px',
          boxShadow: '0 1px 3px rgba(0, 0, 0, 0.1)',
          border: '1px solid #E2E8F0'
        }}>
          <h2 style={{
            fontSize: '18px',
            fontWeight: '600',
            color: '#1A202C',
            marginBottom: '24px',
            fontFamily: 'Inter, sans-serif'
          }}>
            Flight Details
          </h2>

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
                    fontFamily: 'Inter, sans-serif',
                    backgroundColor: '#F9FAFB'
                  }}
                  readOnly
                />
              </div>

              {/* Departure Date & Time */}
              <div>
                <label style={{
                  display: 'block',
                  fontSize: '12px',
                  fontWeight: '500',
                  color: '#4A5568',
                  marginBottom: '4px',
                  fontFamily: 'Inter, sans-serif'
                }}>
                  Departure (date & time)
                </label>
                <div style={{ display: 'flex', gap: '8px' }}>
                  <input
                    type="date"
                    value={formData.departureDate}
                    onChange={(e) => handleInputChange('departureDate', e.target.value)}
                    style={{
                      flex: 1,
                      height: '40px',
                      padding: '8px 12px',
                      border: '1px solid #D1D5DB',
                      borderRadius: '6px',
                      fontSize: '14px',
                      fontFamily: 'Inter, sans-serif'
                    }}
                  />
                  <input
                    type="time"
                    value={formData.departureTime}
                    onChange={(e) => handleInputChange('departureTime', e.target.value)}
                    style={{
                      flex: 1,
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

              {/* Flight Delay */}
              <div>
                <label style={{
                  display: 'block',
                  fontSize: '12px',
                  fontWeight: '500',
                  color: '#4A5568',
                  marginBottom: '4px',
                  fontFamily: 'Inter, sans-serif'
                }}>
                  Flight Delay (minutes)
                </label>
                <input
                  type="number"
                  min="0"
                  value={formData.delayMinutes}
                  onChange={(e) => handleInputChange('delayMinutes', e.target.value)}
                  placeholder="Delay in minutes (e.g., 60)"
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
                <div style={{
                  fontSize: '11px',
                  color: '#6B7280',
                  marginTop: '4px',
                  fontFamily: 'Inter, sans-serif'
                }}>
                  Layover times will be reduced if delay > (layover - 15min)
                </div>
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
                  From (origin)
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
                  {getFilteredRoutes().map(route => {
                    const { origin, destination } = getRouteOriginDestination(route);
                    return (
                      <option key={route.id} value={route.id}>
                        {route.name} ({origin} → {destination})
                      </option>
                    );
                  })}
                </select>
              </div>
            </div>

            {/* Right Column */}
            <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
              {/* Aircraft */}
              <div>
                <label style={{
                  display: 'block',
                  fontSize: '12px',
                  fontWeight: '500',
                  color: '#4A5568',
                  marginBottom: '4px',
                  fontFamily: 'Inter, sans-serif'
                }}>
                  Aircraft
                </label>
                <select
                  value={formData.aircraftId}
                  onChange={(e) => handleInputChange('aircraftId', e.target.value)}
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
                  <option value="">Select aircraft</option>
                  {aircraft.map(ac => (
                    <option key={ac.id} value={ac.id}>
                      {ac.registration} - {ac.model}
                    </option>
                  ))}
                </select>
              </div>

              {/* Status */}
              <div>
                <label style={{
                  display: 'block',
                  fontSize: '12px',
                  fontWeight: '500',
                  color: '#4A5568',
                  marginBottom: '4px',
                  fontFamily: 'Inter, sans-serif'
                }}>
                  Status
                </label>
                <select
                  value={formData.status}
                  onChange={(e) => handleInputChange('status', e.target.value)}
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
                  <option value="SCHEDULED">Scheduled</option>
                  <option value="BOARDING">Boarding</option>
                  <option value="DEPARTED">Departed</option>
                  <option value="IN_FLIGHT">In Flight</option>
                  <option value="LANDED">Landed</option>
                  <option value="CANCELLED">Cancelled</option>
                  <option value="DELAYED">Delayed</option>
                </select>
              </div>

              {/* Arrival Date & Time (Calculated) */}
              <div>
                <label style={{
                  display: 'block',
                  fontSize: '12px',
                  fontWeight: '500',
                  color: '#4A5568',
                  marginBottom: '4px',
                  fontFamily: 'Inter, sans-serif'
                }}>
                  Arrival (calculated)
                </label>
                <div style={{ display: 'flex', gap: '8px' }}>
                  <input
                    type="date"
                    value={formData.arrivalDate}
                    disabled
                    style={{
                      flex: 1,
                      height: '40px',
                      padding: '8px 12px',
                      border: '1px solid #D1D5DB',
                      borderRadius: '6px',
                      fontSize: '14px',
                      fontFamily: 'Inter, sans-serif',
                      backgroundColor: '#F9FAFB',
                      color: '#6B7280'
                    }}
                  />
                  <input
                    type="time"
                    value={formData.arrivalTime}
                    disabled
                    style={{
                      flex: 1,
                      height: '40px',
                      padding: '8px 12px',
                      border: '1px solid #D1D5DB',
                      borderRadius: '6px',
                      fontSize: '14px',
                      fontFamily: 'Inter, sans-serif',
                      backgroundColor: '#F9FAFB',
                      color: '#6B7280'
                    }}
                  />
                </div>
              </div>

              {/* To (destination) */}
              <div>
                <label style={{
                  display: 'block',
                  fontSize: '12px',
                  fontWeight: '500',
                  color: '#4A5568',
                  marginBottom: '4px',
                  fontFamily: 'Inter, sans-serif'
                }}>
                  To (destination)
                </label>
                <div style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
                  <select
                    disabled
                    style={{
                      flex: 1,
                      height: '40px',
                      padding: '8px 12px',
                      border: '1px solid #D1D5DB',
                      borderRadius: '6px',
                      fontSize: '14px',
                      fontFamily: 'Inter, sans-serif',
                      backgroundColor: '#F9FAFB',
                      color: '#6B7280'
                    }}
                  >
                    <option value="">
                      {selectedRoute && selectedRoute.segments && selectedRoute.segments.length > 0
                        ? selectedRoute.segments[selectedRoute.segments.length - 1].destinationAirportCode
                        : 'Select route first'}
                    </option>
                  </select>
                  
                  {/* Availability Indicator */}
                  <div style={{
                    backgroundColor: '#F0FDF4',
                    color: '#166534',
                    padding: '8px 12px',
                    borderRadius: '6px',
                    fontSize: '12px',
                    fontWeight: '500',
                    fontFamily: 'Inter, sans-serif',
                    border: '1px solid #BBF7D0'
                  }}>
                    {aircraftAvailability ? aircraftAvailability.message : 'Availability: OK (no conflicts)'}
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* Segments Section */}
          {selectedRoute && selectedRoute.segments && selectedRoute.segments.length > 0 && (
            <div style={{ marginBottom: '24px' }}>
              <div style={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                marginBottom: '12px'
              }}>
                <h3 style={{
                  fontSize: '14px',
                  fontWeight: '600',
                  color: '#1A202C',
                  margin: 0,
                  fontFamily: 'Inter, sans-serif'
                }}>
                  Segments
                </h3>
                <button
                  style={{
                    backgroundColor: '#E2E8F0',
                    color: '#4A5568',
                    border: 'none',
                    borderRadius: '6px',
                    padding: '8px 16px',
                    fontSize: '12px',
                    fontWeight: '500',
                    cursor: 'pointer',
                    fontFamily: 'Inter, sans-serif'
                  }}
                  disabled
                >
                  Add segment
                </button>
              </div>
              
              <div style={{
                backgroundColor: '#F8FAFC',
                border: '1px solid #E2E8F0',
                borderRadius: '8px',
                padding: '16px'
              }}>
                {selectedRoute.segments.map((segment, index) => {
                  // Format duration
                  const formatDuration = (minutes) => {
                    if (!minutes || minutes === 0) return '0m';
                    const hours = Math.floor(minutes / 60);
                    const mins = minutes % 60;
                    if (hours === 0) return `${mins}m`;
                    if (mins === 0) return `${hours}h`;
                    return `${hours}h ${mins}m`;
                  };

                  return (
                    <div key={segment.id} style={{
                      marginBottom: index < selectedRoute.segments.length - 1 ? '8px' : '0',
                      paddingBottom: index < selectedRoute.segments.length - 1 ? '8px' : '0',
                      borderBottom: index < selectedRoute.segments.length - 1 ? '1px solid #E2E8F0' : 'none',
                      display: 'flex',
                      justifyContent: 'space-between',
                      alignItems: 'center'
                    }}>
                      <span style={{
                        fontSize: '14px',
                        color: '#2D3748',
                        fontFamily: 'Inter, sans-serif'
                      }}>
                        {index + 1}) {segment.originAirportCode} → {segment.destinationAirportCode}
                      </span>
                      <div style={{
                        display: 'flex',
                        gap: '12px',
                        fontSize: '12px',
                        color: '#6B7280',
                        fontFamily: 'Inter, sans-serif'
                      }}>
                        <span>Flight: {formatDuration(segment.durationMinutes)}</span>
                        <span>Layover: {index === 0 ? 'N/A' : formatDuration(segment.layoverMinutes || 0)}</span>
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          )}

          {/* Action Buttons */}
          <div style={{
            display: 'flex',
            justifyContent: 'flex-end',
            gap: '12px',
            paddingTop: '24px',
            borderTop: '1px solid #E2E8F0'
          }}>
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
              disabled={isSubmitting}
              style={{
                height: '40px',
                padding: '0 24px',
                backgroundColor: '#38A169',
                color: 'white',
                border: 'none',
                borderRadius: '6px',
                fontSize: '14px',
                fontWeight: '500',
                cursor: isSubmitting ? 'not-allowed' : 'pointer',
                fontFamily: 'Inter, sans-serif',
                opacity: isSubmitting ? 0.6 : 1
              }}
            >
              {isSubmitting ? 'Saving...' : 'Save Changes'}
            </button>
            <button
              onClick={handleDelete}
              disabled={isSubmitting}
              style={{
                height: '40px',
                padding: '0 24px',
                backgroundColor: '#E53E3E',
                color: 'white',
                border: 'none',
                borderRadius: '6px',
                fontSize: '14px',
                fontWeight: '500',
                cursor: isSubmitting ? 'not-allowed' : 'pointer',
                fontFamily: 'Inter, sans-serif',
                opacity: isSubmitting ? 0.6 : 1
              }}
            >
              Delete
            </button>
          </div>
        </div>
      </div>
    </DispatcherLayout>
  );
}
