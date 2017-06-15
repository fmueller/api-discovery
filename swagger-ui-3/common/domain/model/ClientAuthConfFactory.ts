import { Validator } from '../validate';
import ClientAuthConf from './ClientAuthConf';
import ClientBasicAuthConf from './ClientBasicAuthConf';
import ClientOAuth2Conf from './ClientOAuth2Conf';

export default class ClientAuthConfFactory {
  private readonly validate: Validator;

  constructor(validator: Validator) {
    this.validate = validator;
  }

  public create(data: any): ClientAuthConf {
    if (data.scheme === 'basic') {
      return new ClientBasicAuthConf(this.validate(data, ClientBasicAuthConf.schema));
    } else if (data.scheme === 'oauth2') {
      return new ClientOAuth2Conf(this.validate(data, ClientOAuth2Conf.schema));
    } else {
      throw new Error(`Unknown ClientAuthConf scheme ${data.scheme}`);
    }
  }

  public bindCreate(): (data: any) => ClientAuthConf {
    return this.create.bind(this);
  }
}
