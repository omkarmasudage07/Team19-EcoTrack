import React, { useEffect, useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiShoppingBag } from 'react-icons/fi';
import PageHeader from '../../components/common/PageHeader';
import StatusBadge from '../../components/common/StatusBadge';
import { LoadingSpinner, EmptyState, ErrorState } from '../../components/common/Feedback';
import { orderService } from '../../services/materialService';
import { getErrorMessage } from '../../services/api';
import { useToast } from '../../context/ToastContext';

const STATUS_FILTERS = ['ALL', 'PLACED', 'CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED'];

const IndustryOrders = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [cancellingId, setCancellingId] = useState(null);
  const { showToast } = useToast();
  const navigate = useNavigate();

  const load = useCallback(async () => {
    setLoading(true);
    setError(false);
    try {
      const params = { size: 20, sort: 'orderDate,desc' };
      if (statusFilter !== 'ALL') params.status = statusFilter;
      const data = await orderService.getMyOrders(params);
      setOrders(data.content || []);
    } catch (e) {
      setError(true);
    } finally {
      setLoading(false);
    }
  }, [statusFilter]);

  useEffect(() => { load(); }, [load]);

  const handleCancel = async (id) => {
    if (!window.confirm('Cancel this order? This cannot be undone.')) return;
    setCancellingId(id);
    try {
      await orderService.cancel(id);
      showToast('Order cancelled', 'success');
      load();
    } catch (err) {
      showToast(getErrorMessage(err), 'error');
    } finally {
      setCancellingId(null);
    }
  };

  const canCancel = (o) => !['SHIPPED', 'DELIVERED', 'CANCELLED'].includes(o.orderStatus);

  return (
    <div>
      <PageHeader title="My Orders" subtitle="Track and manage the materials you've purchased" />

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

      <div className="card border-0 shadow-sm">
        <div className="card-body p-0">
          {loading ? (
            <LoadingSpinner />
          ) : error ? (
            <ErrorState />
          ) : orders.length === 0 ? (
            <EmptyState icon={FiShoppingBag} title="No orders yet" message="Orders you place from the Marketplace will appear here." />
          ) : (
            <div className="table-responsive">
              <table className="table align-middle mb-0">
                <thead>
                  <tr className="text-muted small text-uppercase">
                    <th className="ps-3">Order #</th>
                    <th>Total</th>
                    <th>Payment</th>
                    <th>Status</th>
                    <th className="pe-3"></th>
                  </tr>
                </thead>
                <tbody>
                  {orders.map((o) => (
                    <tr key={o.id}>
                      <td className="ps-3 small fw-semibold">{o.orderNumber}</td>
                      <td className="small">₹{o.totalAmount}</td>
                      <td><StatusBadge status={o.paymentStatus} /></td>
                      <td><StatusBadge status={o.orderStatus} /></td>
                      <td className="pe-3 text-end">
                        <div className="d-flex justify-content-end gap-2">
                          {o.paymentStatus === 'PENDING' && o.orderStatus !== 'CANCELLED' && (
                            <button className="btn btn-sm btn-success" onClick={() => navigate(`/industry/orders/${o.id}/pay`)}>
                              Pay Now
                            </button>
                          )}
                          {canCancel(o) && (
                            <button
                              className="btn btn-sm btn-outline-danger"
                              disabled={cancellingId === o.id}
                              onClick={() => handleCancel(o.id)}
                            >
                              Cancel
                            </button>
                          )}
                        </div>
                      </td>
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

export default IndustryOrders;
