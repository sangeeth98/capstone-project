# Facial Recognition & Motion Detection Module

This module provides the computer vision backbone of SentinelAI, handling real-time face recognition, motion detection, and Firebase data synchronization.

## Architecture

```
┌─────────────────┐     ┌──────────────────┐     ┌─────────────────┐
│  Video Stream   │────▶│  OpenCV + dlib    │────▶│  Firebase RTDB  │
│  (Webcam/File)  │     │  Face/Motion Det. │     │  (Monitoring/)  │
└─────────────────┘     └──────────────────┘     └─────────────────┘
```

## Scripts

| Script | Purpose | Key Flags |
|--------|---------|-----------|
| `encode_faces.py` | Build facial encodings from a dataset folder | `--dataset`, `--encodings`, `--detection-method` |
| `pi_face_recognition.py` | Real-time face recognition from webcam | `--cascade`, `--encodings` |
| `motion_detector.py` | Room occupancy detection via motion sensing | `--video`, `--room`, `--min-area` |

## Quick Start

```bash
# Install dependencies
uv sync

# 1. Encode face dataset
uv run encode_faces.py --dataset dataset --encodings encodings.pickle --detection-method hog

# 2. Run live face recognition
uv run pi_face_recognition.py --cascade haarcascade_frontalface_default.xml --encodings encodings.pickle

# 3. Run motion detection for room occupancy
uv run motion_detector.py --room R0101
```

## Configuration

Copy `.env.example` to `.env` and configure:

```dotenv
FIREBASE_DATABASE_URL=https://your-project.firebaseio.com/
# GOOGLE_APPLICATION_CREDENTIALS=path/to/service-account.json
```

## Dataset Structure

```
dataset/
├── PersonName1/
│   ├── img001.jpg
│   ├── img002.jpg
│   └── ...
├── PersonName2/
│   └── ...
```

Each subfolder name becomes the person's label in the recognition system.

## Detection Methods

| Method | Speed | Accuracy | Best For |
|--------|-------|----------|----------|
| `hog`  | ⚡ Fast | Good | Raspberry Pi, CPU-only |
| `cnn`  | 🐢 Slow | Best | Desktop with GPU |
