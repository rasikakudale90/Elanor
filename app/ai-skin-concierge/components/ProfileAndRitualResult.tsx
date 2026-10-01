'use client';

import React, { useState, useEffect, useRef } from 'react';
import Image from 'next/image';
import Link from 'next/link';
import gsap from 'gsap';
import { ConciergeResult, RitualStep } from '../types';
import { useStore } from '@/context/StoreContext';
import {
  Sun,
  Moon,
  Sparkles,
  ShoppingBag,
  RotateCcw,
  Check,
  ChevronRight,
  ShieldAlert,
} from 'lucide-react';

interface ProfileAndRitualResultProps {
  result: ConciergeResult;
  onRestart: () => void;
}

export default function ProfileAndRitualResult({
  result,
  onRestart,
}: ProfileAndRitualResultProps) {
  const { addToCart } = useStore();
  const [activeTab, setActiveTab] = useState<'ALL' | 'AM' | 'PM'>('ALL');
  const [isAddedAll, setIsAddedAll] = useState(false);

  const containerRef = useRef<HTMLDivElement>(null);
  const headerRef = useRef<HTMLDivElement>(null);
  const metricsRef = useRef<HTMLDivElement>(null);
  const ritualSectionRef = useRef<HTMLDivElement>(null);

  // Animate metrics counter and layout reveal
  useEffect(() => {
    const prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    if (prefersReducedMotion) return;

    const ctx = gsap.context(() => {
      const tl = gsap.timeline({ defaults: { ease: 'power3.out' } });

      tl.fromTo(
        headerRef.current,
        { opacity: 0, y: 30 },
        { opacity: 1, y: 0, duration: 0.9 }
      )
        .fromTo(
          metricsRef.current,
          { opacity: 0, y: 20 },
          { opacity: 1, y: 0, duration: 0.8 },
          '-=0.5'
        )
        .fromTo(
          ritualSectionRef.current,
          { opacity: 0, y: 25 },
          { opacity: 1, y: 0, duration: 0.8 },
          '-=0.4'
        );
    }, containerRef);

    return () => ctx.revert();
  }, []);

  const handleAddAllToCart = () => {
    result.uniqueProducts.forEach((product) => {
      addToCart(product, 1);
    });
    setIsAddedAll(true);
    setTimeout(() => setIsAddedAll(false), 2500);
  };

  const { profile, morningRitual, eveningRitual, totalPrice, bundlePrice, uniqueProducts } =
    result;

  return (
    <div ref={containerRef} className="max-w-6xl w-full mx-auto py-8 sm:py-12 px-4 sm:px-8 space-y-12">
      {/* Top Banner & Title */}
      <div ref={headerRef} className="text-center space-y-4 max-w-3xl mx-auto">
        <div className="inline-flex items-center space-x-2 px-4 py-1.5 rounded-full bg-[#FFFDF9] border border-[#E4D7C5] shadow-sm text-xs font-semibold text-[#7D9075]">
          <Sparkles className="w-3.5 h-3.5 text-[#C8A46A]" />
          <span className="uppercase tracking-[0.2em] text-[10px]">
            Personalized Skin Prescription
          </span>
        </div>

        <h1 className="font-serif-luxury text-4xl sm:text-5xl lg:text-6xl text-[#1B1A17] font-normal leading-tight">
          {profile.archetype}
        </h1>

        <p className="text-xs uppercase tracking-[0.25em] text-[#8E857A] font-medium">
          {profile.frenchTitle}
        </p>

        <p className="text-xs sm:text-sm text-[#5E584F] leading-relaxed max-w-xl mx-auto font-light">
          {profile.summary}
        </p>
      </div>

      {/* 1. Skin Profile Balance Indicators */}
      <div
        ref={metricsRef}
        className="bg-[#FFFDF9] rounded-3xl p-6 sm:p-10 border border-[#EFE3D3] shadow-card space-y-6"
      >
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 pb-4 border-b border-[#EADFCF]">
          <div>
            <h3 className="font-serif-luxury text-2xl text-[#1B1A17]">
              Dermal Vitality Matrix
            </h3>
            <p className="text-xs text-[#8E857A]">
              Personalization indicators calibrated for botanical ritual synergy.
            </p>
          </div>

          <div className="text-xs font-medium text-[#7D9075] bg-[#F2EBE2] px-3 py-1.5 rounded-full self-start sm:self-auto">
            {profile.matchScore}% Formulation Match
          </div>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
          {profile.metrics.map((metric) => (
            <div
              key={metric.name}
              className="p-5 rounded-2xl bg-[#F8F3EB] border border-[#EADFCF]/80 space-y-3"
            >
              <div className="flex justify-between items-baseline">
                <div>
                  <p className="text-xs font-semibold text-[#1B1A17]">{metric.name}</p>
                  <p className="text-[10px] text-[#8E857A] uppercase tracking-wider">
                    {metric.frenchName}
                  </p>
                </div>
                <span className="font-serif-luxury text-2xl font-bold text-[#1B1A17]">
                  {metric.value}%
                </span>
              </div>

              {/* Meter bar */}
              <div className="w-full h-1.5 bg-[#EADFCF] rounded-full overflow-hidden">
                <div
                  className="h-full rounded-full transition-all duration-1000 ease-out"
                  style={{
                    width: `${metric.value}%`,
                    backgroundColor: metric.color,
                  }}
                />
              </div>
            </div>
          ))}
        </div>

        <div className="flex items-start space-x-2 text-[11px] text-[#8E857A] pt-2">
          <ShieldAlert className="w-4 h-4 shrink-0 text-[#8E857A] mt-0.5" />
          <span>
            Indicators represent algorithmic cosmetic formulation suggestions and are not medical diagnoses.
          </span>
        </div>
      </div>

      {/* 2. Morning & Evening Ritual Architecture */}
      <div ref={ritualSectionRef} className="space-y-8">
        {/* Navigation Tabs */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div>
            <h2 className="font-serif-luxury text-3xl sm:text-4xl text-[#1B1A17]">
              Your Bespoke Daily Regimen
            </h2>
            <p className="text-xs text-[#5E584F] mt-1">
              Engineered in precise pH-compatible order for cellular absorption.
            </p>
          </div>

          <div className="inline-flex p-1.5 bg-[#FFFDF9] rounded-full border border-[#EADFCF] shadow-sm self-start sm:self-auto">
            <button
              onClick={() => setActiveTab('ALL')}
              className={`px-4 py-2 rounded-full text-xs font-semibold tracking-wider transition-all duration-200 cursor-pointer ${
                activeTab === 'ALL'
                  ? 'bg-[#1B1A17] text-[#FFFDF9] shadow-sm'
                  : 'text-[#5E584F] hover:text-[#1B1A17]'
              }`}
            >
              Full Protocol
            </button>
            <button
              onClick={() => setActiveTab('AM')}
              className={`px-4 py-2 rounded-full text-xs font-semibold tracking-wider transition-all duration-200 flex items-center space-x-1.5 cursor-pointer ${
                activeTab === 'AM'
                  ? 'bg-[#1B1A17] text-[#FFFDF9] shadow-sm'
                  : 'text-[#5E584F] hover:text-[#1B1A17]'
              }`}
            >
              <Sun className="w-3.5 h-3.5 text-[#C8A46A]" />
              <span>Morning (AM)</span>
            </button>
            <button
              onClick={() => setActiveTab('PM')}
              className={`px-4 py-2 rounded-full text-xs font-semibold tracking-wider transition-all duration-200 flex items-center space-x-1.5 cursor-pointer ${
                activeTab === 'PM'
                  ? 'bg-[#1B1A17] text-[#FFFDF9] shadow-sm'
                  : 'text-[#5E584F] hover:text-[#1B1A17]'
              }`}
            >
              <Moon className="w-3.5 h-3.5 text-[#6F8FAF]" />
              <span>Evening (PM)</span>
            </button>
          </div>
        </div>

        {/* Morning Ritual View */}
        {(activeTab === 'ALL' || activeTab === 'AM') && (
          <div className="space-y-4">
            <div className="flex items-center space-x-2 text-xs font-semibold uppercase tracking-widest text-[#C8A46A] pt-4">
              <Sun className="w-4 h-4" />
              <span>Morning Ritual • Cellular Awakening &amp; Protection</span>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {morningRitual.map((step) => (
                <RitualStepCard key={`am-${step.stepNumber}-${step.product.id}`} step={step} />
              ))}
            </div>
          </div>
        )}

        {/* Evening Ritual View */}
        {(activeTab === 'ALL' || activeTab === 'PM') && (
          <div className="space-y-4 pt-6">
            <div className="flex items-center space-x-2 text-xs font-semibold uppercase tracking-widest text-[#6F8FAF] pt-4">
              <Moon className="w-4 h-4" />
              <span>Evening Ritual • Nocturnal Cellular Metamorphosis</span>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {eveningRitual.map((step) => (
                <RitualStepCard key={`pm-${step.stepNumber}-${step.product.id}`} step={step} />
              ))}
            </div>
          </div>
        )}
      </div>

      {/* 3. Sticky / Elevated "Add Entire Ritual to Bag" Bar */}
      <div className="sticky bottom-6 z-30 bg-[#1B1A17] text-[#FFFDF9] rounded-3xl p-6 sm:p-8 border border-[#3E3A33] shadow-floating flex flex-col md:flex-row items-center justify-between gap-6">
        <div className="space-y-1 text-center md:text-left">
          <div className="flex items-center justify-center md:justify-start space-x-2">
            <span className="font-serif-luxury text-2xl sm:text-3xl text-[#FFFDF9]">
              Complete Bespoke Ritual ({uniqueProducts.length} Formulations)
            </span>
            <span className="text-[10px] uppercase font-bold tracking-widest bg-[#C8A46A] text-[#1B1A17] px-2 py-0.5 rounded-full">
              Save 15%
            </span>
          </div>
          <p className="text-xs text-[#8E857A]">
            Includes complimentary bespoke discovery samples and luxury gold gift packaging.
          </p>
        </div>

        <div className="flex flex-col sm:flex-row items-center space-y-3 sm:space-y-0 sm:space-x-6 w-full md:w-auto">
          <div className="text-center sm:text-right">
            <div className="flex items-baseline justify-center sm:justify-end space-x-2">
              <span className="text-xs line-through text-[#8E857A]">${totalPrice}</span>
              <span className="font-serif-luxury text-3xl font-bold text-[#E4C894]">
                ${bundlePrice}
              </span>
            </div>
            <span className="text-[10px] text-[#7D9075] uppercase tracking-wider font-semibold">
              Free Global Insured Delivery
            </span>
          </div>

          <div className="flex items-center space-x-3 w-full sm:w-auto">
            <button
              onClick={handleAddAllToCart}
              className="flex-1 sm:flex-none px-8 py-4 bg-[#C8A46A] text-[#1B1A17] rounded-full text-xs uppercase tracking-[0.2em] font-semibold hover:bg-[#E4C894] transition-all duration-300 shadow-md flex items-center justify-center space-x-2 cursor-pointer"
            >
              {isAddedAll ? (
                <>
                  <Check className="w-4 h-4 text-[#1B1A17]" />
                  <span>Ritual Added to Bag</span>
                </>
              ) : (
                <>
                  <ShoppingBag className="w-4 h-4" />
                  <span>Add Entire Ritual to Bag</span>
                </>
              )}
            </button>

            <button
              onClick={onRestart}
              title="Retake Consultation"
              className="p-4 rounded-full border border-[#484239] text-[#FFFDF9] hover:bg-[#262420] transition-colors cursor-pointer"
            >
              <RotateCcw className="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

function RitualStepCard({ step }: { step: RitualStep }) {
  const { addToCart } = useStore();
  const [isAdded, setIsAdded] = useState(false);

  const handleAdd = () => {
    addToCart(step.product, 1);
    setIsAdded(true);
    setTimeout(() => setIsAdded(false), 2000);
  };

  return (
    <div className="bg-[#FFFDF9] rounded-3xl p-6 border border-[#EADFCF] hover:border-[#C8A46A] transition-all duration-300 hover:shadow-card flex flex-col justify-between group">
      <div>
        {/* Step Header */}
        <div className="flex items-center justify-between pb-3 border-b border-[#EFE3D3]">
          <span className="text-[10px] tracking-widest uppercase font-semibold text-[#8E857A]">
            Step {step.stepNumber} • {step.phase}
          </span>
          <span className="text-xs font-semibold text-[#1B1A17]">
            ${step.product.price}
          </span>
        </div>

        {/* Product Image & Info */}
        <div className="mt-4 flex space-x-4">
          <div className="relative w-20 h-24 rounded-xl overflow-hidden bg-[#F8F3EB] shrink-0 border border-[#EADFCF]">
            <Image
              src={step.product.image}
              alt={step.product.name}
              fill
              className="object-cover group-hover:scale-105 transition-transform duration-500"
              sizes="80px"
            />
          </div>

          <div className="space-y-1">
            <Link
              href={`/product/${step.product.id}`}
              className="font-serif-luxury text-lg text-[#1B1A17] hover:text-[#C8A46A] transition-colors leading-snug line-clamp-1 block"
            >
              {step.product.name}
            </Link>
            <p className="text-[10px] text-[#8E857A] italic line-clamp-1">
              {step.product.frenchSubtitle}
            </p>
            <p className="text-[11px] text-[#5E584F] line-clamp-2 mt-1">
              {step.product.tagline}
            </p>
          </div>
        </div>

        {/* Why It's Here */}
        <div className="mt-4 p-3.5 bg-[#F8F3EB] rounded-2xl space-y-1.5">
          <p className="text-[10px] uppercase tracking-wider font-semibold text-[#7D9075]">
            Why It&apos;s In Your Ritual
          </p>
          <ul className="space-y-1 text-[11px] text-[#5E584F]">
            {step.whyItsHere.map((reason, i) => (
              <li key={i} className="flex items-start space-x-1.5">
                <span className="text-[#C8A46A]">•</span>
                <span>{reason}</span>
              </li>
            ))}
          </ul>
        </div>
      </div>

      {/* Card Actions */}
      <div className="mt-5 pt-3 border-t border-[#EFE3D3] flex items-center justify-between">
        <Link
          href={`/product/${step.product.id}`}
          className="text-xs font-semibold text-[#1B1A17] hover:text-[#C8A46A] flex items-center space-x-1"
        >
          <span>View Details</span>
          <ChevronRight className="w-3.5 h-3.5" />
        </Link>

        <button
          onClick={handleAdd}
          className="px-4 py-2 bg-[#1B1A17] text-[#FFFDF9] rounded-full text-[11px] font-semibold uppercase tracking-wider hover:bg-[#322F2A] transition-colors flex items-center space-x-1.5 cursor-pointer"
        >
          {isAdded ? (
            <>
              <Check className="w-3 h-3 text-[#E4C894]" />
              <span>Added</span>
            </>
          ) : (
            <>
              <ShoppingBag className="w-3 h-3" />
              <span>Add</span>
            </>
          )}
        </button>
      </div>
    </div>
  );
}
