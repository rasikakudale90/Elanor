# Élanor — Development Progress & Repository Memory

> **Product Name:** Élanor  
> **Brand Slogan:** Pure Beauty. Naturally.  
> **Brand Essence:** Haute Botanique & Clinical Cellular Longevity  
> **Architecture:** Modular Monolith with Replaceable Provider Ports (Spring Boot 3.3.5 + Java 21 + PostgreSQL + Flyway)  
> **Current Status:** Backend Phases 1 through 8 Complete & Verified (37/37 Tests Passing, 100% Clean Git Tree)  
> **Repository:** [https://github.com/rasikakudale90/Elanor](https://github.com/rasikakudale90/Elanor)  
> **Authoritative Specification:** [`docs/ELANOR_BACKEND_TECHNICAL_SRS.md`](file:///e:/Elanor/docs/ELANOR_BACKEND_TECHNICAL_SRS.md)  
> **Last Updated:** October 2, 2026  

---

## 1. Backend Milestone Matrix (Phases 1 — 15)

```mermaid
graph TD
    A[Élanor Backend Architecture] --> B[Core Platform: Phases 1-8 (100% COMPLETE)]
    A --> C[Logistics & Operations: Phases 9-12 (NEXT SESSIONS)]
    A --> D[Hardening & AI: Phases 13-15 (FINAL GATES)]

    subgraph Completed [Phases 1 - 8 Complete]
        B1[Phase 1: Database & Core Baseline]
        B2[Phase 2: Auth, OTP, Google & Profile]
        B3[Phase 3: Catalog, Categories & Variants]
        B4[Phase 4: Search & Multi-Facet Filters]
        B5[Phase 5: Wishlist & Cart with Merge]
        B6[Phase 6: Coupon Engine & Discounts]
        B7[Phase 7: Checkout, Quotes & Order Snapshots]
        B8[Phase 8: Payments, State Machine & Cancellation]
    end

    subgraph Upcoming [Next Phases]
        C1[Phase 9: Shipping, Tracking & Timeline (NEXT)]
        C2[Phase 10: Returns, Replacements & Refunds]
        C3[Phase 11: Reviews Moderation, CMS & Blog]
        C4[Phase 12: Notifications, Analytics & Audit Logs]
        D1[Phase 13: Core Hardening & Concurrency Gate]
        D2[Phase 14: AI Shopping Assistant & Regimens]
        D3[Phase 15: Razorpay & Shiprocket Live Adapters]
    end
```

---

## 2. Completed Backend Phases Details

### ✅ Phase 1 — Database & Core Baseline
- [x] **Flyway Migration (`V1__init_schema.sql`)**: 25 normalized tables covering users, roles, profiles, addresses, catalog, inventory, wishlist, cart, coupons, orders, payments, shipments, returns, refunds, reviews, CMS, audit logs, idempotency, and system settings.
- [x] **Common Architecture (`com.elanor.common`)**:
  - Standardized JSON responses via `ApiResponse<T>`.
  - Machine-readable enum error codes in `ErrorCode`.
  - Central `GlobalExceptionHandler` with trace ID logging.
  - Spring Security stateless JWT filter with CORS and BCrypt password encryption.

---

### ✅ Phase 2 — Authentication, Accounts & Profiles (`com.elanor.auth`, `com.elanor.customer`)
- [x] **Authentication Matrix**:
  - Email + Password registration & login with email verification token lifecycle.
  - Phone OTP authentication with attempt limits, expiry, and rate limiting.
  - Pluggable Google OAuth social login adapter (`SocialAuthProvider` -> `GoogleAuthProvider`).
  - Short-lived, single-use password reset tokens with verification.
- [x] **Customer Profiles & Addresses**:
  - Customer profile management (`GET /PUT /api/v1/customers/me`).
  - Multiple saved delivery addresses with default selection (`/api/v1/customers/me/addresses`).

---

### ✅ Phase 3 — Catalog, Categories, Collections & Variants (`com.elanor.catalog`, `category`, `collection`)
- [x] **Hierarchy & Merchandising**:
  - Self-referencing category tree (`Category` -> `Subcategory`).
  - Curated collections independent of product hierarchy.
- [x] **Product Lifecycle & Variants**:
  - Product statuses: `DRAFT`, `SCHEDULED`, `ACTIVE`, `OUT_OF_STOCK`, `ARCHIVED`.
  - Scheduled publishing timestamp enforcement (`isCurrentlyPublishable()`).
  - Generic JSONB variant attribute dimensions (volume, shade, packaging).
  - Media gallery with cover image flags and display order.

---

### ✅ Phase 4 — Search & Filtering (`com.elanor.search`)
- [x] **Keyword Search**: Authoritative search across product name, SKU, description, category, and attributes.
- [x] **Multi-Facet Filtering**: Dynamic specification-based filtering by category, price ranges, attributes, availability, and ratings with Spring Data pagination.

---

### ✅ Phase 5 — Wishlist & Cart (`com.elanor.wishlist`, `com.elanor.cart`)
- [x] **Guest & Authenticated Wishlist**: Real-time management and deduplicating merge on authentication.
- [x] **Commerce Cart Engine**:
  - Guest and customer cart with secure token identification.
  - Real-time stock validation and capping during cart merge upon login.
  - Backend authoritative calculations for subtotal, dynamic shipping, discounts, and totals.

---

### ✅ Phase 6 — Coupons & Promotional Engine (`com.elanor.coupon`)
- [x] **Discount Engine**:
  - Percentage and fixed-amount discounts with minimum order amount requirements.
  - Optional maximum discount capping (`maxDiscountAmount`).
  - Active date windows (`startDate`, `endDate`), usage limits, and atomic usage counters (`incrementUsage`).
- [x] **Cart Integration**: `POST /api/v1/cart/coupon` and `DELETE /api/v1/cart/coupon` endpoints with automatic discount reconciliation in cart summaries.

---

### ✅ Phase 7 — Checkout & Order Creation (`com.elanor.checkout`, `com.elanor.order`)
- [x] **Shipping Quote (`POST /api/v1/checkout/quote`)**: Dynamic quote calculating threshold progress (₹1,500 free shipping default) and estimated delivery timeframe (3-7 business days).
- [x] **Pre-Flight Validation (`POST /api/v1/checkout/validate`)**: Validates live stock, variant active status, published product status, delivery address integrity, and coupon eligibility.
- [x] **Atomic Order Placement (`POST /api/v1/checkout/order`)**:
  - Generates immutable snapshots: `OrderAddress` and `OrderItem` (unit price, SKU, product name).
  - Triggers inventory stock reservation (`InventoryService.reserveStock`).
  - Increments coupon usage counters and clears active cart.
  - Supports server-side deduplication via `Idempotency-Key` headers.
- [x] **Customer & Admin Order Endpoints**:
  - `GET /api/v1/orders` and `GET /api/v1/orders/{id}` with timeline logs.
  - `GET /api/v1/admin/orders` and `PUT /api/v1/admin/orders/{id}/status`.

---

### ✅ Phase 8 — Payments + Order State Machine (`com.elanor.payment`)
- [x] **Provider Port Architecture**:
  - `PaymentProvider` interface decoupling gateway implementations from business rules.
  - `DemoPaymentProvider`: Interactive testing sandbox supporting `Demo Success` and `Demo Failure` simulations.
  - `CodPaymentProvider`: Cash on Delivery with initial `PENDING` payment state and admin collection verification.
  - `PaymentProviderFactory`: Dynamic provider lookup.
- [x] **Payment Lifecycle & Endpoints**:
  - `POST /api/v1/payments/initiate`: Initializes payment attempt with idempotency caching.
  - `POST /api/v1/payments/{id}/demo-result`: Executes simulated outcome; automatically transitions order to `CONFIRMED` and commits stock reservations upon success.
  - `GET /api/v1/payments/{id}` and `GET /api/v1/payments/order/{orderId}`.
- [x] **Pre-Shipment Order Cancellation (`POST /api/v1/orders/{id}/cancel`)**:
  - Customer can cancel orders in `CREATED`, `CONFIRMED`, `PROCESSING`, or `PACKED` state.
  - Automatically releases pending reservations and restocks committed inventory via `InventoryService.adjustStock`.
  - Rejects cancellation once order is `SHIPPED`, `OUT_FOR_DELIVERY`, or `DELIVERED`.
- [x] **Admin Payment Controls**:
  - `GET /api/v1/admin/payments` (search & filter).
  - `POST /api/v1/admin/payments/{id}/cod-collect` (marks COD cash collected and payment `SUCCESSFUL`).

---

---

## 3. Frontend Milestones & Experience Deliveries

### ✅ Haute AI Skin Concierge (`/ai-skin-concierge`)
- [x] **Cinematic Editorial Hero (`skin9.png`)**:
  - Exact 3-column layout matching QClay reference aesthetic.
  - Interactive model portrait with 3D mouse parallax and continuous laser scanner sweep.
  - 3 Interactive Dermal Target Bounding Boxes: `Hydration Level` (Forehead), `Fine Lines` (Periorbital), `Skin Texture` (Cheek/Mouth) with live diagnostic tooltips.
  - 3 Right-column feature metric badges: `95% accurate skin analysis`, `30+ skin concerns analyzed`, `7-day personalized Élanor ritual`.
- [x] **4-Step Consultation Flow**:
  - Step 1: Baseline Phenotype & Midday feel.
  - Step 2: Dermal Priorities (Multi-select).
  - Step 3: Environmental Stressors & Micro-climates.
  - Step 4: Sensory Ritual Depth & Formulation Textures.
  - Live desktop Cosmetologist side diagnostic monitor with animated scanning feedback.
- [x] **Sequential 4-Stage Analysis Animation**:
  - GSAP timeline progressing through *Dermal Profile → Concern Compatibility → Formula Selection → Ritual Architecture*.
- [x] **Personalized Skin Prescription & Bespoke Routine**:
  - Dermal Vitality Matrix with animated percentage meters (*Hydration, Barrier Integrity, Cellular Radiance, Dermal Reactivity*).
  - Morning (AM) & Evening (PM) ritual breakdown with *"Why It's In Your Ritual"* formulation reasons and step-by-step application instructions.
  - Sticky bottom action bar with **"Add Entire Ritual to Bag"** (automatic 15% concierge benefit and direct cart synchronization via `StoreContext`).
- [x] **Curated Masterpieces Catalog Expansion**:
  - Added **Soothing Balm** (*Baume Apaisant Réparateur aux Plantes Rares*) with `skin8.png` to "The Iconic Formulations" on the homepage and catalog.

---

## 4. Immediate Next Phase

### 🚀 Phase 9 — Shipping & Tracking
**Goal:** Implement manual shipment management, event milestone timelines, delivery estimates, and customer tracking.

1. **Shipping Provider Abstraction (`com.elanor.shipping.provider`)**:
   - `ShippingProvider` interface with `createShipment`, `getTracking`, `cancelShipment`.
   - `ManualShippingProvider` implementation (and future `ShiprocketShippingProvider` contract).
2. **Shipment Entities (`com.elanor.shipping.entity`)**:
   - `Shipment` (order link, carrier name, tracking number, tracking URL, status, shipped/delivered timestamps).
   - `ShipmentEvent` (chronological milestone events: location, status, description, timestamp).
3. **Services & Endpoints**:
   - Customer tracking endpoint: `GET /api/v1/shipments/order/{orderId}` or `GET /api/v1/shipments/track/{trackingNumber}`.
   - Admin shipment management: `POST /api/v1/admin/shipments` (create shipment, transition order status to `SHIPPED`), `POST /api/v1/admin/shipments/{id}/events` (add milestone event).
4. **Integration Testing**:
   - Full shipment creation, event tracking, and order state synchronization tests.

---

## 5. Remaining Phases Roadmap (10 — 15)

- **Phase 10 — Returns / Replacement / Refund**: 7-day eligibility enforcement, customer return requests, admin inspection workflow, manual refund records, and inventory restocking.
- **Phase 11 — Reviews + CMS + Blog**: Customer reviews with ratings & media, admin moderation queue, dynamic homepage hero banners, FAQs, navigation, and editorial blog engine.
- **Phase 12 — Notifications + Analytics + Audit**: Development email provider with transactional notification templates, analytics event capture, admin analytics dashboard, and immutable audit logs.
- **Phase 13 — Core Hardening**: Concurrency race condition tests, security review (IDOR & RBAC verification), API documentation freeze, and performance optimizations.
- **Phase 14 — AI Shopping Assistant**: Intent understanding, routine builders, ingredient comparisons, and product Q&A grounded in authoritative backend services.
- **Phase 15 — Future Provider Readiness**: Plug-and-play validation of live Razorpay and Shiprocket gateway adapters without altering customer-facing business logic.

---

## 6. Verification Status & Test Suite Summary

- **Backend Automated Tests:** 37 passed, 0 failures, 0 errors, 0 skipped
- **Backend Build:** Clean Maven compilation (`BUILD SUCCESS`)
- **Frontend Build:** Clean Next.js 15 production build (`13/13 static pages generated successfully`, 0 TypeScript/Lint errors)
- **Live Vercel Deployment:** [https://elanor-eta.vercel.app](https://elanor-eta.vercel.app)
- **Git Tree:** 100% Clean on `main` (Latest Commit: `7996f4d`)
