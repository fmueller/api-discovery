import koa from 'koa';
import koaRouter from 'koa-router';
import koaStatic from 'koa-static';
import * as conf from './conf';

const app = new koa();

if (conf.isProduction()) {
  app.use(koaStatic(conf.staticDir(), { gzip: true }));
  console.log('Serving static files from', conf.staticDir());
} else {
  console.log('Using webpack-dev-middleware');
  app.use(require('./webpack-dev')({ publicPath: '/', index: '/index.html', lazy: true }));
}

app.listen(conf.port());
console.log('Server is listening at http://localhost:%d', conf.port());
