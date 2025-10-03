import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";

export default function Dashboard() {
  const [user, setUser] = useState(null);
  const [flights, setFlights] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Simulate user and flight data loading
    setTimeout(() => {
      setUser({
        firstName: "John",
        lastName: "Doe",
        email: "john.doe@example.com"
      });
      
      setFlights([
        {
          id: 1,
          origin: "Belgrade",
          destination: "Paris",
          date: "2024-02-15",
          time: "14:30",
          status: "Confirmed"
        },
        {
          id: 2,
          origin: "Paris",
          destination: "Belgrade",
          date: "2024-02-22",
          time: "18:45",
          status: "Pending"
        }
      ]);
      
      setLoading(false);
    }, 1000);
  }, []);

  const handleLogout = () => {
    console.log("Logging out...");
    // TODO: Implement actual logout logic
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center" style={{ backgroundColor: "#EEF4FB" }}>
        <div style={{ color: "#738396", fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif" }}>
          Loading...
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen" style={{ backgroundColor: "#EEF4FB" }}>
      {/* Header */}
      <div 
        className="bg-white shadow-sm px-6 py-4"
        style={{ borderBottom: "1px solid #D9E1EA" }}
      >
        <div className="flex justify-between items-center max-w-6xl mx-auto">
          <h1 
            style={{
              fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
              fontSize: "24px",
              fontWeight: 700,
              color: "#2F3E4D"
            }}
          >
            Air Company Dashboard
          </h1>
          
          <div className="flex items-center gap-4">
            <span 
              style={{
                fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
                fontSize: "14px",
                color: "#738396"
              }}
            >
              Welcome, {user?.firstName}!
            </span>
            
            <button
              onClick={handleLogout}
              className="px-4 py-2 rounded-lg hover:opacity-90 transition-opacity"
              style={{
                backgroundColor: "#3F8EFC",
                fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
                fontSize: "12px",
                fontWeight: 600,
                color: "#FFFFFF",
                border: "none",
                cursor: "pointer"
              }}
            >
              Logout
            </button>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="max-w-6xl mx-auto p-6">
        {/* User Info Card */}
        <div 
          className="bg-white rounded-lg p-6 mb-6 shadow-sm"
          style={{ border: "1px solid #D9E1EA" }}
        >
          <h2 
            className="mb-4"
            style={{
              fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
              fontSize: "18px",
              fontWeight: 700,
              color: "#2F3E4D"
            }}
          >
            Your Profile
          </h2>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label 
                style={{
                  fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
                  fontSize: "12px",
                  fontWeight: 400,
                  color: "#738396"
                }}
              >
                First Name
              </label>
              <p 
                style={{
                  fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
                  fontSize: "14px",
                  color: "#2F3E4D",
                  marginTop: "4px"
                }}
              >
                {user?.firstName}
              </p>
            </div>
            
            <div>
              <label 
                style={{
                  fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
                  fontSize: "12px",
                  fontWeight: 400,
                  color: "#738396"
                }}
              >
                Last Name
              </label>
              <p 
                style={{
                  fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
                  fontSize: "14px",
                  color: "#2F3E4D",
                  marginTop: "4px"
                }}
              >
                {user?.lastName}
              </p>
            </div>
            
            <div className="md:col-span-2">
              <label 
                style={{
                  fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
                  fontSize: "12px",
                  fontWeight: 400,
                  color: "#738396"
                }}
              >
                Email
              </label>
              <p 
                style={{
                  fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
                  fontSize: "14px",
                  color: "#2F3E4D",
                  marginTop: "4px"
                }}
              >
                {user?.email}
              </p>
            </div>
          </div>
        </div>

        {/* Flights Section */}
        <div 
          className="bg-white rounded-lg p-6 shadow-sm"
          style={{ border: "1px solid #D9E1EA" }}
        >
          <div className="flex justify-between items-center mb-4">
            <h2 
              style={{
                fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
                fontSize: "18px",
                fontWeight: 700,
                color: "#2F3E4D"
              }}
            >
              Your Flights
            </h2>
            
            <Link
              to="/book-flight"
              className="px-4 py-2 rounded-lg hover:opacity-90 transition-opacity"
              style={{
                backgroundColor: "#3F8EFC",
                fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
                fontSize: "12px",
                fontWeight: 600,
                color: "#FFFFFF",
                textDecoration: "none"
              }}
            >
              Book New Flight
            </Link>
          </div>

          {flights.length === 0 ? (
            <p 
              style={{
                fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
                fontSize: "14px",
                color: "#738396",
                textAlign: "center",
                padding: "40px 0"
              }}
            >
              No flights booked yet.
            </p>
          ) : (
            <div className="space-y-4">
              {flights.map((flight) => (
                <div 
                  key={flight.id}
                  className="p-4 rounded-lg"
                  style={{ 
                    backgroundColor: "#F6F8FB",
                    border: "1px solid #D9E1EA"
                  }}
                >
                  <div className="flex justify-between items-start">
                    <div>
                      <h3 
                        style={{
                          fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
                          fontSize: "16px",
                          fontWeight: 600,
                          color: "#2F3E4D",
                          marginBottom: "8px"
                        }}
                      >
                        {flight.origin} → {flight.destination}
                      </h3>
                      
                      <div className="flex gap-6">
                        <div>
                          <span 
                            style={{
                              fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
                              fontSize: "12px",
                              color: "#738396"
                            }}
                          >
                            Date:
                          </span>
                          <p 
                            style={{
                              fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
                              fontSize: "14px",
                              color: "#2F3E4D"
                            }}
                          >
                            {flight.date}
                          </p>
                        </div>
                        
                        <div>
                          <span 
                            style={{
                              fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
                              fontSize: "12px",
                              color: "#738396"
                            }}
                          >
                            Time:
                          </span>
                          <p 
                            style={{
                              fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
                              fontSize: "14px",
                              color: "#2F3E4D"
                            }}
                          >
                            {flight.time}
                          </p>
                        </div>
                      </div>
                    </div>
                    
                    <span 
                      className="px-3 py-1 rounded-full text-xs"
                      style={{
                        backgroundColor: flight.status === "Confirmed" ? "#E8F5E8" : "#FFF3CD",
                        color: flight.status === "Confirmed" ? "#2E7D2E" : "#8B5A00",
                        fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
                        fontWeight: 600
                      }}
                    >
                      {flight.status}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}