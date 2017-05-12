import TokenProvider from './TokenProvider'
import LocalProvider from './LocalProvider'

function selectProvider(): TokenProvider | void {
  if (!!process.env.OAUTH2_ACCESS_TOKENS) {
    return new LocalProvider()
  }
}

export default selectProvider()
