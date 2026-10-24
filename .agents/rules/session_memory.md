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
  - **Frontend Luxury Chatbot UI (`components/ChatBotDrawer.tsx`):**
    - Floating concierge launcher pill with gold pulse animation and glassmorphic slide-out window.
    - Mounted globally in `app/layout.tsx` within `<StoreProvider>`.
    - High-contrast patron typography: User messages render in `#FDFBF8` ivory linen on `#1B1A17` obsidian bubble with gold "You" patron tag.
    - Session patron identity memory: Remembers patron name ("My name is Rasika") across the entire conversation.
    - Resilient offline client-side grounded reasoning fallback with 8-second quick race timeout to provide instant answers even when the Render backend container is hibernating.
    - 1-click cart addition directly from concierge formulation recommendation cards.
    - Static Page Generation: 18/18 static routes compiled cleanly (`npm run build` -> Exit code 0).
- **MCA UI/UX Coursework Documentation Suite:**
  - `Assignment_1_UI_UX_Evaluation_Report.docx`: Heuristic evaluation (Nielsen's 10 Heuristics, WCAG 2.1 AA), typography scale (Hero 88px/72px, H1 56px, H2 42px, H3 30px, H4 22px, Body 16-18px, Caption 12px), 5 strategic improvements, 7 presentation slides deck.
  - `Assignment_2_User_Research_and_Competitive_Analysis.docx`: 5-user qualitative research, competitor matrix (Aesop vs Kiehl's), personas (Genevieve & Claire), empathy map, customer journey map, problem statement, 5 HMWs, and 5 Agile user stories with acceptance criteria.
  - `Assignment_3_Information_Architecture_and_Wireframing.docx`: Hierarchical sitemap, dual-layer navigation model, hybrid card sorting matrix, 3 user flows, and low-fidelity wireframes for 7 screens (Login, Registration, Dashboard, List, Details, Profile, Settings).
  - `Elanor_UI_UX_Comprehensive_Documentation_Master.docx`: Unified master coursework report combining Assignments 1, 2, and 3 into one comprehensive submission file.
