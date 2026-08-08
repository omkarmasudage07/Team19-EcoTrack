import React, { useEffect, useState, useCallback } from 'react';
import { FiTruck, FiMapPin, FiCalendar, FiMap, FiFilter, FiNavigation } from 'react-icons/fi';
import PageHeader from '../../components/common/PageHeader';
import { LoadingSpinner, EmptyState, ErrorState } from '../../components/common/Feedback';
import PickupMap from '../../components/common/PickupMap';
import pickupService from '../../services/pickupService';
import { getErrorMessage } from '../../services/api';
import { useToast } from '../../context/ToastContext';

const REGIONS = [
  'All Regions',
  'Pune Region',
  'Mumbai Region',
  'Kolhapur Region',
  'Nagpur Region',
  'Nashik Region',
  'Satara Region'
];

import { recyclerService } from '../../services/userService';

const AvailablePickups = () => {
  const [pickups, setPickups] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [userRegion, setUserRegion] = useState(null);
  const [selectedRegion, setSelectedRegion] = useState('My Region');
  const [actingId, setActingId] = useState(null);
  const [expandedMapId, setExpandedMapId] = useState(null);
  const { showToast } = useToast();

  const loadProfileAndPickups = useCallback(async () => {
    setLoading(true);
    setError(false);
    try {
      let regName = userRegion;
      if (!regName) {
        try {
          const profile = await recyclerService.getMyProfile();
          if (profile && profile.regionName) {
            regName = profile.regionName;
            setUserRegion(profile.regionName);
          }
        } catch (e) {
          // fallback
        }
      }

      const params = { size: 20, sort: 'createdAt,asc' };
      const activeFilterRegion = selectedRegion === 'My Region' ? (regName || 'Pune Region') : selectedRegion;
      if (activeFilterRegion && activeFilterRegion !== 'All Regions') {
        params.regionName = activeFilterRegion;
      }

      const data = await pickupService.getAvailable(params);
      setPickups(data.content || []);
    } catch (e) {
      setError(true);
    } finally {
      setLoading(false);
    }
  }, [selectedRegion, userRegion]);

  useEffect(() => { loadProfileAndPickups(); }, [loadProfileAndPickups]);

  const handleAccept = async (id) => {
    setActingId(id);
    try {
      await pickupService.accept(id);
      showToast('Pickup accepted - it now appears in "My Pickups"', 'success');
      setPickups((prev) => prev.filter((p) => p.id !== id));
    } catch (err) {
      showToast(getErrorMessage(err), 'error');
    } finally {
      setActingId(null);
    }
  };

  const handleReject = async (id) => {
    setActingId(id);
    try {
      await pickupService.reject(id);
      setPickups((prev) => prev.filter((p) => p.id !== id));
    } catch (err) {
      showToast(getErrorMessage(err), 'error');
    } finally {
      setActingId(null);
    }
  };

  return (
    <div>
      <PageHeader
        title="Available Pickups"
        subtitle="Pickups in your region waiting for Recycler acceptance"
        action={
          <div className="d-flex align-items-center gap-2">
            <FiFilter size={16} className="text-muted" />
            <select
              className="form-select form-select-sm w-auto"
              value={selectedRegion}
              onChange={(e) => setSelectedRegion(e.target.value)}
            >
              {REGIONS.map((r) => (
                <option key={r} value={r}>{r}</option>
              ))}
            </select>
          </div>
        }
      />

      {loading ? (
        <LoadingSpinner />
      ) : error ? (
        <ErrorState />
      ) : pickups.length === 0 ? (
        <div className="card border-0 shadow-sm">
          <EmptyState
            icon={FiTruck}
            title="No available pickups right now"
            message={selectedRegion !== 'All Regions' ? `No pending pickups found in ${selectedRegion}. Try switching region filter.` : "Check back soon - new requests appear here as Citizens schedule them."}
          />
        </div>
      ) : (
        <div className="row g-3">
          {pickups.map((p) => (
            <div className="col-md-6 col-lg-4" key={p.id}>
              <div className="card border-0 shadow-sm h-100 et-card-hover">
                <div className="card-body">
                  <div className="d-flex justify-content-between align-items-start mb-2">
                    <span className="fw-semibold small">{p.pickupNumber}</span>
                    <div className="d-flex gap-1">
                      <span className="et-badge et-badge-slate">
                        <span className="et-badge-dot" />
                        {p.wasteCategoryName}
                      </span>
                      {p.regionName && (
                        <span className="badge bg-light text-dark border small fw-normal">
                          {p.regionName}
                        </span>
                      )}
                    </div>
                  </div>
                  <div className="small text-muted d-flex align-items-start gap-1 mb-1">
                    <FiMapPin size={14} className="mt-1 flex-shrink-0" />
                    <span>{p.pickupAddress}{p.pickupCity ? `, ${p.pickupCity}` : ''}</span>
                  </div>
                  <div className="small text-muted d-flex align-items-center gap-1 mb-3">
                    <FiCalendar size={14} className="flex-shrink-0" />
                    <span>{p.pickupDate} · {p.pickupTimeSlot}</span>
                    {p.distanceKm != null && (
                      <span className="badge bg-success-subtle text-success ms-auto">
                        <FiNavigation size={10} className="me-1" />
                        {p.distanceKm} km away
                      </span>
                    )}
                  </div>
                  {p.notes && <p className="small text-muted fst-italic mb-3">"{p.notes}"</p>}
                  {p.latitude != null && (
                    <button
                      type="button"
                      className="btn btn-sm btn-outline-secondary d-inline-flex align-items-center gap-1 mb-3"
                      onClick={() => setExpandedMapId(expandedMapId === p.id ? null : p.id)}
                    >
                      <FiMap size={14} /> {expandedMapId === p.id ? 'Hide map' : 'View on map'}
                    </button>
                  )}
                  {expandedMapId === p.id && (
                    <div className="mb-3">
                      <PickupMap latitude={p.latitude} longitude={p.longitude} label={p.pickupNumber} height={200} />
                    </div>
                  )}
                  <div className="d-flex gap-2">
                    <button
                      className="btn btn-success btn-sm flex-fill"
                      disabled={actingId === p.id}
                      onClick={() => handleAccept(p.id)}
                    >
                      Accept
                    </button>
                    <button
                      className="btn btn-outline-secondary btn-sm flex-fill"
                      disabled={actingId === p.id}
                      onClick={() => handleReject(p.id)}
                    >
                      Decline
                    </button>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default AvailablePickups;
