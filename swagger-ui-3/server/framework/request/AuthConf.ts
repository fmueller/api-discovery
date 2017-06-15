import { JsonSchema } from 'tv4';
import { Validated } from '../../../common/domain/validate';
import conf from '../conf';

export type AuthConf = BasicConf | AbstractOAuth2Conf | NullAuthConf;
export default AuthConf;

export class BasicConf extends Validated {
  public readonly scheme: 'basic';
  public readonly baseUrl: string;
  public readonly user: string;
  public readonly pass: string;

  private static readonly schema: JsonSchema = {
    type: 'object',
    properties: {
      scheme: {
        type: 'string',
        enum: ['basic']
      },
      baseUrl: {
        type: 'string',
        format: 'uri'
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

export abstract class AbstractOAuth2Conf extends Validated {
  public readonly scheme: 'oauth2';
  public readonly baseUrl: string;
  public readonly scopes: string[];

  protected static readonly schema: tv4.JsonSchema = {
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

export class DynamicOAuth2Conf extends AbstractOAuth2Conf {
  public readonly accessTokenUri: string;
  public readonly tokenInfoUri?: string;
  public readonly realm?: string;
  public readonly credentialsDir?: string;

  protected static readonly schema: tv4.JsonSchema = {
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

export class StaticOAuth2Conf extends AbstractOAuth2Conf {
  public readonly accessTokens: string;

  protected static readonly schema: tv4.JsonSchema = {
    type: 'object',
    allOf: [AbstractOAuth2Conf.schema],
    properties: {
      accessTokens: {
        type: 'string',
        pattern: '^\\S+=\\S+'
      }
    },
    required: ['accessTokens']
  };

  constructor(data: any) {
    super(data, StaticOAuth2Conf.schema);
  }
}

export class NullAuthConf {
  public readonly scheme: 'null';
  public readonly baseUrl: string;

  constructor(data: { baseUrl?: string } = {}) {
    this.scheme = 'null';
    this.baseUrl = data.baseUrl || '';
  }
}

function getAuthConfParser(accessTokens?: string): (data: any) => AuthConf {
  return (data: any) => {
    if (data && data.scheme === 'basic') return new BasicConf(data);
    if (data && data.scheme === 'oauth2') {
      if (accessTokens) return new StaticOAuth2Conf({ ...data, accessTokens });
      else return new DynamicOAuth2Conf(data);
    }
    return new NullAuthConf(data);
  };
}

export function readAuthConf(options: {
  accessTokensConfigKey: string;
  authConfigKey: string;
}): AuthConf {
  const accessTokens = conf.getString(options.accessTokensConfigKey);
  return conf.get(options.authConfigKey, getAuthConfParser(accessTokens));
}
