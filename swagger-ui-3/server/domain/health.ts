import { Context } from 'koa';

export default async function(ctx: Context) {
  ctx.body = 'OK';
}
