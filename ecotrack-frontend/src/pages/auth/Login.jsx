import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import AuthLayout from './AuthLayout';
import authService from '../../services/authService';
import { useAuth, HOME_ROUTE_BY_ROLE } from '../../context/AuthContext';
import { getErrorMessage } from '../../services/api';

const Login = () => {
  const { register, handleSubmit, formState: { errors, isValid } } = useForm({ mode: 'onChange' });
  const [serverError, setServerError] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const onSubmit = async (values) => {
    setServerError('');
    setSubmitting(true);
    try {
      const payload = {
        email: values.email ? values.email.trim().toLowerCase() : '',
        password: values.password ? values.password.trim() : '',
      };
      const authResponse = await authService.login(payload);
      const user = login(authResponse);
      const redirectTo = location.state?.from || HOME_ROUTE_BY_ROLE[user.role] || '/';
      navigate(redirectTo, { replace: true });
    } catch (err) {
      setServerError(getErrorMessage(err, 'Invalid email or password.'));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <AuthLayout
      title="Welcome back"
      subtitle="Sign in to your EcoTrack account"
      footer={
        <>
          New to EcoTrack?{' '}
          <Link to="/register" className="fw-semibold">Create a Citizen account</Link>
        </>
      }
    >
      {serverError && (
        <div className="alert alert-danger small py-2 mb-3">{serverError}</div>
      )}

      <form onSubmit={handleSubmit(onSubmit)} noValidate>
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

        <div className="mb-4">
          <div className="d-flex justify-content-between align-items-center mb-1">
            <label className="form-label small fw-semibold mb-0">Password</label>
            <Link to="/forgot-password" className="small fw-semibold">Forgot password?</Link>
          </div>
          <input
            type="password"
            className={`form-control ${errors.password ? 'is-invalid' : ''}`}
            placeholder="••••••••"
            {...register('password', {
              required: 'Password is required',
              validate: (val) => val.trim().length > 0 || 'Password cannot be empty or only spaces',
            })}
          />
          {errors.password && <div className="invalid-feedback">{errors.password.message}</div>}
        </div>

        <button
          type="submit"
          className="btn btn-success w-100 py-2 fw-semibold d-flex align-items-center justify-content-center gap-2"
          disabled={!isValid || submitting}
        >
          {submitting && <span className="spinner-border spinner-border-sm" />}
          Sign in
        </button>
      </form>

      <div className="mt-4 pt-3 border-top text-center">
        <p className="small text-muted mb-2">Recycler or Industrial Buyer?</p>
        <div className="d-flex gap-2 justify-content-center">
          <Link to="/become-recycler" className="small fw-semibold">Become a Recycler Partner</Link>
          <span className="text-muted">·</span>
          <Link to="/become-industry" className="small fw-semibold">Become an Industry Buyer</Link>
        </div>
      </div>
    </AuthLayout>
  );
};

export default Login;
