import { ApiLifecycleState } from './ApiMetaData';

export interface ApiDeploymentLink {
  api_url: string;
  api_ui: string;
  lifecycle_state: ApiLifecycleState;
  created: string;
  last_updated: string;
  href: string;
}

export default ApiDeploymentLink;
