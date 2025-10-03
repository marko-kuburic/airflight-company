import { SearchBar } from "../components/SearchBar";

export default function FlightSearch() {
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
          <SearchBar />
        </div>
        
        {/* Placeholder za rezultate pretrage */}
        <div className="max-w-4xl mx-auto">
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
        </div>
      </div>
    </div>
  );
}