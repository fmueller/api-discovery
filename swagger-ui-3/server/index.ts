import Koa = require('koa')
import koaStatic = require('koa-static')
import { Server } from 'http'
import routes from './routes'
import * as conf from './conf'

export function init(): Koa {
  const app = new Koa()

  if (conf.isProduction()) {
    app.use(koaStatic(conf.staticDir(), { gzip: true }))
    console.log('Serving static files from', conf.staticDir())
  } else if (conf.enableWebpackDev()) {
    app.use(require('./webpack-dev')({ publicPath: '/', index: '/index.html' }))
    console.log('Using webpack-dev-middleware')
  }
  app.use(routes())
  return app
}

export function start(port: number): Server {
  const app = init()
  const server = app.listen(port)
  console.info('Listening at http://localhost:%d (%s)', conf.port(), app.env)
  return server
}

if (require.main === module) {
  start(conf.port())
}
