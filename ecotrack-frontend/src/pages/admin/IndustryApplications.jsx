import React from 'react';
import ApplicationReviewPage from './ApplicationReviewPage';
import { industryApplicationService } from '../../services/userService';

const IndustryApplications = () => (
  <ApplicationReviewPage
    title="Industrial Buyer Applications"
    subtitle="Review and verify companies applying to become Industrial Buyers"
    service={industryApplicationService}
  />
);

export default IndustryApplications;
