# Contributing to MorseLight

Thanks for your interest in improving MorseLight — an ad-free Morse-code flashlight app for
Android. Contributions of all kinds are welcome: bug reports, feature ideas, docs, and code.

## Ways to contribute

- **Report a bug** or **suggest a feature** by opening an [issue](https://github.com/ranjan-malav/MorseLight/issues).
  Please search existing issues first. For bugs, include your device model, Android version, and
  steps to reproduce.
- **Submit a pull request** for a fix or enhancement (see below).
- **Improve the docs** — the README and this guide.

## Development setup

- **Android Studio** (latest stable) with **JDK 17**.
- Open the project and let Gradle sync. Min SDK 26, target SDK 36.
- Run the app on a device or emulator. The flashlight and camera-decode features need a physical
  device to verify end to end.

Useful commands:

```bash
./gradlew :app:testDebugUnitTest        # unit tests
./gradlew :app:assembleDebug            # build a debug APK
./gradlew :app:lintDebug                # lint
./gradlew :app:connectedDebugAndroidTest  # instrumented / Compose UI tests (needs a device)
```

CI (GitHub Actions) runs the unit tests, a debug build, lint, and the instrumented tests on every
push and pull request — please make sure these pass.

## Pull request guidelines

1. **Branch** off `main` (or the current working branch) for your change.
2. Keep PRs focused — one logical change per PR, with a clear description of what and why.
3. **Match the surrounding style.** The UI is Jetpack Compose on the "Personal UI" design system;
   domain logic (`morse/`) is pure Kotlin and unit-tested.
4. **Add or update tests** for behavior changes — especially in the `morse/` package.
5. Run the commands above locally and confirm they pass before opening the PR.
6. Reference any related issue (e.g. "Fixes #3") in the description.

## Reporting security issues

Please do not open a public issue for security-sensitive reports. Email the maintainer instead.

## License

By contributing, you agree that your contributions will be licensed under the project's
[MIT License](LICENSE).
