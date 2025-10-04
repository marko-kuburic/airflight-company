import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Layout } from '../components/Layout';
import { bookingAPI } from '../services/api';
import { generatePDFTicket, generateBoardingPass } from '../utils/pdfGenerator';
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
      let allTickets = [];
      
      // Load from localStorage first (for completed bookings in this session)
      const savedBookings = JSON.parse(localStorage.getItem('myBookings') || '[]');
      const localTickets = savedBookings.map(booking => ({
        id: booking.ticketNumber || `TCK-${booking.reservationId || Date.now()}`,
        flightNumber: booking.flightDetails?.flightNumber || 'N/A',
        route: booking.flightDetails?.route || 'N/A',
        date: booking.bookingDate ? new Date(booking.bookingDate).toISOString().split('T')[0] : new Date().toISOString().split('T')[0],
        class: booking.flightDetails?.class || 'Economy',
        status: 'Confirmed',
        passengerName: booking.passengerName,
        totalAmount: booking.paymentAmount,
        seat: booking.selectedSeat,
        email: booking.email
      }));
      allTickets = [...localTickets];
      
      // Try to load from backend
      const userData = JSON.parse(localStorage.getItem('user') || '{}');
      if (userData.id) {
        try {
          const response = await bookingAPI.getReservationsByCustomer(userData.id);
          // Transform reservations to ticket format
          const backendTickets = response.data.map(reservation => ({
            id: reservation.reservationNumber || `TCK-${reservation.id}`,
            flightNumber: reservation.flightNumber || 'N/A',
            route: reservation.route || 'N/A',
            date: reservation.date || new Date().toISOString().split('T')[0],
            class: 'Economy',
            status: reservation.status || 'Confirmed',
            passengerName: reservation.passengerName,
            totalAmount: reservation.totalAmount,
            seat: reservation.seatNumber,
            email: reservation.email
          }));
          
          // Merge backend tickets with local tickets (avoid duplicates)
          const backendTicketIds = new Set(backendTickets.map(t => t.id));
          const uniqueLocalTickets = allTickets.filter(t => !backendTicketIds.has(t.id));
          allTickets = [...backendTickets, ...uniqueLocalTickets];
        } catch (backendError) {
          console.warn('Backend not available, using localStorage only:', backendError);
        }
      }
      
      // If no tickets found, show sample data
      if (allTickets.length === 0) {
        allTickets = [
          {
            id: 'TCK-SAMPLE',
            flightNumber: 'AC101',
            route: 'BEG → CDG',
            date: '2025-10-15',
            class: 'Economy',
            status: 'Confirmed',
            passengerName: 'Sample Passenger',
            totalAmount: '€142.00'
          }
        ];
      }
      
      setTickets(allTickets);
    } catch (error) {
      console.error('Error loading tickets:', error);
      // Use sample data as fallback
      setTickets([
        {
          id: 'TCK-SAMPLE',
          flightNumber: 'AC101',
          route: 'BEG → CDG',
          date: '2025-10-15',
          class: 'Economy',
          status: 'Confirmed',
          passengerName: 'Sample Passenger',
          totalAmount: '€142.00'
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
    gridTemplateColumns: '120px 180px 120px 100px 140px 100px 100px 160px',
    gap: '12px',
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
    gridTemplateColumns: '120px 180px 120px 100px 140px 100px 100px 160px',
    gap: '12px',
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
      padding: '4px 8px',
      borderRadius: '4px',
      fontSize: '11px',
      fontWeight: '500',
      cursor: 'pointer',
      border: 'none',
      marginRight: '4px',
      marginBottom: '4px',
      fontFamily: 'Inter, sans-serif'
    };

    const downloadButtonStyle = {
      ...buttonBaseStyle,
      backgroundColor: '#2563eb',
      color: 'white'
    };

    const boardingButtonStyle = {
      ...buttonBaseStyle,
      backgroundColor: '#10b981',
      color: 'white'
    };

    const cancelButtonStyle = {
      ...buttonBaseStyle,
      backgroundColor: '#dc2626',
      color: 'white'
    };

    if (ticket.status === 'Confirmed') {
      return (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
          <button 
            style={downloadButtonStyle}
            onClick={() => handleDownloadTicket(ticket)}
            onMouseEnter={(e) => e.target.style.backgroundColor = '#1d4ed8'}
            onMouseLeave={(e) => e.target.style.backgroundColor = '#2563eb'}
            title="Download PDF Ticket"
          >
            📄 E-ticket
          </button>
          <button 
            style={boardingButtonStyle}
            onClick={() => handleDownloadBoardingPass(ticket)}
            onMouseEnter={(e) => e.target.style.backgroundColor = '#059669'}
            onMouseLeave={(e) => e.target.style.backgroundColor = '#10b981'}
            title="Download Boarding Pass"
          >
            🎫 Boarding Pass
          </button>
        </div>
      );
    } else if (ticket.status === 'Created') {
      return (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
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
    }
    return null;
  };

  const handleEdit = (ticketId) => {
    console.log('Edit ticket:', ticketId);
    toast.info('Editing functionality coming soon!');
  };

  const handleCancel = (ticketId) => {
    console.log('Cancel ticket:', ticketId);
    toast.info('Cancellation functionality coming soon!');
  };

  const handleDownloadTicket = (ticket) => {
    try {
      console.log('Download ticket:', ticket.id);
      
      // Convert ticket data to the format expected by PDF generator
      const ticketData = {
        ticketNumber: ticket.id,
        status: 'Confirmed / Paid',
        flightDetails: {
          flightNumber: ticket.flightNumber,
          route: ticket.route,
          departure: 'N/A', // You might want to add these fields to your ticket data
          arrival: 'N/A',
          class: ticket.class
        },
        passengerName: ticket.passengerName,
        bookingReference: `REF-${ticket.id}`,
        paymentAmount: ticket.totalAmount,
        email: ticket.email,
        selectedSeat: ticket.seat,
        paymentMethod: 'Card',
        bookingDate: ticket.date
      };

      const success = generatePDFTicket(ticketData);
      
      if (success) {
        toast.success('✈️ E-ticket downloaded successfully!');
      } else {
        throw new Error('PDF generation failed');
      }
    } catch (error) {
      console.error('Error downloading ticket:', error);
      toast.error('❌ Error downloading ticket. Please try again.');
    }
  };

  const handleDownloadBoardingPass = (ticket) => {
    try {
      console.log('Download boarding pass:', ticket.id);
      
      // Convert ticket data to the format expected by PDF generator
      const ticketData = {
        ticketNumber: ticket.id,
        flightDetails: {
          flightNumber: ticket.flightNumber,
          route: ticket.route,
          class: ticket.class
        },
        passengerName: ticket.passengerName,
        selectedSeat: ticket.seat,
        bookingDate: ticket.date
      };

      const success = generateBoardingPass(ticketData);
      
      if (success) {
        toast.success('🎫 Boarding pass downloaded successfully!');
      } else {
        throw new Error('Boarding pass generation failed');
      }
    } catch (error) {
      console.error('Error downloading boarding pass:', error);
      toast.error('❌ Error downloading boarding pass. Please try again.');
    }
  };

  const handleDownload = (ticketId) => {
    console.log('Download ticket:', ticketId);
    toast.info('Legacy download function - use new PDF buttons!');
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
              <div style={headerCellStyle}>Passenger</div>
              <div style={headerCellStyle}>Amount</div>
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
                  <div>{ticket.flightNumber}</div>
                  <div style={{ fontSize: '12px', color: '#6b7280' }}>{ticket.route}</div>
                  {ticket.seat && (
                    <div style={{ fontSize: '12px', color: '#6b7280' }}>Seat: {ticket.seat}</div>
                  )}
                </div>
                <div style={cellStyle}>{ticket.date}</div>
                <div style={cellStyle}>{ticket.class}</div>
                <div style={cellStyle}>
                  {ticket.passengerName || 'N/A'}
                </div>
                <div style={cellStyle}>
                  {ticket.totalAmount || 'N/A'}
                </div>
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