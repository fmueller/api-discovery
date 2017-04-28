# API Discovery UI

Based on [swagger-ui 3](https://github.com/swagger-api/swagger-ui).

### Configuration

Set an OAUTH token for the local storage value `API_DISCOVERY_TOKEN`.

Then you can load new API definitions by typing in a valid Api Discovery Storage service app-endpoint URL and pressing 'Discover'. E.g. `https://apidisco.zalando.com/apps/awesome-app`.

### Development

Run in development mode with automatic reloading:

```bash
npm start
```

Run in production mode:

```bash
npm run start-dist
```

Build a new distribution for production:

```bash
npm run dist
```
