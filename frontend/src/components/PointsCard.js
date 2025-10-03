import React from 'react';

export function PointsCard({ points }) {
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

  const pointsStyle = {
    fontSize: '32px',
    fontWeight: '700',
    color: '#2563eb',
    marginBottom: '8px'
  };

  const descriptionStyle = {
    fontSize: '14px',
    color: '#6b7280',
    lineHeight: '1.5'
  };

  return (
    <div style={containerStyle}>
      <div style={titleStyle}>Points / Miles</div>
      <div style={pointsStyle}>{points?.toLocaleString() || '18,450'}</div>
      <div style={descriptionStyle}>
        Points are credited after completed flights.
      </div>
    </div>
  );
}