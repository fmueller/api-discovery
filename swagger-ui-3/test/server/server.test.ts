/// <reference types='jest' />

import supertest = require('supertest');
import { Server } from 'http';
import { start as startServer } from '../../server';

describe('server', () => {
  let server: Server;
  let request: supertest.SuperTest<supertest.Test>;

  beforeEach(async () => {
    server = await startServer(3001);
    request = supertest.agent(server);
  });

  afterEach(() => (server ? server.close() : null));

  it('returns health', async () => {
    await request.get('/health').expect('OK');
  });
});
