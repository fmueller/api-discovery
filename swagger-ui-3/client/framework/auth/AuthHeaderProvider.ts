export interface AuthHeaderProvider {
  getAuthorizationHeader(): string;
}

export default AuthHeaderProvider;
