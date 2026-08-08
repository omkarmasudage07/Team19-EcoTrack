import React, { useEffect, useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { FiBox, FiMapPin, FiArrowLeft } from 'react-icons/fi';
import PageHeader from '../../components/common/PageHeader';
import { LoadingSpinner, ErrorState } from '../../components/common/Feedback';
import { materialService } from '../../services/materialService';
import { recyclerService } from '../../services/userService';

const MaterialDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [material, setMaterial] = useState(null);
  const [recyclerName, setRecyclerName] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [quantity, setQuantity] = useState('');

  useEffect(() => {
    let active = true;
    setLoading(true);
    setError(false);
    materialService.getDetail(id)
      .then((data) => {
        if (!active) return;
        setMaterial(data);
        // The Recycler's company name isn't on the Material response itself
        // (Material only stores the owning userId) - a second, small
        // lookup fills it in. If it fails we just show "Recycler Partner"
        // instead of blocking the whole page.
        recyclerService.getByUserId(data.recyclerId)
          .then((r) => active && setRecyclerName(r.companyName))
          .catch(() => active && setRecyclerName('Recycler Partner'));
      })
      .catch(() => active && setError(true))
      .finally(() => active && setLoading(false));
    return () => { active = false; };
  }, [id]);

  if (loading) return <LoadingSpinner label="Loading material..." />;
  if (error || !material) return <ErrorState message="This material could not be found." />;

  const handleBuyNow = () => {
    const qty = Number(quantity);
    if (!qty || qty <= 0) return;
    navigate(`/industry/checkout/${material.id}?quantity=${qty}`);
  };

  return (
    <div>
      <button className="btn btn-sm btn-light border d-inline-flex align-items-center gap-1 mb-3" onClick={() => navigate(-1)}>
        <FiArrowLeft size={14} /> Back to Marketplace
      </button>

      <PageHeader title={material.materialName} subtitle={`Listed by ${recyclerName || 'a Recycler Partner'}`} />

      <div className="row g-4">
        <div className="col-lg-5">
          <div
            className="card border-0 shadow-sm d-flex align-items-center justify-content-center"
            style={{ height: 280, background: 'var(--color-primary-light)' }}
          >
            {material.imageUrls && material.imageUrls.length > 0 ? (
              <img src={material.imageUrls[0]} alt={material.materialName} className="w-100 h-100" style={{ objectFit: 'cover' }} />
            ) : (
              <FiBox size={64} style={{ color: 'var(--color-primary)' }} />
            )}
          </div>
        </div>

        <div className="col-lg-7">
          <div className="card border-0 shadow-sm">
            <div className="card-body p-4">
              <div className="d-flex justify-content-between align-items-start mb-3">
                <span className="et-badge et-badge-slate"><span className="et-badge-dot" />{material.categoryName}</span>
                <div className="fs-3 fw-bold">
                  ₹{material.pricePerUnit} <span className="fs-6 fw-normal text-muted">/ {material.unit}</span>
                </div>
              </div>

              <div className="row g-3 mb-3">
                {material.purity && (
                  <div className="col-6">
                    <div className="small text-muted mb-1">Purity</div>
                    <div className="fw-semibold small">{material.purity}</div>
                  </div>
                )}
                <div className="col-6">
                  <div className="small text-muted mb-1">Available Quantity</div>
                  <div className="fw-semibold small">{material.quantity} {material.unit}</div>
                </div>
                <div className="col-6">
                  <div className="small text-muted mb-1">Recycler</div>
                  <div className="fw-semibold small">{recyclerName || '—'}</div>
                </div>
                {material.warehouseLocation && (
                  <div className="col-6">
                    <div className="small text-muted mb-1">Location</div>
                    <div className="fw-semibold small d-flex align-items-center gap-1">
                      <FiMapPin size={12} /> {material.warehouseLocation}
                    </div>
                  </div>
                )}
              </div>

              {material.description && (
                <div className="mb-4">
                  <div className="small text-muted mb-1">Description</div>
                  <p className="small mb-0">{material.description}</p>
                </div>
              )}

              <hr />

              <label className="form-label small fw-semibold">Quantity ({material.unit})</label>
              <div className="d-flex gap-2">
                <input
                  type="number"
                  step="0.01"
                  className="form-control"
                  style={{ maxWidth: 160 }}
                  value={quantity}
                  onChange={(e) => setQuantity(e.target.value)}
                  placeholder="0"
                />
                <button
                  className="btn btn-success px-4 fw-semibold"
                  disabled={!quantity || Number(quantity) <= 0 || Number(quantity) > material.quantity}
                  onClick={handleBuyNow}
                >
                  Buy Now
                </button>
              </div>
              {quantity > material.quantity && (
                <div className="small text-danger mt-2">Only {material.quantity} {material.unit} available</div>
              )}
              {quantity > 0 && quantity <= material.quantity && (
                <div className="small text-muted mt-2">
                  Estimated total: <strong>₹{(Number(quantity) * material.pricePerUnit).toFixed(2)}</strong>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default MaterialDetails;
