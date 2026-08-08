import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { FiAward, FiClock, FiCheckCircle, FiTruck } from 'react-icons/fi';
import PageHeader from '../../components/common/PageHeader';
import StatCard from '../../components/cards/StatCard';
import StatusBadge from '../../components/common/StatusBadge';
import { LoadingSpinner, EmptyState, ErrorState } from '../../components/common/Feedback';
import pickupService from '../../services/pickupService';
import { ecoPointsService } from '../../services/materialService';
import { useAuth } from '../../context/AuthContext';

const CitizenDashboard = () => {
  const { user } = useAuth();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [wallet, setWallet] = useState(null);
  const [recentPickups, setRecentPickups] = useState([]);
  const [pendingCount, setPendingCount] = useState(0);
  const [completedCount, setCompletedCount] = useState(0);

  useEffect(() => {
    let active = true;
    (async () => {
      try {
        const results = await Promise.allSettled([
          ecoPointsService.getWallet(),
          pickupService.getMyPickups({ size: 5, sort: 'createdAt,desc' }),
          pickupService.getMyPickups({ status: 'PENDING', size: 1 }),
          pickupService.getMyPickups({ status: 'COMPLETED', size: 1 }),
        ]);

        const getValue = (res) => (res.status === 'fulfilled' ? res.value : null);

        const [walletData, pickupsData, pendingData, completedData] = results.map(getValue);

        if (!active) return;

        const allFailed = results.every((r) => r.status === 'rejected');
        if (allFailed) {
          setError(true);
          return;
        }

        setWallet(walletData);
        setRecentPickups(pickupsData?.content || []);
        setPendingCount(pendingData?.totalElements ?? 0);
        setCompletedCount(completedData?.totalElements ?? 0);
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
        title={`Welcome back${user?.email ? ', ' + user.email.split('@')[0] : ''}`}
        subtitle="Here's what's happening with your recycling activity"
        action={
          <Link to="/citizen/schedule-pickup" className="btn btn-success">
            Schedule a Pickup
          </Link>
        }
      />

      <div className="row g-3 mb-4">
        <div className="col-sm-6 col-lg-3">
          <StatCard icon={FiAward} label="EcoPoints Balance" value={wallet?.currentBalance ?? 0} />
        </div>
        <div className="col-sm-6 col-lg-3">
          <StatCard icon={FiClock} label="Pending Pickups" value={pendingCount} />
        </div>
        <div className="col-sm-6 col-lg-3">
          <StatCard icon={FiCheckCircle} label="Completed Pickups" value={completedCount} />
        </div>
        <div className="col-sm-6 col-lg-3">
          <StatCard icon={FiTruck} label="Total Requests" value={recentPickups.length ? recentPickups.length : 0} hint="Recent activity" />
        </div>
      </div>

      <div className="card border-0 shadow-sm bg-light mb-4">
        <div className="card-body p-4 d-flex flex-column flex-md-row align-items-md-center justify-content-between gap-3">
          <div>
            <h6 className="fw-bold text-dark mb-1">Looking to partner with EcoTrack?</h6>
            <p className="text-muted small mb-0">
              Apply to transition your Citizen account into a verified Recycler Partner or Industrial Raw Material Buyer.
            </p>
          </div>
          <div className="d-flex flex-wrap gap-2">
            <Link to="/citizen/become-recycler" className="btn btn-outline-success btn-sm fw-semibold">
              Become Recycler Partner
            </Link>
            <Link to="/citizen/become-industry" className="btn btn-outline-primary btn-sm fw-semibold">
              Become Industry Buyer
            </Link>
          </div>
        </div>
      </div>

      <div className="card border-0 shadow-sm">
        <div className="card-body">
          <h6 className="fw-bold mb-3">Recent Pickup Requests</h6>
          {recentPickups.length === 0 ? (
            <EmptyState
              icon={FiTruck}
              title="No pickups yet"
              message="Schedule your first e-waste pickup to start earning EcoPoints."
              action={
                <Link to="/citizen/schedule-pickup" className="btn btn-success btn-sm">
                  Schedule Pickup
                </Link>
              }
            />
          ) : (
            <div className="table-responsive">
              <table className="table align-middle mb-0">
                <thead>
                  <tr className="text-muted small text-uppercase">
                    <th>Pickup #</th>
                    <th>Category</th>
                    <th>Date</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>
                  {recentPickups.map((p) => (
                    <tr key={p.id}>
                      <td className="small fw-semibold">
                        <Link to={`/citizen/pickups`}>{p.pickupNumber}</Link>
                      </td>
                      <td className="small">{p.wasteCategoryName}</td>
                      <td className="small">{p.pickupDate}</td>
                      <td><StatusBadge status={p.status} /></td>
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

export default CitizenDashboard;
