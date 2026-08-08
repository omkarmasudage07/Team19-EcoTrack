import React from 'react';

const StatCard = ({ icon: Icon, label, value, tint = 'green', hint }) => (
  <div className="card border-0 shadow-sm h-100 et-card-hover">
    <div className="card-body d-flex align-items-start gap-3 p-3">
      <div
        className="d-flex align-items-center justify-content-center rounded-3"
        style={{
          width: 44,
          height: 44,
          background: `var(--color-primary-light)`,
          color: 'var(--color-primary-dark)',
          flexShrink: 0,
        }}
      >
        {Icon && <Icon size={20} />}
      </div>
      <div className="flex-fill">
        <div className="text-muted small mb-1">{label}</div>
        <div className="fs-4 fw-bold lh-1">{value}</div>
        {hint && <div className="small text-muted mt-1">{hint}</div>}
      </div>
    </div>
  </div>
);

export default StatCard;
