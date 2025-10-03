import axios from 'axios';
import toast from 'react-hot-toast';

// Create axios instance with base configuration
const api = axios.create({
  baseURL: process.env.REACT_APP_API_URL || '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor for adding auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor for handling errors
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    const message = error.response?.data?.message || error.message || 'Something went wrong';
    
    if (error.response?.status === 401) {
      localStorage.removeItem('authToken');
      toast.error('Session expired. Please login again.');
      window.location.href = '/login';
    } else if (error.response?.status >= 500) {
      toast.error('Server error. Please try again later.');
    } else {
      toast.error(message);
    }
    
    return Promise.reject(error);
  }
);

// API endpoints
export const authAPI = {
  // Authentication
  login: (credentials) => api.post('/users/login', credentials),
  register: (userData) => api.post('/users/register', userData),
  
  // User management
  getUserProfile: (userId) => api.get(`/users/profile/${userId}`),
  updateUserProfile: (userId, data) => api.put(`/users/profile/${userId}`, data),
  getUserReservations: (userId) => api.get(`/users/${userId}/reservations`),
  getUserLoyalty: (userId) => api.get(`/users/${userId}/loyalty`),
};

export const flightAPI = {
  // Flight search
  searchFlights: (params) => api.get('/flights/search', { params }),
  getFlightById: (id) => api.get(`/flights/${id}`),
  
  // Airports
  getAirports: () => api.get('/flights/airports'),
  getDestinationsFromOrigin: (origin) => api.get('/flights/destinations', { params: { origin } }),
  getOriginsToDestination: (destination) => api.get('/flights/origins', { params: { destination } }),
  
  // Offers
  getOffers: () => api.get('/offers'),
  getOfferById: (id) => api.get(`/offers/${id}`),
  getOffersByFlight: (flightId) => api.get(`/offers/flight/${flightId}`),
  checkOfferValidity: (id) => api.get(`/offers/${id}/validity`),
  
  // Refresh expired offers with new pricing
  refreshOffer: (flightId) => api.post(`/flights/${flightId}/refresh-offer`),
  
  // Seat management
  getOccupiedSeats: (flightId) => api.get(`/bookings/flights/${flightId}/occupied-seats`),
  checkSeatAvailability: (flightId, seatNumber) => 
    api.get(`/bookings/flights/${flightId}/seats/${seatNumber}/availability`),
};

export const bookingAPI = {
  // Reservations - using test endpoint for now
  createReservation: (data) => api.post('/bookings/reservations', data),
  getReservation: (id) => api.get(`/bookings/reservations/${id}`),
  getReservationByNumber: (reservationNumber) => 
    api.get(`/bookings/reservations/number/${reservationNumber}`),
  getReservationsByCustomer: (customerId) => 
    api.get(`/test/reservations/${customerId}`),
  cancelReservation: (id, reason) => 
    api.post(`/bookings/reservations/${id}/cancel`, null, { params: { reason } }),
  
  // Payments
  processPayment: (data) => api.post('/bookings/payments', data),
  
  // Tickets
  getTicketsForReservation: (reservationId) => 
    api.get(`/bookings/reservations/${reservationId}/tickets`),
  updateTicketStatus: (ticketId, status) => 
    api.patch(`/bookings/tickets/${ticketId}/status`, null, { params: { status } }),
};

export const passengerAPI = {
  createPassenger: (data) => api.post('/passengers', data),
  getPassenger: (id) => api.get(`/passengers/${id}`),
  updatePassenger: (id, data) => api.put(`/passengers/${id}`, data),
  searchPassengers: (searchTerm) => api.get(`/passengers/search`, { params: { q: searchTerm } }),
};

export const loyaltyAPI = {
  getCustomerPoints: (customerId) => api.get(`/loyalty/customers/${customerId}/points`),
  getCustomerTier: (customerId) => api.get(`/loyalty/customers/${customerId}/tier`),
  addPoints: (customerId, points) => api.post(`/loyalty/customers/${customerId}/points`, { points }),
  deductPoints: (customerId, points) => api.delete(`/loyalty/customers/${customerId}/points`, { data: { points } }),
};

// Utility functions
export const handleApiError = (error) => {
  console.error('API Error:', error);
  const message = error.response?.data?.message || error.message || 'An error occurred';
  toast.error(message);
  throw error;
};

export const downloadTicket = async (ticketId) => {
  try {
    const response = await api.get(`/tickets/${ticketId}/download`, {
      responseType: 'blob'
    });
    
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `ticket-${ticketId}.pdf`);
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
    
    toast.success('Ticket downloaded successfully');
  } catch (error) {
    handleApiError(error);
  }
};

export default api;