import conf from '../framework/conf';
import { log } from '../framework/logger';
import LocalProvider from './LocalProvider';
import NullProvider from './NullProvider';
import PasswordCredentialsFlowProvider, { TokenScopes } from './PasswordCredentialsFlowProvider';
import TokenProvider from './TokenProvider';

function selectProvider(): TokenProvider {
  const oauth2AccessTokens = conf.get('oauth2AccessTokens');

  if (!!oauth2AccessTokens) {
    log.info('Using OAuth2 LocalProvider.');
    return new LocalProvider(oauth2AccessTokens);
  }

  const accessTokenUri = conf.getString('oauth2TokenUri');
  const tokenInfoUri = conf.getString('oauth2TokenInfoUri');
  const tokenScopes = conf.getObject('oauth2TokenScopes');
  const credentialsDir = conf.getString('oauth2CredentialsDir');

  if (accessTokenUri && tokenInfoUri && tokenScopes && credentialsDir) {
    log.info('Using OAuth2 PasswordCredentialsFlowProvider.');
    const provider = new PasswordCredentialsFlowProvider({
      accessTokenUri,
      tokenInfoUri,
      credentialsDir
    });

    return provider.addTokens(tokenScopes as TokenScopes);
  }

  log.info('Using token NullProvider.');
  return new NullProvider();
}

export default selectProvider();
