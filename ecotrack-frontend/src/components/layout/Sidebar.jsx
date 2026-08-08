import React from 'react';
import { NavLink } from 'react-router-dom';
import { FaLeaf } from 'react-icons/fa';
import { NAV_BY_ROLE } from './navConfig';

const Sidebar = ({ role, collapsed, mobileOpen, onNavigate }) => {
  const items = NAV_BY_ROLE[role] || [];

  return (
    <aside className={`et-sidebar ${collapsed ? 'collapsed' : ''} ${mobileOpen ? 'mobile-open' : ''}`}>
      <div className="d-flex align-items-center gap-2 px-3" style={{ height: 64, borderBottom: '1px solid var(--color-border)' }}>
        <div
          className="d-flex align-items-center justify-content-center rounded-2 flex-shrink-0"
          style={{ width: 32, height: 32, background: 'var(--color-primary)', color: '#fff' }}
        >
          <FaLeaf size={18} />
        </div>
        {!collapsed && <span className="fw-bold fs-5">EcoTrack</span>}
      </div>

      <nav className="py-3">
        {items.map(({ to, label, icon: Icon, end }) => (
          <NavLink
            key={to}
            to={to}
            end={end}
            onClick={onNavigate}
            className={({ isActive }) => `et-nav-link ${isActive ? 'active' : ''}`}
            title={collapsed ? label : undefined}
          >
            <Icon size={18} className="flex-shrink-0" />
            {!collapsed && <span>{label}</span>}
          </NavLink>
        ))}
      </nav>
    </aside>
  );
};

export default Sidebar;
