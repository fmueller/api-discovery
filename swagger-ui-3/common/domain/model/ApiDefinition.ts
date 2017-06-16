import ApiDeploymentLink from './ApiDeploymentLink';

export interface ApiDefinition {
  type: string;
  definition: string;
  applications: ApiDeploymentLink[];
}

export default ApiDefinition;
