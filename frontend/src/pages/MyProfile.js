import React, { useState, useEffect } from 'react';
import { Layout } from '../components/Layout';
import { ProfileForm } from '../components/ProfileForm';
import { SavedPaymentMethods } from '../components/SavedPaymentMethods';
import { RecentPurchases } from '../components/RecentPurchases';
import { authAPI, bookingAPI } from '../services/api';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';

export default function MyProfile() {
  const [profileData, setProfileData] = useState(null);
  const [paymentMethods, setPaymentMethods] = useState([]);
  const [recentPurchases, setRecentPurchases] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    loadProfileData();
  }, []);

  const loadProfileData = async () => {
    setIsLoading(true);
    try {
      // Get user data from localStorage
      const userData = JSON.parse(localStorage.getItem('user') || '{}');
      
      if (!userData.id) {
        toast.error('Please log in to view your profile');
        navigate('/login');
        return;
      }

      // Set the current user data
      setProfileData({
        firstName: userData.firstName || '',
        lastName: userData.lastName || '',
        email: userData.email || '',
        phone: userData.phone || '',
        userId: userData.id
      });

      // Load user's recent purchases/reservations
      try {
        const reservationsResponse = await bookingAPI.getReservationsByCustomer(userData.id);
        const reservations = reservationsResponse.data || [];
        
        // Transform reservations to recent purchases format
        const purchases = reservations.map(reservation => ({
          ticketNumber: reservation.reservationNumber || `RES-${reservation.id}`,
          status: reservation.status || 'Confirmed',
          flightNumber: reservation.flightNumber || 'N/A',
          date: reservation.date || new Date().toISOString().split('T')[0]
        }));
        
        setRecentPurchases(purchases);
      } catch (error) {
        console.log('No recent purchases found');
        setRecentPurchases([]);
      }

      // For now, use empty payment methods (would come from a real API)
      setPaymentMethods([]);
      
    } catch (error) {
      console.error('Error loading profile data:', error);
      toast.error('Error loading profile data');
    } finally {
      setIsLoading(false);
    }
  };

  const handleProfileSave = async (formData) => {
    setIsLoading(true);
    try {
      const userData = JSON.parse(localStorage.getItem('user') || '{}');
      
      if (!userData.id) {
        toast.error('Please log in to update your profile');
        navigate('/login');
        return;
      }

      // Update user profile via API
      const response = await authAPI.updateUserProfile(userData.id, formData);
      
      if (response.data) {
        // Update localStorage with new data
        const updatedUser = { ...userData, ...formData };
        localStorage.setItem('user', JSON.stringify(updatedUser));
        
        setProfileData({ ...profileData, ...formData });
        toast.success('Profile updated successfully!');
      }
      
    } catch (error) {
      console.error('Error updating profile:', error);
      toast.error('Error updating profile. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleAddCard = () => {
    // In real app, open modal or navigate to add card page
    console.log('Add new card');
    alert('Add new card functionality would open here');
  };

  const handleRemoveCard = async (cardId) => {
    try {
      // In real app, call API to remove card
      // await fetch(`/api/user/payment-methods/${cardId}`, {
      //   method: 'DELETE'
      // });
      
      console.log('Remove card:', cardId);
      setPaymentMethods(prev => prev.filter(card => card.id !== cardId));
      alert('Payment method removed successfully!');
      
    } catch (error) {
      console.error('Error removing card:', error);
      alert('Error removing payment method. Please try again.');
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

  const contentGridStyle = {
    display: 'grid',
    gridTemplateColumns: '2fr 1fr',
    gap: '24px'
  };

  const leftColumnStyle = {
    display: 'flex',
    flexDirection: 'column',
    gap: '24px'
  };

  const rightColumnStyle = {
    display: 'flex',
    flexDirection: 'column',
    gap: '24px'
  };

  if (isLoading && !profileData) {
    return (
      <Layout>
        <div style={{
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          height: '400px',
          fontSize: '16px',
          color: '#6b7280'
        }}>
          Loading profile...
        </div>
      </Layout>
    );
  }

  return (
    <Layout>
      <div style={pageStyle}>
        <h1 style={headerStyle}>Profile</h1>
        
        <div style={contentGridStyle}>
          <div style={leftColumnStyle}>
            <ProfileForm
              profileData={profileData}
              onSave={handleProfileSave}
              isLoading={isLoading}
            />
            
            <SavedPaymentMethods
              paymentMethods={paymentMethods}
              onAddCard={handleAddCard}
              onRemoveCard={handleRemoveCard}
            />
          </div>
          
          <div style={rightColumnStyle}>
            <RecentPurchases purchases={recentPurchases} />
          </div>
        </div>
      </div>
    </Layout>
  );
}