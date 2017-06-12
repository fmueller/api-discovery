import request = require('request-promise-native');
import { FullResponse, RequestPromiseOptions } from 'request-promise-native';
import { AuthorizationSupplier } from './AuthorizationSupplier';

export type RequestOptions = {
  baseUrl?: string;
  authSupplier?: AuthorizationSupplier;
};

export default class Request {
  public readonly baseUrl: string;
  private readonly authSupplier?: AuthorizationSupplier;

  constructor(options: RequestOptions = {}) {
    this.baseUrl = (options.baseUrl || '').replace(/\/$/, '');
    this.authSupplier = options.authSupplier;
  }

  /**
   * Make an HTTP request. Uses the [request](https://github.com/request/request) library.
   * @param options Request options.
   * @param authSupplier Optional authorization supplier.
   * @return Full HTTP response.
   */
  private async makeReqest(
    method: 'get' | 'head' | 'post' | 'put' | 'delete' | 'options',
    path: string,
    options: RequestPromiseOptions = {}
  ): Promise<FullResponse> {
    const url = `${this.baseUrl}/${path.replace(/^\//, '')}`;
    const optionsWithUrl = Object.assign({}, options, { method, url });

    if (this.authSupplier) {
      optionsWithUrl.auth = await this.authSupplier();
    }
    // Force a full response that includes headers etc.
    optionsWithUrl.resolveWithFullResponse = true;
    return request(optionsWithUrl);
  }

  public get(path: string, options?: RequestPromiseOptions): Promise<FullResponse> {
    return this.makeReqest('get', path, options);
  }

  public head(path: string, options?: RequestPromiseOptions): Promise<FullResponse> {
    return this.makeReqest('head', path, options);
  }

  public post(path: string, options?: RequestPromiseOptions): Promise<FullResponse> {
    return this.makeReqest('post', path, options);
  }

  public put(path: string, options?: RequestPromiseOptions): Promise<FullResponse> {
    return this.makeReqest('put', path, options);
  }

  public delete(path: string, options?: RequestPromiseOptions): Promise<FullResponse> {
    return this.makeReqest('delete', path, options);
  }

  public options(path: string, options?: RequestPromiseOptions): Promise<FullResponse> {
    return this.makeReqest('options', path, options);
  }
}
