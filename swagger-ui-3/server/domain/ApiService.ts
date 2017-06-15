import { IMiddleware } from 'koa-router';
import AuthContext from '../domain/model/AuthContext';
import ApiStorageGateway from '../gateway/ApiStorageGateway';

/**
 * Fetches APIs from the remote API Storage service.
 */
export default class ApiService {
  private readonly apiStorage: ApiStorageGateway;

  constructor(authContext: AuthContext) {
    this.apiStorage = new ApiStorageGateway(authContext);
  }

  public getApisReadHandler(): IMiddleware {
    return async ctx => {
      ctx.body = await this.apiStorage.withContext(ctx).getApis({
        lifecycleState: 'ACTIVE'
      });
    };
  }

  public getApiReadHandler(): IMiddleware {
    return async ctx => {
      ctx.body = await this.apiStorage.withContext(ctx).getApi(ctx.params.id);
    };
  }

  public getDefinitionReadHandler(): IMiddleware {
    return async ctx => {
      ctx.body = await this.apiStorage.withContext(ctx).getLatestDefinition(ctx.params.id);
    };
  }
}
