import { Context } from 'koa';
import { log } from '../../framework/logger';
import AuthContext from './AuthContext';
import BasicAuthConf from './BasicAuthConf';

/**
 * Authorisation context for Basic client authentication.
 */
export default class BasicAuthContext implements AuthContext {
  public readonly baseUrl: string;
  private readonly forwardAuthorization: boolean;
  private readonly user: string;
  private readonly pass: string;

  constructor(authConf: BasicAuthConf) {
    this.baseUrl = authConf.baseUrl;
    this.forwardAuthorization = !!authConf.forwardClientAuthorization;
    this.user = authConf.user;
    this.pass = authConf.pass;
  }

  /**
   * Get a Basic HTTP authorization header. If the context is configured to
   * foward client-side authorisation data, it will be used instead of
   * the server-side credentials.
   *
   * @param ctx Optional HTTP request context.
   * @return Basic authorization header.
   */
  public async getAuthorizationHeader(ctx?: Context): Promise<string> {
    if (ctx && this.forwardAuthorization) {
      log.debug('Request headers: %j', ctx.headers);
      const auth: string = ctx.headers['authorization'];
      if (auth && /^Basic (\S+)$/.test(auth)) {
        log.debug('Forwarding client-side Basic authorization.');
        return auth;
      }
      log.warn('AuthContext should forward Basic authorization but cannot.');
      return '';
    }
    log.debug('Using server-side Basic authorization.');
    return 'Basic ' + new Buffer(`${this.user}:${this.pass}`).toString('base64');
  }
}
