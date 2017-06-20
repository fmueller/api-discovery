import { Context } from 'koa';
import { log } from '../../framework/logger';
import AbstractOAuth2Conf from './AbstractOAuth2Conf';
import AuthContext from './AuthContext';

import TokenProvider from '../../framework/tokens/TokenProvider';
import TokenSupplierFactory from '../../framework/tokens/TokenSupplierFactory';

/**
 * Authorisation context for OAuth2 based client authentication.
 */
export default class OAuth2Context implements AuthContext {
  public readonly baseUrl: string;
  private readonly forwardAuthorization: boolean;
  private readonly tokenSupplier: TokenProvider.TokenSupplier;

  /**
   * @param authConf Authentication configuration.
   */
  constructor(authConf: AbstractOAuth2Conf) {
    this.baseUrl = authConf.baseUrl;
    this.forwardAuthorization = !!authConf.forwardClientAuthorization;
    const factory = new TokenSupplierFactory();
    this.tokenSupplier = factory.create(authConf);
  }

  /**
   * Get a Bearer HTTP authorization header. If the context is configured to
   * foward client-side authorization data, it will be used instead of
   * invoking the server-side authentication flow.
   *
   * @param ctx Optional HTTP request context.
   * @return Bearer authorization header.
   */
  public async getAuthorizationHeader(ctx?: Context): Promise<string> {
    if (ctx && this.forwardAuthorization) {
      const auth: string = ctx.headers['Authorization'];
      if (auth && /^Bearer (\S+)$/.test(auth)) {
        log.debug('Forwarding client-side Bearer authorization.');
        return auth;
      }
      log.warn('AuthContext should forward Bearer authorization but cannot.');
    }
    log.debug('Using server-side Bearer authorization.');
    const token = await this.tokenSupplier();
    return `Bearer ${token}`;
  }
}
