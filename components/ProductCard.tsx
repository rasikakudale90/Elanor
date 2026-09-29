'use client';

import React, { useState } from 'react';
import Image from 'next/image';
import Link from 'next/link';
import { Product } from '@/data/products';
import { useStore } from '@/context/StoreContext';
import { Heart, Eye, ShoppingBag, Check, SlidersHorizontal } from 'lucide-react';

interface ProductCardProps {
  product: Product;
  showCategoryBadge?: boolean;
}

export default function ProductCard({ product, showCategoryBadge = true }: ProductCardProps) {
  const { addToCart, isInWishlist, toggleWishlist, setQuickViewProduct, addToCompare, compareList } = useStore();
  const [isHovered, setIsHovered] = useState(false);
  const [added, setAdded] = useState(false);

  const isWishlisted = isInWishlist(product.id);
  const isCompared = compareList.includes(product.id);

  const handleAdd = (e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    addToCart(product, 1);
    setAdded(true);
    setTimeout(() => setAdded(false), 1800);
  };

  const handleQuickView = (e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    setQuickViewProduct(product);
  };

  const handleWishlist = (e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    toggleWishlist(product.id);
  };

  const handleCompare = (e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    addToCompare(product.id);
  };

  return (
    <div
      className="group relative flex flex-col bg-[#FFFDF9] rounded-3xl p-4 sm:p-5 border border-[#EFE3D3] transition-all duration-500 hover:shadow-card hover:-translate-y-2 hover:border-[#DCCDBA]"
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
    >
      {/* Visual Canvas (Pedestal staging & Aspect Ratio 4:5) */}
      <div className="relative aspect-[4/5] w-full rounded-2xl overflow-hidden bg-[#F2EBE2] border border-[#EAE1D3] flex items-center justify-center">
        {/* Soft Radial Glow */}
        <div className="absolute inset-0 bg-glow-radial opacity-50 transition-opacity duration-500 group-hover:opacity-90" />

        {/* Primary Image */}
        <Image
          src={isHovered && product.hoverImage ? product.hoverImage : product.image}
          alt={product.name}
          fill
          className="object-cover transition-transform duration-700 group-hover:scale-105"
        />

        {/* Top Badges */}
        <div className="absolute top-3 left-3 flex flex-col space-y-1.5 z-10">
          {product.isBestSeller && (
            <span className="px-2.5 py-0.5 bg-[#1B1A17] text-[#FFFDF9] text-[9px] tracking-widest uppercase font-semibold rounded-full shadow-sm">
              Iconic
            </span>
          )}
          {product.isNew && (
            <span className="px-2.5 py-0.5 bg-[#7D9075] text-[#FFFDF9] text-[9px] tracking-widest uppercase font-semibold rounded-full shadow-sm">
              New Ritual
            </span>
          )}
          {product.isAwardWinner && (
            <span className="px-2.5 py-0.5 bg-[#C8A46A] text-[#1B1A17] text-[9px] tracking-widest uppercase font-semibold rounded-full shadow-sm">
              Awarded
            </span>
          )}
        </div>

        {/* Wishlist Button (Floating Top Right) */}
        <button
          onClick={handleWishlist}
          aria-label={isWishlisted ? 'Remove from wishlist' : 'Add to wishlist'}
          className={`absolute top-3 right-3 z-10 p-2 rounded-full backdrop-blur-md transition-all ${
            isWishlisted
              ? 'bg-[#1B1A17] text-[#C8A46A]'
              : 'bg-[#FFFDF9]/85 text-[#1B1A17] hover:bg-[#FFFDF9] hover:text-[#C8A46A] shadow-sm'
          }`}
        >
          <Heart className={`w-3.5 h-3.5 ${isWishlisted ? 'fill-[#C8A46A]' : ''}`} />
        </button>

        {/* Hover Quick Action Buttons */}
        <div className="absolute bottom-3 inset-x-3 flex items-center justify-center space-x-2 z-10 opacity-0 translate-y-2 group-hover:opacity-100 group-hover:translate-y-0 transition-all duration-300">
          <button
            onClick={handleQuickView}
            className="flex-1 py-2 px-3 bg-[#FFFDF9]/95 text-[#1B1A17] text-[11px] font-semibold tracking-wider rounded-full shadow-sm hover:bg-[#1B1A17] hover:text-[#FFFDF9] transition-colors flex items-center justify-center space-x-1 border border-[#E4D7C5]"
          >
            <Eye className="w-3.5 h-3.5" />
            <span>Quick View</span>
          </button>

          <button
            onClick={handleCompare}
            title={isCompared ? 'In comparator' : 'Add to comparator'}
            className={`p-2 rounded-full border border-[#E4D7C5] shadow-sm transition-colors ${
              isCompared
                ? 'bg-[#C8A46A] text-[#1B1A17]'
                : 'bg-[#FFFDF9]/95 text-[#1B1A17] hover:bg-[#1B1A17] hover:text-[#FFFDF9]'
            }`}
          >
            <SlidersHorizontal className="w-3.5 h-3.5" />
          </button>
        </div>
      </div>

      {/* Product Content Details */}
      <div className="mt-4 flex-1 flex flex-col justify-between">
        <div>
          {showCategoryBadge && (
            <div className="flex items-center justify-between text-[11px] uppercase tracking-wider text-[#8E857A] font-medium mb-1">
              <span>{product.category}</span>
              <span className="text-[#7D9075] font-semibold">{product.concern}</span>
            </div>
          )}

          <Link href={`/product/${product.id}`} className="block group-hover:text-[#C8A46A] transition-colors">
            <h3 className="font-serif-luxury text-lg font-semibold text-[#1B1A17] line-clamp-1">
              {product.name}
            </h3>
            <p className="text-[11px] text-[#8E857A] italic line-clamp-1">{product.frenchSubtitle}</p>
          </Link>

          <p className="text-xs text-[#5E584F] line-clamp-2 mt-2 leading-relaxed">
            {product.tagline}
          </p>
        </div>

        <div className="mt-4 pt-3 border-t border-[#EFE3D3] flex items-center justify-between">
          <div>
            <span className="font-serif-luxury text-lg font-semibold text-[#1B1A17]">
              ${product.price}
            </span>
            <span className="block text-[10px] text-[#8E857A]">{product.volume}</span>
          </div>

          <button
            onClick={handleAdd}
            aria-label="Add to Bag"
            className="p-2.5 rounded-full bg-[#1B1A17] text-[#FFFDF9] hover:bg-[#322F2A] transition-all flex items-center space-x-1.5 px-3.5 shadow-sm group/btn"
          >
            {added ? (
              <>
                <Check className="w-3.5 h-3.5 text-[#C8A46A]" />
                <span className="text-[11px] font-semibold tracking-wider uppercase text-[#C8A46A]">Added</span>
              </>
            ) : (
              <>
                <ShoppingBag className="w-3.5 h-3.5 group-hover/btn:scale-110 transition-transform" />
                <span className="text-[11px] font-semibold tracking-wider uppercase">Bag</span>
              </>
            )}
          </button>
        </div>
      </div>
    </div>
  );
}
