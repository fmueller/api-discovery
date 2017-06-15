import fs = require('fs');
import path = require('path');
import Router = require('koa-router');
import conf from './framework/conf';
import { log } from './framework/logger';
import webpackArgs = require('../webpack.args');
import ApiService from './domain/ApiService';
import HealthService from './domain/HealthService';
import AuthConfFactory from './domain/model/AuthConfFactory';
import AuthContextFactory from './domain/model/AuthContextFactory';
import StaticService from './domain/StaticService';

import ClientAuthConfFactory from '../common/domain/model/ClientAuthConfFactory';
import validate from '../common/domain/validate';

function getStaticOptions() {
  let files: { js: string[]; css: string[] } = { js: [], css: [] };

  if (conf.getBoolean('enableWebpackDev')) {
    const names = webpackArgs.fileNames();
    const fileNames = Object.keys(names).map(n => names[n] as string);
    const js = fileNames.filter(f => /^.*\.js$/.test(f));
    const css = fileNames.filter(f => /^.*\.css$/.test(f));
    files = { js, css };
  } else if (conf.getBoolean('serveStatic')) {
    const staticDir = conf.getString('staticDir')!;
    const clientFiles = fs.readdirSync(staticDir);
    const js = clientFiles.filter(f => /^.*\.js$/.test(f)).map(f => path.basename(f));
    const css = clientFiles.filter(f => /^.*\.css$/.test(f)).map(f => path.basename(f));
    files = { js, css };
  }

  const scripts = webpackArgs.scripts();
  const templateFile = path.resolve(__dirname, '../static/index.ejs');
  const faviconFile = path.resolve(__dirname, '../static/favicon.png');
  const authConfFactory = new ClientAuthConfFactory(validate);
  const clientAuthConf = conf.get('clientAuthConf', authConfFactory.bindCreate())!;
  const configuration = { authConf: clientAuthConf };

  const options = { files, scripts, templateFile, faviconFile, configuration };
  log.debug('Using static options %j', options);
  return options;
}

const createRoutes = (router: Router) => {
  const accessTokens = conf.getString('oauth2AccessTokens');
  const authConfFactory = new AuthConfFactory({ accessTokens });
  const authContextFactory = new AuthContextFactory();

  const staticOptions = getStaticOptions();
  const staticService = new StaticService(staticOptions);
  const healthService = new HealthService();

  const apiStorageConf = conf.get('apiStorageConf', authConfFactory.bindCreate());
  const apiStorageContext = authContextFactory.create(apiStorageConf);
  const apiService = new ApiService(apiStorageContext);

  router.get('/', staticService.getStaticHandler());
  router.get('/favicon.png', staticService.getFaviconHandler());
  router.get('/health', healthService.getHealthHandler());
  router.get('/apis', apiService.getApisReadHandler());
  router.get('/apis/:id', apiService.getApiReadHandler());
  router.get('/apis/:id/definition', apiService.getDefinitionReadHandler());

  return router;
};

export default () => createRoutes(new Router());
