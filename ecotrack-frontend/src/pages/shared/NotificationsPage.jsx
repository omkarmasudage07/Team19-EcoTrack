import React, { useEffect, useState, useCallback } from 'react';
import { FiBell, FiCheck } from 'react-icons/fi';
import PageHeader from '../../components/common/PageHeader';
import { LoadingSpinner, EmptyState, ErrorState } from '../../components/common/Feedback';
import notificationService from '../../services/notificationService';
import { useNotifications } from '../../context/NotificationContext';

const TYPE_ICON_TINT = {
  PICKUP_ACCEPTED: 'et-badge-blue',
  PICKUP_COMPLETED: 'et-badge-green',
  PICKUP_CANCELLED: 'et-badge-red',
  ORDER_PLACED: 'et-badge-blue',
  PAYMENT_SUCCESSFUL: 'et-badge-green',
  ORDER_SHIPPED: 'et-badge-blue',
  ORDER_DELIVERED: 'et-badge-green',
  RECYCLER_APPROVED: 'et-badge-green',
  INDUSTRY_APPROVED: 'et-badge-green',
  GENERAL_ANNOUNCEMENT: 'et-badge-slate',
};

const timeAgo = (isoString) => {
  const diffMs = Date.now() - new Date(isoString).getTime();
  const mins = Math.floor(diffMs / 60000);
  if (mins < 1) return 'just now';
  if (mins < 60) return `${mins}m ago`;
  const hours = Math.floor(mins / 60);
  if (hours < 24) return `${hours}h ago`;
  return `${Math.floor(hours / 24)}d ago`;
};

const NotificationsPage = () => {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const { refreshUnreadCount } = useNotifications();

  const load = useCallback(async () => {
    setLoading(true);
    setError(false);
    try {
      const data = await notificationService.getMyNotifications({ size: 30, sort: 'createdAt,desc' });
      setNotifications(data?.content || []);
    } catch (e) {
      setError(true);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const handleMarkAsRead = async (id) => {
    setNotifications((prev) => prev.map((n) => (n.id === id ? { ...n, read: true } : n)));
    try {
      await notificationService.markAsRead(id);
      refreshUnreadCount();
    } catch (e) {
      load();
    }
  };

  const handleMarkAllAsRead = async () => {
    setNotifications((prev) => prev.map((n) => ({ ...n, read: true })));
    try {
      await notificationService.markAllAsRead();
      refreshUnreadCount();
    } catch (e) {
      load();
    }
  };

  const hasUnread = notifications.some((n) => !n.read);

  return (
    <div>
      <PageHeader
        title="Notifications"
        subtitle="Everything happening with your pickups and orders"
        action={
          hasUnread && (
            <button className="btn btn-outline-success btn-sm" onClick={handleMarkAllAsRead}>
              Mark all as read
            </button>
          )
        }
      />

      <div className="card border-0 shadow-sm">
        <div className="card-body p-0">
          {loading ? (
            <LoadingSpinner />
          ) : error ? (
            <ErrorState retry={load} />
          ) : notifications.length === 0 ? (
            <EmptyState icon={FiBell} title="No notifications yet" message="You'll see updates about your pickups and orders here." />
          ) : (
            <div>
              {notifications.map((n) => (
                <div
                  key={n.id}
                  className={`d-flex align-items-start gap-3 px-3 py-3 border-bottom ${!n.read ? 'bg-light' : ''}`}
                >
                  <span className={`et-badge ${TYPE_ICON_TINT[n.type] || 'et-badge-slate'} mt-1`}>
                    <span className="et-badge-dot" />
                  </span>
                  <div className="flex-fill">
                    <div className="d-flex justify-content-between align-items-start gap-2">
                      <div className="fw-semibold small">{n.title}</div>
                      <div className="text-muted small flex-shrink-0">{timeAgo(n.createdAt)}</div>
                    </div>
                    <div className="small text-muted">{n.message}</div>
                  </div>
                  {!n.read && (
                    <button
                      className="btn btn-sm btn-light border flex-shrink-0"
                      title="Mark as read"
                      onClick={() => handleMarkAsRead(n.id)}
                    >
                      <FiCheck size={14} />
                    </button>
                  )}
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default NotificationsPage;
