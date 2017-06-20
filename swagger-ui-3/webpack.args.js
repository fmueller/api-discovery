/**
 * Arguments for the webpack build.
 * Build environments must set the NODE_ENV environment variable.
 */

exports.fileNames = () => {
  switch (process.env.NODE_ENV) {
    case 'production':
      return {
        mainEntryJs: '[name].[chunkHash].js',
        stylesCss: '[name].[chunkHash].css'
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
      return {
        'swagger-ui': 'SwaggerUIBundle',
        react: 'React',
        'react-dom': 'ReactDOM'
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
          src: 'https://cdnjs.cloudflare.com/ajax/libs/swagger-ui/3.0.16/swagger-ui-bundle.js',
          integrity: 'sha256-lZyFcMoDTftDTAMgebAnAYnVIvrwYEoJwT8PmpoF5qc=',
          crossorigin: 'anonymous'
        },
        {
          src: 'https://cdnjs.cloudflare.com/ajax/libs/react/15.6.1/react.min.js',
          integrity: 'sha256-ivdPAn5h6U67z6OPgwfiLM9ug6levxmYFqWNxNCV0YE=',
          crossorigin: 'anonymous'
        },
        {
          src: 'https://cdnjs.cloudflare.com/ajax/libs/react/15.6.1/react-dom.min.js',
          integrity: 'sha256-UEqn5+tyzezD6A5HBMNTlc5mXkmt+ohTfCBPtXMaGb0=',
          crossorigin: 'anonymous'
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
