# API Discovery UI

A frontend for the API portal based on [swagger-ui 3](https://github.com/swagger-api/swagger-ui).

### Development

**Run in development mode with automatic reloading:**

```sh
npm run dev
```

Set the base URL of the api-storage service and provide a valid OAuth token by exporting these environment variables:

```sh
export API_PORTAL_OAUTH2_ACCESS_TOKENS="apistorage.com=some-token"
export API_PORTAL_API_STORAGE_CONF__BASE_URL="https://apistorage.com"
```

**Run in production mode:**

```sh
npm run dist
npm start
```

### Configuration

All configuration is managed by [TypeConf](https://github.com/mfellner/typeconf) and can be provided through configuration files, environment variables and command line arguments. Here is a list of the supported options:

* port (number) – ⚙️ which local port to start the server on (defaults to 3001)
* logLevel (string) – ⚙️ level of detail for log output(uses [winston logging levels](https://github.com/winstonjs/winston#logging-levels))
* staticDir (string) – ⚙️ directory to serve static files from (defaults to dist/client/)
* serveStatic (boolean) – ⚙️ serve static files from the static directory
* enableWebpackDev (boolean) – 🛠️ render static content with webpack
* checkClientAuthorisation (boolean) – 🔒 validate client credentials (on the browser client side)
* apiStorageConf (object) – 📡 configuration for the remote api-storage service
    * apiStorageConf (basic)
        * scheme (string) – 🔒 `basic` (required)
        * baseUrl (string) – ⚙️ URL of the api-storage service (required)
        * forwardClientAuthorization (boolean) – 🔒 use `Authorization` header of the browser client
        * user (string) – 🔒 username for api-storage (required)
        * pass (string) – 🔒 password for api-storage (required)
    * apiStorageConf (oauth2)
        * scheme (string) – 🔒 `oauth2` (required)
        * baseUrl (string) – ⚙️ URL of the api-storage service (required)
        * forwardClientAuthorization (boolean) – 🔒 use `Authorization` header of the browser client
        * scopes (array) – 🔒 OAuth2 scopes for api-storage
        * accessTokenUri (string) – 🔒 OAuth2 access token endpoint (requires [DynamicOAuth2Conf](server/domain/model/DynamicOAuth2Conf.ts))
        * accessTokens (string) – 🔒 static OAuth2 access tokens (requires [StaticOAuth2Conf](server/domain/model/StaticOAuth2Conf.ts))
* clientAuthConf (object) – 🔒 configuration for browser client authentication
    * clientAuthConf (basic)
        * scheme (string) – 🔒 `basic` (required)
        * username (string) – 🔒 username for api-storage (required)
        * password (string) – 🔒 password for api-storage (required)
    * clientAuthConf (oauth2)
        * scheme (string) – 🔒 `oauth2` (required)
        * authorizationUri – 🔒 OAuth2 implicit flow authorization URI
        * clientId (string) – 🔒 OAuth2 implicit flow client ID
        * redirectUri (string) – 🔒 OAuth2 implicit flow redirect URI
        * requestParameters (object) – 🔒 additional request parameters

All configuration values can be set (or overridden) with environment variables. Variable names must start with `API_PORTAL_` followed by the configuration value name in CONSTANT_CASE. Nested object properties can be defined using double underscores (`__`), for example, `API_PORTAL_API_STORAGE_CONF__BASE_URL`.

For examples take a look at the [conf](conf) directory or dive into [conf.ts](server/framework/conf.ts) to see the implementation.
