/**
 * Cloud Functions for Firebase trigger to check Emergency flags
 * and send push notifications to registered devices.
 */

const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp();

exports.checkflag = functions.database.ref('Emergencies')
    .onUpdate((snapshot, context) => {
        // Retrieve the current device registration token from configuration or fallback
        // WARNING: Hardcoding device tokens in source control is not recommended for production.
        // It is better to store tokens dynamically in the database or retrieve them via config.
        const defaultDeviceToken = 'enpOsuehqFg:APA91bGN8xytVoPaTTLEpguap7mJnPP1TwSMCDXfr6rNy6h1cQvX7cnCCuHPaHOYijBIhik5k31NgrClo0_iEISYVe2f4MWXmMiOeHGvxJ_4z4a1GbkWS1ewNx9iwsyWPTxmAZsu2Wd5';
        const targetToken = process.env.FCM_DEVICE_TOKEN || defaultDeviceToken;

        const emergencies = snapshot.after.val();
        if (!emergencies) {
            console.log("No emergencies data found.");
            return null;
        }

        const keys = Object.keys(emergencies);
        const lastKey = keys[keys.length - 1];
        const lastEmergency = emergencies[lastKey];

        const statusMessage = `Emergency of: ${lastEmergency}`;

        const message = {
            notification: {
                title: 'Emergency Alert',
                body: statusMessage
            },
            token: targetToken
        };

        // CRITICAL: Return the promise chain so the function execution environment remains
        // active until the asynchronous notification operation finishes.
        return admin.messaging().send(message)
            .then((response) => {
                console.log("Notification message sent successfully:", response);
                return response;
            })
            .catch((error) => {
                console.error("Error sending notification message:", error);
                throw error;
            });
    });