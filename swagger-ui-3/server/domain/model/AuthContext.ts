import { Context } from 'koa';

/**
 * Authorisation context for server-side HTTP clients.
 */
export interface AuthContext {
  readonly baseUrl: string;
  /**
   * Get an HTTP authorization header.
   *
   * @param ctx Optional HTTP request context.
   * @return Authorization header.
   */
  getAuthorizationHeader(ctx?: Context): Promise<string>;
}

export default AuthContext;
