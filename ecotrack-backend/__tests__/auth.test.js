const mongoose = require('mongoose');
const request = require('supertest');
const { MongoMemoryServer } = require('mongodb-memory-server');

process.env.JWT_SECRET = 'test-secret';

let mongod;
let app;

beforeAll(async () => {
  mongod = await MongoMemoryServer.create();
  process.env.MONGODB_URI = mongod.getUri();
  await mongoose.connect(process.env.MONGODB_URI);
  app = require('../server');
});

afterAll(async () => {
  await mongoose.disconnect();
  await mongod.stop();
});

describe('POST /api/auth/register', () => {
  test('registers a new user and never returns the plaintext password', async () => {
    const res = await request(app)
      .post('/api/auth/register')
      .send({ name: 'Thandiwe', email: 'thandiwe@example.com', password: 'password123' });

    expect(res.status).toBe(201);
    expect(res.body.token).toBeDefined();
    expect(res.body.user.email).toBe('thandiwe@example.com');
    expect(res.body.user.hashedPassword).toBeUndefined();
    expect(JSON.stringify(res.body)).not.toContain('password123');
  });

  test('rejects a duplicate email', async () => {
    await request(app)
      .post('/api/auth/register')
      .send({ name: 'A', email: 'dup@example.com', password: 'password123' });

    const res = await request(app)
      .post('/api/auth/register')
      .send({ name: 'B', email: 'dup@example.com', password: 'password456' });

    expect(res.status).toBe(409);
  });

  test('rejects a short password', async () => {
    const res = await request(app)
      .post('/api/auth/register')
      .send({ name: 'C', email: 'short@example.com', password: '123' });

    expect(res.status).toBe(400);
  });
});

describe('POST /api/auth/login', () => {
  test('logs in with correct credentials', async () => {
    await request(app)
      .post('/api/auth/register')
      .send({ name: 'Login Test', email: 'login@example.com', password: 'password123' });

    const res = await request(app)
      .post('/api/auth/login')
      .send({ email: 'login@example.com', password: 'password123' });

    expect(res.status).toBe(200);
    expect(res.body.token).toBeDefined();
  });

  test('rejects the wrong password', async () => {
    const res = await request(app)
      .post('/api/auth/login')
      .send({ email: 'login@example.com', password: 'wrongpassword' });

    expect(res.status).toBe(401);
  });
});
