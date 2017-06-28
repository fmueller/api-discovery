import ClientAuthConf from './ClientAuthConf';
import ClientBasicAuthConf from './ClientBasicAuthConf';
import ClientOAuth2Conf from './ClientOAuth2Conf';

export default class ClientAuthConfFactory {
  private readonly urlResolver: (url: string) => string;

  /**
   * @param urlResolver Function that resolves URLs in configuration values.
   */
  constructor(urlResolver?: (url: string) => string) {
    this.urlResolver = urlResolver || (_ => _);
  }

  public create(data: any): ClientAuthConf {
    if (data.scheme === 'basic') {
      return new ClientBasicAuthConf(data);
    } else if (data.scheme === 'oauth2') {
      return new ClientOAuth2Conf(data, this.urlResolver);
    } else {
      throw new Error(`Unknown ClientAuthConf scheme ${data.scheme}`);
    }
  }

  public bindCreate(): (data: any) => ClientAuthConf {
    return this.create.bind(this);
  }
}
