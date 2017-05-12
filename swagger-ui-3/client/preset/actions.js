import fetch from 'isomorphic-fetch';
import log from '../debug';

export const FETCH_API = 'API_DISCOVERY_FETCH_API';

const fetchApi = url => async system => {
  system.specActions.updateLoadingStatus('loading');
  log('Fetch API %s', url);

  const token = localStorage.getItem('API_DISCOVERY_TOKEN');
  let response;

  try {
    response = await fetch(url, {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${token}`
      },
      mode: 'cors'
    });
  } catch (e) {
    log('Error fetching API', e);
    return system.specActions.updateLoadingStatus('failed');
  }

  if (!response.ok) {
    log('Error fetching API: %s %s', response.status, response.statusText);
    return system.specActions.updateLoadingStatus('failed');
  }

  const json = await response.json();

  system.specActions.updateLoadingStatus('success');
  system.specActions.updateSpec(json.definition);
  system.specActions.updateUrl(url);
};

export default { fetchApi };
