import log from '../debug';

type Options = {
  authorizationUri: string;
  clientId: string;
  redirectUri: string;
  requestParameters?: object;
};

type TokenInfo = {
  state?: string;
  accessToken?: string;
  expiryTime: number;
};

const refreshThreshold = 4000; // milliseconds

export default class OAuth2Client {
  private readonly authorizationUri: string;
  private readonly requestParameters: object;
  private readonly tokenInfo: TokenInfo;

  constructor(options: Options) {
    log('Create new OAuth2Client %j', options);
    this.authorizationUri = options.authorizationUri;
    this.requestParameters = {
      response_type: 'token',
      client_id: options.clientId,
      redirect_uri: options.redirectUri,
      ...options.requestParameters
    };
    // Load a previously stored TokenInfo object from local storage.
    const storedInfo = window.localStorage.getItem('tokenInfo');
    this.tokenInfo = storedInfo ? JSON.parse(storedInfo) : { expiryTime: 0 };

    // In case we were redirected, parse the token information from the URL.
    const tokenInfo = OAuth2Client.parseUrl();
    if (tokenInfo.accessToken) {
      log('Received a new access token.');
      if (tokenInfo.state !== this.tokenInfo.state) throw new Error('Unexpected state.');
      this.updateTokenInfo(tokenInfo);
    }
  }

  private static parseUrl(): TokenInfo {
    const match = /https?:[^\?]+\?(\S+)/.exec(window.location.href);
    if (match) {
      const pairs = match[1].split('&');
      const params = pairs.reduce((obj, pair) => {
        const [key, value] = pair.split('=');
        return Object.assign(obj, { [key]: decodeURIComponent(value) });
      }, {} as { [name: string]: string });

      return {
        state: params['state'],
        accessToken: params['access_token'],
        expiryTime: new Date().getTime() + parseInt(params['expires_in'], 10) * 1000
      };
    }
    return { expiryTime: 0 };
  }

  private updateTokenInfo(tokenInfo: TokenInfo) {
    Object.assign(this.tokenInfo, tokenInfo);
    window.localStorage.setItem('tokenInfo', JSON.stringify(this.tokenInfo));
  }

  private getUri(state: string): string {
    const params: { [key: string]: string } = { ...this.requestParameters, state };
    const qs = Object.keys(params)
      .filter(key => !!params[key])
      .map(key => [key, encodeURIComponent(params[key])].join('='))
      .join('&');
    return `${this.authorizationUri}?${qs}`;
  }

  private refreshTokenIfNecessary() {
    if (this.isAuthorised()) {
      log('Existing token is still valid.');
    } else {
      log('Requesting new token.');
      const state = Math.random().toString(36).slice(2);
      this.updateTokenInfo({ state, expiryTime: 0 });
      window.location.href = this.getUri(state);
    }
  }

  public isAuthorised(): boolean {
    return this.tokenInfo.expiryTime - new Date().getTime() < refreshThreshold;
  }

  public getToken(): string {
    this.refreshTokenIfNecessary();
    if (!this.tokenInfo.accessToken) throw new Error('No token available.');
    else return this.tokenInfo.accessToken;
  }
}
