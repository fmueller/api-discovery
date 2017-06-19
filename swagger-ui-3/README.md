# API Discovery UI

Based on [swagger-ui 3](https://github.com/swagger-api/swagger-ui).

### Configuration

In your browser, set an OAUTH token for the local storage value `API_DISCOVERY_TOKEN`.

Then you can load new API definitions by typing in a valid Api Discovery Storage service app-endpoint URL and pressing 'Discover'. E.g. `https://apidisco.zalando.com/apps/awesome-app`.

### Development

**Run in development mode with automatic reloading:**

```sh
npm run dev
```

Configure authentication by setting a valid OAuth token:

```sh
export API_DISCOVERY_OAUTH2_ACCESS_TOKENS=api-storage-domain=some-token
```

`api-storage-domain` must be the hostname of the API storage backend used in the `apiStorageConf` application configuration (see the [/conf](/conf) directory).

**Run in production mode:**

```sh
npm run dist
npm start
```

### Configuration

All configuration is external to the application and can be set with configuration files, environment variables and command line arguments. Check the [/conf](/conf) directory for examples and take a look at [conf.ts](/server/framework/conf.ts) to see the implementation.
