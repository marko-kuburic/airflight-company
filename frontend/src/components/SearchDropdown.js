import { useState, useEffect, useRef } from "react";
import { flightAPI } from "../services/api";

export function SearchDropdown({ 
  label, 
  value, 
  onChange, 
  placeholder = "Search for a city or airport...",
  className = "",
  isOrigin = false,
  isDestination = false,
  otherFieldValue = null
}) {
  const [isOpen, setIsOpen] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");
  const [airports, setAirports] = useState([]);
  const [filteredAirports, setFilteredAirports] = useState([]);
  const [anywhereOptions, setAnywhereOptions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [isFocused, setIsFocused] = useState(false);
  const [selectedIndex, setSelectedIndex] = useState(-1);
  const [showAnywhere, setShowAnywhere] = useState(false);
  
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

  // Load anywhere options when other field has a value
  useEffect(() => {
    const loadAnywhereOptions = async () => {
      if (!otherFieldValue) {
        setAnywhereOptions([]);
        setShowAnywhere(false);
        return;
      }

      try {
        const otherCode = otherFieldValue.match(/\(([^)]+)\)/)?.[1];
        if (!otherCode) return;

        let response;
        if (isOrigin) {
          // If this is origin field and destination is selected, get origins to that destination
          response = await flightAPI.getOriginsToDestination(otherCode);
        } else if (isDestination) {
          // If this is destination field and origin is selected, get destinations from that origin
          response = await flightAPI.getDestinationsFromOrigin(otherCode);
        }

        if (response && response.data) {
          setAnywhereOptions(response.data);
          setShowAnywhere(true);
        }
      } catch (error) {
        console.error("Error loading anywhere options:", error);
        setAnywhereOptions([]);
        setShowAnywhere(false);
      }
    };

    loadAnywhereOptions();
  }, [otherFieldValue, isOrigin, isDestination]);

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

    const anywhereCount = showAnywhere ? Math.min(anywhereOptions.length, 5) : 0;
    const regularCount = showAnywhere ? Math.min(filteredAirports.length, 3) : Math.min(filteredAirports.length, 8);
    const maxIndex = 1 + anywhereCount + regularCount - 1; // +1 for "Anywhere" option

    switch (e.key) {
      case 'ArrowDown':
        e.preventDefault();
        setSelectedIndex(prev => prev < maxIndex ? prev + 1 : prev);
        break;
      case 'ArrowUp':
        e.preventDefault();
        setSelectedIndex(prev => prev > 0 ? prev - 1 : 0); // Don't go below 0 (Anywhere option)
        break;
      case 'Enter':
        e.preventDefault();
        if (selectedIndex >= 0) {
          if (selectedIndex === 0) {
            // Selecting "Anywhere"
            handleAnywhereSelect();
          } else if (selectedIndex <= anywhereCount) {
            // Selecting from anywhere options
            const anywhereIndex = selectedIndex - 1;
            if (anywhereOptions[anywhereIndex]) {
              handleAirportSelect(anywhereOptions[anywhereIndex]);
            }
          } else {
            // Selecting from regular airports
            const regularIndex = selectedIndex - 1 - anywhereCount;
            if (filteredAirports[regularIndex]) {
              handleAirportSelect(filteredAirports[regularIndex]);
            }
          }
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
    const displayValue = airport.label || airport.name || airport; // Handle "Anywhere" case
    // Update local state immediately
    setSearchTerm(displayValue);
    setIsOpen(false);
    setIsFocused(false);
    // Notify parent component
    onChange(displayValue);
  };

  // Handle "Anywhere" selection
  const handleAnywhereSelect = () => {
    // Update local state immediately
    setSearchTerm("Anywhere");
    setIsOpen(false);
    setIsFocused(false);
    // Notify parent component
    onChange("Anywhere");
  };

  // Initialize display value when value prop changes
  useEffect(() => {
    if (value !== undefined && !isFocused) {
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
          value={isFocused ? searchTerm : (value || searchTerm || "")}
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
            ) : (
              <>
                {/* Always show "Anywhere" option at the top */}
                <div
                  className={`p-3 text-xs cursor-pointer border-b border-gray-100 transition-colors ${
                    selectedIndex === 0 
                      ? 'bg-blue-100 border-blue-200' 
                      : 'hover:bg-blue-50'
                  }`}
                  onClick={handleAnywhereSelect}
                  style={{ color: "#6B7785" }}
                >
                  <div className="flex justify-between items-start">
                    <div className="flex-1">
                      <div className="font-semibold text-gray-800 mb-1">
                        Anywhere
                        <span className={`ml-2 px-1.5 py-0.5 rounded text-xs font-mono ${
                          selectedIndex === 0 
                            ? 'bg-blue-600 text-white' 
                            : 'bg-blue-100 text-blue-800'
                        }`}>
                          ANY
                        </span>
                      </div>
                      <div className={`text-xs ${
                        selectedIndex === 0 ? 'text-gray-600' : 'text-gray-500'
                      }`}>
                        Search all destinations
                      </div>
                    </div>
                  </div>
                </div>

                {/* Show "Anywhere" options if available */}
                {showAnywhere && anywhereOptions.length > 0 && (
                  <>
                    <div className="px-3 py-2 bg-blue-50 border-b border-blue-200">
                      <div className="text-xs font-semibold text-blue-800">
                        {isOrigin ? "Anywhere to " : "Anywhere from "}
                        {otherFieldValue?.split('(')[0]?.trim()}
                      </div>
                    </div>
                    {anywhereOptions.slice(0, 5).map((airport, index) => {
                      const adjustedIndex = index + 1; // +1 because "Anywhere" is at index 0
                      return (
                        <div
                          key={`anywhere-${airport.code}`}
                          className={`p-3 text-xs cursor-pointer border-b border-gray-100 transition-colors ${
                            selectedIndex === adjustedIndex 
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
                                  selectedIndex === adjustedIndex 
                                    ? 'bg-blue-600 text-white' 
                                    : 'bg-blue-100 text-blue-800'
                                }`}>
                                  {airport.code}
                                </span>
                              </div>
                              <div className={`text-xs ${
                                selectedIndex === adjustedIndex ? 'text-gray-600' : 'text-gray-500'
                              }`}>
                                {airport.name}
                              </div>
                              <div className={`text-xs mt-0.5 ${
                                selectedIndex === adjustedIndex ? 'text-gray-500' : 'text-gray-400'
                              }`}>
                                {airport.country}
                              </div>
                            </div>
                          </div>
                        </div>
                      );
                    })}
                    {filteredAirports.length > 0 && (
                      <div className="px-3 py-2 bg-gray-50 border-b border-gray-200">
                        <div className="text-xs font-semibold text-gray-600">
                          All Airports
                        </div>
                      </div>
                    )}
                  </>
                )}
                
                {/* Regular filtered airports */}
                {filteredAirports.length > 0 && (
                  filteredAirports.slice(0, showAnywhere ? 3 : 8).map((airport, index) => {
                    const baseIndex = 1; // Start after "Anywhere" option
                    const anywhereCount = showAnywhere ? Math.min(anywhereOptions.length, 5) : 0;
                    const adjustedIndex = baseIndex + anywhereCount + index;
                    return (
                      <div
                        key={airport.code}
                        className={`p-3 text-xs cursor-pointer border-b border-gray-100 last:border-b-0 transition-colors ${
                          selectedIndex === adjustedIndex 
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
                                selectedIndex === adjustedIndex 
                                  ? 'bg-blue-600 text-white' 
                                  : 'bg-blue-100 text-blue-800'
                              }`}>
                                {airport.code}
                              </span>
                            </div>
                            <div className={`text-xs ${
                              selectedIndex === adjustedIndex ? 'text-gray-600' : 'text-gray-500'
                            }`}>
                              {airport.name}
                            </div>
                            <div className={`text-xs mt-0.5 ${
                              selectedIndex === adjustedIndex ? 'text-gray-500' : 'text-gray-400'
                            }`}>
                              {airport.country}
                            </div>
                          </div>
                        </div>
                      </div>
                    );
                  })
                )}
              </>
            )}
          </div>
        )}
      </div>
    </div>
  );
}