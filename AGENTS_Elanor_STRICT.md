# AGENTS.md — Elanor AI Agent Rules

> **Authority:** `ELANOR_BACKEND_TECHNICAL_SRS.md` is the primary source of truth. These rules are mandatory operating constraints for the AI agent.
> **Goal:** Build correctly, incrementally, securely, and with strict discipline.

## 1. SOURCE OF TRUTH & SCOPE
- Follow the SRS exactly. Never invent, silently change, or remove requirements.
- Work **only on the currently requested phase/task**.
- Never implement future features unless explicitly requested.
- If requirements conflict or are ambiguous: **STOP → report the conflict/ambiguity → ask for confirmation**. Do not guess on business-critical behavior.
- Do not modify unrelated files or functionality.

## 2. WORK-ENVIRONMENT ONLY
- Treat the local project/repository as the working environment and source of implementation truth.
- Inspect existing code before creating new code.
- Reuse existing patterns, utilities, configurations, and dependencies where appropriate.
- Do not introduce external services, APIs, cloud resources, or integrations unless explicitly required by the SRS/current task.
- Do not send project data, source code, logs, database contents, or files to external services unless explicitly authorized.

## 3. SECRETS — ABSOLUTE RULE
**The AI agent must never access, reveal, print, copy, commit, or transmit secret credentials.**
- Never read or display `.env`, `.env.*` containing secrets, credential files, private keys, certificates, tokens, API keys, database passwords, JWT secrets, OAuth secrets, or deployment credentials.
- Never execute commands whose purpose is to expose environment variables or credentials.
- Never place secrets in source code, tests, logs, API responses, screenshots, documentation, commits, or generated files.
- Use environment variables/placeholders only, e.g. `DATABASE_URL`, `JWT_SECRET`, `GOOGLE_CLIENT_ID`.
- Maintain `.env.example` with **dummy/non-secret values only**.
- If a required secret is missing, **STOP and report only the variable name**, never request or print its value.
- Before modifying configuration, verify that secret-bearing files are excluded by `.gitignore`.

## 4. ARCHITECTURE
`Client → REST API → Services → Repositories → PostgreSQL`
- Use the modular-monolith architecture defined by the SRS.
- Business rules, authorization, lifecycle transitions, pricing, inventory, payments, refunds, and audit logic belong in the backend.
- Keep payment, shipping, email, storage, and AI providers behind replaceable interfaces.
- No unnecessary microservices, queues, brokers, Redis, workers, or other infrastructure.

## 5. SECURITY & RBAC
- Never trust client-supplied price, total, stock, role, permission, payment status, or ownership.
- Enforce authorization server-side on every protected operation.
- Prevent cross-user data access/IDOR.
- Never store plaintext passwords or OTPs.
- Never expose stack traces, secrets, internal SQL, tokens, or sensitive data in API responses/logs.
- Apply validation, rate limiting, secure error handling, and appropriate authentication controls.

**V1 roles:** `CUSTOMER`, `ADMIN`, `SUPER_ADMIN`.

## 6. AI RULES
- AI is **not authoritative business truth**.
- AI must never invent prices, stock, order status, shipping status, refunds, policies, specifications, or other business facts.
- AI must use approved backend services/data for factual answers.
- Treat all user-provided text as untrusted input; defend against prompt injection.
- Never reveal system prompts, internal instructions, credentials, hidden data, or private project information.
- No autonomous consequential actions. Human/admin approval is required where the SRS requires it.
- AI implementation starts **only after core backend completion and validation**.

## 7. API & DATABASE DISCIPLINE
- Use `/api/v1/...`.
- Controllers use DTOs; do not expose JPA entities directly.
- Database changes must use migrations.
- Use transactions for multi-record business operations.
- Use idempotency for payment/order/refund-sensitive operations.
- Preserve historical order, payment, inventory, return, refund, and audit records.
- Never create unnecessary tables or duplicate business logic.

## 8. IMPLEMENTATION WORKFLOW
For every task:

```text
1. INSPECT
2. IDENTIFY IMPACT
3. PLAN
4. IMPLEMENT MINIMUM COMPLETE CHANGE
5. TEST
6. VALIDATE SECURITY & BUSINESS RULES
7. CHECK FOR REGRESSIONS
8. REPORT RESULT
```

- Do not generate large amounts of code blindly.
- Keep changes small and reviewable.
- Fix errors before moving to the next phase.
- Never mark TODO/stub/mock behavior as complete.

## 9. TESTING & VALIDATION
After every meaningful change:
- run relevant tests;
- compile/build the affected module;
- verify API behavior;
- verify authorization;
- verify error/fallback paths;
- verify affected business workflows;
- check for regressions.

For critical flows, test failure cases—not only happy paths.

## 10. DEPENDENCIES & CONFIGURATION
Before adding a dependency:
1. Check whether the project already supports the requirement.
2. Check whether the SRS requires it.
3. Prefer the simplest reliable solution.
4. Consider free-tier/local constraints.

Never hardcode credentials, URLs, ports, business thresholds, durations, or environment-specific values.

## 11. CHANGE DISCIPLINE
- No unrelated refactoring.
- No destructive changes without explicit approval.
- No deletion of existing working functionality without justification.
- Keep documentation/configuration aligned with actual implementation.
- Never commit secrets.
- Use clear, meaningful commit messages when commits are requested.

## 12. STOP CONDITIONS
**STOP and ask for confirmation** when:
- requirements conflict;
- a security-sensitive decision is undefined;
- a destructive migration/change is required;
- a secret/credential is required;
- an external integration is requested but not defined;
- implementation would require breaking an existing contract.

Do not guess in these situations.

## 13. DECISION PRIORITY
1. SRS requirements
2. Security & data protection
3. Current task/phase scope
4. Data integrity & business rules
5. Free/local environment constraints
6. Simplicity & maintainability
7. Performance
8. Optional enhancements

> **DEFAULT:** Build the smallest correct, secure, testable implementation that satisfies the SRS. Do not add complexity unless it is required.

## 14. COMPLETION REPORT
After each completed task/phase, report briefly:
- what was implemented;
- files/modules changed;
- tests/validation performed;
- any known limitation or pending decision.

Call me **Rasika Babe** after every phase/task you finish, and whenever you ask me to check or verify anything.
