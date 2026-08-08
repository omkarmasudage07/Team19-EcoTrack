import React, { useEffect, useState, useCallback } from 'react';
import { FiPackage, FiMapPin, FiCalendar, FiMap } from 'react-icons/fi';
import PageHeader from '../../components/common/PageHeader';
import StatusBadge from '../../components/common/StatusBadge';
import { LoadingSpinner, EmptyState, ErrorState } from '../../components/common/Feedback';
import PickupMap from '../../components/common/PickupMap';
import pickupService from '../../services/pickupService';
import { getErrorMessage } from '../../services/api';
import { useToast } from '../../context/ToastContext';

const NEXT_STATUS = {
  ACCEPTED: 'ON_THE_WAY',
  ON_THE_WAY: 'COLLECTED',
  COLLECTED: 'PROCESSING',
  PROCESSING: 'COMPLETED',
};

const NEXT_LABEL = {
  ACCEPTED: 'Mark On The Way',
  ON_THE_WAY: 'Mark Collected',
  COLLECTED: 'Mark Processing',
  PROCESSING: 'Mark Completed',
};

const STATUS_FILTERS = ['ALL', 'ACCEPTED', 'ON_THE_WAY', 'COLLECTED', 'PROCESSING', 'COMPLETED'];

const AssignedPickups = () => {
  const [pickups, setPickups] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [actingId, setActingId] = useState(null);
  const [expandedMapId, setExpandedMapId] = useState(null);
  const { showToast } = useToast();

  const load = useCallback(async () => {
    setLoading(true);
    setError(false);
    try {
      const params = { size: 20, sort: 'createdAt,desc' };
      if (statusFilter !== 'ALL') params.status = statusFilter;
      const data = await pickupService.getAssigned(params);
      setPickups(data.content || []);
    } catch (e) {
      setError(true);
    } finally {
      setLoading(false);
    }
  }, [statusFilter]);

  useEffect(() => { load(); }, [load]);

  const handleAdvance = async (pickup) => {
    const nextStatus = NEXT_STATUS[pickup.status];
    if (!nextStatus) return;
    setActingId(pickup.id);
    try {
      await pickupService.updateStatus(pickup.id, { status: nextStatus });
      showToast(`Pickup marked ${nextStatus.replace(/_/g, ' ').toLowerCase()}`, 'success');
      load();
    } catch (err) {
      showToast(getErrorMessage(err), 'error');
    } finally {
      setActingId(null);
    }
  };

  return (
    <div>
      <PageHeader title="My Pickups" subtitle="Pickups you've accepted and are working on" />

      <div className="d-flex gap-2 flex-wrap mb-3">
        {STATUS_FILTERS.map((s) => (
          <button
            key={s}
            className={`btn btn-sm ${statusFilter === s ? 'btn-success' : 'btn-outline-secondary'}`}
            onClick={() => setStatusFilter(s)}
          >
            {s.replace(/_/g, ' ')}
          </button>
        ))}
      </div>

      {loading ? (
        <LoadingSpinner />
      ) : error ? (
        <ErrorState />
      ) : pickups.length === 0 ? (
        <div className="card border-0 shadow-sm">
          <EmptyState icon={FiPackage} title="No pickups here" message="Accept a pickup from the Available Pickups tab to see it here." />
        </div>
      ) : (
        <div className="row g-3">
          {pickups.map((p) => (
            <div className="col-md-6 col-lg-4" key={p.id}>
              <div className="card border-0 shadow-sm h-100">
                <div className="card-body">
                  <div className="d-flex justify-content-between align-items-start mb-2">
                    <span className="fw-semibold small">{p.pickupNumber}</span>
                    <StatusBadge status={p.status} />
                  </div>
                  <div className="small text-muted d-flex align-items-start gap-1 mb-1">
                    <FiMapPin size={14} className="mt-1 flex-shrink-0" />
                    <span>{p.pickupAddress}{p.pickupCity ? `, ${p.pickupCity}` : ''}</span>
                  </div>
                  <div className="small text-muted d-flex align-items-center gap-1 mb-3">
                    <FiCalendar size={14} className="flex-shrink-0" />
                    <span>{p.pickupDate} · {p.pickupTimeSlot}</span>
                  </div>

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

                  {NEXT_STATUS[p.status] ? (
                    <button
                      className="btn btn-success btn-sm w-100"
                      disabled={actingId === p.id}
                      onClick={() => handleAdvance(p)}
                    >
                      {actingId === p.id ? 'Updating...' : NEXT_LABEL[p.status]}
                    </button>
                  ) : (
                    <div className="text-center small text-muted">
                      {p.status === 'COMPLETED' ? 'Pickup complete' : 'No further action'}
                    </div>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default AssignedPickups;
