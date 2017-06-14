import tv4 = require('tv4');

function validate<T>(data: T, schema: tv4.JsonSchema): T {
  const result = tv4.validateResult(data, schema);
  if (!result.valid) throw new Error(result.error.message);
  else return data;
}

export default class ClientAuthConf {
  public readonly authorizationUri: string;
  public readonly clientId: string;
  public readonly redirectUri: string;
  public readonly requestParameters?: { [name: string]: string };

  private static readonly schema: tv4.JsonSchema = {
    type: 'object',
    properties: {
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
    required: ['authorizationUri', 'clientId', 'redirectUri']
  };

  constructor(data: any) {
    Object.assign(this, validate(data, ClientAuthConf.schema));
  }
}
