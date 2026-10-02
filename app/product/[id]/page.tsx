'use client';

import React, { useState } from 'react';
import Image from 'next/image';
import Link from 'next/link';
import { notFound, useParams } from 'next/navigation';
import { PRODUCTS } from '@/data/products';
import { useStore } from '@/context/StoreContext';
import ProductCard from '@/components/ProductCard';
import {
  Heart,
  Star,
  ShieldCheck,
  Leaf,
  Plus,
  Minus,
  Check,
  Share2,
  ShoppingBag
} from 'lucide-react';

export default function ProductDetailPage() {
  const params = useParams();
  const id = typeof params?.id === 'string' ? params.id : Array.isArray(params?.id) ? params.id[0] : '';
  const { getProductById, products, addToCart, isInWishlist, toggleWishlist, addToCompare, compareList } = useStore();
  const product = getProductById(id) || products.find((p) => p.id === id);

  if (!product) {
    notFound();
  }

  const [quantity, setQuantity] = useState(1);
  const [selectedImage, setSelectedImage] = useState(product.image);
  const [activeTab, setActiveTab] = useState<'ritual' | 'clinical' | 'ingredients'>('ritual');
  const [addedAnimation, setAddedAnimation] = useState(false);
  const [copiedLink, setCopiedLink] = useState(false);

  const isWishlisted = isInWishlist(product.id);
  const isCompared = compareList.includes(product.id);

  const handleAddToCart = () => {
    addToCart(product, quantity);
    setAddedAnimation(true);
    setTimeout(() => setAddedAnimation(false), 2000);
  };

  const handleShare = () => {
    if (typeof window !== 'undefined') {
      navigator.clipboard?.writeText(window.location.href);
      setCopiedLink(true);
      setTimeout(() => setCopiedLink(false), 2000);
    }
  };

  // Recommendations: Other products in similar concern or category
  const relatedProducts = PRODUCTS.filter((p) => p.id !== product.id).slice(0, 3);

  return (
    <div className="bg-[#F8F3EB] min-h-screen py-8 sm:py-16">
      <div className="max-w-[1440px] mx-auto px-4 sm:px-10 space-y-12 sm:space-y-16">
        {/* Breadcrumb */}
        <nav className="flex items-center space-x-2 text-xs text-[#8E857A] overflow-x-auto whitespace-nowrap">
          <Link href="/" className="hover:text-[#1B1A17]">Maison Élanor</Link>
          <span>/</span>
          <Link href="/shop" className="hover:text-[#1B1A17]">Curated Formulary</Link>
          <span>/</span>
          <Link href={`/shop?category=${product.category}`} className="hover:text-[#1B1A17]">{product.category}</Link>
          <span>/</span>
          <span className="text-[#1B1A17] font-medium truncate">{product.name}</span>
        </nav>

        {/* Product Hero: Visual Gallery & Formulation Dossier */}
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 lg:gap-12 xl:gap-16 items-start">
          {/* Left: Gallery with Marble Pedestal Staging */}
          <div className="lg:col-span-7 space-y-4">
            <div className="relative aspect-[4/5] sm:aspect-square w-full rounded-3xl overflow-hidden bg-[#F2EBE2] border border-[#EADFCF] shadow-card flex items-center justify-center">
              <div className="absolute inset-0 bg-glow-radial opacity-60" />
              <div className="relative w-4/5 h-4/5 rounded-2xl overflow-hidden shadow-floating border border-[#FFFDF9]">
                <Image
                  src={selectedImage}
                  alt={product.name}
                  fill
                  priority
                  className="object-cover transition-all duration-700 hover:scale-105"
                />
              </div>

              {/* Floating Badges */}
              <div className="absolute top-4 left-4 sm:top-6 sm:left-6 flex flex-col space-y-2">
                {product.isBestSeller && (
                  <span className="px-3 py-1 bg-[#1B1A17] text-[#FFFDF9] text-[9px] sm:text-[10px] tracking-widest uppercase font-semibold rounded-full shadow-md">
                    Iconic Masterpiece
                  </span>
                )}
                {product.isAwardWinner && (
                  <span className="px-3 py-1 bg-[#C8A46A] text-[#1B1A17] text-[9px] sm:text-[10px] tracking-widest uppercase font-semibold rounded-full shadow-md">
                    Prix de Haute Beauté
                  </span>
                )}
              </div>
            </div>

            {/* Thumbnail Switcher */}
            <div className="flex space-x-3">
              {[product.image, product.hoverImage || product.image].map((img, idx) => (
                <button
                  key={idx}
                  onClick={() => setSelectedImage(img)}
                  className={`relative w-16 h-16 sm:w-20 sm:h-20 rounded-2xl overflow-hidden border-2 transition-all ${
                    selectedImage === img
                      ? 'border-[#C8A46A] shadow-md scale-105'
                      : 'border-[#EADFCF] opacity-70 hover:opacity-100'
                  }`}
                >
                  <Image src={img} alt={`View ${idx + 1}`} fill className="object-cover" />
                </button>
              ))}
            </div>
          </div>

          {/* Right: Formulation Details & Purchase Ritual */}
          <div className="lg:col-span-5 space-y-6 sm:space-y-8 bg-[#FFFDF9] p-6 sm:p-10 rounded-3xl border border-[#EFE3D3] shadow-card">
            <div>
              <div className="flex items-center justify-between text-xs uppercase tracking-widest text-[#7D9075] font-semibold mb-2">
                <span>{product.category} • {product.concern}</span>
                <div className="flex items-center space-x-2">
                  <button
                    onClick={handleShare}
                    className="p-2 text-[#8E857A] hover:text-[#1B1A17] rounded-full hover:bg-[#F2EBE2]"
                    title="Share ritual link"
                  >
                    <Share2 className="w-4 h-4" />
                  </button>
                  <button
                    onClick={() => toggleWishlist(product.id)}
                    className="p-2 text-[#8E857A] hover:text-[#C8A46A] rounded-full hover:bg-[#F2EBE2]"
                    title="Wishlist"
                  >
                    <Heart className={`w-4 h-4 ${isWishlisted ? 'fill-[#C8A46A] text-[#C8A46A]' : ''}`} />
                  </button>
                </div>
              </div>

              {copiedLink && (
                <p className="text-[11px] text-[#7D9075] font-medium mb-1">
                  ✨ Sacred link copied to clipboard!
                </p>
              )}

              <h1 className="font-serif-luxury text-3xl sm:text-4xl text-[#1B1A17]">
                {product.name}
              </h1>
              <p className="text-xs text-[#8E857A] italic mt-1">{product.frenchSubtitle}</p>

              {/* Price & Reviews */}
              <div className="flex items-center space-x-4 mt-4 pb-4 border-b border-[#EAE1D3]">
                <span className="font-serif-luxury text-2xl sm:text-3xl text-[#1B1A17] font-semibold">
                  ${product.price}
                </span>
                <span className="text-xs text-[#8E857A]">{product.volume}</span>
                <div className="h-4 w-px bg-[#EADFCF]" />
                <div className="flex items-center space-x-1.5 text-xs">
                  <div className="flex text-[#C8A46A]">
                    {[...Array(5)].map((_, i) => (
                      <Star key={i} className="w-3.5 h-3.5 fill-[#C8A46A]" />
                    ))}
                  </div>
                  <span className="font-semibold text-[#1B1A17]">{product.rating}</span>
                  <span className="text-[#8E857A]">({product.reviewsCount})</span>
                </div>
              </div>
            </div>

            <p className="text-xs sm:text-sm text-[#5E584F] leading-relaxed">
              {product.description}
            </p>

            {/* Key Actives Pills */}
            <div className="space-y-2">
              <p className="text-xs uppercase tracking-wider font-semibold text-[#8E857A]">
                Key Active Phytomolecules
              </p>
              <div className="flex flex-wrap gap-1.5 sm:gap-2">
                {product.keyActives.map((act) => (
                  <span
                    key={act}
                    className="px-3 py-1 rounded-full text-[11px] sm:text-xs bg-[#F8F3EB] text-[#1B1A17] font-medium border border-[#E4D7C5]"
                  >
                    {act}
                  </span>
                ))}
              </div>
            </div>

            {/* Sensory Texture & Skin Compatibility */}
            <div className="p-4 rounded-2xl bg-[#F8F3EB] border border-[#EADFCF] space-y-2 text-xs">
              <div className="flex justify-between">
                <span className="text-[#8E857A]">Sensory Finish:</span>
                <span className="font-medium text-[#1B1A17] text-right">{product.texture}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-[#8E857A]">Prescribed For:</span>
                <span className="font-medium text-[#1B1A17] text-right">{product.skinTypes.join(', ')}</span>
              </div>
            </div>

            {/* Quantity & Add to Cart */}
            <div className="space-y-4 pt-2">
              <div className="flex items-center space-x-3 sm:space-x-4">
                <div className="flex items-center border border-[#DCCDBA] rounded-full px-3 py-1.5 sm:px-3.5 sm:py-2 bg-[#FFFDF9]">
                  <button
                    onClick={() => setQuantity((q) => Math.max(1, q - 1))}
                    className="p-1 text-[#5E584F] hover:text-[#1B1A17]"
                  >
                    <Minus className="w-4 h-4" />
                  </button>
                  <span className="text-xs font-semibold px-3 sm:px-4 text-[#1B1A17]">{quantity}</span>
                  <button
                    onClick={() => setQuantity((q) => q + 1)}
                    className="p-1 text-[#5E584F] hover:text-[#1B1A17]"
                  >
                    <Plus className="w-4 h-4" />
                  </button>
                </div>

                <button
                  onClick={handleAddToCart}
                  className="flex-1 py-3.5 sm:py-4 px-6 sm:px-8 bg-[#1B1A17] text-[#FFFDF9] rounded-full text-xs uppercase tracking-widest font-semibold hover:bg-[#322F2A] transition-all shadow-md flex items-center justify-center space-x-2"
                >
                  {addedAnimation ? (
                    <>
                      <Check className="w-4 h-4 text-[#C8A46A]" />
                      <span>Added to Sacred Bag</span>
                    </>
                  ) : (
                    <span>Add To Bag • ${(product.price * quantity).toFixed(2)}</span>
                  )}
                </button>
              </div>

              <div className="flex items-center justify-between text-[11px] text-[#8E857A]">
                <span className="flex items-center gap-1.5">
                  <ShieldCheck className="w-3.5 h-3.5 text-[#7D9075]" />
                  Complimentary 30-Day Guarantee
                </span>
                <button
                  onClick={() => addToCompare(product.id)}
                  className="hover:text-[#1B1A17] underline"
                >
                  {isCompared ? 'In Comparator' : '+ Compare Formula'}
                </button>
              </div>
            </div>
          </div>
        </div>

        {/* Detailed Tabs: Usage Ritual / Clinical Results / Full Ingredients */}
        <div className="bg-[#FFFDF9] rounded-3xl p-6 sm:p-12 border border-[#EFE3D3] shadow-card">
          <div className="flex border-b border-[#EADFCF] space-x-6 sm:space-x-8 mb-6 sm:mb-8 overflow-x-auto whitespace-nowrap">
            <button
              onClick={() => setActiveTab('ritual')}
              className={`pb-3 sm:pb-4 text-xs sm:text-sm uppercase tracking-wider font-semibold transition-colors relative ${
                activeTab === 'ritual'
                  ? 'text-[#1B1A17] font-bold'
                  : 'text-[#8E857A] hover:text-[#1B1A17]'
              }`}
            >
              The Application Ritual
              {activeTab === 'ritual' && (
                <div className="absolute bottom-0 inset-x-0 h-0.5 bg-[#C8A46A]" />
              )}
            </button>

            <button
              onClick={() => setActiveTab('clinical')}
              className={`pb-3 sm:pb-4 text-xs sm:text-sm uppercase tracking-wider font-semibold transition-colors relative ${
                activeTab === 'clinical'
                  ? 'text-[#1B1A17] font-bold'
                  : 'text-[#8E857A] hover:text-[#1B1A17]'
              }`}
            >
              Clinical Trial Dossier
              {activeTab === 'clinical' && (
                <div className="absolute bottom-0 inset-x-0 h-0.5 bg-[#C8A46A]" />
              )}
            </button>

            <button
              onClick={() => setActiveTab('ingredients')}
              className={`pb-3 sm:pb-4 text-xs sm:text-sm uppercase tracking-wider font-semibold transition-colors relative ${
                activeTab === 'ingredients'
                  ? 'text-[#1B1A17] font-bold'
                  : 'text-[#8E857A] hover:text-[#1B1A17]'
              }`}
            >
              Phytochemical Sourcing
              {activeTab === 'ingredients' && (
                <div className="absolute bottom-0 inset-x-0 h-0.5 bg-[#C8A46A]" />
              )}
            </button>
          </div>

          {/* Tab 1: Ritual */}
          {activeTab === 'ritual' && (
            <div className="space-y-6 max-w-3xl">
              <h3 className="font-serif-luxury text-2xl text-[#1B1A17]">The Morning & Evening Anointment</h3>
              <p className="text-xs sm:text-sm text-[#5E584F] leading-relaxed">
                {product.usageRitual}
              </p>
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 pt-4">
                <div className="p-4 rounded-2xl bg-[#F8F3EB] border border-[#EADFCF]">
                  <p className="text-[10px] uppercase tracking-wider font-semibold text-[#8E857A]">Step Sequence</p>
                  <p className="font-serif-luxury text-base sm:text-lg text-[#1B1A17] mt-1">Post-Cleanse / Essence</p>
                </div>
                <div className="p-4 rounded-2xl bg-[#F8F3EB] border border-[#EADFCF]">
                  <p className="text-[10px] uppercase tracking-wider font-semibold text-[#8E857A]">Dosage</p>
                  <p className="font-serif-luxury text-base sm:text-lg text-[#1B1A17] mt-1">3 to 4 Warm Drops</p>
                </div>
                <div className="p-4 rounded-2xl bg-[#F8F3EB] border border-[#EADFCF]">
                  <p className="text-[10px] uppercase tracking-wider font-semibold text-[#8E857A]">Pressure</p>
                  <p className="font-serif-luxury text-base sm:text-lg text-[#1B1A17] mt-1">Lymphatic Upward Glide</p>
                </div>
              </div>
            </div>
          )}

          {/* Tab 2: Clinical */}
          {activeTab === 'clinical' && (
            <div className="space-y-6 max-w-3xl">
              <h3 className="font-serif-luxury text-2xl text-[#1B1A17]">Independent Double-Blind Clinical Efficacy</h3>
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 sm:gap-6 pt-2">
                {product.clinicalResults.map((res, idx) => (
                  <div key={idx} className="p-5 sm:p-6 rounded-2xl bg-[#EEF2E8] border border-[#7D9075]/30 space-y-2">
                    <span className="font-serif-luxury text-3xl sm:text-4xl font-semibold text-[#55624E]">{res.metric}</span>
                    <p className="text-xs text-[#1B1A17] leading-relaxed">{res.description}</p>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Tab 3: Ingredients */}
          {activeTab === 'ingredients' && (
            <div className="space-y-6 max-w-3xl">
              <h3 className="font-serif-luxury text-2xl text-[#1B1A17]">Complete Botanical & Clinical INCI Breakdown</h3>
              <p className="text-xs text-[#5E584F] leading-relaxed">
                Rosa Damascena Flower Water*, Squalane (Sugar Cane Derived), Saffron Stem Extract, Niacinamide, Sodium Hyaluronate Multi-Matrix, Ceramide NP, Ceramide AP, Phytosphingosine, Edelweiss Callus Culture, Camellia Sinensis Leaf Extract, Tocopherol, Bisabolol. (*Certified Organic Wild-Harvest).
              </p>
              <div className="flex items-center space-x-2 text-xs text-[#7D9075] pt-2">
                <Leaf className="w-4 h-4" />
                <span>100% Vegan • Cruelty-Free • Preserved in Miron Violet Glass</span>
              </div>
            </div>
          )}
        </div>

        {/* Paired Ritual Recommendations */}
        <div className="space-y-8">
          <div className="flex justify-between items-end">
            <div>
              <span className="text-xs uppercase tracking-widest text-[#7D9075] font-semibold">Harmonious Alchemy</span>
              <h2 className="font-serif-luxury text-2xl sm:text-4xl text-[#1B1A17] mt-1">Complete The Sacred Ritual</h2>
            </div>
            <Link href="/shop" className="text-xs uppercase tracking-widest font-semibold text-[#1B1A17] hover:text-[#C8A46A]">
              Explore All →
            </Link>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {relatedProducts.map((p) => (
              <ProductCard key={p.id} product={p} />
            ))}
          </div>
        </div>
      </div>

      {/* Sticky Mobile Purchase Bar */}
      <div className="lg:hidden fixed bottom-14 inset-x-0 z-30 bg-[#FFFDF9]/95 backdrop-blur-md border-t border-[#EADFCF] p-3 px-4 shadow-card flex items-center justify-between">
        <div>
          <h4 className="font-serif-luxury text-sm font-semibold text-[#1B1A17] line-clamp-1">{product.name}</h4>
          <p className="text-xs font-bold text-[#1B1A17]">${product.price}</p>
        </div>
        <button
          onClick={handleAddToCart}
          className="px-5 py-2.5 bg-[#1B1A17] text-[#FFFDF9] rounded-full text-xs uppercase tracking-widest font-semibold hover:bg-[#322F2A] flex items-center space-x-1.5 shadow-sm shrink-0"
        >
          <ShoppingBag className="w-3.5 h-3.5" />
          <span>Add to Bag</span>
        </button>
      </div>
    </div>
  );
}
