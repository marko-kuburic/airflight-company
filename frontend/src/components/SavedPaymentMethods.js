import React, { useState } from 'react';

export function SavedPaymentMethods({ paymentMethods, onAddCard, onRemoveCard }) {
  const [showAddForm, setShowAddForm] = useState(false);
  const [formData, setFormData] = useState({
    cardholderName: '',
    cardNumber: '',
    expiryDate: '',
    cardType: 'VISA'
  });
  const [errors, setErrors] = useState({});
  const [touched, setTouched] = useState({});

  // Validation functions (same as PaymentMethodSelector)
  const validateCardholderName = (value) => {
    if (!value.trim()) return 'Cardholder name is required';
    if (value.trim().length < 2) return 'Name must be at least 2 characters';
    if (!/^[a-zA-Z\s]+$/.test(value)) return 'Name can only contain letters and spaces';
    return null;
  };

  const validateCardNumber = (value) => {
    const cleaned = value.replace(/\s/g, '');
    if (!cleaned) return 'Card number is required';
    if (!/^\d{16}$/.test(cleaned)) return 'Card number must be exactly 16 digits';
    return null;
  };

  const validateExpiry = (value) => {
    if (!value) return 'Expiry date is required';
    if (!/^\d{2}\/\d{2}$/.test(value)) return 'Use MM/YY format';
    const [month, year] = value.split('/');
    const monthNum = parseInt(month);
    const yearNum = parseInt('20' + year);
    if (monthNum < 1 || monthNum > 12) return 'Invalid month';
    const now = new Date();
    const expiryDate = new Date(yearNum, monthNum - 1);
    if (expiryDate <= now) return 'Card has expired';
    return null;
  };

  // Formatting functions
  const formatCardNumber = (value) => {
    const cleaned = value.replace(/\D/g, '');
    const limited = cleaned.substring(0, 16);
    return limited.replace(/(\d{4})(?=\d)/g, '$1 ');
  };

  const formatExpiry = (value) => {
    const cleaned = value.replace(/\D/g, '');
    const limited = cleaned.substring(0, 4);
    if (limited.length >= 2) {
      return limited.substring(0, 2) + '/' + limited.substring(2);
    }
    return limited;
  };

  const handleAddCard = () => {
    setShowAddForm(true);
  };

  const handleInputChange = (field, value) => {
    let formattedValue = value;
    let error = null;

    // Format and validate based on field
    switch (field) {
      case 'cardholderName':
        if (touched[field]) {
          error = validateCardholderName(value);
        }
        break;
      case 'cardNumber':
        formattedValue = formatCardNumber(value);
        if (touched[field]) {
          error = validateCardNumber(formattedValue);
        }
        break;
      case 'expiryDate':
        formattedValue = formatExpiry(value);
        if (touched[field]) {
          error = validateExpiry(formattedValue);
        }
        break;
    }

    setFormData(prev => ({ ...prev, [field]: formattedValue }));
    setErrors(prev => ({ ...prev, [field]: error }));
  };

  const handleFieldBlur = (field) => {
    setTouched(prev => ({ ...prev, [field]: true }));
    
    // Validate on blur
    let error = null;
    switch (field) {
      case 'cardholderName':
        error = validateCardholderName(formData.cardholderName);
        break;
      case 'cardNumber':
        error = validateCardNumber(formData.cardNumber);
        break;
      case 'expiryDate':
        error = validateExpiry(formData.expiryDate);
        break;
    }
    
    setErrors(prev => ({ ...prev, [field]: error }));
  };

  const isFormValid = () => {
    return !errors.cardholderName && !errors.cardNumber && !errors.expiryDate &&
           formData.cardholderName.trim() &&
           formData.cardNumber.replace(/\s/g, '').length === 16 &&
           formData.expiryDate.length === 5;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // Validate all fields
    const cardholderError = validateCardholderName(formData.cardholderName);
    const cardNumberError = validateCardNumber(formData.cardNumber);
    const expiryError = validateExpiry(formData.expiryDate);
    
    const newErrors = {
      cardholderName: cardholderError,
      cardNumber: cardNumberError,
      expiryDate: expiryError
    };
    
    setErrors(newErrors);
    setTouched({
      cardholderName: true,
      cardNumber: true,
      expiryDate: true
    });

    // Check if form is valid
    if (cardholderError || cardNumberError || expiryError) {
      return;
    }

    if (onAddCard) {
      await onAddCard(formData);
      setFormData({
        cardholderName: '',
        cardNumber: '',
        expiryDate: '',
        cardType: 'VISA'
      });
      setErrors({});
      setTouched({});
      setShowAddForm(false);
    }
  };

  const handleCancel = () => {
    setFormData({
      cardholderName: '',
      cardNumber: '',
      expiryDate: '',
      cardType: 'VISA'
    });
    setErrors({});
    setTouched({});
    setShowAddForm(false);
  };
  const containerStyle = {
    backgroundColor: 'white',
    border: '1px solid #e5e7eb',
    borderRadius: '8px',
    padding: '24px',
    fontFamily: 'Inter, sans-serif'
  };

  const titleStyle = {
    fontSize: '16px',
    fontWeight: '600',
    color: '#1f2937',
    marginBottom: '16px'
  };

  const cardListStyle = {
    display: 'flex',
    flexDirection: 'column',
    gap: '12px'
  };

  const cardItemStyle = {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: '12px 16px',
    backgroundColor: '#f9fafb',
    border: '1px solid #e5e7eb',
    borderRadius: '6px'
  };

  const cardInfoStyle = {
    fontSize: '14px',
    color: '#374151'
  };

  const removeButtonStyle = {
    backgroundColor: '#ef4444',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    padding: '4px 8px',
    fontSize: '12px',
    cursor: 'pointer',
    fontFamily: 'Inter, sans-serif'
  };

  const addCardButtonStyle = {
    backgroundColor: 'white',
    color: '#2563eb',
    border: '1px solid #2563eb',
    borderRadius: '6px',
    padding: '12px 16px',
    fontSize: '14px',
    fontWeight: '500',
    cursor: 'pointer',
    fontFamily: 'Inter, sans-serif',
    transition: 'all 0.2s ease',
    width: 'fit-content'
  };

  return (
    <div style={containerStyle}>
      <h3 style={titleStyle}>Saved payment methods</h3>
      
      <div style={cardListStyle}>
        {paymentMethods && paymentMethods.length > 0 ? (
          paymentMethods.map((card, index) => (
            <div key={index} style={cardItemStyle}>
              <span style={cardInfoStyle}>
                {card.cardType} •••• {card.maskedCardNumber.slice(-4)} (exp {card.expiryDate})
              </span>
              <button 
                style={removeButtonStyle}
                onClick={() => onRemoveCard && onRemoveCard(card.id)}
                onMouseEnter={(e) => e.target.style.backgroundColor = '#dc2626'}
                onMouseLeave={(e) => e.target.style.backgroundColor = '#ef4444'}
              >
                Remove
              </button>
            </div>
          ))
        ) : (
          <div style={cardItemStyle}>
            <span style={cardInfoStyle}>
              No payment methods saved
            </span>
          </div>
        )}
        
        {showAddForm ? (
          <form onSubmit={handleSubmit} style={{ marginTop: '16px' }}>
            <div style={{ marginBottom: '12px' }}>
              <label style={{ display: 'block', marginBottom: '4px', fontSize: '14px', fontWeight: '500' }}>
                Cardholder Name
              </label>
              <input
                type="text"
                value={formData.cardholderName}
                onChange={(e) => handleInputChange('cardholderName', e.target.value)}
                onBlur={() => handleFieldBlur('cardholderName')}
                style={{
                  width: '100%',
                  padding: '8px 12px',
                  border: `1px solid ${errors.cardholderName ? '#ef4444' : '#d1d5db'}`,
                  borderRadius: '4px',
                  fontSize: '14px',
                  outline: 'none'
                }}
                placeholder="John Doe"
                required
              />
              {errors.cardholderName && touched.cardholderName && (
                <div style={{ color: '#ef4444', fontSize: '12px', marginTop: '4px' }}>
                  {errors.cardholderName}
                </div>
              )}
            </div>
            <div style={{ marginBottom: '12px' }}>
              <label style={{ display: 'block', marginBottom: '4px', fontSize: '14px', fontWeight: '500' }}>
                Card Number
              </label>
              <input
                type="text"
                value={formData.cardNumber}
                onChange={(e) => handleInputChange('cardNumber', e.target.value)}
                onBlur={() => handleFieldBlur('cardNumber')}
                style={{
                  width: '100%',
                  padding: '8px 12px',
                  border: `1px solid ${errors.cardNumber ? '#ef4444' : '#d1d5db'}`,
                  borderRadius: '4px',
                  fontSize: '14px',
                  outline: 'none'
                }}
                placeholder="1234 5678 9012 3456"
                required
              />
              {errors.cardNumber && touched.cardNumber && (
                <div style={{ color: '#ef4444', fontSize: '12px', marginTop: '4px' }}>
                  {errors.cardNumber}
                </div>
              )}
            </div>
            <div style={{ marginBottom: '12px' }}>
              <label style={{ display: 'block', marginBottom: '4px', fontSize: '14px', fontWeight: '500' }}>
                Expiry Date
              </label>
              <input
                type="text"
                value={formData.expiryDate}
                onChange={(e) => handleInputChange('expiryDate', e.target.value)}
                onBlur={() => handleFieldBlur('expiryDate')}
                style={{
                  width: '100%',
                  padding: '8px 12px',
                  border: `1px solid ${errors.expiryDate ? '#ef4444' : '#d1d5db'}`,
                  borderRadius: '4px',
                  fontSize: '14px',
                  outline: 'none'
                }}
                placeholder="MM/YY"
                required
              />
              {errors.expiryDate && touched.expiryDate && (
                <div style={{ color: '#ef4444', fontSize: '12px', marginTop: '4px' }}>
                  {errors.expiryDate}
                </div>
              )}
            </div>
            <div style={{ marginBottom: '16px' }}>
              <label style={{ display: 'block', marginBottom: '4px', fontSize: '14px', fontWeight: '500' }}>
                Card Type
              </label>
              <select
                value={formData.cardType}
                onChange={(e) => handleInputChange('cardType', e.target.value)}
                style={{
                  width: '100%',
                  padding: '8px 12px',
                  border: '1px solid #d1d5db',
                  borderRadius: '4px',
                  fontSize: '14px',
                  outline: 'none'
                }}
              >
                <option value="VISA">Visa</option>
                <option value="MASTERCARD">Mastercard</option>
                <option value="AMEX">American Express</option>
              </select>
            </div>
            <div style={{ display: 'flex', gap: '8px' }}>
              <button
                type="submit"
                disabled={!isFormValid()}
                style={{
                  backgroundColor: isFormValid() ? '#2563eb' : '#9ca3af',
                  color: 'white',
                  border: 'none',
                  borderRadius: '4px',
                  padding: '8px 16px',
                  fontSize: '14px',
                  cursor: isFormValid() ? 'pointer' : 'not-allowed',
                  opacity: isFormValid() ? 1 : 0.6
                }}
              >
                Save Card
              </button>
              <button
                type="button"
                onClick={handleCancel}
                style={{
                  backgroundColor: '#6b7280',
                  color: 'white',
                  border: 'none',
                  borderRadius: '4px',
                  padding: '8px 16px',
                  fontSize: '14px',
                  cursor: 'pointer'
                }}
              >
                Cancel
              </button>
            </div>
          </form>
        ) : (
          <button 
            style={addCardButtonStyle}
            onClick={handleAddCard}
            onMouseEnter={(e) => {
              e.target.style.backgroundColor = '#2563eb';
              e.target.style.color = 'white';
            }}
            onMouseLeave={(e) => {
              e.target.style.backgroundColor = 'white';
              e.target.style.color = '#2563eb';
            }}
          >
            Add new card
          </button>
        )}
      </div>
    </div>
  );
}