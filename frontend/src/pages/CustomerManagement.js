import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Analytics from './Analytics';
import toast from 'react-hot-toast';
import './CustomerManagement.css';

const CustomerManagement = () => {
  const navigate = useNavigate();

  useEffect(() => {
    // Check if admin is authenticated
    const isAdminAuth = localStorage.getItem('adminAuth');
    if (!isAdminAuth) {
      toast.error('Please login to access Customer Management');
      navigate('/admin-login');
    }
  }, [navigate]);

  const handleLogout = () => {
    localStorage.removeItem('adminAuth');
    localStorage.removeItem('adminUsername');
    toast.success('Logged out from admin panel');
    navigate('/admin-login');
  };

  const adminUsername = localStorage.getItem('adminUsername');

  return (
    <div className="customer-management-wrapper">
      <div className="admin-header">
        <div className="admin-header-left">
          <div className="admin-badge">🔐 Admin Panel</div>
          <span className="admin-username">Welcome, {adminUsername}</span>
        </div>
        <div className="admin-header-right">
          <button onClick={() => navigate('/search')} className="back-to-app-link">
            🏠 Back to App
          </button>
          <button onClick={handleLogout} className="admin-logout-btn">
            🚪 Logout
          </button>
        </div>
      </div>
      
      <div className="customer-management-content">
        <Analytics />
      </div>
    </div>
  );
};

export default CustomerManagement;
