import { IMiddleware } from 'koa-router';

export default class HealthService {
  public getHealthHandler(): IMiddleware {
    return async ctx => {
      ctx.body = 'OK';
    };
  }
}
