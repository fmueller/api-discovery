export type ApiLifecycleState = 'ACTIVE' | 'INACTIVE' | 'DECOMMISSIONED';

export interface ApiMetaData {
  id: string;
  lifecycle_state: ApiLifecycleState;
}

export default ApiMetaData;
