export const INDIAN_LOCATIONS = {
  'Maharashtra': ['Mumbai', 'Pune', 'Kolhapur', 'Satara', 'Nashik', 'Nagpur', 'Thane', 'Solapur', 'Aurangabad', 'Amravati'],
  'Rajasthan': ['Jaipur', 'Jodhpur', 'Udaipur', 'Ajmer', 'Kota', 'Bikaner', 'Bhilwara', 'Alwar'],
  'Gujarat': ['Ahmedabad', 'Surat', 'Vadodara', 'Rajkot', 'Bhavnagar', 'Jamnagar', 'Gandhinagar'],
  'Karnataka': ['Bengaluru', 'Mysuru', 'Hubballi', 'Mangaluru', 'Belagavi', 'Davanagere', 'Ballari'],
  'Delhi': ['New Delhi', 'North Delhi', 'South Delhi', 'West Delhi', 'Central Delhi', 'East Delhi'],
  'Tamil Nadu': ['Chennai', 'Coimbatore', 'Madurai', 'Tiruchirappalli', 'Salem', 'Tirunelveli', 'Vellore'],
  'Uttar Pradesh': ['Lucknow', 'Kanpur', 'Agra', 'Varanasi', 'Noida', 'Ghaziabad', 'Meerut', 'Prayagraj'],
  'West Bengal': ['Kolkata', 'Howrah', 'Durgapur', 'Siliguri', 'Asansol', 'Bardhaman'],
  'Telangana': ['Hyderabad', 'Warangal', 'Nizamabad', 'Karimnagar', 'Khammam'],
  'Madhya Pradesh': ['Bhopal', 'Indore', 'Gwalior', 'Jabalpur', 'Ujjain', 'Sagar'],
  'Punjab': ['Ludhiana', 'Amritsar', 'Jalandhar', 'Patiala', 'Bathinda'],
  'Haryana': ['Gurugram', 'Faridabad', 'Panipat', 'Ambala', 'Karnal'],
  'Kerala': ['Thiruvananthapuram', 'Kochi', 'Kozhikode', 'Thrissur', 'Kollam'],
  'Bihar': ['Patna', 'Gaya', 'Bhagalpur', 'Muzaffarpur', 'Purnia'],
  'Goa': ['Panaji', 'Margao', 'Vasco da Gama', 'Mapusa'],
};

export const INDIAN_STATES = Object.keys(INDIAN_LOCATIONS);

export const getCitiesForState = (stateName) => {
  if (!stateName || !INDIAN_LOCATIONS[stateName]) {
    return [];
  }
  return INDIAN_LOCATIONS[stateName];
};
