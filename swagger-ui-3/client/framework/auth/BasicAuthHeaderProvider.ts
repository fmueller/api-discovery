import ClientBasicAuthConf from '../../../common/domain/model/ClientBasicAuthConf';
import AuthHeaderProvider from './AuthHeaderProvider';

export default class BasicAuthHeaderProvider implements AuthHeaderProvider {
  private readonly username: string;
  private readonly password: string;

  constructor(authConf: ClientBasicAuthConf) {
    this.username = authConf.username;
    this.password = authConf.password;
  }

  public isAuthorised(): boolean {
    // TODO: actually check authorisation.
    return !!this.username && !!this.password;
  }

  public getAuthorizationHeader(): string {
    return 'Basic ' + btoa(`${this.username}:${this.password}`);
  }
}
