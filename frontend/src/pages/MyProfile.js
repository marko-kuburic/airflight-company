import React, { useState, useEffect } from 'react';
import { Layout } from '../components/Layout';
import { ProfileForm } from '../components/ProfileForm';
import { SavedPaymentMethods } from '../components/SavedPaymentMethods';
import { RecentPurchases } from '../components/RecentPurchases';

export default function MyProfile() {
  const [profileData, setProfileData] = useState(null);
  const [paymentMethods, setPaymentMethods] = useState([]);
  const [recentPurchases, setRecentPurchases] = useState([]);
  const [isLoading, setIsLoading] = useState(false);

  // Mock data - in real app this would come from API
  const mockProfileData = {
    firstName: 'Ana',
    lastName: 'Petrović',
    email: 'ana@example.com',
    phone: '+381 64 123 4567',
    userId: 'user-123'
  };

  const mockPaymentMethods = [
    {
      id: 'card-1',
      type: 'Visa',
      lastFour: '1234',
      expiry: '07/27'
    }
  ];

  const mockRecentPurchases = [
    {
      ticketNumber: 'TCK-10218',
      status: 'Confirmed',
      flightNumber: 'FD-815',
      date: '2025-08-28'
    },
    {
      ticketNumber: 'TCK-10190',
      status: 'Cancelled',
      flightNumber: 'FD-829',
      date: '2025-08-12'
    }
  ];

  useEffect(() => {
    // In real app, fetch user profile data from API
    const fetchProfileData = async () => {
      setIsLoading(true);
      try {
        // Simulate API calls
        // const [profileResponse, paymentsResponse, purchasesResponse] = await Promise.all([
        //   fetch('/api/user/profile'),
        //   fetch('/api/user/payment-methods'),
        //   fetch('/api/user/recent-purchases')
        // ]);
        
        // For now, use mock data
        setTimeout(() => {
          setProfileData(mockProfileData);
          setPaymentMethods(mockPaymentMethods);
          setRecentPurchases(mockRecentPurchases);
          setIsLoading(false);
        }, 500);
      } catch (error) {
        console.error('Error fetching profile data:', error);
        setIsLoading(false);
      }
    };

    fetchProfileData();
  }, []);

  const handleProfileSave = async (formData) => {
    setIsLoading(true);
    try {
      // In real app, send update to API
      // const response = await fetch('/api/user/profile', {
      //   method: 'PUT',
      //   headers: {
      //     'Content-Type': 'application/json',
      //   },
      //   body: JSON.stringify(formData)
      // });
      
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      console.log('Profile updated:', formData);
      setProfileData({ ...profileData, ...formData });
      alert('Profile updated successfully!');
      
    } catch (error) {
      console.error('Error updating profile:', error);
      alert('Error updating profile. Please try again.');
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