import React from 'react';

export function NotificationItem({ 
  type, 
  title, 
  message, 
  timestamp, 
  isHighlighted = false,
  onClick 
}) {
  const getNotificationIcon = (type) => {
    const iconStyle = {
      width: '8px',
      height: '8px',
      borderRadius: '50%',
      marginRight: '12px',
      flexShrink: 0,
      marginTop: '4px'
    };

    switch (type) {
      case 'ticket':
        return <div style={{ ...iconStyle, backgroundColor: '#2563eb' }} />;
      case 'points':
        return <div style={{ ...iconStyle, backgroundColor: '#10b981' }} />;
      case 'offer':
        return <div style={{ ...iconStyle, backgroundColor: '#f59e0b' }} />;
      case 'membership':
        return <div style={{ ...iconStyle, backgroundColor: '#8b5cf6' }} />;
      default:
        return <div style={{ ...iconStyle, backgroundColor: '#6b7280' }} />;
    }
  };

  const containerStyle = {
    display: 'flex',
    alignItems: 'flex-start',
    padding: '16px',
    backgroundColor: isHighlighted ? '#eff6ff' : 'white',
    border: isHighlighted ? '1px solid #2563eb' : '1px solid #f3f4f6',
    borderRadius: '8px',
    marginBottom: '1px',
    cursor: onClick ? 'pointer' : 'default',
    transition: 'all 0.2s ease',
    fontFamily: 'Inter, sans-serif'
  };

  const contentStyle = {
    flex: 1,
    minWidth: 0
  };

  const headerStyle = {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
    marginBottom: '4px'
  };

  const titleStyle = {
    fontSize: '14px',
    fontWeight: '600',
    color: '#1f2937',
    margin: 0
  };

  const timestampStyle = {
    fontSize: '12px',
    color: '#9ca3af',
    flexShrink: 0,
    marginLeft: '12px'
  };

  const messageStyle = {
    fontSize: '14px',
    color: '#6b7280',
    lineHeight: '1.4',
    margin: 0
  };

  const handleClick = () => {
    if (onClick) {
      onClick();
    }
  };

  return (
    <div 
      style={containerStyle}
      onClick={handleClick}
      onMouseEnter={(e) => {
        if (onClick) {
          e.target.style.backgroundColor = isHighlighted ? '#dbeafe' : '#f9fafb';
        }
      }}
      onMouseLeave={(e) => {
        e.target.style.backgroundColor = isHighlighted ? '#eff6ff' : 'white';
      }}
    >
      {getNotificationIcon(type)}
      <div style={contentStyle}>
        <div style={headerStyle}>
          <h4 style={titleStyle}>{title}</h4>
          <span style={timestampStyle}>{timestamp}</span>
        </div>
        <p style={messageStyle}>{message}</p>
      </div>
    </div>
  );
}