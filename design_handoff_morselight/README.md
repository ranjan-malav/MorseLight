# Handoff: MorseLight — Material-guidelines redesign

## Overview
MorseLight is an existing Play Store app that transmits messages in Morse code with the phone's
flashlight, decodes incoming Morse (manual key or camera), and teaches Morse code. This bundle is
the redesign of that app on the **Personal UI design system** (Material elevation logic + iOS
control geometry, single signal-blue accent, light and dark).

Six screens across three tabs — **Send**, **Receive** (manual key / camera) and **More**
(learning, preferences, support) — plus three sub-screens pushed from More: decoding drill,
sending drill and the reference chart.

## About the Design Files
The files in this bundle are **design references created in HTML** — working prototypes showing
intended look and behaviour, not production code to copy. The task is to **recreate these designs
in the target codebase's environment** (Android/Kotlin + Material 3, React Native, Flutter, etc.)
using its established patterns and component library. The Morse encode/decode logic in
`MorseFrame.dc.html` is correct and worth porting; the markup and inline styles are not.

## Fidelity
**High fidelity.** Final colours, typography, spacing, radii, elevation and motion. Timing,
keying, decoding, drill scoring and the transmit progress states are all functional in the
prototype. Recreate the UI faithfully using the target platform's equivalents of the components
listed below.

## Platform notes
- Designed at **390 × 844** (phone). **The task screens must not scroll** — Send, Receive (both
  modes) and both drills fit the viewport exactly, with press-and-hold targets in the lower third.
  Only the two list screens scroll: **More** and the **reference chart**.
- Flashlight is **simulated** in the prototype (the disc fills with accent + glow). In the app
  this drives `CameraManager.setTorchMode` / equivalent.
- Camera decoding is **mocked** with a "Demo" button that plays a known signal and animates a
  luminance readout. In the app this is a per-frame mean-luminance sample inside the detection box,
  thresholded by the Sensitivity slider.

---

## Screens

### 1. Send — `screen: 'send'`
Purpose: compose and transmit a message; also key by hand.

Layout: a 5-row CSS grid filling the content area, `grid-template-rows: auto auto 1fr auto auto`,
`gap: 12px`, page padding `0 16px 14px`.

1. **Message input** — multiline Input, 2 rows, placeholder "Type your message". Radius 16,
   `--border-default` hairline, `--inset-field` inner shadow.
2. **Morse card** — sunken tint, padding sm, radius 24.
   - Header row: eyebrow "MORSE" (12px/600, uppercase, `--tracking-caps` .06em, `--text-subtle`)
     and, right-aligned, either "N symbols" (idle) or "NN% sent" (transmitting).
   - Morse string: JetBrains Mono **22px / 700**, line-height 1.35, letter-spacing .06em, fixed
     46–60px height with overflow hidden.
     Per-symbol colour while transmitting: **sent = `--success` #0B8A5C**, **current =
     `--accent` #1E5EFF**, **pending = `--text-subtle` #8A93A3**. Idle = `--text-body` #272D39.
   - Progress bar: 4px pill, track `--track` (transparent when idle), fill `--accent`, width =
     elapsed/total. **Do not fade it in with a transition** — it is repainted ~60×/s and a
     re-triggered opacity transition never completes.
3. **Torch disc** (vertically centred in the flexible row) — 168px circle, `--surface-sunken`,
   1px `--border-subtle`. On: inner circle (inset 7px) fills `--accent` and the disc takes
   `0 0 46px -6px var(--accent)` glow; icon + label flip to `--text-on-accent`. Icon
   `flashlight` 40px, label "ON"/"OFF" 13px/600 uppercase. Press-and-hold keys manually.
   Below: status Badge ("Idle" / "Keying" / "Transmitting") + caption "Hold to key by hand".
4. **Slider** — "Transmission speed", 5–25 wpm, value label "12 wpm".
5. **Action row** — `display:flex; gap:8px`:
   - `Signal` — soft accent button, 52px. Immediately transmits the attention prosign `-.-.-`.
   - `Send` / `Stop` — filled accent primary, `flex:1`, icon play/square, `--shadow-accent`.
     Tone flips to danger while running.
   - `SOS` — soft danger button. Immediately transmits `...---...` as one unbroken group.

### 2. Receive — Manual key — `screen: 'receive', rxMode: 'manual'`
Segmented control at top: "Manual key" | "Camera".
Grid `auto 1fr auto`:
1. **Decoded card** — eyebrow "DECODED" + ghost "Reset" button; decoded text at
   `--type-title-2` (22/600); hairline; raw dot-dash buffer in mono 15px `--text-muted`.
2. Caption, centred: "Hold while the sender's light is on. Short is a dot, long is a dash."
3. **Key disc** — 168px, same treatment as the torch, icon `circle-dot`, label "HOLD", pinned to
   the bottom of the content area.

### 3. Receive — Camera — `rxMode: 'camera'`
1. **Viewfinder** — 172px tall, radius 24, near-black `#05070B`, centred detection box outlined
   2px `--accent` with `0 0 0 999px rgba(0,0,0,.28)` vignette. Overlay badge top-left
   ("Waiting"/"Reading"); mono luminance chip bottom-right (`lum 6` → `lum ~180`). A full-bleed
   accent flash layer at opacity 1 marks a detected pulse.
2. Row: caption "Point the camera at the sender's light." + soft "Demo" button.
3. **Tuning card** (sunken) — *deliberately outside the viewfinder, unlike the old app*:
   Sensitivity slider (0–100%), Detection area slider (30–140px, drives the box size).
4. **Decoded card** — same as manual.

### 4. More — \`screen: 'learn'\`
The hub tab: learning, preferences and support in one scrolling list. Sections are separated by
12px caps eyebrow labels (\`--text-subtle\`) sitting directly above each card.

1. **Coffee banner** (top, full width) — \`--warning-soft\` fill, radius 24, no border or shadow,
   padding 14/16, tappable. A 40px white circle holding a 20px \`coffee\` icon in \`--warning\`,
   then "Buy me a coffee" (17/600) over "MorseLight is free and ad-free" (13), then an 18px
   \`chevron-right\`. All ink is \`--warning-on-soft\` at full opacity.
2. **Progress card** — accent tint (no border, no shadow, per the system's tinted-card rule):
   \`ProgressRing\` 82px on a white pill, "12 of 36 learned" title, "Lesson 4 covers K, R and S.",
   success Badge "6 day streak".
3. **LEARN** — grouped list card: "Decoding drill" / "Copy a played message by ear and eye",
   "Sending drill" / "Tap out one letter at a time", "Reference chart" / "All 36 characters".
   Each has a chevron and pushes a sub-screen.
4. **Timing card** (sunken) — "A dash is three dots long. Letters are three dots apart, words are
   seven." plus a 9-bar dot/dash rhythm graphic (9px per unit, accent bars, \`--track\` gaps).
5. **PREFERENCES** — three Switch rows: "Key tone" (sidetone while the light is on),
   "Loop transmission" (repeat the message until stopped), "Keep screen awake" (while sending or
   receiving).
6. **SUPPORT** — two chevron rows with a 20px leading Lucide icon in \`--text-muted\`:
   \`star\` "Rate this app" / "Leave a review on the Play Store" → Play Store review intent;
   \`code\` "Source code" / "MorseLight is open source, browse the repository" → repository link.
   Donation lives in the banner, not here.
7. **About card** (sunken) — "MorseLight 3.0. Timing follows ITU-R M.1677, one unit at 12 wpm."

Only two washes on this screen: the amber banner and the accent progress card. There is **no
separate Settings screen and no header gear** — this tab replaced both.

### 5. Decoding drill — `screen: 'drill'`
Grid `auto 1fr auto auto`. Target card (message masked as bullets until Reveal; badge
Ready/Playing/Match; Play, Next, Reveal buttons) → mocked sender disc 126px that flashes the
message → "Your copy" card → full-width 64px pill "HOLD TO COPY" + reset IconButton, bottom-aligned.
Match is detected by comparing the decoded buffer to the target string.

### 6. Sending drill — `screen: 'sdrill'`
Prompt card: eyebrow "SEND THIS CHARACTER", the character at `--type-display` (44/800), a mono
hint line (dots until "Hint" is tapped), and a state Badge (Waiting / Correct / Not that one).
"You keyed" card shows the buffer. Clear / Hint / Skip small buttons. Full-width 64px
"HOLD TO KEY" pill at the bottom. Correct answers auto-advance after 900ms.

### 7. Reference chart — `screen: 'chart'`
Search Input (icon `search`) filtering by character or code, then a 2-column grid of tiles
(radius 20, card surface, hairline): character 17/700 left, code in mono 15px `--accent`.
Order is A–Z then 0–9. Tapping a tile plays that character. This screen scrolls.

---

## Chrome
- **Status bar** 46px: "9:41" + `signal`/`wifi`/`battery-full` icons, 14px/600.
- **Header**: optional back IconButton, eyebrow "MORSELIGHT" (12px caps) over the screen title
  (`--type-title-1`, 28/700, `-0.01em`), trailing settings IconButton (hidden on Settings).
- **Tab bar** (main screens only): Send / Receive / Learn, icons `flashlight`, `radio`,
  `graduation-cap` at 23px, label 11px/600. Active = `--accent`, inactive = `--text-subtle`.
  Background `color-mix(in oklab, var(--surface-card) 82%, transparent)` + `--blur-sheet`
  (`saturate(180%) blur(20px)`), 1px top hairline, 22px bottom inset for the home indicator.
  Sub-screens (chart, both drills) replace the tab bar with a back button and return to More.

## Interactions & Behavior

### Morse timing (ITU-R M.1677)
One unit = `1200 / wpm` ms. Dot = 1 unit on. Dash = 3 units on. Intra-character gap = 1 unit.
Inter-character gap = 3 units. Word gap = 7 units. String form: `.... . .-.. .-.. --- / .-- ---`
(space between characters, ` / ` between words).

### Transmit playback
Build an event list of `{t, on, symbolIndex}` in units, then tick at 16ms comparing elapsed
units to the list. Each tick sets: torch on/off, current symbol index (**held through the off-gap
so the accent state stays visible for the whole element**), completed index, and percent elapsed.
On finish: reset, and if "Loop transmission" is on, restart after 800ms.

### Manual keying (press-and-hold classification)
On pointer down: measure the gap since the last release — `>= 6` units inserts ` / ` (word),
`>= 2` inserts ` ` (character). On pointer up: duration `< 2` units = dot, else dash.
Two idle timers commit a character separator at 3.5 units and a word separator at 8 units.

### Sidetone
620Hz sine via WebAudio, gain ramped to 0.05 with an 8ms time constant while the light is on;
gated by the "Key tone" setting. On device, use a short looped tone or `ToneGenerator`.

### Feedback while transmitting
The torch/key disc fills + glows, the badge changes, the morse string colours three ways, and the
progress bar advances. This was the single biggest gap in the old UI.

### Motion
`--dur-fast` 140ms (hover/colour), `--dur-base` 220ms (state), `--dur-slow` 320ms (entrance),
`--ease-standard` `cubic-bezier(.2,0,0,1)`. Light on/off transitions are shorter — 50–70ms
linear — so keying feels instant. All durations collapse to 0 under `prefers-reduced-motion`.

## State
```
screen           'send'|'receive'|'learn'|'chart'|'drill'|'sdrill'   ('learn' = the More tab)
back             screen to return to from a sub-screen
rxMode           'manual'|'camera'
wpm              5..25 (default 12)
msg              composed message text
txMorse          the string actually being transmitted (message, prosign or SOS)
txRun/txOn       transmitting / light currently on
txIdx/txDone     current + last-completed symbol index
txPct            0..1 elapsed
buffer           raw dot-dash string being decoded
keyOn            manual key held
srcOn/camRun/lum mocked incoming light + luminance readout
sens/box         camera sensitivity, detection box px
drill/reveal     decoding drill index + reveal toggle
sdIdx/sdBuf/sdResult/sdHint   sending drill
query            chart search
tone/loop/awake  preference switches
```

## Design tokens (light → dark)
```
--accent            #1E5EFF  →  #5A85FF     single accent; primary action, active tab, focus
--bg-app            #F6F7F9  →  #0E1116
--surface-card      #FFFFFF  →  #161A21
--surface-sunken    #F1F3F6  →  #1D222B
--text-heading      #101319  →  #F3F5F8
--text-body         #272D39  →  #C9D0DC
--text-muted        #5A6373  →  #97A1B2
--text-subtle       #8A93A3  →  #6C7688
--success           #0B8A5C      held / decoded correctly / symbols already sent
--warning           #B26A00      at risk
--danger            #C8304C      SOS, stop, wrong answer
--info              #6B4BD6      system-owned
--border-subtle / --border-default / --border-strong   1px only, three strengths
--radius-control 999px · --radius-field 16 · --radius-tile 20 · --radius-card 24 · bezel 44
--shadow-1 resting card · --shadow-2 raised control · --shadow-3 hover · --shadow-4 modal
--shadow-accent     blue glow, only under the filled accent button
--dur-fast 140 · --dur-base 220 · --dur-slow 320 · --ease-standard cubic-bezier(.2,0,0,1)
gaps: inline 8 · stack 12 · group 20 · section 32 · screen padding 16 (phone)
```
Type: **Plus Jakarta Sans** (400 body / 500 controls / 600 titles / 700 page titles / 800 display),
**JetBrains Mono** for morse strings and codes. Ramp 11·12·13·15·17·20·22·28·34·44, body 17/1.45.
Exact values live in `_ds/.../tokens/*.css` in this bundle.

## Assets
No bitmap assets. Icons are **Lucide v0.544.0** outline, rendered as CSS masks filled with
`currentColor`: `flashlight`, `radio`, `graduation-cap`, `circle-dot`, `play`, `square`,
`rotate-ccw`, `search`, `settings`, `info`, `chevron-left`, `signal`, `wifi`,
`battery-full`. Use the platform's equivalent (Material Symbols on Android) at matching optical
sizes — 16 inline, 20 controls, 23–24 nav.

## What changed from the old app (rationale to preserve)
1. **One object per job.** The old Send screen stacked four outlined boxes with equal weight. The
   torch disc is now the only large target; text, morse and speed read top-to-bottom.
2. **Tappable things look tappable.** Pills, one filled primary, soft secondaries; read-only output
   uses sunken cards, never field-like bordered boxes.
3. **Live transmit feedback** — glow, badge, three-state symbol colouring, progress bar.
4. **Signal and SOS demoted but not hidden** — soft-tinted buttons flanking the primary. Signal
   fires the attention prosign `-.-.-` before a message; SOS fires the distress group.
5. **Camera tuning moved out of the viewfinder** — sliders no longer overlay the preview.
6. **The task screens never scroll**, and hold targets sit low in thumb reach. Only More and the
   36-row chart scroll.
7. **Learn promoted to a tab** with a progress card, two drills and a searchable chart — then
   merged with Settings into one **More** tab, so the app has three tabs and no buried gear.

## Files
- `MorseLight.dc.html` — the canvas: two live frames (light + dark) side by side, plus the
  rationale notes. Open this one.
- `MorseFrame.dc.html` — the app itself. Template = markup for all seven screens; the logic class
  holds the Morse table, encoder/decoder, playback engine, keying classifier and drill scoring.
  Takes one prop: `theme: 'light' | 'dark'`.
- `support.js` — prototype runtime. Not part of the design; do not port.
- `_ds/personal-ui-design-system-.../` — the Personal UI design system: token CSS files and the
  component bundle. `tokens/colors.css`, `dark.css`, `typography.css`, `spacing.css`,
  `radius.css`, `elevation.css` and `motion.css` are the authoritative values.

Open `MorseLight.dc.html` in a browser to interact with both themes: hold the torch, type a
message and press Send, or open Learn for the drills.

## Screenshots
`screens/` holds each screen in both themes (2× PNG, 780×1688):
01 Send · 02 Receive manual · 03 Receive camera · 04 Learn hub · 05 Decoding drill ·
06 Sending drill · 07 Reference chart · 08 Settings.

**Capture caveat:** these are DOM re-renders, so Lucide icons (CSS-mask spans) and the slider
track render as flat grey blocks. Colour, type, spacing and layout are accurate; for icon
placement and the real slider, open `MorseLight.dc.html` in a browser.
