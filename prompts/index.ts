/**
 * Maison Élanor — Automated AI Prompt Management Engine
 * 
 * Provides type-safe prompt builders, schema definitions, and system instructions
 * for Gemini API interactions across the Élanor luxury platform.
 */

export interface DiagnosticInputs {
  skinType: 'dry' | 'combination' | 'oily' | 'sensitive' | string;
  primaryConcern: 'radiance' | 'anti-aging' | 'barrier-repair' | 'hydration' | 'calming' | 'clarifying' | string;
  climate: 'urban' | 'dry' | 'tropical' | 'temperate' | string;
  pace: 'minimalist' | 'haute' | string;
  sensitivities?: string;
}

export interface DiagnosticStep {
  stepNumber: number;
  productId: string;
  productName: string;
  action: string;
  dosage: string;
  clinicalRationale: string;
  technique: string;
}

export interface DiagnosticResponse {
  diagnosticSummary: {
    title: string;
    dermalProfile: string;
    climateAdaptation: string;
    keySynergyActives: string[];
  };
  morningRitual: {
    stepCount: number;
    steps: DiagnosticStep[];
  };
  eveningRitual: {
    stepCount: number;
    steps: DiagnosticStep[];
  };
  bundleRecommendation: {
    productIds: string[];
    discountPercentage: number;
    rationale: string;
  };
}

export const SYSTEM_PROMPT_ELANOR_CORE = `
You are the Lead Formulation Chemist & Dermatological Advisor for Maison Élanor (Paris Atelier at 24 Place Vendôme & Alpine Botany Laboratory in Grasse).
Brand Essence: Haute Botanique & Clinical Cellular Longevity.
Philosophy: "Pure Beauty. Naturally." Synthesizing wild-harvested French/Swiss alpine botanicals with lipid-identical clinical biotechnology.
Tone: Quiet luxury, sophisticated, scientifically rigorous, sensory, poetic, and reassuring.
Guardrails: Provide cosmetic formulation advice. Never fabricate products outside the authentic Élanor catalog.
Always respond in valid, structured JSON adhering strictly to the requested schema without wrapping in markdown code fences unless specified.
`.trim();

/**
 * Automates the creation of the diagnostic prompt payload for the Gemini API
 */
export function buildDiagnosticPrompt(inputs: DiagnosticInputs): string {
  return `
Analyze the patron's skin profile and formulate a bespoke Morning and Evening Haute Botanique ritual:
- Dermal Phenotype: ${inputs.skinType}
- Primary Concern: ${inputs.primaryConcern}
- Environment / Climate: ${inputs.climate}
- Regimen Architecture: ${inputs.pace === 'haute' ? 'Haute 5-Step Complex' : 'Minimalist 3-Step Protocol'}
${inputs.sensitivities ? `- Reported Sensitivities / Notes: ${inputs.sensitivities}` : ''}

Available Formulation IDs in Catalog:
- "celestial-nectar-serum" (Saffron Stem Cells + Hyaluronic Matrix for Cellular Radiance)
- "velvet-barrier-cream" (5-Ceramide Biomimetic Matrix + Alpine Edelweiss for Barrier Fortification)
- "aurora-eye-elixir" (Liposomal Caffeine + Bio-Peptides for Micro-circulation)
- "rose-resurfacing-essence" (Wild Damask Rose Hydrosol + Micro-AHA for Gentle Exfoliation)
- "botanical-cleansing-oil" (Camellia Seed + Squalane for Deep Double Cleansing)
- "nocturne-recovery-balm" (Encapsulated Retinaldehyde 0.1% + Bakuchiol for Overnight Metamorphosis)

Output must follow the JSON schema with:
1. diagnosticSummary (title, dermalProfile, climateAdaptation, keySynergyActives)
2. morningRitual (steps with productId, productName, action, dosage, clinicalRationale, technique)
3. eveningRitual (steps with productId, productName, action, dosage, clinicalRationale, technique)
4. bundleRecommendation (productIds, discountPercentage: 15, rationale)
`.trim();
}

/**
 * JSON Schema definition for Gemini response_schema
 */
export const DIAGNOSTIC_RESPONSE_SCHEMA = {
  type: "OBJECT",
  properties: {
    diagnosticSummary: {
      type: "OBJECT",
      properties: {
        title: { type: "STRING" },
        dermalProfile: { type: "STRING" },
        climateAdaptation: { type: "STRING" },
        keySynergyActives: {
          type: "ARRAY",
          items: { type: "STRING" }
        }
      },
      required: ["title", "dermalProfile", "climateAdaptation", "keySynergyActives"]
    },
    morningRitual: {
      type: "OBJECT",
      properties: {
        stepCount: { type: "INTEGER" },
        steps: {
          type: "ARRAY",
          items: {
            type: "OBJECT",
            properties: {
              stepNumber: { type: "INTEGER" },
              productId: { type: "STRING" },
              productName: { type: "STRING" },
              action: { type: "STRING" },
              dosage: { type: "STRING" },
              clinicalRationale: { type: "STRING" },
              technique: { type: "STRING" }
            },
            required: ["stepNumber", "productId", "productName", "action", "dosage", "clinicalRationale", "technique"]
          }
        }
      },
      required: ["stepCount", "steps"]
    },
    eveningRitual: {
      type: "OBJECT",
      properties: {
        stepCount: { type: "INTEGER" },
        steps: {
          type: "ARRAY",
          items: {
            type: "OBJECT",
            properties: {
              stepNumber: { type: "INTEGER" },
              productId: { type: "STRING" },
              productName: { type: "STRING" },
              action: { type: "STRING" },
              dosage: { type: "STRING" },
              clinicalRationale: { type: "STRING" },
              technique: { type: "STRING" }
            },
            required: ["stepNumber", "productId", "productName", "action", "dosage", "clinicalRationale", "technique"]
          }
        }
      },
      required: ["stepCount", "steps"]
    },
    bundleRecommendation: {
      type: "OBJECT",
      properties: {
        productIds: {
          type: "ARRAY",
          items: { type: "STRING" }
        },
        discountPercentage: { type: "INTEGER" },
        rationale: { type: "STRING" }
      },
      required: ["productIds", "discountPercentage", "rationale"]
    }
  },
  required: ["diagnosticSummary", "morningRitual", "eveningRitual", "bundleRecommendation"]
};
