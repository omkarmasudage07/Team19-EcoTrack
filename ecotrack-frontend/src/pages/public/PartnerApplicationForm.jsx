import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { FiCheckCircle, FiInfo } from 'react-icons/fi';
import { FaLeaf } from 'react-icons/fa';
import { getErrorMessage } from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import { INDIAN_STATES, getCitiesForState } from '../../constants/indianLocations';

const PartnerApplicationForm = ({ title, subtitle, submitFn, successMessage }) => {
  const { user } = useAuth();
  const { register, handleSubmit, setValue, watch, formState: { errors, isValid } } = useForm({
    mode: 'onChange',
    defaultValues: {
      state: 'Maharashtra',
      city: 'Pune',
      regionName: 'Pune Region'
    }
  });

  const selectedState = watch('state');
  const availableCities = getCitiesForState(selectedState);

  const [serverError, setServerError] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [submitted, setSubmitted] = useState(false);

  useEffect(() => {
    if (user?.email) {
      setValue('email', user.email);
    }
  }, [user, setValue]);

  // When state changes, reset city selection to first available city or empty string
  const handleStateChange = (e) => {
    const newState = e.target.value;
    setValue('state', newState);
    const cities = getCitiesForState(newState);
    setValue('city', cities.length > 0 ? cities[0] : '');
  };

  const onSubmit = async (values) => {
    setServerError('');
    setSubmitting(true);
    try {
      const payload = {
        ...values,
        companyName: values.companyName.trim(),
        registrationNumber: values.registrationNumber.trim().toUpperCase(),
        email: values.email.trim().toLowerCase(),
        contactPerson: values.contactPerson.trim(),
        address: values.address.trim(),
        city: values.city.trim(),
        state: values.state.trim(),
      };
      await submitFn(payload);
      setSubmitted(true);
    } catch (err) {
      setServerError(getErrorMessage(err, 'Could not submit your application. Please check your details.'));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="min-vh-100" style={{ background: 'var(--color-bg)' }}>
      <nav className="d-flex align-items-center px-4 py-3 border-bottom bg-white">
        <Link to="/" className="d-flex align-items-center gap-2 text-decoration-none">
          <div
            className="d-flex align-items-center justify-content-center rounded-2"
            style={{ width: 32, height: 32, background: 'var(--color-primary)', color: '#fff' }}
          >
            <FaLeaf size={18} />
          </div>
          <span className="fw-bold fs-5 text-dark">EcoTrack</span>
        </Link>
      </nav>

      <div className="container py-5" style={{ maxWidth: 680 }}>
        {submitted ? (
          <div className="card border-0 shadow-sm p-5 text-center">
            <FiCheckCircle size={40} className="text-success mx-auto mb-3" />
            <h4 className="fw-bold mb-2">Application submitted</h4>
            <p className="text-muted mb-4">{successMessage}</p>
            <Link to="/login" className="btn btn-success">Back to sign in</Link>
          </div>
        ) : (
          <div className="card border-0 shadow-sm p-4 p-md-5">
            <div className="mb-4">
              <h4 className="fw-bold mb-1">{title}</h4>
              <p className="text-muted small mb-0">{subtitle}</p>
            </div>

            {user?.email && (
              <div className="alert alert-info small py-2 mb-3 d-flex align-items-center gap-2">
                <FiInfo size={16} />
                <span>Applying for account: <strong>{user.email}</strong></span>
              </div>
            )}

            {serverError && <div className="alert alert-danger small py-2 mb-3">{serverError}</div>}

            <form onSubmit={handleSubmit(onSubmit)} noValidate>
              <div className="row g-3">
                <div className="col-md-6">
                  <label className="form-label small fw-semibold">Company name</label>
                  <input
                    className={`form-control ${errors.companyName ? 'is-invalid' : ''}`}
                    placeholder="Eco Recycle Pvt Ltd"
                    {...register('companyName', {
                      required: 'Company name is required',
                      minLength: { value: 3, message: 'Must be at least 3 characters' },
                      maxLength: { value: 100, message: 'Must not exceed 100 characters' },
                    })}
                  />
                  {errors.companyName && <div className="invalid-feedback">{errors.companyName.message}</div>}
                </div>

                <div className="col-md-6">
                  <label className="form-label small fw-semibold">GST / Registration number</label>
                  <input
                    className={`form-control ${errors.registrationNumber ? 'is-invalid' : ''}`}
                    placeholder="27ABCDE1234F1Z5"
                    {...register('registrationNumber', {
                      required: 'GST / Registration number is required',
                      pattern: {
                        value: /^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$/,
                        message: 'Enter a valid 15-character GSTIN format (e.g. 27ABCDE1234F1Z5)',
                      },
                    })}
                  />
                  {errors.registrationNumber && <div className="invalid-feedback">{errors.registrationNumber.message}</div>}
                </div>

                <div className="col-md-6">
                  <label className="form-label small fw-semibold">Email address</label>
                  <input
                    type="email"
                    className={`form-control ${errors.email ? 'is-invalid' : ''}`}
                    placeholder="contact@company.com"
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

                <div className="col-md-6">
                  <label className="form-label small fw-semibold">Contact person</label>
                  <input
                    className={`form-control ${errors.contactPerson ? 'is-invalid' : ''}`}
                    placeholder="John Smith"
                    {...register('contactPerson', {
                      required: 'Contact person is required',
                      minLength: { value: 3, message: 'Must be at least 3 characters' },
                      maxLength: { value: 100, message: 'Must not exceed 100 characters' },
                    })}
                  />
                  {errors.contactPerson && <div className="invalid-feedback">{errors.contactPerson.message}</div>}
                </div>

                <div className="col-md-6">
                  <label className="form-label small fw-semibold">Phone number</label>
                  <input
                    className={`form-control ${errors.phone ? 'is-invalid' : ''}`}
                    placeholder="9876543210"
                    {...register('phone', {
                      required: 'Phone number is required',
                      pattern: { value: /^[6-9]\d{9}$/, message: 'Enter a valid 10 digit mobile number starting with 6-9' },
                      validate: (val) => !/^(\d)\1{9}$/.test(val) || 'Invalid phone number (repeated digits not allowed)',
                    })}
                  />
                  {errors.phone && <div className="invalid-feedback">{errors.phone.message}</div>}
                </div>

                <div className="col-md-6">
                  <label className="form-label small fw-semibold">Pincode</label>
                  <input
                    className={`form-control ${errors.pincode ? 'is-invalid' : ''}`}
                    placeholder="411001"
                    {...register('pincode', {
                      required: 'Pincode is required',
                      pattern: { value: /^\d{6}$/, message: 'Enter a valid 6 digit pincode' },
                    })}
                  />
                  {errors.pincode && <div className="invalid-feedback">{errors.pincode.message}</div>}
                </div>

                <div className="col-12">
                  <label className="form-label small fw-semibold">Address</label>
                  <input
                    className={`form-control ${errors.address ? 'is-invalid' : ''}`}
                    placeholder="Plot 42, Industrial Area Phase 1"
                    {...register('address', {
                      required: 'Address is required',
                      minLength: { value: 10, message: 'Address must be at least 10 characters' },
                      maxLength: { value: 250, message: 'Address must not exceed 250 characters' },
                      validate: (val) => {
                        if (/^\d+$/.test(val.trim())) return 'Address cannot be numbers only';
                        if (/^[^a-zA-Z0-9]+$/.test(val.trim())) return 'Address cannot be special characters only';
                        return true;
                      },
                    })}
                  />
                  {errors.address && <div className="invalid-feedback">{errors.address.message}</div>}
                </div>

                <div className="col-md-4">
                  <label className="form-label small fw-semibold">State</label>
                  <select
                    className={`form-select ${errors.state ? 'is-invalid' : ''}`}
                    {...register('state', { required: 'State is required' })}
                    onChange={handleStateChange}
                  >
                    {INDIAN_STATES.map((s) => (
                      <option key={s} value={s}>{s}</option>
                    ))}
                  </select>
                  {errors.state && <div className="invalid-feedback">{errors.state.message}</div>}
                </div>

                <div className="col-md-4">
                  <label className="form-label small fw-semibold">City</label>
                  <select
                    className={`form-select ${errors.city ? 'is-invalid' : ''}`}
                    {...register('city', { required: 'City is required' })}
                  >
                    {availableCities.map((c) => (
                      <option key={c} value={c}>{c}</option>
                    ))}
                  </select>
                  {errors.city && <div className="invalid-feedback">{errors.city.message}</div>}
                </div>

                <div className="col-md-4">
                  <label className="form-label small fw-semibold">Region</label>
                  <select
                    className="form-select"
                    {...register('regionName')}
                  >
                    <option value="Pune Region">Pune Region</option>
                    <option value="Mumbai Region">Mumbai Region</option>
                    <option value="Kolhapur Region">Kolhapur Region</option>
                    <option value="Nagpur Region">Nagpur Region</option>
                    <option value="Nashik Region">Nashik Region</option>
                    <option value="Satara Region">Satara Region</option>
                  </select>
                </div>
              </div>

              <button
                type="submit"
                className="btn btn-success w-100 py-2 fw-semibold mt-4 d-flex align-items-center justify-content-center gap-2"
                disabled={!isValid || submitting}
              >
                {submitting && <span className="spinner-border spinner-border-sm" />}
                Submit application
              </button>

              <p className="small text-muted text-center mt-3 mb-0">
                Already approved? <Link to="/login" className="fw-semibold">Sign in</Link>
              </p>
            </form>
          </div>
        )}
      </div>
    </div>
  );
};

export default PartnerApplicationForm;
