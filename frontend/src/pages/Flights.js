import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { DispatcherLayout } from '../components/Layout/DispatcherLayout';
import { DispatcherSidebar } from '../components/Layout/DispatcherSidebar';
import { flightAPI, aircraftAPI, maintenanceAPI } from '../services/api';
import toast from 'react-hot-toast';

export default function Flights() {
  const navigate = useNavigate();
  const [flights, setFlights] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    // Add a small delay to allow login process to complete
    const timer = setTimeout(() => {
      fetchFlights();
    }, 100);

    return () => clearTimeout(timer);
  }, []);

  const fetchFlights = async () => {
    try {
      setLoading(true);

      // Check authentication first
      const authToken = localStorage.getItem('authToken');
      const userData = localStorage.getItem('user');

      console.log('Flights page - Auth token:', authToken ? 'Present' : 'Missing');
      console.log('Flights page - User data:', userData ? 'Present' : 'Missing');

      // For now, make authentication optional for Flights page
      // You can remove this check if you want to allow access without login
      if (!authToken) {
        console.warn('No auth token found, but allowing access for now');
        // toast.error('Please log in to continue');
        // navigate('/login');
        // return;
      }

      // Load flights from the API
      console.log('Flights page - Making API call...');

      const headers = {};
      if (authToken) {
        headers['Authorization'] = `Bearer ${authToken}`;
      }

      const response = await fetch(`${process.env.REACT_APP_API_URL || 'http://localhost:8080/api'}/flights/management/all`, {
        headers
      });
      
      if (response.ok) {
        const data = await response.json();
        
        // Load routes and aircraft data
        const [routesResponse, aircraftResponse] = await Promise.all([
          fetch(`${process.env.REACT_APP_API_URL || 'http://localhost:8080/api'}/flight/routes`, { headers }),
          fetch(`${process.env.REACT_APP_API_URL || 'http://localhost:8080/api'}/flight/aircraft`, { headers })
        ]);
        
        const routesData = routesResponse.ok ? await routesResponse.json() : [];
        const aircraftData = aircraftResponse.ok ? await aircraftResponse.json() : [];
        
        // Create lookup maps
        const routesMap = {};
        routesData.forEach(route => {
          routesMap[route.id] = route.name;
        });
        
        const aircraftMap = {};
        aircraftData.forEach(aircraft => {
          aircraftMap[aircraft.id] = `${aircraft.model} (${aircraft.registration})`;
        });
        
        const flightsData = data.flights.map(flight => ({
          id: flight.id,
          flightNumber: flight.flightNumber,
          route: routesMap[flight.routeId] || 'Unknown Route',
          departureTime: flight.departureTime ? new Date(flight.departureTime).toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit' }) : 'N/A',
          arrivalTime: flight.arrivalTime ? new Date(flight.arrivalTime).toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit' }) : 'N/A',
          aircraft: aircraftMap[flight.aircraftId] || 'Unknown Aircraft',
          status: flight.status,
          // Store full datetime for date display
          departureDateTime: flight.departureTime,
          arrivalDateTime: flight.arrivalTime
        }));
        
        setFlights(flightsData);
        setError(null);
      } else {
        throw new Error('Failed to fetch flights');
      }
    } catch (err) {
      console.error('Error fetching flights:', err);
      setError('Failed to load flights data');
      toast.error('Failed to load flights data');
    } finally {
      setLoading(false);
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'SCHEDULED':
        return '#3182CE'; // Blue for Planned
      case 'BOARDING':
        return '#38A169'; // Green for Active
      case 'DEPARTED':
        return '#38A169'; // Green for Active
      case 'IN_FLIGHT':
        return '#38A169'; // Green for Active
      case 'LANDED':
        return '#718096'; // Gray for Completed
      case 'CANCELLED':
        return '#E53E3E'; // Red for Canceled
      case 'DELAYED':
        return '#DD6B20'; // Orange for Rescheduled
      default:
        return '#718096'; // Gray
    }
  };
  
  const getStatusBgColor = (status) => {
    switch (status) {
      case 'SCHEDULED':
        return '#EBF8FF'; // Light blue
      case 'BOARDING':
        return '#F0FFF4'; // Light green
      case 'DEPARTED':
        return '#F0FFF4'; // Light green
      case 'IN_FLIGHT':
        return '#F0FFF4'; // Light green
      case 'LANDED':
        return '#F7FAFC'; // Light gray
      case 'CANCELLED':
        return '#FFF5F5'; // Light red
      case 'DELAYED':
        return '#FFFAF0'; // Light orange
      default:
        return '#F7FAFC'; // Light gray
    }
  };

  const getStatusDisplayName = (status) => {
    switch (status) {
      case 'SCHEDULED':
        return 'Planned';
      case 'BOARDING':
        return 'Active';
      case 'DEPARTED':
        return 'Active';
      case 'IN_FLIGHT':
        return 'Active';
      case 'LANDED':
        return 'Completed';
      case 'CANCELLED':
        return 'Canceled';
      case 'DELAYED':
        return 'Rescheduled';
      default:
        return status;
    }
  };

  const handleEditFlight = (flightId) => {
    navigate(`/dispatcher/flights/edit/${flightId}`);
  };

  const handleDeleteFlight = async (flightId) => {
    if (!window.confirm('Are you sure you want to delete this flight?')) {
      return;
    }

    try {
      await flightAPI.deleteFlight(flightId);
      toast.success('Flight deleted successfully!');
      fetchFlights(); // Refresh the list
    } catch (error) {
      console.error('Error deleting flight:', error);
      toast.error('Failed to delete flight');
    }
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
          Flights — Today
        </h1>

        {/* Status Legend */}
        <div style={{
          display: 'flex',
          alignItems: 'center',
          gap: '24px',
          marginBottom: '16px',
          padding: '12px 16px',
          backgroundColor: 'white',
          borderRadius: '8px',
          boxShadow: '0 1px 3px rgba(0, 0, 0, 0.1)'
        }}>
          <span style={{
            fontSize: '14px',
            fontWeight: '500',
            color: '#4A5568',
            fontFamily: 'Inter, sans-serif'
          }}>
            Status Legend:
          </span>
          
          {/* Planned */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
            <div style={{
              width: '8px',
              height: '8px',
              borderRadius: '50%',
              backgroundColor: '#3182CE'
            }}></div>
            <span style={{
              fontSize: '12px',
              color: '#4A5568',
              fontFamily: 'Inter, sans-serif'
            }}>Planned</span>
          </div>

          {/* Active */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
            <div style={{
              width: '8px',
              height: '8px',
              borderRadius: '50%',
              backgroundColor: '#38A169'
            }}></div>
            <span style={{
              fontSize: '12px',
              color: '#4A5568',
              fontFamily: 'Inter, sans-serif'
            }}>Active</span>
          </div>

          {/* Completed */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
            <div style={{
              width: '8px',
              height: '8px',
              borderRadius: '50%',
              backgroundColor: '#718096'
            }}></div>
            <span style={{
              fontSize: '12px',
              color: '#4A5568',
              fontFamily: 'Inter, sans-serif'
            }}>Completed</span>
          </div>

          {/* Rescheduled */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
            <div style={{
              width: '8px',
              height: '8px',
              borderRadius: '50%',
              backgroundColor: '#DD6B20'
            }}></div>
            <span style={{
              fontSize: '12px',
              color: '#4A5568',
              fontFamily: 'Inter, sans-serif'
            }}>Rescheduled</span>
          </div>

          {/* Canceled */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
            <div style={{
              width: '8px',
              height: '8px',
              borderRadius: '50%',
              backgroundColor: '#E53E3E'
            }}></div>
            <span style={{
              fontSize: '12px',
              color: '#4A5568',
              fontFamily: 'Inter, sans-serif'
            }}>Canceled</span>
          </div>
        </div>

        {/* Alerts/Warnings */}
        <div style={{
          marginBottom: '24px'
        }}>
          <div style={{
            backgroundColor: '#FEF3C7',
            border: '1px solid #F59E0B',
            borderRadius: '8px',
            padding: '12px 16px',
            marginBottom: '8px'
          }}>
            <span style={{
              fontSize: '14px',
              color: '#92400E',
              fontFamily: 'Inter, sans-serif'
            }}>
              ▲ A320-11 in service 13:00-17:00 — auto-unavailable
            </span>
          </div>
          
          <div style={{
            backgroundColor: '#FEF3C7',
            border: '1px solid #F59E0B',
            borderRadius: '8px',
            padding: '12px 16px'
          }}>
            <span style={{
              fontSize: '14px',
              color: '#92400E',
              fontFamily: 'Inter, sans-serif'
            }}>
              ▲ Conflict: FD-804 overlaps maintenance — crew notified
            </span>
          </div>
        </div>

        {/* Loading State */}
        {loading && (
          <div style={{
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            height: '200px',
            fontSize: '16px',
            color: '#4A5568',
            fontFamily: 'Inter, sans-serif'
          }}>
            Loading flights data...
          </div>
        )}

        {/* Error State */}
        {error && (
          <div style={{
            backgroundColor: '#FED7D7',
            border: '1px solid #F56565',
            borderRadius: '8px',
            padding: '16px',
            marginBottom: '24px',
            color: '#C53030',
            fontSize: '14px',
            fontFamily: 'Inter, sans-serif'
          }}>
            {error}
            <button 
              onClick={fetchFlights}
              style={{
                marginLeft: '16px',
                backgroundColor: '#E53E3E',
                color: 'white',
                border: 'none',
                borderRadius: '4px',
                padding: '8px 16px',
                fontSize: '12px',
                cursor: 'pointer',
                fontFamily: 'Inter, sans-serif'
              }}
            >
              Retry
            </button>
          </div>
        )}

        {/* Flights Table */}
        {!loading && !error && (
          <div style={{
            backgroundColor: 'white',
            borderRadius: '12px',
            border: '1px solid #E2E8F0',
            overflow: 'hidden'
          }}>
            <table style={{
              width: '100%',
              borderCollapse: 'collapse',
              fontFamily: 'Inter, sans-serif'
            }}>
              <thead>
                <tr style={{
                  backgroundColor: '#F7FAFC',
                  borderBottom: '1px solid #E2E8F0'
                }}>
                  <th style={{
                    padding: '16px 24px',
                    textAlign: 'left',
                    fontSize: '14px',
                    fontWeight: '600',
                    color: '#4A5568',
                    borderBottom: '1px solid #E2E8F0'
                  }}>
                    Flight
                  </th>
                  <th style={{
                    padding: '16px 24px',
                    textAlign: 'left',
                    fontSize: '14px',
                    fontWeight: '600',
                    color: '#4A5568',
                    borderBottom: '1px solid #E2E8F0'
                  }}>
                    Date
                  </th>
                  <th style={{
                    padding: '16px 24px',
                    textAlign: 'left',
                    fontSize: '14px',
                    fontWeight: '600',
                    color: '#4A5568',
                    borderBottom: '1px solid #E2E8F0'
                  }}>
                    Route
                  </th>
                  <th style={{
                    padding: '16px 24px',
                    textAlign: 'left',
                    fontSize: '14px',
                    fontWeight: '600',
                    color: '#4A5568',
                    borderBottom: '1px solid #E2E8F0'
                  }}>
                    Dep/Arr
                  </th>
                  <th style={{
                    padding: '16px 24px',
                    textAlign: 'left',
                    fontSize: '14px',
                    fontWeight: '600',
                    color: '#4A5568',
                    borderBottom: '1px solid #E2E8F0'
                  }}>
                    Aircraft
                  </th>
                  <th style={{
                    padding: '16px 24px',
                    textAlign: 'left',
                    fontSize: '14px',
                    fontWeight: '600',
                    color: '#4A5568',
                    borderBottom: '1px solid #E2E8F0'
                  }}>
                    Status
                  </th>
                  <th style={{
                    padding: '16px 24px',
                    textAlign: 'left',
                    fontSize: '14px',
                    fontWeight: '600',
                    color: '#4A5568',
                    borderBottom: '1px solid #E2E8F0'
                  }}>
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody>
                {flights.length === 0 ? (
                  <tr>
                    <td colSpan="7" style={{
                      padding: '32px',
                      textAlign: 'center',
                      fontSize: '14px',
                      color: '#718096',
                      fontFamily: 'Inter, sans-serif'
                    }}>
                      No flights found for today
                    </td>
                  </tr>
                ) : (
                  flights.map((flight, index) => (
                    <tr key={flight.id} style={{
                      borderBottom: index < flights.length - 1 ? '1px solid #E2E8F0' : 'none'
                    }}>
                      <td style={{
                        padding: '16px 24px',
                        fontSize: '14px',
                        color: '#2D3748',
                        fontWeight: '500'
                      }}>
                        {flight.flightNumber}
                      </td>
                      <td style={{
                        padding: '16px 24px',
                        fontSize: '14px',
                        color: '#4A5568'
                      }}>
                        {flight.departureDateTime ? new Date(flight.departureDateTime).toLocaleDateString() : 'N/A'}
                      </td>
                      <td style={{
                        padding: '16px 24px',
                        fontSize: '14px',
                        color: '#4A5568'
                      }}>
                        {flight.route}
                      </td>
                      <td style={{
                        padding: '16px 24px',
                        fontSize: '14px',
                        color: '#4A5568'
                      }}>
                        {flight.departureTime} / {flight.arrivalTime}
                      </td>
                      <td style={{
                        padding: '16px 24px',
                        fontSize: '14px',
                        color: '#4A5568'
                      }}>
                        {flight.aircraft}
                      </td>
                      <td style={{
                        padding: '16px 24px'
                      }}>
                        <span style={{
                          backgroundColor: getStatusBgColor(flight.status),
                          color: getStatusColor(flight.status),
                          padding: '6px 12px',
                          borderRadius: '20px',
                          fontSize: '12px',
                          fontWeight: '500',
                          fontFamily: 'Inter, sans-serif'
                        }}>
                          {getStatusDisplayName(flight.status)}
                        </span>
                      </td>
                      <td style={{
                        padding: '16px 24px'
                      }}>
                        <div style={{ display: 'flex', gap: '8px' }}>
                          <button 
                            onClick={() => handleEditFlight(flight.id)}
                            style={{
                              backgroundColor: '#3182CE',
                              color: 'white',
                              border: 'none',
                              borderRadius: '6px',
                              padding: '8px 16px',
                              fontSize: '12px',
                              fontWeight: '500',
                              cursor: 'pointer',
                              fontFamily: 'Inter, sans-serif',
                              transition: 'opacity 0.2s ease'
                            }}
                            onMouseEnter={(e) => e.target.style.opacity = '0.9'}
                            onMouseLeave={(e) => e.target.style.opacity = '1'}
                          >
                            Edit
                          </button>
                          <button 
                            onClick={() => handleDeleteFlight(flight.id)}
                            style={{
                              backgroundColor: '#E53E3E',
                              color: 'white',
                              border: 'none',
                              borderRadius: '6px',
                              padding: '8px 16px',
                              fontSize: '12px',
                              fontWeight: '500',
                              cursor: 'pointer',
                              fontFamily: 'Inter, sans-serif',
                              transition: 'opacity 0.2s ease'
                            }}
                            onMouseEnter={(e) => e.target.style.opacity = '0.9'}
                            onMouseLeave={(e) => e.target.style.opacity = '1'}
                          >
                            Delete
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </DispatcherLayout>
  );
}
