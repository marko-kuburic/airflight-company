import { Link, useLocation, useNavigate } from "react-router-dom";
import { useState } from "react";
import toast from 'react-hot-toast';

// Simple utility function to combine class names
const cn = (...classes) => {
  return classes.filter(Boolean).join(' ');
};

// Simple Menu and X icons as SVG components
const Menu = ({ size = 24 }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
    <line x1="4" x2="20" y1="6" y2="6"/>
    <line x1="4" x2="20" y1="12" y2="12"/>
    <line x1="4" x2="20" y1="18" y2="18"/>
  </svg>
);

const X = ({ size = 24 }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
    <path d="m18 6-12 12"/>
    <path d="m6 6 12 12"/>
  </svg>
);

const dispatcherNavItems = [
  { label: 'Flights', path: '/dispatcher/flights' },
  { label: 'Create Flight', path: '/dispatcher/create-flight' },
  { label: 'Aircrafts', path: '/dispatcher/aircrafts' },
  { label: 'Routes', path: '/dispatcher/routes' },
  { label: 'Add Route', path: '/dispatcher/routes/add' },
  { label: 'Maintenance', path: '/dispatcher/maintenance' },
  { label: 'Reports', path: '/dispatcher/reports' },
];

export function DispatcherSidebar() {
  const location = useLocation();
  const navigate = useNavigate();
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

  const handleLogout = () => {
    try {
      // Clear user data from localStorage
      localStorage.removeItem('authToken');
      localStorage.removeItem('user');
      
      // Show success message
      toast.success('Logged out successfully!');
      
      // Redirect to login page
      navigate('/login');
    } catch (error) {
      console.error('Logout error:', error);
      toast.error('Error during logout');
    }
  };

  const sidebarContent = (
    <>
      {/* Panel Title */}
      <div style={{
        padding: '20px',
        paddingBottom: '10px',
        borderBottom: '1px solid #2D3748'
      }}>
        <h1 style={{
          color: '#FFFFFF',
          fontSize: '18px',
          fontWeight: 'bold',
          margin: 0,
          fontFamily: 'Inter, sans-serif'
        }}>
          Flight Dispatcher Panel
        </h1>
      </div>

      <nav style={{
        display: 'flex',
        flexDirection: 'column',
        gap: '8px',
        padding: '20px',
        paddingTop: '20px'
      }}>
        {dispatcherNavItems.map((item, index) => {
          const isActive = location.pathname === item.path;
          
          let buttonStyle = {
            borderRadius: '8px',
            height: '40px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'flex-start',
            paddingLeft: '16px',
            fontSize: '14px',
            textDecoration: 'none',
            transition: 'all 0.2s ease',
            fontFamily: 'Inter, sans-serif',
            border: 'none',
            cursor: 'pointer',
            color: '#E2E8F0'
          };

          if (isActive) {
            buttonStyle = {
              ...buttonStyle,
              backgroundColor: '#3182CE',
              color: 'white',
              fontWeight: '500'
            };
          } else {
            buttonStyle = {
              ...buttonStyle,
              backgroundColor: 'transparent',
              color: '#E2E8F0'
            };
          }
          
          return (
            <Link
              key={item.path + index}
              to={item.path}
              onClick={() => setIsMobileMenuOpen(false)}
              style={buttonStyle}
            >
              {item.label}
            </Link>
          );
        })}
      </nav>
      
      <div style={{
        marginTop: 'auto',
        padding: '20px',
        paddingBottom: '20px'
      }}>
        <button style={{
          width: '100%',
          borderRadius: '8px',
          height: '40px',
          backgroundColor: '#E53E3E',
          color: 'white',
          fontWeight: '500',
          fontSize: '14px',
          transition: 'opacity 0.2s ease',
          border: 'none',
          cursor: 'pointer',
          fontFamily: 'Inter, sans-serif'
        }}
        onMouseEnter={(e) => e.target.style.opacity = '0.9'}
        onMouseLeave={(e) => e.target.style.opacity = '1'}
        onClick={handleLogout}
        >
          Logout
        </button>
      </div>
    </>
  );

  return (
    <>
      {/* Mobile menu button */}
      <button
        onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
        style={{
          display: window.innerWidth >= 1024 ? 'none' : 'block',
          position: 'fixed',
          top: '16px',
          left: '16px',
          zIndex: 50,
          padding: '8px',
          backgroundColor: '#1A202C',
          color: 'white',
          borderRadius: '8px',
          border: 'none',
          cursor: 'pointer'
        }}
      >
        {isMobileMenuOpen ? <X size={24} /> : <Menu size={24} />}
      </button>

      {/* Mobile overlay */}
      {isMobileMenuOpen && (
        <div
          style={{
            display: window.innerWidth >= 1024 ? 'none' : 'block',
            position: 'fixed',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            backgroundColor: 'rgba(0, 0, 0, 0.5)',
            zIndex: 30
          }}
          onClick={() => setIsMobileMenuOpen(false)}
        />
      )}

      {/* Sidebar */}
      <aside
        style={{
          width: '250px',
          height: '100vh',
          backgroundColor: '#1A202C',
          flexShrink: 0,
          display: 'flex',
          flexDirection: 'column',
          transition: 'transform 0.3s ease',
          zIndex: 40,
          position: window.innerWidth >= 1024 ? 'sticky' : 'fixed',
          top: 0,
          left: 0,
          transform: window.innerWidth >= 1024 ? 'translateX(0)' : 
                   (isMobileMenuOpen ? 'translateX(0)' : 'translateX(-100%)')
        }}
      >
        {sidebarContent}
      </aside>
    </>
  );
}

