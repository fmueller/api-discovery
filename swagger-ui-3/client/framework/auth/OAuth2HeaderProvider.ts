import ClientOAuth2Conf from '../../../common/domain/model/ClientOAuth2Conf';
import AuthHeaderProvider from './AuthHeaderProvider';
import OAuth2Client from './OAuth2Client';

export default class OAuth2HeaderProvider implements AuthHeaderProvider {
  private readonly authConf: ClientOAuth2Conf;
  private readonly oauth2client: OAuth2Client;

  constructor(authConf: ClientOAuth2Conf) {
    this.authConf = authConf;
    this.oauth2client = new OAuth2Client(authConf);
  }

  public isAuthorised(): boolean {
    return this.oauth2client.isAuthorised();
  }

  public getAuthorizationHeader(): string {
    return `Bearer ${this.oauth2client.getToken()}`;
  }
}
