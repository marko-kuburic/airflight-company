import { Link, useLocation } from "react-router-dom";
import { useState } from "react";

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

const navItems = [
  { label: 'Customer Panel', path: '/', variant: 'primary' },
  { label: 'Search Flights', path: '/search' },
  { label: 'My Tickets', path: '/tickets' },
  { label: 'My Profile', path: '/profile' },
  { label: 'Loyalty Program', path: '/loyalty' },
  { label: 'Notifications', path: '/notifications' },
];

export function Sidebar() {
  const location = useLocation();
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

  const sidebarContent = (
    <>
      <nav style={{
        display: 'flex',
        flexDirection: 'column',
        gap: '10px',
        padding: '10px',
        paddingTop: '14px'
      }}>
        {navItems.map((item, index) => {
          const isActive = location.pathname === item.path;
          const isPrimary = item.variant === 'primary';
          
          let buttonStyle = {
            borderRadius: '12px',
            height: '44px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            textAlign: 'center',
            fontSize: '14px',
            textDecoration: 'none',
            transition: 'all 0.2s ease',
            fontFamily: 'Inter, sans-serif',
            border: 'none',
            cursor: 'pointer'
          };

          if (isPrimary) {
            buttonStyle = {
              ...buttonStyle,
              backgroundColor: '#2563eb',
              color: 'white',
              fontWeight: 'bold',
              borderRadius: '12px'
            };
          } else if (isActive) {
            buttonStyle = {
              ...buttonStyle,
              backgroundColor: '#2563eb',
              color: 'white',
              fontWeight: 'bold',
              textDecoration: 'underline',
              borderRadius: '10px'
            };
          } else {
            buttonStyle = {
              ...buttonStyle,
              backgroundColor: '#394B5C',
              color: 'white',
              textDecoration: 'underline',
              fontWeight: 'normal',
              borderRadius: '10px'
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
        padding: '10px',
        paddingBottom: '10px'
      }}>
        <button style={{
          width: '100%',
          borderRadius: '10px',
          height: '40px',
          backgroundColor: '#dc2626',
          color: 'white',
          fontWeight: 'bold',
          fontSize: '14px',
          textDecoration: 'underline',
          transition: 'opacity 0.2s ease',
          border: 'none',
          cursor: 'pointer',
          fontFamily: 'Inter, sans-serif'
        }}
        onMouseEnter={(e) => e.target.style.opacity = '0.9'}
        onMouseLeave={(e) => e.target.style.opacity = '1'}
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
          backgroundColor: '#1f2937',
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
          width: '220px',
          height: '100vh',
          backgroundColor: '#1f2937',
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