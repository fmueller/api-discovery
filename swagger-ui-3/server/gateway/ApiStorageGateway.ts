import superagent = require('superagent');
import AuthContext from '../domain/model/AuthContext';
import AbstractGateway from './AbstractGateway';
import NotFoundError from './NotFoundError';

export type LifecycleState = 'ACTIVE' | 'INACTIVE' | 'DECOMMISSIONED';

export default class ApiStorageGateway extends AbstractGateway {
  private readonly baseUrl: string;

  constructor(authContext: AuthContext) {
    super(authContext);
    this.baseUrl = authContext.baseUrl;
  }

  public async getApis(options: { lifecycleState?: LifecycleState }): Promise<any> {
    const auth = await this.getAuthorizationHeader();
    const response = await superagent
      .get(`${this.baseUrl}/apis`)
      .query({
        lifecycle_state: options.lifecycleState
      })
      .set('Authorization', auth);
    return response.body;
  }

  public async getApi(id: string): Promise<any> {
    const auth = await this.getAuthorizationHeader();
    const response = await superagent.get(`${this.baseUrl}/apis/${id}`).set('Authorization', auth);
    return response.body;
  }

  public async getLatestDefinition(id: string): Promise<any> {
    const auth = await this.getAuthorizationHeader();
    const response = await superagent
      .get(`${this.baseUrl}/apis/${id}/versions`)
      .set('Authorization', auth);

    const versions = response.body.versions.slice().sort((a: any, b: any) => {
      if (a.api_version < b.api_version) return 1;
      if (a.api_version > b.api_version) return -1;
      return 0;
    });

    if (versions[0]) return versions[0].definitions[0];
    throw new NotFoundError(`${this.baseUrl}/apis/${id}/versions`);
  }
}
