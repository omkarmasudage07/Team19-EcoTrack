import React from 'react';
import { Link } from 'react-router-dom';
import { FiTruck, FiRepeat, FiShoppingBag, FiAward } from 'react-icons/fi';
import { FaLeaf } from 'react-icons/fa';

const Home = () => (
  <div>
    <nav className="d-flex align-items-center justify-content-between px-4 py-3 border-bottom bg-white">
      <div className="d-flex align-items-center gap-2">
        <div
          className="d-flex align-items-center justify-content-center rounded-2"
          style={{ width: 32, height: 32, background: 'var(--color-primary)', color: '#fff' }}
        >
          <FaLeaf size={18} />
        </div>
        <span className="fw-bold fs-5">EcoTrack</span>
      </div>
      <div className="d-flex align-items-center gap-3">
        <Link to="/login" className="btn btn-outline-success btn-sm">Sign in</Link>
        <Link to="/register" className="btn btn-success btn-sm">Get started</Link>
      </div>
    </nav>

    <header className="py-5" style={{ background: 'var(--color-bg)' }}>
      <div className="container py-5 text-center" style={{ maxWidth: 720 }}>
        <h1 className="fw-bold display-5 mb-3">
          Turn e-waste into a <span style={{ color: 'var(--color-primary)' }}>circular economy</span>
        </h1>
        <p className="text-muted fs-5 mb-4">
          EcoTrack connects Citizens, Recycler Partners, and Industrial Buyers into one platform -
          responsible collection, resource recovery, and industrial reuse, all in one place.
        </p>
        <div className="d-flex gap-3 justify-content-center">
          <Link to="/register" className="btn btn-success btn-lg px-4">Schedule a pickup</Link>
          <Link to="/become-recycler" className="btn btn-outline-success btn-lg px-4">Become a Partner</Link>
        </div>
      </div>
    </header>

    <section className="container py-5">
      <div className="row g-4">
        {[
          { icon: FiTruck, title: 'Schedule Pickup', text: 'Citizens book a free e-waste pickup in minutes.' },
          { icon: FiRepeat, title: 'Recover Materials', text: 'Recycler Partners extract reusable raw materials.' },
          { icon: FiShoppingBag, title: 'Industrial Exchange', text: 'Industries buy verified recycled materials.' },
          { icon: FiAward, title: 'Earn EcoPoints', text: 'Citizens are rewarded for every completed pickup.' },
        ].map(({ icon: Icon, title, text }) => (
          <div className="col-md-3 col-sm-6" key={title}>
            <div className="card border-0 shadow-sm h-100 p-3 text-center">
              <div
                className="mx-auto mb-3 d-flex align-items-center justify-content-center rounded-3"
                style={{ width: 48, height: 48, background: 'var(--color-primary-light)', color: 'var(--color-primary-dark)' }}
              >
                <Icon size={22} />
              </div>
              <h6 className="fw-bold">{title}</h6>
              <p className="small text-muted mb-0">{text}</p>
            </div>
          </div>
        ))}
      </div>
    </section>

    <footer className="border-top py-4 text-center small text-muted">
      © {new Date().getFullYear()} EcoTrack — Smart E-Waste Collection & Industrial Raw Material Exchange
    </footer>
  </div>
);

export default Home;
