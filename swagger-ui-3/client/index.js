import SwaggerUI from 'swagger-ui';
import ApiDiscoveryPreset from './preset';
import 'swagger-ui/dist/swagger-ui.css';
import './index.css';

const ui = SwaggerUI({
  url: 'http://petstore.swagger.io/v2/swagger.json',
  dom_id: '#swagger-ui',
  presets: [SwaggerUI.presets.apis, ApiDiscoveryPreset],
  plugins: [SwaggerUI.plugins.DownloadUrl],
  layout: 'ApiDiscovery',
  validatorUrl: null
});
