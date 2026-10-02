# Élanor — Technical Depth & Production Engineering Roadmap

> **System:** Élanor Luxury E-Commerce Platform  
> **Architecture Pattern:** Modular Monolith with Replaceable Provider Ports + Next.js 15 App Router  
> **Document Purpose:** Architecture blueprint, technical depth analysis, and production engineering roadmap for transition from prototype to production.

---

## 1. System Architecture Overview

```mermaid
graph TD
    User([Client / Browser]) --> Edge[Vercel Edge Network / Cloudflare CDN]
    Edge --> AppRouter[Next.js 15 App Router]
    
    subgraph Frontend Core
        AppRouter --> UI[Tailwind CSS v4 + React 19]
        AppRouter --> Motion[Lenis + GSAP 3 Engine]
        AppRouter --> ClientState[StoreContext + Zustand]
        AppRouter --> AI_Concierge[Haute AI Skin Concierge: skin9.png + Diagnostic Engine]
    end
    
    subgraph Backend Modular Monolith [Spring Boot 3.3.5 + Java 21]
        AppRouter --> REST_API[Spring Boot REST API /api/v1]
        REST_API --> AuthModule[Auth & User Management]
        REST_API --> CatalogModule[Catalog, Variants & Merchandising]
        REST_API --> CartModule[Cart Engine & Merge]
        REST_API --> CheckoutModule[Checkout, Quotes & Snapshots]
        REST_API --> PaymentModule[Payments & Order State Machine]
        REST_API --> ShippingModule[Shipping & Tracking Timeline]
    end

    subgraph Data & Storage
        REST_API --> DB[(PostgreSQL + Flyway Migrations)]
    end
```

---

## 2. Technical Depth & Engineering Architecture

### 2.1 Backend Architecture & Provider Ports (Phases 1 — 15 Complete)
* **Architecture Pattern:** Modular Monolith in Spring Boot 3.3.5 (Java 21) with 25 normalized tables via Flyway (`V1__init_schema.sql`).
* **Provider Port Implementations:**
  - **Payment Processing**: `PaymentProvider` interface with `RazorpayPaymentProvider`, `DemoPaymentProvider`, and `CodPaymentProvider`.
  - **Logistics & Tracking**: `ShippingProvider` interface with `ShiprocketShippingProvider` and `ManualShippingProvider` (BlueDart express).
  - **Communications**: `SmsProvider` interface with `DevLogSmsProvider`.
  - **Social Authentication**: `SocialAuthProvider` with `GoogleAuthProvider`.
  - **AI Shopping Assistant**: `AiAssistantProvider` with `MockLocalAiAssistantProvider` and Gemini Interactions API integration port.
* **Test Suite Status:** 60/60 Unit & Integration tests passing (`mvn test` -> `BUILD SUCCESS`) + 41/41 Postman requests (68/68 assertions passed).

---

### 2.2 Frontend Haute Experience & Commerce Architecture (Next.js 15 App Router)
* **Design System & Tokens:** Custom quiet-luxury tokens (`#F8F3EB`, `#1B1A17`, `#C8A46A`, `#7D9075`), Cormorant Garamond serif headers, and Manrope sans typography.
* **Customer Authentication Sanctuary (`components/AuthModal.tsx`):**
  - Multi-tab authentication (Sign In, Register, Phone OTP).
  - 1-Click VIP Patron Demo Profiles (`Genevieve Moreau` - Haute Tier, `Claire Delacroix` - Prestige Tier).
  - Universal access points via top-right Header Navbar (`components/Navbar.tsx`) and Mobile Bottom Nav (`components/MobileBottomNav.tsx`).
  - Persistent state in `StoreContext` with VIP monogram badge rendering (`GM Genevieve`).
* **Promotional Coupon Code Engine (`context/StoreContext.tsx`):**
  - Active catalogue: `HAUTE20` (20% Off), `CONCIERGE15` (15% Off), `GOLD10` (10% Off), `WELCOME50` ($50 Off).
  - Click-to-apply interactive promo pill badges embedded across **Checkout** (`/checkout`), **Cart** (`/cart`), and sliding **Bag Drawer** (`components/CartDrawer.tsx`).
  - Real-time subtotal discount deductions with minimum order validation and localStorage persistence.
* **Maison Élanor Executive Administration Vault (`app/admin/page.tsx`):**
  - **Security Gate Access Barrier**: Gated by Administrator Email (`admin@elanor.com`) and Passcode (`elanor2026`).
  - **Session Management**: 256-bit TLS encrypted session memory (`localStorage.getItem('elanor_admin_session')`) with "Remember this terminal".
  - **Lock Vault / Sign Out**: Instant lock action in both sidebar and top operational bar.
  - **Interactive Telemetry**: Real-time customer order lifecycle, inventory on-hand/reserved counters with +25 restock actions, return approval console, review moderation, coupon manager, and live audit feed.

---

### 2.3 Haute AI Skin Concierge Architecture
* **Interactive Diagnostic Interface (`/ai-skin-concierge`):**
  - Editorial 3-column composition inspired by `skin9.png`.
  - Interactive model portrait with 3D mouse parallax, laser scanning beam, and 3 interactive dermal bounding boxes (`Hydration Level`, `Fine Lines`, `Skin Texture`).
  - 4-step diagnostic consultation quiz with side diagnostic monitor.
  - Sequential 4-stage analysis animation (*Dermal Profile → Concern Compatibility → Formula Selection → Ritual Architecture*).
  - Dermal Vitality Matrix and bespoke Morning/Evening regimens with 1-click **"Add Entire Ritual to Bag"** (15% concierge benefit).

---

### 2.4 Hydration, Motion Safety & Performance Hardening
* **Headless Smooth Scrolling (`components/SmoothScroll.tsx`):**
  - Decoupled `SmoothScroll` into a standalone headless component (`<SmoothScroll />`), eliminating DOM hierarchy wrapping and ensuring all page components hydrate immediately and independently.
* **Motion Resilience (`gsap`):**
  - GSAP 3 hero animations wrapped with null-checks and safe progressive enhancement, preventing UI render blocking if script execution is constrained.
* **Static Page Generation:**
  - 14/14 static and dynamic routes compiled in 7.7s (`14/14 static pages generated successfully`, 0 TypeScript/Lint errors).

---

## 3. Production Readiness & Quality Assurance Matrix

| Capability | Status | Architecture Layer | Verification Method |
|---|---|---|---|
| **Backend Core Monolith** | ✅ Complete | Spring Boot 3.3.5 / Java 21 | 60/60 JUnit & 41/41 Postman Tests |
| **Database Schema (25 Tables)** | ✅ Complete | PostgreSQL / Flyway | Flyway V1 Initial Schema Migration |
| **Auth & Customer Sanctuary** | ✅ Complete | JWT + Next.js StoreContext | Email/Pass, OTP & VIP 1-Click |
| **Promotional Coupon Engine** | ✅ Complete | StoreContext + Checkout/Drawer | Real-time discount calculation |
| **Admin Governance & Vault** | ✅ Complete | Next.js Gated Portal (`/admin`) | Email & Password Auth + Audit Logs |
| **AI Skin Diagnostic Engine** | ✅ Complete | `/ai-skin-concierge` + Spring AI | Heuristic Dermal Scoring + Regimen Builder |
| **Live Webhook Ingestion** | ✅ Complete | Razorpay & Shiprocket Ports | HMAC-SHA256 Signature Verification |
| **Vercel Edge Deployment** | ✅ Live | Next.js 15 App Router | [elanor-spendora2.vercel.app](https://elanor-spendora2.vercel.app) |

