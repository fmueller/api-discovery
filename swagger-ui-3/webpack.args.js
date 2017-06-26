/**
 * Arguments for the webpack build.
 * Build environments must set the NODE_ENV environment variable.
 */

exports.fileNames = () => {
  switch (process.env.NODE_ENV) {
    case 'production':
      return {
        mainEntryJs: 'main.[chunkHash].js',
        stylesCss: 'styles.[chunkHash].css'
      };
    default:
      return {
        mainEntryJs: 'main.js',
        stylesCss: 'styles.css'
      };
  }
};

exports.externals = () => {
  switch (process.env.NODE_ENV) {
    case 'production':
      return (
        /** @type {any} */ _,
        /** @type {string} */ request,
        /** @type {function((Error | null), string)} */ callback
      ) => {
        if (/^core-js\/library\/fn\/.+/.test(request)) {
          let match = /^core-js\/library\/fn\/json\/([a-zA-Z]+)$/.exec(request);
          if (match) {
            return callback(null, `core.JSON.${match[1]}`);
          }
          match = /^core-js\/library\/fn\/object\/([a-zA-Z]+)$/.exec(request);
          if (match) {
            return callback(null, `core.Object.${match[1]}`);
          }
          match = /^core-js\/library\/fn\/array\/([a-zA-Z]+)$/.exec(request);
          if (match) {
            return callback(null, `core.Array.${match[1]}`);
          }
        }
        if (/^lodash\/.+$/.test(request)) {
          const match = /^lodash\/(.+)$/.exec(request);
          if (match) {
            return callback(null, '_.' + match[1]);
          }
        }
        return callback(
          null,
          {
            react: 'React',
            'react-dom': 'ReactDOM',
            babel: 'Babel',
            'babel-core': 'babel-core',
            'babel-runtime': 'babel-runtime',
            'babel-polyfill': 'Babel',
            'core-js/library/fn/object/define-property': 'core.Object.defineProperty',
            'core-js/library/fn/weak-map': 'core.WeakMap',
            'core-js/library/fn/promise': 'core.Promise',
            'core-js/library/fn/symbol': 'core.Symbol',
            'core-js': 'core',
            lodash: '_',
            'js-yaml': 'jsyaml',
            remarkable: 'Remarkable',
            immutable: 'Immutable',
            tv4: 'tv4',
            'yaml-js': 'yaml'
          }[request]
        );
      };
    default:
      return {};
  }
};

exports.scripts = () => {
  switch (process.env.NODE_ENV) {
    case 'production':
      return [
        {
          src: 'https://cdnjs.cloudflare.com/ajax/libs/react/15.6.1/react.min.js',
          integrity: 'sha256-ivdPAn5h6U67z6OPgwfiLM9ug6levxmYFqWNxNCV0YE=',
          crossorigin: 'anonymous'
        },
        {
          src: 'https://cdnjs.cloudflare.com/ajax/libs/react/15.6.1/react-dom.min.js',
          integrity: 'sha256-UEqn5+tyzezD6A5HBMNTlc5mXkmt+ohTfCBPtXMaGb0=',
          crossorigin: 'anonymous'
        },
        {
          src: 'https://cdnjs.cloudflare.com/ajax/libs/babel-standalone/6.25.0/babel.min.js',
          integrity: 'sha256-KztsNop9TrlPYQWLLoS/eCnCfUTrO7Mum+mOCHGR5zA=',
          crossorigin: 'anonymous'
        },
        {
          src: 'https://cdnjs.cloudflare.com/ajax/libs/babel-polyfill/6.23.0/polyfill.min.js',
          integrity: 'sha256-2nu8qdFj9AM9XRd75EAp3XRGEKiRprWL4hPMhYkj150=',
          crossorigin: 'anonymous'
        },
        {
          src: 'https://cdnjs.cloudflare.com/ajax/libs/lodash.js/4.17.4/lodash.min.js',
          integrity: 'sha256-8E6QUcFg1KTnpEU8TFGhpTGHw5fJqB9vCms3OhAYLqw=',
          crossorigin: 'anonymous'
        },
        {
          src: 'https://cdnjs.cloudflare.com/ajax/libs/js-yaml/3.8.4/js-yaml.min.js',
          integrity: 'sha256-0fVOf/R0H784sL7gcJSJo2mOapjzsIVdW0Ep3CuOKrg=',
          crossorigin: 'anonymous'
        },
        {
          src: 'https://cdnjs.cloudflare.com/ajax/libs/remarkable/1.7.1/remarkable.min.js',
          integrity: 'sha256-ltAts6+/XysEs9E5RF/t0H+0eD3ET6NpbqzshWkqeic=',
          crossorigin: 'anonymous'
        },
        {
          src: 'https://cdnjs.cloudflare.com/ajax/libs/immutable/3.8.1/immutable.min.js',
          integrity: 'sha256-13JFytp+tj8jsxr6GQOVLCgcYfMUo2Paw4jVrnXLUPE=',
          crossorigin: 'anonymous'
        },
        {
          src: 'https://cdnjs.cloudflare.com/ajax/libs/tv4/1.3.0/tv4.min.js',
          integrity: 'sha256-6VMg4vOn7Y0wfDcw6rnhBy6JqV4ZvEi8QSyN2R8wdBE=',
          crossorigin: 'anonymous'
        },
        {
          src: 'https://unpkg.com/yaml-js@0.1.5/yaml.min.js'
        }
      ];
    default:
      return [];
  }
};

exports.definitions = () => {
  switch (process.env.NODE_ENV) {
    case 'production':
      return {
        'process.env': {
          NODE_ENV: '"production"'
        }
      };
    default:
      return {
        'process.env': {
          NODE_ENV: '"development"'
        }
      };
  }
};
