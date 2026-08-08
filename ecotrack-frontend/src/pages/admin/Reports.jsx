import React, { useEffect, useState } from 'react';
import { Bar } from 'react-chartjs-2';
import {
  Chart as ChartJS, CategoryScale, LinearScale, BarElement, Tooltip, Legend,
} from 'chart.js';
import PageHeader from '../../components/common/PageHeader';
import { LoadingSpinner, ErrorState } from '../../components/common/Feedback';
import pickupService from '../../services/pickupService';
import { orderService } from '../../services/materialService';

ChartJS.register(CategoryScale, LinearScale, BarElement, Tooltip, Legend);

const PICKUP_STATUSES = ['PENDING', 'ACCEPTED', 'ON_THE_WAY', 'COLLECTED', 'PROCESSING', 'COMPLETED', 'CANCELLED'];
const ORDER_STATUSES = ['PLACED', 'CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED'];

const chartOptions = {
  responsive: true,
  plugins: { legend: { display: false } },
  scales: { y: { beginAtZero: true, ticks: { precision: 0 } } },
};

const Reports = () => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [pickupCounts, setPickupCounts] = useState([]);
  const [orderCounts, setOrderCounts] = useState([]);

  useEffect(() => {
    let active = true;
    (async () => {
      try {
        const pickupSettled = await Promise.allSettled(
          PICKUP_STATUSES.map((s) => pickupService.getAll({ status: s, size: 1 }))
        );
        const orderSettled = await Promise.allSettled(
          ORDER_STATUSES.map((s) => orderService.getAll({ status: s, size: 1 }))
        );
        if (!active) return;

        setPickupCounts(pickupSettled.map((r) => (r.status === 'fulfilled' ? r.value?.totalElements ?? 0 : 0)));
        setOrderCounts(orderSettled.map((r) => (r.status === 'fulfilled' ? r.value?.totalElements ?? 0 : 0)));
      } catch (e) {
        if (active) setError(true);
      } finally {
        if (active) setLoading(false);
      }
    })();
    return () => { active = false; };
  }, []);

  if (loading) return <LoadingSpinner label="Building reports..." />;
  if (error) return <ErrorState />;

  return (
    <div>
      <PageHeader title="Reports" subtitle="Pickup and order status breakdown across the platform" />

      <div className="row g-3">
        <div className="col-lg-6">
          <div className="card border-0 shadow-sm h-100">
            <div className="card-body">
              <h6 className="fw-bold mb-3">Pickups by Status</h6>
              <Bar
                options={chartOptions}
                data={{
                  labels: PICKUP_STATUSES.map((s) => s.replace(/_/g, ' ')),
                  datasets: [{ data: pickupCounts, backgroundColor: '#0f9d58', borderRadius: 4 }],
                }}
              />
            </div>
          </div>
        </div>
        <div className="col-lg-6">
          <div className="card border-0 shadow-sm h-100">
            <div className="card-body">
              <h6 className="fw-bold mb-3">Orders by Status</h6>
              <Bar
                options={chartOptions}
                data={{
                  labels: ORDER_STATUSES,
                  datasets: [{ data: orderCounts, backgroundColor: '#2563eb', borderRadius: 4 }],
                }}
              />
            </div>
          </div>
        </div>
      </div>

      <div className="alert alert-light border small mt-3 mb-0">
        Revenue and EcoPoints reports need dedicated aggregate endpoints on the backend
        (currently only per-order and per-wallet data is exposed) - a good next backend addition
        if you want fuller financial reporting here.
      </div>
    </div>
  );
};

export default Reports;
