import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import './Navbar.css';

const Navbar = () => {
  const { isAuthenticated, logout, user } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  if (!isAuthenticated) {
    return null; // Don't show navbar if not authenticated
  }

  return (
    <nav className="navbar">
      <div className="navbar-container">
        <Link to="/dashboard" className="navbar-brand">
          <img src="/logo.svg" alt="Expense Tracker" className="navbar-logo" />Expense Tracker
        </Link>

        <div className="navbar-menu">
          <Link to="/expenses" className="navbar-link">
            Transactions
          </Link>
          <Link to="/categories" className="navbar-link">
            Categories
          </Link>
          <Link to="/expenses/add" className="navbar-link">
            Add Transaction
          </Link>
          <Link to="/expenses/import" className="navbar-link">
            Import Transactions
          </Link>
        </div>

        <div className="navbar-user">
          <span className="navbar-user-greeting">
            Welcome, {user?.username}
          </span>
          <button onClick={handleLogout} className="btn btn-secondary navbar-logout">
            Logout
          </button>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
