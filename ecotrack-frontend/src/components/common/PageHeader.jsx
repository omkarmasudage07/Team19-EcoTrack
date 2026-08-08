import React from 'react';

const PageHeader = ({ title, subtitle, action }) => (
  <div className="d-flex flex-wrap justify-content-between align-items-start gap-3 mb-4">
    <div>
      <h4 className="fw-bold mb-1">{title}</h4>
      {subtitle && <p className="text-muted small mb-0">{subtitle}</p>}
    </div>
    {action && <div>{action}</div>}
  </div>
);

export default PageHeader;
