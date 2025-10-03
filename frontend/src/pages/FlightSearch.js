import { useState } from "react";
import { SearchBar } from "../components/SearchBar";
import { FlightRow } from "../components/FlightRow";
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

  return (
    <div className="min-h-screen" style={{ backgroundColor: "#EEF4FB" }}>
      <div className="container mx-auto px-4 py-8">
        <h1 
          className="text-2xl font-bold mb-6 text-center"
          style={{
            fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
            color: "#2F3E4D"
          }}
        >
          Search Flights
        </h1>
        
        {/* SearchBar komponenta */}
        <div className="max-w-4xl mx-auto">
          <SearchBar onSearchResults={handleSearchResults} />
        </div>
        
        {/* Flight search results */}
        <div className="max-w-4xl mx-auto">
          {isSearched ? (
            searchResults.length > 0 ? (
              <div className="bg-white rounded-xl" style={{ border: "1px solid #D9E1EA" }}>
                <div className="p-4 border-b" style={{ borderColor: "#D9E1EA" }}>
                  <h2 className="text-lg font-semibold" style={{ fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif", color: "#2F3E4D" }}>
                    Available Flights ({searchResults.length})
                  </h2>
                </div>
                <div>
                  {searchResults.map((flight, index) => (
                    <div key={flight.id || index} className="cursor-pointer hover:bg-gray-50" onClick={() => handleFlightSelect(flight)}>
                      <FlightRow
                        flightNumber={flight.flightNumber}
                        route={`${flight.originAirport} → ${flight.destinationAirport}`}
                        departure={flight.departureTime}
                        arrival={flight.arrivalTime}
                        flightClass={flight.cabinClass || "Economy"}
                        price={`€${flight.price}`}
                        offerTime={flight.offerValidUntil || "24h"}
                      />
                    </div>
                  ))}
                </div>
              </div>
            ) : (
              <div 
                className="bg-white rounded-xl p-6 text-center"
                style={{ border: "1px solid #D9E1EA" }}
              >
                <p 
                  style={{
                    fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
                    color: "#738396",
                    fontSize: "14px"
                  }}
                >
                  No flights found for your search criteria.
                </p>
              </div>
            )
          ) : (
            <div 
              className="bg-white rounded-xl p-6 text-center"
              style={{ border: "1px solid #D9E1EA" }}
            >
              <p 
                style={{
                  fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
                  color: "#738396",
                  fontSize: "14px"
                }}
              >
                Search results will appear here...
              </p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}