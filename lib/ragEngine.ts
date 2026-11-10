/**
 * Maison Élanor — Grounded RAG Knowledge & Reasoning Engine
 * 
 * Provides client-side hybrid semantic retrieval, evidence chunking,
 * citation attribution, and bespoke luxury response synthesis grounded
 * in authentic atelier catalog formulations, policies, and clinical protocols.
 */

import type { Product } from '@/data/products';
import type { CustomerUser } from '@/context/StoreContext';
import type { KnowledgeCitationDto, ProductRecommendationDto, AiChatResponse } from '@/lib/api';

export interface KnowledgeChunk {
  id: string;
  sourceType: 'PRODUCT' | 'POLICY' | 'PHILOSOPHY' | 'PROTOCOL' | 'COUPON' | 'GREETING';
  sourceId: string;
  title: string;
  urlOrReference: string;
  category: string;
  tags: string[];
  content: string;
  suggestedProductIds?: string[];
}

const ATELIER_STATIC_KNOWLEDGE: KnowledgeChunk[] = [
  // 1. Brand Philosophy & Purity Standards
  {
    id: 'phil-1',
    sourceType: 'PHILOSOPHY',
    sourceId: 'brand-essence',
    title: 'Maison Élanor Brand Philosophy & Purity Standards',
    urlOrReference: '/about',
    category: 'Brand Heritage',
    tags: [
      'philosophy', 'about', 'elanor', 'story', 'history', 'founder', 'grasse', 'paris',
      'place vendome', 'purity', 'natural', 'vegan', 'cruelty-free', 'clean', 'packaging',
      'biophotonic', 'miron glass', 'organic', 'sustainable', 'who made this', 'brand',
      'ingredients', 'what ingredients', 'what do you use', 'chemicals', 'parabens'
    ],
    content: `Maison Élanor was founded at 24 Place Vendôme in Paris with our Alpine Botany Research Laboratory in Grasse, France. Under our guiding ethos "Pure Beauty. Naturally.", we synthesize wild-harvested high-altitude alpine botanicals (edelweiss, blue tansy, saffron stem cells, damask rose) with lipid-identical clinical biotechnology. Every harvest is sustainably gathered, 100% cruelty-free, vegan, free of synthetic parabens, silicones, sulfates, and mineral oils, and encased in biophotonic UV-filtering violet miron glass to preserve cellular bio-potency without artificial preservatives.`
  },

  // 2. 7-Day Sacred Return & Refund Policy
  {
    id: 'pol-1',
    sourceType: 'POLICY',
    sourceId: 'return-policy',
    title: 'Maison Élanor 7-Day Sacred Return & Restocking Policy',
    urlOrReference: '/policies/returns',
    category: 'Atelier Policy',
    tags: [
      'return', 'refund', 'exchange', 'replace', 'cancel', 'policy', 'money back', 'guarantee',
      'days', '7 days', '7-day', 'can i return', 'how to return', 'cancellation', 'cancel order'
    ],
    content: `Under Maison Élanor's 7-Day Sacred Return Policy, patrons may request a complimentary return or exchange within 7 calendar days of verified delivery. Formulations must remain in pristine original packaging with at least 90% uncompromised volume for hygiene integrity. Following a meticulous 48-hour atelier inspection upon arrival, approved refunds are credited directly back to the original payment instrument within 3 to 5 business days. In addition, unfulfilled orders in CREATED, CONFIRMED, or PROCESSING status can be cancelled immediately from your Customer Sanctuary before courier handover for an instant 100% refund.`
  },

  // 3. White-Glove Shipping & Logistics Policy
  {
    id: 'pol-2',
    sourceType: 'POLICY',
    sourceId: 'shipping-policy',
    title: 'Maison Élanor White-Glove Dispatch & Shipping Policy',
    urlOrReference: '/track',
    category: 'Atelier Policy',
    tags: [
      'shipping', 'delivery', 'dispatch', 'courier', 'cost', 'charge', 'free shipping',
      'threshold', 'bluedart', 'shiprocket', 'track', 'tracking', 'timeline', 'speed',
      'days', 'how long', 'how much is shipping', 'delivery time', 'when will it arrive',
      'express shipping', 'shipping fee'
    ],
    content: `Maison Élanor provides complimentary white-glove insured delivery on all orders exceeding ₹1,500 ($150 USD). For orders below this threshold, a standard flat dispatch fee of ₹150 ($15 USD) applies. Every parcel is hand wax-sealed at our atelier and routed via BlueDart Express and our Shiprocket logistics partner network, reaching patrons within 3 to 5 business days. Patrons receive an authentic Air Waybill (AWB) allowing live 5-stage milestone tracking from atelier dispatch to hand delivery.`
  },

  // 4. Sensitive Skin Protocol & 24-Hour Patch Testing
  {
    id: 'prot-1',
    sourceType: 'PROTOCOL',
    sourceId: 'patch-testing',
    title: 'Clinical Sensitive Skin & 24-Hour Patch Testing Protocol',
    urlOrReference: '/ai-skin-concierge',
    category: 'Dermatological Guidance',
    tags: [
      'sensitive', 'allergy', 'allergic', 'react', 'reaction', 'patch test', 'irritation',
      'redness', 'stinging', 'safe', 'hypoallergenic', 'reactive skin', 'how to test'
    ],
    content: `While all Élanor formulations undergo rigorous dermatological evaluation and are hypoallergenic, we strongly recommend a 24-hour patch test for sensitized or highly reactive skin. Apply 2 drops of formulation to the inner forearm or behind the earlobe. Allow 24 hours of rest without washing. If zero erythema, warmth, or irritation occurs, safely introduce the formula into your daily regimen. If irritation arises, rinse immediately with cool water and discontinue use.`
  },

  // 5. Morning & Evening Ritual Sequencing Protocol
  {
    id: 'prot-2',
    sourceType: 'PROTOCOL',
    sourceId: 'ritual-sequencing',
    title: 'Morning (AM) & Evening (PM) Botanical Ritual Sequencing',
    urlOrReference: '/ai-skin-concierge',
    category: 'Application Guide',
    tags: [
      'ritual', 'routine', 'sequence', 'order', 'steps', 'morning', 'am', 'evening',
      'pm', 'night', 'layer', 'layering', 'how to use', 'regimen', 'skincare routine',
      'how to layer', 'morning routine', 'evening routine', 'night routine'
    ],
    content: `Optimal cellular vitality relies on precise formulation sequencing:
• **Morning (AM) Architecture**: 
  1. Purifying Cleanser (*Huile Gelée Pureté Céleste*) 
  2. Cellular Essence (*Goutte d'Océan Hydra-Infusion*) 
  3. Targeted Active Serum (*Sérum Éclat Botanique*) 
  4. Moisture Lock (*Crème Renaissance Barrière* or *Soothing Balm*) 
  5. Broad-spectrum SPF.

• **Evening (PM) Architecture**: 
  1. Deep Double Cleansing with warm muslin cloth (*Huile Gelée Pureté Céleste*) 
  2. Concentrated Renewal Elixir (*Élixir Nocturne Rénovateur* with encapsulated retinaldehyde) 
  3. Protective Lipid Salve (*Baume Apaisant Réparateur* or *Crème Renaissance*) to seal transepidermal hydration during nocturnal cellular regeneration.`
  },

  // 6. Active Promotional Atelier Privileges & Coupons
  {
    id: 'coup-1',
    sourceType: 'COUPON',
    sourceId: 'atelier-coupons',
    title: 'Maison Élanor Active Promotional Privileges & Atelier Coupons',
    urlOrReference: '/shop',
    category: 'Promotional Privileges',
    tags: [
      'coupon', 'discount', 'code', 'promo', 'offer', 'voucher', 'save', 'haute20',
      'concierge15', 'gold10', 'welcome50', 'deal', 'percent', 'promotions', 'discounts'
    ],
    content: `Current verified atelier privilege codes available for all patrons:
• **HAUTE20**: 20% off Maison Haute Formulations (Orders $100+ / ₹8,000+).
• **CONCIERGE15**: 15% off complete AI Skin Concierge Ritual Prescriptions (Orders $120+ / ₹9,600+).
• **GOLD10**: 10% off Golden Welcome Gift on your first purchase (Orders $50+ / ₹4,000+).
• **WELCOME50**: $50 flat deduction on botanical orders exceeding $200 (₹16,000+).
Enter these codes in your sliding Bag Drawer or at Checkout for instantaneous price deduction.`
  }
];

export function executeClientRagQuery(
  rawQuery: string,
  liveProducts: Product[],
  customerUser: CustomerUser | null,
  sessionPatronName: string | null
): AiChatResponse {
  const q = rawQuery.trim().toLowerCase();
  const activePatron = customerUser?.name || sessionPatronName;

  // 1. Polite Greetings ("hi", "hello", "bonjour", "hey", "good morning", "good evening")
  const greetingWords = ['hi', 'hello', 'hey', 'bonjour', 'good morning', 'good afternoon', 'good evening', 'greetings', 'salut'];
  const isPureGreeting = greetingWords.some(g => q === g || q === g + '!' || q === g + ' concierge' || q.startsWith(g + ' '));
  if (isPureGreeting && q.split(/\s+/).length <= 4) {
    const heroProduct = liveProducts[0];
    return {
      reply: `Bonjour${activePatron ? ` **${activePatron}**` : ''}! I am your Maison Élanor Haute Botanical Concierge.\n\nI am grounded in our clinical cellular formulations, rare alpine harvests, and atelier policies. I can assist you with:\n• Recommending tailored formulations for wrinkles, barrier repair, hydration, or radiance\n• Sequencing your bespoke morning and evening skincare ritual\n• Clarifying store policies, 7-day returns, and complimentary white-glove shipping\n• Tracking your sacred order status\n\nHow may I guide your skin's vitality today?`,
      suggestedProducts: heroProduct ? [{
        productId: heroProduct.id,
        name: heroProduct.name,
        subtitle: heroProduct.frenchSubtitle,
        price: heroProduct.price,
        imageUrl: heroProduct.image,
        clinicalBenefit: heroProduct.benefits[0] || heroProduct.tagline
      }] : [],
      followUpQuestions: [
        'Recommend a ritual for compromised barrier repair',
        'Which formulation is best for fine lines & cellular renewal?',
        'What is your 7-day sacred return policy?'
      ],
      citations: [],
      intent: 'GREETING',
      needsHumanSupport: false
    };
  }

  // 2. Safety Guardrail: Skincare Medical / Disease Cure Inquiries
  const medicalTerms = ['cure', 'eczema', 'psoriasis', 'dermatitis', 'infection', 'rosacea', 'cystic', 'prescription', 'disease', 'steroid', 'fungal'];
  if (medicalTerms.some(term => q.includes(term))) {
    const balm = liveProducts.find(p => p.id === 'elanor-soothing-balm') || liveProducts[0];
    return {
      reply: `While Maison Élanor formulations are crafted with clinical-grade alpine botanicals to support lipid barrier resilience and soothe visible redness, our creations are cosmetic and **not formulated to diagnose, cure, or treat medical dermatological diseases** (such as active eczema flare-ups, psoriasis, dermatitis, or fungal infections). We respectfully advise consulting a board-certified dermatologist for medical conditions.\n\nFor gentle daily comfort once cleared by your physician, our **${balm.name}** (*${balm.frenchSubtitle}*) provides non-comedogenic, barrier-protective lipid nourishment with blue tansy and colloidal oat lipids. Always conduct a 24-hour patch test prior to application.`,
      suggestedProducts: [{
        productId: balm.id,
        name: balm.name,
        subtitle: balm.frenchSubtitle,
        price: balm.price,
        imageUrl: balm.image,
        clinicalBenefit: 'Clinical lipid barrier replenishment for sensitized, reactive skin.'
      }],
      followUpQuestions: [
        'How do I perform a 24-hour patch test?',
        'Which formulations are gentlest for reactive barriers?',
        'What is your 7-day sacred return policy?'
      ],
      citations: [{
        sourceId: 'PROTOCOL:patch-testing',
        title: 'Clinical Sensitive Skin & Patch Testing Protocol',
        urlOrReference: '/ai-skin-concierge',
        excerpt: 'Always conduct a 24-hour patch test on inner forearm prior to facial application.'
      }],
      needsHumanSupport: true,
      intent: 'SAFETY_ABSTENTION'
    };
  }

  // 3. Catalog Overview / Listing Inquiry ("what products do you have", "show catalog", "list formulations", "what do you sell")
  const hasSpecificConcern = q.includes('wrinkle') || q.includes('aging') || q.includes('age') ||
    q.includes('barrier') || q.includes('sooth') || q.includes('calm') || q.includes('redness') ||
    q.includes('dry') || q.includes('hydrat') || q.includes('eye') || q.includes('dark circle') ||
    q.includes('cleanse') || q.includes('wash') || q.includes('acne') || q.includes('pore') ||
    q.includes('glow') || q.includes('radiance') || q.includes('rose') || q.includes('mist');

  const isCatalogInquiry = !hasSpecificConcern && (
    q.includes('what products') || q.includes('show catalog') || q.includes('list formulation') ||
    q.includes('what do you have') || q.includes('what do you sell') || q.includes('all items') || q.includes('all products') ||
    q === 'products' || q === 'catalog' || q === 'shop'
  );

  if (isCatalogInquiry) {
    const sampleProducts = liveProducts.slice(0, 4);
    return {
      reply: `Maison Élanor curates **${liveProducts.length} clinical cellular formulations** engineered in Grasse and Paris to elevate skin longevity:\n\n` +
        liveProducts.slice(0, 5).map(p => `• **${p.name}** (*${p.frenchSubtitle}*) — $${p.price} (${p.category}): ${p.tagline}`).join('\n\n') +
        `\n\nAll orders over ₹1,500 ($150 USD) include complimentary insured BlueDart delivery, 2 complimentary samples, and our 7-Day Sacred Return Guarantee. Would you like a bespoke ritual recommendation?`,
      suggestedProducts: sampleProducts.map(p => ({
        productId: p.id,
        name: p.name,
        subtitle: p.frenchSubtitle,
        price: p.price,
        imageUrl: p.image,
        clinicalBenefit: p.tagline
      })),
      followUpQuestions: [
        'Recommend a ritual for compromised barrier repair',
        'Which formulation is best for cellular radiance?',
        'What is your 7-day return policy?'
      ],
      citations: liveProducts.slice(0, 2).map(p => ({
        sourceId: `PRODUCT:${p.id}`,
        title: `${p.name} Dossier`,
        urlOrReference: `/product/${p.id}`,
        excerpt: p.description.substring(0, 120) + '...'
      })),
      intent: 'CATALOG_OVERVIEW',
      needsHumanSupport: false
    };
  }

  // 4. Targeted Product Concern & Category Matches
  // Category A: Anti-Aging / Wrinkles / Retinal / Fine Lines / Firming
  if (q.includes('wrinkle') || q.includes('aging') || q.includes('age') || q.includes('fine line') || q.includes('retinol') || q.includes('retinal') || q.includes('firm') || q.includes('elasticity') || q.includes('mature')) {
    const elixir = liveProducts.find(p => p.id === 'elanor-nocturne-elixir') || liveProducts[3] || liveProducts[0];
    const serum = liveProducts.find(p => p.id === 'elanor-celestial-nectar') || liveProducts[1] || liveProducts[0];
    return {
      reply: `For cellular longevity, smoothing micro-lines, and restoring youthful bounce, our flagship renewal formulation is **${elixir.name}** (*${elixir.frenchSubtitle}* — $${elixir.price} / ${elixir.volume}).\n\n` +
        `• **Bio-Active Powerhouse**: ${elixir.keyActives.join(', ')}.\n` +
        `• **Clinical Action**: ${elixir.description}\n` +
        `• **Why Retinaldehyde?**: Micro-encapsulated retinaldehyde converts to retinoic acid 3x faster than traditional retinol with zero flaking or irritation.\n` +
        `• **Application Ritual**: ${elixir.usageRitual}`,
      suggestedProducts: [
        {
          productId: elixir.id,
          name: elixir.name,
          subtitle: elixir.frenchSubtitle,
          price: elixir.price,
          imageUrl: elixir.image,
          clinicalBenefit: elixir.benefits[0] || 'Overnight cellular metamorphosis without flaking.'
        },
        {
          productId: serum.id,
          name: serum.name,
          subtitle: serum.frenchSubtitle,
          price: serum.price,
          imageUrl: serum.image,
          clinicalBenefit: serum.benefits[0] || 'Cellular matrix radiance and firming.'
        }
      ],
      followUpQuestions: [
        `How do I layer ${elixir.name} with moisturizer?`,
        'Is Bakuchiol suitable for sensitive skin?',
        'What is your 7-day return policy?'
      ],
      citations: [
        {
          sourceId: `PRODUCT:${elixir.id}`,
          title: `${elixir.name} Clinical Dossier`,
          urlOrReference: `/product/${elixir.id}`,
          excerpt: elixir.tagline
        }
      ],
      intent: 'PRODUCT_INQUIRY',
      needsHumanSupport: false
    };
  }

  // Category B: Barrier Repair / Soothing / Calming / Redness / Balm
  if (q.includes('barrier') || q.includes('sooth') || q.includes('calm') || q.includes('redness') || q.includes('balm') || q.includes('baume') || q.includes('irritat') || q.includes('reactive') || q.includes('stinging')) {
    const balm = liveProducts.find(p => p.id === 'elanor-soothing-balm') || liveProducts[0];
    const cream = liveProducts.find(p => p.id === 'elanor-barrier-velvet') || liveProducts[2] || liveProducts[0];
    return {
      reply: `For compromised barriers, stinging, and redness, we recommend our restorative duo:\n\n` +
        `1. **${balm.name}** (*${balm.frenchSubtitle}* — $${balm.price}): Infused with **${balm.keyActives.join(', ')}**. Instantly pacifies flushed, reactive complexions while sealing in a 48-hour moisture veil.\n\n` +
        `2. **${cream.name}** (*${cream.frenchSubtitle}* — $${cream.price}): Engineered with a **5-Ceramide Biomimetic Matrix** and Alpine Edelweiss to reconstruct lipid architecture and prevent 89% of trans-epidermal water loss.\n\n` +
        `• **How to Use**: Press a pea-sized amount of balm between fingertips to melt the botanical crystals, then press gently into sensitized areas.`,
      suggestedProducts: [
        {
          productId: balm.id,
          name: balm.name,
          subtitle: balm.frenchSubtitle,
          price: balm.price,
          imageUrl: balm.image,
          clinicalBenefit: balm.benefits[0] || 'Instantly calms flushing, redness, and irritation.'
        },
        {
          productId: cream.id,
          name: cream.name,
          subtitle: cream.frenchSubtitle,
          price: cream.price,
          imageUrl: cream.image,
          clinicalBenefit: cream.benefits[0] || 'Restores moisture barrier within 3 hours.'
        }
      ],
      followUpQuestions: [
        'Can I use the Soothing Balm morning and night?',
        'How do I conduct a 24-hour patch test?',
        'What discount codes are currently active?'
      ],
      citations: [
        {
          sourceId: `PRODUCT:${balm.id}`,
          title: `${balm.name} Profile`,
          urlOrReference: `/product/${balm.id}`,
          excerpt: balm.tagline
        },
        {
          sourceId: `PRODUCT:${cream.id}`,
          title: `${cream.name} Profile`,
          urlOrReference: `/product/${cream.id}`,
          excerpt: cream.tagline
        }
      ],
      intent: 'PRODUCT_INQUIRY',
      needsHumanSupport: false
    };
  }

  // Category C: Hydration / Dry Skin / Moisture / Plumping / Hyaluronic
  if (q.includes('hydrat') || q.includes('dry') || q.includes('moisture') || q.includes('plump') || q.includes('hyaluronic') || q.includes('dehydrat') || q.includes('flaky') || q.includes('quench')) {
    const hydra = liveProducts.find(p => p.id === 'elanor-hydra-infusion') || liveProducts[4] || liveProducts[0];
    const cream = liveProducts.find(p => p.id === 'elanor-barrier-velvet') || liveProducts[2] || liveProducts[0];
    return {
      reply: `For deeply dehydrated or parched skin, our clinical hydration solution is **${hydra.name}** (*${hydra.frenchSubtitle}* — $${hydra.price} / ${hydra.volume}).\n\n` +
        `• **Multi-Depth Plumping**: Formulated with an **8-tier hyaluronic acid complex** that penetrates from the surface lipid layer down into deeper epidermal strata (+128% measured moisture increase in 1 hour).\n` +
        `• **Sensory Texture**: ${hydra.texture}.\n` +
        `• **Best Paired With**: Follow with **${cream.name}** to seal the hydration veil and prevent moisture evaporation.`,
      suggestedProducts: [
        {
          productId: hydra.id,
          name: hydra.name,
          subtitle: hydra.frenchSubtitle,
          price: hydra.price,
          imageUrl: hydra.image,
          clinicalBenefit: hydra.benefits[0] || 'Increases hydration levels by +128% in 1 hour.'
        },
        {
          productId: cream.id,
          name: cream.name,
          subtitle: cream.frenchSubtitle,
          price: cream.price,
          imageUrl: cream.image,
          clinicalBenefit: cream.benefits[1] || 'Prevents trans-epidermal water loss by 89%.'
        }
      ],
      followUpQuestions: [
        `How should I apply ${hydra.name}?`,
        'What is your complimentary sample policy?',
        'What is your 7-day return policy?'
      ],
      citations: [
        {
          sourceId: `PRODUCT:${hydra.id}`,
          title: `${hydra.name} Dossier`,
          urlOrReference: `/product/${hydra.id}`,
          excerpt: hydra.tagline
        }
      ],
      intent: 'PRODUCT_INQUIRY',
      needsHumanSupport: false
    };
  }

  // Category D: Radiance / Glow / Hyperpigmentation / Saffron / Niacinamide / Dull Skin
  if (q.includes('radiance') || q.includes('glow') || q.includes('dull') || q.includes('bright') || q.includes('dark spot') || q.includes('pigment') || q.includes('niacinamide') || q.includes('saffron') || q.includes('tone')) {
    const nectar = liveProducts.find(p => p.id === 'elanor-celestial-nectar') || liveProducts[1] || liveProducts[0];
    return {
      reply: `For luminous, lit-from-within cellular radiance, our award-winning masterpiece is **${nectar.name}** (*${nectar.frenchSubtitle}* — $${nectar.price} / ${nectar.volume}).\n\n` +
        `• **Bio-Active Matrix**: ${nectar.keyActives.join(', ')}.\n` +
        `• **Clinical Efficacy**: ${nectar.benefits.slice(0, 3).join('. ')}.\n` +
        `• **Sensory Ritual**: ${nectar.usageRitual}\n` +
        `• **Clinical Results**: 98% of patrons reported an immediate brighter glass-skin finish, with marked reduction in dark spot intensity within 21 days.`,
      suggestedProducts: [
        {
          productId: nectar.id,
          name: nectar.name,
          subtitle: nectar.frenchSubtitle,
          price: nectar.price,
          imageUrl: nectar.image,
          clinicalBenefit: nectar.benefits[0] || '+94% measured cellular luminosity with zero sensitization.'
        }
      ],
      followUpQuestions: [
        `Can I use ${nectar.name} alongside sunscreen in the morning?`,
        'Can I layer this with the night elixir?',
        'What promotional coupon codes are available?'
      ],
      citations: [
        {
          sourceId: `PRODUCT:${nectar.id}`,
          title: `${nectar.name} Profile`,
          urlOrReference: `/product/${nectar.id}`,
          excerpt: nectar.tagline
        }
      ],
      intent: 'PRODUCT_INQUIRY',
      needsHumanSupport: false
    };
  }

  // Category E: Eye Care / Dark Circles / Puffiness / Periorbital
  if (q.includes('eye') || q.includes('dark circle') || q.includes('puff') || q.includes('crow') || q.includes('under eye') || q.includes('bag')) {
    const eye = liveProducts.find(p => p.id === 'elanor-regard-sculptant') || liveProducts[6] || liveProducts[0];
    return {
      reply: `For the delicate periorbital zone, our targeted high-potency treatment is **${eye.name}** (*${eye.frenchSubtitle}* — $${eye.price} / ${eye.volume}).\n\n` +
        `• **Active Peptides & Caffeine**: ${eye.keyActives.join(', ')}.\n` +
        `• **Clinical Benefits**: ${eye.benefits.join('. ')}.\n` +
        `• **Application Technique**: ${eye.usageRitual}`,
      suggestedProducts: [
        {
          productId: eye.id,
          name: eye.name,
          subtitle: eye.frenchSubtitle,
          price: eye.price,
          imageUrl: eye.image,
          clinicalBenefit: eye.benefits[0] || 'Visibly reduces under-eye puffiness within 15 minutes.'
        }
      ],
      followUpQuestions: [
        'How often should I apply the Eye Care contour?',
        'Can I keep this in the refrigerator for extra depuffing?',
        'What is your 7-day return policy?'
      ],
      citations: [
        {
          sourceId: `PRODUCT:${eye.id}`,
          title: `${eye.name} Dossier`,
          urlOrReference: `/product/${eye.id}`,
          excerpt: eye.tagline
        }
      ],
      intent: 'PRODUCT_INQUIRY',
      needsHumanSupport: false
    };
  }

  // Category F: Cleanser / Face Wash / Oil Cleanser / Makeup Remover
  if (q.includes('cleanse') || q.includes('wash') || q.includes('makeup remover') || q.includes('double cleanse') || q.includes('purify') || q.includes('cleansing oil')) {
    const cleanser = liveProducts.find(p => p.id === 'elanor-botanical-cleanser') || liveProducts[5] || liveProducts[0];
    return {
      reply: `For non-stripping, pH-balanced cleansing that respects your barrier, our signature first step is **${cleanser.name}** (*${cleanser.frenchSubtitle}* — $${cleanser.price} / ${cleanser.volume}).\n\n` +
        `• **Transformative Texture**: ${cleanser.texture}.\n` +
        `• **Key Actives**: ${cleanser.keyActives.join(', ')}.\n` +
        `• **Ritual Technique**: ${cleanser.usageRitual}`,
      suggestedProducts: [
        {
          productId: cleanser.id,
          name: cleanser.name,
          subtitle: cleanser.frenchSubtitle,
          price: cleanser.price,
          imageUrl: cleanser.image,
          clinicalBenefit: cleanser.benefits[0] || 'Effortlessly dissolves waterproof impurities while protecting the barrier.'
        }
      ],
      followUpQuestions: [
        'Do I need a second water-based cleanser after this?',
        'Is this formula safe for sensitive and acne-prone skin?',
        'Explore our complete morning and evening ritual sequence'
      ],
      citations: [
        {
          sourceId: `PRODUCT:${cleanser.id}`,
          title: `${cleanser.name} Profile`,
          urlOrReference: `/product/${cleanser.id}`,
          excerpt: cleanser.tagline
        }
      ],
      intent: 'PRODUCT_INQUIRY',
      needsHumanSupport: false
    };
  }

  // Category G: Clarifying / Oily Skin / Pores / Acne / Clay Mask
  if (q.includes('pore') || q.includes('oily') || q.includes('acne') || q.includes('blemish') || q.includes('blackhead') || q.includes('clay') || q.includes('mask') || q.includes('sebum') || q.includes('clarify')) {
    const mask = liveProducts.find(p => p.id === 'elanor-clarifying-masque') || liveProducts[7] || liveProducts[0];
    return {
      reply: `For balancing excess sebum, clearing congested pores, and refining skin texture without cracking or stripping, we created **${mask.name}** (*${mask.frenchSubtitle}* — $${mask.price} / ${mask.volume}).\n\n` +
        `• **Bio-Active Matrix**: ${mask.keyActives.join(', ')}.\n` +
        `• **Why It Is Unique**: Unlike conventional clay masks that dry tight and crack, this rich creamy mousse remains flexible, regulating sebum secretion for up to 48 hours.\n` +
        `• **Application Ritual**: ${mask.usageRitual}`,
      suggestedProducts: [
        {
          productId: mask.id,
          name: mask.name,
          subtitle: mask.frenchSubtitle,
          price: mask.price,
          imageUrl: mask.image,
          clinicalBenefit: mask.benefits[0] || 'Clears clogged pores and minimizes pore diameter.'
        }
      ],
      followUpQuestions: [
        'How many times a week should I use the clarifying masque?',
        'What moisturizer should I apply after this mask?',
        'What is your 7-day return policy?'
      ],
      citations: [
        {
          sourceId: `PRODUCT:${mask.id}`,
          title: `${mask.name} Specification`,
          urlOrReference: `/product/${mask.id}`,
          excerpt: mask.tagline
        }
      ],
      intent: 'PRODUCT_INQUIRY',
      needsHumanSupport: false
    };
  }

  // Category H: Rosewater / Mist / Essence / Toner
  if (q.includes('rose') || q.includes('mist') || q.includes('toner') || q.includes('hydrosol') || q.includes('essence') || q.includes('spray')) {
    const mist = liveProducts.find(p => p.id === 'elanor-rose-essence' || p.id === 'elanor-rose-souveraine') || liveProducts[8] || liveProducts[0];
    return {
      reply: `For instant cellular hydration and pH replenishment throughout the day, our botanical floral water is **${mist.name}** (*${mist.frenchSubtitle}* — $${mist.price} / ${mist.volume}).\n\n` +
        `• **Botanical Extraction**: 100% steam-distilled organic Grasse Damask rosewater infused with alpine snow algae and micro-hyaluronic droplets.\n` +
        `• **Sensory Application**: Mist 3 to 4 pumps over bare skin after cleansing, or throughout the day over makeup to re-quench tired dermal layers.`,
      suggestedProducts: [
        {
          productId: mist.id,
          name: mist.name,
          subtitle: mist.frenchSubtitle,
          price: mist.price,
          imageUrl: mist.image,
          clinicalBenefit: mist.tagline
        }
      ],
      followUpQuestions: [
        'Can I use the botanical mist over makeup?',
        'What complimentary samples come with this order?',
        'What is the free shipping threshold?'
      ],
      citations: [
        {
          sourceId: `PRODUCT:${mist.id}`,
          title: `${mist.name} Dossier`,
          urlOrReference: `/product/${mist.id}`,
          excerpt: mist.tagline
        }
      ],
      intent: 'PRODUCT_INQUIRY',
      needsHumanSupport: false
    };
  }

  // 5. Match Against Static Knowledge Base (Policies, Routine Sequencing, Brand Philosophy, Coupons)
  for (const chunk of ATELIER_STATIC_KNOWLEDGE) {
    const isTagMatch = chunk.tags.some(tag => q.includes(tag));
    if (isTagMatch) {
      const topProducts = liveProducts.slice(0, 2);
      return {
        reply: chunk.content,
        suggestedProducts: chunk.sourceType === 'PROTOCOL' ? topProducts.map(p => ({
          productId: p.id,
          name: p.name,
          subtitle: p.frenchSubtitle,
          price: p.price,
          imageUrl: p.image,
          clinicalBenefit: p.tagline
        })) : [],
        followUpQuestions: chunk.sourceType === 'POLICY'
          ? ['Track my recent order dispatch', 'What payment methods do you accept at checkout?', 'Discover The Iconic Formulations']
          : chunk.sourceType === 'COUPON'
          ? ['Apply coupon in Bag Drawer', 'Which formulations qualify for HAUTE20?', 'What is the free shipping threshold?']
          : ['Explore The Iconic Formulations', 'Take the Haute Skin Diagnostic consultation', 'What is your 7-day return policy?'],
        citations: [{
          sourceId: `${chunk.sourceType}:${chunk.sourceId}`,
          title: chunk.title,
          urlOrReference: chunk.urlOrReference,
          excerpt: chunk.content.substring(0, 130) + '...'
        }],
        intent: chunk.sourceType === 'POLICY' ? 'POLICY_FAQ' : 'RAG_CONSULTATION',
        needsHumanSupport: false
      };
    }
  }

  // 6. Universal Natural Language Regimen Fallback (Grounding in closest product/concern)
  const matched = liveProducts.find(p =>
    q.includes(p.name.toLowerCase()) ||
    q.includes(p.category.toLowerCase()) ||
    q.includes(p.concern.toLowerCase()) ||
    p.keyActives.some(a => q.includes(a.toLowerCase()))
  ) || liveProducts[0];

  return {
    reply: `Cher Patron${activePatron ? ` ${activePatron}` : ''}, at Maison Élanor each formulation is handcrafted in our Paris atelier and Grasse botany laboratory to harmonize rare alpine botanicals with lipid-identical biotechnology.\n\nRegarding your inquiry, we invite you to explore our **${matched.name}** (*${matched.frenchSubtitle}* — $${matched.price}), clinically engineered for **${matched.concern.toLowerCase()}** with **${matched.keyActives.slice(0, 2).join(' and ')}**.\n\nAll orders exceeding ₹1,500 ($150 USD) receive complimentary white-glove BlueDart Express delivery, 5-stage milestone tracking, and our 7-Day Sacred Return Guarantee. How may I further tailor your botanical ritual?`,
    suggestedProducts: [{
      productId: matched.id,
      name: matched.name,
      subtitle: matched.frenchSubtitle,
      price: matched.price,
      imageUrl: matched.image,
      clinicalBenefit: matched.benefits[0] || matched.tagline
    }],
    followUpQuestions: [
      `Tell me more about ${matched.name}`,
      'Recommend a routine for morning and evening',
      'What is your 7-day return policy?',
      'What are your active coupon codes?'
    ],
    citations: [{
      sourceId: `PRODUCT:${matched.id}`,
      title: `${matched.name} Atelier Dossier`,
      urlOrReference: `/product/${matched.id}`,
      excerpt: matched.description.substring(0, 120) + '...'
    }],
    intent: 'GROUNDED_FALLBACK_CONSULTATION',
    needsHumanSupport: false
  };
}
