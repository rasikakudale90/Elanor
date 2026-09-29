'use client';

import React, { useEffect, useRef } from 'react';
import Link from 'next/link';
import Image from 'next/image';
import { PRODUCTS, CONCERNS } from '@/data/products';
import ProductCard from '@/components/ProductCard';
import { Sparkles, ArrowRight, ShieldCheck, Leaf, Award, CheckCircle2, ChevronRight, Droplets } from 'lucide-react';
import gsap from 'gsap';

export default function HomePage() {
  const heroRef = useRef<HTMLDivElement>(null);
  const heroBottleRef = useRef<HTMLDivElement>(null);
  const heroTextRef = useRef<HTMLDivElement>(null);
  const concernsRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    // GSAP Hero Reveal Animation
    const ctx = gsap.context(() => {
      gsap.fromTo(
        heroTextRef.current,
        { opacity: 0, y: 40 },
        { opacity: 1, y: 0, duration: 1.4, ease: 'power3.out', delay: 0.2 }
      );

      gsap.fromTo(
        heroBottleRef.current,
        { opacity: 0, scale: 0.9, y: 60 },
        { opacity: 1, scale: 1, y: 0, duration: 1.6, ease: 'power3.out', delay: 0.4 }
      );
    }, heroRef);

    return () => ctx.revert();
  }, []);

  const bestSellers = PRODUCTS.filter((p) => p.isBestSeller);
  const heroProduct = PRODUCTS[0]; // Sérum Éclat Botanique

  return (
    <div className="relative overflow-hidden">
      {/* 1. HERO SECTION */}
      <section
        ref={heroRef}
        className="relative min-h-[92vh] flex items-center bg-hero-gradient pt-10 pb-20 px-6 sm:px-10 border-b border-[#EADFCF]"
      >
        {/* Ambient Glows */}
        <div className="absolute top-1/4 right-1/4 w-[500px] h-[500px] bg-glow-radial pointer-events-none opacity-80" />
        <div className="absolute -bottom-20 -left-20 w-[400px] h-[400px] bg-[#EEF2E8] rounded-full blur-3xl pointer-events-none" />

        <div className="max-w-[1440px] mx-auto w-full grid grid-cols-1 lg:grid-cols-12 gap-12 items-center relative z-10">
          {/* Left Column: Editorial Headline & Actions */}
          <div ref={heroTextRef} className="lg:col-span-6 space-y-6">
            <div className="inline-flex items-center space-x-2 px-3.5 py-1.5 rounded-full bg-[#FFFDF9]/80 border border-[#E4D7C5] shadow-sm text-xs font-medium text-[#5E584F]">
              <span className="w-2 h-2 rounded-full bg-[#C8A46A] animate-ping" />
              <span className="uppercase tracking-[0.2em] text-[10px] text-[#1B1A17] font-semibold">
                Haute Botanique • Vintage 2026
              </span>
            </div>

            <h1 className="font-serif-luxury text-5xl sm:text-6xl xl:text-7xl text-[#1B1A17] leading-[0.98] font-normal">
              Where Rare Botany <br />
              <span className="italic font-light text-[#C8A46A]">Meets Clinical</span> Rigor.
            </h1>

            <p className="text-sm sm:text-base text-[#5E584F] max-w-lg leading-relaxed font-normal">
              Formulated at our Paris atelier with wild-harvested French saffron stem cells, lipid-identical bio-ceramides, and micro-encapsulated retinal. Experience transcendent cellular luminosity.
            </p>

            <div className="flex flex-col sm:flex-row items-stretch sm:items-center space-y-3 sm:space-y-0 sm:space-x-4 pt-2">
              <Link
                href="/shop"
                className="px-8 py-4 bg-[#1B1A17] text-[#FFFDF9] rounded-full text-xs uppercase tracking-widest font-semibold hover:bg-[#322F2A] transition-all duration-300 shadow-card flex items-center justify-center space-x-2 group"
              >
                <span>Explore The Collection</span>
                <ArrowRight className="w-4 h-4 group-hover:translate-x-1.5 transition-transform" />
              </Link>

              <Link
                href="/routine-builder"
                className="px-7 py-4 rounded-full text-xs uppercase tracking-widest font-semibold border border-[#DCCDBA] bg-[#FFFDF9]/70 text-[#1B1A17] hover:bg-[#F2EBE2] transition-all flex items-center justify-center space-x-2 shadow-sm"
              >
                <Sparkles className="w-3.5 h-3.5 text-[#C8A46A]" />
                <span>AI Skin Diagnostic</span>
              </Link>
            </div>

            {/* Quick Metrics Bar */}
            <div className="pt-8 border-t border-[#E4D7C5] grid grid-cols-3 gap-6 text-[#1B1A17]">
              <div>
                <span className="font-serif-luxury text-2xl sm:text-3xl font-semibold">94%</span>
                <p className="text-[11px] text-[#8E857A] mt-0.5 leading-tight">Measured Cellular Radiance</p>
              </div>
              <div>
                <span className="font-serif-luxury text-2xl sm:text-3xl font-semibold">100%</span>
                <p className="text-[11px] text-[#8E857A] mt-0.5 leading-tight">Bio-Identical Lipids</p>
              </div>
              <div>
                <span className="font-serif-luxury text-2xl sm:text-3xl font-semibold">0%</span>
                <p className="text-[11px] text-[#8E857A] mt-0.5 leading-tight">Synthetic Fillers</p>
              </div>
            </div>
          </div>

          {/* Right Column: Hero Brand Showcase */}
          <div ref={heroBottleRef} className="lg:col-span-6 relative flex items-center justify-center">
            <div className="relative w-full max-w-[580px] aspect-[3/2] flex items-center justify-center">
              {/* Marble Pedestal Backdrop */}
              <div className="absolute -inset-2 rounded-3xl bg-gradient-to-tr from-[#EFE3D3] via-[#FDFBF8] to-[#E5D7C5] shadow-floating border border-[#FFFDF9]" />
              
              {/* Brand Visual Canvas */}
              <div className="relative z-10 w-full h-full rounded-3xl overflow-hidden shadow-floating border-2 border-[#FFFDF9]/90 animate-float bg-[#F8F3EB]">
                <Image
                  src="/images/skin7.png"
                  alt="Élanor — Pure Beauty. Naturally."
                  fill
                  priority
                  className="object-cover object-center"
                />
              </div>

              {/* Floating Glass Pill Badges - Placed carefully so they never obscure the ÉLANOR brand text */}
              <div className="absolute -bottom-4 left-6 z-20 glass-panel rounded-2xl py-2 px-3.5 shadow-card flex items-center space-x-2.5 border border-[#FFFDF9]/90">
                <div className="w-7 h-7 rounded-full bg-[#E4C894] flex items-center justify-center text-[#1B1A17]">
                  <Sparkles className="w-3.5 h-3.5" />
                </div>
                <div>
                  <p className="text-[11px] font-semibold text-[#1B1A17]">Pure Beauty. Naturally.</p>
                  <p className="text-[9px] text-[#7D9075]">Haute French Botany</p>
                </div>
              </div>

              <div className="absolute -top-3 right-4 z-20 glass-panel rounded-2xl py-2 px-3.5 shadow-card flex items-center space-x-2.5 border border-[#FFFDF9]/90">
                <div className="w-7 h-7 rounded-full bg-[#7D9075] flex items-center justify-center text-[#FFFDF9]">
                  <Droplets className="w-3.5 h-3.5" />
                </div>
                <div>
                  <p className="text-[11px] font-semibold text-[#1B1A17]">100% Bio-Identical</p>
                  <p className="text-[9px] text-[#8E857A]">Dermatologist Verified</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* 2. EDITORIAL ESSENCE / BRAND MARQUEE */}
      <section className="py-6 bg-[#1B1A17] text-[#FFFDF9] overflow-hidden border-y border-[#322F2A]">
        <div className="flex items-center space-x-12 whitespace-nowrap animate-marquee text-xs uppercase tracking-[0.25em] font-medium text-[#E4C894]">
          <span>• Pure Damask Rose Distillate</span>
          <span>• Wild-Harvested Alpine Edelweiss</span>
          <span>• Cold-Pressed Botanical Squalane</span>
          <span>• Liposomal Retinaldehyde 0.1%</span>
          <span>• 5-Ceramide Biomimetic Architecture</span>
          <span>• Handcrafted In Paris</span>
          <span>• Carbon-Neutral Luxury Packaging</span>
        </div>
      </section>

      {/* 3. ICONIC RITUALS (BESTSELLERS) */}
      <section className="py-24 max-w-[1440px] mx-auto px-6 sm:px-10">
        <div className="flex flex-col md:flex-row md:items-end justify-between mb-14">
          <div>
            <span className="text-xs font-semibold uppercase tracking-[0.2em] text-[#7D9075]">
              Curated Masterpieces
            </span>
            <h2 className="font-serif-luxury text-4xl sm:text-5xl text-[#1B1A17] mt-1.5">
              The Iconic Formulations
            </h2>
            <p className="text-xs sm:text-sm text-[#5E584F] mt-2 max-w-lg">
              Each formulation is aged in amber glass vats to stabilize delicate phytomolecules and deliver uncompromised dermal transformation.
            </p>
          </div>

          <Link
            href="/shop"
            className="mt-6 md:mt-0 text-xs uppercase tracking-widest font-semibold text-[#1B1A17] hover:text-[#C8A46A] flex items-center space-x-1.5 group self-start md:self-end"
          >
            <span>View All Formulations ({PRODUCTS.length})</span>
            <ChevronRight className="w-4 h-4 group-hover:translate-x-1 transition-transform" />
          </Link>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8">
          {bestSellers.map((product) => (
            <ProductCard key={product.id} product={product} />
          ))}
        </div>
      </section>

      {/* 4. SHOP BY SKIN CONCERN SPOTLIGHT */}
      <section ref={concernsRef} className="py-24 bg-[#F2EBE2] border-y border-[#E4D7C5]">
        <div className="max-w-[1440px] mx-auto px-6 sm:px-10">
          <div className="text-center max-w-2xl mx-auto mb-16 space-y-3">
            <span className="text-xs font-semibold uppercase tracking-[0.2em] text-[#C8A46A]">
              Targeted Dermatology
            </span>
            <h2 className="font-serif-luxury text-4xl sm:text-5xl text-[#1B1A17]">
              Prescribed by Dermal Need
            </h2>
            <p className="text-xs sm:text-sm text-[#5E584F]">
              Select your primary skin priority to reveal tailored botanicals and clinical synergistic protocols.
            </p>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {CONCERNS.map((concern) => (
              <Link
                key={concern.slug}
                href={`/concern/${concern.slug}`}
                className="group bg-[#FFFDF9] rounded-3xl p-6 border border-[#EADFCF] hover:border-[#C8A46A] transition-all duration-300 hover:shadow-card flex flex-col justify-between"
              >
                <div>
                  <div className="flex justify-between items-center mb-4">
                    <span className="text-[10px] tracking-widest uppercase font-semibold text-[#8E857A]">
                      {concern.frenchTitle}
                    </span>
                    <span
                      className="w-3 h-3 rounded-full"
                      style={{ backgroundColor: concern.color }}
                    />
                  </div>

                  <h3 className="font-serif-luxury text-2xl text-[#1B1A17] group-hover:text-[#C8A46A] transition-colors">
                    {concern.name}
                  </h3>

                  <p className="text-xs text-[#5E584F] leading-relaxed mt-2.5">
                    {concern.description}
                  </p>
                </div>

                <div className="mt-8 pt-4 border-t border-[#EFE3D3] flex items-center justify-between text-xs font-semibold text-[#1B1A17] group-hover:text-[#C8A46A]">
                  <span>Explore Concern Protocol</span>
                  <ArrowRight className="w-4 h-4 group-hover:translate-x-1.5 transition-transform" />
                </div>
              </Link>
            ))}
          </div>
        </div>
      </section>

      {/* 5. INTERACTIVE AI ROUTINE DIAGNOSTIC HERO TEASER */}
      <section className="py-24 max-w-[1440px] mx-auto px-6 sm:px-10">
        <div className="bg-[#1B1A17] text-[#FFFDF9] rounded-3xl p-8 sm:p-14 lg:p-16 relative overflow-hidden shadow-2xl border border-[#3E3A33]">
          {/* Subtle Background Radial Glow */}
          <div className="absolute -top-20 -right-20 w-[450px] h-[450px] bg-[#C8A46A]/20 rounded-full blur-3xl pointer-events-none" />

          <div className="grid grid-cols-1 lg:grid-cols-12 gap-10 items-center relative z-10">
            <div className="lg:col-span-7 space-y-6">
              <div className="inline-flex items-center space-x-2 px-3.5 py-1.5 rounded-full bg-[#262420] border border-[#484239] text-xs font-medium text-[#C8A46A]">
                <Sparkles className="w-3.5 h-3.5" />
                <span className="uppercase tracking-[0.2em] text-[10px]">
                  Algorithmic Dermatology
                </span>
              </div>

              <h2 className="font-serif-luxury text-4xl sm:text-5xl lg:text-6xl text-[#FFFDF9] leading-[1.05]">
                Discover your bespoke <br />
                <span className="italic text-[#E4C894]">Morning & Evening</span> Regimen.
              </h2>

              <p className="text-xs sm:text-sm text-[#8E857A] leading-relaxed max-w-lg">
                Answer 4 guided questions regarding your dermal type, climate exposures, and sensory desires. Our AI diagnostic synthesizes the exact pH-compatible sequence for optimal cellular absorption.
              </p>

              <div className="pt-2 flex flex-col sm:flex-row space-y-3 sm:space-y-0 sm:space-x-4">
                <Link
                  href="/routine-builder"
                  className="px-8 py-4 bg-[#C8A46A] text-[#1B1A17] rounded-full text-xs uppercase tracking-widest font-semibold hover:bg-[#E4C894] transition-all duration-300 shadow-md flex items-center justify-center space-x-2 group"
                >
                  <span>Start AI Consultation (2 Mins)</span>
                  <ArrowRight className="w-4 h-4 group-hover:translate-x-1.5 transition-transform" />
                </Link>

                <Link
                  href="/ingredients"
                  className="px-7 py-4 rounded-full text-xs uppercase tracking-widest font-semibold border border-[#484239] bg-[#262420] text-[#FFFDF9] hover:bg-[#322F2A] transition-all flex items-center justify-center space-x-2"
                >
                  <span>Explore Ingredient Glossary</span>
                </Link>
              </div>
            </div>

            <div className="lg:col-span-5 flex justify-center">
              <div className="w-full max-w-sm bg-[#262420] border border-[#484239] rounded-3xl p-6 shadow-xl space-y-4">
                <div className="flex items-center justify-between pb-3 border-b border-[#3E3A33] text-xs text-[#8E857A]">
                  <span>Prescription Matrix #8492</span>
                  <span className="text-[#C8A46A] font-semibold">99.4% Match</span>
                </div>

                <div className="space-y-3">
                  <div className="flex items-center space-x-3 p-2.5 rounded-xl bg-[#1B1A17] border border-[#3E3A33]">
                    <div className="w-6 h-6 rounded-full bg-[#C8A46A]/20 text-[#C8A46A] flex items-center justify-center text-xs font-semibold">
                      1
                    </div>
                    <div>
                      <p className="text-xs font-medium text-[#FFFDF9]">Huile Gelée Pureté</p>
                      <p className="text-[10px] text-[#8E857A]">Step 1 • pH 5.5 Clarifying Cleanse</p>
                    </div>
                  </div>

                  <div className="flex items-center space-x-3 p-2.5 rounded-xl bg-[#1B1A17] border border-[#3E3A33]">
                    <div className="w-6 h-6 rounded-full bg-[#C8A46A]/20 text-[#C8A46A] flex items-center justify-center text-xs font-semibold">
                      2
                    </div>
                    <div>
                      <p className="text-xs font-medium text-[#FFFDF9]">Sérum Éclat Botanique</p>
                      <p className="text-[10px] text-[#8E857A]">Step 2 • Saffron Stem Cells & Glow</p>
                    </div>
                  </div>

                  <div className="flex items-center space-x-3 p-2.5 rounded-xl bg-[#1B1A17] border border-[#3E3A33]">
                    <div className="w-6 h-6 rounded-full bg-[#C8A46A]/20 text-[#C8A46A] flex items-center justify-center text-xs font-semibold">
                      3
                    </div>
                    <div>
                      <p className="text-xs font-medium text-[#FFFDF9]">Crème Renaissance Barrière</p>
                      <p className="text-[10px] text-[#8E857A]">Step 3 • 5-Ceramide Seal</p>
                    </div>
                  </div>
                </div>

                <div className="pt-3 border-t border-[#3E3A33] flex items-center justify-between text-xs text-[#C8A46A]">
                  <span className="flex items-center gap-1">
                    <CheckCircle2 className="w-3.5 h-3.5" />
                    Custom Ritual Ready
                  </span>
                  <span className="font-semibold text-[#FFFDF9]">$483 (Save 15%)</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* 6. CLINICAL VERIFICATION & DERMAL TRIALS */}
      <section className="py-20 max-w-[1440px] mx-auto px-6 sm:px-10 border-t border-[#EADFCF]">
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-12 items-center">
          <div className="lg:col-span-5 relative">
            <div className="aspect-[4/5] rounded-3xl overflow-hidden shadow-card border border-[#EADFCF] relative bg-[#F2EBE2]">
              <Image
                src="/images/skin5.jfif"
                alt="Clinical Dermal Testing"
                fill
                className="object-cover"
              />
            </div>
            <div className="absolute -bottom-6 -right-6 glass-panel p-5 rounded-2xl max-w-xs shadow-floating border border-[#FFFDF9]">
              <p className="font-serif-luxury text-3xl font-semibold text-[#1B1A17]">98.6%</p>
              <p className="text-xs text-[#5E584F] mt-1 font-medium">
                Participant satisfaction verified by Independent Swiss Clinical Dermatological Trials (2025).
              </p>
            </div>
          </div>

          <div className="lg:col-span-7 space-y-6">
            <span className="text-xs font-semibold uppercase tracking-[0.2em] text-[#7D9075]">
              Evidence-Based Formulation
            </span>
            <h2 className="font-serif-luxury text-4xl sm:text-5xl text-[#1B1A17] leading-tight">
              Clinical Purity Without Compromise.
            </h2>
            <p className="text-xs sm:text-sm text-[#5E584F] leading-relaxed">
              Every drop of Élanor undergoes stringent bio-compatibility testing to guarantee zero micro-tears, non-comedogenic texture, and maximum cellular uptake across all skin phenotypes.
            </p>

            <div className="space-y-4 pt-2">
              <div className="flex items-start space-x-3.5 p-3.5 rounded-2xl bg-[#FFFDF9] border border-[#EFE3D3]">
                <ShieldCheck className="w-5 h-5 text-[#7D9075] shrink-0 mt-0.5" />
                <div>
                  <h4 className="text-xs font-semibold uppercase tracking-wider text-[#1B1A17]">
                    Double-Blind Dermatological Verification
                  </h4>
                  <p className="text-xs text-[#5E584F] mt-0.5">
                    Evaluated across 240 subjects over 56 consecutive days for barrier strengthening and hyperpigmentation erasure.
                  </p>
                </div>
              </div>

              <div className="flex items-start space-x-3.5 p-3.5 rounded-2xl bg-[#FFFDF9] border border-[#EFE3D3]">
                <Leaf className="w-5 h-5 text-[#7D9075] shrink-0 mt-0.5" />
                <div>
                  <h4 className="text-xs font-semibold uppercase tracking-wider text-[#1B1A17]">
                    100% Ethical Wild-Harvesting
                  </h4>
                  <p className="text-xs text-[#5E584F] mt-0.5">
                    Sustainably picked by hand at dawn in the high valleys of Grasse and Swiss alpine sanctuaries.
                  </p>
                </div>
              </div>

              <div className="flex items-start space-x-3.5 p-3.5 rounded-2xl bg-[#FFFDF9] border border-[#EFE3D3]">
                <Award className="w-5 h-5 text-[#7D9075] shrink-0 mt-0.5" />
                <div>
                  <h4 className="text-xs font-semibold uppercase tracking-wider text-[#1B1A17]">
                    Zero Synthetic Toxins or Endocrine Disruptors
                  </h4>
                  <p className="text-xs text-[#5E584F] mt-0.5">
                    Formulated without parabens, synthetic fragrances, silicones, sulfates, or microplastics.
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}
