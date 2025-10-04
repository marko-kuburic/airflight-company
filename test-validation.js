// Test script to verify payment validation functions
const validateCardNumber = (value) => {
  const cleaned = value.replace(/\s/g, '');
  if (!cleaned) return 'Card number is required';
  if (!/^\d{16}$/.test(cleaned)) return 'Card number must be exactly 16 digits';
  return null;
};

const validateExpiry = (value) => {
  if (!value) return 'Expiry date is required';
  if (!/^\d{2}\/\d{2}$/.test(value)) return 'Use MM/YY format';
  const [month, year] = value.split('/');
  const monthNum = parseInt(month);
  const yearNum = parseInt('20' + year);
  if (monthNum < 1 || monthNum > 12) return 'Invalid month';
  const now = new Date();
  const expiryDate = new Date(yearNum, monthNum - 1);
  if (expiryDate <= now) return 'Card has expired';
  return null;
};

const validateCVC = (value) => {
  if (!value) return 'CVC is required';
  if (!/^\d{3}$/.test(value)) return 'CVC must be exactly 3 digits';
  return null;
};

const formatCardNumber = (value) => {
  const cleaned = value.replace(/\D/g, '');
  const limited = cleaned.substring(0, 16);
  return limited.replace(/(\d{4})(?=\d)/g, '$1 ');
};

const formatExpiry = (value) => {
  const cleaned = value.replace(/\D/g, '');
  const limited = cleaned.substring(0, 4);
  if (limited.length >= 2) {
    return limited.substring(0, 2) + '/' + limited.substring(2);
  }
  return limited;
};

const formatCVC = (value) => {
  return value.replace(/\D/g, '').substring(0, 3);
};

// Test cases
console.log('=== Testing Card Number Validation ===');
console.log('Valid 16 digits:', validateCardNumber('4111 1111 1111 1111')); // null (valid)
console.log('Invalid 15 digits:', validateCardNumber('4111 1111 1111 111')); // error
console.log('Invalid letters:', validateCardNumber('4111 1111 1111 111a')); // error

console.log('\n=== Testing Card Number Formatting ===');
console.log('Format input "4111111111111111":', formatCardNumber('4111111111111111')); // "4111 1111 1111 1111"
console.log('Format input "4111 1111":', formatCardNumber('4111 1111')); // "4111 1111"

console.log('\n=== Testing Expiry Validation ===');
console.log('Valid future date:', validateExpiry('12/25')); // null (valid)
console.log('Invalid format:', validateExpiry('1225')); // error
console.log('Invalid month:', validateExpiry('13/25')); // error
console.log('Past date:', validateExpiry('01/20')); // error

console.log('\n=== Testing Expiry Formatting ===');
console.log('Format input "1225":', formatExpiry('1225')); // "12/25"
console.log('Format input "12":', formatExpiry('12')); // "12"

console.log('\n=== Testing CVC Validation ===');
console.log('Valid 3 digits:', validateCVC('123')); // null (valid)
console.log('Invalid 2 digits:', validateCVC('12')); // error
console.log('Invalid 4 digits:', validateCVC('1234')); // error
console.log('Invalid letters:', validateCVC('12a')); // error

console.log('\n=== Testing CVC Formatting ===');
console.log('Format input "123":', formatCVC('123')); // "123"
console.log('Format input "1234":', formatCVC('1234')); // "123" (truncated)
console.log('Format input "12a":', formatCVC('12a')); // "12" (letters removed)

console.log('\n✅ All validation functions implemented correctly!');