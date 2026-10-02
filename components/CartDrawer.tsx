'use client';

import React from 'react';
import Link from 'next/link';
import Image from 'next/image';
import { useStore, COMPLIMENTARY_SAMPLES, AVAILABLE_COUPONS } from '@/context/StoreContext';
import { X, Plus, Minus, Trash2, Gift, Sparkles, ArrowRight, ShieldCheck, Tag } from 'lucide-react';

export default function CartDrawer() {
  const {
    cart,
    isCartOpen,
    setIsCartOpen,
    removeFromCart,
    updateQuantity,
    cartSubtotal,
    freeShippingThreshold,
    freeShippingProgress,
    isFreeShipping,
    selectedSample,
    setSelectedSample,
    isGiftWrap,
    setIsGiftWrap,
    giftNote,
    setGiftNote,
    appliedCoupon,
    couponDiscount,
    applyCoupon,
    removeCoupon
  } = useStore();

  const [couponInput, setCouponInput] = React.useState('');
  const [couponFeedback, setCouponFeedback] = React.useState<{ success: boolean; message: string } | null>(null);

  if (!isCartOpen) return null;

  const giftWrapFee = isGiftWrap ? 15 : 0;
  const discountedSubtotal = Math.max(0, cartSubtotal - couponDiscount);
  const grandTotal = discountedSubtotal + giftWrapFee;

  const handleApplyCoupon = (codeToApply?: string) => {
    const code = codeToApply || couponInput;
    if (!code.trim()) return;
    const res = applyCoupon(code);
    setCouponFeedback(res);
    if (res.success) {
      setCouponInput('');
    }
  };

  return (
    <div className="fixed inset-0 z-50 overflow-hidden">
      {/* Backdrop */}
      <div
        onClick={() => setIsCartOpen(false)}
        className="absolute inset-0 bg-[#1B1A17]/40 backdrop-blur-sm transition-opacity"
      />

      <div className="fixed inset-y-0 right-0 max-w-full flex pl-10">
        <div className="w-screen max-w-md bg-[#FFFDF9] shadow-2xl flex flex-col justify-between border-l border-[#EADFCF]">
          {/* Header */}
          <div className="p-6 border-b border-[#EADFCF] bg-[#F8F3EB]/60">
            <div className="flex items-center justify-between">
              <div>
                <h2 className="font-serif-luxury text-2xl text-[#1B1A17]">Your Shopping Bag</h2>
                <p className="text-xs text-[#8E857A] mt-0.5">Complimentary shipping on orders over ${freeShippingThreshold}</p>
              </div>
              <button
                onClick={() => setIsCartOpen(false)}
                className="p-2 text-[#5E584F] hover:text-[#1B1A17] rounded-full hover:bg-[#EFE3D3] transition-colors"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            {/* Free Shipping Meter */}
            <div className="mt-4 pt-3 border-t border-[#EAE1D3]">
              <div className="flex justify-between text-xs font-medium text-[#1B1A17] mb-1.5">
                <span>
                  {isFreeShipping
                    ? '✨ You have unlocked Complimentary Express Shipping!'
                    : `Add $${(freeShippingThreshold - cartSubtotal).toFixed(2)} more for free shipping`}
                </span>
                <span>{freeShippingProgress}%</span>
              </div>
              <div className="w-full bg-[#EADFCF] h-1.5 rounded-full overflow-hidden">
                <div
                  className="bg-[#C8A46A] h-full rounded-full transition-all duration-500"
                  style={{ width: `${freeShippingProgress}%` }}
                />
              </div>
            </div>
          </div>

          {/* Cart Items List */}
          <div className="flex-1 overflow-y-auto p-6 space-y-6">
            {cart.length === 0 ? (
              <div className="text-center py-16 space-y-4">
                <div className="w-16 h-16 rounded-full bg-[#F2EBE2] mx-auto flex items-center justify-center text-[#8E857A]">
                  <Sparkles className="w-8 h-8 text-[#C8A46A]" />
                </div>
                <h3 className="font-serif-luxury text-xl text-[#1B1A17]">Your bag is currently empty</h3>
                <p className="text-xs text-[#5E584F] max-w-xs mx-auto">
                  Discover our pure botanical elixirs and personalized skin rituals.
                </p>
                <Link
                  href="/shop"
                  onClick={() => setIsCartOpen(false)}
                  className="inline-block mt-3 px-6 py-3 bg-[#1B1A17] text-[#FFFDF9] text-xs uppercase tracking-widest rounded-full hover:bg-[#322F2A] transition-colors"
                >
                  Explore The Collection
                </Link>
              </div>
            ) : (
              <>
                <div className="space-y-4">
                  {cart.map((item) => (
                    <div
                      key={item.product.id}
                      className="flex space-x-4 p-3.5 rounded-2xl bg-[#FDFBF8] border border-[#EFE3D3] hover:border-[#DCCDBA] transition-colors"
                    >
                      <div className="relative w-20 h-24 rounded-xl overflow-hidden bg-[#F2EBE2] shrink-0 border border-[#EADFCF]">
                        <Image
                          src={item.product.image}
                          alt={item.product.name}
                          fill
                          className="object-cover"
                        />
                      </div>

                      <div className="flex-1 flex flex-col justify-between">
                        <div>
                          <div className="flex justify-between items-start">
                            <h4 className="font-serif-luxury text-base text-[#1B1A17] leading-snug line-clamp-1">
                              {item.product.name}
                            </h4>
                            <span className="font-medium text-sm text-[#1B1A17]">
                              ${item.product.price * item.quantity}
                            </span>
                          </div>
                          <p className="text-[11px] text-[#8E857A]">{item.product.volume}</p>
                        </div>

                        <div className="flex items-center justify-between mt-3">
                          <div className="flex items-center space-x-2 border border-[#EADFCF] rounded-full px-2 py-0.5 bg-[#FFFDF9]">
                            <button
                              onClick={() => updateQuantity(item.product.id, -1)}
                              className="p-1 text-[#5E584F] hover:text-[#1B1A17]"
                            >
                              <Minus className="w-3 h-3" />
                            </button>
                            <span className="text-xs font-semibold px-1">{item.quantity}</span>
                            <button
                              onClick={() => updateQuantity(item.product.id, 1)}
                              className="p-1 text-[#5E584F] hover:text-[#1B1A17]"
                            >
                              <Plus className="w-3 h-3" />
                            </button>
                          </div>

                          <button
                            onClick={() => removeFromCart(item.product.id)}
                            className="text-[#8E857A] hover:text-[#B84A4A] transition-colors p-1"
                            title="Remove from bag"
                          >
                            <Trash2 className="w-4 h-4" />
                          </button>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>

                {/* Complimentary Deluxe Sample Selector */}
                <div className="p-4 rounded-2xl bg-[#F8F3EB] border border-[#E4C894]/60">
                  <div className="flex items-center space-x-2 mb-2">
                    <Sparkles className="w-4 h-4 text-[#C8A46A]" />
                    <span className="text-xs font-semibold tracking-wide uppercase text-[#1B1A17]">
                      Choose 1 Complimentary Deluxe Sample
                    </span>
                  </div>
                  <div className="grid grid-cols-3 gap-2 mt-3">
                    {COMPLIMENTARY_SAMPLES.map((sample) => (
                      <button
                        key={sample.id}
                        type="button"
                        onClick={() => setSelectedSample(sample.id)}
                        className={`p-2 rounded-xl text-left text-xs transition-all border ${
                          selectedSample === sample.id
                            ? 'bg-[#FFFDF9] border-[#C8A46A] shadow-sm ring-1 ring-[#C8A46A]'
                            : 'bg-[#FFFDF9]/60 border-[#EADFCF] hover:bg-[#FFFDF9]'
                        }`}
                      >
                        <p className="font-serif-luxury text-[13px] font-semibold text-[#1B1A17] line-clamp-1">
                          {sample.name}
                        </p>
                        <p className="text-[10px] text-[#8E857A]">{sample.size}</p>
                      </button>
                    ))}
                  </div>
                </div>

                {/* Luxury Gift Packaging Option */}
                <div className="p-4 rounded-2xl bg-[#FFFDF9] border border-[#EADFCF] space-y-3">
                  <label className="flex items-start space-x-3 cursor-pointer">
                    <input
                      type="checkbox"
                      checked={isGiftWrap}
                      onChange={(e) => setIsGiftWrap(e.target.checked)}
                      className="mt-1 accent-[#C8A46A] rounded"
                    />
                    <div className="text-xs">
                      <span className="font-semibold text-[#1B1A17] flex items-center gap-1.5">
                        <Gift className="w-3.5 h-3.5 text-[#C8A46A]" />
                        Élanor Signature Gold Rigid Gift Box (+$15)
                      </span>
                      <p className="text-[#8E857A] mt-0.5">
                        Includes embossed silk ribbon and handwritten calligraphy note.
                      </p>
                    </div>
                  </label>

                  {isGiftWrap && (
                    <textarea
                      placeholder="Enter your personal gift message for the recipient..."
                      value={giftNote}
                      onChange={(e) => setGiftNote(e.target.value)}
                      rows={2}
                      className="w-full text-xs p-2.5 rounded-xl border border-[#DCCDBA] bg-[#FDFBF8] focus:outline-none focus:ring-1 focus:ring-[#C8A46A] text-[#1B1A17]"
                    />
                  )}
                </div>

                {/* Promotional Coupon Section */}
                <div className="p-4 rounded-2xl bg-[#FFFDF9] border border-[#EADFCF] space-y-2.5">
                  <div className="flex items-center justify-between">
                    <span className="text-xs font-semibold text-[#1B1A17] flex items-center space-x-1.5">
                      <Tag className="w-3.5 h-3.5 text-[#C8A46A]" />
                      <span>Promotional Privilege Code</span>
                    </span>
                    {appliedCoupon && (
                      <span className="text-[10px] font-bold text-[#7D9075] uppercase tracking-wider bg-[#EEF2E8] px-2 py-0.5 rounded-full">
                        Active
                      </span>
                    )}
                  </div>

                  <div className="flex space-x-2">
                    <input
                      type="text"
                      placeholder="e.g. HAUTE20"
                      value={couponInput}
                      onChange={(e) => setCouponInput(e.target.value.toUpperCase())}
                      className="flex-1 text-xs px-3 py-2 rounded-xl border border-[#DCCDBA] bg-[#FDFBF8] focus:outline-none focus:ring-1 focus:ring-[#C8A46A] font-mono uppercase text-[#1B1A17]"
                    />
                    <button
                      type="button"
                      onClick={() => handleApplyCoupon()}
                      className="px-3.5 py-2 bg-[#1B1A17] text-[#FFFDF9] rounded-xl text-xs font-semibold hover:bg-[#322F2A] transition-colors cursor-pointer"
                    >
                      Apply
                    </button>
                  </div>

                  {couponFeedback && (
                    <p
                      className={`text-[11px] ${
                        couponFeedback.success ? 'text-[#7D9075] font-semibold' : 'text-[#A8381D]'
                      }`}
                    >
                      {couponFeedback.message}
                    </p>
                  )}

                  {/* Available Clickable Promo Badges */}
                  <div className="pt-1 flex flex-wrap gap-1.5">
                    {AVAILABLE_COUPONS.slice(0, 3).map((cp) => (
                      <button
                        key={cp.code}
                        type="button"
                        onClick={() => handleApplyCoupon(cp.code)}
                        className={`px-2 py-0.5 rounded-lg text-[10px] font-mono font-semibold transition-all cursor-pointer border ${
                          appliedCoupon?.code === cp.code
                            ? 'bg-[#C8A46A] text-[#1B1A17] border-[#C8A46A] shadow-xs'
                            : 'bg-[#F8F3EB] text-[#5E584F] border-[#EADFCF] hover:bg-[#F2EBE2] hover:text-[#1B1A17]'
                        }`}
                      >
                        {cp.code} ({cp.type === 'PERCENTAGE' ? `${cp.value}%` : `$${cp.value}`})
                      </button>
                    ))}
                  </div>
                </div>
              </>
            )}
          </div>

          {/* Footer / Checkout CTA */}
          {cart.length > 0 && (
            <div className="p-6 border-t border-[#EADFCF] bg-[#F8F3EB]/80 space-y-4">
              <div className="space-y-1.5 text-xs text-[#5E584F]">
                <div className="flex justify-between">
                  <span>Bag Subtotal</span>
                  <span className="font-medium text-[#1B1A17]">${cartSubtotal.toFixed(2)}</span>
                </div>

                {/* Applied Discount Line */}
                {appliedCoupon && couponDiscount > 0 && (
                  <div className="flex justify-between text-[#7D9075] font-semibold">
                    <span className="flex items-center space-x-1">
                      <Tag className="w-3.5 h-3.5 text-[#C8A46A]" />
                      <span>Privilege ({appliedCoupon.code})</span>
                    </span>
                    <div className="flex items-center space-x-1.5">
                      <span>-${couponDiscount.toFixed(2)}</span>
                      <button
                        type="button"
                        onClick={removeCoupon}
                        title="Remove coupon"
                        className="text-[#8E857A] hover:text-[#A8381D] p-0.5 cursor-pointer"
                      >
                        <X className="w-3 h-3" />
                      </button>
                    </div>
                  </div>
                )}

                {isGiftWrap && (
                  <div className="flex justify-between text-[#C8A46A]">
                    <span>Signature Gift Packaging</span>
                    <span>+$15.00</span>
                  </div>
                )}
                <div className="flex justify-between text-sm font-semibold text-[#1B1A17] pt-2 border-t border-[#EAE1D3]">
                  <span>Estimated Total</span>
                  <span className="font-serif-luxury text-lg">${grandTotal.toFixed(2)}</span>
                </div>
              </div>

              <div className="space-y-2">
                <Link
                  href="/checkout"
                  onClick={() => setIsCartOpen(false)}
                  className="w-full py-4 bg-[#1B1A17] text-[#FFFDF9] rounded-full text-xs uppercase tracking-widest font-semibold flex items-center justify-center space-x-2 hover:bg-[#322F2A] transition-all shadow-md group"
                >
                  <span>Proceed To Checkout</span>
                  <ArrowRight className="w-4 h-4 group-hover:translate-x-1 transition-transform" />
                </Link>

                <Link
                  href="/cart"
                  onClick={() => setIsCartOpen(false)}
                  className="w-full py-2.5 text-center text-xs text-[#5E584F] hover:text-[#1B1A17] block font-medium underline"
                >
                  View Full Bag & Ritual Details
                </Link>
              </div>

              <div className="flex items-center justify-center space-x-2 text-[11px] text-[#8E857A]">
                <ShieldCheck className="w-3.5 h-3.5 text-[#7D9075]" />
                <span>100% Secure Checkout & Carbon-Neutral Luxury Packaging</span>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
