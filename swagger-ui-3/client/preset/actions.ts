import superagent = require('superagent');
import { getAuthorizationHeader } from '../framework/auth';
import log from '../framework/debug';

export const FETCH_API = 'API_DISCOVERY_FETCH_API';
export const FETCH_APIS = 'API_DISCOVERY_FETCH_APIS';

const fetchApis = () => async (_: any) => {
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
  }
  log('Got APIs', response.body);
};

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

export default { fetchApis, fetchApi };
