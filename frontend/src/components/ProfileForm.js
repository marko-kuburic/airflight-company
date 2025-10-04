import React, { useState } from 'react';

export function ProfileForm({ profileData, onSave, isLoading = false }) {
  const [formData, setFormData] = useState({
    firstName: profileData?.firstName || '',
    lastName: profileData?.lastName || '',
    email: profileData?.email || '',
    phone: profileData?.phone || '',
    dateOfBirth: profileData?.dateOfBirth || '',
    preferredLanguage: profileData?.preferredLanguage || ''
  });

  const [isEditing, setIsEditing] = useState(false);

  const handleInputChange = (field, value) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const handleEdit = () => {
    setIsEditing(true);
  };

  const handleSave = async () => {
    if (onSave) {
      await onSave(formData);
      setIsEditing(false);
    }
  };

  const handleCancel = () => {
    setFormData({
      firstName: profileData?.firstName || '',
      lastName: profileData?.lastName || '',
      email: profileData?.email || '',
      phone: profileData?.phone || '',
      dateOfBirth: profileData?.dateOfBirth || '',
      preferredLanguage: profileData?.preferredLanguage || ''
    });
    setIsEditing(false);
  };

  const containerStyle = {
    backgroundColor: 'white',
    border: '1px solid #e5e7eb',
    borderRadius: '8px',
    padding: '24px',
    fontFamily: 'Inter, sans-serif'
  };

  const formGridStyle = {
    display: 'grid',
    gridTemplateColumns: '1fr 1fr',
    gap: '16px',
    marginBottom: '20px'
  };

  const formGroupStyle = {
    display: 'flex',
    flexDirection: 'column'
  };

  const labelStyle = {
    fontSize: '14px',
    fontWeight: '500',
    color: '#374151',
    marginBottom: '8px'
  };

  const inputStyle = {
    height: '40px',
    padding: '0 12px',
    border: '1px solid #d1d5db',
    borderRadius: '6px',
    fontSize: '14px',
    fontFamily: 'Inter, sans-serif',
    backgroundColor: isEditing ? 'white' : '#f9fafb',
    outline: 'none',
    color: '#374151'
  };

  const buttonContainerStyle = {
    display: 'flex',
    justifyContent: 'flex-end',
    gap: '12px',
    marginTop: '20px'
  };

  const editButtonStyle = {
    backgroundColor: '#2563eb',
    color: 'white',
    border: 'none',
    borderRadius: '8px',
    padding: '10px 20px',
    fontSize: '14px',
    fontWeight: '500',
    cursor: 'pointer',
    fontFamily: 'Inter, sans-serif',
    transition: 'background-color 0.2s ease'
  };

  const saveButtonStyle = {
    backgroundColor: '#10b981',
    color: 'white',
    border: 'none',
    borderRadius: '8px',
    padding: '10px 20px',
    fontSize: '14px',
    fontWeight: '500',
    cursor: 'pointer',
    fontFamily: 'Inter, sans-serif',
    transition: 'background-color 0.2s ease',
    opacity: isLoading ? 0.6 : 1
  };

  const cancelButtonStyle = {
    backgroundColor: 'white',
    color: '#374151',
    border: '1px solid #d1d5db',
    borderRadius: '8px',
    padding: '10px 20px',
    fontSize: '14px',
    fontWeight: '500',
    cursor: 'pointer',
    fontFamily: 'Inter, sans-serif',
    transition: 'background-color 0.2s ease'
  };

  return (
    <div style={containerStyle}>
      <div style={formGridStyle}>
        <div style={formGroupStyle}>
          <label style={labelStyle}>First Name</label>
          <input
            type="text"
            value={formData.firstName}
            onChange={(e) => handleInputChange('firstName', e.target.value)}
            style={inputStyle}
            disabled={!isEditing}
            placeholder="Name"
          />
        </div>
        <div style={formGroupStyle}>
          <label style={labelStyle}>Last Name</label>
          <input
            type="text"
            value={formData.lastName}
            onChange={(e) => handleInputChange('lastName', e.target.value)}
            style={inputStyle}
            disabled={!isEditing}
            placeholder="Surname"
          />
        </div>
      </div>

      <div style={{ marginBottom: '24px' }}>
        <label style={labelStyle}>Email</label>
        <input
          type="email"
          value={formData.email}
          onChange={(e) => handleInputChange('email', e.target.value)}
          style={inputStyle}
          disabled={!isEditing}
          placeholder="mail@example.com"
        />
      </div>

      <div style={{ marginBottom: '16px' }}>
        <label style={labelStyle}>Phone</label>
        <input
          type="tel"
          value={formData.phone}
          onChange={(e) => handleInputChange('phone', e.target.value)}
          style={inputStyle}
          disabled={!isEditing}
          placeholder="+381 64 123 4567"
        />
      </div>

      <div style={{ marginBottom: '16px' }}>
        <label style={labelStyle}>Date of Birth</label>
        <input
          type="date"
          value={formData.dateOfBirth}
          onChange={(e) => handleInputChange('dateOfBirth', e.target.value)}
          style={inputStyle}
          disabled={!isEditing}
        />
      </div>

      <div style={{ marginBottom: '16px' }}>
        <label style={labelStyle}>Preferred Language</label>
        <select
          value={formData.preferredLanguage}
          onChange={(e) => handleInputChange('preferredLanguage', e.target.value)}
          style={inputStyle}
          disabled={!isEditing}
        >
          <option value="">Select language</option>
          <option value="en">English</option>
          <option value="sr">Serbian</option>
          <option value="de">German</option>
          <option value="fr">French</option>
          <option value="es">Spanish</option>
        </select>
      </div>

      <div style={buttonContainerStyle}>
        {!isEditing ? (
          <button 
            style={editButtonStyle}
            onClick={handleEdit}
            onMouseEnter={(e) => e.target.style.backgroundColor = '#1d4ed8'}
            onMouseLeave={(e) => e.target.style.backgroundColor = '#2563eb'}
          >
            Edit
          </button>
        ) : (
          <>
            <button 
              style={cancelButtonStyle}
              onClick={handleCancel}
              onMouseEnter={(e) => e.target.style.backgroundColor = '#f9fafb'}
              onMouseLeave={(e) => e.target.style.backgroundColor = 'white'}
            >
              Cancel
            </button>
            <button 
              style={saveButtonStyle}
              onClick={handleSave}
              disabled={isLoading}
              onMouseEnter={(e) => !isLoading && (e.target.style.backgroundColor = '#059669')}
              onMouseLeave={(e) => !isLoading && (e.target.style.backgroundColor = '#10b981')}
            >
              {isLoading ? 'Saving...' : 'Save'}
            </button>
          </>
        )}
      </div>
    </div>
  );
}