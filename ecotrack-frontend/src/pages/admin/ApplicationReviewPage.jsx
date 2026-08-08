import React, { useEffect, useState, useCallback } from 'react';
import { FiCheckSquare, FiX, FiCheck } from 'react-icons/fi';
import PageHeader from '../../components/common/PageHeader';
import StatusBadge from '../../components/common/StatusBadge';
import { LoadingSpinner, EmptyState, ErrorState } from '../../components/common/Feedback';
import { getErrorMessage } from '../../services/api';
import { useToast } from '../../context/ToastContext';

const STATUS_FILTERS = ['PENDING', 'APPROVED', 'REJECTED', 'ALL'];

/**
 * One shared implementation for both Recycler and Industry application
 * review - the workflow, fields, and UI are identical; only the service
 * passed in differs.
 */
const ApplicationReviewPage = ({ title, subtitle, service }) => {
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [statusFilter, setStatusFilter] = useState('PENDING');
  const [reviewModal, setReviewModal] = useState(null); // { application, approve }
  const [remarks, setRemarks] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const { showToast } = useToast();

  const load = useCallback(async () => {
    setLoading(true);
    setError(false);
    try {
      const params = { size: 20, sort: 'submittedDate,desc' };
      if (statusFilter !== 'ALL') params.status = statusFilter;
      const data = await service.getAll(params);
      setApplications(data.content || []);
    } catch (e) {
      setError(true);
    } finally {
      setLoading(false);
    }
  }, [statusFilter, service]);

  useEffect(() => { load(); }, [load]);

  const openReviewModal = (application, approve) => {
    setReviewModal({ application, approve });
    setRemarks('');
  };

  const handleSubmitReview = async () => {
    setSubmitting(true);
    try {
      await service.review(reviewModal.application.id, { approve: reviewModal.approve, remarks });
      showToast(
        reviewModal.approve
          ? 'Application approved. Login credentials have been created.'
          : 'Application rejected.',
        'success'
      );
      setReviewModal(null);
      load();
    } catch (err) {
      showToast(getErrorMessage(err), 'error');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div>
      <PageHeader title={title} subtitle={subtitle} />

      <div className="d-flex gap-2 flex-wrap mb-3">
        {STATUS_FILTERS.map((s) => (
          <button
            key={s}
            className={`btn btn-sm ${statusFilter === s ? 'btn-success' : 'btn-outline-secondary'}`}
            onClick={() => setStatusFilter(s)}
          >
            {s}
          </button>
        ))}
      </div>

      <div className="card border-0 shadow-sm">
        <div className="card-body p-0">
          {loading ? (
            <LoadingSpinner />
          ) : error ? (
            <ErrorState />
          ) : applications.length === 0 ? (
            <EmptyState icon={FiCheckSquare} title="No applications found" message="Try a different filter." />
          ) : (
            <div className="table-responsive">
              <table className="table align-middle mb-0">
                <thead>
                  <tr className="text-muted small text-uppercase">
                    <th className="ps-3">Company</th>
                    <th>Contact</th>
                    <th>Email / Phone</th>
                    <th>Submitted</th>
                    <th>Status</th>
                    <th className="pe-3"></th>
                  </tr>
                </thead>
                <tbody>
                  {applications.map((a) => (
                    <tr key={a.id}>
                      <td className="ps-3 small fw-semibold">{a.companyName}</td>
                      <td className="small">{a.contactPerson}</td>
                      <td className="small">
                        <div>{a.email}</div>
                        <div className="text-muted">{a.phone}</div>
                      </td>
                      <td className="small">{a.submittedDate?.split('T')[0]}</td>
                      <td><StatusBadge status={a.status} /></td>
                      <td className="pe-3 text-end">
                        {a.status === 'PENDING' && (
                          <div className="d-flex justify-content-end gap-2">
                            <button className="btn btn-sm btn-success" onClick={() => openReviewModal(a, true)}>
                              Approve
                            </button>
                            <button className="btn btn-sm btn-outline-danger" onClick={() => openReviewModal(a, false)}>
                              Reject
                            </button>
                          </div>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>

      {reviewModal && (
        <div
          className="position-fixed top-0 start-0 w-100 h-100 d-flex align-items-center justify-content-center"
          style={{ background: 'rgba(15,23,42,0.4)', zIndex: 100 }}
          onClick={() => setReviewModal(null)}
        >
          <div className="card border-0 shadow-lg" style={{ width: 420 }} onClick={(e) => e.stopPropagation()}>
            <div className="card-body p-4">
              <div className="d-flex justify-content-between align-items-start mb-3">
                <h6 className="fw-bold mb-0 d-flex align-items-center gap-2">
                  {reviewModal.approve ? <FiCheck className="text-success" /> : <FiX className="text-danger" />}
                  {reviewModal.approve ? 'Approve' : 'Reject'} Application
                </h6>
                <button className="btn btn-sm btn-light" onClick={() => setReviewModal(null)}>
                  <FiX size={16} />
                </button>
              </div>
              <p className="small text-muted mb-3">{reviewModal.application.companyName}</p>

              {reviewModal.approve && (
                <div className="alert alert-light border small mb-3">
                  Approving will create login credentials for <strong>{reviewModal.application.email}</strong> automatically.
                </div>
              )}

              <label className="form-label small fw-semibold">Remarks (optional)</label>
              <textarea
                className="form-control mb-3"
                rows={3}
                value={remarks}
                onChange={(e) => setRemarks(e.target.value)}
                placeholder={reviewModal.approve ? 'Any notes for the record...' : 'Reason for rejection...'}
              />

              <button
                className={`btn w-100 d-flex align-items-center justify-content-center gap-2 ${reviewModal.approve ? 'btn-success' : 'btn-danger'}`}
                disabled={submitting}
                onClick={handleSubmitReview}
              >
                {submitting && <span className="spinner-border spinner-border-sm" />}
                Confirm {reviewModal.approve ? 'Approval' : 'Rejection'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ApplicationReviewPage;
