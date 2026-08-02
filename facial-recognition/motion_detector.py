#!/usr/bin/env -S uv run
# /// script
# requires-python = ">=3.9"
# dependencies = [
#   "opencv-python>=4.9.0",
#   "imutils>=0.5.4",
#   "firebase-admin>=6.5.0",
#   "python-dotenv>=1.0.1",
#   "requests>=2.31.0",
# ]
# ///
"""
motion_detector.py

Detects motion in a video stream using background subtraction and logs
room occupancy status to Firebase Realtime Database.

Usage:
    uv run motion_detector.py
    uv run motion_detector.py --video path/to/video.mp4
    uv run motion_detector.py --room R0101 --min-area 800
"""

import argparse
import datetime
import logging
import os
import time

import cv2
import imutils
import requests
from dotenv import load_dotenv
from imutils.video import VideoStream

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(message)s",
    handlers=[logging.StreamHandler()],
)
logger = logging.getLogger(__name__)

# Load environment variables
load_dotenv()


class FirebaseHelper:
    """Handles database operations using Firebase SDK or REST API fallback.

    Mirrors the pattern in pi_face_recognition.py for consistency.
    """

    def __init__(self, db_url: str):
        self.db_url = db_url.rstrip("/")
        self.use_sdk = False

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
                logger.error(
                    f"Failed to initialize Firebase Admin SDK: {e}. "
                    "Using REST fallback."
                )
        else:
            logger.info(
                "GOOGLE_APPLICATION_CREDENTIALS not set or file not found. "
                "Using REST API fallback."
            )

    def patch_room_status(
        self, room_id: str, date_str: str, time_str: str, status: str
    ):
        """Patches occupancy status for a room at a specific time."""
        data_payload = {time_str: status}

        if self.use_sdk:
            try:
                from firebase_admin import db

                ref = db.reference(f"Room Occupied/{room_id}/{date_str}")
                ref.update(data_payload)
                logger.info(
                    f"[SDK] Logged {status} for room '{room_id}' at {time_str}"
                )
            except Exception as e:
                logger.error(f"Firebase SDK update failed: {e}")
        else:
            try:
                url = f"{self.db_url}/Room Occupied/{room_id}/{date_str}.json"
                response = requests.patch(url, json=data_payload, timeout=10)
                if response.status_code == 200:
                    logger.info(
                        f"[REST] Logged {status} for room '{room_id}' at {time_str}"
                    )
                else:
                    logger.error(
                        f"[REST] Failed to patch database. "
                        f"Status: {response.status_code}, Body: {response.text}"
                    )
            except Exception as e:
                logger.error(f"[REST] Error patching database: {e}")

    def increment_counter(
        self, room_id: str, date_str: str, counter_key: str
    ):
        """Increments the Occupied or Unoccupied counter for a room/date."""
        if self.use_sdk:
            try:
                from firebase_admin import db

                ref = db.reference(
                    f"Room Occupied/{room_id}/{date_str}/{counter_key}"
                )
                current = ref.get()
                ref.set((current or 0) + 1)
            except Exception as e:
                logger.error(f"Firebase SDK counter increment failed: {e}")
        else:
            try:
                url = (
                    f"{self.db_url}/Room Occupied/{room_id}/"
                    f"{date_str}/{counter_key}.json"
                )
                response = requests.get(url, timeout=10)
                current = response.json() if response.status_code == 200 else None
                new_val = (current or 0) + 1
                requests.put(url, json=new_val, timeout=10)
            except Exception as e:
                logger.error(f"[REST] Error incrementing counter: {e}")


def parse_arguments():
    """Parses command line arguments."""
    ap = argparse.ArgumentParser(
        description="Motion detection with Firebase room occupancy logging"
    )
    ap.add_argument(
        "-v",
        "--video",
        help="path to a video file (omit to use webcam)",
    )
    ap.add_argument(
        "-a",
        "--min-area",
        type=int,
        default=500,
        help="minimum contour area to qualify as motion (default: 500)",
    )
    ap.add_argument(
        "-r",
        "--room",
        type=str,
        default="R0088",
        help="room ID for database logging (default: R0088)",
    )
    return vars(ap.parse_args())


def main():
    args = parse_arguments()

    # Load database URL configuration
    db_url = os.getenv(
        "FIREBASE_DATABASE_URL",
        "https://capstone-prototype-7b1f9.firebaseio.com/",
    )
    firebase_helper = FirebaseHelper(db_url)

    room_id = args["room"]

    # Initialize video source
    if args.get("video") is None:
        logger.info("Starting webcam video stream...")
        vs = VideoStream(src=0).start()
        time.sleep(2.0)
        use_webcam = True
    else:
        logger.info(f"Opening video file: {args['video']}")
        vs = cv2.VideoCapture(args["video"])
        use_webcam = False

    # Background reference frame
    first_frame = None

    logger.info(
        f"Motion detection started for room '{room_id}'. Press 'q' to quit."
    )

    try:
        while True:
            # Read the next frame
            frame = vs.read() if use_webcam else vs.read()[1]
            status_text = "Unoccupied"

            if frame is None:
                if not use_webcam:
                    logger.info("End of video file reached.")
                break

            # Preprocess: resize, grayscale, blur
            frame = imutils.resize(frame, width=500)
            gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
            gray = cv2.GaussianBlur(gray, (21, 21), 0)

            # Initialize reference frame
            if first_frame is None:
                first_frame = gray
                continue

            # Compute difference from reference frame
            frame_delta = cv2.absdiff(first_frame, gray)
            thresh = cv2.threshold(
                frame_delta, 25, 255, cv2.THRESH_BINARY
            )[1]
            thresh = cv2.dilate(thresh, None, iterations=2)

            # Find contours on thresholded image
            cnts = cv2.findContours(
                thresh.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE
            )
            cnts = imutils.grab_contours(cnts)

            for c in cnts:
                if cv2.contourArea(c) < args["min_area"]:
                    continue
                (x, y, w, h) = cv2.boundingRect(c)
                cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 0), 2)
                status_text = "Occupied"

            # Draw status and timestamp on the frame
            cv2.putText(
                frame,
                f"Room Status: {status_text}",
                (10, 20),
                cv2.FONT_HERSHEY_SIMPLEX,
                0.5,
                (0, 0, 255),
                2,
            )
            cv2.putText(
                frame,
                datetime.datetime.now().strftime("%A %d %B %Y %I:%M:%S%p"),
                (10, frame.shape[0] - 10),
                cv2.FONT_HERSHEY_SIMPLEX,
                0.35,
                (0, 0, 255),
                1,
            )

            # Log to Firebase
            now = datetime.datetime.now()
            date_str = now.strftime("%Y-%m-%d")
            time_str = now.strftime("%H:%M:%S")

            firebase_helper.patch_room_status(
                room_id, date_str, time_str, status_text
            )
            firebase_helper.increment_counter(
                room_id, date_str, status_text
            )

            # Display the frames
            cv2.imshow("Security Feed", frame)
            cv2.imshow("Threshold", thresh)
            cv2.imshow("Frame Delta", frame_delta)

            key = cv2.waitKey(1) & 0xFF
            if key == ord("q"):
                logger.info("Exit key pressed.")
                break

    except KeyboardInterrupt:
        logger.info("Process interrupted by user.")
    finally:
        # Cleanup
        if use_webcam:
            vs.stop()
        else:
            vs.release()
        cv2.destroyAllWindows()
        logger.info("Video stream stopped and windows cleared.")


if __name__ == "__main__":
    main()
