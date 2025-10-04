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
  const [seatPrice, setSeatPrice] = useState(0);
  const [isFormValid, setIsFormValid] = useState(false);
  const [formErrors, setFormErrors] = useState({});
  const [showValidationErrors, setShowValidationErrors] = useState(false);
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

  const handleFormValidation = (isValid, errors) => {
    setIsFormValid(isValid);
    setFormErrors(errors);
  };

  const handleSeatSelection = (seatNumber, seatInfo) => {
    setSelectedSeat(seatNumber);
  };

  const handleSeatPriceChange = (price) => {
    setSeatPrice(price);
  };

  const handleBackToResults = () => {
    navigate('/search');
  };

  const handleContinue = () => {
    setShowValidationErrors(true);
    
    // Check form validation
    if (!isFormValid || !passengerData) {
      const missingFields = [];
      if (!passengerData?.firstName) missingFields.push('First Name');
      if (!passengerData?.lastName) missingFields.push('Last Name');
      if (!passengerData?.dateOfBirth) missingFields.push('Date of Birth');
      if (!passengerData?.documentNumber) missingFields.push('Document Number');
      if (!passengerData?.phone) missingFields.push('Phone');
      if (!passengerData?.email) missingFields.push('Email');
      
      if (missingFields.length > 0) {
        toast.error(`Please fill in the following required fields: ${missingFields.join(', ')}`);
        return;
      }
      
      if (Object.keys(formErrors).length > 0) {
        toast.error('Please fix the validation errors in the form');
        return;
      }
    }

    if (!selectedSeat) {
      toast.error('Please select a seat');
      return;
    }

    // Calculate total price
    const basePrice = parseFloat(selectedFlight.currentPrice || selectedFlight.price || '0');
    const totalPrice = basePrice + seatPrice;

    // Navigate to payment with all booking data
    navigate('/payment', { 
      state: { 
        selectedFlight,
        passengerData,
        selectedSeat,
        seatPrice,
        totalPrice
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
            <PassengerDetailsForm 
              passengerData={passengerData}
              onFormChange={handlePassengerFormChange}
              onValidationChange={handleFormValidation}
            />
          </div>
          
          <div style={sectionStyle}>
            <h2 style={sectionTitleStyle}>Seat Selection</h2>
            <SeatSelection 
              onSeatSelect={handleSeatSelection}
              onPriceChange={handleSeatPriceChange}
            />
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
            style={{
              ...continueButtonStyle,
              backgroundColor: (!isFormValid || !selectedSeat) && showValidationErrors ? '#dc2626' : '#2563eb'
            }}
            onClick={handleContinue}
            onMouseEnter={(e) => {
              if ((!isFormValid || !selectedSeat) && showValidationErrors) {
                e.target.style.backgroundColor = '#b91c1c';
              } else {
                e.target.style.backgroundColor = '#1d4ed8';
              }
            }}
            onMouseLeave={(e) => {
              if ((!isFormValid || !selectedSeat) && showValidationErrors) {
                e.target.style.backgroundColor = '#dc2626';
              } else {
                e.target.style.backgroundColor = '#2563eb';
              }
            }}
          >
            {(!isFormValid || !selectedSeat) && showValidationErrors ? 'Please Complete Required Fields' : 'Continue'}
          </button>
        </div>
      </div>
    </Layout>
  );
}