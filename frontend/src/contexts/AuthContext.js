import React, { createContext, useState, useContext, useEffect } from 'react';
import { authService } from '../services/api';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Check if user is already logged in (token exists)
    const token = localStorage.getItem('token');
    if (token) {
      setIsAuthenticated(true);
      // You could decode the token to get user info if needed
    }
    setLoading(false);
  }, []);

  const login = async (username, password) => {
    try {
      const response = await authService.login({ username, password });
      console.log('Login response:', response);
      if (response.token) {
        localStorage.setItem('token', response.token);
        setIsAuthenticated(true);
        setUser({ username });
        return { success: true };
      }
      return { success: false, message: response.message };
    } catch (error) {
      return { success: false, message: error.response?.data?.message || 'Login failed' };
    }
  };

  const signup = async (fullName, username, password) => {
    try {
      const response = await authService.signup({ fullName, username, password });
      if (response.token) {
        localStorage.setItem('token', response.token);
        setIsAuthenticated(true);
        setUser({ username });
        return { success: true };
      }
      return { success: false, message: response.message };
    } catch (error) {
      return { success: false, message: error.response?.data?.message || 'Signup failed' };
    }
  };

    const changePassword = async (username, currentPassword, newPassword) => {
      try {
        const response = await authService.changePassword({ username, currentPassword, newPassword });
        console.log('Change password response:', response);
        if (response.ok) {
          return { success: true };
        }
        return { success: false, message: response.message };
      } catch (error) {
        return { success: false, message: error.response?.data?.message || 'Change password failed' };
      }
    };

  const logout = () => {
    localStorage.removeItem('token');
    setIsAuthenticated(false);
    setUser(null);
  };

  const value = {
    isAuthenticated,
    user,
    loading,
    login,
    signup,
    logout
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};
