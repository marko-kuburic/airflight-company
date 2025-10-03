import React from 'react';

// Simple utility function to combine class names
const cn = (...classes) => {
  return classes.filter(Boolean).join(' ');
};

export function FlightRow({
  flightNumber,
  route,
  departure,
  arrival,
  flightClass,
  price,
  offerTime,
  isUnderlined = false,
}) {
  const containerStyle = {
    backgroundColor: 'white',
    border: '1px solid #e5e7eb',
    borderRadius: '12px',
    padding: '17px 16px',
    fontFamily: 'Inter, sans-serif'
  };

  const flightNumberStyle = {
    fontSize: '13px',
    color: '#1f2937',
    fontWeight: '600',
    textDecoration: isUnderlined ? 'underline' : 'none'
  };

  const offerBadgeStyle = {
    height: '24px',
    padding: '0 12px',
    backgroundColor: '#fef3c7',
    border: '1px solid #f59e0b',
    borderRadius: '4px',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center'
  };

  const offerTextStyle = {
    fontSize: '12px',
    color: '#92400e'
  };

  const routeTextStyle = {
    fontSize: '13px',
    color: '#1f2937'
  };

  const selectStyle = {
    height: '24px',
    padding: '0 8px',
    fontSize: '12px',
    backgroundColor: '#f9fafb',
    border: '1px solid #e5e7eb',
    borderRadius: '4px',
    color: '#6b7280',
    fontFamily: 'Inter, sans-serif',
    outline: 'none'
  };

  const priceStyle = {
    fontSize: '13px',
    color: '#1f2937',
    fontWeight: '600'
  };

  const mobileLayoutStyle = {
    display: 'flex',
    flexDirection: 'column',
    gap: '12px'
  };

  const mobileRowStyle = {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center'
  };

  const mobileBottomRowStyle = {
    display: 'flex',
    alignItems: 'center',
    gap: '12px'
  };

  const mobileSelectStyle = {
    ...selectStyle,
    flex: 1
  };

  const desktopLayoutStyle = {
    display: 'none',
    gridTemplateColumns: '70px 100px 110px 110px 60px 104px',
    gap: '24px',
    alignItems: 'center'
  };

  // Check if screen is large (desktop)
  const isDesktop = typeof window !== 'undefined' && window.innerWidth >= 1024;

  return (
    <div style={containerStyle}>
      {/* Mobile layout */}
      <div style={{
        ...mobileLayoutStyle,
        display: isDesktop ? 'none' : 'flex'
      }}>
        <div style={mobileRowStyle}>
          <div style={flightNumberStyle}>
            {flightNumber}
          </div>
          <div style={offerBadgeStyle}>
            <span style={offerTextStyle}>{offerTime}</span>
          </div>
        </div>
        <div style={routeTextStyle}>
          {route} • {departure} / {arrival}
        </div>
        <div style={mobileBottomRowStyle}>
          <select style={mobileSelectStyle}>
            <option>{flightClass} ▾</option>
          </select>
          <div style={priceStyle}>
            {price}
          </div>
        </div>
      </div>

      {/* Desktop layout */}
      <div style={{
        ...desktopLayoutStyle,
        display: isDesktop ? 'grid' : 'none'
      }}>
        <div style={flightNumberStyle}>
          {flightNumber}
        </div>

        <div style={routeTextStyle}>
          {route}
        </div>

        <div style={routeTextStyle}>
          {departure} / {arrival}
        </div>

        <div>
          <select style={{
            ...selectStyle,
            width: '100%'
          }}>
            <option>{flightClass} ▾</option>
          </select>
        </div>

        <div style={{
          ...priceStyle,
          textAlign: 'left'
        }}>
          {price}
        </div>

        <div>
          <div style={offerBadgeStyle}>
            <span style={offerTextStyle}>{offerTime}</span>
          </div>
        </div>
      </div>
    </div>
  );
}