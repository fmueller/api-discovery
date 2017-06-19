import { Validator } from '../validate';
import ClientAuthConf from './ClientAuthConf';
import ClientBasicAuthConf from './ClientBasicAuthConf';
import ClientOAuth2Conf from './ClientOAuth2Conf';

export default class ClientAuthConfFactory {
  private readonly validate: Validator;
  private readonly urlResolver: (url: string) => string;

  /**
   * @param validator Function that validates JSON schema.
   * @param urlResolver Function that resolves URLs in configuration values.
   */
  constructor(validator: Validator, urlResolver?: (url: string) => string) {
    this.validate = validator;
    this.urlResolver = urlResolver || (_ => _);
  }

  public create(data: any): ClientAuthConf {
    if (data.scheme === 'basic') {
      return new ClientBasicAuthConf(this.validate(data, ClientBasicAuthConf.schema));
    } else if (data.scheme === 'oauth2') {
      return new ClientOAuth2Conf(this.validate(data, ClientOAuth2Conf.schema), this.urlResolver);
    } else {
      throw new Error(`Unknown ClientAuthConf scheme ${data.scheme}`);
    }
  }

  public bindCreate(): (data: any) => ClientAuthConf {
    return this.create.bind(this);
  }
}
