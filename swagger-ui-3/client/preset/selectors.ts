import { createSelector } from 'reselect';
import ApiList from '../../common/domain/model/ApiList';
import ApiVersionList from '../../common/domain/model/ApiVersionList';
import { State } from './reducers';

export default {
  apis: createSelector(
    _ => _,
    (state: State) => {
      if (state.has('apiList')) {
        const apiList = state.get('apiList') as ApiList;
        return apiList.apis;
      } else {
        return [];
      }
    }
  ),
  apiVersions: createSelector(
    _ => _,
    (state: State) => {
      if (state.has('apiVersionList')) {
        const apiVersionList = state.get('apiVersionList') as ApiVersionList;
        return apiVersionList.versions;
      } else {
        return [];
      }
    }
  ),
  selectedApiVersion: createSelector(_ => _, (state: State) => state.get('selectedApiVersion'))
};
