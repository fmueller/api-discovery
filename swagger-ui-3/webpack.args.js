/**
 * Arguments for webpack build.
 */

exports.externals = () => {
  switch (process.env.NODE_ENV) {
    case 'production':
      return {
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
          src: 'https://cdnjs.cloudflare.com/ajax/libs/react/15.5.4/react.min.js',
          integrity: 'sha256-lLTXVU5NHLl101VgD3LswV6ZgI2PjSjZ5dVzhBcq52k=',
          crossorigin: 'anonymous'
        },
        {
          src: 'https://cdnjs.cloudflare.com/ajax/libs/react/15.5.4/react-dom.min.js',
          integrity: 'sha256-4DRNdBX+quo7fRIFuR9yhr157hq/9FcAsHRDNQEXZSM=',
          crossorigin: 'anonymous'
        }
      ];
    default:
      return [];
  }
};
