const webpack = require('webpack');
const webpackDev = require('webpack-koa2-middleware');
const webpackConfig = require('../../webpack.config');

module.exports = options => webpackDev(webpack(webpackConfig), options);
