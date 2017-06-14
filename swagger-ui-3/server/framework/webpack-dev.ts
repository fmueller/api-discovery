import { Middleware } from 'koa';
import { Options } from 'webpack-koa2-middleware';

export default function(options: Options): Middleware {
  // Dynamically require modules to prevent static dependencies in production.
  const webpack = require('webpack');
  const webpackDev = require('webpack-koa2-middleware');
  const webpackConfig = require('../../webpack.config');
  return webpackDev(webpack(webpackConfig), options);
}
