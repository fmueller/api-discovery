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

  test('/health', async () => {
    await request.get('/health').expect('OK');
  });

  test('/favicon.png', async () => {
    const { body } = await request.get('/favicon.png').expect(200);
    expect(body).toBeInstanceOf(Buffer);
  });

  test('/apis', async () => {
    await request.get('/apis/example').expect(200).expect('Content-Type', /text\/html/);
  });
});
