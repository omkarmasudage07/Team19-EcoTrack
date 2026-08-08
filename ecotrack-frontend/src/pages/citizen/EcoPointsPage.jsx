import React, { useEffect, useState, useCallback, useMemo } from 'react';
import { 
  FiAward, FiTrendingUp, FiTrendingDown, FiGift, FiClock, FiShoppingBag, 
  FiSearch, FiFilter, FiCheckCircle, FiCopy, FiCheck, FiPackage, FiAlertCircle 
} from 'react-icons/fi';
import PageHeader from '../../components/common/PageHeader';
import StatCard from '../../components/cards/StatCard';
import { LoadingSpinner, EmptyState, ErrorState } from '../../components/common/Feedback';
import { ecoPointsService } from '../../services/materialService';
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
  { id: 'ALL', label: 'All Rewards' },
  { id: 'ECO_PRODUCT', label: 'Eco Products' },
  { id: 'GIFT_CARD', label: 'Gift Vouchers' },
  { id: 'TREE_PLANTATION', label: 'Tree Planting' },
  { id: 'DISCOUNT_COUPON', label: 'Discounts' },
  { id: 'CERTIFICATE', label: 'Certificates' }
];

const EcoPointsPage = () => {
  const [activeTab, setActiveTab] = useState('marketplace'); // 'marketplace', 'orders', 'history'
  const [wallet, setWallet] = useState(null);
  const [transactions, setTransactions] = useState([]);
  const [rewards, setRewards] = useState([]);
  const [myOrders, setMyOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  // Marketplace filter & sort states
  const [selectedCategory, setSelectedCategory] = useState('ALL');
  const [searchQuery, setSearchQuery] = useState('');
  const [sortBy, setSortBy] = useState('lowest'); // 'lowest', 'highest'
  const [inStockOnly, setInStockOnly] = useState(false);

  // Redemption Modal State
  const [selectedReward, setSelectedReward] = useState(null);
  const [deliveryAddress, setDeliveryAddress] = useState('');
  const [redeeming, setRedeeming] = useState(false);
  const [redeemError, setRedeemError] = useState('');
  const [redeemSuccess, setRedeemSuccess] = useState(null);
  const [copiedCode, setCopiedCode] = useState(null);

  const loadData = useCallback(async () => {
    setLoading(true);
    setError(false);
    try {
      const [walletRes, txRes, rewardsRes, ordersRes] = await Promise.allSettled([
        ecoPointsService.getWallet(),
        ecoPointsService.getTransactions({ size: 50 }),
        rewardService.getRewards({ active: true, size: 50 }),
        rewardService.getMyOrders({ size: 50 })
      ]);

      if (walletRes.status === 'fulfilled') setWallet(walletRes.value);
      if (txRes.status === 'fulfilled') setTransactions(txRes.value?.content || []);
      if (rewardsRes.status === 'fulfilled') setRewards(rewardsRes.value?.content || []);
      if (ordersRes.status === 'fulfilled') setMyOrders(ordersRes.value?.content || []);

      if (walletRes.status === 'rejected' && txRes.status === 'rejected') {
        setError(true);
      }
    } catch (e) {
      setError(true);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadData();
  }, [loadData]);

  const handleRedeemSubmit = async (e) => {
    e.preventDefault();
    if (!selectedReward) return;
    setRedeeming(true);
    setRedeemError('');

    try {
      const order = await rewardService.redeemReward(selectedReward.id, {
        deliveryAddress: deliveryAddress.trim() || undefined
      });
      setRedeemSuccess(order);
      setSelectedReward(null);
      setDeliveryAddress('');
      loadData(); // Refresh wallet, orders, and rewards stock
    } catch (err) {
      setRedeemError(err.response?.data?.message || 'Failed to redeem reward. Please check your EcoPoints balance.');
    } finally {
      setRedeeming(false);
    }
  };

  const copyVoucher = (code) => {
    navigator.clipboard.writeText(code);
    setCopiedCode(code);
    setTimeout(() => setCopiedCode(null), 2500);
  };

  const filteredRewards = useMemo(() => {
    return rewards
      .filter((r) => {
        if (selectedCategory !== 'ALL' && r.category !== selectedCategory) return false;
        if (inStockOnly && r.stockQuantity <= 0) return false;
        if (searchQuery) {
          const q = searchQuery.toLowerCase();
          return r.title.toLowerCase().includes(q) || (r.description && r.description.toLowerCase().includes(q));
        }
        return true;
      })
      .sort((a, b) => {
        if (sortBy === 'lowest') return a.pointsRequired - b.pointsRequired;
        if (sortBy === 'highest') return b.pointsRequired - a.pointsRequired;
        return 0;
      });
  }, [rewards, selectedCategory, searchQuery, sortBy, inStockOnly]);

  if (loading) return <LoadingSpinner label="Loading Eco Wallet & Rewards Marketplace..." />;
  if (error) return <ErrorState retry={loadData} message="Unable to load Eco Wallet data. Please try again." />;

  const currentBalance = wallet?.currentBalance ?? 0;
  const totalEarned = wallet?.totalEarned ?? currentBalance;
  const totalRedeemed = wallet?.totalRedeemed ?? 0;

  return (
    <div>
      <PageHeader
        title="Eco Wallet & Citizen Rewards Marketplace"
        subtitle="Earn EcoPoints for recycling e-waste and redeem them for exciting eco-friendly rewards"
      />

      {/* Wallet Stats Summary */}
      <div className="row g-3 mb-4">
        <div className="col-sm-6 col-lg-4">
          <StatCard
            icon={FiAward}
            label="Current EcoPoints Balance"
            value={`${currentBalance} pts`}
            hint="Available balance to spend"
            color="success"
          />
        </div>
        <div className="col-sm-6 col-lg-4">
          <StatCard
            icon={FiTrendingUp}
            label="Total Points Earned"
            value={`${totalEarned} pts`}
            hint="Lifetime recycling rewards"
            color="primary"
          />
        </div>
        <div className="col-sm-6 col-lg-4">
          <StatCard
            icon={FiTrendingDown}
            label="Total Points Redeemed"
            value={`${totalRedeemed} pts`}
            hint="Spent on citizen rewards"
            color="purple"
          />
        </div>
      </div>

      {/* Redeemed Success Alert Banner */}
      {redeemSuccess && (
        <div className="alert alert-success alert-dismissible fade show d-flex align-items-center gap-3 mb-4 shadow-sm border-0 p-3" role="alert">
          <FiCheckCircle size={32} className="text-success flex-shrink-0" />
          <div>
            <h6 className="fw-bold mb-1">Reward Redeemed Successfully! 🎉</h6>
            <div className="small">
              You redeemed <strong>{redeemSuccess.rewardTitle}</strong> for <strong>{redeemSuccess.pointsSpent} EcoPoints</strong>. 
              Order #: <span className="badge bg-dark">{redeemSuccess.orderNumber}</span>
              {redeemSuccess.voucherCode && (
                <div className="mt-1">
                  Voucher Code: <code className="bg-light px-2 py-1 rounded fw-bold text-success">{redeemSuccess.voucherCode}</code>
                </div>
              )}
            </div>
          </div>
          <button type="button" className="btn-close" onClick={() => setRedeemSuccess(null)}></button>
        </div>
      )}

      {/* Main Tab Navigation */}
      <ul className="nav nav-tabs border-bottom mb-4">
        <li className="nav-item">
          <button
            className={`nav-link fw-semibold px-4 py-2 ${activeTab === 'marketplace' ? 'active et-nav-link-active' : 'text-secondary'}`}
            onClick={() => setActiveTab('marketplace')}
          >
            <FiGift className="me-2" />
            Rewards Marketplace
          </button>
        </li>
        <li className="nav-item">
          <button
            className={`nav-link fw-semibold px-4 py-2 ${activeTab === 'orders' ? 'active et-nav-link-active' : 'text-secondary'}`}
            onClick={() => setActiveTab('orders')}
          >
            <FiPackage className="me-2" />
            My Reward Orders ({myOrders.length})
          </button>
        </li>
        <li className="nav-item">
          <button
            className={`nav-link fw-semibold px-4 py-2 ${activeTab === 'history' ? 'active et-nav-link-active' : 'text-secondary'}`}
            onClick={() => setActiveTab('history')}
          >
            <FiClock className="me-2" />
            Points Ledger ({transactions.length})
          </button>
        </li>
      </ul>

      {/* TAB 1: REWARDS MARKETPLACE */}
      {activeTab === 'marketplace' && (
        <div>
          {/* Category Filter Pills & Search Controls */}
          <div className="card border-0 shadow-sm mb-4">
            <div className="card-body">
              <div className="d-flex flex-wrap gap-2 mb-3">
                {CATEGORIES.map((cat) => (
                  <button
                    key={cat.id}
                    className={`btn btn-sm ${selectedCategory === cat.id ? 'btn-success fw-bold' : 'btn-outline-secondary'}`}
                    onClick={() => setSelectedCategory(cat.id)}
                  >
                    {cat.label}
                  </button>
                ))}
              </div>

              <div className="row g-2 align-items-center">
                <div className="col-md-5">
                  <div className="input-group input-group-sm">
                    <span className="input-group-text bg-white"><FiSearch /></span>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="Search rewards by name..."
                      value={searchQuery}
                      onChange={(e) => setSearchQuery(e.target.value)}
                    />
                  </div>
                </div>
                <div className="col-md-4">
                  <div className="input-group input-group-sm">
                    <span className="input-group-text bg-white"><FiFilter /></span>
                    <select
                      className="form-select"
                      value={sortBy}
                      onChange={(e) => setSortBy(e.target.value)}
                    >
                      <option value="lowest">Sort: Lowest Points First</option>
                      <option value="highest">Sort: Highest Points First</option>
                    </select>
                  </div>
                </div>
                <div className="col-md-3">
                  <div className="form-check form-switch small">
                    <input
                      className="form-check-input"
                      type="checkbox"
                      id="inStockCheck"
                      checked={inStockOnly}
                      onChange={(e) => setInStockOnly(e.target.checked)}
                    />
                    <label className="form-check-label fw-semibold" htmlFor="inStockCheck">
                      In Stock Only
                    </label>
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* Rewards Grid */}
          {filteredRewards.length === 0 ? (
            <EmptyState
              icon={FiGift}
              title="No rewards found"
              message="No reward items match your selected filters. Try choosing a different category or search term."
            />
          ) : (
            <div className="row g-3">
              {filteredRewards.map((reward) => {
                const canAfford = currentBalance >= reward.pointsRequired;
                const isOutOfStock = reward.stockQuantity <= 0;

                return (
                  <div key={reward.id} className="col-sm-6 col-md-4 col-lg-3">
                    <div className="card h-100 border-0 shadow-sm overflow-hidden hover-shadow transition">
                      <div className="position-relative bg-light text-center p-3" style={{ height: '160px' }}>
                        {reward.imageUrl ? (
                          <img
                            src={reward.imageUrl}
                            alt={reward.title}
                            className="img-fluid h-100 object-fit-cover rounded"
                            onError={(e) => { e.target.style.display = 'none'; }}
                          />
                        ) : (
                          <div className="h-100 d-flex align-items-center justify-content-center">
                            <FiGift size={48} className="text-muted opacity-50" />
                          </div>
                        )}
                        <span className="position-absolute top-0 start-0 m-2 badge bg-success shadow-sm">
                          {reward.pointsRequired} pts
                        </span>
                        <span className={`position-absolute top-0 end-0 m-2 badge ${isOutOfStock ? 'bg-danger' : 'bg-dark'}`}>
                          {isOutOfStock ? 'Out of Stock' : `${reward.stockQuantity} Left`}
                        </span>
                      </div>

                      <div className="card-body d-flex flex-column">
                        <div className="small text-muted text-uppercase fw-semibold mb-1">
                          {reward.category?.replace('_', ' ')}
                        </div>
                        <h6 className="fw-bold mb-2 text-dark">{reward.title}</h6>
                        <p className="small text-muted flex-grow-1 line-clamp-2 mb-3">
                          {reward.description || 'Exclusive citizen reward for e-waste recycling.'}
                        </p>

                        <button
                          className={`btn btn-sm w-100 fw-bold ${
                            isOutOfStock 
                              ? 'btn-secondary disabled' 
                              : canAfford 
                              ? 'btn-success' 
                              : 'btn-outline-danger disabled'
                          }`}
                          onClick={() => {
                            setSelectedReward(reward);
                            setRedeemError('');
                          }}
                          disabled={isOutOfStock || !canAfford}
                        >
                          {isOutOfStock
                            ? 'Out of Stock'
                            : canAfford
                            ? 'Redeem Reward'
                            : `Need ${reward.pointsRequired - currentBalance} more pts`}
                        </button>
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>
      )}

      {/* TAB 2: MY REWARD ORDERS */}
      {activeTab === 'orders' && (
        <div className="card border-0 shadow-sm">
          <div className="card-body">
            <h6 className="fw-bold mb-3">Redeemed Rewards History</h6>
            {myOrders.length === 0 ? (
              <EmptyState
                icon={FiShoppingBag}
                title="No reward orders yet"
                message="Redeem your EcoPoints for eco products, vouchers, or tree planting to see orders here."
              />
            ) : (
              <div className="table-responsive">
                <table className="table align-middle mb-0">
                  <thead>
                    <tr className="text-muted small text-uppercase">
                      <th>Order #</th>
                      <th>Reward Item</th>
                      <th>Points Spent</th>
                      <th>Voucher Code</th>
                      <th>Status</th>
                      <th>Redemption Date</th>
                    </tr>
                  </thead>
                  <tbody>
                    {myOrders.map((ord) => (
                      <tr key={ord.id}>
                        <td className="fw-bold small">{ord.orderNumber}</td>
                        <td className="fw-semibold small">{ord.rewardTitle}</td>
                        <td className="small text-danger fw-bold">-{ord.pointsSpent} pts</td>
                        <td className="small">
                          {ord.voucherCode ? (
                            <span className="d-inline-flex align-items-center gap-1 bg-light border px-2 py-1 rounded">
                              <code className="text-success fw-bold">{ord.voucherCode}</code>
                              <button
                                className="btn btn-sm btn-link p-0 text-muted ms-1"
                                title="Copy voucher code"
                                onClick={() => copyVoucher(ord.voucherCode)}
                              >
                                {copiedCode === ord.voucherCode ? <FiCheck className="text-success" /> : <FiCopy />}
                              </button>
                            </span>
                          ) : (
                            <span className="text-muted small">N/A (Physical/Digital)</span>
                          )}
                        </td>
                        <td>
                          <span className={`et-badge ${
                            ord.status === 'DELIVERED' ? 'et-badge-green' :
                            ord.status === 'CONFIRMED' || ord.status === 'SHIPPED' ? 'et-badge-blue' : 'et-badge-slate'
                          }`}>
                            <span className="et-badge-dot" />
                            {ord.status}
                          </span>
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

      {/* TAB 3: POINTS LEDGER */}
      {activeTab === 'history' && (
        <div className="card border-0 shadow-sm">
          <div className="card-body">
            <h6 className="fw-bold mb-3">EcoPoints Transaction Ledger</h6>
            {transactions.length === 0 ? (
              <EmptyState
                icon={FiClock}
                title="No transactions recorded yet"
                message="Schedule an e-waste pickup to earn EcoPoints."
              />
            ) : (
              <div className="table-responsive">
                <table className="table align-middle mb-0">
                  <thead>
                    <tr className="text-muted small text-uppercase">
                      <th>Type</th>
                      <th>Description</th>
                      <th>Points</th>
                      <th>Date & Time</th>
                    </tr>
                  </thead>
                  <tbody>
                    {transactions.map((tx) => {
                      const isEarned = tx.transactionType === 'EARNED' || tx.transactionType === 'CREDIT';
                      return (
                        <tr key={tx.id || Math.random()}>
                          <td>
                            <span className={`et-badge ${isEarned ? 'et-badge-green' : 'et-badge-red'}`}>
                              <span className="et-badge-dot" />
                              {tx.transactionType}
                            </span>
                          </td>
                          <td className="small fw-semibold">{tx.description}</td>
                          <td className={`small fw-bold ${isEarned ? 'text-success' : 'text-danger'}`}>
                            {isEarned ? `+${tx.points}` : `-${tx.points}`} pts
                          </td>
                          <td className="small text-muted">{formatDate(tx.transactionDate)}</td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </div>
      )}

      {/* REDEMPTION CONFIRMATION MODAL */}
      {selectedReward && (
        <div className="modal fade show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }} tabIndex="-1">
          <div className="modal-dialog modal-dialog-centered">
            <div className="modal-content border-0 shadow-lg">
              <div className="modal-header bg-success text-white">
                <h5 className="modal-title fw-bold d-flex align-items-center gap-2">
                  <FiGift /> Confirm Reward Redemption
                </h5>
                <button
                  type="button"
                  className="btn-close btn-close-white"
                  onClick={() => setSelectedReward(null)}
                ></button>
              </div>
              <form onSubmit={handleRedeemSubmit}>
                <div className="modal-body p-4">
                  {redeemError && (
                    <div className="alert alert-danger small d-flex align-items-center gap-2 mb-3">
                      <FiAlertCircle size={20} className="flex-shrink-0" />
                      <div>{redeemError}</div>
                    </div>
                  )}

                  <div className="d-flex align-items-center gap-3 p-3 bg-light rounded mb-3 border">
                    <FiGift size={40} className="text-success flex-shrink-0" />
                    <div>
                      <h6 className="fw-bold mb-1">{selectedReward.title}</h6>
                      <div className="small text-muted">{selectedReward.category?.replace('_', ' ')}</div>
                      <div className="badge bg-success mt-1">{selectedReward.pointsRequired} EcoPoints</div>
                    </div>
                  </div>

                  <div className="card bg-light border-0 mb-3">
                    <div className="card-body p-3 small">
                      <div className="d-flex justify-content-between mb-1">
                        <span className="text-muted">Current Wallet Balance:</span>
                        <span className="fw-bold">{currentBalance} pts</span>
                      </div>
                      <div className="d-flex justify-content-between mb-1 text-danger">
                        <span>Points Required:</span>
                        <span className="fw-bold">-{selectedReward.pointsRequired} pts</span>
                      </div>
                      <hr className="my-2" />
                      <div className="d-flex justify-content-between fw-bold text-success">
                        <span>Balance After Redemption:</span>
                        <span>{currentBalance - selectedReward.pointsRequired} pts</span>
                      </div>
                    </div>
                  </div>

                  <div className="mb-3">
                    <label className="form-label small fw-semibold">Delivery Address / Notes (Optional)</label>
                    <textarea
                      className="form-control form-control-sm"
                      rows="2"
                      placeholder="Enter shipping address for physical items, or leave blank for digital vouchers."
                      value={deliveryAddress}
                      onChange={(e) => setDeliveryAddress(e.target.value)}
                    ></textarea>
                  </div>
                </div>

                <div className="modal-footer bg-light border-top-0">
                  <button
                    type="button"
                    className="btn btn-sm btn-outline-secondary"
                    onClick={() => setSelectedReward(null)}
                    disabled={redeeming}
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    className="btn btn-sm btn-success fw-bold px-4"
                    disabled={redeeming}
                  >
                    {redeeming ? 'Redeeming...' : 'Confirm Redemption'}
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

export default EcoPointsPage;
