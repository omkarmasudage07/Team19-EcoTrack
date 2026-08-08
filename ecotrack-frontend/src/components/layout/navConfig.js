import {
  FiHome, FiTruck, FiClock, FiAward, FiBell, FiUser,
  FiPackage, FiBox, FiShoppingBag, FiDollarSign,
  FiShoppingCart, FiUsers, FiFileText, FiBarChart2, FiSettings, FiCheckSquare, FiGift,
} from 'react-icons/fi';

export const NAV_BY_ROLE = {
  CITIZEN: [
    { to: '/citizen', label: 'Dashboard', icon: FiHome, end: true },
    { to: '/citizen/schedule-pickup', label: 'Schedule Pickup', icon: FiTruck },
    { to: '/citizen/pickups', label: 'Pickup History', icon: FiClock },
    { to: '/citizen/ecopoints', label: 'Rewards Marketplace', icon: FiGift },
    { to: '/citizen/notifications', label: 'Notifications', icon: FiBell },
    { to: '/citizen/profile', label: 'Profile', icon: FiUser },
  ],
  RECYCLER: [
    { to: '/recycler', label: 'Dashboard', icon: FiHome, end: true },
    { to: '/recycler/pickups/available', label: 'Available Pickups', icon: FiTruck },
    { to: '/recycler/pickups/assigned', label: 'My Pickups', icon: FiPackage },
    { to: '/recycler/materials', label: 'Material Listings', icon: FiBox },
    { to: '/recycler/orders', label: 'Orders', icon: FiShoppingBag },
    { to: '/recycler/notifications', label: 'Notifications', icon: FiBell },
    { to: '/recycler/profile', label: 'Profile', icon: FiUser },
  ],
  INDUSTRY: [
    { to: '/industry', label: 'Dashboard', icon: FiHome, end: true },
    { to: '/industry/marketplace', label: 'Marketplace', icon: FiShoppingCart },
    { to: '/industry/orders', label: 'My Orders', icon: FiShoppingBag },
    { to: '/industry/notifications', label: 'Notifications', icon: FiBell },
    { to: '/industry/profile', label: 'Profile', icon: FiUser },
  ],
  ADMIN: [
    { to: '/admin', label: 'Dashboard', icon: FiHome, end: true },
    { to: '/admin/rewards', label: 'Manage Rewards', icon: FiGift },
    { to: '/admin/users', label: 'Users', icon: FiUsers },
    { to: '/admin/recycler-applications', label: 'Recycler Applications', icon: FiCheckSquare },
    { to: '/admin/industry-applications', label: 'Industry Applications', icon: FiCheckSquare },
    { to: '/admin/pickups', label: 'Pickups', icon: FiTruck },
    { to: '/admin/materials', label: 'Materials', icon: FiBox },
    { to: '/admin/orders', label: 'Orders', icon: FiShoppingBag },
    { to: '/admin/reports', label: 'Reports', icon: FiBarChart2 },
    { to: '/admin/audit-logs', label: 'Audit Logs', icon: FiFileText },
    { to: '/admin/notifications', label: 'Notifications', icon: FiBell },
    { to: '/admin/settings', label: 'Settings', icon: FiSettings },
  ],
};
