import { Context } from 'koa';
import AuthContext from '../domain/model/AuthContext';

export default abstract class AbstractGateway {
  private readonly authContext: AuthContext;
  private ctx?: Context;

  constructor(authContext: AuthContext) {
    this.authContext = authContext;
  }

  public withContext(ctx: Context) {
    this.ctx = ctx;
    return this;
  }

  protected getAuthorizationHeader(): Promise<string> {
    return this.authContext.getAuthorizationHeader(this.ctx);
  }
}
