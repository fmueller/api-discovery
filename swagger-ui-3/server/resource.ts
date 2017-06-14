import fs = require('fs');
import path = require('path');
import Router = require('koa-router');
import ClientAuthConf from './domain/model/ClientAuthConf';
import conf from './framework/conf';
import { log } from './framework/logger';
import webpackArgs = require('../webpack.args');
import ApiService from './domain/ApiService';
import HealthService from './domain/HealthService';
import StaticService from './domain/StaticService';

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
  const configuration = conf.getType('clientAuthConf', ClientAuthConf)!;

  const options = { files, scripts, templateFile, faviconFile, configuration };
  log.debug('Using static options %j', options);
  return options;
}

const createRoutes = (router: Router) => {
  const staticOptions = getStaticOptions();
  const staticService = new StaticService(staticOptions);
  const healthService = new HealthService();
  const apiService = new ApiService();

  router.get('/', staticService.getStaticHandler());
  router.get('/favicon.png', staticService.getFaviconHandler());
  router.get('/health', healthService.getHealthHandler());
  router.get('/apis', apiService.getApisReadHandler());
  router.get('/apis/:id', apiService.getApiReadHandler());
  router.get('/apis/:id/definition', apiService.getDefinitionReadHandler());

  return router;
};

export default () => createRoutes(new Router());
