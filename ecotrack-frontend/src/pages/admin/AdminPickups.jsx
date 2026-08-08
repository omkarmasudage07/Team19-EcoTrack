import React, { useEffect, useState, useCallback } from 'react';
import { FiTruck, FiMap, FiX } from 'react-icons/fi';
import PageHeader from '../../components/common/PageHeader';
import StatusBadge from '../../components/common/StatusBadge';
import { LoadingSpinner, EmptyState, ErrorState } from '../../components/common/Feedback';
import PickupMap from '../../components/common/PickupMap';
import pickupService from '../../services/pickupService';

const STATUS_FILTERS = ['ALL', 'PENDING', 'ACCEPTED', 'ON_THE_WAY', 'COLLECTED', 'PROCESSING', 'COMPLETED', 'CANCELLED'];

const AdminPickups = () => {
  const [pickups, setPickups] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [mapModalPickup, setMapModalPickup] = useState(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError(false);
    try {
      const params = { size: 30, sort: 'createdAt,desc' };
      if (statusFilter !== 'ALL') params.status = statusFilter;
      const data = await pickupService.getAll(params);
      setPickups(data.content || []);
    } catch (e) {
      setError(true);
    } finally {
      setLoading(false);
    }
  }, [statusFilter]);

  useEffect(() => { load(); }, [load]);

  return (
    <div>
      <PageHeader title="Pickups" subtitle="Every pickup across the platform" />

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

      <div className="card border-0 shadow-sm">
        <div className="card-body p-0">
          {loading ? (
            <LoadingSpinner />
          ) : error ? (
            <ErrorState />
          ) : pickups.length === 0 ? (
            <EmptyState icon={FiTruck} title="No pickups found" />
          ) : (
            <div className="table-responsive">
              <table className="table align-middle mb-0">
                <thead>
                  <tr className="text-muted small text-uppercase">
                    <th className="ps-3">Pickup #</th>
                    <th>Category</th>
                    <th>Date</th>
                    <th>Citizen ID</th>
                    <th>Recycler ID</th>
                    <th>Status</th>
                    <th className="pe-3"></th>
                  </tr>
                </thead>
                <tbody>
                  {pickups.map((p) => (
                    <tr key={p.id}>
                      <td className="ps-3 small fw-semibold">{p.pickupNumber}</td>
                      <td className="small">{p.wasteCategoryName}</td>
                      <td className="small">{p.pickupDate}</td>
                      <td className="small text-muted">#{p.citizenId}</td>
                      <td className="small text-muted">{p.recyclerId ? `#${p.recyclerId}` : '—'}</td>
                      <td><StatusBadge status={p.status} /></td>
                      <td className="pe-3 text-end">
                        {p.latitude != null && (
                          <button
                            className="btn btn-sm btn-light border"
                            title="View pickup location"
                            onClick={() => setMapModalPickup(p)}
                          >
                            <FiMap size={14} />
                          </button>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>

      {mapModalPickup && (
        <div
          className="position-fixed top-0 start-0 w-100 h-100 d-flex align-items-center justify-content-center"
          style={{ background: 'rgba(15,23,42,0.4)', zIndex: 100 }}
          onClick={() => setMapModalPickup(null)}
        >
          <div className="card border-0 shadow-lg" style={{ width: 480 }} onClick={(e) => e.stopPropagation()}>
            <div className="card-body p-4">
              <div className="d-flex justify-content-between align-items-start mb-3">
                <h6 className="fw-bold mb-0">{mapModalPickup.pickupNumber}</h6>
                <button className="btn btn-sm btn-light" onClick={() => setMapModalPickup(null)}>
                  <FiX size={16} />
                </button>
              </div>
              <PickupMap
                latitude={mapModalPickup.latitude}
                longitude={mapModalPickup.longitude}
                label={mapModalPickup.pickupNumber}
                height={280}
              />
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminPickups;
