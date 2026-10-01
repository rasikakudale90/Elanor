import { Product } from '@/data/products';

export type SkinType =
  | 'Comfortable'
  | 'Dry / Tight'
  | 'Oily / Shiny'
  | 'Combination'
  | 'Sensitive / Reactive';

export type SkinConcern =
  | 'Radiance'
  | 'Firming'
  | 'Barrier Restoration'
  | 'Deep Quenching'
  | 'Calming'
  | 'Pore Clarifying'
  | 'Uneven Tone'
  | 'Texture';

export type SkinContext =
  | 'Air-Conditioned / Indoor'
  | 'High Humidity / Tropical'
  | 'Dry / Alpine / Cold'
  | 'Urban Pollution / Commuting'
  | 'Frequent Sun Exposure';

export type RitualPace =
  | 'Minimal'
  | 'Balanced'
  | 'Complete';

export type TexturePreference =
  | 'Lightweight'
  | 'Creamy'
  | 'Rich'
  | 'Oil-based'
  | 'No preference';

export interface ConsultationAnswers {
  skinType: SkinType;
  concerns: SkinConcern[];
  skinContext: SkinContext;
  ritualPace: RitualPace;
  texturePreference: TexturePreference;
}

export interface MetricScore {
  name: string;
  frenchName: string;
  value: number; // 0 - 100
  color: string;
}

export interface SkinProfileResult {
  archetype: string;
  frenchTitle: string;
  headline: string;
  summary: string;
  cellularFocus: string;
  metrics: MetricScore[];
  matchScore: number;
}

export interface RitualStep {
  stepNumber: string;
  phase: string;
  timeOfDay: 'AM' | 'PM';
  product: Product;
  whyItsHere: string[];
  applicationTip: string;
}

export interface ConciergeResult {
  profile: SkinProfileResult;
  morningRitual: RitualStep[];
  eveningRitual: RitualStep[];
  uniqueProducts: Product[];
  totalPrice: number;
  bundlePrice: number;
}
