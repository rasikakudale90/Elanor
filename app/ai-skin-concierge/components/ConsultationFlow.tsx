'use client';

import React, { useRef, useEffect } from 'react';
import gsap from 'gsap';
import {
  SkinType,
  SkinConcern,
  SkinContext,
  RitualPace,
  TexturePreference,
  ConsultationAnswers,
} from '../types';
import { ArrowRight, ArrowLeft, Check, Sparkles } from 'lucide-react';

interface ConsultationFlowProps {
  step: number;
  answers: ConsultationAnswers;
  onUpdateAnswers: (updated: Partial<ConsultationAnswers>) => void;
  onNext: () => void;
  onPrev: () => void;
  onSubmit: () => void;
}

export default function ConsultationFlow({
  step,
  answers,
  onUpdateAnswers,
  onNext,
  onPrev,
  onSubmit,
}: ConsultationFlowProps) {
  const cardRef = useRef<HTMLDivElement>(null);

  // Smooth directional step entrance transition
  useEffect(() => {
    const prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    if (prefersReducedMotion || !cardRef.current) return;

    gsap.fromTo(
      cardRef.current,
      { opacity: 0, y: 16 },
      { opacity: 1, y: 0, duration: 0.55, ease: 'power2.out' }
    );
  }, [step]);

  const toggleConcern = (concern: SkinConcern) => {
    const current = [...answers.concerns];
    if (current.includes(concern)) {
      if (current.length > 1) {
        onUpdateAnswers({ concerns: current.filter((c) => c !== concern) });
      }
    } else {
      if (current.length < 3) {
        onUpdateAnswers({ concerns: [...current, concern] });
      }
    }
  };

  const isStepValid = () => {
    if (step === 1) return !!answers.skinType;
    if (step === 2) return answers.concerns.length > 0;
    if (step === 3) return !!answers.skinContext;
    if (step === 4) return !!answers.ritualPace && !!answers.texturePreference;
    return true;
  };

  return (
    <div className="max-w-3xl w-full mx-auto py-8 px-4 sm:px-6">
      {/* Progress & Header */}
      <div className="mb-8 space-y-3">
        <div className="flex items-center justify-between text-xs tracking-widest uppercase font-semibold text-[#8E857A]">
          <button
            onClick={onPrev}
            className="flex items-center space-x-2 text-[#5E584F] hover:text-[#1B1A17] transition-colors py-1 cursor-pointer"
          >
            <ArrowLeft className="w-4 h-4" />
            <span>{step === 1 ? 'Back to Intro' : 'Previous Step'}</span>
          </button>
          <span>Step 0{step} / 04</span>
        </div>

        {/* Animated Progress Bar */}
        <div className="w-full h-1 bg-[#EADFCF] rounded-full overflow-hidden">
          <div
            className="h-full bg-[#1B1A17] transition-all duration-500 ease-out"
            style={{ width: `${(step / 4) * 100}%` }}
          />
        </div>
      </div>

      {/* Main Question Card */}
      <div
        ref={cardRef}
        className="bg-[#FFFDF9] rounded-3xl p-6 sm:p-10 border border-[#EFE3D3] shadow-card space-y-8"
      >
        {/* Step 1: Skin Type */}
        {step === 1 && (
          <div className="space-y-6">
            <div className="space-y-2">
              <span className="text-[11px] font-semibold uppercase tracking-[0.2em] text-[#7D9075]">
                Baseline Phenotype
              </span>
              <h2 className="font-serif-luxury text-3xl sm:text-4xl text-[#1B1A17]">
                How does your skin usually feel by midday?
              </h2>
              <p className="text-xs sm:text-sm text-[#5E584F]">
                Select the primary sensation that best characterizes your natural dermal barrier.
              </p>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3.5">
              {(
                [
                  {
                    id: 'Comfortable',
                    title: 'Comfortable & Balanced',
                    desc: 'Neither excessively dry nor oily; retains natural hydration cushion.',
                  },
                  {
                    id: 'Dry / Tight',
                    title: 'Dry & Tight',
                    desc: 'Prone to tightness, fine flaking, and requires immediate post-wash cream.',
                  },
                  {
                    id: 'Oily / Shiny',
                    title: 'Oily & Sebum-Rich',
                    desc: 'Persistent midday shine across forehead, nose, and cheeks.',
                  },
                  {
                    id: 'Combination',
                    title: 'Combination',
                    desc: 'Midday shine in T-zone with balanced or dry cheeks.',
                  },
                  {
                    id: 'Sensitive / Reactive',
                    title: 'Sensitive & Reactive',
                    desc: 'Prone to sudden flushing, irritation, or burning sensations.',
                  },
                ] as { id: SkinType; title: string; desc: string }[]
              ).map((item) => {
                const isSelected = answers.skinType === item.id;
                return (
                  <button
                    key={item.id}
                    onClick={() => onUpdateAnswers({ skinType: item.id })}
                    className={`p-5 rounded-2xl text-left border transition-all duration-200 cursor-pointer flex flex-col justify-between ${
                      isSelected
                        ? 'bg-[#F8F3EB] border-[#C8A46A] ring-1 ring-[#C8A46A] shadow-sm'
                        : 'bg-[#FFFDF9] border-[#EADFCF] hover:bg-[#FDFBF8] hover:border-[#D8C9B7]'
                    }`}
                  >
                    <div>
                      <div className="flex items-center justify-between">
                        <span className="font-serif-luxury text-lg text-[#1B1A17] font-semibold">
                          {item.title}
                        </span>
                        {isSelected && (
                          <span className="w-5 h-5 rounded-full bg-[#1B1A17] text-[#FFFDF9] flex items-center justify-center">
                            <Check className="w-3 h-3" />
                          </span>
                        )}
                      </div>
                      <p className="text-xs text-[#5E584F] mt-2 leading-relaxed">{item.desc}</p>
                    </div>
                  </button>
                );
              })}
            </div>
          </div>
        )}

        {/* Step 2: Primary Concerns */}
        {step === 2 && (
          <div className="space-y-6">
            <div className="space-y-2">
              <div className="flex items-center justify-between">
                <span className="text-[11px] font-semibold uppercase tracking-[0.2em] text-[#7D9075]">
                  Dermal Priorities
                </span>
                <span className="text-xs text-[#8E857A]">Select 1 to 3 priorities</span>
              </div>
              <h2 className="font-serif-luxury text-3xl sm:text-4xl text-[#1B1A17]">
                What are your primary skin goals?
              </h2>
              <p className="text-xs sm:text-sm text-[#5E584F]">
                Our clinical phytomolecules target specific biological pathways to restore vitality.
              </p>
            </div>

            <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
              {(
                [
                  { id: 'Radiance', label: 'Cellular Radiance' },
                  { id: 'Barrier Restoration', label: 'Barrier Rebuild' },
                  { id: 'Deep Quenching', label: 'Deep Quenching' },
                  { id: 'Firming', label: 'Firming & Bounce' },
                  { id: 'Calming', label: 'Calming & Redness' },
                  { id: 'Pore Clarifying', label: 'Pore Clarifying' },
                  { id: 'Uneven Tone', label: 'Uneven Tone' },
                  { id: 'Texture', label: 'Texture Smoothing' },
                ] as { id: SkinConcern; label: string }[]
              ).map((item) => {
                const isSelected = answers.concerns.includes(item.id);
                return (
                  <button
                    key={item.id}
                    onClick={() => toggleConcern(item.id)}
                    className={`p-4 rounded-2xl text-center border transition-all duration-200 cursor-pointer flex flex-col items-center justify-center min-h-[90px] ${
                      isSelected
                        ? 'bg-[#1B1A17] text-[#FFFDF9] border-[#1B1A17] shadow-sm'
                        : 'bg-[#FFFDF9] text-[#1B1A17] border-[#EADFCF] hover:bg-[#F8F3EB]'
                    }`}
                  >
                    <span className="text-xs font-semibold tracking-wide">{item.label}</span>
                    {isSelected && (
                      <span className="text-[10px] text-[#E4C894] mt-1 font-medium">Selected</span>
                    )}
                  </button>
                );
              })}
            </div>
          </div>
        )}

        {/* Step 3: Skin Context / Lifestyle */}
        {step === 3 && (
          <div className="space-y-6">
            <div className="space-y-2">
              <span className="text-[11px] font-semibold uppercase tracking-[0.2em] text-[#7D9075]">
                Environmental Stressors
              </span>
              <h2 className="font-serif-luxury text-3xl sm:text-4xl text-[#1B1A17]">
                What environment do you spend most time in?
              </h2>
              <p className="text-xs sm:text-sm text-[#5E584F]">
                Micro-climates and UV exposure directly govern trans-epidermal moisture loss.
              </p>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3.5">
              {(
                [
                  {
                    id: 'Air-Conditioned / Indoor',
                    title: 'Indoor Climate Controlled',
                    desc: 'Constant air conditioning or central heating causing subtle barrier dehydration.',
                  },
                  {
                    id: 'Urban Pollution / Commuting',
                    title: 'Urban Metropolises',
                    desc: 'High particulate matter, exhaust, and heavy environmental oxidants.',
                  },
                  {
                    id: 'Dry / Alpine / Cold',
                    title: 'Alpine / Arid Cold',
                    desc: 'Low humidity, biting winds, requiring deep lipid cushion.',
                  },
                  {
                    id: 'High Humidity / Tropical',
                    title: 'Tropical & Humid',
                    desc: 'High ambient humidity with sebum activation and sweat.',
                  },
                  {
                    id: 'Frequent Sun Exposure',
                    title: 'Active Sun & Outdoors',
                    desc: 'Elevated UV index requiring intensive antioxidant defense.',
                  },
                ] as { id: SkinContext; title: string; desc: string }[]
              ).map((item) => {
                const isSelected = answers.skinContext === item.id;
                return (
                  <button
                    key={item.id}
                    onClick={() => onUpdateAnswers({ skinContext: item.id })}
                    className={`p-5 rounded-2xl text-left border transition-all duration-200 cursor-pointer ${
                      isSelected
                        ? 'bg-[#F8F3EB] border-[#C8A46A] ring-1 ring-[#C8A46A] shadow-sm'
                        : 'bg-[#FFFDF9] border-[#EADFCF] hover:bg-[#FDFBF8]'
                    }`}
                  >
                    <div className="flex items-center justify-between">
                      <span className="font-serif-luxury text-lg text-[#1B1A17] font-semibold">
                        {item.title}
                      </span>
                      {isSelected && (
                        <span className="w-5 h-5 rounded-full bg-[#1B1A17] text-[#FFFDF9] flex items-center justify-center">
                          <Check className="w-3 h-3" />
                        </span>
                      )}
                    </div>
                    <p className="text-xs text-[#5E584F] mt-2 leading-relaxed">{item.desc}</p>
                  </button>
                );
              })}
            </div>
          </div>
        )}

        {/* Step 4: Ritual & Texture Preferences */}
        {step === 4 && (
          <div className="space-y-8">
            <div className="space-y-2">
              <span className="text-[11px] font-semibold uppercase tracking-[0.2em] text-[#7D9075]">
                Sensory & Ritual Preference
              </span>
              <h2 className="font-serif-luxury text-3xl sm:text-4xl text-[#1B1A17]">
                How would you like to experience your daily ritual?
              </h2>
              <p className="text-xs sm:text-sm text-[#5E584F]">
                Configure the pace and sensory texture that brings you joy every morning and evening.
              </p>
            </div>

            {/* Ritual Pace Sub-section */}
            <div className="space-y-3">
              <label className="text-xs font-semibold uppercase tracking-wider text-[#8E857A]">
                1. Ritual Depth &amp; Number of Steps
              </label>
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
                {(
                  [
                    {
                      id: 'Minimal',
                      title: 'Minimalist',
                      steps: '2-3 Core Essentials',
                    },
                    {
                      id: 'Balanced',
                      title: 'Balanced & Targeted',
                      steps: '3-4 Synergistic Steps',
                    },
                    {
                      id: 'Complete',
                      title: 'Haute Bespoke',
                      steps: '4-5 Comprehensive Steps',
                    },
                  ] as { id: RitualPace; title: string; steps: string }[]
                ).map((pace) => {
                  const isSelected = answers.ritualPace === pace.id;
                  return (
                    <button
                      key={pace.id}
                      onClick={() => onUpdateAnswers({ ritualPace: pace.id })}
                      className={`p-4 rounded-2xl text-left border transition-all duration-200 cursor-pointer ${
                        isSelected
                          ? 'bg-[#1B1A17] text-[#FFFDF9] border-[#1B1A17] shadow-sm'
                          : 'bg-[#FFFDF9] text-[#1B1A17] border-[#EADFCF] hover:bg-[#F8F3EB]'
                      }`}
                    >
                      <p className="font-serif-luxury text-lg font-semibold">{pace.title}</p>
                      <p
                        className={`text-xs mt-1 ${
                          isSelected ? 'text-[#E4C894]' : 'text-[#8E857A]'
                        }`}
                      >
                        {pace.steps}
                      </p>
                    </button>
                  );
                })}
              </div>
            </div>

            {/* Texture Preference Sub-section */}
            <div className="space-y-3">
              <label className="text-xs font-semibold uppercase tracking-wider text-[#8E857A]">
                2. Preferred Formulation Texture
              </label>
              <div className="grid grid-cols-2 sm:grid-cols-3 gap-3">
                {(
                  [
                    'Lightweight',
                    'Creamy',
                    'Rich',
                    'Oil-based',
                    'No preference',
                  ] as TexturePreference[]
                ).map((tex) => {
                  const isSelected = answers.texturePreference === tex;
                  return (
                    <button
                      key={tex}
                      onClick={() => onUpdateAnswers({ texturePreference: tex })}
                      className={`p-3.5 rounded-2xl text-center border text-xs font-medium transition-all duration-200 cursor-pointer ${
                        isSelected
                          ? 'bg-[#F8F3EB] border-[#C8A46A] ring-1 ring-[#C8A46A] text-[#1B1A17] font-semibold'
                          : 'bg-[#FFFDF9] border-[#EADFCF] text-[#5E584F] hover:bg-[#FDFBF8]'
                      }`}
                    >
                      {tex}
                    </button>
                  );
                })}
              </div>
            </div>
          </div>
        )}

        {/* Action Controls */}
        <div className="pt-4 border-t border-[#EADFCF] flex items-center justify-between">
          <span className="text-xs text-[#8E857A]">
            Answers are securely analyzed for this session
          </span>

          <button
            onClick={() => {
              if (step < 4) {
                onNext();
              } else {
                onSubmit();
              }
            }}
            disabled={!isStepValid()}
            className={`px-8 py-4 rounded-full text-xs uppercase tracking-[0.2em] font-semibold flex items-center space-x-2 transition-all duration-300 cursor-pointer ${
              isStepValid()
                ? 'bg-[#1B1A17] text-[#FFFDF9] hover:bg-[#322F2A] hover:shadow-soft'
                : 'bg-[#EADFCF] text-[#8E857A] cursor-not-allowed opacity-60'
            }`}
          >
            <span>{step === 4 ? 'Formulate Bespoke Ritual' : 'Continue'}</span>
            <ArrowRight className="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>
  );
}
