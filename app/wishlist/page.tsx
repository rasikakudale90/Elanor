'use client';

import React from 'react';
import Link from 'next/link';
import { useStore } from '@/context/StoreContext';
import { PRODUCTS } from '@/data/products';
import ProductCard from '@/components/ProductCard';
import { Heart, Sparkles, ArrowRight, ShoppingBag } from 'lucide-react';

export default function WishlistPage() {
  const { wishlist, addToCart } = useStore();

  const wishlistedProducts = PRODUCTS.filter((p) => wishlist.includes(p.id));

  const handleMoveAllToCart = () => {
    wishlistedProducts.forEach((p) => addToCart(p, 1));
  };

  return (
    <div className="bg-[#F8F3EB] min-h-screen py-12 sm:py-16">
      <div className="max-w-[1440px] mx-auto px-6 sm:px-10">
        <div className="mb-12 flex flex-col md:flex-row md:items-end justify-between border-b border-[#EADFCF] pb-8 gap-4">
          <div>
            <span className="text-xs uppercase tracking-widest text-[#7D9075] font-semibold flex items-center gap-2">
              <Heart className="w-3.5 h-3.5 fill-[#C8A46A] text-[#C8A46A]" />
              Sacred Sanctuary
            </span>
            <h1 className="font-serif-luxury text-4xl sm:text-5xl text-[#1B1A17] mt-1">
              Your Saved Formulations ({wishlist.length})
            </h1>
            <p className="text-xs sm:text-sm text-[#5E584F] mt-2 max-w-xl">
              Your preserved ritual elixirs. Add them to your sacred bag at any time to initiate preparation.
            </p>
          </div>

          {wishlistedProducts.length > 0 && (
            <button
              onClick={handleMoveAllToCart}
              className="px-6 py-3.5 bg-[#1B1A17] text-[#FFFDF9] rounded-full text-xs uppercase tracking-widest font-semibold hover:bg-[#322F2A] transition-all flex items-center space-x-2 shadow-sm shrink-0"
            >
              <ShoppingBag className="w-4 h-4" />
              <span>Move All to Sacred Bag</span>
            </button>
          )}
        </div>

        {wishlistedProducts.length === 0 ? (
          <div className="text-center py-24 bg-[#FFFDF9] rounded-3xl border border-[#EFE3D3] p-10 space-y-6 max-w-xl mx-auto shadow-sm">
            <div className="w-20 h-20 rounded-full bg-[#F2EBE2] mx-auto flex items-center justify-center text-[#8E857A]">
              <Heart className="w-10 h-10 text-[#C8A46A]" />
            </div>
            <h2 className="font-serif-luxury text-3xl text-[#1B1A17]">No rituals currently saved</h2>
            <p className="text-xs sm:text-sm text-[#5E584F] leading-relaxed">
              Explore our botanical creations and tap the heart emblem on any formulation to curate your personal collection.
            </p>
            <Link
              href="/shop"
              className="inline-block px-8 py-4 bg-[#1B1A17] text-[#FFFDF9] rounded-full text-xs uppercase tracking-widest font-semibold hover:bg-[#322F2A] transition-all shadow-md"
            >
              Explore The Formulary
            </Link>
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8">
            {wishlistedProducts.map((product) => (
              <ProductCard key={product.id} product={product} />
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
