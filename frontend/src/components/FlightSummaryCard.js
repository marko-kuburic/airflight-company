import React from 'react';

export function FlightSummaryCard({ flight }) {
  const cardStyle = {
    backgroundColor: 'white',
    border: '1px solid #e5e7eb',
    borderRadius: '8px',
    padding: '16px',
    marginBottom: '20px',
    fontFamily: 'Inter, sans-serif'
  };

  const headerStyle = {
    fontSize: '14px',
    fontWeight: '500',
    color: '#374151',
    marginBottom: '8px'
  };

  const flightInfoStyle = {
    fontSize: '14px',
    fontWeight: '600',
    color: '#1f2937',
    marginBottom: '8px'
  };

  const fareInfoStyle = {
    fontSize: '12px',
    color: '#6b7280',
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center'
  };

  return (
    <div style={cardStyle}>
      <div style={headerStyle}>Selected Flight</div>
      <div style={flightInfoStyle}>
        {flight.flightNumber} • {flight.route} • {flight.departure} / {flight.arrival} • {flight.class}
      </div>
      <div style={fareInfoStyle}>
        <span>Fare includes: {flight.fareIncludes}</span>
      </div>
    </div>
  );
}