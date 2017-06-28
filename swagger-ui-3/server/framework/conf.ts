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
      level: process.env['API_PORTAL_LOG_LEVEL']
    })
  ]
});

const defaultConf = {
  staticDir: path.resolve(__dirname, '../../client'),
  serveStatic: process.env['NODE_ENV'] !== 'development'
};

const configFile = process.env['API_PORTAL_CONF'] || '';

if (configFile) {
  log.info('Using configuration file %s', configFile);
}

export default new TypeConf()
  .withStore(defaultConf)
  .withFile(configFile)
  .withEnv('API_PORTAL')
  .withArgv();
