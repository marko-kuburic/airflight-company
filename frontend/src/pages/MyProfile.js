import React, { useState, useEffect } from 'react';
import { Layout } from '../components/Layout';
import { ProfileForm } from '../components/ProfileForm';
import { SavedPaymentMethods } from '../components/SavedPaymentMethods';
import { RecentPurchases } from '../components/RecentPurchases';
import { authAPI } from '../services/api';
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
      const userData = JSON.parse(localStorage.getItem('user') || '{}');
      
      if (!userData.id) {
        toast.error('Please log in to view your profile');
        navigate('/login');
        return;
      }

      // Get fresh profile data from backend
      try {
        const profileResponse = await authAPI.getUserProfile(userData.id);
        setProfileData(profileResponse.data);
      } catch (error) {
        console.warn('Backend profile not available, using localStorage:', error);
        // Fallback to localStorage data
        setProfileData({
          firstName: userData.firstName || '',
          lastName: userData.lastName || '',
          email: userData.email || '',
          phone: userData.phone || '',
          userId: userData.id,
          id: userData.id
        });
      }

      // Load payment methods from backend
      try {
        const paymentResponse = await authAPI.getUserPaymentMethods(userData.id);
        setPaymentMethods(paymentResponse.data || []);
      } catch (error) {
        setPaymentMethods([]);
      }

      // Load recent purchases from backend
      try {
        const reservationsResponse = await authAPI.getUserReservations(userData.id);
        const reservations = reservationsResponse.data?.reservations || [];
        
        const purchases = reservations.map(reservation => ({
          ticketNumber: reservation.reservationNumber || `RES-${reservation.id}`,
          status: reservation.status || 'Confirmed',
          flightCode: reservation.flightNumber || 'N/A',
          date: reservation.date || new Date().toISOString().split('T')[0]
        }));
        
        setRecentPurchases(purchases);
      } catch (error) {
        setRecentPurchases([]);
      }
    } catch (error) {
      console.error('Error loading profile data:', error);
      toast.error('Error loading profile data');
    } finally {
      setIsLoading(false);
    }
  };

  const handleProfileUpdate = async (updatedData) => {
    try {
      setIsLoading(true);
      const userData = JSON.parse(localStorage.getItem('user') || '{}');
      
      if (!userData.id) {
        toast.error('Please log in to update your profile');
        return;
      }

      const response = await authAPI.updateUserProfile(userData.id, updatedData);
      setProfileData(response.data);
      
      const updatedUser = { ...userData, ...response.data };
      localStorage.setItem('user', JSON.stringify(updatedUser));
      
      toast.success('Profile updated successfully!');
    } catch (error) {
      console.error('Error updating profile:', error);
      toast.error('Failed to update profile');
    } finally {
      setIsLoading(false);
    }
  };

  const handlePaymentMethodSave = async (paymentMethodData) => {
    try {
      setIsLoading(true);
      const userData = JSON.parse(localStorage.getItem('user') || '{}');
      
      if (!userData.id) {
        toast.error('Please log in to save payment methods');
        return;
      }

      await authAPI.savePaymentMethod(userData.id, paymentMethodData);
      await loadPaymentMethods();
      toast.success('Payment method saved successfully!');
    } catch (error) {
      console.error('Error saving payment method:', error);
      const errorMessage = error.response?.data?.error || 'Failed to save payment method';
      toast.error(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  const handlePaymentMethodDelete = async (paymentMethodId) => {
    try {
      setIsLoading(true);
      const userData = JSON.parse(localStorage.getItem('user') || '{}');
      
      if (!userData.id) {
        toast.error('Please log in to delete payment methods');
        return;
      }

      await authAPI.deletePaymentMethod(userData.id, paymentMethodId);
      await loadPaymentMethods();
      toast.success('Payment method deleted successfully!');
    } catch (error) {
      console.error('Error deleting payment method:', error);
      toast.error('Failed to delete payment method');
    } finally {
      setIsLoading(false);
    }
  };

  const loadPaymentMethods = async () => {
    try {
      const userData = JSON.parse(localStorage.getItem('user') || '{}');
      if (userData.id) {
        const response = await authAPI.getUserPaymentMethods(userData.id);
        setPaymentMethods(response.data || []);
      }
    } catch (error) {
      console.error('Error loading payment methods:', error);
    }
  };

  return (
    <Layout>
      <div style={{
        padding: '24px',
        fontFamily: 'Inter, sans-serif',
        backgroundColor: '#f8fafc',
        minHeight: '100vh'
      }}>
        <div style={{
          maxWidth: '1200px',
          margin: '0 auto'
        }}>
          <h1 style={{
            fontSize: '28px',
            fontWeight: '600',
            color: '#1f2937',
            marginBottom: '32px'
          }}>
            Profile
          </h1>

          {isLoading ? (
            <div style={{
              display: 'flex',
              justifyContent: 'center',
              alignItems: 'center',
              padding: '40px',
              backgroundColor: 'white',
              borderRadius: '8px',
              border: '1px solid #e5e7eb'
            }}>
              <p style={{ color: '#6b7280' }}>Loading profile...</p>
            </div>
          ) : (
            <div style={{
              display: 'grid',
              gridTemplateColumns: '1fr',
              gap: '24px'
            }}>
              <div style={{
                backgroundColor: 'white',
                borderRadius: '8px',
                border: '1px solid #e5e7eb',
                padding: '24px'
              }}>
                <ProfileForm 
                  profileData={profileData}
                  onUpdate={handleProfileUpdate}
                  isLoading={isLoading}
                />
              </div>

              <div style={{
                display: 'grid',
                gridTemplateColumns: '1fr 1fr',
                gap: '24px'
              }}>
                <div style={{
                  backgroundColor: 'white',
                  borderRadius: '8px',
                  border: '1px solid #e5e7eb',
                  padding: '24px'
                }}>
                  <SavedPaymentMethods 
                    paymentMethods={paymentMethods}
                    onAddCard={handlePaymentMethodSave}
                    onRemoveCard={handlePaymentMethodDelete}
                    isLoading={isLoading}
                  />
                </div>

                <div style={{
                  backgroundColor: 'white',
                  borderRadius: '8px',
                  border: '1px solid #e5e7eb',
                  padding: '24px'
                }}>
                  <RecentPurchases 
                    purchases={recentPurchases}
                    onViewTickets={() => navigate('/tickets')}
                  />
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </Layout>
  );
}