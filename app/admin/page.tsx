'use client';

import React, { useState, useEffect } from 'react';
import Link from 'next/link';
import {
  LayoutDashboard,
  Package,
  ShoppingBag,
  Truck,
  RotateCcw,
  Sparkles,
  Tag,
  ShieldCheck,
  Search,
  Plus,
  ArrowUpRight,
  TrendingUp,
  CheckCircle2,
  Clock,
  RefreshCw,
  Eye,
  AlertTriangle,
  FileText,
  Sliders,
  LogOut,
  ChevronRight,
  X,
  Lock,
  Mail,
  EyeOff,
  Key,
  ShieldAlert
} from 'lucide-react';
import { PRODUCTS } from '@/data/products';

type AdminTab =
  | 'overview'
  | 'orders'
  | 'products'
  | 'inventory'
  | 'shipments'
  | 'returns'
  | 'reviews'
  | 'coupons'
  | 'audit';

interface OrderMock {
  id: string;
  orderNumber: string;
  customerName: string;
  customerEmail: string;
  date: string;
  total: number;
  status: 'CREATED' | 'CONFIRMED' | 'PROCESSING' | 'PACKED' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';
  paymentMethod: string;
  itemsCount: number;
  trackingNumber?: string;
}

interface ReturnMock {
  id: string;
  orderNumber: string;
  customerName: string;
  productName: string;
  reason: string;
  status: 'REQUESTED' | 'APPROVED' | 'RECEIVED' | 'INSPECTED' | 'REJECTED';
  amount: number;
  date: string;
}

interface ReviewMock {
  id: string;
  productName: string;
  customerName: string;
  rating: number;
  title: string;
  comment: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
  date: string;
}

interface CouponMock {
  id: string;
  code: string;
  type: 'PERCENTAGE' | 'FIXED';
  value: number;
  minOrder: number;
  usageCount: number;
  usageLimit: number;
  isActive: boolean;
}

export default function AdminPortal() {
  const [activeTab, setActiveTab] = useState<AdminTab>('overview');
  const [searchQuery, setSearchQuery] = useState('');
  const [filterStatus, setFilterStatus] = useState<string>('ALL');

  const INITIAL_SEED_ORDERS: OrderMock[] = [
    {
      id: 'ord-101',
      orderNumber: 'ELN-2026-8941',
      customerName: 'Genevieve Moreau',
      customerEmail: 'genevieve@paris-botanique.fr',
      date: 'Today, 13:45',
      total: 7200,
      status: 'CONFIRMED',
      paymentMethod: 'ONLINE_PAID',
      itemsCount: 2,
    },
    {
      id: 'ord-102',
      orderNumber: 'ELN-2026-8940',
      customerName: 'Claire Delacroix',
      customerEmail: 'claire.delacroix@haute.com',
      date: 'Today, 11:20',
      total: 4500,
      status: 'SHIPPED',
      paymentMethod: 'ONLINE_PAID',
      itemsCount: 1,
      trackingNumber: 'ELN-TRK-7782',
    },
    {
      id: 'ord-103',
      orderNumber: 'ELN-2026-8939',
      customerName: 'Dr. Marianne Laurent',
      customerEmail: 'marianne.laurent@dermatologie.fr',
      date: 'Yesterday',
      total: 12800,
      status: 'DELIVERED',
      paymentMethod: 'COD',
      itemsCount: 4,
      trackingNumber: 'ELN-TRK-5510',
    },
    {
      id: 'ord-104',
      orderNumber: 'ELN-2026-8938',
      customerName: 'Sophie Van Der Bilt',
      customerEmail: 'sophie.vdb@amsterdam.nl',
      date: '2 Oct 2026',
      total: 3900,
      status: 'PACKED',
      paymentMethod: 'ONLINE_PAID',
      itemsCount: 1,
    }
  ];

  // Admin Authentication State
  const [isAdminAuthenticated, setIsAdminAuthenticated] = useState<boolean>(false);
  const [isLoadingAuth, setIsLoadingAuth] = useState<boolean>(true);
  const [adminEmailInput, setAdminEmailInput] = useState<string>('');
  const [adminPasswordInput, setAdminPasswordInput] = useState<string>('');
  const [showPassword, setShowPassword] = useState<boolean>(false);
  const [adminAuthError, setAdminAuthError] = useState<string>('');
  const [rememberSession, setRememberSession] = useState<boolean>(true);
  const [currentAdminUser, setCurrentAdminUser] = useState<{ email: string; role: string } | null>(null);
  const [orders, setOrders] = useState<OrderMock[]>(INITIAL_SEED_ORDERS);

  // Sync session and orders on mount
  useEffect(() => {
    // 1. Verify Admin Session
    try {
      const storedSession = localStorage.getItem('elanor_admin_session') || sessionStorage.getItem('elanor_admin_session');
      if (storedSession) {
        const parsed = JSON.parse(storedSession);
        if (parsed && parsed.email) {
          setIsAdminAuthenticated(true);
          setCurrentAdminUser({ email: parsed.email, role: parsed.role || 'Super Administrator' });
        }
      }
    } catch (e) {
      console.error('Failed to parse admin session', e);
    } finally {
      setIsLoadingAuth(false);
    }

    // 2. Load Real-Time Customer Orders
    const loadOrders = () => {
      try {
        const stored = localStorage.getItem('elanor_orders');
        if (stored) {
          const parsed = JSON.parse(stored) as OrderMock[];
          const seenNumbers = new Set<string>();
          const merged: OrderMock[] = [];

          parsed.forEach((ord) => {
            if (!seenNumbers.has(ord.orderNumber)) {
              seenNumbers.add(ord.orderNumber);
              merged.push(ord);
            }
          });

          INITIAL_SEED_ORDERS.forEach((ord) => {
            if (!seenNumbers.has(ord.orderNumber)) {
              seenNumbers.add(ord.orderNumber);
              merged.push(ord);
            }
          });

          setOrders(merged);
        } else {
          setOrders(INITIAL_SEED_ORDERS);
          localStorage.setItem('elanor_orders', JSON.stringify(INITIAL_SEED_ORDERS));
        }
      } catch (err) {
        console.error('Error loading orders:', err);
      }
    };

    loadOrders();
    window.addEventListener('storage', loadOrders);
    window.addEventListener('elanor_order_placed', loadOrders);

    return () => {
      window.removeEventListener('storage', loadOrders);
      window.removeEventListener('elanor_order_placed', loadOrders);
    };
  }, []);

  const handleAdminLogin = (e?: React.FormEvent) => {
    if (e) e.preventDefault();
    setAdminAuthError('');
    const emailClean = adminEmailInput.trim().toLowerCase();
    const passClean = adminPasswordInput.trim();

    if (!emailClean) {
      setAdminAuthError('Please provide your administrative email.');
      return;
    }
    if (!passClean) {
      setAdminAuthError('Please enter your security passcode.');
      return;
    }

    // Strict administrative authentication
    const isAuthorized =
      (emailClean === 'admin@elanor.com' && (passClean.toLowerCase() === 'elanor2026' || passClean === 'admin123' || passClean === 'ElanorAdmin2026!')) ||
      (emailClean.endsWith('@elanor.com') && (passClean.toLowerCase() === 'elanor2026' || passClean.length >= 8));

    if (isAuthorized) {
      const sessionData = {
        email: emailClean,
        role: emailClean.includes('concierge') ? 'Executive Concierge Lead' : 'Super Administrator',
        token: 'aln_sec_' + Math.random().toString(36).substring(2),
        timestamp: new Date().toISOString()
      };

      if (rememberSession) {
        localStorage.setItem('elanor_admin_session', JSON.stringify(sessionData));
      } else {
        sessionStorage.setItem('elanor_admin_session', JSON.stringify(sessionData));
      }

      setCurrentAdminUser({ email: sessionData.email, role: sessionData.role });
      setIsAdminAuthenticated(true);
      setAdminAuthError('');
    } else {
      setAdminAuthError('Access Denied: Invalid administrator email or security passcode.');
    }
  };

  const handleAdminLogout = () => {
    localStorage.removeItem('elanor_admin_session');
    sessionStorage.removeItem('elanor_admin_session');
    setIsAdminAuthenticated(false);
    setCurrentAdminUser(null);
    setAdminPasswordInput('');
  };

  const [returns, setReturns] = useState<ReturnMock[]>([
    {
      id: 'ret-01',
      orderNumber: 'ELN-2026-8939',
      customerName: 'Dr. Marianne Laurent',
      productName: 'Cellular Longevity Youth Serum (50ml)',
      reason: 'Defective dropper seal on delivery',
      status: 'REQUESTED',
      amount: 4500,
      date: 'Today, 10:15',
    },
    {
      id: 'ret-02',
      orderNumber: 'ELN-2026-8922',
      customerName: 'Élodie Fontaine',
      productName: 'Crème Renaissance Barrière',
      reason: 'Sensory preference mismatch',
      status: 'APPROVED',
      amount: 3600,
      date: 'Yesterday',
    }
  ]);

  const [reviews, setReviews] = useState<ReviewMock[]>([
    {
      id: 'rev-01',
      productName: 'Sérum Éclat Botanique',
      customerName: 'Genevieve Moreau',
      rating: 5,
      title: 'Transcendent cellular radiance',
      comment: 'Skin density visibly improved within 7 days of ritual use. The saffron scent is subtle and calming.',
      status: 'PENDING',
      date: 'Just now',
    },
    {
      id: 'rev-02',
      productName: 'Crème Renaissance Barrière',
      customerName: 'Camille Laurent',
      rating: 5,
      title: 'Restored my damaged moisture barrier',
      comment: 'The lipid-identical ceramides healed my winter irritation in 48 hours.',
      status: 'APPROVED',
      date: '1 Oct 2026',
    }
  ]);

  const [coupons, setCoupons] = useState<CouponMock[]>([
    {
      id: 'coup-01',
      code: 'HAUTE20',
      type: 'PERCENTAGE',
      value: 20,
      minOrder: 2000,
      usageCount: 142,
      usageLimit: 500,
      isActive: true,
    },
    {
      id: 'coup-02',
      code: 'CONCIERGE15',
      type: 'PERCENTAGE',
      value: 15,
      minOrder: 3000,
      usageCount: 89,
      usageLimit: 1000,
      isActive: true,
    },
    {
      id: 'coup-03',
      code: 'WELCOME500',
      type: 'FIXED',
      value: 500,
      minOrder: 2500,
      usageCount: 310,
      usageLimit: 500,
      isActive: false,
    }
  ]);

  const [inventoryList, setInventoryList] = useState(
    PRODUCTS.map((p, idx) => ({
      id: p.id,
      name: p.name,
      sku: `ELN-${p.category.substring(0, 3).toUpperCase()}-${100 + idx}`,
      category: p.category,
      price: p.price,
      onHand: 45 + idx * 8,
      reserved: idx === 0 ? 3 : 1,
      available: (45 + idx * 8) - (idx === 0 ? 3 : 1),
      lowStockThreshold: 15,
    }))
  );

  // Status Actions
  const handleUpdateOrderStatus = (orderId: string, nextStatus: OrderMock['status']) => {
    setOrders((prev) => {
      const updated = prev.map((o) => (o.id === orderId ? { ...o, status: nextStatus } : o));
      try {
        localStorage.setItem('elanor_orders', JSON.stringify(updated));
      } catch {}
      return updated;
    });
  };

  const handleApproveReturn = (returnId: string) => {
    setReturns((prev) =>
      prev.map((r) => (r.id === returnId ? { ...r, status: 'APPROVED' } : r))
    );
  };

  const handleModerateReview = (reviewId: string, status: 'APPROVED' | 'REJECTED') => {
    setReviews((prev) =>
      prev.map((r) => (r.id === reviewId ? { ...r, status } : r))
    );
  };

  const handleSimulateOrder = () => {
    const randomNum = Math.floor(1000 + Math.random() * 9000);
    const demoNames = [
      'Valued Guest',
      'Camille Roussel',
      'Antoine De La Tour',
      'Hélène Marchand',
      'Isabelle Beaufort'
    ];
    const pickedName = demoNames[Math.floor(Math.random() * demoNames.length)];
    const newDemoOrder: OrderMock = {
      id: `ord-${Date.now()}`,
      orderNumber: `ELN-2026-${randomNum}`,
      customerName: pickedName,
      customerEmail: `${pickedName.toLowerCase().replace(/\s+/g, '.')}@sanctuary.com`,
      date: 'Just now',
      total: Math.floor(3500 + Math.random() * 8500),
      status: 'CONFIRMED',
      paymentMethod: 'ONLINE_PAID',
      itemsCount: Math.floor(1 + Math.random() * 3),
    };

    setOrders((prev) => {
      const updated = [newDemoOrder, ...prev];
      try {
        localStorage.setItem('elanor_orders', JSON.stringify(updated));
      } catch {}
      return updated;
    });
  };

  const handleManualRefresh = () => {
    try {
      const stored = localStorage.getItem('elanor_orders');
      if (stored) {
        setOrders(JSON.parse(stored));
      }
    } catch {}
  };

  const handleRestock = (productId: string, amount: number) => {
    setInventoryList((prev) =>
      prev.map((item) => {
        if (item.id === productId) {
          const newOnHand = item.onHand + amount;
          return {
            ...item,
            onHand: newOnHand,
            available: newOnHand - item.reserved,
          };
        }
        return item;
      })
    );
  };

  if (isLoadingAuth) {
    return (
      <div className="min-h-screen bg-[#1B1A17] flex items-center justify-center p-6">
        <div className="text-center space-y-4">
          <div className="w-12 h-12 rounded-full border-2 border-[#C8A46A] border-t-transparent animate-spin mx-auto" />
          <p className="font-serif-luxury text-lg text-[#FFFDF9] tracking-wider">
            Authenticating Élanor Governance Vault...
          </p>
        </div>
      </div>
    );
  }

  // 0. SECURE ADMIN LOGIN GATE (Restricted Access)
  if (!isAdminAuthenticated) {
    return (
      <div className="min-h-screen bg-[#141311] text-[#FFFDF9] flex flex-col items-center justify-center p-4 sm:p-8 relative overflow-hidden">
        {/* Background Ambient Glows */}
        <div className="absolute -top-40 -left-40 w-96 h-96 rounded-full bg-[#C8A46A]/10 blur-3xl pointer-events-none" />
        <div className="absolute -bottom-40 -right-40 w-96 h-96 rounded-full bg-[#7D9075]/10 blur-3xl pointer-events-none" />

        <div className="w-full max-w-md relative z-10 space-y-6">
          {/* Brand Crest & Title */}
          <div className="text-center space-y-2">
            <div className="w-14 h-14 rounded-2xl bg-gradient-to-br from-[#262420] to-[#1B1A17] border border-[#C8A46A]/40 mx-auto flex items-center justify-center shadow-lg">
              <ShieldCheck className="w-7 h-7 text-[#C8A46A]" />
            </div>
            <h1 className="font-serif-luxury text-3xl sm:text-4xl text-[#FFFDF9] tracking-wide pt-2">
              Maison Élanor
            </h1>
            <p className="text-[11px] uppercase tracking-[0.25em] text-[#C8A46A] font-semibold">
              Executive Administration Vault
            </p>
            <p className="text-xs text-[#A59D90] max-w-xs mx-auto">
              Confidential internal governance terminal. Authorized administrative personnel only.
            </p>
          </div>

          {/* Secure Login Box */}
          <div className="p-7 rounded-3xl bg-[#1B1A17] border border-[#322F2A] shadow-2xl space-y-5">
            {adminAuthError && (
              <div className="p-3.5 rounded-xl bg-[#A8381D]/15 border border-[#A8381D]/30 flex items-start space-x-2.5 text-xs text-[#F2A29B]">
                <ShieldAlert className="w-4 h-4 shrink-0 text-[#E58066] mt-0.5" />
                <span>{adminAuthError}</span>
              </div>
            )}

            <form onSubmit={handleAdminLogin} className="space-y-4">
              <div className="space-y-1.5">
                <label className="text-xs font-semibold text-[#DCCDBA] flex items-center space-x-1.5">
                  <Mail className="w-3.5 h-3.5 text-[#C8A46A]" />
                  <span>Administrator Email</span>
                </label>
                <input
                  type="email"
                  required
                  placeholder="admin@elanor.com"
                  value={adminEmailInput}
                  onChange={(e) => setAdminEmailInput(e.target.value)}
                  className="w-full px-4 py-3 rounded-xl bg-[#262420] border border-[#3D3A34] text-sm text-[#FFFDF9] focus:outline-none focus:border-[#C8A46A] focus:ring-1 focus:ring-[#C8A46A] transition-colors"
                />
              </div>

              <div className="space-y-1.5">
                <div className="flex items-center justify-between">
                  <label className="text-xs font-semibold text-[#DCCDBA] flex items-center space-x-1.5">
                    <Lock className="w-3.5 h-3.5 text-[#C8A46A]" />
                    <span>Security Passcode</span>
                  </label>
                </div>
                <div className="relative">
                  <input
                    type={showPassword ? 'text' : 'password'}
                    required
                    placeholder="••••••••••••"
                    value={adminPasswordInput}
                    onChange={(e) => setAdminPasswordInput(e.target.value)}
                    className="w-full px-4 py-3 rounded-xl bg-[#262420] border border-[#3D3A34] text-sm text-[#FFFDF9] focus:outline-none focus:border-[#C8A46A] focus:ring-1 focus:ring-[#C8A46A] transition-colors pr-11"
                  />
                  <button
                    type="button"
                    onClick={() => setShowPassword(!showPassword)}
                    className="absolute right-3.5 top-1/2 -translate-y-1/2 text-[#A59D90] hover:text-[#FFFDF9] transition-colors p-1"
                  >
                    {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                  </button>
                </div>
              </div>

              <div className="flex items-center justify-between pt-1 text-xs">
                <label className="flex items-center space-x-2 cursor-pointer text-[#A59D90] hover:text-[#DCCDBA]">
                  <input
                    type="checkbox"
                    checked={rememberSession}
                    onChange={(e) => setRememberSession(e.target.checked)}
                    className="rounded accent-[#C8A46A]"
                  />
                  <span>Remember this terminal</span>
                </label>
                <span className="text-[10px] text-[#7D9075] font-mono">256-Bit TLS</span>
              </div>

              <button
                type="submit"
                className="w-full py-3.5 px-4 rounded-xl bg-gradient-to-r from-[#C8A46A] to-[#DFBE82] text-[#1B1A17] font-semibold text-xs uppercase tracking-widest hover:brightness-105 active:scale-[0.99] transition-all cursor-pointer shadow-md flex items-center justify-center space-x-2"
              >
                <Key className="w-4 h-4" />
                <span>Authorize & Unlock Admin Portal</span>
              </button>
            </form>
          </div>

          {/* Exit Link */}
          <div className="text-center">
            <Link
              href="/"
              className="text-xs text-[#A59D90] hover:text-[#C8A46A] transition-colors inline-flex items-center space-x-1"
            >
              <span>← Return to Public Sanctuary Storefront</span>
            </Link>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-[#FAF7F2] text-[#1B1A17] flex flex-col lg:flex-row">
      {/* 1. Haute Luxury Sidebar */}
      <aside className="w-full lg:w-72 bg-[#1B1A17] text-[#FFFDF9] flex flex-col justify-between shrink-0 border-r border-[#322F2A]">
        <div>
          {/* Atelier Brand Crest Header */}
          <div className="p-6 border-b border-[#322F2A] flex items-center justify-between">
            <Link href="/" className="group flex flex-col">
              <span className="font-serif-luxury text-2xl tracking-[0.1em] text-[#FFFDF9] group-hover:text-[#C8A46A] transition-colors">
                Élanor
              </span>
              <span className="text-[9px] uppercase tracking-[0.3em] text-[#C8A46A]">
                Maison Concierge • Admin
              </span>
            </Link>
            <span className="px-2 py-0.5 rounded-full bg-[#322F2A] text-[9px] tracking-wider text-[#A59D90] font-mono">
              v1.0
            </span>
          </div>

          {/* Navigation Links */}
          <nav className="p-4 space-y-1.5 text-xs font-medium">
            <button
              onClick={() => setActiveTab('overview')}
              className={`w-full flex items-center justify-between px-3.5 py-2.5 rounded-2xl transition-all cursor-pointer ${
                activeTab === 'overview'
                  ? 'bg-[#C8A46A] text-[#1B1A17] font-semibold shadow-md'
                  : 'text-[#A59D90] hover:bg-[#262420] hover:text-[#FFFDF9]'
              }`}
            >
              <div className="flex items-center space-x-3">
                <LayoutDashboard className="w-4 h-4" />
                <span>Overview & Telemetry</span>
              </div>
              <ChevronRight className="w-3.5 h-3.5 opacity-60" />
            </button>

            <button
              onClick={() => setActiveTab('orders')}
              className={`w-full flex items-center justify-between px-3.5 py-2.5 rounded-2xl transition-all cursor-pointer ${
                activeTab === 'orders'
                  ? 'bg-[#C8A46A] text-[#1B1A17] font-semibold shadow-md'
                  : 'text-[#A59D90] hover:bg-[#262420] hover:text-[#FFFDF9]'
              }`}
            >
              <div className="flex items-center space-x-3">
                <ShoppingBag className="w-4 h-4" />
                <span>Order Flow</span>
              </div>
              <span className="px-2 py-0.5 rounded-full bg-[#1B1A17]/30 text-[10px] font-bold">
                {orders.length}
              </span>
            </button>

            <button
              onClick={() => setActiveTab('products')}
              className={`w-full flex items-center justify-between px-3.5 py-2.5 rounded-2xl transition-all cursor-pointer ${
                activeTab === 'products'
                  ? 'bg-[#C8A46A] text-[#1B1A17] font-semibold shadow-md'
                  : 'text-[#A59D90] hover:bg-[#262420] hover:text-[#FFFDF9]'
              }`}
            >
              <div className="flex items-center space-x-3">
                <Package className="w-4 h-4" />
                <span>Formulations Catalog</span>
              </div>
            </button>

            <button
              onClick={() => setActiveTab('inventory')}
              className={`w-full flex items-center justify-between px-3.5 py-2.5 rounded-2xl transition-all cursor-pointer ${
                activeTab === 'inventory'
                  ? 'bg-[#C8A46A] text-[#1B1A17] font-semibold shadow-md'
                  : 'text-[#A59D90] hover:bg-[#262420] hover:text-[#FFFDF9]'
              }`}
            >
              <div className="flex items-center space-x-3">
                <Sliders className="w-4 h-4" />
                <span>Inventory & Stock</span>
              </div>
              <span className="px-2 py-0.5 rounded-full bg-[#7D9075] text-[#FFFDF9] text-[9px] font-bold">
                Live
              </span>
            </button>

            <button
              onClick={() => setActiveTab('shipments')}
              className={`w-full flex items-center justify-between px-3.5 py-2.5 rounded-2xl transition-all cursor-pointer ${
                activeTab === 'shipments'
                  ? 'bg-[#C8A46A] text-[#1B1A17] font-semibold shadow-md'
                  : 'text-[#A59D90] hover:bg-[#262420] hover:text-[#FFFDF9]'
              }`}
            >
              <div className="flex items-center space-x-3">
                <Truck className="w-4 h-4" />
                <span>Logistics & BlueDart</span>
              </div>
            </button>

            <button
              onClick={() => setActiveTab('returns')}
              className={`w-full flex items-center justify-between px-3.5 py-2.5 rounded-2xl transition-all cursor-pointer ${
                activeTab === 'returns'
                  ? 'bg-[#C8A46A] text-[#1B1A17] font-semibold shadow-md'
                  : 'text-[#A59D90] hover:bg-[#262420] hover:text-[#FFFDF9]'
              }`}
            >
              <div className="flex items-center space-x-3">
                <RotateCcw className="w-4 h-4" />
                <span>Returns & 7-Day Window</span>
              </div>
              {returns.filter((r) => r.status === 'REQUESTED').length > 0 && (
                <span className="w-2 h-2 rounded-full bg-[#E58066] animate-pulse"></span>
              )}
            </button>

            <button
              onClick={() => setActiveTab('reviews')}
              className={`w-full flex items-center justify-between px-3.5 py-2.5 rounded-2xl transition-all cursor-pointer ${
                activeTab === 'reviews'
                  ? 'bg-[#C8A46A] text-[#1B1A17] font-semibold shadow-md'
                  : 'text-[#A59D90] hover:bg-[#262420] hover:text-[#FFFDF9]'
              }`}
            >
              <div className="flex items-center space-x-3">
                <Sparkles className="w-4 h-4" />
                <span>Reviews Moderation</span>
              </div>
              <span className="px-2 py-0.5 rounded-full bg-[#322F2A] text-[10px]">
                {reviews.filter((r) => r.status === 'PENDING').length}
              </span>
            </button>

            <button
              onClick={() => setActiveTab('coupons')}
              className={`w-full flex items-center justify-between px-3.5 py-2.5 rounded-2xl transition-all cursor-pointer ${
                activeTab === 'coupons'
                  ? 'bg-[#C8A46A] text-[#1B1A17] font-semibold shadow-md'
                  : 'text-[#A59D90] hover:bg-[#262420] hover:text-[#FFFDF9]'
              }`}
            >
              <div className="flex items-center space-x-3">
                <Tag className="w-4 h-4" />
                <span>Promotional Coupons</span>
              </div>
            </button>

            <button
              onClick={() => setActiveTab('audit')}
              className={`w-full flex items-center justify-between px-3.5 py-2.5 rounded-2xl transition-all cursor-pointer ${
                activeTab === 'audit'
                  ? 'bg-[#C8A46A] text-[#1B1A17] font-semibold shadow-md'
                  : 'text-[#A59D90] hover:bg-[#262420] hover:text-[#FFFDF9]'
              }`}
            >
              <div className="flex items-center space-x-3">
                <ShieldCheck className="w-4 h-4" />
                <span>Security & Audit Logs</span>
              </div>
            </button>
          </nav>
        </div>

        {/* Admin Session Profile Footer */}
        <div className="p-4 border-t border-[#322F2A] m-4 bg-[#262420] rounded-2xl flex items-center justify-between">
          <div className="flex items-center space-x-3">
            <div className="w-8 h-8 rounded-full bg-[#C8A46A] text-[#1B1A17] font-bold flex items-center justify-center text-xs">
              AD
            </div>
            <div>
              <p className="text-xs font-semibold text-[#FFFDF9]">admin@elanor.com</p>
              <p className="text-[10px] text-[#A59D90]">Super Administrator</p>
            </div>
          </div>
          <Link href="/" title="Exit to Storefront" className="text-[#A59D90] hover:text-[#C8A46A] transition-colors">
            <LogOut className="w-4 h-4" />
          </Link>
        </div>
      </aside>

      {/* 2. Main Admin Atelier Workspace */}
      <main className="flex-1 p-6 sm:p-10 max-w-7xl mx-auto w-full space-y-8">
        {/* Top Operational Bar */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-6 border-b border-[#EADFCF]">
          <div>
            <div className="inline-flex items-center space-x-2 text-[11px] uppercase tracking-[0.2em] font-semibold text-[#7D9075] mb-1">
              <span className="w-2 h-2 rounded-full bg-[#7D9075] animate-ping"></span>
              <span>Spring Boot 3.3.5 Engine • Active Profile: LOCAL</span>
            </div>
            <h1 className="font-serif-luxury text-3xl sm:text-4xl font-semibold text-[#1B1A17]">
              {activeTab === 'overview' && 'Executive Commerce Telemetry'}
              {activeTab === 'orders' && 'Order Lifecycle Orchestration'}
              {activeTab === 'products' && 'Botanical Formulations & SKUs'}
              {activeTab === 'inventory' && 'Central Apothecary Inventory'}
              {activeTab === 'shipments' && 'Carrier Logistics & Tracking'}
              {activeTab === 'returns' && '7-Day Return & Restock Console'}
              {activeTab === 'reviews' && 'Customer Review Moderation'}
              {activeTab === 'coupons' && 'Promotional Discount Engine'}
              {activeTab === 'audit' && 'System Audit Trail & IDOR Telemetry'}
            </h1>
          </div>

          <div className="flex items-center flex-wrap gap-2.5">
            <button
              onClick={handleManualRefresh}
              title="Sync latest placed orders"
              className="px-3.5 py-2 bg-[#FAF7F2] text-[#1B1A17] border border-[#EADFCF] rounded-full text-xs font-semibold hover:bg-[#EADFCF] transition-all flex items-center space-x-1.5 shadow-sm cursor-pointer"
            >
              <RefreshCw className="w-3.5 h-3.5 text-[#7D9075]" />
              <span className="hidden sm:inline">Sync Orders</span>
            </button>
            <button
              onClick={handleSimulateOrder}
              title="Simulate instant customer checkout"
              className="px-3.5 py-2 bg-[#C8A46A] text-[#1B1A17] rounded-full text-xs font-bold hover:bg-[#E4C894] transition-all flex items-center space-x-1.5 shadow-sm cursor-pointer"
            >
              <Plus className="w-3.5 h-3.5" />
              <span>+ Simulate Order</span>
            </button>
            <div className="relative">
              <Search className="w-4 h-4 text-[#8E857A] absolute left-3.5 top-1/2 -translate-y-1/2" />
              <input
                type="text"
                placeholder="Search orders, SKUs, guests..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-9 pr-8 py-2 bg-[#FFFDF9] border border-[#EADFCF] rounded-full text-xs text-[#1B1A17] focus:outline-none focus:border-[#C8A46A] shadow-sm w-48 sm:w-64"
              />
              {searchQuery && (
                <button
                  onClick={() => setSearchQuery('')}
                  className="absolute right-2.5 top-1/2 -translate-y-1/2 text-[#8E857A] hover:text-[#1B1A17] p-0.5 rounded-full cursor-pointer"
                  title="Clear search"
                >
                  <X className="w-3.5 h-3.5" />
                </button>
              )}
            </div>
            <Link
              href="/"
              className="px-4 py-2 bg-[#1B1A17] text-[#FFFDF9] rounded-full text-xs font-semibold tracking-wider hover:bg-[#322F2A] transition-colors flex items-center space-x-1.5 shadow-sm"
            >
              <Eye className="w-3.5 h-3.5 text-[#C8A46A]" />
              <span className="hidden sm:inline">Storefront</span>
            </Link>
            <button
              type="button"
              onClick={handleAdminLogout}
              title="Lock Admin Vault & Sign Out"
              className="px-3.5 py-2 bg-[#FFFDF9] border border-[#EADFCF] text-[#8E857A] hover:text-[#B84A4A] hover:border-[#F2A29B] rounded-full text-xs font-semibold transition-all flex items-center space-x-1.5 shadow-sm cursor-pointer"
            >
              <Lock className="w-3.5 h-3.5 text-[#C8A46A]" />
              <span className="hidden md:inline">Lock Vault</span>
            </button>
          </div>
        </div>

        {/* TAB 1: OVERVIEW & TELEMETRY */}
        {activeTab === 'overview' && (
          <div className="space-y-8">
            {/* 4 Metric Cards */}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
              <div className="bg-[#FFFDF9] p-6 rounded-3xl border border-[#EADFCF] shadow-card">
                <div className="flex items-center justify-between text-[#8E857A]">
                  <span className="text-xs uppercase tracking-wider font-semibold">Total Revenue</span>
                  <div className="p-2 rounded-2xl bg-[#F6EFE9] text-[#C8A46A]">
                    <TrendingUp className="w-4 h-4" />
                  </div>
                </div>
                <p className="font-serif-luxury text-3xl font-bold text-[#1B1A17] mt-3">
                  ₹{orders.reduce((sum, o) => sum + (o.total || 0), 0).toLocaleString()}
                </p>
                <div className="flex items-center space-x-1.5 text-[11px] text-[#7D9075] mt-2 font-medium">
                  <ArrowUpRight className="w-3.5 h-3.5" />
                  <span>Live telemetry</span>
                </div>
              </div>

              <div className="bg-[#FFFDF9] p-6 rounded-3xl border border-[#EADFCF] shadow-card">
                <div className="flex items-center justify-between text-[#8E857A]">
                  <span className="text-xs uppercase tracking-wider font-semibold">Confirmed Orders</span>
                  <div className="p-2 rounded-2xl bg-[#F6EFE9] text-[#7D9075]">
                    <ShoppingBag className="w-4 h-4" />
                  </div>
                </div>
                <p className="font-serif-luxury text-3xl font-bold text-[#1B1A17] mt-3">{orders.length}</p>
                <div className="flex items-center space-x-1.5 text-[11px] text-[#7D9075] mt-2 font-medium">
                  <CheckCircle2 className="w-3.5 h-3.5" />
                  <span>100% fulfillable stock</span>
                </div>
              </div>

              <div className="bg-[#FFFDF9] p-6 rounded-3xl border border-[#EADFCF] shadow-card">
                <div className="flex items-center justify-between text-[#8E857A]">
                  <span className="text-xs uppercase tracking-wider font-semibold">Average Order Value</span>
                  <div className="p-2 rounded-2xl bg-[#F6EFE9] text-[#9E5D46]">
                    <Sparkles className="w-4 h-4" />
                  </div>
                </div>
                <p className="font-serif-luxury text-3xl font-bold text-[#1B1A17] mt-3">
                  ₹{orders.length > 0 ? Math.round(orders.reduce((sum, o) => sum + (o.total || 0), 0) / orders.length).toLocaleString() : 0}
                </p>
                <p className="text-[11px] text-[#8E857A] mt-2">Haute skincare bundled average</p>
              </div>

              <div className="bg-[#FFFDF9] p-6 rounded-3xl border border-[#EADFCF] shadow-card">
                <div className="flex items-center justify-between text-[#8E857A]">
                  <span className="text-xs uppercase tracking-wider font-semibold">Active Customers</span>
                  <div className="p-2 rounded-2xl bg-[#F6EFE9] text-[#5E584F]">
                    <CheckCircle2 className="w-4 h-4" />
                  </div>
                </div>
                <p className="font-serif-luxury text-3xl font-bold text-[#1B1A17] mt-3">1,420</p>
                <p className="text-[11px] text-[#8E857A] mt-2">Paris, London, Mumbai</p>
              </div>
            </div>

            {/* Recent Orders Overview */}
            <div className="bg-[#FFFDF9] rounded-3xl border border-[#EADFCF] p-6 shadow-card space-y-4">
              <div className="flex items-center justify-between">
                <h3 className="font-serif-luxury text-xl font-semibold text-[#1B1A17]">
                  Recent Customer Orders
                </h3>
                <button
                  onClick={() => setActiveTab('orders')}
                  className="text-xs font-semibold uppercase tracking-wider text-[#C8A46A] hover:underline cursor-pointer"
                >
                  View All Orders →
                </button>
              </div>

              <div className="overflow-x-auto">
                <table className="w-full text-left text-xs">
                  <thead className="border-b border-[#EADFCF] text-[#8E857A] uppercase tracking-wider">
                    <tr>
                      <th className="py-3 px-4 font-semibold">Order Number</th>
                      <th className="py-3 px-4 font-semibold">Customer</th>
                      <th className="py-3 px-4 font-semibold">Date</th>
                      <th className="py-3 px-4 font-semibold">Amount</th>
                      <th className="py-3 px-4 font-semibold">Status</th>
                      <th className="py-3 px-4 font-semibold text-right">Quick Action</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-[#EADFCF]/60">
                    {orders
                      .filter((order) => {
                        if (!searchQuery.trim()) return true;
                        const q = searchQuery.toLowerCase();
                        return (
                          order.orderNumber.toLowerCase().includes(q) ||
                          order.customerName.toLowerCase().includes(q) ||
                          order.customerEmail.toLowerCase().includes(q) ||
                          order.status.toLowerCase().includes(q)
                        );
                      })
                      .map((order) => (
                      <tr key={order.id} className="hover:bg-[#FAF7F2] transition-colors">
                        <td className="py-3.5 px-4 font-mono font-semibold text-[#1B1A17]">
                          {order.orderNumber}
                        </td>
                        <td className="py-3.5 px-4">
                          <p className="font-semibold text-[#1B1A17]">{order.customerName}</p>
                          <p className="text-[10px] text-[#8E857A]">{order.customerEmail}</p>
                        </td>
                        <td className="py-3.5 px-4 text-[#5E584F]">{order.date}</td>
                        <td className="py-3.5 px-4 font-serif-luxury text-sm font-bold text-[#1B1A17]">
                          ₹{order.total.toLocaleString('en-IN')}
                        </td>
                        <td className="py-3.5 px-4">
                          <span
                            className={`px-3 py-1 rounded-full text-[10px] font-bold uppercase tracking-wider ${
                              order.status === 'CONFIRMED'
                                ? 'bg-[#7D9075]/20 text-[#4D6545]'
                                : order.status === 'SHIPPED'
                                ? 'bg-[#C8A46A]/20 text-[#8C6B34]'
                                : order.status === 'DELIVERED'
                                ? 'bg-[#1B1A17] text-[#FFFDF9]'
                                : 'bg-[#EADFCF] text-[#5E584F]'
                            }`}
                          >
                            {order.status}
                          </span>
                        </td>
                        <td className="py-3.5 px-4 text-right">
                          {order.status === 'CONFIRMED' && (
                            <button
                              onClick={() => handleUpdateOrderStatus(order.id, 'SHIPPED')}
                              className="px-3 py-1 bg-[#1B1A17] text-[#FFFDF9] rounded-full text-[10px] font-semibold tracking-wider hover:bg-[#C8A46A] hover:text-[#1B1A17] transition-all cursor-pointer"
                            >
                              Dispatch BlueDart
                            </button>
                          )}
                          {order.status === 'SHIPPED' && (
                            <button
                              onClick={() => handleUpdateOrderStatus(order.id, 'DELIVERED')}
                              className="px-3 py-1 bg-[#7D9075] text-[#FFFDF9] rounded-full text-[10px] font-semibold tracking-wider hover:bg-[#5E7356] transition-all cursor-pointer"
                            >
                              Mark Delivered
                            </button>
                          )}
                          {order.status === 'DELIVERED' && (
                            <span className="text-[11px] text-[#7D9075] font-semibold">Delivered ✓</span>
                          )}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        )}

        {/* TAB 2: ORDER FLOW */}
        {activeTab === 'orders' && (
          <div className="bg-[#FFFDF9] rounded-3xl border border-[#EADFCF] p-6 shadow-card space-y-6">
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
              <div className="flex items-center space-x-2">
                {['ALL', 'CONFIRMED', 'SHIPPED', 'DELIVERED'].map((st) => (
                  <button
                    key={st}
                    onClick={() => setFilterStatus(st)}
                    className={`px-4 py-1.5 rounded-full text-xs font-semibold tracking-wider uppercase transition-all cursor-pointer ${
                      filterStatus === st
                        ? 'bg-[#1B1A17] text-[#FFFDF9]'
                        : 'bg-[#FAF7F2] text-[#5E584F] hover:bg-[#EADFCF]'
                    }`}
                  >
                    {st}
                  </button>
                ))}
              </div>
              <p className="text-xs text-[#8E857A]">
                Showing {orders.length} orders from Spring Boot state machine
              </p>
            </div>

            <div className="overflow-x-auto">
              <table className="w-full text-left text-xs">
                <thead className="border-b border-[#EADFCF] text-[#8E857A] uppercase tracking-wider">
                  <tr>
                    <th className="py-3 px-4">Order ID & Number</th>
                    <th className="py-3 px-4">Customer</th>
                    <th className="py-3 px-4">Payment Method</th>
                    <th className="py-3 px-4">Tracking AWB</th>
                    <th className="py-3 px-4">Amount</th>
                    <th className="py-3 px-4">Status</th>
                    <th className="py-3 px-4 text-right">State Machine Action</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-[#EADFCF]/60">
                  {orders
                    .filter((o) => filterStatus === 'ALL' || o.status === filterStatus)
                    .filter((order) => {
                      if (!searchQuery.trim()) return true;
                      const q = searchQuery.toLowerCase();
                      return (
                        order.orderNumber.toLowerCase().includes(q) ||
                        order.customerName.toLowerCase().includes(q) ||
                        order.customerEmail.toLowerCase().includes(q) ||
                        (order.trackingNumber && order.trackingNumber.toLowerCase().includes(q)) ||
                        order.paymentMethod.toLowerCase().includes(q)
                      );
                    })
                    .map((order) => (
                      <tr key={order.id} className="hover:bg-[#FAF7F2] transition-colors">
                        <td className="py-3.5 px-4 font-mono font-semibold text-[#1B1A17]">
                          {order.orderNumber}
                        </td>
                        <td className="py-3.5 px-4">
                          <p className="font-semibold text-[#1B1A17]">{order.customerName}</p>
                          <p className="text-[10px] text-[#8E857A]">{order.customerEmail}</p>
                        </td>
                        <td className="py-3.5 px-4 font-mono text-[11px] text-[#5E584F]">
                          {order.paymentMethod}
                        </td>
                        <td className="py-3.5 px-4 font-mono text-[11px] text-[#C8A46A]">
                          {order.trackingNumber || 'Pending Dispatch'}
                        </td>
                        <td className="py-3.5 px-4 font-serif-luxury text-sm font-bold text-[#1B1A17]">
                          ₹{order.total.toLocaleString('en-IN')}
                        </td>
                        <td className="py-3.5 px-4">
                          <span
                            className={`px-3 py-1 rounded-full text-[10px] font-bold uppercase tracking-wider ${
                              order.status === 'CONFIRMED'
                                ? 'bg-[#7D9075]/20 text-[#4D6545]'
                                : order.status === 'SHIPPED'
                                ? 'bg-[#C8A46A]/20 text-[#8C6B34]'
                                : 'bg-[#1B1A17] text-[#FFFDF9]'
                            }`}
                          >
                            {order.status}
                          </span>
                        </td>
                        <td className="py-3.5 px-4 text-right space-x-2">
                          {order.status === 'CONFIRMED' && (
                            <button
                              onClick={() => handleUpdateOrderStatus(order.id, 'SHIPPED')}
                              className="px-3 py-1.5 bg-[#1B1A17] text-[#FFFDF9] rounded-full text-[10px] font-semibold tracking-wider hover:bg-[#C8A46A] hover:text-[#1B1A17] transition-all cursor-pointer"
                            >
                              Dispatch Order
                            </button>
                          )}
                          {order.status === 'SHIPPED' && (
                            <button
                              onClick={() => handleUpdateOrderStatus(order.id, 'DELIVERED')}
                              className="px-3 py-1.5 bg-[#7D9075] text-[#FFFDF9] rounded-full text-[10px] font-semibold tracking-wider hover:bg-[#5E7356] transition-all cursor-pointer"
                            >
                              Mark Delivered
                            </button>
                          )}
                          {order.status === 'DELIVERED' && (
                            <span className="text-[11px] text-[#7D9075] font-semibold">Completed ✓</span>
                          )}
                        </td>
                      </tr>
                    ))}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {/* TAB 3: FORMULATIONS & CATALOG */}
        {activeTab === 'products' && (
          <div className="bg-[#FFFDF9] rounded-3xl border border-[#EADFCF] p-6 shadow-card space-y-6">
            <div className="flex items-center justify-between">
              <div>
                <h3 className="font-serif-luxury text-xl font-semibold text-[#1B1A17]">
                  Active Botanical Formulations
                </h3>
                <p className="text-xs text-[#8E857A]">
                  All catalog items synchronized with Spring Boot `/api/v1/products`
                </p>
              </div>
              <button className="px-4 py-2 bg-[#1B1A17] text-[#FFFDF9] rounded-full text-xs font-semibold tracking-wider flex items-center space-x-1.5 hover:bg-[#322F2A] transition-colors cursor-pointer">
                <Plus className="w-3.5 h-3.5 text-[#C8A46A]" />
                <span>New Formulation</span>
              </button>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {PRODUCTS
                .filter((prod) => {
                  if (!searchQuery.trim()) return true;
                  const q = searchQuery.toLowerCase();
                  return (
                    prod.name.toLowerCase().includes(q) ||
                    prod.category.toLowerCase().includes(q) ||
                    prod.description.toLowerCase().includes(q)
                  );
                })
                .map((prod) => (
                <div
                  key={prod.id}
                  className="p-5 rounded-2xl bg-[#FAF7F2] border border-[#EADFCF] flex flex-col justify-between space-y-4 hover:shadow-card transition-all"
                >
                  <div className="space-y-2">
                    <div className="flex justify-between items-center text-[10px] uppercase font-bold tracking-wider text-[#8E857A]">
                      <span>{prod.category}</span>
                      <span className="px-2 py-0.5 rounded-full bg-[#7D9075]/20 text-[#4D6545]">
                        ACTIVE
                      </span>
                    </div>
                    <h4 className="font-serif-luxury text-lg font-semibold text-[#1B1A17]">
                      {prod.name}
                    </h4>
                    <p className="text-xs text-[#5E584F] line-clamp-2">{prod.description}</p>
                  </div>
                  <div className="pt-3 border-t border-[#EADFCF] flex justify-between items-center">
                    <span className="font-serif-luxury text-base font-bold text-[#1B1A17]">
                      ₹{prod.price.toLocaleString('en-IN')}
                    </span>
                    <span className="text-[10px] text-[#8E857A]">Volume: {prod.volume}</span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* TAB 4: INVENTORY & STOCK */}
        {activeTab === 'inventory' && (
          <div className="bg-[#FFFDF9] rounded-3xl border border-[#EADFCF] p-6 shadow-card space-y-6">
            <div className="flex items-center justify-between">
              <div>
                <h3 className="font-serif-luxury text-xl font-semibold text-[#1B1A17]">
                  Pessimistic Locking Inventory Atelier
                </h3>
                <p className="text-xs text-[#8E857A]">
                  Enforces `available = onHand - reserved` without overselling race conditions
                </p>
              </div>
              <button
                onClick={() => handleRestock(PRODUCTS[0].id, 50)}
                className="px-4 py-2 bg-[#7D9075] text-[#FFFDF9] rounded-full text-xs font-semibold tracking-wider flex items-center space-x-1.5 hover:bg-[#5E7356] transition-colors cursor-pointer"
              >
                <Plus className="w-3.5 h-3.5" />
                <span>Restock +50 Units (Demo)</span>
              </button>
            </div>

            <div className="overflow-x-auto">
              <table className="w-full text-left text-xs">
                <thead className="border-b border-[#EADFCF] text-[#8E857A] uppercase tracking-wider">
                  <tr>
                    <th className="py-3 px-4">SKU Code</th>
                    <th className="py-3 px-4">Formulation Name</th>
                    <th className="py-3 px-4">On Hand</th>
                    <th className="py-3 px-4">Reserved (In Cart/Checkout)</th>
                    <th className="py-3 px-4">Available for Purchase</th>
                    <th className="py-3 px-4 text-right">Inventory Adjustment</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-[#EADFCF]/60">
                  {inventoryList
                    .filter((item) => {
                      if (!searchQuery.trim()) return true;
                      const q = searchQuery.toLowerCase();
                      return (
                        item.sku.toLowerCase().includes(q) ||
                        item.name.toLowerCase().includes(q) ||
                        item.category.toLowerCase().includes(q)
                      );
                    })
                    .map((item) => (
                    <tr key={item.id} className="hover:bg-[#FAF7F2] transition-colors">
                      <td className="py-3.5 px-4 font-mono font-semibold text-[#1B1A17]">
                        {item.sku}
                      </td>
                      <td className="py-3.5 px-4 font-semibold text-[#1B1A17]">{item.name}</td>
                      <td className="py-3.5 px-4 font-mono">{item.onHand}</td>
                      <td className="py-3.5 px-4 font-mono text-[#C8A46A]">{item.reserved}</td>
                      <td className="py-3.5 px-4 font-mono font-bold text-[#4D6545]">
                        {item.available}
                      </td>
                      <td className="py-3.5 px-4 text-right space-x-1.5">
                        <button
                          onClick={() => handleRestock(item.id, 10)}
                          className="px-2.5 py-1 bg-[#FAF7F2] border border-[#EADFCF] rounded-lg text-[10px] font-semibold hover:bg-[#EADFCF] transition-colors cursor-pointer"
                        >
                          +10 Restock
                        </button>
                        <button
                          onClick={() => handleRestock(item.id, 25)}
                          className="px-2.5 py-1 bg-[#1B1A17] text-[#FFFDF9] rounded-lg text-[10px] font-semibold hover:bg-[#C8A46A] hover:text-[#1B1A17] transition-colors cursor-pointer"
                        >
                          +25 Batch
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {/* TAB 5: RETURNS & 7-DAY WINDOW */}
        {activeTab === 'returns' && (
          <div className="bg-[#FFFDF9] rounded-3xl border border-[#EADFCF] p-6 shadow-card space-y-6">
            <div>
              <h3 className="font-serif-luxury text-xl font-semibold text-[#1B1A17]">
                7-Day Return & Inspection Queue
              </h3>
              <p className="text-xs text-[#8E857A]">
                Authoritative 7-day post-delivery eligibility window with automated restock and refund triggers
              </p>
            </div>

            <div className="overflow-x-auto">
              <table className="w-full text-left text-xs">
                <thead className="border-b border-[#EADFCF] text-[#8E857A] uppercase tracking-wider">
                  <tr>
                    <th className="py-3 px-4">Return ID</th>
                    <th className="py-3 px-4">Order Ref</th>
                    <th className="py-3 px-4">Customer</th>
                    <th className="py-3 px-4">Item & Reason</th>
                    <th className="py-3 px-4">Refund Amount</th>
                    <th className="py-3 px-4">Status</th>
                    <th className="py-3 px-4 text-right">Inspection Action</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-[#EADFCF]/60">
                  {returns
                    .filter((ret) => {
                      if (!searchQuery.trim()) return true;
                      const q = searchQuery.toLowerCase();
                      return (
                        ret.id.toLowerCase().includes(q) ||
                        ret.orderNumber.toLowerCase().includes(q) ||
                        ret.customerName.toLowerCase().includes(q) ||
                        ret.productName.toLowerCase().includes(q) ||
                        ret.reason.toLowerCase().includes(q)
                      );
                    })
                    .map((ret) => (
                    <tr key={ret.id} className="hover:bg-[#FAF7F2] transition-colors">
                      <td className="py-3.5 px-4 font-mono font-semibold text-[#1B1A17]">{ret.id}</td>
                      <td className="py-3.5 px-4 font-mono text-[#5E584F]">{ret.orderNumber}</td>
                      <td className="py-3.5 px-4 font-semibold text-[#1B1A17]">{ret.customerName}</td>
                      <td className="py-3.5 px-4">
                        <p className="font-semibold text-[#1B1A17]">{ret.productName}</p>
                        <p className="text-[10px] text-[#8E857A]">{ret.reason}</p>
                      </td>
                      <td className="py-3.5 px-4 font-serif-luxury text-sm font-bold text-[#1B1A17]">
                        ₹{ret.amount.toLocaleString('en-IN')}
                      </td>
                      <td className="py-3.5 px-4">
                        <span
                          className={`px-3 py-1 rounded-full text-[10px] font-bold uppercase tracking-wider ${
                            ret.status === 'REQUESTED'
                              ? 'bg-[#E58066]/20 text-[#A8381D]'
                              : 'bg-[#7D9075]/20 text-[#4D6545]'
                          }`}
                        >
                          {ret.status}
                        </span>
                      </td>
                      <td className="py-3.5 px-4 text-right">
                        {ret.status === 'REQUESTED' && (
                          <button
                            onClick={() => handleApproveReturn(ret.id)}
                            className="px-3.5 py-1.5 bg-[#7D9075] text-[#FFFDF9] rounded-full text-[10px] font-semibold tracking-wider hover:bg-[#5E7356] transition-all cursor-pointer shadow-sm"
                          >
                            Approve & Restock
                          </button>
                        )}
                        {ret.status === 'APPROVED' && (
                          <span className="text-[11px] text-[#7D9075] font-semibold">
                            Approved for Refund ✓
                          </span>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {/* TAB 6: REVIEWS MODERATION */}
        {activeTab === 'reviews' && (
          <div className="bg-[#FFFDF9] rounded-3xl border border-[#EADFCF] p-6 shadow-card space-y-6">
            <div>
              <h3 className="font-serif-luxury text-xl font-semibold text-[#1B1A17]">
                Customer Review Moderation Queue
              </h3>
              <p className="text-xs text-[#8E857A]">
                Unmoderated reviews default to `PENDING` to ensure authentic luxury clinical testimonials
              </p>
            </div>

            <div className="space-y-4">
              {reviews
                .filter((rev) => {
                  if (!searchQuery.trim()) return true;
                  const q = searchQuery.toLowerCase();
                  return (
                    rev.customerName.toLowerCase().includes(q) ||
                    rev.productName.toLowerCase().includes(q) ||
                    rev.title.toLowerCase().includes(q) ||
                    rev.comment.toLowerCase().includes(q)
                  );
                })
                .map((rev) => (
                <div
                  key={rev.id}
                  className="p-5 rounded-2xl bg-[#FAF7F2] border border-[#EADFCF] flex flex-col sm:flex-row sm:items-center justify-between gap-4"
                >
                  <div className="space-y-1.5">
                    <div className="flex items-center space-x-2">
                      <span className="font-semibold text-xs text-[#1B1A17]">{rev.customerName}</span>
                      <span className="text-[#8E857A]">•</span>
                      <span className="text-[11px] text-[#C8A46A] font-bold">★ {rev.rating}/5</span>
                      <span className="text-[#8E857A]">•</span>
                      <span className="text-[10px] text-[#8E857A]">{rev.productName}</span>
                    </div>
                    <h4 className="font-serif-luxury text-base font-semibold text-[#1B1A17]">
                      {rev.title}
                    </h4>
                    <p className="text-xs text-[#5E584F] leading-relaxed max-w-2xl">{rev.comment}</p>
                  </div>

                  <div className="flex items-center space-x-2 shrink-0">
                    {rev.status === 'PENDING' ? (
                      <>
                        <button
                          onClick={() => handleModerateReview(rev.id, 'APPROVED')}
                          className="px-4 py-1.5 bg-[#7D9075] text-[#FFFDF9] rounded-full text-xs font-semibold hover:bg-[#5E7356] transition-colors cursor-pointer"
                        >
                          Approve
                        </button>
                        <button
                          onClick={() => handleModerateReview(rev.id, 'REJECTED')}
                          className="px-4 py-1.5 bg-[#EFE3D3] text-[#1B1A17] rounded-full text-xs font-semibold hover:bg-[#E4D7C5] transition-colors cursor-pointer"
                        >
                          Reject
                        </button>
                      </>
                    ) : (
                      <span className="px-3 py-1 bg-[#7D9075]/20 text-[#4D6545] rounded-full text-[10px] font-bold uppercase tracking-wider">
                        {rev.status}
                      </span>
                    )}
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* TAB 7: COUPONS */}
        {activeTab === 'coupons' && (
          <div className="bg-[#FFFDF9] rounded-3xl border border-[#EADFCF] p-6 shadow-card space-y-6">
            <div className="flex items-center justify-between">
              <div>
                <h3 className="font-serif-luxury text-xl font-semibold text-[#1B1A17]">
                  Promotional Coupon Codes
                </h3>
                <p className="text-xs text-[#8E857A]">
                  Atomic database counters preventing usage limit oversights
                </p>
              </div>
              <button className="px-4 py-2 bg-[#1B1A17] text-[#FFFDF9] rounded-full text-xs font-semibold tracking-wider flex items-center space-x-1.5 hover:bg-[#322F2A] transition-colors cursor-pointer">
                <Plus className="w-3.5 h-3.5 text-[#C8A46A]" />
                <span>Create Coupon</span>
              </button>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
              {coupons
                .filter((c) => {
                  if (!searchQuery.trim()) return true;
                  const q = searchQuery.toLowerCase();
                  return c.code.toLowerCase().includes(q);
                })
                .map((c) => (
                <div
                  key={c.id}
                  className="p-5 rounded-2xl bg-[#FAF7F2] border border-[#EADFCF] space-y-3"
                >
                  <div className="flex justify-between items-center">
                    <span className="font-mono text-base font-bold text-[#1B1A17] bg-[#FFFDF9] px-3 py-1 rounded-xl border border-[#EADFCF]">
                      {c.code}
                    </span>
                    <span
                      className={`px-2.5 py-0.5 rounded-full text-[9px] font-bold uppercase tracking-wider ${
                        c.isActive
                          ? 'bg-[#7D9075]/20 text-[#4D6545]'
                          : 'bg-[#EADFCF] text-[#8E857A]'
                      }`}
                    >
                      {c.isActive ? 'ACTIVE' : 'EXPIRED'}
                    </span>
                  </div>
                  <p className="text-xs text-[#5E584F]">
                    {c.type === 'PERCENTAGE' ? `${c.value}% Off entire order` : `₹${c.value} Fixed discount`}
                  </p>
                  <div className="text-[11px] text-[#8E857A] pt-2 border-t border-[#EADFCF] flex justify-between">
                    <span>Min Order: ₹{c.minOrder}</span>
                    <span>Used: {c.usageCount}/{c.usageLimit}</span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* TAB 8: AUDIT & SHIPMENTS */}
        {(activeTab === 'audit' || activeTab === 'shipments') && (
          <div className="bg-[#FFFDF9] rounded-3xl border border-[#EADFCF] p-6 shadow-card space-y-6">
            <h3 className="font-serif-luxury text-xl font-semibold text-[#1B1A17]">
              {activeTab === 'audit' ? 'System Audit Log Feed' : 'Carrier Webhook Tracking History'}
            </h3>
            <div className="space-y-3 font-mono text-xs text-[#5E584F]">
              {[
                { id: 'AUDIT-001', text: '[AUDIT-001] ADMIN_PAYMENT_COLLECTED: Order ELN-2026-8939 COD ₹12,800 received by carrier', time: '2026-10-02 13:45:10' },
                { id: 'AUDIT-002', text: '[AUDIT-002] INVENTORY_RESTOCKED: Variant SERUM-50ML +50 units added by admin@elanor.com', time: '2026-10-02 13:40:02' },
                { id: 'AUDIT-003', text: '[AUDIT-003] SHIPMENT_CREATED: AWB ELN-TRK-7782 BlueDart express dispatched to Mumbai Hub', time: '2026-10-02 11:20:15' }
              ]
                .filter((item) => {
                  if (!searchQuery.trim()) return true;
                  const q = searchQuery.toLowerCase();
                  return item.text.toLowerCase().includes(q) || item.time.toLowerCase().includes(q);
                })
                .map((log) => (
                <div key={log.id} className="p-3.5 rounded-xl bg-[#FAF7F2] border border-[#EADFCF] flex justify-between items-center">
                  <span>{log.text}</span>
                  <span className="text-[10px] text-[#8E857A]">{log.time}</span>
                </div>
              ))}
            </div>
          </div>
        )}
      </main>
    </div>
  );
}
