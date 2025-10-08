import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { DispatcherLayout } from '../components/Layout/DispatcherLayout';
import { DispatcherSidebar } from '../components/Layout/DispatcherSidebar';
import { aircraftAPI } from '../services/api';
import toast from 'react-hot-toast';

export default function EditAircraft() {
  const { registration } = useParams();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [aircraft, setAircraft] = useState({
    registration: '',
    model: '',
    capacity: '',
    status: 'ACTIVE'
  });

  useEffect(() => {
    fetchAircraft();
  }, [registration]);

  const fetchAircraft = async () => {
    try {
      setLoading(true);
      const response = await aircraftAPI.getAircraftByRegistration(registration);
      const aircraftData = response.data;
      setAircraft({
        registration: aircraftData.registration || '',
        model: aircraftData.model || '',
        capacity: aircraftData.capacity || '',
        status: aircraftData.status || 'ACTIVE'
      });
    } catch (err) {
      console.error('Error fetching aircraft:', err);
      toast.error('Failed to load aircraft data');
      navigate('/dispatcher/aircrafts');
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (field, value) => {
    setAircraft(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const handleSave = async () => {
    try {
      setSaving(true);
      
      // Validate required fields
      if (!aircraft.registration.trim()) {
        toast.error('Registration is required');
        return;
      }
      if (!aircraft.model.trim()) {
        toast.error('Aircraft model is required');
        return;
      }
      if (!aircraft.capacity || aircraft.capacity <= 0) {
        toast.error('Valid capacity is required');
        return;
      }

      const updateData = {
        registration: aircraft.registration.trim(),
        model: aircraft.model.trim(),
        capacity: parseInt(aircraft.capacity),
        status: aircraft.status
      };

      await aircraftAPI.updateAircraftByRegistration(registration, updateData);
      toast.success('Aircraft updated successfully!');
      navigate('/dispatcher/aircrafts');
    } catch (err) {
      console.error('Error updating aircraft:', err);
      toast.error('Failed to update aircraft');
    } finally {
      setSaving(false);
    }
  };

  const handleCancel = () => {
    navigate('/dispatcher/aircrafts');
  };

  if (loading) {
    return (
      <DispatcherLayout>
        <DispatcherSidebar />
        <div style={{
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          height: '100vh',
          fontSize: '16px',
          color: '#4A5568',
          fontFamily: 'Inter, sans-serif'
        }}>
          Loading aircraft data...
        </div>
      </DispatcherLayout>
    );
  }

  return (
    <DispatcherLayout>
      <DispatcherSidebar />
      <main style={{
        flex: 1,
        backgroundColor: '#FFFFFF',
        overflow: 'auto',
        height: '100vh',
        padding: '32px',
        fontFamily: 'Inter, sans-serif'
      }}>
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
            Edit Aircraft — {aircraft.registration}
          </h1>
        </div>

        <div style={{
          backgroundColor: '#FFFFFF',
          borderRadius: '12px',
          border: '1px solid #E2E8F0',
          padding: '32px',
          maxWidth: '600px'
        }}>
          <div style={{
            display: 'flex',
            flexDirection: 'column',
            gap: '24px'
          }}>
            {/* Registration Field */}
            <div>
              <label style={{
                display: 'block',
                fontSize: '14px',
                fontWeight: '500',
                color: '#374151',
                marginBottom: '8px'
              }}>
                Registration (Tail)
              </label>
              <input
                type="text"
                value={aircraft.registration}
                onChange={(e) => handleInputChange('registration', e.target.value)}
                style={{
                  width: '100%',
                  height: '40px',
                  padding: '0 12px',
                  border: '1px solid #d1d5db',
                  borderRadius: '6px',
                  fontSize: '14px',
                  fontFamily: 'Inter, sans-serif',
                  backgroundColor: '#f9fafb',
                  outline: 'none',
                  color: '#6b7280'
                }}
                placeholder="Enter aircraft registration"
                disabled={true}
              />
            </div>

            {/* Type Field */}
            <div>
              <label style={{
                display: 'block',
                fontSize: '14px',
                fontWeight: '500',
                color: '#374151',
                marginBottom: '8px'
              }}>
                Type
              </label>
              <select
                value={aircraft.model}
                onChange={(e) => handleInputChange('model', e.target.value)}
                style={{
                  width: '100%',
                  height: '40px',
                  padding: '0 12px',
                  border: '1px solid #d1d5db',
                  borderRadius: '6px',
                  fontSize: '14px',
                  fontFamily: 'Inter, sans-serif',
                  backgroundColor: 'white',
                  outline: 'none',
                  color: '#374151'
                }}
              >
                <option value="">Select aircraft type</option>
                <option value="Airbus A320">Airbus A320</option>
                <option value="Airbus A321">Airbus A321</option>
                <option value="Airbus A330">Airbus A330</option>
                <option value="Boeing 737">Boeing 737</option>
                <option value="Boeing 777">Boeing 777</option>
                <option value="Boeing 787">Boeing 787</option>
                <option value="Embraer E190">Embraer E190</option>
                <option value="ATR 72">ATR 72</option>
              </select>
            </div>

            {/* Capacity Field */}
            <div>
              <label style={{
                display: 'block',
                fontSize: '14px',
                fontWeight: '500',
                color: '#374151',
                marginBottom: '8px'
              }}>
                Capacity (seats)
              </label>
              <input
                type="number"
                value={aircraft.capacity}
                onChange={(e) => handleInputChange('capacity', e.target.value)}
                style={{
                  width: '100%',
                  height: '40px',
                  padding: '0 12px',
                  border: '1px solid #d1d5db',
                  borderRadius: '6px',
                  fontSize: '14px',
                  fontFamily: 'Inter, sans-serif',
                  backgroundColor: 'white',
                  outline: 'none',
                  color: '#374151'
                }}
                placeholder="Enter seat capacity"
                min="1"
                max="1000"
              />
            </div>

            {/* Status Field */}
            <div>
              <label style={{
                display: 'block',
                fontSize: '14px',
                fontWeight: '500',
                color: '#374151',
                marginBottom: '8px'
              }}>
                Status
              </label>
              <select
                value={aircraft.status}
                onChange={(e) => handleInputChange('status', e.target.value)}
                style={{
                  width: '100%',
                  height: '40px',
                  padding: '0 12px',
                  border: '1px solid #d1d5db',
                  borderRadius: '6px',
                  fontSize: '14px',
                  fontFamily: 'Inter, sans-serif',
                  backgroundColor: 'white',
                  outline: 'none',
                  color: '#374151'
                }}
              >
                <option value="AVAILABLE">Available</option>
                <option value="ACTIVE">Active</option>
                <option value="MAINTENANCE">Maintenance</option>
                <option value="RETIRED">Retired</option>
              </select>
            </div>

            {/* Information Message */}
            <div style={{
              backgroundColor: '#F0FDF4',
              border: '1px solid #BBF7D0',
              borderRadius: '8px',
              padding: '16px',
              fontSize: '14px',
              color: '#166534',
              fontFamily: 'Inter, sans-serif'
            }}>
              Switching to "In Service" makes this aircraft unavailable for flights and triggers conflict checks.
            </div>

            {/* Action Buttons */}
            <div style={{
              display: 'flex',
              justifyContent: 'flex-end',
              gap: '12px',
              marginTop: '24px'
            }}>
              <button
                onClick={handleCancel}
                style={{
                  backgroundColor: '#6B7280',
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
                Cancel
              </button>
              <button
                onClick={handleSave}
                disabled={saving}
                style={{
                  backgroundColor: saving ? '#9CA3AF' : '#10B981',
                  color: 'white',
                  border: 'none',
                  borderRadius: '8px',
                  padding: '12px 24px',
                  fontSize: '14px',
                  fontWeight: '500',
                  cursor: saving ? 'not-allowed' : 'pointer',
                  fontFamily: 'Inter, sans-serif',
                  transition: 'opacity 0.2s ease'
                }}
                onMouseEnter={(e) => !saving && (e.target.style.opacity = '0.9')}
                onMouseLeave={(e) => !saving && (e.target.style.opacity = '1')}
              >
                {saving ? 'Saving...' : 'Save Changes'}
              </button>
            </div>
          </div>
        </div>
      </main>
    </DispatcherLayout>
  );
}
