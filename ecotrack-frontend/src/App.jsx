import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';

import ProtectedRoute from './routes/ProtectedRoute';
import DashboardLayout from './components/layout/DashboardLayout';
import ComingSoon from './components/common/ComingSoon';

// Public pages
import Home from './pages/public/Home';
import BecomeRecycler from './pages/public/BecomeRecycler';
import BecomeIndustry from './pages/public/BecomeIndustry';

// Auth pages
import Login from './pages/auth/Login';
import Register from './pages/auth/Register';
import ForgotPassword from './pages/auth/ForgotPassword';
import ResetPassword from './pages/auth/ResetPassword';

// Citizen pages
import CitizenDashboard from './pages/citizen/CitizenDashboard';
import SchedulePickup from './pages/citizen/SchedulePickup';
import PickupHistory from './pages/citizen/PickupHistory';
import EcoPointsPage from './pages/citizen/EcoPointsPage';
import CitizenProfile from './pages/citizen/CitizenProfile';

// Recycler pages
import RecyclerDashboard from './pages/recycler/RecyclerDashboard';
import AvailablePickups from './pages/recycler/AvailablePickups';
import AssignedPickups from './pages/recycler/AssignedPickups';
import MaterialListings from './pages/recycler/MaterialListings';
import RecyclerOrders from './pages/recycler/RecyclerOrders';
import RecyclerProfile from './pages/recycler/RecyclerProfile';

// Industry pages
import IndustryDashboard from './pages/industry/IndustryDashboard';
import Marketplace from './pages/industry/Marketplace';
import MaterialDetails from './pages/industry/MaterialDetails';
import Checkout from './pages/industry/Checkout';
import PaymentSelection from './pages/industry/PaymentSelection';
import OrderSuccess from './pages/industry/OrderSuccess';
import IndustryOrders from './pages/industry/IndustryOrders';
import IndustryProfile from './pages/industry/IndustryProfile';

// Admin pages
import AdminDashboard from './pages/admin/AdminDashboard';
import AdminRewardsPage from './pages/admin/AdminRewardsPage';
import AdminUsers from './pages/admin/AdminUsers';
import RecyclerApplications from './pages/admin/RecyclerApplications';
import IndustryApplications from './pages/admin/IndustryApplications';
import AdminPickups from './pages/admin/AdminPickups';
import AdminMaterials from './pages/admin/AdminMaterials';
import AdminOrders from './pages/admin/AdminOrders';
import Reports from './pages/admin/Reports';
import AuditLogs from './pages/admin/AuditLogs';
import AdminSettings from './pages/admin/AdminSettings';

// Shared pages
import NotificationsPage from './pages/shared/NotificationsPage';

function App() {
  return (
    <Routes>
      {/* Public */}
      <Route path="/" element={<Home />} />
      <Route path="/become-recycler" element={<BecomeRecycler />} />
      <Route path="/become-industry" element={<BecomeIndustry />} />

      {/* Auth */}
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/forgot-password" element={<ForgotPassword />} />
      <Route path="/reset-password" element={<ResetPassword />} />

      {/* Citizen */}
      <Route
        path="/citizen"
        element={
          <ProtectedRoute allowedRoles={['CITIZEN']}>
            <DashboardLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<CitizenDashboard />} />
        <Route path="schedule-pickup" element={<SchedulePickup />} />
        <Route path="pickups" element={<PickupHistory />} />
        <Route path="ecopoints" element={<EcoPointsPage />} />
        <Route path="rewards" element={<EcoPointsPage />} />
        <Route path="notifications" element={<NotificationsPage />} />
        <Route path="profile" element={<CitizenProfile />} />
        <Route path="become-recycler" element={<BecomeRecycler />} />
        <Route path="become-industry" element={<BecomeIndustry />} />
      </Route>

      {/* Recycler */}
      <Route
        path="/recycler"
        element={
          <ProtectedRoute allowedRoles={['RECYCLER']}>
            <DashboardLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<RecyclerDashboard />} />
        <Route path="pickups/available" element={<AvailablePickups />} />
        <Route path="pickups/assigned" element={<AssignedPickups />} />
        <Route path="materials" element={<MaterialListings />} />
        <Route path="orders" element={<RecyclerOrders />} />
        <Route path="notifications" element={<NotificationsPage />} />
        <Route path="profile" element={<RecyclerProfile />} />
      </Route>

      {/* Industry */}
      <Route
        path="/industry"
        element={
          <ProtectedRoute allowedRoles={['INDUSTRY']}>
            <DashboardLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<IndustryDashboard />} />
        <Route path="marketplace" element={<Marketplace />} />
        <Route path="marketplace/:id" element={<MaterialDetails />} />
        <Route path="checkout/:materialId" element={<Checkout />} />
        <Route path="orders/:orderId/pay" element={<PaymentSelection />} />
        <Route path="orders/:orderId/success" element={<OrderSuccess />} />
        <Route path="orders" element={<IndustryOrders />} />
        <Route path="notifications" element={<NotificationsPage />} />
        <Route path="profile" element={<IndustryProfile />} />
      </Route>

      {/* Admin */}
      <Route
        path="/admin"
        element={
          <ProtectedRoute allowedRoles={['ADMIN']}>
            <DashboardLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<AdminDashboard />} />
        <Route path="rewards" element={<AdminRewardsPage />} />
        <Route path="users" element={<AdminUsers />} />
        <Route path="recycler-applications" element={<RecyclerApplications />} />
        <Route path="industry-applications" element={<IndustryApplications />} />
        <Route path="pickups" element={<AdminPickups />} />
        <Route path="materials" element={<AdminMaterials />} />
        <Route path="orders" element={<AdminOrders />} />
        <Route path="reports" element={<Reports />} />
        <Route path="audit-logs" element={<AuditLogs />} />
        <Route path="notifications" element={<NotificationsPage />} />
        <Route path="settings" element={<AdminSettings />} />
      </Route>

      {/* Fallback */}
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

export default App;
