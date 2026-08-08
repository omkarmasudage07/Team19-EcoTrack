import React, { useEffect, useState, useCallback } from 'react';
import { useForm } from 'react-hook-form';
import { FiBox, FiPlus, FiEdit2, FiTrash2, FiX } from 'react-icons/fi';
import PageHeader from '../../components/common/PageHeader';
import StatusBadge from '../../components/common/StatusBadge';
import { LoadingSpinner, EmptyState, ErrorState } from '../../components/common/Feedback';
import { materialService, materialCategoryService } from '../../services/materialService';
import { getErrorMessage } from '../../services/api';
import { useToast } from '../../context/ToastContext';

const emptyDefaults = {
  categoryId: '',
  materialName: '',
  description: '',
  purity: '',
  quantity: '',
  unit: 'kg',
  pricePerUnit: '',
  warehouseLocation: '',
};

const MaterialListings = () => {
  const [materials, setMaterials] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [formOpen, setFormOpen] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [deletingId, setDeletingId] = useState(null);
  const { showToast } = useToast();

  const { register, handleSubmit, reset, formState: { errors } } = useForm({ defaultValues: emptyDefaults });

  const load = useCallback(async () => {
    setLoading(true);
    setError(false);
    try {
      const [materialsData, categoriesData] = await Promise.all([
        materialService.getMyMaterials({ size: 50, sort: 'createdAt,desc' }),
        materialCategoryService.getActive(),
      ]);
      setMaterials(materialsData.content || []);
      setCategories(categoriesData || []);
    } catch (e) {
      setError(true);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const openCreateForm = () => {
    setEditingId(null);
    reset(emptyDefaults);
    setFormOpen(true);
  };

  const openEditForm = (material) => {
    setEditingId(material.id);
    reset({
      categoryId: material.categoryId,
      materialName: material.materialName,
      description: material.description || '',
      purity: material.purity || '',
      quantity: material.quantity,
      unit: material.unit,
      pricePerUnit: material.pricePerUnit,
      warehouseLocation: material.warehouseLocation || '',
    });
    setFormOpen(true);
  };

  const onSubmit = async (values) => {
    setSubmitting(true);
    try {
      const payload = {
        ...values,
        categoryId: Number(values.categoryId),
        quantity: Number(values.quantity),
        pricePerUnit: Number(values.pricePerUnit),
        imageUrls: [],
      };
      if (editingId) {
        await materialService.update(editingId, payload);
        showToast('Material listing updated', 'success');
      } else {
        await materialService.create(payload);
        showToast('Material listed successfully', 'success');
      }
      setFormOpen(false);
      load();
    } catch (err) {
      showToast(getErrorMessage(err), 'error');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Remove this material listing? This cannot be undone.')) return;
    setDeletingId(id);
    try {
      await materialService.remove(id);
      showToast('Material listing removed', 'success');
      setMaterials((prev) => prev.filter((m) => m.id !== id));
    } catch (err) {
      showToast(getErrorMessage(err), 'error');
    } finally {
      setDeletingId(null);
    }
  };

  return (
    <div>
      <PageHeader
        title="Material Listings"
        subtitle="Recovered raw materials you're offering on the marketplace"
        action={
          <button className="btn btn-success d-inline-flex align-items-center gap-2" onClick={openCreateForm}>
            <FiPlus size={16} /> List New Material
          </button>
        }
      />

      {formOpen && (
        <div className="card border-0 shadow-sm mb-4">
          <div className="card-body">
            <div className="d-flex justify-content-between align-items-center mb-3">
              <h6 className="fw-bold mb-0">{editingId ? 'Edit Material' : 'New Material Listing'}</h6>
              <button className="btn btn-sm btn-light" onClick={() => setFormOpen(false)}>
                <FiX size={16} />
              </button>
            </div>

            <form onSubmit={handleSubmit(onSubmit)} noValidate>
              <div className="row g-3">
                <div className="col-md-6">
                  <label className="form-label small fw-semibold">Category</label>
                  <select
                    className={`form-select ${errors.categoryId ? 'is-invalid' : ''}`}
                    {...register('categoryId', { required: 'Category is required' })}
                  >
                    <option value="">Select category...</option>
                    {categories.map((c) => (
                      <option key={c.id} value={c.id}>{c.name}</option>
                    ))}
                  </select>
                  {errors.categoryId && <div className="invalid-feedback">{errors.categoryId.message}</div>}
                </div>

                <div className="col-md-6">
                  <label className="form-label small fw-semibold">Material name</label>
                  <input
                    className={`form-control ${errors.materialName ? 'is-invalid' : ''}`}
                    placeholder="e.g. Copper Wire Scrap"
                    {...register('materialName', { required: 'Material name is required' })}
                  />
                  {errors.materialName && <div className="invalid-feedback">{errors.materialName.message}</div>}
                </div>

                <div className="col-md-4">
                  <label className="form-label small fw-semibold">Quantity</label>
                  <input
                    type="number"
                    step="0.01"
                    className={`form-control ${errors.quantity ? 'is-invalid' : ''}`}
                    {...register('quantity', { required: 'Quantity is required', min: { value: 0.01, message: 'Must be greater than 0' } })}
                  />
                  {errors.quantity && <div className="invalid-feedback">{errors.quantity.message}</div>}
                </div>

                <div className="col-md-4">
                  <label className="form-label small fw-semibold">Unit</label>
                  <input
                    className={`form-control ${errors.unit ? 'is-invalid' : ''}`}
                    placeholder="kg, tonnes, units..."
                    {...register('unit', { required: 'Unit is required' })}
                  />
                  {errors.unit && <div className="invalid-feedback">{errors.unit.message}</div>}
                </div>

                <div className="col-md-4">
                  <label className="form-label small fw-semibold">Price per unit (₹)</label>
                  <input
                    type="number"
                    step="0.01"
                    className={`form-control ${errors.pricePerUnit ? 'is-invalid' : ''}`}
                    {...register('pricePerUnit', { required: 'Price is required', min: { value: 0.01, message: 'Must be greater than 0' } })}
                  />
                  {errors.pricePerUnit && <div className="invalid-feedback">{errors.pricePerUnit.message}</div>}
                </div>

                <div className="col-md-6">
                  <label className="form-label small fw-semibold">Purity (optional)</label>
                  <input className="form-control" placeholder="e.g. 99.9%" {...register('purity')} />
                </div>

                <div className="col-md-6">
                  <label className="form-label small fw-semibold">Warehouse location (optional)</label>
                  <input className="form-control" {...register('warehouseLocation')} />
                </div>

                <div className="col-12">
                  <label className="form-label small fw-semibold">Description (optional)</label>
                  <textarea className="form-control" rows={2} {...register('description')} />
                </div>
              </div>

              <button type="submit" className="btn btn-success mt-3 d-inline-flex align-items-center gap-2" disabled={submitting}>
                {submitting && <span className="spinner-border spinner-border-sm" />}
                {editingId ? 'Save Changes' : 'List Material'}
              </button>
            </form>
          </div>
        </div>
      )}

      <div className="card border-0 shadow-sm">
        <div className="card-body p-0">
          {loading ? (
            <LoadingSpinner />
          ) : error ? (
            <ErrorState />
          ) : materials.length === 0 ? (
            <EmptyState icon={FiBox} title="No materials listed yet" message="List your first recovered material to start selling on the marketplace." />
          ) : (
            <div className="table-responsive">
              <table className="table align-middle mb-0">
                <thead>
                  <tr className="text-muted small text-uppercase">
                    <th className="ps-3">Material</th>
                    <th>Category</th>
                    <th>Quantity</th>
                    <th>Price / Unit</th>
                    <th>Status</th>
                    <th className="pe-3"></th>
                  </tr>
                </thead>
                <tbody>
                  {materials.map((m) => (
                    <tr key={m.id}>
                      <td className="ps-3 small fw-semibold">{m.materialName}</td>
                      <td className="small">{m.categoryName}</td>
                      <td className="small">{m.quantity} {m.unit}</td>
                      <td className="small">₹{m.pricePerUnit}</td>
                      <td><StatusBadge status={m.availabilityStatus} /></td>
                      <td className="pe-3 text-end">
                        <button className="btn btn-sm btn-light border me-1" onClick={() => openEditForm(m)}>
                          <FiEdit2 size={14} />
                        </button>
                        <button
                          className="btn btn-sm btn-light border text-danger"
                          disabled={deletingId === m.id}
                          onClick={() => handleDelete(m.id)}
                        >
                          <FiTrash2 size={14} />
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

export default MaterialListings;
