import webpack from 'webpack';
import webpackDev from 'webpack-koa2-middleware';
import webpackConfig from '../webpack.config';

module.exports = options => webpackDev(webpack(webpackConfig), options);
