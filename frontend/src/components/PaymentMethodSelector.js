import React, { useState } from 'react';

export function PaymentMethodSelector({ onPaymentChange, totalAmount = 0 }) {
  const [selectedMethod, setSelectedMethod] = useState('card');
  const [cardDetails, setCardDetails] = useState({
    cardNumber: '',
    mmyy: '',
    cvc: ''
  });
  const [loyaltyPoints, setLoyaltyPoints] = useState('');
  const [errors, setErrors] = useState({});
  const [touched, setTouched] = useState({});

  // Validation functions
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

  const validateCVC = (value) => {
    if (!value) return 'CVC is required';
    if (!/^\d{3}$/.test(value)) return 'CVC must be exactly 3 digits';
    return null;
  };

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

  const formatCVC = (value) => {
    return value.replace(/\D/g, '').substring(0, 3);
  };

  const handleMethodChange = (method) => {
    setSelectedMethod(method);
    if (onPaymentChange) {
      onPaymentChange({ method, cardDetails, loyaltyPoints });
    }
  };

  const handleCardChange = (field, value) => {
    let formattedValue = value;
    let error = null;

    // Format and validate based on field
    switch (field) {
      case 'cardNumber':
        formattedValue = formatCardNumber(value);
        if (touched[field]) {
          error = validateCardNumber(formattedValue);
        }
        break;
      case 'mmyy':
        formattedValue = formatExpiry(value);
        if (touched[field]) {
          error = validateExpiry(formattedValue);
        }
        break;
      case 'cvc':
        formattedValue = formatCVC(value);
        if (touched[field]) {
          error = validateCVC(formattedValue);
        }
        break;
    }

    const newCardDetails = { ...cardDetails, [field]: formattedValue };
    setCardDetails(newCardDetails);
    
    // Update errors
    setErrors(prev => ({ ...prev, [field]: error }));

    // Check if all fields are valid for payment
    const allErrors = {
      ...errors,
      [field]: error
    };
    
    const isValid = !allErrors.cardNumber && !allErrors.mmyy && !allErrors.cvc &&
                   newCardDetails.cardNumber.replace(/\s/g, '').length === 16 &&
                   newCardDetails.mmyy.length === 5 &&
                   newCardDetails.cvc.length === 3;

    if (onPaymentChange) {
      onPaymentChange({ 
        method: selectedMethod, 
        cardDetails: newCardDetails, 
        loyaltyPoints,
        isValid
      });
    }
  };

  const handleFieldBlur = (field) => {
    setTouched(prev => ({ ...prev, [field]: true }));
    
    // Validate on blur
    let error = null;
    switch (field) {
      case 'cardNumber':
        error = validateCardNumber(cardDetails.cardNumber);
        break;
      case 'mmyy':
        error = validateExpiry(cardDetails.mmyy);
        break;
      case 'cvc':
        error = validateCVC(cardDetails.cvc);
        break;
    }
    
    setErrors(prev => ({ ...prev, [field]: error }));
  };

  const handleLoyaltyChange = (value) => {
    setLoyaltyPoints(value);
    if (onPaymentChange) {
      onPaymentChange({ method: selectedMethod, cardDetails, loyaltyPoints: value });
    }
  };

  const containerStyle = {
    backgroundColor: 'white',
    border: '1px solid #e5e7eb',
    borderRadius: '8px',
    padding: '20px',
    fontFamily: 'Inter, sans-serif'
  };

  const titleStyle = {
    fontSize: '14px',
    fontWeight: '500',
    color: '#374151',
    marginBottom: '16px'
  };

  const methodSelectorStyle = {
    display: 'grid',
    gridTemplateColumns: '1fr 1fr 1fr',
    gap: '0',
    marginBottom: '20px',
    border: '1px solid #e5e7eb',
    borderRadius: '6px',
    overflow: 'hidden'
  };

  const methodButtonStyle = (isSelected) => ({
    padding: '10px 16px',
    fontSize: '14px',
    fontWeight: '500',
    border: 'none',
    cursor: 'pointer',
    backgroundColor: isSelected ? '#f3f4f6' : 'white',
    color: isSelected ? '#1f2937' : '#6b7280',
    fontFamily: 'Inter, sans-serif',
    borderRight: '1px solid #e5e7eb'
  });

  const inputRowStyle = {
    display: 'grid',
    gridTemplateColumns: '2fr 1fr 1fr',
    gap: '12px',
    marginBottom: '16px'
  };

  const inputStyle = {
    height: '40px',
    padding: '0 12px',
    border: '1px solid #d1d5db',
    borderRadius: '6px',
    fontSize: '14px',
    fontFamily: 'Inter, sans-serif',
    outline: 'none',
    transition: 'border-color 0.2s'
  };

  const errorInputStyle = {
    ...inputStyle,
    borderColor: '#ef4444',
    backgroundColor: '#fef2f2'
  };

  const errorTextStyle = {
    fontSize: '12px',
    color: '#ef4444',
    marginTop: '4px',
    fontWeight: '500'
  };

  const labelStyle = {
    fontSize: '12px',
    color: '#6b7280',
    marginBottom: '4px',
    display: 'block',
    fontFamily: 'Inter, sans-serif'
  };

  const totalStyle = {
    fontSize: '16px',
    fontWeight: '600',
    color: '#1f2937',
    marginBottom: '16px'
  };

  const loyaltyContainerStyle = {
    marginTop: '16px',
    padding: '16px',
    backgroundColor: '#f9fafb',
    borderRadius: '6px'
  };

  const loyaltyInputStyle = {
    ...inputStyle,
    width: '120px',
    marginRight: '12px'
  };

  const balanceStyle = {
    fontSize: '12px',
    color: '#6b7280',
    marginTop: '4px'
  };

  return (
    <div style={containerStyle}>
      <div style={titleStyle}>Choose payment method</div>
      
      {totalAmount > 0 && (
        <div style={totalStyle}>
          Total: €{totalAmount.toFixed(2)}
        </div>
      )}
      
      <div style={methodSelectorStyle}>
        <button 
          style={methodButtonStyle(selectedMethod === 'card')}
          onClick={() => handleMethodChange('card')}
        >
          Card
        </button>
        <button 
          style={methodButtonStyle(selectedMethod === 'loyalty')}
          onClick={() => handleMethodChange('loyalty')}
        >
          Loyalty points
        </button>
        <button 
          style={{
            ...methodButtonStyle(selectedMethod === 'combined'),
            borderRight: 'none'
          }}
          onClick={() => handleMethodChange('combined')}
        >
          Combined
        </button>
      </div>

      {totalAmount > 0 && (
        <div style={totalStyle}>
          Total: €{totalAmount.toFixed(2)}
        </div>
      )}

      {(selectedMethod === 'card' || selectedMethod === 'combined') && (
        <div>
          <div style={inputRowStyle}>
            <div>
              <label style={labelStyle}>Card number</label>
              <input
                type="text"
                value={cardDetails.cardNumber}
                onChange={(e) => handleCardChange('cardNumber', e.target.value)}
                onBlur={() => handleFieldBlur('cardNumber')}
                placeholder="1234 5678 9012 3456"
                style={errors.cardNumber && touched.cardNumber ? errorInputStyle : inputStyle}
                maxLength="19"
              />
              {errors.cardNumber && touched.cardNumber && (
                <div style={errorTextStyle}>{errors.cardNumber}</div>
              )}
            </div>
            <div>
              <label style={labelStyle}>MM/YY</label>
              <input
                type="text"
                value={cardDetails.mmyy}
                onChange={(e) => handleCardChange('mmyy', e.target.value)}
                onBlur={() => handleFieldBlur('mmyy')}
                placeholder="MM/YY"
                style={errors.mmyy && touched.mmyy ? errorInputStyle : inputStyle}
                maxLength="5"
              />
              {errors.mmyy && touched.mmyy && (
                <div style={errorTextStyle}>{errors.mmyy}</div>
              )}
            </div>
            <div>
              <label style={labelStyle}>CVC</label>
              <input
                type="text"
                value={cardDetails.cvc}
                onChange={(e) => handleCardChange('cvc', e.target.value)}
                onBlur={() => handleFieldBlur('cvc')}
                placeholder="123"
                style={errors.cvc && touched.cvc ? errorInputStyle : inputStyle}
                maxLength="3"
              />
              {errors.cvc && touched.cvc && (
                <div style={errorTextStyle}>{errors.cvc}</div>
              )}
            </div>
          </div>
        </div>
      )}

      {(selectedMethod === 'loyalty' || selectedMethod === 'combined') && (
        <div style={loyaltyContainerStyle}>
          <label style={labelStyle}>Use loyalty points</label>
          <div style={{ display: 'flex', alignItems: 'center' }}>
            <input
              type="number"
              value={loyaltyPoints}
              onChange={(e) => handleLoyaltyChange(e.target.value)}
              placeholder="e.g. 1200"
              style={loyaltyInputStyle}
            />
          </div>
          <div style={balanceStyle}>
            Balance: 3,450 pts
          </div>
        </div>
      )}
    </div>
  );
}