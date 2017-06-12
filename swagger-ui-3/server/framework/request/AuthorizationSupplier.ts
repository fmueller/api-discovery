import { AuthOptions } from 'request';
import { TokenProvider } from '../tokens';
import { BasicConf } from './AuthConf';

/**
 * Returns authorization options for the request client.
 */
export type AuthorizationSupplier = () => Promise<AuthOptions>;

/**
 * Create a basic authorization supplier.
 * @param conf Authorization configuration.
 * @return A basic authorization supplier.
 */
export function basicAuthorizationSupplier(conf: BasicConf): AuthorizationSupplier {
  return async () => ({
    sendImmediately: true,
    credentials: {
      user: conf.user,
      pass: conf.pass
    }
  });
}

/**
 * Create an OAuth2 authorization supplier.
 * @param tokenSupplier A token supplier.
 * @return An OAuth2 authorization supplier.
 */
export function oauth2AuthorizationSupplier(
  tokenSupplier: TokenProvider.TokenSupplier
): AuthorizationSupplier {
  return async () => ({
    sendImmediately: true,
    bearer: await tokenSupplier()
  });
}
