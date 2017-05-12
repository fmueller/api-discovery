if (process.env.NODE_ENV !== 'production') {
  localStorage.setItem('debug', '*');
}

export default require('debug')('api-discovery');
