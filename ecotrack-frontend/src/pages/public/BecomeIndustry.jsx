import React from 'react';
import PartnerApplicationForm from './PartnerApplicationForm';
import { industryApplicationService } from '../../services/userService';

const BecomeIndustry = () => (
  <PartnerApplicationForm
    title="Become an Industrial Buyer"
    subtitle="Submit your company details for verification. You'll receive login credentials once approved."
    submitFn={industryApplicationService.apply}
    successMessage="Your Industrial Buyer application has been received. Our team will verify it and email your login credentials once approved."
  />
);

export default BecomeIndustry;
