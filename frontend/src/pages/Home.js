import React from 'react';
import { Link } from 'react-router-dom';

export default function Home() {
  const containerStyle = {
    minHeight: '100vh',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#f8fafc',
    fontFamily: 'Inter, sans-serif'
  };

  const cardStyle = {
    backgroundColor: 'white',
    padding: '48px',
    borderRadius: '12px',
    boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1)',
    textAlign: 'center',
    maxWidth: '500px'
  };

  const titleStyle = {
    fontSize: '32px',
    fontWeight: '700',
    color: '#1f2937',
    marginBottom: '16px'
  };

  const subtitleStyle = {
    fontSize: '16px',
    color: '#6b7280',
    marginBottom: '32px'
  };

  const linkStyle = {
    display: 'inline-block',
    backgroundColor: '#2563eb',
    color: 'white',
    padding: '12px 24px',
    borderRadius: '8px',
    textDecoration: 'none',
    fontWeight: '600',
    margin: '0 8px',
    transition: 'background-color 0.2s ease'
  };

  return (
    <div style={containerStyle}>
      <div style={cardStyle}>
        <h1 style={titleStyle}>AirFlight Company</h1>
        <p style={subtitleStyle}>Welcome to your flight booking system</p>
        <div>
          <Link to="/login" style={linkStyle}>Login</Link>
          <Link to="/register" style={linkStyle}>Register</Link>
          <Link to="/search" style={linkStyle}>Search Flights</Link>
          <Link to="/booking" style={linkStyle}>View Booking Page</Link>
        </div>
      </div>
    </div>
  );
}