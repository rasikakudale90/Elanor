'use client';

import React, { useEffect, useState, useRef } from 'react';
import gsap from 'gsap';
import { Sparkles, Check } from 'lucide-react';

interface AnalysisScreenProps {
  onComplete: () => void;
}

const STAGES = [
  { id: 1, number: '01', title: 'Synthesizing Dermal Profile', desc: 'Evaluating barrier integrity and moisture retention metrics' },
  { id: 2, number: '02', title: 'Assessing Concern Compatibility', desc: 'Cross-referencing targeted bio-active signaling pathways' },
  { id: 3, number: '03', title: 'Molecular Formula Selection', desc: 'Calibrating lipid-identical bio-ceramides and pH sequence' },
  { id: 4, number: '04', title: 'Constructing AM/PM Ritual Architecture', desc: 'Finalizing synergistic morning and nocturnal application order' },
];

export default function AnalysisScreen({ onComplete }: AnalysisScreenProps) {
  const [currentStageIndex, setCurrentStageIndex] = useState(0);
  const containerRef = useRef<HTMLDivElement>(null);
  const stagesRef = useRef<(HTMLDivElement | null)[]>([]);

  useEffect(() => {
    let timer1: NodeJS.Timeout;
    let timer2: NodeJS.Timeout;
    let timer3: NodeJS.Timeout;
    let timerEnd: NodeJS.Timeout;

    timer1 = setTimeout(() => setCurrentStageIndex(1), 600);
    timer2 = setTimeout(() => setCurrentStageIndex(2), 1250);
    timer3 = setTimeout(() => setCurrentStageIndex(3), 1900);
    timerEnd = setTimeout(() => {
      onComplete();
    }, 2600);

    return () => {
      clearTimeout(timer1);
      clearTimeout(timer2);
      clearTimeout(timer3);
      clearTimeout(timerEnd);
    };
  }, [onComplete]);

  useEffect(() => {
    const prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    if (prefersReducedMotion) return;

    const ctx = gsap.context(() => {
      gsap.fromTo(
        containerRef.current,
        { opacity: 0, scale: 0.98 },
        { opacity: 1, scale: 1, duration: 0.6, ease: 'power2.out' }
      );
    }, containerRef);

    return () => ctx.revert();
  }, []);

  return (
    <div
      ref={containerRef}
      className="min-h-[75vh] flex flex-col justify-center items-center py-12 px-6"
    >
      <div className="max-w-xl w-full mx-auto bg-[#FFFDF9] rounded-3xl p-8 sm:p-12 border border-[#EADFCF] shadow-card space-y-8 text-center">
        {/* Animated Badge */}
        <div className="inline-flex items-center space-x-2 px-4 py-1.5 rounded-full bg-[#F8F3EB] border border-[#E4D7C5] text-xs font-semibold text-[#7D9075]">
          <Sparkles className="w-3.5 h-3.5 text-[#C8A46A] animate-spin" style={{ animationDuration: '4s' }} />
          <span className="uppercase tracking-[0.2em] text-[10px]">
            Understanding Your Skin
          </span>
        </div>

        <div className="space-y-2">
          <h2 className="font-serif-luxury text-3xl sm:text-4xl text-[#1B1A17]">
            Calibrating Your Formulation Matrix
          </h2>
          <p className="text-xs sm:text-sm text-[#5E584F] max-w-md mx-auto">
            Synthesizing Élanor&apos;s haute botanical formulations with your personal dermal priorities.
          </p>
        </div>

        {/* 4 Sequential Analysis Stages */}
        <div className="space-y-4 text-left pt-2">
          {STAGES.map((stage, idx) => {
            const isCompleted = idx < currentStageIndex;
            const isActive = idx === currentStageIndex;

            return (
              <div
                key={stage.id}
                ref={(el) => { stagesRef.current[idx] = el; }}
                className={`p-4 rounded-2xl border transition-all duration-300 flex items-center space-x-4 ${
                  isActive
                    ? 'bg-[#F8F3EB] border-[#C8A46A] shadow-sm'
                    : isCompleted
                    ? 'bg-[#FFFDF9] border-[#EADFCF] opacity-90'
                    : 'bg-[#FFFDF9]/40 border-transparent opacity-40'
                }`}
              >
                {/* Status Indicator */}
                <div
                  className={`w-7 h-7 rounded-full flex items-center justify-center text-xs font-semibold shrink-0 transition-colors ${
                    isCompleted
                      ? 'bg-[#7D9075] text-[#FFFDF9]'
                      : isActive
                      ? 'bg-[#1B1A17] text-[#FFFDF9] animate-pulse'
                      : 'bg-[#EADFCF] text-[#8E857A]'
                  }`}
                >
                  {isCompleted ? (
                    <Check className="w-3.5 h-3.5" />
                  ) : (
                    <span>{stage.number}</span>
                  )}
                </div>

                {/* Content */}
                <div className="space-y-0.5">
                  <p
                    className={`text-xs font-semibold tracking-wide ${
                      isActive
                        ? 'text-[#1B1A17]'
                        : isCompleted
                        ? 'text-[#5E584F]'
                        : 'text-[#8E857A]'
                    }`}
                  >
                    {stage.title}
                  </p>
                  <p className="text-[11px] text-[#8E857A]">{stage.desc}</p>
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}
