# Running the Facial Recognition Scripts

This module uses [uv](https://docs.astral.sh/uv/) for Python environment management and supports two execution modes.

---

## Prerequisites

Install `uv` if you haven't already:

```bash
# Windows (PowerShell)
powershell -ExecutionPolicy ByPass -c "irm https://astral.sh/uv/install.ps1 | iex"

# macOS / Linux
curl -LsSf https://astral.sh/uv/install.sh | sh
```

---

## Mode 1 — Project Environment (recommended for repeated use)

Use this when you'll be running the scripts frequently. `uv` creates a managed virtual environment based on `pyproject.toml`.

```bash
cd facial-recognition

# Create the virtual environment and install all dependencies
uv sync

# Encode your dataset (builds encodings.pickle from images in /dataset)
uv run encode_faces.py \
  --dataset dataset \
  --encodings encodings.pickle \
  --detection-method hog

# Run the live face recognition loop
uv run pi_face_recognition.py \
  --cascade haarcascade_frontalface_default.xml \
  --encodings encodings.pickle
```

> On Windows, use `^` instead of `\` for line continuation, or write each flag on one line.

---

## Mode 2 — Inline Script (zero setup, one-shot use)

Each script embeds its own dependency metadata ([PEP 723](https://peps.python.org/pep-0723/)). `uv` reads this block and auto-installs everything into an isolated cache — **no venv creation needed**.

```bash
# From anywhere on your system — no cd required
uv run path/to/encode_faces.py --dataset dataset --encodings encodings.pickle
uv run path/to/pi_face_recognition.py --cascade haarcascade_frontalface_default.xml --encodings encodings.pickle
```

On **Linux / macOS** only, you can also make the scripts directly executable:

```bash
chmod +x encode_faces.py pi_face_recognition.py

# Run directly (the #!/usr/bin/env -S uv run shebang handles the rest)
./encode_faces.py --dataset dataset --encodings encodings.pickle
./pi_face_recognition.py --cascade haarcascade_frontalface_default.xml --encodings encodings.pickle
```

---

## Configuration (Firebase & Credentials)

Before running `pi_face_recognition.py`, set up your environment:

1. Copy `.env.example` to `.env`:
   ```bash
   cp .env.example .env
   ```

2. Edit `.env` and fill in your values:
   ```dotenv
   FIREBASE_DATABASE_URL=https://your-project.firebaseio.com/

   # Optional: path to Firebase service account JSON for Admin SDK auth
   # GOOGLE_APPLICATION_CREDENTIALS=path/to/service-account.json
   ```

> If `GOOGLE_APPLICATION_CREDENTIALS` is not set, the script falls back to the public Firebase REST API (works if your Realtime Database rules allow public reads/writes).

---

## Script Reference

| Script | Purpose | Key Flags |
|--------|---------|-----------|
| `encode_faces.py` | Build facial encodings from a dataset folder | `--dataset`, `--encodings`, `--detection-method` |
| `pi_face_recognition.py` | Real-time face recognition from webcam | `--cascade`, `--encodings` |

### Detection Methods

| Method | Speed | Accuracy | Best for |
|--------|-------|----------|---------|
| `hog` | Fast | Good | Raspberry Pi, CPU-only |
| `cnn` | Slow | Best | Desktop with GPU |
