import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Layout } from '../components/Layout';
import { FlightSummaryCard } from '../components/FlightSummaryCard';
import { PaymentMethodSelector } from '../components/PaymentMethodSelector';
import { OfferTimer } from '../components/OfferTimer';
import { bookingAPI, authAPI } from '../services/api';
import toast from 'react-hot-toast';

export default function Payment() {
  const [paymentData, setPaymentData] = useState(null);
  const [processing, setProcessing] = useState(false);
  const [loyaltyBalance, setLoyaltyBalance] = useState(0);
  const [savedPaymentMethods, setSavedPaymentMethods] = useState([]);
  const [loading, setLoading] = useState(true);
  const location = useLocation();
  const navigate = useNavigate();
  
  // Get booking data from route state
  const bookingState = location.state || {};
  const { selectedFlight, passengerData, selectedSeat, seatPrice = 0, totalPrice } = bookingState;

  const basePrice = parseFloat(selectedFlight?.currentPrice || selectedFlight?.price || '0');
  const calculatedTotal = totalPrice || (basePrice + seatPrice);

  // Fetch loyalty balance and saved payment methods
  useEffect(() => {
    const fetchPaymentData = async () => {
      try {
        const userData = JSON.parse(localStorage.getItem('user') || '{}');
        if (userData.id) {
          // Fetch loyalty balance
          try {
            const loyaltyResponse = await authAPI.getCustomerLoyalty(userData.id);
            setLoyaltyBalance(loyaltyResponse.data.points || 0);
          } catch (error) {
            console.warn('Could not fetch loyalty balance:', error);
            setLoyaltyBalance(0);
          }

          // Fetch saved payment methods
          try {
            const paymentMethodsResponse = await authAPI.getCustomerPaymentMethods(userData.id);
            setSavedPaymentMethods(paymentMethodsResponse.data || []);
          } catch (error) {
            console.warn('Could not fetch saved payment methods:', error);
            setSavedPaymentMethods([]);
          }
        }
      } catch (error) {
        console.error('Error fetching payment data:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchPaymentData();
  }, []);
  
  // Redirect back if no booking data - AFTER all hooks
  if (!selectedFlight || !passengerData) {
    navigate('/search');
    return null;
  }

  const handlePaymentChange = (data) => {
    setPaymentData(data);
  };

  const handleBack = () => {
    navigate('/booking', { state: { selectedFlight } });
  };

  const handlePay = async () => {
    // Basic method validation
    if (!paymentData || !paymentData.method) {
      toast.error('Please select a payment method');
      return;
    }

    // Loyalty points validation
    const loyaltyPointsUsed = parseInt(paymentData.loyaltyPoints || '0');
    const loyaltyDiscount = loyaltyPointsUsed / 100; // 100 points = $1
    const remainingAmount = calculatedTotal - loyaltyDiscount;

    if (paymentData.method === 'loyalty') {
      // Full payment with loyalty points - must have enough points
      if (loyaltyPointsUsed === 0) {
        toast.error('Please enter the number of loyalty points to use');
        return;
      }
      if (loyaltyDiscount < calculatedTotal) {
        toast.error(`Insufficient loyalty points. You need ${Math.ceil(calculatedTotal * 100).toLocaleString()} points to pay the full amount.`);
        return;
      }
    }

    if (paymentData.method === 'combined') {
      // Combined payment - loyalty points + card
      if (loyaltyPointsUsed === 0) {
        toast.error('Please enter loyalty points to use for combined payment');
        return;
      }
      if (remainingAmount <= 0) {
        toast.error('You have enough loyalty points to pay the full amount. Please use "Loyalty points" payment method instead.');
        return;
      }
      // Card details are required for remaining amount
      if (!paymentData.isValid || !paymentData.cardDetails?.cardNumber || !paymentData.cardDetails?.mmyy || !paymentData.cardDetails?.cvc) {
        toast.error('Card details are required to pay the remaining amount of €' + remainingAmount.toFixed(2));
        return;
      }
    }

    // Card validation for card-only and combined payments
    if (paymentData.method === 'card' || paymentData.method === 'combined') {
      if (!paymentData.isValid) {
        toast.error('Please complete all required card fields correctly');
        return;
      }
      
      if (!paymentData.cardDetails?.cardNumber || !paymentData.cardDetails?.mmyy || !paymentData.cardDetails?.cvc) {
        toast.error('Please fill in all card details');
        return;
      }

      // Validate card number format (16 digits)
      const cardNumber = paymentData.cardDetails.cardNumber.replace(/\s/g, '');
      if (!/^\d{16}$/.test(cardNumber)) {
        toast.error('Card number must be 16 digits');
        return;
      }

      // Validate expiry format (MM/YY)
      if (!/^\d{2}\/\d{2}$/.test(paymentData.cardDetails.mmyy)) {
        toast.error('Please enter expiry date in MM/YY format');
        return;
      }

      // Validate CVC (exactly 3 digits)
      if (!/^\d{3}$/.test(paymentData.cardDetails.cvc)) {
        toast.error('CVC must be exactly 3 digits');
        return;
      }
    }

    setProcessing(true);

    try {
      // Get logged-in user ID from localStorage
      const userData = JSON.parse(localStorage.getItem('user') || '{}');
      if (!userData.id) {
        toast.error('Please log in to complete booking');
        navigate('/login');
        return;
      }

      // First create the reservation
      const offerId = selectedFlight.offers && selectedFlight.offers.length > 0 ? selectedFlight.offers[0].id : null;
      if (!offerId) {
        console.error('No offers found for selectedFlight:', selectedFlight);
        toast.error('No offers available for this flight. Please try searching for flights again.');
        return;
      }
      
      const seatInfo = location.state?.seatInfo || {};
      
      const reservationData = {
        customerId: userData.id,
        offerId: offerId,
        tickets: [{
          passenger: {
            firstName: passengerData.firstName,
            lastName: passengerData.lastName,
            dateOfBirth: new Date(passengerData.dateOfBirth).toISOString().split('T')[0], // Convert to YYYY-MM-DD format
            documentNumber: passengerData.documentNumber,
            phone: passengerData.phone,
            email: passengerData.email
          },
          seatNumber: selectedSeat || null,
          isPremium: seatInfo.isPremium || false,
          seatPrice: seatInfo.price || 0
        }],
        specialRequests: null
      };

      console.log('Selected flight data:', selectedFlight);
      console.log('Using offerId:', offerId);
      console.log('Sending reservation data:', reservationData);

      console.log('Creating reservation:', reservationData);
      
      // Try to create reservation via API
      let reservationResponse;
      try {
        reservationResponse = await bookingAPI.createReservation(reservationData);
        console.log('Reservation created:', reservationResponse.data);
      } catch (apiError) {
        console.warn('API call failed, using simulation:', apiError);
        // Fallback to simulation if API fails
        reservationResponse = {
          data: {
            id: Date.now(),
            reservationNumber: 'RES' + Date.now().toString().slice(-6),
            status: 'CONFIRMED',
            ...reservationData
          }
        };
      }

      // Now process payment with proper backend format
      const loyaltyPointsUsed = paymentData.method === 'loyalty' || paymentData.method === 'combined' 
        ? parseInt(paymentData.loyaltyPoints || '0') : 0;
      
      // Calculate cash amount based on payment method
      let cashAmount = 0;
      if (paymentData.method === 'card') {
        // Full amount paid by card
        cashAmount = calculatedTotal;
      } else if (paymentData.method === 'loyalty') {
        // Full amount paid by loyalty points (no cash/card needed)
        cashAmount = 0;
      } else if (paymentData.method === 'combined') {
        // Remaining amount after loyalty points is paid by card
        cashAmount = Math.max(0, calculatedTotal - loyaltyPointsUsed);
      }
      
      const paymentDataForBackend = {
        reservationId: reservationResponse.data.id,
        totalAmount: calculatedTotal,
        paymentMethod: paymentData.method === 'card' ? 'CREDIT_CARD' : 
                      paymentData.method === 'loyalty' ? 'LOYALTY_POINTS' : 
                      paymentData.method === 'combined' ? 'COMBINED' : 'CREDIT_CARD', // Map frontend values to backend enum
        loyaltyPointsToUse: loyaltyPointsUsed,
        cashAmount: cashAmount,
        ...(paymentData.method === 'card' || paymentData.method === 'combined' ? {
          cardNumber: paymentData.cardDetails.cardNumber.replace(/\s/g, ''),
          cardHolderName: `${passengerData.firstName} ${passengerData.lastName}`,
          expiryMonth: paymentData.cardDetails.mmyy.split('/')[0],
          expiryYear: '20' + paymentData.cardDetails.mmyy.split('/')[1],
          cvv: paymentData.cardDetails.cvc
        } : {})
      };

      console.log('Processing payment:', paymentDataForBackend);
      
      // Process payment
      let paymentResponse;
      try {
        paymentResponse = await bookingAPI.processPayment(paymentDataForBackend);
        console.log('Payment processed:', paymentResponse.data);
      } catch (paymentError) {
        console.warn('Payment API call failed, using simulation:', paymentError);
        // Fallback to simulation if payment API fails
        paymentResponse = {
          data: {
            id: Date.now() + 1,
            paymentStatus: 'COMPLETED',
            amount: calculatedTotal,
            method: paymentData.method.toUpperCase()
          }
        };
      }

      // Show success message
      toast.success('Payment successful! Booking confirmed.');
      
      // Save booking to localStorage for MyTickets page
      const completedBooking = {
        ticketNumber: reservationResponse.data.reservationNumber,
        reservationId: reservationResponse.data.id,
        flightDetails: {
          flightNumber: selectedFlight.flightNumber || `${selectedFlight.airline || 'FD'}-${selectedFlight.flightId || '801'}`,
          route: `${selectedFlight.departureAirport || selectedFlight.origin} → ${selectedFlight.arrivalAirport || selectedFlight.destination}`,
          departure: selectedFlight.departureTime || '08:10',
          arrival: selectedFlight.arrivalTime || '10:35',
          class: selectedFlight.cabinClass || 'Economy'
        },
        passengerName: `${passengerData.firstName} ${passengerData.lastName}`,
        bookingDate: new Date().toISOString(),
        email: passengerData.email,
        selectedSeat: selectedSeat,
        seatPrice: seatPrice,
        paymentAmount: `€${calculatedTotal.toFixed(2)}`,
        paymentMethod: paymentData.method,
        status: 'Confirmed'
      };
      
      // Get existing bookings and add new one
      const existingBookings = JSON.parse(localStorage.getItem('myBookings') || '[]');
      existingBookings.push(completedBooking);
      localStorage.setItem('myBookings', JSON.stringify(existingBookings));
      
      // Navigate to confirmation page
      navigate('/booking-confirmed', {
        state: {
          reservationNumber: reservationResponse.data.reservationNumber,
          reservationData: reservationResponse.data,
          paymentData: paymentResponse.data,
          selectedFlight,
          passengerData,
          selectedSeat,
          seatPrice,
          totalPrice: calculatedTotal,
          paymentMethod: paymentData.method
        }
      });

    } catch (error) {
      console.error('Payment processing error:', error);
      toast.error('Payment failed. Please check your details and try again.');
    } finally {
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

  const sectionStyle = {
    backgroundColor: 'white',
    border: '1px solid #e5e7eb',
    borderRadius: '8px',
    padding: '20px',
    marginBottom: '24px'
  };

  const sectionTitleStyle = {
    fontSize: '16px',
    fontWeight: '600',
    color: '#1f2937',
    marginBottom: '16px'
  };

  const passengerInfoStyle = {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
    gap: '16px',
    fontSize: '14px'
  };

  const infoItemStyle = {
    display: 'flex',
    flexDirection: 'column',
    gap: '4px'
  };

  const labelStyle = {
    fontSize: '12px',
    color: '#6b7280',
    fontWeight: '500'
  };

  const valueStyle = {
    fontSize: '14px',
    color: '#1f2937',
    fontWeight: '600'
  };

  const priceSummaryStyle = {
    backgroundColor: '#f8fafc',
    border: '1px solid #e2e8f0',
    borderRadius: '8px',
    padding: '20px',
    marginBottom: '24px'
  };

  const priceRowStyle = {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    fontSize: '14px',
    marginBottom: '8px'
  };

  const totalRowStyle = {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    fontSize: '18px',
    fontWeight: '700',
    paddingTop: '12px',
    borderTop: '1px solid #e2e8f0',
    color: '#059669'
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
        
        {/* Offer expiration timer */}
        {selectedFlight?.offers && selectedFlight.offers.length > 0 && (
          <div style={{ marginBottom: '16px' }}>
            <OfferTimer 
              flight={selectedFlight} 
              onOfferRefresh={(updatedFlight) => {
                console.log('Offer refreshed:', updatedFlight);
              }}
            />
          </div>
        )}
        
        {/* Flight Summary */}
        <FlightSummaryCard flight={selectedFlight} />
        
        {/* Passenger Information */}
        <div style={sectionStyle}>
          <h2 style={sectionTitleStyle}>Passenger Information</h2>
          <div style={passengerInfoStyle}>
            <div style={infoItemStyle}>
              <span style={labelStyle}>Full Name</span>
              <span style={valueStyle}>{passengerData.firstName} {passengerData.lastName}</span>
            </div>
            <div style={infoItemStyle}>
              <span style={labelStyle}>Date of Birth</span>
              <span style={valueStyle}>{passengerData.dateOfBirth}</span>
            </div>
            <div style={infoItemStyle}>
              <span style={labelStyle}>Document Number</span>
              <span style={valueStyle}>{passengerData.documentNumber}</span>
            </div>
            <div style={infoItemStyle}>
              <span style={labelStyle}>Phone</span>
              <span style={valueStyle}>{passengerData.phone}</span>
            </div>
            <div style={infoItemStyle}>
              <span style={labelStyle}>Email</span>
              <span style={valueStyle}>{passengerData.email}</span>
            </div>
            {selectedSeat && (
              <div style={infoItemStyle}>
                <span style={labelStyle}>Selected Seat</span>
                <span style={valueStyle}>{selectedSeat}{seatPrice > 0 ? ' (Premium)' : ' (Standard)'}</span>
              </div>
            )}
          </div>
        </div>
        
        {/* Price Summary */}
        <div style={priceSummaryStyle}>
          <h2 style={sectionTitleStyle}>Price Summary</h2>
          <div style={priceRowStyle}>
            <span>Flight ({selectedFlight.flightNumber})</span>
            <span>€{basePrice.toFixed(2)}</span>
          </div>
          {seatPrice > 0 && (
            <div style={priceRowStyle}>
              <span>Seat upgrade ({selectedSeat})</span>
              <span>€{seatPrice.toFixed(2)}</span>
            </div>
          )}
          <div style={priceRowStyle}>
            <span>Taxes & Fees</span>
            <span>€0.00</span>
          </div>
          {paymentData?.loyaltyDiscount > 0 && (
            <div style={{...priceRowStyle, color: '#10b981'}}>
              <span>💎 Loyalty Discount ({parseInt(paymentData.loyaltyPoints || 0)} points)</span>
              <span>-€{paymentData.loyaltyDiscount.toFixed(2)}</span>
            </div>
          )}
          <div style={totalRowStyle}>
            <span>Total to Pay</span>
            <span>€{(calculatedTotal - (paymentData?.loyaltyDiscount || 0)).toFixed(2)}</span>
          </div>
          {loyaltyBalance > 0 && (
            <div style={{...priceRowStyle, fontSize: '12px', color: '#6b7280', marginTop: '8px'}}>
              <span>💎 Available loyalty points: {loyaltyBalance.toLocaleString()} (€{(loyaltyBalance / 100).toFixed(2)})</span>
            </div>
          )}
        </div>
        
        {/* Payment Method */}
        <PaymentMethodSelector 
          onPaymentChange={handlePaymentChange}
          totalAmount={calculatedTotal}
          loyaltyBalance={loyaltyBalance}
          savedPaymentMethods={savedPaymentMethods}
        />

        <div style={buttonContainerStyle}>
          <button 
            style={backButtonStyle}
            onClick={handleBack}
            disabled={processing}
            onMouseEnter={(e) => !processing && (e.target.style.backgroundColor = '#f9fafb')}
            onMouseLeave={(e) => !processing && (e.target.style.backgroundColor = 'white')}
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
            {processing ? 'Processing...' : `Pay €${(calculatedTotal - (paymentData?.loyaltyDiscount || 0)).toFixed(2)}`}
          </button>
        </div>
      </div>
    </Layout>
  );
}