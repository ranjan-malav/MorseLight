# MorseLight

*Learn, send and decode Morse code with your phone's flashlight and camera.*

[Play Store](https://play.google.com/store/apps/details?id=com.ranjan.malav.morselight_flashlightwithmorsecode)

MorseLight turns a typed message into Morse code and transmits it with the flashlight, decodes
incoming light — by hand or with the camera — and teaches Morse through drills and a reference chart.

## Features

- **Send** — type a message and transmit it as Morse over the flashlight, with live per-symbol
  progress. Press-and-hold to key by hand. Signal (attention prosign) and SOS shortcuts.
- **Receive** — decode incoming Morse by tapping a key in time with the sender's light, or
  automatically with the camera (adjustable sensitivity and detection area).
- **Learn (More tab)** — a decoding drill, a sending drill, and a searchable A–Z / 0–9 reference
  chart, plus preferences (key tone, loop, keep-awake).

Timing follows **ITU-R M.1677** (one unit = 1200 / wpm ms).

## Tech

Rebuilt in 2026 from the 2021 codebase:

- **Jetpack Compose** UI on a custom design system (Material 3 + iOS control geometry, single
  signal-blue accent, light + dark), Plus Jakarta Sans + JetBrains Mono.
- **Kotlin** (AGP built-in), **AGP 9.4** / Gradle 9.7, **compileSdk 37 / targetSdk 36 / minSdk 26**.
- **CameraX** for the flashlight torch and camera luminance decoding.
- **DataStore** for settings, **Firebase** Analytics + Crashlytics.
- Morse encode/decode, WPM transmit engine and the keying classifier are pure Kotlin, unit-tested.

## Build

```bash
./gradlew :app:assembleDebug      # debug APK
./gradlew :app:testDebugUnitTest  # unit tests
```

Source is public. Contact: ranjan2192@gmail.com
