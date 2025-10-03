import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Layout } from '../components/Layout';
import { BookingConfirmation } from '../components/BookingConfirmation';

export default function BookingConfirmed() {
  const navigate = useNavigate();

  // This data would come from the booking process/API response in a real app
  // For now, using sample data that matches the design
  const bookingData = {
    ticketNumber: 'TCK-10218',
    status: 'Confirmed / Paid',
    flightDetails: {
      flightNumber: 'FD-801',
      route: 'BEG → CDG',
      departure: '08:10',
      arrival: '10:35',
      class: 'Economy'
    },
    passengerName: 'Ana Petrović',
    // Additional data that would come from backend
    bookingReference: 'ABC123',
    paymentAmount: '€142.00',
    bookingDate: new Date().toISOString(),
    email: 'ana.petrovic@email.com'
  };

  const handleDownloadTicket = async () => {
    try {
      // In a real app, this would call the backend API to generate PDF
      // const response = await fetch(`/api/tickets/${bookingData.ticketNumber}/download`);
      // const blob = await response.blob();
      // const url = window.URL.createObjectURL(blob);
      // const link = document.createElement('a');
      // link.href = url;
      // link.download = `ticket-${bookingData.ticketNumber}.pdf`;
      // link.click();
      
      // For now, just show a mock action
      console.log('Downloading e-ticket for:', bookingData.ticketNumber);
      alert('E-ticket download started! (This is a demo - no actual PDF will be downloaded)');
    } catch (error) {
      console.error('Error downloading ticket:', error);
      alert('Error downloading ticket. Please try again.');
    }
  };

  const handleGoToTickets = () => {
    // Navigate to My Tickets page
    navigate('/tickets');
  };

  return (
    <Layout>
      <BookingConfirmation
        ticketNumber={bookingData.ticketNumber}
        status={bookingData.status}
        flightDetails={bookingData.flightDetails}
        passengerName={bookingData.passengerName}
        onDownloadTicket={handleDownloadTicket}
        onGoToTickets={handleGoToTickets}
      />
    </Layout>
  );
}