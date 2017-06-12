import { IMiddleware } from 'koa-router';
import { createRequest, readAuthConf } from '../framework/request';
import ApiStorageGateway from '../gateway/ApiStorageGateway';

export default class ApiService {
  private readonly apiStorage: ApiStorageGateway;

  constructor() {
    this.apiStorage = new ApiStorageGateway(
      createRequest(
        readAuthConf({
          authConfigKey: 'apiStorageConf',
          accessTokensConfigKey: 'oauth2AccessTokens'
        })
      )
    );
  }

  public getApisReadHandler(): IMiddleware {
    return async ctx => {
      ctx.body = await this.apiStorage.getApis({
        lifecycleState: 'ACTIVE'
      });
    };
  }

  public getApiReadHandler(): IMiddleware {
    return async ctx => {
      ctx.body = await this.apiStorage.getApi(ctx.params.id);
    };
  }

  public getDefinitionReadHandler(): IMiddleware {
    return async ctx => {
      ctx.body = await this.apiStorage.getLatestDefinition(ctx.params.id);
    };
  }
}
