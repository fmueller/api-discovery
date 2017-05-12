import TokenProvider from './TokenProvider'

export type Options = {
  environmentVariableName?: string;
}

const defaultOptions = {
  environmentVariableName: 'OAUTH2_ACCESS_TOKENS'
}

export class LocalProvider implements TokenProvider {
  private environmentVariableName: string;

  constructor(options: Options = {}) {
    const mergedOptions = { ...defaultOptions, ...options }
    this.environmentVariableName = mergedOptions.environmentVariableName
  }

  getTokens(): { [key: string]: string } {
    const str = process.env[this.environmentVariableName]
    if (typeof str !== 'string' || str.length < 1) {
      return {}
    }

    return str
      .split(',')
      .reduce((obj, pair) => {
        const [key, token] = pair.split('=')
        if (typeof key === 'string' && typeof token === 'string') {
          return Object.assign(obj, { [key]: token })
        } else {
          return obj
        }
      }, {})
  }

  getToken(key: string): string | void {
    return this.getTokens()[key]
  }
}

export default LocalProvider
