import SwaggerUI from 'swagger-ui';
import SwaggerUIStandalonePreset from 'swagger-ui-dist/swagger-ui-standalone-preset';
import 'swagger-ui-dist/swagger-ui.css';
import './index.css';

const ui = SwaggerUI({
  url: 'http://petstore.swagger.io/v2/swagger.json',
  dom_id: '#swagger-ui',
  presets: [SwaggerUI.presets.apis, SwaggerUIStandalonePreset],
  plugins: [SwaggerUI.plugins.DownloadUrl],
  layout: 'StandaloneLayout'
});
