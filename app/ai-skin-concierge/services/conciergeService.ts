import { PRODUCTS, Product } from '@/data/products';
import {
  ConsultationAnswers,
  ConciergeResult,
  SkinProfileResult,
  RitualStep,
  MetricScore,
} from '../types';

export function evaluateConsultation(answers: ConsultationAnswers): ConciergeResult {
  const { skinType, concerns, skinContext, ritualPace, texturePreference } = answers;

  // 1. Calculate Personalization Indicators (0 - 100)
  let hydrationScore = 75;
  let barrierScore = 80;
  let radianceScore = 70;
  let sensitivityScore = 40;

  if (skinType === 'Dry / Tight') {
    hydrationScore = 58;
    barrierScore = 64;
    sensitivityScore += 15;
  } else if (skinType === 'Oily / Shiny') {
    hydrationScore = 82;
    radianceScore = 68;
    barrierScore = 85;
  } else if (skinType === 'Sensitive / Reactive') {
    sensitivityScore = 78;
    barrierScore = 62;
  } else if (skinType === 'Combination') {
    hydrationScore = 74;
    barrierScore = 78;
    radianceScore = 80;
  } else {
    // Comfortable
    hydrationScore = 88;
    barrierScore = 90;
    radianceScore = 85;
    sensitivityScore = 28;
  }

  // Adjust for concerns
  if (concerns.includes('Radiance') || concerns.includes('Uneven Tone')) {
    radianceScore = Math.min(96, radianceScore + 14);
  }
  if (concerns.includes('Barrier Restoration') || concerns.includes('Calming')) {
    barrierScore = Math.max(50, barrierScore - 10);
    sensitivityScore = Math.min(92, sensitivityScore + 18);
  }
  if (concerns.includes('Deep Quenching')) {
    hydrationScore = Math.max(45, hydrationScore - 12);
  }

  // Adjust for climate
  if (skinContext === 'Dry / Alpine / Cold' || skinContext === 'Air-Conditioned / Indoor') {
    hydrationScore = Math.max(40, hydrationScore - 8);
    barrierScore = Math.max(45, barrierScore - 6);
  }

  const metrics: MetricScore[] = [
    {
      name: 'Hydration Level',
      frenchName: 'Hydratation Derme',
      value: hydrationScore,
      color: '#6F8FAF',
    },
    {
      name: 'Barrier Integrity',
      frenchName: 'Bouclier Lipidique',
      value: barrierScore,
      color: '#7D9075',
    },
    {
      name: 'Cellular Radiance',
      frenchName: 'Luminance Cellulaire',
      value: radianceScore,
      color: '#C8A46A',
    },
    {
      name: 'Dermal Reactivity',
      frenchName: 'Sensibilité Cutanée',
      value: sensitivityScore,
      color: '#B87A6A',
    },
  ];

  // 2. Derive Archetype & Titles
  let archetype = 'The Luminous Resilience Protocol';
  let frenchTitle = 'Protocole Éclat & Architecture Barrière';
  let headline = 'Harmonizing deep cellular hydration with phytomolecular lipid reinforcement.';
  let cellularFocus = 'Bio-fermented saffron & 5-ceramide lipid synthesis';

  if (concerns.includes('Barrier Restoration') || skinType === 'Sensitive / Reactive') {
    archetype = 'The Velvet Barrier Sanctuary';
    frenchTitle = 'Sanctuaire Réparateur aux Céramides Nobles';
    headline = 'Intense lipid reconstruction to neutralize micro-reactivity and soothe fragile moisture mantles.';
    cellularFocus = 'Alpine Edelweiss callus + 5-ceramide biomimetic architecture';
  } else if (concerns.includes('Radiance') || concerns.includes('Uneven Tone')) {
    archetype = 'The Solar Radiance Infusion';
    frenchTitle = 'Cure Illuminatrice Éclat Suprême';
    headline = 'High-potency cellular brightening and antioxidant defense to dissolve micro-pigment shadows.';
    cellularFocus = 'Golden saffron stem cells + 10% bio-niacinamide';
  } else if (concerns.includes('Firming')) {
    archetype = 'The Cellular Longevity Architecture';
    frenchTitle = 'Architecture Cellulaire & Rénovation Nocturne';
    headline = 'Accelerating nocturnal cellular turnover and collagen scaffolding with liposomal retinaldehyde.';
    cellularFocus = 'Liposomal Retinaldehyde 0.1% + Tri-peptide complexes';
  } else if (concerns.includes('Deep Quenching')) {
    archetype = 'The Deep Ocean Quench';
    frenchTitle = 'Immersion Hydra-Cellulaire Profonde';
    headline = 'Multi-molecular thermal plumping to saturate intercellular reservoirs with lasting bounce.';
    cellularFocus = '8-tier hyaluronic complex + Glacier thermal springs';
  }

  const profile: SkinProfileResult = {
    archetype,
    frenchTitle,
    headline,
    summary: `Based on your ${skinType.toLowerCase()} profile and ${concerns.join(', ').toLowerCase()} priorities under ${skinContext.toLowerCase()} conditions, your formulation strategy emphasizes targeted botanical actives in precise molecular weights.`,
    cellularFocus,
    metrics,
    matchScore: 98.6,
  };

  // 3. Map Products to Routine Steps (AM & PM)
  const cleanser = PRODUCTS.find((p) => p.category === 'Cleansers') || PRODUCTS[4];
  const serum = concerns.includes('Barrier Restoration')
    ? PRODUCTS.find((p) => p.id === 'elanor-hydra-infusion') || PRODUCTS[3]
    : PRODUCTS.find((p) => p.id === 'elanor-celestial-nectar') || PRODUCTS[0];
  const cream = PRODUCTS.find((p) => p.id === 'elanor-barrier-velvet') || PRODUCTS[1];
  const nightElixir = PRODUCTS.find((p) => p.id === 'elanor-nocturne-elixir') || PRODUCTS[2];
  const eyeCare = PRODUCTS.find((p) => p.id === 'elanor-regard-sculptant') || PRODUCTS[5];
  const mist = PRODUCTS.find((p) => p.id === 'elanor-rose-nectar-mist') || PRODUCTS[3];

  const morningRitual: RitualStep[] = [];
  const eveningRitual: RitualStep[] = [];

  // Step 1: Cleanse (Always)
  morningRitual.push({
    stepNumber: '01',
    phase: 'Purify & Awaken',
    timeOfDay: 'AM',
    product: cleanser,
    whyItsHere: [
      'Preserves the acid mantle with pH 5.5 lipid balance',
      'Removes overnight sebum without drying tight',
    ],
    applicationTip: 'Massage 2 pumps onto dry skin. Add water to transform to milk, then rinse.',
  });

  eveningRitual.push({
    stepNumber: '01',
    phase: 'Double Cleanse Dissolution',
    timeOfDay: 'PM',
    product: cleanser,
    whyItsHere: [
      'Breaks down environmental pollution and daily sunscreen effortlessly',
      'Infuses calming chamomile and camellia seed lipids',
    ],
    applicationTip: 'Take time to massage circular motions over face and neck for 60 seconds.',
  });

  // Step 2: Treat / Serum
  morningRitual.push({
    stepNumber: '02',
    phase: 'Cellular Activation',
    timeOfDay: 'AM',
    product: serum,
    whyItsHere: [
      `Directly addresses ${concerns[0] || 'Radiance'} via bio-available actives`,
      'Provides daily antioxidant shield against environmental stress',
    ],
    applicationTip: 'Press 3-4 drops into skin with warm palms, breathing in the delicate floral essence.',
  });

  eveningRitual.push({
    stepNumber: '02',
    phase: 'Nocturnal Metamorphosis',
    timeOfDay: 'PM',
    product: nightElixir,
    whyItsHere: [
      'Micro-encapsulated retinaldehyde accelerates overnight cellular turnover',
      'Silky botanical squalane prevents retinoid dryness',
    ],
    applicationTip: 'Smooth 4 drops across forehead, cheeks, and neck before cream.',
  });

  // Step 3: Eye Care (if Balanced or Complete)
  if (ritualPace !== 'Minimal') {
    morningRitual.push({
      stepNumber: '03',
      phase: 'Periorbital Sculpting',
      timeOfDay: 'AM',
      product: eyeCare,
      whyItsHere: [
        'Tri-peptides and green tea caffeine rapidly depuff and tighten morning contour',
        'Reflects light to brighten dark shadows',
      ],
      applicationTip: 'Glide the cooling ceramic tip from inner eye outwards.',
    });

    eveningRitual.push({
      stepNumber: '03',
      phase: 'Periorbital Regeneration',
      timeOfDay: 'PM',
      product: eyeCare,
      whyItsHere: [
        'Continuous peptide repair throughout sleep',
        'Deeply cushions fragile under-eye tissue',
      ],
      applicationTip: 'Lightly tap around orbital bone with ring finger until melted.',
    });
  }

  // Step 4: Seal / Cream
  morningRitual.push({
    stepNumber: ritualPace === 'Minimal' ? '03' : '04',
    phase: 'Lipid Shield & Prime',
    timeOfDay: 'AM',
    product: cream,
    whyItsHere: [
      '5-ceramide biomimetic structure locks active serums in place',
      'Protects against trans-epidermal moisture loss',
    ],
    applicationTip: 'Warm a pearl-sized amount and press upward across face and neck.',
  });

  eveningRitual.push({
    stepNumber: ritualPace === 'Minimal' ? '03' : '04',
    phase: 'Deep Dermal Cushion',
    timeOfDay: 'PM',
    product: cream,
    whyItsHere: [
      'Creates a breathable nocturnal barrier film',
      'Alpine Edelweiss callus extracts stimulate overnight repair',
    ],
    applicationTip: 'Apply generously as the luxurious final sealing veil of your evening.',
  });

  // Calculate distinct products and prices
  const allProducts = [...morningRitual.map((r) => r.product), ...eveningRitual.map((r) => r.product)];
  const uniqueProducts = allProducts.filter(
    (product, index, self) => index === self.findIndex((p) => p.id === product.id)
  );

  const totalPrice = uniqueProducts.reduce((sum, p) => sum + p.price, 0);
  const bundlePrice = Math.round(totalPrice * 0.85); // 15% bespoke concierge ritual discount

  return {
    profile,
    morningRitual,
    eveningRitual,
    uniqueProducts,
    totalPrice,
    bundlePrice,
  };
}
