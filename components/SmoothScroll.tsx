'use client';

import React, { useEffect, ReactNode } from 'react';

export default function SmoothScroll({ children }: { children: ReactNode }) {
  useEffect(() => {
    if (typeof window === 'undefined') return;

    let lenisInstance: any = null;
    let rafId: number | null = null;

    try {
      import('lenis')
        .then(({ default: Lenis }) => {
          try {
            lenisInstance = new Lenis({
              duration: 1.1,
              easing: (t: number) => Math.min(1, 1.001 - Math.pow(2, -10 * t)),
              orientation: 'vertical',
              gestureOrientation: 'vertical',
              smoothWheel: true,
              touchMultiplier: 1.2,
            });

            function raf(time: number) {
              if (lenisInstance) {
                lenisInstance.raf(time);
                rafId = requestAnimationFrame(raf);
              }
            }

            rafId = requestAnimationFrame(raf);
          } catch (e) {
            console.warn('Lenis initialization skipped:', e);
          }
        })
        .catch(() => {});
    } catch {}

    return () => {
      if (rafId) cancelAnimationFrame(rafId);
      if (lenisInstance) {
        try {
          lenisInstance.destroy();
        } catch {}
      }
    };
  }, []);

  return <>{children}</>;
}

