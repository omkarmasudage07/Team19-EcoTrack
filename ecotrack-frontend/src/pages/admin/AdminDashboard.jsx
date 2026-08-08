import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { FiUsers, FiCheckSquare, FiTruck, FiShoppingBag } from 'react-icons/fi';
import PageHeader from '../../components/common/PageHeader';
import StatCard from '../../components/cards/StatCard';
import { LoadingSpinner, ErrorState } from '../../components/common/Feedback';
import { recyclerApplicationService, industryApplicationService, recyclerService, industryService } from '../../services/userService';
import pickupService from '../../services/pickupService';
import { orderService } from '../../services/materialService';

const AdminDashboard = () => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [stats, setStats] = useState(null);

  useEffect(() => {
    let active = true;
    (async () => {
      try {
        const results = await Promise.allSettled([
          recyclerApplicationService.getAll({ status: 'PENDING', size: 1 }),
          industryApplicationService.getAll({ status: 'PENDING', size: 1 }),
          recyclerService.search({ size: 1 }),
          industryService.search({ size: 1 }),
          pickupService.getAll({ status: 'PENDING', size: 1 }),
          pickupService.getAll({ status: 'COMPLETED', size: 1 }),
          orderService.getAll({ status: 'PLACED', size: 1 }),
          orderService.getAll({ size: 1 }),
        ]);

        const getValue = (res) => (res.status === 'fulfilled' ? res.value : {});

        const [
          pendingRecyclerApps, pendingIndustryApps,
          recyclers, industries,
          pendingPickups, completedPickups,
          placedOrders, allOrders,
        ] = results.map(getValue);

        if (!active) return;

        // If ALL requests failed, show error state
        const allFailed = results.every((r) => r.status === 'rejected');
        if (allFailed) {
          setError(true);
          return;
        }

        setStats({
          pendingRecyclerApps: pendingRecyclerApps?.totalElements ?? 0,
          pendingIndustryApps: pendingIndustryApps?.totalElements ?? 0,
          totalRecyclers: recyclers?.totalElements ?? 0,
          totalIndustries: industries?.totalElements ?? 0,
          pendingPickups: pendingPickups?.totalElements ?? 0,
          completedPickups: completedPickups?.totalElements ?? 0,
          placedOrders: placedOrders?.totalElements ?? 0,
          totalOrders: allOrders?.totalElements ?? 0,
        });
      } catch (e) {
        if (active) setError(true);
      } finally {
        if (active) setLoading(false);
      }
    })();
    return () => { active = false; };
  }, []);

  if (loading) return <LoadingSpinner label="Loading platform overview..." />;
  if (error) return <ErrorState />;

  const hasPendingReviews = stats.pendingRecyclerApps > 0 || stats.pendingIndustryApps > 0;

  return (
    <div>
      <PageHeader title="Admin Dashboard" subtitle="Platform-wide overview" />

      {hasPendingReviews && (
        <div className="alert alert-warning d-flex align-items-center justify-content-between mb-4">
          <div className="small">
            <strong>{stats.pendingRecyclerApps + stats.pendingIndustryApps} application(s)</strong> waiting for your review.
          </div>
          <div className="d-flex gap-2">
            {stats.pendingRecyclerApps > 0 && (
              <Link to="/admin/recycler-applications" className="btn btn-sm btn-outline-dark">Review Recyclers</Link>
            )}
            {stats.pendingIndustryApps > 0 && (
              <Link to="/admin/industry-applications" className="btn btn-sm btn-outline-dark">Review Industries</Link>
            )}
          </div>
        </div>
      )}

      <div className="row g-3 mb-4">
        <div className="col-sm-6 col-lg-3">
          <StatCard icon={FiUsers} label="Recycler Partners" value={stats.totalRecyclers} />
        </div>
        <div className="col-sm-6 col-lg-3">
          <StatCard icon={FiUsers} label="Industrial Buyers" value={stats.totalIndustries} />
        </div>
        <div className="col-sm-6 col-lg-3">
          <StatCard icon={FiCheckSquare} label="Pending Applications" value={stats.pendingRecyclerApps + stats.pendingIndustryApps} />
        </div>
        <div className="col-sm-6 col-lg-3">
          <StatCard icon={FiShoppingBag} label="Total Orders" value={stats.totalOrders} />
        </div>
      </div>

      <div className="row g-3">
        <div className="col-md-6">
          <div className="card border-0 shadow-sm h-100">
            <div className="card-body">
              <h6 className="fw-bold mb-3 d-flex align-items-center gap-2"><FiTruck /> Pickups</h6>
              <div className="d-flex justify-content-between small mb-2">
                <span className="text-muted">Pending</span>
                <span className="fw-semibold">{stats.pendingPickups}</span>
              </div>
              <div className="d-flex justify-content-between small">
                <span className="text-muted">Completed</span>
                <span className="fw-semibold">{stats.completedPickups}</span>
              </div>
              <Link to="/admin/pickups" className="btn btn-outline-success btn-sm mt-3">View All Pickups</Link>
            </div>
          </div>
        </div>
        <div className="col-md-6">
          <div className="card border-0 shadow-sm h-100">
            <div className="card-body">
              <h6 className="fw-bold mb-3 d-flex align-items-center gap-2"><FiShoppingBag /> Orders</h6>
              <div className="d-flex justify-content-between small mb-2">
                <span className="text-muted">Awaiting Payment</span>
                <span className="fw-semibold">{stats.placedOrders}</span>
              </div>
              <div className="d-flex justify-content-between small">
                <span className="text-muted">Total</span>
                <span className="fw-semibold">{stats.totalOrders}</span>
              </div>
              <Link to="/admin/orders" className="btn btn-outline-success btn-sm mt-3">View All Orders</Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminDashboard;
