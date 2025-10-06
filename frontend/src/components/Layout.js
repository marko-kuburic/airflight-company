import React from 'react';
import { Sidebar } from './Sidebar';

export function Layout({ children }) {
  return (
    <div style={{
      display: 'flex',
      height: '100vh',
      fontFamily: 'Inter, sans-serif',
      overflow: 'hidden'
    }}>
      <Sidebar />
      <main style={{
        flex: 1,
        backgroundColor: '#f8fafc',
        overflow: 'auto',
        height: '100vh'
      }}>
        <div style={{ 
          width: '100%', 
          margin: '0', 
          padding: '0' 
        }}>
          {children}
        </div>
      </main>
    </div>
  );
}