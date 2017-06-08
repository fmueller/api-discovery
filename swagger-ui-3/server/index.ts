/// <reference path="./typings/index.d.ts" />

import Koa = require('koa');
import koaStatic = require('koa-static');
import { Server } from 'http';
import conf from './framework/conf';
import { log, logger } from './framework/logger';
import createRouter from './resource';

export function init(): Koa {
  const app = new Koa();
  app.use(logger());

  if (conf.get('enableWebpackDev')) {
    app.use(require('./framework/webpack-dev')({ publicPath: '/', index: '/index.html' }));
    log.info('Using webpack-dev-middleware');
  } else if (conf.getBoolean('serveStatic')) {
    const staticDir = conf.getString('staticDir') as string;
    app.use(koaStatic(staticDir, { gzip: true }));
    log.info('Serving static files from', staticDir);
  } else {
    log.warn('Not serving any static files.');
  }

  const router = createRouter();
  app.use(router.routes());
  app.use(router.allowedMethods());
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
