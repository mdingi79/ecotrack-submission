const express = require('express');
const User = require('../models/User');
const { requireAuth } = require('../middleware/auth');
const { sendPushNotification } = require('../services/firebase');

const router = express.Router();

router.use(requireAuth);

// Register or refresh the signed-in user's device token.
router.post('/register', async (req, res) => {
  try {
    const token = req.body?.token;

    if (typeof token !== 'string' || token.trim().length < 20 || token.length > 4096) {
      return res.status(400).json({ error: 'A valid device token is required.' });
    }

    const user = await User.findByIdAndUpdate(
      req.userId,
      { $addToSet: { fcmTokens: token.trim() } },
      { new: true }
    );

    if (!user) return res.status(404).json({ error: 'User not found.' });

    return res.json({ message: 'Device registered for notifications.' });
  } catch (error) {
    console.error('Notification token registration failed:', error.message);
    return res.status(500).json({ error: 'Could not register device.' });
  }
});

// Send a test notification to this user's registered devices only.
router.post('/test', async (req, res) => {
  try {
    const user = await User.findById(req.userId);

    if (!user) return res.status(404).json({ error: 'User not found.' });
    if (!user.fcmTokens?.length) {
      return res.status(400).json({ error: 'No registered devices. Register a device first.' });
    }

    const results = await Promise.allSettled(
      user.fcmTokens.map((token) =>
        sendPushNotification(
          token,
          'EcoTrack test notification',
          'Your EcoTrack notifications are working.'
        )
      )
    );

    const sent = results.filter((result) => result.status === 'fulfilled').length;

    if (sent === 0) {
      console.error('All EcoTrack test notifications failed:', results);
      return res.status(502).json({ error: 'Firebase could not deliver the test notification.' });
    }

    return res.json({
      message: 'Test notification sent.',
      sent,
      failed: results.length - sent,
    });
  } catch (error) {
    console.error('Test notification failed:', error.message);
    return res.status(503).json({ error: 'Notification service is unavailable or not configured.' });
  }
});

module.exports = router;
