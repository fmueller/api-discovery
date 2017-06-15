import { JsonSchema } from 'tv4';
import AbstractOAuth2Conf from './AbstractOAuth2Conf';

/**
 * Configuration for dynamic, OAuth2 based client authentication.
 */
export default class DynamicOAuth2Conf extends AbstractOAuth2Conf {
  public readonly accessTokenUri: string;
  public readonly tokenInfoUri?: string;
  public readonly realm?: string;
  public readonly credentialsDir?: string;

  protected static readonly schema: JsonSchema = {
    type: 'object',
    allOf: [AbstractOAuth2Conf.schema],
    properties: {
      accessTokenUri: {
        type: 'string',
        format: 'uri'
      },
      tokenInfoUri: {
        type: 'string',
        format: 'uri'
      },
      realm: {
        type: 'string'
      },
      credentialsDir: {
        type: 'string'
      }
    },
    required: ['accessTokenUri']
  };

  constructor(data: any) {
    super(data, DynamicOAuth2Conf.schema);
  }
}
