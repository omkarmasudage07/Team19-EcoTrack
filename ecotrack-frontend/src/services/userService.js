import api from './api';

export const citizenService = {
  getMyProfile: () => api.get('/citizens/me').then((r) => r.data.data),
  updateMyProfile: (payload) => api.put('/citizens/me', payload).then((r) => r.data.data),
  updatePhoto: (photoUrl) =>
    api.patch(`/citizens/me/photo?photoUrl=${encodeURIComponent(photoUrl)}`).then((r) => r.data.data),
};

export const recyclerService = {
  getMyProfile: () => api.get('/recyclers/me').then((r) => r.data.data),
  getById: (id) => api.get(`/recyclers/${id}`).then((r) => r.data.data),
  /** Materials store the owning Recycler's userId, not their Recycler-table id, so Material Details looks them up this way. */
  getByUserId: (userId) => api.get(`/recyclers/internal/${userId}`).then((r) => r.data.data),
  search: (params) => api.get('/recyclers', { params }).then((r) => r.data.data),
  suspend: (id, suspend) => api.patch(`/recyclers/${id}/suspend?suspend=${suspend}`).then((r) => r.data.data),
};

export const industryService = {
  getMyProfile: () => api.get('/industries/me').then((r) => r.data.data),
  getById: (id) => api.get(`/industries/${id}`).then((r) => r.data.data),
  search: (params) => api.get('/industries', { params }).then((r) => r.data.data),
  suspend: (id, suspend) => api.patch(`/industries/${id}/suspend?suspend=${suspend}`).then((r) => r.data.data),
};

export const recyclerApplicationService = {
  apply: (payload) => api.post('/recycler-applications/apply', payload).then((r) => r.data.data),
  getAll: (params) => api.get('/recycler-applications', { params }).then((r) => r.data.data),
  review: (id, payload) => api.patch(`/recycler-applications/${id}/review`, payload).then((r) => r.data.data),
};

export const industryApplicationService = {
  apply: (payload) => api.post('/industry-applications/apply', payload).then((r) => r.data.data),
  getAll: (params) => api.get('/industry-applications', { params }).then((r) => r.data.data),
  review: (id, payload) => api.patch(`/industry-applications/${id}/review`, payload).then((r) => r.data.data),
};

export const regionService = {
  getActive: () => api.get('/users/regions').then((r) => r.data.data),
  getAll: () => api.get('/users/regions/all').then((r) => r.data.data),
  create: (payload) => api.post('/users/regions', payload).then((r) => r.data.data),
  update: (id, payload) => api.put(`/users/regions/${id}`, payload).then((r) => r.data.data),
  toggle: (id, active) => api.patch(`/users/regions/${id}/toggle?active=${active}`).then((r) => r.data.data),
};
