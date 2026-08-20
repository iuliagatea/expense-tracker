import React, { useState, useMemo } from 'react';
import { Link, useSearchParams, useNavigate } from 'react-router-dom';
import { authService } from '../services/api';
import './Auth.css';

const ResetPassword = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    email: searchParams.get('email') || '',
    token: searchParams.get('token') || '',
    newPassword: '',
    confirmPassword: ''
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);

  const missingCredentials = useMemo(() => !formData.email || !formData.token, [formData.email, formData.token]);

  const handleChange = (e) => {
    setFormData((prev) => ({
      ...prev,
      [e.target.name]: e.target.value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (formData.newPassword.length < 6) {
      setError('Password must be at least 6 characters long.');
      return;
    }

    if (formData.newPassword !== formData.confirmPassword) {
      setError('Passwords do not match.');
      return;
    }

    if (missingCredentials) {
      setError('This reset link is missing the required email or token.');
      return;
    }

    setLoading(true);

    try {
      const response = await authService.resetPassword({
        email: formData.email,
        token: formData.token,
        newPassword: formData.newPassword
      });

      if (response.success) {
        setSuccess(response.message || 'Password reset successfully.');
        setTimeout(() => navigate('/login', { state: { message: response.message || 'Password reset successfully.' } }), 1500);
      } else {
        setError(response.message || 'Unable to reset your password.');
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Unable to reset your password right now.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <div className="auth-logo">
          <img src="/expense_tracker_logo.svg" alt="Expense Tracker Logo" />
        </div>
        <h2>Set a New Password</h2>

        {error && <div className="alert alert-error">{error}</div>}
        {success && <div className="alert alert-success">{success}</div>}

        {missingCredentials && (
          <div className="alert alert-error">The recovery link is incomplete. Please request a new password reset email.</div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="email">Email</label>
            <input
              type="email"
              id="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              required
              placeholder="Email address"
              readOnly={Boolean(searchParams.get('email'))}
            />
          </div>

          <div className="form-group">
            <label htmlFor="newPassword">New password</label>
            <input
              type="password"
              id="newPassword"
              name="newPassword"
              value={formData.newPassword}
              onChange={handleChange}
              required
              placeholder="Enter a new password"
              minLength="6"
            />
          </div>

          <div className="form-group">
            <label htmlFor="confirmPassword">Confirm password</label>
            <input
              type="password"
              id="confirmPassword"
              name="confirmPassword"
              value={formData.confirmPassword}
              onChange={handleChange}
              required
              placeholder="Confirm new password"
              minLength="6"
            />
          </div>

          <button type="submit" className="btn btn-primary btn-full" disabled={loading || missingCredentials}>
            {loading ? 'Updating password...' : 'Update password'}
          </button>
        </form>

        <div className="auth-links">
          <p>
            <Link to="/login" className="auth-link">
              Back to login
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default ResetPassword;
