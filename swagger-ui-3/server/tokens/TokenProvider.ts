export interface TokenProvider {
  getTokens(): { [key: string]: string }
  getToken(key: string): string | void
}

export default TokenProvider
