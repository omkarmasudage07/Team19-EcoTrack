import api from './api';

const notificationService = {
  getMyNotifications: (params) => api.get('/notifications/my', { params }).then((r) => r.data.data),
  getUnreadCount: () => api.get('/notifications/unread-count').then((r) => r.data.data),
  markAsRead: (id) => api.patch(`/notifications/${id}/read`).then((r) => r.data.data),
  markAllAsRead: () => api.patch('/notifications/read-all').then((r) => r.data.data),
  getAuditLogs: (params) => api.get('/notifications/audit-logs', { params }).then((r) => r.data.data),
};

export default notificationService;
