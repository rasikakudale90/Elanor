'use client';

import React, { useState, useEffect } from 'react';
import Link from 'next/link';
import Image from 'next/image';
import { useStore } from '@/context/StoreContext';
import { PRODUCTS, CONCERNS } from '@/data/products';
import { Search, X, ArrowRight, Sparkles } from 'lucide-react';

export default function SearchModal() {
  const { isSearchOpen, setIsSearchOpen } = useStore();
  const [query, setQuery] = useState('');

  // Close with Esc key
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') setIsSearchOpen(false);
    };
    if (isSearchOpen) {
      window.addEventListener('keydown', handleKeyDown);
    }
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [isSearchOpen, setIsSearchOpen]);

  if (!isSearchOpen) return null;

  const filteredProducts = query.trim() === ''
    ? []
    : PRODUCTS.filter((p) =>
        p.name.toLowerCase().includes(query.toLowerCase()) ||
        p.category.toLowerCase().includes(query.toLowerCase()) ||
        p.concern.toLowerCase().includes(query.toLowerCase()) ||
        p.keyActives.some((k) => k.toLowerCase().includes(query.toLowerCase())) ||
        p.tagline.toLowerCase().includes(query.toLowerCase())
      );

  const popularQueries = [
    'Saffron Stem Cells',
    'Lipid Ceramide Cream',
    'Retinal Nocturne Elixir',
    'Hydra Plumping Essence',
    'Illite Green Clay Masque'
  ];

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto">
      {/* Backdrop */}
      <div
        onClick={() => setIsSearchOpen(false)}
        className="fixed inset-0 bg-[#1B1A17]/60 backdrop-blur-md transition-opacity"
      />

      <div className="relative min-h-screen px-4 pt-16 pb-20 flex justify-center items-start">
        <div className="relative w-full max-w-3xl bg-[#FFFDF9] rounded-3xl shadow-floating border border-[#EADFCF] overflow-hidden">
          {/* Top Search Input Bar */}
          <div className="p-6 sm:p-8 border-b border-[#EADFCF] bg-[#F8F3EB]/80 flex items-center space-x-4">
            <Search className="w-6 h-6 text-[#C8A46A]" strokeWidth={1.75} />
            <input
              type="text"
              placeholder="Search by botanical elixir, skin concern, or active ingredient..."
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              autoFocus
              className="flex-1 bg-transparent text-base sm:text-lg text-[#1B1A17] placeholder-[#8E857A] focus:outline-none font-medium"
            />
            {query && (
              <button
                onClick={() => setQuery('')}
                className="text-xs text-[#8E857A] hover:text-[#1B1A17] px-2 py-1 rounded bg-[#EFE3D3]"
              >
                Clear
              </button>
            )}
            <button
              onClick={() => setIsSearchOpen(false)}
              className="p-2 text-[#5E584F] hover:text-[#1B1A17] rounded-full hover:bg-[#EFE3D3] transition-colors"
            >
              <X className="w-5 h-5" />
            </button>
          </div>

          {/* Body Content */}
          <div className="p-6 sm:p-8 max-h-[70vh] overflow-y-auto space-y-6">
            {query.trim() === '' ? (
              <div className="space-y-6">
                <div>
                  <h4 className="text-xs uppercase tracking-widest font-semibold text-[#8E857A] mb-3">
                    Trending Search Inquiries
                  </h4>
                  <div className="flex flex-wrap gap-2">
                    {popularQueries.map((term) => (
                      <button
                        key={term}
                        onClick={() => setQuery(term)}
                        className="px-4 py-2 rounded-full text-xs font-medium bg-[#F2EBE2] text-[#1B1A17] hover:bg-[#EFE3D3] hover:text-[#C8A46A] transition-colors border border-[#E4D7C5]"
                      >
                        {term}
                      </button>
                    ))}
                  </div>
                </div>

                <div className="pt-4 border-t border-[#EAE1D3]">
                  <h4 className="text-xs uppercase tracking-widest font-semibold text-[#8E857A] mb-3">
                    Explore Specific Skin Concerns
                  </h4>
                  <div className="grid grid-cols-2 sm:grid-cols-3 gap-3">
                    {CONCERNS.map((c) => (
                      <Link
                        key={c.slug}
                        href={`/concern/${c.slug}`}
                        onClick={() => setIsSearchOpen(false)}
                        className="p-3.5 rounded-2xl bg-[#F8F3EB] hover:bg-[#F2EBE2] transition-colors border border-[#EADFCF] group"
                      >
                        <p className="font-serif-luxury text-sm font-semibold text-[#1B1A17] group-hover:text-[#C8A46A] transition-colors">
                          {c.name}
                        </p>
                        <p className="text-[11px] text-[#8E857A] mt-0.5">{c.frenchTitle}</p>
                      </Link>
                    ))}
                  </div>
                </div>
              </div>
            ) : filteredProducts.length === 0 ? (
              <div className="text-center py-12 space-y-3">
                <p className="font-serif-luxury text-xl text-[#1B1A17]">No botanical formulations match “{query}”</p>
                <p className="text-xs text-[#5E584F]">
                  Try searching for ingredients like <em>Niacinamide</em>, <em>Ceramides</em>, or <em>Retinal</em>.
                </p>
              </div>
            ) : (
              <div className="space-y-4">
                <div className="flex justify-between items-center text-xs text-[#8E857A]">
                  <span>Found {filteredProducts.length} formulated results</span>
                  <span>Click product to view ritual</span>
                </div>
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  {filteredProducts.map((p) => (
                    <Link
                      key={p.id}
                      href={`/product/${p.id}`}
                      onClick={() => setIsSearchOpen(false)}
                      className="flex space-x-3.5 p-3 rounded-2xl bg-[#FDFBF8] border border-[#EFE3D3] hover:border-[#C8A46A] hover:shadow-card transition-all group"
                    >
                      <div className="relative w-16 h-20 rounded-xl overflow-hidden bg-[#F2EBE2] shrink-0 border border-[#EADFCF]">
                        <Image
                          src={p.image}
                          alt={p.name}
                          fill
                          className="object-cover group-hover:scale-105 transition-transform"
                        />
                      </div>
                      <div className="flex-1 min-w-0">
                        <span className="text-[10px] tracking-wider uppercase font-medium text-[#7D9075]">
                          {p.category} • {p.concern}
                        </span>
                        <h4 className="font-serif-luxury text-sm font-semibold text-[#1B1A17] group-hover:text-[#C8A46A] transition-colors truncate">
                          {p.name}
                        </h4>
                        <p className="text-[11px] text-[#5E584F] line-clamp-1 mt-0.5">
                          {p.tagline}
                        </p>
                        <p className="text-xs font-semibold text-[#1B1A17] mt-1.5">${p.price}</p>
                      </div>
                    </Link>
                  ))}
                </div>
              </div>
            )}
          </div>

          {/* Quick Consultation CTA */}
          <div className="p-4 bg-[#F2EBE2] border-t border-[#E4D7C5] flex items-center justify-between px-6 sm:px-8">
            <div className="flex items-center space-x-2 text-xs text-[#5E584F]">
              <Sparkles className="w-4 h-4 text-[#C8A46A]" />
              <span>Unsure which Élanor formulation is suited for your skin?</span>
            </div>
            <Link
              href="/routine-builder"
              onClick={() => setIsSearchOpen(false)}
              className="text-xs font-semibold text-[#1B1A17] hover:text-[#C8A46A] flex items-center space-x-1"
            >
              <span>Take AI Skin Quiz</span>
              <ArrowRight className="w-3.5 h-3.5" />
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}
