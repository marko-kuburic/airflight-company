import React, { useState } from 'react';
import { Layout } from '../components/Layout';
import { FlightSummaryCard } from '../components/FlightSummaryCard';
import { PaymentMethodSelector } from '../components/PaymentMethodSelector';

export default function Payment() {
  const [paymentData, setPaymentData] = useState(null);
  
  // Sample flight data - would come from props or state in real app
  const selectedFlight = {
    flightNumber: 'FD-801',
    route: 'BEG → CDG',
    departure: '08:10',
    arrival: '10:35',
    class: 'Economy',
    fareIncludes: 'Offer: 09:35'
  };

  const handlePaymentChange = (data) => {
    setPaymentData(data);
  };

  const handleBack = () => {
    // Navigate back to booking details
    console.log('Back to booking details');
  };

  const handlePay = () => {
    // Process payment
    console.log('Processing payment', { paymentData });
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
              {selectedFlight.flightNumber} • {selectedFlight.route} • {selectedFlight.departure}/{selectedFlight.arrival} • {selectedFlight.class}
            </div>
          </div>
          <div style={offerBadgeStyle}>
            Offer: 09:35
          </div>
        </div>
        
        <PaymentMethodSelector onPaymentChange={handlePaymentChange} />

        <div style={buttonContainerStyle}>
          <button 
            style={backButtonStyle}
            onClick={handleBack}
            onMouseEnter={(e) => e.target.style.backgroundColor = '#f9fafb'}
            onMouseLeave={(e) => e.target.style.backgroundColor = 'white'}
          >
            Back
          </button>
          <button 
            style={payButtonStyle}
            onClick={handlePay}
            onMouseEnter={(e) => e.target.style.backgroundColor = '#059669'}
            onMouseLeave={(e) => e.target.style.backgroundColor = '#10b981'}
          >
            Pay
          </button>
        </div>
      </div>
    </Layout>
  );
}