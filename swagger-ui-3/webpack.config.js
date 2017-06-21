const path = require('path');
const webpack = require('webpack');
const ExtractTextPlugin = require('extract-text-webpack-plugin');
const args = require('./webpack.args');

module.exports = {
  target: 'web',
  entry: {
    index: './client/index.ts'
  },
  output: {
    filename: args.fileNames().mainEntryJs,
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
        test: /\.tsx?$/,
        loader: 'awesome-typescript-loader',
        options: {
          configFileName: path.resolve(__dirname, './client/tsconfig.json')
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
        loader: 'file-loader',
        options: {
          publicPath: '/static/'
        }
      }
    ]
  },
  externals: args.externals(),
  resolve: {
    extensions: ['.ts', '.tsx', '.js', '.json', '.png', '.jpg', '.css'],
    alias: {
      'yaml-js$': path.resolve(__dirname, './client/yaml-js.js')
    }
  },
  plugins: [
    new webpack.DefinePlugin(args.definitions()),
    new ExtractTextPlugin(args.fileNames().stylesCss)
  ],
  devtool: 'cheap-module-source-map'
};