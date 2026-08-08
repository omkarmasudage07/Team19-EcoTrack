import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { FiMap } from 'react-icons/fi';
import PageHeader from '../../components/common/PageHeader';
import LocationPicker from '../../components/common/LocationPicker';
import pickupService, { wasteCategoryService } from '../../services/pickupService';
import { getErrorMessage } from '../../services/api';
import { useToast } from '../../context/ToastContext';

import { INDIAN_STATES, getCitiesForState } from '../../constants/indianLocations';

const TIME_SLOTS = ['9AM - 11AM', '11AM - 1PM', '1PM - 3PM', '3PM - 5PM', '5PM - 7PM'];

const SchedulePickup = () => {
  const { register, handleSubmit, setValue, watch, formState: { errors, isValid } } = useForm({
    mode: 'onChange',
    defaultValues: {
      pickupState: 'Maharashtra',
      pickupCity: 'Pune',
      regionName: 'Pune Region'
    }
  });

  const selectedState = watch('pickupState');
  const availableCities = getCitiesForState(selectedState);

  const [categories, setCategories] = useState([]);
  const [submitting, setSubmitting] = useState(false);
  const [serverError, setServerError] = useState('');
  const [showMap, setShowMap] = useState(false);
  const [pickedLocation, setPickedLocation] = useState(null);
  const navigate = useNavigate();
  const { showToast } = useToast();

  useEffect(() => {
    wasteCategoryService.getActive().then(setCategories).catch(() => setCategories([]));
  }, []);

  const handleStateChange = (e) => {
    const newState = e.target.value;
    setValue('pickupState', newState);
    const cities = getCitiesForState(newState);
    setValue('pickupCity', cities.length > 0 ? cities[0] : '');
  };

  const handleLocationSelected = ({ latitude, longitude, address }) => {
    setPickedLocation({ latitude, longitude });
    if (address) {
      setValue('pickupAddress', address, { shouldValidate: true });
    }
  };

  const onSubmit = async (values) => {
    setServerError('');
    setSubmitting(true);
    try {
      await pickupService.schedule({
        ...values,
        wasteCategoryId: Number(values.wasteCategoryId),
        latitude: pickedLocation?.latitude ?? null,
        longitude: pickedLocation?.longitude ?? null,
        imageUrls: [],
      });
      showToast('Pickup scheduled successfully!', 'success');
      navigate('/citizen/pickups');
    } catch (err) {
      setServerError(getErrorMessage(err, 'Could not schedule your pickup. Please check your details.'));
    } finally {
      setSubmitting(false);
    }
  };

  const todayStr = new Date().toISOString().split('T')[0];

  return (
    <div>
      <PageHeader title="Schedule a Pickup" subtitle="Tell us what you'd like recycled and when works for you" />

      <div className="card border-0 shadow-sm" style={{ maxWidth: 720 }}>
        <div className="card-body p-4">
          {serverError && <div className="alert alert-danger small py-2 mb-3">{serverError}</div>}

          <form onSubmit={handleSubmit(onSubmit)} noValidate>
            <div className="row g-3 mb-3">
              <div className="col-md-8">
                <label className="form-label small fw-semibold">Waste category</label>
                <select
                  className={`form-select ${errors.wasteCategoryId ? 'is-invalid' : ''}`}
                  {...register('wasteCategoryId', { required: 'Please select a waste category' })}
                >
                  <option value="">Select a category...</option>
                  {categories.map((c) => (
                    <option key={c.id} value={c.id}>{c.name}</option>
                  ))}
                </select>
                {errors.wasteCategoryId && <div className="invalid-feedback">{errors.wasteCategoryId.message}</div>}
              </div>

              <div className="col-md-4">
                <label className="form-label small fw-semibold">Estimated Weight (kg)</label>
                <input
                  type="number"
                  step="0.1"
                  className={`form-control ${errors.estimatedWeight ? 'is-invalid' : ''}`}
                  placeholder="e.g. 5.5"
                  {...register('estimatedWeight', {
                    required: 'Weight is required',
                    min: { value: 0.1, message: 'Must be greater than 0' },
                    max: { value: 500, message: 'Maximum 500 kg per request' },
                  })}
                />
                {errors.estimatedWeight && <div className="invalid-feedback">{errors.estimatedWeight.message}</div>}
              </div>
            </div>

            <div className="mb-2 d-flex justify-content-between align-items-center">
              <label className="form-label small fw-semibold mb-0">Pickup address</label>
              <button
                type="button"
                className="btn btn-sm btn-outline-success d-inline-flex align-items-center gap-1"
                onClick={() => setShowMap((s) => !s)}
              >
                <FiMap size={14} /> {showMap ? 'Hide map' : 'Pick on map'}
              </button>
            </div>

            {showMap && (
              <div className="mb-3">
                <LocationPicker
                  latitude={pickedLocation?.latitude}
                  longitude={pickedLocation?.longitude}
                  onLocationSelected={handleLocationSelected}
                />
              </div>
            )}

            <div className="mb-3">
              <input
                className={`form-control ${errors.pickupAddress ? 'is-invalid' : ''}`}
                placeholder="Flat / Street / Locality"
                {...register('pickupAddress', {
                  required: 'Pickup address is required',
                  minLength: { value: 10, message: 'Address must be at least 10 characters' },
                  maxLength: { value: 250, message: 'Address must not exceed 250 characters' },
                  validate: (val) => {
                    if (/^\d+$/.test(val.trim())) return 'Address cannot be numbers only';
                    if (/^[^a-zA-Z0-9]+$/.test(val.trim())) return 'Address cannot be special characters only';
                    return true;
                  },
                })}
              />
              {errors.pickupAddress && <div className="invalid-feedback">{errors.pickupAddress.message}</div>}
            </div>

            <div className="row g-3 mb-3">
              <div className="col-md-4">
                <label className="form-label small fw-semibold">State</label>
                <select
                  className={`form-select ${errors.pickupState ? 'is-invalid' : ''}`}
                  {...register('pickupState', { required: 'State is required' })}
                  onChange={handleStateChange}
                >
                  {INDIAN_STATES.map((s) => (
                    <option key={s} value={s}>{s}</option>
                  ))}
                </select>
                {errors.pickupState && <div className="invalid-feedback">{errors.pickupState.message}</div>}
              </div>

              <div className="col-md-4">
                <label className="form-label small fw-semibold">City</label>
                <select
                  className={`form-select ${errors.pickupCity ? 'is-invalid' : ''}`}
                  {...register('pickupCity', { required: 'City is required' })}
                >
                  {availableCities.map((c) => (
                    <option key={c} value={c}>{c}</option>
                  ))}
                </select>
                {errors.pickupCity && <div className="invalid-feedback">{errors.pickupCity.message}</div>}
              </div>

              <div className="col-md-4">
                <label className="form-label small fw-semibold">Pincode</label>
                <input
                  className={`form-control ${errors.pickupPincode ? 'is-invalid' : ''}`}
                  {...register('pickupPincode', {
                    required: 'Pincode is required',
                    pattern: { value: /^\d{6}$/, message: 'Enter a valid 6 digit pincode' },
                  })}
                />
                {errors.pickupPincode && <div className="invalid-feedback">{errors.pickupPincode.message}</div>}
              </div>
            </div>

            <div className="row g-3 mb-3">
              <div className="col-md-6">
                <label className="form-label small fw-semibold">Preferred date</label>
                <input
                  type="date"
                  min={todayStr}
                  className={`form-control ${errors.pickupDate ? 'is-invalid' : ''}`}
                  {...register('pickupDate', { required: 'Pickup date is required' })}
                />
                {errors.pickupDate && <div className="invalid-feedback">{errors.pickupDate.message}</div>}
              </div>
              <div className="col-md-6">
                <label className="form-label small fw-semibold">Preferred time slot</label>
                <select
                  className={`form-select ${errors.pickupTimeSlot ? 'is-invalid' : ''}`}
                  {...register('pickupTimeSlot', { required: 'Please select a time slot' })}
                >
                  <option value="">Select a slot...</option>
                  {TIME_SLOTS.map((slot) => (
                    <option key={slot} value={slot}>{slot}</option>
                  ))}
                </select>
                {errors.pickupTimeSlot && <div className="invalid-feedback">{errors.pickupTimeSlot.message}</div>}
              </div>
            </div>

            <div className="mb-4">
              <label className="form-label small fw-semibold">Notes (optional)</label>
              <textarea
                className="form-control"
                rows={3}
                placeholder="Anything the Recycler should know"
                {...register('notes', { maxLength: { value: 500, message: 'Notes must be under 500 characters' } })}
              />
            </div>

            <button
              type="submit"
              className="btn btn-success px-4 fw-semibold d-inline-flex align-items-center gap-2"
              disabled={!isValid || submitting}
            >
              {submitting && <span className="spinner-border spinner-border-sm" />}
              Schedule Pickup
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default SchedulePickup;
