/// <reference path="./typings/index.d.ts" />

import 'source-map-support/register';
import Koa = require('koa');
import koaStatic = require('koa-static');
import koaMount = require('koa-mount');
import { Server } from 'http';
import conf from './framework/conf';
import createErrorHandler from './framework/error-handler';
import { log, logger } from './framework/logger';
import createWebpackDev from './framework/webpack-dev';
import createRouter from './resource';

export function init(): Koa {
  const app = new Koa();
  app.use(logger());

  if (conf.getBoolean('enableWebpackDev')) {
    app.use(createWebpackDev({ publicPath: '/static' }));
    log.info('Using webpack-dev-middleware');
  } else if (conf.getBoolean('serveStatic')) {
    const staticDir = conf.getString('staticDir')!;
    app.use(koaMount('/static', koaStatic(staticDir, { gzip: true })));
    log.info('Serving static files from', staticDir);
  } else {
    log.warn('Not serving any static files.');
  }

  const router = createRouter();
  app.use(createErrorHandler());
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
