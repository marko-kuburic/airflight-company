import { useState } from "react";

export function SearchBar() {
  const [from, setFrom] = useState("Belgrade (BEG)");
  const [to, setTo] = useState("Paris (CDG)");
  const [date, setDate] = useState("2025-09-10");

  const handleSearch = () => {
    console.log("Search clicked:", { from, to, date });
    // Ovde možete dodati logiku za pretragu
  };

  return (
    <div 
      className="bg-white rounded-xl p-5 mb-5"
      style={{ border: "1px solid #D9E1EA" }}
    >
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-[1fr_1fr_1fr_auto] gap-4 items-end">
        {/* From Field */}
        <div className="flex flex-col gap-2">
          <label 
            className="text-xs"
            style={{
              fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
              color: "#738396",
              fontWeight: 400
            }}
          >
            From
          </label>
          <input
            type="text"
            value={from}
            onChange={(e) => setFrom(e.target.value)}
            className="h-7 px-2.5 text-xs rounded-md outline-none"
            style={{
              backgroundColor: "#F6F8FB",
              border: "1px solid #D9E1EA",
              fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
              color: "#6B7785"
            }}
          />
        </div>

        {/* To Field */}
        <div className="flex flex-col gap-2">
          <label 
            className="text-xs"
            style={{
              fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
              color: "#738396",
              fontWeight: 400
            }}
          >
            To
          </label>
          <input
            type="text"
            value={to}
            onChange={(e) => setTo(e.target.value)}
            className="h-7 px-2.5 text-xs rounded-md outline-none"
            style={{
              backgroundColor: "#F6F8FB",
              border: "1px solid #D9E1EA",
              fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
              color: "#6B7785"
            }}
          />
        </div>

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
          className="h-8 px-6 text-white font-bold text-xs rounded-lg hover:opacity-90 transition-opacity sm:col-span-2 lg:col-span-1"
          style={{
            backgroundColor: "#3F8EFC",
            fontFamily: "Inter, -apple-system, Roboto, Helvetica, sans-serif",
            fontStyle: "italic",
            border: "none",
            cursor: "pointer"
          }}
        >
          Search
        </button>
      </div>
    </div>
  );
}