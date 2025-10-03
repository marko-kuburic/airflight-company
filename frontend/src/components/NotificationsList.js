import React from 'react';
import { NotificationItem } from './NotificationItem';

export function NotificationsList({ notifications, onNotificationClick, onMarkAsRead }) {
  const containerStyle = {
    backgroundColor: 'white',
    border: '1px solid #e5e7eb',
    borderRadius: '8px',
    overflow: 'hidden',
    fontFamily: 'Inter, sans-serif'
  };

  const handleNotificationClick = (notification) => {
    // Mark as read if it's unread
    if (!notification.isRead && onMarkAsRead) {
      onMarkAsRead(notification.id);
    }
    
    // Handle notification action
    if (onNotificationClick) {
      onNotificationClick(notification);
    }
  };

  return (
    <div style={containerStyle}>
      {notifications && notifications.length > 0 ? (
        notifications.map((notification, index) => (
          <NotificationItem
            key={notification.id || index}
            type={notification.type}
            title={notification.title}
            message={notification.message}
            timestamp={notification.timestamp}
            isHighlighted={notification.isHighlighted}
            onClick={() => handleNotificationClick(notification)}
          />
        ))
      ) : (
        <div style={{
          padding: '48px 24px',
          textAlign: 'center',
          color: '#6b7280',
          fontSize: '14px'
        }}>
          No notifications
        </div>
      )}
    </div>
  );
}