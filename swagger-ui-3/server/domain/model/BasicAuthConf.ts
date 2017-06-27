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
      user: {
        type: 'string'
      },
      pass: {
        type: 'string'
      }
    },
    required: ['user', 'pass']
  };

  constructor(data: any) {
    super(data, BasicConf.schema);
  }
}
