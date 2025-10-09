import React from 'react';

export function DispatcherLayout({ children }) {
  return (
    <div style={{
      display: 'flex',
      height: '100vh',
      fontFamily: 'Inter, sans-serif',
      overflow: 'hidden'
    }}>
      {children}
    </div>
  );
}

