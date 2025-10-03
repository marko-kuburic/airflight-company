import React from 'react';

export function MembershipTierCard({ tier, currentPoints, nextTierPoints }) {
  const containerStyle = {
    backgroundColor: 'white',
    border: '1px solid #e5e7eb',
    borderRadius: '8px',
    padding: '24px',
    fontFamily: 'Inter, sans-serif'
  };

  const titleStyle = {
    fontSize: '16px',
    fontWeight: '500',
    color: '#6b7280',
    marginBottom: '8px'
  };

  const tierStyle = {
    fontSize: '24px',
    fontWeight: '700',
    color: '#f59e0b',
    marginBottom: '16px'
  };

  const progressContainerStyle = {
    marginBottom: '8px'
  };

  const progressLabelStyle = {
    fontSize: '14px',
    color: '#6b7280',
    marginBottom: '8px'
  };

  const progressBarContainerStyle = {
    width: '100%',
    height: '8px',
    backgroundColor: '#f3f4f6',
    borderRadius: '4px',
    overflow: 'hidden'
  };

  const progressBarStyle = {
    height: '100%',
    backgroundColor: '#f59e0b',
    borderRadius: '4px',
    transition: 'width 0.3s ease'
  };

  const progressTextStyle = {
    fontSize: '12px',
    color: '#6b7280',
    marginTop: '4px'
  };

  // Calculate progress percentage
  const calculateProgress = () => {
    if (!currentPoints || !nextTierPoints) return 65; // Default 65% as shown in image
    
    const tierThresholds = {
      'Bronze': { min: 0, max: 10000 },
      'Silver': { min: 10000, max: 25000 },
      'Gold': { min: 25000, max: 50000 },
      'Platinum': { min: 50000, max: 100000 }
    };

    const currentTier = tierThresholds[tier];
    if (!currentTier) return 0;

    const progressInTier = currentPoints - currentTier.min;
    const tierRange = currentTier.max - currentTier.min;
    
    return Math.min(Math.max((progressInTier / tierRange) * 100, 0), 100);
  };

  const progressPercentage = calculateProgress();

  const getNextTierName = () => {
    const tiers = ['Bronze', 'Silver', 'Gold', 'Platinum'];
    const currentIndex = tiers.indexOf(tier);
    if (currentIndex >= 0 && currentIndex < tiers.length - 1) {
      return tiers[currentIndex + 1];
    }
    return 'Platinum'; // Already at highest tier
  };

  const getPointsToNextTier = () => {
    const tierThresholds = {
      'Bronze': 10000,
      'Silver': 25000,
      'Gold': 50000,
      'Platinum': 100000
    };

    const nextTier = getNextTierName();
    if (nextTier === 'Platinum' && tier === 'Platinum') {
      return 0; // Already at highest tier
    }

    return tierThresholds[nextTier] - (currentPoints || 18450);
  };

  return (
    <div style={containerStyle}>
      <div style={titleStyle}>Membership Tier</div>
      <div style={tierStyle}>{tier || 'Gold'}</div>
      
      <div style={progressContainerStyle}>
        <div style={progressLabelStyle}>
          Progress to {getNextTierName()}
        </div>
        
        <div style={progressBarContainerStyle}>
          <div 
            style={{
              ...progressBarStyle,
              width: `${progressPercentage}%`
            }}
          />
        </div>
        
        <div style={progressTextStyle}>
          {getPointsToNextTier() > 0 
            ? `${getPointsToNextTier().toLocaleString()} points to ${getNextTierName()}`
            : `Congratulations! You've reached ${tier} tier`
          }
        </div>
      </div>
    </div>
  );
}