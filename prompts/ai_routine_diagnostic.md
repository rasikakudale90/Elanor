# AI Routine Builder — Clinical Diagnostic Prompt

## Context & Purpose
This prompt transforms user diagnostic inputs (Dermal Phenotype, Primary Concern, Climate, Ritual Pace, and Sensitivity) into a personalized Haute Botanique Morning & Evening regimen with clinical rationale.

---

## User Input Variables
```json
{
  "skinType": "{{skinType}}",         // e.g. "dry", "combination", "oily", "sensitive"
  "primaryConcern": "{{primaryConcern}}", // e.g. "radiance", "anti-aging", "barrier-repair", "hydration", "calming", "clarifying"
  "climate": "{{climate}}",           // e.g. "urban", "dry", "tropical", "temperate"
  "pace": "{{pace}}",                 // e.g. "minimalist" (3 steps), "haute" (5 steps)
  "sensitivities": "{{sensitivities}}" // optional custom user notes
}
```

---

## Task Instructions
1. **Dermal Diagnostic**: Analyze the cellular state, lipid barrier integrity, and micro-environmental oxidative stress based on the user's climate and skin phenotype.
2. **Regimen Architecture**:
   - **Morning Awakening Protocol**: Focus on antioxidant shield, moisture barrier reinforcement, and daytime radiance without clogging pores.
   - **Nocturnal Metamorphosis Protocol**: Focus on cellular turnover, lipid replenishment, collagen synthesis, and overnight moisture lock.
3. **Synergy Rationale**: Explain why these specific botanical actives and biotech molecules synergize (e.g., Saffron Stem Cells + 5-Ceramide Matrix).
4. **Application Technique**: Describe the sensory tactile ritual (e.g. warming drops between palms, upward lymphatic drainage glides).
5. **Deterministic Product Selection**: Select from available Élanor product IDs (`celestial-nectar-serum`, `velvet-barrier-cream`, `aurora-eye-elixir`, `rose-resurfacing-essence`, `botanical-cleansing-oil`, `nocturne-recovery-balm`).

---

## Structured JSON Output Schema
```json
{
  "diagnosticSummary": {
    "title": "Cellular Longevity & Barrier Fortification Protocol",
    "dermalProfile": "string",
    "climateAdaptation": "string",
    "keySynergyActives": ["string"]
  },
  "morningRitual": {
    "stepCount": 3,
    "steps": [
      {
        "stepNumber": 1,
        "productId": "string",
        "productName": "string",
        "action": "Cleanse / Hydrate / Protect",
        "dosage": "string (e.g. 3-4 drops)",
        "clinicalRationale": "string",
        "technique": "string"
      }
    ]
  },
  "eveningRitual": {
    "stepCount": 3,
    "steps": [
      {
        "stepNumber": 1,
        "productId": "string",
        "productName": "string",
        "action": "Double Cleanse / Regenerate / Seal",
        "dosage": "string (e.g. pearl-sized amount)",
        "clinicalRationale": "string",
        "technique": "string"
      }
    ]
  },
  "bundleRecommendation": {
    "productIds": ["string"],
    "discountPercentage": 15,
    "rationale": "string"
  }
}
```
