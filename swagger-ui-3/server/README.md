# API Discovery UI Server

A web service that delegates API requests of the frontend to other backends and handles authentication. It also serves the static files of the client.

### Configuration

The server can be configured with following environment variables:

* `NODE_ENV`: set to `production` to run the server in production mode
* `API_DISCOVERY_ENABLE_WEBPACK_DEV`: set this value to enable the webpack-dev middleware for development
* `API_DISCOVERY_PORT`: port to listen on
* `API_DISCOVERY_STATIC_DIR`: directory which contains the static client files
