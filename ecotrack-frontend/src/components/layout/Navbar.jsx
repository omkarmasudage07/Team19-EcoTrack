import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiMenu, FiBell, FiChevronDown, FiLogOut, FiUser } from 'react-icons/fi';
import { useAuth, HOME_ROUTE_BY_ROLE } from '../../context/AuthContext';
import { useNotifications } from '../../context/NotificationContext';

const ROLE_LABELS = {
  CITIZEN: 'Citizen',
  RECYCLER: 'Recycler Partner',
  INDUSTRY: 'Industrial Buyer',
  ADMIN: 'Administrator',
};

const Navbar = ({ onToggleSidebar, onToggleMobileSidebar }) => {
  const { user, logout } = useAuth();
  const { unreadCount } = useNotifications();
  const navigate = useNavigate();
  const [menuOpen, setMenuOpen] = useState(false);

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  const notificationsPath = user ? `${HOME_ROUTE_BY_ROLE[user.role]}/notifications` : '/login';
  const profilePath = user ? `${HOME_ROUTE_BY_ROLE[user.role]}/profile` : '/login';

  return (
    <header className="et-navbar">
      <button
        className="btn btn-sm btn-light border d-none d-md-inline-flex align-items-center"
        onClick={onToggleSidebar}
        aria-label="Toggle sidebar"
      >
        <FiMenu size={16} />
      </button>
      <button
        className="btn btn-sm btn-light border d-inline-flex d-md-none align-items-center"
        onClick={onToggleMobileSidebar}
        aria-label="Open menu"
      >
        <FiMenu size={16} />
      </button>

      <div className="flex-fill" />

      <button
        className="btn btn-sm btn-light border position-relative d-inline-flex align-items-center justify-content-center"
        style={{ width: 36, height: 36 }}
        onClick={() => navigate(notificationsPath)}
        aria-label="Notifications"
      >
        <FiBell size={16} />
        {unreadCount > 0 && (
          <span
            className="position-absolute badge rounded-pill bg-danger"
            style={{ top: -4, right: -4, fontSize: '0.6rem' }}
          >
            {unreadCount > 9 ? '9+' : unreadCount}
          </span>
        )}
      </button>

      <div className="position-relative">
        <button
          className="btn btn-sm btn-light border d-flex align-items-center gap-2"
          onClick={() => setMenuOpen((o) => !o)}
        >
          <div
            className="d-flex align-items-center justify-content-center rounded-circle bg-success-subtle text-success fw-bold"
            style={{ width: 26, height: 26, fontSize: '0.75rem' }}
          >
            {(user?.email || '?')[0].toUpperCase()}
          </div>
          <span className="small d-none d-sm-inline">{ROLE_LABELS[user?.role] || ''}</span>
          <FiChevronDown size={14} />
        </button>

        {menuOpen && (
          <>
            <div
              className="position-fixed top-0 start-0 w-100 h-100"
              style={{ zIndex: 30 }}
              onClick={() => setMenuOpen(false)}
            />
            <div
              className="position-absolute end-0 mt-2 bg-white border rounded-3 shadow-sm py-1"
              style={{ minWidth: 200, zIndex: 31 }}
            >
              <div className="px-3 py-2 border-bottom">
                <div className="small fw-semibold text-truncate">{user?.email}</div>
                <div className="small text-muted">{ROLE_LABELS[user?.role]}</div>
              </div>
              <button
                className="dropdown-item small py-2 d-flex align-items-center gap-2"
                onClick={() => {
                  setMenuOpen(false);
                  navigate(profilePath);
                }}
              >
                <FiUser size={14} /> Profile
              </button>
              <button
                className="dropdown-item small py-2 d-flex align-items-center gap-2 text-danger"
                onClick={handleLogout}
              >
                <FiLogOut size={14} /> Logout
              </button>
            </div>
          </>
        )}
      </div>
    </header>
  );
};

export default Navbar;
