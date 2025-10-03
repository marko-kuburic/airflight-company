import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Layout } from '../components/Layout';
import { FlightSummaryCard } from '../components/FlightSummaryCard';
import { PaymentMethodSelector } from '../components/PaymentMethodSelector';
import { bookingAPI } from '../services/api';
import toast from 'react-hot-toast';

export default function Payment() {
  const [paymentData, setPaymentData] = useState(null);
  const [processing, setProcessing] = useState(false);
  const location = useLocation();
  const navigate = useNavigate();
  
  // Get booking data from route state
  const { selectedFlight, passengerData, selectedSeat } = location.state || {};
  
  // Fallback to sample data if no state
  const flightData = selectedFlight || {
    flightNumber: 'AC101',
    route: 'BEG → CDG',
    departure: '08:10',
    arrival: '10:40',
    class: 'Economy',
    fareIncludes: 'Offer: 24h',
    price: '€250'
  };

  const handlePaymentChange = (data) => {
    setPaymentData(data);
  };

  const handleBack = () => {
    navigate('/booking', { state: { selectedFlight } });
  };

  const handlePay = async () => {
    if (!paymentData) {
      toast.error('Please select a payment method');
      return;
    }

    if (!passengerData) {
      toast.error('Missing passenger information');
      return;
    }

    setProcessing(true);

    try {
      // Create booking/reservation
      const reservationData = {
        flightId: flightData.id,
        passengerData,
        selectedSeat,
        paymentData
      };

      // For now, simulate successful payment
      setTimeout(() => {
        toast.success('Payment successful! Booking confirmed.');
        navigate('/booking-confirmed', {
          state: {
            reservationNumber: 'RES' + Date.now(),
            flightData,
            passengerData,
            selectedSeat
          }
        });
      }, 2000);

    } catch (error) {
      console.error('Payment error:', error);
      toast.error('Payment failed. Please try again.');
      setProcessing(false);
    }
  };

  const pageStyle = {
    padding: '24px',
    maxWidth: '800px',
    margin: '0 auto',
    fontFamily: 'Inter, sans-serif'
  };

  const headerStyle = {
    fontSize: '24px',
    fontWeight: '600',
    color: '#1f2937',
    marginBottom: '24px'
  };

  const flightCardStyle = {
    backgroundColor: 'white',
    border: '1px solid #e5e7eb',
    borderRadius: '8px',
    padding: '16px',
    marginBottom: '24px',
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center'
  };

  const selectedFlightStyle = {
    fontSize: '14px',
    color: '#6b7280',
    marginBottom: '4px'
  };

  const flightDetailsStyle = {
    fontSize: '14px',
    fontWeight: '600',
    color: '#1f2937'
  };

  const offerBadgeStyle = {
    backgroundColor: '#fef3c7',
    border: '1px solid #f59e0b',
    borderRadius: '6px',
    padding: '4px 8px',
    fontSize: '12px',
    color: '#92400e'
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

  const payButtonStyle = {
    backgroundColor: '#10b981',
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
        <h1 style={headerStyle}>Payment</h1>
        
        <div style={flightCardStyle}>
          <div>
            <div style={selectedFlightStyle}>Selected</div>
            <div style={flightDetailsStyle}>
              {flightData.flightNumber} • {flightData.route} • {flightData.departure}/{flightData.arrival} • {flightData.class}
            </div>
            {selectedSeat && (
              <div style={{ fontSize: '12px', color: '#6b7280', marginTop: '4px' }}>
                Seat: {selectedSeat}
              </div>
            )}
          </div>
          <div style={offerBadgeStyle}>
            {flightData.price || '€250'}
          </div>
        </div>
        
        <PaymentMethodSelector onPaymentChange={handlePaymentChange} />

        <div style={buttonContainerStyle}>
          <button 
            style={backButtonStyle}
            onClick={handleBack}
            disabled={processing}
            onMouseEnter={(e) => e.target.style.backgroundColor = '#f9fafb'}
            onMouseLeave={(e) => e.target.style.backgroundColor = 'white'}
          >
            Back
          </button>
          <button 
            style={{
              ...payButtonStyle,
              backgroundColor: processing ? '#9ca3af' : '#10b981',
              cursor: processing ? 'not-allowed' : 'pointer'
            }}
            onClick={handlePay}
            disabled={processing}
            onMouseEnter={(e) => !processing && (e.target.style.backgroundColor = '#059669')}
            onMouseLeave={(e) => !processing && (e.target.style.backgroundColor = '#10b981')}
          >
            {processing ? 'Processing...' : 'Pay'}
          </button>
        </div>
      </div>
    </Layout>
  );
}