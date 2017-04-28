import koa from 'koa';
import koaRouter from 'koa-router';
import koaStatic from 'koa-static';
import * as conf from './conf';

const app = new koa();

app.use(koaStatic(conf.staticDir(), { gzip: true }));

app.listen(conf.port());
console.log('Server is listening at http://localhost:%d', conf.port());
console.log('Serving static files from', conf.staticDir());
