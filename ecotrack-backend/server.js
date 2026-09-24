require('dotenv').config();
const express = require('express');
const cors = require('cors');
const mongoose = require('mongoose');

const authRoutes = require('./routes/auth');
const logRoutes = require('./routes/logs');
const categoryRoutes = require('./routes/categories');

const app = express();
app.use(cors());
app.use(express.json());

app.get('/api/health', (req, res) => res.json({ status: 'ok' }));

app.use('/api/auth', authRoutes);
app.use('/api/logs', logRoutes);
app.use('/api/categories', categoryRoutes);

// Fallback error handler so a thrown error never crashes the process
app.use((err, req, res, next) => {
  console.error('Unhandled error:', err);
  res.status(500).json({ error: 'Internal server error' });
});

const PORT = process.env.PORT || 4000;

// Only auto-connect/listen outside of tests - tests spin up their own in-memory DB.
if (require.main === module) {
  mongoose
    .connect(process.env.MONGODB_URI)
    .then(() => {
      console.log('Connected to MongoDB');
      app.listen(PORT, () => console.log(`EcoTrack API listening on port ${PORT}`));
    })
    .catch((err) => {
      console.error('Failed to connect to MongoDB:', err.message);
      process.exit(1);
    });
}

module.exports = app;
