import api from './api';

const authService = {
  register: (payload) => api.post('/auth/register', payload).then((r) => r.data.data),
  login: (payload) => api.post('/auth/login', payload).then((r) => r.data.data),
  logout: (refreshToken) => api.post('/auth/logout', { refreshToken }).then((r) => r.data.data),
  forgotPassword: (email) => api.post('/auth/forgot-password', { email }).then((r) => r.data.data),
  resetPassword: (payload) => api.post('/auth/reset-password', payload).then((r) => r.data.data),
  changePassword: (payload) => api.post('/auth/change-password', payload).then((r) => r.data.data),
  getCurrentUser: () => api.get('/auth/me').then((r) => r.data.data),
};

export default authService;
