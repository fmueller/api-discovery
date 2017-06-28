import TokenProvider from './TokenProvider';

function parseTokens(tokenString: string): { [key: string]: string } {
  if (!/^\S+=\S+/.test(tokenString)) {
    return {};
  }

  return tokenString.split(',').reduce((obj, pair) => {
    const [key, token] = pair.split('=');
    if (typeof key === 'string' && typeof token === 'string') {
      return Object.assign(obj, { [key]: token });
    } else {
      return obj;
    }
  }, {});
}

/**
 * Provides tokens from an in-memory store.
 */
export default class LocalProvider implements TokenProvider {
  private readonly oauth2AccessTokens: TokenProvider.TokenSet;

  /**
   * Tokens can be provided as an object or as a string. Example:
   *
   * "token1=abc,token2=xyz"
   *
   * @param tokens A set of OAuth2 access okens as a string or as an object.
   */
  constructor(tokens: string | TokenProvider.TokenSet) {
    this.oauth2AccessTokens = typeof tokens === 'string' ? parseTokens(tokens) : tokens;
  }

  public getTokens(): Promise<TokenProvider.TokenSet> {
    return Promise.resolve(Object.assign({}, this.oauth2AccessTokens));
  }

  public getToken(key: string): Promise<string> {
    return Promise.resolve(this.oauth2AccessTokens[key] || '');
  }

  public getTokenSupplier(name: string): TokenProvider.TokenSupplier {
    return () => this.getToken(name);
  }

  public toJSON(): object {
    return { oauth2AccessTokens: Object.keys(this.oauth2AccessTokens) };
  }

  public toString(): string {
    return JSON.stringify(this.toJSON());
  }
}
