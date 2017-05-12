import Router = require('koa-router')
import compose = require('koa-compose')
import health from './health'

const routes: ((r: Router) => Router)[] = [
  health
]

export default () => {
  const router = routes.reduce((r, fn) => fn(r), new Router())
  return compose([router.routes(), router.allowedMethods()])
}
