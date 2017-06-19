import superagent = require('superagent');
import { getAuthorizationHeader } from '../framework/auth';

import ApiList from '../../common/domain/model/ApiList';
import { ApiLifecycleState } from '../../common/domain/model/ApiMetaData';
import ApiVersionList from '../../common/domain/model/ApiVersionList';

export default class ApiServiceClient {
  private readonly baseUrl: string;

  constructor(options: { baseUrl: string }) {
    this.baseUrl = options.baseUrl;
  }

  public async getApis(lifecycleState: ApiLifecycleState = 'ACTIVE'): Promise<ApiList> {
    const response = await superagent
      .get(`${this.baseUrl}/apis`)
      .query({ lifecycle_state: lifecycleState })
      .set('Authorization', getAuthorizationHeader());

    if (!response.ok) throw new Error(response.text);
    return response.body;
  }

  public async getApiVersions(id: string): Promise<ApiVersionList> {
    const response = await superagent
      .get(`${this.baseUrl}/apis/${id}/versions`)
      .set('Authorization', getAuthorizationHeader());

    if (!response.ok) throw new Error(response.text);
    return response.body;
  }
}
