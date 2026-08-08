import api from './api';

export const wasteCategoryService = {
  getActive: () => api.get('/waste-categories').then((r) => r.data.data),
  getAll: () => api.get('/waste-categories/all').then((r) => r.data.data),
  create: (payload) => api.post('/waste-categories', payload).then((r) => r.data.data),
  setActive: (id, active) => api.patch(`/waste-categories/${id}/active?active=${active}`).then((r) => r.data.data),
};

const pickupService = {
  // Citizen
  schedule: (payload) => api.post('/pickups', payload).then((r) => r.data.data),
  getMyPickups: (params) => api.get('/pickups/my', { params }).then((r) => r.data.data),
  cancel: (id) => api.patch(`/pickups/${id}/cancel`).then((r) => r.data.data),

  // Recycler
  getAvailable: (params) => api.get('/pickups/available', { params }).then((r) => r.data.data),
  getAssigned: (params) => api.get('/pickups/assigned', { params }).then((r) => r.data.data),
  accept: (id) => api.patch(`/pickups/${id}/accept`).then((r) => r.data.data),
  reject: (id) => api.patch(`/pickups/${id}/reject`).then((r) => r.data.data),
  updateStatus: (id, payload) => api.patch(`/pickups/${id}/status`, payload).then((r) => r.data.data),

  // Shared / Admin
  getDetail: (id) => api.get(`/pickups/${id}`).then((r) => r.data.data),
  getAll: (params) => api.get('/pickups', { params }).then((r) => r.data.data),
};

export default pickupService;
