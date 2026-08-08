import React, { useEffect, useState, useCallback } from 'react';
import { FiPlus, FiTag } from 'react-icons/fi';
import PageHeader from '../../components/common/PageHeader';
import StatusBadge from '../../components/common/StatusBadge';
import { LoadingSpinner, EmptyState, ErrorState } from '../../components/common/Feedback';
import { wasteCategoryService } from '../../services/pickupService';
import { materialCategoryService } from '../../services/materialService';
import { regionService } from '../../services/userService';
import { getErrorMessage } from '../../services/api';
import { useToast } from '../../context/ToastContext';

const TABS = [
  { key: 'WASTE', label: 'Waste Categories', service: wasteCategoryService, toggleMethod: 'setActive' },
  { key: 'MATERIAL', label: 'Material Categories', service: materialCategoryService, toggleMethod: 'setActive' },
  { key: 'REGION', label: 'Region Master', service: regionService, toggleMethod: 'toggle' },
];

const AdminSettings = () => {
  const [tab, setTab] = useState('WASTE');
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [newName, setNewName] = useState('');
  const [newDescription, setNewDescription] = useState('');
  const [creating, setCreating] = useState(false);
  const [actingId, setActingId] = useState(null);
  const { showToast } = useToast();

  const currentTab = TABS.find((t) => t.key === tab);
  const activeService = currentTab.service;

  const load = useCallback(async () => {
    setLoading(true);
    setError(false);
    try {
      const data = await activeService.getAll();
      setCategories(data || []);
    } catch (e) {
      setError(true);
    } finally {
      setLoading(false);
    }
  }, [activeService]);

  useEffect(() => { load(); }, [load]);

  const handleCreate = async (e) => {
    e.preventDefault();
    if (!newName.trim()) return;
    setCreating(true);
    try {
      await activeService.create({ name: newName.trim(), description: newDescription.trim() });
      showToast('Item created successfully', 'success');
      setNewName('');
      setNewDescription('');
      load();
    } catch (err) {
      showToast(getErrorMessage(err), 'error');
    } finally {
      setCreating(false);
    }
  };

  const handleToggleActive = async (category) => {
    setActingId(category.id);
    try {
      if (currentTab.toggleMethod === 'toggle') {
        await activeService.toggle(category.id, !category.active);
      } else {
        await activeService.setActive(category.id, !category.active);
      }
      load();
    } catch (err) {
      showToast(getErrorMessage(err), 'error');
    } finally {
      setActingId(null);
    }
  };

  return (
    <div>
      <PageHeader title="Settings" subtitle="Manage the categories Citizens and Recyclers choose from" />

      <div className="d-flex gap-2 mb-4">
        {TABS.map((t) => (
          <button
            key={t.key}
            className={`btn btn-sm ${tab === t.key ? 'btn-success' : 'btn-outline-secondary'}`}
            onClick={() => setTab(t.key)}
          >
            {t.label}
          </button>
        ))}
      </div>

      <div className="card border-0 shadow-sm mb-4">
        <div className="card-body">
          <h6 className="fw-bold mb-3 d-flex align-items-center gap-2"><FiPlus /> Add Category</h6>
          <form onSubmit={handleCreate} className="row g-2 align-items-end">
            <div className="col-md-4">
              <label className="form-label small fw-semibold">Name</label>
              <input className="form-control" value={newName} onChange={(e) => setNewName(e.target.value)} required />
            </div>
            <div className="col-md-6">
              <label className="form-label small fw-semibold">Description (optional)</label>
              <input className="form-control" value={newDescription} onChange={(e) => setNewDescription(e.target.value)} />
            </div>
            <div className="col-md-2">
              <button type="submit" className="btn btn-success w-100" disabled={creating}>
                {creating ? 'Adding...' : 'Add'}
              </button>
            </div>
          </form>
        </div>
      </div>

      <div className="card border-0 shadow-sm">
        <div className="card-body p-0">
          {loading ? (
            <LoadingSpinner />
          ) : error ? (
            <ErrorState />
          ) : categories.length === 0 ? (
            <EmptyState icon={FiTag} title="No categories yet" />
          ) : (
            <div className="table-responsive">
              <table className="table align-middle mb-0">
                <thead>
                  <tr className="text-muted small text-uppercase">
                    <th className="ps-3">Name</th>
                    <th>Description</th>
                    <th>Status</th>
                    <th className="pe-3"></th>
                  </tr>
                </thead>
                <tbody>
                  {categories.map((c) => (
                    <tr key={c.id}>
                      <td className="ps-3 small fw-semibold">{c.name}</td>
                      <td className="small text-muted">{c.description || '—'}</td>
                      <td><StatusBadge status={c.active ? 'ACTIVE' : 'DEACTIVATED'} /></td>
                      <td className="pe-3 text-end">
                        <button
                          className={`btn btn-sm ${c.active ? 'btn-outline-danger' : 'btn-outline-success'}`}
                          disabled={actingId === c.id}
                          onClick={() => handleToggleActive(c)}
                        >
                          {c.active ? 'Deactivate' : 'Activate'}
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

export default AdminSettings;
