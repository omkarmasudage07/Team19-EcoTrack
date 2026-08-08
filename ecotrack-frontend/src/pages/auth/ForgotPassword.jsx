import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import AuthLayout from './AuthLayout';
import authService from '../../services/authService';
import { getErrorMessage } from '../../services/api';

const ForgotPassword = () => {
  const { register, handleSubmit, formState: { errors } } = useForm();
  const [submitting, setSubmitting] = useState(false);
  const [serverError, setServerError] = useState('');
  const [sent, setSent] = useState(false);

  const onSubmit = async ({ email }) => {
    setServerError('');
    setSubmitting(true);
    try {
      await authService.forgotPassword(email);
      setSent(true);
    } catch (err) {
      setServerError(getErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  };

  if (sent) {
    return (
      <AuthLayout title="Check your email" subtitle="We've sent password reset instructions">
        <p className="small text-muted text-center mb-4">
          If an account exists for that email, a reset link has been generated. Follow the
          link to choose a new password.
        </p>
        <Link to="/login" className="btn btn-outline-success w-100 py-2 fw-semibold">
          Back to sign in
        </Link>
      </AuthLayout>
    );
  }

  return (
    <AuthLayout
      title="Forgot your password?"
      subtitle="Enter your email and we'll send you a reset link"
      footer={<Link to="/login" className="fw-semibold">Back to sign in</Link>}
    >
      {serverError && <div className="alert alert-danger small py-2 mb-3">{serverError}</div>}
      <form onSubmit={handleSubmit(onSubmit)} noValidate>
        <div className="mb-4">
          <label className="form-label small fw-semibold">Email address</label>
          <input
            type="email"
            className={`form-control ${errors.email ? 'is-invalid' : ''}`}
            placeholder="you@example.com"
            {...register('email', { required: 'Email is required' })}
          />
          {errors.email && <div className="invalid-feedback">{errors.email.message}</div>}
        </div>
        <button type="submit" className="btn btn-success w-100 py-2 fw-semibold d-flex align-items-center justify-content-center gap-2" disabled={submitting}>
          {submitting && <span className="spinner-border spinner-border-sm" />}
          Send reset link
        </button>
      </form>
    </AuthLayout>
  );
};

export default ForgotPassword;
