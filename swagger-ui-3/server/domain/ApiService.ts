import { IMiddleware } from 'koa-router';
import ApiStorageGateway from '../gateway/ApiStorageGateway';

/**
 * Proxy for the remote API Storage service.
 */
export default class ApiService {
  private readonly apiStorage: ApiStorageGateway;

  /**
   * @param gateway Gateway instance for accessing the remote API storage service.
   */
  constructor(gateway: ApiStorageGateway) {
    this.apiStorage = gateway;
  }

  /**
   * Foward any GET request.
   * @param mount Path under which which this service is mounted on the server.
   */
  public getReadHandler(mount: string): IMiddleware {
    return async ctx => {
      const path = ctx.request.url.replace(mount, '').replace(/^\/+/, '');
      ctx.body = await this.apiStorage.withContext(ctx).get(path);
    };
  }
}
