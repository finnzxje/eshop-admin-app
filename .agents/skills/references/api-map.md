# E-Shop Admin API Map

Use docs in `main/backend/e-shop/docs` as the first source of truth. If a detail is missing, read the Spring controller and DTO source under `main/backend/e-shop/src/main/java/com/eshop/api`.

## Runtime

- Backend local URL: `http://localhost:8080`.
- Android emulator URL for host machine backend: `http://10.0.2.2:8080`.
- Swagger UI, when backend is running: `http://localhost:8080/swagger-ui.html`.
- Default admin credentials from `main/README.md`: `admin@gmail.com` / `123456`.

## Auth

Docs:

- `main/backend/e-shop/docs/auth-api.md`

Endpoints:

- `POST /api/auth/login`
- `POST /api/auth/refresh`
- `GET /api/auth/me`

Login response includes `token`, `refreshToken`, and `roles`. Persist the token and user fields in `SharedPreferences`. Require `ADMIN` or `STAFF` for the Android admin app. Send protected calls with:

```text
Authorization: Bearer <token>
```

## Dashboard Analytics

Docs:

- `main/backend/e-shop/docs/admin-analytics-api.md`

Endpoints:

- `GET /api/admin/analytics/summary?period=30d`
- `GET /api/admin/analytics/revenue?start=<iso>&end=<iso>&interval=daily`

Notes:

- Use `period` values like `1d`, `7d`, `30d`, `24h`.
- `interval` is `daily` or `weekly`.
- If charting is too advanced, display revenue time series in a `ListView`.

## Users

Docs:

- `main/backend/e-shop/docs/admin-users-api.md`

Endpoints:

- `GET /api/admin/users`
- `GET /api/admin/users/{userId}`
- `PATCH /api/admin/users/{userId}/status`
- `PUT /api/admin/users/{userId}/roles`

Only `ADMIN` users should see these screens.

## Products, Images, Colors, Variants

Docs:

- `main/backend/e-shop/docs/admin-products-api.md`
- `main/backend/e-shop/docs/api.md`

Public/supporting endpoints:

- `GET /api/catalog/categories`

Admin product endpoints:

- `GET /api/admin/catalog/products`
- `GET /api/admin/catalog/products/{productId}`
- `POST /api/admin/catalog/products`
- `PUT /api/admin/catalog/products/{productId}`
- `PATCH /api/admin/catalog/products/{productId}/status`

Media/color endpoints:

- `GET /api/admin/catalog/colors`
- `POST /api/admin/catalog/products/{productId}/images`
- `GET /api/admin/catalog/products/{productId}/colors`
- `PATCH /api/admin/catalog/products/{productId}/images/{imageId}`
- `DELETE /api/admin/catalog/products/{productId}/images/{imageId}`

Variant endpoints:

- `GET /api/admin/catalog/products/{productId}/variants`
- `POST /api/admin/catalog/products/{productId}/variants`
- `PUT /api/admin/catalog/products/{productId}/variants/{variantId}`
- `PATCH /api/admin/catalog/products/{productId}/variants/{variantId}/status`
- `DELETE /api/admin/catalog/products/{productId}/variants/{variantId}`
- `POST /api/admin/catalog/products/{productId}/variants/{variantId}/stock-adjustments`
- `GET /api/admin/catalog/products/{productId}/variants/{variantId}/stock-adjustments`

Important payload fields:

- Product: `name`, `slug`, `description`, `basePrice`, `categoryId`, `status`, `featured`, `gender`, `productType`, `taxonomyPath`, `tags`.
- Status body: `{ "status": "active" }`.
- Variant bulk create: `colorId`, `variants[]` with `size`, `sku`, `price`, `quantity`, `active`, `currency`.
- Stock adjustment: `newQuantity`, `reason`, `notes`.

## Orders And Payments

Docs:

- `main/backend/e-shop/docs/orders-payments-api.md`
- Controller: `main/backend/e-shop/src/main/java/com/eshop/api/order/admin/AdminPaymentTransactionController.java`

Admin endpoints:

- `GET /api/admin/payments/transactions`
- `GET /api/admin/payments/transactions/{transactionId}`
- `GET /api/admin/orders/{orderNumber}/transactions`

Common filters:

- `status`, `method`, `provider`, `orderNumber`, `createdAfter`, `createdBefore`, plus Spring pageable `page`, `size`, `sort`.

## Support Messaging

Docs:

- `main/backend/e-shop/docs/support-messaging-api.md`

REST endpoints:

- `GET /api/admin/support/conversations`
- `GET /api/admin/support/conversations/assigned`
- `GET /api/support/conversations/{conversationId}/messages`
- `POST /api/support/conversations/{conversationId}/messages`
- `PATCH /api/admin/support/conversations/{conversationId}/status`

Prefer REST/manual refresh first. WebSocket/STOMP exists at `/ws` but should be treated as advanced and used only with approval.

## Profile

React source:

- `main/admin/src/pages/admin/UserProfile.tsx`

Endpoints:

- `GET /api/account/profile`
- `PUT /api/account/profile`
- `PATCH /api/account/profile/password`

## Error Handling

Backend errors use the global `ErrorResponse` envelope. In Android:

- Show a short `Toast` message.
- Log or print the detailed message for debugging.
- For `401`, clear saved token and return to login.
- For `403`, keep the user signed in but show an access denied message.
