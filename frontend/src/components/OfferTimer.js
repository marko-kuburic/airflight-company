import React, { useState, useEffect } from 'react';
import { flightAPI } from '../services/api';
import toast from 'react-hot-toast';

export const OfferTimer = ({ flight, onOfferRefresh }) => {
    const [timeRemaining, setTimeRemaining] = useState(null);
    const [isExpired, setIsExpired] = useState(false);
    const [isRefreshing, setIsRefreshing] = useState(false);

    useEffect(() => {
        if (!flight?.offers || flight.offers.length === 0) return;
        
        const offer = flight.offers[0]; // Get first offer
        if (!offer?.expiresAt) return;

        const updateTimer = () => {
            const now = new Date().getTime();
            const expireTime = new Date(offer.expiresAt).getTime();
            const difference = expireTime - now;

            if (difference > 0) {
                const minutes = Math.floor((difference % (1000 * 60 * 60)) / (1000 * 60));
                const seconds = Math.floor((difference % (1000 * 60)) / 1000);
                
                setTimeRemaining(`${minutes}:${seconds.toString().padStart(2, '0')}`);
                setIsExpired(false);
            } else {
                setTimeRemaining('0:00');
                setIsExpired(true);
            }
        };

        // Update immediately
        updateTimer();
        
        // Update every second
        const interval = setInterval(updateTimer, 1000);
        
        return () => clearInterval(interval);
    }, [flight]);

    const handleRefresh = async () => {
        setIsRefreshing(true);
        try {
            const response = await flightAPI.refreshOffer(flight.id);
            
            if (response.data.success) {
                toast.success('Offer refreshed with new pricing!');
                
                // Call parent callback with updated flight data
                if (onOfferRefresh) {
                    onOfferRefresh(response.data.flight);
                }
                
                setIsExpired(false);
            }
        } catch (error) {
            console.error('Error refreshing offer:', error);
            toast.error('Failed to refresh offer. Please try again.');
        } finally {
            setIsRefreshing(false);
        }
    };

    if (!flight?.offers || flight.offers.length === 0) {
        return null;
    }

    return (
        <div className="flex items-center justify-between p-3 bg-gradient-to-r from-blue-50 to-indigo-50 rounded-lg border border-blue-200">
            <div className="flex items-center space-x-3">
                <div className="flex items-center space-x-2">
                    <svg className="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                    <span className="text-sm font-medium text-blue-800">
                        {isExpired ? 'Offer Expired' : 'Offer expires in:'}
                    </span>
                </div>
                
                {!isExpired && timeRemaining && (
                    <span className="text-lg font-bold text-blue-900 bg-white px-2 py-1 rounded border">
                        {timeRemaining}
                    </span>
                )}
            </div>

            {isExpired && (
                <div className="flex items-center space-x-3">
                    <span className="text-sm text-gray-600">
                        Price may have changed
                    </span>
                    <button
                        onClick={handleRefresh}
                        disabled={isRefreshing}
                        className="bg-blue-600 hover:bg-blue-700 disabled:bg-blue-400 text-white font-medium py-2 px-4 rounded-lg transition-colors duration-200 flex items-center space-x-2"
                    >
                        {isRefreshing ? (
                            <>
                                <svg className="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                                </svg>
                                Refreshing...
                            </>
                        ) : (
                            <>
                                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                                </svg>
                                Get New Price
                            </>
                        )}
                    </button>
                </div>
            )}
        </div>
    );
};