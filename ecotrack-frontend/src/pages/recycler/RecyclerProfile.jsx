import React, { useEffect, useState } from 'react';
import PageHeader from '../../components/common/PageHeader';
import StatusBadge from '../../components/common/StatusBadge';
import { LoadingSpinner, ErrorState } from '../../components/common/Feedback';
import { recyclerService } from '../../services/userService';

const Field = ({ label, value }) => (
  <div className="col-md-6">
    <div className="small text-muted mb-1">{label}</div>
    <div className="fw-semibold small">{value || '—'}</div>
  </div>
);

const RecyclerProfile = () => {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  useEffect(() => {
    recyclerService.getMyProfile()
      .then(setProfile)
      .catch(() => setError(true))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <LoadingSpinner />;
  if (error) return <ErrorState />;

  return (
    <div>
      <PageHeader title="My Profile" subtitle="Your company details on file with EcoTrack" />

      <div className="card border-0 shadow-sm" style={{ maxWidth: 640 }}>
        <div className="card-body p-4">
          <div className="d-flex justify-content-between align-items-start mb-4">
            <div>
              <h5 className="fw-bold mb-1">{profile.companyName}</h5>
              <div className="small text-muted">Registration #{profile.companyRegistrationNumber}</div>
            </div>
            <StatusBadge status={profile.suspended ? 'BLOCKED' : 'ACTIVE'} />
          </div>

          <div className="row g-3">
            <Field label="Contact Person" value={profile.contactPerson} />
            <Field label="Phone" value={profile.phone} />
            <Field label="Address" value={profile.address} />
            <Field label="City" value={profile.city} />
            <Field label="State" value={profile.state} />
            <Field label="Pincode" value={profile.pincode} />
          </div>

          <div className="alert alert-light border small mt-4 mb-0">
            Need to update your company details? Contact EcoTrack support - profile edits for
            Recycler Partners currently go through the Admin team.
          </div>
        </div>
      </div>
    </div>
  );
};

export default RecyclerProfile;
