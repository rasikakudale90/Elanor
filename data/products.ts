export interface Product {
  id: string;
  name: string;
  frenchSubtitle: string;
  category: 'Serums' | 'Creams' | 'Elixirs' | 'Cleansers' | 'Masks' | 'Eye Care';
  concern: 'Hydration' | 'Anti-Aging' | 'Radiance' | 'Barrier Repair' | 'Calming' | 'Clarifying';
  price: number;
  rating: number;
  reviewsCount: number;
  tagline: string;
  description: string;
  volume: string;
  texture: string;
  skinTypes: string[];
  keyActives: string[];
  benefits: string[];
  usageRitual: string;
  clinicalResults: { metric: string; description: string }[];
  image: string;
  hoverImage?: string;
  isBestSeller?: boolean;
  isNew?: boolean;
  isAwardWinner?: boolean;
  stock: number;
}

export const PRODUCTS: Product[] = [
  {
    id: 'elanor-soothing-balm',
    name: 'Soothing Balm',
    frenchSubtitle: 'Baume Apaisant Réparateur aux Plantes Rares',
    category: 'Creams',
    concern: 'Calming',
    price: 175,
    rating: 4.96,
    reviewsCount: 88,
    tagline: 'Infused with blue tansy, colloidal oat lipids, and rare alpine arnica to instantly pacify stressed and irritated complexions.',
    description: 'A comforting, melt-in botanical salve crafted to cocoon sensitized, reactive, and stressed skin in restorative phytomolecular comfort and moisture.',
    volume: '50 ml / 1.7 fl. oz.',
    texture: 'Silken whipped melting balm with a delicate herbal hue',
    skinTypes: ['Sensitive', 'Reactive', 'Dry', 'Redness-Prone', 'All Skin Types'],
    keyActives: ['Blue Tansy Essential Oil', 'Colloidal Oat Lipids', 'Alpine Arnica Extract', 'Centella Asiatica'],
    benefits: [
      'Instantly calms flushing, redness, and irritation',
      'Strengthens compromised dermal lipid barrier',
      'Locks in continuous 48-hour moisture veil',
      'Non-greasy velvety melting finish'
    ],
    usageRitual: 'Melt a pea-sized amount between clean fingertips and press gently into areas of sensitivity or apply all over face as an intensive soothing ritual.',
    clinicalResults: [
      { metric: '98%', description: 'felt immediate soothing relief from tightness and heat' },
      { metric: '94%', description: 'saw marked reduction in visible skin redness within 7 days' },
      { metric: '100%', description: 'reported zero sensitivity or irritation' }
    ],
    image: '/images/skin8.png',
    hoverImage: '/images/skin7.png',
    isBestSeller: true,
    isNew: true,
    stock: 28
  },
  {
    id: 'elanor-celestial-nectar',
    name: 'Sérum Éclat Botanique',
    frenchSubtitle: 'Concentré Illuminateur aux Cellules Végétales',
    category: 'Serums',
    concern: 'Radiance',
    price: 185,
    rating: 4.95,
    reviewsCount: 142,
    tagline: 'Infused with golden saffron stem cells and bio-fermented niacinamide for an ethereal glass skin finish.',
    description: 'A transcendent elixir engineered to illuminate dull complexions, dissolve micro-pigmentation, and restore dermal luminescence within 14 days of botanical immersion.',
    volume: '30 ml / 1.0 fl. oz.',
    texture: 'Silken nectar with pearlescent micronized gold veil',
    skinTypes: ['All Skin Types', 'Dullness', 'Uneven Tone', 'Mature'],
    keyActives: ['Bio-Niacinamide 10%', 'Saffron Stem Extract', 'Ferulic Acid', 'White Truffle Essence'],
    benefits: [
      '+94% measured cellular luminosity',
      'Neutralizes free radicals and environmental stress',
      'Evens tone and reduces hyperpigmentation',
      'Provides 72-hour moisture cushion'
    ],
    usageRitual: 'Warm 3 to 4 drops between clean palms. Inhale the gentle damask rose notes, then press gently upwards from clavicle to forehead each morning and evening.',
    clinicalResults: [
      { metric: '98%', description: 'felt skin looked immediately brighter & lit-from-within' },
      { metric: '94%', description: 'observed visible reduction in dark spot intensity in 21 days' },
      { metric: '100%', description: 'reported zero irritation or sensitization' }
    ],
    image: '/images/skin.jfif',
    hoverImage: '/images/skin1.jfif',
    isBestSeller: true,
    isAwardWinner: true,
    stock: 24
  },
  {
    id: 'elanor-barrier-velvet',
    name: 'Crème Renaissance Barrière',
    frenchSubtitle: 'Soin Réparateur Profond aux Céramides Nobles',
    category: 'Creams',
    concern: 'Barrier Repair',
    price: 210,
    rating: 4.92,
    reviewsCount: 98,
    tagline: 'Lipid-identical bio-ceramides and cold-pressed Alpine Edelweiss to seal moisture resilience.',
    description: 'A comforting, whipped lipid cream that reconstructs compromised moisture barriers, calming redness and shielding dermal architecture against extreme climates.',
    volume: '50 ml / 1.7 fl. oz.',
    texture: 'Rich whipped velvet that melts seamlessly on touch',
    skinTypes: ['Sensitive', 'Dry', 'Post-Treatment', 'Dehydrated'],
    keyActives: ['5-Ceramide Biomimetic Complex', 'Alpine Edelweiss Callus', 'Phytosterols', 'Oat Beta-Glucan'],
    benefits: [
      'Instant relief for sensitized and reactive skin',
      'Restores the acid mantle within 3 hours',
      'Prevents trans-epidermal water loss by 89%',
      'Non-comedogenic comforting barrier film'
    ],
    usageRitual: 'Using the carved marble spatula, take a pearl-sized amount. Warm gently and smooth across face, neck, and décolletage as the grand finale of your evening ritual.',
    clinicalResults: [
      { metric: '96%', description: 'measured significant decrease in transepidermal water loss' },
      { metric: '92%', description: 'experienced immediate soothing of redness and tightness' },
      { metric: '95%', description: 'agreed their skin barrier felt fortified after 7 days' }
    ],
    image: '/images/skin1.jfif',
    hoverImage: '/images/skin2.jfif',
    isBestSeller: true,
    stock: 18
  },
  {
    id: 'elanor-nocturne-elixir',
    name: 'Élixir Nocturne Rénovateur',
    frenchSubtitle: 'Huile Précieuse au Rétinal Encapsulé',
    category: 'Elixirs',
    concern: 'Anti-Aging',
    price: 230,
    rating: 4.98,
    reviewsCount: 167,
    tagline: 'Micro-encapsulated 0.1% retinaldehyde paired with botanical squalane for overnight cellular metamorphosis.',
    description: 'Formulated for deep nocturnal renewal. Dramatically accelerates cellular turnover, smoothens micro-lines, and firms contours without the dryness typical of synthetic retinoids.',
    volume: '30 ml / 1.0 fl. oz.',
    texture: 'Featherlight dry botanical oil with a golden amber hue',
    skinTypes: ['Fine Lines', 'Loss of Elasticity', 'Uneven Texture'],
    keyActives: ['Liposomal Retinaldehyde 0.1%', 'Sugar-Cane Squalane', 'Bakuchiol', 'Evening Primrose Seed Oil'],
    benefits: [
      '3x faster conversion than retinol with zero flaking',
      'Stimulates collagen synthesis and cellular bounce',
      'Deeply plumps fine expression lines',
      'Silky non-greasy cushion overnight'
    ],
    usageRitual: 'Dispense 4 drops onto fingertips at night. Press gently into clean skin after water-based essences. Follow with Crème Renaissance for profound hydration.',
    clinicalResults: [
      { metric: '97%', description: 'showed dramatic improvement in skin firmness after 4 weeks' },
      { metric: '91%', description: 'saw marked reduction in depth of forehead and laugh lines' },
      { metric: '99%', description: 'awoke to supple, re-densified skin texture' }
    ],
    image: '/images/skin2.jfif',
    hoverImage: '/images/skin3.jfif',
    isAwardWinner: true,
    stock: 15
  },
  {
    id: 'elanor-hydra-infusion',
    name: 'Goutte d’Océan Hydra-Infusion',
    frenchSubtitle: 'Essence Plumping aux Multi-Molécules d’Acide Hyaluronique',
    category: 'Serums',
    concern: 'Hydration',
    price: 145,
    rating: 4.88,
    reviewsCount: 84,
    tagline: '8 weights of bio-compatible hyaluronic acid with glacier thermal water for intense quenching.',
    description: 'An ultra-dense micro-droplet infusion that penetrates deep epidermal strata, flooding cells with long-lasting moisture and instigating an undeniable dewy bounce.',
    volume: '100 ml / 3.4 fl. oz.',
    texture: 'Cooling liquid gel that bursts into weightless water essence',
    skinTypes: ['Dehydrated', 'Oily', 'Combination', 'All Skin Types'],
    keyActives: ['8-Tier Hyaluronic Complex', 'Glacier Thermal Springs', 'Blue Agave Bio-Ferment', 'Ectoin'],
    benefits: [
      'Increases hydration levels by +128% in 1 hour',
      'Plumps micro-crevices and dehydrated lines',
      'Primes skin to absorb subsequent active treatments',
      'Leaves a refreshed, non-sticky dew'
    ],
    usageRitual: 'Pour a few drops into the cupped palm. Press and pat vigorously onto damp skin directly after cleansing.',
    clinicalResults: [
      { metric: '100%', description: 'clinical increase in skin hydration instantly after application' },
      { metric: '94%', description: 'agreed skin felt soft, supple, and comfortably quenched all day' }
    ],
    image: '/images/skin3.jfif',
    hoverImage: '/images/skin4.jfif',
    isNew: true,
    stock: 30
  },
  {
    id: 'elanor-botanical-cleanser',
    name: 'Huile Gelée Pureté Céleste',
    frenchSubtitle: 'Nettoyant Fondant aux Huiles de Fleurs Rares',
    category: 'Cleansers',
    concern: 'Calming',
    price: 88,
    rating: 4.89,
    reviewsCount: 112,
    tagline: 'Transformative jelly-to-milk cleanser that melts water-resistant impurities while pampering the barrier.',
    description: 'A transcendent first-step cleanser. Gently dissolves long-wear makeup, sunscreens, and pollution without stripping essential moisture, leaving skin cushioned and purified.',
    volume: '150 ml / 5.1 fl. oz.',
    texture: 'Golden honey-like jelly transforming into a rich milky emulsion',
    skinTypes: ['All Skin Types', 'Sensitive', 'Dry', 'Acne-Prone'],
    keyActives: ['Camellia Japonica Seed Oil', 'Roman Chamomile Infusion', 'Bisabolol', 'Sweet Almond Lipids'],
    benefits: [
      'Effortlessly breaks down waterproof SPF & makeup',
      'Rinses completely clean without oily film',
      'Maintains optimal dermal pH balance (5.5)',
      'Calms irritation and micro-inflammation'
    ],
    usageRitual: 'Massage 2 pumps onto dry skin in circular motions. Add lukewarm water to transform into a milky veil, then rinse cleanly with warm water or our muslin cloth.',
    clinicalResults: [
      { metric: '99%', description: 'reported effective removal of all long-wear impurities' },
      { metric: '96%', description: 'noted skin felt comforted and moisturized post-rinse' }
    ],
    image: '/images/skin4.jfif',
    hoverImage: '/images/skin5.jfif',
    stock: 45
  },
  {
    id: 'elanor-regard-sculptant',
    name: 'Soin Regard Infini Peptides',
    frenchSubtitle: 'Crème Yeux Contour Liftante & Défatigante',
    category: 'Eye Care',
    concern: 'Anti-Aging',
    price: 165,
    rating: 4.93,
    reviewsCount: 76,
    tagline: 'Tri-peptide architecture with green tea caffeine to depuff, firm, and banish shadowy circles.',
    description: 'A targeted contour balm engineered specifically for the fragile periorbital zone. Instantly cools and tightens slackened contours while dramatically lightening vascular shadows.',
    volume: '15 ml / 0.5 fl. oz.',
    texture: 'Cooling silk emulsion with immediate tightening sensation',
    skinTypes: ['Dark Circles', 'Puffiness', 'Crow’s Feet', 'Tired Eyes'],
    keyActives: ['Palmitoyl Tripeptide-38', 'Supercritical Caffeine', 'Chrysin Complex', 'Marine Algae Bio-Vessel'],
    benefits: [
      'Visibly reduces under-eye bags within 15 minutes',
      'Brightens chronic dark circles and discoloration',
      'Smoothens fine expression lines and crow’s feet',
      'Cooling ceramic applicator tip delivers lymphatic drainage'
    ],
    usageRitual: 'Gently glide the cooling tip from inner corner outward along the orbital bone. Lightly tap with ring finger until fully absorbed.',
    clinicalResults: [
      { metric: '93%', description: 'observed reduction in under-eye puffiness in 15 minutes' },
      { metric: '89%', description: 'saw noticeable lightening of dark circles over 28 days' }
    ],
    image: '/images/skin5.jfif',
    hoverImage: '/images/skin6.jfif',
    isBestSeller: true,
    stock: 22
  },
  {
    id: 'elanor-clarifying-masque',
    name: 'Masque Purifiant d’Argile Verte',
    frenchSubtitle: 'Traitement Détoxifiant & Affinant aux Enzymes de Papaye',
    category: 'Masks',
    concern: 'Clarifying',
    price: 120,
    rating: 4.87,
    reviewsCount: 65,
    tagline: 'French green illite clay enriched with bioactive enzymes and soothing zinc for refined pores.',
    description: 'A non-drying detoxifying ritual that decongests pores, balances sebum secretion, and clarifies skin texture while delivering vital mineral nourishment.',
    volume: '75 ml / 2.5 fl. oz.',
    texture: 'Creamy mousse clay that stays flexible without cracking',
    skinTypes: ['Oily', 'Blemish-Prone', 'Congested', 'Combination'],
    keyActives: ['French Illite Clay', 'Papaya Enzyme Ferment', 'Zinc PCA', 'Tea Tree Bio-Hydrosol'],
    benefits: [
      'Clears clogged pores and minimizes pore diameter',
      'Regulates excess oiliness for up to 48 hours',
      'Gently dissolves keratinized dead surface cells',
      'Preserves moisture without the tight, cracked feeling'
    ],
    usageRitual: 'Apply an even layer over cleansed face with a mask brush, avoiding eye area. Leave on for 10-12 minutes. Rinse with warm water before mask dries completely.',
    clinicalResults: [
      { metric: '95%', description: 'confirmed visible purification of pores after single use' },
      { metric: '90%', description: 'reported prolonged matte finish without dryness' }
    ],
    image: '/images/skin6.jfif',
    hoverImage: '/images/skin.jfif',
    isNew: true,
    stock: 19
  },
  {
    id: 'elanor-rose-nectar-mist',
    name: 'Brume Botanique Rose Souveraine',
    frenchSubtitle: 'Élixir d’Hydratation Immédiate & Bouclier Antioxydant',
    category: 'Elixirs',
    concern: 'Calming',
    price: 95,
    rating: 4.91,
    reviewsCount: 54,
    tagline: '100% steam-distilled organic Grasse Damask rosewater with adaptogenic ashwagandha.',
    description: 'A micro-fine botanical cloud that delivers immediate tranquility to stressed skin, setting makeup and bathing the complexion in delicate petal antioxidants.',
    volume: '100 ml / 3.4 fl. oz.',
    texture: 'Micro-misted aromatic dew',
    skinTypes: ['All Skin Types', 'Sensitive', 'Jet-lagged', 'Dry'],
    keyActives: ['Grasse Damask Rose Distillate', 'Ashwagandha Adaptogen', 'Centella Asiatica', 'Glycerin'],
    benefits: [
      'Instant calming and hydration reset on the go',
      'Protects against blue light and environmental oxidative stress',
      'Sets and refreshes makeup with a natural dew',
      'Aromatherapeutic calm for mind and spirit'
    ],
    usageRitual: 'Close eyes and mist generously over face and neck whenever skin calls for a burst of hydration or peace.',
    clinicalResults: [
      { metric: '97%', description: 'felt an instantaneous sense of skin comfort and relaxation' },
      { metric: '93%', description: 'reported makeup stayed luminous and fresh all day' }
    ],
    image: '/images/skin.jfif',
    hoverImage: '/images/skin2.jfif',
    stock: 35
  }
];

export const CONCERNS = [
  {
    slug: 'radiance',
    name: 'Radiance & Luminescence',
    frenchTitle: 'Éclat & Lumière',
    description: 'Target dullness, hyperpigmentation, and uneven tone with high-potency saffron cells and bio-niacinamide.',
    color: '#E4C894',
    bgBadge: 'bg-[#F9F3E8]',
    productIds: ['elanor-celestial-nectar', 'elanor-rose-nectar-mist'],
    image: '/images/skin.jfif'
  },
  {
    slug: 'anti-aging',
    name: 'Cellular Longevity & Firming',
    frenchTitle: 'Jeunesse & Régénération',
    description: 'Promote dermal density, smooth fine expression lines, and restore resilient bounce.',
    color: '#C8A46A',
    bgBadge: 'bg-[#F7EFE3]',
    productIds: ['elanor-nocturne-elixir', 'elanor-regard-sculptant'],
    image: '/images/skin2.jfif'
  },
  {
    slug: 'barrier-repair',
    name: 'Barrier Restoration & Lipids',
    frenchTitle: 'Protection & Nutrition',
    description: 'Bio-identical ceramides and phytosterols to fortify fragile moisture barriers against stressors.',
    color: '#7D9075',
    bgBadge: 'bg-[#EEF2E8]',
    productIds: ['elanor-barrier-velvet', 'elanor-botanical-cleanser'],
    image: '/images/skin1.jfif'
  },
  {
    slug: 'hydration',
    name: 'Deep Quenching & Dew',
    frenchTitle: 'Hydratation Profonde',
    description: 'Multi-molecular hyaluronic matrices and alpine glacier water for long-lasting plumpness.',
    color: '#6F8FAF',
    bgBadge: 'bg-[#EBF2F8]',
    productIds: ['elanor-hydra-infusion', 'elanor-rose-nectar-mist'],
    image: '/images/skin3.jfif'
  },
  {
    slug: 'calming',
    name: 'Calming & Anti-Redness',
    frenchTitle: 'Apaisement Céleste',
    description: 'Gentle chamomile, bisabolol, and adaptogens to soothe reactivity and inflammatory triggers.',
    color: '#55624E',
    bgBadge: 'bg-[#EDF2EB]',
    productIds: ['elanor-botanical-cleanser', 'elanor-rose-nectar-mist', 'elanor-barrier-velvet'],
    image: '/images/skin4.jfif'
  },
  {
    slug: 'clarifying',
    name: 'Pore Clarifying & Balance',
    frenchTitle: 'Pureté & Équilibre',
    description: 'French illite clay and active papaya enzymes to refine pores and harmonize sebum without dryness.',
    color: '#8E857A',
    bgBadge: 'bg-[#F4F1ED]',
    productIds: ['elanor-clarifying-masque', 'elanor-botanical-cleanser'],
    image: '/images/skin6.jfif'
  }
];

export const INGREDIENTS = [
  {
    id: 'saffron-stem-cells',
    name: 'Saffron Flower Stem Cells',
    botanicalName: 'Crocus Sativus Meristem Cell Culture',
    origin: 'Provence, France (Wild Harvested)',
    category: 'Cellular Botanical',
    benefits: 'Boosts cellular respiration, neutralizes free radicals, and delivers deep luminosity.',
    compatibility: 'Synergistic with Niacinamide, Vitamin C, and Peptides.',
    safetyRating: '100% Non-sensitizing',
    foundIn: ['Sérum Éclat Botanique'],
    description: 'Harvested at dawn when phytochemical potency reaches its zenith, our saffron meristem cells are cold-extracted to preserve bio-active crocin and safranal.'
  },
  {
    id: 'biomimetic-ceramides',
    name: '5-Ceramide Biomimetic Complex',
    botanicalName: 'Ceramides EOP, NP, AP, AS, NS + Phytosphingosine',
    origin: 'Biotech Bio-Fermentation (Switzerland)',
    category: 'Lipid Architecture',
    benefits: 'Replicates natural stratum corneum lipids to seal micro-fissures and eliminate trans-epidermal moisture loss.',
    compatibility: 'Safe with all actives including Retinoids and Exfoliating Acids.',
    safetyRating: 'Dermatologist Verified Hypoallergenic',
    foundIn: ['Crème Renaissance Barrière'],
    description: 'A scientifically balanced 3:1:1 lipid ratio that signals dermal cells to accelerate lipid synthesis and shield against dry climatic shifts.'
  },
  {
    id: 'liposomal-retinal',
    name: 'Encapsulated Retinaldehyde (0.1%)',
    botanicalName: 'Liposomal Retinal',
    origin: 'Alpine Biotech Institute',
    category: 'Clinical Active',
    benefits: 'Converts to retinoic acid in one single enzymatic step, working up to 11x faster than traditional retinol with minimal irritation.',
    compatibility: 'Best used at night; pair with Ceramides and Squalane. Alternate with direct AHA/BHA acids.',
    safetyRating: 'High-Tolerance Time-Release',
    foundIn: ['Élixir Nocturne Rénovateur'],
    description: 'Encapsulated within phospholipid spheres that slowly rupture through nocturnal hours, ensuring continuous cellular rejuvenation without flaking.'
  },
  {
    id: 'hyaluronic-matrices',
    name: '8-Tier Hyaluronic Complex',
    botanicalName: 'Sodium Hyaluronate Multi-Molecular Matrix',
    origin: 'Bio-Fermented Wheat Grain (France)',
    category: 'Hydration Engine',
    benefits: 'High molecular weights protect surface moisture while ultra-low weights penetrate deep dermal reservoirs.',
    compatibility: 'Universal synergy with all skincare regimens.',
    safetyRating: 'EWG Green 1',
    foundIn: ['Goutte d’Océan Hydra-Infusion', 'Sérum Éclat Botanique'],
    description: 'Eight distinct molecular weights ranging from 5 kDa to 3000 kDa create an interconnected hydration scaffolding across every skin layer.'
  },
  {
    id: 'camellia-japonica',
    name: 'Camellia Japonica Seed Oil',
    botanicalName: 'Camellia Japonica Seed Oil (Tsubaki)',
    origin: 'Jeju Island (Cold Pressed Virgin)',
    category: 'Precious Botanical Oil',
    benefits: 'Rich in Oleic Acid (Omega-9), vitamins A, B, D, and E to protect and nourish the delicate acid mantle.',
    compatibility: 'Pairs smoothly with all oil and water-based treatments.',
    safetyRating: 'Non-comedogenic',
    foundIn: ['Huile Gelée Pureté Céleste', 'Élixir Nocturne Rénovateur'],
    description: 'Used for centuries by geishas to achieve translucent porcelain skin, this virgin cold-pressed oil provides opulent cushion and velvety silkiness.'
  },
  {
    id: 'palmitoyl-tripeptide',
    name: 'Palmitoyl Tripeptide-38 & Copper Peptides',
    botanicalName: 'Matrikine Peptide Scaffold',
    origin: 'Geneva Bio-Labs',
    category: 'Peptide Architecture',
    benefits: 'Rebuilds 6 major components of the skin matrix and dermal-epidermal junction for lifted contours.',
    compatibility: 'Synergistic with Hyaluronic Acid and Antioxidants.',
    safetyRating: 'Ultra Clean Clinical',
    foundIn: ['Soin Regard Infini Peptides'],
    description: 'Signals fibroblasts to synthesize collagen I, III, IV, fibronectin, hyaluronic acid, and laminin-5, smoothing stubborn expression lines.'
  }
];
