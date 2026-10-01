'use client';

import React, { useEffect, useRef } from 'react';
import Image from 'next/image';
import gsap from 'gsap';
import { Sparkles, ArrowRight, ShieldCheck, HeartHandshake } from 'lucide-react';

interface IntroScreenProps {
  onStart: () => void;
}

export default function IntroScreen({ onStart }: IntroScreenProps) {
  const containerRef = useRef<HTMLDivElement>(null);
  const badgeRef = useRef<HTMLDivElement>(null);
  const headlineRef = useRef<HTMLHeadingElement>(null);
  const subtextRef = useRef<HTMLParagraphElement>(null);
  const ctaRef = useRef<HTMLButtonElement>(null);
  const imageWrapperRef = useRef<HTMLDivElement>(null);
  const metaRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    if (prefersReducedMotion) return;

    const ctx = gsap.context(() => {
      const tl = gsap.timeline({ defaults: { ease: 'power3.out' } });

      tl.fromTo(
        imageWrapperRef.current,
        { scale: 1.08, opacity: 0 },
        { scale: 1, opacity: 1, duration: 1.4 }
      )
        .fromTo(
          badgeRef.current,
          { y: -20, opacity: 0 },
          { y: 0, opacity: 1, duration: 0.8 },
          '-=1.0'
        )
        .fromTo(
          headlineRef.current,
          { y: 30, opacity: 0 },
          { y: 0, opacity: 1, duration: 1.0 },
          '-=0.6'
        )
        .fromTo(
          subtextRef.current,
          { y: 20, opacity: 0 },
          { y: 0, opacity: 1, duration: 0.8 },
          '-=0.6'
        )
        .fromTo(
          ctaRef.current,
          { scale: 0.95, opacity: 0 },
          { scale: 1, opacity: 1, duration: 0.7 },
          '-=0.4'
        )
        .fromTo(
          metaRef.current,
          { opacity: 0 },
          { opacity: 1, duration: 0.8 },
          '-=0.3'
        );
    }, containerRef);

    return () => ctx.revert();
  }, []);

  return (
    <div
      ref={containerRef}
      className="min-h-[85vh] flex flex-col justify-center items-center relative overflow-hidden py-12 px-6 sm:px-12"
    >
      {/* Background Soft Glow */}
      <div className="absolute top-1/4 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[600px] h-[600px] bg-[#E4C894]/15 rounded-full blur-3xl pointer-events-none" />

      <div className="max-w-6xl w-full mx-auto grid grid-cols-1 lg:grid-cols-12 gap-12 lg:gap-16 items-center relative z-10">
        {/* Left Column: Editorial Headline & Consultation Invitation */}
        <div className="lg:col-span-7 space-y-8 text-left">
          <div
            ref={badgeRef}
            className="inline-flex items-center space-x-2.5 px-4 py-1.5 rounded-full bg-[#FFFDF9] border border-[#E4D7C5] shadow-sm text-xs font-semibold text-[#7D9075]"
          >
            <Sparkles className="w-3.5 h-3.5 text-[#C8A46A]" />
            <span className="uppercase tracking-[0.2em] text-[11px]">
              Élanor Haute Skin Concierge
            </span>
          </div>

          <div className="space-y-4">
            <h1
              ref={headlineRef}
              className="font-serif-luxury text-4xl sm:text-6xl lg:text-7xl text-[#1B1A17] font-normal leading-[1.06] tracking-tight"
            >
              Your skin has a story. <br />
              <span className="italic font-light text-[#8E857A]">
                Let&apos;s understand yours.
              </span>
            </h1>

            <p
              ref={subtextRef}
              className="text-sm sm:text-base text-[#5E584F] leading-relaxed max-w-xl font-light"
            >
              A private 2-minute clinical &amp; botanical consultation. Discover an exact morning
              and nocturnal sequence calibrated to your unique moisture barrier, environment, and cellular rhythm.
            </p>
          </div>

          <div className="pt-2 flex flex-col sm:flex-row items-start sm:items-center space-y-4 sm:space-y-0 sm:space-x-6">
            <button
              ref={ctaRef}
              onClick={onStart}
              className="w-full sm:w-auto px-10 py-5 bg-[#1B1A17] text-[#FFFDF9] rounded-full text-xs uppercase tracking-[0.2em] font-semibold hover:bg-[#322F2A] hover:shadow-floating transition-all duration-300 flex items-center justify-center space-x-3 group cursor-pointer"
            >
              <span>Begin Consultation</span>
              <ArrowRight className="w-4 h-4 group-hover:translate-x-1.5 transition-transform" />
            </button>

            <span className="text-xs text-[#8E857A] tracking-wider uppercase font-medium">
              4 Questions • 2 Minutes
            </span>
          </div>

          {/* Pillars */}
          <div
            ref={metaRef}
            className="pt-6 border-t border-[#EADFCF] grid grid-cols-2 sm:grid-cols-3 gap-6 text-xs text-[#5E584F]"
          >
            <div className="flex items-center space-x-2.5">
              <ShieldCheck className="w-4 h-4 text-[#7D9075]" />
              <span>Grounded in Real Formulations</span>
            </div>
            <div className="flex items-center space-x-2.5">
              <HeartHandshake className="w-4 h-4 text-[#C8A46A]" />
              <span>Zero Artificial Claims</span>
            </div>
            <div className="hidden sm:flex items-center space-x-2.5">
              <Sparkles className="w-4 h-4 text-[#6F8FAF]" />
              <span>Bespoke 15% Ritual Benefit</span>
            </div>
          </div>
        </div>

        {/* Right Column: Luxury Editorial Product Visual */}
        <div className="lg:col-span-5 flex justify-center">
          <div
            ref={imageWrapperRef}
            className="relative w-full max-w-[420px] aspect-[4/5] rounded-[36px] overflow-hidden shadow-floating border border-[#E4D7C5] bg-[#FFFDF9]"
          >
            <Image
              src="/images/skin.jfif"
              alt="Élanor Haute Botanical Formulation"
              fill
              priority
              className="object-cover transition-transform duration-700 hover:scale-105"
              sizes="(max-width: 768px) 100vw, 420px"
            />
            <div className="absolute inset-0 bg-gradient-to-t from-[#1B1A17]/70 via-transparent to-transparent" />

            <div className="absolute bottom-6 left-6 right-6 text-[#FFFDF9] space-y-1">
              <span className="text-[10px] tracking-[0.25em] uppercase text-[#E4C894] font-medium">
                Haute Botanique
              </span>
              <p className="font-serif-luxury text-xl leading-snug">
                Cellular Longevity &amp; Dermal Harmony
              </p>
              <p className="text-[11px] text-[#FDFBF8]/80">
                Formulated with lipid-identical bio-ceramides &amp; wild Alpine Edelweiss.
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
