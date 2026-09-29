'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import Image from 'next/image';
import { useStore } from '@/context/StoreContext';
import { PRODUCTS, Product } from '@/data/products';
import { Sparkles, ArrowRight, Check, ArrowLeft, Sun, Moon, ShieldCheck, RefreshCw, ShoppingBag } from 'lucide-react';

export default function RoutineBuilderPage() {
  const { addToCart } = useStore();

  const [step, setStep] = useState<number>(1);
  const [skinType, setSkinType] = useState<string>('Combination');
  const [primaryConcern, setPrimaryConcern] = useState<string>('Radiance');
  const [climate, setClimate] = useState<string>('Urban');
  const [ritualPace, setRitualPace] = useState<string>('Haute');

  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [analysisComplete, setAnalysisComplete] = useState(false);
  const [addedAll, setAddedAll] = useState(false);

  const startAnalysis = () => {
    setIsAnalyzing(true);
    setTimeout(() => {
      setIsAnalyzing(false);
      setAnalysisComplete(true);
    }, 1800);
  };

  const restartQuiz = () => {
    setStep(1);
    setAnalysisComplete(false);
    setIsAnalyzing(false);
  };

  // Curate dynamic Morning and Evening Routine based on selections
  const morningProducts: Product[] = [
    PRODUCTS[4], // Cleanser
    PRODUCTS[0], // Saffron Serum (Radiance)
    PRODUCTS[3], // Hydra Infusion
  ];

  const eveningProducts: Product[] = [
    PRODUCTS[4], // Cleanser
    PRODUCTS[2], // Nocturne Retinal Elixir
    PRODUCTS[1], // Barrier Velvet Cream
    PRODUCTS[5], // Eye Contour
  ];

  const totalRoutinePrice = [...morningProducts, ...eveningProducts]
    .filter((v, i, a) => a.findIndex(t => t.id === v.id) === i)
    .reduce((sum, p) => sum + p.price, 0);

  const discountedBundlePrice = Math.round(totalRoutinePrice * 0.85);

  const handleAddEntireRoutine = () => {
    const uniqueProducts = [...morningProducts, ...eveningProducts].filter(
      (v, i, a) => a.findIndex(t => t.id === v.id) === i
    );
    uniqueProducts.forEach(p => addToCart(p, 1));
    setAddedAll(true);
    setTimeout(() => setAddedAll(false), 2500);
  };

  return (
    <div className="bg-[#F8F3EB] min-h-screen py-12 sm:py-16">
      <div className="max-w-[1280px] mx-auto px-6 sm:px-10">
        {/* Header */}
        <div className="text-center max-w-2xl mx-auto mb-12 space-y-3">
          <div className="inline-flex items-center space-x-2 px-3.5 py-1.5 rounded-full bg-[#FFFDF9] border border-[#E4D7C5] text-xs font-semibold text-[#7D9075] shadow-sm">
            <Sparkles className="w-3.5 h-3.5 text-[#C8A46A]" />
            <span>Algorithmic Cellular Diagnostics</span>
          </div>
          <h1 className="font-serif-luxury text-4xl sm:text-5xl lg:text-6xl text-[#1B1A17] font-normal">
            Bespoke Skin Architecture
          </h1>
          <p className="text-xs sm:text-sm text-[#5E584F] leading-relaxed">
            Answer 4 diagnostic inquiries to configure your optimal botanical sequence tailored to your cellular barrier and environmental exposures.
          </p>
        </div>

        {/* QUIZ IN PROGRESS */}
        {!analysisComplete && !isAnalyzing && (
          <div className="max-w-3xl mx-auto bg-[#FFFDF9] rounded-3xl p-8 sm:p-12 border border-[#EFE3D3] shadow-card space-y-8">
            {/* Progress indicator */}
            <div className="flex items-center justify-between pb-4 border-b border-[#EADFCF] text-xs text-[#8E857A]">
              <span>Step {step} of 4</span>
              <span className="font-semibold text-[#1B1A17]">{step * 25}% Formulated</span>
            </div>

            {/* Step 1: Skin Type */}
            {step === 1 && (
              <div className="space-y-6">
                <div>
                  <h3 className="font-serif-luxury text-2xl sm:text-3xl text-[#1B1A17]">
                    How would you describe your baseline dermal phenotype?
                  </h3>
                  <p className="text-xs text-[#5E584F] mt-1">Select the sensation closest to your midday skin state.</p>
                </div>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  {[
                    { id: 'Dry', label: 'Lipid-Deficient & Dry', desc: 'Prone to tightness, dullness, and flaking in dry climates.' },
                    { id: 'Combination', label: 'Balanced / Combination', desc: 'T-zone shine with balanced or slightly dry cheeks.' },
                    { id: 'Oily', label: 'Sebum-Rich & Blemish-Prone', desc: 'Frequent shine, visible pore dilation, and congestion.' },
                    { id: 'Sensitive', label: 'Reactive & Fragile', desc: 'Prone to redness, burning sensation, or environmental flushing.' },
                  ].map((type) => (
                    <button
                      key={type.id}
                      onClick={() => setSkinType(type.id)}
                      className={`p-5 rounded-2xl text-left border transition-all ${
                        skinType === type.id
                          ? 'bg-[#F8F3EB] border-[#C8A46A] ring-1 ring-[#C8A46A] shadow-sm'
                          : 'bg-[#FFFDF9] border-[#EADFCF] hover:bg-[#FDFBF8]'
                      }`}
                    >
                      <h4 className="font-serif-luxury text-lg font-semibold text-[#1B1A17]">{type.label}</h4>
                      <p className="text-xs text-[#5E584F] mt-1">{type.desc}</p>
                    </button>
                  ))}
                </div>

                <div className="flex justify-end pt-4">
                  <button
                    onClick={() => setStep(2)}
                    className="px-8 py-3.5 bg-[#1B1A17] text-[#FFFDF9] rounded-full text-xs uppercase tracking-widest font-semibold hover:bg-[#322F2A] flex items-center space-x-2"
                  >
                    <span>Next Inquiry</span>
                    <ArrowRight className="w-4 h-4" />
                  </button>
                </div>
              </div>
            )}

            {/* Step 2: Primary Concern */}
            {step === 2 && (
              <div className="space-y-6">
                <div>
                  <h3 className="font-serif-luxury text-2xl sm:text-3xl text-[#1B1A17]">
                    What is your paramount skin priority?
                  </h3>
                  <p className="text-xs text-[#5E584F] mt-1">Our bio-actives target specific cellular signaling pathways.</p>
                </div>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  {[
                    { id: 'Radiance', label: 'Cellular Luminescence & Tone', desc: 'Dissolve hyperpigmentation and illuminate tired, dull skin.' },
                    { id: 'Anti-Aging', label: 'Longevity & Fine Lines', desc: 'Stimulate collagen architecture, density, and elastic bounce.' },
                    { id: 'Barrier Repair', label: 'Barrier Rebuilding & Lipids', desc: 'Heal micro-fissures and fortify compromised moisture mantles.' },
                    { id: 'Hydration', label: 'Deep Multi-Tier Hydration', desc: 'Replenish intercellular moisture reservoirs with 8-tier hyaluronic.' },
                  ].map((c) => (
                    <button
                      key={c.id}
                      onClick={() => setPrimaryConcern(c.id)}
                      className={`p-5 rounded-2xl text-left border transition-all ${
                        primaryConcern === c.id
                          ? 'bg-[#F8F3EB] border-[#C8A46A] ring-1 ring-[#C8A46A] shadow-sm'
                          : 'bg-[#FFFDF9] border-[#EADFCF] hover:bg-[#FDFBF8]'
                      }`}
                    >
                      <h4 className="font-serif-luxury text-lg font-semibold text-[#1B1A17]">{c.label}</h4>
                      <p className="text-xs text-[#5E584F] mt-1">{c.desc}</p>
                    </button>
                  ))}
                </div>

                <div className="flex justify-between pt-4">
                  <button
                    onClick={() => setStep(1)}
                    className="px-6 py-3.5 border border-[#DCCDBA] text-[#1B1A17] rounded-full text-xs uppercase tracking-widest font-semibold hover:bg-[#F2EBE2]"
                  >
                    Back
                  </button>
                  <button
                    onClick={() => setStep(3)}
                    className="px-8 py-3.5 bg-[#1B1A17] text-[#FFFDF9] rounded-full text-xs uppercase tracking-widest font-semibold hover:bg-[#322F2A] flex items-center space-x-2"
                  >
                    <span>Next Inquiry</span>
                    <ArrowRight className="w-4 h-4" />
                  </button>
                </div>
              </div>
            )}

            {/* Step 3: Climate */}
            {step === 3 && (
              <div className="space-y-6">
                <div>
                  <h3 className="font-serif-luxury text-2xl sm:text-3xl text-[#1B1A17]">
                    What environmental ecosystem are you exposed to?
                  </h3>
                  <p className="text-xs text-[#5E584F] mt-1">Atmospheric humidity and pollution alter barrier permeability.</p>
                </div>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  {[
                    { id: 'Urban', label: 'Metropolitan & High Pollution', desc: 'Frequent smog, airborne free radicals, and high blue light screen exposure.' },
                    { id: 'Dry', label: 'Arid / Cold Mountain Climate', desc: 'Low ambient humidity, rapid trans-epidermal moisture loss.' },
                    { id: 'Tropical', label: 'Tropical & High Humidity', desc: 'Heat, sweat, and elevated sebum oxidation risk.' },
                    { id: 'Temperate', label: 'Moderate & Seasonally Shifting', desc: 'Variable seasonal transitions requiring adaptable lipid layers.' },
                  ].map((cl) => (
                    <button
                      key={cl.id}
                      onClick={() => setClimate(cl.id)}
                      className={`p-5 rounded-2xl text-left border transition-all ${
                        climate === cl.id
                          ? 'bg-[#F8F3EB] border-[#C8A46A] ring-1 ring-[#C8A46A] shadow-sm'
                          : 'bg-[#FFFDF9] border-[#EADFCF] hover:bg-[#FDFBF8]'
                      }`}
                    >
                      <h4 className="font-serif-luxury text-lg font-semibold text-[#1B1A17]">{cl.label}</h4>
                      <p className="text-xs text-[#5E584F] mt-1">{cl.desc}</p>
                    </button>
                  ))}
                </div>

                <div className="flex justify-between pt-4">
                  <button
                    onClick={() => setStep(2)}
                    className="px-6 py-3.5 border border-[#DCCDBA] text-[#1B1A17] rounded-full text-xs uppercase tracking-widest font-semibold hover:bg-[#F2EBE2]"
                  >
                    Back
                  </button>
                  <button
                    onClick={() => setStep(4)}
                    className="px-8 py-3.5 bg-[#1B1A17] text-[#FFFDF9] rounded-full text-xs uppercase tracking-widest font-semibold hover:bg-[#322F2A] flex items-center space-x-2"
                  >
                    <span>Next Inquiry</span>
                    <ArrowRight className="w-4 h-4" />
                  </button>
                </div>
              </div>
            )}

            {/* Step 4: Ritual Desire */}
            {step === 4 && (
              <div className="space-y-6">
                <div>
                  <h3 className="font-serif-luxury text-2xl sm:text-3xl text-[#1B1A17]">
                    Choose your daily ritual architecture.
                  </h3>
                  <p className="text-xs text-[#5E584F] mt-1">Select the depth of your morning and nocturnal skincare practice.</p>
                </div>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  {[
                    { id: 'Minimalist', label: '3-Step Essential Precision', desc: 'Targeted cleanse, potent cellular treatment, and lipid lock.' },
                    { id: 'Haute', label: 'Haute 5-Step Atelier Immersion', desc: 'Comprehensive essence layering, contour sculpting, and nocturnal rejuvenation.' },
                  ].map((rp) => (
                    <button
                      key={rp.id}
                      onClick={() => setRitualPace(rp.id)}
                      className={`p-5 rounded-2xl text-left border transition-all ${
                        ritualPace === rp.id
                          ? 'bg-[#F8F3EB] border-[#C8A46A] ring-1 ring-[#C8A46A] shadow-sm'
                          : 'bg-[#FFFDF9] border-[#EADFCF] hover:bg-[#FDFBF8]'
                      }`}
                    >
                      <h4 className="font-serif-luxury text-lg font-semibold text-[#1B1A17]">{rp.label}</h4>
                      <p className="text-xs text-[#5E584F] mt-1">{rp.desc}</p>
                    </button>
                  ))}
                </div>

                <div className="flex justify-between pt-4">
                  <button
                    onClick={() => setStep(3)}
                    className="px-6 py-3.5 border border-[#DCCDBA] text-[#1B1A17] rounded-full text-xs uppercase tracking-widest font-semibold hover:bg-[#F2EBE2]"
                  >
                    Back
                  </button>
                  <button
                    onClick={startAnalysis}
                    className="px-8 py-4 bg-[#C8A46A] text-[#1B1A17] rounded-full text-xs uppercase tracking-widest font-semibold hover:bg-[#E4C894] flex items-center space-x-2 shadow-md"
                  >
                    <span>Generate Algorithmic Regimen</span>
                    <Sparkles className="w-4 h-4" />
                  </button>
                </div>
              </div>
            )}
          </div>
        )}

        {/* LOADING ANIMATION STATE */}
        {isAnalyzing && (
          <div className="max-w-xl mx-auto bg-[#FFFDF9] rounded-3xl p-12 border border-[#EFE3D3] shadow-card text-center space-y-6">
            <div className="relative w-24 h-24 mx-auto flex items-center justify-center">
              <div className="absolute inset-0 rounded-full border-4 border-[#EADFCF] border-t-[#C8A46A] animate-spin" />
              <Sparkles className="w-8 h-8 text-[#C8A46A]" />
            </div>
            <div className="space-y-2">
              <h3 className="font-serif-luxury text-2xl text-[#1B1A17]">Synthesizing Cellular Blueprint</h3>
              <p className="text-xs text-[#5E584F] max-w-sm mx-auto">
                Aligning pH gradients, lipid bioavailability, and botanical active synergies for {skinType} skin...
              </p>
            </div>
          </div>
        )}

        {/* RESULT / GENERATED REGIMEN */}
        {analysisComplete && (
          <div className="space-y-12 animate-in fade-in zoom-in-95 duration-500">
            {/* Diagnosis Banner */}
            <div className="bg-[#1B1A17] text-[#FFFDF9] rounded-3xl p-8 sm:p-10 border border-[#3E3A33] shadow-2xl flex flex-col md:flex-row justify-between items-start md:items-center gap-6">
              <div className="space-y-2">
                <span className="text-xs uppercase tracking-widest text-[#C8A46A] font-semibold flex items-center gap-2">
                  <Check className="w-4 h-4" />
                  Prescription Protocol Generated
                </span>
                <h2 className="font-serif-luxury text-3xl sm:text-4xl text-[#FFFDF9]">
                  The Élanor {primaryConcern} & Resilience Regimen
                </h2>
                <p className="text-xs text-[#8E857A]">
                  Profile: {skinType} Skin • {climate} Environment • {ritualPace} Architecture
                </p>
              </div>

              <div className="flex items-center space-x-3">
                <button
                  onClick={restartQuiz}
                  className="px-5 py-3 rounded-full text-xs uppercase tracking-widest font-semibold border border-[#484239] hover:bg-[#262420] text-[#FFFDF9] flex items-center space-x-1.5"
                >
                  <RefreshCw className="w-3.5 h-3.5" />
                  <span>Retake Quiz</span>
                </button>

                <button
                  onClick={handleAddEntireRoutine}
                  className="px-8 py-3.5 rounded-full text-xs uppercase tracking-widest font-semibold bg-[#C8A46A] text-[#1B1A17] hover:bg-[#E4C894] transition-all flex items-center space-x-2 shadow-md"
                >
                  {addedAll ? (
                    <>
                      <Check className="w-4 h-4" />
                      <span>Entire Routine Added!</span>
                    </>
                  ) : (
                    <>
                      <ShoppingBag className="w-4 h-4" />
                      <span>Add Complete Set (${discountedBundlePrice})</span>
                    </>
                  )}
                </button>
              </div>
            </div>

            {/* Morning & Evening Regimen Columns */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
              {/* Morning Ritual */}
              <div className="bg-[#FFFDF9] p-8 rounded-3xl border border-[#EFE3D3] shadow-card space-y-6">
                <div className="flex items-center space-x-2.5 pb-4 border-b border-[#EADFCF] text-[#C8A46A]">
                  <Sun className="w-5 h-5" />
                  <h3 className="font-serif-luxury text-2xl text-[#1B1A17]">Morning Awakening Ritual</h3>
                </div>

                <div className="space-y-4">
                  {morningProducts.map((p, idx) => (
                    <div key={p.id} className="flex items-center space-x-4 p-3.5 rounded-2xl bg-[#F8F3EB] border border-[#EADFCF]">
                      <div className="w-7 h-7 rounded-full bg-[#1B1A17] text-[#FFFDF9] flex items-center justify-center text-xs font-semibold shrink-0">
                        {idx + 1}
                      </div>
                      <div className="relative w-16 h-18 rounded-xl overflow-hidden bg-[#F2EBE2] shrink-0 border border-[#E4D7C5]">
                        <Image src={p.image} alt={p.name} fill className="object-cover" />
                      </div>
                      <div className="flex-1 min-w-0">
                        <span className="text-[10px] uppercase font-semibold text-[#7D9075]">{p.category}</span>
                        <Link href={`/product/${p.id}`} className="hover:text-[#C8A46A]">
                          <h4 className="font-serif-luxury text-base font-semibold text-[#1B1A17] truncate">{p.name}</h4>
                        </Link>
                        <p className="text-[11px] text-[#5E584F] line-clamp-1">{p.tagline}</p>
                      </div>
                      <span className="font-serif-luxury text-sm font-semibold text-[#1B1A17]">${p.price}</span>
                    </div>
                  ))}
                </div>
              </div>

              {/* Evening Ritual */}
              <div className="bg-[#FFFDF9] p-8 rounded-3xl border border-[#EFE3D3] shadow-card space-y-6">
                <div className="flex items-center space-x-2.5 pb-4 border-b border-[#EADFCF] text-[#7D9075]">
                  <Moon className="w-5 h-5" />
                  <h3 className="font-serif-luxury text-2xl text-[#1B1A17]">Nocturnal Metamorphosis Ritual</h3>
                </div>

                <div className="space-y-4">
                  {eveningProducts.map((p, idx) => (
                    <div key={p.id} className="flex items-center space-x-4 p-3.5 rounded-2xl bg-[#F8F3EB] border border-[#EADFCF]">
                      <div className="w-7 h-7 rounded-full bg-[#7D9075] text-[#FFFDF9] flex items-center justify-center text-xs font-semibold shrink-0">
                        {idx + 1}
                      </div>
                      <div className="relative w-16 h-18 rounded-xl overflow-hidden bg-[#F2EBE2] shrink-0 border border-[#E4D7C5]">
                        <Image src={p.image} alt={p.name} fill className="object-cover" />
                      </div>
                      <div className="flex-1 min-w-0">
                        <span className="text-[10px] uppercase font-semibold text-[#7D9075]">{p.category}</span>
                        <Link href={`/product/${p.id}`} className="hover:text-[#C8A46A]">
                          <h4 className="font-serif-luxury text-base font-semibold text-[#1B1A17] truncate">{p.name}</h4>
                        </Link>
                        <p className="text-[11px] text-[#5E584F] line-clamp-1">{p.tagline}</p>
                      </div>
                      <span className="font-serif-luxury text-sm font-semibold text-[#1B1A17]">${p.price}</span>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
