import actions from './actions';
import ApiDiscovery from './ApiDiscovery';
import DefinitionInfo from './DefinitionInfo';
import DefinitionView from './DefinitionView';
import reducers from './reducers';
import selectors from './selectors';
import Topbar from './Topbar';

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
          reducers,
          selectors
        }
      },
      components: { ApiDiscovery, DefinitionInfo, DefinitionView }
    };
  }
];
