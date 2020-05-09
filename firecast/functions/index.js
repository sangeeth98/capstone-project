// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp();
exports.checkflag = functions.database.ref('Emergencies') //give your database path instead here
.onUpdate((snapshot, context) => {
const temptoken = 'enpOsuehqFg:APA91bGN8xytVoPaTTLEpguap7mJnPP1TwSMCDXfr6rNy6h1cQvX7cnCCuHPaHOYijBIhik5k31NgrClo0_iEISYVe2f4MWXmMiOeHGvxJ_4z4a1GbkWS1ewNx9iwsyWPTxmAZsu2Wd5';  //replace it with your app token
// const flag = snapshot.before.val();   TO GET THE OLD VALUE BEFORE UPDATE
const flag = snapshot.after.val();
var keys = Object.keys(flag);
var key=keys.length;
let statusMessage = `Emergency of:${flag[key]}`
var message = {
notification: {
title: 'Emergency Message',
body: statusMessage
},
token: temptoken
};
admin.messaging().send(message).then((response) => {
console.log("Message sent successfully:", response);
return response;
})
.catch((error) => {
console.log("Error sending message: ", error);
});
});