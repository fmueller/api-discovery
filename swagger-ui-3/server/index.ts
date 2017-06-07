/// <reference path="./typings/index.d.ts" />

import Koa = require('koa');
import koaStatic = require('koa-static');
import { Server } from 'http';
import conf from './conf';
import { log, logger } from './logger';
import routes from './routes';

export function init(): Koa {
  const app = new Koa();
  app.use(logger());

  if (conf.getBoolean('isProduction')) {
    const staticDir = conf.getString('staticDir') as string;
    app.use(koaStatic(staticDir, { gzip: true }));
    log.info('Serving static files from', staticDir);
  } else if (conf.get('enableWebpackDev')) {
    app.use(require('./webpack-dev')({ publicPath: '/', index: '/index.html' }));
    log.info('Using webpack-dev-middleware');
  }
  app.use(routes());
  return app;
}

export function start(port: number): Server {
  const app = init();
  const server = app.listen(port);
  log.info('Listening at http://localhost:%d (%s)', port, app.env);
  return server;
}

if (require.main === module) {
  start(conf.getNumber('port', 3001));
}
