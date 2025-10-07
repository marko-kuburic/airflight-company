import React, { useState, useEffect } from "react";
import axios from 'axios';

// Simple utility function to combine class names
const cn = (...classes) => {
  return classes.filter(Boolean).join(' ');
};

// Initial seat layout (premium seats marked, but availability comes from backend)
const initialSeatLayout = {
  "1-A": "available", "1-B": "available", "1-C": "premium", "1-D": "available", "1-E": "available", "1-F": "available",
  "2-A": "available", "2-B": "available", "2-C": "available", "2-D": "premium", "2-E": "premium", "2-F": "available",
  "3-A": "available", "3-B": "premium", "3-C": "available", "3-D": "available", "3-E": "available", "3-F": "available",
  "4-A": "available", "4-B": "available", "4-C": "available", "4-D": "available", "4-E": "premium", "4-F": "available",
  "5-A": "available", "5-B": "available", "5-C": "available", "5-D": "available", "5-E": "available", "5-F": "available",
  "6-A": "available", "6-B": "premium", "6-C": "available", "6-D": "available", "6-E": "available", "6-F": "available",
  "R1-A": "premium", "R1-B": "available", "R1-C": "available",
  "R2-A": "available", "R2-B": "available", "R2-C": "premium",
  "R3-A": "available", "R3-B": "premium", "R3-C": "available",
  "R4-A": "available", "R4-B": "available", "R4-C": "available",
  "R5-A": "available", "R5-B": "available", "R5-C": "premium",
  "R6-A": "available", "R6-B": "available", "R6-C": "available",
};

export function SeatSelection({ onSeatSelect, onPriceChange, flightId }) {
  const [selectedSeat, setSelectedSeat] = useState(null);
  const [seatData, setSeatData] = useState(initialSeatLayout);
  const [seatPrices] = useState(() => {
    // Generate dynamic pricing for premium seats (30-70 USD range)
    const prices = {};
    Object.keys(initialSeatLayout).forEach(seatId => {
      if (initialSeatLayout[seatId] === 'premium') {
        // Random price between 30 and 70
        prices[seatId] = Math.floor(Math.random() * 41) + 30; // 30-70 range
      }
    });
    return prices;
  });

  // Fetch occupied seats from backend
  useEffect(() => {
    console.log('SeatSelection - flightId prop received:', flightId);
    console.log('SeatSelection - flightId type:', typeof flightId);
    
    if (flightId) {
      const apiUrl = `/api/bookings/flights/${flightId}/occupied-seats`;
      console.log('SeatSelection - Fetching occupied seats from:', apiUrl);
      
      axios.get(apiUrl)
        .then(response => {
          const occupiedSeats = response.data;
          console.log('SeatSelection - Occupied seats for flight', flightId, ':', occupiedSeats);
          console.log('SeatSelection - Number of occupied seats:', occupiedSeats.length);
          
          // Update seat data to mark occupied seats as unavailable
          const updatedSeatData = { ...initialSeatLayout };
          occupiedSeats.forEach(seatNumber => {
            console.log('SeatSelection - Marking seat as unavailable:', seatNumber);
            if (updatedSeatData[seatNumber]) {
              updatedSeatData[seatNumber] = 'unavailable';
            } else {
              console.warn('SeatSelection - Seat not found in layout:', seatNumber);
            }
          });
          console.log('SeatSelection - Updated seat data:', updatedSeatData);
          setSeatData(updatedSeatData);
        })
        .catch(error => {
          console.error('SeatSelection - Error fetching occupied seats:', error);
          console.error('SeatSelection - Error details:', error.response?.data);
          console.error('SeatSelection - Error status:', error.response?.status);
          // Use initial layout if fetch fails
          setSeatData(initialSeatLayout);
        });
    } else {
      console.log('SeatSelection - No flightId provided, using initial layout');
      setSeatData(initialSeatLayout);
    }
  }, [flightId]);

  const rows = 6;
  const leftSection = ["A", "B", "C"];
  const middleSection = ["D", "E", "F"];
  const rightSection = ["A", "B", "C"];

  const getSeatStyle = (seatId, status) => {
    const isSelected = selectedSeat === seatId;
    
    const baseStyle = {
      width: '16px',
      height: '16px',
      border: '1px solid',
      transition: 'all 0.2s ease',
      backgroundColor: '#f3f4f6',
      borderColor: '#d1d5db',
      cursor: 'pointer'
    };

    if (status === "unavailable") {
      return {
        ...baseStyle,
        backgroundColor: '#3F8EFC',
        borderColor: '#d1d5db',
        cursor: 'not-allowed'
      };
    }
    if (isSelected) {
      return {
        ...baseStyle,
        backgroundColor: '#34D399',
        borderColor: '#d1d5db',
        cursor: 'pointer'
      };
    }
    if (status === "premium") {
      return {
        ...baseStyle,
        backgroundColor: '#f59e0b',
        borderColor: '#f59e0b',
        cursor: 'pointer'
      };
    }
    return {
      ...baseStyle,
      backgroundColor: '#D1FAE5',
      borderColor: '#34D399',
      cursor: 'pointer'
    };
  };

  const handleSeatClick = (seatId, status) => {
    if (status !== "unavailable") {
      const newSelectedSeat = selectedSeat === seatId ? null : seatId;
      setSelectedSeat(newSelectedSeat);
      
      // Calculate pricing - use dynamic price for premium seats
      const isPremium = status === "premium";
      const seatPrice = isPremium ? (seatPrices[seatId] || 50) : 0; // Default to 50 if not found
      
      if (onSeatSelect) {
        onSeatSelect(newSelectedSeat, { isPremium, price: seatPrice });
      }
      if (onPriceChange) {
        onPriceChange(newSelectedSeat ? seatPrice : 0);
      }
    }
  };

  const renderSeatGrid = (sections, prefix = "") => (
    <div style={{ display: 'flex', gap: '24px' }}>
      {sections.map((section, sectionIndex) => (
        <div key={sectionIndex} style={{ display: 'flex', flexDirection: 'column' }}>
          <div style={{ display: 'flex', gap: '8px', marginBottom: '8px' }}>
            {section.map((col) => (
              <div key={col} style={{
                width: '16px',
                height: '16px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                fontSize: '12px',
                fontFamily: 'Inter, sans-serif'
              }}>
                {col}
              </div>
            ))}
          </div>
          {Array.from({ length: rows }).map((_, rowIndex) => {
            const rowNum = rowIndex + 1;
            return (
              <div key={rowNum} style={{ display: 'flex', gap: '8px', marginBottom: '4px' }}>
                {section.map((col) => {
                  const seatId = `${prefix}${rowNum}-${col}`;
                  const status = seatData[seatId] || "available";
                  return (
                    <button
                      key={seatId}
                      onClick={() => handleSeatClick(seatId, status)}
                      style={getSeatStyle(seatId, status)}
                      disabled={status === "unavailable"}
                      onMouseEnter={(e) => {
                        if (status !== "unavailable") {
                          e.target.style.opacity = '0.8';
                        }
                      }}
                      onMouseLeave={(e) => {
                        e.target.style.opacity = '1';
                      }}
                      title={
                        status === "premium" ? `Premium seat (+$${seatPrices[seatId] || 50})` :
                        status === "unavailable" ? "Seat unavailable" :
                        "Standard seat (included)"
                      }
                    >
                      {status === 'premium' && (
                        <span style={{
                          fontSize: '8px',
                          fontWeight: '600',
                          color: '#fff',
                          display: 'block',
                          lineHeight: '1'
                        }}>
                          ${seatPrices[seatId] || 50}
                        </span>
                      )}
                    </button>
                  );
                })}
              </div>
            );
          })}
        </div>
      ))}
    </div>
  );

  const containerStyle = {
    backgroundColor: '#F9FAFB',
    border: '1px solid #E5E7EB',
    borderRadius: '6px',
    padding: '12px',
    fontFamily: 'Inter, sans-serif'
  };

  const titleStyle = {
    fontSize: '12px',
    marginBottom: '12px',
    fontWeight: '500',
    color: '#1f2937'
  };

  const seatMapStyle = {
    backgroundColor: 'white',
    border: '1px solid #E5E7EB',
    borderRadius: '4px',
    padding: '12px',
    marginBottom: '12px'
  };

  const legendStyle = {
    display: 'flex',
    alignItems: 'center',
    gap: '16px',
    fontSize: '12px',
    marginBottom: '16px'
  };

  const legendItemStyle = {
    display: 'flex',
    alignItems: 'center',
    gap: '6px'
  };

  const legendColorStyle = {
    width: '10px',
    height: '10px'
  };



  return (
    <div style={containerStyle}>
      <div style={titleStyle}>Seat Selection</div>
      
      <div style={seatMapStyle}>
        <div style={{ display: 'flex', gap: '32px' }}>
          <div style={{ display: 'flex', alignItems: 'flex-start', gap: '4px' }}>
            <div style={{
              fontSize: '12px',
              marginRight: '8px',
              paddingTop: '20px',
              display: 'flex',
              flexDirection: 'column',
              gap: '5px'
            }}>
              {Array.from({ length: rows }).map((_, i) => (
                <div key={i} style={{
                  height: '16px',
                  display: 'flex',
                  alignItems: 'center'
                }}>
                  {i + 1}
                </div>
              ))}
            </div>
            {renderSeatGrid([leftSection, middleSection])}
          </div>

          <div style={{ display: 'flex', alignItems: 'flex-start', gap: '4px' }}>
            <div style={{
              fontSize: '12px',
              marginRight: '8px',
              paddingTop: '20px',
              display: 'flex',
              flexDirection: 'column',
              gap: '5px'
            }}>
              {Array.from({ length: rows }).map((_, i) => (
                <div key={i} style={{
                  height: '16px',
                  display: 'flex',
                  alignItems: 'center'
                }}>
                  {i + 1}
                </div>
              ))}
            </div>
            {renderSeatGrid([rightSection], "R")}
          </div>
        </div>
      </div>

      <div style={legendStyle}>
        <div style={legendItemStyle}>
          <div style={{
            ...legendColorStyle,
            backgroundColor: '#D1FAE5',
            border: '1px solid #34D399'
          }} />
          <span>Included</span>
        </div>
        <div style={legendItemStyle}>
          <div style={{
            ...legendColorStyle,
            backgroundColor: '#f59e0b',
            border: '1px solid #f59e0b'
          }} />
          <span>Premium ($30-$70)</span>
        </div>
        <div style={legendItemStyle}>
          <div style={{
            ...legendColorStyle,
            backgroundColor: '#3F8EFC',
            border: '1px solid #d1d5db'
          }} />
          <span>Unavailable</span>
        </div>
      </div>

      {selectedSeat && (
        <div style={{
          backgroundColor: '#eff6ff',
          border: '1px solid #dbeafe',
          borderRadius: '6px',
          padding: '12px',
          fontSize: '12px',
          color: '#1e40af',
          fontWeight: '500'
        }}>
          <div style={{ marginBottom: '4px' }}>
            ✓ Selected seat: <strong>{selectedSeat}</strong>
          </div>
          <div>
            {seatData[selectedSeat] === "premium" 
              ? `Premium seat - Additional $${seatPrices[selectedSeat] || 50}` 
              : 'Standard seat - Included in ticket price'
            }
          </div>
        </div>
      )}

      {!selectedSeat && (
        <div style={{
          fontSize: '12px',
          color: '#6b7280',
          fontStyle: 'italic',
          textAlign: 'center'
        }}>
          Click on a seat to select it
        </div>
      )}
    </div>
  );
}