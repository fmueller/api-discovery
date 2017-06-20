export namespace TokenProvider {
  /**
   * A collection of token values by token name.
   */
  export type TokenSet = { [name: string]: string };
  /**
   * A collection of token scopes by token name.
   */
  export type TokenScopes = { [name: string]: string[] };
  /**
   * A function that returns a single authorization token.
   */
  export type TokenSupplier = () => Promise<string>;
}

/**
 * Provides authorization tokens.
 */
export interface TokenProvider {
  /**
   * Get a collection of all token values by name.
   * @return Collection of all tokens by name.
   */
  getTokens(): Promise<TokenProvider.TokenSet>;

  /**
   * Get a single token by name.
   * @param name Name of the token.
   * @return Value of the token.
   */
  getToken(name: string): Promise<string>;

  /**
   * Get a supplier function for a given token name.
   * @param name Name of the token.
   * @return Function that returns a valid token value.
   */
  getTokenSupplier(name: string): TokenProvider.TokenSupplier;
}

export default TokenProvider;
