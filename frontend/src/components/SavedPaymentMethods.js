import React from 'react';

export function SavedPaymentMethods({ paymentMethods, onAddCard, onRemoveCard }) {
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
                {card.type} •••• {card.lastFour} (exp {card.expiry})
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
              Visa •••• 1234 (exp 07/27)
            </span>
            <button 
              style={removeButtonStyle}
              onClick={() => onRemoveCard && onRemoveCard('default')}
              onMouseEnter={(e) => e.target.style.backgroundColor = '#dc2626'}
              onMouseLeave={(e) => e.target.style.backgroundColor = '#ef4444'}
            >
              Remove
            </button>
          </div>
        )}
        
        <button 
          style={addCardButtonStyle}
          onClick={onAddCard}
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
      </div>
    </div>
  );
}