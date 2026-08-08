import React, { useEffect, useState, useCallback } from 'react';
import { FiUsers, FiSearch } from 'react-icons/fi';
import PageHeader from '../../components/common/PageHeader';
import StatusBadge from '../../components/common/StatusBadge';
import { LoadingSpinner, EmptyState, ErrorState } from '../../components/common/Feedback';
import { recyclerService, industryService } from '../../services/userService';
import { getErrorMessage } from '../../services/api';
import { useToast } from '../../context/ToastContext';

const TABS = [
  { key: 'RECYCLER', label: 'Recycler Partners', service: recyclerService },
  { key: 'INDUSTRY', label: 'Industrial Buyers', service: industryService },
];

const AdminUsers = () => {
  const [tab, setTab] = useState('RECYCLER');
  const [search, setSearch] = useState('');
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [actingId, setActingId] = useState(null);
  const { showToast } = useToast();

  const activeService = TABS.find((t) => t.key === tab).service;

  const load = useCallback(async () => {
    setLoading(true);
    setError(false);
    try {
      const params = { size: 30 };
      if (search) params.companyName = search;
      const data = await activeService.search(params);
      setItems(data.content || []);
    } catch (e) {
      setError(true);
    } finally {
      setLoading(false);
    }
  }, [activeService, search]);

  useEffect(() => {
    const timeout = setTimeout(load, 300);
    return () => clearTimeout(timeout);
  }, [load]);

  const handleToggleSuspend = async (item) => {
    setActingId(item.id);
    try {
      await activeService.suspend(item.id, !item.suspended);
      showToast(item.suspended ? 'Account reactivated' : 'Account suspended', 'success');
      load();
    } catch (err) {
      showToast(getErrorMessage(err), 'error');
    } finally {
      setActingId(null);
    }
  };

  return (
    <div>
      <PageHeader title="Users" subtitle="Manage Recycler Partner and Industrial Buyer accounts" />

      <div className="alert alert-light border small mb-3">
        Citizen accounts aren't listable through the current API - only Recycler and Industry
        accounts (which go through Admin approval) are manageable here.
      </div>

      <div className="d-flex flex-wrap justify-content-between gap-2 mb-3">
        <div className="d-flex gap-2">
          {TABS.map((t) => (
            <button
              key={t.key}
              className={`btn btn-sm ${tab === t.key ? 'btn-success' : 'btn-outline-secondary'}`}
              onClick={() => { setTab(t.key); setSearch(''); }}
            >
              {t.label}
            </button>
          ))}
        </div>
        <div className="position-relative" style={{ minWidth: 240 }}>
          <FiSearch size={16} className="position-absolute text-muted" style={{ left: 12, top: 11 }} />
          <input
            className="form-control ps-4"
            placeholder="Search by company name..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>
      </div>

      <div className="card border-0 shadow-sm">
        <div className="card-body p-0">
          {loading ? (
            <LoadingSpinner />
          ) : error ? (
            <ErrorState />
          ) : items.length === 0 ? (
            <EmptyState icon={FiUsers} title="No accounts found" />
          ) : (
            <div className="table-responsive">
              <table className="table align-middle mb-0">
                <thead>
                  <tr className="text-muted small text-uppercase">
                    <th className="ps-3">Company</th>
                    <th>Contact</th>
                    <th>Phone</th>
                    <th>City</th>
                    <th>Status</th>
                    <th className="pe-3"></th>
                  </tr>
                </thead>
                <tbody>
                  {items.map((item) => (
                    <tr key={item.id}>
                      <td className="ps-3 small fw-semibold">{item.companyName}</td>
                      <td className="small">{item.contactPerson}</td>
                      <td className="small">{item.phone}</td>
                      <td className="small">{item.city || '—'}</td>
                      <td><StatusBadge status={item.suspended ? 'BLOCKED' : 'ACTIVE'} /></td>
                      <td className="pe-3 text-end">
                        <button
                          className={`btn btn-sm ${item.suspended ? 'btn-outline-success' : 'btn-outline-danger'}`}
                          disabled={actingId === item.id}
                          onClick={() => handleToggleSuspend(item)}
                        >
                          {item.suspended ? 'Reactivate' : 'Suspend'}
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
    </div>
  );
};

export default AdminUsers;
