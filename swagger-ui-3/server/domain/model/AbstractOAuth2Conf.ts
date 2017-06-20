import { JsonSchema } from 'tv4';
import AuthConf from './AuthConf';

/**
 * Abstract configuration for OAuth2 based authentication.
 */
export default abstract class AbstractOAuth2Conf extends AuthConf {
  public readonly scheme: 'oauth2';
  public readonly scopes: string[];

  protected static readonly schema: JsonSchema = {
    type: 'object',
    allOf: [AuthConf.schema],
    properties: {
      scheme: {
        type: 'string',
        enum: ['oauth2']
      },
      baseUrl: {
        type: 'string',
        format: 'uri'
      },
      forwardClientAuthorization: {
        type: 'boolean',
        default: false
      },
      scopes: {
        type: 'array',
        items: {
          type: 'string'
        }
      }
    },
    required: ['scheme', 'baseUrl', 'scopes']
  };

  constructor(data: any, schema: JsonSchema) {
    super(data, schema);
  }
}
