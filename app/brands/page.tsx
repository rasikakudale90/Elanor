'use client';

import React from 'react';
import Link from 'next/link';
import Image from 'next/image';
import { Leaf, ShieldCheck, Award, Sparkles, ArrowRight } from 'lucide-react';

export default function BrandsPage() {
  return (
    <div className="bg-[#F8F3EB] min-h-screen py-12 sm:py-16">
      <div className="max-w-[1440px] mx-auto px-6 sm:px-10 space-y-20">
        {/* Editorial Hero */}
        <div className="text-center max-w-3xl mx-auto space-y-4">
          <span className="text-xs uppercase tracking-[0.25em] text-[#7D9075] font-semibold">
            Maison Élanor • Founded in Paris
          </span>
          <h1 className="font-serif-luxury text-4xl sm:text-5xl lg:text-6xl text-[#1B1A17] font-normal leading-tight">
            The Philosophy of Haute Botanique & Clinical Cellular Longevity
          </h1>
          <p className="text-xs sm:text-sm text-[#5E584F] leading-relaxed">
            Élanor was founded upon a single radical conviction: that true dermal radiance is born when unadulterated botanical essences are amplified by state-of-the-art cellular bio-fermentation.
          </p>
        </div>

        {/* Narrative Section 1: The Paris Atelier */}
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-12 items-center">
          <div className="lg:col-span-6 relative aspect-[4/3] rounded-3xl overflow-hidden bg-[#F2EBE2] border border-[#EADFCF] shadow-card">
            <Image
              src="/images/skin2.jfif"
              alt="Maison Élanor Atelier Paris"
              fill
              className="object-cover"
            />
          </div>

          <div className="lg:col-span-6 space-y-6">
            <span className="text-xs uppercase tracking-widest font-semibold text-[#C8A46A]">
              Sanctuary of Creation
            </span>
            <h2 className="font-serif-luxury text-3xl sm:text-4xl text-[#1B1A17]">
              Handcrafted in Small Micro-Batches at 24 Place Vendôme.
            </h2>
            <p className="text-xs sm:text-sm text-[#5E584F] leading-relaxed">
              Unlike mass-industrial cosmetics, every Élanor elixir is formulated in limited editions of no more than 500 vessels per vintage. This ensures that delicate phytomolecules like crocin, safranal, and retinaldehyde retain their peak bio-potency until the moment they touch your skin.
            </p>
            <p className="text-xs sm:text-sm text-[#5E584F] leading-relaxed">
              We never use synthetic thickeners, silicones, artificial pigments, or endocrine disruptors. Each texture is an authentic expression of cold-pressed plant lipids and biomimetic ceramides.
            </p>
          </div>
        </div>

        {/* The Three Pillars */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          <div className="bg-[#FFFDF9] p-8 rounded-3xl border border-[#EFE3D3] shadow-card space-y-4">
            <div className="w-12 h-12 rounded-full bg-[#F8F3EB] flex items-center justify-center text-[#7D9075]">
              <Leaf className="w-6 h-6" />
            </div>
            <h3 className="font-serif-luxury text-2xl text-[#1B1A17]">Wild-Harvested Sourcing</h3>
            <p className="text-xs text-[#5E584F] leading-relaxed">
              Our botanicals are hand-picked at dawn across Grasse rose fields, Provence saffron valleys, and Swiss alpine peaks during seasonal windows of peak phytochemical vibrancy.
            </p>
          </div>

          <div className="bg-[#FFFDF9] p-8 rounded-3xl border border-[#EFE3D3] shadow-card space-y-4">
            <div className="w-12 h-12 rounded-full bg-[#F8F3EB] flex items-center justify-center text-[#C8A46A]">
              <ShieldCheck className="w-6 h-6" />
            </div>
            <h3 className="font-serif-luxury text-2xl text-[#1B1A17]">Clinical Bio-Fermentation</h3>
            <p className="text-xs text-[#5E584F] leading-relaxed">
              We employ enzymatic bio-fermentation to micronize active nutrients into ultra-low molecular weights, allowing effortless penetration past the stratum corneum without disrupting pH.
            </p>
          </div>

          <div className="bg-[#FFFDF9] p-8 rounded-3xl border border-[#EFE3D3] shadow-card space-y-4">
            <div className="w-12 h-12 rounded-full bg-[#F8F3EB] flex items-center justify-center text-[#55624E]">
              <Award className="w-6 h-6" />
            </div>
            <h3 className="font-serif-luxury text-2xl text-[#1B1A17]">Miron Violet Glass Alchemy</h3>
            <p className="text-xs text-[#5E584F] leading-relaxed">
              Every elixir is housed in biophotonic Swiss violet glass. This patented glass acts as a natural filter, blocking degrading visible light while allowing beneficial violet and infrared frequencies.
            </p>
          </div>
        </div>

        {/* CTA */}
        <div className="bg-[#1B1A17] text-[#FFFDF9] rounded-3xl p-10 sm:p-16 text-center space-y-6 shadow-2xl">
          <Sparkles className="w-10 h-10 text-[#C8A46A] mx-auto" />
          <h2 className="font-serif-luxury text-3xl sm:text-4xl lg:text-5xl text-[#FFFDF9]">
            Begin Your Sacred Skincare Transformation
          </h2>
          <p className="text-xs sm:text-sm text-[#8E857A] max-w-lg mx-auto">
            Discover our complete range of certified organic formulations, or consult our AI diagnostic for a tailored regimen.
          </p>
          <div className="flex flex-col sm:flex-row justify-center space-y-3 sm:space-y-0 sm:space-x-4 pt-2">
            <Link
              href="/shop"
              className="px-8 py-4 bg-[#C8A46A] text-[#1B1A17] rounded-full text-xs uppercase tracking-widest font-semibold hover:bg-[#E4C894] transition-all flex items-center justify-center space-x-2"
            >
              <span>Explore The Formulary</span>
              <ArrowRight className="w-4 h-4" />
            </Link>
            <Link
              href="/routine-builder"
              className="px-8 py-4 bg-[#262420] text-[#FFFDF9] border border-[#484239] rounded-full text-xs uppercase tracking-widest font-semibold hover:bg-[#322F2A] transition-all flex items-center justify-center"
            >
              <span>AI Routine Diagnostic</span>
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}
