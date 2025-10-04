import React from 'react';

export function FlightSummaryCard({ flight }) {
  // Format time from ISO string to HH:MM
  const formatTime = (dateTimeString) => {
    if (!dateTimeString) return '';
    try {
      return new Date(dateTimeString).toLocaleTimeString('en-US', {
        hour: '2-digit',
        minute: '2-digit',
        hour12: false
      });
    } catch {
      return dateTimeString;
    }
  };

  // Format date from ISO string to readable format
  const formatDate = (dateTimeString) => {
    if (!dateTimeString) return '';
    try {
      return new Date(dateTimeString).toLocaleDateString('en-US', {
        weekday: 'short',
        month: 'short',
        day: 'numeric'
      });
    } catch {
      return dateTimeString;
    }
  };

  const cardStyle = {
    backgroundColor: 'white',
    border: '1px solid #e5e7eb',
    borderRadius: '12px',
    padding: '20px',
    marginBottom: '24px',
    fontFamily: 'Inter, sans-serif',
    boxShadow: '0 1px 3px 0 rgba(0, 0, 0, 0.1)'
  };

  const headerStyle = {
    fontSize: '16px',
    fontWeight: '600',
    color: '#1f2937',
    marginBottom: '16px',
    borderBottom: '1px solid #f3f4f6',
    paddingBottom: '8px'
  };

  const flightMainInfoStyle = {
    display: 'grid',
    gridTemplateColumns: '1fr auto 1fr',
    alignItems: 'center',
    gap: '16px',
    marginBottom: '16px'
  };

  const airportInfoStyle = {
    textAlign: 'center'
  };

  const flightRouteStyle = {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    gap: '8px'
  };

  const timeStyle = {
    fontSize: '20px',
    fontWeight: '700',
    color: '#1f2937'
  };

  const airportStyle = {
    fontSize: '14px',
    fontWeight: '600',
    color: '#374151',
    marginTop: '4px'
  };

  const dateStyle = {
    fontSize: '12px',
    color: '#6b7280',
    marginTop: '2px'
  };

  const durationStyle = {
    fontSize: '12px',
    color: '#6b7280',
    textAlign: 'center'
  };

  const flightNumberStyle = {
    fontSize: '14px',
    fontWeight: '600',
    color: '#2563eb',
    backgroundColor: '#eff6ff',
    padding: '4px 8px',
    borderRadius: '6px',
    display: 'inline-block',
    marginBottom: '8px'
  };

  const detailsGridStyle = {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
    gap: '12px',
    backgroundColor: '#f9fafb',
    padding: '12px',
    borderRadius: '8px'
  };

  const detailItemStyle = {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center'
  };

  const detailLabelStyle = {
    fontSize: '12px',
    color: '#6b7280',
    fontWeight: '500'
  };

  const detailValueStyle = {
    fontSize: '12px',
    color: '#1f2937',
    fontWeight: '600'
  };

  const priceStyle = {
    fontSize: '18px',
    fontWeight: '700',
    color: '#059669',
    textAlign: 'right'
  };

  const arrowStyle = {
    width: '24px',
    height: '2px',
    backgroundColor: '#d1d5db',
    position: 'relative'
  };

  const arrowHeadStyle = {
    position: 'absolute',
    right: '-4px',
    top: '-3px',
    width: 0,
    height: 0,
    borderLeft: '4px solid #d1d5db',
    borderTop: '4px solid transparent',
    borderBottom: '4px solid transparent'
  };

  return (
    <div style={cardStyle}>
      <div style={headerStyle}>Selected Flight</div>
      
      <div style={flightNumberStyle}>
        {flight.flightNumber || 'Flight Number'}
      </div>

      <div style={flightMainInfoStyle}>
        {/* Departure */}
        <div style={airportInfoStyle}>
          <div style={timeStyle}>
            {formatTime(flight.departureTime) || flight.departure || '--:--'}
          </div>
          <div style={airportStyle}>
            {flight.origin || 'Departure Airport'}
          </div>
          <div style={dateStyle}>
            {formatDate(flight.departureTime)}
          </div>
        </div>

        {/* Flight Route */}
        <div style={flightRouteStyle}>
          <div style={arrowStyle}>
            <div style={arrowHeadStyle}></div>
          </div>
          <div style={durationStyle}>
            {flight.duration || 'Duration'}
          </div>
        </div>

        {/* Arrival */}
        <div style={airportInfoStyle}>
          <div style={timeStyle}>
            {formatTime(flight.arrivalTime) || flight.arrival || '--:--'}
          </div>
          <div style={airportStyle}>
            {flight.destination || 'Arrival Airport'}
          </div>
          <div style={dateStyle}>
            {formatDate(flight.arrivalTime)}
          </div>
        </div>
      </div>

      <div style={detailsGridStyle}>
        <div style={detailItemStyle}>
          <span style={detailLabelStyle}>Aircraft</span>
          <span style={detailValueStyle}>{flight.aircraft || 'Unknown'}</span>
        </div>
        <div style={detailItemStyle}>
          <span style={detailLabelStyle}>Available Seats</span>
          <span style={detailValueStyle}>{flight.availableSeats || 'N/A'}</span>
        </div>
        <div style={detailItemStyle}>
          <span style={detailLabelStyle}>Base Price</span>
          <span style={detailValueStyle}>€{flight.basePrice || flight.currentPrice || '0.00'}</span>
        </div>
        <div style={detailItemStyle}>
          <span style={detailLabelStyle}>Current Price</span>
          <span style={priceStyle}>€{flight.currentPrice || flight.price || '0.00'}</span>
        </div>
      </div>
    </div>
  );
}