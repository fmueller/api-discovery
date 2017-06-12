import TokenProvider from './TokenProvider';

/**
 * Empty token provider which returns nothing.
 */
export default class NullProvider implements TokenProvider {
  public getTokens(): Promise<{ [key: string]: string }> {
    return Promise.resolve({});
  }

  public getToken(): Promise<string> {
    return Promise.resolve('');
  }

  public getTokenSupplier(): TokenProvider.TokenSupplier {
    return () => this.getToken();
  }
}
