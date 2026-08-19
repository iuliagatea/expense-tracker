import React, { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { authService } from '../services/api';
import './Auth.css';

const ConfirmEmail = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const [message, setMessage] = useState('Confirming your email...');

  useEffect(() => {
    const token = searchParams.get('token');
    if (!token) {
      navigate('/login?confirmed=false&error=invalid', { replace: true });
      return;
    }

    const confirm = async () => {
      try {
        const response = await authService.confirmEmail(token);
        setMessage(response.message || 'Email confirmed successfully.');
        navigate('/login?confirmed=true', { replace: true });
      } catch (error) {
        setMessage('Unable to confirm your email. Redirecting to login...');
        navigate('/login?confirmed=false&error=invalid', { replace: true });
      }
    };

    confirm();
  }, [navigate, searchParams]);

  return (
    <div className="auth-container">
      <div className="auth-card">
        <div className="auth-logo">
          <img src="/expense_tracker_logo.svg" alt="Expense Tracker Logo" />
        </div>
        <h2>Confirming Email</h2>
        <p className="text-center">{message}</p>
      </div>
    </div>
  );
};

export default ConfirmEmail;

