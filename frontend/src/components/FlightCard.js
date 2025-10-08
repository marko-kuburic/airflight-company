import React, { useState } from 'react';
import { OfferTimer } from './OfferTimer';

export const FlightCard = ({ flight, onFlightSelect, onFlightUpdate }) => {
    const [currentFlight, setCurrentFlight] = useState(flight);

    const handleOfferRefresh = (updatedFlight) => {
        setCurrentFlight(updatedFlight);
        if (onFlightUpdate) {
            onFlightUpdate(updatedFlight);
        }
    };

    const formatTime = (dateTime) => {
        return new Date(dateTime).toLocaleTimeString('en-US', {
            hour: '2-digit',
            minute: '2-digit',
            hour12: false
        });
    };

    const formatDate = (dateTime) => {
        return new Date(dateTime).toLocaleDateString('en-US', {
            month: 'short',
            day: 'numeric'
        });
    };

    const getLowestPrice = () => {
        if (!currentFlight.offers || currentFlight.offers.length === 0) {
            return currentFlight.currentPrice;
        }
        
        const prices = currentFlight.offers.flatMap(offer => 
            offer.fares ? offer.fares.map(fare => fare.price) : []
        );
        
        return prices.length > 0 ? Math.min(...prices) : currentFlight.currentPrice;
    };

    return (
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden hover:shadow-md transition-shadow duration-200">
            {/* Main flight info */}
            <div className="p-6">
                <div className="flex items-center justify-between mb-4">
                    <div className="flex items-center space-x-3">
                        <div className="text-sm font-semibold text-blue-600 bg-blue-50 px-2 py-1 rounded">
                            {currentFlight.flightNumber}
                        </div>
                        <div className="text-sm text-gray-500">
                            {currentFlight.aircraft}
                        </div>
                    </div>
                    
                    <div className="text-right">
                        <div className="text-2xl font-bold text-gray-900">
                            €{getLowestPrice()}
                        </div>
                        <div className="text-sm text-gray-500">
                            from / {currentFlight.availableSeats} seats left
                        </div>
                    </div>
                </div>

                {/* Route and time info - Centered and Larger */}
                <div className="flex flex-col items-center justify-center mb-6 py-4">
                    <div className="flex items-center justify-center w-full max-w-4xl space-x-12">
                        {/* Departure */}
                        <div className="text-center flex-shrink-0">
                            <div className="text-4xl font-bold text-gray-900 mb-2">
                                {formatTime(currentFlight.departureTime)}
                            </div>
                            <div className="text-base font-medium text-gray-600 mb-1">
                                {currentFlight.origin}
                            </div>
                            <div className="text-sm text-gray-400">
                                {formatDate(currentFlight.departureTime)}
                            </div>
                        </div>

                        {/* Flight path */}
                        <div className="flex-1 flex flex-col items-center justify-center relative px-8">
                            <div className="w-full relative">
                                <div className="absolute w-full h-0.5 bg-gradient-to-r from-blue-400 to-blue-600 top-1/2 transform -translate-y-1/2"></div>
                                <div className="relative flex justify-center">
                                    <div className="bg-white px-4 py-2 border-2 border-blue-500 rounded-full text-sm font-medium text-blue-600 shadow-sm">
                                        ✈️ {currentFlight.duration}
                                    </div>
                                </div>
                            </div>
                            <svg className="absolute right-4 w-6 h-6 text-blue-600" fill="currentColor" viewBox="0 0 20 20">
                                <path fillRule="evenodd" d="M10.293 3.293a1 1 0 011.414 0l6 6a1 1 0 010 1.414l-6 6a1 1 0 01-1.414-1.414L14.586 11H3a1 1 0 110-2h11.586l-4.293-4.293a1 1 0 010-1.414z" clipRule="evenodd" />
                            </svg>
                        </div>

                        {/* Arrival */}
                        <div className="text-center flex-shrink-0">
                            <div className="text-4xl font-bold text-gray-900 mb-2">
                                {formatTime(currentFlight.arrivalTime)}
                            </div>
                            <div className="text-base font-medium text-gray-600 mb-1">
                                {currentFlight.destination}
                            </div>
                            <div className="text-sm text-gray-400">
                                {formatDate(currentFlight.arrivalTime)}
                            </div>
                        </div>
                    </div>
                </div>

                {/* Action button */}
                <div className="flex justify-end">
                    <button
                        onClick={() => onFlightSelect && onFlightSelect(currentFlight)}
                        className="bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 px-6 rounded-lg transition-colors duration-200"
                    >
                        Select Flight
                    </button>
                </div>
            </div>

            {/* Offer timer - only show if flight has offers */}
            {currentFlight.offers && currentFlight.offers.length > 0 && (
                <div className="px-6 pb-4">
                    <OfferTimer 
                        flight={currentFlight} 
                        onOfferRefresh={handleOfferRefresh}
                    />
                </div>
            )}
        </div>
    );
};