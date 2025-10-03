import React, { useState, useEffect } from 'react';
import { Layout } from '../components/Layout';
import { PointsCard } from '../components/PointsCard';
import { MembershipTierCard } from '../components/MembershipTierCard';
import { EarningHistoryTable } from '../components/EarningHistoryTable';

export default function LoyaltyProgram() {
  const [loyaltyData, setLoyaltyData] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  // Mock loyalty data - in real app this would come from API
  const mockLoyaltyData = {
    points: 18450,
    tier: 'Gold',
    earningHistory: [
      {
        date: '2025-08-28',
        flight: 'FD-815 BEG → CDG',
        status: 'Completed',
        points: 1250
      },
      {
        date: '2025-07-02',
        flight: 'FD-702 BEG → FRA',
        status: 'Completed',
        points: 620
      }
    ],
    // Additional data for tier calculations
    tierThresholds: {
      'Bronze': { min: 0, max: 10000 },
      'Silver': { min: 10000, max: 25000 },
      'Gold': { min: 25000, max: 50000 },
      'Platinum': { min: 50000, max: 100000 }
    }
  };

  useEffect(() => {
    // In real app, fetch loyalty data from API
    const fetchLoyaltyData = async () => {
      setIsLoading(true);
      try {
        // Simulate API call
        // const response = await fetch('/api/user/loyalty');
        // const data = await response.json();
        
        // For now, use mock data
        setTimeout(() => {
          setLoyaltyData(mockLoyaltyData);
          setIsLoading(false);
        }, 500);
      } catch (error) {
        console.error('Error fetching loyalty data:', error);
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
        
        <div style={historyContainerStyle}>
          <EarningHistoryTable earningHistory={loyaltyData?.earningHistory} />
        </div>
      </div>
    </Layout>
  );
}