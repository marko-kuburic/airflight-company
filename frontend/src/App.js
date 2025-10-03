import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
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
import { Sidebar } from './components/Sidebar';
import './App.css';

function App() {
  return (
    <Router>
      <div className="App">
        <Routes>
          <Route path="/" element={<Home />} />
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
        </Routes>
      </div>
    </Router>
  );
}

export default App;