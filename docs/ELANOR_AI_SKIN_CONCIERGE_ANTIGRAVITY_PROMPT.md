# ÉLANOR — AI SKIN CONCIERGE / AI ROUTINE BUILDER
## Antigravity Implementation Prompt

> Build a premium AI Skin Concierge experience inside the existing Élanor frontend. Use this document as the implementation brief. Do not redesign unrelated parts of the application.

## 1. SOURCE OF TRUTH

Before changing code, inspect and follow these sources in this order:

1. Existing Élanor frontend codebase, design system, components, routes, and assets.
2. `AGENTS.md` and all applicable agent rules.
3. `ELANOR_BACKEND_TECHNICAL_SRS.md`, if present.
4. Existing Élanor AI Routine Builder / AI Skin Diagnostic implementation.
5. Visual references:
   - https://dribbble.com/shots/27265179-AI-Skincare-Cosmetologist-Landing-Page
   - https://elanor-eta.vercel.app/
   - https://dribbble.com/shots/27460340-Website-Design-for-a-Beauty-Brand-Veloura

Use references for inspiration only. Do not copy copyrighted assets, branding, or layouts pixel-for-pixel.

---

## 2. PRIMARY OBJECTIVE

Create a dedicated Élanor **AI Skin Concierge** experience.

Suggested route:

`/ai-skin-concierge`

If an equivalent AI Routine Builder / Skin Diagnostic route already exists, extend it instead of creating duplicate functionality.

The experience must feel like:

**Luxury skincare consultation + editorial beauty + intelligent personalization**

It must NOT feel like a generic AI chatbot, SaaS dashboard, questionnaire form, or copied Dribbble template.

---

## 3. CORE USER JOURNEY

Implement:

```text
INTRO
  ↓
SKIN TYPE
  ↓
PRIMARY CONCERNS
  ↓
SKIN CONTEXT / LIFESTYLE
  ↓
ROUTINE + PRODUCT PREFERENCES
  ↓
ANALYSIS
  ↓
PERSONALIZED SKIN PROFILE
  ↓
MORNING + EVENING RITUAL
  ↓
PRODUCT DETAILS
  ↓
ADD RITUAL TO BAG
```

Use one primary question per screen/state rather than one long form.

---

## 4. VISUAL DIRECTION

Élanor's existing identity is the primary design system.

Use:

- editorial luxury
- haute botanical skincare
- clinical but warm styling
- generous whitespace
- sophisticated typography
- ivory / cream
- muted botanical greens
- warm taupe / sand
- restrained terracotta
- dark brown / near-black typography
- organic shapes
- subtle borders
- soft shadows
- premium skincare/product photography
- botanical textures
- restrained motion

The AI reference should influence the **interaction model**, not replace Élanor's identity.

Avoid:

- neon AI gradients
- purple/blue SaaS styling
- excessive glassmorphism
- robot/AI imagery
- generic stock-looking faces
- excessive cards
- excessive animation
- dashboard-like UI

---

## 5. INTRO SCREEN

Create a cinematic premium opening state.

Suggested copy:

```text
ÉLANOR

AI SKIN CONCIERGE

Your skin has a story.

Let's understand yours.

A private 2-minute consultation.

[ BEGIN CONSULTATION → ]
```

Use an editorial botanical/product visual and large negative space.

---

## 6. QUESTION FLOW

Show elegant progress such as:

`01 / 04`

### Question 1 — Skin Type

```text
How does your skin usually feel?

○ Comfortable
○ Dry / Tight
○ Oily / Shiny
○ Combination
○ Sensitive / Reactive
```

### Question 2 — Primary Concerns

Allow multi-select:

```text
Radiance
Firming
Barrier Restoration
Deep Quenching
Calming
Pore Clarifying
Uneven Tone
Texture
```

Prefer existing Élanor concern terminology/data.

### Question 3 — Skin Context

Use concise non-medical questions such as:

```text
How does your skin behave throughout the day?

What environment do you spend most of your time in?

How would you describe your current routine?
```

### Question 4 — Preferences

Examples:

```text
What kind of ritual do you prefer?

Minimal
Balanced
Complete

Which textures do you prefer?

Lightweight
Creamy
Rich
Oil-based
No preference
```

Do not add unnecessary questions.

---

## 7. INTERACTION

Required:

- clear selected state
- subtle selection animation
- elegant Continue CTA
- Back navigation
- smooth progress update
- preserve answers when going backward
- no page reload between questions
- keyboard accessible
- touch-friendly
- responsive

Use restrained fade/slide/opacity transitions. Avoid excessive animation.

---

## 8. ANALYSIS STATE

After the final question, show a short premium analysis state.

Example:

```text
UNDERSTANDING YOUR SKIN

Analyzing your preferences
and Élanor's botanical formulations.

01  Skin profile
02  Concern compatibility
03  Formula selection
04  Ritual construction
```

Do not pretend that real AI is running if no AI/backend service is connected.

Use deterministic/mock analysis only when necessary.

---

## 9. PERSONALIZED SKIN PROFILE

Create a premium result screen:

```text
YOUR ÉLANOR SKIN PROFILE

THE LUMINOUS BALANCE

Hydration       78%
Barrier         84%
Radiance        91%
Sensitivity     42%
```

These are personalization indicators, not medical diagnoses or validated measurements. Do not present them as medical/scientific measurements unless authoritative backend data exists.

---

## 10. PERSONALIZED RITUAL

Create AM and PM routines:

```text
YOUR BESPOKE RITUAL

MORNING

01  Cleanse
02  Treat
03  Hydrate
04  Protect

EVENING

01  Cleanse
02  Treat
03  Restore
```

Map steps to actual Élanor products.

Never invent:

- products
- prices
- ingredients
- inventory
- discounts
- clinical results
- product claims

---

## 11. RECOMMENDATION ARCHITECTURE

Use this conceptual flow:

```text
User Answers
     ↓
Structured Skin Profile
     ↓
Concern Matching
     ↓
Product Attributes
     ↓
Compatible Products
     ↓
AM / PM Routine
```

If recommendation APIs already exist, consume them.

If not, create a clean service/interface layer with mock data so the backend can replace it later.

Keep recommendation/business logic out of visual components.

Example conceptual interfaces:

```ts
getSkinProfile()
getRecommendedProducts()
getRecommendedRoutine()
```

Adapt naming to the existing project conventions.

---

## 12. PRODUCT RESULT CARD

Use real product data:

```text
[PRODUCT IMAGE]

PRODUCT NAME
Short product description

WHY IT'S HERE
→ Supports your selected concern
→ Fits your selected preference

PRICE

[ VIEW PRODUCT ]
```

Primary CTA:

`ADD RITUAL TO BAG`

Use the existing cart implementation. Do not create another cart.

---

## 13. HOMEPAGE INTEGRATION

Find the existing Élanor AI Routine Builder / AI Skin Diagnostic section.

Update its primary CTA to point to this experience.

Preferred wording:

`MEET YOUR SKIN CONCIERGE →`

Do not remove existing functionality unnecessarily.

---

## 14. EXISTING COMPONENT INTEGRATION

Reuse existing:

- navigation
- typography
- buttons
- product cards
- product detail pages
- cart
- wishlist
- responsive utilities
- theme tokens
- image assets

Do not duplicate components when an equivalent already exists.

---

## 15. RESPONSIVE REQUIREMENTS

Test at:

- 1440px+
- 1280px
- 1024px
- 768px
- 390px
- 375px

Desktop should be editorial and spacious.

Mobile must be intentionally designed, not merely a compressed desktop version.

Pay particular attention to imagery, typography, option controls, sticky CTAs, result cards, product grids, and navigation.

---

## 16. IMAGE / ASSET RULES

Use existing Élanor assets first.

For new imagery:

- premium editorial skincare photography
- botanical/clinical Élanor aesthetic
- consistent lighting
- consistent color temperature
- natural textures
- useful negative space
- no generic AI-looking beauty imagery

Do not add imagery just to fill space.

---

## 17. TECHNICAL ARCHITECTURE

Follow the existing frontend architecture.

Maintain:

```text
UI Components
     ↓
Feature Logic
     ↓
Service / API Layer
     ↓
Backend
```

A dedicated feature structure may be used if compatible with the current project:

```text
ai-skin-concierge/
  components/
  data/
  hooks/
  services/
  types/
  utils/
```

Adapt to existing conventions instead of forcing this exact structure.

---

## 18. BACKEND READINESS

The frontend must remain compatible with the planned Spring Boot + PostgreSQL + REST backend.

Use typed domain models.

Do not hardcode business rules throughout UI components.

The implementation should allow:

```text
mockRecommendationService
```

to be replaced by:

```text
realRecommendationService
```

without rebuilding the UI.

---

## 19. AI READINESS

Do not integrate an LLM merely for visual demonstration.

Future AI capabilities may include:

- product questions
- recommendations
- comparisons
- shopping intent
- personalized routine generation
- product explanations

Future AI output must be grounded in authoritative Élanor catalog/business data.

AI must never invent:

- products
- prices
- inventory
- shipping
- orders
- refunds
- discounts
- clinical claims

---

## 20. ACCESSIBILITY

Implement:

- semantic HTML
- keyboard navigation
- visible focus states
- accessible labels
- sufficient contrast
- screen-reader-friendly controls
- accessible progress indication
- reduced-motion support

Do not use color alone to communicate selection.

---

## 21. PERFORMANCE

Keep the feature lightweight:

- lazy-load below-the-fold images
- optimize image dimensions
- avoid huge unnecessary assets
- avoid unnecessary dependencies
- avoid duplicate API calls
- prevent layout shift
- preserve existing application performance

---

## 22. STRICT DO-NOT RULES

Never:

- rebuild the entire Élanor website
- replace the existing design system without justification
- copy the Dribbble page pixel-for-pixel
- copy copyrighted assets
- create a generic chatbot UI
- create a fake medical diagnosis
- invent product information
- invent clinical claims
- create a second cart
- hardcode backend business logic into UI
- add unnecessary libraries
- introduce microservices
- modify unrelated features
- expose secrets
- read, print, copy, or commit credentials
- read/display secret `.env` contents
- place API keys, tokens, passwords, or JWT secrets in source code
- weaken existing security controls

---

## 23. AGENT WORKFLOW

Follow exactly:

```text
1. INSPECT
2. IDENTIFY EXISTING ROUTES / COMPONENTS / ASSETS
3. IDENTIFY REUSABLE DESIGN TOKENS
4. INSPECT EXISTING AI ROUTINE BUILDER
5. INSPECT PRODUCT + CART SERVICES
6. IDENTIFY MINIMUM REQUIRED CHANGES
7. PLAN
8. IMPLEMENT
9. TYPECHECK / LINT
10. BUILD
11. TEST DESKTOP
12. TEST MOBILE
13. CHECK EXISTING ROUTES FOR REGRESSIONS
14. CHECK ACCESSIBILITY
15. CHECK PERFORMANCE
16. REPORT RESULT
```

Do not start implementation before inspecting the existing code.

Only modify files required for this feature.

---

## 24. DEFINITION OF DONE

- [ ] AI Skin Concierge route works
- [ ] Intro screen implemented
- [ ] Four-step consultation implemented
- [ ] Progress indicator works
- [ ] Back navigation works
- [ ] Answers persist during consultation
- [ ] Analysis state works
- [ ] Personalized profile works
- [ ] AM/PM routine works
- [ ] Real Élanor product data is used where available
- [ ] Add-to-Bag uses existing cart
- [ ] Homepage AI CTA links to the experience
- [ ] Desktop responsive
- [ ] Mobile responsive
- [ ] Accessibility checked
- [ ] No console errors
- [ ] Typecheck passes
- [ ] Build passes
- [ ] Existing functionality remains intact
- [ ] No secrets exposed
- [ ] No unnecessary dependencies added

---

## 25. FINAL PRODUCT PRINCIPLE

Build this as:

**ÉLANOR'S DIGITAL SKIN CONSULTATION**

—not as an AI chatbot.

The final experience should feel:

**Editorial + Botanical + Clinical + Personal + Intelligent + Premium**

The QClay/AI skincare reference provides interaction inspiration.

Veloura provides editorial luxury inspiration.

Élanor remains the brand.

Do not sacrifice Élanor's identity to reproduce either reference.

---

# 26. ANIMATION SYSTEM — MANDATORY

The target is **90–95% visual and interaction fidelity** to the referenced AI Skincare Cosmetologist concept, while keeping Élanor's own brand identity.

Animation is a first-class feature, not an optional enhancement.

## Primary animation stack

Use:

- **GSAP** as the primary animation engine
- **GSAP ScrollTrigger** for scroll-linked animation, pinning, scrubbing, parallax and section choreography
- **Lenis** for smooth scrolling where compatible with the existing application
- Existing CSS/SVG capabilities for lightweight visual effects
- **Motion** only for isolated React micro-interactions where it provides a clear advantage; do not duplicate GSAP responsibilities
- **Three.js / React Three Fiber** ONLY if a specific reference effect genuinely requires WebGL/3D; do not add it by default

### Animation responsibility rule

```text
GSAP = primary choreography
ScrollTrigger = scroll choreography
Lenis = smooth scrolling
CSS/SVG = lightweight styling/effects
Motion = small isolated React interactions only
Three.js/R3F = optional WebGL only when justified
```

Do not install multiple animation libraries and use them randomly.

Before adding any dependency, inspect the existing package.json and project architecture. If GSAP/Lenis/Motion already exists, reuse the installed version and existing conventions.

---

## 26.1 Animation philosophy

The experience must feel:

- editorial
- cinematic
- premium
- fluid
- organic
- restrained
- intentional
- expensive

Avoid:

- cartoon animation
- excessive bouncing
- aggressive rotation
- flashy transitions
- constant looping effects
- excessive parallax
- decorative motion with no UX purpose
- generic SaaS animation presets

The goal is not to animate everything. The goal is to make the interface feel alive and continuously connected.

---

## 26.2 Page-entry choreography

On initial entry to `/ai-skin-concierge`, use a GSAP timeline to reveal the experience in sequence:

```text
background / visual atmosphere
        ↓
navigation / brand mark
        ↓
hero image
        ↓
headline
        ↓
supporting copy
        ↓
CTA
```

Use subtle combinations of:

- opacity
- translateY
- translateX where appropriate
- scale
- clip-path/mask reveal where visually justified

Hero imagery may begin slightly zoomed, e.g. approximately `1.03–1.08`, and settle naturally to `1`.

Do not make the entrance excessively slow.

---

## 26.3 Hero image motion

Hero/product imagery should have subtle cinematic motion:

- controlled scale
- subtle x/y movement
- optional low-intensity parallax
- natural image-position shift
- soft reveal/masking where appropriate

Do not use obvious infinite floating animations for premium product imagery.

---

## 26.4 Editorial typography animation

Do not fade an entire headline as one block when a more refined reveal is appropriate.

Use GSAP to animate lines/words/elements progressively.

Example:

```text
Your
    ↓
skin
    ↓
has a story.
```

Use line/word splitting only when it does not harm accessibility or responsive wrapping.

The semantic text must remain accessible to screen readers.

---

## 26.5 Consultation transition

When the user clicks:

`BEGIN CONSULTATION →`

animate the transition rather than performing an abrupt visual replacement.

Sequence:

```text
intro content exits
        ↓
consultation background/state enters
        ↓
progress indicator appears
        ↓
question heading enters
        ↓
options stagger in
        ↓
Continue CTA enters
```

Use a coordinated GSAP timeline.

---

## 26.6 Question-to-question transition

When changing questions, use a smooth directional transition.

Current state:

```text
opacity: 1
x: 0
```

Exit approximately toward:

```text
opacity: 0
x: -20 to -40px
```

New state enters approximately from:

```text
opacity: 0
x: 20 to 40px
```

and settles at:

```text
opacity: 1
x: 0
```

Target overall transition: approximately `500–800ms`, tuned visually rather than treated as a rigid number.

Back navigation should reverse the direction appropriately.

---

## 26.7 Option-selection micro-interactions

When a user selects an answer:

- subtle scale change
- border/background transition
- selected indicator/check reveal
- restrained text/icon movement where appropriate

Do not bounce selections.

Selection must remain obvious even with reduced motion enabled.

---

## 26.8 Progress animation

Progress between:

```text
01 / 04
02 / 04
03 / 04
04 / 04
```

must interpolate smoothly.

If a progress bar/line is used, animate its visual position rather than instantly replacing it.

The progress state must remain accessible to assistive technology.

---

## 26.9 Analysis animation

The analysis state is a major motion-design moment.

Animate stages sequentially:

```text
UNDERSTANDING YOUR SKIN

01  Skin profile
02  Concern compatibility
03  Formula selection
04  Ritual construction
```

Each stage should:

- enter
- become active
- complete
- transition to the next

Use GSAP timelines.

Important: do not falsely claim that real AI processing is happening if the backend/AI service is not connected. This is a presentation state until real processing exists.

---

## 26.10 Results reveal choreography

Do not render the entire result page visibly at once.

Reveal progressively:

```text
section/background
        ↓
result heading
        ↓
profile name
        ↓
profile metrics
        ↓
metric visuals
        ↓
AM ritual
        ↓
PM ritual
        ↓
recommended products
        ↓
Add Ritual CTA
```

Use staggered GSAP timelines.

---

## 26.11 Metric animation

For personalization indicators such as:

```text
Hydration 78%
Barrier 84%
Radiance 91%
```

animate numeric values from `0` to the final value and synchronize the visual indicator.

These are personalization indicators, not medical measurements. Do not present them as validated medical/scientific measurements.

---

## 26.12 Product reveal and interaction

Recommended products should enter with subtle:

- opacity
- translateY
- scale
- image reveal/mask
- stagger

Product images may use subtle hover movement/parallax where appropriate.

Do not use aggressive product spinning or artificial floating effects.

Use actual Élanor product data and images.

---

## 26.13 ScrollTrigger requirements

Use **GSAP ScrollTrigger** where scrolling contributes to the storytelling.

Potential uses:

- section reveal
- image parallax
- product movement
- typography reveal
- pinned sections where justified
- scrubbed transitions
- staggered ingredient/product reveals

Use `scrub`, `pin`, `start`, `end`, responsive triggers and timelines only where they improve the experience.

Do not create unnecessary ScrollTriggers for every element.

Avoid scroll-jacking that makes normal browsing difficult.

---

## 26.14 Smooth scrolling

If the current application architecture permits, use **Lenis** for premium smooth scrolling.

Integrate Lenis with GSAP/ScrollTrigger correctly so scroll-linked animations remain synchronized.

Do not introduce Lenis if it causes:

- broken native touch scrolling
- accessibility problems
- routing problems
- excessive bundle/performance cost
- conflicts with existing smooth-scroll implementation

On mobile, prioritize native-feeling touch scrolling.

---

## 26.15 Motion usage rule

Motion may be used for small isolated React interactions such as:

- button hover/tap
- small layout/state transitions
- local component interactions

GSAP remains the primary animation system.

Do not implement the same animation with both Motion and GSAP.

---

## 26.16 Optional Three.js / React Three Fiber rule

Three.js/R3F is **not required by default**.

Only introduce it when a specific visual requirement cannot be achieved cleanly with GSAP/CSS/SVG/image techniques, such as:

- genuine 3D product presentation
- WebGL image displacement
- advanced liquid simulation
- interactive 3D botanical object
- shader-based visual treatment

If introduced:

- lazy-load it
- isolate it to the required section
- provide a fallback
- avoid making the entire page dependent on WebGL
- test low-end devices
- respect reduced motion

Do not add Three.js merely because the page is called AI.

---

## 26.17 GSAP React architecture

Do not scatter GSAP code randomly through components.

Follow the existing project structure. A possible structure is:

```text
ai-skin-concierge/
  animations/
    hero.ts
    consultation.ts
    analysis.ts
    results.ts
    products.ts
  components/
  hooks/
  services/
  types/
  utils/
```

Adapt this to the existing architecture instead of forcing a new folder system.

Use `useGSAP()` or equivalent GSAP context cleanup for React components where available.

All timelines and ScrollTriggers must be properly cleaned up when components unmount.

---

## 26.18 Performance rules for animation

Prefer GPU-friendly properties:

```text
transform
opacity
```

Avoid continuous animation of expensive layout properties such as:

```text
width
height
top
left
```

unless there is a specific reason.

Use filters/blur/shadows carefully because they can be expensive, especially on mobile.

Do not create unnecessary `requestAnimationFrame` loops.

Do not create dozens of independent ScrollTriggers for the same section.

Lazy-load large visual assets.

Use appropriately sized WebP/AVIF images where possible.

4K source imagery is acceptable, but do not blindly ship 4K files to every viewport. Generate responsive sizes and serve the smallest appropriate asset.

---

## 26.19 Reduced-motion behavior

Respect:

```css
prefers-reduced-motion: reduce
```

When reduced motion is enabled:

- disable parallax
- remove large transforms
- shorten or remove decorative motion
- avoid continuous loops
- preserve state clarity
- retain necessary opacity/state transitions where useful

The consultation must remain fully usable without animation.

---

## 26.20 Animation quality gate

The feature is NOT considered complete if it only has static screens.

Before declaring completion, visually verify:

- initial page reveal
- hero image motion
- typography reveal
- CTA interaction
- consultation transition
- question transitions
- answer selection
- progress movement
- analysis sequence
- result reveal
- metric animation
- product reveal
- scroll behavior
- mobile behavior
- reduced-motion behavior

Animations must feel coordinated rather than independently triggered.

---

# 27. FIDELITY TARGET — 90–95%

The implementation target is **90–95% visual/interaction fidelity** to the referenced AI skincare concept, not literal source-code or copyrighted-asset duplication.

Match the reference's:

- visual hierarchy
- composition principles
- spacing rhythm
- typography scale and reveal behavior
- image treatment
- section transitions
- interaction pacing
- animation language
- CTA behavior
- scrolling experience
- overall premium polish

But retain Élanor's:

- brand identity
- typography system where already established
- colors/design tokens where applicable
- botanical/clinical positioning
- product catalog
- existing navigation
- existing commerce flows

Do not claim exact implementation of hidden reference code or unavailable assets.

---

# 28. DEPENDENCY AND FREE-TIER RULE

This feature must remain compatible with the project's **free-tier-first** constraint.

Before adding any package:

1. inspect whether it already exists
2. verify it is open-source/free for this use
3. assess bundle/performance impact
4. confirm it is actually necessary
5. prefer the smallest viable dependency set

Recommended baseline:

```text
GSAP
GSAP ScrollTrigger
Lenis
CSS/SVG
```

Motion is optional.

Three.js/R3F is optional and must be justified.

Do not add paid animation platforms, paid APIs, or hosted animation services.

---

# 29. FINAL ANIMATION STACK DECISION

Unless the existing repository has a strong reason to differ, implement the feature with:

```text
React / Next.js
      │
      ├── GSAP
      │     ├── timelines
      │     ├── tweens
      │     ├── typography reveals
      │     ├── image choreography
      │     └── page/section transitions
      │
      ├── ScrollTrigger
      │     ├── scroll reveals
      │     ├── scrub
      │     ├── pin
      │     └── parallax
      │
      ├── Lenis
      │     └── smooth scrolling
      │
      ├── CSS / SVG
      │     └── lightweight visual effects
      │
      ├── Motion (optional)
      │     └── isolated React micro-interactions
      │
      └── Three.js / R3F (optional)
            └── only justified WebGL effects
```

**GSAP is the animation authority.** Do not duplicate animation responsibilities across libraries.

---

# 30. UPDATED DEFINITION OF DONE — ANIMATION

In addition to the functional checklist above, the feature is complete only when:

- [ ] GSAP is used for primary animation choreography
- [ ] ScrollTrigger is used where scroll storytelling requires it
- [ ] smooth scrolling is implemented only if compatible with the existing app
- [ ] page-entry sequence is polished
- [ ] typography reveal is polished
- [ ] hero/image motion is polished
- [ ] consultation transitions are animated
- [ ] question transitions are animated
- [ ] option selection has micro-interaction
- [ ] progress transitions smoothly
- [ ] analysis sequence is animated
- [ ] results progressively reveal
- [ ] metrics animate into their final values
- [ ] product cards/images reveal with stagger
- [ ] desktop scroll behavior is tested
- [ ] mobile touch behavior is tested
- [ ] reduced-motion behavior is tested
- [ ] animations do not cause console errors
- [ ] GSAP/ScrollTrigger contexts are cleaned up
- [ ] no unnecessary animation dependency was introduced
- [ ] no paid service is required
- [ ] performance remains acceptable on normal/free-tier hosting
- [ ] 4K source assets are responsively optimized rather than shipped at full resolution everywhere

---

# 31. FINAL IMPLEMENTATION PRIORITY

When there is a conflict between visual fidelity and engineering quality, use this priority:

```text
1. Existing Élanor architecture
2. Security / accessibility
3. Functional correctness
4. Élanor brand identity
5. 90–95% reference interaction fidelity
6. Animation quality
7. Performance
8. Optional visual enhancements
```

Never sacrifice application stability merely to reproduce an animation.

The final result should feel like a **real, premium, production-quality Élanor digital skincare consultation**, with the reference's level of motion and polish while remaining maintainable, responsive, accessible, and free-tier compatible.
