import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { DispatcherLayout } from '../components/Layout/DispatcherLayout';
import { TechnicianSidebar } from '../components/Layout/TechnicianSidebar';
import { maintenanceAPI, aircraftAPI } from '../services/api';
import toast from 'react-hot-toast';

export default function AddService() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    aircraft: '',
    serviceType: '',
    startDateTime: '',
    endDateTime: '',
    description: '',
    setInService: true
  });

  const [aircraftOptions, setAircraftOptions] = useState([]);
  const [loading, setLoading] = useState(false);

  // Fetch aircraft options from API
  useEffect(() => {
    const fetchAircraft = async () => {
      try {
        const response = await aircraftAPI.getAllAircraft();
        const aircraftData = response.data.map(aircraft => ({
          id: aircraft.id,
          registration: aircraft.registration,
          model: aircraft.model
        }));
        setAircraftOptions(aircraftData);
      } catch (error) {
        console.error('Error fetching aircraft:', error);
        toast.error('Failed to load aircraft list');
      }
    };

    fetchAircraft();
  }, []);

  const serviceTypes = [
    'Scheduled',
    'Unscheduled',
    'Inspection'
  ];

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // Basic validation
    if (!formData.aircraft || !formData.serviceType || !formData.startDateTime || !formData.endDateTime) {
      toast.error('Please fill in all required fields');
      return;
    }

    // Check if end date is after start date
    if (new Date(formData.endDateTime) <= new Date(formData.startDateTime)) {
      toast.error('End date must be after start date');
      return;
    }

    try {
      setLoading(true);
      
      // Get current user (technician) ID from localStorage
      const user = JSON.parse(localStorage.getItem('user') || '{}');
      if (!user.id) {
        toast.error('User not found. Please login again.');
        navigate('/login');
        return;
      }

      // Prepare service data for API
      const serviceData = {
        aircraftId: parseInt(formData.aircraft),
        technicianId: user.id,
        serviceType: formData.serviceType.toUpperCase(),
        description: formData.description || 'Service maintenance',
        startDate: formData.startDateTime,
        endDate: formData.endDateTime,
        status: 'SCHEDULED'
      };

      console.log('Creating service with data:', serviceData);
      
      // Create service via API
      await maintenanceAPI.createService(serviceData);
      
      // Update aircraft status if requested
      if (formData.setInService) {
        await aircraftAPI.updateAircraftStatus(parseInt(formData.aircraft), 'MAINTENANCE');
      }
      
      toast.success('Service record added successfully!');
      
      // Reset form
      setFormData({
        aircraft: '',
        serviceType: '',
        startDateTime: '',
        endDateTime: '',
        description: '',
        setInService: true
      });
      
      // Navigate back to service records
      setTimeout(() => {
        navigate('/technician/records');
      }, 1500);
      
    } catch (error) {
      console.error('Error creating service:', error);
      toast.error('Failed to create service record');
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    navigate('/technician/records');
  };

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
          marginBottom: '24px'
        }}>
          <h1 style={{
            fontSize: '28px',
            fontWeight: 'bold',
            color: '#2D3748',
            margin: 0,
            fontFamily: 'Inter, sans-serif'
          }}>
            Add Service
          </h1>
        </div>

        {/* Form Card */}
        <div style={{
          backgroundColor: 'white',
          borderRadius: '12px',
          padding: '24px',
          boxShadow: '0 1px 3px 0 rgba(0, 0, 0, 0.1)',
          maxWidth: '600px'
        }}>
          <h2 style={{
            fontSize: '20px',
            fontWeight: '600',
            color: '#2D3748',
            marginBottom: '24px',
            fontFamily: 'Inter, sans-serif'
          }}>
            Service Details
          </h2>

          <form onSubmit={handleSubmit}>
            {/* Form Fields */}
            <div style={{
              display: 'grid',
              gridTemplateColumns: '1fr 1fr',
              gap: '16px',
              marginBottom: '16px'
            }}>
              {/* Aircraft */}
              <div>
                <label style={{
                  display: 'block',
                  fontSize: '14px',
                  fontWeight: '500',
                  color: '#4A5568',
                  marginBottom: '6px',
                  fontFamily: 'Inter, sans-serif'
                }}>
                  Aircraft
                </label>
                <select
                  name="aircraft"
                  value={formData.aircraft}
                  onChange={handleInputChange}
                  required
                  disabled={loading}
                  style={{
                    width: '100%',
                    padding: '12px',
                    borderRadius: '8px',
                    border: '1px solid #E2E8F0',
                    fontSize: '14px',
                    backgroundColor: 'white',
                    fontFamily: 'Inter, sans-serif',
                    opacity: loading ? 0.6 : 1
                  }}
                >
                  <option value="">Select aircraft ▾</option>
                  {aircraftOptions.map(aircraft => (
                    <option key={aircraft.id} value={aircraft.id}>
                      {aircraft.registration} - {aircraft.model}
                    </option>
                  ))}
                </select>
              </div>

              {/* Service Type */}
              <div>
                <label style={{
                  display: 'block',
                  fontSize: '14px',
                  fontWeight: '500',
                  color: '#4A5568',
                  marginBottom: '6px',
                  fontFamily: 'Inter, sans-serif'
                }}>
                  Service Type
                </label>
                <select
                  name="serviceType"
                  value={formData.serviceType}
                  onChange={handleInputChange}
                  required
                  style={{
                    width: '100%',
                    padding: '12px',
                    borderRadius: '8px',
                    border: '1px solid #E2E8F0',
                    fontSize: '14px',
                    backgroundColor: 'white',
                    fontFamily: 'Inter, sans-serif'
                  }}
                >
                  <option value="">Scheduled / Unscheduled / Inspection ▾</option>
                  {serviceTypes.map(type => (
                    <option key={type} value={type}>{type}</option>
                  ))}
                </select>
              </div>

              {/* Start Date & Time */}
              <div>
                <label style={{
                  display: 'block',
                  fontSize: '14px',
                  fontWeight: '500',
                  color: '#4A5568',
                  marginBottom: '6px',
                  fontFamily: 'Inter, sans-serif'
                }}>
                  Start (date & time)
                </label>
                <input
                  type="datetime-local"
                  name="startDateTime"
                  value={formData.startDateTime}
                  onChange={handleInputChange}
                  required
                  style={{
                    width: '100%',
                    padding: '12px',
                    borderRadius: '8px',
                    border: '1px solid #E2E8F0',
                    fontSize: '14px',
                    fontFamily: 'Inter, sans-serif'
                  }}
                />
              </div>

              {/* End Date & Time */}
              <div>
                <label style={{
                  display: 'block',
                  fontSize: '14px',
                  fontWeight: '500',
                  color: '#4A5568',
                  marginBottom: '6px',
                  fontFamily: 'Inter, sans-serif'
                }}>
                  End (date & time)
                </label>
                <input
                  type="datetime-local"
                  name="endDateTime"
                  value={formData.endDateTime}
                  onChange={handleInputChange}
                  required
                  style={{
                    width: '100%',
                    padding: '12px',
                    borderRadius: '8px',
                    border: '1px solid #E2E8F0',
                    fontSize: '14px',
                    fontFamily: 'Inter, sans-serif'
                  }}
                />
              </div>
            </div>

            {/* Description */}
            <div style={{ marginBottom: '16px' }}>
              <label style={{
                display: 'block',
                fontSize: '14px',
                fontWeight: '500',
                color: '#4A5568',
                marginBottom: '6px',
                fontFamily: 'Inter, sans-serif'
              }}>
                Short Description
              </label>
              <textarea
                name="description"
                value={formData.description}
                onChange={handleInputChange}
                placeholder="Describe service briefly..."
                rows={3}
                style={{
                  width: '100%',
                  padding: '12px',
                  borderRadius: '8px',
                  border: '1px solid #E2E8F0',
                  fontSize: '14px',
                  fontFamily: 'Inter, sans-serif',
                  resize: 'vertical'
                }}
              />
            </div>

            {/* Checkbox */}
            <div style={{
              display: 'flex',
              alignItems: 'flex-start',
              gap: '12px',
              marginBottom: '16px'
            }}>
              <input
                type="checkbox"
                id="setInService"
                name="setInService"
                checked={formData.setInService}
                onChange={handleInputChange}
                style={{
                  width: '18px',
                  height: '18px',
                  accentColor: '#3182CE',
                  marginTop: '2px'
                }}
              />
              <div>
                <label htmlFor="setInService" style={{
                  fontSize: '14px',
                  fontWeight: '500',
                  color: '#4A5568',
                  fontFamily: 'Inter, sans-serif',
                  cursor: 'pointer'
                }}>
                  Set aircraft status to 'In Service' during this period
                </label>
                <div style={{
                  fontSize: '12px',
                  color: '#718096',
                  marginTop: '4px',
                  fontFamily: 'Inter, sans-serif'
                }}>
                  During service the aircraft is auto-unavailable. Conflicts will trigger alerts to dispatchers.
                </div>
              </div>
            </div>

            {/* Action Buttons */}
            <div style={{
              display: 'flex',
              gap: '12px',
              justifyContent: 'flex-end',
              marginTop: '24px'
            }}>
              <button
                type="button"
                onClick={handleCancel}
                style={{
                  padding: '12px 24px',
                  borderRadius: '8px',
                  border: '1px solid #E2E8F0',
                  backgroundColor: 'white',
                  color: '#4A5568',
                  fontSize: '14px',
                  fontWeight: '500',
                  cursor: 'pointer',
                  fontFamily: 'Inter, sans-serif'
                }}
              >
                Cancel
              </button>
              <button
                type="submit"
                disabled={loading}
                style={{
                  padding: '12px 24px',
                  borderRadius: '8px',
                  border: 'none',
                  backgroundColor: loading ? '#A0AEC0' : '#48BB78',
                  color: 'white',
                  fontSize: '14px',
                  fontWeight: '500',
                  cursor: loading ? 'not-allowed' : 'pointer',
                  fontFamily: 'Inter, sans-serif',
                  opacity: loading ? 0.6 : 1
                }}
              >
                {loading ? 'Saving...' : 'Save Service'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </DispatcherLayout>
  );
}
