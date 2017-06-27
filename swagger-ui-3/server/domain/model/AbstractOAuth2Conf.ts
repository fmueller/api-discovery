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
      scopes: {
        type: 'array',
        items: {
          type: 'string'
        }
      }
    },
    required: ['scopes']
  };

  constructor(data: any, schema: JsonSchema) {
    super(data, schema);
  }
}
