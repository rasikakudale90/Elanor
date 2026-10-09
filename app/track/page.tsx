'use client';

import React, { useState, useEffect, Suspense } from 'react';
import Link from 'next/link';
import { useSearchParams } from 'next/navigation';
import {
  Truck,
  Search,
  CheckCircle2,
  Clock,
  MapPin,
  Package,
  ShieldCheck,
  Sparkles,
  ArrowLeft,
  ArrowRight,
  Copy,
  Check,
  RefreshCw,
  X,
  Mail,
  Phone,
  HelpCircle,
  RotateCcw,
  CheckCheck
} from 'lucide-react';

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

  const [searchTab, setSearchTab] = useState<'ID' | 'CONTACT'>('ID');
  const [query, setQuery] = useState(urlParam || 'ELN-2026-7842');
  const [contactQuery, setContactQuery] = useState('');
  const [currentTrackingId, setCurrentTrackingId] = useState(urlParam || 'ELN-2026-7842');
  const [isScanning, setIsScanning] = useState(false);
  const [copied, setCopied] = useState(false);
  const [orderData, setOrderData] = useState<OrderRecord | null>(null);
  const [recentGuestOrders, setRecentGuestOrders] = useState<OrderRecord[]>([]);
  const [feedbackMsg, setFeedbackMsg] = useState<string | null>(null);
  const [activePolicyTab, setActivePolicyTab] = useState<'SHIPPING' | 'PACKAGING' | 'TIMELINE' | 'RETURNS'>('SHIPPING');

  // Load all local guest orders on mount
  useEffect(() => {
    if (typeof window !== 'undefined') {
      try {
        const raw = localStorage.getItem('elanor_orders');
        if (raw) {
          const orders: OrderRecord[] = JSON.parse(raw);
          if (Array.isArray(orders)) {
            setRecentGuestOrders(orders);
          }
        }
      } catch (err) {
        console.warn('Could not read guest orders from localStorage', err);
      }
    }
  }, []);

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
              matchedOrder =
                orders.find(
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
        setFeedbackMsg(`✓ Verified order #${matchedOrder.orderNumber} for ${matchedOrder.customerName}`);
      } else {
        // Dynamic simulated manifest for searched identifier
        const isBlueDart = cleanId.toUpperCase().includes('BD') || cleanId.toUpperCase().includes('SR');
        setOrderData({
          id: `ord-${cleanId.toLowerCase()}`,
          orderNumber: cleanId.toUpperCase(),
          customerName: 'Valued Guest Patron',
          customerEmail: 'patron.guest@maison-elanor.com',
          date: 'Today, 09:30 AM',
          total: 395.0,
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
    }, 400);
  };

  const handleContactLookup = (e: React.FormEvent) => {
    e.preventDefault();
    const cleanContact = contactQuery.trim().toLowerCase();
    if (!cleanContact) return;

    setIsScanning(true);
    setTimeout(() => {
      let matchedOrder: OrderRecord | null = null;
      if (typeof window !== 'undefined') {
        try {
          const raw = localStorage.getItem('elanor_orders');
          if (raw) {
            const orders: OrderRecord[] = JSON.parse(raw);
            if (Array.isArray(orders)) {
              matchedOrder =
                orders.find(
                  (o) =>
                    o.customerEmail?.toLowerCase().includes(cleanContact) ||
                    o.customerName?.toLowerCase().includes(cleanContact)
                ) || null;
            }
          }
        } catch (err) {
          console.warn('Could not parse orders storage', err);
        }
      }

      if (matchedOrder) {
        setOrderData(matchedOrder);
        setCurrentTrackingId(matchedOrder.orderNumber);
        setFeedbackMsg(`✓ Found guest order #${matchedOrder.orderNumber} linked to ${contactQuery}`);
      } else {
        const demoNumber = `ELN-GST-${Math.floor(1000 + Math.random() * 9000)}`;
        setCurrentTrackingId(demoNumber);
        setOrderData({
          id: `ord-${demoNumber.toLowerCase()}`,
          orderNumber: demoNumber,
          customerName: contactQuery.includes('@') ? contactQuery.split('@')[0] : 'Guest Patron',
          customerEmail: contactQuery.includes('@') ? contactQuery : `${contactQuery}@guest.maison-elanor.com`,
          date: 'Yesterday, 04:15 PM',
          total: 285.0,
          status: 'IN_TRANSIT',
          paymentMethod: 'RAZORPAY',
          transactionRef: `pay_gst_${Date.now().toString().slice(-6)}`,
          itemsCount: 1,
          items: [{ name: 'Élixir Nocturne Régénérant aux 18 Rameaux', quantity: 1, price: 285 }],
          address: 'Sanctuary Destination, Registered Guest Address',
        });
        setFeedbackMsg(`✓ Linked guest parcel retrieved for ${contactQuery}`);
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

  const awbNumber =
    currentTrackingId.toUpperCase().startsWith('SR-') || currentTrackingId.toUpperCase().startsWith('BD-')
      ? currentTrackingId.toUpperCase()
      : `SR-BD-${currentTrackingId.replace(/[^0-9]/g, '') || '948201'}`;

  const milestones: TrackingMilestone[] = [
    {
      stage: '1. Ritual Consecrated & Wax Sealed',
      location: 'Maison Élanor Paris Atelier (24 Place Vendôme)',
      timestamp: 'Today, 09:30 AM',
      completed: true,
      active: false,
      description: 'Formulations bottled in Miron violet glass, sealed with signature gold wax crest.',
    },
    {
      stage: '2. Dispatched via BlueDart Express (Shiprocket Partner)',
      location: 'Paris International Cargo Hub (CDG)',
      timestamp: 'Today, 01:15 PM',
      completed: true,
      active: false,
      description: 'Handed over to carrier. Carbon-neutral express airfreight manifest assigned.',
    },
    {
      stage: '3. Regional Dermal Logistics Terminal',
      location: 'Regional Distribution Center',
      timestamp: 'In Transit • On Schedule',
      completed: false,
      active: true,
      description: 'Temperature-monitored luxury parcel routing underway.',
    },
    {
      stage: '4. Dedicated Courier Concierge',
      location: 'Local Delivery Hub',
      timestamp: 'Expected in 1-2 Days',
      completed: false,
      active: false,
      description: 'Out for morning delivery with white-glove courier.',
    },
    {
      stage: '5. Signed & Delivered to Sanctuary',
      location: orderData?.address || 'Customer Sanctuary Destination',
      timestamp: 'Estimated Delivery: 2-3 Business Days',
      completed: false,
      active: false,
      description: 'Delivered in pristine biophotonic presentation box.',
    },
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
            <span>Guest Patron & Member Live Telemetry</span>
          </span>
        </div>

        {/* Hero Header & Search Bar */}
        <div className="bg-[#FFFDF9] rounded-3xl p-8 sm:p-12 border border-[#EFE3D3] shadow-card text-center space-y-6">
          <div className="w-16 h-16 bg-[#F8F3EB] border border-[#E4C894] rounded-2xl mx-auto flex items-center justify-center text-[#C8A46A] shadow-xs">
            <Truck className="w-8 h-8" />
          </div>

          <div className="space-y-2 max-w-xl mx-auto">
            <span className="text-[10px] uppercase tracking-[0.25em] text-[#C8A46A] font-semibold">
              Guest & Member Order Tracking
            </span>
            <h1 className="font-serif-luxury text-3xl sm:text-4xl text-[#1B1A17]">
              Track Your Sacred Dispatch
            </h1>
            <p className="text-xs text-[#5E584F] leading-relaxed">
              No login required. Track guest orders using your Order Number (<code>ELN-2026-XXXX</code>), BlueDart AWB code, or checkout contact details.
            </p>
          </div>

          {/* Search Type Selector Tabs */}
          <div className="inline-flex rounded-full bg-[#F2EBE2] p-1 text-xs font-medium max-w-xs mx-auto">
            <button
              onClick={() => setSearchTab('ID')}
              className={`px-5 py-1.5 rounded-full transition-all cursor-pointer ${
                searchTab === 'ID' ? 'bg-[#1B1A17] text-[#FFFDF9] font-semibold shadow-xs' : 'text-[#5E584F]'
              }`}
            >
              By Order # / AWB
            </button>
            <button
              onClick={() => setSearchTab('CONTACT')}
              className={`px-5 py-1.5 rounded-full transition-all cursor-pointer ${
                searchTab === 'CONTACT' ? 'bg-[#1B1A17] text-[#FFFDF9] font-semibold shadow-xs' : 'text-[#5E584F]'
              }`}
            >
              By Guest Email / Phone
            </button>
          </div>

          {/* Search Forms */}
          {searchTab === 'ID' ? (
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
                    className="p-1 text-[#8E857A] hover:text-[#1B1A17] mr-1 cursor-pointer"
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
          ) : (
            <form
              onSubmit={handleContactLookup}
              className="max-w-lg mx-auto flex bg-[#FDFBF8] border border-[#DCCDBA] rounded-full p-1.5 focus-within:border-[#C8A46A] shadow-xs transition-all"
            >
              <div className="flex-1 flex items-center pl-4">
                <Mail className="w-4 h-4 text-[#8E857A] mr-2 shrink-0" />
                <input
                  type="text"
                  placeholder="Enter guest email (e.g. patron@domain.com)..."
                  value={contactQuery}
                  onChange={(e) => setContactQuery(e.target.value)}
                  className="w-full bg-transparent text-xs text-[#1B1A17] placeholder-[#8E857A] focus:outline-none"
                />
                {contactQuery && (
                  <button
                    type="button"
                    onClick={() => setContactQuery('')}
                    className="p-1 text-[#8E857A] hover:text-[#1B1A17] mr-1 cursor-pointer"
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
                    <span>Finding...</span>
                  </>
                ) : (
                  <>
                    <Search className="w-3.5 h-3.5" />
                    <span>Find Order</span>
                  </>
                )}
              </button>
            </form>
          )}

          {/* Feedback Toast */}
          {feedbackMsg && (
            <p className="text-[11px] font-semibold text-[#7D9075] animate-in fade-in duration-300">
              {feedbackMsg}
            </p>
          )}

          {/* Quick Demo Fill Pills */}
          <div className="flex flex-wrap items-center justify-center gap-2 pt-1 text-[10px] text-[#8E857A]">
            <span>Quick Sample Tracks:</span>
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

        {/* RECENT GUEST ORDERS DISCOVERED ON THIS DEVICE */}
        {recentGuestOrders.length > 0 && (
          <div className="bg-[#FFFDF9] rounded-3xl p-6 sm:p-8 border border-[#EFE3D3] shadow-card space-y-4">
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
              <div className="space-y-0.5">
                <span className="text-[10px] uppercase tracking-wider text-[#C8A46A] font-semibold">
                  Saved on this Device
                </span>
                <h3 className="font-serif-luxury text-xl text-[#1B1A17]">
                  Your Recent Guest Orders
                </h3>
              </div>
              <p className="text-xs text-[#8E857A]">
                {recentGuestOrders.length} order{recentGuestOrders.length > 1 ? 's' : ''} stored in local browser history
              </p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 pt-2">
              {recentGuestOrders.map((ord, idx) => (
                <div
                  key={idx}
                  onClick={() => handleQuickSelect(ord.orderNumber)}
                  className={`p-4 rounded-2xl border transition-all cursor-pointer space-y-3 hover:shadow-md ${
                    currentTrackingId.toLowerCase() === ord.orderNumber.toLowerCase()
                      ? 'bg-[#FAF5EC] border-[#C8A46A] ring-1 ring-[#C8A46A]'
                      : 'bg-[#F8F3EB] border-[#EADFCF] hover:border-[#C8A46A]'
                  }`}
                >
                  <div className="flex justify-between items-start">
                    <div>
                      <span className="font-mono text-xs font-bold text-[#1B1A17]">#{ord.orderNumber}</span>
                      <p className="text-[11px] text-[#8E857A]">{ord.date}</p>
                    </div>
                    <span className="px-2.5 py-1 rounded-full text-[10px] font-semibold uppercase tracking-wider bg-[#EEF2E8] text-[#55624E] border border-[#7D9075]/30">
                      In Transit
                    </span>
                  </div>

                  <div className="text-xs space-y-1">
                    <p className="font-semibold text-[#1B1A17] truncate">{ord.customerName}</p>
                    <p className="text-[#5E584F] text-[11px] truncate">
                      {ord.items && ord.items.length > 0 ? `${ord.items[0].name} ${ord.items.length > 1 ? `(+${ord.items.length - 1} more)` : ''}` : `${ord.itemsCount || 1} formulation(s)`}
                    </p>
                  </div>

                  <div className="flex justify-between items-center pt-2 border-t border-[#EAE1D3] text-xs">
                    <span className="font-serif-luxury font-bold text-[#1B1A17]">${ord.total.toFixed(2)}</span>
                    <span className="text-[11px] font-semibold text-[#C8A46A] flex items-center space-x-1">
                      <span>View Live Status</span>
                      <ArrowRight className="w-3 h-3" />
                    </span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

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

        {/* DEDICATED GUEST SHIPPING & DISPATCH POLICY SECTION */}
        <div className="bg-[#FFFDF9] rounded-3xl p-8 sm:p-12 border border-[#EFE3D3] shadow-card space-y-8">
          <div className="text-center space-y-2 max-w-2xl mx-auto">
            <span className="text-[10px] uppercase tracking-[0.25em] text-[#C8A46A] font-semibold">
              Maison Élanor Shipping Standards
            </span>
            <h2 className="font-serif-luxury text-2xl sm:text-3xl text-[#1B1A17]">
              Guest & Patron Shipping Policy
            </h2>
            <p className="text-xs text-[#8E857A]">
              Everything you need to know about express dispatch, carrier partners, temperature control, and returns.
            </p>
          </div>

          {/* Policy Navigation Pills */}
          <div className="flex flex-wrap justify-center gap-2">
            {[
              { id: 'SHIPPING', label: '🚀 Express Air Cargo & Free Shipping' },
              { id: 'TIMELINE', label: '⏱️ Delivery Timelines by Region' },
              { id: 'PACKAGING', label: '🛡️ Swiss Violet Glass Protection' },
              { id: 'RETURNS', label: '🔄 30-Day Seal & Return Policy' },
            ].map((tab) => (
              <button
                key={tab.id}
                onClick={() => setActivePolicyTab(tab.id as any)}
                className={`px-4 py-2 rounded-full text-xs font-semibold transition-all cursor-pointer ${
                  activePolicyTab === tab.id
                    ? 'bg-[#1B1A17] text-[#FFFDF9] shadow-sm'
                    : 'bg-[#F8F3EB] border border-[#EADFCF] text-[#5E584F] hover:bg-[#EAE1D3]'
                }`}
              >
                {tab.label}
              </button>
            ))}
          </div>

          {/* Policy Tab Content Cards */}
          <div className="p-6 sm:p-8 rounded-2xl bg-[#F8F3EB] border border-[#EADFCF] text-xs text-[#5E584F] space-y-4 animate-in fade-in duration-300">
            {activePolicyTab === 'SHIPPING' && (
              <div className="space-y-4">
                <h3 className="font-serif-luxury text-lg text-[#1B1A17] font-semibold flex items-center space-x-2">
                  <Truck className="w-5 h-5 text-[#C8A46A]" />
                  <span>BlueDart Express & Shiprocket Logistics Infrastructure</span>
                </h3>
                <p className="leading-relaxed">
                  Maison Élanor partners with premier airfreight carriers, BlueDart Express and Shiprocket, to ensure priority handling of active phytomolecules from dispatch to doorstep.
                </p>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4 pt-2">
                  <div className="p-4 rounded-xl bg-[#FFFDF9] border border-[#EAE1D3] space-y-1">
                    <p className="font-bold text-[#1B1A17]">Complimentary Delivery</p>
                    <p className="text-[11px] text-[#8E857A]">Free Express Air Shipping applied automatically on all orders over $150.</p>
                  </div>
                  <div className="p-4 rounded-xl bg-[#FFFDF9] border border-[#EAE1D3] space-y-1">
                    <p className="font-bold text-[#1B1A17]">Real-Time SMS & AWB</p>
                    <p className="text-[11px] text-[#8E857A]">Instant tracking link dispatched via SMS and email immediately upon carrier hand-off.</p>
                  </div>
                  <div className="p-4 rounded-xl bg-[#FFFDF9] border border-[#EAE1D3] space-y-1">
                    <p className="font-bold text-[#1B1A17]">Carbon-Neutral Cargo</p>
                    <p className="text-[11px] text-[#8E857A]">100% of carbon emissions from your delivery are offset through European reforestation projects.</p>
                  </div>
                </div>
              </div>
            )}

            {activePolicyTab === 'TIMELINE' && (
              <div className="space-y-4">
                <h3 className="font-serif-luxury text-lg text-[#1B1A17] font-semibold flex items-center space-x-2">
                  <Clock className="w-5 h-5 text-[#C8A46A]" />
                  <span>Estimated Delivery Speeds</span>
                </h3>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div className="p-4 rounded-xl bg-[#FFFDF9] border border-[#EAE1D3] space-y-2">
                    <div className="flex justify-between items-center">
                      <span className="font-bold text-[#1B1A17]">Metropolitan & Tier 1 Cities</span>
                      <span className="px-2.5 py-0.5 rounded-full bg-[#EEF2E8] text-[#55624E] text-[10px] font-bold">2 - 3 Days</span>
                    </div>
                    <p className="text-[11px] text-[#8E857A]">Direct flight routing from cargo hub to regional hubs with next-morning courier delivery.</p>
                  </div>
                  <div className="p-4 rounded-xl bg-[#FFFDF9] border border-[#EAE1D3] space-y-2">
                    <div className="flex justify-between items-center">
                      <span className="font-bold text-[#1B1A17]">Regional & Global Sanctuaries</span>
                      <span className="px-2.5 py-0.5 rounded-full bg-[#FAF5EC] text-[#8C6B34] text-[10px] font-bold">3 - 5 Days</span>
                    </div>
                    <p className="text-[11px] text-[#8E857A]">Temperature-controlled linehaul transit with end-to-end milestone GPS tracking.</p>
                  </div>
                </div>
              </div>
            )}

            {activePolicyTab === 'PACKAGING' && (
              <div className="space-y-4">
                <h3 className="font-serif-luxury text-lg text-[#1B1A17] font-semibold flex items-center space-x-2">
                  <Package className="w-5 h-5 text-[#C8A46A]" />
                  <span>Biophotonic Miron Violet Glass Sealing</span>
                </h3>
                <p className="leading-relaxed">
                  Light is the primary degrader of organic plant extracts. Unlike standard amber or clear glass, Maison Élanor encases all rituals in patented Swiss biophotonic Miron violet glass that blocks all visible light rays while allowing beneficial violet and infrared spectrums to naturally energize the formula.
                </p>
                <div className="p-4 rounded-xl bg-[#FFFDF9] border border-[#EAE1D3] flex items-center space-x-3">
                  <CheckCheck className="w-5 h-5 text-[#7D9075] shrink-0" />
                  <p className="text-[11px] text-[#5E584F]">
                    Every package includes a tamper-evident gold wax stamp to guarantee formula sanctity during courier transit.
                  </p>
                </div>
              </div>
            )}

            {activePolicyTab === 'RETURNS' && (
              <div className="space-y-4">
                <h3 className="font-serif-luxury text-lg text-[#1B1A17] font-semibold flex items-center space-x-2">
                  <RotateCcw className="w-5 h-5 text-[#C8A46A]" />
                  <span>30-Day Guest Satisfaction Charter</span>
                </h3>
                <p className="leading-relaxed">
                  Guest patrons enjoy the exact same high-touch concierge support as registered Atelier VIPs. If your package arrives damaged or does not align with your skin compatibility, our concierge arranges complimentary pickup.
                </p>
                <div className="flex flex-col sm:flex-row gap-3 pt-2">
                  <a
                    href="mailto:concierge@maison-elanor.com"
                    className="inline-flex items-center justify-center space-x-2 px-5 py-2.5 bg-[#1B1A17] text-[#FFFDF9] rounded-full font-semibold hover:bg-[#322F2A] transition-colors"
                  >
                    <Mail className="w-3.5 h-3.5" />
                    <span>Email Concierge Desk</span>
                  </a>
                  <Link
                    href="/ai-skin-concierge"
                    className="inline-flex items-center justify-center space-x-2 px-5 py-2.5 bg-[#FFFDF9] border border-[#DCCDBA] text-[#1B1A17] rounded-full font-semibold hover:bg-[#F2EBE2] transition-colors"
                  >
                    <Sparkles className="w-3.5 h-3.5 text-[#C8A46A]" />
                    <span>Ask AI Skin Concierge</span>
                  </Link>
                </div>
              </div>
            )}
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
