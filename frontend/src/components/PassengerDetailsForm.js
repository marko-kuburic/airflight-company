import React, { useState } from 'react';

export function PassengerDetailsForm({ onFormChange }) {
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    dateOfBirth: '',
    documentNumber: '',
    phone: '',
    email: ''
  });

  const handleInputChange = (field, value) => {
    const newData = { ...formData, [field]: value };
    setFormData(newData);
    if (onFormChange) {
      onFormChange(newData);
    }
  };

  const inputStyle = {
    width: '100%',
    height: '40px',
    padding: '0 12px',
    border: '1px solid #d1d5db',
    borderRadius: '6px',
    fontSize: '14px',
    fontFamily: 'Inter, sans-serif',
    outline: 'none',
    backgroundColor: 'white'
  };

  const labelStyle = {
    fontSize: '14px',
    fontWeight: '500',
    color: '#374151',
    marginBottom: '6px',
    display: 'block',
    fontFamily: 'Inter, sans-serif'
  };

  const formRowStyle = {
    display: 'grid',
    gridTemplateColumns: '1fr 1fr 1fr',
    gap: '16px',
    marginBottom: '16px'
  };

  const formRowTwoColStyle = {
    display: 'grid',
    gridTemplateColumns: '1fr 1fr',
    gap: '16px',
    marginBottom: '16px'
  };

  const noteStyle = {
    fontSize: '12px',
    color: '#6b7280',
    marginTop: '12px',
    fontFamily: 'Inter, sans-serif'
  };

  return (
    <div>
      <div style={formRowStyle}>
        <div>
          <label style={labelStyle}>First Name</label>
          <input
            type="text"
            value={formData.firstName}
            onChange={(e) => handleInputChange('firstName', e.target.value)}
            style={inputStyle}
            placeholder="Enter first name"
          />
        </div>
        <div>
          <label style={labelStyle}>Last Name</label>
          <input
            type="text"
            value={formData.lastName}
            onChange={(e) => handleInputChange('lastName', e.target.value)}
            style={inputStyle}
            placeholder="Enter last name"
          />
        </div>
        <div>
          <label style={labelStyle}>Date of Birth</label>
          <input
            type="date"
            value={formData.dateOfBirth}
            onChange={(e) => handleInputChange('dateOfBirth', e.target.value)}
            style={inputStyle}
          />
        </div>
      </div>

      <div style={formRowTwoColStyle}>
        <div>
          <label style={labelStyle}>Document Number</label>
          <input
            type="text"
            value={formData.documentNumber}
            onChange={(e) => handleInputChange('documentNumber', e.target.value)}
            style={inputStyle}
            placeholder="Enter document number"
          />
        </div>
        <div>
          <label style={labelStyle}>Phone</label>
          <input
            type="tel"
            value={formData.phone}
            onChange={(e) => handleInputChange('phone', e.target.value)}
            style={inputStyle}
            placeholder="Enter phone number"
          />
        </div>
      </div>

      <div style={{ marginBottom: '16px' }}>
        <label style={labelStyle}>Email</label>
        <input
          type="email"
          value={formData.email}
          onChange={(e) => handleInputChange('email', e.target.value)}
          style={inputStyle}
          placeholder="Enter email address"
        />
      </div>

      <div style={noteStyle}>
        We'll send confirmations and the e-ticket to this email.
      </div>
    </div>
  );
}