/// <reference path="./typings/index.d.ts" />

import swaggerUI = require('swagger-ui');
import ApiDiscoveryPreset from './preset';

import 'react-select/dist/react-select.css';
import 'swagger-ui/dist/swagger-ui.css';
import './index.css';

swaggerUI({
  // url: 'http://petstore.swagger.io/v2/swagger.json',
  dom_id: '#swagger-ui',
  presets: [swaggerUI.presets.apis, ApiDiscoveryPreset],
  plugins: [swaggerUI.plugins.DownloadUrl],
  layout: 'ApiDiscovery',
  validatorUrl: null
});
