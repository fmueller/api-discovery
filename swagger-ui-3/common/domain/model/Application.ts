import ApiDeploymentLink from './ApiDeploymentLink';

export interface Application {
  name: string;
  app_url: string;
  definitions: ApiDeploymentLink[];
  created: string;
}

export default Application;
