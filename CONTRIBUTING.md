# Contributing to Aura Browser

Thank you for your interest in contributing to Aura Browser. As an open-source project focused on minimalism and performance, we maintain clear standards to preserve code quality and efficiency.

## Code Standards and Guidelines

1. **Lightweight and Minimal**: Keep dependencies to an absolute minimum. Avoid pulling in large third-party libraries for trivial functionality.
2. **Kotlin and Compose First**: Write all user interfaces using Jetpack Compose and idiomatic Kotlin. Follow official Material Design 3 guidelines.
3. **Reactive Architecture**: State must flow unidirectionally from ViewModels or repositories via `StateFlow` and be observed using lifecycle-aware collectors.
4. **Performance Awareness**: Keep low-end device constraints in mind (2–3 GB RAM, quad-core/octa-core budget processors). Avoid allocations in critical drawing paths or unconstrained memory caching.

## Pull Request Process

1. Fork the repository and create your branch from `main`:
   ```bash
   git checkout -b feature/your-feature-name
   ```
2. Implement your changes following established formatting conventions.
3. Verify that all local tests and compilation checks pass:
   ```bash
   ./gradlew testDebugUnitTest
   ./gradlew assembleDebug -PsplitApks
   ```
4. Commit your changes with clear, descriptive commit messages:
   ```bash
   git commit -m "feat(webview): optimize cache eviction strategy"
   ```
5. Push to your branch and submit a Pull Request against the `main` branch.
6. Ensure all CI workflow checks pass successfully.

## Reporting Issues

If you encounter bugs, performance regressions, or security vulnerabilities, please open an Issue with:
- Target device model and Android version.
- Steps to reproduce the issue.
- Expected versus actual behavior.
- Relevant logcat snippets if available.
