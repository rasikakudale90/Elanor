'use client';

import React, { useEffect, useRef, useState } from 'react';
import Image from 'next/image';
import gsap from 'gsap';
import { ArrowRight, Leaf, User, Sparkles } from 'lucide-react';

interface IntroScreenProps {
  onStart: () => void;
}

export default function IntroScreen({ onStart }: IntroScreenProps) {
  const containerRef = useRef<HTMLDivElement>(null);
  const brandRef = useRef<HTMLDivElement>(null);
  const headlineLine1Ref = useRef<HTMLSpanElement>(null);
  const headlineLine2Ref = useRef<HTMLSpanElement>(null);
  const headlineLine3Ref = useRef<HTMLSpanElement>(null);
  const subtextRef = useRef<HTMLParagraphElement>(null);
  const ctaRef = useRef<HTMLButtonElement>(null);

  // Center model and bounding boxes
  const modelWrapperRef = useRef<HTMLDivElement>(null);
  const modelImageRef = useRef<HTMLDivElement>(null);
  const scanLineRef = useRef<HTMLDivElement>(null);
  const boxHydrationRef = useRef<HTMLDivElement>(null);
  const boxFineLinesRef = useRef<HTMLDivElement>(null);
  const boxTextureRef = useRef<HTMLDivElement>(null);

  // Right column metric cards
  const metricCard1Ref = useRef<HTMLDivElement>(null);
  const metricCard2Ref = useRef<HTMLDivElement>(null);
  const metricCard3Ref = useRef<HTMLDivElement>(null);

  const [hoveredBox, setHoveredBox] = useState<string | null>(null);

  // GSAP Entrance Timeline & Idle Animations
  useEffect(() => {
    const prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    if (prefersReducedMotion) return;

    const ctx = gsap.context(() => {
      const tl = gsap.timeline({ defaults: { ease: 'power3.out' } });

      // Initial state reveals
      tl.fromTo(
        brandRef.current,
        { y: -20, opacity: 0 },
        { y: 0, opacity: 1, duration: 0.8 }
      )
        .fromTo(
          [headlineLine1Ref.current, headlineLine2Ref.current, headlineLine3Ref.current],
          { y: 35, opacity: 0 },
          { y: 0, opacity: 1, duration: 0.9, stagger: 0.12 },
          '-=0.5'
        )
        .fromTo(
          subtextRef.current,
          { y: 20, opacity: 0 },
          { y: 0, opacity: 1, duration: 0.7 },
          '-=0.5'
        )
        .fromTo(
          ctaRef.current,
          { scale: 0.92, opacity: 0 },
          { scale: 1, opacity: 1, duration: 0.6, ease: 'back.out(1.5)' },
          '-=0.4'
        )
        .fromTo(
          modelImageRef.current,
          { scale: 1.08, opacity: 0 },
          { scale: 1, opacity: 1, duration: 1.2 },
          '-=1.2'
        )
        .fromTo(
          [boxHydrationRef.current, boxFineLinesRef.current, boxTextureRef.current],
          { scale: 0.75, opacity: 0 },
          { scale: 1, opacity: 1, duration: 0.8, stagger: 0.18, ease: 'back.out(1.6)' },
          '-=0.6'
        )
        .fromTo(
          [metricCard1Ref.current, metricCard2Ref.current, metricCard3Ref.current],
          { x: 35, opacity: 0 },
          { x: 0, opacity: 1, duration: 0.8, stagger: 0.15, ease: 'power2.out' },
          '-=0.7'
        );

      // Scanning Laser Beam Animation
      if (scanLineRef.current) {
        gsap.to(scanLineRef.current, {
          y: '400px',
          duration: 3.6,
          ease: 'power1.inOut',
          repeat: -1,
          yoyo: true,
        });
      }

      // Gentle Floating Effect for Bounding Target Boxes
      gsap.to(boxHydrationRef.current, {
        y: '-=5px',
        duration: 2.6,
        repeat: -1,
        yoyo: true,
        ease: 'sine.inOut',
      });
      gsap.to(boxFineLinesRef.current, {
        y: '+=6px',
        duration: 3.1,
        repeat: -1,
        yoyo: true,
        ease: 'sine.inOut',
        delay: 0.3,
      });
      gsap.to(boxTextureRef.current, {
        y: '-=4px',
        duration: 2.9,
        repeat: -1,
        yoyo: true,
        ease: 'sine.inOut',
        delay: 0.6,
      });
    }, containerRef);

    return () => ctx.revert();
  }, []);

  // 3D Parallax on Mouse Move
  const handleMouseMove = (e: React.MouseEvent<HTMLDivElement>) => {
    if (window.matchMedia('(prefers-reduced-motion: reduce)').matches || !modelWrapperRef.current)
      return;

    const rect = modelWrapperRef.current.getBoundingClientRect();
    const x = (e.clientX - rect.left) / rect.width - 0.5;
    const y = (e.clientY - rect.top) / rect.height - 0.5;

    gsap.to(modelImageRef.current, {
      x: x * 16,
      y: y * 16,
      duration: 0.7,
      ease: 'power1.out',
    });

    if (boxHydrationRef.current) {
      gsap.to(boxHydrationRef.current, { x: x * 26, y: y * 26, duration: 0.8 });
    }
    if (boxFineLinesRef.current) {
      gsap.to(boxFineLinesRef.current, { x: x * 22, y: y * 22, duration: 0.8 });
    }
    if (boxTextureRef.current) {
      gsap.to(boxTextureRef.current, { x: x * 28, y: y * 28, duration: 0.8 });
    }
  };

  const handleMouseLeave = () => {
    if (!modelImageRef.current) return;
    gsap.to(modelImageRef.current, { x: 0, y: 0, duration: 0.8, ease: 'power2.out' });
    if (boxHydrationRef.current) gsap.to(boxHydrationRef.current, { x: 0, duration: 0.8 });
    if (boxFineLinesRef.current) gsap.to(boxFineLinesRef.current, { x: 0, duration: 0.8 });
    if (boxTextureRef.current) gsap.to(boxTextureRef.current, { x: 0, duration: 0.8 });
  };

  return (
    <div
      ref={containerRef}
      onMouseMove={handleMouseMove}
      onMouseLeave={handleMouseLeave}
      className="min-h-[88vh] flex items-center justify-center relative overflow-hidden bg-[#FAF7F2] py-8 sm:py-12 px-6 sm:px-12 lg:px-16"
    >
      {/* Background Ambience */}
      <div className="absolute top-1/3 left-1/4 -translate-x-1/2 -translate-y-1/2 w-[650px] h-[650px] bg-[#EFE3D3]/40 rounded-full blur-3xl pointer-events-none" />
      <div className="absolute bottom-10 right-1/4 w-[450px] h-[450px] bg-[#E4C894]/15 rounded-full blur-3xl pointer-events-none" />

      {/* Main Luxury 3-Column Grid */}
      <div className="max-w-[1400px] w-full mx-auto grid grid-cols-1 lg:grid-cols-12 gap-8 lg:gap-8 items-center relative z-10">
        {/* ================= LEFT COLUMN: Brand & Invitation ================= */}
        <div className="lg:col-span-4 space-y-7 text-left">
          {/* Brand Mark with Botanical Leaf Emblem */}
          <div ref={brandRef} className="space-y-1">
            <div className="flex items-center space-x-2">
              <span className="font-serif-luxury text-2xl sm:text-3xl tracking-[0.22em] text-[#1B1A17] uppercase font-medium">
                É L A N O R
              </span>
              {/* Botanical Leaf SVG Emblem */}
              <svg
                className="w-5 h-5 text-[#9E5D46]"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="1.6"
                strokeLinecap="round"
                strokeLinejoin="round"
              >
                <path d="M11 20A7 7 0 0 1 9.8 6.1C15.5 5 17 4.48 19 2c1 2 2 4.18 2 8 0 5.5-4.78 10-10 10Z" />
                <path d="M2 21c0-3 1.85-5.36 5.08-6C9.5 14.52 12 13 13 12" />
              </svg>
            </div>
            <p className="text-[10px] uppercase tracking-[0.32em] text-[#8E857A] font-semibold">
              A I &nbsp; S K I N &nbsp; C O N C I E R G E
            </p>
          </div>

          {/* Editorial Headline */}
          <div className="space-y-1">
            <h1 className="font-serif-luxury text-4xl sm:text-5xl lg:text-6xl text-[#1B1A17] font-normal leading-[1.04] tracking-tight">
              <span ref={headlineLine1Ref} className="block">
                Understand
              </span>
              <span
                ref={headlineLine2Ref}
                className="block italic font-light text-[#9E5D46] tracking-tight"
              >
                your skin&apos;s
              </span>
              <span ref={headlineLine3Ref} className="block">
                true potential
              </span>
            </h1>
          </div>

          {/* Subtitle */}
          <p
            ref={subtextRef}
            className="text-xs sm:text-sm text-[#5E584F] leading-relaxed max-w-sm font-light"
          >
            Our AI Skin Concierge analyzes your skin and reveals a personalized Élanor ritual, made for you.
          </p>

          {/* Primary Terracotta CTA Button */}
          <div className="pt-1">
            <button
              ref={ctaRef}
              onClick={onStart}
              className="w-full sm:w-auto px-9 py-4 bg-[#9E5D46] text-[#FFFDF9] rounded-2xl text-xs uppercase tracking-[0.2em] font-semibold hover:bg-[#884B35] hover:shadow-floating transition-all duration-300 flex items-center justify-center space-x-3 group cursor-pointer"
            >
              <span>Start Analysis</span>
              <ArrowRight className="w-4 h-4 group-hover:translate-x-1.5 transition-transform" />
            </button>
          </div>
        </div>

        {/* ================= CENTER COLUMN: Model with Targeted Bounding Boxes ================= */}
        <div
          ref={modelWrapperRef}
          className="lg:col-span-5 flex justify-center items-center relative select-none"
        >
          <div className="relative w-full max-w-[460px] aspect-[4/4.8] rounded-[36px] overflow-hidden">
            {/* The Model Portrait Image from skin9.png */}
            <div ref={modelImageRef} className="absolute inset-0 w-full h-full">
              <Image
                src="/images/skin9.png"
                alt="Élanor AI Skin Concierge - Personalized Analysis"
                fill
                priority
                className="object-cover object-center"
                sizes="(max-width: 768px) 100vw, 460px"
              />
            </div>

            {/* Soft Bottom Gradient Fade */}
            <div className="absolute inset-x-0 bottom-0 h-28 bg-gradient-to-t from-[#FAF7F2] via-[#FAF7F2]/60 to-transparent pointer-events-none" />

            {/* Continuous Laser Scanning Line Sweep */}
            <div
              ref={scanLineRef}
              className="absolute top-0 inset-x-0 h-0.5 bg-gradient-to-r from-transparent via-[#9E5D46] to-transparent shadow-[0_0_12px_#9E5D46] opacity-75 pointer-events-none z-20"
            />

            {/* TARGET BOUNDING BOX 1: Forehead (Hydration Level) */}
            <div
              ref={boxHydrationRef}
              onMouseEnter={() => setHoveredBox('hydration')}
              onMouseLeave={() => setHoveredBox(null)}
              className="absolute top-[18%] left-[53%] w-[78px] h-[78px] border border-white/90 rounded-sm z-30 transition-all duration-300 hover:border-[#9E5D46] hover:bg-white/10 cursor-pointer"
            >
              {/* Top-left solid white marker */}
              <div className="absolute top-1 left-1 w-1.5 h-1.5 bg-white" />
              <div className="absolute bottom-2 left-2 text-[10px] text-white font-medium tracking-wide leading-tight drop-shadow-md">
                Hydration
                <br />
                Level
              </div>
              {hoveredBox === 'hydration' && (
                <div className="absolute -top-7 left-0 bg-[#1B1A17]/90 backdrop-blur-md text-[#FFFDF9] text-[9px] px-2 py-0.5 rounded shadow-card whitespace-nowrap">
                  94.2% Optimal
                </div>
              )}
            </div>

            {/* TARGET BOUNDING BOX 2: Eye / Periorbital (Fine Lines) */}
            <div
              ref={boxFineLinesRef}
              onMouseEnter={() => setHoveredBox('lines')}
              onMouseLeave={() => setHoveredBox(null)}
              className="absolute top-[37%] left-[54%] w-[84px] h-[72px] border border-white/90 rounded-sm z-30 transition-all duration-300 hover:border-[#9E5D46] hover:bg-white/10 cursor-pointer"
            >
              {/* Top-left solid white marker */}
              <div className="absolute top-1 left-1 w-1.5 h-1.5 bg-white" />
              <div className="absolute bottom-2 left-2 text-[10px] text-white font-medium tracking-wide leading-tight drop-shadow-md">
                Fine
                <br />
                Lines
              </div>
              {hoveredBox === 'lines' && (
                <div className="absolute -top-7 left-0 bg-[#1B1A17]/90 backdrop-blur-md text-[#FFFDF9] text-[9px] px-2 py-0.5 rounded shadow-card whitespace-nowrap">
                  Tri-Peptide Restored
                </div>
              )}
            </div>

            {/* TARGET BOUNDING BOX 3: Cheek (Skin Texture) */}
            <div
              ref={boxTextureRef}
              onMouseEnter={() => setHoveredBox('texture')}
              onMouseLeave={() => setHoveredBox(null)}
              className="absolute top-[58%] left-[40%] w-[72px] h-[64px] border border-white/90 rounded-sm z-30 transition-all duration-300 hover:border-[#9E5D46] hover:bg-white/10 cursor-pointer"
            >
              {/* Top-left solid white marker */}
              <div className="absolute top-1 left-1 w-1.5 h-1.5 bg-white" />
              <div className="absolute bottom-2 left-2 text-[10px] text-white font-medium tracking-wide leading-tight drop-shadow-md">
                Skin
                <br />
                Texture
              </div>
              {hoveredBox === 'texture' && (
                <div className="absolute -top-7 left-0 bg-[#1B1A17]/90 backdrop-blur-md text-[#FFFDF9] text-[9px] px-2 py-0.5 rounded shadow-card whitespace-nowrap">
                  Smooth &amp; Refined
                </div>
              )}
            </div>
          </div>
        </div>

        {/* ================= RIGHT COLUMN: 3 Feature Metric Cards ================= */}
        <div className="lg:col-span-3 space-y-4 text-left">
          {/* Metric Card 1: 95% Accurate Skin Analysis */}
          <div
            ref={metricCard1Ref}
            className="p-5 rounded-3xl bg-[#FFFDF9] border border-[#EFE3D3] shadow-card hover:shadow-floating transition-all duration-300 flex items-center space-x-4 group"
          >
            <div className="w-12 h-12 rounded-2xl bg-[#F6EFE9] flex items-center justify-center shrink-0 group-hover:scale-105 transition-transform">
              <Leaf className="w-5 h-5 text-[#9E5D46]" strokeWidth={1.75} />
            </div>
            <div>
              <p className="font-serif-luxury text-3xl font-bold text-[#1B1A17] leading-none">
                95%
              </p>
              <p className="text-xs text-[#5E584F] mt-1 font-light leading-snug">
                accurate skin analysis
              </p>
            </div>
          </div>

          {/* Metric Card 2: 30+ Skin Concerns Analyzed */}
          <div
            ref={metricCard2Ref}
            className="p-5 rounded-3xl bg-[#FFFDF9] border border-[#EFE3D3] shadow-card hover:shadow-floating transition-all duration-300 flex items-center space-x-4 group"
          >
            <div className="w-12 h-12 rounded-2xl bg-[#F6EFE9] flex items-center justify-center shrink-0 group-hover:scale-105 transition-transform">
              <User className="w-5 h-5 text-[#9E5D46]" strokeWidth={1.75} />
            </div>
            <div>
              <p className="font-serif-luxury text-3xl font-bold text-[#1B1A17] leading-none">
                30+
              </p>
              <p className="text-xs text-[#5E584F] mt-1 font-light leading-snug">
                skin concerns analyzed
              </p>
            </div>
          </div>

          {/* Metric Card 3: 7-day Personalized Élanor Ritual */}
          <div
            ref={metricCard3Ref}
            className="p-5 rounded-3xl bg-[#FFFDF9] border border-[#EFE3D3] shadow-card hover:shadow-floating transition-all duration-300 flex items-center space-x-4 group"
          >
            <div className="w-12 h-12 rounded-2xl bg-[#F6EFE9] flex items-center justify-center shrink-0 group-hover:scale-105 transition-transform">
              <Sparkles className="w-5 h-5 text-[#9E5D46]" strokeWidth={1.75} />
            </div>
            <div>
              <p className="font-serif-luxury text-3xl font-bold text-[#1B1A17] leading-none">
                7-day
              </p>
              <p className="text-xs text-[#5E584F] mt-1 font-light leading-snug">
                personalized Élanor ritual
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
