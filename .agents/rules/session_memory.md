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
- **Phase 16: Evidence-Grounded RAG AI Pipeline & Haute Botanical Concierge:**
  - **Backend RAG Knowledge Ingestion & Retrieval:**
    - Flyway `V4__knowledge_base.sql` (`knowledge_chunks` table with source_type, source_id, title, content, metadata_json, content_hash).
    - Auto-indexing startup service (`KnowledgeIngestionService.java`) with SHA-256 deduplication indexing active products, brand philosophy, 7-day returns, and shipping policies.
    - Token-based hybrid relevance retrieval (`KnowledgeRetrievalService.java`) with top-k bounding and citation DTO generation.
    - AI Provider port: `GeminiAiAssistantProvider.java` (Google Gemini 1.5 Flash REST API with grounded prompt injection) and `MockLocalAiAssistantProvider.java` fallback.
    - Live order status tool secured strictly via JWT `UserPrincipal` (zero IDOR) and cosmetic safety abstention for medical queries.
    - Test Suite: 65/65 unit and integration tests passing (`mvn test` -> `BUILD SUCCESS`).
  - **Frontend Luxury Chatbot UI & Comprehensive Grounded Engine (`components/ChatBotDrawer.tsx` & `lib/ragEngine.ts`):**
    - Resolved repetitive greeting welcome loop by implementing domain-grounded retrieval matching all master catalog products, active ingredients, store policies, and ritual sequencing.
    - Floating concierge launcher: Sleek luxury circular button (`w-12 h-12`) on mobile with gold border and `É` monogram; expands to full pill on desktop.
    - Full-viewport mobile drawer (`fixed inset-0 sm:inset-auto sm:bottom-6 sm:right-6 w-full sm:w-[440px] h-[100dvh] sm:h-[640px]`) with safe-area padding (`pb-safe`) for mobile keyboards.
    - High-contrast patron typography: User messages render in `#FDFBF8` ivory linen on `#1B1A17` obsidian bubble with gold "You" patron tag.
    - Session patron identity memory: Remembers patron name across multi-turn consultations.
    - 1-click cart addition directly from concierge formulation recommendation cards.
    - Automated test suite: 12/12 tests passing (`test_rag_automated.mjs`).
- **Phase 17: Comprehensive Mobile Responsiveness & UX Engineering:**
  - **Navigation & Header (`components/Navbar.tsx` & `MobileBottomNav.tsx`):** Eliminated 6-icon overcrowding on mobile viewports (<640px); concealed redundant icons (Account, Track, Wishlist) accessible via MobileBottomNav and mobile menu drawer; preserved Search, compact Bag with notification badge, and Hamburger toggle.
  - **Sticky Action Bars Overlapping Bottom Nav:**
    - Product Detail Page (`app/product/[id]/page.tsx`): Set sticky mobile purchase bar to `bottom-[57px]` directly above `MobileBottomNav` with `pb-32` bottom padding on page.
    - AI Concierge (`app/ai-skin-concierge/components/ProfileAndRitualResult.tsx`): Elevated sticky "Add Entire Ritual to Bag" summary bar from `bottom-6` to `bottom-20 lg:bottom-6` to avoid bottom nav overlap.
  - **Dermal Diagnostic Engine (`app/ai-skin-concierge/components/IntroScreen.tsx`):** Added touch/tap interaction (`onClick` and `touch-manipulation`) to all 3 dermal bounding boxes (Hydration, Fine Lines, Texture) allowing mobile users without mouse hover to tap and reveal live clinical diagnostic scores.
  - **Slide-Out Cart & Modals:**
    - Cart Drawer (`components/CartDrawer.tsx`): Set `pl-0 sm:pl-10` to provide 100% full-width drawer experience on mobile phones.
    - AuthModal, QuickViewModal, and SearchModal: Configured `max-h-[92vh] overflow-y-auto`, responsive padding (`p-5 sm:p-8`), and safe viewport scaling for mobile keyboards.
  - **Checkout & Bag Review (`app/checkout/page.tsx` & `app/cart/page.tsx`):** Mobile-optimized stepper labels ("Address", "Packaging", "Payment"), adaptive single-column address grid on small mobile, and compact summary padding.
  - **Global Horizontal Overflow Guard (`app/globals.css`):** Applied `overflow-x: hidden; max-width: 100vw;` to `html` and `body`.
  - **Verification & Deployment:** TypeScript check (`npx tsc --noEmit`) passed with 0 errors; committed (`f2a7695`) and pushed to `main` for automatic Vercel deployment.
- **MCA UI/UX Coursework Documentation Suite:**
  - `Assignment_1_UI_UX_Evaluation_Report.docx`: Heuristic evaluation (Nielsen's 10 Heuristics, WCAG 2.1 AA), typography scale (Hero 88px/72px, H1 56px, H2 42px, H3 30px, H4 22px, Body 16-18px, Caption 12px), 5 strategic improvements, 7 presentation slides deck.
  - `Assignment_2_User_Research_and_Competitive_Analysis.docx`: 5-user qualitative research, competitor matrix (Aesop vs Kiehl's), personas (Genevieve & Claire), empathy map, customer journey map, problem statement, 5 HMWs, and 5 Agile user stories with acceptance criteria.
  - `Assignment_3_Information_Architecture_and_Wireframing.docx`: Hierarchical sitemap, dual-layer navigation model, hybrid card sorting matrix, 3 user flows, and low-fidelity wireframes for 7 screens (Login, Registration, Dashboard, List, Details, Profile, Settings).
  - `Elanor_UI_UX_Comprehensive_Documentation_Master.docx`: Unified master coursework report combining Assignments 1, 2, and 3 into one comprehensive submission file.
