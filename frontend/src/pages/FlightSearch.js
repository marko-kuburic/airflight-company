import { useState } from "react";
import { SearchBar } from "../components/SearchBar";
import { FlightCard } from "../components/FlightCard";
import { Sidebar } from "../components/Sidebar";
import { useNavigate } from "react-router-dom";

export default function FlightSearch() {
  const [searchResults, setSearchResults] = useState([]);
  const [isSearched, setIsSearched] = useState(false);
  const navigate = useNavigate();

  const handleSearchResults = (results) => {
    setSearchResults(results);
    setIsSearched(true);
  };

  const handleFlightSelect = (flight) => {
    // Navigate to booking page with flight data
    navigate('/booking', { state: { selectedFlight: flight } });
  };

  const handleFlightUpdate = (updatedFlight) => {
    // Update the specific flight in search results
    setSearchResults(prevResults => 
      prevResults.map(flight => 
        flight.flightId === updatedFlight.flightId ? updatedFlight : flight
      )
    );
  };

  return (
    <div style={{ backgroundColor: "#EEF4FB", minHeight: "100vh", position: "relative" }}>
      {/* Sidebar */}
      <Sidebar />
      
      {/* Main content - absolute positioning to avoid sidebar issues */}
      <div style={{
        position: 'absolute',
        top: '0px',
        left: '280px',
        right: '0px',
        padding: '20px'
      }}>
        <h1 
          style={{
            fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
            color: "#2F3E4D",
            fontSize: "28px",
            fontWeight: "bold",
            marginBottom: "15px",
            marginTop: "0px",
            paddingTop: "30px",
            textAlign: "center"
          }}
        >
          Search Flights
        </h1>
        
        {/* SearchBar komponenta - made bigger */}
        <div style={{
          width: '100%',
          maxWidth: '1200px',
          margin: '0 auto',
          marginBottom: '20px'
        }}>
          <SearchBar onSearchResults={handleSearchResults} />
        </div>
        
        {/* Flight search results */}
        <div style={{
          width: '100%',
          maxWidth: '1200px',
          margin: '0 auto'
        }}>
          {isSearched ? (
            searchResults.length > 0 ? (
              <div style={{ 
                backgroundColor: 'white', 
                borderRadius: '12px',
                border: "1px solid #D9E1EA",
                overflow: 'hidden'
              }}>
                <div style={{ 
                  padding: '20px', 
                  borderBottom: "1px solid #D9E1EA" 
                }}>
                  <h2 style={{ 
                    fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif", 
                    color: "#2F3E4D",
                    fontSize: "18px",
                    fontWeight: "600",
                    margin: 0
                  }}>
                    Available Flights ({searchResults.length})
                  </h2>
                </div>
                <div>
                  {searchResults.map((flight, index) => (
                    <FlightCard
                      key={flight.id || index}
                      flight={flight}
                      onFlightSelect={handleFlightSelect}
                      onFlightUpdate={handleFlightUpdate}
                    />
                  ))}
                </div>
              </div>
            ) : (
              <div style={{
                backgroundColor: 'white',
                borderRadius: '12px',
                padding: '40px',
                textAlign: 'center',
                border: "1px solid #D9E1EA"
              }}>
                <p style={{
                  fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
                  color: "#738396",
                  fontSize: "16px",
                  margin: 0
                }}>
                  No flights found for your search criteria.
                </p>
              </div>
            )
          ) : (
            <div style={{
              backgroundColor: 'white',
              borderRadius: '12px',
              padding: '40px',
              textAlign: 'center',
              border: "1px solid #D9E1EA"
            }}>
              <p style={{
                fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
                color: "#738396",
                fontSize: "16px",
                margin: 0
              }}>
                Search results will appear here...
              </p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}