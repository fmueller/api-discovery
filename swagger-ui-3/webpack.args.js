/**
 * Arguments for webpack build.
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
          src: 'https://cdnjs.cloudflare.com/ajax/libs/swagger-ui/3.0.14/swagger-ui-bundle.js',
          integrity: 'sha256-xU14TvNaRLU+PcUYoi49k21OsdfDzSwSiDoI4h6NDsQ=',
          crossorigin: 'anonymous'
        },
        {
          src: 'https://cdnjs.cloudflare.com/ajax/libs/react/15.6.0/react.min.js',
          integrity: 'sha256-mMnqBTPWAOYxp2vj8RYEqx34vhMT4PGvwxm5Ndl5yt8=',
          crossorigin: 'anonymous'
        },
        {
          src: 'https://cdnjs.cloudflare.com/ajax/libs/react/15.6.0/react-dom.min.js',
          integrity: 'sha256-eG+W/mO3HZuwuYJjKT9aYbZC/MVN1mmpbjyQhGBihhE=',
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
