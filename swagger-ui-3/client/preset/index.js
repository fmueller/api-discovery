import ApiDiscovery from './ApiDiscovery';
import Topbar from './Topbar';
import actions from './actions';
import reducers from './reducers';

/**
 * A SwaggerUI preset is a list of plugins.
 * See https://github.com/swagger-api/swagger-ui/blob/master/src/plugins/add-plugin.md
 */
export default [
  Topbar,
  () => {
    return {
      statePlugins: {
        apiDiscovery: {
          actions,
          reducers
        }
      },
      components: { ApiDiscovery }
    };
  }
];
