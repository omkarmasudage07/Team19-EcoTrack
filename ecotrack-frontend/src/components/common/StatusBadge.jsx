import React from 'react';

/**
 * Maps every status enum used across the app (pickup, order, payment,
 * approval, account) to one consistent color so a "PENDING" always looks
 * the same regardless of which screen it's on.
 */
const STATUS_STYLES = {
  PENDING: 'et-badge-orange',
  PENDING_APPROVAL: 'et-badge-orange',
  ACCEPTED: 'et-badge-blue',
  ON_THE_WAY: 'et-badge-blue',
  COLLECTED: 'et-badge-blue',
  PROCESSING: 'et-badge-blue',
  CONFIRMED: 'et-badge-blue',
  SHIPPED: 'et-badge-blue',
  COMPLETED: 'et-badge-green',
  DELIVERED: 'et-badge-green',
  APPROVED: 'et-badge-green',
  SUCCESS: 'et-badge-green',
  ACTIVE: 'et-badge-green',
  AVAILABLE: 'et-badge-green',
  PLACED: 'et-badge-slate',
  CANCELLED: 'et-badge-red',
  REJECTED: 'et-badge-red',
  FAILED: 'et-badge-red',
  BLOCKED: 'et-badge-red',
  DEACTIVATED: 'et-badge-red',
  OUT_OF_STOCK: 'et-badge-red',
};

const formatLabel = (status) =>
  String(status || '')
    .replace(/_/g, ' ')
    .toLowerCase()
    .replace(/\b\w/g, (c) => c.toUpperCase());

const StatusBadge = ({ status }) => {
  const style = STATUS_STYLES[status] || 'et-badge-slate';
  return (
    <span className={`et-badge ${style}`}>
      <span className="et-badge-dot" />
      {formatLabel(status)}
    </span>
  );
};

export default StatusBadge;
