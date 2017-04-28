import path from 'path';

export const port = () => parseInt(process.env.API_DISCOVERY_PORT) || 3001;
export const baseDir = () => process.env.API_DISCOVERY_BASE_DIR || __dirname;
export const staticDir = () => path.resolve(baseDir(), '../client');
