export interface AuthHeaderProvider {
  isAuthorised(): boolean;
  getAuthorizationHeader(): string;
}

export default AuthHeaderProvider;
