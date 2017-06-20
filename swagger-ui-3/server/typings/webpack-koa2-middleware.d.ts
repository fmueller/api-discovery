// Type definitions for webpack-koa2-middleware 1.x
// Project: https://github.com/iyuq/webpack-koa2-middleware
// Definitions by: mfellner <https://github.com/mfellner>

declare module 'webpack-koa2-middleware' {
  import { Middleware } from 'koa';
  import { Compiler, Watching } from 'webpack';

  namespace webpackKoa2Middleware {
    export interface Options {
      publicPath: string;
      noInfo?: boolean;
      quiet?: boolean;
      lazy?: boolean;
      watchOptions?: object;
      index?: string;
      headers?: { [name: string]: string };
      stats?: object;
      reporter?: any;
      serverSideRender?: boolean;
    }
  }

  function webpackKoa2Middleware(
    webpack: Compiler | Watching,
    options: webpackKoa2Middleware.Options
  ): Middleware;

  export = webpackKoa2Middleware;
}
