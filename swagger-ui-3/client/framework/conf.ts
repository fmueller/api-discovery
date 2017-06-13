import log from './debug';

function get(key: string): any {
  const localValue = window.localStorage.getItem(key);

  const configTags = document.getElementsByName('configuration');
  const staticConf: { [key: string]: string } = {};

  for (let i = 0; i < configTags.length; i += 1) {
    const tag = configTags[i];
    const content = tag.getAttribute('content') || '{}';
    try {
      Object.assign(staticConf, JSON.parse(content));
    } catch (e) {
      log('Invalid configuration tag', tag);
    }
  }

  return localValue || staticConf[key];
}

export function getString(key: string): string {
  const value = get(key);
  if (typeof value === 'string') return value;
  else return JSON.stringify(value);
}

export function getObject(key: string): any {
  const value = get(key);
  if (typeof value === 'string') return JSON.parse(value);
  else return value;
}
