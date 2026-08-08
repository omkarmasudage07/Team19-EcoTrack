import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { FiTruck, FiPackage, FiBox, FiShoppingBag } from 'react-icons/fi';
import PageHeader from '../../components/common/PageHeader';
import StatCard from '../../components/cards/StatCard';
import { LoadingSpinner, ErrorState } from '../../components/common/Feedback';
import pickupService from '../../services/pickupService';
import { materialService, orderService } from '../../services/materialService';

const RecyclerDashboard = () => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [counts, setCounts] = useState({ available: 0, assigned: 0, materials: 0, orders: 0 });

  useEffect(() => {
    let active = true;
    (async () => {
      try {
        const results = await Promise.allSettled([
          pickupService.getAvailable({ size: 1 }),
          pickupService.getAssigned({ size: 1 }),
          materialService.getMyMaterials({ size: 1 }),
          orderService.getReceivedOrders({ size: 1 }),
        ]);

        const getValue = (res) => (res.status === 'fulfilled' ? res.value : {});

        const [available, assigned, materials, orders] = results.map(getValue);

        if (!active) return;

        const allFailed = results.every((r) => r.status === 'rejected');
        if (allFailed) {
          setError(true);
          return;
        }

        setCounts({
          available: available?.totalElements ?? 0,
          assigned: assigned?.totalElements ?? 0,
          materials: materials?.totalElements ?? 0,
          orders: orders?.totalElements ?? 0,
        });
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
      <PageHeader title="Recycler Dashboard" subtitle="Your pickups, inventory and orders at a glance" />

      <div className="row g-3 mb-4">
        <div className="col-sm-6 col-lg-3">
          <Link to="/recycler/pickups/available" className="text-decoration-none">
            <StatCard icon={FiTruck} label="Available Pickups" value={counts.available} />
          </Link>
        </div>
        <div className="col-sm-6 col-lg-3">
          <Link to="/recycler/pickups/assigned" className="text-decoration-none">
            <StatCard icon={FiPackage} label="My Pickups" value={counts.assigned} />
          </Link>
        </div>
        <div className="col-sm-6 col-lg-3">
          <Link to="/recycler/materials" className="text-decoration-none">
            <StatCard icon={FiBox} label="Material Listings" value={counts.materials} />
          </Link>
        </div>
        <div className="col-sm-6 col-lg-3">
          <Link to="/recycler/orders" className="text-decoration-none">
            <StatCard icon={FiShoppingBag} label="Orders Received" value={counts.orders} />
          </Link>
        </div>
      </div>

      <div className="row g-3">
        <div className="col-md-6">
          <div className="card border-0 shadow-sm h-100">
            <div className="card-body">
              <h6 className="fw-bold mb-2">Pick up e-waste</h6>
              <p className="small text-muted mb-3">
                Browse pickups Citizens have requested and accept the ones near you.
              </p>
              <Link to="/recycler/pickups/available" className="btn btn-success btn-sm">Browse Available Pickups</Link>
            </div>
          </div>
        </div>
        <div className="col-md-6">
          <div className="card border-0 shadow-sm h-100">
            <div className="card-body">
              <h6 className="fw-bold mb-2">List recovered materials</h6>
              <p className="small text-muted mb-3">
                Add materials you've recovered to the marketplace for Industrial Buyers.
              </p>
              <Link to="/recycler/materials" className="btn btn-success btn-sm">Manage Listings</Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RecyclerDashboard;
