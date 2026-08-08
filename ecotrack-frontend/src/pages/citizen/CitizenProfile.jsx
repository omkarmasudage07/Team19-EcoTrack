import React, { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import PageHeader from '../../components/common/PageHeader';
import { LoadingSpinner, ErrorState } from '../../components/common/Feedback';
import { citizenService } from '../../services/userService';
import { getErrorMessage } from '../../services/api';
import { useToast } from '../../context/ToastContext';

const CitizenProfile = () => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const { showToast } = useToast();
  const { register, handleSubmit, reset, formState: { errors } } = useForm();

  useEffect(() => {
    citizenService.getMyProfile()
      .then((data) => reset(data))
      .catch(() => setError(true))
      .finally(() => setLoading(false));
  }, [reset]);

  const onSubmit = async (values) => {
    setSubmitting(true);
    try {
      await citizenService.updateMyProfile(values);
      showToast('Profile updated successfully', 'success');
    } catch (err) {
      showToast(getErrorMessage(err), 'error');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <LoadingSpinner />;
  if (error) return <ErrorState />;

  return (
    <div>
      <PageHeader title="My Profile" subtitle="Keep your contact and address details up to date" />

      <div className="card border-0 shadow-sm" style={{ maxWidth: 640 }}>
        <div className="card-body p-4">
          <form onSubmit={handleSubmit(onSubmit)} noValidate>
            <div className="row g-3">
              <div className="col-md-6">
                <label className="form-label small fw-semibold">Full name</label>
                <input
                  className={`form-control ${errors.fullName ? 'is-invalid' : ''}`}
                  {...register('fullName', { required: 'Full name is required' })}
                />
                {errors.fullName && <div className="invalid-feedback">{errors.fullName.message}</div>}
              </div>

              <div className="col-md-6">
                <label className="form-label small fw-semibold">Mobile number</label>
                <input
                  className={`form-control ${errors.phone ? 'is-invalid' : ''}`}
                  {...register('phone', {
                    required: 'Mobile number is required',
                    pattern: { value: /^[6-9]\d{9}$/, message: 'Enter a valid 10 digit mobile number' },
                  })}
                />
                {errors.phone && <div className="invalid-feedback">{errors.phone.message}</div>}
              </div>

              <div className="col-12">
                <label className="form-label small fw-semibold">Address</label>
                <input className="form-control" {...register('address')} />
              </div>

              <div className="col-md-4">
                <label className="form-label small fw-semibold">City</label>
                <input className="form-control" {...register('city')} />
              </div>
              <div className="col-md-4">
                <label className="form-label small fw-semibold">State</label>
                <input className="form-control" {...register('state')} />
              </div>
              <div className="col-md-4">
                <label className="form-label small fw-semibold">Pincode</label>
                <input
                  className={`form-control ${errors.pincode ? 'is-invalid' : ''}`}
                  {...register('pincode', { pattern: { value: /^\d{6}$/, message: 'Enter a valid 6 digit pincode' } })}
                />
                {errors.pincode && <div className="invalid-feedback">{errors.pincode.message}</div>}
              </div>
            </div>

            <button type="submit" className="btn btn-success mt-4 d-inline-flex align-items-center gap-2" disabled={submitting}>
              {submitting && <span className="spinner-border spinner-border-sm" />}
              Save Changes
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default CitizenProfile;
