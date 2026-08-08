import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { FiCheckCircle, FiCreditCard, FiZap } from 'react-icons/fi';
import PageHeader from '../../components/common/PageHeader';
import { LoadingSpinner, ErrorState } from '../../components/common/Feedback';
import { orderService } from '../../services/materialService';
import { getErrorMessage } from '../../services/api';
import { useToast } from '../../context/ToastContext';
import { loadRazorpayScript } from '../../utils/loadRazorpayScript';
import RazorpayModal from '../../components/common/RazorpayModal';

const PAYMENT_METHODS = [
  { value: 'MOCK_UPI', label: 'UPI (Mock)' },
  { value: 'MOCK_CARD', label: 'Card (Mock)' },
  { value: 'MOCK_NET_BANKING', label: 'Net Banking (Mock)' },
];

const PaymentSelection = () => {
  const { orderId } = useParams();
  const navigate = useNavigate();
  const { showToast } = useToast();

  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [option, setOption] = useState('MOCK'); // 'MOCK' or 'RAZORPAY'
  const [mockMethod, setMockMethod] = useState('MOCK_UPI');
  const [paying, setPaying] = useState(false);

  // Razorpay Sandbox checkout modal state
  const [razorpayOrder, setRazorpayOrder] = useState(null);
  const [showRazorpayModal, setShowRazorpayModal] = useState(false);

  useEffect(() => {
    orderService.getDetail(orderId)
      .then((data) => {
        if (data.paymentStatus === 'SUCCESS') {
          // Already paid (e.g. user refreshed this page) - go straight to success.
          navigate(`/industry/orders/${orderId}/success`, { replace: true });
          return;
        }
        setOrder(data);
      })
      .catch(() => setError(true))
      .finally(() => setLoading(false));
  }, [orderId, navigate]);

  const handleMockPay = async () => {
    setPaying(true);
    try {
      await orderService.pay(orderId, { paymentMethod: mockMethod });
      showToast('Mock Payment processed successfully!', 'success');
      navigate(`/industry/orders/${orderId}/success`);
    } catch (err) {
      showToast(getErrorMessage(err), 'error');
    } finally {
      setPaying(false);
    }
  };

  const handleRazorpayPay = async () => {
    setPaying(true);
    try {
      const rzpOrder = await orderService.createRazorpayOrder(orderId);
      setRazorpayOrder(rzpOrder);

      // Check if real live Razorpay API keys are configured and available
      const isRealKey = rzpOrder.keyId 
        && !rzpOrder.keyId.includes('demo') 
        && !rzpOrder.keyId.includes('placeholder') 
        && !rzpOrder.keyId.includes('YOUR_KEY_ID')
        && !rzpOrder.razorpayOrderId.startsWith('order_rzp_test_');

      const scriptLoaded = isRealKey ? await loadRazorpayScript() : false;

      if (isRealKey && scriptLoaded && window.Razorpay) {
        // Open Official Razorpay Checkout Modal
        const rzp = new window.Razorpay({
          key: rzpOrder.keyId,
          amount: rzpOrder.amountInPaise,
          currency: rzpOrder.currency,
          name: 'EcoTrack',
          description: `Order ${order.orderNumber}`,
          order_id: rzpOrder.razorpayOrderId,
          handler: async (response) => {
            try {
              await orderService.verifyRazorpayPayment(orderId, {
                razorpayOrderId: response.razorpay_order_id,
                razorpayPaymentId: response.razorpay_payment_id,
                razorpaySignature: response.razorpay_signature,
              });
              showToast('Razorpay Payment verified successfully!', 'success');
              navigate(`/industry/orders/${orderId}/success`);
            } catch (err) {
              showToast(getErrorMessage(err, 'Payment verification failed.'), 'error');
            } finally {
              setPaying(false);
            }
          },
          modal: {
            ondismiss: () => setPaying(false),
          },
          theme: { color: '#0f9d58' },
        });

        rzp.on('payment.failed', () => {
          showToast('Payment was cancelled or failed.', 'error');
          setPaying(false);
        });

        rzp.open();
      } else {
        // Launch Interactive Razorpay Sandbox Test Checkout Modal
        setShowRazorpayModal(true);
        setPaying(false);
      }
    } catch (err) {
      showToast(getErrorMessage(err, 'Could not initialize Razorpay checkout.'), 'error');
      setPaying(false);
    }
  };

  const handleRazorpayModalSuccess = async (response) => {
    setShowRazorpayModal(false);
    setPaying(true);
    try {
      await orderService.verifyRazorpayPayment(orderId, {
        razorpayOrderId: response.razorpay_order_id || razorpayOrder?.razorpayOrderId,
        razorpayPaymentId: response.razorpay_payment_id || ('pay_test_' + Date.now()),
        razorpaySignature: response.razorpay_signature || ('sig_test_' + Date.now()),
      });
      showToast('Razorpay Test Mode: Payment verified successfully!', 'success');
      navigate(`/industry/orders/${orderId}/success`);
    } catch (err) {
      showToast(getErrorMessage(err, 'Payment verification failed.'), 'error');
      setPaying(false);
    }
  };

  const handleRazorpayModalFailure = (reason) => {
    showToast(reason || 'Payment was declined by issuing bank.', 'error');
    setPaying(false);
  };

  const handlePay = () => {
    if (option === 'MOCK') {
      handleMockPay();
    } else {
      handleRazorpayPay();
    }
  };

  if (loading) return <LoadingSpinner label="Loading order..." />;
  if (error || !order) return <ErrorState message="This order could not be found." />;

  return (
    <div>
      <PageHeader title="Choose Payment Method" subtitle={`Order ${order.orderNumber} · ₹${order.totalAmount}`} />

      <div className="row g-4">
        <div className="col-lg-7">
          <div className="card border-0 shadow-sm mb-3">
            <div
              className={`card-body p-4 border-bottom ${option === 'MOCK' ? 'bg-light' : ''}`}
              role="button"
              onClick={() => setOption('MOCK')}
            >
              <div className="d-flex align-items-center gap-3">
                <input type="radio" checked={option === 'MOCK'} onChange={() => setOption('MOCK')} />
                <FiCreditCard size={20} className="text-muted" />
                <div>
                  <div className="fw-semibold small">Mock Payment</div>
                  <div className="small text-muted">Instant test payment - no real transaction, always succeeds</div>
                </div>
              </div>

              {option === 'MOCK' && (
                <div className="mt-3 ms-5">
                  <label className="form-label small fw-semibold">Payment method</label>
                  <select className="form-select" style={{ maxWidth: 260 }} value={mockMethod} onChange={(e) => setMockMethod(e.target.value)}>
                    {PAYMENT_METHODS.map((m) => (
                      <option key={m.value} value={m.value}>{m.label}</option>
                    ))}
                  </select>
                </div>
              )}
            </div>

            <div
              className={`card-body p-4 ${option === 'RAZORPAY' ? 'bg-light' : ''}`}
              role="button"
              onClick={() => setOption('RAZORPAY')}
            >
              <div className="d-flex align-items-center gap-3">
                <input type="radio" checked={option === 'RAZORPAY'} onChange={() => setOption('RAZORPAY')} />
                <FiZap size={20} className="text-muted" />
                <div>
                  <div className="fw-semibold small">Razorpay (Test Mode)</div>
                  <div className="small text-muted">Pay via UPI, Card, Net Banking or Wallet through Razorpay's sandbox</div>
                </div>
              </div>
            </div>
          </div>

          <button
            className="btn btn-success w-100 py-2 fw-semibold d-flex align-items-center justify-content-center gap-2"
            disabled={paying}
            onClick={handlePay}
          >
            {paying && <span className="spinner-border spinner-border-sm" />}
            {option === 'MOCK' ? `Pay ₹${order.totalAmount} (Mock)` : `Pay ₹${order.totalAmount} with Razorpay`}
          </button>
        </div>

        <div className="col-lg-5">
          <div className="card border-0 shadow-sm">
            <div className="card-body p-4">
              <h6 className="fw-bold mb-3 d-flex align-items-center gap-2"><FiCheckCircle className="text-success" /> Order Summary</h6>
              {order.items?.map((item) => (
                <div key={item.materialId} className="d-flex justify-content-between small mb-2">
                  <span className="text-muted">{item.materialName} × {item.quantity}</span>
                  <span>₹{item.subtotal}</span>
                </div>
              ))}
              <hr />
              <div className="d-flex justify-content-between fw-bold">
                <span>Total</span>
                <span>₹{order.totalAmount}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Razorpay Test Mode Checkout Dialog */}
      <RazorpayModal
        isOpen={showRazorpayModal}
        onClose={() => setShowRazorpayModal(false)}
        order={order}
        razorpayOrder={razorpayOrder}
        onPaymentSuccess={handleRazorpayModalSuccess}
        onPaymentFailure={handleRazorpayModalFailure}
      />
    </div>
  );
};

export default PaymentSelection;
