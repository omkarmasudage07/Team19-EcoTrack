/**
 * Razorpay's Checkout widget is loaded from their own CDN script, not
 * an npm package - this is how every Razorpay integration works,
 * including production ones. We load it once and cache the promise so
 * clicking "Pay with Razorpay" twice doesn't inject the script twice.
 */
let loadPromise = null;

export function loadRazorpayScript() {
  if (window.Razorpay) return Promise.resolve(true);
  if (loadPromise) return loadPromise;

  loadPromise = new Promise((resolve) => {
    const script = document.createElement('script');
    script.src = 'https://checkout.razorpay.com/v1/checkout.js';
    script.onload = () => resolve(true);
    script.onerror = () => resolve(false);
    document.body.appendChild(script);
  });

  return loadPromise;
}
