'use client';

import React, { useEffect, useRef, useState } from 'react';
import Image from 'next/image';
import gsap from 'gsap';
import {
  Sparkles,
  ArrowRight,
  ShieldCheck,
  HeartHandshake,
  Activity,
  Droplets,
  Scan,
} from 'lucide-react';

interface IntroScreenProps {
  onStart: () => void;
}

export default function IntroScreen({ onStart }: IntroScreenProps) {
  const containerRef = useRef<HTMLDivElement>(null);
  const badgeRef = useRef<HTMLDivElement>(null);
  const headlineRef = useRef<HTMLHeadingElement>(null);
  const subtextRef = useRef<HTMLParagraphElement>(null);
  const ctaRef = useRef<HTMLButtonElement>(null);
  const heroCardRef = useRef<HTMLDivElement>(null);
  const imageInnerRef = useRef<HTMLDivElement>(null);
  const metaRef = useRef<HTMLDivElement>(null);
  const scanLineRef = useRef<HTMLDivElement>(null);

  // Floating badges refs for parallax
  const badge1Ref = useRef<HTMLDivElement>(null);
  const badge2Ref = useRef<HTMLDivElement>(null);
  const badge3Ref = useRef<HTMLDivElement>(null);

  const [activeHotspot, setActiveHotspot] = useState<string | null>('cheek');

  // GSAP Page Entrance & Scanning Loop
  useEffect(() => {
    const prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    if (prefersReducedMotion) return;

    const ctx = gsap.context(() => {
      const tl = gsap.timeline({ defaults: { ease: 'power3.out' } });

      tl.fromTo(
        heroCardRef.current,
        { scale: 1.05, opacity: 0, y: 30 },
        { scale: 1, opacity: 1, y: 0, duration: 1.2 }
      )
        .fromTo(
          badgeRef.current,
          { y: -20, opacity: 0 },
          { y: 0, opacity: 1, duration: 0.8 },
          '-=0.9'
        )
        .fromTo(
          headlineRef.current,
          { y: 30, opacity: 0 },
          { y: 0, opacity: 1, duration: 0.9 },
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
          [badge1Ref.current, badge2Ref.current, badge3Ref.current],
          { scale: 0.85, opacity: 0, y: 15 },
          { scale: 1, opacity: 1, y: 0, stagger: 0.15, duration: 0.8, ease: 'back.out(1.4)' },
          '-=0.4'
        )
        .fromTo(
          metaRef.current,
          { opacity: 0 },
          { opacity: 1, duration: 0.8 },
          '-=0.3'
        );

      // Continuous Scanning Line Animation across Model Portrait
      if (scanLineRef.current) {
        gsap.to(scanLineRef.current, {
          y: '380px',
          duration: 3.8,
          ease: 'power1.inOut',
          repeat: -1,
          yoyo: true,
        });
      }

      // Floating Badges Gentle Idle Bobbing
      gsap.to(badge1Ref.current, {
        y: '-=8px',
        duration: 2.8,
        repeat: -1,
        yoyo: true,
        ease: 'sine.inOut',
      });
      gsap.to(badge2Ref.current, {
        y: '+=10px',
        duration: 3.4,
        repeat: -1,
        yoyo: true,
        ease: 'sine.inOut',
        delay: 0.4,
      });
      gsap.to(badge3Ref.current, {
        y: '-=6px',
        duration: 3.0,
        repeat: -1,
        yoyo: true,
        ease: 'sine.inOut',
        delay: 0.8,
      });
    }, containerRef);

    return () => ctx.revert();
  }, []);

  // Mouse Parallax Effect on Hero Card & Floating Badges
  const handleMouseMove = (e: React.MouseEvent<HTMLDivElement>) => {
    if (!heroCardRef.current || window.matchMedia('(prefers-reduced-motion: reduce)').matches)
      return;

    const rect = heroCardRef.current.getBoundingClientRect();
    const x = (e.clientX - rect.left) / rect.width - 0.5;
    const y = (e.clientY - rect.top) / rect.height - 0.5;

    gsap.to(heroCardRef.current, {
      rotationY: x * 10,
      rotationX: -y * 10,
      transformPerspective: 1000,
      duration: 0.6,
      ease: 'power1.out',
    });

    if (imageInnerRef.current) {
      gsap.to(imageInnerRef.current, {
        x: -x * 14,
        y: -y * 14,
        duration: 0.6,
        ease: 'power1.out',
      });
    }

    if (badge1Ref.current) {
      gsap.to(badge1Ref.current, { x: x * 22, y: y * 22, duration: 0.7 });
    }
    if (badge2Ref.current) {
      gsap.to(badge2Ref.current, { x: -x * 26, y: -y * 26, duration: 0.7 });
    }
    if (badge3Ref.current) {
      gsap.to(badge3Ref.current, { x: x * 18, y: -y * 18, duration: 0.7 });
    }
  };

  const handleMouseLeave = () => {
    if (!heroCardRef.current) return;
    gsap.to(heroCardRef.current, {
      rotationY: 0,
      rotationX: 0,
      duration: 0.8,
      ease: 'power2.out',
    });
    if (imageInnerRef.current) {
      gsap.to(imageInnerRef.current, { x: 0, y: 0, duration: 0.8, ease: 'power2.out' });
    }
    if (badge1Ref.current) gsap.to(badge1Ref.current, { x: 0, duration: 0.8 });
    if (badge2Ref.current) gsap.to(badge2Ref.current, { x: 0, duration: 0.8 });
    if (badge3Ref.current) gsap.to(badge3Ref.current, { x: 0, duration: 0.8 });
  };

  return (
    <div
      ref={containerRef}
      className="min-h-[88vh] flex flex-col justify-center items-center relative overflow-hidden py-10 px-6 sm:px-12"
    >
      {/* Background Atmosphere */}
      <div className="absolute top-1/4 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[700px] h-[700px] bg-[#E4C894]/15 rounded-full blur-3xl pointer-events-none" />
      <div className="absolute bottom-10 right-10 w-[400px] h-[400px] bg-[#7D9075]/10 rounded-full blur-3xl pointer-events-none" />

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
              className="font-serif-luxury text-4xl sm:text-6xl lg:text-7xl text-[#1B1A17] font-normal leading-[1.05] tracking-tight"
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
              A private 2-minute clinical &amp; botanical consultation. Our AI diagnostician
              evaluates your moisture mantle, cellular luminescence, and environmental exposure to
              synthesize your bespoke morning and evening regimen.
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

        {/* Right Column: Interactive AI Skincare Model Portrait with Animated Diagnostics */}
        <div
          className="lg:col-span-5 flex justify-center perspective-[1200px]"
          onMouseMove={handleMouseMove}
          onMouseLeave={handleMouseLeave}
        >
          <div
            ref={heroCardRef}
            className="relative w-full max-w-[420px] aspect-[4/5] rounded-[36px] overflow-hidden shadow-floating border border-[#E4D7C5] bg-[#1B1A17] select-none"
            style={{ transformStyle: 'preserve-3d' }}
          >
            {/* Model Image with GSAP Inner Parallax */}
            <div ref={imageInnerRef} className="absolute inset-[-5%] w-[110%] h-[110%]">
              <Image
                src="/images/skin7.png"
                alt="Élanor AI Skincare Cosmetologist Diagnostic"
                fill
                priority
                className="object-cover object-top"
                sizes="(max-width: 768px) 100vw, 420px"
              />
            </div>

            {/* Subtle Gradient Overlays for Luxury Contrast */}
            <div className="absolute inset-0 bg-gradient-to-t from-[#1B1A17]/85 via-transparent to-[#1B1A17]/20 pointer-events-none" />

            {/* Glowing Laser Scan Beam across Model Face */}
            <div
              ref={scanLineRef}
              className="absolute top-0 inset-x-0 h-1 bg-gradient-to-r from-transparent via-[#C8A46A] to-transparent shadow-[0_0_15px_#C8A46A] opacity-80 pointer-events-none z-20"
            />

            {/* Interactive Dermal Hotspots on Face */}
            {/* 1. Forehead Hotspot */}
            <button
              onClick={() => setActiveHotspot('forehead')}
              className="absolute top-[22%] left-[48%] -translate-x-1/2 -translate-y-1/2 z-30 group cursor-pointer"
              aria-label="Forehead Barrier Point"
            >
              <span className="relative flex h-5 w-5 items-center justify-center">
                <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-[#E4C894] opacity-75"></span>
                <span className="relative inline-flex rounded-full h-2.5 w-2.5 bg-[#C8A46A] border border-[#FFFDF9]"></span>
              </span>
            </button>

            {/* 2. Cheek Hotspot */}
            <button
              onClick={() => setActiveHotspot('cheek')}
              className="absolute top-[42%] left-[34%] -translate-x-1/2 -translate-y-1/2 z-30 group cursor-pointer"
              aria-label="Cheek Luminescence Point"
            >
              <span className="relative flex h-5 w-5 items-center justify-center">
                <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-[#7D9075] opacity-75"></span>
                <span className="relative inline-flex rounded-full h-2.5 w-2.5 bg-[#7D9075] border border-[#FFFDF9]"></span>
              </span>
            </button>

            {/* 3. Jawline Hotspot */}
            <button
              onClick={() => setActiveHotspot('jawline')}
              className="absolute top-[58%] left-[62%] -translate-x-1/2 -translate-y-1/2 z-30 group cursor-pointer"
              aria-label="Jawline Elasticity Point"
            >
              <span className="relative flex h-5 w-5 items-center justify-center">
                <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-[#6F8FAF] opacity-75"></span>
                <span className="relative inline-flex rounded-full h-2.5 w-2.5 bg-[#6F8FAF] border border-[#FFFDF9]"></span>
              </span>
            </button>

            {/* Floating Glass Diagnostic Badge 1 (Top-Right) */}
            <div
              ref={badge1Ref}
              className="absolute top-6 right-5 z-30 bg-[#FFFDF9]/90 backdrop-blur-md rounded-2xl px-3.5 py-2 border border-[#EADFCF] shadow-card flex items-center space-x-2.5"
            >
              <div className="w-6 h-6 rounded-full bg-[#7D9075]/20 flex items-center justify-center text-[#7D9075]">
                <Droplets className="w-3.5 h-3.5" />
              </div>
              <div className="text-left">
                <p className="text-[9px] uppercase tracking-widest text-[#8E857A] font-semibold">
                  Moisture Index
                </p>
                <p className="text-xs font-bold text-[#1B1A17]">94.2% Quenched</p>
              </div>
            </div>

            {/* Floating Glass Diagnostic Badge 2 (Mid-Left) */}
            <div
              ref={badge2Ref}
              className="absolute top-[46%] left-4 z-30 bg-[#1B1A17]/85 backdrop-blur-md rounded-2xl px-3.5 py-2 border border-[#484239] text-[#FFFDF9] shadow-floating flex items-center space-x-2.5"
            >
              <div className="w-6 h-6 rounded-full bg-[#C8A46A]/20 flex items-center justify-center text-[#C8A46A]">
                <Scan className="w-3.5 h-3.5" />
              </div>
              <div className="text-left">
                <p className="text-[9px] uppercase tracking-widest text-[#E4C894] font-semibold">
                  Cellular Scan
                </p>
                <p className="text-xs font-semibold text-[#FFFDF9]">5-Ceramide Sync</p>
              </div>
            </div>

            {/* Floating Glass Diagnostic Badge 3 (Bottom) */}
            <div
              ref={badge3Ref}
              className="absolute bottom-6 inset-x-5 z-30 bg-[#FFFDF9]/95 backdrop-blur-md rounded-2xl p-3.5 border border-[#EADFCF] shadow-card flex items-center justify-between"
            >
              <div className="flex items-center space-x-3">
                <div className="w-7 h-7 rounded-xl bg-[#C8A46A]/15 flex items-center justify-center text-[#C8A46A]">
                  <Activity className="w-4 h-4 animate-pulse" />
                </div>
                <div>
                  <p className="text-[10px] uppercase tracking-wider font-semibold text-[#1B1A17]">
                    {activeHotspot === 'forehead'
                      ? 'Zone: Forehead Mantle'
                      : activeHotspot === 'jawline'
                      ? 'Zone: Periorbital & Jawline'
                      : 'Zone: Malar Radiance Scan'}
                  </p>
                  <p className="text-[11px] text-[#5E584F]">
                    {activeHotspot === 'forehead'
                      ? 'Hydro-lipid equilibrium: 91%'
                      : activeHotspot === 'jawline'
                      ? 'Collagen bounce elasticity: 96%'
                      : 'Phytomolecular affinity: 98.4%'}
                  </p>
                </div>
              </div>
              <span className="text-[10px] bg-[#F2EBE2] text-[#7D9075] px-2 py-1 rounded-full font-bold uppercase tracking-wider">
                Active
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
