import React from 'react';
import { FiTool } from 'react-icons/fi';
import PageHeader from './PageHeader';

const ComingSoon = ({ title }) => (
  <div>
    <PageHeader title={title} />
    <div className="card border-0 shadow-sm">
      <div className="et-empty">
        <FiTool size={36} className="text-muted mb-3" />
        <h6 className="fw-semibold mb-1">This page is still being built</h6>
        <p className="small text-muted mb-0">
          The backend API for this section is already live - this screen just hasn't been wired up yet.
        </p>
      </div>
    </div>
  </div>
);

export default ComingSoon;
