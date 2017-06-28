import debug = require('debug');

if (process.env.NODE_ENV !== 'production') {
  localStorage.setItem('debug', '*');
}

export default debug('api-portal');
