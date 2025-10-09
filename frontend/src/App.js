import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import FlightSearch from './pages/FlightSearch';
import BookingDetails from './pages/BookingDetails';
import Payment from './pages/Payment';
import BookingConfirmed from './pages/BookingConfirmed';
import MyTickets from './pages/MyTickets';
import EditTicket from './pages/EditTicket';
import MyProfile from './pages/MyProfile';
import LoyaltyProgram from './pages/LoyaltyProgram';
import Notifications from './pages/Notifications';
import AircraftManagement from './pages/AircraftManagement';
import EditAircraft from './pages/EditAircraft';
import AddAircraft from './pages/AddAircraft';
import ServiceRecords from './pages/ServiceRecords';
import AddService from './pages/AddService';
import AddRoute from './pages/AddRoute';
import RoutesList from './pages/RoutesList';
import DispatcherMaintenance from './pages/DispatcherMaintenance';
import CreateFlight from './pages/CreateFlight';
import EditFlight from './pages/EditFlight';
import Flights from './pages/Flights';
import Reports from './pages/Reports';
import AdminLogin from './pages/AdminLogin';
import CustomerManagement from './pages/CustomerManagement';
import { Sidebar } from './components/Sidebar';
import './App.css';

function App() {
  return (
    <Router>
      <div className="App">
        <Routes>
          <Route path="/" element={<Navigate to="/search" replace />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/search" element={<FlightSearch />} />
          <Route path="/booking" element={<BookingDetails />} />
          <Route path="/payment" element={<Payment />} />
          <Route path="/booking-confirmed" element={<BookingConfirmed />} />
          <Route path="/tickets" element={<MyTickets />} />
          <Route path="/tickets/:ticketId/edit" element={<EditTicket />} />
          <Route path="/profile" element={<MyProfile />} />
          <Route path="/loyalty" element={<LoyaltyProgram />} />
          <Route path="/notifications" element={<Notifications />} />
          
          {/* Flight Dispatcher Routes */}
          <Route path="/dispatcher/flights" element={<Flights />} />
          <Route path="/dispatcher/create-flight" element={<CreateFlight />} />
          <Route path="/dispatcher/flights/edit/:flightId" element={<EditFlight />} />
          <Route path="/dispatcher/aircrafts" element={<AircraftManagement />} />
          <Route path="/dispatcher/aircrafts/edit/:registration" element={<EditAircraft />} />
          <Route path="/dispatcher/aircrafts/add" element={<AddAircraft />} />
          <Route path="/dispatcher/routes" element={<RoutesList />} />
          <Route path="/dispatcher/routes/add" element={<AddRoute />} />
          <Route path="/dispatcher/maintenance" element={<DispatcherMaintenance />} />
          <Route path="/dispatcher/reports" element={<Reports />} />
          
          {/* Technician Routes */}
          <Route path="/technician/records" element={<ServiceRecords />} />
          <Route path="/technician/add-service" element={<AddService />} />
          <Route path="/admin-login" element={<AdminLogin />} />
          <Route path="/customer-management" element={<CustomerManagement />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;