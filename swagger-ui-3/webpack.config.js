const path = require('path');
const webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const ExtractTextPlugin = require('extract-text-webpack-plugin');
const args = require('./webpack.args');

module.exports = {
  target: 'web',
  entry: {
    index: './client/index.js',
    vendor: ['swagger-ui']
  },
  output: {
    filename: '[name].js',
    path: path.join(__dirname, 'dist', 'client'),
    libraryTarget: 'umd2'
  },
  module: {
    rules: [
      {
        test: /\.jsx?$/,
        exclude: /node_modules/,
        loader: 'babel-loader',
        options: {
          presets: ['es2015', 'react', 'stage-2'],
          babelrc: false
        }
      },
      {
        test: /\.css$/,
        use: ExtractTextPlugin.extract({
          fallback: 'style-loader',
          use: 'css-loader'
        })
      },
      {
        test: /\.(png|jpg)$/,
        use: 'file-loader'
      }
    ]
  },
  externals: args.externals(),
  resolve: {
    alias: {
      'yaml-js$': path.resolve(__dirname, './client/yaml-js.js')
    }
  },
  plugins: [
    new ExtractTextPlugin('index.css'),
    new webpack.optimize.CommonsChunkPlugin({
      name: 'vendor'
    }),
    new HtmlWebpackPlugin({
      filename: 'index.html',
      template: './client/index.ejs',
      inject: false,
      scripts: args.scripts()
    })
  ],
  devtool: 'cheap-module-source-map'
};
