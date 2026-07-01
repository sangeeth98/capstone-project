#!/usr/bin/env -S uv run
# /// script
# requires-python = ">=3.9"
# dependencies = [
#   "opencv-python>=4.9.0",
#   "face-recognition>=1.3.0",
#   "imutils>=0.5.4",
#   "firebase-admin>=6.5.0",
#   "python-dotenv>=1.0.1",
#   "requests>=2.31.0",
#   "setuptools<70",
# ]
# ///
"""
pi_face_recognition.py

Recognizes faces in a video stream using a Haar cascade and pre-computed facial embeddings,
and logs the monitored results to Firebase Realtime Database.

Usage:
    uv run pi_face_recognition.py --cascade haarcascade_frontalface_default.xml --encodings encodings.pickle
"""

import argparse
import datetime
import logging
import os
import pickle
import time
from time import sleep
import cv2
import face_recognition
from dotenv import load_dotenv
from imutils.video import FPS, VideoStream
import imutils
import requests

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(message)s",
    handlers=[logging.StreamHandler()]
)
logger = logging.getLogger(__name__)

# Load environment variables
load_dotenv()

class FirebaseHelper:
    """Helper class to handle database operations using Firebase SDK or REST API fallback."""
    def __init__(self, db_url):
        self.db_url = db_url.rstrip("/")
        self.use_sdk = False
        
        # Check for service account credentials to initialize the admin SDK
        cred_path = os.getenv("GOOGLE_APPLICATION_CREDENTIALS")
        if cred_path and os.path.exists(cred_path):
            try:
                import firebase_admin
                from firebase_admin import credentials
                cred = credentials.Certificate(cred_path)
                firebase_admin.initialize_app(cred, {"databaseURL": self.db_url})
                self.use_sdk = True
                logger.info("Firebase Admin SDK successfully initialized.")
            except Exception as e:
                logger.error(f"Failed to initialize Firebase Admin SDK: {e}. Using REST fallback.")
        else:
            logger.info("GOOGLE_APPLICATION_CREDENTIALS not set or file not found. Using REST API fallback.")

    def patch_monitoring_status(self, name: str, counter: int, timestamp: str):
        """Patches the monitoring detection timestamp for a given person."""
        data_payload = {str(counter): timestamp}
        if self.use_sdk:
            try:
                from firebase_admin import db
                ref = db.reference(f"Monitoring/{name}")
                ref.update(data_payload)
                logger.info(f"[SDK] Logged detection for '{name}' (Count: {counter})")
            except Exception as e:
                logger.error(f"Firebase SDK update failed: {e}")
        else:
            # Fallback to Firebase REST API
            try:
                url = f"{self.db_url}/Monitoring/{name}.json"
                response = requests.patch(url, json=data_payload, timeout=10)
                if response.status_code == 200:
                    logger.info(f"[REST] Logged detection for '{name}' (Count: {counter})")
                else:
                    logger.error(f"[REST] Failed to patch database. Status: {response.status_code}, Body: {response.text}")
            except Exception as e:
                logger.error(f"[REST] Error patching database: {e}")


def parse_arguments():
    """Parses command line arguments."""
    ap = argparse.ArgumentParser()
    ap.add_argument(
        "-c", "--cascade",
        required=True,
        help="path to where the face cascade resides"
    )
    ap.add_argument(
        "-e", "--encodings",
        required=True,
        help="path to serialized db of facial encodings"
    )
    return vars(ap.parse_args())


def main():
    args = parse_arguments()

    # Load database URL configuration
    db_url = os.getenv("FIREBASE_DATABASE_URL", "https://capstone-prototype-7b1f9.firebaseio.com/")
    firebase_helper = FirebaseHelper(db_url)

    # Load known faces and embeddings along with OpenCV's Haar Cascade
    logger.info("Loading encodings and face detector...")
    try:
        with open(args["encodings"], "rb") as f:
            data = pickle.loads(f.read())
    except Exception as e:
        logger.critical(f"Failed to load encodings pickle file: {e}")
        return

    detector = cv2.CascadeClassifier(args["cascade"])
    if detector.empty():
        logger.critical("Failed to load Haar cascade classifier. Check the cascade file path.")
        return

    # Initialize video stream
    logger.info("Starting video stream...")
    vs = VideoStream(src=0).start()
    time.sleep(2.0)

    # Tracking lists and dictionaries for current stream session
    detected_names = []
    detection_counters = {}

    # Start the FPS counter
    fps = FPS().start()

    logger.info("Streaming started. Press 'q' in the window to quit.")
    try:
        while True:
            frame = vs.read()
            if frame is None:
                logger.warning("Captured empty frame from video stream.")
                continue

            frame = imutils.resize(frame, width=500)

            # Convert BGR to grayscale (for face detection) and BGR to RGB (for face recognition)
            gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
            rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)

            # Detect faces in the frame
            rects = detector.detectMultiScale(
                gray,
                scaleFactor=1.1,
                minNeighbors=5,
                minSize=(30, 30),
                flags=cv2.CASCADE_SCALE_IMAGE
            )

            # Convert bounding boxes from (x, y, w, h) to (top, right, bottom, left)
            boxes = [(y, x + w, y + h, x) for (x, y, w, h) in rects]

            # Compute facial embeddings
            encodings = face_recognition.face_encodings(rgb, boxes)
            current_frame_names = []

            for encoding in encodings:
                # Compare face encodings
                matches = face_recognition.compare_faces(data["encodings"], encoding)
                name = "Unknown"

                if True in matches:
                    # Vote system to find matching index
                    matched_idxs = [i for (i, b) in enumerate(matches) if b]
                    counts = {}
                    for i in matched_idxs:
                        name = data["names"][i]
                        counts[name] = counts.get(name, 0) + 1
                    name = max(counts, key=counts.get)

                current_frame_names.append(name)

                # Process database logging
                timestamp_str = datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")
                if name in detected_names:
                    detection_counters[name] += 1
                else:
                    detected_names.append(name)
                    detection_counters[name] = 1

                # Send update to Firebase
                firebase_helper.patch_monitoring_status(name, detection_counters[name], timestamp_str)
                sleep(5)

            # Render bounding boxes and names on the frame
            for ((top, right, bottom, left), name) in zip(boxes, current_frame_names):
                cv2.rectangle(frame, (left, top), (right, bottom), (0, 255, 0), 2)
                y_pos = top - 15 if top - 15 > 15 else top + 15
                cv2.putText(frame, name, (left, y_pos), cv2.FONT_HERSHEY_SIMPLEX, 0.75, (0, 255, 0), 2)

            # Display window
            cv2.imshow("Frame", frame)
            key = cv2.waitKey(1) & 0xFF
            if key == ord("q"):
                logger.info("Exit key pressed.")
                break

            fps.update()
    except KeyboardInterrupt:
        logger.info("Process interrupted by user.")
    finally:
        fps.stop()
        logger.info(f"Elapsed time: {fps.elapsed():.2f} seconds")
        logger.info(f"Approximate FPS: {fps.fps():.2f}")

        # Cleanup
        cv2.destroyAllWindows()
        vs.stop()
        logger.info("Video stream stopped and windows cleared.")

if __name__ == "__main__":
    main()
