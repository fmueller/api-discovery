import Router = require('koa-router');
import health from './domain/health';

const createRoutes = (router: Router) => router.get('/health', health);

export default () => createRoutes(new Router());
