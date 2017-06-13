import superagent = require('superagent');
import log from '../framework/debug';
import oAuth2Client from '../framework/OAuth2Client';

export const FETCH_API = 'API_DISCOVERY_FETCH_API';
export const FETCH_TOKEN = 'API_DISCOVERY_FETCH_TOKEN';

const fetchApi = (id: string) => async (system: any) => {
  system.specActions.updateLoadingStatus('loading');
  log('Fetch API %s', id);

  const token = localStorage.getItem('API_DISCOVERY_TOKEN');
  let response;

  try {
    response = await superagent
      .get(`/apis/${id}/definition`)
      .set('Authorization', `Bearer ${token}`);
  } catch (e) {
    log('Error fetching API', e);
    return system.specActions.updateLoadingStatus('failed');
  }

  if (!response.ok) {
    log('Error fetching API: %s %s', response.status, response.text);
    return system.specActions.updateLoadingStatus('failed');
  }

  system.specActions.updateLoadingStatus('success');
  system.specActions.updateSpec(response.body.definition);
  system.specActions.updateUrl(id);
};

const fetchToken = () => async (_: any) => {
  try {
    log('Fetch token.');
    const token = oAuth2Client.getToken();
    log('Fetched token: %s', token);
  } catch (e) {
    log('Fetching token failed.', e);
  }
};

export default { fetchApi, fetchToken };
