const admin = require('firebase-admin');

let initialized = false;

function getFirebaseMessaging() {
  if (!initialized) {
    const encodedCredentials = process.env.FIREBASE_SERVICE_ACCOUNT_BASE64;

    if (!encodedCredentials) {
      throw new Error('Firebase service account is not configured.');
    }

    const serviceAccount = JSON.parse(
      Buffer.from(encodedCredentials, 'base64').toString('utf8')
    );

    admin.initializeApp({
      credential: admin.credential.cert(serviceAccount),
    });

    initialized = true;
  }

  return admin.messaging();
}

async function sendPushNotification(token, title, body) {
  return getFirebaseMessaging().send({
    token,
    notification: { title, body },
    android: {
      priority: 'high',
      notification: { channelId: 'ecotrack_updates' },
    },
  });
}

module.exports = { sendPushNotification };
