import Router = require('koa-router');
import ApiService from './domain/ApiService';
import HealthService from './domain/HealthService';

const createRoutes = (router: Router) => {
  const healthService = new HealthService();
  const apiService = new ApiService();

  router.get('/health', healthService.getHealthHandler());
  router.get('/apis', apiService.getApisReadHandler());
  router.get('/apis/:id', apiService.getApiReadHandler());
  router.get('/apis/:id/definition', apiService.getDefinitionReadHandler());

  return router;
};

export default () => createRoutes(new Router());
