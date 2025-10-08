import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { DispatcherLayout } from '../components/Layout/DispatcherLayout';
import { DispatcherSidebar } from '../components/Layout/DispatcherSidebar';
import { flightAPI, routeAPI, segmentAPI } from '../services/api';
import toast from 'react-hot-toast';

export default function AddRoute() {
  const navigate = useNavigate();
  const [segments, setSegments] = useState([]);
  const [airports, setAirports] = useState([]);
  const [loading, setLoading] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);

  // Route form state
  const [routeName, setRouteName] = useState('');

  // Form state for new segment
  const [newSegment, setNewSegment] = useState({
    originAirportId: '',
    destinationAirportId: '',
    departureTime: '',
    arrivalTime: ''
  });

  // Load airports on component mount
  useEffect(() => {
    const loadAirports = async () => {
      try {
        setLoading(true);
        const response = await flightAPI.getAirports();
        console.log('Raw airports response:', response.data);
        console.log('First airport:', response.data[0]);
        setAirports(response.data);
      } catch (error) {
        console.error('Error loading airports:', error);
        toast.error('Failed to load airports');
      } finally {
        setLoading(false);
      }
    };

    loadAirports();
  }, []);

  const handleInputChange = (field, value) => {
    console.log(`Input changed: ${field} = "${value}" (type: ${typeof value})`);
    setNewSegment(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const addSegment = () => {
    console.log('Add Segment clicked!', newSegment);
    
    // Validate required fields
    if (!newSegment.originAirportId || !newSegment.destinationAirportId || 
        !newSegment.departureTime || !newSegment.arrivalTime) {
      console.log('Validation failed - missing fields');
      toast.error('Please fill in all fields');
      return;
    }

    // Validate that origin and destination are different
    if (newSegment.originAirportId === newSegment.destinationAirportId) {
      toast.error('Origin and destination airports must be different');
      return;
    }

    // Validate time format and logic
    const depTime = newSegment.departureTime;
    const arrTime = newSegment.arrivalTime;
    
    if (depTime >= arrTime) {
      toast.error('Arrival time must be after departure time');
      return;
    }

    // Find airport details
    const originAirport = airports.find(airport => airport.id === parseInt(newSegment.originAirportId));
    const destinationAirport = airports.find(airport => airport.id === parseInt(newSegment.destinationAirportId));

    if (!originAirport || !destinationAirport) {
      toast.error('Invalid airport selection');
      return;
    }

    // Create new segment (distance will be calculated by backend)
    const segment = {
      id: segments.length + 1,
      originAirport: originAirport,
      destinationAirport: destinationAirport,
      departureTime: newSegment.departureTime,
      arrivalTime: newSegment.arrivalTime,
      distance: 0 // Will be calculated by backend
    };

    console.log('Adding segment to list:', segment);
    setSegments(prev => {
      const newSegments = [...prev, segment];
      console.log('New segments list:', newSegments);
      return newSegments;
    });
    
    // Reset form
    setNewSegment({
      originAirportId: '',
      destinationAirportId: '',
      departureTime: '',
      arrivalTime: ''
    });

    console.log('Segment added successfully!');
    toast.success('Segment added successfully');
  };

  const removeLastSegment = () => {
    if (segments.length > 0) {
      setSegments(prev => prev.slice(0, -1));
      toast.success('Last segment removed');
    }
  };

  const handleSubmit = async () => {
    if (!routeName.trim()) {
      toast.error('Please enter a route name');
      return;
    }

    if (segments.length === 0) {
      toast.error('Please add at least one segment');
      return;
    }

    setIsSubmitting(true);
    try {
      // Step 1: Create the route (totalDistance will be calculated by backend)
      const routeData = {
        name: routeName.trim(),
        totalDistance: 0 // Will be calculated from segments
      };

      const routeResponse = await routeAPI.createRoute(routeData);
      const createdRoute = routeResponse.data;

      toast.success('Route created successfully!');

      // Small delay to ensure route is fully committed to database
      await new Promise(resolve => setTimeout(resolve, 100));

      // Step 2: Create all segments for this route
      const segmentPromises = segments.map(segment => {
        const segmentData = {
          routeId: createdRoute.id,
          originAirportId: segment.originAirport.id,
          destinationAirportId: segment.destinationAirport.id,
          departureTime: segment.departureTime,
          arrivalTime: segment.arrivalTime
        };
        return segmentAPI.createSegment(segmentData);
      });

      await Promise.all(segmentPromises);
      
      // Recalculate route total distance after all segments are created
      await routeAPI.recalculateTotalDistance(createdRoute.id);
      
      toast.success(`Route "${routeName}" created with ${segments.length} segment(s)!`);
      
      // Clear the form
      setRouteName('');
      setSegments([]);
      setNewSegment({
        originAirportId: '',
        destinationAirportId: '',
        departureTime: '',
        arrivalTime: ''
      });
    } catch (error) {
      console.error('Error creating route:', error);
      toast.error('Failed to create route');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleCancel = () => {
    navigate('/dispatcher/routes');
  };

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
          Add Route
        </h1>

        {/* Route Name Section */}
        <div style={{
          backgroundColor: 'white',
          borderRadius: '12px',
          padding: '24px',
          boxShadow: '0 1px 3px rgba(0, 0, 0, 0.1)',
          marginBottom: '24px'
        }}>
          <h2 style={{
            fontSize: '18px',
            fontWeight: '600',
            color: '#1A202C',
            marginBottom: '16px',
            fontFamily: 'Inter, sans-serif'
          }}>
            Route Information
          </h2>
          
          <div style={{ maxWidth: '400px' }}>
            <label style={{
              display: 'block',
              fontSize: '12px',
              fontWeight: '500',
              color: '#4A5568',
              marginBottom: '4px',
              fontFamily: 'Inter, sans-serif'
            }}>
              Route Name *
            </label>
            <input
              type="text"
              value={routeName}
              onChange={(e) => setRouteName(e.target.value)}
              placeholder="Enter route name (e.g., Belgrade-Paris-London)"
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

        {/* Segments Section */}
        <div style={{
          backgroundColor: 'white',
          borderRadius: '12px',
          padding: '24px',
          boxShadow: '0 1px 3px rgba(0, 0, 0, 0.1)',
          marginBottom: '24px'
        }}>
          <h2 style={{
            fontSize: '18px',
            fontWeight: '600',
            color: '#1A202C',
            marginBottom: '20px',
            fontFamily: 'Inter, sans-serif'
          }}>
            Segments (optional for connections)
          </h2>

          {/* Segments Table */}
          {segments.length > 0 && (
            <div style={{
              marginBottom: '20px',
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
                      padding: '12px',
                      textAlign: 'left',
                      fontSize: '12px',
                      fontWeight: '600',
                      color: '#4A5568',
                      borderBottom: '1px solid #E2E8F0'
                    }}>#</th>
                    <th style={{
                      padding: '12px',
                      textAlign: 'left',
                      fontSize: '12px',
                      fontWeight: '600',
                      color: '#4A5568',
                      borderBottom: '1px solid #E2E8F0'
                    }}>From</th>
                    <th style={{
                      padding: '12px',
                      textAlign: 'left',
                      fontSize: '12px',
                      fontWeight: '600',
                      color: '#4A5568',
                      borderBottom: '1px solid #E2E8F0'
                    }}>To</th>
                    <th style={{
                      padding: '12px',
                      textAlign: 'left',
                      fontSize: '12px',
                      fontWeight: '600',
                      color: '#4A5568',
                      borderBottom: '1px solid #E2E8F0'
                    }}>Dep</th>
                    <th style={{
                      padding: '12px',
                      textAlign: 'left',
                      fontSize: '12px',
                      fontWeight: '600',
                      color: '#4A5568',
                      borderBottom: '1px solid #E2E8F0'
                    }}>Arr</th>
                    <th style={{
                      padding: '12px',
                      textAlign: 'left',
                      fontSize: '12px',
                      fontWeight: '600',
                      color: '#4A5568',
                      borderBottom: '1px solid #E2E8F0'
                    }}>Distance</th>
                  </tr>
                </thead>
                <tbody>
                  {segments.map((segment, index) => (
                    <tr key={segment.id}>
                      <td style={{
                        padding: '12px',
                        fontSize: '14px',
                        color: '#2D3748',
                        borderBottom: '1px solid #E2E8F0'
                      }}>{index + 1}</td>
                      <td style={{
                        padding: '12px',
                        fontSize: '14px',
                        color: '#2D3748',
                        borderBottom: '1px solid #E2E8F0'
                      }}>{segment.originAirport.iataCode}</td>
                      <td style={{
                        padding: '12px',
                        fontSize: '14px',
                        color: '#2D3748',
                        borderBottom: '1px solid #E2E8F0'
                      }}>{segment.destinationAirport.iataCode}</td>
                      <td style={{
                        padding: '12px',
                        fontSize: '14px',
                        color: '#2D3748',
                        borderBottom: '1px solid #E2E8F0'
                      }}>{segment.departureTime}</td>
                      <td style={{
                        padding: '12px',
                        fontSize: '14px',
                        color: '#2D3748',
                        borderBottom: '1px solid #E2E8F0'
                      }}>{segment.arrivalTime}</td>
                      <td style={{
                        padding: '12px',
                        fontSize: '14px',
                        color: '#2D3748',
                        borderBottom: '1px solid #E2E8F0'
                      }}>{segment.distance} km</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}

          {/* Add Segment Form */}
          <div style={{
            display: 'grid',
            gridTemplateColumns: '1fr 1fr 1fr 1fr 1fr auto',
            gap: '16px',
            alignItems: 'end',
            marginBottom: '16px'
          }}>
            {/* Origin Airport */}
            <div>
              <label style={{
                display: 'block',
                fontSize: '12px',
                fontWeight: '500',
                color: '#4A5568',
                marginBottom: '4px',
                fontFamily: 'Inter, sans-serif'
              }}>
                Origin Airport
              </label>
              <select
                value={newSegment.originAirportId}
                onChange={(e) => handleInputChange('originAirportId', e.target.value)}
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
                <option value="">Select origin</option>
                {airports.map(airport => (
                  <option key={airport.id} value={airport.id}>
                    {airport.iataCode} - {airport.name}
                  </option>
                ))}
              </select>
            </div>

            {/* Destination Airport */}
            <div>
              <label style={{
                display: 'block',
                fontSize: '12px',
                fontWeight: '500',
                color: '#4A5568',
                marginBottom: '4px',
                fontFamily: 'Inter, sans-serif'
              }}>
                Destination Airport
              </label>
              <select
                value={newSegment.destinationAirportId}
                onChange={(e) => handleInputChange('destinationAirportId', e.target.value)}
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
                <option value="">Select destination</option>
                {airports.map(airport => (
                  <option key={airport.id} value={airport.id}>
                    {airport.iataCode} - {airport.name}
                  </option>
                ))}
              </select>
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
                value={newSegment.departureTime}
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

            {/* Arrival Time */}
            <div>
              <label style={{
                display: 'block',
                fontSize: '12px',
                fontWeight: '500',
                color: '#4A5568',
                marginBottom: '4px',
                fontFamily: 'Inter, sans-serif'
              }}>
                Arrival Time
              </label>
              <input
                type="time"
                value={newSegment.arrivalTime}
                onChange={(e) => handleInputChange('arrivalTime', e.target.value)}
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

            {/* Add Segment Button */}
            <button
              onClick={addSegment}
              disabled={loading}
              style={{
                height: '40px',
                padding: '0 16px',
                backgroundColor: '#3182CE',
                color: 'white',
                border: 'none',
                borderRadius: '6px',
                fontSize: '14px',
                fontWeight: '500',
                cursor: loading ? 'not-allowed' : 'pointer',
                fontFamily: 'Inter, sans-serif',
                opacity: loading ? 0.6 : 1
              }}
            >
              Add Segment
            </button>
          </div>

          {/* Remove Last Button */}
          {segments.length > 0 && (
            <button
              onClick={removeLastSegment}
              style={{
                height: '40px',
                padding: '0 16px',
                backgroundColor: '#E2E8F0',
                color: '#4A5568',
                border: 'none',
                borderRadius: '6px',
                fontSize: '14px',
                fontWeight: '500',
                cursor: 'pointer',
                fontFamily: 'Inter, sans-serif',
                marginRight: '12px'
              }}
            >
              Remove Last
            </button>
          )}
        </div>

        {/* Footer */}
        <div style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          marginTop: '32px'
        }}>
          <p style={{
            fontSize: '12px',
            color: '#718096',
            margin: 0,
            fontFamily: 'Inter, sans-serif'
          }}>
            Initial status is "Planned". All changes and notifications are recorded in audit log.
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
              disabled={isSubmitting || segments.length === 0 || !routeName.trim()}
              style={{
                height: '40px',
                padding: '0 24px',
                backgroundColor: '#38A169',
                color: 'white',
                border: 'none',
                borderRadius: '6px',
                fontSize: '14px',
                fontWeight: '500',
                cursor: (isSubmitting || segments.length === 0 || !routeName.trim()) ? 'not-allowed' : 'pointer',
                fontFamily: 'Inter, sans-serif',
                opacity: (isSubmitting || segments.length === 0 || !routeName.trim()) ? 0.6 : 1
              }}
            >
              {isSubmitting ? 'Creating...' : 'Create Route'}
            </button>
          </div>
        </div>
      </div>
    </DispatcherLayout>
  );
}
