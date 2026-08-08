import React, { useEffect, useState } from 'react';
import { useParams, useNavigate, useSearchParams } from 'react-router-dom';
import { FiArrowLeft } from 'react-icons/fi';
import PageHeader from '../../components/common/PageHeader';
import { LoadingSpinner, ErrorState } from '../../components/common/Feedback';
import { materialService, orderService } from '../../services/materialService';
import { getErrorMessage } from '../../services/api';
import { useToast } from '../../context/ToastContext';

const Checkout = () => {
  const { materialId } = useParams();
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { showToast } = useToast();

  const [material, setMaterial] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [quantity, setQuantity] = useState(searchParams.get('quantity') || '');
  const [placingOrder, setPlacingOrder] = useState(false);

  useEffect(() => {
    materialService.getDetail(materialId)
      .then(setMaterial)
      .catch(() => setError(true))
      .finally(() => setLoading(false));
  }, [materialId]);

  if (loading) return <LoadingSpinner label="Loading checkout..." />;
  if (error || !material) return <ErrorState message="This material could not be found." />;

  const qty = Number(quantity) || 0;
  const total = qty * material.pricePerUnit;
  const validQuantity = qty > 0 && qty <= material.quantity;

  const handlePlaceOrder = async () => {
    if (!validQuantity) return;
    setPlacingOrder(true);
    try {
      const order = await orderService.place({
        items: [{ materialId: material.id, quantity: qty }],
      });
      showToast('Order placed! Choose how you\'d like to pay.', 'success');
      navigate(`/industry/orders/${order.id}/pay`);
    } catch (err) {
      showToast(getErrorMessage(err), 'error');
    } finally {
      setPlacingOrder(false);
    }
  };

  return (
    <div>
      <button className="btn btn-sm btn-light border d-inline-flex align-items-center gap-1 mb-3" onClick={() => navigate(-1)}>
        <FiArrowLeft size={14} /> Back
      </button>

      <PageHeader title="Checkout" subtitle="Review your order before placing it" />

      <div className="row g-4">
        <div className="col-lg-7">
          <div className="card border-0 shadow-sm">
            <div className="card-body p-4">
              <h6 className="fw-bold mb-3">{material.materialName}</h6>
              <p className="small text-muted mb-3">
                ₹{material.pricePerUnit} / {material.unit} · {material.quantity} {material.unit} available
              </p>

              <label className="form-label small fw-semibold">Quantity ({material.unit})</label>
              <input
                type="number"
                step="0.01"
                className={`form-control ${quantity && !validQuantity ? 'is-invalid' : ''}`}
                style={{ maxWidth: 200 }}
                value={quantity}
                onChange={(e) => setQuantity(e.target.value)}
              />
              {quantity && !validQuantity && (
                <div className="text-danger small mt-1">
                  Enter a quantity between 0 and {material.quantity} {material.unit}
                </div>
              )}
            </div>
          </div>
        </div>

        <div className="col-lg-5">
          <div className="card border-0 shadow-sm">
            <div className="card-body p-4">
              <h6 className="fw-bold mb-3">Order Summary</h6>
              <div className="d-flex justify-content-between small mb-2">
                <span className="text-muted">Quantity</span>
                <span>{qty || 0} {material.unit}</span>
              </div>
              <div className="d-flex justify-content-between small mb-2">
                <span className="text-muted">Price per {material.unit}</span>
                <span>₹{material.pricePerUnit}</span>
              </div>
              <hr />
              <div className="d-flex justify-content-between fw-bold mb-4">
                <span>Total</span>
                <span>₹{total.toFixed(2)}</span>
              </div>
              <button
                className="btn btn-success w-100 d-flex align-items-center justify-content-center gap-2"
                disabled={!validQuantity || placingOrder}
                onClick={handlePlaceOrder}
              >
                {placingOrder && <span className="spinner-border spinner-border-sm" />}
                Place Order
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Checkout;
