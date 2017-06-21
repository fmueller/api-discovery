import { Middleware } from 'koa';
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

interface SuperagentError extends Error {
  readonly status: number;
  readonly response: {
    readonly text: string;
    readonly req: any;
    readonly body: any;
    readonly header: { [name: string]: string };
    readonly type: string;
    readonly status: number;
  };
}

function parseUrl(req: any): string {
  try {
    return req.connection._host + req.path;
  } catch (e) {
    return '';
  }
}

/**
 * Create an error handling middleware function that
 * catches exceptions and maps them to Problem responses.
 */
export default function createErrorHandler(): Middleware {
  return async (ctx, next) => {
    try {
      await next();
    } catch (e) {
      if (e instanceof NotFoundError) {
        ctx.status = 404;
        ctx.body = new NotFoundProblem({
          title: 'Not Found',
          status: 404,
          url: e.url
        });
        log.info(`Not found: ${e.url}`);
      } else if (e.status && e.response) {
        const error = e as SuperagentError;
        const req = error.response.req || {};
        const url = parseUrl(error.response.req);
        ctx.status = error.status;
        ctx.body = new RemoteAccessProblem({
          title: 'Remote Access Error',
          status: error.status,
          detail: error.response.text,
          url
        });
        log.error(`${req.method} ${url} ${error.response.text}`);
      } else if (e.code === 'ECONNREFUSED') {
        ctx.status = 502;
        ctx.body = new RemoteAccessProblem({
          title: 'Remote Access Error',
          status: 502,
          detail: e.message,
          url: e.address
        });
        log.error(e.message);
      } else {
        throw e;
      }
    }
  };
}
