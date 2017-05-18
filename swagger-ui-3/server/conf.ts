import path = require('path')

export const isProduction = () => process.env.NODE_ENV === 'production'
export const enableWebpackDev = () => !!process.env.API_DISCOVERY_ENABLE_WEBPACK_DEV
export const port = () => parseInt(process.env.API_DISCOVERY_PORT) || 3001
export const staticDir = () => process.env.API_DISCOVERY_STATIC_DIR || path.resolve(__dirname, '../client')
