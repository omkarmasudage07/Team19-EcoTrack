import React, { useEffect, useState, useCallback } from 'react';
import { FiShoppingBag } from 'react-icons/fi';
import PageHeader from '../../components/common/PageHeader';
import StatusBadge from '../../components/common/StatusBadge';
import { LoadingSpinner, EmptyState, ErrorState } from '../../components/common/Feedback';
import { orderService } from '../../services/materialService';

const STATUS_FILTERS = ['ALL', 'PLACED', 'CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED'];

const AdminOrders = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [statusFilter, setStatusFilter] = useState('ALL');

  const load = useCallback(async () => {
    setLoading(true);
    setError(false);
    try {
      const params = { size: 30, sort: 'orderDate,desc' };
      if (statusFilter !== 'ALL') params.status = statusFilter;
      const data = await orderService.getAll(params);
      setOrders(data.content || []);
    } catch (e) {
      setError(true);
    } finally {
      setLoading(false);
    }
  }, [statusFilter]);

  useEffect(() => { load(); }, [load]);

  const totalRevenue = orders
    .filter((o) => o.paymentStatus === 'SUCCESS')
    .reduce((sum, o) => sum + Number(o.totalAmount || 0), 0);

  return (
    <div>
      <PageHeader title="Orders" subtitle="Every order across the platform" />

      <div className="d-flex gap-2 flex-wrap mb-3">
        {STATUS_FILTERS.map((s) => (
          <button
            key={s}
            className={`btn btn-sm ${statusFilter === s ? 'btn-success' : 'btn-outline-secondary'}`}
            onClick={() => setStatusFilter(s)}
          >
            {s}
          </button>
        ))}
      </div>

      {!loading && !error && orders.length > 0 && (
        <div className="alert alert-light border small mb-3">
          Revenue from paid orders on this page: <strong>₹{totalRevenue.toFixed(2)}</strong>
        </div>
      )}

      <div className="card border-0 shadow-sm">
        <div className="card-body p-0">
          {loading ? (
            <LoadingSpinner />
          ) : error ? (
            <ErrorState />
          ) : orders.length === 0 ? (
            <EmptyState icon={FiShoppingBag} title="No orders found" />
          ) : (
            <div className="table-responsive">
              <table className="table align-middle mb-0">
                <thead>
                  <tr className="text-muted small text-uppercase">
                    <th className="ps-3">Order #</th>
                    <th>Industry ID</th>
                    <th>Recycler ID</th>
                    <th>Total</th>
                    <th>Payment</th>
                    <th className="pe-3">Status</th>
                  </tr>
                </thead>
                <tbody>
                  {orders.map((o) => (
                    <tr key={o.id}>
                      <td className="ps-3 small fw-semibold">{o.orderNumber}</td>
                      <td className="small text-muted">#{o.industryId}</td>
                      <td className="small text-muted">#{o.recyclerId}</td>
                      <td className="small">₹{o.totalAmount}</td>
                      <td><StatusBadge status={o.paymentStatus} /></td>
                      <td className="pe-3"><StatusBadge status={o.orderStatus} /></td>
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

export default AdminOrders;
