import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Layout } from '../components/Layout';
import { NotificationsList } from '../components/NotificationsList';

export default function Notifications() {
  const [notifications, setNotifications] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  // Mock notifications data - in real app this would come from API
  const mockNotifications = [
    {
      id: 'notif-1',
      type: 'ticket',
      title: 'E-ticket issued',
      message: 'Ticket TCK-10218 confirmed and issued. Check My Tickets.',
      timestamp: '2025-08-28 12:18',
      isRead: false,
      isHighlighted: false,
      actionUrl: '/tickets'
    },
    {
      id: 'notif-2',
      type: 'points',
      title: 'Points credited',
      message: '+1,250 points for flight FD-815 (completed).',
      timestamp: '2025-08-29 09:00',
      isRead: false,
      isHighlighted: false,
      actionUrl: '/loyalty'
    },
    {
      id: 'notif-3',
      type: 'offer',
      title: 'Offer expires',
      message: 'Search offer BEG → CDG will expire in 10 minutes.',
      timestamp: '2025-09-05 10:03',
      isRead: false,
      isHighlighted: false,
      actionUrl: '/search'
    },
    {
      id: 'notif-4',
      type: 'membership',
      title: 'Membership update',
      message: 'Congratulations! You reached Gold tier.',
      timestamp: '2025-08-30 08:00',
      isRead: false,
      isHighlighted: true,
      actionUrl: '/loyalty'
    }
  ];

  useEffect(() => {
    // In real app, fetch notifications from API
    const fetchNotifications = async () => {
      setIsLoading(true);
      try {
        // Simulate API call
        // const response = await fetch('/api/user/notifications');
        // const data = await response.json();
        
        // For now, use mock data
        setTimeout(() => {
          setNotifications(mockNotifications);
          setIsLoading(false);
        }, 500);
      } catch (error) {
        console.error('Error fetching notifications:', error);
        setIsLoading(false);
      }
    };

    fetchNotifications();
  }, []);

  const handleNotificationClick = async (notification) => {
    try {
      // In real app, track notification interaction
      console.log('Notification clicked:', notification);
      
      // Navigate to relevant page based on notification type
      if (notification.actionUrl) {
        navigate(notification.actionUrl);
      }
    } catch (error) {
      console.error('Error handling notification click:', error);
    }
  };

  const handleMarkAsRead = async (notificationId) => {
    try {
      // In real app, mark notification as read via API
      // await fetch(`/api/notifications/${notificationId}/read`, {
      //   method: 'PUT'
      // });
      
      // Update local state
      setNotifications(prev => 
        prev.map(notif => 
          notif.id === notificationId 
            ? { ...notif, isRead: true }
            : notif
        )
      );
      
      console.log('Marked as read:', notificationId);
    } catch (error) {
      console.error('Error marking notification as read:', error);
    }
  };

  const handleMarkAllAsRead = async () => {
    try {
      // In real app, mark all notifications as read via API
      // await fetch('/api/notifications/mark-all-read', {
      //   method: 'PUT'
      // });
      
      // Update local state
      setNotifications(prev => 
        prev.map(notif => ({ ...notif, isRead: true }))
      );
      
      console.log('All notifications marked as read');
    } catch (error) {
      console.error('Error marking all notifications as read:', error);
    }
  };

  const pageStyle = {
    padding: '24px',
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
    color: '#1f2937',
    margin: 0
  };

  const markAllButtonStyle = {
    backgroundColor: 'white',
    color: '#2563eb',
    border: '1px solid #2563eb',
    borderRadius: '6px',
    padding: '8px 16px',
    fontSize: '14px',
    fontWeight: '500',
    cursor: 'pointer',
    fontFamily: 'Inter, sans-serif',
    transition: 'all 0.2s ease'
  };

  const unreadCount = notifications.filter(n => !n.isRead).length;

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
          Loading notifications...
        </div>
      </Layout>
    );
  }

  return (
    <Layout>
      <div style={pageStyle}>
        <div style={headerStyle}>
          <h1 style={titleStyle}>
            Notifications {unreadCount > 0 && <span style={{ color: '#ef4444' }}>({unreadCount})</span>}
          </h1>
          {unreadCount > 0 && (
            <button
              style={markAllButtonStyle}
              onClick={handleMarkAllAsRead}
              onMouseEnter={(e) => {
                e.target.style.backgroundColor = '#2563eb';
                e.target.style.color = 'white';
              }}
              onMouseLeave={(e) => {
                e.target.style.backgroundColor = 'white';
                e.target.style.color = '#2563eb';
              }}
            >
              Mark all as read
            </button>
          )}
        </div>
        
        <NotificationsList
          notifications={notifications}
          onNotificationClick={handleNotificationClick}
          onMarkAsRead={handleMarkAsRead}
        />
      </div>
    </Layout>
  );
}