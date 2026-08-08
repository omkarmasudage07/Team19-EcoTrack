import React, { useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import AuthLayout from './AuthLayout';
import authService from '../../services/authService';
import { getErrorMessage } from '../../services/api';

const ResetPassword = () => {
  const [searchParams] = useSearchParams();
  const tokenFromUrl = searchParams.get('token') || '';
  const { register, handleSubmit, watch, formState: { errors } } = useForm({
    defaultValues: { token: tokenFromUrl },
  });
  const [submitting, setSubmitting] = useState(false);
  const [serverError, setServerError] = useState('');
  const [done, setDone] = useState(false);
  const navigate = useNavigate();
  const newPassword = watch('newPassword');

  const onSubmit = async (values) => {
    setServerError('');
    setSubmitting(true);
    try {
      await authService.resetPassword({ token: values.token, newPassword: values.newPassword });
      setDone(true);
      setTimeout(() => navigate('/login'), 2000);
    } catch (err) {
      setServerError(getErrorMessage(err, 'This reset link is invalid or has expired.'));
    } finally {
      setSubmitting(false);
    }
  };

  if (done) {
    return (
      <AuthLayout title="Password reset" subtitle="You can now sign in with your new password">
        <p className="small text-muted text-center mb-0">Redirecting you to sign in...</p>
      </AuthLayout>
    );
  }

  return (
    <AuthLayout
      title="Reset your password"
      subtitle="Enter the reset token and choose a new password"
      footer={<Link to="/login" className="fw-semibold">Back to sign in</Link>}
    >
      {serverError && <div className="alert alert-danger small py-2 mb-3">{serverError}</div>}
      <form onSubmit={handleSubmit(onSubmit)} noValidate>
        <div className="mb-3">
          <label className="form-label small fw-semibold">Reset token</label>
          <input
            className={`form-control ${errors.token ? 'is-invalid' : ''}`}
            placeholder="Paste the token from your email"
            {...register('token', { required: 'Reset token is required' })}
          />
          {errors.token && <div className="invalid-feedback">{errors.token.message}</div>}
        </div>

        <div className="mb-3">
          <label className="form-label small fw-semibold">New password</label>
          <input
            type="password"
            className={`form-control ${errors.newPassword ? 'is-invalid' : ''}`}
            placeholder="At least 8 characters"
            {...register('newPassword', {
              required: 'New password is required',
              minLength: { value: 8, message: 'Must be at least 8 characters' },
              pattern: { value: /^(?=.*[A-Za-z])(?=.*\d).+$/, message: 'Include at least one letter and one number' },
            })}
          />
          {errors.newPassword && <div className="invalid-feedback">{errors.newPassword.message}</div>}
        </div>

        <div className="mb-4">
          <label className="form-label small fw-semibold">Confirm new password</label>
          <input
            type="password"
            className={`form-control ${errors.confirmPassword ? 'is-invalid' : ''}`}
            placeholder="Re-enter your new password"
            {...register('confirmPassword', {
              required: 'Please confirm your password',
              validate: (value) => value === newPassword || 'Passwords do not match',
            })}
          />
          {errors.confirmPassword && <div className="invalid-feedback">{errors.confirmPassword.message}</div>}
        </div>

        <button type="submit" className="btn btn-success w-100 py-2 fw-semibold d-flex align-items-center justify-content-center gap-2" disabled={submitting}>
          {submitting && <span className="spinner-border spinner-border-sm" />}
          Reset password
        </button>
      </form>
    </AuthLayout>
  );
};

export default ResetPassword;
