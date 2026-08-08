import React from 'react';
import ApplicationReviewPage from './ApplicationReviewPage';
import { recyclerApplicationService } from '../../services/userService';

const RecyclerApplications = () => (
  <ApplicationReviewPage
    title="Recycler Partner Applications"
    subtitle="Review and approve companies applying to become Recycler Partners"
    service={recyclerApplicationService}
  />
);

export default RecyclerApplications;
