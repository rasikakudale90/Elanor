'use client';

import React from 'react';
import Link from 'next/link';
import Image from 'next/image';
import { notFound, useParams } from 'next/navigation';
import { CONCERNS, PRODUCTS } from '@/data/products';
import ProductCard from '@/components/ProductCard';
import { Sparkles, ShieldCheck, ArrowRight, ArrowLeft } from 'lucide-react';

export default function ConcernDetailPage() {
  const params = useParams();
  const slug = typeof params?.slug === 'string' ? params.slug : Array.isArray(params?.slug) ? params.slug[0] : '';
  const concern = CONCERNS.find((c) => c.slug.toLowerCase() === slug.toLowerCase());

  if (!concern) {
    notFound();
  }

  const matchingProducts = PRODUCTS.filter((p) =>
    concern.productIds.includes(p.id) || p.concern.toLowerCase().includes(concern.slug.toLowerCase())
  );

  return (
    <div className="bg-[#F8F3EB] min-h-screen py-12 sm:py-16">
      <div className="max-w-[1440px] mx-auto px-6 sm:px-10 space-y-16">
        {/* Navigation Breadcrumb */}
        <div className="flex items-center space-x-2 text-xs text-[#8E857A]">
          <Link href="/" className="hover:text-[#1B1A17]">Maison Élanor</Link>
          <span>/</span>
          <Link href="/shop" className="hover:text-[#1B1A17]">Concerns</Link>
          <span>/</span>
          <span className="text-[#1B1A17] font-medium">{concern.name}</span>
        </div>

        {/* Hero Editorial Banner */}
        <div className="bg-[#FFFDF9] rounded-3xl p-8 sm:p-14 border border-[#EFE3D3] shadow-card grid grid-cols-1 lg:grid-cols-12 gap-10 items-center">
          <div className="lg:col-span-7 space-y-4">
            <div className="flex items-center space-x-2">
              <span
                className="w-3 h-3 rounded-full"
                style={{ backgroundColor: concern.color }}
              />
              <span className="text-xs uppercase tracking-widest font-semibold text-[#8E857A]">
                {concern.frenchTitle}
              </span>
            </div>

            <h1 className="font-serif-luxury text-4xl sm:text-5xl lg:text-6xl text-[#1B1A17] leading-tight">
              {concern.name}
            </h1>

            <p className="text-xs sm:text-sm text-[#5E584F] leading-relaxed max-w-xl">
              {concern.description} Our dermatological protocol blends bio-compatible plant stem cells with lipid barrier repair agents to restore equilibrium at the cellular stratum.
            </p>

            <div className="pt-3 flex items-center space-x-3 text-xs text-[#7D9075]">
              <ShieldCheck className="w-4 h-4" />
              <span>Dermatologically Evaluated for Hypoallergenic Tolerance</span>
            </div>
          </div>

          <div className="lg:col-span-5 relative aspect-[4/3] rounded-2xl overflow-hidden bg-[#F2EBE2] border border-[#EADFCF] shadow-inner">
            <Image
              src={concern.image}
              alt={concern.name}
              fill
              className="object-cover"
            />
          </div>
        </div>

        {/* Targeted Formulations */}
        <div className="space-y-8">
          <div className="flex justify-between items-end">
            <div>
              <span className="text-xs uppercase tracking-widest text-[#7D9075] font-semibold">
                Clinical Prescription
              </span>
              <h2 className="font-serif-luxury text-3xl sm:text-4xl text-[#1B1A17] mt-1">
                Targeted Formulations for {concern.name}
              </h2>
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8">
            {matchingProducts.map((p) => (
              <ProductCard key={p.id} product={p} />
            ))}
          </div>
        </div>

        {/* Diagnostic CTA */}
        <div className="bg-[#1B1A17] text-[#FFFDF9] rounded-3xl p-8 sm:p-12 text-center space-y-4 shadow-xl">
          <Sparkles className="w-8 h-8 text-[#C8A46A] mx-auto" />
          <h3 className="font-serif-luxury text-3xl text-[#FFFDF9]">
            Want a fully personalized skin regimen?
          </h3>
          <p className="text-xs text-[#8E857A] max-w-md mx-auto">
            Take our 2-minute diagnostic to receive custom morning and evening pH-balanced rituals.
          </p>
          <Link
            href="/routine-builder"
            className="inline-flex items-center space-x-2 px-8 py-3.5 bg-[#C8A46A] text-[#1B1A17] rounded-full text-xs uppercase tracking-widest font-semibold hover:bg-[#E4C894] transition-all"
          >
            <span>Launch AI Routine Builder</span>
            <ArrowRight className="w-4 h-4" />
          </Link>
        </div>
      </div>
    </div>
  );
}
