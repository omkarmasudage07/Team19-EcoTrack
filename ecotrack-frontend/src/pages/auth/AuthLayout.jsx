import React from 'react';
import { Link } from 'react-router-dom';
import { FaLeaf } from 'react-icons/fa';

const AuthLayout = ({ title, subtitle, children, footer }) => (
  <div className="min-vh-100 d-flex align-items-center justify-content-center py-5 px-3" style={{ background: 'var(--color-bg)' }}>
    <div className="w-100" style={{ maxWidth: 440 }}>
      <div className="text-center mb-4">
        <Link to="/" className="d-inline-flex align-items-center gap-2 text-decoration-none">
          <div
            className="d-flex align-items-center justify-content-center rounded-3"
            style={{ width: 40, height: 40, background: 'var(--color-primary)', color: '#fff' }}
          >
            <FaLeaf size={22} />
          </div>
          <span className="fs-4 fw-bold text-dark">EcoTrack</span>
        </Link>
      </div>

      <div className="card border-0 shadow-sm p-4 p-md-5">
        <div className="text-center mb-4">
          <h4 className="fw-bold mb-1">{title}</h4>
          {subtitle && <p className="text-muted small mb-0">{subtitle}</p>}
        </div>
        {children}
      </div>

      {footer && <div className="text-center mt-4 small text-muted">{footer}</div>}
    </div>
  </div>
);

export default AuthLayout;
