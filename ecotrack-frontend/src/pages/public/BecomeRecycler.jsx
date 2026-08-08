import React from 'react';
import PartnerApplicationForm from './PartnerApplicationForm';
import { recyclerApplicationService } from '../../services/userService';

const BecomeRecycler = () => (
  <PartnerApplicationForm
    title="Become a Recycler Partner"
    subtitle="Submit your company details for Admin review. You'll receive login credentials once approved."
    submitFn={recyclerApplicationService.apply}
    successMessage="Your Recycler Partner application has been received. Our team will review it and email your login credentials once approved."
  />
);

export default BecomeRecycler;
