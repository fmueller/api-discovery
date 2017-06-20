import fs = require('fs');
import ejs = require('ejs');
import koaSend = require('koa-send');
import { IMiddleware } from 'koa-router';
import { log } from '../framework/logger';

export type Options = {
  files: {
    js: string[];
    css: string[];
  };
  scripts: { [key: string]: any };
  configuration: object;
  templateFile: string;
  faviconFile: string;
};

/**
 * Renders the static HTML page.
 */
export default class StaticService {
  private readonly options: ejs.Options;
  private readonly data: ejs.Data;
  private readonly templateFunction: ejs.TemplateFunction;
  private readonly faviconFile: string;

  constructor(options: Options) {
    // https://github.com/mde/ejs#options
    this.options = { strict: true, rmWhitespace: true } as any;
    this.data = StaticService.getData(options);
    this.faviconFile = options.faviconFile;
    log.info('Compile template %s', options.templateFile);
    const buffer = fs.readFileSync(options.templateFile);
    this.templateFunction = ejs.compile(buffer.toString(), this.options);
  }

  private static getData(options: Options): ejs.Data {
    const configuration = new Buffer(JSON.stringify(options.configuration)).toString('base64');
    return { files: options.files, scripts: options.scripts, configuration };
  }

  private renderTemplate(): Promise<string> {
    log.debug('Render template with data %j', this.data);
    return this.templateFunction(this.data);
  }

  public getStaticHandler(): IMiddleware {
    return async ctx => {
      ctx.body = this.renderTemplate();
    };
  }

  public getFaviconHandler(): IMiddleware {
    return async ctx => {
      log.debug('send favicon %s', this.faviconFile);
      await koaSend(ctx, this.faviconFile, { root: '/', gzip: true });
    };
  }
}
