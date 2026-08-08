import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth, HOME_ROUTE_BY_ROLE } from '../context/AuthContext';

/**
 * Wrap any route element with this to require login, and optionally
 * restrict it to specific roles: <ProtectedRoute allowedRoles={['ADMIN']}>.
 */
const ProtectedRoute = ({ children, allowedRoles }) => {
  const { user, isAuthenticated, loading } = useAuth();

  if (loading) {
    return (
      <div className="d-flex justify-content-center align-items-center vh-100">
        <div className="spinner-border text-success" role="status">
          <span className="visually-hidden">Loading session...</span>
        </div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles && !allowedRoles.includes(user.role)) {
    return <Navigate to={HOME_ROUTE_BY_ROLE[user.role] || '/'} replace />;
  }

  return children;
};

export default ProtectedRoute;
