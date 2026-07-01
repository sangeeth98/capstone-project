# Modernization Log & Pending Actions

This document tracks all code quality, modernization, and security improvements applied to the Capstone project, along with a checklist of validation and testing steps that should be performed manually.

---

## 🛠️ Completed Modernization Changes

### 1. Web Application (`WebApp/`)
*   **Modular Architecture**: Created [firebase-config.js](../WebApp/firebase-config.js) to initialize Firebase App and Realtime Database in a single central place using ES6 modules and Web SDK v10.
*   **Design & Theme**: Created [index.css](../WebApp/index.css), implementing a premium glassmorphic dark theme with vibrant blue and teal accents, modern typography (Google Fonts Outfit), and styled custom tables.
*   **Toast Notification System**: Created [utils.js](../WebApp/utils.js) to export a non-intrusive `showToast(msg, type)` alert helper, replacing all native browser `alert()` popups.
*   **Refactored Views**:
    *   **Login**: Rewrote `loginpract.html` and `loginpract.js` to use modules, modern variable scoping (`const`/`let`), async/await, and a loading spinner indicator on verification.
    *   **Dashboard**: Redesigned `connectiontofirebase.html` as the central admin panel with interactive navigation cards for each module.
    *   **Employee Logs**: Revamped `EmpOperations.html` and `EmpOperations.js` with structured input groups and optimized O(1) key check for employee existence.
    *   **Monitoring & Occupancy**: Updated `MonitoringPage.html` and `RoomOccupiedPage.html` to query logs asynchronously using modular SDK syntax, printing results in responsive glassmorphic data tables. Fixed spelling typos ("Priosoner" and "Databse").

### 2. Python Facial Recognition (`facial-recognition/`)
*   **Environment & Dependency Management**: Set up a virtual environment powered by `uv`, declaring dependencies natively in [pyproject.toml](./pyproject.toml).
*   **Inline Dependency Execution (PEP 723)**: Embedded inline metadata headers inside standalone scripts (`pi_face_recognition.py` and `encode_faces.py`) allowing one-shot execution via `uv run` without venv setup.
*   **Robust Firebase Integration**: Refactored `pi_face_recognition.py` to use the official `firebase-admin` SDK with a fallback to the Firebase Realtime Database REST API if local service account credentials aren't configured.
*   **Code Standards & Quality**: Rewrote scripts to strictly follow PEP 8 standards, wrapped execution inside `main()`, set up proper error handling, and replaced print statements with the standard `logging` library.

### 3. Android Clients (`CapstonePrototype/` & `Application/`)
*   **Platform Upgrades**: Upgraded compile and target SDKs to **API 34** (Android 14) and upgraded Gradle configurations (`Gradle 8.9` and `AGP 8.3.2`).
*   **Support & Dependency Cleanup**: 
    *   Replaced legacy Support Libraries (`com.android.support`) with modern AndroidX libraries.
    *   Removed deprecated Kotlin plugins (`kotlin-android-extensions`).
    *   Upgraded Firebase Database, Storage, Auth, Messaging, and Analytics dependencies to current stable releases.
*   **Namespace Declaration**: Configured `namespace 'com.example.capstoneprototype'` inside module-level `build.gradle` blocks to satisfy AGP 8.x requirements.

### 4. Firebase Cloud Functions (`firecast/functions/`)
*   **Promise Lifecycle**: Refactored the database trigger in `index.js` to properly return messaging promise chains, ensuring the function container is not terminated before push notifications are successfully delivered.
*   **Code Hygiene**: Standardized variable declarations to modern ES6 (`const`/`let`).

---

## 📋 Pending Actions & Verification Checklist

Since some components rely on local hardware (camera) and external accounts, the following manual validations should be performed:

- [ ] **Web Application Login Flow**
    - Open `loginpract.html` in a web browser.
    - Test logging in using valid admin credentials matching your database records.
    - Confirm redirection to the Admin Dashboard (`connectiontofirebase.html`).
- [ ] **Web Application Realtime Queries**
    - Search for a valid prisoner ID inside Prisoner Monitoring and verify table generation.
    - Add an employee ID on the Employee Operations page and confirm it updates the database state correctly.
- [ ] **Python Encodings & Recognition**
    - Create a test `.env` file in `facial-recognition/` and configure `FIREBASE_DATABASE_URL`.
    - Run face dataset encoding:
      ```bash
      uv run facial-recognition/encode_faces.py --dataset dataset --encodings encodings.pickle --detection-method hog
      ```
    - Run live video recognition:
      ```bash
      uv run facial-recognition/pi_face_recognition.py --cascade haarcascade_frontalface_default.xml --encodings encodings.pickle
      ```
    - Confirm the camera window opens, detects the target face, and patches updates to Firebase.
- [ ] **Android Studio Integration & Sync**
    - Import the Kotlin `CapstonePrototype` or Java `Application` project into Android Studio.
    - Confirm Gradle syncs successfully with the upgraded AGP 8.3.2 and dependencies.
    - Place your Firebase `google-services.json` inside the `app/` folder.
    - Build and test debug APK generation.
- [ ] **Cloud Functions Triggering**
    - Deploy updated functions to Firebase via CLI:
      ```bash
      firebase deploy --only functions
      ```
    - Manually trigger an event in the `Emergencies/` database path and confirm standard FCM notifications are pushed to target device tokens.
