import React from 'react';

export function BookingConfirmation({ 
  ticketNumber, 
  status, 
  flightDetails, 
  passengerName,
  onDownloadTicket,
  onGoToTickets 
}) {
  const containerStyle = {
    padding: '24px',
    maxWidth: '800px',
    margin: '0 auto',
    fontFamily: 'Inter, sans-serif'
  };

  const headerStyle = {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: '24px'
  };

  const titleStyle = {
    fontSize: '24px',
    fontWeight: '600',
    color: '#1f2937'
  };

  const stepStyle = {
    fontSize: '14px',
    color: '#6b7280'
  };

  const successCardStyle = {
    backgroundColor: 'white',
    border: '1px solid #e5e7eb',
    borderRadius: '8px',
    padding: '24px',
    marginBottom: '20px'
  };

  const successTitleStyle = {
    fontSize: '20px',
    fontWeight: '600',
    color: '#10b981',
    marginBottom: '12px'
  };

  const ticketInfoStyle = {
    fontSize: '14px',
    color: '#374151',
    marginBottom: '8px'
  };

  const noteStyle = {
    fontSize: '14px',
    color: '#6b7280',
    lineHeight: '1.5'
  };

  const flightCardStyle = {
    backgroundColor: 'white',
    border: '1px solid #e5e7eb',
    borderRadius: '8px',
    padding: '24px',
    marginBottom: '24px'
  };

  const flightLabelStyle = {
    fontSize: '14px',
    color: '#6b7280',
    marginBottom: '8px'
  };

  const flightDetailsStyle = {
    fontSize: '16px',
    fontWeight: '600',
    color: '#1f2937',
    marginBottom: '12px'
  };

  const passengerLabelStyle = {
    fontSize: '14px',
    color: '#6b7280',
    marginBottom: '4px'
  };

  const passengerNameStyle = {
    fontSize: '14px',
    color: '#1f2937'
  };

  const buttonContainerStyle = {
    display: 'flex',
    gap: '12px'
  };

  const downloadButtonStyle = {
    backgroundColor: '#2563eb',
    color: 'white',
    border: 'none',
    borderRadius: '8px',
    padding: '12px 20px',
    fontSize: '14px',
    fontWeight: '500',
    cursor: 'pointer',
    fontFamily: 'Inter, sans-serif',
    transition: 'background-color 0.2s ease'
  };

  const ticketsButtonStyle = {
    backgroundColor: 'white',
    color: '#2563eb',
    border: '1px solid #2563eb',
    borderRadius: '8px',
    padding: '12px 20px',
    fontSize: '14px',
    fontWeight: '500',
    cursor: 'pointer',
    fontFamily: 'Inter, sans-serif',
    transition: 'all 0.2s ease'
  };

  return (
    <div style={containerStyle}>
      <div style={headerStyle}>
        <h1 style={titleStyle}>Booking Confirmed</h1>
        <span style={stepStyle}>Step 5 / 5</span>
      </div>

      <div style={successCardStyle}>
        <h2 style={successTitleStyle}>Payment successful</h2>
        <div style={ticketInfoStyle}>
          Ticket: {ticketNumber} • Status: {status}
        </div>
        <div style={noteStyle}>
          E-ticket sent to your email. You can also download it below or find it in My Tickets.
        </div>
      </div>

      <div style={flightCardStyle}>
        <div style={flightLabelStyle}>Flight</div>
        <div style={flightDetailsStyle}>
          {flightDetails?.flightNumber} • {flightDetails?.route} • {flightDetails?.departure}/{flightDetails?.arrival} • {flightDetails?.class}
        </div>
        <div style={passengerLabelStyle}>Passenger</div>
        <div style={passengerNameStyle}>{passengerName}</div>
      </div>

      <div style={buttonContainerStyle}>
        <button 
          style={downloadButtonStyle}
          onClick={onDownloadTicket}
          onMouseEnter={(e) => e.target.style.backgroundColor = '#1d4ed8'}
          onMouseLeave={(e) => e.target.style.backgroundColor = '#2563eb'}
        >
          Download e-ticket (PDF)
        </button>
        <button 
          style={ticketsButtonStyle}
          onClick={onGoToTickets}
          onMouseEnter={(e) => {
            e.target.style.backgroundColor = '#2563eb';
            e.target.style.color = 'white';
          }}
          onMouseLeave={(e) => {
            e.target.style.backgroundColor = 'white';
            e.target.style.color = '#2563eb';
          }}
        >
          Go to My Tickets
        </button>
      </div>
    </div>
  );
}