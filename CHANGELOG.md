# Changelog

All notable changes to MorseLight are documented here. The format is based on
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres to
[Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [4.0.0] - 2026-09-25

A complete rewrite: the UI is now Jetpack Compose on a new design system, and the Morse
transmit/decode engine was rebuilt on a standard WPM timing model. versionCode 12.

### Added
- **Camera decoding** — point the camera at a flashing light, calibrate the ambient
  brightness once, and read the message. Adjustable sensitivity and a square detection region.
- **Adaptive decoder** — reads Morse at *any* sending speed by learning the dot/dash timing
  from the signal itself; no need to agree a WPM in advance. Ambiguous single taps are shown
  as both readings (e.g. `E/T`).
- **Hold-to-key** — tap out Morse by hand by holding the torch button (and a dedicated manual
  key on the Receive screen).
- **Practice** — a Decoding drill (copy a played message), a Sending drill (key prompted
  characters and words, with saved progress and a Random button), and a searchable
  reference chart of every letter and number.
- **Send extras** — Signal and SOS, a live per-symbol progress readout, key-tone sidetone,
  loop transmission, and keep-screen-awake.
- **Share app** action, plus in-app links to Rate and the source code.
- Project files: `LICENSE` (MIT) and `CONTRIBUTING.md`; fastlane setup for CLI releases.

### Changed
- **Timing** now follows ITU-R M.1677 with a real **words-per-minute** model (1–10 wpm),
  replacing the old 1–10 speed scale.
- **SOS** is sent as one unbroken prosign (`...---...`).
- **Redesign** to the "Personal UI" system: a signal-blue accent (was teal — a brand change),
  Plus Jakarta Sans + JetBrains Mono, light and dark themes.
- **Camera permission is deferred** — the flashlight works with no permission (via the system
  torch API); `CAMERA` is requested only when you open the camera-decode tab.
- Looping transmissions are separated by a full word gap so repeats decode cleanly.

### Fixed
- Manual sending is now possible (addresses "you can't manually tap Morse code").
- Decoder no longer misreads `O` as `TTT` when dots and dashes are both present.
- A transmission no longer keeps flashing the torch after leaving the screen or backgrounding
  the app (a battery drain).

### Removed
- The old Fragment/XML UI, the "User Details" screen, and the unused `WAKE_LOCK` permission.

### Technical
- Toolchain: Android Gradle Plugin 9.4.1, Gradle 9.7.1, Kotlin 2.2.10 (AGP built-in),
  Compose. minSdk 26, targetSdk 36. R8 minification + resource shrinking; 16 KB-aligned.
- Koin removed; SharedPreferences migrated to DataStore. Morse core is pure Kotlin, unit-tested.

## [3.0.0] - 2021

Last release of the original app (Fragments + XML UI, AGP 4.2 / Kotlin 1.5). Baseline for the
4.0.0 rewrite. versionCode 11.

[4.0.0]: https://github.com/ranjan-malav/MorseLight/releases/tag/v4.0.0
[3.0.0]: https://github.com/ranjan-malav/MorseLight/releases/tag/v3.0.0
