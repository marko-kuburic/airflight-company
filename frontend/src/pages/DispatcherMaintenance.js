import React, { useState, useEffect } from 'react';
import { DispatcherLayout } from '../components/Layout/DispatcherLayout';
import { DispatcherSidebar } from '../components/Layout/DispatcherSidebar';
import { flightAPI, maintenanceAPI, aircraftAPI } from '../services/api';
import toast from 'react-hot-toast';

export default function DispatcherMaintenance() {
  const [services, setServices] = useState([]);
  const [aircraft, setAircraft] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filterStatus, setFilterStatus] = useState('ALL');

  useEffect(() => {
    const loadData = async () => {
      try {
        setLoading(true);
        const [servicesResponse, aircraftResponse] = await Promise.all([
          maintenanceAPI.getAllServices(),
          aircraftAPI.getAllAircraft()
        ]);
        
        setServices(servicesResponse.data);
        setAircraft(aircraftResponse.data);
      } catch (error) {
        console.error('Error loading data:', error);
        toast.error('Failed to load maintenance data');
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, []);

  const getStatusColor = (status) => {
    switch (status) {
      case 'SCHEDULED':
        return { backgroundColor: '#FEF3C7', color: '#92400E' }; // Yellow
      case 'IN_PROGRESS':
        return { backgroundColor: '#DBEAFE', color: '#1E40AF' }; // Blue
      case 'COMPLETED':
        return { backgroundColor: '#D1FAE5', color: '#065F46' }; // Green
      case 'CANCELLED':
        return { backgroundColor: '#FEE2E2', color: '#991B1B' }; // Red
      default:
        return { backgroundColor: '#F3F4F6', color: '#374151' }; // Gray
    }
  };

  const getAircraftStatusColor = (status) => {
    switch (status) {
      case 'AVAILABLE':
        return { backgroundColor: '#D1FAE5', color: '#065F46' }; // Green
      case 'ACTIVE':
        return { backgroundColor: '#FEE2E2', color: '#991B1B' }; // Red
      case 'MAINTENANCE':
        return { backgroundColor: '#FEF3C7', color: '#92400E' }; // Yellow
      case 'RETIRED':
        return { backgroundColor: '#F3F4F6', color: '#6B7280' }; // Gray
      default:
        return { backgroundColor: '#F3F4F6', color: '#374151' }; // Gray
    }
  };

  const filteredServices = filterStatus === 'ALL' 
    ? services 
    : services.filter(service => service.status === filterStatus);

  const getAircraftInfo = (aircraftId) => {
    return aircraft.find(a => a.id === aircraftId) || { registration: 'Unknown', status: 'UNKNOWN' };
  };

  if (loading) {
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
            Loading maintenance records...
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
          Maintenance Overview
        </h1>

        {/* Summary Cards */}
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
          gap: '16px',
          marginBottom: '24px'
        }}>
          <div style={{
            backgroundColor: 'white',
            borderRadius: '8px',
            padding: '20px',
            boxShadow: '0 1px 3px rgba(0, 0, 0, 0.1)'
          }}>
            <div style={{
              fontSize: '24px',
              fontWeight: 'bold',
              color: '#1A202C',
              marginBottom: '4px',
              fontFamily: 'Inter, sans-serif'
            }}>
              {services.length}
            </div>
            <div style={{
              fontSize: '14px',
              color: '#718096',
              fontFamily: 'Inter, sans-serif'
            }}>
              Total Services
            </div>
          </div>
          
          <div style={{
            backgroundColor: 'white',
            borderRadius: '8px',
            padding: '20px',
            boxShadow: '0 1px 3px rgba(0, 0, 0, 0.1)'
          }}>
            <div style={{
              fontSize: '24px',
              fontWeight: 'bold',
              color: '#1A202C',
              marginBottom: '4px',
              fontFamily: 'Inter, sans-serif'
            }}>
              {services.filter(s => s.status === 'IN_PROGRESS').length}
            </div>
            <div style={{
              fontSize: '14px',
              color: '#718096',
              fontFamily: 'Inter, sans-serif'
            }}>
              In Progress
            </div>
          </div>

          <div style={{
            backgroundColor: 'white',
            borderRadius: '8px',
            padding: '20px',
            boxShadow: '0 1px 3px rgba(0, 0, 0, 0.1)'
          }}>
            <div style={{
              fontSize: '24px',
              fontWeight: 'bold',
              color: '#1A202C',
              marginBottom: '4px',
              fontFamily: 'Inter, sans-serif'
            }}>
              {aircraft.filter(a => a.status === 'MAINTENANCE').length}
            </div>
            <div style={{
              fontSize: '14px',
              color: '#718096',
              fontFamily: 'Inter, sans-serif'
            }}>
              Aircraft in Maintenance
            </div>
          </div>
        </div>

        {/* Services Table */}
        <div style={{
          backgroundColor: 'white',
          borderRadius: '12px',
          padding: '24px',
          boxShadow: '0 1px 3px rgba(0, 0, 0, 0.1)'
        }}>
          <div style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            marginBottom: '20px'
          }}>
            <h2 style={{
              fontSize: '18px',
              fontWeight: '600',
              color: '#1A202C',
              margin: 0,
              fontFamily: 'Inter, sans-serif'
            }}>
              Service Records
            </h2>
            
            <select
              value={filterStatus}
              onChange={(e) => setFilterStatus(e.target.value)}
              style={{
                padding: '8px 12px',
                border: '1px solid #D1D5DB',
                borderRadius: '6px',
                fontSize: '14px',
                fontFamily: 'Inter, sans-serif'
              }}
            >
              <option value="ALL">All Statuses</option>
              <option value="SCHEDULED">Scheduled</option>
              <option value="IN_PROGRESS">In Progress</option>
              <option value="COMPLETED">Completed</option>
              <option value="CANCELLED">Cancelled</option>
            </select>
          </div>

          {filteredServices.length === 0 ? (
            <div style={{
              textAlign: 'center',
              padding: '40px',
              color: '#718096',
              fontFamily: 'Inter, sans-serif'
            }}>
              No service records found.
            </div>
          ) : (
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
                      padding: '12px',
                      textAlign: 'left',
                      fontSize: '12px',
                      fontWeight: '600',
                      color: '#4A5568',
                      borderBottom: '1px solid #E2E8F0'
                    }}>Aircraft</th>
                    <th style={{
                      padding: '12px',
                      textAlign: 'left',
                      fontSize: '12px',
                      fontWeight: '600',
                      color: '#4A5568',
                      borderBottom: '1px solid #E2E8F0'
                    }}>Service Type</th>
                    <th style={{
                      padding: '12px',
                      textAlign: 'left',
                      fontSize: '12px',
                      fontWeight: '600',
                      color: '#4A5568',
                      borderBottom: '1px solid #E2E8F0'
                    }}>Description</th>
                    <th style={{
                      padding: '12px',
                      textAlign: 'left',
                      fontSize: '12px',
                      fontWeight: '600',
                      color: '#4A5568',
                      borderBottom: '1px solid #E2E8F0'
                    }}>Status</th>
                    <th style={{
                      padding: '12px',
                      textAlign: 'left',
                      fontSize: '12px',
                      fontWeight: '600',
                      color: '#4A5568',
                      borderBottom: '1px solid #E2E8F0'
                    }}>Start Date</th>
                    <th style={{
                      padding: '12px',
                      textAlign: 'left',
                      fontSize: '12px',
                      fontWeight: '600',
                      color: '#4A5568',
                      borderBottom: '1px solid #E2E8F0'
                    }}>End Date</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredServices.map((service) => {
                    const aircraftInfo = getAircraftInfo(service.aircraftId);
                    return (
                      <tr key={service.id}>
                        <td style={{
                          padding: '12px',
                          fontSize: '14px',
                          color: '#2D3748',
                          borderBottom: '1px solid #E2E8F0'
                        }}>
                          <div style={{ fontWeight: '500' }}>
                            {aircraftInfo.registration}
                          </div>
                          <div style={{
                            fontSize: '12px',
                            color: '#718096',
                            marginTop: '2px'
                          }}>
                            <span style={{
                              ...getAircraftStatusColor(aircraftInfo.status),
                              padding: '2px 6px',
                              borderRadius: '4px',
                              fontSize: '10px',
                              fontWeight: '500'
                            }}>
                              {aircraftInfo.status}
                            </span>
                          </div>
                        </td>
                        <td style={{
                          padding: '12px',
                          fontSize: '14px',
                          color: '#2D3748',
                          borderBottom: '1px solid #E2E8F0'
                        }}>
                          {service.serviceType}
                        </td>
                        <td style={{
                          padding: '12px',
                          fontSize: '14px',
                          color: '#2D3748',
                          borderBottom: '1px solid #E2E8F0',
                          maxWidth: '200px',
                          overflow: 'hidden',
                          textOverflow: 'ellipsis',
                          whiteSpace: 'nowrap'
                        }}>
                          {service.description}
                        </td>
                        <td style={{
                          padding: '12px',
                          fontSize: '14px',
                          borderBottom: '1px solid #E2E8F0'
                        }}>
                          <span style={{
                            ...getStatusColor(service.status),
                            padding: '4px 8px',
                            borderRadius: '6px',
                            fontSize: '12px',
                            fontWeight: '500'
                          }}>
                            {service.status}
                          </span>
                        </td>
                        <td style={{
                          padding: '12px',
                          fontSize: '14px',
                          color: '#2D3748',
                          borderBottom: '1px solid #E2E8F0'
                        }}>
                          {new Date(service.startDate).toLocaleDateString()}
                        </td>
                        <td style={{
                          padding: '12px',
                          fontSize: '14px',
                          color: '#2D3748',
                          borderBottom: '1px solid #E2E8F0'
                        }}>
                          {service.endDate ? new Date(service.endDate).toLocaleDateString() : 'N/A'}
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </DispatcherLayout>
  );
}

