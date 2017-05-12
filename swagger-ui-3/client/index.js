import swaggerUI from 'swagger-ui';
import ApiDiscoveryPreset from './preset';
import 'swagger-ui/dist/swagger-ui.css';
import './index.css';

swaggerUI({
  url: 'http://petstore.swagger.io/v2/swagger.json',
  dom_id: '#swagger-ui',
  presets: [swaggerUI.presets.apis, ApiDiscoveryPreset],
  plugins: [swaggerUI.plugins.DownloadUrl],
  layout: 'ApiDiscovery',
  validatorUrl: null
});
