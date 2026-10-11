# Personal UI — Design System

**Personal UI** is the shared interface language for Ranjan's personal apps: a small family of self-built tools — a personal dashboard, a habits & health tracker, and internal/ops tooling. One person designs, builds and uses them, so the system optimises for *fast assembly of familiar parts* rather than broad configurability.

The mood the brief asked for: **soft modern** — rounded, airy, pastel-tinted surfaces — with a single signal-blue accent, light and dark modes, balanced density, and heavily rounded corners. Technique is drawn from two references named in the brief: **Material** (elevation logic, state layers, emphasised easing) and **iOS/Apple HIG** (control geometry, grouped lists, segmented controls, 44px touch minimum, tabular numerals).

---

## Sources

This system was authored **from a written brief only**. To be explicit about provenance:

- **No codebase** was attached or linked (no local folder, no GitHub repository).
- **No Figma file** or design-context link was provided.
- **No slide decks**, screenshots, PDFs or brand guidelines were provided.
- **No logo, font binaries, imagery or icon assets** were provided.

The brief itself: *"Ranjan's personal apps UI design"*, plus answers to a direction form — apps to dress (personal dashboard/tracker, habits & health, internal tools), aesthetic (soft modern), accent `#1E5EFF`, both light and dark, balanced density, heaviest corner-rounding option, name "Personal UI", and the note *"Follow best practices from both Material UI and Apple's iOS design."*

Everything below is therefore an **original construction consistent with that brief** — not a recreation of an existing product. Two substitutions are flagged: **fonts** (Google Fonts stand-ins) and **icons** (Lucide). See `assets/README.md`.

---

## Content fundamentals

The product talks to one person who already knows what they are doing. Copy is short, factual, and quietly encouraging — never coaching, never congratulatory.

**Voice**
- **Second person, implied.** "Log habit", "Keep the walk and you hit ten days." The app rarely says *I*; it never says *we*. Data belongs to the user: "Your 34-day history goes with it."
- **Plain, concrete nouns.** "Steps", "Sleep", "Spend", "Runs" — not "Wellness insights" or "Activity intelligence".
- **State before advice.** A card reports ("Missed yesterday") and stops. It does not say "Don't give up!".

**Casing and punctuation**
- **Sentence case everywhere** — buttons, titles, nav, dialog headings. Title Case never appears.
- Uppercase is reserved for the eyebrow/label role only (12px, `--tracking-caps` 0.06em): `STEPS`, `TODAY`, `LOG`.
- **No terminal punctuation** on labels, badges, toasts or stat captions. Full stops only in sentences of two clauses or more, inside dialog descriptions and hints.
- **No exclamation marks. No question marks except in a dialog title** that genuinely asks one ("Delete this habit?").

**Specific patterns**
| Slot | Rule | Examples |
| --- | --- | --- |
| Button | Verb + object, 1–3 words | "Log habit", "Queue run", "Add entry", "Export" |
| Cancel path | Name the outcome, don't say "Cancel" where it matters | "Keep it" (next to Delete), "Cancel" (neutral forms) |
| Badge | One or two words of state | "Held", "At risk", "Broken", "Connected", "Not linked" |
| Toast | Past tense, subject first, no punctuation | "Walk logged", "Run queued", "Entry added" |
| Stat caption | Always a comparison | "vs last week", "under daily cap", "2 of 4 this week" |
| Empty state | What's missing + the one action | "No runs match" / "Clear the filter or widen the status." |
| Destructive dialog | Cost, then irreversibility | "Your 34-day history goes with it. This cannot be undone." |
| Hint text | One clause, no period if under ~5 words | "Optional", "Shown on your dashboard" |
| Error | What happened, not who's at fault | "That address is already in use", "Couldn't reach the server" |

**Numbers**
Always formatted, never raw: `8,412` with a thousands separator, `6.4 hrs` with the unit as a smaller grey suffix, `+12%` with an explicit sign, `3m 12s` for durations. Every compared number is set in tabular numerals.

**Emoji: never.** Not in UI copy, not in labels, not as icons, not in empty states. Status is carried by a tinted `Badge` and a Lucide glyph.

**Vibe check.** Read any screen aloud: it should sound like a competent tool reporting facts to its owner. If a string sounds like marketing, a fitness coach, or a chatbot, rewrite it.

---

## Visual foundations

### Colour
One accent — **Signal Blue `#1E5EFF`** — used for the primary action, the active nav pill, focus rings, and the first data-viz series. Nothing else is blue. Neutrals are a **cool slate ramp** with a faint blue cast (`#F6F7F9` → `#161A21`) so the accent sits in the same family rather than on top of a warm grey.

Four semantic hues (success mint, warning amber, danger rose, info violet) each exist at three strengths: full (`--success`), a **pastel wash** (`--success-soft`), and an ink for text on that wash (`--success-on-soft`). **Full-opacity ink on washes, never alpha-muted type** — that is what keeps contrast at 4.5:1.

Semantics are behavioural, not decorative: success = held/healthy/connected, warning = at risk, danger = broken/failed, info = automated or system-owned, accent = new or primary. Backgrounds carry at most two colours per screen: `--bg-app` plus one wash.

Dark mode is a **token remap under `[data-theme="dark"]`**, not a second design: surfaces go `#0E1116 / #161A21 / #1D222B`, the accent lightens to `#5A85FF` so it survives on dark, all washes become ~16–20% alpha of their hue, and shadows deepen to near-black. Components read semantic tokens only, so the switch is free.

### Type
**Plus Jakarta Sans** for everything (display through caption) and **JetBrains Mono** for ids, logs and code. A single humanist-geometric family carries the "soft modern" read; the mono is there for machine data only.

The ramp is iOS-derived on whole pixels: 11 · 12 · 13 · 15 · 17 · 20 · 22 · 28 · 34 · 44. Body is 17/1.45. Display and titles take negative tracking (`-0.02em` / `-0.01em`); the 12px uppercase label takes `+0.06em`. Weights: 400 body, 500 controls, 600 titles and labels, 700 page titles, 800 display. Four ink levels only — heading, body, muted, subtle.

### Spacing and layout
4px base, reached by role rather than number: `--gap-inline` 8 (chips, buttons in a row), `--gap-stack` 12 (label→control, row→row), `--gap-group` 20 (card→card), `--gap-section` 32. Card inset 20 (24 for `lg`); screen padding 24 on desktop, 16 on phone.

Fixed structural elements: a **248px sidebar** on the dashboard, a **72px icon rail** on the ops console, a **390×780** phone frame with a translucent bottom tab bar. Content columns cap at `--content-max` 1120px and centre. Reading measure caps at 66ch. Touch targets never below 44px; list rows are minimum 44px.

Layout is **flex/grid with `gap`** throughout — never margin chains, never inline-flow spacing.

### Backgrounds
Flat colour and pastel washes only. **No photography, no illustration, no gradients, no textures, no patterns, no noise/grain.** (No imagery was supplied — see `assets/README.md`; if it arrives, document its treatment here.) The one gradient in the system is functional: the conic fill of `ProgressRing` and the linear track fill of `Slider`. There are no protection gradients because there is no imagery to protect text from — overlay text sits on a wash or a card, not on a photo.

### Corners
Heavily rounded, and role-based rather than free-choice: **controls are pills** (`--radius-control: 999px` — buttons, tags, badges, toasts, nav pills, icon buttons are perfect circles), **fields 16px**, **tiles 20px**, **cards 24px**, **modal sheets 28px**, **phone bezel 44px**. The single exception is the 22px **checkbox at 6px radius** — the one squarish corner in the system, which is what stops it reading as a radio.

### Cards
White `--surface-card`, a 1px `--border-subtle` hairline, `--shadow-1`, 24px radius, 20px padding. **A tinted card drops both the shadow and the border** — the wash alone separates it. Tints never nest inside tints; nest on `--surface-sunken` instead. No card ever takes a coloured left border.

### Elevation
Five soft, cool-tinted, multi-layer shadows (`rgba(16,19,26,…)` — never hard black): `shadow-1` resting cards, `shadow-2` raised controls and secondary buttons, `shadow-3` hover lift and popovers, `shadow-4` modals, drawers and toasts, plus `--shadow-accent` (a blue-tinted glow) which is used *only* under a filled accent button or the raised phone add-button. Dark mode swaps all five for near-black equivalents. Elevation is the only depth cue — there are no inner bevels except `--inset-field`, a 1px inner shadow that makes empty inputs read as recessed.

### Motion
Short and functional. `--dur-fast` 140ms for hover/colour, `--dur-base` 220ms for state change, `--dur-slow` 320ms for entrances, `--dur-sheet` 420ms for sheets. Easings: `--ease-standard` `cubic-bezier(.2,0,0,1)` (Material's decelerate — the default), `--ease-emphasized` `cubic-bezier(.32,.72,0,1)` (iOS sheet feel, for anything that travels), `--ease-spring` `cubic-bezier(.34,1.32,.64,1)` — **one small overshoot only**, on the radio dot. Entrances fade + rise 10px and scale from .98; exits fade with `--ease-exit`. **Nothing bounces, nothing slides in from off-screen except the drawer, nothing loops** except the button spinner. All durations collapse to 0 under `prefers-reduced-motion`.

### Interaction states
- **Hover:** filled buttons darken via `brightness(.94)` (never a colour swap); ghost and rows take a 6%-accent `--surface-hover` layer — a Material-style state layer, not an opacity change; interactive cards raise to `shadow-3` and translate `-1px`.
- **Press:** `scale(.97)` at 90ms, plus `--surface-press` (12% accent) on flat controls. No colour change on press — the scale does the work.
- **Focus:** `3px solid var(--focus-ring)` at 2px offset globally; inputs additionally swap their border to accent and take a 3px halo. Focus is never removed.
- **Selected:** `--surface-selected` fill (table rows, nav pills). Never a border change — borders shifting causes layout jitter.
- **Disabled:** 45% opacity plus `not-allowed`. Never greyed-out custom colours.

### Transparency and blur
Rare and purposeful, exactly two places: the **modal scrim** (`--surface-overlay` + `blur(3px)`) and the **phone tab bar** (`color-mix` on the card surface + `--blur-sheet` = `saturate(180%) blur(20px)`, the iOS material). Everything else is opaque. Text is never set on a translucent surface without an opaque card behind it.

### Borders
1px only, in three strengths: `--border-subtle` (row and card hairlines), `--border-default` (field and control outlines), `--border-strong` (unchecked checkbox/radio, dashed data hints). Tables use hairline row borders with **no zebra striping**. Radios use a 2px ring — the only 2px border in the system.

---

## Iconography

**Lucide v0.544.0** — 24×24 grid, 2px rounded-cap stroke, outline only. Its geometry matches the corner language, which is why it was chosen as the substitute.

- **Delivery:** `components/core/Icon.jsx` renders each glyph as a **CSS mask** on a `<span>`, filled with `currentColor`, pulled from `https://unpkg.com/lucide-static@0.544.0/icons/<name>.svg`. No inline path data, no icon font, no sprite sheet, and no React icon package. Every icon therefore inherits text colour automatically, including in dark mode.
- **Sizes:** 16 (inline with footnote text, badges), **20 (default — controls, buttons)**, 22–24 (nav rails, tab bars, dialog badges). Nothing else.
- **Colour:** `currentColor` by default. An icon is tinted only when it carries status (a success check, a danger triangle), and then with a semantic token.
- **Labelling:** decorative icons are `aria-hidden`; meaningful ones take `label`. Icon-only controls use `IconButton`, where `label` is required, and get a `Tooltip` when their meaning isn't obvious.
- **Common set in use:** `house`, `target`, `activity`, `wallet`, `notebook-pen`, `footprints`, `moon`, `timer`, `heart-pulse`, `scale`, `bell`, `calendar`, `calendar-check`, `settings`, `search`, `search-x`, `plus`, `check`, `check-check`, `x`, `minus`, `chevron-right`, `chevron-down`, `arrow-up-right`, `rotate-ccw`, `play`, `ban`, `download`, `trash-2`, `triangle-alert`, `info`, `trending-up`, `trending-down`, `loader-circle`, `list-checks`, `server-cog`, `key-round`, `scroll-text`, `user-round`, `book-open`, `chart-column`, `circle-check-big`, `square-dashed`, `crosshair`.
- **Emoji: never used.** Unicode characters are never used as icons either — no ✓, ×, →, •, ★. The one exception is the literal `*` marking a required field, and `—` as an em-dash placeholder for absent data.
- **No hand-drawn SVG.** If a needed concept isn't in Lucide, pick a nearer Lucide concept or use a word.

**⚠️ Substitution flag:** no icon assets were provided, so Lucide is a stand-in chosen for stroke-weight and corner compatibility. Replace it if a real set exists.

---

## Intentional additions

No source defined a component inventory, so the standard set was authored. Beyond it, three additions specific to what these apps do:

- **`ProgressRing`** — conic completion ring. The habit and dashboard surfaces are built around completion; a linear bar reads wrong at 34px inside a list row.
- **`Stat`** — label / big tabular number / delta / comparison caption. Every dashboard card needed the identical composition.
- **`Icon`** — thin wrapper over the Lucide glyph set so colour, size and accessibility are consistent and the icon source is swappable in one place.

`SegmentedControl` and `ListRow` are part of the standard set here rather than additions: the iOS-flavoured brief makes grouped lists and segmented switches primitives, not compositions.

---

## Index

**Root**
| File | What it is |
| --- | --- |
| `styles.css` | The one stylesheet consumers link. `@import` lines only |
| `readme.md` | This document |
| `SKILL.md` | Agent-skill front matter for use outside this project |
| `thumbnail.html` | Homepage tile for the system |
| `assets/README.md` | What is missing from `assets/` and how to supply it |

**Tokens** — `tokens/`
`fonts.css` (webfont imports + substitution note) · `colors.css` (base ramps + semantic aliases) · `dark.css` (`[data-theme="dark"]` remap) · `typography.css` (families, ramp, composed `--type-*` roles) · `spacing.css` (scale, density roles, fixed structure) · `radius.css` · `elevation.css` · `motion.css` · `base.css` (resets, element defaults, link colours)

**Components** — `components/`, each with `.jsx`, `.d.ts`, `.prompt.md` and one `@dsCard` per directory
| Group | Components |
| --- | --- |
| `core/` | `Icon`, `Button`, `IconButton`, `Card` (+ `CardHeader`), `Badge`, `Tag` |
| `forms/` | `Field`, `Input`, `Select`, `Checkbox`, `Radio`, `Switch`, `Slider` |
| `navigation/` | `Tabs`, `SegmentedControl`, `ListRow` |
| `data/` | `Stat`, `ProgressRing` |
| `feedback/` | `Dialog`, `Toast`, `Tooltip` |

**UI kits** — `ui_kits/`
| Kit | Product | Entry |
| --- | --- | --- |
| `dashboard/` | Personal dashboard (desktop web) | `index.html` — sidebar, metric grid, habit list, health detail, quick-add |
| `habits_app/` | Habits & health (iOS-geometry phone) | `index.html` — Today, Stats, Log, You across three live frames |
| `internal_tool/` | Ops console (internal tool) | `index.html` — icon rail, filterable run table, bulk select, detail drawer |

Each kit has its own `README.md` describing files, interactions and density rules. Surfaces with no source design (Money, Notes, Jobs, API keys, Audit) are deliberately left blank with a disclaimer rather than invented.

**Templates** — `templates/` · starting folders consuming projects copy. Each is a Design Component whose `ds-base.js` loads this system's `styles.css` and bundle; edit the `base` line in `ds-base.js` when the folder moves.
| Template | Entry | What it seeds |
| --- | --- | --- |
| Dashboard shell | `templates/dashboard-shell/DashboardShell.dc.html` | Sidebar, page header, 4-up metric row, 1.5fr/1fr content split |
| Mobile app screen | `templates/mobile-screen/MobileScreen.dc.html` | 390×780 frame, status bar, hero ring card, grouped list, translucent tab bar |
| Ops console | `templates/ops-console/OpsConsole.dc.html` | Icon rail, toolbar with search + status segments, dense record table |

**Guidelines** — `guidelines/` · 19 specimen cards feeding the Design System tab, grouped **Colors** (7), **Type** (4), **Spacing** (3), **Brand** (5: corners, elevation, motion, wordmark, iconography).

**No slide templates** were created: no deck or slide source was provided.

**Fully interactive prototypes** live in `ui_kits/`; `templates/` holds the static, directly-editable starting points. Use the kits to see behaviour, the templates to start a new design.
