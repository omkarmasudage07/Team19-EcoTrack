import api from './api';

export const rewardService = {
  // Public / Citizen / Admin catalog endpoints
  getRewards: async (params = {}) => {
    const response = await api.get('/rewards', { params });
    return response.data.data;
  },

  getRewardById: async (id) => {
    const response = await api.get(`/rewards/${id}`);
    return response.data.data;
  },

  redeemReward: async (id, data = {}) => {
    const response = await api.post(`/rewards/${id}/redeem`, data);
    return response.data.data;
  },

  getMyOrders: async (params = {}) => {
    const response = await api.get('/rewards/my-orders', { params });
    return response.data.data;
  },

  // Admin Management Endpoints
  createReward: async (rewardData) => {
    const response = await api.post('/rewards', rewardData);
    return response.data.data;
  },

  updateReward: async (id, rewardData) => {
    const response = await api.put(`/rewards/${id}`, rewardData);
    return response.data.data;
  },

  deleteReward: async (id) => {
    const response = await api.delete(`/rewards/${id}`);
    return response.data.data;
  },

  toggleActive: async (id) => {
    const response = await api.patch(`/rewards/${id}/toggle-active`);
    return response.data.data;
  },

  getAdminOrders: async (params = {}) => {
    const response = await api.get('/rewards/admin/orders', { params });
    return response.data.data;
  },

  updateOrderStatus: async (id, status) => {
    const response = await api.put(`/rewards/admin/orders/${id}/status`, null, { params: { status } });
    return response.data.data;
  },

  getAdminReports: async () => {
    const response = await api.get('/rewards/admin/reports');
    return response.data.data;
  },

  // EcoPoint Rules Endpoints
  getRules: async () => {
    const response = await api.get('/ecopoint-rules');
    return response.data.data;
  },

  createRule: async (ruleData) => {
    const response = await api.post('/ecopoint-rules', ruleData);
    return response.data.data;
  },

  updateRule: async (id, ruleData) => {
    const response = await api.put(`/ecopoint-rules/${id}`, ruleData);
    return response.data.data;
  },

  deleteRule: async (id) => {
    const response = await api.delete(`/ecopoint-rules/${id}`);
    return response.data.data;
  },
};

export default rewardService;
