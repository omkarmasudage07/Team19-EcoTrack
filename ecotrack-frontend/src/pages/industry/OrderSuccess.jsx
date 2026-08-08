import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { FiCheckCircle } from 'react-icons/fi';
import { LoadingSpinner, ErrorState } from '../../components/common/Feedback';
import { orderService } from '../../services/materialService';

const OrderSuccess = () => {
  const { orderId } = useParams();
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  useEffect(() => {
    orderService.getDetail(orderId)
      .then(setOrder)
      .catch(() => setError(true))
      .finally(() => setLoading(false));
  }, [orderId]);

  if (loading) return <LoadingSpinner />;
  if (error || !order) return <ErrorState message="This order could not be found." />;

  return (
    <div className="d-flex justify-content-center py-5">
      <div className="card border-0 shadow-sm text-center p-5" style={{ maxWidth: 480 }}>
        <div
          className="mx-auto mb-4 d-flex align-items-center justify-content-center rounded-circle"
          style={{ width: 72, height: 72, background: 'var(--color-primary-light)', color: 'var(--color-primary)' }}
        >
          <FiCheckCircle size={36} />
        </div>
        <h4 className="fw-bold mb-2">Order Confirmed!</h4>
        <p className="text-muted mb-4">
          Your payment for order <strong>{order.orderNumber}</strong> was successful. The Recycler
          has been notified and will begin processing your order shortly.
        </p>

        <div className="card border-0 bg-light text-start p-3 mb-4">
          <div className="d-flex justify-content-between small mb-1">
            <span className="text-muted">Order Number</span>
            <span className="fw-semibold">{order.orderNumber}</span>
          </div>
          <div className="d-flex justify-content-between small mb-1">
            <span className="text-muted">Amount Paid</span>
            <span className="fw-semibold">₹{order.totalAmount}</span>
          </div>
          {order.payment?.transactionNumber && (
            <div className="d-flex justify-content-between small">
              <span className="text-muted">Transaction ID</span>
              <span className="fw-semibold">{order.payment.transactionNumber}</span>
            </div>
          )}
        </div>

        <div className="d-flex gap-2 justify-content-center">
          <Link to="/industry/orders" className="btn btn-success">View My Orders</Link>
          <Link to="/industry/marketplace" className="btn btn-outline-secondary">Continue Shopping</Link>
        </div>
      </div>
    </div>
  );
};

export default OrderSuccess;
