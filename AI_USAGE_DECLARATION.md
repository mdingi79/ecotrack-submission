# Declaration of AI Tool Use

Word count: ~430

I used Claude (Anthropic) as a coding assistant while building the Part 2 prototype of EcoTrack,
working from the architecture, database schema, API contract, and Impact Score rules I had already
defined in my Part 1 design document.

**What I gave the AI tool:** my existing design decisions (MVVM + Repository pattern, the
Users/WasteCategories/WasteLogs/Notifications schema, the `POST /api/logs` request/response shape,
and the exact High/Medium/Low Impact Score rules), the assignment rubric with mark allocations, and
an explicit instruction to skip anything marked "PoE only" (Google SSO, full offline sync, Firebase
push) since this stage is a prototype.

**What the AI tool produced:** a working scaffold across both halves of the app —

- The backend (Node.js/Express/MongoDB): route handlers for register/login/logs/categories, the
  bcrypt password hashing, JWT issuance and verification middleware, the Impact Score calculation
  function, and Jest/Supertest tests for both.
- The Android app (Kotlin/Compose): the Gradle project setup, the Retrofit/Room/DataStore data
  layer, repository and ViewModel classes, the five main screens (Login, Register, Dashboard,
  Categories, Logs, Settings), the bottom-navigation graph, the eco-themed Material 3 colour
  palette, and a JUnit test suite mirroring the Impact Score logic client-side.
- Supporting project files: the GitHub Actions CI workflows for both projects, the root README,
  this disclosure, and the demonstration video script.

**What I did myself:** the original Part 1 design (architecture, schema, API contract, and Impact
Score rules) that the AI tool built from; deploying the backend to a live hosting provider and
wiring the real `BASE_URL` into the app; setting up the MongoDB Atlas cluster and its credentials;
creating the actual GitHub repository and pushing the generated code as a series of commits;
running the app on my own physical device to confirm it installs and works without crashing;
recording the demonstration video following the provided script in my own voice; and reviewing the
generated code so I can explain any part of it if asked.

**Why I used it this way:** the rubric rewards a working, well-structured prototype across a large
surface area (auth, a hosted REST API, three integrated features, tests, and CI) in a short
timeframe. Using an AI assistant to generate the boilerplate and wiring — while I supplied the
actual design decisions and verified the result — let me spend my own time on making sure the app
runs correctly on a real device, that the deployed API actually works end-to-end, and on
understanding the code well enough to defend it, rather than on typing out Retrofit interfaces and
Compose layouts by hand.

**Verification:** before submitting, I ran the app on my own device end-to-end (register → log an
item → see it scored and reflected on the dashboard), ran the automated test suites locally, and
read through the Impact Score logic in both the API and the app to confirm it matches the rules
from my Part 1 design.
