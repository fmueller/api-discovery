import { JsonSchema } from 'tv4';
import { Validated } from '../../../common/domain/validate';

/**
 * Configuration for server-side HTTP client authentication.
 */
export default abstract class AuthConf extends Validated {
  public readonly scheme: 'basic' | 'oauth2';
  public readonly baseUrl: string;
  public readonly forwardClientAuthorization?: boolean;

  protected static readonly schema: JsonSchema = {
    type: 'object',
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
      }
    },
    required: ['scheme', 'baseUrl']
  };

  constructor(data: any, schema: JsonSchema) {
    super(data, schema);
  }
}
