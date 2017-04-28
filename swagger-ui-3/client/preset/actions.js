import fetch from 'isomorphic-fetch';

export const FETCH_API = 'API_DISCOVERY_FETCH_API';

const fetchApi = url => async system => {
  system.specActions.updateLoadingStatus('loading');

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
    return system.specActions.updateLoadingStatus('failed');
  }

  if (!response.ok) {
    return system.specActions.updateLoadingStatus('failed');
  }

  const json = await response.json();

  system.specActions.updateLoadingStatus('success');
  system.specActions.updateSpec(json.definition);
  system.specActions.updateUrl(url);
};

export default { fetchApi };
