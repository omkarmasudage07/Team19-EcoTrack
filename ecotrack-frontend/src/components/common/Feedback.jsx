import React from 'react';
import { FiInbox, FiAlertTriangle } from 'react-icons/fi';

export const LoadingSpinner = ({ label = 'Loading...' }) => (
  <div className="d-flex flex-column align-items-center justify-content-center py-5 text-muted">
    <div className="spinner-border text-success mb-2" role="status" />
    <div className="small">{label}</div>
  </div>
);

export const EmptyState = ({ icon: Icon = FiInbox, title = 'Nothing here yet', message, action }) => (
  <div className="et-empty">
    <Icon size={36} className="text-muted mb-3" />
    <h6 className="fw-semibold mb-1">{title}</h6>
    {message && <p className="small text-muted mb-3">{message}</p>}
    {action}
  </div>
);

export const ErrorState = ({ message = 'Something went wrong. Please try again.', retry }) => (
  <div className="et-empty">
    <FiAlertTriangle size={36} className="text-danger mb-3" />
    <p className="small text-muted mb-3">{message}</p>
    {retry && (
      <button className="btn btn-sm btn-outline-danger" onClick={retry}>
        Try Again
      </button>
    )}
  </div>
);

export const SkeletonRow = ({ height = 16, width = '100%' }) => (
  <div className="et-skeleton" style={{ height, width, marginBottom: 8 }} />
);
