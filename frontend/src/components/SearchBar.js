import { useState } from "react";
import { flightAPI } from "../services/api";
import toast from "react-hot-toast";
import { useNavigate } from "react-router-dom";
import { SearchDropdown } from "./SearchDropdown";

export function SearchBar({ onSearchResults }) {
  const [from, setFrom] = useState("Belgrade (BEG)");
  const [to, setTo] = useState("Paris (CDG)");
  const [date, setDate] = useState("2025-09-10");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSearch = async () => {
    try {
      setLoading(true);
      
      // Extract airport codes from the input
      const originCode = from.match(/\(([^)]+)\)/)?.[1] || from;
      const destinationCode = to.match(/\(([^)]+)\)/)?.[1] || to;
      
      const searchParams = {
        origin: originCode,
        destination: destinationCode,
        departureDate: date,
        passengers: 1
      };
      
      console.log("Searching flights:", searchParams);
      const response = await flightAPI.searchFlights(searchParams);
      
      if (onSearchResults) {
        onSearchResults(response.data);
      }
      
      toast.success(`Found ${response.data.length} flights`);
    } catch (error) {
      console.error("Flight search error:", error);
      toast.error("Error searching flights. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div 
      className="bg-white rounded-xl p-5 mb-5"
      style={{ border: "1px solid #D9E1EA" }}
    >
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-[1fr_1fr_1fr_auto] gap-4 items-end">
        {/* From Field */}
        <SearchDropdown
          label="From"
          value={from}
          onChange={setFrom}
          placeholder="City, airport, country or code..."
        />

        {/* To Field */}
        <SearchDropdown
          label="To"
          value={to}
          onChange={setTo}
          placeholder="City, airport, country or code..."
        />

        {/* Date Field */}
        <div className="flex flex-col gap-2">
          <label 
            className="text-xs"
            style={{
              fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
              color: "#738396",
              fontWeight: 400
            }}
          >
            Date
          </label>
          <input
            type="date"
            value={date}
            onChange={(e) => setDate(e.target.value)}
            className="h-7 px-2.5 text-xs rounded-md outline-none"
            style={{
              backgroundColor: "#F6F8FB",
              border: "1px solid #D9E1EA",
              fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
              color: "#6B7785"
            }}
          />
        </div>

        {/* Search Button */}
        <button 
          onClick={handleSearch}
          disabled={loading}
          className="h-8 px-6 text-white font-bold text-xs rounded-lg hover:opacity-90 transition-opacity sm:col-span-2 lg:col-span-1 disabled:opacity-50"
          style={{
            backgroundColor: "#3F8EFC",
            fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
            fontStyle: "italic",
            border: "none",
            cursor: loading ? "not-allowed" : "pointer"
          }}
        >
          {loading ? "Searching..." : "Search"}
        </button>
      </div>
    </div>
  );
}