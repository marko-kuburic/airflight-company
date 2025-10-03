import React from 'react';

export function EarningHistoryTable({ earningHistory }) {
  const containerStyle = {
    backgroundColor: 'white',
    border: '1px solid #e5e7eb',
    borderRadius: '8px',
    padding: '24px',
    fontFamily: 'Inter, sans-serif'
  };

  const titleStyle = {
    fontSize: '18px',
    fontWeight: '600',
    color: '#1f2937',
    marginBottom: '16px'
  };

  const tableStyle = {
    width: '100%',
    borderCollapse: 'collapse'
  };

  const headerRowStyle = {
    borderBottom: '1px solid #e5e7eb'
  };

  const headerCellStyle = {
    padding: '12px 0',
    fontSize: '14px',
    fontWeight: '500',
    color: '#6b7280',
    textAlign: 'left'
  };

  const rowStyle = {
    borderBottom: '1px solid #f3f4f6'
  };

  const cellStyle = {
    padding: '12px 0',
    fontSize: '14px',
    color: '#374151'
  };

  const pointsCellStyle = {
    ...cellStyle,
    color: '#2563eb',
    fontWeight: '500'
  };

  const statusCellStyle = {
    ...cellStyle,
    color: '#10b981',
    fontWeight: '500'
  };

  const noteStyle = {
    fontSize: '14px',
    color: '#6b7280',
    marginTop: '16px',
    lineHeight: '1.5'
  };

  // Default data if none provided
  const defaultHistory = [
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
  ];

  const history = earningHistory || defaultHistory;

  return (
    <div style={containerStyle}>
      <h3 style={titleStyle}>Earning history</h3>
      
      <table style={tableStyle}>
        <thead>
          <tr style={headerRowStyle}>
            <th style={headerCellStyle}>Date</th>
            <th style={headerCellStyle}>Flight</th>
            <th style={headerCellStyle}>Status</th>
            <th style={headerCellStyle}>Points</th>
          </tr>
        </thead>
        <tbody>
          {history.map((entry, index) => (
            <tr key={index} style={rowStyle}>
              <td style={cellStyle}>{entry.date}</td>
              <td style={cellStyle}>{entry.flight}</td>
              <td style={statusCellStyle}>{entry.status}</td>
              <td style={pointsCellStyle}>+{entry.points.toLocaleString()}</td>
            </tr>
          ))}
        </tbody>
      </table>
      
      <div style={noteStyle}>
        You can use points to fully or partially pay during checkout (per fare rules).
      </div>
    </div>
  );
}