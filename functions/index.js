/**
 * Import function triggers from their respective submodules:
 *
 * const { onCall } = require("firebase-functions/v2/https");
 * const { onDocumentWritten } = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

const functions = require("firebase-functions/v2/https");
const admin = require("firebase-admin");

admin.initializeApp({
  credential: admin.credential.cert("functions/config/serviceAccountkey.json"),
});

const db = admin.firestore(); // Định nghĩa biến db

// Cập nhật vào đầu mỗi tháng
exports.UPIM = functions.pubsub.schedule("0 0 1 * *").onRun(async (context) => {
  console.log("updatePlayInMonth function at the beginning of the month");

  const now = new Date();
  const startOfMonth = new Date(now.getFullYear(), now.getMonth(), 1);
  const endOfMonth = new Date(now.getFullYear(), now.getMonth() + 1, 0);

  try {
    const songsRef = db.collection("song");
    const snapshot = await songsRef.get();

    if (snapshot.empty) {
      console.log("No songs found");
      return null;
    }

    const batch = db.batch();
    snapshot.forEach((doc) => {
      const songRef = doc.ref;
      batch.update(songRef, {
        play_in_month: {
          start: startOfMonth,
          end: endOfMonth,
          count: 0, // Hoặc giá trị cần cập nhật
        },
      });
    });

    await batch.commit();
    console.log("Successfully updated play_in_month for all songs");
  } catch (error) {
    console.error("Error updating play_in_month:", error);
  }
  return null;
});

// Cập nhật vào đầu mỗi tuần
exports.UPIW = functions.pubsub.schedule("0 0 * * 0").onRun(async (context) => {
  console.log("Running updatePlayInWeek function at the beginning of the week");

  const now = new Date();
  const startOfWeek = new Date(now.setDate(now.getDate() - now.getDay()));
  const endOfWeek = new Date(now.setDate(startOfWeek.getDate() + 6));

  try {
    const songsRef = db.collection("song");
    const snapshot = await songsRef.get();

    if (snapshot.empty) {
      console.log("No songs found");
      return null;
    }

    const batch = db.batch();
    snapshot.forEach((doc) => {
      const songRef = doc.ref;
      batch.update(songRef, {
        play_in_week: {
          start: startOfWeek,
          end: endOfWeek,
          count: 0, // Hoặc giá trị cần cập nhật
        },
      });
    });

    await batch.commit();
    console.log("Successfully updated play_in_week for all songs");
  } catch (error) {
    console.error("Error updating play_in_week:", error);
  }
  return null;
});
