# EcoTrack — Part 2: App Prototype

**Track it. Recycle it. Sustain it.**

EcoTrack is a sustainability / waste-tracking Android app for South African users. Users log
waste items, get an automatic **Impact Score** (High / Medium / Low) based on hazard type and
collection deadline, and see a dashboard of total weight recycled, estimated CO2 saved, and
upcoming collections.

This repository contains two projects, matching the Part 1 design (MVVM + Repository pattern on
Android, talking to a REST API):

```
/EcoTrack            → Android app (Kotlin, Jetpack Compose, Material 3)
/ecotrack-backend     → REST API (Node.js, Express, MongoDB, JWT auth)
```

> **Scope note (Part 2 prototype):** per the assignment brief, this build intentionally skips
> everything marked "PoE only" in the Part 1 design: Google SSO, a full offline-sync engine, and
> Firebase push notifications. Login is plain email/password (passwords hashed with bcrypt), the
> app is online-only, and the language selector in Settings is a working UI stub that stores the
> user's choice without full translations yet.

## Design decisions

- **MVVM + Repository pattern** on Android: Composable screens observe `StateFlow` from
  ViewModels, which never talk to Retrofit/Room directly — they go through `AuthRepository` /
  `WasteRepository`. This mirrors the Part 1 architecture diagram and keeps networking, caching
  and UI logic independently testable.
- **Room as a local read-cache**, not a sync engine: `WasteCategoryEntity` / `WasteLogEntity`
  mirror the API response shape so Logs/Categories screens can render instantly from cache while a
  network refresh runs in the background. Full offline write-sync is out of scope for the prototype.
- **The Impact Score engine is implemented twice, deliberately**: once in the API
  (`ecotrack-backend/utils/impactScore.js`, the source of truth returned to the app) and once in
  Kotlin (`util/ImpactScoreCalculator.kt`) purely so it's unit-testable on both sides and the UI
  can be built/tested independently of a live server.
- **Passwords are always hashed**: bcrypt (`bcryptjs`, cost factor 10) hashes the password
  server-side before it's stored; the plaintext password is never written to the database or
  returned in any API response. See the auth tests in `ecotrack-backend/__tests__/auth.test.js`
  which explicitly assert the plaintext never appears in a response body.
- **Manual dependency injection** (`EcoTrackApp` + `ViewModelFactory`) instead of Hilt, to keep the
  wiring easy for a marker to read end-to-end without needing familiarity with a DI framework.

## Rubric coverage map

| Rubric item | Where |
|---|---|
| Sign-in | `ui/screens/auth/*`, `viewmodel/AuthViewModel.kt`, `routes/auth.js` |
| Settings | `ui/screens/settings/SettingsScreen.kt`, `viewmodel/SettingsViewModel.kt` |
| REST API creation | `ecotrack-backend/` (all routes, models, middleware) |
| REST API integration | `data/remote/*`, `data/repository/*` |
| Waste Logging & Categories | `ui/screens/logs/`, `ui/screens/categories/`, `routes/logs.js`, `routes/categories.js` |
| Impact Score Engine | `utils/impactScore.js` (backend), `util/ImpactScoreCalculator.kt` (Android) |
| Sustainability Dashboard | `ui/screens/dashboard/DashboardScreen.kt`, `GET /api/logs/summary/dashboard` |
| UI | eco green/earth Material 3 theme (`ui/theme/`), bottom nav (`ui/navigation/`) |
| Tests | `ecotrack-backend/__tests__/*` (Jest), `app/src/test/.../ImpactScoreCalculatorTest.kt` (JUnit) |
| CI | `.github/workflows/backend-ci.yml`, `.github/workflows/android-ci.yml` |

## Running the backend locally

```bash
cd ecotrack-backend
cp .env.example .env      # fill in a real MongoDB Atlas URI + a random JWT_SECRET
npm install
npm run dev                # starts on http://localhost:4000
npm test                   # Jest unit + integration tests (uses an in-memory Mongo, no setup needed)
```

**Deploying it for free (so the Android app can reach it from a real phone):** push
`ecotrack-backend/` to its own GitHub repo (or a subfolder, pointing Render/Railway at that
subdirectory), create a free Web Service on [Render](https://render.com) or
[Railway](https://railway.app), set the same environment variables as `.env.example`, and it will
give you a public HTTPS URL like `https://ecotrack-api.onrender.com`.

## Running the Android app

1. Open the `EcoTrack/` folder in Android Studio (Hedgehog or newer). Opening it in Studio
   auto-generates the `gradlew` wrapper script and jar the first time — no manual setup needed. If
   you're building from the command line instead, run `gradle wrapper --gradle-version 8.7` once
   first.
2. In `app/build.gradle.kts`, update `BASE_URL` in `defaultConfig` to your deployed backend URL
   (or `http://10.0.2.2:4000/` if running the API locally and testing on the emulator — a real
   device needs your machine's LAN IP instead, since `10.0.2.2` only resolves inside the emulator).
3. Run on a **physical device** via USB debugging (rubric item 1 requires this, not just the
   emulator) or an emulator for day-to-day development.
4. Unit tests: `./gradlew testDebugUnitTest` (runs `ImpactScoreCalculatorTest`).

## CI/CD

Two independent GitHub Actions workflows run on every push/PR to `main`:

- **`backend-ci.yml`** — installs backend dependencies and runs the Jest suite (Impact Score unit
  tests + auth integration tests against an in-memory MongoDB).
- **`android-ci.yml`** — sets up JDK 17, runs `./gradlew testDebugUnitTest`, then builds a debug
  APK and uploads it as a workflow artifact so a marker can download a freshly-built APK from the
  Actions tab without building locally.

## Commit history

This project was built in stages (scaffold → auth → REST integration → the three features → UI
polish → CI/tests), with a commit at the end of each stage rather than one large commit — see the
commit log on GitHub for the breakdown.

## Screenshots

_Add screenshots of the Login, Dashboard, Logs, Categories and Settings screens here once you've
run the app, per the rubric requirement._
