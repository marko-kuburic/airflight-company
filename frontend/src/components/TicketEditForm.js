import React, { useState } from 'react';

export function TicketEditForm({ 
  ticketData, 
  onSave, 
  onBack,
  isLoading = false 
}) {
  const [formData, setFormData] = useState({
    class: ticketData?.class || 'Economy',
    baggage: ticketData?.baggage || 'Included (1 carry-on + 1 checked)',
    extraBaggage: ticketData?.extraBaggage || 'None',
    seat: ticketData?.seat || 'Auto-assigned',
    contactEmail: ticketData?.contactEmail || ''
  });

  const handleInputChange = (field, value) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const handleSave = () => {
    if (onSave) {
      onSave(formData);
    }
  };

  const containerStyle = {
    padding: '24px',
    maxWidth: '800px',
    margin: '0 auto',
    fontFamily: 'Inter, sans-serif'
  };

  const headerStyle = {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: '24px'
  };

  const titleStyle = {
    fontSize: '24px',
    fontWeight: '600',
    color: '#1f2937'
  };

  const ticketNumberStyle = {
    fontSize: '14px',
    color: '#6b7280'
  };

  const flightCardStyle = {
    backgroundColor: 'white',
    border: '1px solid #e5e7eb',
    borderRadius: '8px',
    padding: '20px',
    marginBottom: '24px',
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center'
  };

  const flightInfoStyle = {
    fontSize: '16px',
    fontWeight: '600',
    color: '#1f2937'
  };

  const flightLabelStyle = {
    fontSize: '14px',
    color: '#6b7280',
    marginBottom: '4px'
  };

  const statusBadgeStyle = {
    backgroundColor: '#d1fae5',
    color: '#065f46',
    padding: '6px 12px',
    borderRadius: '6px',
    fontSize: '12px',
    fontWeight: '500',
    border: '1px solid #10b981'
  };

  const formGridStyle = {
    display: 'grid',
    gridTemplateColumns: '1fr 1fr 1fr',
    gap: '20px',
    marginBottom: '24px'
  };

  const formGroupStyle = {
    display: 'flex',
    flexDirection: 'column'
  };

  const labelStyle = {
    fontSize: '14px',
    fontWeight: '500',
    color: '#374151',
    marginBottom: '8px'
  };

  const selectStyle = {
    height: '40px',
    padding: '0 12px',
    border: '1px solid #d1d5db',
    borderRadius: '6px',
    fontSize: '14px',
    fontFamily: 'Inter, sans-serif',
    backgroundColor: 'white',
    outline: 'none'
  };

  const inputStyle = {
    height: '40px',
    padding: '0 12px',
    border: '1px solid #d1d5db',
    borderRadius: '6px',
    fontSize: '14px',
    fontFamily: 'Inter, sans-serif',
    backgroundColor: '#f9fafb',
    outline: 'none',
    color: '#6b7280'
  };

  const noteStyle = {
    fontSize: '12px',
    color: '#6b7280',
    backgroundColor: '#f9fafb',
    padding: '12px',
    borderRadius: '6px',
    marginBottom: '24px',
    lineHeight: '1.5'
  };

  const buttonContainerStyle = {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginTop: '32px'
  };

  const backButtonStyle = {
    backgroundColor: 'white',
    border: '1px solid #d1d5db',
    borderRadius: '8px',
    padding: '12px 24px',
    fontSize: '14px',
    fontWeight: '500',
    color: '#374151',
    cursor: 'pointer',
    fontFamily: 'Inter, sans-serif',
    transition: 'background-color 0.2s ease'
  };

  const saveButtonStyle = {
    backgroundColor: '#10b981',
    color: 'white',
    border: 'none',
    borderRadius: '8px',
    padding: '12px 32px',
    fontSize: '14px',
    fontWeight: '600',
    cursor: 'pointer',
    fontFamily: 'Inter, sans-serif',
    transition: 'background-color 0.2s ease',
    opacity: isLoading ? 0.6 : 1
  };

  const footerNoteStyle = {
    fontSize: '12px',
    color: '#9ca3af',
    textAlign: 'center',
    marginTop: '24px',
    lineHeight: '1.5'
  };

  return (
    <div style={containerStyle}>
      <div style={headerStyle}>
        <h1 style={titleStyle}>Edit Ticket — Travel Details</h1>
        <span style={ticketNumberStyle}>Ticket: {ticketData?.ticketNumber || 'TCK-10231'}</span>
      </div>

      <div style={flightCardStyle}>
        <div>
          <div style={flightLabelStyle}>Flight</div>
          <div style={flightInfoStyle}>
            {ticketData?.flightNumber || 'FD-801'} • {ticketData?.route || 'BEG → CDG'} • {ticketData?.departure || '08:10'} / {ticketData?.arrival || '10:35'} • {ticketData?.aircraft || 'A320-11'}
          </div>
        </div>
        <div style={statusBadgeStyle}>
          Status: {ticketData?.status || 'Confirmed'}
        </div>
      </div>

      <div style={formGridStyle}>
        <div style={formGroupStyle}>
          <label style={labelStyle}>Class</label>
          <select
            value={formData.class}
            onChange={(e) => handleInputChange('class', e.target.value)}
            style={selectStyle}
          >
            <option value="Economy">Economy</option>
            <option value="Business">Business</option>
            <option value="First">First</option>
          </select>
        </div>

        <div style={formGroupStyle}>
          <label style={labelStyle}>Baggage</label>
          <select
            value={formData.baggage}
            onChange={(e) => handleInputChange('baggage', e.target.value)}
            style={selectStyle}
          >
            <option value="Included (1 carry-on + 1 checked)">Included (1 carry-on + 1 checked)</option>
            <option value="Carry-on only">Carry-on only</option>
            <option value="No baggage">No baggage</option>
          </select>
        </div>

        <div style={formGroupStyle}>
          <label style={labelStyle}>Extra baggage</label>
          <select
            value={formData.extraBaggage}
            onChange={(e) => handleInputChange('extraBaggage', e.target.value)}
            style={selectStyle}
          >
            <option value="None">None</option>
            <option value="1 extra bag">1 extra bag</option>
            <option value="2 extra bags">2 extra bags</option>
          </select>
        </div>
      </div>

      <div style={formGridStyle}>
        <div style={formGroupStyle}>
          <label style={labelStyle}>Seat</label>
          <select
            value={formData.seat}
            onChange={(e) => handleInputChange('seat', e.target.value)}
            style={selectStyle}
          >
            <option value="Auto-assigned">Auto-assigned</option>
            <option value="Window seat">Window seat</option>
            <option value="Aisle seat">Aisle seat</option>
            <option value="Specific seat">Specific seat</option>
          </select>
        </div>

        <div style={formGroupStyle}>
          <label style={labelStyle}>Contact email (readonly)</label>
          <input
            type="email"
            value={formData.contactEmail}
            readOnly
            style={inputStyle}
            placeholder="ana@example.com"
          />
        </div>

        <div style={formGroupStyle}>
          {/* Empty column for grid alignment */}
        </div>
      </div>

      <div style={noteStyle}>
        Changes follow fare rules. Upgrades / baggage may adjust price; refunds per tariff policy.
      </div>

      <div style={buttonContainerStyle}>
        <button 
          style={backButtonStyle}
          onClick={onBack}
          onMouseEnter={(e) => e.target.style.backgroundColor = '#f9fafb'}
          onMouseLeave={(e) => e.target.style.backgroundColor = 'white'}
        >
          Back
        </button>
        <button 
          style={saveButtonStyle}
          onClick={handleSave}
          disabled={isLoading}
          onMouseEnter={(e) => !isLoading && (e.target.style.backgroundColor = '#059669')}
          onMouseLeave={(e) => !isLoading && (e.target.style.backgroundColor = '#10b981')}
        >
          {isLoading ? 'Saving...' : 'Save'}
        </button>
      </div>

      <div style={footerNoteStyle}>
        On save: ticket updated and confirmation email sent. Back returns to My Tickets.
      </div>
    </div>
  );
}