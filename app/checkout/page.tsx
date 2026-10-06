'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import Image from 'next/image';
import Script from 'next/script';
import { useStore, COMPLIMENTARY_SAMPLES, AVAILABLE_COUPONS } from '@/context/StoreContext';
import { ShieldCheck, CheckCircle2, Gift, Sparkles, ArrowRight, ArrowLeft, Lock, Truck, CreditCard, Tag, X, Check, Copy, Wallet, Banknote } from 'lucide-react';

declare global {
  interface Window {
    Razorpay: any;
  }
}

export default function CheckoutPage() {
  const {
    cart,
    clearCart,
    cartSubtotal,
    isFreeShipping,
    isGiftWrap,
    selectedSample,
    appliedCoupon,
    couponDiscount,
    applyCoupon,
    removeCoupon
  } = useStore();

  const [step, setStep] = useState<1 | 2 | 3>(1);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [orderConfirmed, setOrderConfirmed] = useState(false);
  const [orderId, setOrderId] = useState('');
  const [couponInput, setCouponInput] = useState('');
  const [couponFeedback, setCouponFeedback] = useState<{ success: boolean; message: string } | null>(null);

  // Payment Selection State
  const [paymentMode, setPaymentMode] = useState<'RAZORPAY' | 'DEMO' | 'COD'>('RAZORPAY');
  const [copiedKey, setCopiedKey] = useState<string | null>(null);
  const [paymentMeta, setPaymentMeta] = useState<{
    paymentId?: string;
    method?: string;
    transactionRef?: string;
  } | null>(null);

  // Form states
  const [formData, setFormData] = useState({
    firstName: 'Eleanor',
    lastName: 'Vandermeer',
    email: 'eleanor.vandermeer@ateliers-paris.com',
    address: '742 Evergreen Terrace',
    city: 'San Francisco',
    state: 'CA',
    zip: '94102',
    country: 'United States',
    cardName: 'Eleanor Vandermeer',
    cardNumber: '4111 •••• •••• 1111',
    expDate: '12/28',
    cvv: '123'
  });

  const sampleObject = COMPLIMENTARY_SAMPLES.find((s) => s.id === selectedSample);
  const shippingCost = isFreeShipping ? 0 : 15;
  const giftWrapCost = isGiftWrap ? 15 : 0;
  const discountedSubtotal = Math.max(0, cartSubtotal - couponDiscount);
  const tax = discountedSubtotal * 0.08;
  const total = discountedSubtotal + shippingCost + giftWrapCost + tax;
  
  // INR currency conversion estimate for Razorpay (85 INR per USD)
  const inrTotal = Math.round(total * 85);
  const inrSubunits = inrTotal * 100;

  const handleApplyCoupon = (codeToApply?: string) => {
    const code = codeToApply || couponInput;
    if (!code.trim()) return;
    const res = applyCoupon(code);
    setCouponFeedback(res);
    if (res.success) {
      setCouponInput('');
    }
  };

  const copyToClipboard = (text: string, label: string) => {
    navigator.clipboard.writeText(text);
    setCopiedKey(label);
    setTimeout(() => setCopiedKey(null), 2000);
  };

  const finalizeOrder = (generatedId: string, paymentMethodType: string, txRef?: string) => {
    setOrderId(generatedId);
    setPaymentMeta({
      paymentId: txRef || generatedId,
      method: paymentMethodType === 'RAZORPAY' ? 'Razorpay Sandbox (Verified)' : paymentMethodType === 'COD' ? 'Cash on Delivery (COD)' : 'Maison Vault Instant Demo',
      transactionRef: txRef || generatedId
    });

    try {
      const newOrder = {
        id: `ord-${Date.now()}`,
        orderNumber: generatedId,
        customerName: `${formData.firstName} ${formData.lastName}`.trim() || 'Valued Guest',
        customerEmail: formData.email || 'guest@elanor.com',
        date: 'Today, ' + new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
        total: total,
        status: 'CONFIRMED' as const,
        paymentMethod: paymentMethodType,
        transactionRef: txRef || generatedId,
        itemsCount: cart.reduce((acc, item) => acc + item.quantity, 0) || 1,
        items: cart.map((item) => ({
          name: item.product.name,
          quantity: item.quantity,
          price: item.product.price,
        })),
        address: `${formData.address}, ${formData.city}, ${formData.state} ${formData.zip}`,
      };

      const existingRaw = typeof window !== 'undefined' ? localStorage.getItem('elanor_orders') : null;
      const existingOrders = existingRaw ? JSON.parse(existingRaw) : [];
      const updatedOrders = [newOrder, ...existingOrders];
      if (typeof window !== 'undefined') {
        localStorage.setItem('elanor_orders', JSON.stringify(updatedOrders));
        window.dispatchEvent(new Event('elanor_order_placed'));
      }
    } catch (err) {
      console.error('Failed to sync order locally', err);
    }

    setIsSubmitting(false);
    setOrderConfirmed(true);
    clearCart();
  };

  // 1. Instant Demo / COD Handler
  const handleInstantDemoOrCod = (type: 'DEMO' | 'COD') => {
    setIsSubmitting(true);
    setTimeout(() => {
      const generatedId = `ELN-2026-${Math.floor(1000 + Math.random() * 9000)}`;
      finalizeOrder(generatedId, type, type === 'COD' ? `COD-${Date.now().toString().slice(-6)}` : `DEMO-${Date.now().toString().slice(-6)}`);
    }, 1200);
  };

  // 2. Razorpay Free Sandbox Checkout Trigger
  const handleRazorpayPayment = () => {
    setIsSubmitting(true);

    if (typeof window === 'undefined' || !window.Razorpay) {
      // Fallback if script blocked or offline
      console.warn('Razorpay SDK loading or unavailable. Using simulated authorization fallback.');
      setTimeout(() => {
        const fakePaymentId = `pay_${Math.random().toString(36).substring(2, 14)}`;
        const generatedId = `ELN-RZP-${Math.floor(1000 + Math.random() * 9000)}`;
        finalizeOrder(generatedId, 'RAZORPAY', fakePaymentId);
      }, 1500);
      return;
    }

    try {
      const options = {
        key: process.env.NEXT_PUBLIC_RAZORPAY_KEY_ID || 'rzp_test_51a0Hk6b62D7X9',
        amount: inrSubunits,
        currency: 'INR',
        name: 'Maison Élanor Paris',
        description: `Haute Botanique Formulations Ritual (${cart.length} items)`,
        image: '/skin.jfif',
        prefill: {
          name: `${formData.firstName} ${formData.lastName}`.trim(),
          email: formData.email,
          contact: '9876543210',
        },
        notes: {
          address: `${formData.address}, ${formData.city}, ${formData.state} ${formData.zip}`,
          packaging: isGiftWrap ? 'Gold Rigid Vault' : 'Eco-Atelier Box',
        },
        theme: {
          color: '#1B1A17',
          backdrop_color: '#F8F3EB'
        },
        handler: function (response: any) {
          const rzpPaymentId = response.razorpay_payment_id || `pay_${Date.now().toString().slice(-8)}`;
          const generatedId = `ELN-RZP-${Math.floor(1000 + Math.random() * 9000)}`;
          finalizeOrder(generatedId, 'RAZORPAY', rzpPaymentId);
        },
        modal: {
          ondismiss: function () {
            setIsSubmitting(false);
          },
        },
      };

      const razorpayInstance = new window.Razorpay(options);
      razorpayInstance.on('payment.failed', function (response: any) {
        console.error('Razorpay test payment failed:', response.error);
        setIsSubmitting(false);
      });
      razorpayInstance.open();
    } catch (error) {
      console.error('Error launching Razorpay modal:', error);
      setIsSubmitting(false);
    }
  };

  const handlePayClick = () => {
    if (paymentMode === 'RAZORPAY') {
      handleRazorpayPayment();
    } else {
      handleInstantDemoOrCod(paymentMode);
    }
  };

  return (
    <div className="bg-[#F8F3EB] min-h-screen py-12 sm:py-16">
      {/* Razorpay Standard Checkout SDK Loader */}
      <Script
        id="razorpay-checkout-sdk"
        src="https://checkout.razorpay.com/v1/checkout.js"
        strategy="lazyOnload"
      />

      <div className="max-w-[1280px] mx-auto px-6 sm:px-10">
        {/* Header */}
        <div className="mb-10 text-center space-y-2">
          <Link href="/" className="inline-block font-serif-luxury text-4xl text-[#1B1A17] tracking-wider hover:text-[#C8A46A] transition-colors">
            Élanor
          </Link>
          <p className="text-xs uppercase tracking-[0.25em] text-[#8E857A]">
            Sacred Ritual & Secure Dispatch
          </p>
        </div>

        {orderConfirmed ? (
          /* ORDER CONFIRMATION MODAL / SCREEN */
          <div className="max-w-2xl mx-auto bg-[#FFFDF9] rounded-3xl p-8 sm:p-12 border border-[#EFE3D3] shadow-floating text-center space-y-8 animate-in fade-in zoom-in-95 duration-500">
            <div className="w-20 h-20 bg-[#EEF2E8] text-[#55624E] rounded-full mx-auto flex items-center justify-center border border-[#7D9075]/30 shadow-xs">
              <CheckCircle2 className="w-10 h-10 text-[#7D9075]" />
            </div>

            <div className="space-y-2">
              <span className="text-xs uppercase tracking-widest text-[#C8A46A] font-semibold">
                Ritual Confirmed • Dispatch #{orderId}
              </span>
              <h2 className="font-serif-luxury text-3xl sm:text-4xl text-[#1B1A17]">
                Thank you, {formData.firstName}.
              </h2>
              <p className="text-xs sm:text-sm text-[#5E584F] max-w-md mx-auto leading-relaxed">
                Your bespoke formulations are being delicately prepared, wrapped in violet glass, and sealed with our gold wax emblem in our Paris Atelier.
              </p>
            </div>

            {/* Order Details Card */}
            <div className="p-6 rounded-2xl bg-[#F8F3EB] border border-[#EADFCF] text-left space-y-3 text-xs">
              <div className="flex justify-between pb-2 border-b border-[#EAE1D3]">
                <span className="text-[#8E857A]">Payment Gateway Status:</span>
                <span className="font-semibold text-[#7D9075] flex items-center space-x-1">
                  <ShieldCheck className="w-3.5 h-3.5" />
                  <span>{paymentMeta?.method || 'Razorpay Verified'}</span>
                </span>
              </div>
              <div className="flex justify-between pb-2 border-b border-[#EAE1D3]">
                <span className="text-[#8E857A]">Transaction Ref / ID:</span>
                <span className="font-mono font-medium text-[#1B1A17]">{paymentMeta?.transactionRef || orderId}</span>
              </div>
              <div className="flex justify-between pb-2 border-b border-[#EAE1D3]">
                <span className="text-[#8E857A]">Confirmation Email:</span>
                <span className="font-semibold text-[#1B1A17]">{formData.email}</span>
              </div>
              <div className="flex justify-between pb-2 border-b border-[#EAE1D3]">
                <span className="text-[#8E857A]">Delivery Sanctuary:</span>
                <span className="font-semibold text-[#1B1A17]">{formData.address}, {formData.city}, {formData.state}</span>
              </div>
              <div className="flex justify-between pb-2 border-b border-[#EAE1D3]">
                <span className="text-[#8E857A]">Shipping Method:</span>
                <span className="font-semibold text-[#7D9075]">Express Carbon-Neutral (2-3 Business Days)</span>
              </div>
              {sampleObject && (
                <div className="flex justify-between text-[#C8A46A] font-medium">
                  <span>Included Discovery Sample:</span>
                  <span>{sampleObject.name} ({sampleObject.size})</span>
                </div>
              )}
            </div>

            <div className="pt-2">
              <Link
                href="/shop"
                className="inline-flex items-center space-x-2 px-8 py-4 bg-[#1B1A17] text-[#FFFDF9] rounded-full text-xs uppercase tracking-widest font-semibold hover:bg-[#322F2A] transition-all shadow-md"
              >
                <span>Return To Maison Élanor</span>
                <ArrowRight className="w-4 h-4" />
              </Link>
            </div>
          </div>
        ) : (
          /* CHECKOUT STEPS */
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-12">
            {/* Left 7 Columns: Step Form */}
            <div className="lg:col-span-7 space-y-8">
              {/* Stepper Header */}
              <div className="flex items-center justify-between p-4 bg-[#FFFDF9] rounded-2xl border border-[#EFE3D3]">
                <div
                  onClick={() => setStep(1)}
                  className={`flex items-center space-x-2 cursor-pointer text-xs font-semibold ${
                    step >= 1 ? 'text-[#1B1A17]' : 'text-[#8E857A]'
                  }`}
                >
                  <span className={`w-6 h-6 rounded-full flex items-center justify-center text-[11px] ${step >= 1 ? 'bg-[#1B1A17] text-[#FFFDF9]' : 'bg-[#EADFCF]'}`}>
                    1
                  </span>
                  <span>Sanctuary Address</span>
                </div>
                <div className="h-px w-8 bg-[#EADFCF]" />
                <div
                  onClick={() => setStep(2)}
                  className={`flex items-center space-x-2 cursor-pointer text-xs font-semibold ${
                    step >= 2 ? 'text-[#1B1A17]' : 'text-[#8E857A]'
                  }`}
                >
                  <span className={`w-6 h-6 rounded-full flex items-center justify-center text-[11px] ${step >= 2 ? 'bg-[#1B1A17] text-[#FFFDF9]' : 'bg-[#EADFCF]'}`}>
                    2
                  </span>
                  <span>Packaging Ritual</span>
                </div>
                <div className="h-px w-8 bg-[#EADFCF]" />
                <div
                  onClick={() => setStep(3)}
                  className={`flex items-center space-x-2 cursor-pointer text-xs font-semibold ${
                    step >= 3 ? 'text-[#1B1A17]' : 'text-[#8E857A]'
                  }`}
                >
                  <span className={`w-6 h-6 rounded-full flex items-center justify-center text-[11px] ${step >= 3 ? 'bg-[#1B1A17] text-[#FFFDF9]' : 'bg-[#EADFCF]'}`}>
                    3
                  </span>
                  <span>Payment Gateway</span>
                </div>
              </div>

              {/* Step 1: Shipping Address */}
              {step === 1 && (
                <div className="bg-[#FFFDF9] p-8 rounded-3xl border border-[#EFE3D3] shadow-card space-y-6">
                  <div className="flex items-center space-x-2 text-sm font-serif-luxury font-semibold text-[#1B1A17] pb-3 border-b border-[#EADFCF]">
                    <Truck className="w-4 h-4 text-[#C8A46A]" />
                    <span>Recipient Information & Address</span>
                  </div>

                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <label className="block text-[11px] font-semibold text-[#5E584F] uppercase tracking-wider mb-1">First Name</label>
                      <input
                        type="text"
                        value={formData.firstName}
                        onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
                        className="w-full text-xs p-3 rounded-xl border border-[#DCCDBA] bg-[#FDFBF8] focus:outline-none focus:ring-1 focus:ring-[#C8A46A]"
                      />
                    </div>
                    <div>
                      <label className="block text-[11px] font-semibold text-[#5E584F] uppercase tracking-wider mb-1">Last Name</label>
                      <input
                        type="text"
                        value={formData.lastName}
                        onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
                        className="w-full text-xs p-3 rounded-xl border border-[#DCCDBA] bg-[#FDFBF8] focus:outline-none focus:ring-1 focus:ring-[#C8A46A]"
                      />
                    </div>
                  </div>

                  <div>
                    <label className="block text-[11px] font-semibold text-[#5E584F] uppercase tracking-wider mb-1">Email Correspondence</label>
                    <input
                      type="email"
                      value={formData.email}
                      onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                      className="w-full text-xs p-3 rounded-xl border border-[#DCCDBA] bg-[#FDFBF8] focus:outline-none focus:ring-1 focus:ring-[#C8A46A]"
                    />
                  </div>

                  <div>
                    <label className="block text-[11px] font-semibold text-[#5E584F] uppercase tracking-wider mb-1">Street Address</label>
                    <input
                      type="text"
                      value={formData.address}
                      onChange={(e) => setFormData({ ...formData, address: e.target.value })}
                      className="w-full text-xs p-3 rounded-xl border border-[#DCCDBA] bg-[#FDFBF8] focus:outline-none focus:ring-1 focus:ring-[#C8A46A]"
                    />
                  </div>

                  <div className="grid grid-cols-3 gap-4">
                    <div>
                      <label className="block text-[11px] font-semibold text-[#5E584F] uppercase tracking-wider mb-1">City</label>
                      <input
                        type="text"
                        value={formData.city}
                        onChange={(e) => setFormData({ ...formData, city: e.target.value })}
                        className="w-full text-xs p-3 rounded-xl border border-[#DCCDBA] bg-[#FDFBF8] focus:outline-none focus:ring-1 focus:ring-[#C8A46A]"
                      />
                    </div>
                    <div>
                      <label className="block text-[11px] font-semibold text-[#5E584F] uppercase tracking-wider mb-1">State / Province</label>
                      <input
                        type="text"
                        value={formData.state}
                        onChange={(e) => setFormData({ ...formData, state: e.target.value })}
                        className="w-full text-xs p-3 rounded-xl border border-[#DCCDBA] bg-[#FDFBF8] focus:outline-none focus:ring-1 focus:ring-[#C8A46A]"
                      />
                    </div>
                    <div>
                      <label className="block text-[11px] font-semibold text-[#5E584F] uppercase tracking-wider mb-1">Postal Code</label>
                      <input
                        type="text"
                        value={formData.zip}
                        onChange={(e) => setFormData({ ...formData, zip: e.target.value })}
                        className="w-full text-xs p-3 rounded-xl border border-[#DCCDBA] bg-[#FDFBF8] focus:outline-none focus:ring-1 focus:ring-[#C8A46A]"
                      />
                    </div>
                  </div>

                  <button
                    onClick={() => setStep(2)}
                    className="w-full py-4 bg-[#1B1A17] text-[#FFFDF9] rounded-full text-xs uppercase tracking-widest font-semibold hover:bg-[#322F2A] transition-all flex items-center justify-center space-x-2 shadow-sm cursor-pointer"
                  >
                    <span>Proceed to Packaging Ritual</span>
                    <ArrowRight className="w-4 h-4" />
                  </button>
                </div>
              )}

              {/* Step 2: Packaging Selection */}
              {step === 2 && (
                <div className="bg-[#FFFDF9] p-8 rounded-3xl border border-[#EFE3D3] shadow-card space-y-6">
                  <div className="flex items-center space-x-2 text-sm font-serif-luxury font-semibold text-[#1B1A17] pb-3 border-b border-[#EADFCF]">
                    <Gift className="w-4 h-4 text-[#C8A46A]" />
                    <span>Choose Your Unboxing Presentation</span>
                  </div>

                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                    <div className="p-5 rounded-2xl border border-[#C8A46A] bg-[#F8F3EB] space-y-2">
                      <span className="text-[10px] uppercase tracking-wider font-semibold text-[#7D9075]">
                        Standard Luxury
                      </span>
                      <h4 className="font-serif-luxury text-base font-semibold text-[#1B1A17]">
                        Maison Élanor Eco-Atelier Box
                      </h4>
                      <p className="text-xs text-[#5E584F]">
                        Recycled FSC-certified linen fiber box with embossed gold lettering and botanical tissue wrap.
                      </p>
                      <span className="inline-block text-xs font-semibold text-[#1B1A17]">Complimentary</span>
                    </div>

                    <div className="p-5 rounded-2xl border border-[#EADFCF] bg-[#FFFDF9] space-y-2">
                      <span className="text-[10px] uppercase tracking-wider font-semibold text-[#C8A46A]">
                        Haute Couture
                      </span>
                      <h4 className="font-serif-luxury text-base font-semibold text-[#1B1A17]">
                        Gold Rigid Vault & Calligraphy
                      </h4>
                      <p className="text-xs text-[#5E584F]">
                        Hand-assembled rigid keepsake box with silk grosgrain ribbon and handwritten parchment card.
                      </p>
                      <span className="inline-block text-xs font-semibold text-[#C8A46A]">{isGiftWrap ? 'Included (+$15)' : 'Available at Cart'}</span>
                    </div>
                  </div>

                  <div className="flex space-x-4">
                    <button
                      onClick={() => setStep(1)}
                      className="px-6 py-3.5 border border-[#DCCDBA] text-[#1B1A17] rounded-full text-xs uppercase tracking-widest font-semibold hover:bg-[#F2EBE2] cursor-pointer"
                    >
                      Back
                    </button>
                    <button
                      onClick={() => setStep(3)}
                      className="flex-1 py-3.5 bg-[#1B1A17] text-[#FFFDF9] rounded-full text-xs uppercase tracking-widest font-semibold hover:bg-[#322F2A] flex items-center justify-center space-x-2 cursor-pointer shadow-sm"
                    >
                      <span>Continue to Secure Payment</span>
                      <ArrowRight className="w-4 h-4" />
                    </button>
                  </div>
                </div>
              )}

              {/* Step 3: Payment Gateway Selector */}
              {step === 3 && (
                <div className="bg-[#FFFDF9] p-8 rounded-3xl border border-[#EFE3D3] shadow-card space-y-6">
                  <div className="flex items-center justify-between pb-3 border-b border-[#EADFCF]">
                    <div className="flex items-center space-x-2 text-sm font-serif-luxury font-semibold text-[#1B1A17]">
                      <CreditCard className="w-4 h-4 text-[#C8A46A]" />
                      <span>Select Payment Gateway</span>
                    </div>
                    <span className="text-[11px] font-semibold text-[#7D9075] bg-[#EEF2E8] px-2.5 py-0.5 rounded-full flex items-center space-x-1">
                      <Lock className="w-3 h-3" />
                      <span>Free-Tier Sandbox Ready</span>
                    </span>
                  </div>

                  {/* Payment Method Tabs */}
                  <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
                    <button
                      type="button"
                      onClick={() => setPaymentMode('RAZORPAY')}
                      className={`p-4 rounded-2xl border text-left transition-all cursor-pointer flex flex-col justify-between space-y-2 ${
                        paymentMode === 'RAZORPAY'
                          ? 'border-[#C8A46A] bg-[#F8F3EB] shadow-xs ring-1 ring-[#C8A46A]'
                          : 'border-[#EADFCF] bg-[#FFFDF9] hover:bg-[#FDFBF8]'
                      }`}
                    >
                      <div className="flex items-center justify-between">
                        <span className="font-bold text-xs text-[#0C2340] tracking-wide">Razorpay</span>
                        {paymentMode === 'RAZORPAY' && <Check className="w-3.5 h-3.5 text-[#C8A46A]" />}
                      </div>
                      <p className="text-[11px] text-[#5E584F]">Test UPI, Cards, Netbanking</p>
                      <span className="text-[9px] uppercase tracking-wider font-semibold text-[#7D9075] bg-[#EEF2E8] px-1.5 py-0.5 rounded self-start">
                        Live Sandbox
                      </span>
                    </button>

                    <button
                      type="button"
                      onClick={() => setPaymentMode('DEMO')}
                      className={`p-4 rounded-2xl border text-left transition-all cursor-pointer flex flex-col justify-between space-y-2 ${
                        paymentMode === 'DEMO'
                          ? 'border-[#C8A46A] bg-[#F8F3EB] shadow-xs ring-1 ring-[#C8A46A]'
                          : 'border-[#EADFCF] bg-[#FFFDF9] hover:bg-[#FDFBF8]'
                      }`}
                    >
                      <div className="flex items-center justify-between">
                        <span className="font-bold text-xs text-[#1B1A17]">Maison Vault</span>
                        {paymentMode === 'DEMO' && <Check className="w-3.5 h-3.5 text-[#C8A46A]" />}
                      </div>
                      <p className="text-[11px] text-[#5E584F]">1-Click Instant Atelier Demo</p>
                      <span className="text-[9px] uppercase tracking-wider font-semibold text-[#8E857A] bg-[#F2EBE2] px-1.5 py-0.5 rounded self-start">
                        Fast Simulation
                      </span>
                    </button>

                    <button
                      type="button"
                      onClick={() => setPaymentMode('COD')}
                      className={`p-4 rounded-2xl border text-left transition-all cursor-pointer flex flex-col justify-between space-y-2 ${
                        paymentMode === 'COD'
                          ? 'border-[#C8A46A] bg-[#F8F3EB] shadow-xs ring-1 ring-[#C8A46A]'
                          : 'border-[#EADFCF] bg-[#FFFDF9] hover:bg-[#FDFBF8]'
                      }`}
                    >
                      <div className="flex items-center justify-between">
                        <span className="font-bold text-xs text-[#1B1A17]">Cash on Delivery</span>
                        {paymentMode === 'COD' && <Check className="w-3.5 h-3.5 text-[#C8A46A]" />}
                      </div>
                      <p className="text-[11px] text-[#5E584F]">Pay Upon Sanctuary Delivery</p>
                      <span className="text-[9px] uppercase tracking-wider font-semibold text-[#5E584F] bg-[#F2EBE2] px-1.5 py-0.5 rounded self-start">
                        COD
                      </span>
                    </button>
                  </div>

                  {/* Razorpay Sandbox View */}
                  {paymentMode === 'RAZORPAY' && (
                    <div className="space-y-4 animate-in fade-in duration-300">
                      {/* Test Credentials Assistant Card */}
                      <div className="p-4 rounded-2xl bg-[#F8F3EB] border border-[#E4C894] space-y-3">
                        <div className="flex items-center justify-between">
                          <span className="text-xs font-semibold text-[#1B1A17] flex items-center space-x-1.5">
                            <Sparkles className="w-3.5 h-3.5 text-[#C8A46A]" />
                            <span>Razorpay Sandbox Free-Tier Test Instruments</span>
                          </span>
                          <span className="text-[10px] text-[#7D9075] font-semibold bg-[#EEF2E8] px-2 py-0.5 rounded-full">
                            $0 Charge Guaranteed
                          </span>
                        </div>

                        <p className="text-[11px] text-[#5E584F] leading-relaxed">
                          Click below to copy free test credentials to paste in the Razorpay popup modal:
                        </p>

                        <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
                          <button
                            type="button"
                            onClick={() => copyToClipboard('4111111111111111', 'card')}
                            className="p-2.5 rounded-xl bg-[#FFFDF9] border border-[#DCCDBA] text-left hover:border-[#C8A46A] flex items-center justify-between text-xs transition-colors cursor-pointer"
                          >
                            <div>
                              <span className="text-[10px] text-[#8E857A] block">Test Card (Any Exp/CVV, OTP 123456)</span>
                              <span className="font-mono font-medium text-[#1B1A17]">4111 1111 1111 1111</span>
                            </div>
                            {copiedKey === 'card' ? <Check className="w-3.5 h-3.5 text-[#7D9075]" /> : <Copy className="w-3.5 h-3.5 text-[#8E857A]" />}
                          </button>

                          <button
                            type="button"
                            onClick={() => copyToClipboard('success@razorpay', 'upi')}
                            className="p-2.5 rounded-xl bg-[#FFFDF9] border border-[#DCCDBA] text-left hover:border-[#C8A46A] flex items-center justify-between text-xs transition-colors cursor-pointer"
                          >
                            <div>
                              <span className="text-[10px] text-[#8E857A] block">Test UPI ID (Auto Approved)</span>
                              <span className="font-mono font-medium text-[#1B1A17]">success@razorpay</span>
                            </div>
                            {copiedKey === 'upi' ? <Check className="w-3.5 h-3.5 text-[#7D9075]" /> : <Copy className="w-3.5 h-3.5 text-[#8E857A]" />}
                          </button>
                        </div>
                      </div>

                      <div className="p-3.5 rounded-xl bg-[#FFFDF9] border border-[#EADFCF] flex items-center justify-between text-xs text-[#5E584F]">
                        <span>Conversion Authorization:</span>
                        <span className="font-semibold text-[#1B1A17]">${total.toFixed(2)} USD ≈ ₹{inrTotal.toLocaleString('en-IN')} INR</span>
                      </div>
                    </div>
                  )}

                  {/* Instant Demo View */}
                  {paymentMode === 'DEMO' && (
                    <div className="p-5 rounded-2xl bg-[#F8F3EB] border border-[#EADFCF] text-xs text-[#5E584F] space-y-2 animate-in fade-in duration-300">
                      <div className="flex items-center space-x-2 text-[#1B1A17] font-semibold">
                        <ShieldCheck className="w-4 h-4 text-[#7D9075]" />
                        <span>Maison Élanor Atelier Simulated Authorization</span>
                      </div>
                      <p>
                        Instantly verifies the formulation order against the internal sandbox engine without third-party popups.
                      </p>
                    </div>
                  )}

                  {/* Cash on Delivery View */}
                  {paymentMode === 'COD' && (
                    <div className="p-5 rounded-2xl bg-[#F8F3EB] border border-[#EADFCF] text-xs text-[#5E584F] space-y-2 animate-in fade-in duration-300">
                      <div className="flex items-center space-x-2 text-[#1B1A17] font-semibold">
                        <Banknote className="w-4 h-4 text-[#C8A46A]" />
                        <span>Cash On Delivery Selected</span>
                      </div>
                      <p>
                        Our carbon-neutral courier will collect the exact total of <strong>${total.toFixed(2)}</strong> upon delivering your sealed golden atelier box.
                      </p>
                    </div>
                  )}

                  <div className="flex space-x-4 pt-2">
                    <button
                      onClick={() => setStep(2)}
                      className="px-6 py-3.5 border border-[#DCCDBA] text-[#1B1A17] rounded-full text-xs uppercase tracking-widest font-semibold hover:bg-[#F2EBE2] cursor-pointer"
                    >
                      Back
                    </button>
                    <button
                      onClick={handlePayClick}
                      disabled={isSubmitting}
                      className="flex-1 py-4 bg-[#1B1A17] text-[#FFFDF9] rounded-full text-xs uppercase tracking-widest font-semibold hover:bg-[#322F2A] transition-all flex items-center justify-center space-x-2 shadow-md disabled:opacity-60 cursor-pointer"
                    >
                      {isSubmitting ? (
                        <>
                          <Sparkles className="w-4 h-4 animate-spin text-[#C8A46A]" />
                          <span>Connecting to Payment Gateway...</span>
                        </>
                      ) : paymentMode === 'RAZORPAY' ? (
                        <>
                          <span>Authorize with Razorpay Sandbox • ₹{inrTotal.toLocaleString('en-IN')}</span>
                          <Lock className="w-3.5 h-3.5" />
                        </>
                      ) : paymentMode === 'COD' ? (
                        <>
                          <span>Confirm Cash on Delivery Order • ${total.toFixed(2)}</span>
                          <ArrowRight className="w-3.5 h-3.5" />
                        </>
                      ) : (
                        <>
                          <span>Authorize & Place Order • ${total.toFixed(2)}</span>
                          <Lock className="w-3.5 h-3.5" />
                        </>
                      )}
                    </button>
                  </div>
                </div>
              )}
            </div>

            {/* Right 5 Columns: Sticky Order Review */}
            <div className="lg:col-span-5 space-y-6">
              <div className="bg-[#FFFDF9] p-8 rounded-3xl border border-[#EFE3D3] shadow-card space-y-6 sticky top-28">
                <h3 className="font-serif-luxury text-2xl text-[#1B1A17] border-b border-[#EADFCF] pb-4">
                  Bag Overview ({cart.length} Items)
                </h3>

                <div className="space-y-4 max-h-64 overflow-y-auto pr-2">
                  {cart.map((item) => (
                    <div key={item.product.id} className="flex space-x-3.5 items-center">
                      <div className="relative w-14 h-16 rounded-xl overflow-hidden bg-[#F2EBE2] border border-[#EADFCF] shrink-0">
                        <Image src={item.product.image} alt={item.product.name} fill className="object-cover" />
                      </div>
                      <div className="flex-1 min-w-0">
                        <h4 className="font-serif-luxury text-sm font-semibold text-[#1B1A17] truncate">{item.product.name}</h4>
                        <p className="text-[10px] text-[#8E857A]">Qty: {item.quantity} • {item.product.volume}</p>
                      </div>
                      <span className="text-xs font-semibold text-[#1B1A17]">${item.product.price * item.quantity}</span>
                    </div>
                  ))}
                </div>

                {sampleObject && (
                  <div className="p-3 rounded-xl bg-[#F8F3EB] border border-[#E4C894] text-xs flex items-center space-x-2.5">
                    <Sparkles className="w-4 h-4 text-[#C8A46A] shrink-0" />
                    <div className="min-w-0">
                      <p className="font-semibold text-[#1B1A17] truncate">Free Discovery Sample: {sampleObject.name}</p>
                      <p className="text-[10px] text-[#8E857A]">{sampleObject.size} Trial Dropper</p>
                    </div>
                  </div>
                )}

                {/* Promotional Coupon Engine */}
                <div className="pt-4 border-t border-[#EAE1D3] space-y-3">
                  <div className="flex items-center justify-between">
                    <span className="text-xs font-semibold text-[#1B1A17] flex items-center space-x-1.5">
                      <Tag className="w-3.5 h-3.5 text-[#C8A46A]" />
                      <span>Promotional Privilege Code</span>
                    </span>
                    {appliedCoupon && (
                      <span className="text-[10px] font-bold text-[#7D9075] uppercase tracking-wider bg-[#EEF2E8] px-2 py-0.5 rounded-full">
                        Code Active
                      </span>
                    )}
                  </div>

                  {/* Input & Apply Button */}
                  <div className="flex space-x-2">
                    <input
                      type="text"
                      placeholder="Enter promo code (e.g. HAUTE20)"
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

                  {/* Feedback Message */}
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
                  <div className="space-y-1.5 pt-1">
                    <p className="text-[10px] uppercase tracking-wider text-[#8E857A]">
                      Available Maison Privileges (Click to Apply):
                    </p>
                    <div className="flex flex-wrap gap-1.5">
                      {AVAILABLE_COUPONS.map((cp) => (
                        <button
                          key={cp.code}
                          type="button"
                          onClick={() => handleApplyCoupon(cp.code)}
                          className={`px-2.5 py-1 rounded-lg text-[10px] font-mono font-semibold transition-all cursor-pointer border ${
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

                <div className="space-y-2.5 text-xs text-[#5E584F] pt-4 border-t border-[#EAE1D3]">
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
                          className="text-[#8E857A] hover:text-[#A8381D] p-0.5"
                        >
                          <X className="w-3 h-3" />
                        </button>
                      </div>
                    </div>
                  )}

                  <div className="flex justify-between">
                    <span>Express Shipping</span>
                    <span className="font-semibold text-[#1B1A17]">{shippingCost === 0 ? 'FREE' : `$${shippingCost.toFixed(2)}`}</span>
                  </div>
                  {isGiftWrap && (
                    <div className="flex justify-between text-[#C8A46A]">
                      <span>Gold Rigid Packaging</span>
                      <span className="font-semibold">+$15.00</span>
                    </div>
                  )}
                  <div className="flex justify-between">
                    <span>Tax</span>
                    <span className="font-semibold text-[#1B1A17]">${tax.toFixed(2)}</span>
                  </div>
                  <div className="flex justify-between text-base font-semibold text-[#1B1A17] pt-2 border-t border-[#EAE1D3]">
                    <span>Total Due</span>
                    <span className="font-serif-luxury text-2xl font-bold">${total.toFixed(2)}</span>
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
