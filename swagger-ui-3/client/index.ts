/// <reference path="./typings/index.d.ts" />

import swaggerUI = require('swagger-ui');
import ApiPortalPreset from './preset';

import 'react-select/dist/react-select.css';
import 'swagger-ui/dist/swagger-ui.css';
import './index.css';

swaggerUI({
  dom_id: '#swagger-ui',
  presets: [swaggerUI.presets.apis, ApiPortalPreset],
  plugins: [swaggerUI.plugins.DownloadUrl],
  layout: 'ApiPortal',
  validatorUrl: null
});
