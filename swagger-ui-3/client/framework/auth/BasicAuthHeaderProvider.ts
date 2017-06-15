import ClientBasicAuthConf from '../../../common/domain/model/ClientBasicAuthConf';
import AuthHeaderProvider from './AuthHeaderProvider';

export default class BasicAuthHeaderProvider implements AuthHeaderProvider {
  private readonly authConf: ClientBasicAuthConf;

  constructor(authConf: ClientBasicAuthConf) {
    this.authConf = authConf;
  }

  public getAuthorizationHeader(): string {
    return 'Basic ' + btoa(`${this.authConf.username}:${this.authConf.password}`);
  }
}
