import React, { useEffect, useState, useCallback } from 'react';
import { FiShoppingBag } from 'react-icons/fi';
import PageHeader from '../../components/common/PageHeader';
import StatusBadge from '../../components/common/StatusBadge';
import { LoadingSpinner, EmptyState, ErrorState } from '../../components/common/Feedback';
import { orderService } from '../../services/materialService';
import { getErrorMessage } from '../../services/api';
import { useToast } from '../../context/ToastContext';

const NEXT_STATUS = {
  CONFIRMED: 'PROCESSING',
  PROCESSING: 'SHIPPED',
  SHIPPED: 'DELIVERED',
};

const NEXT_LABEL = {
  CONFIRMED: 'Start Processing',
  PROCESSING: 'Mark Shipped',
  SHIPPED: 'Mark Delivered',
};

const STATUS_FILTERS = ['ALL', 'PLACED', 'CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED'];

const RecyclerOrders = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [actingId, setActingId] = useState(null);
  const { showToast } = useToast();

  const load = useCallback(async () => {
    setLoading(true);
    setError(false);
    try {
      const params = { size: 20, sort: 'orderDate,desc' };
      if (statusFilter !== 'ALL') params.status = statusFilter;
      const data = await orderService.getReceivedOrders(params);
      setOrders(data.content || []);
    } catch (e) {
      setError(true);
    } finally {
      setLoading(false);
    }
  }, [statusFilter]);

  useEffect(() => { load(); }, [load]);

  const handleAdvance = async (order) => {
    const nextStatus = NEXT_STATUS[order.orderStatus];
    if (!nextStatus) return;
    setActingId(order.id);
    try {
      await orderService.updateStatus(order.id, { status: nextStatus });
      showToast(`Order marked ${nextStatus.toLowerCase()}`, 'success');
      load();
    } catch (err) {
      showToast(getErrorMessage(err), 'error');
    } finally {
      setActingId(null);
    }
  };

  return (
    <div>
      <PageHeader title="Orders" subtitle="Orders Industrial Buyers have placed against your materials" />

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
            <EmptyState icon={FiShoppingBag} title="No orders yet" message="Orders placed against your material listings will appear here." />
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
                        {o.paymentStatus === 'SUCCESS' && NEXT_STATUS[o.orderStatus] && (
                          <button
                            className="btn btn-sm btn-success"
                            disabled={actingId === o.id}
                            onClick={() => handleAdvance(o)}
                          >
                            {actingId === o.id ? 'Updating...' : NEXT_LABEL[o.orderStatus]}
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
    </div>
  );
};

export default RecyclerOrders;
