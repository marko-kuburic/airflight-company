import React from 'react';
import { useNavigate } from 'react-router-dom';

export function RecentPurchases({ purchases }) {
  const navigate = useNavigate();

  const containerStyle = {
    backgroundColor: 'white',
    border: '1px solid #e5e7eb',
    borderRadius: '8px',
    padding: '20px',
    fontFamily: 'Inter, sans-serif'
  };

  const titleStyle = {
    fontSize: '16px',
    fontWeight: '600',
    color: '#1f2937',
    marginBottom: '16px'
  };

  const purchaseListStyle = {
    display: 'flex',
    flexDirection: 'column',
    gap: '12px',
    marginBottom: '20px'
  };

  const purchaseItemStyle = {
    display: 'flex',
    flexDirection: 'column',
    gap: '4px'
  };

  const ticketNumberStyle = {
    fontSize: '14px',
    fontWeight: '500',
    color: '#1f2937'
  };

  const flightInfoStyle = {
    fontSize: '13px',
    color: '#6b7280'
  };

  const getStatusColor = (status) => {
    switch (status.toLowerCase()) {
      case 'confirmed':
        return '#10b981';
      case 'cancelled':
        return '#ef4444';
      case 'used':
        return '#6366f1';
      default:
        return '#f59e0b';
    }
  };

  const openTicketsButtonStyle = {
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
    width: '100%'
  };

  const handleOpenTickets = () => {
    navigate('/tickets');
  };

  return (
    <div style={containerStyle}>
      <h3 style={titleStyle}>Recent purchases</h3>
      
      <div style={purchaseListStyle}>
        {purchases && purchases.length > 0 ? (
          purchases.map((purchase, index) => (
            <div key={index} style={purchaseItemStyle}>
              <div style={ticketNumberStyle}>
                {purchase.ticketNumber} — <span style={{ color: getStatusColor(purchase.status) }}>{purchase.status}</span>
              </div>
              <div style={flightInfoStyle}>
                {purchase.flightNumber} {purchase.date}
              </div>
            </div>
          ))
        ) : (
          <>
            <div style={purchaseItemStyle}>
              <div style={ticketNumberStyle}>
                TCK-10218 — <span style={{ color: getStatusColor('Confirmed') }}>Confirmed</span>
              </div>
              <div style={flightInfoStyle}>
                FD-815 2025-08-28
              </div>
            </div>
            <div style={purchaseItemStyle}>
              <div style={ticketNumberStyle}>
                TCK-10190 — <span style={{ color: getStatusColor('Cancelled') }}>Cancelled</span>
              </div>
              <div style={flightInfoStyle}>
                FD-829 2025-08-12
              </div>
            </div>
          </>
        )}
      </div>

      <button 
        style={openTicketsButtonStyle}
        onClick={handleOpenTickets}
        onMouseEnter={(e) => {
          e.target.style.backgroundColor = '#2563eb';
          e.target.style.color = 'white';
        }}
        onMouseLeave={(e) => {
          e.target.style.backgroundColor = 'white';
          e.target.style.color = '#2563eb';
        }}
      >
        Open My Tickets
      </button>
    </div>
  );
}