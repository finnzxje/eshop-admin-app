# Development Workflow

Follow this checklist for each Android admin task.

## 1. Locate Parity Source

Find the React admin file that already implements the behavior:

```text
main/admin/src/pages/admin
main/admin/src/components/layout
main/admin/src/context
main/admin/src/router
```

Record the screen, routes, request URLs, role rules, filters, and visible fields.

## 2. Locate API Contract

Read the matching doc under:

```text
main/backend/e-shop/docs
```

If the doc is incomplete, inspect:

```text
main/backend/e-shop/src/main/java/com/eshop/api
```

Prefer DTO/controller names over guessing JSON fields.

## 3. Locate Android Style Reference

Use `ref-android` for the nearest pattern:

- Login/session: `LoginShareReference`.
- Form/list/DAO: `DemoQLSV`.
- Grid/list adapters: `tempProject`, `AdvanceGridView`, `ListViewNangCao`.
- Bottom navigation/ViewPager: `ViewPagerNNavigation`, `tempProject`.
- Basic WebView mechanics only: `DemoWebView`.

## 4. Design The Android Slice

Keep the slice small:

- One activity/fragment and its XML layout.
- One adapter if the screen lists data.
- One or more plain model classes if parsing JSON.
- One helper method or small API helper call for the endpoint.

Prefer explicit code over generic abstractions. Do not add a large architecture layer.

## 5. Implement

Use Java and XML. Keep names classroom-readable:

- Activities: `LoginActivity`, `AdminMainActivity`, `ProductManagementActivity`.
- Views: `edtEmail`, `btnLogin`, `lvProducts`, `tvRevenue`, `spStatus`.
- Methods: `loadProducts`, `fillUsersToListView`, `saveProduct`, `showToast`.

For forms:

- Validate required fields before sending HTTP.
- Convert numbers with `try/catch`.
- Show success/failure `Toast`.
- Refresh the list after create/update/status changes.

For lists:

- Use `BaseAdapter`.
- Inflate row XML layouts.
- Open detail screens with `Intent` extras.

## 6. Verify

Run the most relevant available command:

```text
./gradlew assembleDebug
./gradlew test
```

If the Android project is not present yet, verify by checking file paths and XML/Java consistency. Report that a build could not be run because the Android app has not been created or Gradle is unavailable.

## 7. Report

Mention:

- The admin parity area changed.
- The React file(s) used.
- The backend doc/controller used.
- The `ref-android` pattern followed.
- The verification command and result.
