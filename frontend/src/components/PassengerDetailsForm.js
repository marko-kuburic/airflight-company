import React, { useState, useEffect } from 'react';

export function PassengerDetailsForm({ passengerData, onFormChange, onValidationChange }) {
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    dateOfBirth: '',
    documentNumber: '',
    phone: '',
    email: ''
  });

  const [errors, setErrors] = useState({});
  const [touched, setTouched] = useState({});

  useEffect(() => {
    if (passengerData) {
      setFormData(passengerData);
    }
  }, [passengerData]);

  // Validation functions
  const validateField = (name, value) => {
    switch (name) {
      case 'firstName':
        if (!value.trim()) return 'First name is required';
        if (value.trim().length < 2) return 'First name must be at least 2 characters';
        if (!/^[a-zA-Z\s-']+$/.test(value)) return 'First name can only contain letters, spaces, hyphens, and apostrophes';
        return null;

      case 'lastName':
        if (!value.trim()) return 'Last name is required';
        if (value.trim().length < 2) return 'Last name must be at least 2 characters';
        if (!/^[a-zA-Z\s-']+$/.test(value)) return 'Last name can only contain letters, spaces, hyphens, and apostrophes';
        return null;

      case 'dateOfBirth':
        if (!value) return 'Date of birth is required';
        const birthDate = new Date(value);
        const today = new Date();
        const age = today.getFullYear() - birthDate.getFullYear();
        if (age < 0 || (age === 0 && today < new Date(today.getFullYear(), birthDate.getMonth(), birthDate.getDate()))) {
          return 'Date of birth cannot be in the future';
        }
        if (age > 120) return 'Please enter a valid date of birth';
        if (age < 2) return 'Passengers must be at least 2 years old';
        return null;

      case 'documentNumber':
        if (!value.trim()) return 'Document number is required';
        if (value.trim().length < 6) return 'Document number must be at least 6 characters';
        if (!/^[A-Z0-9]+$/.test(value.trim().replace(/\s/g, ''))) return 'Document number can only contain letters and numbers';
        return null;

      case 'phone':
        if (!value.trim()) return 'Phone number is required';
        const phoneRegex = /^[\+]?[1-9][\d]{0,15}$/;
        const cleanPhone = value.replace(/[\s\-\(\)]/g, '');
        if (!phoneRegex.test(cleanPhone)) return 'Please enter a valid phone number';
        if (cleanPhone.length < 7) return 'Phone number must be at least 7 digits';
        return null;

      case 'email':
        if (!value.trim()) return 'Email is required';
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(value)) return 'Please enter a valid email address';
        return null;

      default:
        return null;
    }
  };

  const validateAllFields = () => {
    const newErrors = {};
    Object.keys(formData).forEach(field => {
      const error = validateField(field, formData[field]);
      if (error) newErrors[field] = error;
    });
    return newErrors;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    
    // Format specific fields
    let formattedValue = value;
    if (name === 'documentNumber') {
      formattedValue = value.toUpperCase().replace(/[^A-Z0-9]/g, '');
    } else if (name === 'firstName' || name === 'lastName') {
      formattedValue = value.replace(/[^a-zA-Z\s-']/g, '');
    } else if (name === 'phone') {
      formattedValue = value.replace(/[^+\d\s\-\(\)]/g, '');
    }

    const updatedData = {
      ...formData,
      [name]: formattedValue
    };
    
    setFormData(updatedData);
    
    // Validate field if it has been touched
    if (touched[name]) {
      const error = validateField(name, formattedValue);
      setErrors(prev => ({
        ...prev,
        [name]: error
      }));
    }

    // Check if form is valid
    const allErrors = validateAllFields();
    const isValid = Object.keys(allErrors).length === 0;
    
    onFormChange(updatedData);
    if (onValidationChange) {
      onValidationChange(isValid, allErrors);
    }
  };

  const handleBlur = (e) => {
    const { name, value } = e.target;
    setTouched(prev => ({ ...prev, [name]: true }));
    
    const error = validateField(name, value);
    setErrors(prev => ({
      ...prev,
      [name]: error
    }));
    
    e.target.style.borderColor = error ? '#ef4444' : '#d1d5db';
  };

  const handleFocus = (e) => {
    const { name } = e.target;
    const hasError = errors[name];
    e.target.style.borderColor = hasError ? '#ef4444' : '#2563eb';
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
    backgroundColor: 'white',
    transition: 'border-color 0.2s'
  };

  const errorInputStyle = {
    ...inputStyle,
    borderColor: '#ef4444',
    backgroundColor: '#fef2f2'
  };

  const labelStyle = {
    fontSize: '14px',
    fontWeight: '500',
    color: '#374151',
    marginBottom: '6px',
    display: 'block',
    fontFamily: 'Inter, sans-serif'
  };

  const requiredStyle = {
    color: '#ef4444',
    marginLeft: '2px'
  };

  const formRowStyle = {
    display: 'grid',
    gridTemplateColumns: '1fr 1fr 1fr',
    gap: '16px',
    marginBottom: '16px'
  };

  const formRowTwoColStyle = {
    gridTemplateColumns: '1fr 1fr',
    gap: '16px',
    marginBottom: '16px'
  };

  const errorTextStyle = {
    fontSize: '12px',
    color: '#ef4444',
    marginTop: '4px',
    fontWeight: '500'
  };

  const noteStyle = {
    fontSize: '12px',
    color: '#6b7280',
    marginTop: '12px',
    fontFamily: 'Inter, sans-serif'
  };

  const getInputStyle = (fieldName) => {
    return errors[fieldName] && touched[fieldName] ? errorInputStyle : inputStyle;
  };

  return (
    <div>
      <div style={formRowStyle}>
        <div>
          <label style={labelStyle}>
            First Name <span style={requiredStyle}>*</span>
          </label>
          <input
            type="text"
            name="firstName"
            value={formData.firstName}
            onChange={handleChange}
            onBlur={handleBlur}
            onFocus={handleFocus}
            style={getInputStyle('firstName')}
            placeholder="Enter first name"
            maxLength="50"
          />
          {errors.firstName && touched.firstName && (
            <div style={errorTextStyle}>{errors.firstName}</div>
          )}
        </div>
        <div>
          <label style={labelStyle}>
            Last Name <span style={requiredStyle}>*</span>
          </label>
          <input
            type="text"
            name="lastName"
            value={formData.lastName}
            onChange={handleChange}
            onBlur={handleBlur}
            onFocus={handleFocus}
            style={getInputStyle('lastName')}
            placeholder="Enter last name"
            maxLength="50"
          />
          {errors.lastName && touched.lastName && (
            <div style={errorTextStyle}>{errors.lastName}</div>
          )}
        </div>
        <div>
          <label style={labelStyle}>
            Date of Birth <span style={requiredStyle}>*</span>
          </label>
          <input
            type="date"
            name="dateOfBirth"
            value={formData.dateOfBirth}
            onChange={handleChange}
            onBlur={handleBlur}
            onFocus={handleFocus}
            style={getInputStyle('dateOfBirth')}
            max={new Date().toISOString().split('T')[0]}
            min={new Date(new Date().getFullYear() - 120, 0, 1).toISOString().split('T')[0]}
          />
          {errors.dateOfBirth && touched.dateOfBirth && (
            <div style={errorTextStyle}>{errors.dateOfBirth}</div>
          )}
        </div>
      </div>

      <div style={formRowTwoColStyle}>
        <div>
          <label style={labelStyle}>
            Document Number <span style={requiredStyle}>*</span>
          </label>
          <input
            type="text"
            name="documentNumber"
            value={formData.documentNumber}
            onChange={handleChange}
            onBlur={handleBlur}
            onFocus={handleFocus}
            style={getInputStyle('documentNumber')}
            placeholder="e.g., A1234567"
            maxLength="20"
          />
          {errors.documentNumber && touched.documentNumber && (
            <div style={errorTextStyle}>{errors.documentNumber}</div>
          )}
        </div>
        <div>
          <label style={labelStyle}>
            Phone <span style={requiredStyle}>*</span>
          </label>
          <input
            type="tel"
            name="phone"
            value={formData.phone}
            onChange={handleChange}
            onBlur={handleBlur}
            onFocus={handleFocus}
            style={getInputStyle('phone')}
            placeholder="+1234567890"
            maxLength="20"
          />
          {errors.phone && touched.phone && (
            <div style={errorTextStyle}>{errors.phone}</div>
          )}
        </div>
      </div>

      <div style={{ marginBottom: '16px' }}>
        <label style={labelStyle}>
          Email <span style={requiredStyle}>*</span>
        </label>
        <input
          type="email"
          name="email"
          value={formData.email}
          onChange={handleChange}
          onBlur={handleBlur}
          onFocus={handleFocus}
          style={getInputStyle('email')}
          placeholder="your.email@example.com"
          maxLength="100"
        />
        {errors.email && touched.email && (
          <div style={errorTextStyle}>{errors.email}</div>
        )}
      </div>

      <div style={noteStyle}>
        We'll send confirmations and the e-ticket to this email.
      </div>
    </div>
  );
}