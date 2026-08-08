import api from './api';

export const materialCategoryService = {
  getActive: () => api.get('/categories').then((r) => r.data.data),
  getAll: () => api.get('/categories/all').then((r) => r.data.data),
  create: (payload) => api.post('/categories', payload).then((r) => r.data.data),
  setActive: (id, active) => api.patch(`/categories/${id}/active?active=${active}`).then((r) => r.data.data),
};

export const materialService = {
  create: (payload) => api.post('/materials', payload).then((r) => r.data.data),
  update: (id, payload) => api.put(`/materials/${id}`, payload).then((r) => r.data.data),
  remove: (id) => api.delete(`/materials/${id}`).then((r) => r.data.data),
  getMyMaterials: (params) => api.get('/materials/my', { params }).then((r) => r.data.data),
  browse: (params) => api.get('/materials', { params }).then((r) => r.data.data),
  getDetail: (id) => api.get(`/materials/${id}`).then((r) => r.data.data),
};

export const orderService = {
  place: (payload) => api.post('/orders', payload).then((r) => r.data.data),
  pay: (id, payload) => api.post(`/orders/${id}/pay`, payload).then((r) => r.data.data),
  createRazorpayOrder: (id) => api.post(`/orders/${id}/razorpay/create`).then((r) => r.data.data),
  verifyRazorpayPayment: (id, payload) => api.post(`/orders/${id}/razorpay/verify`, payload).then((r) => r.data.data),
  cancel: (id) => api.patch(`/orders/${id}/cancel`).then((r) => r.data.data),
  getMyOrders: (params) => api.get('/orders/my', { params }).then((r) => r.data.data),
  getReceivedOrders: (params) => api.get('/orders/received', { params }).then((r) => r.data.data),
  updateStatus: (id, payload) => api.patch(`/orders/${id}/status`, payload).then((r) => r.data.data),
  getDetail: (id) => api.get(`/orders/${id}`).then((r) => r.data.data),
  getAll: (params) => api.get('/orders', { params }).then((r) => r.data.data),
};

export const ecoPointsService = {
  getWallet: () => api.get('/ecopoints/wallet').then((r) => r.data.data),
  getTransactions: (params) => api.get('/ecopoints/transactions', { params }).then((r) => r.data.data),
};
