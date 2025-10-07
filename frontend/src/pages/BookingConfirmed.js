import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { Layout } from '../components/Layout';
import { BookingConfirmation } from '../components/BookingConfirmation';
import { generatePDFTicket, generateBoardingPass } from '../utils/pdfGenerator';
import toast from 'react-hot-toast';

export default function BookingConfirmed() {
  const navigate = useNavigate();
  const location = useLocation();
  
  // Get the booking data passed from the payment page
  const navigationState = location.state || {};
  const {
    reservationNumber,
    reservationData,
    paymentData,
    selectedFlight,
    passengerData,
    selectedSeat,
    seatPrice,
    totalPrice,
    paymentMethod
  } = navigationState;

  // If no booking data is available, redirect to search
  if (!selectedFlight || !passengerData) {
    navigate('/search');
    return null;
  }

  // Format flight details from actual booking data
  const bookingData = {
    ticketNumber: reservationNumber || `TCK-${Date.now().toString().slice(-6)}`,
    status: 'Confirmed / Paid',
    flightDetails: {
      flightNumber: selectedFlight.flightNumber || `${selectedFlight.airline || 'FD'}-${selectedFlight.flightId || '801'}`,
      route: `${selectedFlight.departureAirport || selectedFlight.origin} → ${selectedFlight.arrivalAirport || selectedFlight.destination}`,
      departure: selectedFlight.departureTime || '08:10',
      arrival: selectedFlight.arrivalTime || '10:35',
      class: selectedFlight.cabinClass || 'Economy'
    },
    passengerName: `${passengerData.firstName} ${passengerData.lastName}`,
    bookingReference: reservationData?.id ? `REF-${reservationData.id}` : `REF-${Date.now().toString().slice(-6)}`,
    paymentAmount: `€${(totalPrice || selectedFlight.currentPrice || selectedFlight.price || 0).toFixed(2)}`,
    bookingDate: new Date().toISOString(),
    email: passengerData.email,
    selectedSeat: selectedSeat,
    seatPrice: seatPrice,
    paymentMethod: paymentMethod || 'card'
  };

  const handleDownloadTicket = async () => {
    try {
      console.log('Downloading e-ticket for:', bookingData.ticketNumber);
      
      // Use the new PDF generator
      const success = generatePDFTicket(bookingData);
      
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

  const handleDownloadBoardingPass = async () => {
    try {
      console.log('Downloading boarding pass for:', bookingData.ticketNumber);
      
      // Generate boarding pass
      const success = generateBoardingPass(bookingData);
      
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
        bookingReference={bookingData.bookingReference}
        paymentAmount={bookingData.paymentAmount}
        email={bookingData.email}
        selectedSeat={bookingData.selectedSeat}
        seatPrice={bookingData.seatPrice}
        paymentMethod={bookingData.paymentMethod}
        onDownloadTicket={handleDownloadTicket}
        onDownloadBoardingPass={handleDownloadBoardingPass}
        onGoToTickets={handleGoToTickets}
      />
    </Layout>
  );
}