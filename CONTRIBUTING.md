# Contributing to SentinelAI

Thank you for considering contributing to SentinelAI! We welcome contributions to help improve and expand this smart surveillance platform.

---

## 🛠️ Getting Started

1. **Fork the repository** on GitHub.
2. **Clone your fork** locally:
   ```bash
   git clone https://github.com/YOUR_USERNAME/capstone-project.git
   cd capstone-project
   ```
3. **Create a topic branch**:
   ```bash
   git checkout -b feature/your-feature-name
   ```

---

## 📋 Code Standards & Conventions

### Python (`facial-recognition/`)
- Use [`uv`](https://docs.astral.sh/uv/) for environment & package management.
- Embed [PEP 723](https://peps.python.org/pep-0723/) inline metadata headers in standalone scripts.
- Follow **PEP 8** formatting guidelines.
- Use `logging` instead of `print()` statements.

### Web Application (`WebApp/`)
- Use standard **HTML5**, **Vanilla CSS**, and **ES6 JavaScript Modules**.
- Do not add heavy external CSS frameworks (Tailwind, Bootstrap) — stick to the design system in `index.css`.
- Ensure all credentials use environment placeholders and never commit API keys.

### Firebase Cloud Functions (`firecast/functions/`)
- Target **Node.js 18 (LTS)**.
- Use **Firebase Functions v2** API (`onValueUpdated`).
- Return Promise chains or use `async/await` in background triggers.

### Android Application (`CapstonePrototype/`)
- Target **Android 14 (API 34)** with AndroidX.
- Use Kotlin as the primary language.

---

## 🔒 Security Policy

Never commit API keys, service account JSON files, or device tokens. Refer to **[SECURITY.md](./SECURITY.md)** for details on reporting security vulnerabilities.

---

## 📝 Commit Messages

We use Conventional Commit guidelines:
- `feat:` New features
- `fix:` Bug fixes
- `docs:` Documentation changes
- `style:` Formatting / styling changes
- `refactor:` Code restructuring
- `chore:` Maintenance tasks
