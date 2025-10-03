import { useState, useEffect, useRef } from "react";
import { flightAPI } from "../services/api";

export function SearchDropdown({ 
  label, 
  value, 
  onChange, 
  placeholder = "Search for a city or airport...",
  className = "" 
}) {
  const [isOpen, setIsOpen] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");
  const [airports, setAirports] = useState([]);
  const [filteredAirports, setFilteredAirports] = useState([]);
  const [loading, setLoading] = useState(false);
  const [isFocused, setIsFocused] = useState(false);
  const [selectedIndex, setSelectedIndex] = useState(-1);
  
  const dropdownRef = useRef(null);
  const inputRef = useRef(null);

  // Load airports on component mount
  useEffect(() => {
    const loadAirports = async () => {
      try {
        setLoading(true);
        const response = await flightAPI.getAirports();
        setAirports(response.data);
        setFilteredAirports(response.data);
      } catch (error) {
        console.error("Error loading airports:", error);
        setAirports([]);
        setFilteredAirports([]);
      } finally {
        setLoading(false);
      }
    };

    loadAirports();
  }, []);

  // Enhanced filter airports based on search term - smart search like airline websites
  useEffect(() => {
    if (!searchTerm.trim()) {
      setFilteredAirports(airports);
    } else {
      const term = searchTerm.toLowerCase().trim();
      
      const filtered = airports.filter(airport => {
        // Search in multiple fields for comprehensive matching
        const searchableText = [
          airport.name,      // Airport name: "Nikola Tesla Airport"
          airport.city,      // City: "Belgrade"
          airport.code,      // IATA code: "BEG"
          airport.country,   // Country: "Serbia"
          airport.label      // Combined: "Belgrade (BEG)"
        ].join(' ').toLowerCase();
        
        // Split search term into words for better matching
        const searchWords = term.split(/\s+/);
        
        // Check if all search words match somewhere in the searchable text
        return searchWords.every(word => 
          searchableText.includes(word)
        );
      });
      
      // Sort results by relevance
      const sortedResults = filtered.sort((a, b) => {
        const aText = `${a.city} ${a.name} ${a.code} ${a.country}`.toLowerCase();
        const bText = `${b.city} ${b.name} ${b.code} ${b.country}`.toLowerCase();
        
        // Prioritize matches that start with the search term
        const aStartsWithCity = a.city.toLowerCase().startsWith(term);
        const bStartsWithCity = b.city.toLowerCase().startsWith(term);
        const aStartsWithCode = a.code.toLowerCase().startsWith(term);
        const bStartsWithCode = b.code.toLowerCase().startsWith(term);
        const aStartsWithCountry = a.country.toLowerCase().startsWith(term);
        const bStartsWithCountry = b.country.toLowerCase().startsWith(term);
        
        // Priority: Code match > City match > Country match > Other matches
        if (aStartsWithCode && !bStartsWithCode) return -1;
        if (bStartsWithCode && !aStartsWithCode) return 1;
        if (aStartsWithCity && !bStartsWithCity) return -1;
        if (bStartsWithCity && !aStartsWithCity) return 1;
        if (aStartsWithCountry && !bStartsWithCountry) return -1;
        if (bStartsWithCountry && !aStartsWithCountry) return 1;
        
        // Fallback to alphabetical by city
        return a.city.localeCompare(b.city);
      });
      
      setFilteredAirports(sortedResults);
    }
  }, [searchTerm, airports]);

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsOpen(false);
        setIsFocused(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  const handleInputChange = (e) => {
    const term = e.target.value;
    setSearchTerm(term);
    setIsOpen(true);
    setSelectedIndex(-1); // Reset selection when typing
    
    // If user types and then clears, reset to current value
    if (!term && value) {
      const currentAirport = airports.find(airport => 
        airport.label === value || airport.code === value
      );
      if (currentAirport) {
        setSearchTerm(currentAirport.label);
      }
    }
  };

  const handleKeyDown = (e) => {
    if (!isOpen) return;

    const maxIndex = Math.min(filteredAirports.length - 1, 7); // Limit to 8 items

    switch (e.key) {
      case 'ArrowDown':
        e.preventDefault();
        setSelectedIndex(prev => prev < maxIndex ? prev + 1 : prev);
        break;
      case 'ArrowUp':
        e.preventDefault();
        setSelectedIndex(prev => prev > 0 ? prev - 1 : -1);
        break;
      case 'Enter':
        e.preventDefault();
        if (selectedIndex >= 0 && filteredAirports[selectedIndex]) {
          handleAirportSelect(filteredAirports[selectedIndex]);
        }
        break;
      case 'Escape':
        setIsOpen(false);
        setIsFocused(false);
        inputRef.current?.blur();
        break;
    }
  };

  const handleInputFocus = () => {
    setIsFocused(true);
    setIsOpen(true);
    // Clear the search term to show placeholder when focused
    if (searchTerm === value || !searchTerm) {
      setSearchTerm("");
    }
  };

  const handleInputBlur = () => {
    // Delay to allow click on dropdown items
    setTimeout(() => {
      if (!dropdownRef.current?.contains(document.activeElement)) {
        setIsFocused(false);
        setIsOpen(false);
        // If no selection was made and input is empty, restore original value
        if (!searchTerm && value) {
          setSearchTerm(value);
        }
      }
    }, 150);
  };

  const handleAirportSelect = (airport) => {
    const displayValue = airport.label; // e.g., "Belgrade (BEG)"
    setSearchTerm(displayValue);
    onChange(displayValue);
    setIsOpen(false);
    setIsFocused(false);
  };

  // Initialize display value when value prop changes
  useEffect(() => {
    if (value && !isFocused) {
      setSearchTerm(value);
    }
  }, [value, isFocused]);

  return (
    <div className={`flex flex-col gap-2 ${className}`} ref={dropdownRef}>
      <label 
        className="text-xs"
        style={{
          fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
          color: "#738396",
          fontWeight: 400
        }}
      >
        {label}
      </label>
      
      <div className="relative">
        <input
          ref={inputRef}
          type="text"
          value={isFocused ? searchTerm : (searchTerm || "")}
          onChange={handleInputChange}
          onFocus={handleInputFocus}
          onBlur={handleInputBlur}
          onKeyDown={handleKeyDown}
          placeholder={isFocused ? placeholder : ""}
          className="h-7 px-2.5 text-xs rounded-md outline-none w-full"
          style={{
            backgroundColor: "#F6F8FB",
            border: "1px solid #D9E1EA",
            fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
            color: "#6B7785"
          }}
          autoComplete="off"
        />
        
        {isOpen && (
          <div 
            className="absolute top-full left-0 right-0 mt-1 bg-white border rounded-md shadow-lg z-50 max-h-60 overflow-y-auto"
            style={{
              border: "1px solid #D9E1EA",
              fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif"
            }}
          >
            {loading ? (
              <div className="p-3 text-xs text-gray-500 text-center">
                Loading airports...
              </div>
            ) : filteredAirports.length > 0 ? (
              filteredAirports.slice(0, 8).map((airport, index) => ( // Limit to 8 results for better UX
                <div
                  key={airport.code}
                  className={`p-3 text-xs cursor-pointer border-b border-gray-100 last:border-b-0 transition-colors ${
                    selectedIndex === index 
                      ? 'bg-blue-100 border-blue-200' 
                      : 'hover:bg-blue-50'
                  }`}
                  onClick={() => handleAirportSelect(airport)}
                  style={{ color: "#6B7785" }}
                >
                  <div className="flex justify-between items-start">
                    <div className="flex-1">
                      <div className="font-semibold text-gray-800 mb-1">
                        {airport.city} 
                        <span className={`ml-2 px-1.5 py-0.5 rounded text-xs font-mono ${
                          selectedIndex === index 
                            ? 'bg-blue-600 text-white' 
                            : 'bg-blue-100 text-blue-800'
                        }`}>
                          {airport.code}
                        </span>
                      </div>
                      <div className={`text-xs ${
                        selectedIndex === index ? 'text-gray-600' : 'text-gray-500'
                      }`}>
                        {airport.name}
                      </div>
                      <div className={`text-xs mt-0.5 ${
                        selectedIndex === index ? 'text-gray-500' : 'text-gray-400'
                      }`}>
                        {airport.country}
                      </div>
                    </div>
                  </div>
                </div>
              ))
            ) : (
              <div className="p-4 text-xs text-gray-500 text-center">
                <div className="mb-1">No airports found</div>
                <div className="text-xs text-gray-400">
                  Try searching by city, airport name, country, or IATA code
                </div>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}