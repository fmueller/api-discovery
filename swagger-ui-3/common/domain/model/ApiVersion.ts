import ApiDefinition from './ApiDefinition';
import { ApiLifecycleState } from './ApiMetaData';

export interface ApiVersion {
  api_version: string;
  lifecycle_state: ApiLifecycleState;
  definitions: ApiDefinition[];
}

export default ApiVersion;
