import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Layout } from '../components/Layout';
import { bookingAPI } from '../services/api';
import toast from 'react-hot-toast';

export default function MyTickets() {
  const navigate = useNavigate();
  const [tickets, setTickets] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadTickets();
  }, []);

  const loadTickets = async () => {
    try {
      const userData = JSON.parse(localStorage.getItem('user') || '{}');
      if (userData.id) {
        const response = await bookingAPI.getReservationsByCustomer(userData.id);
        // Transform reservations to ticket format
        const ticketData = response.data.map(reservation => ({
          id: reservation.reservationNumber || `TCK-${reservation.id}`,
          flightNumber: reservation.flightNumber,
          route: reservation.route,
          date: reservation.date,
          class: 'Economy',
          status: reservation.status
        }));
        setTickets(ticketData);
      } else {
        // Use sample data if no user logged in
        setTickets([
          {
            id: 'TCK-10231',
            flightNumber: 'AC101',
            route: 'BEG → CDG',
            date: '2025-10-15',
            class: 'Economy',
            status: 'Confirmed'
          }
        ]);
      }
    } catch (error) {
      console.error('Error loading tickets:', error);
      // Use sample data as fallback
      setTickets([
        {
          id: 'TCK-10231',
          flightNumber: 'AC101',
          route: 'BEG → CDG',
          date: '2025-10-15',
          class: 'Economy',
          status: 'Confirmed'
        },
        {
          id: 'TCK-10218',
          flightNumber: 'AC102',
          route: 'BEG → CDG',
          date: '2025-08-28',
          class: 'Business',
          status: 'Confirmed'
        }
      ]);
    } finally {
      setLoading(false);
    }
  };

  const pageStyle = {
    padding: '24px',
    fontFamily: 'Inter, sans-serif'
  };

  const headerStyle = {
    fontSize: '24px',
    fontWeight: '600',
    color: '#1f2937',
    marginBottom: '24px'
  };

  const tableContainerStyle = {
    backgroundColor: 'white',
    border: '1px solid #e5e7eb',
    borderRadius: '8px',
    overflow: 'hidden'
  };

  const tableHeaderStyle = {
    display: 'grid',
    gridTemplateColumns: '120px 200px 120px 100px 120px 150px',
    gap: '16px',
    padding: '16px 20px',
    backgroundColor: '#f9fafb',
    borderBottom: '1px solid #e5e7eb'
  };

  const headerCellStyle = {
    fontSize: '14px',
    fontWeight: '500',
    color: '#6b7280',
    textAlign: 'left'
  };

  const tableRowStyle = {
    display: 'grid',
    gridTemplateColumns: '120px 200px 120px 100px 120px 150px',
    gap: '16px',
    padding: '16px 20px',
    borderBottom: '1px solid #f3f4f6',
    alignItems: 'center'
  };

  const cellStyle = {
    fontSize: '14px',
    color: '#374151'
  };

  const getStatusBadge = (status) => {
    const baseStyle = {
      padding: '4px 12px',
      borderRadius: '6px',
      fontSize: '12px',
      fontWeight: '500',
      textAlign: 'center',
      border: '1px solid'
    };

    switch (status) {
      case 'Created':
        return {
          ...baseStyle,
          backgroundColor: '#fef3c7',
          color: '#92400e',
          borderColor: '#f59e0b'
        };
      case 'Confirmed':
        return {
          ...baseStyle,
          backgroundColor: '#d1fae5',
          color: '#065f46',
          borderColor: '#10b981'
        };
      case 'Cancelled':
        return {
          ...baseStyle,
          backgroundColor: '#fee2e2',
          color: '#991b1b',
          borderColor: '#ef4444'
        };
      case 'Used':
        return {
          ...baseStyle,
          backgroundColor: '#e0e7ff',
          color: '#3730a3',
          borderColor: '#6366f1'
        };
      default:
        return baseStyle;
    }
  };

  const getActionButtons = (ticket) => {
    const buttonBaseStyle = {
      padding: '6px 16px',
      borderRadius: '6px',
      fontSize: '12px',
      fontWeight: '500',
      cursor: 'pointer',
      border: 'none',
      marginRight: '8px',
      fontFamily: 'Inter, sans-serif'
    };

    const editButtonStyle = {
      ...buttonBaseStyle,
      backgroundColor: '#2563eb',
      color: 'white'
    };

    const cancelButtonStyle = {
      ...buttonBaseStyle,
      backgroundColor: '#dc2626',
      color: 'white'
    };

    const downloadButtonStyle = {
      ...buttonBaseStyle,
      backgroundColor: '#10b981',
      color: 'white'
    };

    switch (ticket.status) {
      case 'Created':
        return (
          <div>
            <button 
              style={editButtonStyle}
              onClick={() => handleEdit(ticket.id)}
              onMouseEnter={(e) => e.target.style.backgroundColor = '#1d4ed8'}
              onMouseLeave={(e) => e.target.style.backgroundColor = '#2563eb'}
            >
              Edit
            </button>
            <button 
              style={cancelButtonStyle}
              onClick={() => handleCancel(ticket.id)}
              onMouseEnter={(e) => e.target.style.backgroundColor = '#b91c1c'}
              onMouseLeave={(e) => e.target.style.backgroundColor = '#dc2626'}
            >
              Cancel
            </button>
          </div>
        );
      case 'Confirmed':
        return (
          <button 
            style={downloadButtonStyle}
            onClick={() => handleDownload(ticket.id)}
            onMouseEnter={(e) => e.target.style.backgroundColor = '#059669'}
            onMouseLeave={(e) => e.target.style.backgroundColor = '#10b981'}
          >
            Download e-ticket
          </button>
        );
      case 'Cancelled':
      case 'Used':
        return (
          <div>
            <button 
              style={editButtonStyle}
              onClick={() => handleEdit(ticket.id)}
              onMouseEnter={(e) => e.target.style.backgroundColor = '#1d4ed8'}
              onMouseLeave={(e) => e.target.style.backgroundColor = '#2563eb'}
            >
              Edit
            </button>
            <button 
              style={cancelButtonStyle}
              onClick={() => handleCancel(ticket.id)}
              onMouseEnter={(e) => e.target.style.backgroundColor = '#b91c1c'}
              onMouseLeave={(e) => e.target.style.backgroundColor = '#dc2626'}
            >
              Cancel
            </button>
          </div>
        );
      default:
        return null;
    }
  };

  const handleEdit = (ticketId) => {
    console.log('Edit ticket:', ticketId);
    // Navigate to edit page
    navigate(`/tickets/${ticketId}/edit`);
  };

  const handleCancel = (ticketId) => {
    console.log('Cancel ticket:', ticketId);
    // API call to cancel ticket
  };

  const handleDownload = (ticketId) => {
    console.log('Download ticket:', ticketId);
    // API call to download PDF
  };

  const emptyStateStyle = {
    textAlign: 'center',
    padding: '48px',
    color: '#6b7280',
    backgroundColor: 'white'
  };

  return (
    <Layout>
      <div style={pageStyle}>
        <h1 style={headerStyle}>My Tickets</h1>
        
        {loading ? (
          <div style={{ textAlign: 'center', padding: '40px' }}>
            <p style={{ color: '#6b7280', fontFamily: 'Inter, sans-serif' }}>Loading tickets...</p>
          </div>
        ) : tickets.length > 0 ? (
          <div style={tableContainerStyle}>
            {/* Table Header */}
            <div style={tableHeaderStyle}>
              <div style={headerCellStyle}>Ticket #</div>
              <div style={headerCellStyle}>Flight / Route</div>
              <div style={headerCellStyle}>Date</div>
              <div style={headerCellStyle}>Class</div>
              <div style={headerCellStyle}>Status</div>
              <div style={headerCellStyle}>Actions</div>
            </div>
            
            {/* Table Rows */}
            {tickets.map((ticket, index) => (
              <div key={ticket.id} style={{
                ...tableRowStyle,
                backgroundColor: index % 2 === 0 ? 'white' : '#fafafa'
              }}>
                <div style={cellStyle}>{ticket.id}</div>
                <div style={cellStyle}>
                  {ticket.flightNumber} — {ticket.route}
                </div>
                <div style={cellStyle}>{ticket.date}</div>
                <div style={cellStyle}>{ticket.class}</div>
                <div>
                  <span style={getStatusBadge(ticket.status)}>
                    {ticket.status}
                  </span>
                </div>
                <div>
                  {getActionButtons(ticket)}
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div style={emptyStateStyle}>
            <p>No tickets found. Book your first flight to see tickets here.</p>
          </div>
        )}
      </div>
    </Layout>
  );
}