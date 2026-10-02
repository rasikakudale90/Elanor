# Session Context & Memory Rule

## 1. Automatic Session Startup Protocol
At the beginning of every session or upon receiving user instructions:
1. **Always Read Repository Memory First**:
   - Read `progress.md` to know the exact completed milestones, guarantees, and current roadmap state.
   - Read `AGENTS_Elanor.md` / `AGENTS_Elanor_STRICT.md` to follow all project rules, free-tier constraints, and addressing conventions ("Rasika Babe").
   - Read `technical-depth.md` or SRS docs when working on architecture and backend services.
2. **Frontend Quality & Security Guarantee**:
   - Do NOT modify, simplify, or downgrade existing frontend styling, design tokens, Lenis smooth scroll, GSAP animations, or the Haute AI Skin Concierge experience when implementing backend/API integrations.
   - The Admin Portal (`/admin`) must NEVER be linked in customer-facing navigation, modals, or footers. It is strictly gated by administrator credentials (`admin@elanor.com` / `elanor2026`).
3. **Session Checkpoint**:
   - At the conclusion of every phase or task, record progress in `progress.md` and commit/push to `main`.

## 2. Current Verified System State
- **Backend Architecture:** Modular Monolith (Spring Boot 3.3.5 + Java 21 + PostgreSQL + Flyway).
- **Backend Status:** All 15 Phases Complete & Hardened (60/60 automated JUnit tests passing, 41/41 Postman e2e requests passing, live Razorpay & Shiprocket webhook adapters integrated).
- **Frontend Architecture:** Next.js 15 App Router + React 19 + Tailwind CSS + GSAP 3 + Lenis + StoreContext.
- **Dynamic Catalog Engine:**
  - Storefront and Admin share real-time dynamic `products` catalog in `context/StoreContext.tsx`.
  - Admin Formulation Creator in `app/admin/page.tsx` (`+ Add New Formulation`) allows publishing new formulations live with price, category, concern, bottle volume, stock, clinical benefits, and image preset selector.
  - Dynamically consumed across Homepage (`/`), Curated Shop (`/shop`), Formulation Detail (`/product/[id]`), Concern Protocols (`/concern/[slug]`), and Search Modal.
- **Admin Security Isolation:**
  - Admin Portal (`/admin`) is completely hidden from public/customer menus and modals.
  - Enforces manual email (`admin@elanor.com`) and security passcode (`elanor2026`) verification.
- **Frontend AI Skin Concierge (`/ai-skin-concierge`):**
  - Live on [https://elanor-spendora2.vercel.app/ai-skin-concierge](https://elanor-spendora2.vercel.app/ai-skin-concierge).
  - Exact 3-column composition based on `skin9.png` reference with GSAP mouse parallax, laser scanning beam, 3 dermal bounding boxes, and 1-click ritual cart addition with 15% discount.
- **Live Deployment:** [https://elanor-spendora2.vercel.app](https://elanor-spendora2.vercel.app) (GitHub `main`).

