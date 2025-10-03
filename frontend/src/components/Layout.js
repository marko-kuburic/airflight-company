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
        {children}
      </main>
    </div>
  );
}