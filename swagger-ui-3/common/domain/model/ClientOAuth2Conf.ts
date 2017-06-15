import { JsonSchema } from 'tv4';
import ClientAuthConf from './ClientAuthConf';

export default class ClientOAuth2Conf implements ClientAuthConf {
  public readonly scheme: 'oauth2';
  public readonly authorizationUri: string;
  public readonly clientId: string;
  public readonly redirectUri: string;
  public readonly requestParameters?: object;

  public static schema: JsonSchema = {
    type: 'object',
    properties: {
      scheme: {
        type: 'string',
        enum: ['oauth2']
      },
      authorizationUri: {
        type: 'string',
        format: 'uri'
      },
      clientId: {
        type: 'string'
      },
      redirectUri: {
        type: 'string',
        format: 'uri'
      },
      requestParameters: {
        type: 'object'
      }
    },
    required: ['scheme', 'authorizationUri', 'clientId', 'redirectUri']
  };

  constructor(data: {
    scheme: 'oauth2';
    authorizationUri: string;
    clientId: string;
    redirectUri: string;
    requestParameters?: object;
  }) {
    Object.assign(this, data);
  }
}
