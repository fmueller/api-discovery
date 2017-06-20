import { JsonSchema } from 'tv4';
import AbstractOAuth2Conf from './AbstractOAuth2Conf';

/**
 * Configuration for static, OAuth2 based client authentication.
 */
export default class StaticOAuth2Conf extends AbstractOAuth2Conf {
  public readonly accessTokens: string;

  protected static readonly schema: JsonSchema = {
    type: 'object',
    allOf: [AbstractOAuth2Conf.schema],
    properties: {
      accessTokens: {
        type: 'string',
        pattern: '^\\S+=\\S+'
      }
    },
    required: ['accessTokens']
  };

  constructor(data: any) {
    super(data, StaticOAuth2Conf.schema);
  }
}
