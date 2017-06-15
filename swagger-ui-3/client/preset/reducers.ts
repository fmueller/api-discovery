import { RECEIVE_API_LIST, ReceiveApiListAction } from './actions';

export default {
  [RECEIVE_API_LIST]: (state: any, action: ReceiveApiListAction) => {
    return state.set('apiList', action.payload);
  }
};
