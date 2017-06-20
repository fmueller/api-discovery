import { createSelector } from 'reselect';
import { State } from './reducers';

import ApiDeploymentLink from '../../common/domain/model/ApiDeploymentLink';
import ApiList from '../../common/domain/model/ApiList';
import { ApiLifecycleState } from '../../common/domain/model/ApiMetaData';
import ApiVersion from '../../common/domain/model/ApiVersion';
import ApiVersionList from '../../common/domain/model/ApiVersionList';

export interface SelectedApiVersion {
  version: string;
  lifecycleState: ApiLifecycleState;
  definition: string;
  applications: ApiDeploymentLink[];
}

function id<T>(_: T) {
  return _;
}

export default {
  apis: createSelector(id, (state: State) => {
    if (state.has('apiList')) {
      const apiList = state.get('apiList') as ApiList;
      return apiList.apis;
    } else {
      return [];
    }
  }),
  selectedApi: createSelector(id, (state: State) => state.get('selectedApi')),
  apiVersions: createSelector(id, (state: State) => {
    if (state.has('apiVersionList')) {
      const apiVersionList = state.get('apiVersionList') as ApiVersionList;
      return apiVersionList.versions;
    } else {
      return [];
    }
  }),
  selectedApiVersion: createSelector(id, (state: State) => {
    const apiVersion = state.get('selectedApiVersion') as ApiVersion;
    if (!apiVersion) return;
    return {
      version: apiVersion.api_version,
      lifecycleState: apiVersion.lifecycle_state,
      definition: apiVersion.definitions[0].definition, // TODO: order by created_at and select latest
      applications: apiVersion.definitions[0].applications
    } as SelectedApiVersion;
  })
};
