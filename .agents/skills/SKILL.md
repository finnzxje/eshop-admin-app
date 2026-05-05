---
name: e-shop-android-admin
description: Build or modify the current e-shop repository's Android admin application in Android Studio using Java and beginner-friendly XML Android patterns. Use when the task concerns Android admin parity with main/admin, Java Android code, Android XML layouts, admin API integration, product/user/order/support/profile/dashboard screens, or keeping Android work aligned with ref-android classroom style.
---

# E-Shop Android Admin

## Purpose

Use this skill to develop only the Android admin app for this repository. The target is full feature parity with the React admin dashboard in `main/admin`, implemented in Java with Android Studio and the simpler style shown in `ref-android`.

Treat these sources as authoritative:

- Product behavior and screen parity: `main/admin/src`.
- API contracts: `main/backend/e-shop/docs` first, then Spring controllers and DTOs if docs are incomplete.
- Android coding style: `ref-android`.

## Required Workflow

1. Identify the matching React admin screen or component before changing Android code.
2. Read the relevant backend API doc and, when needed, the controller/DTO source.
3. Read a similar `ref-android` example for the Android pattern.
4. Implement the smallest complete Android slice using Java/XML classroom style.
5. Keep code readable for an instructor: direct names, simple classes, explicit listeners, and minimal abstractions.
6. Verify with an Android/Gradle build command when the project can be built locally.

If a task asks about Android SDK, AndroidX, Gradle, or another library/API, fetch current docs with Context7 before answering or coding. The local style reference still wins over modern recommendations unless the user approves a style change.

## Reference Files

Load these only as needed:

- `references/admin-parity.md`: React admin parity map and Android screen targets.
- `references/android-style.md`: allowed/discouraged Android patterns based on `ref-android`.
- `references/api-map.md`: admin auth, endpoints, payloads, and local source paths.
- `references/workflow.md`: step-by-step development checklist.

## Style Rules

Use:

- Java, XML layouts, Android Studio project structure.
- `AppCompatActivity`, simple `Fragment`, `Intent`, `Bundle`, `Toast`, `SharedPreferences`.
- `ListView`, `GridView`, `Spinner`, `BaseAdapter`, and simple model classes.
- `SQLiteOpenHelper` and DAO classes only for local persistence or offline classroom demos.
- Direct `findViewById`, `setOnClickListener`, `OnItemSelectedListener`, and explicit helper methods.
- Simple HTTP code that is easy to read. If a library is already present in the Android project, follow its existing pattern.

Avoid unless the user explicitly approves:

- Kotlin, Jetpack Compose, Room, ViewModel, LiveData, Hilt, Dagger, Retrofit, coroutines, Navigation Component, Paging, complex MVVM/Clean Architecture.
- WebView wrappers for admin pages as a shortcut to parity.
- Large framework migrations or unrelated backend/frontend rewrites.

## Parity Target

The Android admin app must cover the same admin capabilities as `main/admin`:

- Login and admin/staff role gating.
- Dashboard analytics summary and revenue trend.
- User management, user detail, status changes, and role changes.
- Product management, create/edit/status, media, color grouping, variants, variant status, stock adjustments.
- Order/payment transaction list and transaction detail.
- Admin support conversations, messages, status updates, and assigned conversations.
- Admin profile and password update.

Implement these as native Android screens, not as embedded React pages.

## Backend Rules

- Use base URL defaults from the repo docs: local backend `http://localhost:8080`; Android emulator usually needs `http://10.0.2.2:8080`.
- Store access token and user JSON in `SharedPreferences`.
- Send `Authorization: Bearer <token>` for protected endpoints.
- Check roles from login response. `ADMIN` gets all screens; `STAFF` gets shared staff screens and must not see admin-only user management.
- Use the backend error envelope when displaying failures. Show concise `Toast` messages and keep details in logs.

## Output Expectations

When finishing an Android change, report:

- Which parity area was implemented.
- Which local React/admin and backend docs were used.
- Which Android reference pattern was followed.
- What build or verification command was run, or why it could not be run.
