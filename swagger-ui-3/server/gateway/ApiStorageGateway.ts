import superagent = require('superagent');
import AuthContext from '../domain/model/AuthContext';
import AbstractGateway from './AbstractGateway';

import Api from '../../common/domain/model/Api';
import ApiList from '../../common/domain/model/ApiList';
import { ApiLifecycleState } from '../../common/domain/model/ApiMetaData';
import ApiVersion from '../../common/domain/model/ApiVersion';
import ApiVersionList from '../../common/domain/model/ApiVersionList';

export default class ApiStorageGateway extends AbstractGateway {
  private readonly baseUrl: string;

  constructor(authContext: AuthContext) {
    super(authContext);
    this.baseUrl = authContext.baseUrl;
  }

  public async getApis(options: { lifecycleState?: ApiLifecycleState }): Promise<ApiList> {
    const auth = await this.getAuthorizationHeader();
    const response = await superagent
      .get(`${this.baseUrl}/apis`)
      .query({
        lifecycle_state: options.lifecycleState
      })
      .set('Authorization', auth);
    return response.body;
  }

  public async getApi(id: string): Promise<Api> {
    const auth = await this.getAuthorizationHeader();
    const response = await superagent.get(`${this.baseUrl}/apis/${id}`).set('Authorization', auth);
    return response.body;
  }

  public async getVersions(id: string): Promise<ApiVersionList> {
    const auth = await this.getAuthorizationHeader();
    const response = await superagent
      .get(`${this.baseUrl}/apis/${id}/versions`)
      .set('Authorization', auth);
    return response.body;
  }

  public async getVersion(id: string, version: string): Promise<ApiVersion> {
    const auth = await this.getAuthorizationHeader();
    const response = await superagent
      .get(`${this.baseUrl}/apis/${id}/versions/${version}`)
      .set('Authorization', auth);
    return response.body;
  }

  public async get(path: string): Promise<ApiVersion> {
    const auth = await this.getAuthorizationHeader();
    const response = await superagent
      .get(`${this.baseUrl}/${path.replace(/^\//, '')}`)
      .set('Authorization', auth);
    return response.body;
  }
}
