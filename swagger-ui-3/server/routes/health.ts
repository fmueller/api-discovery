import Router = require('koa-router');

export default (router: Router) =>
  router.get('/health', ctx => {
    ctx.body = 'OK';
  });
