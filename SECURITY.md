# Security Policy

## Supported Versions

| Component | Version | Security Support |
| --------- | ------- | ---------------- |
| WebApp | ES6 Modules / Firebase SDK v10 | ✅ Supported |
| Python Module | Python 3.9+ / `uv` | ✅ Supported |
| Cloud Functions | Node 18 / Functions v2 | ✅ Supported |
| Android Client | API 34 (Android 14) | ✅ Supported |

---

## Reporting a Vulnerability

If you discover a potential security vulnerability in SentinelAI (e.g. exposed credentials, database rule bypass, or insecure API usage):

1. **Do NOT open a public GitHub issue.**
2. Email the maintainer directly or submit a private security advisory through GitHub.
3. Provide details including:
   - Affected component(s)
   - Steps to reproduce the issue
   - Potential impact

We will review and address reported vulnerabilities promptly.

---

## Best Practices for Deployments

- **Firebase Credentials**: Never commit `google-services.json`, service account private keys, or web API keys to public repositories. Use `.env` files (excluded by `.gitignore`).
- **Database Rules**: Configure Firebase Realtime Database rules to restrict unauthorized read/write access.
- **Device Tokens**: Store FCM registration tokens dynamically in database nodes or environment variables rather than hardcoding them in Cloud Functions.
