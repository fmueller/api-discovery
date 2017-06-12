import { Middleware } from 'koa';
import { OptionsWithUrl } from 'request-promise-native';
import errors = require('request-promise-native/errors');
import NotFoundError from '../gateway/NotFoundError';
import { log } from './logger';

abstract class Problem {
  public readonly title: string;
  public readonly status: number;
  public readonly detail?: string;

  constructor(options: { title: string; status: number; detail?: string }) {
    this.title = options.title;
    this.status = options.status;
    this.detail = options.detail;
  }
}

class RemoteAccessProblem extends Problem {
  public readonly url: string;

  constructor(options: { title: string; status: number; detail?: string; url: string }) {
    super(options);
    this.url = options.url;
  }
}

class NotFoundProblem extends Problem {
  public readonly url: string;

  constructor(options: { title: string; status: number; detail?: string; url: string }) {
    super(options);
    this.url = options.url;
  }
}

export default function createErrorHandler(): Middleware {
  return async (ctx, next) => {
    try {
      await next();
    } catch (e) {
      if (e instanceof errors.StatusCodeError) {
        const url = (e.options as OptionsWithUrl).url.toString();
        ctx.status = e.statusCode;
        ctx.body = new RemoteAccessProblem({
          title: 'Remote Access Error',
          status: e.statusCode,
          detail: e.message,
          url
        });
        log.error(`${url} ${e.message}`);
      } else if (e instanceof NotFoundError) {
        ctx.status = 404;
        ctx.body = new NotFoundProblem({
          title: 'Not Found',
          status: 404,
          url: e.url
        });
        log.info(`Not found: ${e.url}`);
      } else {
        throw e;
      }
    }
  };
}
