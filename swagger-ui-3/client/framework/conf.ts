import log from './debug';

class MissingConfigurationError extends Error {
  constructor(key: string) {
    super(`Configuration value "${key}" is undefined.`);
  }
}

/**
 * Load configuration from configuration meta-tags.
 * Example: <meta name="configuration" content="eyJhIjo0Mn0=">
 */
function getStaticConfiguration() {
  const elements = document.getElementsByName('configuration');
  const conf: { [key: string]: string } = {};

  for (let i = 0; i < elements.length; i += 1) {
    const tag = elements[i];
    const content = tag.getAttribute('content');
    if (!content) continue;
    try {
      Object.assign(conf, JSON.parse(atob(content)));
    } catch (e) {
      log('Invalid configuration tag', tag);
    }
  }

  return conf;
}

function get(key: string): any {
  const localValue = window.localStorage.getItem(key);
  const staticConf = getStaticConfiguration();

  return localValue || staticConf[key];
}

export function getString(key: string, fallback?: string): string {
  const value = get(key);
  if (value === undefined && fallback === undefined) throw new MissingConfigurationError(key);
  if (value === undefined && fallback !== undefined) return fallback;
  if (typeof value === 'string') return value;
  return JSON.stringify(value);
}

export function getObject(key: string, fallback?: any): any {
  const value = get(key);
  if (value === undefined && fallback === undefined) throw new MissingConfigurationError(key);
  if (value === undefined && fallback !== undefined) return fallback;
  if (typeof value === 'string' && value) return JSON.parse(value);
  return value;
}
