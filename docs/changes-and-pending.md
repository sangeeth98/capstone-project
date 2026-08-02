# SentinelAI — Modernization Changelog & Architecture Audit

This document records the complete transformation of SentinelAI from a college capstone project into a modern, portfolio-ready security platform.

---

## 📜 Full Modernization Changelog

### Version 2.0.0 (August 2026) — Complete Project Revamp

#### 1. Repository Reorganization & Academic Migration
* **Google Drive Migration**: Extracted capstone thesis (`BIS-28-Thesis.pdf`), published research paper, project completion report, review PPTs (Reviews 1-3), architecture diagrams, and 8 IEEE/Springer reference papers from Google Drive into a structured `docs/` tree.
* **Repository Cleanup**: 
  * Removed 848 raw YOLO training data files (`data/` directory) to fix git bloat (dataset archived safely in Google Drive).
  * Removed duplicate legacy Java Android project (`Application/`), establishing `CapstonePrototype/` (Kotlin) as canonical.
  * Removed dead test scripts (`checkfirebase.py`).
  * Untracked generated binaries (`encodings.pickle`) and credential files (`google-services.json`).
  * Created comprehensive `.gitignore` with sections for Python, Android, Node, credentials, and OS files.

#### 2. Web Application (`WebApp/`)
* **Design System**: Replaced default styling with a command-center glassmorphism design system in `index.css` (Inter typography, custom scrollbars, toast notifications, confirmation modal dialogs).
* **Particle Canvas**: Created `particles.js` to render an animated floating particle background.
* **Command Center Dashboard**: Created `dashboard.html` with real-time stat cards (rooms, staff, today's logins) and live activity feed powered by Firebase RTDB.
* **CSV Export Engine**: Added `exportTableToCSV()` utility across all monitoring and occupancy view pages.
* **Security & Auth**: Sanitized `firebase-config.js` by replacing hardcoded credentials with placeholders and created `WebApp/.env.example`.

#### 3. Python Computer Vision Engine (`facial-recognition/`)
* **Motion Detector Modernization**: Rewrote `motion_detectorp3.py` → `motion_detector.py` with:
  * PEP 723 inline dependency metadata block for `uv run` single-command execution.
  * `firebase-admin` SDK with automatic REST API fallback.
  * Structured `logging` replacing `print` statements.
  * Command-line argument parsing (`--video`, `--room`, `--min-area`).
  * PEP 8 compliance and ISO date formats (`YYYY-MM-DD`).
* **Module Documentation**: Created `facial-recognition/README.md`.

#### 4. Serverless Cloud Functions (`firecast/functions/`)
* **API Modernization**: Upgraded from legacy Firebase Functions v1 (`functions.database.ref.onUpdate`) to Functions v2 (`onValueUpdated`).
* **Runtime Upgrade**: Upgraded Node engine from Node 8 (EOL 2019) to Node 18 LTS.
* **Security**: Removed hardcoded FCM device registration tokens — now reads from `process.env.FCM_DEVICE_TOKEN`.

#### 5. Project Governance & Polish
* **Banner Art**: Generated high-resolution digital artwork for the project header (`docs/assets/banner.png`).
* **License Upgrade**: Re-licensed project from GPL v3 to MIT License for open-source portfolio presentation.
* **GitHub Community Files**: Added `CONTRIBUTING.md`, `SECURITY.md`, and issue templates for bug reports and feature requests.
* **Portfolio README**: Created a top-tier `README.md` with badges, architecture diagrams (Mermaid), module summaries, and quick-start instructions.

---

## 📋 Verification & Testing Checklist

- [x] WebApp modular architecture & design system
- [x] WebApp CSV export engine
- [x] Python PEP 723 dependency isolation
- [x] Python motion detector rewrite
- [x] Cloud Functions Node 18 & v2 API upgrade
- [x] Academic document migration from Google Drive
- [x] Repository cleanup & credential sanitization
- [x] MIT License & GitHub community templates
