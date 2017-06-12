import { Request } from '../framework/request';
import NotFoundError from './NotFoundError';

export type LifecycleState = 'ACTIVE' | 'INACTIVE' | 'DECOMMISSIONED';

export default class ApiStorageGateway {
  private readonly request: Request;

  constructor(request: Request) {
    this.request = request;
  }

  public async getApis(options: { lifecycleState?: LifecycleState }): Promise<any> {
    const response = await this.request.get('/apis', {
      json: true,
      qs: {
        lifecycle_state: options.lifecycleState
      }
    });
    return response.body;
  }

  public async getApi(id: string): Promise<any> {
    const response = await this.request.get(`/apis/${id}`, { json: true });
    return response.body;
  }

  public async getLatestDefinition(id: string): Promise<any> {
    const response = await this.request.get(`/apis/${id}/versions`, { json: true });
    const versions = response.body.versions.slice().sort((a: any, b: any) => {
      if (a.api_version < b.api_version) return 1;
      if (a.api_version > b.api_version) return -1;
      return 0;
    });
    if (versions[0]) return versions[0].definitions[0];
    throw new NotFoundError(`${this.request.baseUrl}/apis/${id}/versions`);
  }
}
