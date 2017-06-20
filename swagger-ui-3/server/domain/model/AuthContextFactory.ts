import { log } from '../../framework/logger';
import AbstractOAuth2Conf from './AbstractOAuth2Conf';
import AuthConf from './AuthConf';
import AuthContext from './AuthContext';
import BasicAuthConf from './BasicAuthConf';
import BasicAuthContext from './BasicAuthContext';
import OAuth2Context from './OAuth2Context';

export default class AuthContextFactory {
  /**
   * Create a new authorisation context from a given configuration.
   *
   * @param authConf Auhtentication configuration.
   */
  public create(authConf: AuthConf): AuthContext {
    if (authConf instanceof AbstractOAuth2Conf) {
      log.info('Creating OAuth2Context.');
      return new OAuth2Context(authConf);
    }
    if (authConf instanceof BasicAuthConf) {
      log.info('Creating BasicAuthContext.');
      return new BasicAuthContext(authConf);
    }
    throw new Error(`Cannot create AuthContext for given AuthConf.`);
  }
}
