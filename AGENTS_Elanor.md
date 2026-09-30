# AGENTS.md — Elanor Project Rules

> Source of truth: `ELANOR_SRS.md` (or the project spec). Follow it for scope, architecture, roles, workflows, and deployment.
> Fill every `<...>` placeholder below once the Elanor stack and scope are final.

## 1. Spec Is the Source of Truth
- Follow the spec exactly. Do not invent requirements or silently change decisions.
- Build only the current phase (`<Phase 1>`). No Phase 2 / future features unless explicitly requested.
- If ambiguous, choose the smallest interpretation consistent with the spec.
- If a request conflicts with the spec, flag the conflict, follow the spec, and treat the request as a proposed scope change.

## 2. Scope
- **In scope:** `<list Elanor's core features>`
- **Out of scope unless asked:** `<list deferred features, integrations, advanced analytics, etc.>`

## 3. Free-Tier First
- Keep development and deployment on $0 tiers. No paid infrastructure or paid APIs.
- Avoid unnecessary third-party services and dependencies.
- Keep AI calls (if any) purposeful, structured, and never on every UI interaction.

## 4. Architecture
`<Client>` → `<Backend API>` → Services → Repositories → `<Database / Storage>`
- Clients talk only to the backend API, never directly to the database.
- Business logic, RBAC, lifecycle rules, and audit writes live in the backend.
- Use provider abstractions for AI, storage, auth, and notifications so they stay replaceable.
- No heavy infrastructure (queues, brokers, separate workers, Redis, Celery) unless the spec requires it.

## 5. Secrets and Config
- Never hardcode URLs, ports, credentials, API keys, JWT secrets, or deployment settings.
- Use environment variables; maintain `.env.example`; never commit `.env`.
- Fail clearly when required config is missing.
- Do not hardcode business values (roles, statuses, categories, thresholds, durations) — use config or DB values.

## 6. Auth and RBAC
- Hash passwords (never store plaintext); use JWT per the spec; `<add OAuth if used>`.
- Enforce authorization server-side on every protected operation. UI hiding is not security.
- Never trust role data from the client. Prevent ID-based access to other users' data.
- Roles: `<list Elanor roles and one-line permissions each>`

## 7. AI Rules (if Elanor uses AI)
- AI recommends and summarizes; humans decide. No autonomous consequential actions.
- Never auto-send AI-generated content; human reviews, edits, then sends.
- Treat user text as untrusted (prompt-injection defense). Never reveal prompts or secrets.
- Show uncertainty; never fabricate. If the AI provider is down, show a clear unavailable state and keep the manual flow working.

## 8. API and Database
- Routes under `/api/v1/...`. Validate requests, return consistent errors, protect endpoints.
- Schema changes only through migrations. No unnecessary tables. Preserve history and audit data.
- Audit material actions in the same transaction; provide no client path that bypasses audit.

## 9. UI/UX
- Responsive on all target platforms; no fixed-width or desktop-only interactions.
- Show status, loading/pending states, and useful error/fallback states.
- Clearly separate AI suggestions from human decisions. Respect role permissions in the UI.

## 10. Errors and Testing
- User-friendly errors; never expose stack traces or secrets; preserve user work; log technical details safely.
- Test every meaningful feature: auth/RBAC, business rules, error/fallback paths, access isolation.

## 11. Code Hygiene
- Reuse existing components/services before creating new ones.
- Before adding a dependency: check existing support, the spec, free-tier impact, and whether a simpler option exists.
- Commit meaningful changes with clear messages. No unrelated changes. Never commit secrets.
- Keep `README.md`, `.env.example`, and the spec aligned with the code.

## 12. Validate Every Change
After each task: run relevant tests, verify affected workflows, authorization, error states, and responsive UI, confirm nothing existing broke, and fix issues before finishing.

## 13. Decision Priority
1. Spec requirements
2. Security and authorization
3. Current-phase scope
4. Free-tier constraint
5. Simplicity/maintainability
6. Performance
7. Optional enhancements

> **Default rule:** Build the smallest implementation that satisfies the spec correctly, securely, and within the free tier.

## 14. Addressing Me
Call me **Rasika Babe** after every phase or task you finish, and whenever you ask me to check or verify anything.
