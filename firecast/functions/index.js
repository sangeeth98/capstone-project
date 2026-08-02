/**
 * SentinelAI — Firebase Cloud Functions
 *
 * Listens for emergency events written to the Realtime Database
 * and dispatches push notifications to registered devices via FCM.
 *
 * Environment Variables:
 *   FCM_DEVICE_TOKEN — The target device registration token for notifications.
 *                      Set via: firebase functions:config:set app.fcm_token="<token>"
 *                      Or set as a runtime environment variable in the Firebase Console.
 */

const { onValueUpdated } = require("firebase-functions/v2/database");
const { logger } = require("firebase-functions");
const { initializeApp } = require("firebase-admin/app");
const { getMessaging } = require("firebase-admin/messaging");

initializeApp();

/**
 * Trigger: Fires whenever the /Emergencies path is updated in Realtime Database.
 *
 * Reads the latest emergency entry and sends a push notification
 * to the configured device token.
 */
exports.checkflag = onValueUpdated(
  {
    ref: "/Emergencies",
    region: "us-central1",
  },
  async (event) => {
    // Retrieve device token from environment or Functions config
    const targetToken = process.env.FCM_DEVICE_TOKEN;

    if (!targetToken) {
      logger.error(
        "FCM_DEVICE_TOKEN is not configured. " +
        "Set it via: firebase functions:config:set app.fcm_token=\"<token>\" " +
        "or as a runtime environment variable."
      );
      return null;
    }

    const emergencies = event.data.after.val();
    if (!emergencies) {
      logger.info("No emergencies data found in the updated snapshot.");
      return null;
    }

    // Get the most recent emergency entry
    const keys = Object.keys(emergencies);
    const lastKey = keys[keys.length - 1];
    const lastEmergency = emergencies[lastKey];

    logger.info(`Processing emergency: key=${lastKey}, value=${lastEmergency}`);

    const message = {
      notification: {
        title: "🚨 SentinelAI Emergency Alert",
        body: `Emergency reported: ${lastEmergency}`,
      },
      token: targetToken,
    };

    try {
      const response = await getMessaging().send(message);
      logger.info("Push notification sent successfully:", response);
      return response;
    } catch (error) {
      logger.error("Failed to send push notification:", error);
      throw error;
    }
  }
);