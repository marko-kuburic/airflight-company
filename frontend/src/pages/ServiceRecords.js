import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { DispatcherLayout } from '../components/Layout/DispatcherLayout';
import { TechnicianSidebar } from '../components/Layout/TechnicianSidebar';
import { maintenanceAPI, aircraftAPI } from '../services/api';
import toast from 'react-hot-toast';

export default function ServiceRecords() {
  const navigate = useNavigate();
  const [aircraftStatus, setAircraftStatus] = useState([]);
  const [recentServices, setRecentServices] = useState([]);
  const [loading, setLoading] = useState(true);

  const [alerts, setAlerts] = useState([]);

  // Fetch data from API
  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        
        // Fetch aircraft data
        const aircraftResponse = await aircraftAPI.getAllAircraft();
        const aircraftData = aircraftResponse.data.map(aircraft => ({
          id: aircraft.id,
          aircraft: aircraft.registration,
          status: aircraft.status,
          statusColor: getStatusColor(aircraft.status)
        }));
        setAircraftStatus(aircraftData);
        
        // Fetch recent services
        const servicesResponse = await maintenanceAPI.getAllServices();
        const servicesData = servicesResponse.data.slice(0, 10).map(service => ({
          id: service.id,
          aircraft: service.aircraftModel,
          description: service.description,
          startDate: new Date(service.startDate).toLocaleDateString('sr-RS'),
          endDate: service.endDate ? new Date(service.endDate).toLocaleDateString('sr-RS') : 'Ongoing',
          type: service.serviceType
        }));
        setRecentServices(servicesData);
        
        // Generate alerts based on services
        const serviceAlerts = servicesResponse.data
          .filter(service => service.status === 'IN_PROGRESS')
          .map((service, index) => ({
            id: index + 1,
            type: 'warning',
            message: `${service.aircraftModel} set to "In Service" — service in progress`,
            color: '#E53E3E'
          }));
        setAlerts(serviceAlerts);
        
      } catch (error) {
        console.error('Error fetching data:', error);
        toast.error('Failed to load service records');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const getStatusColor = (status) => {
    switch (status) {
      case 'AVAILABLE':
        return '#48BB78'; // Green
      case 'ACTIVE':
        return '#E53E3E'; // Red
      case 'MAINTENANCE':
        return '#F59E0B'; // Orange
      case 'RETIRED':
        return '#718096'; // Gray
      default:
        return '#718096'; // Gray
    }
  };

  const handleStatusChange = async (aircraftId, newStatus) => {
    try {
      await aircraftAPI.updateAircraftStatus(aircraftId, newStatus);
      setAircraftStatus(prev => 
        prev.map(aircraft => 
          aircraft.id === aircraftId 
            ? { ...aircraft, status: newStatus, statusColor: getStatusColor(newStatus) }
            : aircraft
        )
      );
      toast.success(`Aircraft status updated to ${newStatus}`);
    } catch (error) {
      console.error('Error updating aircraft status:', error);
      toast.error('Failed to update aircraft status');
    }
  };

  if (loading) {
    return (
      <DispatcherLayout>
        <TechnicianSidebar />
        <div style={{
          flex: 1,
          backgroundColor: '#F7FAFC',
          overflow: 'auto',
          padding: '20px',
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center'
        }}>
          <div style={{
            fontSize: '18px',
            color: '#4A5568'
          }}>
            Loading service records...
          </div>
        </div>
      </DispatcherLayout>
    );
  }

  return (
    <DispatcherLayout>
      <TechnicianSidebar />
      
      {/* Main Content */}
      <div style={{
        flex: 1,
        backgroundColor: '#F7FAFC',
        overflow: 'auto',
        padding: '20px'
      }}>
        {/* Header */}
        <div style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          marginBottom: '24px'
        }}>
          <h1 style={{
            fontSize: '28px',
            fontWeight: 'bold',
            color: '#2D3748',
            margin: 0,
            fontFamily: 'Inter, sans-serif'
          }}>
            Service Records
          </h1>
          
          <button
            onClick={() => navigate('/technician/add-service')}
            style={{
              backgroundColor: '#3182CE',
              color: 'white',
              padding: '12px 24px',
              borderRadius: '8px',
              border: 'none',
              fontSize: '14px',
              fontWeight: '500',
              cursor: 'pointer',
              fontFamily: 'Inter, sans-serif'
            }}
          >
            Add Service
          </button>
        </div>

        {/* Alerts */}
        <div style={{ marginBottom: '24px' }}>
          {alerts.map(alert => (
            <div
              key={alert.id}
              style={{
                padding: '12px 16px',
                borderRadius: '8px',
                marginBottom: '8px',
                backgroundColor: alert.type === 'warning' ? '#FED7D7' : '#C6F6D5',
                borderLeft: `4px solid ${alert.color}`,
                color: '#2D3748',
                fontSize: '14px',
                fontFamily: 'Inter, sans-serif'
              }}
            >
              <strong style={{ color: alert.color }}>
                {alert.type === 'warning' ? 'Warning:' : 'Notice:'}
              </strong> {alert.message}
            </div>
          ))}
        </div>

        {/* Aircraft Status Card */}
        <div style={{
          backgroundColor: 'white',
          borderRadius: '12px',
          padding: '24px',
          marginBottom: '24px',
          boxShadow: '0 1px 3px 0 rgba(0, 0, 0, 0.1)'
        }}>
          <h2 style={{
            fontSize: '20px',
            fontWeight: '600',
            color: '#2D3748',
            marginBottom: '16px',
            fontFamily: 'Inter, sans-serif'
          }}>
            Aircraft Status
          </h2>
          
          <div style={{ overflow: 'hidden', borderRadius: '8px' }}>
            <table style={{
              width: '100%',
              borderCollapse: 'collapse',
              fontFamily: 'Inter, sans-serif'
            }}>
              <thead>
                <tr style={{ backgroundColor: '#F7FAFC' }}>
                  <th style={{
                    padding: '12px 16px',
                    textAlign: 'left',
                    fontSize: '14px',
                    fontWeight: '500',
                    color: '#4A5568',
                    borderBottom: '1px solid #E2E8F0'
                  }}>
                    Aircraft
                  </th>
                  <th style={{
                    padding: '12px 16px',
                    textAlign: 'left',
                    fontSize: '14px',
                    fontWeight: '500',
                    color: '#4A5568',
                    borderBottom: '1px solid #E2E8F0'
                  }}>
                    Current Status
                  </th>
                  <th style={{
                    padding: '12px 16px',
                    textAlign: 'left',
                    fontSize: '14px',
                    fontWeight: '500',
                    color: '#4A5568',
                    borderBottom: '1px solid #E2E8F0'
                  }}>
                    Change
                  </th>
                </tr>
              </thead>
              <tbody>
                {aircraftStatus.map(aircraft => (
                  <tr key={aircraft.id} style={{ borderBottom: '1px solid #E2E8F0' }}>
                    <td style={{
                      padding: '12px 16px',
                      fontSize: '14px',
                      color: '#2D3748'
                    }}>
                      {aircraft.aircraft}
                    </td>
                    <td style={{
                      padding: '12px 16px',
                      fontSize: '14px'
                    }}>
                      <span style={{
                        backgroundColor: aircraft.statusColor,
                        color: 'white',
                        padding: '4px 12px',
                        borderRadius: '20px',
                        fontSize: '12px',
                        fontWeight: '500'
                      }}>
                        {aircraft.status}
                      </span>
                    </td>
                    <td style={{
                      padding: '12px 16px',
                      fontSize: '14px'
                    }}>
                      <select
                        value={aircraft.status}
                        onChange={(e) => handleStatusChange(aircraft.id, e.target.value)}
                        style={{
                          padding: '6px 12px',
                          borderRadius: '6px',
                          border: '1px solid #E2E8F0',
                          fontSize: '12px',
                          backgroundColor: 'white',
                          cursor: 'pointer'
                        }}
                      >
                        <option value="Active">Active</option>
                        <option value="In Service">In Service</option>
                        <option value="Maintenance">Maintenance</option>
                        <option value="Out of Service">Out of Service</option>
                      </select>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* Recent Services Card */}
        <div style={{
          backgroundColor: 'white',
          borderRadius: '12px',
          padding: '24px',
          boxShadow: '0 1px 3px 0 rgba(0, 0, 0, 0.1)'
        }}>
          <h2 style={{
            fontSize: '20px',
            fontWeight: '600',
            color: '#2D3748',
            marginBottom: '16px',
            fontFamily: 'Inter, sans-serif'
          }}>
            Recent Services
          </h2>
          
          <div style={{ overflow: 'hidden', borderRadius: '8px' }}>
            <table style={{
              width: '100%',
              borderCollapse: 'collapse',
              fontFamily: 'Inter, sans-serif'
            }}>
              <thead>
                <tr style={{ backgroundColor: '#F7FAFC' }}>
                  <th style={{
                    padding: '12px 16px',
                    textAlign: 'left',
                    fontSize: '14px',
                    fontWeight: '500',
                    color: '#4A5568',
                    borderBottom: '1px solid #E2E8F0'
                  }}>
                    Aircraft
                  </th>
                  <th style={{
                    padding: '12px 16px',
                    textAlign: 'left',
                    fontSize: '14px',
                    fontWeight: '500',
                    color: '#4A5568',
                    borderBottom: '1px solid #E2E8F0'
                  }}>
                    Start / End
                  </th>
                  <th style={{
                    padding: '12px 16px',
                    textAlign: 'left',
                    fontSize: '14px',
                    fontWeight: '500',
                    color: '#4A5568',
                    borderBottom: '1px solid #E2E8F0'
                  }}>
                    Type
                  </th>
                </tr>
              </thead>
              <tbody>
                {recentServices.map(service => (
                  <tr key={service.id} style={{ borderBottom: '1px solid #E2E8F0' }}>
                    <td style={{
                      padding: '12px 16px',
                      fontSize: '14px',
                      color: '#2D3748'
                    }}>
                      <div>
                        <div style={{ fontWeight: '500' }}>{service.aircraft}</div>
                        <div style={{ fontSize: '12px', color: '#718096' }}>{service.description}</div>
                      </div>
                    </td>
                    <td style={{
                      padding: '12px 16px',
                      fontSize: '14px',
                      color: '#2D3748'
                    }}>
                      {service.startDate} / {service.endDate}
                    </td>
                    <td style={{
                      padding: '12px 16px',
                      fontSize: '14px'
                    }}>
                      <span style={{
                        backgroundColor: service.type === 'Regular' ? '#C6F6D5' : '#FED7D7',
                        color: service.type === 'Regular' ? '#22543D' : '#742A2A',
                        padding: '4px 12px',
                        borderRadius: '20px',
                        fontSize: '12px',
                        fontWeight: '500'
                      }}>
                        {service.type}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* Footer Note */}
        <div style={{
          marginTop: '24px',
          padding: '16px',
          backgroundColor: '#EDF2F7',
          borderRadius: '8px',
          fontSize: '12px',
          color: '#718096',
          fontFamily: 'Inter, sans-serif',
          textAlign: 'center'
        }}>
          During service, aircraft is auto-unavailable for planning. Conflicts trigger alerts for dispatchers.
        </div>
      </div>
    </DispatcherLayout>
  );
}
