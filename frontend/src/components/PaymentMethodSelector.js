import React, { useState } from 'react';

export function PaymentMethodSelector({ onPaymentChange }) {
  const [selectedMethod, setSelectedMethod] = useState('card');
  const [cardDetails, setCardDetails] = useState({
    cardNumber: '',
    mmyy: '',
    cvc: ''
  });
  const [loyaltyPoints, setLoyaltyPoints] = useState('');

  const handleMethodChange = (method) => {
    setSelectedMethod(method);
    if (onPaymentChange) {
      onPaymentChange({ method, cardDetails, loyaltyPoints });
    }
  };

  const handleCardChange = (field, value) => {
    const newCardDetails = { ...cardDetails, [field]: value };
    setCardDetails(newCardDetails);
    if (onPaymentChange) {
      onPaymentChange({ method: selectedMethod, cardDetails: newCardDetails, loyaltyPoints });
    }
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
    outline: 'none'
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

      <div style={totalStyle}>
        Total: €142.00
      </div>

      {(selectedMethod === 'card' || selectedMethod === 'combined') && (
        <div>
          <div style={inputRowStyle}>
            <div>
              <label style={labelStyle}>Card number</label>
              <input
                type="text"
                value={cardDetails.cardNumber}
                onChange={(e) => handleCardChange('cardNumber', e.target.value)}
                placeholder="•••• •••• •••• ••••"
                style={inputStyle}
              />
            </div>
            <div>
              <label style={labelStyle}>MM/YY</label>
              <input
                type="text"
                value={cardDetails.mmyy}
                onChange={(e) => handleCardChange('mmyy', e.target.value)}
                placeholder="MM/YY"
                style={inputStyle}
              />
            </div>
            <div>
              <label style={labelStyle}>CVC</label>
              <input
                type="text"
                value={cardDetails.cvc}
                onChange={(e) => handleCardChange('cvc', e.target.value)}
                placeholder="•••"
                style={inputStyle}
              />
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