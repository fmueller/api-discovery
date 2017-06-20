import path = require('path');
import TypeConf from 'typeconf';
import { Logger, transports } from 'winston';

/**
 * Special logger for this module only.
 * Prevents a cyclic dependency with conf.ts.
 */
const log = new Logger({
  transports: [
    new transports.Console({
      level: process.env['API_DISCOVERY_LOG_LEVEL']
    })
  ]
});

const defaultConf = {
  staticDir: path.resolve(__dirname, '../../client'),
  serveStatic: process.env['NODE_ENV'] !== 'development'
};

const configFile = process.env['API_DISCOVERY_CONF'];

if (!configFile) {
  log.warn('No config file found. Consider setting API_DISCOVERY_CONF');
} else {
  log.info('Using configuration from %s', configFile);
}

export default new TypeConf()
  .withArgv()
  .withEnv('API_DISCOVERY')
  .withFile(configFile)
  .withStore(defaultConf);
