'use client';

import React from 'react';
import Link from 'next/link';
import Image from 'next/image';
import { useStore, COMPLIMENTARY_SAMPLES, AVAILABLE_COUPONS } from '@/context/StoreContext';
import { Trash2, Plus, Minus, Gift, Sparkles, ArrowRight, ShieldCheck, ArrowLeft, Tag, X } from 'lucide-react';

export default function CartPage() {
  const {
    cart,
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

  const giftWrapFee = isGiftWrap ? 15 : 0;
  const discountedSubtotal = Math.max(0, cartSubtotal - couponDiscount);
  const estimatedTax = discountedSubtotal * 0.08;
  const shippingFee = isFreeShipping ? 0 : 15;
  const grandTotal = discountedSubtotal + giftWrapFee + shippingFee + estimatedTax;

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
    <div className="bg-[#F8F3EB] min-h-screen py-12 sm:py-16">
      <div className="max-w-[1440px] mx-auto px-6 sm:px-10">
        <div className="mb-10 flex items-center justify-between border-b border-[#EADFCF] pb-6">
          <div>
            <span className="text-xs uppercase tracking-widest text-[#7D9075] font-semibold">
              Maison Élanor Atelier
            </span>
            <h1 className="font-serif-luxury text-4xl sm:text-5xl text-[#1B1A17] mt-1">
              Your Sacred Bag
            </h1>
          </div>
          <Link
            href="/shop"
            className="text-xs uppercase tracking-widest font-semibold text-[#1B1A17] hover:text-[#C8A46A] flex items-center space-x-1"
          >
            <ArrowLeft className="w-3.5 h-3.5" />
            <span>Continue Shopping</span>
          </Link>
        </div>

        {cart.length === 0 ? (
          <div className="text-center py-24 bg-[#FFFDF9] rounded-3xl border border-[#EFE3D3] p-10 space-y-6 max-w-xl mx-auto shadow-sm">
            <div className="w-20 h-20 rounded-full bg-[#F2EBE2] mx-auto flex items-center justify-center text-[#C8A46A]">
              <Sparkles className="w-10 h-10" />
            </div>
            <h2 className="font-serif-luxury text-3xl text-[#1B1A17]">Your bag is currently empty</h2>
            <p className="text-xs sm:text-sm text-[#5E584F] leading-relaxed">
              Explore our biocompatible formulations and curated rituals to begin your skin metamorphosis.
            </p>
            <Link
              href="/shop"
              className="inline-block px-8 py-4 bg-[#1B1A17] text-[#FFFDF9] rounded-full text-xs uppercase tracking-widest font-semibold hover:bg-[#322F2A] transition-all shadow-md"
            >
              Explore The Formulary
            </Link>
          </div>
        ) : (
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-12">
            {/* Left Column: Cart Items & Sample Selector */}
            <div className="lg:col-span-8 space-y-8">
              {/* Shipping Progress Bar */}
              <div className="bg-[#FFFDF9] p-6 rounded-3xl border border-[#EFE3D3] shadow-sm space-y-3">
                <div className="flex justify-between text-xs font-semibold text-[#1B1A17]">
                  <span>
                    {isFreeShipping
                      ? '✨ You have unlocked Complimentary Express Shipping!'
                      : `Add $${(freeShippingThreshold - cartSubtotal).toFixed(2)} more to receive complimentary express shipping`}
                  </span>
                  <span>{freeShippingProgress}%</span>
                </div>
                <div className="w-full bg-[#EADFCF] h-2 rounded-full overflow-hidden">
                  <div
                    className="bg-[#C8A46A] h-full rounded-full transition-all duration-500"
                    style={{ width: `${freeShippingProgress}%` }}
                  />
                </div>
              </div>

              {/* Items Table */}
              <div className="bg-[#FFFDF9] rounded-3xl border border-[#EFE3D3] overflow-hidden shadow-sm divide-y divide-[#EAE1D3]">
                {cart.map((item) => (
                  <div key={item.product.id} className="p-6 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-6 hover:bg-[#FDFBF8] transition-colors">
                    <div className="flex items-center space-x-4">
                      <div className="relative w-24 h-28 rounded-2xl overflow-hidden bg-[#F2EBE2] border border-[#EADFCF] shrink-0">
                        <Image
                          src={item.product.image}
                          alt={item.product.name}
                          fill
                          className="object-cover"
                        />
                      </div>
                      <div>
                        <span className="text-[10px] uppercase tracking-wider font-semibold text-[#7D9075]">
                          {item.product.category}
                        </span>
                        <Link href={`/product/${item.product.id}`} className="hover:text-[#C8A46A]">
                          <h3 className="font-serif-luxury text-xl text-[#1B1A17]">
                            {item.product.name}
                          </h3>
                        </Link>
                        <p className="text-xs text-[#8E857A] mt-0.5">{item.product.volume}</p>
                        <p className="text-xs font-semibold text-[#1B1A17] sm:hidden mt-2">
                          ${item.product.price} each
                        </p>
                      </div>
                    </div>

                    <div className="flex items-center justify-between w-full sm:w-auto space-x-6">
                      <div className="flex items-center border border-[#DCCDBA] rounded-full px-3 py-1.5 bg-[#FFFDF9]">
                        <button
                          onClick={() => updateQuantity(item.product.id, -1)}
                          className="p-1 text-[#5E584F] hover:text-[#1B1A17]"
                        >
                          <Minus className="w-3.5 h-3.5" />
                        </button>
                        <span className="text-xs font-semibold px-3 text-[#1B1A17]">
                          {item.quantity}
                        </span>
                        <button
                          onClick={() => updateQuantity(item.product.id, 1)}
                          className="p-1 text-[#5E584F] hover:text-[#1B1A17]"
                        >
                          <Plus className="w-3.5 h-3.5" />
                        </button>
                      </div>

                      <span className="font-serif-luxury text-lg font-semibold text-[#1B1A17] min-w-[70px] text-right">
                        ${(item.product.price * item.quantity).toFixed(2)}
                      </span>

                      <button
                        onClick={() => removeFromCart(item.product.id)}
                        className="p-2 text-[#8E857A] hover:text-[#B84A4A] transition-colors rounded-full hover:bg-[#F2EBE2]"
                        title="Remove from bag"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </div>
                  </div>
                ))}
              </div>

              {/* Complimentary Deluxe Sample Selector */}
              <div className="bg-[#FFFDF9] p-6 sm:p-8 rounded-3xl border border-[#E4C894] shadow-sm space-y-4">
                <div className="flex items-center space-x-2">
                  <Sparkles className="w-4 h-4 text-[#C8A46A]" />
                  <h3 className="font-serif-luxury text-xl text-[#1B1A17]">
                    Select Your Complimentary Bespoke Discovery Sample
                  </h3>
                </div>
                <p className="text-xs text-[#5E584F]">
                  Included with every Élanor dispatch. Sealed in violet glass trial vials.
                </p>

                <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 pt-2">
                  {COMPLIMENTARY_SAMPLES.map((sample) => (
                    <button
                      key={sample.id}
                      type="button"
                      onClick={() => setSelectedSample(sample.id)}
                      className={`p-4 rounded-2xl text-left transition-all border ${
                        selectedSample === sample.id
                          ? 'bg-[#F8F3EB] border-[#C8A46A] ring-1 ring-[#C8A46A] shadow-sm'
                          : 'bg-[#FFFDF9] border-[#EADFCF] hover:bg-[#FDFBF8]'
                      }`}
                    >
                      <span className="text-[10px] uppercase tracking-wider font-semibold text-[#7D9075]">
                        {sample.category}
                      </span>
                      <h4 className="font-serif-luxury text-base font-semibold text-[#1B1A17] mt-1">
                        {sample.name}
                      </h4>
                      <p className="text-[11px] text-[#8E857A] mt-0.5">{sample.size} Trial Dropper</p>
                    </button>
                  ))}
                </div>
              </div>

              {/* Gift Wrap Packaging Toggle */}
              <div className="bg-[#FFFDF9] p-6 sm:p-8 rounded-3xl border border-[#EFE3D3] shadow-sm space-y-4">
                <label className="flex items-start space-x-3.5 cursor-pointer">
                  <input
                    type="checkbox"
                    checked={isGiftWrap}
                    onChange={(e) => setIsGiftWrap(e.target.checked)}
                    className="mt-1 accent-[#C8A46A] w-4 h-4"
                  />
                  <div className="text-xs">
                    <span className="font-semibold text-sm text-[#1B1A17] flex items-center gap-2">
                      <Gift className="w-4 h-4 text-[#C8A46A]" />
                      Add Maison Élanor Gold Rigid Gift Box (+$15)
                    </span>
                    <p className="text-[#8E857A] mt-1">
                      Includes handmade silk ribbon, gold hot-stamped seal, and a personalized hand-calligraphed note.
                    </p>
                  </div>
                </label>

                {isGiftWrap && (
                  <div className="pt-2">
                    <textarea
                      placeholder="Write your personal gift correspondence here..."
                      value={giftNote}
                      onChange={(e) => setGiftNote(e.target.value)}
                      rows={3}
                      className="w-full text-xs p-3.5 rounded-2xl border border-[#DCCDBA] bg-[#FDFBF8] focus:outline-none focus:ring-1 focus:ring-[#C8A46A] text-[#1B1A17]"
                    />
                  </div>
                )}
              </div>
            </div>

            {/* Right Column: Order Summary & Checkout CTA */}
            <div className="lg:col-span-4 space-y-6">
              <div className="bg-[#FFFDF9] p-8 rounded-3xl border border-[#EFE3D3] shadow-card space-y-6 sticky top-28">
                <h3 className="font-serif-luxury text-2xl text-[#1B1A17] border-b border-[#EADFCF] pb-4">
                  Order Summary
                </h3>

                {/* Promotional Coupon Box */}
                <div className="pt-2 pb-2 space-y-2.5">
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
                      className="flex-1 text-xs px-3.5 py-2.5 rounded-xl border border-[#DCCDBA] bg-[#FDFBF8] focus:outline-none focus:ring-1 focus:ring-[#C8A46A] font-mono uppercase text-[#1B1A17]"
                    />
                    <button
                      type="button"
                      onClick={() => handleApplyCoupon()}
                      className="px-4 py-2.5 bg-[#1B1A17] text-[#FFFDF9] rounded-xl text-xs font-semibold hover:bg-[#322F2A] transition-colors cursor-pointer"
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

                  {/* Clickable Available Promo Badges */}
                  <div className="space-y-1 pt-1">
                    <p className="text-[10px] uppercase tracking-wider text-[#8E857A]">
                      Available Codes (Click to Apply):
                    </p>
                    <div className="flex flex-wrap gap-1.5">
                      {AVAILABLE_COUPONS.map((cp) => (
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
                </div>

                <div className="space-y-3 text-xs text-[#5E584F] pt-3 border-t border-[#EAE1D3]">
                  <div className="flex justify-between">
                    <span>Bag Subtotal</span>
                    <span className="font-semibold text-[#1B1A17]">${cartSubtotal.toFixed(2)}</span>
                  </div>

                  {/* Applied Discount Line */}
                  {appliedCoupon && couponDiscount > 0 && (
                    <div className="flex justify-between text-[#7D9075] font-semibold">
                      <span className="flex items-center space-x-1">
                        <Tag className="w-3.5 h-3.5 text-[#C8A46A]" />
                        <span>Maison Privilege ({appliedCoupon.code})</span>
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

                  <div className="flex justify-between">
                    <span>Express Carbon-Neutral Shipping</span>
                    <span className="font-semibold text-[#1B1A17]">
                      {isFreeShipping ? 'FREE' : '$15.00'}
                    </span>
                  </div>

                  {isGiftWrap && (
                    <div className="flex justify-between text-[#C8A46A]">
                      <span>Gold Rigid Gift Packaging</span>
                      <span className="font-semibold">+$15.00</span>
                    </div>
                  )}

                  <div className="flex justify-between">
                    <span>Estimated Local Taxes</span>
                    <span className="font-semibold text-[#1B1A17]">${estimatedTax.toFixed(2)}</span>
                  </div>

                  <div className="pt-4 border-t border-[#EAE1D3] flex justify-between items-center text-sm text-[#1B1A17]">
                    <span className="font-semibold">Grand Total</span>
                    <span className="font-serif-luxury text-2xl font-bold">
                      ${grandTotal.toFixed(2)}
                    </span>
                  </div>
                </div>

                <Link
                  href="/checkout"
                  className="w-full py-4 bg-[#1B1A17] text-[#FFFDF9] rounded-full text-xs uppercase tracking-widest font-semibold hover:bg-[#322F2A] transition-all shadow-md flex items-center justify-center space-x-2 group"
                >
                  <span>Proceed To Sacred Checkout</span>
                  <ArrowRight className="w-4 h-4 group-hover:translate-x-1.5 transition-transform" />
                </Link>

                <div className="pt-4 border-t border-[#EAE1D3] space-y-2 text-[11px] text-[#8E857A]">
                  <div className="flex items-center space-x-2">
                    <ShieldCheck className="w-4 h-4 text-[#7D9075]" />
                    <span>256-Bit Encrypted Secure Checkout (Razorpay)</span>
                  </div>
                  <div className="flex items-center space-x-2">
                    <Sparkles className="w-4 h-4 text-[#C8A46A]" />
                    <span>Complimentary Deluxe Sample Included</span>
                  </div>
                  <div className="flex items-center space-x-2">
                    <span className="w-1.5 h-1.5 rounded-full bg-[#7D9075] animate-pulse" />
                    <span>Fulfilled via <strong>Shiprocket</strong> & <strong>BlueDart Express</strong></span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
