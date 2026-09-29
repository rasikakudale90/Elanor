'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import { INGREDIENTS } from '@/data/products';
import { Sparkles, Leaf, Search, ShieldCheck, ArrowRight } from 'lucide-react';

export default function IngredientsPage() {
  const [search, setSearch] = useState('');
  const [selectedCategory, setSelectedCategory] = useState<string>('All');

  const categories = ['All', 'Cellular Botanical', 'Lipid Architecture', 'Clinical Active', 'Hydration Engine', 'Precious Botanical Oil', 'Peptide Architecture'];

  const filteredIngredients = INGREDIENTS.filter((item) => {
    const matchCat = selectedCategory === 'All' || item.category === selectedCategory;
    const matchSearch =
      search.trim() === '' ||
      item.name.toLowerCase().includes(search.toLowerCase()) ||
      item.botanicalName.toLowerCase().includes(search.toLowerCase()) ||
      item.benefits.toLowerCase().includes(search.toLowerCase()) ||
      item.origin.toLowerCase().includes(search.toLowerCase());
    return matchCat && matchSearch;
  });

  return (
    <div className="bg-[#F8F3EB] min-h-screen py-12 sm:py-16">
      <div className="max-w-[1440px] mx-auto px-6 sm:px-10 space-y-12">
        {/* Header */}
        <div className="border-b border-[#EADFCF] pb-10 space-y-3">
          <div className="flex items-center space-x-2 text-xs uppercase tracking-widest text-[#7D9075] font-semibold">
            <Leaf className="w-3.5 h-3.5" />
            <span>Phytochemical Sourcing & Bio-Actives</span>
          </div>
          <h1 className="font-serif-luxury text-4xl sm:text-5xl lg:text-6xl text-[#1B1A17] font-normal">
            The Élanor Ingredient Glossary
          </h1>
          <p className="text-xs sm:text-sm text-[#5E584F] max-w-2xl leading-relaxed">
            An open-source index of our wild-harvested botanicals and bio-fermented actives. We believe in total transparency, uncompromised bioavailability, and clinical synergy.
          </p>
        </div>

        {/* Search & Filter Bar */}
        <div className="bg-[#FFFDF9] p-6 rounded-3xl border border-[#EFE3D3] shadow-sm flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
          <div className="flex items-center space-x-3 bg-[#F8F3EB] px-4 py-2.5 rounded-full border border-[#EADFCF] w-full md:w-80">
            <Search className="w-4 h-4 text-[#8E857A]" />
            <input
              type="text"
              placeholder="Search botanical or clinical active..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="bg-transparent text-xs text-[#1B1A17] placeholder-[#8E857A] focus:outline-none w-full"
            />
          </div>

          <div className="flex flex-wrap gap-2">
            {categories.map((cat) => (
              <button
                key={cat}
                onClick={() => setSelectedCategory(cat)}
                className={`px-3.5 py-1.5 rounded-full text-xs transition-colors ${
                  selectedCategory === cat
                    ? 'bg-[#1B1A17] text-[#FFFDF9] font-medium'
                    : 'bg-[#F2EBE2] text-[#5E584F] hover:bg-[#EFE3D3] hover:text-[#1B1A17]'
                }`}
              >
                {cat}
              </button>
            ))}
          </div>
        </div>

        {/* Ingredients Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          {filteredIngredients.map((ing) => (
            <div
              key={ing.id}
              className="bg-[#FFFDF9] rounded-3xl p-8 border border-[#EFE3D3] shadow-card hover:border-[#C8A46A] transition-all flex flex-col justify-between space-y-6"
            >
              <div className="space-y-4">
                <div className="flex justify-between items-start">
                  <span className="text-[10px] uppercase tracking-wider font-semibold text-[#7D9075] bg-[#EEF2E8] px-2.5 py-0.5 rounded-full">
                    {ing.category}
                  </span>
                  <span className="text-[10px] text-[#8E857A] font-medium">{ing.origin}</span>
                </div>

                <div>
                  <h3 className="font-serif-luxury text-2xl text-[#1B1A17]">{ing.name}</h3>
                  <p className="text-xs text-[#8E857A] italic mt-0.5">{ing.botanicalName}</p>
                </div>

                <p className="text-xs text-[#5E584F] leading-relaxed">{ing.description}</p>

                <div className="space-y-2 pt-3 border-t border-[#EAE1D3] text-xs">
                  <div>
                    <span className="font-semibold text-[#1B1A17]">Dermal Benefits: </span>
                    <span className="text-[#5E584F]">{ing.benefits}</span>
                  </div>
                  <div>
                    <span className="font-semibold text-[#C8A46A]">Bio-Synergy: </span>
                    <span className="text-[#5E584F]">{ing.compatibility}</span>
                  </div>
                </div>
              </div>

              <div className="pt-4 border-t border-[#EFE3D3] flex items-center justify-between text-xs">
                <div className="text-[11px] text-[#8E857A]">
                  <span>Present in: </span>
                  <strong className="text-[#1B1A17]">{ing.foundIn.join(', ')}</strong>
                </div>
                <Link
                  href="/shop"
                  className="text-xs font-semibold text-[#1B1A17] hover:text-[#C8A46A] flex items-center space-x-1"
                >
                  <span>Shop Ritual</span>
                  <ArrowRight className="w-3.5 h-3.5" />
                </Link>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
