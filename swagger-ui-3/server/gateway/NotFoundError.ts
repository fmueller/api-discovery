export default class NotFoundError extends Error {
  public readonly url: string;

  constructor(url: string) {
    super('Resource not found.');
    this.url = url;
  }
}
