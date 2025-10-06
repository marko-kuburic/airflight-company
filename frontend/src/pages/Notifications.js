import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Layout } from '../components/Layout';
import { NotificationsList } from '../components/NotificationsList';
import { authAPI } from '../services/api';

export default function Notifications() {
  const [notifications, setNotifications] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [unreadCount, setUnreadCount] = useState(0);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchNotifications = async () => {
      setIsLoading(true);
      try {
        // Get user data from localStorage
        const userData = localStorage.getItem('user');
        if (!userData) {
          console.error('No user data found');
          setIsLoading(false);
          return;
        }

        const user = JSON.parse(userData);
        const userId = user.id;

        const response = await authAPI.getUserNotifications(userId);
        const data = response.data;
        
        // Transform backend notifications to frontend format
        const transformedNotifications = data.notifications.map(notif => ({
          id: notif.id,
          type: getNotificationType(notif.type),
          title: getNotificationTitle(notif.type),
          message: notif.message,
          timestamp: formatTimestamp(notif.createdAt),
          isRead: notif.isRead,
          isHighlighted: false,
          actionUrl: getActionUrl(notif.type)
        }));
        
        setNotifications(transformedNotifications);
        setUnreadCount(data.unreadCount);
      } catch (error) {
        console.error('Error fetching notifications:', error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchNotifications();
  }, []);

  // Helper functions to transform notification data
  const getNotificationType = (backendType) => {
    switch (backendType) {
      case 'FLIGHT_UPDATE': return 'flight';
      case 'GENERAL': return 'points';
      default: return 'general';
    }
  };

  const getNotificationTitle = (backendType) => {
    switch (backendType) {
      case 'FLIGHT_UPDATE': return 'Flight Update';
      case 'GENERAL': return 'Notification';
      default: return 'Notification';
    }
  };

  const getActionUrl = (backendType) => {
    switch (backendType) {
      case 'FLIGHT_UPDATE': return '/tickets';
      case 'GENERAL': return '/loyalty';
      default: return null;
    }
  };

  const formatTimestamp = (timestamp) => {
    const date = new Date(timestamp);
    return date.toLocaleString('en-US', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

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
      // Get user data from localStorage
      const userData = localStorage.getItem('user');
      if (!userData) {
        console.error('No user data found');
        return;
      }

      const user = JSON.parse(userData);
      const userId = user.id;

      const response = await authAPI.markNotificationAsRead(userId, notificationId);
      const data = response.data;
      
      // Update local state
      setNotifications(prev => 
        prev.map(notif => 
          notif.id === notificationId 
            ? { ...notif, isRead: true }
            : notif
        )
      );
      
      // Update unread count
      setUnreadCount(data.unreadCount);
      
      console.log('Marked as read:', notificationId);
    } catch (error) {
      console.error('Error marking notification as read:', error);
    }
  };

  const handleMarkAllAsRead = async () => {
    try {
      // Get user data from localStorage
      const userData = localStorage.getItem('user');
      if (!userData) {
        console.error('No user data found');
        return;
      }

      const user = JSON.parse(userData);
      const userId = user.id;

      const response = await authAPI.markAllNotificationsAsRead(userId);
      const data = response.data;
      
      // Update local state
      setNotifications(prev => 
        prev.map(notif => ({ ...notif, isRead: true }))
      );
      
      // Set unread count to 0
      setUnreadCount(0);
      
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