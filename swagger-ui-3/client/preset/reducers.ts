import { Map } from 'immutable';
import ApiList from '../../common/domain/model/ApiList';
import ApiVersion from '../../common/domain/model/ApiVersion';
import ApiVersionList from '../../common/domain/model/ApiVersionList';
import {
  RECEIVE_API_VERSIONS,
  RECEIVE_APIS,
  SET_SELECTED_API,
  SET_SELECTED_API_VERSION
} from './actions';

export type State = Map<string, ApiList | string | ApiVersionList | ApiVersion>;

export default {
  [RECEIVE_APIS]: (state: State, action: { payload: ApiList }) => {
    return state.set('apiList', action.payload);
  },
  [SET_SELECTED_API]: (state: State, action: { payload: string }) => {
    return state.set('selectedApi', action.payload);
  },
  [RECEIVE_API_VERSIONS]: (state: State, action: { payload: ApiVersionList }) => {
    return state.set('apiVersionList', action.payload);
  },
  [SET_SELECTED_API_VERSION]: (state: State, action: { payload: ApiVersion }) => {
    return state.set('selectedApiVersion', action.payload);
  }
};
