'use client';

import React, { useState, useMemo, Suspense } from 'react';
import { useSearchParams } from 'next/navigation';
import { PRODUCTS, CONCERNS } from '@/data/products';
import ProductCard from '@/components/ProductCard';
import { SlidersHorizontal, ArrowUpDown, Sparkles, X } from 'lucide-react';

function ShopContent() {
  const searchParams = useSearchParams();
  const initialCategory = searchParams.get('category') || 'All';
  const initialConcern = searchParams.get('concern') || 'All';

  const [selectedCategory, setSelectedCategory] = useState<string>(initialCategory);
  const [selectedConcern, setSelectedConcern] = useState<string>(initialConcern);
  const [selectedActive, setSelectedActive] = useState<string>('All');
  const [sortBy, setSortBy] = useState<'featured' | 'price-asc' | 'price-desc' | 'rating'>('featured');
  const [mobileFilterOpen, setMobileFilterOpen] = useState(false);

  const categories = ['All', 'Serums', 'Creams', 'Elixirs', 'Cleansers', 'Masks', 'Eye Care'];
  const allActives = [
    'All',
    'Bio-Niacinamide 10%',
    '5-Ceramide Biomimetic Complex',
    'Liposomal Retinaldehyde 0.1%',
    '8-Tier Hyaluronic Complex',
    'Saffron Stem Extract',
    'Palmitoyl Tripeptide-38'
  ];

  const filteredProducts = useMemo(() => {
    return PRODUCTS.filter((p) => {
      const matchCategory = selectedCategory === 'All' || p.category === selectedCategory;
      const matchConcern = selectedConcern === 'All' || p.concern.toLowerCase() === selectedConcern.toLowerCase();
      const matchActive = selectedActive === 'All' || p.keyActives.includes(selectedActive);
      return matchCategory && matchConcern && matchActive;
    }).sort((a, b) => {
      if (sortBy === 'price-asc') return a.price - b.price;
      if (sortBy === 'price-desc') return b.price - a.price;
      if (sortBy === 'rating') return b.rating - a.rating;
      return 0; // featured
    });
  }, [selectedCategory, selectedConcern, selectedActive, sortBy]);

  const clearAllFilters = () => {
    setSelectedCategory('All');
    setSelectedConcern('All');
    setSelectedActive('All');
    setSortBy('featured');
  };

  const hasActiveFilters = selectedCategory !== 'All' || selectedConcern !== 'All' || selectedActive !== 'All';

  return (
    <div className="bg-[#F8F3EB] min-h-screen py-12 px-6 sm:px-10 max-w-[1440px] mx-auto">
      {/* Editorial Header */}
      <div className="mb-12 border-b border-[#EADFCF] pb-10">
        <div className="flex items-center space-x-2 text-xs uppercase tracking-widest text-[#7D9075] font-semibold mb-2">
          <Sparkles className="w-3.5 h-3.5" />
          <span>Haute Botanique Curated Dispensary</span>
        </div>
        <h1 className="font-serif-luxury text-4xl sm:text-5xl lg:text-6xl text-[#1B1A17] font-normal">
          The Full Élanor Formulary
        </h1>
        <p className="text-xs sm:text-sm text-[#5E584F] max-w-2xl mt-3 leading-relaxed">
          Crafted in micro-batches with biocompatible cellular nutrients, cold-pressed botanicals, and scientifically measured clinical efficacy.
        </p>
      </div>

      {/* Main Layout: Sidebar Filters + Products Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-10">
        {/* Left: Desktop Filters Sidebar */}
        <aside className="hidden lg:block lg:col-span-3 space-y-8 bg-[#FFFDF9] p-6 rounded-3xl border border-[#EFE3D3] shadow-sm h-fit sticky top-28">
          <div className="flex justify-between items-center pb-4 border-b border-[#EADFCF]">
            <div className="flex items-center space-x-2 text-sm font-serif-luxury text-[#1B1A17] font-semibold">
              <SlidersHorizontal className="w-4 h-4 text-[#C8A46A]" />
              <span>Refine Formulary</span>
            </div>
            {hasActiveFilters && (
              <button
                onClick={clearAllFilters}
                className="text-xs text-[#8E857A] hover:text-[#1B1A17] underline"
              >
                Reset All
              </button>
            )}
          </div>

          {/* Category Filter */}
          <div className="space-y-2.5">
            <h4 className="text-xs uppercase tracking-wider font-semibold text-[#8E857A]">
              Product Category
            </h4>
            <div className="space-y-1">
              {categories.map((cat) => (
                <button
                  key={cat}
                  onClick={() => setSelectedCategory(cat)}
                  className={`w-full text-left px-3 py-1.5 rounded-xl text-xs transition-colors flex justify-between items-center ${
                    selectedCategory === cat
                      ? 'bg-[#1B1A17] text-[#FFFDF9] font-medium'
                      : 'text-[#5E584F] hover:bg-[#F2EBE2] hover:text-[#1B1A17]'
                  }`}
                >
                  <span>{cat === 'All' ? 'All Categories' : cat}</span>
                  {cat !== 'All' && (
                    <span className="text-[10px] opacity-60">
                      ({PRODUCTS.filter((p) => p.category === cat).length})
                    </span>
                  )}
                </button>
              ))}
            </div>
          </div>

          {/* Concern Filter */}
          <div className="space-y-2.5 pt-4 border-t border-[#EAE1D3]">
            <h4 className="text-xs uppercase tracking-wider font-semibold text-[#8E857A]">
              Skin Concern
            </h4>
            <div className="space-y-1">
              <button
                onClick={() => setSelectedConcern('All')}
                className={`w-full text-left px-3 py-1.5 rounded-xl text-xs transition-colors ${
                  selectedConcern === 'All'
                    ? 'bg-[#1B1A17] text-[#FFFDF9] font-medium'
                    : 'text-[#5E584F] hover:bg-[#F2EBE2] hover:text-[#1B1A17]'
                }`}
              >
                All Concerns
              </button>
              {CONCERNS.map((c) => (
                <button
                  key={c.slug}
                  onClick={() => setSelectedConcern(c.slug)}
                  className={`w-full text-left px-3 py-1.5 rounded-xl text-xs transition-colors flex items-center justify-between ${
                    selectedConcern === c.slug
                      ? 'bg-[#1B1A17] text-[#FFFDF9] font-medium'
                      : 'text-[#5E584F] hover:bg-[#F2EBE2] hover:text-[#1B1A17]'
                  }`}
                >
                  <div className="flex items-center space-x-2">
                    <span
                      className="w-2 h-2 rounded-full"
                      style={{ backgroundColor: c.color }}
                    />
                    <span>{c.name}</span>
                  </div>
                </button>
              ))}
            </div>
          </div>

          {/* Key Bio-Active Filter */}
          <div className="space-y-2.5 pt-4 border-t border-[#EAE1D3]">
            <h4 className="text-xs uppercase tracking-wider font-semibold text-[#8E857A]">
              Key Bio-Active
            </h4>
            <div className="space-y-1">
              {allActives.map((active) => (
                <button
                  key={active}
                  onClick={() => setSelectedActive(active)}
                  className={`w-full text-left px-3 py-1.5 rounded-xl text-xs transition-colors truncate ${
                    selectedActive === active
                      ? 'bg-[#1B1A17] text-[#FFFDF9] font-medium'
                      : 'text-[#5E584F] hover:bg-[#F2EBE2] hover:text-[#1B1A17]'
                  }`}
                >
                  {active}
                </button>
              ))}
            </div>
          </div>
        </aside>

        {/* Right: Product Grid & Sorting Toolbar */}
        <div className="lg:col-span-9 space-y-6">
          {/* Top Control Bar */}
          <div className="bg-[#FFFDF9] p-4 rounded-2xl border border-[#EFE3D3] flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
            <div className="flex items-center space-x-3">
              <span className="text-xs font-semibold text-[#1B1A17]">
                Showing {filteredProducts.length} Formulations
              </span>
              {hasActiveFilters && (
                <span className="text-xs text-[#7D9075] bg-[#EEF2E8] px-2.5 py-0.5 rounded-full font-medium">
                  Filtered
                </span>
              )}
            </div>

            <div className="flex items-center space-x-4 w-full sm:w-auto justify-between sm:justify-end">
              {/* Mobile Filter Toggle */}
              <button
                onClick={() => setMobileFilterOpen(true)}
                className="lg:hidden px-4 py-2 bg-[#F2EBE2] text-[#1B1A17] rounded-full text-xs font-semibold flex items-center space-x-1.5"
              >
                <SlidersHorizontal className="w-3.5 h-3.5" />
                <span>Filters</span>
              </button>

              {/* Sort Selector */}
              <div className="flex items-center space-x-2 text-xs text-[#5E584F]">
                <ArrowUpDown className="w-3.5 h-3.5 text-[#8E857A]" />
                <span className="hidden sm:inline">Sort:</span>
                <select
                  value={sortBy}
                  onChange={(e) => setSortBy(e.target.value as any)}
                  className="bg-[#F8F3EB] border border-[#EADFCF] rounded-xl px-3 py-1.5 text-xs text-[#1B1A17] font-medium focus:outline-none focus:ring-1 focus:ring-[#C8A46A]"
                >
                  <option value="featured">Featured Curations</option>
                  <option value="price-asc">Price: Modest to Opulent</option>
                  <option value="price-desc">Price: Opulent to Modest</option>
                  <option value="rating">Highest Clinical Rating</option>
                </select>
              </div>
            </div>
          </div>

          {/* Products Grid */}
          {filteredProducts.length === 0 ? (
            <div className="text-center py-20 bg-[#FFFDF9] rounded-3xl border border-[#EFE3D3] space-y-4 p-8">
              <p className="font-serif-luxury text-2xl text-[#1B1A17]">No formulations match the active filter criteria.</p>
              <p className="text-xs text-[#5E584F]">
                Try selecting &ldquo;All Categories&rdquo; or clearing active filters to reveal our complete atelier offerings.
              </p>
              <button
                onClick={clearAllFilters}
                className="px-6 py-2.5 bg-[#1B1A17] text-[#FFFDF9] rounded-full text-xs uppercase tracking-widest font-semibold"
              >
                Clear All Filters
              </button>
            </div>
          ) : (
            <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 gap-6">
              {filteredProducts.map((product) => (
                <ProductCard key={product.id} product={product} />
              ))}
            </div>
          )}
        </div>
      </div>

      {/* Mobile Filter Sheet Modal */}
      {mobileFilterOpen && (
        <div className="fixed inset-0 z-50 lg:hidden overflow-hidden">
          <div
            onClick={() => setMobileFilterOpen(false)}
            className="fixed inset-0 bg-[#1B1A17]/60 backdrop-blur-sm"
          />
          <div className="fixed inset-y-0 right-0 max-w-full flex pl-10">
            <div className="w-screen max-w-sm bg-[#FFFDF9] p-6 shadow-2xl overflow-y-auto space-y-6">
              <div className="flex justify-between items-center pb-4 border-b border-[#EADFCF]">
                <h3 className="font-serif-luxury text-xl text-[#1B1A17]">Filters</h3>
                <button
                  onClick={() => setMobileFilterOpen(false)}
                  className="p-2 text-[#5E584F]"
                >
                  <X className="w-5 h-5" />
                </button>
              </div>

              {/* Categories */}
              <div className="space-y-2">
                <p className="text-xs font-semibold uppercase tracking-wider text-[#8E857A]">Category</p>
                <div className="flex flex-wrap gap-1.5">
                  {categories.map((c) => (
                    <button
                      key={c}
                      onClick={() => setSelectedCategory(c)}
                      className={`px-3 py-1.5 rounded-full text-xs ${
                        selectedCategory === c
                          ? 'bg-[#1B1A17] text-[#FFFDF9]'
                          : 'bg-[#F2EBE2] text-[#1B1A17]'
                      }`}
                    >
                      {c}
                    </button>
                  ))}
                </div>
              </div>

              {/* Concerns */}
              <div className="space-y-2">
                <p className="text-xs font-semibold uppercase tracking-wider text-[#8E857A]">Concern</p>
                <div className="flex flex-wrap gap-1.5">
                  <button
                    onClick={() => setSelectedConcern('All')}
                    className={`px-3 py-1.5 rounded-full text-xs ${
                      selectedConcern === 'All'
                        ? 'bg-[#1B1A17] text-[#FFFDF9]'
                        : 'bg-[#F2EBE2] text-[#1B1A17]'
                    }`}
                  >
                    All
                  </button>
                  {CONCERNS.map((c) => (
                    <button
                      key={c.slug}
                      onClick={() => setSelectedConcern(c.slug)}
                      className={`px-3 py-1.5 rounded-full text-xs ${
                        selectedConcern === c.slug
                          ? 'bg-[#1B1A17] text-[#FFFDF9]'
                          : 'bg-[#F2EBE2] text-[#1B1A17]'
                      }`}
                    >
                      {c.name}
                    </button>
                  ))}
                </div>
              </div>

              <div className="pt-6 border-t border-[#EADFCF]">
                <button
                  onClick={() => setMobileFilterOpen(false)}
                  className="w-full py-3.5 bg-[#1B1A17] text-[#FFFDF9] rounded-full text-xs uppercase tracking-widest font-semibold"
                >
                  Apply Filters ({filteredProducts.length} Results)
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default function ShopPage() {
  return (
    <Suspense fallback={<div className="min-h-screen bg-[#F8F3EB] flex items-center justify-center text-xs text-[#8E857A]">Loading Curated Formulary...</div>}>
      <ShopContent />
    </Suspense>
  );
}
