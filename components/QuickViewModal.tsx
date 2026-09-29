'use client';

import React, { useState } from 'react';
import Image from 'next/image';
import Link from 'next/link';
import { useStore } from '@/context/StoreContext';
import { X, Heart, Star, Check, Plus, Minus, ArrowRight } from 'lucide-react';

export default function QuickViewModal() {
  const { quickViewProduct, setQuickViewProduct, addToCart, isInWishlist, toggleWishlist } = useStore();
  const [quantity, setQuantity] = useState(1);
  const [addedAnimation, setAddedAnimation] = useState(false);

  if (!quickViewProduct) return null;

  const product = quickViewProduct;
  const isWishlisted = isInWishlist(product.id);

  const handleAddToCart = () => {
    addToCart(product, quantity);
    setAddedAnimation(true);
    setTimeout(() => setAddedAnimation(false), 2000);
  };

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto">
      <div
        onClick={() => setQuickViewProduct(null)}
        className="fixed inset-0 bg-[#1B1A17]/60 backdrop-blur-md transition-opacity"
      />

      <div className="relative min-h-screen px-4 py-12 flex items-center justify-center">
        <div className="relative w-full max-w-4xl bg-[#FFFDF9] rounded-3xl shadow-floating border border-[#EADFCF] overflow-hidden">
          {/* Close button */}
          <button
            onClick={() => setQuickViewProduct(null)}
            className="absolute top-5 right-5 z-20 p-2 text-[#5E584F] hover:text-[#1B1A17] bg-[#FFFDF9]/80 rounded-full hover:bg-[#EFE3D3] transition-colors"
          >
            <X className="w-5 h-5" />
          </button>

          <div className="grid grid-cols-1 md:grid-cols-2">
            {/* Left: Product Imagery with marble pedestal backdrop */}
            <div className="relative h-[380px] md:h-full bg-[#F2EBE2] p-8 flex items-center justify-center border-b md:border-b-0 md:border-r border-[#EADFCF] overflow-hidden">
              <div className="absolute inset-0 bg-glow-radial opacity-60 pointer-events-none" />
              <div className="relative w-full h-full max-w-[320px] max-h-[400px] rounded-2xl overflow-hidden shadow-card border border-[#E4D7C5]">
                <Image
                  src={product.image}
                  alt={product.name}
                  fill
                  className="object-cover"
                />
              </div>

              {/* Badges */}
              <div className="absolute top-6 left-6 flex flex-col space-y-1.5">
                {product.isBestSeller && (
                  <span className="px-3 py-1 bg-[#1B1A17] text-[#FFFDF9] text-[10px] tracking-widest uppercase font-medium rounded-full shadow-sm">
                    Iconic Ritual
                  </span>
                )}
                {product.isAwardWinner && (
                  <span className="px-3 py-1 bg-[#C8A46A] text-[#1B1A17] text-[10px] tracking-widest uppercase font-semibold rounded-full shadow-sm">
                    Prix de Beauté
                  </span>
                )}
              </div>
            </div>

            {/* Right: Formulation Details */}
            <div className="p-8 sm:p-10 flex flex-col justify-between space-y-6">
              <div>
                <div className="flex justify-between items-start">
                  <div>
                    <span className="text-xs uppercase tracking-widest font-semibold text-[#7D9075]">
                      {product.category} • {product.concern}
                    </span>
                    <h3 className="font-serif-luxury text-3xl text-[#1B1A17] mt-1">
                      {product.name}
                    </h3>
                    <p className="text-xs text-[#8E857A] italic mt-0.5">{product.frenchSubtitle}</p>
                  </div>
                  <button
                    onClick={() => toggleWishlist(product.id)}
                    className="p-2.5 rounded-full bg-[#F8F3EB] hover:bg-[#EFE3D3] transition-colors border border-[#EADFCF]"
                  >
                    <Heart
                      className={`w-4 h-4 ${isWishlisted ? 'fill-[#C8A46A] text-[#C8A46A]' : 'text-[#1B1A17]'}`}
                    />
                  </button>
                </div>

                {/* Rating & Price */}
                <div className="flex items-center space-x-4 mt-3">
                  <span className="font-serif-luxury text-2xl text-[#1B1A17] font-semibold">
                    ${product.price}
                  </span>
                  <div className="h-4 w-px bg-[#EADFCF]" />
                  <div className="flex items-center space-x-1.5 text-xs text-[#5E584F]">
                    <div className="flex text-[#C8A46A]">
                      {[...Array(5)].map((_, i) => (
                        <Star key={i} className="w-3.5 h-3.5 fill-[#C8A46A]" />
                      ))}
                    </div>
                    <span className="font-medium text-[#1B1A17]">{product.rating}</span>
                    <span className="text-[#8E857A]">({product.reviewsCount} reviews)</span>
                  </div>
                </div>

                <p className="text-xs text-[#5E584F] leading-relaxed mt-4">
                  {product.description}
                </p>

                {/* Key Actives */}
                <div className="mt-5 pt-4 border-t border-[#EAE1D3]">
                  <p className="text-xs font-semibold uppercase tracking-wider text-[#8E857A] mb-2">
                    Primary Bio-Actives
                  </p>
                  <div className="flex flex-wrap gap-1.5">
                    {product.keyActives.map((active) => (
                      <span
                        key={active}
                        className="px-3 py-1 rounded-full text-[11px] bg-[#F8F3EB] text-[#1B1A17] font-medium border border-[#E4D7C5]"
                      >
                        {active}
                      </span>
                    ))}
                  </div>
                </div>

                {/* Clinical claims snippet */}
                {product.clinicalResults.length > 0 && (
                  <div className="mt-4 p-3 rounded-2xl bg-[#EEF2E8] border border-[#7D9075]/30">
                    <p className="text-[11px] font-semibold text-[#55624E] uppercase tracking-wide">
                      Clinical Measurement
                    </p>
                    <p className="text-xs text-[#1B1A17] mt-0.5">
                      <strong>{product.clinicalResults[0].metric}</strong> {product.clinicalResults[0].description}
                    </p>
                  </div>
                )}
              </div>

              {/* Actions */}
              <div className="space-y-4 pt-4 border-t border-[#EAE1D3]">
                <div className="flex items-center space-x-4">
                  <div className="flex items-center border border-[#DCCDBA] rounded-full px-3 py-1.5 bg-[#FFFDF9]">
                    <button
                      onClick={() => setQuantity((q) => Math.max(1, q - 1))}
                      className="p-1 text-[#5E584F] hover:text-[#1B1A17]"
                    >
                      <Minus className="w-3.5 h-3.5" />
                    </button>
                    <span className="text-xs font-semibold px-3 text-[#1B1A17]">{quantity}</span>
                    <button
                      onClick={() => setQuantity((q) => q + 1)}
                      className="p-1 text-[#5E584F] hover:text-[#1B1A17]"
                    >
                      <Plus className="w-3.5 h-3.5" />
                    </button>
                  </div>

                  <button
                    onClick={handleAddToCart}
                    className="flex-1 py-3.5 px-6 bg-[#1B1A17] text-[#FFFDF9] rounded-full text-xs uppercase tracking-widest font-semibold hover:bg-[#322F2A] transition-all flex items-center justify-center space-x-2 shadow-sm"
                  >
                    {addedAnimation ? (
                      <>
                        <Check className="w-4 h-4 text-[#C8A46A]" />
                        <span>Added to Sacred Bag</span>
                      </>
                    ) : (
                      <span>Add to Bag • ${(product.price * quantity).toFixed(2)}</span>
                    )}
                  </button>
                </div>

                <div className="text-center">
                  <Link
                    href={`/product/${product.id}`}
                    onClick={() => setQuickViewProduct(null)}
                    className="inline-flex items-center space-x-1.5 text-xs text-[#8E857A] hover:text-[#1B1A17] underline font-medium"
                  >
                    <span>View Full Clinical Dossier & Usage Ritual</span>
                    <ArrowRight className="w-3 h-3" />
                  </Link>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
