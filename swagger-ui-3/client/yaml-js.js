/**
 * Shim for https://github.com/connec/yaml-js.
 *
 * By default the yaml-js package loads NodeJS modules and thus
 * cannot be used in the browser. This shim exports only the
 * necessary API for swagger-ui.
 */

import loader from 'yaml-js/lib/loader';
import compose from 'yaml-js/lib/composer';

const YAML = {
  compose: function(stream, Loader = loader.Loader) {
    const _loader = new Loader(stream);
    return _loader.get_single_node();
  }
};

export default YAML;
