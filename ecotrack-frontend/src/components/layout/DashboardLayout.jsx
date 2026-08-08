import React, { useState } from 'react';
import { Outlet } from 'react-router-dom';
import Sidebar from './Sidebar';
import Navbar from './Navbar';
import { useAuth } from '../../context/AuthContext';

const DashboardLayout = () => {
  const { user } = useAuth();
  const [collapsed, setCollapsed] = useState(false);
  const [mobileOpen, setMobileOpen] = useState(false);

  return (
    <div className="et-shell">
      <Sidebar
        role={user?.role}
        collapsed={collapsed}
        mobileOpen={mobileOpen}
        onNavigate={() => setMobileOpen(false)}
      />
      <div className="et-main">
        <Navbar
          onToggleSidebar={() => setCollapsed((c) => !c)}
          onToggleMobileSidebar={() => setMobileOpen((o) => !o)}
        />
        <main className="et-content">
          <Outlet />
        </main>
      </div>
    </div>
  );
};

export default DashboardLayout;
