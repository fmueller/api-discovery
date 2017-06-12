import url = require('url');
import { log } from '../logger';
import { LocalProvider, NullProvider, PasswordCredentialsFlowProvider } from '../tokens';
import { AuthConf, DynamicOAuth2Conf, StaticOAuth2Conf } from './AuthConf';
import { basicAuthorizationSupplier, oauth2AuthorizationSupplier } from './AuthorizationSupplier';
import Request from './Request';

export default function createRequest(conf: AuthConf): Request {
  if (conf.scheme === 'basic') {
    const authSupplier = basicAuthorizationSupplier(conf);
    return new Request({ baseUrl: conf.baseUrl, authSupplier });
  }
  if (conf.scheme === 'oauth2' && conf instanceof StaticOAuth2Conf) {
    log.info('Using OAuth2 LocalProvider.');
    const provider = new LocalProvider(conf.accessTokens);

    // Convention: we use the hostname of the base URL as the token name.
    const hostname = url.parse(conf.baseUrl).hostname!;
    const tokenSupplier = provider.getTokenSupplier(hostname);
    const authSupplier = oauth2AuthorizationSupplier(tokenSupplier);
    log.debug('LocalProvider: %j', provider);

    return new Request({ baseUrl: conf.baseUrl, authSupplier });
  }
  if (conf.scheme === 'oauth2' && conf instanceof DynamicOAuth2Conf) {
    log.info('Using OAuth2 PasswordCredentialsFlowProvider.');
    const provider = new PasswordCredentialsFlowProvider({
      accessTokenUri: conf.accessTokenUri,
      tokenInfoUri: conf.tokenInfoUri,
      credentialsDir: conf.credentialsDir
    });

    // Convention: we use the hostname of the base URL as the token name.
    const hostname = url.parse(conf.baseUrl).hostname!;
    provider.addToken(hostname, conf.scopes);
    const tokenSupplier = provider.getTokenSupplier(hostname);
    const authSupplier = oauth2AuthorizationSupplier(tokenSupplier);
    log.debug('PasswordCredentialsFlowProvider: %j', provider);

    return new Request({ baseUrl: conf.baseUrl, authSupplier });
  }
  log.warn('Using OAuth2 NullProvider.');
  const provider = new NullProvider();
  const authSupplier = provider.getTokenSupplier();
  return new Request({ baseUrl: conf.baseUrl, authSupplier });
}
