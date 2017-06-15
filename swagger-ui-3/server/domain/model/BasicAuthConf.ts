import { JsonSchema } from 'tv4';
import AuthConf from './AuthConf';

/**
 * Configuration for Basic client authentication.
 */
export default class BasicConf extends AuthConf {
  public readonly scheme: 'basic';
  public readonly user: string;
  public readonly pass: string;

  protected static readonly schema: JsonSchema = {
    type: 'object',
    allOf: [AuthConf.schema],
    properties: {
      scheme: {
        type: 'string',
        enum: ['basic']
      },
      baseUrl: {
        type: 'string',
        format: 'uri'
      },
      forwardClientAuthorization: {
        type: 'boolean',
        default: false
      },
      user: {
        type: 'string'
      },
      pass: {
        type: 'string'
      }
    },
    required: ['scheme', 'baseUrl', 'user', 'pass']
  };

  constructor(data: any) {
    super(data, BasicConf.schema);
  }
}
