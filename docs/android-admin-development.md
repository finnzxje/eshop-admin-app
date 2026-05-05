# Android Admin Development Guide

This guide explains how teammates should build the native Android admin app in this repository.

## Goal

Build a native Android admin application with feature parity to the React admin dashboard. Do not embed the React admin app in a WebView.

The Android app must use Java and XML layouts, with simple Android Studio patterns that match the examples in `ref-android`.

## Source Of Truth

Use these sources in this order before coding a feature:

1. React admin behavior: `ref/e-shop/admin/src`
2. Backend API docs: `ref/e-shop/backend/e-shop/docs`
3. Backend controllers and DTOs, only when docs are incomplete: `ref/e-shop/backend/e-shop/src/main/java/com/eshop/api`
4. Android style examples: `ref-android`
5. Project skill references: `.agents/skills/references`

## Required Android Style

Use:

- Java classes and XML layouts.
- `AppCompatActivity`.
- Simple `Fragment` only when it clearly matches an existing reference pattern.
- `Intent` and `Bundle` for screen navigation and detail IDs.
- `SharedPreferences` for login token and user session data.
- `Toast` for short user-facing messages.
- `ListView`, `GridView`, `Spinner`, `BaseAdapter`, and simple model classes.
- Direct `findViewById`, `setOnClickListener`, and `OnItemSelectedListener`.
- Small helper methods such as `loadProducts`, `fillProductsToListView`, `saveProduct`, and `showMessage`.

Avoid unless the team explicitly approves it first:

- Kotlin.
- Jetpack Compose.
- Retrofit.
- Room.
- ViewModel, LiveData, or complex MVVM.
- Hilt, Dagger, or dependency injection frameworks.
- Navigation Component.
- Paging.
- WebSocket/STOMP support chat.
- Chart libraries.
- Advanced image picker or upload helper libraries.
- WebView wrappers for admin pages.

## Local Android Reference Map

Read the closest Android example before implementing a screen:

| Need | Reference |
| --- | --- |
| Login and `SharedPreferences` session | `ref-android/LoginShareReference` |
| Form, `Spinner`, `ListView`, save button flow | `ref-android/DemoQLSV` |
| `BaseAdapter` row pattern | `ref-android/ListViewNangCao` and `ref-android/DemoQLSV` |
| Grid/list layouts | `ref-android/tempProject` and `ref-android/AdvanceGridView` |
| Bottom navigation and ViewPager pattern | `ref-android/ViewPagerNNavigation` and `ref-android/tempProject` |
| Basic HTTP/API loading pattern | `ref-android/WeatherAPI` |
| WebView mechanics only, not admin parity | `ref-android/DemoWebView` |

## Planned Package Structure

Keep one Android module: `:app`.

Use this Java package structure:

```text
app/src/main/java/com/ptithcm/e_shopadmin/
  LoginActivity.java
  AdminMainActivity.java

  common/
    ApiConfig.java
    ApiClient.java
    SessionManager.java
    JsonUtils.java
    DateUtils.java

  model/
    User.java
    Product.java
    ProductVariant.java
    ProductImage.java
    Category.java
    DashboardSummary.java
    RevenueBucket.java
    PaymentTransaction.java
    SupportConversation.java
    SupportMessage.java
    AdminProfile.java

  adapter/
    ProductAdapter.java
    UserAdapter.java
    TransactionAdapter.java
    SupportConversationAdapter.java
    SupportMessageAdapter.java
    RevenueBucketAdapter.java
    VariantAdapter.java
    ProductImageAdapter.java

  dashboard/
    DashboardActivity.java

  users/
    UserManagementActivity.java
    UserDetailActivity.java
    RoleEditActivity.java

  products/
    ProductManagementActivity.java
    ProductFormActivity.java
    ProductMediaActivity.java
    ProductVariantActivity.java
    StockAdjustmentActivity.java

  orders/
    TransactionListActivity.java
    TransactionDetailActivity.java

  support/
    SupportConversationActivity.java
    SupportMessagesActivity.java

  profile/
    ProfileActivity.java
```

Use matching layout names:

```text
activity_login.xml
activity_admin_main.xml
activity_dashboard.xml
activity_product_management.xml
activity_product_form.xml
activity_user_management.xml
activity_user_detail.xml
activity_transaction_list.xml
activity_transaction_detail.xml
activity_support_conversation.xml
activity_support_messages.xml
activity_profile.xml
row_product.xml
row_user.xml
row_transaction.xml
row_support_conversation.xml
row_support_message.xml
```

## Naming Rules

Use direct, readable names:

- Activities: `LoginActivity`, `ProductManagementActivity`, `UserDetailActivity`.
- Layout files: `activity_login.xml`, `activity_product_management.xml`.
- Row layouts: `row_product.xml`, `row_user.xml`.
- View IDs: `edtEmail`, `edtPassword`, `btnLogin`, `lvProducts`, `spStatus`, `tvOrderNumber`.
- Adapter names: `ProductAdapter`, `UserAdapter`, `TransactionAdapter`.
- Model names: `Product`, `User`, `PaymentTransaction`.
- Helper methods: `initViews`, `loadUsers`, `fillUsersToListView`, `saveProduct`, `showMessage`.

## Backend Rules

Use these base URLs:

- Android emulator calling host machine backend: `http://10.0.2.2:8080`
- Local backend outside emulator: `http://localhost:8080`

Authentication rules:

- Login with `POST /api/auth/login`.
- Store access token, refresh token, role list, and user JSON in `SharedPreferences`.
- Send protected API calls with `Authorization: Bearer <token>`.
- Allow only users with `ADMIN` or `STAFF`.
- `ADMIN` can access every screen.
- `STAFF` can access shared screens but must not see user management.
- On `401`, clear the session and return to `LoginActivity`.
- On `403`, keep the session and show an access denied `Toast`.

Error handling:

- Show short `Toast` messages for users.
- Print or log detailed backend errors for debugging.
- Validate required form fields before sending API calls.
- Wrap number, date, and JSON parsing in `try/catch`.

## Feature Workflow

Follow this workflow for every feature:

1. Find the matching React admin file under `ref/e-shop/admin/src`.
2. Read the matching backend API doc under `ref/e-shop/backend/e-shop/docs`.
3. If the docs are incomplete, inspect the matching Spring controller and DTO.
4. Read the closest Android example under `ref-android`.
5. Implement the smallest complete Android slice.
6. Use one Activity and one XML layout unless the feature clearly needs a detail screen.
7. Add one `BaseAdapter` if the screen displays a list.
8. Add plain model classes only for data used by the screen.
9. Check `ADMIN` and `STAFF` visibility rules.
10. Run `.\gradlew.bat assembleDebug`.
11. Fill out `docs/android-admin-feature-checklist.md` in the task notes or pull request.

## Feature Areas

Implement parity in this order:

1. Auth and session.
2. Admin main menu or app shell.
3. Dashboard analytics summary and revenue list.
4. Product management, product form, media, variants, and stock adjustment.
5. User management and role/status updates for `ADMIN`.
6. Payment transaction list and detail.
7. Support conversations and messages with manual refresh.
8. Admin profile and password update.

## Verification

Run this after code changes:

```powershell
.\gradlew.bat assembleDebug
```

Run unit tests when helper logic is added:

```powershell
.\gradlew.bat test
```

Manual checks for every feature:

- Login/session still works.
- Protected requests include the bearer token.
- `ADMIN` and `STAFF` visibility is correct.
- Empty states and API errors are readable.
- Success actions refresh the list or return to the previous screen.
- `401` returns to login.
- `403` shows access denied.

## Handoff Notes

Every teammate handoff should include:

- Feature area implemented.
- React admin file used.
- Backend doc or controller used.
- Android reference project used.
- Activities, layouts, models, and adapters changed.
- Build command and result.
- Manual test notes.
