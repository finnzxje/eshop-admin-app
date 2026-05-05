# Admin Parity Map

Use this file to map React admin behavior to native Android screens. Read the referenced React files before implementing the matching Android feature.

## App Shell

React source:

- `main/admin/src/App.tsx`
- `main/admin/src/pages/admin/AdminLayout.tsx`
- `main/admin/src/components/layout/Sidebar.tsx`
- `main/admin/src/components/layout/Header.tsx`
- `main/admin/src/router/privateRoute.tsx`
- `main/admin/src/context/useContext.tsx`

Android target:

- Login activity as public entry.
- Main admin activity after successful login.
- Bottom navigation or simple navigation list for Dashboard, Products, Orders, Support, Profile, and Users.
- Hide Users unless the logged-in user has `ADMIN`.
- Store user and token with `SharedPreferences`.

## Login

React source:

- `main/admin/src/pages/login.tsx`

Android target:

- `LoginActivity` with email/password `EditText`, login `Button`, loading/disabled state if simple to add.
- `POST /api/auth/login`.
- Reject users without `ADMIN` or `STAFF` role.
- Save token and user data in `SharedPreferences`.
- Navigate to main admin activity with `Intent`.

## Dashboard

React source:

- `main/admin/src/pages/admin/Dashboard.tsx`

Android target:

- Summary cards using `TextView` groups in a vertical XML layout.
- Period selector with `Spinner` or simple buttons.
- Revenue trend can start as a `ListView` of date/gross/order count buckets if charts would require an advanced library. Ask before adding chart libraries.

## Users

React source:

- `main/admin/src/pages/admin/userManagement/UserManagement.tsx`
- `main/admin/src/pages/admin/userManagement/UserDetail.tsx`
- `main/admin/src/pages/admin/userManagement/RoleEditModal.tsx`

Android target:

- `UserManagementActivity` or fragment with search/status/role filters and `ListView`.
- `UserDetailActivity` for profile, roles, enabled state, and addresses.
- Use `AlertDialog` or a simple second screen for role editing.
- Only visible to `ADMIN`.

## Products

React source:

- `main/admin/src/pages/admin/productsMannagement/ProductManagement.tsx`
- `main/admin/src/pages/admin/productsMannagement/ProductCreate.tsx`
- `main/admin/src/pages/admin/productsMannagement/ProductEdit.tsx`
- `main/admin/src/pages/admin/productsMannagement/ProductForm.tsx`
- `main/admin/src/pages/admin/productsMannagement/ProductMediaManagement.tsx`
- `main/admin/src/pages/admin/productsMannagement/ProductVariantManagement.tsx`
- `main/admin/src/pages/admin/productsMannagement/types.ts`

Android target:

- Product list with filters and `ListView`/`GridView`.
- Product create/edit form with `EditText`, `Spinner`, `CheckBox`, and simple tag input.
- Product status action using buttons or a `Spinner`.
- Product media screen for list/upload/delete/update image metadata. Ask before adding image picker/upload helper libraries.
- Product variants screen grouped by color, using nested simple lists or separate screens for color groups.
- Stock adjustment form with `EditText` quantity/reason/notes.

## Orders And Transactions

React source:

- `main/admin/src/pages/admin/managerOrder/ManagerOrder.tsx`
- `main/admin/src/pages/admin/managerOrder/TransactionDetail.tsx`

Android target:

- Transaction list with filters for status/method/provider/order number and date range if requested.
- Transaction detail screen showing order/payment/customer/address/items fields returned by the backend.
- Keep list row layouts compact and readable.

## Support

React source:

- `main/admin/src/pages/admin/AdminSupportChat.tsx`

Android target:

- Conversation list with status filter and assigned/all toggle.
- Messages screen with `ListView`.
- Send message with `EditText` and `Button`.
- Use REST polling or manual refresh first. WebSocket/STOMP is advanced; ask before adding it.

## Profile

React source:

- `main/admin/src/pages/admin/UserProfile.tsx`

Android target:

- Profile form for current admin/staff user.
- Password change form.
- Keep separate save buttons for profile and password.
