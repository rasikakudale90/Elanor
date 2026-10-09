'use client';

import React, { useState, useEffect, Suspense } from 'react';
import Link from 'next/link';
import Image from 'next/image';
import { useSearchParams } from 'next/navigation';
import { Truck, Search, CheckCircle2, Clock, MapPin, Package, ShieldCheck, Sparkles, ArrowLeft, ArrowRight, ExternalLink, Copy, Check } from 'lucide-react';

interface TrackingMilestone {
  stage: string;
  location: string;
  timestamp: string;
  completed: boolean;
  active: boolean;
  description: string;
}

interface OrderRecord {
  id: string;
  orderNumber: string;
  customerName: string;
  customerEmail: string;
  date: string;
  total: number;
  status: string;
  paymentMethod: string;
  transactionRef?: string;
  itemsCount: number;
  items: Array<{ name: string; quantity: number; price: number }>;
  address: string;
}

function TrackingContent() {
  const searchParams = useSearchParams();
  const initialQuery = searchParams.get('number') || searchParams.get('tracking') || searchParams.get('id') || '';

  const [query, setQuery] = useState(initialQuery);
  const [searchedNumber, setSearchedNumber] = useState(initialQuery || 'ELN-2026-7842');
  const [copied, setCopied] = useState(false);
  const [orderData, setOrderData] = useState<OrderRecord | null>(null);

  // Load from localStorage or mock demo
  useEffect(() => {
    if (typeof window !== 'undefined') {
      try {
        const raw = localStorage.getItem('elanor_orders');
        if (raw) {
          const orders: OrderRecord[] = JSON.parse(raw);
          if (orders && orders.length > 0) {
            const found = orders.find(
              (o) =>
                o.orderNumber.toLowerCase() === searchedNumber.toLowerCase() ||
                o.id.toLowerCase() === searchedNumber.toLowerCase() ||
                (o.transactionRef && o.transactionRef.toLowerCase() === searchedNumber.toLowerCase())
            );
            if (found) {
              setOrderData(found);
              return;
            } else if (!initialQuery && orders[0]) {
              setOrderData(orders[0]);
              setSearchedNumber(orders[0].orderNumber);
              return;
            }
          }
        }
      } catch (err) {
        console.warn('Could not read orders from storage', err);
      }
    }

    // Default Fallback Mock
    setOrderData({
      id: 'ord-demo-1',
      orderNumber: searchedNumber || 'ELN-2026-7842',
      customerName: 'Genevieve Moreau',
      customerEmail: 'genevieve.moreau@ateliers-paris.com',
      date: 'Today, 09:30 AM',
      total: 395.00,
      status: 'CONFIRMED',
      paymentMethod: 'RAZORPAY',
      transactionRef: 'pay_N942K8xQ1102',
      itemsCount: 2,
      items: [
        { name: 'Sérum Éclat Botanique', quantity: 1, price: 210 },
        { name: 'Crème Cellulaire Intense', quantity: 1, price: 185 },
      ],
      address: '24 Place Vendôme, 75001 Paris, France',
    });
  }, [searchedNumber, initialQuery]);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    if (query.trim()) {
      setSearchedNumber(query.trim());
    }
  };

  const copyTracking = (text: string) => {
    navigator.clipboard.writeText(text);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const awbNumber = `SR-BD-${searchedNumber.replace(/[^0-9]/g, '') || '948201'}`;

  const milestones: TrackingMilestone[] = [
    {
      stage: '1. Ritual Consecrated & Wax Sealed',
      location: 'Maison Élanor Atelier, Paris',
      timestamp: 'Today, 09:30 AM',
      completed: true,
      active: false,
      description: 'Formulations bottled in Miron violet glass and sealed with gold wax crest.'
    },
    {
      stage: '2. Dispatched via BlueDart Express (Shiprocket)',
      location: 'Paris International Cargo Hub (CDG)',
      timestamp: 'Today, 01:15 PM',
      completed: true,
      active: false,
      description: 'Handed over to carrier. Carbon-neutral express airfreight manifest assigned.'
    },
    {
      stage: '3. Regional Dermal Logistics Center',
      location: 'Regional Central Transit Terminal',
      timestamp: 'Expected Tomorrow, 08:00 AM',
      completed: false,
      active: true,
      description: 'Temperature-controlled parcel sorting underway.'
    },
    {
      stage: '4. Dedicated Courier Concierge',
      location: 'Local Delivery Hub',
      timestamp: 'Expected in 2 Days',
      completed: false,
      active: false,
      description: 'Dispatched for sanctuary delivery.'
    },
    {
      stage: '5. Signed & Delivered to Sanctuary',
      location: orderData?.address || 'Customer Sanctuary Destination',
      timestamp: 'Estimated Delivery: 2-3 Business Days',
      completed: false,
      active: false,
      description: 'Delivered in pristine condition.'
    }
  ];

  return (
    <div className="bg-[#F8F3EB] min-h-screen py-12 sm:py-16">
      <div className="max-w-[1280px] mx-auto px-6 sm:px-10 space-y-12">
        {/* Breadcrumb & Navigation */}
        <div className="flex items-center justify-between text-xs text-[#8E857A]">
          <Link href="/" className="hover:text-[#1B1A17] flex items-center space-x-1 transition-colors">
            <ArrowLeft className="w-3.5 h-3.5" />
            <span>Return to Maison Élanor</span>
          </Link>
          <span className="uppercase tracking-widest text-[10px] text-[#C8A46A] font-semibold">
            Carrier Logistics & Real-Time Telemetry
          </span>
        </div>

        {/* Hero Header & Search Bar */}
        <div className="bg-[#FFFDF9] rounded-3xl p-8 sm:p-12 border border-[#EFE3D3] shadow-card text-center space-y-6">
          <div className="w-16 h-16 bg-[#F8F3EB] border border-[#E4C894] rounded-2xl mx-auto flex items-center justify-center text-[#C8A46A] shadow-xs">
            <Truck className="w-8 h-8" />
          </div>

          <div className="space-y-2 max-w-xl mx-auto">
            <span className="text-[10px] uppercase tracking-[0.25em] text-[#C8A46A] font-semibold">
              Live Dispatch Status
            </span>
            <h1 className="font-serif-luxury text-3xl sm:text-4xl text-[#1B1A17]">
              Track Your Botanical Ritual
            </h1>
            <p className="text-xs text-[#5E584F] leading-relaxed">
              Enter your Élanor Order Number (e.g. <code>ELN-2026-XXXX</code>) or Shiprocket / BlueDart AWB tracking number.
            </p>
          </div>

          {/* Search Form */}
          <form onSubmit={handleSearch} className="max-w-md mx-auto flex bg-[#FDFBF8] border border-[#DCCDBA] rounded-full p-1.5 focus-within:border-[#C8A46A] shadow-xs transition-colors">
            <input
              type="text"
              placeholder="e.g. ELN-2026-7842 or SR-BD-948201"
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              className="flex-1 bg-transparent px-4 text-xs font-mono uppercase text-[#1B1A17] placeholder-[#8E857A] focus:outline-none"
            />
            <button
              type="submit"
              className="px-6 py-2.5 bg-[#1B1A17] text-[#FFFDF9] text-xs uppercase tracking-widest font-semibold rounded-full hover:bg-[#322F2A] transition-colors flex items-center space-x-1.5 cursor-pointer"
            >
              <Search className="w-3.5 h-3.5" />
              <span>Track</span>
            </button>
          </form>

          {/* Quick Demo Fill Pills */}
          <div className="flex flex-wrap items-center justify-center gap-2 pt-2 text-[10px] text-[#8E857A]">
            <span>Try Sample Identifiers:</span>
            <button
              type="button"
              onClick={() => { setQuery('ELN-2026-7842'); setSearchedNumber('ELN-2026-7842'); }}
              className="px-2.5 py-1 rounded-lg bg-[#F8F3EB] border border-[#EADFCF] hover:border-[#C8A46A] text-[#1B1A17] font-mono cursor-pointer"
            >
              ELN-2026-7842
            </button>
            <button
              type="button"
              onClick={() => { setQuery('SR-BD-551029'); setSearchedNumber('SR-BD-551029'); }}
              className="px-2.5 py-1 rounded-lg bg-[#F8F3EB] border border-[#EADFCF] hover:border-[#C8A46A] text-[#1B1A17] font-mono cursor-pointer"
            >
              SR-BD-551029 (BlueDart)
            </button>
          </div>
        </div>

        {/* Order & Tracking Status Container */}
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-10">
          {/* Left 8 Columns: Live 5-Stage Timeline */}
          <div className="lg:col-span-8 space-y-8">
            <div className="bg-[#FFFDF9] rounded-3xl p-8 border border-[#EFE3D3] shadow-card space-y-6">
              <div className="flex flex-col sm:flex-row sm:items-center justify-between pb-6 border-b border-[#EAE1D3] gap-4">
                <div>
                  <span className="text-[10px] uppercase tracking-wider text-[#8E857A] font-semibold block">
                    Dispatch Reference
                  </span>
                  <h2 className="font-serif-luxury text-2xl text-[#1B1A17]">
                    Order #{searchedNumber}
                  </h2>
                </div>

                <div className="flex items-center space-x-2">
                  <span className="px-3 py-1 bg-[#EEF2E8] border border-[#7D9075]/30 text-[#55624E] text-xs font-semibold rounded-full flex items-center space-x-1.5">
                    <span className="w-2 h-2 rounded-full bg-[#7D9075] animate-pulse" />
                    <span>In Transit (On Schedule)</span>
                  </span>
                </div>
              </div>

              {/* Carrier Logistics Badge Banner */}
              <div className="p-4 rounded-2xl bg-[#F8F3EB] border border-[#EADFCF] grid grid-cols-1 sm:grid-cols-3 gap-4 text-xs">
                <div>
                  <span className="text-[10px] uppercase tracking-wider text-[#8E857A] block">Logistics Partner</span>
                  <span className="font-semibold text-[#1B1A17]">BlueDart Express (Shiprocket)</span>
                </div>
                <div>
                  <span className="text-[10px] uppercase tracking-wider text-[#8E857A] block">AWB Tracking Code</span>
                  <div className="flex items-center space-x-1.5 font-mono text-[#1B1A17] font-medium">
                    <span>{awbNumber}</span>
                    <button
                      type="button"
                      onClick={() => copyTracking(awbNumber)}
                      title="Copy AWB Number"
                      className="text-[#8E857A] hover:text-[#1B1A17] p-0.5 cursor-pointer"
                    >
                      {copied ? <Check className="w-3.5 h-3.5 text-[#7D9075]" /> : <Copy className="w-3.5 h-3.5" />}
                    </button>
                  </div>
                </div>
                <div>
                  <span className="text-[10px] uppercase tracking-wider text-[#8E857A] block">Estimated Arrival</span>
                  <span className="font-semibold text-[#7D9075]">2-3 Business Days</span>
                </div>
              </div>

              {/* 5-Stage Milestone Progression */}
              <div className="space-y-6 pt-4">
                <h3 className="text-xs uppercase tracking-widest font-semibold text-[#8E857A]">
                  Milestone Verification History
                </h3>

                <div className="relative pl-6 space-y-8 before:absolute before:left-2.5 before:top-3 before:bottom-3 before:w-0.5 before:bg-[#EADFCF]">
                  {milestones.map((m, idx) => (
                    <div key={idx} className="relative group">
                      {/* Status Dot */}
                      <div
                        className={`absolute -left-6 top-0.5 w-5 h-5 rounded-full flex items-center justify-center text-[10px] ${
                          m.completed
                            ? 'bg-[#7D9075] text-[#FFFDF9]'
                            : m.active
                            ? 'bg-[#C8A46A] text-[#1B1A17] ring-4 ring-[#C8A46A]/20 animate-pulse'
                            : 'bg-[#EADFCF] text-[#8E857A]'
                        }`}
                      >
                        {m.completed ? <Check className="w-3 h-3" /> : idx + 1}
                      </div>

                      {/* Content */}
                      <div className="space-y-1">
                        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-1">
                          <h4 className={`text-sm font-serif-luxury font-semibold ${m.completed || m.active ? 'text-[#1B1A17]' : 'text-[#8E857A]'}`}>
                            {m.stage}
                          </h4>
                          <span className="text-[11px] text-[#8E857A] font-mono">{m.timestamp}</span>
                        </div>
                        <p className="text-xs text-[#5E584F] flex items-center space-x-1">
                          <MapPin className="w-3 h-3 text-[#C8A46A] shrink-0" />
                          <span>{m.location}</span>
                        </p>
                        <p className="text-[11px] text-[#8E857A] leading-relaxed pt-0.5">
                          {m.description}
                        </p>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </div>

          {/* Right 4 Columns: Order Summary & Atelier Guarantee */}
          <div className="lg:col-span-4 space-y-6">
            <div className="bg-[#FFFDF9] rounded-3xl p-6 border border-[#EFE3D3] shadow-card space-y-6">
              <h3 className="font-serif-luxury text-xl text-[#1B1A17] pb-3 border-b border-[#EADFCF]">
                Package Contents
              </h3>

              {orderData && (
                <div className="space-y-3 text-xs">
                  <div className="space-y-2 pb-3 border-b border-[#EAE1D3]">
                    {orderData.items.map((item, idx) => (
                      <div key={idx} className="flex justify-between items-center text-[#5E584F]">
                        <span>{item.name} (x{item.quantity})</span>
                        <span className="font-semibold text-[#1B1A17]">${(item.price * item.quantity).toFixed(2)}</span>
                      </div>
                    ))}
                  </div>

                  <div className="flex justify-between text-[#8E857A]">
                    <span>Recipient</span>
                    <span className="font-semibold text-[#1B1A17]">{orderData.customerName}</span>
                  </div>

                  <div className="flex justify-between text-[#8E857A]">
                    <span>Destination</span>
                    <span className="font-semibold text-[#1B1A17] text-right max-w-[180px] truncate">{orderData.address}</span>
                  </div>

                  <div className="flex justify-between text-[#8E857A]">
                    <span>Payment Gateway</span>
                    <span className="font-semibold text-[#7D9075]">Razorpay Sandbox Verified</span>
                  </div>

                  <div className="flex justify-between text-sm font-semibold text-[#1B1A17] pt-2 border-t border-[#EAE1D3]">
                    <span>Total Settled</span>
                    <span className="font-serif-luxury text-lg">${orderData.total.toFixed(2)}</span>
                  </div>
                </div>
              )}

              {/* Atelier Packaging Seal Notice */}
              <div className="p-4 rounded-2xl bg-[#F8F3EB] border border-[#EADFCF] space-y-2 text-xs text-[#5E584F]">
                <div className="flex items-center space-x-2 text-[#1B1A17] font-semibold">
                  <ShieldCheck className="w-4 h-4 text-[#C8A46A]" />
                  <span>Violet Glass Guarantee</span>
                </div>
                <p className="text-[11px] leading-relaxed">
                  Every formulation is encased in Swiss biophotonic Miron violet glass and packed in FSC-certified protective materials to prevent heat/photo-degradation during transit.
                </p>
              </div>

              <div className="pt-2">
                <Link
                  href="/shop"
                  className="w-full py-3.5 bg-[#1B1A17] text-[#FFFDF9] rounded-full text-xs uppercase tracking-widest font-semibold hover:bg-[#322F2A] transition-all flex items-center justify-center space-x-2 shadow-xs"
                >
                  <span>Explore Catalog</span>
                  <ArrowRight className="w-3.5 h-3.5" />
                </Link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default function TrackPage() {
  return (
    <Suspense fallback={<div className="p-20 text-center text-xs font-serif-luxury text-[#1B1A17]">Loading Sacred Dispatch Telemetry...</div>}>
      <TrackingContent />
    </Suspense>
  );
}
