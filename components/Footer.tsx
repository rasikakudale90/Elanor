'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import { CONCERNS } from '@/data/products';
import { Sparkles, ArrowRight, ShieldCheck, Leaf, Award, Check } from 'lucide-react';

export default function Footer() {
  const [email, setEmail] = useState('');
  const [subscribed, setSubscribed] = useState(false);

  const handleSubscribe = (e: React.FormEvent) => {
    e.preventDefault();
    if (email) {
      setSubscribed(true);
      setEmail('');
    }
  };

  return (
    <footer className="bg-[#1B1A17] text-[#FDFBF8] pt-20 pb-12 border-t border-[#322F2A] relative overflow-hidden">
      {/* Botanical Background Glow */}
      <div className="absolute top-0 right-1/4 w-96 h-96 bg-[#C8A46A]/5 rounded-full blur-3xl pointer-events-none" />
      <div className="absolute bottom-0 left-1/4 w-96 h-96 bg-[#7D9075]/5 rounded-full blur-3xl pointer-events-none" />

      <div className="max-w-[1440px] mx-auto px-6 sm:px-10 relative z-10">
        {/* Editorial Newsletter Ribbon */}
        <div className="bg-[#262420] border border-[#3E3A33] rounded-3xl p-8 sm:p-12 mb-16 shadow-2xl">
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-center">
            <div className="lg:col-span-7 space-y-3">
              <span className="text-[11px] uppercase tracking-[0.25em] text-[#C8A46A] font-semibold flex items-center gap-2">
                <Sparkles className="w-3.5 h-3.5" />
                The Élanor Gazette & Private Invitations
              </span>
              <h3 className="font-serif-luxury text-3xl sm:text-4xl text-[#FFFDF9] leading-tight">
                Receive confidential formulations, rare harvest bulletins, and bespoke skin regimens.
              </h3>
              <p className="text-xs text-[#8E857A] max-w-xl">
                Subscribers receive complimentary shipping codes, first access to seasonal micro-batches, and invitations to dermatological webinars.
              </p>
            </div>

            <div className="lg:col-span-5">
              {subscribed ? (
                <div className="p-4 rounded-2xl bg-[#7D9075]/20 border border-[#7D9075]/40 text-center space-y-1">
                  <div className="flex items-center justify-center space-x-2 text-[#E4C894] font-medium text-sm">
                    <Check className="w-4 h-4" />
                    <span>Bienvenue to Maison Élanor</span>
                  </div>
                  <p className="text-[11px] text-[#8E857A]">
                    Your welcome gift dossier has been dispatched to your correspondence address.
                  </p>
                </div>
              ) : (
                <form onSubmit={handleSubscribe} className="space-y-3">
                  <div className="flex bg-[#1B1A17] border border-[#484239] rounded-full p-1.5 focus-within:border-[#C8A46A] transition-colors">
                    <input
                      type="email"
                      required
                      placeholder="Enter your email address..."
                      value={email}
                      onChange={(e) => setEmail(e.target.value)}
                      className="flex-1 bg-transparent px-4 text-xs text-[#FFFDF9] placeholder-[#8E857A] focus:outline-none"
                    />
                    <button
                      type="submit"
                      className="px-6 py-3 bg-[#C8A46A] text-[#1B1A17] text-xs uppercase tracking-widest font-semibold rounded-full hover:bg-[#E4C894] transition-colors flex items-center space-x-1.5 shrink-0"
                    >
                      <span>Join</span>
                      <ArrowRight className="w-3.5 h-3.5" />
                    </button>
                  </div>
                  <p className="text-[10px] text-[#8E857A] text-center">
                    We respect your sanctuary. Unsubscribe at any moment.
                  </p>
                </form>
              )}
            </div>
          </div>
        </div>

        {/* Pillars / Trust Badges */}
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-6 py-8 border-y border-[#322F2A] mb-16 text-xs text-[#8E857A]">
          <div className="flex items-center space-x-3.5">
            <div className="p-2.5 rounded-full bg-[#262420] text-[#C8A46A] border border-[#3E3A33]">
              <Leaf className="w-5 h-5" />
            </div>
            <div>
              <p className="text-sm font-serif-luxury font-semibold text-[#FFFDF9]">Wild-Harvested Potency</p>
              <p className="text-[11px] mt-0.5">Cold-pressed botanical extracts preserved at source in Grasse & Swiss Alps.</p>
            </div>
          </div>

          <div className="flex items-center space-x-3.5">
            <div className="p-2.5 rounded-full bg-[#262420] text-[#C8A46A] border border-[#3E3A33]">
              <ShieldCheck className="w-5 h-5" />
            </div>
            <div>
              <p className="text-sm font-serif-luxury font-semibold text-[#FFFDF9]">Clinical Biomimicry</p>
              <p className="text-[11px] mt-0.5">100% biocompatible lipid matrices proven across third-party clinical trials.</p>
            </div>
          </div>

          <div className="flex items-center space-x-3.5">
            <div className="p-2.5 rounded-full bg-[#262420] text-[#C8A46A] border border-[#3E3A33]">
              <Award className="w-5 h-5" />
            </div>
            <div>
              <p className="text-sm font-serif-luxury font-semibold text-[#FFFDF9]">Sustainable Glass Atelier</p>
              <p className="text-[11px] mt-0.5">Miron violet glass vessels shielding active phytomolecules from photo-oxidation.</p>
            </div>
          </div>
        </div>

        {/* Navigation Columns */}
        <div className="grid grid-cols-2 md:grid-cols-5 gap-10 text-xs">
          {/* Brand Col */}
          <div className="col-span-2 space-y-4">
            <span className="font-serif-luxury text-3xl tracking-wider text-[#FFFDF9]">
              Élanor
            </span>
            <p className="text-[#8E857A] leading-relaxed max-w-sm text-xs">
              Maison de Haute Botanique. Synthesizing rare botanical actives with breakthrough clinical biotechnology for timeless dermal vitality.
            </p>
            <div className="pt-2">
              <p className="text-[11px] text-[#5E584F] tracking-widest uppercase">
                Atelier: 24 Place Vendôme, 75001 Paris
              </p>
            </div>
          </div>

          {/* Col 1: Shop */}
          <div className="space-y-3">
            <p className="font-serif-luxury text-sm font-semibold text-[#FFFDF9] tracking-wider uppercase">
              Curated Shop
            </p>
            <ul className="space-y-2 text-[#8E857A]">
              <li><Link href="/shop" className="hover:text-[#C8A46A] transition-colors">All Formulations</Link></li>
              <li><Link href="/shop?category=Serums" className="hover:text-[#C8A46A] transition-colors">Botanical Serums</Link></li>
              <li><Link href="/shop?category=Creams" className="hover:text-[#C8A46A] transition-colors">Velvet Creams</Link></li>
              <li><Link href="/shop?category=Elixirs" className="hover:text-[#C8A46A] transition-colors">Nocturnal Elixirs</Link></li>
              <li><Link href="/shop?category=Eye%20Care" className="hover:text-[#C8A46A] transition-colors">Eye Contour Balms</Link></li>
            </ul>
          </div>

          {/* Col 2: Concerns */}
          <div className="space-y-3">
            <p className="font-serif-luxury text-sm font-semibold text-[#FFFDF9] tracking-wider uppercase">
              Skin Concerns
            </p>
            <ul className="space-y-2 text-[#8E857A]">
              {CONCERNS.slice(0, 5).map((c) => (
                <li key={c.slug}>
                  <Link href={`/concern/${c.slug}`} className="hover:text-[#C8A46A] transition-colors">
                    {c.name}
                  </Link>
                </li>
              ))}
            </ul>
          </div>

          {/* Col 3: Diagnostics & Maison */}
          <div className="space-y-3">
            <p className="font-serif-luxury text-sm font-semibold text-[#FFFDF9] tracking-wider uppercase">
              Innovations
            </p>
            <ul className="space-y-2 text-[#8E857A]">
              <li><Link href="/routine-builder" className="hover:text-[#C8A46A] transition-colors">AI Routine Diagnostic</Link></li>
              <li><Link href="/ingredients" className="hover:text-[#C8A46A] transition-colors">Ingredient Explorer</Link></li>
              <li><Link href="/compare" className="hover:text-[#C8A46A] transition-colors">Formula Comparator</Link></li>
              <li><Link href="/brands" className="hover:text-[#C8A46A] transition-colors">Maison Philosophy</Link></li>
              <li><Link href="/wishlist" className="hover:text-[#C8A46A] transition-colors">Sacred Wishlist</Link></li>
            </ul>
          </div>
        </div>

        {/* Copyright */}
        <div className="mt-16 pt-8 border-t border-[#322F2A] flex flex-col sm:flex-row items-center justify-between text-[11px] text-[#5E584F] space-y-4 sm:space-y-0">
          <p>© {new Date().getFullYear()} Élanor Haute Botanique. All rights reserved.</p>
          <div className="flex space-x-6">
            <span className="hover:text-[#8E857A] cursor-pointer">Privacy Charter</span>
            <span className="hover:text-[#8E857A] cursor-pointer">Terms of Sacred Ritual</span>
            <span className="hover:text-[#8E857A] cursor-pointer">Dermatological Certifications</span>
          </div>
        </div>
      </div>
    </footer>
  );
}
