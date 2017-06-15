import superagent = require('superagent');
import { getAuthorizationHeader } from '../framework/auth';
import log from '../framework/debug';

type ApiMetaData = {
  id: string;
  lifecycle_state: 'ACTIVE' | 'INACTIVE' | 'DECOMMISSIONED';
};

type ApiList = {
  apis: ApiMetaData[];
};

export interface Action<P> {
  type: string;
  payload: P;
}

export interface ReceiveApiListAction extends Action<ApiList> {
  type: 'API_DISCOVERY_RECEIVE_API_LIST';
}

type ActionCreator<P> = (payload: P) => Action<P>;

export const RECEIVE_API_LIST = 'API_DISCOVERY_RECEIVE_API_LIST';

const fetchApis = () => async (system: any) => {
  log('Fetch APIs');

  let response;
  try {
    response = await superagent.get(`/apis`).set('Authorization', getAuthorizationHeader());
  } catch (e) {
    log('Error fetching APIs', e);
    return;
  }
  if (!response.ok) {
    log('Error fetching APIs: %d %s', response.status, response.text);
    return;
  }
  system.apiDiscoveryActions.receiveApiList(response.body);
};

const receiveApiList: ActionCreator<ApiList> = apiList => ({
  type: RECEIVE_API_LIST,
  payload: apiList
});

const fetchApi = (id: string) => async (system: any) => {
  log('Fetch API %s', id);
  system.specActions.updateLoadingStatus('loading');

  let response;
  try {
    response = await superagent
      .get(`/apis/${id}/definition`)
      .set('Authorization', getAuthorizationHeader());
  } catch (e) {
    log('Error fetching API', e);
    return system.specActions.updateLoadingStatus('failed');
  }
  if (!response.ok) {
    log('Error fetching API: %d %s', response.status, response.text);
    return system.specActions.updateLoadingStatus('failed');
  }

  system.specActions.updateLoadingStatus('success');
  system.specActions.updateSpec(response.body.definition);
  system.specActions.updateUrl(id);
};

export default { fetchApis, receiveApiList, fetchApi };
