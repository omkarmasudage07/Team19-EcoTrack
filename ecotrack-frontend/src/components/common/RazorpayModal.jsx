import React, { useState, useEffect } from 'react';
import {
  FiCheckCircle,
  FiXCircle,
  FiCreditCard,
  FiSmartphone,
  FiGlobe,
  FiShield,
  FiLock,
  FiCheck,
  FiRefreshCw,
  FiAlertCircle,
  FiX,
  FiZap,
} from 'react-icons/fi';

const POPULAR_BANKS = [
  { id: 'HDFC', name: 'HDFC Bank', code: 'HDFC' },
  { id: 'ICICI', name: 'ICICI Bank', code: 'ICIC' },
  { id: 'SBI', name: 'State Bank of India', code: 'SBIN' },
  { id: 'AXIS', name: 'Axis Bank', code: 'UTIB' },
  { id: 'KOTAK', name: 'Kotak Mahindra', code: 'KKBK' },
  { id: 'PNB', name: 'Punjab National Bank', code: 'PUNB' },
];

const UPI_APPS = [
  { id: 'gpay', name: 'Google Pay', handle: 'gpay' },
  { id: 'phonepe', name: 'PhonePe', handle: 'ybl' },
  { id: 'paytm', name: 'Paytm', handle: 'paytm' },
  { id: 'bhim', name: 'BHIM UPI', handle: 'upi' },
  { id: 'cred', name: 'CRED UPI', handle: 'cred' },
];

const WALLETS = [
  { id: 'paytm', name: 'Paytm Wallet' },
  { id: 'phonepe', name: 'PhonePe Wallet' },
  { id: 'amazon', name: 'Amazon Pay Balance' },
  { id: 'mobikwik', name: 'MobiKwik' },
];

export const RazorpayModal = ({ isOpen, onClose, order, razorpayOrder, onPaymentSuccess, onPaymentFailure }) => {
  const [activeTab, setActiveTab] = useState('UPI'); // 'UPI' | 'CARD' | 'NB' | 'WALLET'
  const [processingState, setProcessingState] = useState('IDLE'); // 'IDLE' | 'PROCESSING' | 'SUCCESS' | 'DECLINED'

  // Form states
  const [upiId, setUpiId] = useState('industry@razorpay');
  const [selectedUpiApp, setSelectedUpiApp] = useState('gpay');

  const [cardNumber, setCardNumber] = useState('4111 1111 1111 1111');
  const [cardHolder, setCardHolder] = useState('EcoTrack Industry User');
  const [cardExpiry, setCardExpiry] = useState('12/28');
  const [cardCvv, setCardCvv] = useState('123');

  const [selectedBank, setSelectedBank] = useState('HDFC');
  const [selectedWallet, setSelectedWallet] = useState('paytm');

  const totalAmount = order?.totalAmount || '0.00';
  const orderNumber = order?.orderNumber || 'ORD-TEST';

  useEffect(() => {
    if (isOpen) {
      setProcessingState('IDLE');
    }
  }, [isOpen]);

  if (!isOpen) return null;

  const formatCardNumber = (val) => {
    const digits = val.replace(/\D/g, '').substring(0, 16);
    return digits.replace(/(\d{4})(?=\d)/g, '$1 ');
  };

  const formatExpiry = (val) => {
    const digits = val.replace(/\D/g, '').substring(0, 4);
    if (digits.length >= 3) {
      return digits.substring(0, 2) + '/' + digits.substring(2);
    }
    return digits;
  };

  const handlePreFillSuccessCard = () => {
    setCardNumber('4111 1111 1111 1111');
    setCardHolder('EcoTrack Industry User');
    setCardExpiry('12/28');
    setCardCvv('123');
  };

  const handlePreFillDeclineCard = () => {
    setCardNumber('4000 0000 0000 0002');
    setCardHolder('Declined Test User');
    setCardExpiry('01/29');
    setCardCvv('000');
  };

  const handleExecutePayment = () => {
    setProcessingState('PROCESSING');

    // Check if card is the simulated decline test card
    const isDeclineCard = activeTab === 'CARD' && cardNumber.replace(/\s/g, '').endsWith('0002');

    setTimeout(() => {
      if (isDeclineCard) {
        setProcessingState('DECLINED');
        if (onPaymentFailure) {
          onPaymentFailure('Card was declined by issuing bank (Simulated Test Mode decline)');
        }
      } else {
        setProcessingState('SUCCESS');
        setTimeout(() => {
          const simulatedResponse = {
            razorpay_payment_id: 'pay_test_' + Date.now(),
            razorpay_order_id: razorpayOrder?.razorpayOrderId || 'order_rzp_test_' + Date.now(),
            razorpay_signature: 'sig_test_' + Date.now(),
          };
          onPaymentSuccess(simulatedResponse);
        }, 800);
      }
    }, 1200);
  };

  return (
    <div
      className="position-fixed top-0 start-0 w-100 h-100 d-flex align-items-center justify-content-center"
      style={{
        backgroundColor: 'rgba(15, 23, 42, 0.65)',
        backdropFilter: 'blur(5px)',
        zIndex: 1050,
      }}
      onClick={(e) => {
        if (e.target === e.currentTarget && processingState === 'IDLE') {
          onClose();
        }
      }}
    >
      <div
        className="card border-0 shadow-lg overflow-hidden animate__animated animate__fadeInUp"
        style={{
          width: '100%',
          maxWidth: 680,
          borderRadius: 16,
          backgroundColor: '#ffffff',
        }}
      >
        {/* Top Header - Authentic Razorpay Navy */}
        <div
          className="p-3 px-4 d-flex justify-content-between align-items-center text-white"
          style={{
            background: 'linear-gradient(135deg, #0c2340 0%, #083b66 100%)',
            borderBottom: '1px solid rgba(255,255,255,0.1)',
          }}
        >
          <div className="d-flex align-items-center gap-3">
            <div
              className="d-flex align-items-center justify-content-center rounded-3 bg-white text-primary"
              style={{ width: 38, height: 38, boxShadow: '0 2px 8px rgba(0,0,0,0.2)' }}
            >
              <FiShield size={22} style={{ color: '#0c2340' }} />
            </div>
            <div>
              <div className="d-flex align-items-center gap-2">
                <span className="fw-bold fs-6">EcoTrack</span>
                <span
                  className="badge bg-warning text-dark px-2 py-0 fw-bold"
                  style={{ fontSize: '0.65rem', letterSpacing: '0.5px' }}
                >
                  TEST MODE
                </span>
              </div>
              <div className="text-white-50 small" style={{ fontSize: '0.75rem' }}>
                Order #{orderNumber} · Razorpay Checkout Sandbox
              </div>
            </div>
          </div>

          <div className="d-flex align-items-center gap-3">
            <div className="text-end">
              <div className="text-white-50 small" style={{ fontSize: '0.7rem', textTransform: 'uppercase' }}>Amount to Pay</div>
              <div className="fw-bold fs-5 text-white">₹{totalAmount}</div>
            </div>
            {processingState === 'IDLE' && (
              <button
                type="button"
                className="btn btn-sm btn-link text-white-50 p-1 text-decoration-none"
                onClick={onClose}
                aria-label="Close"
              >
                <FiX size={22} />
              </button>
            )}
          </div>
        </div>

        {/* Trust Banner */}
        <div
          className="px-4 py-2 d-flex justify-content-between align-items-center"
          style={{ backgroundColor: '#f0f9ff', borderBottom: '1px solid #e0f2fe', fontSize: '0.78rem' }}
        >
          <div className="d-flex align-items-center gap-2 text-primary fw-semibold">
            <FiLock size={13} />
            <span>Secured with Razorpay Standard 256-bit Encryption</span>
          </div>
          <div className="text-muted d-none d-sm-block">
            Simulated Sandbox Gateway · Instant Verification
          </div>
        </div>

        {/* Modal Body */}
        {processingState === 'PROCESSING' && (
          <div className="p-5 text-center my-4">
            <div className="spinner-border text-success mb-3" style={{ width: '3rem', height: '3rem' }} role="status">
              <span className="visually-hidden">Loading...</span>
            </div>
            <h5 className="fw-bold text-dark mb-1">Contacting Bank Gateway...</h5>
            <p className="text-muted small mb-0">Please do not close this window or press back button</p>
            <div className="mt-3 small text-success fw-semibold d-flex align-items-center justify-content-center gap-2">
              <FiRefreshCw className="spin" /> Verifying Razorpay test transaction token
            </div>
          </div>
        )}

        {processingState === 'SUCCESS' && (
          <div className="p-5 text-center my-4">
            <div
              className="mx-auto mb-3 d-flex align-items-center justify-content-center rounded-circle"
              style={{ width: 64, height: 64, background: '#e6f6ee', color: '#0f9d58' }}
            >
              <FiCheckCircle size={36} />
            </div>
            <h4 className="fw-bold text-success mb-1">Payment Successful!</h4>
            <p className="text-muted small mb-2">Transaction ID: pay_test_{Date.now().toString().slice(-8)}</p>
            <div className="small text-muted">Authorizing order fulfillment...</div>
          </div>
        )}

        {processingState === 'DECLINED' && (
          <div className="p-5 text-center my-3">
            <div
              className="mx-auto mb-3 d-flex align-items-center justify-content-center rounded-circle"
              style={{ width: 64, height: 64, background: '#fee2e2', color: '#dc2626' }}
            >
              <FiXCircle size={36} />
            </div>
            <h5 className="fw-bold text-danger mb-1">Payment Declined (Simulated)</h5>
            <p className="text-muted small mb-4">
              The test card ending in <strong>0002</strong> simulated an issuing bank decline.
            </p>
            <div className="d-flex gap-2 justify-content-center">
              <button
                className="btn btn-outline-secondary btn-sm px-4"
                onClick={() => {
                  handlePreFillSuccessCard();
                  setProcessingState('IDLE');
                }}
              >
                Switch to Valid Test Card
              </button>
              <button
                className="btn btn-primary btn-sm px-4"
                onClick={() => setProcessingState('IDLE')}
              >
                Try Again
              </button>
            </div>
          </div>
        )}

        {processingState === 'IDLE' && (
          <div className="row g-0">
            {/* Left Nav Tabs */}
            <div
              className="col-4 border-end"
              style={{ backgroundColor: '#f8fafc', minHeight: 340 }}
            >
              <div className="p-2 pt-3">
                <button
                  type="button"
                  className={`btn w-100 text-start d-flex align-items-center gap-2 p-2 px-3 mb-1 border-0 rounded-2 ${
                    activeTab === 'UPI' ? 'bg-white shadow-sm fw-bold text-primary' : 'text-muted'
                  }`}
                  style={{ fontSize: '0.85rem' }}
                  onClick={() => setActiveTab('UPI')}
                >
                  <FiSmartphone size={16} />
                  <span>UPI / QR</span>
                </button>

                <button
                  type="button"
                  className={`btn w-100 text-start d-flex align-items-center gap-2 p-2 px-3 mb-1 border-0 rounded-2 ${
                    activeTab === 'CARD' ? 'bg-white shadow-sm fw-bold text-primary' : 'text-muted'
                  }`}
                  style={{ fontSize: '0.85rem' }}
                  onClick={() => setActiveTab('CARD')}
                >
                  <FiCreditCard size={16} />
                  <span>Cards</span>
                </button>

                <button
                  type="button"
                  className={`btn w-100 text-start d-flex align-items-center gap-2 p-2 px-3 mb-1 border-0 rounded-2 ${
                    activeTab === 'NB' ? 'bg-white shadow-sm fw-bold text-primary' : 'text-muted'
                  }`}
                  style={{ fontSize: '0.85rem' }}
                  onClick={() => setActiveTab('NB')}
                >
                  <FiGlobe size={16} />
                  <span>Netbanking</span>
                </button>

                <button
                  type="button"
                  className={`btn w-100 text-start d-flex align-items-center gap-2 p-2 px-3 mb-1 border-0 rounded-2 ${
                    activeTab === 'WALLET' ? 'bg-white shadow-sm fw-bold text-primary' : 'text-muted'
                  }`}
                  style={{ fontSize: '0.85rem' }}
                  onClick={() => setActiveTab('WALLET')}
                >
                  <FiZap size={16} />
                  <span>Wallets</span>
                </button>
              </div>

              <div className="p-3 mt-4 text-center border-top">
                <div className="text-muted" style={{ fontSize: '0.7rem' }}>Razorpay Test Sandbox</div>
                <div className="badge bg-success-subtle text-success mt-1" style={{ fontSize: '0.65rem' }}>
                  Mock Gateway Active
                </div>
              </div>
            </div>

            {/* Right Tab Content */}
            <div className="col-8 p-4 d-flex flex-column justify-content-between">
              <div>
                {/* 1. UPI Tab */}
                {activeTab === 'UPI' && (
                  <div>
                    <h6 className="fw-bold mb-3 d-flex align-items-center gap-2">
                      <FiSmartphone className="text-success" /> Pay with UPI
                    </h6>

                    {/* Popular UPI Apps */}
                    <label className="form-label small text-muted fw-semibold mb-2">Fast UPI Pay</label>
                    <div className="d-flex flex-wrap gap-2 mb-3">
                      {UPI_APPS.map((app) => (
                        <button
                          key={app.id}
                          type="button"
                          className={`btn btn-sm ${
                            selectedUpiApp === app.id ? 'btn-success' : 'btn-outline-secondary'
                          } d-flex align-items-center gap-1`}
                          onClick={() => {
                            setSelectedUpiApp(app.id);
                            setUpiId(`industry@${app.handle}`);
                          }}
                        >
                          {selectedUpiApp === app.id && <FiCheck size={14} />}
                          <span>{app.name}</span>
                        </button>
                      ))}
                    </div>

                    {/* UPI ID Input */}
                    <div className="mb-3">
                      <label className="form-label small text-muted fw-semibold">Virtual Payment Address (VPA / UPI ID)</label>
                      <div className="input-group input-group-sm">
                        <input
                          type="text"
                          className="form-control"
                          value={upiId}
                          onChange={(e) => setUpiId(e.target.value)}
                          placeholder="yourname@okhdfcbank"
                        />
                        <button
                          className="btn btn-outline-success"
                          type="button"
                          onClick={() => setUpiId('success@razorpay')}
                        >
                          Auto Fill
                        </button>
                      </div>
                      <div className="form-text" style={{ fontSize: '0.72rem' }}>
                        Test mode VPA automatically approved upon checkout.
                      </div>
                    </div>
                  </div>
                )}

                {/* 2. Cards Tab */}
                {activeTab === 'CARD' && (
                  <div>
                    <div className="d-flex justify-content-between align-items-center mb-2">
                      <h6 className="fw-bold mb-0 d-flex align-items-center gap-2">
                        <FiCreditCard className="text-success" /> Credit / Debit Card
                      </h6>
                      <div className="d-flex gap-1">
                        <span className="badge bg-light text-dark border" style={{ fontSize: '0.65rem' }}>VISA</span>
                        <span className="badge bg-light text-dark border" style={{ fontSize: '0.65rem' }}>Mastercard</span>
                        <span className="badge bg-light text-dark border" style={{ fontSize: '0.65rem' }}>RuPay</span>
                      </div>
                    </div>

                    {/* Pre-fill Helpers */}
                    <div className="d-flex gap-2 mb-3">
                      <button
                        type="button"
                        className="btn btn-sm btn-outline-success py-1 px-2"
                        style={{ fontSize: '0.72rem' }}
                        onClick={handlePreFillSuccessCard}
                      >
                        ⚡ Fill Valid Test Card
                      </button>
                      <button
                        type="button"
                        className="btn btn-sm btn-outline-danger py-1 px-2"
                        style={{ fontSize: '0.72rem' }}
                        onClick={handlePreFillDeclineCard}
                      >
                        ⚠️ Fill Decline Card
                      </button>
                    </div>

                    {/* Card Number */}
                    <div className="mb-2">
                      <label className="form-label small text-muted fw-semibold mb-1">Card Number</label>
                      <input
                        type="text"
                        className="form-control form-control-sm font-monospace"
                        value={cardNumber}
                        onChange={(e) => setCardNumber(formatCardNumber(e.target.value))}
                        placeholder="4111 1111 1111 1111"
                        maxLength={19}
                      />
                    </div>

                    {/* Cardholder Name */}
                    <div className="mb-2">
                      <label className="form-label small text-muted fw-semibold mb-1">Cardholder Name</label>
                      <input
                        type="text"
                        className="form-control form-control-sm"
                        value={cardHolder}
                        onChange={(e) => setCardHolder(e.target.value)}
                        placeholder="John Doe"
                      />
                    </div>

                    {/* Expiry & CVV */}
                    <div className="row g-2">
                      <div className="col-6">
                        <label className="form-label small text-muted fw-semibold mb-1">Expiry (MM/YY)</label>
                        <input
                          type="text"
                          className="form-control form-control-sm"
                          value={cardExpiry}
                          onChange={(e) => setCardExpiry(formatExpiry(e.target.value))}
                          placeholder="MM/YY"
                          maxLength={5}
                        />
                      </div>
                      <div className="col-6">
                        <label className="form-label small text-muted fw-semibold mb-1">CVV</label>
                        <input
                          type="password"
                          className="form-control form-control-sm"
                          value={cardCvv}
                          onChange={(e) => setCardCvv(e.target.value.substring(0, 4))}
                          placeholder="123"
                          maxLength={4}
                        />
                      </div>
                    </div>
                  </div>
                )}

                {/* 3. Netbanking Tab */}
                {activeTab === 'NB' && (
                  <div>
                    <h6 className="fw-bold mb-3 d-flex align-items-center gap-2">
                      <FiGlobe className="text-success" /> All Indian Banks
                    </h6>

                    <label className="form-label small text-muted fw-semibold mb-2">Popular Banks</label>
                    <div className="row g-2 mb-3">
                      {POPULAR_BANKS.map((b) => (
                        <div key={b.id} className="col-6">
                          <button
                            type="button"
                            className={`btn btn-sm w-100 text-start d-flex justify-content-between align-items-center p-2 border ${
                              selectedBank === b.id ? 'btn-success text-white' : 'btn-light'
                            }`}
                            onClick={() => setSelectedBank(b.id)}
                            style={{ fontSize: '0.78rem' }}
                          >
                            <span className="text-truncate">{b.name}</span>
                            {selectedBank === b.id && <FiCheck size={14} />}
                          </button>
                        </div>
                      ))}
                    </div>
                  </div>
                )}

                {/* 4. Wallets Tab */}
                {activeTab === 'WALLET' && (
                  <div>
                    <h6 className="fw-bold mb-3 d-flex align-items-center gap-2">
                      <FiZap className="text-success" /> Digital Wallets
                    </h6>

                    <div className="d-flex flex-column gap-2 mb-3">
                      {WALLETS.map((w) => (
                        <label
                          key={w.id}
                          className={`d-flex justify-content-between align-items-center p-2 px-3 border rounded-2 ${
                            selectedWallet === w.id ? 'border-success bg-light fw-semibold' : ''
                          }`}
                          style={{ cursor: 'pointer', fontSize: '0.85rem' }}
                          onClick={() => setSelectedWallet(w.id)}
                        >
                          <div className="d-flex align-items-center gap-2">
                            <input
                              type="radio"
                              name="wallet"
                              checked={selectedWallet === w.id}
                              onChange={() => setSelectedWallet(w.id)}
                            />
                            <span>{w.name}</span>
                          </div>
                          <span className="badge bg-secondary-subtle text-secondary" style={{ fontSize: '0.7rem' }}>
                            Linked
                          </span>
                        </label>
                      ))}
                    </div>
                  </div>
                )}
              </div>

              {/* Bottom Pay Action */}
              <div className="pt-3 border-top mt-3">
                <button
                  type="button"
                  className="btn btn-success w-100 py-2 fw-semibold d-flex align-items-center justify-content-center gap-2 shadow-sm"
                  onClick={handleExecutePayment}
                >
                  <FiLock size={15} />
                  <span>Pay ₹{totalAmount}</span>
                </button>
                <div className="text-center text-muted mt-2" style={{ fontSize: '0.7rem' }}>
                  By clicking Pay, you authorize Razorpay Sandbox transaction verification.
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default RazorpayModal;
