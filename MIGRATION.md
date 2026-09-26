# MorseLight — Modernization & Compose Migration Plan

**Status:** Phases 0–5 complete; Phase 6 (release prep) largely done; Phase 7 (polish) partial. The
app is a full Jetpack Compose redesign on the reworked WPM engine, **verified on a real device
(OnePlus 6, API 30)**, minified release building at **4.1 MB**, unit tests green (incl. the new
`AdaptiveDecoder` suite) + 1 Compose UI test, lint clean (5 intentional warnings). The engineering
work is essentially done — **all outstanding items are user-side / optional (see the Pending TODO
section below).**

 **Scope expanded 2026-09-20:** a full redesign (`design_handoff_morselight/`) now drives the UI, and the transmit/decode logic is being reworked to the handoff's cleaner engine (user request). See §7 Redesign.
**Started:** 2026-09-20
**Owner:** Ranjan Malav
**Goal:** Bring a 2021-era app (AGP 4.2 / Kotlin 1.5 / targetSdk 30 / XML + Fragments) up to a
currently Play-Store-compliant build, and rewrite the UI in Jetpack Compose.

> ### ✅ RESOLVED — release signing key
> **Play App Signing is enabled** — Play Console shows *"Protect app signing key · Releases signed by
> Play"* (confirmed 2026-09-20). Google holds the app signing key, so the keystore lost from the 2021
> machine was only the **upload key**, and its forgotten password is now irrelevant.
>
> Recovery is a routine **upload key reset** (steps in Phase 6). Existing users are unaffected: Google
> keeps re-signing every release with the same app signing key, so the on-device signature never changes
> and updates install over existing installs as normal.
>
> **Search record (2026-09-20).** A full sweep of the home directory found no MorseLight keystore. The
> only non-debug, non-unrelated-project candidate was `~/Downloads/bhojan_android_key.jks` — named for a
> different app, and it did not open with the remembered password (`malavR21`, `malavR21!`, or 10 case/
> punctuation variants; verified byte-exact via `-storepass:file`, so not a shell-quoting artifact).
> Also checked: `~/.gradle/gradle.properties` (no signing entries) and all 50 commits of git history
> (no keystore blob, no `signingConfig`/`storePassword`/`keyAlias` ever committed).
>
> Conclusion: the 2021 upload key is unrecoverable, and that is fine — the upload key reset makes it
> irrelevant. Do not spend more time hunting for it.

---

## Pending TODO

All engineering work is complete and pushed. Everything left is **user-side** (Play Console / device)
or **optional post-ship**. This is the single source of truth for what's outstanding; the phase
checklists below keep their original checkboxes as a historical record.

### User-side — needed before release
- [x] **Upload key reset** — completed; `keystore.properties` filled (§7.8).
- [x] **v13 (4.0.0) live on the internal track** — uploaded via the Play API with release notes, after
  stripping the advertising-ID permissions Play rejected (§7.8). Next upload needs **v14**.
- [ ] **Test v13 from the internal track, then promote to production**
  (`python3 scripts/play_upload.py promote 13 --to production --rollout 0.1`).
- [x] **Play listing refresh** — done via API (§7.8; pending Play review): new title *"Morselight - Morse
  with Flash"*, **store icon** (SOS mark), **feature graphic** (wordmark + "MORSE" transmission), and the
  8 new screenshots in the phone, 7" and 10" slots. Assets live in `fastlane/metadata/android/en-GB/images/`.
- [ ] **Confirm the Data Safety form** in the console (camera only in Receive→Camera; no advertising ID;
  Crashlytics + Analytics data collection).
- [ ] **Verify Firebase Crashlytics + Analytics** report from a **release-signed** build.
- [ ] **Real end-to-end flashing-light decode** — two phones: one sends, one reads with the camera
  (torch + sidetone + manual keying already verified on the OnePlus 6; the camera *preview image* and a
  live light-to-text decode are the last things worth a human eyeball).

### Optional — post-ship (Phase 8 + polish)
- [x] **Play releases from CLI — `scripts/play_upload.py`** (Play Developer API, `pip3 install --user -r
  scripts/requirements.txt`). Commands: `validate`, `status`, `upload [--track] [--draft] [--rollout]`,
  `promote <versionCode> --to production [--rollout 0.1]`; `--dry-run` has Play validate the edit and then
  discards it. Reads release notes from the fastlane `changelogs/<versionCode>.txt`. Verified: validate,
  status, and a production 10% promote dry run all pass. fastlane (`fastlane/`, `Gemfile`) remains wired
  but can't install locally (Homebrew wants Xcode 27). Data Safety + content rating stay console-only.
- [x] **LICENSE (MIT) + CONTRIBUTING.md + CHANGELOG.md** added.
- [ ] **Translations** — all UI strings are extracted to `strings.xml` (translation-ready); no non-English
  locales shipped yet.
- [ ] **Full TalkBack pass** — content descriptions + button semantics are on the discs/hold-pads; a
  complete pass over sliders and large-font layouts is still open.
- [ ] **Baseline Profile** for startup.
- [ ] **AGP 10 readiness** — confirm no old Variant API usage; keep built-in Kotlin enabled.
- [ ] **Hilt** — only if DI needs ever grow (D3 removed Koin; nothing replaces it for now).

---

## 1. Where the project stands today

| Area | Current | Problem |
|---|---|---|
| AGP | 4.2.2 | Ancient; won't build on modern JDK/Studio |
| Gradle wrapper | 6.7.1 | Incompatible with JDK 17+ |
| Kotlin | 1.5.10 | `kotlin-android-extensions` (synthetics) removed in Kotlin 1.9 |
| compileSdk / targetSdk | 30 / 30 | Play requires **API 36** for updates as of Aug 2026 |
| minSdk | 21 | Raises maintenance cost for ~0 users |
| Repositories | `jcenter()` | Shut down; builds will fail/hang |
| DI | Koin 2.2.2 (`@KoinApiExtension`) | API removed in Koin 3+ |
| UI | 15 XML layouts + Fragments + synthetics | To be replaced by Compose |
| Storage | `SharedPreferences` wrapper | Fine, but DataStore fits Compose state better |
| Firebase | BOM 28.2.1, `-ktx` artifacts | `-ktx` artifacts deprecated/merged |
| CameraX | 1.0.0 / `camera-view` 1.0.0-alpha26 | Alpha dependency in production |
| Manifest | `package=` attr, no `android:exported` | Hard build failure on AGP 8+ / API 31+ |
| Build type | `minifyEnabled false` | No shrinking; bigger APK, no obfuscation |

**Scale:** ~2,600 lines of Kotlin across 19 files, ~1,940 lines of layout XML across 15 files.
Small enough for a full rewrite rather than an incremental View-interop migration.

### Screen inventory (what has to exist in Compose at the end)

| Screen | Today | Notes |
|---|---|---|
| Send | `SendFragment` + `fragment_send.xml` | Message input, speed slider, SOS, signal, start/stop, live char + morse readout, press-and-hold torch |
| Receive → Manual | `ManualDecodeFragment` | Tap-and-hold timing capture, decode, reset, report |
| Receive → Auto | `AutoDecodeFragment` (408 lines, the hard one) | Camera preview, luminosity analysis, perceptibility slider, ROI size slider + guide rect, countdown, live decode |
| Learn | `LearnFragment` | Menu list → About / Tutorial / contact / rate / share / source / donate |
| Morse detail | `MorseDetailActivity` | Static reference image + link text |
| Morse tutorial | `MorseTutorialActivity` (271 lines) | Simulated flash playback + practice decoding |
| Info sheets ×4 | `InfoDialog` + 4 XML layouts | Bottom sheets, one per screen |
| Feedback dialog | `feedback_dialog.xml` | Confirm/report decode accuracy |

---

## 2. Target stack

All versions verified against Google Maven / Maven Central on **2026-09-20**.

| Component | Target version |
|---|---|
| Android Gradle Plugin | **9.4.1** |
| Gradle wrapper | **9.7.1** (AGP 9.4 requires ≥ 9.6.0) |
| JDK | **17** (AGP 9.4 minimum — installed: MS OpenJDK 17.0.17 ✅) |
| SDK Build Tools | **36.0.0+** (AGP 9.4 minimum — installed: 36.0.0 ✅) |
| Kotlin | **AGP built-in Kotlin** (no `kotlin-android` plugin — see §2.1) |
| Compose compiler | `org.jetbrains.kotlin.plugin.compose` — version must match AGP's bundled Kotlin |
| Compose BOM | **2026.09.00** |
| Material3 | via BOM (1.4.0) |
| compileSdk / targetSdk | **36** (AGP 9.4 supports up to API 37) |
| minSdk | **26** (Android 8.0) |
| Navigation Compose | 2.10.1 |
| Activity Compose | 1.13.0 |
| Lifecycle (ViewModel Compose) | 2.11.0 |
| CameraX | 1.6.2 (incl. `androidx.camera:camera-compose`) |
| Firebase BOM | 34.19.0 |
| google-services plugin | 4.5.0 |
| Crashlytics Gradle plugin | 3.0.8 |
| DataStore Preferences | 1.2.1 |
| DI framework | **none** — Koin removed |
| Build config | Version catalog (`gradle/libs.versions.toml`) + Kotlin DSL (`.gradle.kts`) |

### 2.1 AGP 9 specifics (verified from developer.android.com, 2026-09-20)

Going straight to AGP 9 from 4.2.2 means handling these on top of the normal upgrade:

- **Built-in Kotlin is on by default.** Drop `org.jetbrains.kotlin.android` / `kotlin-android` from the
  plugins block entirely, and drop `kotlin_version` + the `kotlin-gradle-plugin` classpath from the root
  build file. Escape hatch if it fights us: `android.builtInKotlin=false` in `gradle.properties`
  (deprecated; mandatory in AGP 10).
- **`android.kotlinOptions {}` → `kotlin { compilerOptions {} }`.** `jvmTarget` now defaults to
  `android.compileOptions.targetCompatibility`, so it usually doesn't need setting at all.
- **`kotlin-kapt` is incompatible with built-in Kotlin.** This project applies `kotlin-kapt` but has zero
  annotation processors — just delete the plugin. (If one is ever needed: KSP, or `com.android.legacy-kapt`.)
- **Compose compiler plugin version must line up with the Kotlin version AGP bundles.** Confirm the bundled
  version first (`./gradlew :app:dependencies` / build output) before pinning
  `org.jetbrains.kotlin.plugin.compose`. ⚠️ First thing to verify in Phase 1.
- **Old Variant API becomes mandatory-to-drop in AGP 10** (warning-only in 9.4). This project doesn't use it,
  so nothing to do — but don't introduce it. Opt-out if ever needed: `android.newDsl.optOut`.
- **Official upgrade tooling:** `android skills add agp-9-upgrade`, plus Studio's *Tools → AGP Upgrade
  Assistant*. Worth running the Assistant first and treating its output as a starting point.
- Max supported API level is 37, so compileSdk 36 is comfortably in range.

---

## 3. Decisions

| ID | Decision | Outcome | Status |
|---|---|---|---|
| **D1** | AGP 8.13.2 vs AGP 9.4.1 | **AGP 9.4.1.** Future-proofs the build rather than doing the 8→9 jump again in a year. Cost: AGP 9's breaking changes land on top of the migration — see §2.1. | ✅ Decided 2026-09-20 |
| **D2** | minSdk 21 → 24 or 26 | **26 (Android 8.0).** Cleanest modern baseline: full Java 8 APIs, no core library desugaring, and the `SDK_INT >= M` permission branch in `MainActivity` disappears. | ✅ Decided 2026-09-20 |
| **D3** | Keep Koin or drop DI framework | **Drop Koin.** It exists solely to inject one `SharedPreferenceUtils` singleton. Replaced by a `SettingsRepository` created in `Application`. Removes a dependency and its 2.x→4.x migration entirely. | ✅ Decided 2026-09-20 |
| **D4** | SharedPreferences → DataStore | **Yes.** `SettingsRepository` on DataStore with a one-time migration of the existing `speed`/`react_size`/`perceptibility` values. Flow API feeds Compose state. | ✅ Decided 2026-09-20 |
| **D5** | Keep portrait lock | Recommended: **drop `screenOrientation="portrait"`.** API 36 ignores orientation restrictions on large screens anyway; better to lay out responsively than be letterboxed. | Open |
| **D6** | Visual redesign scope | **Superseded by D7.** The earlier recommendation (keep teal, light reskin) is replaced by the `design_handoff_morselight/` redesign. | ⛔ Superseded 2026-09-20 |
| **D8** | Net-new features (drills, reference chart) | **Build everything in one release** — Phase 4 includes the two drills + reference chart before shipping. | ✅ Decided 2026-09-20 |
| **D7** | Adopt the Personal UI redesign handoff | **Yes.** Ground-up redesign on the *Personal UI* design system: single **signal-blue** accent `#1E5EFF` (replaces teal — a Play-Store brand change), **Plus Jakarta Sans** + **JetBrains Mono** (replaces Nunito Sans), Material-elevation + iOS control geometry, light+dark. Also adopt the handoff's **improved transmit/decode logic** (user request). See §7. | ✅ Decided 2026-09-20 |

---

## 7. Redesign — Personal UI handoff + logic rework (added 2026-09-20)

Source: `design_handoff_morselight/` (HTML design references — recreate faithfully in Compose,
do **not** port markup). Authoritative tokens: `_ds/personal-ui-design-system-*/tokens/*.css`.
Full screen specs + rationale: that folder's `README.md`. Screenshots: `screens/` (light+dark;
icons render as grey blocks — open `MorseLight.dc.html` for the real thing).

> ⚠️ Mockups are being revised (2026-09-20): **Settings is merging into the Learn screen.** Screen
> list below reflects the current handoff; treat Settings as a section of Learn once the update lands.

### 7.1 Design system (replaces the teal M3 theme built in Phase 3)
- [x] **Accent:** signal-blue `--accent` `#1E5EFF` (light) / `#5A85FF` (dark) — in `Color.kt` (raw
  palette) + `MorseColors.kt` (semantic roles, light+dark). **Brand change** from teal → update Play listing.
- **Surfaces:** `--bg-app`, `--surface-card`, `--surface-sunken`, `--surface-raised` (light→dark in
  `tokens/colors.css` + `dark.css`). Read-only output uses **sunken** cards, never bordered fields.
- **Status colors:** success `#0B8A5C`, warning `#B26A00`, danger `#C8304C`, info (system) violet.
- [x] **Type:** **Plus Jakarta Sans** + **JetBrains Mono** bundled as variable TTFs in `res/font`
  (`FontVariation` wght axis, API 26+); iOS ramp in `Type.kt`. Nunito kept until the old XML UI
  goes (Phase 5). ⏳ *Runtime font render validated at the first Compose screen (Phase 4); Roboto is
  the agreed fallback if the variable fonts misbehave.*
- [x] **Radii:** `Shape.kt` (`MorseRadius` + M3 `Shapes`). Motion tokens: `Motion.kt`.
- **Elevation / the halo+shadow effects the user called out** (`tokens/elevation.css`):
  soft cool-tinted `--shadow-1..4`; **`--shadow-accent`** blue glow under the filled primary; the
  **torch/key disc glow** `0 0 46px -6px var(--accent)` when lit; `--inset-field` inner shadow on
  inputs. Implement via Compose `Modifier.shadow`/`drawBehind` + a radial glow layer (M3 elevation
  alone won't give the colored halo). — [x] token values in `MorseColors.accentGlow`; a first
  `Modifier.accentGlow()` in `Glow.kt` (radial wash). Full multi-layer soft shadows land with the components.
- **Motion:** `--dur-fast 140 / base 220 / slow 320`, `--ease-standard cubic-bezier(.2,0,0,1)`;
  light on/off 50–70ms linear so keying feels instant; all collapse under reduced-motion.
- **Icons:** Lucide in the handoff → use **Material Symbols** equivalents at matching sizes.

### 7.2 The text-span color change the user called out
The **Morse string** colours **per symbol, three ways**, live while transmitting:
**sent = success**, **current = accent** (held through the off-gap so the element stays lit),
**pending = subtle**; idle = body colour. Compose: `buildAnnotatedString` with a `SpanStyle` per
symbol index, driven by `txIdx`/`txDone` from the transmit engine. Progress bar underneath is
repainted ~60×/s — **no opacity transition** (a re-triggered one never completes).

### 7.3 Logic rework (user requests: improve decoding + improve transmission)
The handoff's `MorseFrame.dc.html` logic class is correct and **replaces** the old, hand-written
logic. This supersedes the Phase-2 `MorseEncoder`/`MorseDecoder` that were kept faithful to the
**old** behaviour. New `morse/` domain (all pure/unit-testable, coroutine-driven — no `Handler`s):

- [x] **`MorseCode`** — done (6 tests). `encode(text)`: `word → per-char MAP → join(" ") → join(" / ")`, standard ITU
  string (`.... . .-.. .-.. --- / .-- ---`). `decode(morse)`: split on ` / ` then whitespace →
  reverse-map. Far simpler/robust than today's moving-average timing-cluster guess.
- [x] **`MorseTimeline`** (pure event list + `unitMillis(wpm)`) — done (6 tests). The coroutine **`TransmitEngine`** ticker that drives the torch from it is a thin Phase-4 wrapper (needs the torch controller + lifecycle). — **ITU-R M.1677**, `unit = 1200 / wpm` ms. Build an event list
  `{tUnits, on, symbolIndex}` (dot 1u, dash 3u, +1u intra-gap; char gap 3u, word gap 7u), tick at
  16 ms comparing elapsed units, emit `{torchOn, symbolIndex, doneIndex, pct}`. **Replaces the old
  speed 1–10 / (3/speed)s model** with a real WPM model (5–25 wpm, default 12). Optional loop +
  620 Hz sidetone.
- [x] **`KeyClassifier`** (manual key) — done (7 tests).  on down: gap since last release ≥6u → word ` / `, ≥2u → char
  ` `; on up: <2u → dot, else dash; idle timers commit a char sep at 3.5u and a word sep at 8u.
  Feeds `decode`. **Replaces** the fragile timing-difference clustering. **⚠️ Superseded on the receive
  path by `AdaptiveDecoder` (2026-09-22, §7.5)** — `KeyClassifier` now only backs the known-speed
  Decoding drill.
- **Camera decode** — per-frame **mean luminance inside the detection box**, thresholded by a
  **Sensitivity** slider; a crossing = a pulse fed to the decoder. **Replaces** the
  moving-average natural-break `DecoderUtils` clustering (audit B9). `LuminosityAnalyzer` cleanup
  (B5) folds in here. Detection-box size is user-adjustable, **moved out of the viewfinder**.
  (Now feeds the speed-agnostic `AdaptiveDecoder`, §7.5.)
- **Settings migration (confirmed):** old `speed` (1–10) → **wpm**, mapped to nearest (e.g. `wpm ≈ round(5 + (speed-1)*2)`, spanning ~5–23 wpm),
  keep `perceptibility`→sensitivity and `react_size`→detection-box. Add `keyTone`, `loop`,
  `keepAwake`. Extend `SettingsRepository`.
- New unit tests for `MorseCode` (encode/decode round-trip), `TransmitEngine` event list at several
  wpm, and `KeyClassifier` gap/duration classification.

### 7.4 Screens (per current handoff; Settings folding into Learn)
Main tabs **Send · Receive · More**; sub-screens use a back button (no tab bar). *(Mockup update
2026-09-20: Learn + Settings merged into one **More** tab — no separate Settings screen, no header
gear. Screenshots: `screens/04-more-*`.)*
| Screen | Status vs today | Notes |
|---|---|---|
| **Send** | redesign of existing | one torch disc (glow), message input, three-state morse card + progress, wpm slider, Signal/Send/SOS pills |
| **Receive — Manual key** | redesign | segmented Manual|Camera; decoded card + raw buffer; key disc (press-hold) |
| **Receive — Camera** | redesign + logic rework | viewfinder w/ detection box; **tuning card moved out of viewfinder** (Sensitivity, Detection area); decoded card |
| **More** (was Learn+Settings) | redesign; merged tab | progress card + amber donation banner, drill/chart links, Preferences switches (Key tone / Loop / Keep awake), Support links (Rate, Source), About card. Scrolls. |
| **Reference chart** | **net-new** | searchable A–Z/0–9 grid, tap plays; only scrolling screen |
| **Decoding drill** | **net-new** | play masked target, copy by hand, match check |
| **Sending drill** | **net-new** | prompt a character, key it, correct/advance |

**Net-new features** (drills, reference chart, sidetone, loop, keep-awake) are product surface
beyond a migration — sequencing is a scope decision (see the question raised to the user).

### 7.5 Post-device-test rework (2026-09-22, user-driven)
After the real-device pass, a round of usability + logic fixes (all committed in `e06d42a`, pushed):

- **Adaptive receive decoding (`morse/AdaptiveDecoder.kt`, +6 tests).** The receive path is now
  **speed-agnostic** — no WPM anywhere. It records raw mark/gap timings and clusters them from their
  own distribution (dash ≈ 3× dot), so the sender may key at any speed and the receiver just taps /
  the camera pulses along. A single cluster is genuinely ambiguous, so it shows **both readings**
  (one tap → `E/T`, two → `I/M`), resolving live as more of the message arrives. Feeds **both** manual
  keying and camera decode. `KeyClassifier` stays only for the known-speed Decoding drill.
- **Camera permission deferred.** Torch now runs through **`CameraManager.setTorchMode()`** (no CAMERA
  permission) — the flashlight works at launch with no prompt. `CAMERA` is requested **only when
  Receive→Camera is opened** (with an "Allow camera" card). `TorchController` split into permission-free
  torch vs the CameraX preview/luminance feed; `MainActivity` no longer requests at startup. Torch +
  deferral both verified on the OnePlus 6.
- **Camera receive UX.** The viewfinder now shows the **live preview** (was a dead placeholder); a manual
  **Calibrate** button measures and **locks** a fixed ambient average (no drift); current `lum` shows
  **green above the threshold / grey at rest** beside the frozen `avg`; **help bottom sheets** on the
  Sensitivity + Detection-area sliders.
- **Sending drill.** Adds **words** after A–Z/0–9 (keyed letter-by-letter with per-letter progress
  colouring), always shows the current letter's code, auto-resets a wrong attempt, adds a **Random**
  button (a detour that never moves saved progress), and **persists the resume position**
  (`SettingsRepository.sendingDrillIndex`).
- **Decoding drill.** Message is always visible, speed dropped to **2 wpm**, a **3-second big translucent
  countdown** overlay precedes playback, and the transmitted morse is shown with sent/current/pending
  progress colouring.
- **More screen.** Removed the placeholder progress card (no real progress model — dropped by choice);
  added a **coffee mark** to the donation banner.
- **Nav icons:** Receive → `Sensors`, More → `MoreHoriz`. **Launcher icon** replaced from the new brand
  mark (per-density adaptive background/foreground/monochrome + legacy PNGs; dedicated monochrome layer).
- **Verified on device:** keep-screen-awake applies the `FLAG_KEEP_SCREEN_ON` window flag; flashlight
  physically lights via `setTorchMode`; camera permission is asked only on the camera tab.

### 7.6 Layout + battery pass (2026-09-25, OnePlus 12 + OnePlus 6, committed `1eaa339`)
Round of layout/UX fixes after testing on a tall OnePlus 12 display and a battery report:

- **Receive-camera is fixed-height, no scroll.** The viewfinder takes the flexible space (`weight(1f)`
  + `heightIn(min = 120.dp)`) so it grows on tall screens and shrinks on short ones — the sliders below
  (esp. **Detection area**) are never pushed off screen. Decoded font reduced (titleLarge → titleMedium).
- **Detection region is a centred square** (`fillMaxHeight(pct).aspectRatio(1f)`), min size lowered
  **30 → 8%** for targeting a small/distant light. `LuminosityAnalyzer` now averages a matching centred
  **square** (side = area% of the frame's shorter side) and closes each `ImageProxy` in a `finally`
  (fixes the frame-leak, audit B5/B9).
- **Send** keeps the flexible `weight(1f)` middle (torch centred per the mockup); the space under the
  morse card absorbs the height difference.
- **One combined help sheet** (`ReceiveHelpSheet`: Calibrate + Sensitivity + Detection area). Every help
  icon opens it, plus a **help action in the app bar** on the Receive tab (state hoisted in `MorseApp`).
- **Loop transmission gap** fixed: a **7-unit word gap scaled to the wpm** between repeats (was a fixed
  800 ms that merged messages at low speeds and broke decoding).
- **Background battery fix:** transmission now **stops on `ON_STOP`** (app backgrounded / screen off), not
  just on navigation — a looping transmit was flashing the torch with the screen off and draining the
  battery (the coroutine lives in the ViewModel, which survives backgrounding).
- **Removed the unused `WAKE_LOCK` permission** — keep-awake uses `FLAG_KEEP_SCREEN_ON`, which needs none.

### 7.7 Loop test, CI green, issue #3 (2026-09-25, committed `42f1887` + `72ec896`)
On-device loop test (OnePlus 12R) + fixing the long-red CI + acting on the community issue:

- **Background torch drain — real fix.** Loop transmit kept flashing the torch **with the screen off**;
  the §7.6 `ON_STOP` guard used the NavHost's `LocalLifecycleOwner` (back-stack entry), whose `ON_STOP`
  didn't fire on backgrounding. Now observes the **Activity** lifecycle via `LocalActivity`. Verified on
  device: backgrounding mid-loop returns to Idle / torch off. Loop itself confirmed working (word-gap
  separates repeats).
- **CI was red for weeks — fixed.** The `build` job (unit + lint + assemble) passed; only the
  `instrumented` (API-33 emulator) job failed. Root cause: `ReferenceChartScreenTest` asserted a later
  letter (**S**) was displayed on first render, but in the `LazyVerticalGrid` it scrolls off the shorter
  emulator screen and is never composed (passed on the tall OnePlus 12R). Test now asserts **A** initially
  then **searches** to bring S on-screen (screen-independent). Also **upload the androidTest HTML report**
  as a CI artifact on failure so future breaks are diagnosable.
- **Issue #3 (community request) — mostly already addressed** by the rewrite: hold-to-key (press-and-hold
  torch + manual pad + Sending drill), **WPM** speed scaling, **SOS as one unbroken prosign** (`...---...`),
  the adaptive decoder resolving O-vs-TTT, "User Details" gone, help consolidated into **More** + the one
  Receive sheet. **Added** the still-missing pieces: **LICENSE** (MIT), **CONTRIBUTING.md**, and a
  **Share app** action in More → Support. Not added (by design / user-side): a toggle/steady-light mode,
  the Play-listing → repo link, a github.io page.

### 7.8 Release prep: signing, Play upload, keep-awake (2026-09-25/26)
- **Upload key reset completed.** `keystore.properties` filled (gitignored, verified untracked); signed
  `bundleRelease` verified (`jar verified`, signer = the new `morselight-upload` key). **v12 (4.0.0)
  uploaded manually to the internal track**; `versionCode` bumped to **13** for the next upload.
- **Release notes + changelog:** Play "What's new" in `fastlane/metadata/android/en-US/changelogs/{12,13}.txt`
  (485/500 chars); `CHANGELOG.md` (Keep a Changelog) documents the 4.0.0 rewrite.
- **Native debug symbols:** enabled `ndk { debugSymbolLevel = "FULL" }`, but the app has no native code
  and the bundled CameraX/DataStore `.so` files ship **pre-stripped** (verified `stripped … no symbols`),
  so nothing is extracted — Play's "no debug symbols" notice is expected and harmless.
- **Play CLI:** Gradle Play Publisher **doesn't support AGP 9** (needs the removed
  `BaseAppModuleExtension`), so **fastlane `supply`** is wired instead (`fastlane/Appfile` + `Fastfile`).
  fastlane itself **can't install locally**: Homebrew requires **Xcode 27** (machine has 26.5), and the
  system Ruby 2.6 can't build its native gems. The **service account is validated** directly against the
  Play API from Python (opened + aborted an edit; read the internal track) — credentials and app
  permissions work. Uploading can go via that Python path or fastlane in CI.
- **Keep-screen-awake was app-wide — fixed.** `FLAG_KEEP_SCREEN_ON` was set on the whole activity
  whenever the preference was on, so the screen never slept while the app was open (verified: set even
  idle on More). Now per-screen via a `KeepScreenOn` composable: **Send** only while transmitting,
  **Receive** the whole time that screen is open, everything else times out normally. First version
  dropped on Receive because navigating Send → Receive let Send's `onDispose` clear the shared root-view
  flag; now **reference-counted** per View. Verified on device. `View.keepScreenOn` exists since API 1,
  so it behaves the same across the whole minSdk 26+ range.
- **Advertising-ID permissions stripped (Play rejected the first v13 upload).** Firebase Analytics
  (`play-services-measurement`) merges in `com.google.android.gms.permission.AD_ID`,
  `ACCESS_ADSERVICES_ATTRIBUTION` and `ACCESS_ADSERVICES_AD_ID`, contradicting the Play Console declaration
  that the app doesn't use the advertising ID (it's ad-free). Removed with `tools:node="remove"` in the
  manifest and set `google_analytics_adid_collection_enabled=false`. Analytics + Crashlytics unaffected;
  only ad attribution dropped. The Data Safety answer ("no advertising ID") now matches the binary.
  The manually uploaded v12 still carries those permissions; v13 supersedes it on the internal track.
- **v13 uploaded to the internal track via the Play API** (Python, same validated service account):
  `edits.insert` → `bundles.upload` → `tracks.update(internal, status=completed, en-US release notes)` →
  `commit`. Internal track now serves **13 (4.0.0)**. The rejected attempt's edit was aborted, so
  versionCode 13 wasn't consumed.
- **Store listing refreshed via the API** (listing language is **en-GB**). Title → *"Morselight - Morse
  with Flash"* (was "MorseLight - Learn Morse Code with Flashlight 2021"). The 2021 screenshots in the
  phone, 7" and 10" slots were replaced by 8 new ones (Send transmitting/idle, Receive camera reading +
  calibrate, manual key, both drills, reference chart; mixed light/dark). Source captures were 1440×3168
  (2.2:1, over Play's 2:1 limit), so each had the status bar cropped (also hides the real clock/battery),
  the 1dp nav-bar edge border trimmed, and the sides extended with edge pixels → 1512×3007. Saved under
  `fastlane/metadata/android/en-GB/images/phoneScreenshots/` (tablet slots reuse the same files).
  **Store icon** replaced with `~/Downloads/icons/play-store-512.png` (512×512 RGBA, opaque full-bleed
  so Play's mask rounds it cleanly), saved as `images/icon.png`; checksum on Play matches the file.
  **Feature graphic** replaced with `feature-2c-transmission.png` (1024×500; the MorseLight wordmark over
  "MORSE" in sent/pending colours). The source had an unused alpha channel (every pixel opaque), which
  Play rejects, so it was flattened to 24-bit RGB losslessly; saved as `images/featureGraphic.png`.
- **Release notes now per language.** `play_upload.py` sends every `<language>/changelogs/<vc>.txt`;
  added `en-GB` copies (the listing's language) alongside `en-US`, and re-published v13 on internal so
  it carries both.

## 4. Phases

Each phase should end on a **green build + working app**, and get its own commit.

### Phase 0 — Safety net ✅ (2026-09-20)
- [x] Create branch `migration/modernize-compose`
- [x] Tag `v3.0.0-legacy` at `4359d23` (annotated, records the pre-migration stack).
      Note: a `v3.0.0` tag already existed at `076ab88`, one commit earlier — that commit is a README
      edit, so `v3.0.0` already covered the shipped *code*. `v3.0.0-legacy` marks the exact tree.
- [x] Recorded shipped state: **versionCode 11, versionName 3.0.0**, AGP 4.2.2 / Gradle 6.7.1 /
      Kotlin 1.5.10 / compile+targetSdk 30 / minSdk 21
- [x] `.gitignore` rewritten: added `.kotlin/`, `*.hprof`, `*.apk`, `*.aab`, and pre-emptive
      `*.jks` / `*.keystore` / `keystore.properties` rules ahead of the Phase 6 signing config.
      Removed redundant `/.idea/*` sub-entries and the duplicate `local.properties`.
      Verified no already-tracked file became ignored.
- [x] Confirmed clean: `.idea/` and `local.properties` are untracked. `app/google-services.json`
      *is* tracked — normal for Firebase, left as-is.
- [x] Release keystore investigated: not on this machine or in git history, but **Play App Signing is
      enabled**, so it was only the upload key. Replaced via upload key reset in Phase 6 — no longer a
      blocker for any phase.

### Phase 1 — Make it build again (no UI changes)
Goal: same app, same XML UI, modern toolchain. Biggest-risk phase, because of AGP 9 (§2.1).

**Toolchain**
- [x] Toolchain migrated to AGP 9.4.1 — **done by hand** (the AGP Upgrade Assistant / `agp-9-upgrade` skill was not used; the manual path reached the same end state and is what the rest of Phase 1 documents).
- [x] Gradle wrapper → 9.7.1 (`gradle-wrapper.properties` + regenerate the wrapper jar)
- [x] Root `build.gradle` → `build.gradle.kts`; delete `buildscript{}`, `jcenter()`, `allprojects{}`, `kotlin_version`
- [x] `settings.gradle` → `settings.gradle.kts` with `pluginManagement` + `dependencyResolutionManagement` (`google()`, `mavenCentral()`)
- [x] Add `gradle/libs.versions.toml` version catalog
- [x] `app/build.gradle` → `.gradle.kts`, AGP 9.4.1
- [x] **Confirm the Kotlin version AGP 9.4.1 bundles** before pinning the Compose compiler plugin
- [x] Remove `kotlin-android` (built-in Kotlin) and `kotlin-kapt` (unused) from the plugins block
- [x] Remove `kotlin-android-extensions`
- [x] `kotlinOptions {}` → `kotlin { compilerOptions {} }`
- [x] `gradle.properties`: drop `android.enableJetifier`, add `org.gradle.caching`/`org.gradle.parallel`, raise `jvmargs`

**Manifest / SDK**
- [x] Move `package=` out of `AndroidManifest.xml` → `namespace` in `app/build.gradle.kts`
- [x] Add `android:exported="true"` to `MainActivity` (required, API 31+)
- [x] compileSdk 36 / targetSdk 36 / **minSdk 26**; drop `buildToolsVersion`
- [x] Delete the now-dead `SDK_INT >= M` branch in `MainActivity.onCreate` (D2)

**Dependencies**
- [x] **Rip out Koin** (D3): delete `startKoin`/`appModule` from `MorseLightApp`, `KoinComponent`/`by inject()`/`@KoinApiExtension` from `SendFragment`, `ManualDecodeFragment`, `AutoDecodeFragment`, `DecodePagerAdapter`, `MainActivity`. Expose `SharedPreferenceUtils` from the `Application` for now; it becomes `SettingsRepository` in Phase 2.
- [x] Bump AndroidX / Material / ConstraintLayout / Navigation / Fragment to current
- [x] CameraX alpha `camera-view` → stable 1.6.2
- [x] Firebase BOM 34.19.0; `firebase-analytics-ktx`/`firebase-crashlytics-ktx` → `firebase-analytics`/`firebase-crashlytics`
- [x] google-services 4.5.0, Crashlytics Gradle 3.0.8

**Code**
- [x] **Replace Kotlin synthetics with ViewBinding** in all 8 files that use them. Unavoidable even though these views die in Phase 5 — synthetics don't exist in modern Kotlin.
- [x] Fix deprecations: `onBackPressed()` → `OnBackPressedDispatcher`, `LiveData.observe(owner) {}` 2-arg lambda form, `requestPermissions`/`onRequestPermissionsResult` → `ActivityResultContracts`
- [x] **Verify:** `./gradlew :app:assembleDebug` green; `:app:testDebugUnitTest` green. APK: 14 MB, minSdk 26 / targetSdk 36 / compileSdk 37.
- [x] **Emulator smoke test passed (API 33, arm64):** app installs and launches, no crashes in logcat. Verified Send (typed message → correct Morse output `.... . .-.. .-.. ... --- ...`, live "H = ...." char readout, START/SOS → STOP state machine), bottom-nav to Receive and Learn, camera-permission grant, and DataStore default read (speed slider at default 3). ⏳ **Real-device test still pending (user will run).** Torch output itself can't be verified on an emulator.

**Phase 1 outcome (2026-09-20):**
- Toolchain jumped 4.2.2 → **AGP 9.4.1**, Gradle 6.7.1 → **9.7.1**, Kotlin 1.5.10 → **2.2.10 (AGP built-in)**. `kotlin-android`, `kotlin-kapt`, `kotlin-android-extensions` all removed.
- Build converted to Kotlin DSL + version catalog (`gradle/libs.versions.toml`). Repos moved to `settings.gradle.kts` (jcenter gone).
- Manifest: `package` → `namespace`, `android:exported="true"` on launcher activity, dead `SDK_INT >= M` branch removed (minSdk 26).
- Koin fully removed (6 files); `SharedPreferenceUtils` now constructed directly.
- **Synthetics → ViewBinding** across 9 files; `InfoDialog` uses `findViewById` (dynamic layout). Bugs B3 (nullable `icon`) fixed in passing.
- Firebase `-ktx` imports → merged packages (BOM 34). CameraX alpha → 1.6.2. Test deps added (were never declared).

**Deviations from the plan, and why:**
- **compileSdk 36 → 37.** `androidx.core:core-ktx:1.19.0` (and current AndroidX) require compileSdk 37. AGP 9.4 supports up to API 37; installed `platforms;android-37.0`. **targetSdk stays 36** (Play's requirement) — compileSdk and targetSdk are independent.
- **`nonTransitiveRClass=false`** (not the AGP 9 default `true`). The temporary XML/Views reference library attrs via the app R (`colorOnBackground`, `backgroundColor`), which non-transitive R breaks. Flip back to `true` in Phase 5 once Views are gone.
- **`java.net.preferIPv4Stack=true`** added to `org.gradle.jvmargs` — the JVM's IPv6 route to the Gradle CDN / Maven timed out on this machine while IPv4 (curl) worked. Harmless to keep; revisit if it ever matters.
- Deprecation warning left: `Preview.Builder.setTargetAspectRatio` — belongs to the Phase 4 CameraX rewrite.
- **ActivityResult permissions migration deferred to Phase 3/4**, when `MainActivity` becomes a `ComponentActivity`. The deprecated `requestPermissions`/`onRequestPermissionsResult` still compile (warnings only), so this does not block a green build.

### Phase 2 — Extract the domain layer (UI-independent) — morse core + DataStore done; Torch/Luminosity moved to Phase 4
Goal: pull all logic out of Activities/Fragments so Compose screens are thin.
- [x] `morse/MorseTables.kt` — tables moved out of `Extensions.kt` (now immutable `val`s).
- [x] `morse/MorseEncoder.kt` — the 4× duplicated encode block (B1) collapsed into `MorseEncoder.encode()` returning `Transmission(onOffDelays, charUnits, morseCode, finalOffDelay)`. All 4 callers delegate; behaviour verified identical by tests.
- [x] `morse/MorseDecoder.kt` — `DecoderUtils` ported; dead debug scaffolding removed (B8); stray synthetic import already gone in Phase 1.
- [x] `torch/TorchController.kt` — **deferred to Phase 4.** The torch/handler timeline is entangled with `MainActivity` becoming a `ComponentActivity`; cleaner to do once during the Compose rewrite than twice.
- [x] `camera/LuminosityAnalyzer.kt` — **deferred to Phase 4** (B5). Best changed and exercised alongside the Auto-decode camera rewrite, on-device.
- [x] `data/SettingsRepository.kt` — DataStore-backed, exposes `Flow<Settings>` + suspend setters. One-time `SharedPreferencesMigration` imports the existing `speed`/`react_size`/`perceptibility` values from the old prefs file, then deletes it. `SharedPreferenceUtils` removed. Legacy fragments read via a documented temporary `runBlocking` bridge (replaced by Flow collection in the Phase 4 ViewModels); writes go through `lifecycleScope`.
- [x] **Unit tests**: `MorseEncoderTest` (5) + `MorseDecoderTest` (5), all passing. Test deps added in Phase 1 (never previously declared).
- [x] **Verify:** `:app:assembleDebug` + `:app:testDebugUnitTest` green (11 tests, 0 failures). On-device check folded into the pending Phase 1 smoke test.

### Phase 3 — Design-system foundation — Compose enabled; theme now RE-BASED on Personal UI (§7)
> **Rework:** the teal M3 theme built here is replaced by the Personal UI tokens (signal-blue, Plus
> Jakarta Sans + JetBrains Mono, elevation/glow, radii, motion) and a reusable component set
> (TorchDisc w/ halo, three-state MorseString, sunken Card, pill Buttons, SegmentedControl, Slider,
> Badge, ProgressRing, GroupedList, ViewfinderBox). See §7.1–7.2.
- [x] `buildFeatures { compose = true }`; Compose BOM 2026.09.00 + `org.jetbrains.kotlin.plugin.compose` (pinned to AGP's built-in Kotlin 2.2.10). ViewBinding kept alongside until Phase 5.
- [x] `ui/theme/` — Color.kt (teal palette), Type.kt (Nunito Sans `FontFamily` from res/font), Theme.kt (M3 light+dark, dynamic color off). A `@Preview` proves it compiles and renders.
- [x] `MainActivity` → `ComponentActivity` + `setContent {}` + `enableEdgeToEdge()` — **moved to the start of Phase 4.** Doing it now would replace the working fragment UI with empty shells (and can't be smoke-tested here); it lands with the first real screen.
- [x] Navigation Compose bottom bar (Send/Receive/Learn) + nested Detail/Tutorial routes — **Phase 4 start**, with the MainActivity conversion.
- [x] Shared components: `TorchStatusIndicator`, `LabelledContainer` (replaces the custom View), `MenuRow` (replaces `AccountOptionView`), `SpeedSlider`, `MorseReadout`
- [x] **Verify (foundation):** `:app:assembleDebug` green with Compose enabled; theme + preview compile. App still runs the fragment UI (nav-shell verification happens in Phase 4).

### Phase 4 — Screen-by-screen port ✅ (2026-09-20) → all §7.4 redesign screens live
Start with MainActivity→ComponentActivity + nav shell, then port one screen at a time (Learn/Send
first), deleting the Fragment + XML as each lands. Net-new screens (drills, chart) per §7.4 scope decision.
- [x] **Send** — `SendViewModel` + `SendScreen`; press-and-hold torch via `pointerInput`/`detectTapGestures`
- [x] **Learn** — simplest screen, good warm-up; keep the ko-fi/GitHub/rate/share/mail intents
- [x] **Morse detail** — image + linkified text
- [x] **Receive / Manual** — `ManualDecodeViewModel` + `ManualDecodeScreen`; tab host via `PrimaryTabRow` + `HorizontalPager` (replaces `ViewPager2` + `DecodePagerAdapter`)
- [x] **Receive / Auto** — the hard one. `CameraXViewfinder` (`androidx.camera:camera-compose`) or `AndroidView(PreviewView)`; ROI rect drawn with `Canvas` instead of 4 `ConstraintLayout` guidelines; luminosity → `StateFlow` in the ViewModel
- [x] **Morse tutorial** — coroutine-driven simulated playback replaces the nested `postDelayed` chain
- [x] **Info sheets** — one `ModalBottomSheet` + a content model; replaces `InfoDialog` and its 4 layout files
- [x] **Feedback dialog** — `AlertDialog` composable
- [x] Camera permission via `rememberLauncherForActivityResult` + rationale path
- [x] **Verify after each screen:** build green, screen exercised on device

### Phase 5 — Delete the old world ✅ (2026-09-20)
- [x] Remove all 15 layout XMLs, `navigation/mobile_navigation.xml`, `menu/*.xml`
- [x] Remove `FragmentCallbacks`, `DecodePagerAdapter`, `InfoDialog`, all Fragments, `AccountOptionView`, `LabelledContainer`, `Extensions.kt` View helpers, `attrs.xml`, `styles.xml`, `theme_attributes.xml`
- [x] Trim `themes.xml` to a launcher-only theme (`Theme.MorseLight.Starter`) + splash screen
- [x] Drop `com.google.android.material:material`, `constraintlayout`, `navigation-fragment-ktx`, `navigation-ui-ktx`, `fragment-ktx`, `viewpager2` if unreferenced
- [x] Remove ViewBinding once nothing uses it
- [x] Prune unused drawables/strings/dimens (`lint` → `UnusedResources`)

### Phase 6 — Play Store readiness — ✅ except user-side (keystore, listing)
- [x] `targetSdk = 36`, `versionCode = 12`, `versionName = "4.0.0"`
- [x] **Edge-to-edge**: targetSdk 35+ forces it — audit every screen for content under the status/nav bars; apply `WindowInsets` padding
- [x] **Predictive back**: `android:enableOnBackInvokedCallback="true"` + verify nav behaviour
- [x] **16 KB page size** compliance (required since Nov 2025) — verify no bundled `.so` breaks it (`zipalign -c -P 16 -v`); CameraX/Firebase should already be compliant
- [~] **Upload key reset** — the 2021 upload key is lost, but Play App Signing is on, so this is routine.
      **Request submitted 2026-09-21; awaiting Google (~1–2 business days).**
  - [x] `keytool -genkeypair` — new `morselight-upload.jks` generated (gitignored).
  - [x] `keytool -export -rfc … -file upload_certificate.pem` — cert exported (gitignored).
  - [x] Play Console → App integrity → App signing → **Request upload key reset** with the PEM — **submitted**.
  - [ ] On approval: fill gitignored `keystore.properties` from the template so release signs automatically.
  - [x] **Backed up `morselight-upload.jks` + its password** (password manager + off-machine) — 2026-09-21. The 2021 failure mode is now covered.
- [x] Wire `signingConfigs` to read path/passwords from a gitignored `keystore.properties` (or env vars) — never committed; `.gitignore` already blocks `*.jks`, `*.keystore`, `keystore.properties`
- [x] Note: SHA-1-keyed services bind to the *app signing* key, which is unchanged, so Firebase needs no reconfiguration
- [x] `minifyEnabled true` + `shrinkResources true`; write ProGuard keep rules (Firebase, CameraX, any reflective Compose usage) and **test the release build end-to-end** — the current `proguard-rules.pro` is untouched boilerplate
- [x] Build an **App Bundle** (`bundleRelease`) — builds (7.4 MB `.aab`, splits per-device on Play). Internal-app-sharing upload is user-side.
- [x] Remove the portrait lock (D5); verify tablet/foldable layout
- [ ] Play Console: confirm Data Safety form still matches (camera permission, Crashlytics + Analytics data collection), refresh screenshots for the new UI
- [ ] Verify Firebase Crashlytics + Analytics still report from a release build

### Phase 7 — Polish — partial (accessibility on hold pads, README, CI done)
- [x] Compose previews for each screen (light + dark)
- [~] TalkBack pass — partial: content descriptions on the torch/key discs and button semantics on the hold pads. Full pass (sliders, large-font) still open.
- [x] Large-font sanity check — verified at 1.3× font scale (More screen: larger text, nothing clipped or overlapping).
- [x] Update `README.md` for the new stack
- [x] GitHub Actions: build + unit tests on PR

### Phase 8 — Optional, post-ship
- [ ] AGP 10 readiness: confirm no old Variant API usage, keep built-in Kotlin enabled
- [ ] Baseline Profile for startup
- [ ] Consider Hilt if DI needs ever grow (D3 removed Koin; nothing replaces it for now)

---

## 5. Bugs & smells found during the audit

Worth fixing while rewriting — not blockers, but easy wins once the code is in one place.

| # | Where | Issue |
|---|---|---|
| B1 | `SendFragment`, `ManualDecodeFragment`, `AutoDecodeFragment`, `MorseTutorialActivity` | The morse-encoding block is copy-pasted 4× with small divergences |
| B2 | `AutoDecodeFragment` | Imports synthetics from **both** `fragment_auto_decode` and `fragment_manual_decode` — resolves to whichever ID matches first; latent wrong-view bug |
| B3 | `MainActivity.onPrepareOptionsMenu` | `item.icon.alpha` — `icon` is platform-nullable, NPE risk |
| B4 | `MainActivity.releaseWakeLock` | Swallows every exception silently |
| B5 | `LuminosityAnalyzer.analyze` | Boxes the entire frame into a `List<Int>` per frame; also `image.close()` is not in a `finally`, so an exception leaks the `ImageProxy` and stalls analysis |
| B6 | `MainActivity.startCamera` | `cam!!` double-bang right after a nullable assignment |
| B7 | `ManualDecodeFragment.decode_button` | Sets `incoming_message.text` from the dot/dash branch, then unconditionally overwrites it on the next line — dead branch |
| B8 | `DecoderUtils` | Builds `percentageDiffs` + a `StringBuilder` of them that is never used (leftover debug code) |
| B9 | `DecoderUtils.findMorseFromTimings` | `!!` on all four map lookups; classifies by `contains(diff)` on the timing lists, so duplicate timings across buckets are ambiguous |
| B10 | `MorseTutorialActivity` | `removeHandlerCallbacks()` never clears `handler2`, so the cleanup runnable can fire after reset |
| B11 | `Extensions.kt` | `String.format` without a `Locale` (lint `ConstantLocale`); `contactMail` hardcodes the dev email in source |
| B12 | `MainActivity` | 3 separate `Handler`s posting the same runnable instances repeatedly — a runnable posted twice to the same handler can't be individually cancelled |

---

## 6. Progress log

| Date | Phase | Notes |
|---|---|---|
| 2026-09-20 | — | Audit complete; plan written. Nothing implemented yet. |
| 2026-09-20 | — | D1/D2/D3 decided: AGP 9.4.1, minSdk 26, Koin dropped. AGP 9 requirements verified (Gradle ≥ 9.6.0, JDK ≥ 17, Build Tools ≥ 36.0.0, max API 37) — local toolchain already satisfies all of them. §2.1 added. |
| 2026-09-20 | 0 | **Phase 0 complete.** Branch `migration/modernize-compose` created, `v3.0.0-legacy` tagged at `4359d23`, `.gitignore` modernized. |
| 2026-09-20 | 0 | ⚠️ Release keystore not found anywhere on this machine or in git history. Shipping blocker raised; awaiting Play App Signing status from Play Console. |
| 2026-09-20 | 0 | ✅ Signing blocker resolved: Play App Signing confirmed enabled ("Releases signed by Play"). Lost key was the upload key only; replaced via upload key reset in Phase 6. No phase is blocked. |
| 2026-09-20 | 0 | Keystore hunt closed. Full home sweep found no MorseLight key; `~/Downloads/bhojan_android_key.jks` (different app) rejected every remembered password. Proceeding with the upload key reset. |
| 2026-09-20 | 1 | **Phase 1 build green.** AGP 9.4.1 / Gradle 9.7.1 / Kotlin 2.2.10 built-in; Koin removed; synthetics → ViewBinding (9 files); Firebase BOM 34. Deviations: compileSdk 37 (targetSdk still 36), nonTransitiveRClass=false, IPv4 forced for downloads. On-device smoke test still pending. |
| 2026-09-20 | 2 | **Morse domain extracted.** New `morse/` package: `MorseTables`, `MorseEncoder` (dedupes B1 across 4 files), `MorseDecoder` (ex-`DecoderUtils`, B8 removed). 10 new unit tests pass; build green. Deferred: `SettingsRepository` (blocked on D4), `TorchController` + `LuminosityAnalyzer` (moved to Phase 4, done best on-device during the camera rewrite). |
| 2026-09-20 | 3 | **Compose foundation (non-breaking half).** Enabled Compose (BOM 2026.09.00, compiler plugin on Kotlin 2.2.10), added `ui/theme` (teal M3 light+dark, Nunito Sans) with a compiling `@Preview`. Build green; fragment UI still live. MainActivity→ComponentActivity + nav shell deferred to Phase 4 start so the app is never left as empty shells. |
| 2026-09-20 | 2 | **DataStore done (D4).** `SettingsRepository` on DataStore with one-time SharedPreferences migration; `SharedPreferenceUtils` removed; 3 fragments rewired (temporary runBlocking bridge). Build green. |
| 2026-09-20 | 1–3 | **Emulator smoke test passed** (API 33). Send encode + live char readout + transmit state machine correct; nav to Receive/Learn works; DataStore defaults read; no crashes. Real-device test deferred to user. |
| 2026-09-20 | plan | **Redesign folded into roadmap (§7).** `design_handoff_morselight/` adopted: Personal UI design system (signal-blue, Plus Jakarta Sans + JetBrains Mono, elevation/glow, radii), three-state morse coloring, and a reworked transmit/decode engine (WPM/ITU-R M.1677, gap-based keying, luminance camera decode) that replaces the old hand-written logic per user request. D7 added; D6 superseded. Phase 3 theme to be re-based; Phase 4 retargeted to the new screens. Settings merging into Learn (mockups being revised). |
| 2026-09-20 | 2b | **Logic rework core built + tested.** New `MorseCode` (standard-form encode/decode), `MorseTimeline` (unit-based ITU event list + `unitMillis(wpm)`), `KeyClassifier` (gap/duration keying) — 18 new unit tests, all green; app still assembles. These replace the old hand-written transmit/decode logic (user request); the coroutine TransmitEngine ticker + camera luminance decode land in Phase 4 with the torch controller. Old MorseEncoder/Decoder retained until the old UI is deleted. |
| 2026-09-20 | 3 | **Design-system foundation done.** Replaced the teal M3 theme with Personal UI tokens: `Color.kt` (raw palette), `MorseColors.kt` (semantic roles light+dark via `LocalMorseColors`/`MorseTheme`), `Type.kt` (Plus Jakarta Sans + JetBrains Mono variable fonts + iOS ramp), `Shape.kt`, `Motion.kt`, `Glow.kt` (accent halo), and a preview exercising three-state morse colouring. Fonts bundled + packaged in the APK; build green. Also folded in the mockup update: Learn+Settings → one **More** tab. |
| 2026-09-20 | 4 | **App switched to Compose (major milestone).** New Compose `MainActivity` (`ComponentActivity`, edge-to-edge, camera-permission) + `MorseApp` nav shell (Send/Receive/More tabs + Chart/drill routes). `TorchController` (CameraX torch + luminance) and `TransmitEngine` (coroutine ticker) wired. Screens: Send (full transmit — verified three-state colouring, torch halo glow, WPM slider, progress), Receive (manual keying via KeyClassifier; camera luminance decode + tuning), More (merged Learn+Settings: grouped list, preference switches, support, about), Reference chart (real). `SettingsRepository` moved to the WPM schema with speed→wpm derived at read time. Old fragment/activity UI + ViewBinding deleted; manifest trimmed. Emulator: all three tabs render + navigate, transmit works, no crashes. Deferred to next increment: live CameraX PreviewView, the two drills (placeholders now), Phase 5 layout/resource cleanup. |
| 2026-09-20 | 4 | **Camera preview + drills + first polish.** Live CameraX `PreviewView` in Receive-camera (verified: luminance stream `lum 82`, detection box, tuning sliders). Decoding drill (masked target, mocked sender disc via TransmitEngine, hold-to-copy) and Sending drill (prompt char, hold-to-key, correct/advance) — both real, on the tested engine/classifier; placeholder removed. UI-fidelity pass 1: left-aligned appbar (eyebrow+title), tab-bar top hairline, and the More screen's accent progress card + amber donation banner. All verified in portrait on the emulator, no crashes. |
| 2026-09-20 | 5 | **Phase 5 cleanup.** Deleted the old world: all 15 layout XMLs, menu/, navigation/, attrs.xml, styles.xml, theme_attributes.xml, ~20 old drawables, all Nunito fonts, and the superseded MorseEncoder/MorseDecoder + their tests. Slimmed Extensions.kt to the two intents MoreScreen uses. Rewrote themes.xml to a minimal Compose launcher theme (Material3 DayNight) with a DayNight window background. Removed 5 fragment-era deps (constraintlayout, navigation-fragment/ui-ktx, fragment-ktx, viewpager2). Build + tests green; app runs clean on emulator. |
| 2026-09-20 | 6 | **Phase 6 (release prep, partial).** versionCode 12 / versionName 4.0.0. Enabled R8 minify + resource shrinking with keep rules (Crashlytics line numbers, Firebase, CameraX, coroutines, data classes); `enableOnBackInvokedCallback` for predictive back. Verified the minified release: **4.1 MB** (debug 30 MB), signed with the debug key for testing — installs and renders identically, no R8 runtime breakage. Still pending: upload key reset (blocker resolved, action on user), real signing config, 16 KB .so audit, Play listing/screenshots for the new brand. |
| 2026-09-20 | 4 | **Wired the three preference switches** (were persisted but inert). Key tone: 620 Hz `Sidetone` (AudioTrack sine) played while the light is on during transmit + manual keying, gated by the setting. Loop transmission: SendViewModel repeats the message (800 ms gap) while enabled. Keep screen awake: MainActivity toggles `FLAG_KEEP_SCREEN_ON` from the setting. Build green; verified on emulator (transmit + sidetone, no AudioTrack errors, no crash). |
| 2026-09-20 | 7 | **Lint cleanup.** Trimmed the leftover teal `colors.xml` (→ just `window_background`), `strings.xml` (→ the 3 referenced strings), deleted unused `dimens.xml`, and fixed 3 `UseKtx` warnings (`Uri.parse` → `toUri`). Lint down from **164 warnings to 5** (remaining are intentional: targetSdk 36, predictive-back attr, a dependency-update notice). |
| 2026-09-20 | 6-7 | **Release-readiness checks.** Clean build green: 20 unit tests + 1 Compose UI test, debug + minified release + lint. Release APK 4.1 MB. 16 KB page-size verification passed on all native libs. About card shows the version from BuildConfig (4.0.0). New signal-blue adaptive launcher icon. CI gained an instrumented-test job. Remaining is user-side: upload-key reset, real-device pass, and the Play listing refresh for the rebrand. |
| 2026-09-20 | 7 | **All 7 screens verified end-to-end on the emulator** (Send transmit w/ three-state + glow, Receive manual + camera luminance, More, Reference chart, both drills incl. drill playback engine). Accessibility: button semantics on the hold pads. Final clean build green: 20 unit tests + 1 UI test, debug + release + lint. **Migration is feature-complete and release-ready pending the user's upload-key reset, real-device pass, and Play listing refresh.** |
| 2026-09-20 | 7 | **UI layer made fully previewable/testable.** All 7 screens refactored to stateless `XxxContent(state, callbacks)` + thin `XxxScreen(vm)` wrappers, each with a `@Preview` (Receive uses a `cameraContent` slot so previews skip CameraX). Added 4 morse integration tests (encode→timeline, keying→decode). Fixed lint to 3 intentional warnings. **Final state: 28 commits, 24 unit + 1 UI test, clean debug + 4.1 MB release build, all verified on the emulator.** Migration complete; remaining is user-side (upload-key reset, real-device pass, Play listing). |
| 2026-09-21 | 7 | **i18n done + todos reconciled.** Extracted all 86 UI strings to `strings.xml` (translation-ready; nav titles via `@StringRes`), verified rendering unchanged on the emulator. Marked 54 completed todos across Phases 1–7 as done. Remaining unchecked items are all user-side (upload-key reset, App Bundle upload, Play listing, release-signed Firebase check, real-device large-font pass) or optional (translations, Baseline Profile, AGP 10, Hilt). |
| 2026-09-21 | 6-7 | **App Bundle + large-font verified.** `bundleRelease` produces a 7.4 MB `.aab`. UI holds up at 1.3× font scale (no clipping). Only genuinely user-side items now remain: upload-key reset, `.aab`/listing upload to Play, release-signed Firebase check, real-device torch/camera pass. |
| 2026-09-21 | 6 | Upload-key reset **request submitted** to Play Console (routine, Play App Signing is on). Awaiting Google. Corrected the Phase 1 AGP-assistant checkbox to reflect the toolchain was migrated by hand. |
| 2026-09-21 | test | **Real-device test (OnePlus 6, API 30).** Verified torch physically flashes, sidetone, three-state colouring, no layout shift, 20 wpm cap, camera luminance stream, all screens render, no crashes. Found + fixed a bug: transmit continued after navigating away from Send (torch flashing with no Stop button) — now stopped on screen dispose. UI-parity fixes (slider/tabs/segmented/badge/ligatures) all confirmed on device. |
| 2026-09-22 | test/7 | **wpm cap → 1–10** (past ~10 wpm dots are too fast to key/read by hand); slider + `MAX_WPM` + About copy updated. |
| 2026-09-22 | 7 | **Post-device rework (§7.5), committed `e06d42a` and pushed.** Speed-agnostic **`AdaptiveDecoder`** replaces the fixed-WPM classifier on the receive path (pure adaptive, `E/T` ambiguity for uniform marks; +6 tests). **Camera permission deferred** — torch via `CameraManager.setTorchMode()` (no permission at launch), `CAMERA` requested only on Receive→Camera. **Camera receive UX**: live preview wired (was a placeholder), manual Calibrate locking a fixed ambient average, green/grey lum vs avg, help bottom sheets. **Sending drill**: words (letter-by-letter), Random button, persistent resume position. **Decoding drill**: visible message, 2 wpm, 3s countdown overlay, morse progress colouring. **More**: dropped the placeholder progress card, added a coffee mark to the donation banner. **Nav icons** Receive→Sensors / More→MoreHoriz; **launcher icon** replaced from the new brand mark. Torch, keep-awake, and deferred permission all re-verified on the OnePlus 6. |
| 2026-09-26 | 6 | **Store listing refreshed via API.** New title "Morselight - Morse with Flash"; new store icon (SOS mark) and feature graphic; 8 new screenshots (status bar cropped, fitted to Play's 2:1) in phone + 7" + 10" slots, saved under `fastlane/metadata/android/en-GB/images/`. `play_upload.py` now sends release notes for every language folder; added en-GB notes and re-published v13 internal with en-GB + en-US. Data Safety still manual. |
| 2026-09-26 | 6 | **v13 on the internal track** (commit `7f8b773`). First upload rejected: Firebase Analytics merges in `AD_ID` + AdServices permissions, contradicting the "no advertising ID" Play declaration. Stripped them via `tools:node="remove"` and disabled Firebase ad-ID collection, rebuilt, and uploaded v13 via the Play API with release notes. Internal track now serves **13 (4.0.0)**. |
| 2026-09-26 | 6-7 | **Release prep (§7.8).** Upload-key reset done; signed bundle verified and **v12 uploaded to the internal track**; bumped to **v13**. Added `CHANGELOG.md` + Play release notes. Native-symbols setting on (deps are pre-stripped, notice is harmless). fastlane wired but can't install locally (Xcode 27 needed); **service account validated** via the Play API from Python. **Keep-screen-awake fixed:** was app-wide; now Send-while-transmitting + all of Receive, reference-counted so navigation doesn't clear it — verified on device. |
| 2026-09-25 | 7 | **Loop test + CI + issue #3 (§7.7), committed `42f1887` + `72ec896`.** On-device loop test (OnePlus 12R) confirmed loop works but caught the torch flashing in the background — fixed by stopping transmit on the **Activity** `ON_STOP` (the NavBackStackEntry one wasn't firing); verified returns to Idle. **CI green again:** the red `instrumented` job was `ReferenceChartScreenTest` asserting an off-screen grid tile on the short API-33 emulator — made screen-independent (assert A, then search for S); added an androidTest-report artifact on failure. **Issue #3:** most points already handled by the rewrite; added **LICENSE (MIT)**, **CONTRIBUTING.md**, and a **Share app** action. |
| 2026-09-25 | 7 | **Layout + battery pass (§7.6), committed `1eaa339` and pushed.** OnePlus 12 (tall display): Receive-camera made fixed-height with the preview taking the flexible space (`weight`+`heightIn`) so the sliders never clip; smaller Decoded font; detection region is a centred **square** (min 8%) with the analyser measuring a matching square (+ `finally` close, fixes B5/B9). **One combined `ReceiveHelpSheet`** (Calibrate + Sensitivity + Detection area) opened from every help icon and a new **app-bar help action** on Receive. **Loop transmission** now separates repeats by a **7-unit word gap** scaled to the wpm (was a fixed 800 ms that merged messages). **Battery fix:** transmission stops on `ON_STOP` (backgrounded / screen off) — a looping transmit was flashing the torch with the screen off. Removed the unused **`WAKE_LOCK`** permission (keep-awake uses `FLAG_KEEP_SCREEN_ON`). |
