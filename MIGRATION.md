# MorseLight — Modernization & Compose Migration Plan

**Status:** Phase 0 complete — Phase 1 not started
**Started:** 2026-09-20
**Owner:** Ranjan Malav
**Goal:** Bring a 2021-era app (AGP 4.2 / Kotlin 1.5 / targetSdk 30 / XML + Fragments) up to a
currently Play-Store-compliant build, and rewrite the UI in Jetpack Compose.

> ### ⚠️ OPEN BLOCKER — release signing key
> **The release keystore could not be found, and it blocks shipping (Phase 6) — not development.**
> Searched and ruled out on 2026-09-20: the whole home directory, `~/.android` (debug key only),
> Android Studio's remembered signing configs (2023.3 → 2026.1.4), `~/.gradle/gradle.properties`,
> and all 50 commits of git history. The `.jks` file itself is missing, so the forgotten password is
> the *secondary* problem.
>
> **The deciding question:** is Play App Signing enabled?
> Play Console → Test and release → Setup → App integrity → App signing.
>
> - **Enabled →** recoverable. The lost key was only the *upload* key. Generate a new keystore, export
>   its cert (`keytool -export -rfc`), and request an upload key reset in Play Console. Google keeps
>   re-signing with the app signing key it already holds, so existing users update seamlessly.
> - **Not enabled →** not recoverable. MorseLight predates the Aug 2021 mandate, so legacy self-signing
>   is plausible. Google never held a copy and cannot restore it; opting in now would require uploading
>   that same key. Fallback is republishing under a new `applicationId`, losing installs/ratings/reviews
>   and the listing URL. (Store *listing* text can still be edited without the key, so the old listing
>   can point users to the new one before being unpublished.)
>
> **Still to check (off this machine):** the 2021 dev machine or its Time Machine backup, password
> manager, Google Drive / iCloud / email-to-self, old external drives.
> If the `.jks` turns up but the password doesn't, that is the recoverable case — a JKS password is
> brute-forceable against your own keystore, and store/key passwords are often identical.

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
| **D4** | SharedPreferences → DataStore | Recommended: **yes.** Settings feed Compose state; DataStore's `Flow` API is the natural fit. Must include a one-time read of the old prefs so existing users keep their speed/perceptibility settings. | Open |
| **D5** | Keep portrait lock | Recommended: **drop `screenOrientation="portrait"`.** API 36 ignores orientation restrictions on large screens anyway; better to lay out responsively than be letterboxed. | Open |
| **D6** | Visual redesign scope | Recommended: **Material 3 with the existing teal brand palette**, dynamic color off (brand identity matters here), light + dark, keep Nunito Sans. Not a ground-up redesign. | Open |

---

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
- [ ] ⚠️ **Release keystore NOT located — see the blocker callout at the top of this file.**
      Blocks Phase 6 only; Phases 1–5 proceed regardless.

### Phase 1 — Make it build again (no UI changes)
Goal: same app, same XML UI, modern toolchain. Biggest-risk phase, because of AGP 9 (§2.1).

**Toolchain**
- [ ] Run Studio's *AGP Upgrade Assistant* and/or `android skills add agp-9-upgrade` as a first pass, then hand-finish
- [ ] Gradle wrapper → 9.7.1 (`gradle-wrapper.properties` + regenerate the wrapper jar)
- [ ] Root `build.gradle` → `build.gradle.kts`; delete `buildscript{}`, `jcenter()`, `allprojects{}`, `kotlin_version`
- [ ] `settings.gradle` → `settings.gradle.kts` with `pluginManagement` + `dependencyResolutionManagement` (`google()`, `mavenCentral()`)
- [ ] Add `gradle/libs.versions.toml` version catalog
- [ ] `app/build.gradle` → `.gradle.kts`, AGP 9.4.1
- [ ] **Confirm the Kotlin version AGP 9.4.1 bundles** before pinning the Compose compiler plugin
- [ ] Remove `kotlin-android` (built-in Kotlin) and `kotlin-kapt` (unused) from the plugins block
- [ ] Remove `kotlin-android-extensions`
- [ ] `kotlinOptions {}` → `kotlin { compilerOptions {} }`
- [ ] `gradle.properties`: drop `android.enableJetifier`, add `org.gradle.caching`/`org.gradle.parallel`, raise `jvmargs`

**Manifest / SDK**
- [ ] Move `package=` out of `AndroidManifest.xml` → `namespace` in `app/build.gradle.kts`
- [ ] Add `android:exported="true"` to `MainActivity` (required, API 31+)
- [ ] compileSdk 36 / targetSdk 36 / **minSdk 26**; drop `buildToolsVersion`
- [ ] Delete the now-dead `SDK_INT >= M` branch in `MainActivity.onCreate` (D2)

**Dependencies**
- [ ] **Rip out Koin** (D3): delete `startKoin`/`appModule` from `MorseLightApp`, `KoinComponent`/`by inject()`/`@KoinApiExtension` from `SendFragment`, `ManualDecodeFragment`, `AutoDecodeFragment`, `DecodePagerAdapter`, `MainActivity`. Expose `SharedPreferenceUtils` from the `Application` for now; it becomes `SettingsRepository` in Phase 2.
- [ ] Bump AndroidX / Material / ConstraintLayout / Navigation / Fragment to current
- [ ] CameraX alpha `camera-view` → stable 1.6.2
- [ ] Firebase BOM 34.19.0; `firebase-analytics-ktx`/`firebase-crashlytics-ktx` → `firebase-analytics`/`firebase-crashlytics`
- [ ] google-services 4.5.0, Crashlytics Gradle 3.0.8

**Code**
- [ ] **Replace Kotlin synthetics with ViewBinding** in all 8 files that use them. Unavoidable even though these views die in Phase 5 — synthetics don't exist in modern Kotlin.
- [ ] Fix deprecations: `onBackPressed()` → `OnBackPressedDispatcher`, `LiveData.observe(owner) {}` 2-arg lambda form, `requestPermissions`/`onRequestPermissionsResult` → `ActivityResultContracts`
- [ ] **Verify:** `./gradlew assembleDebug` green; app installs and all 6 flows work on device

### Phase 2 — Extract the domain layer (UI-independent)
Goal: pull all logic out of Activities/Fragments so Compose screens are thin.
- [ ] `morse/MorseTables.kt` — move `charToUnits`, `charToTotalUnits`, `charToMorse`, `morseToChar` out of `Extensions.kt`
- [ ] `morse/MorseEncoder.kt` — the ~50-line encode block that is **duplicated verbatim in 4 places** (`SendFragment`, `ManualDecodeFragment`, `AutoDecodeFragment`, `MorseTutorialActivity`) collapses into one function returning on/off delays + char units
- [ ] `morse/MorseDecoder.kt` — port `DecoderUtils` (also drop its stray `kotlinx.android.synthetic` import, which is dead code)
- [ ] `torch/TorchController.kt` — CameraX torch + wake lock, owned by the app rather than `MainActivity`; replace the 3 `Handler`s with a single coroutine on a `Dispatchers.Default` timeline
- [ ] `camera/LuminosityAnalyzer.kt` — keep, but reduce per-frame allocation (`data.map { }` allocates a boxed `List<Int>` of the whole frame on every frame; index the `ByteBuffer` directly)
- [ ] `data/SettingsRepository.kt` — DataStore (D4), with migration from the existing `SharedPreferences` file (keys: `speed`, `react_size`, `perceptibility`)
- [ ] **Unit tests** for `MorseEncoder` / `MorseDecoder` (pure functions — the best test ROI in this codebase)
- [ ] **Verify:** existing XML UI still works against the new domain layer

### Phase 3 — Compose foundation
- [ ] Enable `buildFeatures { compose = true }`, add Compose BOM + the Compose compiler plugin
- [ ] `ui/theme/` — Color.kt (port the teal palette from `colors.xml`), Type.kt (Nunito Sans via `FontFamily`), Theme.kt (M3 light + dark `ColorScheme` mapped from `themes.xml` / `values-night/themes.xml`)
- [ ] `MainActivity` → `ComponentActivity` + `setContent {}` + `enableEdgeToEdge()`
- [ ] Navigation Compose: bottom bar with Send / Receive / Learn; nested routes for Detail + Tutorial
- [ ] Shared components: `TorchStatusIndicator`, `LabelledContainer` (replaces the custom View), `MenuRow` (replaces `AccountOptionView`), `SpeedSlider`, `MorseReadout`
- [ ] **Verify:** app launches into an empty Compose shell with working navigation

### Phase 4 — Screen-by-screen port
Port one screen at a time, deleting the Fragment + XML as each lands.
- [ ] **Send** — `SendViewModel` + `SendScreen`; press-and-hold torch via `pointerInput`/`detectTapGestures`
- [ ] **Learn** — simplest screen, good warm-up; keep the ko-fi/GitHub/rate/share/mail intents
- [ ] **Morse detail** — image + linkified text
- [ ] **Receive / Manual** — `ManualDecodeViewModel` + `ManualDecodeScreen`; tab host via `PrimaryTabRow` + `HorizontalPager` (replaces `ViewPager2` + `DecodePagerAdapter`)
- [ ] **Receive / Auto** — the hard one. `CameraXViewfinder` (`androidx.camera:camera-compose`) or `AndroidView(PreviewView)`; ROI rect drawn with `Canvas` instead of 4 `ConstraintLayout` guidelines; luminosity → `StateFlow` in the ViewModel
- [ ] **Morse tutorial** — coroutine-driven simulated playback replaces the nested `postDelayed` chain
- [ ] **Info sheets** — one `ModalBottomSheet` + a content model; replaces `InfoDialog` and its 4 layout files
- [ ] **Feedback dialog** — `AlertDialog` composable
- [ ] Camera permission via `rememberLauncherForActivityResult` + rationale path
- [ ] **Verify after each screen:** build green, screen exercised on device

### Phase 5 — Delete the old world
- [ ] Remove all 15 layout XMLs, `navigation/mobile_navigation.xml`, `menu/*.xml`
- [ ] Remove `FragmentCallbacks`, `DecodePagerAdapter`, `InfoDialog`, all Fragments, `AccountOptionView`, `LabelledContainer`, `Extensions.kt` View helpers, `attrs.xml`, `styles.xml`, `theme_attributes.xml`
- [ ] Trim `themes.xml` to a launcher-only theme (`Theme.MorseLight.Starter`) + splash screen
- [ ] Drop `com.google.android.material:material`, `constraintlayout`, `navigation-fragment-ktx`, `navigation-ui-ktx`, `fragment-ktx`, `viewpager2` if unreferenced
- [ ] Remove ViewBinding once nothing uses it
- [ ] Prune unused drawables/strings/dimens (`lint` → `UnusedResources`)

### Phase 6 — Play Store readiness
- [ ] `targetSdk = 36`, `versionCode = 12`, `versionName = "4.0.0"`
- [ ] **Edge-to-edge**: targetSdk 35+ forces it — audit every screen for content under the status/nav bars; apply `WindowInsets` padding
- [ ] **Predictive back**: `android:enableOnBackInvokedCallback="true"` + verify nav behaviour
- [ ] **16 KB page size** compliance (required since Nov 2025) — verify no bundled `.so` breaks it (`zipalign -c -P 16 -v`); CameraX/Firebase should already be compliant
- [ ] ⚠️ **Resolve the signing-key blocker first** (see callout at top). Then re-add the signing config, reading the keystore path/passwords from `keystore.properties` or env — never committed (`.gitignore` already blocks them).
- [ ] `minifyEnabled true` + `shrinkResources true`; write ProGuard keep rules (Firebase, CameraX, any reflective Compose usage) and **test the release build end-to-end** — the current `proguard-rules.pro` is untouched boilerplate
- [ ] Build an **App Bundle** (`bundleRelease`) and test via internal app sharing
- [ ] Remove the portrait lock (D5); verify tablet/foldable layout
- [ ] Play Console: confirm Data Safety form still matches (camera permission, Crashlytics + Analytics data collection), refresh screenshots for the new UI
- [ ] Verify Firebase Crashlytics + Analytics still report from a release build

### Phase 7 — Polish
- [ ] Compose previews for each screen (light + dark)
- [ ] TalkBack pass: content descriptions for the torch indicator, sliders, tap-and-hold surface (the old code `@SuppressLint("ClickableViewAccessibility")`-ed this away in 3 places)
- [ ] Large-font / display-size sanity check
- [ ] Update `README.md` for the new stack
- [ ] GitHub Actions: build + unit tests on PR

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
