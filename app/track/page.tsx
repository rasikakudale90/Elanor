'use client';

import React, { useState, useEffect, Suspense } from 'react';
import Link from 'next/link';
import Image from 'next/image';
import { useSearchParams } from 'next/navigation';
import { Truck, Search, CheckCircle2, Clock, MapPin, Package, ShieldCheck, Sparkles, ArrowLeft, ArrowRight, ExternalLink, Copy, Check, RefreshCw, X } from 'lucide-react';

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
  const urlParam = searchParams.get('number') || searchParams.get('tracking') || searchParams.get('id') || '';

  const [query, setQuery] = useState(urlParam || 'ELN-2026-7842');
  const [currentTrackingId, setCurrentTrackingId] = useState(urlParam || 'ELN-2026-7842');
  const [isScanning, setIsScanning] = useState(false);
  const [copied, setCopied] = useState(false);
  const [orderData, setOrderData] = useState<OrderRecord | null>(null);
  const [feedbackMsg, setFeedbackMsg] = useState<string | null>(null);

  // Core lookup logic
  const performLookup = (idToSearch: string) => {
    const cleanId = idToSearch.trim();
    if (!cleanId) return;

    setIsScanning(true);
    setCurrentTrackingId(cleanId);

    setTimeout(() => {
      let matchedOrder: OrderRecord | null = null;

      if (typeof window !== 'undefined') {
        try {
          const raw = localStorage.getItem('elanor_orders');
          if (raw) {
            const orders: OrderRecord[] = JSON.parse(raw);
            if (Array.isArray(orders)) {
              matchedOrder = orders.find(
                (o) =>
                  o.orderNumber?.toLowerCase() === cleanId.toLowerCase() ||
                  o.id?.toLowerCase() === cleanId.toLowerCase() ||
                  (o.transactionRef && o.transactionRef.toLowerCase() === cleanId.toLowerCase())
              ) || null;
            }
          }
        } catch (err) {
          console.warn('Could not parse orders storage', err);
        }
      }

      if (matchedOrder) {
        setOrderData(matchedOrder);
        setFeedbackMsg(`✓ Verified order #${matchedOrder.orderNumber} in Atelier records`);
      } else {
        // Dynamic simulated manifest for searched identifier
        const isBlueDart = cleanId.toUpperCase().includes('BD') || cleanId.toUpperCase().includes('SR');
        setOrderData({
          id: `ord-${cleanId.toLowerCase()}`,
          orderNumber: cleanId.toUpperCase(),
          customerName: 'Valued VIP Patron',
          customerEmail: 'patron.sanctuary@elanor.com',
          date: 'Today, 09:30 AM',
          total: 395.00,
          status: 'IN_TRANSIT',
          paymentMethod: 'RAZORPAY',
          transactionRef: `pay_${cleanId.replace(/[^a-zA-Z0-9]/g, '').slice(0, 10) || 'live98421'}`,
          itemsCount: 2,
          items: [
            { name: 'Sérum Éclat Botanique aux Fleurs Rares', quantity: 1, price: 210 },
            { name: 'Crème Cellulaire Intense aux Plantes Alpines', quantity: 1, price: 185 },
          ],
          address: '24 Place Vendôme, 75001 Paris, France',
        });
        setFeedbackMsg(`✓ Active BlueDart/Shiprocket airfreight manifest connected`);
      }

      setIsScanning(false);
      setTimeout(() => setFeedbackMsg(null), 4000);
    }, 450);
  };

  // Run on mount or URL change
  useEffect(() => {
    if (urlParam) {
      setQuery(urlParam);
      performLookup(urlParam);
    } else {
      performLookup('ELN-2026-7842');
    }
  }, [urlParam]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (query.trim()) {
      performLookup(query.trim());
    }
  };

  const handleQuickSelect = (sampleCode: string) => {
    setQuery(sampleCode);
    performLookup(sampleCode);
  };

  const copyTracking = (text: string) => {
    navigator.clipboard.writeText(text);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const awbNumber = currentTrackingId.toUpperCase().startsWith('SR-') || currentTrackingId.toUpperCase().startsWith('BD-')
    ? currentTrackingId.toUpperCase()
    : `SR-BD-${currentTrackingId.replace(/[^0-9]/g, '') || '948201'}`;

  const milestones: TrackingMilestone[] = [
    {
      stage: '1. Ritual Consecrated & Wax Sealed',
      location: 'Maison Élanor Paris Atelier (24 Place Vendôme)',
      timestamp: 'Today, 09:30 AM',
      completed: true,
      active: false,
      description: 'Formulations bottled in Miron violet glass, sealed with signature gold wax crest.'
    },
    {
      stage: '2. Dispatched via BlueDart Express (Shiprocket Partner)',
      location: 'Paris International Cargo Hub (CDG)',
      timestamp: 'Today, 01:15 PM',
      completed: true,
      active: false,
      description: 'Handed over to carrier. Carbon-neutral express airfreight manifest assigned.'
    },
    {
      stage: '3. Regional Dermal Logistics Terminal',
      location: 'Regional Distribution Center',
      timestamp: 'In Transit • On Schedule',
      completed: false,
      active: true,
      description: 'Temperature-monitored luxury parcel routing underway.'
    },
    {
      stage: '4. Dedicated Courier Concierge',
      location: 'Local Delivery Hub',
      timestamp: 'Expected in 1-2 Days',
      completed: false,
      active: false,
      description: 'Out for morning delivery with white-glove courier.'
    },
    {
      stage: '5. Signed & Delivered to Sanctuary',
      location: orderData?.address || 'Customer Sanctuary Destination',
      timestamp: 'Estimated Delivery: 2-3 Business Days',
      completed: false,
      active: false,
      description: 'Delivered in pristine biophotonic presentation box.'
    }
  ];

  return (
    <div className="bg-[#F8F3EB] min-h-screen py-12 sm:py-16">
      <div className="max-w-[1280px] mx-auto px-6 sm:px-10 space-y-12">
        {/* Breadcrumb & Navigation */}
        <div className="flex items-center justify-between text-xs text-[#8E857A]">
          <Link href="/" className="hover:text-[#1B1A17] flex items-center space-x-1.5 transition-colors">
            <ArrowLeft className="w-3.5 h-3.5" />
            <span>Return to Maison Élanor</span>
          </Link>
          <span className="uppercase tracking-widest text-[10px] text-[#C8A46A] font-semibold flex items-center space-x-1">
            <Sparkles className="w-3 h-3" />
            <span>Carrier Logistics & Real-Time Telemetry</span>
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
          <form
            onSubmit={handleSubmit}
            className="max-w-lg mx-auto flex bg-[#FDFBF8] border border-[#DCCDBA] rounded-full p-1.5 focus-within:border-[#C8A46A] shadow-xs transition-all"
          >
            <div className="flex-1 flex items-center pl-4">
              <input
                type="text"
                placeholder="Enter Order # (e.g. ELN-2026-7842) or AWB..."
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                className="w-full bg-transparent text-xs font-mono uppercase text-[#1B1A17] placeholder-[#8E857A] focus:outline-none"
              />
              {query && (
                <button
                  type="button"
                  onClick={() => setQuery('')}
                  className="p-1 text-[#8E857A] hover:text-[#1B1A17] mr-1"
                >
                  <X className="w-3.5 h-3.5" />
                </button>
              )}
            </div>
            <button
              type="submit"
              disabled={isScanning}
              className="px-6 py-3 bg-[#1B1A17] text-[#FFFDF9] text-xs uppercase tracking-widest font-semibold rounded-full hover:bg-[#322F2A] transition-all flex items-center space-x-2 cursor-pointer disabled:opacity-60 shrink-0"
            >
              {isScanning ? (
                <>
                  <RefreshCw className="w-3.5 h-3.5 animate-spin text-[#C8A46A]" />
                  <span>Scanning...</span>
                </>
              ) : (
                <>
                  <Search className="w-3.5 h-3.5" />
                  <span>Track</span>
                </>
              )}
            </button>
          </form>

          {/* Feedback Toast */}
          {feedbackMsg && (
            <p className="text-[11px] font-semibold text-[#7D9075] animate-in fade-in duration-300">
              {feedbackMsg}
            </p>
          )}

          {/* Quick Demo Fill Pills */}
          <div className="flex flex-wrap items-center justify-center gap-2 pt-1 text-[10px] text-[#8E857A]">
            <span>Click Quick Track Demo:</span>
            <button
              type="button"
              onClick={() => handleQuickSelect('ELN-2026-7842')}
              className={`px-3 py-1.5 rounded-lg border transition-all cursor-pointer font-mono ${
                currentTrackingId === 'ELN-2026-7842'
                  ? 'bg-[#C8A46A] text-[#1B1A17] border-[#C8A46A] font-semibold shadow-xs'
                  : 'bg-[#F8F3EB] border-[#EADFCF] hover:border-[#C8A46A] text-[#1B1A17]'
              }`}
            >
              ELN-2026-7842 (Atelier Order)
            </button>
            <button
              type="button"
              onClick={() => handleQuickSelect('SR-BD-551029')}
              className={`px-3 py-1.5 rounded-lg border transition-all cursor-pointer font-mono ${
                currentTrackingId === 'SR-BD-551029'
                  ? 'bg-[#C8A46A] text-[#1B1A17] border-[#C8A46A] font-semibold shadow-xs'
                  : 'bg-[#F8F3EB] border-[#EADFCF] hover:border-[#C8A46A] text-[#1B1A17]'
              }`}
            >
              SR-BD-551029 (BlueDart Express)
            </button>
            <button
              type="button"
              onClick={() => handleQuickSelect('ELN-RZP-9042')}
              className={`px-3 py-1.5 rounded-lg border transition-all cursor-pointer font-mono ${
                currentTrackingId === 'ELN-RZP-9042'
                  ? 'bg-[#C8A46A] text-[#1B1A17] border-[#C8A46A] font-semibold shadow-xs'
                  : 'bg-[#F8F3EB] border-[#EADFCF] hover:border-[#C8A46A] text-[#1B1A17]'
              }`}
            >
              ELN-RZP-9042 (Razorpay Verified)
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
                    Active Dispatch Manifest
                  </span>
                  <h2 className="font-serif-luxury text-2xl sm:text-3xl text-[#1B1A17]">
                    Order #{currentTrackingId.toUpperCase()}
                  </h2>
                </div>

                <div className="flex items-center space-x-2">
                  <span className="px-3.5 py-1.5 bg-[#EEF2E8] border border-[#7D9075]/30 text-[#55624E] text-xs font-semibold rounded-full flex items-center space-x-1.5 shadow-xs">
                    <span className="w-2 h-2 rounded-full bg-[#7D9075] animate-pulse" />
                    <span>In Transit (On Schedule)</span>
                  </span>
                </div>
              </div>

              {/* Carrier Logistics Badge Banner */}
              <div className="p-5 rounded-2xl bg-[#F8F3EB] border border-[#EADFCF] grid grid-cols-1 sm:grid-cols-3 gap-4 text-xs">
                <div>
                  <span className="text-[10px] uppercase tracking-wider text-[#8E857A] block font-semibold mb-0.5">Logistics Partner</span>
                  <span className="font-semibold text-[#1B1A17]">BlueDart Express (Shiprocket)</span>
                </div>
                <div>
                  <span className="text-[10px] uppercase tracking-wider text-[#8E857A] block font-semibold mb-0.5">AWB Tracking Code</span>
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
                  <span className="text-[10px] uppercase tracking-wider text-[#8E857A] block font-semibold mb-0.5">Estimated Arrival</span>
                  <span className="font-semibold text-[#7D9075]">2-3 Business Days</span>
                </div>
              </div>

              {/* 5-Stage Milestone Progression */}
              <div className="space-y-6 pt-4">
                <div className="flex items-center justify-between">
                  <h3 className="text-xs uppercase tracking-widest font-semibold text-[#8E857A]">
                    Milestone Telemetry Progression
                  </h3>
                  <span className="text-[10px] font-mono text-[#8E857A]">Updated Just Now</span>
                </div>

                <div className="relative pl-6 space-y-8 before:absolute before:left-2.5 before:top-3 before:bottom-3 before:w-0.5 before:bg-[#EADFCF]">
                  {milestones.map((m, idx) => (
                    <div key={idx} className="relative group">
                      {/* Status Dot */}
                      <div
                        className={`absolute -left-6 top-0.5 w-5 h-5 rounded-full flex items-center justify-center text-[10px] font-bold ${
                          m.completed
                            ? 'bg-[#7D9075] text-[#FFFDF9]'
                            : m.active
                            ? 'bg-[#C8A46A] text-[#1B1A17] ring-4 ring-[#C8A46A]/20 animate-pulse'
                            : 'bg-[#EADFCF] text-[#8E857A]'
                        }`}
                      >
                        {m.completed ? <Check className="w-3 h-3 stroke-[3]" /> : idx + 1}
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
            <div className="bg-[#FFFDF9] rounded-3xl p-6 sm:p-8 border border-[#EFE3D3] shadow-card space-y-6">
              <h3 className="font-serif-luxury text-xl text-[#1B1A17] pb-3 border-b border-[#EADFCF]">
                Package Contents
              </h3>

              {orderData && (
                <div className="space-y-3 text-xs">
                  <div className="space-y-2 pb-3 border-b border-[#EAE1D3]">
                    {orderData.items.map((item, idx) => (
                      <div key={idx} className="flex justify-between items-center text-[#5E584F]">
                        <span className="truncate max-w-[180px]">{item.name} (x{item.quantity})</span>
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
                    <span className="font-semibold text-[#1B1A17] text-right max-w-[170px] truncate">{orderData.address}</span>
                  </div>

                  <div className="flex justify-between text-[#8E857A]">
                    <span>Payment Gateway</span>
                    <span className="font-semibold text-[#7D9075]">Razorpay Sandbox Verified</span>
                  </div>

                  <div className="flex justify-between text-sm font-semibold text-[#1B1A17] pt-2 border-t border-[#EAE1D3]">
                    <span>Total Settled</span>
                    <span className="font-serif-luxury text-lg font-bold">${orderData.total.toFixed(2)}</span>
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
                  className="w-full py-3.5 bg-[#1B1A17] text-[#FFFDF9] rounded-full text-xs uppercase tracking-widest font-semibold hover:bg-[#322F2A] transition-all flex items-center justify-center space-x-2 shadow-xs cursor-pointer"
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
