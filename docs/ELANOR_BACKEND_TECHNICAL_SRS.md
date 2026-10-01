# ELANOR — TECHNICAL SOFTWARE REQUIREMENTS SPECIFICATION (SRS)

**Document:** Backend SRS  
**Project:** Elanor B2C E-Commerce Platform  
**Version:** 1.0  
**Status:** Implementation Baseline  
**Backend:** Java + Spring Boot + PostgreSQL + REST API  
**Initial Runtime:** Local / demo only  
**Primary Implementation Target:** Google Antigravity AI Agent  
**Architecture Style:** Modular monolith with replaceable provider adapters  
**AI Delivery Strategy:** Core backend first; AI Shopping Assistant after core backend completion

---

## 0. IMPORTANT — HOW THE AI AGENT MUST USE THIS SRS

This document is the authoritative backend implementation specification for the current Elanor project.

### Mandatory execution rules

1. **Inspect the existing repository before changing code.**
2. **Do not delete or rewrite working frontend functionality unnecessarily.**
3. **Do not invent business rules when this SRS already defines them.**
4. If this SRS does not define a detail, prefer the simplest extensible implementation and record the decision before introducing a new business rule.
5. Implement the backend as a **modular monolith**, not microservices.
6. Keep all business-critical calculations and validations on the backend.
7. Never trust prices, discounts, stock, totals, payment status, coupon validity, shipping charges, or permissions supplied by the client.
8. Use database transactions for operations that modify multiple business records.
9. Use idempotency for payment/order-sensitive operations.
10. Keep external providers behind interfaces/adapters so future Razorpay and Shiprocket integrations do not require changing customer-facing business flows.
11. Do **not** implement AI before the core backend is stable and tested.
12. Do **not** add external integrations in V1.
13. Do **not** add unnecessary microservices, message brokers, Kubernetes, distributed tracing infrastructure, or complex event-driven architecture.
14. Work feature-by-feature:
    - inspect
    - plan
    - implement
    - test
    - validate
    - document
15. After every major phase, run tests and compile/build checks.
16. Never claim a feature is complete if it is only a stub, mock, TODO, or UI simulation.
17. Preserve API contracts once frontend integration begins. Breaking changes require explicit versioning or migration.
18. Secrets must never be committed to source control.

### Required agent behavior

Before implementation:
- inspect repository structure;
- identify whether a backend already exists;
- inspect frontend API assumptions and existing domain models;
- inspect environment/configuration files;
- identify reusable code;
- produce a short implementation plan.

Then implement the smallest complete vertical slice.

Do not generate hundreds of files blindly.

---

# 1. PROJECT PURPOSE

Elanor is a **B2C e-commerce platform** with:

- customer storefront;
- customer authentication;
- guest shopping;
- guest cart and wishlist;
- product catalog;
- variants/SKUs;
- search and filters;
- wishlist;
- cart;
- checkout;
- demo online payment;
- Cash on Delivery;
- order management;
- manual shipping/tracking;
- cancellation;
- returns;
- replacement/exchange;
- manual refunds;
- reviews;
- coupons;
- CMS;
- blog/editorial content;
- email notifications;
- admin operations;
- analytics;
- audit logs;
- RBAC;
- future AI Shopping Assistant;
- future Razorpay integration;
- future Shiprocket-compatible shipping integration.

The backend is the source of truth for business data and business rules.

---

# 2. CONFIRMED PRODUCT DECISIONS

These decisions are fixed for V1 unless explicitly changed later.

| Area | Decision |
|---|---|
| Business model | B2C |
| Backend | Spring Boot |
| Database | PostgreSQL |
| API style | REST |
| Deployment initially | Local/demo |
| Customer login | Email + password + Phone OTP + Google |
| Email verification | Yes |
| Password reset | Yes |
| Saved addresses | Multiple |
| Customer profile | Name + email + phone |
| Product variants | Different variants depending on product |
| Product statuses | Draft, Scheduled, Active, Out of Stock, Archived |
| Categories | Categories + subcategories + collections |
| Scheduled publishing | Yes |
| Search V1 | Basic keyword search |
| AI search | Later |
| Filters | Category + price + attributes + availability + rating |
| Guest wishlist | Yes |
| Reviews | Anyone |
| Review moderation | Admin approval before publication |
| Review media | Images + videos |
| Coupons | Percentage + fixed + minimum order + product/category-specific |
| Multiple coupons | No |
| Automatic promotions | No |
| CMS | Full agreed CMS scope |
| Blog/editorial | Yes |
| V1 notifications | Email only |
| Operational notifications | Yes |
| Admin dashboard | Full analytics dashboard |
| Audit logs | Yes |
| Admin 2FA | Yes |
| Admin controls | All operational controls |
| Low-stock threshold | Configurable |
| Inventory history | Yes |
| Warehouse | One warehouse |
| Return reason | Customer selects |
| Return approval | Admin |
| Replacement/exchange | Yes |
| Free shipping | Free above configurable threshold |
| Delivery estimate | Configurable |
| Shipping V1 | Manual |
| Future shipping | Shiprocket-compatible, replaceable |
| Online payment V1 | Demo success/failure |
| COD | All orders |
| Future payment | Razorpay-compatible, replaceable |
| AI Shopping Assistant | Product questions + recommendations + comparisons + shopping intent |
| AI personalization inputs | Cart + wishlist + searches + views + purchases + profile |
| Analytics events | Views + searches + wishlist + cart + checkout + purchases + cancellations + returns |
| External integrations V1 | None |
| AI implementation | After complete core backend |
| Return window | 7 days |
| Refund | Manual by Admin |
| Cancellation | Allowed before shipping |
| Inventory reservation | During payment |
| Guest cart merge | Yes |
| COD payment | Paid only after admin confirms collection after delivery |

---

# 3. ARCHITECTURE

## 3.1 Architecture style

Use a **modular monolith**.

```text
Client / Next.js Frontend
        |
        v
REST Controllers
        |
        v
Application / Service Layer
        |
        +----------------------+
        |                      |
        v                      v
Domain / Business Rules     Provider Ports
        |                      |
        v                      v
Repositories              Manual/Demo Providers
        |                      |
        v                      v
PostgreSQL              Future Razorpay/Shiprocket
```

Do not create separate deployable services for each module.

## 3.2 Recommended package structure

```text
src/main/java/<base-package>/
├── common/
│   ├── exception/
│   ├── response/
│   ├── validation/
│   ├── security/
│   ├── audit/
│   ├── idempotency/
│   └── util/
│
├── auth/
├── customer/
├── catalog/
├── category/
├── collection/
├── inventory/
├── wishlist/
├── cart/
├── checkout/
├── coupon/
├── order/
├── payment/
├── shipping/
├── returnorder/
├── refund/
├── review/
├── cms/
├── blog/
├── notification/
├── analytics/
├── admin/
└── ai/
```

Use package-by-feature. Avoid a giant generic `service/` package.

---

# 4. TECHNOLOGY REQUIREMENTS

Use:

- Java LTS version compatible with the installed Spring Boot version;
- Spring Boot;
- Spring Web;
- Spring Data JPA;
- Hibernate/JPA;
- PostgreSQL;
- Spring Security;
- Bean Validation;
- Flyway for database migrations;
- JWT or secure server-side session strategy appropriate for the frontend architecture;
- BCrypt/Argon2-compatible password hashing;
- JUnit 5;
- Mockito where useful;
- Testcontainers for PostgreSQL integration tests if practical.

Do not add dependencies without a clear requirement.

---

# 5. DATABASE REQUIREMENTS

## 5.1 General rules

- PostgreSQL is authoritative.
- Use UUIDs for externally exposed identifiers where practical.
- Use timestamps with timezone semantics.
- Store money as `BigDecimal`, never floating-point.
- Store currency explicitly.
- Add created/updated timestamps to major entities.
- Use optimistic locking/versioning where concurrent updates are possible.
- Use database constraints in addition to application validation.
- Use indexes for frequent lookup paths.
- Use foreign keys for relational integrity.
- Never rely only on frontend validation.

## 5.2 Initial entity model

At minimum:

```text
User
Role
UserRole
CustomerProfile
Address
AuthIdentity / OAuthIdentity
OtpChallenge
EmailVerificationToken
PasswordResetToken

Category
Collection
Product
ProductVariant
ProductAttribute
ProductMedia
ProductStatusHistory

Inventory
InventoryMovement
InventoryReservation

Wishlist
WishlistItem

Cart
CartItem
CartMergeLog

Coupon
CouponProduct
CouponCategory

Order
OrderItem
OrderAddressSnapshot
OrderStatusHistory

Payment
PaymentAttempt
PaymentStatusHistory

Shipment
ShipmentEvent

ReturnRequest
ReturnItem
Refund
Replacement

Review
ReviewMedia

CmsPage
CmsSection
Banner
Campaign
BlogPost
Faq
SeoMetadata
NavigationItem

Notification
NotificationTemplate
NotificationLog

AnalyticsEvent
AuditLog

SystemSetting
IdempotencyRecord
```

Do not create entities that are not required by implemented functionality.

---

# 6. AUTHENTICATION AND CUSTOMER ACCOUNTS

## 6.1 Login methods

Support:

1. Email + password
2. Phone OTP
3. Google login

All three must resolve to one internal customer identity.

## 6.2 Registration

Required fields:

- name
- email
- phone
- password where applicable

Email verification is required.

Do not store plaintext passwords or OTPs.

## 6.3 Password reset

Flow:

```text
Request reset
    ↓
Generate short-lived reset token
    ↓
Email reset link
    ↓
Validate token
    ↓
Set new password
    ↓
Invalidate token
```

Tokens must be single-use and expire.

## 6.4 Phone OTP

OTP requirements:

- short expiration;
- one-time use;
- rate limiting;
- attempt limit;
- no plaintext persistence;
- invalidate after successful verification.

Because V1 has no external SMS integration, implement the domain/service abstraction without requiring a real SMS provider.

For local/demo mode, OTP delivery may use a development mechanism such as application logs or a local test endpoint. Clearly mark this as non-production.

## 6.5 Google

Implement a provider abstraction.

```text
SocialAuthProvider
    |
    +-- GoogleAuthProvider
```

Do not hardcode Google-specific logic throughout the customer service.

---

# 7. ROLES AND AUTHORIZATION

V1 roles:

```text
CUSTOMER
ADMIN
SUPER_ADMIN
```

Backend authorization is authoritative.

## 7.1 Customer

Can:

- manage own profile;
- manage own addresses;
- browse products;
- search/filter;
- manage wishlist;
- manage cart;
- checkout;
- view own orders;
- cancel eligible own orders;
- request returns;
- request replacement/exchange;
- view own refunds;
- submit reviews;
- manage own account.

## 7.2 Admin

Can perform operational management:

- products;
- categories;
- collections;
- inventory;
- orders;
- payments;
- shipments;
- returns;
- refunds;
- customers;
- reviews;
- coupons;
- CMS;
- campaigns;
- notifications;
- analytics;
- audit logs;
- settings.

## 7.3 Super Admin

Can additionally manage:

- admin users;
- roles/permissions;
- security settings;
- system-level configuration;
- admin 2FA configuration.

---

# 8. CATALOG

## 8.1 Category hierarchy

Support:

```text
Category
  └── Subcategory
```

Collections are independent merchandising groupings.

## 8.2 Product

Product should support:

- name;
- slug;
- description;
- short description;
- category;
- collections;
- attributes;
- media;
- variants;
- status;
- SEO metadata;
- badges;
- product-level pricing where applicable.

## 8.3 Product status

```text
DRAFT
SCHEDULED
ACTIVE
OUT_OF_STOCK
ARCHIVED
```

Scheduled products require publish/unpublish timestamps.

A scheduled product must not become customer-visible before its publish time.

## 8.4 Variants

Different products may have different variant dimensions.

Do not hardcode only size/color.

Represent variant attributes generically:

```text
Variant
  -> attribute name
  -> attribute value
```

Each sellable variant must have:

- SKU;
- price;
- inventory linkage;
- active status.

---

# 9. SEARCH AND FILTERING

## 9.1 V1 search

Use basic keyword search.

Search should support relevant product fields such as:

- product name;
- SKU;
- description;
- category;
- collection;
- searchable attributes.

AI search is explicitly deferred.

## 9.2 Filters

Support:

- category;
- price;
- product attributes;
- availability;
- rating.

Use pagination.

Do not load the entire catalog into application memory.

---

# 10. WISHLIST

Guest wishlist is supported.

Authenticated wishlist is persisted in the database.

When a guest logs in:

```text
Guest wishlist
      +
Customer wishlist
      ↓
Merge
      ↓
Deduplicate
      ↓
Persist customer wishlist
```

Do not create duplicate wishlist items.

---

# 11. CART

## 11.1 Cart types

Support:

- guest cart;
- customer cart.

Guest carts require a secure client cart identifier/token.

## 11.2 Cart merge

When a guest authenticates:

```text
Guest Cart
    +
Customer Cart
    ↓
Validate products
Validate variants
Validate active status
Validate stock
Validate current pricing
    ↓
Merge duplicate lines
    ↓
Apply stock limits
    ↓
Persist customer cart
```

If combined quantity exceeds available stock, cap or reject the excess with a clear response.

## 11.3 Cart totals

Backend calculates:

- subtotal;
- discount;
- shipping;
- tax where configured;
- final total.

Never accept client-calculated totals as authoritative.

---

# 12. CHECKOUT

Flow:

```text
Cart
 ↓
Validate cart
 ↓
Address
 ↓
Shipping calculation
 ↓
Coupon validation
 ↓
Order review
 ↓
Payment method
 ↓
Payment initiation
 ↓
Order creation
 ↓
Inventory reservation
 ↓
Confirmation
```

The exact transactional boundary must prevent duplicate orders and inconsistent inventory.

---

# 13. SHIPPING PRICING

Rule:

```text
if orderSubtotal >= configuredFreeShippingThreshold:
    shipping = 0
else:
    shipping = configuredShippingCharge
```

Both values must be configurable by admin.

Never hardcode them in the frontend.

---

# 14. DELIVERY ESTIMATE

Admin-configurable delivery estimate.

At minimum support configuration such as:

- minimum delivery days;
- maximum delivery days.

Store an estimated delivery range/snapshot on the order or shipment so historical orders do not unexpectedly change when settings change.

---

# 15. PAYMENTS

## 15.1 Provider architecture

Use:

```java
PaymentProvider
    +-- DemoPaymentProvider
    +-- CodPaymentProvider
    +-- RazorpayPaymentProvider (future)
```

Do not make Razorpay a dependency in V1.

## 15.2 Payment methods

V1:

```text
DEMO_ONLINE
COD
```

## 15.3 Demo online payment

Customer can choose:

```text
Demo Success
Demo Failure
```

This is intentionally a testing payment flow.

It must still execute the real payment state machine.

## 15.4 Payment states

```text
INITIATED
PENDING
SUCCESSFUL
FAILED
CANCELLED
REFUNDED
PARTIALLY_REFUNDED
```

## 15.5 COD

All orders can use COD.

COD lifecycle:

```text
Order created
Payment = PENDING
        ↓
Order delivered
        ↓
Admin confirms payment collected
        ↓
Payment = SUCCESSFUL
```

Do not mark COD as paid at order placement.

## 15.6 Future Razorpay

The Razorpay adapter must fit the same payment contract.

Future integration should require:

- Razorpay provider implementation;
- credentials/configuration;
- order/payment creation mapping;
- webhook verification;
- signature verification;
- idempotent webhook processing.

Customer order/checkout business flow must remain stable.

---

# 16. INVENTORY

Inventory is a business system.

V1 uses **one warehouse**.

Track at minimum:

```text
onHand
reserved
available
```

Recommended invariant:

```text
available = onHand - reserved
```

Do not allow available stock to become negative.

## 16.1 Reservation

Reservation occurs **during payment**.

Required flow:

```text
Validate stock
    ↓
Create payment attempt
    ↓
Reserve inventory
    ↓
Complete payment
    ↓
Confirm order
```

If payment fails/cancels/expires:

```text
Release reservation
```

The implementation must include a configurable reservation expiration/cleanup mechanism. Use a simple scheduled Spring task only if needed; do not introduce a message broker.

## 16.2 Inventory movements

Record:

- purchase/restock;
- reservation;
- reservation release;
- sale;
- cancellation;
- return;
- replacement;
- manual adjustment;
- damaged;
- transfer if later required.

Each movement should record:

- SKU;
- quantity;
- movement type;
- reference;
- reason;
- actor;
- timestamp.

## 16.3 Low stock

Admin-configurable threshold.

Provide low-stock status based on available quantity.

---

# 17. ORDERS

## 17.1 Order lifecycle

Primary lifecycle:

```text
CREATED
  ↓
CONFIRMED
  ↓
PROCESSING
  ↓
PACKED
  ↓
SHIPPED
  ↓
OUT_FOR_DELIVERY
  ↓
DELIVERED
```

Alternative terminal/exception states:

```text
CANCELLED
RETURNED
REFUNDED
```

Do not allow arbitrary status jumps.

## 17.2 Order snapshot

Order must preserve historical values:

- product name;
- SKU;
- variant description;
- unit price;
- quantity;
- discount;
- shipping;
- tax;
- final price;
- shipping address;
- billing address;
- coupon information where relevant.

Do not depend entirely on mutable product records for historical order display.

## 17.3 Cancellation

Customer can cancel before shipping.

Once order reaches `SHIPPED`, customer cancellation is not allowed through the normal cancellation flow.

Cancellation must:

- change order status;
- release/restock inventory according to state;
- create refund process for prepaid payments;
- preserve audit history.

---

# 18. SHIPPING AND TRACKING

V1 is manual.

## 18.1 Provider architecture

```java
ShippingProvider
    +-- ManualShippingProvider
    +-- ShiprocketShippingProvider (future)
```

## 18.2 Admin shipment data

Admin can enter:

- courier/carrier;
- tracking number;
- shipment date;
- estimated delivery;
- tracking URL if available;
- shipment events.

## 18.3 Customer tracking

Customer can see:

- shipment status;
- carrier;
- tracking number;
- estimated delivery;
- timeline/events.

Do not fabricate carrier events.

If no event is available, return a clear unavailable state.

---

# 19. RETURNS

Return window: **7 days from delivery**.

## 19.1 Return lifecycle

```text
REQUESTED
 ↓
APPROVED / REJECTED
 ↓
RETURN_IN_PROGRESS
 ↓
RECEIVED
 ↓
INSPECTED
 ↓
REFUND_INITIATED
 ↓
REFUND_COMPLETED
```

## 19.2 Customer

Customer must:

- select eligible order/item;
- provide return reason;
- request return within 7 days;
- select replacement/exchange where allowed.

## 19.3 Admin

Admin can:

- approve;
- reject;
- record return receipt;
- inspect;
- approve refund;
- process manual refund;
- approve replacement/exchange.

---

# 20. REPLACEMENT / EXCHANGE

V1 supports replacement/exchange.

The design must support:

```text
Return item
   ↓
Choose replacement/exchange
   ↓
Admin approval
   ↓
Inventory validation
   ↓
Replacement order/item
```

Do not implement an exchange by silently modifying the original order.

Preserve original order history and create a traceable replacement transaction.

---

# 21. REFUNDS

Refund is manual in V1.

## 21.1 Refund lifecycle

```text
REFUND_PENDING
 ↓
REFUND_INITIATED
 ↓
REFUND_COMPLETED
```

Admin must be able to record:

- refund amount;
- refund method/reference;
- admin actor;
- timestamp;
- notes.

The backend must prevent refunding more than the refundable amount.

Future Razorpay refund support must fit behind the payment provider abstraction.

---

# 22. COUPONS

Supported types:

- percentage;
- fixed amount;
- minimum order value;
- product-specific;
- category-specific.

Multiple coupons are **not allowed**.

Automatic promotions are **not implemented in V1**.

Backend validates:

- coupon existence;
- active dates;
- enabled state;
- minimum order;
- product/category eligibility;
- usage constraints if configured;
- duplicate application;
- maximum discount constraints.

Never trust the frontend discount value.

---

# 23. REVIEWS

Anyone can submit a review.

However, reviews are moderated before publication.

Lifecycle:

```text
PENDING
 ↓
APPROVED → PUBLISHED
or
REJECTED
```

Support:

- rating;
- title/body;
- images;
- videos;
- product association;
- moderation status;
- moderation metadata.

Do not expose rejected/pending reviews publicly.

Rating aggregation should use published reviews only.

---

# 24. CMS

CMS scope:

- homepage;
- banners;
- categories;
- collections;
- product content;
- FAQs;
- SEO;
- navigation;
- footer;
- campaigns;
- editorial/blog content.

CMS controls **content**, not the entire visual frontend layout.

Do not build a visual page builder unless separately required.

---

# 25. BLOG / EDITORIAL

Support:

- draft;
- scheduled;
- published;
- archived.

Blog posts should support:

- title;
- slug;
- content;
- cover media;
- author;
- publish time;
- SEO metadata;
- status.

---

# 26. NOTIFICATIONS

V1 channel:

```text
EMAIL ONLY
```

Notification events include:

- registration/email verification;
- password reset;
- order confirmation;
- payment status;
- shipment;
- delivery;
- cancellation;
- return;
- refund.

Use:

```text
NotificationService
    ↓
EmailProvider
```

Do not couple business services directly to a specific email vendor.

Because V1 has no external integrations, provide a local/demo email implementation that records or logs outgoing messages without requiring a real provider.

---

# 27. ADMIN DASHBOARD

Dashboard must support real backend-derived metrics.

Metrics should include:

- revenue;
- orders;
- average order value;
- customers;
- products;
- inventory;
- low stock;
- returns;
- refunds;
- top products;
- category performance;
- customer trends;
- campaign performance where data exists.

Never generate fake metrics for presentation.

Analytics queries must be efficient and paginated/aggregated appropriately.

---

# 28. ANALYTICS EVENTS

Track:

```text
PRODUCT_VIEW
SEARCH
WISHLIST_ADD
WISHLIST_REMOVE
CART_ADD
CART_REMOVE
CART_UPDATE
CHECKOUT_START
PURCHASE
ORDER_CANCELLED
RETURN_REQUESTED
```

Event payload should support:

- anonymous/customer identity;
- session ID where applicable;
- event type;
- product/SKU reference where applicable;
- metadata;
- timestamp.

Avoid storing unnecessary personal data.

---

# 29. AUDIT LOGS

Audit important admin/business mutations.

Record:

- actor;
- action;
- entity type;
- entity ID;
- before/after values where appropriate;
- timestamp;
- request metadata where safe.

Audit logging must not expose secrets, passwords, payment credentials, OTPs, or sensitive tokens.

---

# 30. ADMIN 2FA

Admin accounts require 2FA.

Preferred implementation:

- TOTP-based MFA;
- recovery mechanism;
- secure secret storage.

Do not implement 2FA using plaintext secrets.

If an external SMS provider is not available, do not make SMS-based admin 2FA a V1 dependency.

---

# 31. SECURITY REQUIREMENTS

## Mandatory

- password hashing;
- secure authentication;
- role-based authorization;
- endpoint authorization;
- input validation;
- output-safe error responses;
- rate limiting for authentication-sensitive operations;
- CORS configured explicitly;
- CSRF strategy appropriate to authentication architecture;
- secure headers;
- no secrets in frontend;
- no secrets in git;
- no SQL injection;
- no trusting client totals;
- no insecure direct object access;
- ownership checks for customer resources.

## Customer ownership

A customer must only access:

- their own profile;
- their own addresses;
- their own cart;
- their own wishlist;
- their own orders;
- their own returns;
- their own reviews where applicable.

Admin endpoints must never rely on client-provided role claims without server-side verification.

---

# 32. REST API DESIGN

Base path:

```text
/api/v1
```

Use consistent JSON responses.

Example success:

```json
{
  "success": true,
  "data": {},
  "message": "Success"
}
```

Example error:

```json
{
  "success": false,
  "error": {
    "code": "PRODUCT_OUT_OF_STOCK",
    "message": "The selected product is currently unavailable.",
    "details": {}
  },
  "traceId": "..."
}
```

Do not expose stack traces in production responses.

---

# 33. API MODULES

Minimum endpoint groups:

```text
/api/v1/auth/*
/api/v1/customers/*
/api/v1/addresses/*
/api/v1/categories/*
/api/v1/collections/*
/api/v1/products/*
/api/v1/search/*
/api/v1/wishlist/*
/api/v1/cart/*
/api/v1/checkout/*
/api/v1/coupons/*
/api/v1/orders/*
/api/v1/payments/*
/api/v1/shipments/*
/api/v1/returns/*
/api/v1/refunds/*
/api/v1/reviews/*
/api/v1/cms/*
/api/v1/blog/*
/api/v1/notifications/*
/api/v1/analytics/*
/api/v1/admin/*
```

Admin endpoints should be explicitly separated by authorization rules.

---

# 34. CORE API CONTRACTS

## Authentication

```text
POST /auth/register
POST /auth/login
POST /auth/logout
POST /auth/refresh
POST /auth/verify-email
POST /auth/forgot-password
POST /auth/reset-password
POST /auth/request-otp
POST /auth/verify-otp
POST /auth/google
```

## Customer

```text
GET  /customers/me
PUT  /customers/me
GET  /customers/me/addresses
POST /customers/me/addresses
PUT  /customers/me/addresses/{id}
DELETE /customers/me/addresses/{id}
```

## Catalog

```text
GET /products
GET /products/{slug}
GET /categories
GET /categories/{slug}
GET /collections
GET /collections/{slug}
GET /search
```

## Wishlist

```text
GET /wishlist
POST /wishlist/items
DELETE /wishlist/items/{productVariantId}
```

## Cart

```text
GET /cart
POST /cart/items
PATCH /cart/items/{id}
DELETE /cart/items/{id}
POST /cart/merge
POST /cart/coupon
DELETE /cart/coupon
```

## Checkout

```text
POST /checkout/validate
POST /checkout/quote
POST /checkout/order
```

## Orders

```text
GET /orders
GET /orders/{id}
POST /orders/{id}/cancel
POST /orders/{id}/return
POST /orders/{id}/replacement
```

## Payments

```text
POST /payments/initiate
POST /payments/{id}/demo-result
GET  /payments/{id}
```

Future:

```text
POST /payments/webhook/razorpay
```

Do not implement the Razorpay webhook in V1.

## Reviews

```text
GET  /products/{productId}/reviews
POST /products/{productId}/reviews
```

## Admin

Use `/api/v1/admin/...` for management APIs.

Examples:

```text
/admin/products
/admin/categories
/admin/collections
/admin/inventory
/admin/orders
/admin/payments
/admin/shipments
/admin/returns
/admin/refunds
/admin/reviews
/admin/coupons
/admin/cms
/admin/blog
/admin/customers
/admin/analytics
/admin/audit-logs
/admin/users
/admin/settings
```

---

# 35. ERROR CODES

Create stable machine-readable error codes.

Examples:

```text
AUTH_INVALID_CREDENTIALS
AUTH_EMAIL_NOT_VERIFIED
AUTH_OTP_EXPIRED
AUTH_OTP_INVALID
AUTH_TOKEN_EXPIRED

PRODUCT_NOT_FOUND
PRODUCT_NOT_ACTIVE
PRODUCT_OUT_OF_STOCK
VARIANT_NOT_FOUND

CART_NOT_FOUND
CART_ITEM_NOT_FOUND
CART_STOCK_CHANGED
CART_PRICE_CHANGED

COUPON_INVALID
COUPON_EXPIRED
COUPON_NOT_ELIGIBLE
COUPON_ALREADY_APPLIED

CHECKOUT_INVALID
ADDRESS_INVALID
SHIPPING_UNAVAILABLE

PAYMENT_FAILED
PAYMENT_PENDING
PAYMENT_ALREADY_PROCESSED
PAYMENT_INVALID_STATE

ORDER_NOT_FOUND
ORDER_CANNOT_CANCEL
ORDER_ALREADY_CANCELLED

RETURN_NOT_ELIGIBLE
RETURN_WINDOW_EXPIRED
RETURN_ALREADY_REQUESTED

REFUND_AMOUNT_EXCEEDED
REFUND_INVALID_STATE

FORBIDDEN
RESOURCE_NOT_FOUND
VALIDATION_ERROR
CONFLICT
INTERNAL_ERROR
```

---

# 36. IDEMPOTENCY

Idempotency is mandatory for:

- order creation;
- payment initiation;
- payment result processing;
- future payment webhooks;
- refund creation;
- other financial mutation endpoints.

Use an idempotency key stored server-side.

Repeated requests with the same valid idempotency key must not create duplicate business transactions.

---

# 37. CONCURRENCY

Important race conditions:

- two customers buying the last unit;
- simultaneous cart checkout;
- payment callback duplication;
- duplicate refund;
- duplicate order submission.

Use transactions and appropriate locking/versioning.

Inventory reservation must be atomic.

---

# 38. CONFIGURATION

Do not hardcode business configuration.

Configurable values include:

```text
shipping.free-threshold
shipping.below-threshold-charge
shipping.estimated-min-days
shipping.estimated-max-days
inventory.low-stock-threshold
inventory.reservation-timeout
returns.window-days = 7
```

Use environment variables for secrets.

Use database-backed settings for admin-configurable business values where appropriate.

---

# 39. PROVIDER ABSTRACTIONS

## Payment

```java
interface PaymentProvider {
    PaymentInitiationResult initiate(PaymentRequest request);
    PaymentVerificationResult verify(PaymentVerificationRequest request);
    RefundResult refund(RefundRequest request);
}
```

V1:

```text
DemoPaymentProvider
CodPaymentProvider
```

Future:

```text
RazorpayPaymentProvider
```

## Shipping

```java
interface ShippingProvider {
    Shipment createShipment(ShipmentRequest request);
    ShipmentTracking getTracking(String trackingId);
    Shipment cancelShipment(String shipmentId);
}
```

V1:

```text
ManualShippingProvider
```

Future:

```text
ShiprocketShippingProvider
```

## Email

```java
interface EmailProvider {
    void send(EmailMessage message);
}
```

V1:

```text
DevelopmentEmailProvider
```

Future:

```text
RealEmailProvider
```

---

# 40. AI ARCHITECTURE — DEFER IMPLEMENTATION

Do not implement AI until all core backend modules are functional and tested.

AI module should be isolated:

```text
ai/
├── assistant/
├── recommendation/
├── intent/
├── context/
└── provider/
```

First AI feature:

## AI Shopping Assistant

Capabilities:

1. Product questions
2. Product recommendations
3. Product comparisons
4. Shopping-intent understanding

Potential customer context:

- profile;
- cart;
- wishlist;
- searches;
- views;
- purchases.

AI must retrieve authoritative product/business information from backend services.

AI must never invent:

- product price;
- inventory;
- shipping status;
- order status;
- refund status;
- policy;
- specifications.

AI should call internal application services/tools rather than directly reading arbitrary database tables.

---

# 41. AI SAFETY / BUSINESS TRUTH

For example:

```text
User:
"Where is my order?"

AI
 ↓
OrderService
 ↓
Actual order state
 ↓
Assistant response
```

Never:

```text
AI guesses order status
```

For product questions:

```text
AI
 ↓
CatalogService
 ↓
authoritative product data
 ↓
answer
```

---

# 42. DATABASE MIGRATIONS

Use Flyway.

Rules:

- every schema change gets a migration;
- never manually edit already-applied migrations;
- migration names must be sequential and descriptive;
- seed data must be deterministic;
- production-sensitive seed credentials must not be committed.

Example:

```text
V1__initial_schema.sql
V2__seed_roles.sql
V3__catalog_tables.sql
...
```

---

# 43. SEED DATA

Local/demo mode should provide enough seed data to test:

- admin;
- super admin;
- customer;
- categories;
- collections;
- products;
- variants;
- inventory;
- coupons;
- shipping configuration.

Do not seed fake analytics/revenue history merely to make dashboards look populated.

---

# 44. TESTING STRATEGY

## Unit tests

Test:

- pricing;
- coupon rules;
- inventory;
- cart merge;
- shipping calculation;
- return eligibility;
- cancellation;
- payment state transitions;
- refund limits;
- authorization;
- product scheduling.

## Integration tests

At minimum test:

```text
registration
login
cart
cart merge
checkout
inventory reservation
demo payment success
demo payment failure
COD
order creation
order cancellation
shipping
return
replacement
manual refund
review moderation
coupon
admin authorization
```

Use PostgreSQL-compatible integration testing where practical.

---

# 45. CRITICAL BUSINESS TEST CASES

### Cart merge

```text
Guest has SKU-A qty 2
Customer has SKU-A qty 1
Stock = 5

Expected:
Customer cart SKU-A qty 3
```

If:

```text
Guest qty 4
Customer qty 3
Stock = 5
```

Expected:

```text
Final qty <= 5
No overselling
Clear response
```

### Inventory

```text
onHand = 10
reserved = 2
available = 8
```

Reserve 3:

```text
reserved = 5
available = 5
```

Payment failure:

```text
reserved = 2
available = 8
```

### COD

```text
Order created
Payment = PENDING
Delivery completed
Admin confirms collection
Payment = SUCCESSFUL
```

### Cancellation

Before shipping:

```text
Allowed
```

After shipping:

```text
Rejected
```

### Return

Delivered + within 7 days:

```text
Eligible
```

Delivered + after 7 days:

```text
Not eligible
```

### Coupon

Two coupons:

```text
Rejected
```

### Review

Submitted:

```text
PENDING
```

Admin approves:

```text
PUBLISHED
```

---

# 46. OBSERVABILITY

For V1 local/demo:

- structured application logs;
- request correlation/trace ID;
- meaningful error logs;
- no sensitive data logging.

Do not add a complex observability stack unless required.

---

# 47. API DOCUMENTATION

Use OpenAPI/Swagger if compatible with the selected Spring Boot version.

Document:

- authentication;
- request/response DTOs;
- errors;
- pagination;
- important state transitions;
- admin/customer authorization.

API documentation must reflect actual implemented behavior.

---

# 48. PAGINATION AND FILTERING

Collection endpoints must support pagination.

Recommended:

```text
page
size
sort
```

Use safe maximum page size.

Example:

```text
GET /api/v1/products?page=0&size=20&sort=createdAt,desc
```

Never allow unlimited database reads from public endpoints.

---

# 49. DTO RULE

Do not expose JPA entities directly from controllers.

Use:

```text
Request DTO
    ↓
Service
    ↓
Domain/entity
    ↓
Response DTO
```

This protects the API from persistence-layer coupling.

---

# 50. TRANSACTION RULES

Use transactional boundaries around:

- order creation;
- inventory reservation;
- cancellation;
- return approval;
- refund creation;
- replacement creation;
- cart merge where multiple rows change;
- payment state transitions.

Do not use one enormous transaction for unrelated operations.

---

# 51. FRONTEND COMPATIBILITY

The existing Next.js frontend should eventually consume the backend through REST.

Do not force frontend rewrites.

Backend DTOs should be stable, predictable, and frontend-friendly.

The backend must not expose internal database implementation details.

The existing frontend blueprint explicitly requires the backend to own pricing, inventory, order creation, payment verification, authorization, coupon validation, refund logic, shipping rules, tax rules, and transactional integrity. fileciteturn1file2L1-L20

The source blueprint also defines the future backend as Spring Boot + PostgreSQL + JPA/Hibernate + Spring Security + REST APIs + CMS. fileciteturn1file2L1-L20

---

# 52. FREE-TIER / COLLEGE-PROJECT CONSTRAINT

The architecture must be production-quality in code structure without requiring production infrastructure.

V1 must run locally with:

```text
Java
Spring Boot
PostgreSQL
```

Prefer:

```text
Docker Compose
```

for local PostgreSQL only if Docker is available.

Otherwise provide normal local PostgreSQL setup.

Do not require:

- Kubernetes;
- Kafka;
- Redis unless a demonstrated requirement exists;
- Elasticsearch;
- cloud queues;
- paid APIs;
- paid monitoring;
- paid payment provider;
- paid shipping provider.

---

# 53. IMPLEMENTATION PHASES

## Phase 0 — Repository audit

Deliver:

- repository assessment;
- backend status;
- frontend API dependencies;
- proposed package structure;
- configuration strategy.

Do not implement yet.

## Phase 1 — Project foundation

Implement:

- Spring Boot;
- PostgreSQL;
- Flyway;
- base packages;
- global errors;
- validation;
- API response format;
- configuration;
- health endpoint;
- OpenAPI.

## Phase 2 — Authentication + RBAC

Implement:

- customer registration;
- email verification;
- login;
- logout;
- password reset;
- phone OTP abstraction;
- Google abstraction;
- JWT/session strategy;
- roles;
- admin 2FA;
- authorization.

## Phase 3 — Catalog

Implement:

- category;
- subcategory;
- collection;
- product;
- variants;
- SKU;
- media metadata;
- statuses;
- scheduling;
- admin catalog APIs;
- public catalog APIs.

## Phase 4 — Inventory

Implement:

- stock;
- available;
- reserved;
- movements;
- reservation;
- low-stock;
- admin controls.

## Phase 5 — Wishlist + Cart

Implement:

- guest cart;
- customer cart;
- guest wishlist;
- customer wishlist;
- merge;
- validation;
- authoritative totals.

## Phase 6 — Search + Coupons

Implement:

- keyword search;
- filters;
- pagination;
- coupons;
- eligibility;
- discount calculation.

## Phase 7 — Checkout

Implement:

- addresses;
- shipping quote;
- delivery estimate;
- checkout validation;
- order creation.

## Phase 8 — Payments + Orders

Implement:

- payment abstraction;
- demo success/failure;
- COD;
- payment states;
- order state machine;
- cancellation;
- idempotency.

## Phase 9 — Shipping

Implement:

- manual shipment;
- tracking;
- events;
- delivery estimate;
- customer tracking;
- shipping abstraction.

## Phase 10 — Returns / Replacement / Refund

Implement:

- 7-day eligibility;
- return request;
- admin approval;
- replacement;
- manual refund;
- refund records.

## Phase 11 — Reviews + CMS + Blog

Implement:

- reviews;
- moderation;
- media;
- CMS;
- banners;
- campaigns;
- FAQs;
- SEO metadata;
- navigation/footer;
- blog/editorial.

## Phase 12 — Notifications + Analytics + Audit

Implement:

- email abstraction;
- local email provider;
- transactional notification events;
- analytics events;
- admin analytics;
- audit logs.

## Phase 13 — Core hardening

Run:

- unit tests;
- integration tests;
- security review;
- concurrency tests;
- idempotency tests;
- API contract review;
- migration verification;
- error handling review.

## Phase 14 — AI Shopping Assistant

Only after Phase 13 is stable.

Implement:

- intent;
- product Q&A;
- recommendations;
- comparison;
- shopping context;
- controlled tool/service access.

## Phase 15 — Future provider readiness

Do not integrate external providers yet.

Validate that:

```text
DemoPaymentProvider
        ↓
can be replaced by
        ↓
RazorpayPaymentProvider
```

and:

```text
ManualShippingProvider
        ↓
can be replaced by
        ↓
ShiprocketShippingProvider
```

without rewriting checkout/order business logic.

---

# 54. DEFINITION OF DONE

A module is complete only when:

- code compiles;
- database migration works;
- API endpoint works;
- validation exists;
- authorization exists;
- business rules are enforced server-side;
- error handling exists;
- tests cover critical behavior;
- no placeholder TODO is being counted as implementation;
- API documentation is updated;
- frontend integration contract is clear;
- logs are meaningful;
- sensitive information is not exposed.

---

# 55. GLOBAL “DO NOT” LIST

The AI agent MUST NOT:

- create microservices;
- implement Razorpay now;
- implement Shiprocket now;
- add paid integrations;
- implement AI before core backend completion;
- trust frontend totals;
- trust frontend inventory;
- trust frontend payment status;
- expose secrets;
- store plaintext passwords;
- store plaintext OTPs;
- allow customers to access another customer's resources;
- allow negative inventory;
- allow duplicate financial transactions;
- allow multiple coupons;
- mark COD paid before admin confirmation;
- allow cancellation after shipping;
- allow returns after the 7-day window;
- expose unmoderated reviews publicly;
- fabricate tracking events;
- fabricate analytics;
- expose JPA entities directly as public API contracts;
- introduce unnecessary infrastructure;
- make external integrations mandatory for local V1;
- rewrite the frontend unnecessarily;
- create fake functionality only for presentation.

---

# 56. FUTURE INTEGRATION CONTRACT

The architecture must preserve these future replacement points:

```text
PaymentProvider
    |
    +-- DemoPaymentProvider [V1]
    +-- CodPaymentProvider [V1]
    +-- RazorpayPaymentProvider [Future]

ShippingProvider
    |
    +-- ManualShippingProvider [V1]
    +-- ShiprocketShippingProvider [Future]

EmailProvider
    |
    +-- DevelopmentEmailProvider [V1]
    +-- RealEmailProvider [Future]

AIProvider
    |
    +-- Local/Mock [development]
    +-- RealAIProvider [future implementation]
```

Customer-facing services should depend on interfaces, not concrete external providers.

---

# 57. FINAL AGENT INSTRUCTION

Build Elanor backend as a **clean, testable, modular Spring Boot monolith backed by PostgreSQL**.

The implementation priority is:

```text
Correctness
    >
Security
    >
Data integrity
    >
Business rules
    >
API stability
    >
Testability
    >
Performance
    >
Convenience
```

Do not optimize prematurely.

Do not add complexity merely because this is described as “production-grade”.

The goal is:

> **A complete backend that can run locally on a free-tier/college-project setup today, while retaining clean architectural seams for Razorpay, Shiprocket, real email, and AI tomorrow.**

When uncertain, inspect the repository and this SRS first. Do not guess.
