# EcoTrack — Demonstration Video Script

Target length: 4–6 minutes. Record on a physical Android device (screen recording + your voice-over).

---

### Scene 1 — Intro (10–15s)
**Show:** App icon, launch EcoTrack.
**Say:** "This is EcoTrack, a waste-tracking prototype for South African users. It lets you log
waste items, get an automatic Impact Score, and track your recycling progress. I'll walk through
registration, the REST API behind it, and the three core features."

### Scene 2 — Register & password security (45–60s)
**Show:** Tap "Register", fill in name/email/password, submit.
**Say:** "I'll register a new account. As I type my password, it's important to note this password
is never stored or sent as plain text — the app sends it over HTTPS to my API, and the server
hashes it with bcrypt before it ever touches the database. Even I, as the developer, can't see or
recover a user's actual password from the database — only the hash." Submit and show the app
landing on the Dashboard.
**Optional cut-away:** briefly show the MongoDB Atlas collection (or a quick `db.users.find()` in
a terminal) so the marker can see the `hashedPassword` field is a bcrypt hash, not readable text.

### Scene 3 — Log out and log back in (20s)
**Show:** Settings → Log out → Login screen → log back in with the same credentials.
**Say:** "Logging out clears the session token locally. Logging back in re-authenticates against
the API and issues a new JWT, which is what the app attaches to every future request."

### Scene 4 — Settings walkthrough (30–40s)
**Show:** Navigate to Settings. Toggle dark theme, toggle notifications, open the language
dropdown (English / isiZulu / isiXhosa).
**Say:** "The Settings screen shows the logged-in user's profile, a dark theme toggle that
restyles the whole app immediately, a notifications toggle, and a language selector. For this
prototype stage, language is a working stub — it saves your choice, but full isiZulu/isiXhosa
translations are planned for the next phase, per my project brief."

### Scene 5 — REST API + database in action (45–60s)
**Show:** Split screen or cut to a terminal/Postman hitting `GET /api/logs` with the JWT, or show
the MongoDB Atlas collection view for `wastelogs` updating live as you add an item in the app in
the next scene.
**Say:** "Here's the live REST API — built with Node.js, Express, and MongoDB, hosted on
[Render/Railway]. Every waste log the app creates is a real POST request to this API, which
calculates the Impact Score server-side and stores the record here in the database."

### Scene 6 — Feature: Categories (20–30s)
**Show:** Categories tab → add a category, e.g. "E-waste", CO2 factor, material type "hazardous".
**Say:** "First feature: Waste Categories. Each category has a material type — hazardous,
recyclable, or organic — and a CO2 factor, which is how much CO2 is saved per kilogram recycled.
This feeds directly into the Impact Score and the dashboard's CO2 estimate."

### Scene 7 — Feature: Waste Logging & Impact Score Engine (60–90s)
**Show:** Logs tab → tap + → fill in item type, weight, pick the hazardous category, set collection
in 1 day → save → point at the "High" badge that appears. Add a second log with a recyclable
category and a collection 10 days out → point at the "Low"/"Medium" badge.
**Say:** "Second and third features together: Waste Logging, and the Impact Score engine. I'll log
a hazardous item with a collection due tomorrow — notice it's immediately flagged High impact,
because hazardous material or an urgent deadline both trigger a High score. Now I'll log a
recyclable item with a collection ten days out — this comes back as a lower score, since neither
condition for High or Medium applies. That rule-based logic lives in both the API and the app, and
it's covered by automated unit tests in both codebases."

### Scene 8 — Feature: Sustainability Dashboard (30–40s)
**Show:** Dashboard tab, pointing at total weight recycled, CO2 saved, and the upcoming collections
list (should include the items just logged).
**Say:** "Last feature: the Sustainability Dashboard. It pulls a live summary from the API —
total weight recycled, estimated CO2 saved based on each category's CO2 factor, and the next
upcoming collections sorted by date, each still showing its Impact Score."

### Scene 9 — Error handling (15–20s)
**Show:** Turn on airplane mode, try to refresh the dashboard or add a log; show the friendly error
message instead of a crash. Turn airplane mode back off.
**Say:** "The app also handles failures gracefully — here's what happens with no network
connection: a clear message instead of a crash, and everything recovers once connectivity is
back."

### Scene 10 — Wrap-up (10–15s)
**Show:** GitHub repo page, Actions tab showing a green CI run.
**Say:** "Finally, here's the GitHub repository, with the commit history showing incremental
progress, and GitHub Actions automatically running the test suite and building the app on every
push. That's EcoTrack — thanks for watching."

---
**Checklist before recording:** backend deployed and reachable, `BASE_URL` in `app/build.gradle.kts`
pointed at it, at least one category already existing (or create it live in Scene 6), device screen
recording + microphone both on.
