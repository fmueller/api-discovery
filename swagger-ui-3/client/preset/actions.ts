import ApiList from '../../common/domain/model/ApiList';
import ApiVersion from '../../common/domain/model/ApiVersion';
import ApiVersionList from '../../common/domain/model/ApiVersionList';
import ApiServiceClient from '../domain/ApiServiceClient';
import * as conf from '../framework/conf';
import log from '../framework/debug';

interface Action<P> {
  type: string;
  payload: P;
}

type ActionCreator<P> = (payload: P) => Action<P>;

export const UPDATE_APIS_LOADING_STATUS = 'UPDATE_APIS_LOADING_STATUS';
export const RECEIVE_APIS = 'API_DISCOVERY_RECEIVE_APIS';
export const SET_SELECTED_API = 'API_DISCOVERY_SET_SELECTED_API';
export const RECEIVE_API_VERSIONS = 'API_DISCOVERY_RECEIVE_API_VERSIONS';
export const SET_SELECTED_API_VERSION = 'API_DISCOVERY_SET_SELECTED_API_VERSION';
export type LoadingStatus = 'loading' | 'success' | 'failed';

const apiServiceClient = new ApiServiceClient({ baseUrl: conf.getString('apiServiceUrl') });

const updateApisLoadingStatus: ActionCreator<LoadingStatus> = status => ({
  type: UPDATE_APIS_LOADING_STATUS,
  payload: status
});

const fetchApis = () => async (system: any) => {
  log('Action: fetch APIs');
  system.apiDiscoveryActions.updateApisLoadingStatus('loading');

  try {
    const apiList = await apiServiceClient.getApis();
    system.apiDiscoveryActions.updateApisLoadingStatus('success');
    return system.apiDiscoveryActions.receiveApis(apiList);
  } catch (e) {
    log('Error fetching APIs', e);
    return system.apiDiscoveryActions.updateApisLoadingStatus('failed');
  }
};

const receiveApis: ActionCreator<ApiList> = apiList => ({
  type: RECEIVE_APIS,
  payload: apiList
});

const selectApi = (id: string) => async (system: any) => {
  log('Action: fetch API versions for %s', id);
  system.specActions.updateLoadingStatus('loading');

  try {
    const versionList = await apiServiceClient.getApiVersions(id);
    // By default, select the latest API version.
    const latestVersion = versionList.versions.slice().sort((v1, v2) => {
      if (v1.api_version < v2.api_version) return 1;
      if (v1.api_version > v2.api_version) return -1;
      return 0;
    })[0];
    system.specActions.updateLoadingStatus('success');
    system.apiDiscoveryActions.setSelectedApi(id);
    system.apiDiscoveryActions.receiveApiVersions(versionList);
    await system.apiDiscoveryActions.selectApiVersion(latestVersion);
    window.history.replaceState(null, id, `/apis/${id}`);
  } catch (e) {
    log('Error fetching API', e);
    return system.specActions.updateLoadingStatus('failed');
  }
};

const setSelectedApi: ActionCreator<string> = id => ({
  type: SET_SELECTED_API,
  payload: id
});

const receiveApiVersions: ActionCreator<ApiVersionList> = versionList => ({
  type: RECEIVE_API_VERSIONS,
  payload: versionList
});

const selectApiVersion = (apiVersion: ApiVersion) => async (system: any) => {
  const definition = apiVersion.definitions[0]; // TODO: sort by created_at
  system.apiDiscoveryActions.setSelectedApiVersion(apiVersion);
  system.specActions.updateSpec(definition.definition);
};

const setSelectedApiVersion: ActionCreator<ApiVersion> = apiVersion => ({
  type: SET_SELECTED_API_VERSION,
  payload: apiVersion
});

export default {
  updateApisLoadingStatus,
  fetchApis,
  receiveApis,
  selectApi,
  setSelectedApi,
  receiveApiVersions,
  selectApiVersion,
  setSelectedApiVersion
};
