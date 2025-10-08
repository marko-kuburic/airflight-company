import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import './AdminLogin.css';

const AdminLogin = () => {
  const [credentials, setCredentials] = useState({
    username: '',
    password: ''
  });
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  // Hardcoded admin credentials (in production, this should be in backend)
  const ADMIN_USERNAME = 'admin';
  const ADMIN_PASSWORD = 'admin123';

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    // Simulate authentication delay
    setTimeout(() => {
      if (credentials.username === ADMIN_USERNAME && credentials.password === ADMIN_PASSWORD) {
        // Store admin authentication
        localStorage.setItem('adminAuth', 'true');
        localStorage.setItem('adminUsername', credentials.username);
        toast.success('Admin login successful!');
        navigate('/customer-management');
      } else {
        toast.error('Invalid admin credentials');
      }
      setLoading(false);
    }, 500);
  };

  const handleChange = (e) => {
    setCredentials({
      ...credentials,
      [e.target.name]: e.target.value
    });
  };

  return (
    <div className="admin-login-container">
      <div className="admin-login-card">
        <div className="admin-login-header">
          <div className="admin-icon">🔐</div>
          <h1>Customer Management</h1>
          <p>Admin Access Only</p>
        </div>

        <form onSubmit={handleSubmit} className="admin-login-form">
          <div className="form-group">
            <label htmlFor="username">Username</label>
            <input
              type="text"
              id="username"
              name="username"
              value={credentials.username}
              onChange={handleChange}
              placeholder="Enter admin username"
              required
              autoComplete="username"
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              type="password"
              id="password"
              name="password"
              value={credentials.password}
              onChange={handleChange}
              placeholder="Enter admin password"
              required
              autoComplete="current-password"
            />
          </div>

          <button 
            type="submit" 
            className="admin-login-btn"
            disabled={loading}
          >
            {loading ? 'Authenticating...' : 'Login'}
          </button>
        </form>

        <div className="admin-login-footer">
          <button 
            onClick={() => navigate('/search')}
            className="back-to-app-btn"
          >
            ← Back to Application
          </button>
        </div>
      </div>
    </div>
  );
};

export default AdminLogin;
