import ApiMetaData from './ApiMetaData';
import ApiVersion from './ApiVersion';
import Application from './Application';

export interface Api {
  api_meta_data: ApiMetaData;
  versions: ApiVersion[];
  applications: Application[];
}

export default Api;
