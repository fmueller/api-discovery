import AuthConf from './AuthConf';
import BasicAuthConf from './BasicAuthConf';
import DynamicOAuth2Conf from './DynamicOAuth2Conf';
import StaticOAuth2Conf from './StaticOAuth2Conf';

/**
 * @param accessTokens An optional string of static access tokens.
 * Takes precedence over the dynamic authentication method.
 * @return A function that can parse configuration from raw objects.
 */
function getAuthConfParser(accessTokens?: string): (data: any) => AuthConf {
  return (data: any) => {
    if (data && data.scheme === 'basic') return new BasicAuthConf(data);
    if (data && data.scheme === 'oauth2') {
      if (accessTokens) return new StaticOAuth2Conf({ ...data, accessTokens });
      else return new DynamicOAuth2Conf(data);
    }
    throw new Error('Illegal AuthConf.');
  };
}

export default class AuthConfFactory {
  private readonly authConfParser: (data: any) => AuthConf;

  /**
   * @param options Optional parameters.
   * @param options.accessTokens Static access tokens.
   */
  constructor(options: { accessTokens?: string } = {}) {
    this.authConfParser = getAuthConfParser(options.accessTokens);
  }

  /**
   * @param data Some raw object.
   * @return A new {@link AuthConf} instance.
   */
  public create(data: object): AuthConf {
    return this.authConfParser(data);
  }

  /**
   * @return A bound {@link create} function.
   */
  public bindCreate(): (data: object) => AuthConf {
    return this.create.bind(this);
  }
}
