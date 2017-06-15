import { createSelector } from 'reselect';

export default {
  apiList: createSelector(
    _ => _,
    (state: any) => (state.has('apiList') ? state.get('apiList').apis : [])
  )
};
