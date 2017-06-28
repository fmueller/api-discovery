import { JsonSchema } from 'tv4';
import { Validated } from '../validate';
import ClientAuthConf from './ClientAuthConf';

export default class ClientOAuth2Conf extends Validated implements ClientAuthConf {
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

  constructor(
    data: {
      scheme: 'oauth2';
      authorizationUri: string;
      clientId: string;
      redirectUri: string;
      requestParameters?: object;
    },
    urlResolver: (url: string) => string = _ => _,
    objectParser = (s?: any) => (typeof s === 'string' ? JSON.parse(s || '{}') : s)
  ) {
    super(
      {
        ...data,
        clientId: urlResolver(data.clientId),
        requestParameters: objectParser(data.requestParameters)
      },
      ClientOAuth2Conf.schema
    );
  }
}
