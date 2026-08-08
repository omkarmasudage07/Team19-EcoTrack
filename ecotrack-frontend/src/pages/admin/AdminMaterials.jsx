import React, { useEffect, useState, useCallback } from 'react';
import { FiBox, FiSearch } from 'react-icons/fi';
import PageHeader from '../../components/common/PageHeader';
import StatusBadge from '../../components/common/StatusBadge';
import { LoadingSpinner, EmptyState, ErrorState } from '../../components/common/Feedback';
import { materialService } from '../../services/materialService';

const AdminMaterials = () => {
  const [materials, setMaterials] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [search, setSearch] = useState('');

  const load = useCallback(async () => {
    setLoading(true);
    setError(false);
    try {
      const params = { size: 30 };
      if (search) params.materialName = search;
      const data = await materialService.browse(params);
      setMaterials(data.content || []);
    } catch (e) {
      setError(true);
    } finally {
      setLoading(false);
    }
  }, [search]);

  useEffect(() => {
    const timeout = setTimeout(load, 300);
    return () => clearTimeout(timeout);
  }, [load]);

  return (
    <div>
      <PageHeader title="Materials" subtitle="Every material listing on the marketplace" />

      <div className="position-relative mb-3" style={{ maxWidth: 320 }}>
        <FiSearch size={16} className="position-absolute text-muted" style={{ left: 12, top: 11 }} />
        <input
          className="form-control ps-4"
          placeholder="Search materials..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </div>

      <div className="card border-0 shadow-sm">
        <div className="card-body p-0">
          {loading ? (
            <LoadingSpinner />
          ) : error ? (
            <ErrorState />
          ) : materials.length === 0 ? (
            <EmptyState icon={FiBox} title="No materials found" />
          ) : (
            <div className="table-responsive">
              <table className="table align-middle mb-0">
                <thead>
                  <tr className="text-muted small text-uppercase">
                    <th className="ps-3">Material</th>
                    <th>Category</th>
                    <th>Recycler ID</th>
                    <th>Quantity</th>
                    <th>Price / Unit</th>
                    <th className="pe-3">Status</th>
                  </tr>
                </thead>
                <tbody>
                  {materials.map((m) => (
                    <tr key={m.id}>
                      <td className="ps-3 small fw-semibold">{m.materialName}</td>
                      <td className="small">{m.categoryName}</td>
                      <td className="small text-muted">#{m.recyclerId}</td>
                      <td className="small">{m.quantity} {m.unit}</td>
                      <td className="small">₹{m.pricePerUnit}</td>
                      <td className="pe-3"><StatusBadge status={m.availabilityStatus} /></td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default AdminMaterials;
