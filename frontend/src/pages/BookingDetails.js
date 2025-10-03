import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Layout } from '../components/Layout';
import { FlightSummaryCard } from '../components/FlightSummaryCard';
import { PassengerDetailsForm } from '../components/PassengerDetailsForm';
import { SeatSelection } from '../components/SeatSelection';
import toast from 'react-hot-toast';

export default function BookingDetails() {
  const [passengerData, setPassengerData] = useState(null);
  const [selectedSeat, setSelectedSeat] = useState(null);
  const location = useLocation();
  const navigate = useNavigate();
  
  // Get flight data from route state or use sample data
  const selectedFlight = location.state?.selectedFlight || {
    flightNumber: 'AC101',
    route: 'BEG → CDG',
    departure: '08:10',
    arrival: '10:40',
    class: 'Economy',
    fareIncludes: '1 carry-on + 1 checked',
    price: '€250'
  };

  const handlePassengerFormChange = (data) => {
    setPassengerData(data);
  };

  const handleSeatSelection = (seatNumber) => {
    setSelectedSeat(seatNumber);
  };

  const handleBackToResults = () => {
    navigate('/search');
  };

  const handleContinue = () => {
    if (!passengerData) {
      toast.error('Please fill in passenger details');
      return;
    }

    if (!selectedSeat) {
      toast.error('Please select a seat');
      return;
    }

    // Navigate to payment with all booking data
    navigate('/payment', { 
      state: { 
        selectedFlight,
        passengerData,
        selectedSeat
      } 
    });
  };

  const pageStyle = {
    padding: '24px',
    maxWidth: '1200px',
    margin: '0 auto',
    fontFamily: 'Inter, sans-serif'
  };

  const headerStyle = {
    fontSize: '24px',
    fontWeight: '600',
    color: '#1f2937',
    marginBottom: '24px'
  };

  const mainContentStyle = {
    display: 'grid',
    gridTemplateColumns: '1fr',
    gap: '24px'
  };

  const sectionStyle = {
    backgroundColor: 'white',
    border: '1px solid #e5e7eb',
    borderRadius: '8px',
    padding: '20px'
  };

  const sectionTitleStyle = {
    fontSize: '16px',
    fontWeight: '600',
    color: '#1f2937',
    marginBottom: '16px'
  };

  const buttonContainerStyle = {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginTop: '32px',
    paddingTop: '20px',
    borderTop: '1px solid #e5e7eb'
  };

  const backButtonStyle = {
    backgroundColor: 'white',
    border: '1px solid #d1d5db',
    borderRadius: '8px',
    padding: '12px 24px',
    fontSize: '14px',
    fontWeight: '500',
    color: '#374151',
    cursor: 'pointer',
    fontFamily: 'Inter, sans-serif',
    transition: 'background-color 0.2s ease'
  };

  const continueButtonStyle = {
    backgroundColor: '#2563eb',
    color: 'white',
    border: 'none',
    borderRadius: '8px',
    padding: '12px 32px',
    fontSize: '14px',
    fontWeight: '600',
    cursor: 'pointer',
    fontFamily: 'Inter, sans-serif',
    transition: 'background-color 0.2s ease'
  };

  return (
    <Layout>
      <div style={pageStyle}>
        <h1 style={headerStyle}>Search & Book Flights — Passenger Details</h1>
        
        <FlightSummaryCard flight={selectedFlight} />
        
        <div style={mainContentStyle}>
          <div style={sectionStyle}>
            <h2 style={sectionTitleStyle}>Passenger Information</h2>
            <PassengerDetailsForm onFormChange={handlePassengerFormChange} />
          </div>
          
          <div style={sectionStyle}>
            <h2 style={sectionTitleStyle}>Seat Selection</h2>
            <SeatSelection onSeatSelect={handleSeatSelection} />
          </div>
        </div>

        <div style={buttonContainerStyle}>
          <button 
            style={backButtonStyle}
            onClick={handleBackToResults}
            onMouseEnter={(e) => e.target.style.backgroundColor = '#f9fafb'}
            onMouseLeave={(e) => e.target.style.backgroundColor = 'white'}
          >
            Back to Results
          </button>
          <button 
            style={continueButtonStyle}
            onClick={handleContinue}
            onMouseEnter={(e) => e.target.style.backgroundColor = '#1d4ed8'}
            onMouseLeave={(e) => e.target.style.backgroundColor = '#2563eb'}
          >
            Continue
          </button>
        </div>
      </div>
    </Layout>
  );
}