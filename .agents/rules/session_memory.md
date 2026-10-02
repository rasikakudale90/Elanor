# Session Context & Memory Rule

## 1. Automatic Session Startup Protocol
At the beginning of every session or upon receiving user instructions:
1. **Always Read Repository Memory First**:
   - Read `progress.md` to know the exact completed milestones, guarantees, and current roadmap state.
   - Read `AGENTS_Elanor.md` / `AGENTS_Elanor_STRICT.md` to follow all project rules, free-tier constraints, and addressing conventions ("Rasika Babe").
   - Read `technical-depth.md` or SRS docs when working on architecture and backend services.
2. **Frontend Quality Guarantee**:
   - Do NOT modify, simplify, or downgrade existing frontend styling, design tokens, Lenis smooth scroll, GSAP animations, or the Haute AI Skin Concierge experience when implementing backend/API integrations.
3. **Session Checkpoint**:
   - At the conclusion of every phase or task, record progress in `progress.md` and commit/push to `main`.

## 2. Current Verified System State
- **Backend Architecture:** Modular Monolith (Spring Boot 3.3.5 + Java 21 + PostgreSQL + Flyway).
- **Backend Status:** Phases 1 through 12 Complete & Verified (50/50 automated tests passing). Next Phase: Phase 13 (Core Hardening & Concurrency Gate).
- **Frontend Architecture:** Next.js 15 App Router + React 19 + Tailwind CSS + GSAP 3 + Lenis + StoreContext.
- **Frontend AI Skin Concierge (`/ai-skin-concierge`):**
  - Live on [https://elanor-eta.vercel.app/ai-skin-concierge](https://elanor-eta.vercel.app/ai-skin-concierge).
  - Exact 3-column composition based on `skin9.png` reference.
  - Interactive model portrait with GSAP mouse parallax, laser scanning beam, and 3 target bounding boxes (`Hydration Level`, `Fine Lines`, `Skin Texture`).
  - 4-step consultation flow with real-time cosmetologist side diagnostic monitor.
  - Sequential 4-stage analysis animation.
  - Bespoke AM/PM ritual generator with animated vitality matrix metrics and one-click "Add Ritual to Bag" with automatic 15% discount.
- **Catalog Update:** Added **Soothing Balm** with `skin8.png` to "The Iconic Formulations" bento grid.
