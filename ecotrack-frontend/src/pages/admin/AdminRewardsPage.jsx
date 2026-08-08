import React, { useEffect, useState, useCallback } from 'react';
import { 
  FiAward, FiTrendingUp, FiTrendingDown, FiGift, FiPackage, FiSettings, 
  FiBarChart2, FiPlus, FiEdit, FiTrash2, FiCheckCircle, FiXCircle, FiAlertCircle 
} from 'react-icons/fi';
import PageHeader from '../../components/common/PageHeader';
import StatCard from '../../components/cards/StatCard';
import { LoadingSpinner, EmptyState, ErrorState } from '../../components/common/Feedback';
import rewardService from '../../services/rewardService';

const formatDate = (isoString) => {
  if (!isoString) return 'N/A';
  try {
    return new Date(isoString).toLocaleString('en-IN', {
      dateStyle: 'medium',
      timeStyle: 'short',
    });
  } catch (e) {
    return isoString;
  }
};

const CATEGORIES = [
  'ECO_PRODUCT',
  'GIFT_CARD',
  'TREE_PLANTATION',
  'DISCOUNT_COUPON',
  'CERTIFICATE'
];

const ORDER_STATUSES = [
  'PENDING',
  'CONFIRMED',
  'SHIPPED',
  'DELIVERED',
  'CANCELLED'
];

const AdminRewardsPage = () => {
  const [activeTab, setActiveTab] = useState('reports'); // 'reports', 'rewards', 'orders', 'rules'
  const [reports, setReports] = useState(null);
  const [rewards, setRewards] = useState([]);
  const [orders, setOrders] = useState([]);
  const [rules, setRules] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  // Reward Modal Form State
  const [showRewardModal, setShowRewardModal] = useState(false);
  const [editingReward, setEditingReward] = useState(null);
  const [rewardForm, setRewardForm] = useState({
    title: '',
    description: '',
    category: 'ECO_PRODUCT',
    pointsRequired: 100,
    stockQuantity: 50,
    imageUrl: '',
    active: true
  });

  // Rule Modal Form State
  const [showRuleModal, setShowRuleModal] = useState(false);
  const [editingRule, setEditingRule] = useState(null);
  const [ruleForm, setRuleForm] = useState({
    categoryName: '',
    pointsPerUnit: 50,
    ruleType: 'FLAT',
    description: '',
    active: true
  });

  const [saving, setSaving] = useState(false);
  const [modalError, setModalError] = useState('');

  const loadData = useCallback(async () => {
    setLoading(true);
    setError(false);
    try {
      const [reportsData, rewardsData, ordersData, rulesData] = await Promise.allSettled([
        rewardService.getAdminReports(),
        rewardService.getRewards({ size: 100 }),
        rewardService.getAdminOrders({ size: 100 }),
        rewardService.getRules()
      ]);

      if (reportsData.status === 'fulfilled') setReports(reportsData.value);
      if (rewardsData.status === 'fulfilled') setRewards(rewardsData.value?.content || []);
      if (ordersData.status === 'fulfilled') setOrders(ordersData.value?.content || []);
      if (rulesData.status === 'fulfilled') setRules(rulesData.value || []);
    } catch (e) {
      setError(true);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadData();
  }, [loadData]);

  // Open Add Reward Modal
  const handleOpenAddReward = () => {
    setEditingReward(null);
    setRewardForm({
      title: '',
      description: '',
      category: 'ECO_PRODUCT',
      pointsRequired: 100,
      stockQuantity: 50,
      imageUrl: '',
      active: true
    });
    setModalError('');
    setShowRewardModal(true);
  };

  // Open Edit Reward Modal
  const handleOpenEditReward = (reward) => {
    setEditingReward(reward);
    setRewardForm({
      title: reward.title,
      description: reward.description || '',
      category: reward.category,
      pointsRequired: reward.pointsRequired,
      stockQuantity: reward.stockQuantity,
      imageUrl: reward.imageUrl || '',
      active: reward.active
    });
    setModalError('');
    setShowRewardModal(true);
  };

  // Save Reward Submit
  const handleSaveReward = async (e) => {
    e.preventDefault();
    setSaving(true);
    setModalError('');
    try {
      if (editingReward) {
        await rewardService.updateReward(editingReward.id, rewardForm);
      } else {
        await rewardService.createReward(rewardForm);
      }
      setShowRewardModal(false);
      loadData();
    } catch (err) {
      setModalError(err.response?.data?.message || 'Failed to save reward item.');
    } finally {
      setSaving(false);
    }
  };

  // Delete Reward
  const handleDeleteReward = async (id) => {
    if (!window.confirm('Are you sure you want to delete this reward item?')) return;
    try {
      await rewardService.deleteReward(id);
      loadData();
    } catch (err) {
      alert('Failed to delete reward.');
    }
  };

  // Toggle Active Reward
  const handleToggleRewardActive = async (id) => {
    try {
      await rewardService.toggleActive(id);
      loadData();
    } catch (err) {
      alert('Failed to update reward status.');
    }
  };

  // Open Add Rule Modal
  const handleOpenAddRule = () => {
    setEditingRule(null);
    setRuleForm({
      categoryName: '',
      pointsPerUnit: 50,
      ruleType: 'FLAT',
      description: '',
      active: true
    });
    setModalError('');
    setShowRuleModal(true);
  };

  // Open Edit Rule Modal
  const handleOpenEditRule = (rule) => {
    setEditingRule(rule);
    setRuleForm({
      categoryName: rule.categoryName,
      pointsPerUnit: rule.pointsPerUnit,
      ruleType: rule.ruleType || 'FLAT',
      description: rule.description || '',
      active: rule.active
    });
    setModalError('');
    setShowRuleModal(true);
  };

  // Save Rule Submit
  const handleSaveRule = async (e) => {
    e.preventDefault();
    setSaving(true);
    setModalError('');
    try {
      if (editingRule) {
        await rewardService.updateRule(editingRule.id, ruleForm);
      } else {
        await rewardService.createRule(ruleForm);
      }
      setShowRuleModal(false);
      loadData();
    } catch (err) {
      setModalError(err.response?.data?.message || 'Failed to save EcoPoint rule.');
    } finally {
      setSaving(false);
    }
  };

  // Delete Rule
  const handleDeleteRule = async (id) => {
    if (!window.confirm('Are you sure you want to delete this EcoPoint rule?')) return;
    try {
      await rewardService.deleteRule(id);
      loadData();
    } catch (err) {
      alert('Failed to delete rule.');
    }
  };

  // Update Order Status
  const handleUpdateOrderStatus = async (orderId, newStatus) => {
    try {
      await rewardService.updateOrderStatus(orderId, newStatus);
      loadData();
    } catch (err) {
      alert('Failed to update order status.');
    }
  };

  if (loading) return <LoadingSpinner label="Loading Admin Rewards Portal..." />;
  if (error) return <ErrorState retry={loadData} message="Unable to load Admin Rewards data." />;

  return (
    <div>
      <PageHeader
        title="Admin Rewards & Eco Wallet Portal"
        subtitle="Manage reward catalog, update redemption order statuses, configure category points rules, and view analytics"
      />

      {/* Top Stat Cards */}
      <div className="row g-3 mb-4">
        <div className="col-sm-6 col-lg-3">
          <StatCard
            icon={FiTrendingUp}
            label="Total Points Issued"
            value={`${reports?.totalPointsIssued ?? 0} pts`}
            hint="System-wide recycling points"
            color="success"
          />
        </div>
        <div className="col-sm-6 col-lg-3">
          <StatCard
            icon={FiTrendingDown}
            label="Total Points Redeemed"
            value={`${reports?.totalPointsRedeemed ?? 0} pts`}
            hint="Spent on citizen rewards"
            color="purple"
          />
        </div>
        <div className="col-sm-6 col-lg-3">
          <StatCard
            icon={FiAward}
            label="Active Wallet Balance"
            value={`${reports?.currentSystemBalance ?? 0} pts`}
            hint="Current unspent points"
            color="primary"
          />
        </div>
        <div className="col-sm-6 col-lg-3">
          <StatCard
            icon={FiPackage}
            label="Redemption Orders"
            value={reports?.totalOrdersCount ?? 0}
            hint="Total citizen redemptions"
            color="info"
          />
        </div>
      </div>

      {/* Main Tab Navigation */}
      <ul className="nav nav-tabs border-bottom mb-4">
        <li className="nav-item">
          <button
            className={`nav-link fw-semibold px-4 py-2 ${activeTab === 'reports' ? 'active et-nav-link-active' : 'text-secondary'}`}
            onClick={() => setActiveTab('reports')}
          >
            <FiBarChart2 className="me-2" />
            Analytics & Reports
          </button>
        </li>
        <li className="nav-item">
          <button
            className={`nav-link fw-semibold px-4 py-2 ${activeTab === 'rewards' ? 'active et-nav-link-active' : 'text-secondary'}`}
            onClick={() => setActiveTab('rewards')}
          >
            <FiGift className="me-2" />
            Manage Rewards ({rewards.length})
          </button>
        </li>
        <li className="nav-item">
          <button
            className={`nav-link fw-semibold px-4 py-2 ${activeTab === 'orders' ? 'active et-nav-link-active' : 'text-secondary'}`}
            onClick={() => setActiveTab('orders')}
          >
            <FiPackage className="me-2" />
            Redemption Orders ({orders.length})
          </button>
        </li>
        <li className="nav-item">
          <button
            className={`nav-link fw-semibold px-4 py-2 ${activeTab === 'rules' ? 'active et-nav-link-active' : 'text-secondary'}`}
            onClick={() => setActiveTab('rules')}
          >
            <FiSettings className="me-2" />
            EcoPoint Category Rules ({rules.length})
          </button>
        </li>
      </ul>

      {/* TAB 1: ANALYTICS & REPORTS */}
      {activeTab === 'reports' && (
        <div className="row g-3">
          <div className="col-md-6">
            <div className="card border-0 shadow-sm h-100">
              <div className="card-body">
                <h6 className="fw-bold mb-3 d-flex align-items-center gap-2">
                  <FiBarChart2 className="text-success" /> System EcoPoints Summary
                </h6>
                <ul className="list-group list-group-flush small">
                  <li className="list-group-item d-flex justify-content-between align-items-center">
                    Total EcoPoints Issued Across System
                    <span className="fw-bold text-success">+{reports?.totalPointsIssued ?? 0} pts</span>
                  </li>
                  <li className="list-group-item d-flex justify-content-between align-items-center">
                    Total EcoPoints Redeemed by Citizens
                    <span className="fw-bold text-danger">-{reports?.totalPointsRedeemed ?? 0} pts</span>
                  </li>
                  <li className="list-group-item d-flex justify-content-between align-items-center">
                    Current Citizen Active Balance
                    <span className="fw-bold text-primary">{reports?.currentSystemBalance ?? 0} pts</span>
                  </li>
                  <li className="list-group-item d-flex justify-content-between align-items-center">
                    Active Reward Items Available
                    <span className="badge bg-success">{reports?.activeRewardsCount ?? 0} / {reports?.totalRewardsCount ?? 0}</span>
                  </li>
                  <li className="list-group-item d-flex justify-content-between align-items-center">
                    Most Popular Redeemed Reward
                    <span className="fw-bold text-dark">{reports?.mostRedeemedReward || 'N/A'}</span>
                  </li>
                </ul>
              </div>
            </div>
          </div>

          <div className="col-md-6">
            <div className="card border-0 shadow-sm h-100">
              <div className="card-body">
                <h6 className="fw-bold mb-3 d-flex align-items-center gap-2">
                  <FiGift className="text-primary" /> Top Redeemed Citizen Rewards
                </h6>
                {(!reports?.topRewards || reports.topRewards.length === 0) ? (
                  <div className="text-muted small py-4 text-center">No redemptions recorded yet.</div>
                ) : (
                  <div className="table-responsive">
                    <table className="table table-sm align-middle mb-0">
                      <thead>
                        <tr className="text-muted small text-uppercase">
                          <th>Reward Title</th>
                          <th className="text-end">Redemptions</th>
                        </tr>
                      </thead>
                      <tbody>
                        {reports.topRewards.map((item, idx) => (
                          <tr key={idx}>
                            <td className="fw-semibold small">{item.rewardTitle}</td>
                            <td className="text-end fw-bold text-success">{item.redemptionCount} orders</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      )}

      {/* TAB 2: MANAGE REWARDS */}
      {activeTab === 'rewards' && (
        <div className="card border-0 shadow-sm">
          <div className="card-body">
            <div className="d-flex justify-content-between align-items-center mb-3">
              <h6 className="fw-bold mb-0">Citizen Rewards Catalog Management</h6>
              <button className="btn btn-sm btn-success fw-bold d-flex align-items-center gap-1" onClick={handleOpenAddReward}>
                <FiPlus /> Add New Reward
              </button>
            </div>

            {rewards.length === 0 ? (
              <EmptyState title="No rewards created" message="Click 'Add New Reward' to create items for citizens to redeem." />
            ) : (
              <div className="table-responsive">
                <table className="table align-middle mb-0">
                  <thead>
                    <tr className="text-muted small text-uppercase">
                      <th>Reward Item</th>
                      <th>Category</th>
                      <th>Points Cost</th>
                      <th>Stock Quantity</th>
                      <th>Status</th>
                      <th className="text-end">Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {rewards.map((rw) => (
                      <tr key={rw.id}>
                        <td>
                          <div className="d-flex align-items-center gap-2">
                            {rw.imageUrl ? (
                              <img src={rw.imageUrl} alt={rw.title} width="36" height="36" className="rounded object-fit-cover" />
                            ) : (
                              <div className="bg-light p-2 rounded text-muted"><FiGift size={18} /></div>
                            )}
                            <div>
                              <div className="fw-bold small">{rw.title}</div>
                              <div className="text-muted small text-truncate" style={{ maxWidth: '200px' }}>{rw.description}</div>
                            </div>
                          </div>
                        </td>
                        <td className="small text-uppercase fw-semibold">{rw.category?.replace('_', ' ')}</td>
                        <td className="small fw-bold text-success">{rw.pointsRequired} pts</td>
                        <td className="small fw-bold">{rw.stockQuantity} items</td>
                        <td>
                          <button
                            className={`btn btn-sm border-0 badge ${rw.active ? 'bg-success' : 'bg-secondary'}`}
                            onClick={() => handleToggleRewardActive(rw.id)}
                            title="Click to toggle active status"
                          >
                            {rw.active ? 'Active' : 'Inactive'}
                          </button>
                        </td>
                        <td className="text-end">
                          <button className="btn btn-sm btn-outline-primary me-1" onClick={() => handleOpenEditReward(rw)}>
                            <FiEdit />
                          </button>
                          <button className="btn btn-sm btn-outline-danger" onClick={() => handleDeleteReward(rw.id)}>
                            <FiTrash2 />
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </div>
      )}

      {/* TAB 3: REDEMPTION ORDERS */}
      {activeTab === 'orders' && (
        <div className="card border-0 shadow-sm">
          <div className="card-body">
            <h6 className="fw-bold mb-3">Citizen Redemption Orders Management</h6>
            {orders.length === 0 ? (
              <EmptyState title="No orders found" message="Citizen reward redemption orders will appear here." />
            ) : (
              <div className="table-responsive">
                <table className="table align-middle mb-0">
                  <thead>
                    <tr className="text-muted small text-uppercase">
                      <th>Order #</th>
                      <th>Citizen ID</th>
                      <th>Reward Item</th>
                      <th>Points</th>
                      <th>Delivery / Voucher</th>
                      <th>Status</th>
                      <th>Date</th>
                    </tr>
                  </thead>
                  <tbody>
                    {orders.map((ord) => (
                      <tr key={ord.id}>
                        <td className="fw-bold small">{ord.orderNumber}</td>
                        <td className="small">Citizen #{ord.citizenId}</td>
                        <td className="fw-semibold small">{ord.rewardTitle}</td>
                        <td className="small text-danger fw-bold">-{ord.pointsSpent} pts</td>
                        <td className="small text-muted" style={{ maxWidth: '200px' }}>
                          {ord.voucherCode ? <code className="text-success fw-bold">{ord.voucherCode}</code> : ord.deliveryAddress}
                        </td>
                        <td>
                          <select
                            className="form-select form-select-sm fw-bold border-secondary"
                            value={ord.status}
                            onChange={(e) => handleUpdateOrderStatus(ord.id, e.target.value)}
                          >
                            {ORDER_STATUSES.map((st) => (
                              <option key={st} value={st}>{st}</option>
                            ))}
                          </select>
                        </td>
                        <td className="small text-muted">{formatDate(ord.createdAt)}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </div>
      )}

      {/* TAB 4: ECOPOINT CATEGORY RULES */}
      {activeTab === 'rules' && (
        <div className="card border-0 shadow-sm">
          <div className="card-body">
            <div className="d-flex justify-content-between align-items-center mb-3">
              <h6 className="fw-bold mb-0">EcoPoint Rules per Waste Category</h6>
              <button className="btn btn-sm btn-success fw-bold d-flex align-items-center gap-1" onClick={handleOpenAddRule}>
                <FiPlus /> Add Category Rule
              </button>
            </div>

            {rules.length === 0 ? (
              <EmptyState title="No rules created" message="Add points rules for waste categories." />
            ) : (
              <div className="table-responsive">
                <table className="table align-middle mb-0">
                  <thead>
                    <tr className="text-muted small text-uppercase">
                      <th>Category Name</th>
                      <th>Points Awarded</th>
                      <th>Rule Type</th>
                      <th>Description</th>
                      <th>Status</th>
                      <th className="text-end">Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {rules.map((rl) => (
                      <tr key={rl.id}>
                        <td className="fw-bold small">{rl.categoryName}</td>
                        <td className="small fw-bold text-success">+{rl.pointsPerUnit} pts</td>
                        <td className="small badge bg-light text-dark border">{rl.ruleType}</td>
                        <td className="small text-muted">{rl.description || 'N/A'}</td>
                        <td>
                          <span className={`badge ${rl.active ? 'bg-success' : 'bg-secondary'}`}>
                            {rl.active ? 'Active' : 'Inactive'}
                          </span>
                        </td>
                        <td className="text-end">
                          <button className="btn btn-sm btn-outline-primary me-1" onClick={() => handleOpenEditRule(rl)}>
                            <FiEdit />
                          </button>
                          <button className="btn btn-sm btn-outline-danger" onClick={() => handleDeleteRule(rl.id)}>
                            <FiTrash2 />
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </div>
      )}

      {/* REWARD ADD/EDIT MODAL */}
      {showRewardModal && (
        <div className="modal fade show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }} tabIndex="-1">
          <div className="modal-dialog modal-dialog-centered">
            <div className="modal-content border-0 shadow-lg">
              <div className="modal-header bg-success text-white">
                <h5 className="modal-title fw-bold">
                  {editingReward ? 'Edit Reward Item' : 'Add New Reward Item'}
                </h5>
                <button type="button" className="btn-close btn-close-white" onClick={() => setShowRewardModal(false)}></button>
              </div>
              <form onSubmit={handleSaveReward}>
                <div className="modal-body p-4">
                  {modalError && (
                    <div className="alert alert-danger small d-flex align-items-center gap-2 mb-3">
                      <FiAlertCircle size={20} className="flex-shrink-0" />
                      <div>{modalError}</div>
                    </div>
                  )}

                  <div className="mb-3">
                    <label className="form-label small fw-semibold">Reward Title *</label>
                    <input
                      type="text"
                      className="form-control form-control-sm"
                      required
                      value={rewardForm.title}
                      onChange={(e) => setRewardForm({ ...rewardForm, title: e.target.value })}
                    />
                  </div>

                  <div className="row g-2 mb-3">
                    <div className="col-md-6">
                      <label className="form-label small fw-semibold">Category *</label>
                      <select
                        className="form-select form-select-sm"
                        value={rewardForm.category}
                        onChange={(e) => setRewardForm({ ...rewardForm, category: e.target.value })}
                      >
                        {CATEGORIES.map((cat) => (
                          <option key={cat} value={cat}>{cat.replace('_', ' ')}</option>
                        ))}
                      </select>
                    </div>
                    <div className="col-md-6">
                      <label className="form-label small fw-semibold">Points Required *</label>
                      <input
                        type="number"
                        className="form-control form-control-sm"
                        min="1"
                        required
                        value={rewardForm.pointsRequired}
                        onChange={(e) => setRewardForm({ ...rewardForm, pointsRequired: parseInt(e.target.value) || 1 })}
                      />
                    </div>
                  </div>

                  <div className="row g-2 mb-3">
                    <div className="col-md-6">
                      <label className="form-label small fw-semibold">Stock Quantity *</label>
                      <input
                        type="number"
                        className="form-control form-control-sm"
                        min="0"
                        required
                        value={rewardForm.stockQuantity}
                        onChange={(e) => setRewardForm({ ...rewardForm, stockQuantity: parseInt(e.target.value) || 0 })}
                      />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label small fw-semibold">Image URL (Optional)</label>
                      <input
                        type="text"
                        className="form-control form-control-sm"
                        placeholder="https://..."
                        value={rewardForm.imageUrl}
                        onChange={(e) => setRewardForm({ ...rewardForm, imageUrl: e.target.value })}
                      />
                    </div>
                  </div>

                  <div className="mb-3">
                    <label className="form-label small fw-semibold">Description</label>
                    <textarea
                      className="form-control form-control-sm"
                      rows="2"
                      value={rewardForm.description}
                      onChange={(e) => setRewardForm({ ...rewardForm, description: e.target.value })}
                    ></textarea>
                  </div>

                  <div className="form-check form-switch">
                    <input
                      className="form-check-input"
                      type="checkbox"
                      id="rewardActiveCheck"
                      checked={rewardForm.active}
                      onChange={(e) => setRewardForm({ ...rewardForm, active: e.target.checked })}
                    />
                    <label className="form-check-label small fw-semibold" htmlFor="rewardActiveCheck">
                      Active (Visible in Marketplace)
                    </label>
                  </div>
                </div>

                <div className="modal-footer bg-light border-top-0">
                  <button type="button" className="btn btn-sm btn-outline-secondary" onClick={() => setShowRewardModal(false)}>
                    Cancel
                  </button>
                  <button type="submit" className="btn btn-sm btn-success fw-bold px-4" disabled={saving}>
                    {saving ? 'Saving...' : 'Save Reward'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}

      {/* RULE ADD/EDIT MODAL */}
      {showRuleModal && (
        <div className="modal fade show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }} tabIndex="-1">
          <div className="modal-dialog modal-dialog-centered">
            <div className="modal-content border-0 shadow-lg">
              <div className="modal-header bg-success text-white">
                <h5 className="modal-title fw-bold">
                  {editingRule ? 'Edit Category Points Rule' : 'Add Category Points Rule'}
                </h5>
                <button type="button" className="btn-close btn-close-white" onClick={() => setShowRuleModal(false)}></button>
              </div>
              <form onSubmit={handleSaveRule}>
                <div className="modal-body p-4">
                  {modalError && (
                    <div className="alert alert-danger small d-flex align-items-center gap-2 mb-3">
                      <FiAlertCircle size={20} className="flex-shrink-0" />
                      <div>{modalError}</div>
                    </div>
                  )}

                  <div className="mb-3">
                    <label className="form-label small fw-semibold">Waste Category Name *</label>
                    <input
                      type="text"
                      className="form-control form-control-sm"
                      required
                      placeholder="e.g. Mobile Phones, Laptops, Plastic E-Waste"
                      value={ruleForm.categoryName}
                      onChange={(e) => setRuleForm({ ...ruleForm, categoryName: e.target.value })}
                      disabled={!!editingRule}
                    />
                  </div>

                  <div className="row g-2 mb-3">
                    <div className="col-md-6">
                      <label className="form-label small fw-semibold">Points Awarded *</label>
                      <input
                        type="number"
                        className="form-control form-control-sm"
                        min="1"
                        required
                        value={ruleForm.pointsPerUnit}
                        onChange={(e) => setRuleForm({ ...ruleForm, pointsPerUnit: parseInt(e.target.value) || 1 })}
                      />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label small fw-semibold">Rule Type *</label>
                      <select
                        className="form-select form-select-sm"
                        value={ruleForm.ruleType}
                        onChange={(e) => setRuleForm({ ...ruleForm, ruleType: e.target.value })}
                      >
                        <option value="FLAT">FLAT (Per Pickup/Item)</option>
                        <option value="PER_KG">PER_KG (Per Kilogram)</option>
                      </select>
                    </div>
                  </div>

                  <div className="mb-3">
                    <label className="form-label small fw-semibold">Description</label>
                    <input
                      type="text"
                      className="form-control form-control-sm"
                      placeholder="e.g. 100 points per recycled phone"
                      value={ruleForm.description}
                      onChange={(e) => setRuleForm({ ...ruleForm, description: e.target.value })}
                    />
                  </div>

                  <div className="form-check form-switch">
                    <input
                      className="form-check-input"
                      type="checkbox"
                      id="ruleActiveCheck"
                      checked={ruleForm.active}
                      onChange={(e) => setRuleForm({ ...ruleForm, active: e.target.checked })}
                    />
                    <label className="form-check-label small fw-semibold" htmlFor="ruleActiveCheck">
                      Rule Active
                    </label>
                  </div>
                </div>

                <div className="modal-footer bg-light border-top-0">
                  <button type="button" className="btn btn-sm btn-outline-secondary" onClick={() => setShowRuleModal(false)}>
                    Cancel
                  </button>
                  <button type="submit" className="btn btn-sm btn-success fw-bold px-4" disabled={saving}>
                    {saving ? 'Saving...' : 'Save Rule'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminRewardsPage;
