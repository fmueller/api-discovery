import url = require('url');
import { log } from '../logger';
import LocalProvider from './LocalProvider';
import PasswordCredentialsFlowProvider from './PasswordCredentialsFlowProvider';
import TokenProvider from './TokenProvider';

import AbstractOAuth2Conf from '../../domain/model/AbstractOAuth2Conf';
import DynamicOAuth2Conf from '../../domain/model/DynamicOAuth2Conf';
import StaticOAuth2Conf from '../../domain/model/StaticOAuth2Conf';

export default class TokenSupplierFactory {
  public create(authConf: AbstractOAuth2Conf): TokenProvider.TokenSupplier {
    if (authConf instanceof StaticOAuth2Conf) {
      log.info('Using OAuth2 LocalProvider.');
      const provider = new LocalProvider(authConf.accessTokens);

      // Convention: we use the hostname of the base URL as the token name.
      const hostname = url.parse(authConf.baseUrl).hostname!;
      log.debug('LocalProvider: %j', provider);
      return provider.getTokenSupplier(hostname);
    }
    if (authConf instanceof DynamicOAuth2Conf) {
      log.info('Using OAuth2 PasswordCredentialsFlowProvider.');
      const provider = new PasswordCredentialsFlowProvider(authConf);

      // Convention: we use the hostname of the base URL as the token name.
      const hostname = url.parse(authConf.baseUrl).hostname!;
      provider.addToken(hostname, authConf.scopes);
      log.debug('PasswordCredentialsFlowProvider: %j', provider);
      return provider.getTokenSupplier(hostname);
    }
    throw new Error(`Cannot create TokenSupplier for given AuthConf.`);
  }
}
