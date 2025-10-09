import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { DispatcherLayout } from '../components/Layout/DispatcherLayout';
import { DispatcherSidebar } from '../components/Layout/DispatcherSidebar';
import { aircraftAPI } from '../services/api';
import toast from 'react-hot-toast';

export default function AircraftManagement() {
  const navigate = useNavigate();
  const [aircrafts, setAircrafts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchAircrafts();
  }, []);

  const fetchAircrafts = async () => {
    try {
      setLoading(true);
      const response = await aircraftAPI.getAllAircraft();
      setAircrafts(response.data);
      setError(null);
    } catch (err) {
      console.error('Error fetching aircrafts:', err);
      setError('Failed to load aircraft data');
      toast.error('Failed to load aircraft data');
    } finally {
      setLoading(false);
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'ACTIVE':
        return '#3182CE'; // Blue
      case 'AVAILABLE':
        return '#38A169'; // Green
      case 'MAINTENANCE':
        return '#E53E3E'; // Red
      case 'RETIRED':
        return '#718096'; // Gray
      default:
        return '#718096'; // Gray
    }
  };
  
  const getStatusBgColor = (status) => {
    switch (status) {
      case 'ACTIVE':
        return '#EBF8FF'; // Light blue
      case 'AVAILABLE':
        return '#F0FFF4'; // Light green
      case 'MAINTENANCE':
        return '#FFF5F5'; // Light red
      case 'RETIRED':
        return '#F7FAFC'; // Light gray
      default:
        return '#F7FAFC'; // Light gray
    }
  };
  

  const getStatusDisplayName = (status) => {
    switch (status) {
      case 'ACTIVE':
        return 'Active';
      case 'MAINTENANCE':
        return 'Maintenance';
      case 'AVAILABLE':
        return 'Available';
      case 'RETIRED':
        return 'Retired';
      default:
        return status;
    }
  };

  return (
    <DispatcherLayout>
      <DispatcherSidebar />
      <main style={{
        flex: 1,
        backgroundColor: '#FFFFFF',
        overflow: 'auto',
        height: '100vh',
        padding: '32px'
      }}>
        {/* Header */}
        <div style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          marginBottom: '32px'
        }}>
          <h1 style={{
            fontSize: '28px',
            fontWeight: 'bold',
            color: '#1A202C',
            margin: 0,
            fontFamily: 'Inter, sans-serif'
          }}>
            Aircrafts
          </h1>
          
          <button 
            onClick={() => navigate('/dispatcher/aircrafts/add')}
            style={{
              backgroundColor: '#38A169',
              color: 'white',
              border: 'none',
              borderRadius: '8px',
              padding: '12px 24px',
              fontSize: '14px',
              fontWeight: '500',
              cursor: 'pointer',
              fontFamily: 'Inter, sans-serif',
              transition: 'opacity 0.2s ease'
            }}
            onMouseEnter={(e) => e.target.style.opacity = '0.9'}
            onMouseLeave={(e) => e.target.style.opacity = '1'}
          >
            Add Aircraft
          </button>
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
            Loading aircraft data...
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
              onClick={fetchAircrafts}
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

        {/* Aircraft Table */}
        {!loading && !error && (
          <div style={{
            backgroundColor: '#FFFFFF',
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
                    Registration
                  </th>
                  <th style={{
                    padding: '16px 24px',
                    textAlign: 'left',
                    fontSize: '14px',
                    fontWeight: '600',
                    color: '#4A5568',
                    borderBottom: '1px solid #E2E8F0'
                  }}>
                    Model
                  </th>
                  <th style={{
                    padding: '16px 24px',
                    textAlign: 'left',
                    fontSize: '14px',
                    fontWeight: '600',
                    color: '#4A5568',
                    borderBottom: '1px solid #E2E8F0'
                  }}>
                    Capacity
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
                {aircrafts.length === 0 ? (
                  <tr>
                    <td colSpan="5" style={{
                      padding: '32px',
                      textAlign: 'center',
                      fontSize: '14px',
                      color: '#718096',
                      fontFamily: 'Inter, sans-serif'
                    }}>
                      No aircraft found
                    </td>
                  </tr>
                ) : (
                  aircrafts.map((aircraft, index) => (
                    <tr key={aircraft.id} style={{
                      borderBottom: index < aircrafts.length - 1 ? '1px solid #E2E8F0' : 'none'
                    }}>
                      <td style={{
                        padding: '16px 24px',
                        fontSize: '14px',
                        color: '#2D3748',
                        fontWeight: '500'
                      }}>
                        {aircraft.registration}
                      </td>
                      <td style={{
                        padding: '16px 24px',
                        fontSize: '14px',
                        color: '#4A5568'
                      }}>
                        {aircraft.model}
                      </td>
                      <td style={{
                        padding: '16px 24px',
                        fontSize: '14px',
                        color: '#4A5568'
                      }}>
                        {aircraft.capacity}
                      </td>
                      <td style={{
                        padding: '16px 24px'
                      }}>
                        <span style={{
                          backgroundColor: getStatusBgColor(aircraft.status),
                          color: getStatusColor(aircraft.status),
                          padding: '6px 12px',
                          borderRadius: '20px',
                          fontSize: '12px',
                          fontWeight: '500',
                          fontFamily: 'Inter, sans-serif'
                        }}>
                          {getStatusDisplayName(aircraft.status)}
                        </span>
                      </td>
                      <td style={{
                        padding: '16px 24px'
                      }}>
                        <button 
                          onClick={() => navigate(`/dispatcher/aircrafts/edit/${aircraft.registration}`)}
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
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        )}

        {/* Status Note */}
        <div style={{
          marginTop: '24px',
          fontSize: '14px',
          color: '#718096',
          fontFamily: 'Inter, sans-serif'
        }}>
          Status controls flight availability. 'In Service' aircraft are auto-unavailable for scheduling.
        </div>
      </main>
    </DispatcherLayout>
  );
}
