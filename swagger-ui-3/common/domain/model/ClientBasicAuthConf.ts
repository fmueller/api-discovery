import { JsonSchema } from 'tv4';
import ClientAuthConf from './ClientAuthConf';

export default class ClientBasicAuthConf implements ClientAuthConf {
  public readonly scheme: 'basic';
  public readonly username: string;
  public readonly password: string;

  public static schema: JsonSchema = {
    type: 'object',
    properties: {
      scheme: {
        type: 'string',
        enum: ['basic']
      },
      username: {
        type: 'string'
      },
      password: {
        type: 'string'
      }
    },
    required: ['scheme', 'username', 'password']
  };

  constructor(data: { scheme: 'basic'; username: string; password: string }) {
    Object.assign(this, data);
  }
}
