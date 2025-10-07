import React, { useState, useEffect } from 'react';
import { Layout } from '../components/Layout';
import { PointsCard } from '../components/PointsCard';
import { MembershipTierCard } from '../components/MembershipTierCard';
import { authAPI } from '../services/api';

export default function LoyaltyProgram() {
  const [loyaltyData, setLoyaltyData] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchLoyaltyData = async () => {
      setIsLoading(true);
      setError(null);
      
      try {
        // Get user ID from localStorage or context
        const userStr = localStorage.getItem('user');
        if (!userStr) {
          throw new Error('User not logged in');
        }
        
        const user = JSON.parse(userStr);
        const userId = user.id;
        
        // Fetch loyalty data from API
        const response = await authAPI.getCustomerLoyalty(userId);
        setLoyaltyData(response.data);
      } catch (error) {
        console.error('Error fetching loyalty data:', error);
        setError('Failed to load loyalty program data');
        
        // Fallback to mock data if API fails
        const mockLoyaltyData = {
          points: 0,
          tier: 'BRONZE',
          earningHistory: [],
          tierThresholds: {
            'BRONZE': { min: 0, max: 10000 },
            'SILVER': { min: 10000, max: 25000 },
            'GOLD': { min: 25000, max: 50000 },
            'PLATINUM': { min: 50000, max: 100000 },
            'DIAMOND': { min: 100000, max: 999999 }
          }
        };
        setLoyaltyData(mockLoyaltyData);
      } finally {
        setIsLoading(false);
      }
    };

    fetchLoyaltyData();
  }, []);

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

  const topCardsStyle = {
    display: 'grid',
    gridTemplateColumns: '1fr 1fr',
    gap: '24px',
    marginBottom: '24px'
  };

  const historyContainerStyle = {
    gridColumn: '1 / -1'
  };

  if (isLoading) {
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
          Loading loyalty program data...
        </div>
      </Layout>
    );
  }

  if (error) {
    return (
      <Layout>
        <div style={{
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'center',
          alignItems: 'center',
          height: '400px',
          fontSize: '16px',
          color: '#dc2626'
        }}>
          <div style={{ marginBottom: '8px' }}>⚠️ {error}</div>
          <div style={{ fontSize: '14px', color: '#6b7280' }}>
            Showing sample data instead
          </div>
        </div>
      </Layout>
    );
  }

  return (
    <Layout>
      <div style={pageStyle}>
        <h1 style={headerStyle}>Loyalty Program</h1>
        
        <div style={topCardsStyle}>
          <PointsCard points={loyaltyData?.points} />
          
          <MembershipTierCard 
            tier={loyaltyData?.tier}
            currentPoints={loyaltyData?.points}
            nextTierPoints={loyaltyData?.tierThresholds}
          />
        </div>
        
        {/* Earning history removed - using real loyalty points from database */}
      </div>
    </Layout>
  );
}