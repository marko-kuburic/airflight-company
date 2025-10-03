import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Layout } from '../components/Layout';
import { TicketEditForm } from '../components/TicketEditForm';

export default function EditTicket() {
  const { ticketId } = useParams();
  const navigate = useNavigate();
  const [ticketData, setTicketData] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [isSaving, setIsSaving] = useState(false);

  // Mock ticket data - in real app this would come from API
  const mockTicketData = {
    ticketNumber: 'TCK-10231',
    flightNumber: 'FD-801',
    route: 'BEG → CDG',
    departure: '08:10',
    arrival: '10:35',
    aircraft: 'A320-11',
    status: 'Confirmed',
    class: 'Economy',
    baggage: 'Included (1 carry-on + 1 checked)',
    extraBaggage: 'None',
    seat: 'Auto-assigned',
    contactEmail: 'ana@example.com',
    passengerName: 'Ana Petrović',
    bookingReference: 'ABC123'
  };

  useEffect(() => {
    // In real app, fetch ticket data from API
    const fetchTicketData = async () => {
      setIsLoading(true);
      try {
        // Simulate API call
        // const response = await fetch(`/api/tickets/${ticketId}`);
        // const data = await response.json();
        
        // For now, use mock data
        setTimeout(() => {
          setTicketData(mockTicketData);
          setIsLoading(false);
        }, 500);
      } catch (error) {
        console.error('Error fetching ticket data:', error);
        setIsLoading(false);
      }
    };

    if (ticketId) {
      fetchTicketData();
    } else {
      // If no ticketId in URL, use mock data directly
      setTicketData(mockTicketData);
    }
  }, [ticketId]);

  const handleSave = async (formData) => {
    setIsSaving(true);
    try {
      // In real app, send update to API
      // const response = await fetch(`/api/tickets/${ticketId}`, {
      //   method: 'PUT',
      //   headers: {
      //     'Content-Type': 'application/json',
      //   },
      //   body: JSON.stringify({
      //     ...formData,
      //     ticketId: ticketData.ticketNumber
      //   })
      // });
      
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      console.log('Ticket updated:', {
        ticketId: ticketData.ticketNumber,
        ...formData
      });
      
      // Show success message
      alert('Ticket updated successfully! Confirmation email sent.');
      
      // Navigate back to My Tickets
      navigate('/tickets');
      
    } catch (error) {
      console.error('Error updating ticket:', error);
      alert('Error updating ticket. Please try again.');
    } finally {
      setIsSaving(false);
    }
  };

  const handleBack = () => {
    navigate('/tickets');
  };

  if (isLoading) {
    return (
      <Layout>
        <div style={{
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          height: '400px',
          fontSize: '16px',
          color: '#6b7280'
        }}>
          Loading ticket details...
        </div>
      </Layout>
    );
  }

  if (!ticketData) {
    return (
      <Layout>
        <div style={{
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          height: '400px',
          fontSize: '16px',
          color: '#ef4444'
        }}>
          Ticket not found
        </div>
      </Layout>
    );
  }

  return (
    <Layout>
      <TicketEditForm
        ticketData={ticketData}
        onSave={handleSave}
        onBack={handleBack}
        isLoading={isSaving}
      />
    </Layout>
  );
}