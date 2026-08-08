import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { FiShoppingCart, FiClock, FiCheckCircle, FiShoppingBag } from 'react-icons/fi';
import PageHeader from '../../components/common/PageHeader';
import StatCard from '../../components/cards/StatCard';
import StatusBadge from '../../components/common/StatusBadge';
import { LoadingSpinner, EmptyState, ErrorState } from '../../components/common/Feedback';
import { materialService, orderService } from '../../services/materialService';

const IndustryDashboard = () => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [availableCount, setAvailableCount] = useState(0);
  const [pendingCount, setPendingCount] = useState(0);
  const [deliveredCount, setDeliveredCount] = useState(0);
  const [recentOrders, setRecentOrders] = useState([]);

  useEffect(() => {
    let active = true;
    (async () => {
      try {
        const results = await Promise.allSettled([
          materialService.browse({ size: 1 }),
          orderService.getMyOrders({ status: 'PLACED', size: 1 }),
          orderService.getMyOrders({ status: 'DELIVERED', size: 1 }),
          orderService.getMyOrders({ size: 5, sort: 'orderDate,desc' }),
        ]);

        const getValue = (res) => (res.status === 'fulfilled' ? res.value : null);

        const [materials, pending, delivered, recent] = results.map(getValue);

        if (!active) return;

        const allFailed = results.every((r) => r.status === 'rejected');
        if (allFailed) {
          setError(true);
          return;
        }

        setAvailableCount(materials?.totalElements ?? 0);
        setPendingCount(pending?.totalElements ?? 0);
        setDeliveredCount(delivered?.totalElements ?? 0);
        setRecentOrders(recent?.content || []);
      } catch (e) {
        if (active) setError(true);
      } finally {
        if (active) setLoading(false);
      }
    })();
    return () => { active = false; };
  }, []);

  if (loading) return <LoadingSpinner label="Loading your dashboard..." />;
  if (error) return <ErrorState />;

  return (
    <div>
      <PageHeader
        title="Industry Dashboard"
        subtitle="Browse recovered materials and track your orders"
        action={<Link to="/industry/marketplace" className="btn btn-success">Browse Marketplace</Link>}
      />

      <div className="row g-3 mb-4">
        <div className="col-sm-6 col-lg-4">
          <Link to="/industry/marketplace" className="text-decoration-none">
            <StatCard icon={FiShoppingCart} label="Materials Available" value={availableCount} />
          </Link>
        </div>
        <div className="col-sm-6 col-lg-4">
          <Link to="/industry/orders" className="text-decoration-none">
            <StatCard icon={FiClock} label="Pending Orders" value={pendingCount} />
          </Link>
        </div>
        <div className="col-sm-6 col-lg-4">
          <Link to="/industry/orders" className="text-decoration-none">
            <StatCard icon={FiCheckCircle} label="Delivered Orders" value={deliveredCount} />
          </Link>
        </div>
      </div>

      <div className="card border-0 shadow-sm">
        <div className="card-body">
          <h6 className="fw-bold mb-3">Recent Orders</h6>
          {recentOrders.length === 0 ? (
            <EmptyState
              icon={FiShoppingBag}
              title="No orders yet"
              message="Browse the marketplace to place your first order."
              action={<Link to="/industry/marketplace" className="btn btn-success btn-sm">Browse Marketplace</Link>}
            />
          ) : (
            <div className="table-responsive">
              <table className="table align-middle mb-0">
                <thead>
                  <tr className="text-muted small text-uppercase">
                    <th>Order #</th>
                    <th>Total</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>
                  {recentOrders.map((o) => (
                    <tr key={o.id}>
                      <td className="small fw-semibold">
                        <Link to="/industry/orders">{o.orderNumber}</Link>
                      </td>
                      <td className="small">₹{o.totalAmount}</td>
                      <td><StatusBadge status={o.orderStatus} /></td>
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

export default IndustryDashboard;
