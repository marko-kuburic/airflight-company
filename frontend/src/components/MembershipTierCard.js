import React from 'react';

export function MembershipTierCard({ tier, currentPoints, nextTierPoints }) {
  // Debug logging
  console.log('MembershipTierCard props:', { tier, currentPoints, nextTierPoints });
  
  const containerStyle = {
    backgroundColor: 'white',
    border: '1px solid #e5e7eb',
    borderRadius: '8px',
    padding: '24px',
    fontFamily: 'Inter, sans-serif'
  };

  // Helper functions for tier colors
  const getTierColor = () => {
    const tierColors = {
      'BRONZE': '#cd7f32',
      'SILVER': '#c0c0c0', 
      'GOLD': '#ffd700',
      'PLATINUM': '#e5e4e2',
      'DIAMOND': '#b9f2ff'
    };
    return tierColors[tier?.toUpperCase()] || '#f59e0b';
  };

  const getTierGradient = () => {
    const tierGradients = {
      'BRONZE': 'linear-gradient(135deg, #cd7f32, #8b4513)',
      'SILVER': 'linear-gradient(135deg, #c0c0c0, #808080)',
      'GOLD': 'linear-gradient(135deg, #ffd700, #ffb347)',
      'PLATINUM': 'linear-gradient(135deg, #e5e4e2, #a8a8a8)',
      'DIAMOND': 'linear-gradient(135deg, #b9f2ff, #00bfff)'
    };
    return tierGradients[tier?.toUpperCase()] || 'linear-gradient(90deg, #f59e0b, #d97706)';
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
    color: getTierColor(),
    marginBottom: '16px',
    textShadow: '0 1px 2px rgba(0,0,0,0.1)'
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
    height: '12px', // Made slightly taller
    backgroundColor: '#f3f4f6',
    borderRadius: '6px',
    overflow: 'hidden',
    border: '1px solid #e5e7eb' // Added border for visibility
  };

  const progressBarStyle = {
    height: '100%',
    background: getTierGradient(),
    borderRadius: '6px',
    transition: 'width 0.8s ease-in-out',
    boxShadow: `0 2px 4px ${getTierColor()}33`
  };

  const progressTextStyle = {
    fontSize: '12px',
    color: '#6b7280',
    marginTop: '4px'
  };

  // Helper functions for tier management
  const getNextTierName = () => {
    const tierNames = ['BRONZE', 'SILVER', 'GOLD', 'PLATINUM', 'DIAMOND'];
    const currentIndex = tierNames.indexOf(tier?.toUpperCase());
    if (currentIndex >= 0 && currentIndex < tierNames.length - 1) {
      return tierNames[currentIndex + 1];
    }
    return 'DIAMOND'; // Already at highest tier
  };

  const getPointsToNextTier = () => {
    if (!nextTierPoints || !currentPoints) return 0;
    
    const nextTier = getNextTierName();
    const nextTierData = nextTierPoints[nextTier];
    
    if (!nextTierData) return 0;
    
    const pointsNeeded = nextTierData.min - currentPoints;
    return Math.max(pointsNeeded, 0);
  };

  // Calculate progress percentage
  const calculateProgress = () => {
    if (!currentPoints || !nextTierPoints) return 0;
    
    // Use the tier thresholds from the API response
    const tierNames = ['BRONZE', 'SILVER', 'GOLD', 'PLATINUM', 'DIAMOND'];
    const currentTierIndex = tierNames.indexOf(tier?.toUpperCase());
    
    if (currentTierIndex === -1) return 0;
    
    // Get current tier thresholds from API
    const currentTierData = nextTierPoints?.[tier?.toUpperCase()];
    if (!currentTierData) return 0;
    
    const tierMin = currentTierData.min;
    const tierMax = currentTierData.max;
    
    // Calculate progress within current tier
    const progressInTier = currentPoints - tierMin;
    const tierRange = tierMax - tierMin;
    
    if (tierRange <= 0) return 100; // At max tier
    
    return Math.min(Math.max((progressInTier / tierRange) * 100, 0), 100);
  };

  const progressPercentage = calculateProgress();
  
  // Debug logging for progress calculation
  console.log('Progress calculation:', {
    tier,
    currentPoints,
    nextTierPoints,
    progressPercentage,
    nextTier: getNextTierName(),
    pointsToNext: getPointsToNextTier()
  });

  return (
    <div style={containerStyle}>
      <div style={titleStyle}>Membership Tier</div>
      <div style={tierStyle}>{tier || 'SILVER'}</div>
      
      <div style={progressContainerStyle}>
        <div style={{...progressLabelStyle, display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
          <span>Progress to {getNextTierName()}</span>
          <span style={{fontWeight: '600', color: getTierColor()}}>{Math.round(progressPercentage || 0)}%</span>
        </div>
        
        <div style={progressBarContainerStyle}>
          <div 
            style={{
              ...progressBarStyle,
              width: `${Math.max(progressPercentage || 0, 5)}%` // Minimum 5% to always show something
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