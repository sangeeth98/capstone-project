#!/usr/bin/env -S uv run
# /// script
# requires-python = ">=3.9"
# dependencies = [
#   "opencv-python>=4.9.0",
#   "face-recognition>=1.3.0",
#   "imutils>=0.5.4",
#   "setuptools<70",
# ]
# ///
"""
encode_faces.py

Quantifies faces in a dataset directory and serializes facial encodings to a pickle file.

Usage:
    uv run encode_faces.py --dataset dataset --encodings encodings.pickle --detection-method hog
"""

import argparse
import logging
import os
import pickle
import cv2
import face_recognition
from imutils import paths

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(message)s",
    handlers=[logging.StreamHandler()]
)
logger = logging.getLogger(__name__)

def parse_arguments():
    """Parses command line arguments."""
    ap = argparse.ArgumentParser()
    ap.add_argument(
        "-i", "--dataset",
        required=True,
        help="path to input directory of faces + images"
    )
    ap.add_argument(
        "-e", "--encodings",
        required=True,
        help="path to serialized db of facial encodings"
    )
    ap.add_argument(
        "-d", "--detection-method",
        type=str,
        default="cnn",
        choices=["hog", "cnn"],
        help="face detection model to use: either 'hog' or 'cnn'"
    )
    return vars(ap.parse_args())

def main():
    args = parse_arguments()

    # Grab path to input images
    logger.info("Quantifying faces and gathering image paths...")
    image_paths = list(paths.list_images(args["dataset"]))
    if not image_paths:
        logger.warning(f"No images found in the dataset directory: {args['dataset']}")
        return

    known_encodings = []
    known_names = []

    # Loop over all image paths
    for i, image_path in enumerate(image_paths):
        logger.info(f"Processing image {i + 1}/{len(image_paths)}: {image_path}")
        
        # Folder name represents the person's name
        name = image_path.split(os.path.sep)[-2]

        # Load image and convert color space from BGR (OpenCV) to dlib RGB format
        image = cv2.imread(image_path)
        if image is None:
            logger.warning(f"Failed to read image: {image_path}. Skipping.")
            continue
            
        rgb = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)

        # Detect the coordinates of bounding boxes corresponding to each face
        boxes = face_recognition.face_locations(rgb, model=args["detection_method"])

        # Compute the facial embeddings
        encodings = face_recognition.face_encodings(rgb, boxes)

        # Add encodings and name to our catalog
        for encoding in encodings:
            known_encodings.append(encoding)
            known_names.append(name)

    # Serialize encodings to disk
    logger.info(f"Writing {len(known_encodings)} facial encodings to '{args['encodings']}'...")
    try:
        data = {"encodings": known_encodings, "names": known_names}
        with open(args["encodings"], "wb") as f:
            f.write(pickle.dumps(data))
        logger.info("Serialization completed successfully.")
    except Exception as e:
        logger.error(f"Error during serialization: {e}")

if __name__ == "__main__":
    main()