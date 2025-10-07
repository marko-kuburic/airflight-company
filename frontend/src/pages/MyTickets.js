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
      
      // Try to load from backend FIRST (prioritize real data)
      const userData = JSON.parse(localStorage.getItem('user') || '{}');
      if (userData.id) {
        try {
          const response = await bookingAPI.getTicketsByCustomer(userData.id);
          console.log('Backend tickets response:', response.data);
          
          // Transform tickets to display format
          const backendTickets = response.data.map(ticket => ({
            id: ticket.id,
            reservationNumber: ticket.reservation?.reservationNumber || 'N/A',
            flightNumber: ticket.flight?.flightNumber || 'N/A',
            route: ticket.flight?.route || 'N/A',
            date: ticket.flight?.departureTime ? 
              new Date(ticket.flight.departureTime).toISOString().split('T')[0] : 
              new Date().toISOString().split('T')[0],
            class: ticket.cabinClass || 'Economy',
            status: ticket.status || 'Confirmed',
            passengerName: ticket.passenger ? `${ticket.passenger.firstName || ''} ${ticket.passenger.lastName || ''}`.trim() : 'N/A',
            totalAmount: ticket.reservation?.payment?.amount || ticket.totalAmount || ticket.price || 'N/A',
            seat: ticket.seatNumber || 'N/A',
            email: ticket.passenger?.email || 'N/A'
          }));
          
          console.log('Mapped backend tickets:', backendTickets);
          allTickets = [...backendTickets];
        } catch (backendError) {
          console.warn('Backend not available, using localStorage only:', backendError);
          // Only fall back to localStorage if backend fails
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
        }
      } else {
        // No user logged in, show localStorage as fallback
        const savedBookings = JSON.parse(localStorage.getItem('myBookings') || '[]');
        const localTickets = savedBookings.map(booking => ({
          id: `TCK-${booking.reservationId}`,
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
      }
      
      setTickets(allTickets);
    } catch (error) {
      console.error('Error loading tickets:', error);
      setTickets([]);
    } finally {
      setLoading(false);
    }
  };

    
  const pageStyle = {
    padding: '32px 32px 32px 32px',
    fontFamily: 'Inter, sans-serif',
    maxWidth: '100%',
    margin: '0',
    width: '100%',
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'flex-start',
    justifyContent: 'flex-start'
  };

  const headerStyle = {
    fontSize: '32px',
    fontWeight: '600',
    color: '#1f2937',
    marginBottom: '32px',
    textAlign: 'left',
    width: '100%',
    marginLeft: '0'
  };

  const tableContainerStyle = {
    backgroundColor: 'white',
    border: '1px solid #e5e7eb',
    borderRadius: '8px',
    overflow: 'visible',
    width: '100%',             // Full width to accommodate larger table
    minWidth: '1630px',        // Increased min-width for wider table
    marginLeft: '0',
    marginRight: 'auto'        // This pushes it to the left
  };
  const tableHeaderStyle = {
    display: 'grid',
    gridTemplateColumns: '160px 260px 150px 130px 180px 140px 140px 260px',
    gap: '20px',
    padding: '20px 24px',
    backgroundColor: '#f9fafb',
    borderBottom: '1px solid #e5e7eb',
    width: '100%',
    minWidth: '1630px'
  };

  const headerCellStyle = {
    fontSize: '16px',
    fontWeight: '600',
    color: '#6b7280',
    textAlign: 'left'
  };

  const tableRowStyle = {
    display: 'grid',
    gridTemplateColumns: '160px 260px 150px 130px 180px 140px 140px 260px',
    gap: '20px',
    padding: '20px 24px',
    borderBottom: '1px solid #f3f4f6',
    alignItems: 'center',
    width: '100%',
    minWidth: '1630px'
  };

  const cellStyle = {
    fontSize: '16px',
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
      case 'CREATED':
        return {
          ...baseStyle,
          backgroundColor: '#fef3c7',
          color: '#92400e',
          borderColor: '#f59e0b'
        };
      case 'CONFIRMED':
        return {
          ...baseStyle,
          backgroundColor: '#d1fae5',
          color: '#065f46',
          borderColor: '#10b981'
        };
      case 'CANCELLED':
        return {
          ...baseStyle,
          backgroundColor: '#fee2e2',
          color: '#991b1b',
          borderColor: '#ef4444'
        };
      case 'USED':
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
      color: 'white',
      display: 'inline-flex',
      alignItems: 'center',
      justifyContent: 'center',
      width: '100px',
      height: '32px',
      fontSize: '12px'
    };

    const boardingButtonStyle = {
      ...buttonBaseStyle,
      backgroundColor: '#10b981',
      color: 'white',
      display: 'inline-flex',
      alignItems: 'center',
      justifyContent: 'center',
      width: '100px',
      height: '32px',
      fontSize: '12px'
    };

    const cancelButtonStyle = {
      ...buttonBaseStyle,
      backgroundColor: '#dc2626',
      color: 'white'
    };

    if (ticket.status === 'Confirmed') {
      return (
        <div style={{ display: 'flex', flexDirection: 'row', gap: '8px', width: '100%', alignItems: 'center' }}>
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
    toast.info('Editing functionality coming soon!');
  };

  const handleCancel = (ticketId) => {
    toast.info('Cancellation functionality coming soon!');
  };

  const handleDownloadTicket = (ticket) => {
    try {
      
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
        paymentAmount: String(ticket.totalAmount || 0), // ✅ Convert to string
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
        <h1 style={headerStyle}>My Tickets 🎫</h1>
        
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
                <div style={cellStyle}>{"TCK-" + ticket.id + "-LMN"}</div>
                <div style={cellStyle}>
                  <div>{ticket.flightNumber}</div>
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
                <div style={cellStyle}>
                  <span style={getStatusBadge(ticket.status)}>
                    {ticket.status}
                  </span>
                </div>
                <div style={{
                  ...cellStyle, 
                  display: 'flex', 
                  flexDirection: 'column', 
                  gap: '6px',
                  minWidth: '180px',
                  alignItems: 'stretch'
                }}>
                  <button 
                    style={{
                      backgroundColor: '#2563eb', 
                      color: 'white', 
                      padding: '8px 12px', 
                      fontSize: '12px',
                      fontWeight: '500',
                      border: 'none',
                      borderRadius: '4px',
                      cursor: 'pointer',
                      fontFamily: 'Inter, sans-serif'
                    }}
                    onClick={() => handleDownloadTicket(ticket)}
                    onMouseEnter={(e) => e.target.style.backgroundColor = '#1d4ed8'}
                    onMouseLeave={(e) => e.target.style.backgroundColor = '#2563eb'}
                    title="Download PDF Ticket"
                  >
                    📄 E-ticket
                  </button>
                  <button 
                    style={{
                      backgroundColor: '#10b981', 
                      color: 'white', 
                      padding: '8px 12px', 
                      fontSize: '12px',
                      fontWeight: '500',
                      border: 'none',
                      borderRadius: '4px',
                      cursor: 'pointer',
                      fontFamily: 'Inter, sans-serif'
                    }}
                    onClick={() => handleDownloadBoardingPass(ticket)}
                    onMouseEnter={(e) => e.target.style.backgroundColor = '#059669'}
                    onMouseLeave={(e) => e.target.style.backgroundColor = '#10b981'}
                    title="Download Boarding Pass"
                  >
                    🎫 Boarding Pass
                  </button>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div style={emptyStateStyle}>
            <div style={{ fontSize: '48px', marginBottom: '16px' }}>✈️</div>
            <h3 style={{ fontSize: '18px', fontWeight: '600', color: '#374151', marginBottom: '8px' }}>No Tickets Found</h3>
            <p style={{ fontSize: '14px', color: '#6b7280', margin: 0 }}>You haven't booked any flights yet. Start by searching for flights to book your first trip!</p>
          </div>
        )}
      </div>
    </Layout>
  );
}