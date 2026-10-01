# INCI & Bio-Active Synergy Analyzer Prompt

## Context & Purpose
Evaluates ingredient compatibility, bioavailability, pH stability, and contraindications between active botanicals and biotech compounds in the Élanor formulary.

---

## User Input Variables
```json
{
  "activeIngredients": ["Saffron Stem Cells", "Liposomal Retinaldehyde", "5-Ceramide Complex", "Hyaluronic Acid 8D"],
  "userSkinType": "sensitive",
  "targetConcern": "barrier-repair"
}
```

---

## Evaluation Directives
1. **Bio-Synergies**: Identify active pairs that potentiate each other's cellular penetration or efficacy (e.g., Ceramides + Fatty Acids + Cholesterol in 3:1:1 equimolar ratio).
2. **Conflict Prevention**: Flag any pH collisions or high-irritancy combinations (e.g. high-percentage direct acids with unbuffered retinoids).
3. **Miron Violet Preservation Impact**: Note how biophotonic violet glass extends the photochemical stability of delicate alpine antioxidants.

---

## JSON Output Schema
```json
{
  "synergyScore": 96,
  "compatibilityRating": "Optimally Synergistic",
  "keyMechanisms": [
    {
      "activePair": "string",
      "mechanism": "string",
      "cellularBenefit": "string"
    }
  ],
  "layeringAdvice": "string",
  "precautions": ["string"]
}
```
