import * as conf from '../conf';
import log from '../debug';
import AuthHeaderProvider from './AuthHeaderProvider';
import BasicAuthHeaderProvider from './BasicAuthHeaderProvider';
import OAuth2HeaderProvider from './OAuth2HeaderProvider';

import ClientAuthConfFactory from '../../../common/domain/model/ClientAuthConfFactory';
import ClientBasicAuthConf from '../../../common/domain/model/ClientBasicAuthConf';
import ClientOAuth2Conf from '../../../common/domain/model/ClientOAuth2Conf';

let authHeaderProvider: AuthHeaderProvider;

export function getAuthHeaderProvider(): AuthHeaderProvider {
  if (authHeaderProvider) return authHeaderProvider;

  const authConfFactory = new ClientAuthConfFactory();
  const authConfData = conf.getObject('authConf');
  const authConf = authConfFactory.create(authConfData);

  if (authConf instanceof ClientBasicAuthConf) {
    log('Using BasicAuthHeaderProvider');
    authHeaderProvider = new BasicAuthHeaderProvider(authConf);
  } else if (authConf instanceof ClientOAuth2Conf) {
    log('Using OAuth2HeaderProvider');
    authHeaderProvider = new OAuth2HeaderProvider(authConf);
  } else {
    throw new Error(`Unknown auth scheme ${authConfData.scheme}`);
  }

  return authHeaderProvider;
}

export function isAuthorised(): boolean {
  return getAuthHeaderProvider().isAuthorised();
}

export function getAuthorizationHeader(): string {
  return getAuthHeaderProvider().getAuthorizationHeader();
}
