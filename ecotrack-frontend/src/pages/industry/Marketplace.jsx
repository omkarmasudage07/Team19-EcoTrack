import React, { useEffect, useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiShoppingCart, FiSearch } from 'react-icons/fi';
import PageHeader from '../../components/common/PageHeader';
import { LoadingSpinner, EmptyState, ErrorState } from '../../components/common/Feedback';
import { materialService, materialCategoryService } from '../../services/materialService';

const Marketplace = () => {
  const [materials, setMaterials] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [search, setSearch] = useState('');
  const [categoryId, setCategoryId] = useState('');
  const navigate = useNavigate();

  const load = useCallback(async () => {
    setLoading(true);
    setError(false);
    try {
      const params = { size: 24 };
      if (search) params.materialName = search;
      if (categoryId) params.categoryId = categoryId;
      const data = await materialService.browse(params);
      setMaterials(data.content || []);
    } catch (e) {
      setError(true);
    } finally {
      setLoading(false);
    }
  }, [search, categoryId]);

  useEffect(() => {
    materialCategoryService.getActive().then(setCategories).catch(() => setCategories([]));
  }, []);

  useEffect(() => {
    const timeout = setTimeout(load, 300); // debounce search typing
    return () => clearTimeout(timeout);
  }, [load]);

  return (
    <div>
      <PageHeader title="Marketplace" subtitle="Browse verified recovered materials from EcoTrack Recycler Partners" />

      <div className="d-flex flex-wrap gap-2 mb-4">
        <div className="position-relative" style={{ minWidth: 260 }}>
          <FiSearch size={16} className="position-absolute text-muted" style={{ left: 12, top: 11 }} />
          <input
            className="form-control ps-4"
            placeholder="Search materials..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>
        <select className="form-select" style={{ maxWidth: 220 }} value={categoryId} onChange={(e) => setCategoryId(e.target.value)}>
          <option value="">All categories</option>
          {categories.map((c) => (
            <option key={c.id} value={c.id}>{c.name}</option>
          ))}
        </select>
      </div>

      {loading ? (
        <LoadingSpinner />
      ) : error ? (
        <ErrorState />
      ) : materials.length === 0 ? (
        <div className="card border-0 shadow-sm">
          <EmptyState icon={FiShoppingCart} title="No materials found" message="Try a different search term or category." />
        </div>
      ) : (
        <div className="row g-3">
          {materials.map((m) => (
            <div className="col-md-6 col-lg-4" key={m.id}>
              <div
                className="card border-0 shadow-sm h-100 et-card-hover"
                role="button"
                onClick={() => navigate(`/industry/marketplace/${m.id}`)}
              >
                <div className="card-body d-flex flex-column">
                  <div className="d-flex justify-content-between align-items-start mb-2">
                    <h6 className="fw-bold mb-0">{m.materialName}</h6>
                    <span className="et-badge et-badge-slate"><span className="et-badge-dot" />{m.categoryName}</span>
                  </div>
                  {m.purity && <div className="small text-muted mb-1">Purity: {m.purity}</div>}
                  <div className="small text-muted mb-3">
                    {m.quantity} {m.unit} available
                    {m.warehouseLocation && ` · ${m.warehouseLocation}`}
                  </div>
                  <div className="fs-5 fw-bold mb-3">₹{m.pricePerUnit} <span className="fs-6 fw-normal text-muted">/ {m.unit}</span></div>
                  <button
                    className="btn btn-success btn-sm mt-auto"
                    onClick={(e) => {
                      e.stopPropagation();
                      navigate(`/industry/marketplace/${m.id}`);
                    }}
                  >
                    View Details
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default Marketplace;
