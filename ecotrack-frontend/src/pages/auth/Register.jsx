import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import AuthLayout from './AuthLayout';
import authService from '../../services/authService';
import { useAuth, HOME_ROUTE_BY_ROLE } from '../../context/AuthContext';
import { getErrorMessage } from '../../services/api';

const Register = () => {
  const { register, handleSubmit, watch, formState: { errors, isValid } } = useForm({ mode: 'onChange' });
  const [serverError, setServerError] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();
  const password = watch('password');

  const onSubmit = async (values) => {
    setServerError('');
    setSubmitting(true);
    try {
      const { confirmPassword, ...payload } = values;
      payload.email = payload.email.trim().toLowerCase();
      payload.fullName = payload.fullName.trim();
      const authResponse = await authService.register(payload);
      const user = login(authResponse);
      navigate(HOME_ROUTE_BY_ROLE[user.role] || '/', { replace: true });
    } catch (err) {
      setServerError(getErrorMessage(err, 'Registration failed. Please check your details.'));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <AuthLayout
      title="Create your Citizen account"
      subtitle="Schedule pickups and earn EcoPoints for responsible recycling"
      footer={
        <>
          Already have an account? <Link to="/login" className="fw-semibold">Sign in</Link>
        </>
      }
    >
      {serverError && <div className="alert alert-danger small py-2 mb-3">{serverError}</div>}

      <form onSubmit={handleSubmit(onSubmit)} noValidate>
        <div className="mb-3">
          <label className="form-label small fw-semibold">Full name</label>
          <input
            className={`form-control ${errors.fullName ? 'is-invalid' : ''}`}
            placeholder="Jane Doe"
            {...register('fullName', {
              required: 'Full name is required',
              minLength: { value: 3, message: 'Must be at least 3 characters' },
              maxLength: { value: 100, message: 'Must not exceed 100 characters' },
              pattern: { value: /^[A-Za-z ]+$/, message: 'Alphabets and spaces only' },
            })}
          />
          {errors.fullName && <div className="invalid-feedback">{errors.fullName.message}</div>}
        </div>

        <div className="mb-3">
          <label className="form-label small fw-semibold">Email address</label>
          <input
            type="email"
            className={`form-control ${errors.email ? 'is-invalid' : ''}`}
            placeholder="you@example.com"
            {...register('email', {
              required: 'Email is required',
              pattern: {
                value: /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/,
                message: 'Enter a valid email address',
              },
            })}
          />
          {errors.email && <div className="invalid-feedback">{errors.email.message}</div>}
        </div>

        <div className="mb-3">
          <label className="form-label small fw-semibold">Mobile number</label>
          <input
            className={`form-control ${errors.phone ? 'is-invalid' : ''}`}
            placeholder="9876543210"
            {...register('phone', {
              required: 'Mobile number is required',
              pattern: { value: /^[6-9]\d{9}$/, message: 'Enter a valid 10 digit mobile number starting with 6-9' },
              validate: (val) => !/^(\d)\1{9}$/.test(val) || 'Invalid phone number (repeated digits not allowed)',
            })}
          />
          {errors.phone && <div className="invalid-feedback">{errors.phone.message}</div>}
        </div>

        <div className="mb-3">
          <label className="form-label small fw-semibold">Password</label>
          <input
            type="password"
            className={`form-control ${errors.password ? 'is-invalid' : ''}`}
            placeholder="8-20 chars (A-z, 0-9, special char)"
            {...register('password', {
              required: 'Password is required',
              minLength: { value: 8, message: 'Must be at least 8 characters' },
              maxLength: { value: 20, message: 'Must not exceed 20 characters' },
              pattern: {
                value: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#^()_+\-=\[\]{};':"\\|,.<>\/?])[^\s]{8,20}$/,
                message: 'Must contain uppercase, lowercase, number, special character (no spaces)',
              },
            })}
          />
          {errors.password && <div className="invalid-feedback">{errors.password.message}</div>}
        </div>

        <div className="mb-4">
          <label className="form-label small fw-semibold">Confirm password</label>
          <input
            type="password"
            className={`form-control ${errors.confirmPassword ? 'is-invalid' : ''}`}
            placeholder="Re-enter your password"
            {...register('confirmPassword', {
              required: 'Please confirm your password',
              validate: (value) => value === password || 'Passwords do not match',
            })}
          />
          {errors.confirmPassword && <div className="invalid-feedback">{errors.confirmPassword.message}</div>}
        </div>

        <button
          type="submit"
          className="btn btn-success w-100 py-2 fw-semibold d-flex align-items-center justify-content-center gap-2"
          disabled={!isValid || submitting}
        >
          {submitting && <span className="spinner-border spinner-border-sm" />}
          Create account
        </button>
      </form>
    </AuthLayout>
  );
};

export default Register;
