'use client';

import React from 'react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useStore } from '@/context/StoreContext';
import { Home, Sparkles, Heart, ShoppingBag, Search } from 'lucide-react';

export default function MobileBottomNav() {
  const pathname = usePathname();
  const { cartCount, wishlist, setIsCartOpen, setIsSearchOpen } = useStore();

  return (
    <div className="lg:hidden fixed bottom-0 inset-x-0 z-40 bg-[#FFFDF9]/95 backdrop-blur-xl border-t border-[#EADFCF] py-2 px-4 shadow-floating">
      <div className="flex items-center justify-around">
        {/* Home */}
        <Link
          href="/"
          className={`flex flex-col items-center justify-center p-1.5 transition-colors ${
            pathname === '/' ? 'text-[#C8A46A]' : 'text-[#5E584F] hover:text-[#1B1A17]'
          }`}
        >
          <Home className="w-5 h-5" strokeWidth={pathname === '/' ? 2.2 : 1.75} />
          <span className="text-[10px] font-medium mt-0.5 tracking-tight">Home</span>
        </Link>

        {/* Shop */}
        <Link
          href="/shop"
          className={`flex flex-col items-center justify-center p-1.5 transition-colors ${
            pathname === '/shop' ? 'text-[#C8A46A]' : 'text-[#5E584F] hover:text-[#1B1A17]'
          }`}
        >
          <span className="font-serif-luxury text-lg leading-none font-bold">É</span>
          <span className="text-[10px] font-medium mt-0.5 tracking-tight">Shop</span>
        </Link>

        {/* AI Routine Diagnostic */}
        <Link
          href="/ai-skin-concierge"
          className={`flex flex-col items-center justify-center p-1.5 transition-colors ${
            pathname === '/ai-skin-concierge' ? 'text-[#C8A46A]' : 'text-[#5E584F] hover:text-[#1B1A17]'
          }`}
        >
          <Sparkles className="w-5 h-5 text-[#C8A46A]" strokeWidth={2} />
          <span className="text-[10px] font-medium mt-0.5 tracking-tight">AI Concierge</span>
        </Link>

        {/* Search */}
        <button
          onClick={() => setIsSearchOpen(true)}
          className="flex flex-col items-center justify-center p-1.5 text-[#5E584F] hover:text-[#1B1A17]"
        >
          <Search className="w-5 h-5" strokeWidth={1.75} />
          <span className="text-[10px] font-medium mt-0.5 tracking-tight">Search</span>
        </button>

        {/* Wishlist */}
        <Link
          href="/wishlist"
          className={`relative flex flex-col items-center justify-center p-1.5 transition-colors ${
            pathname === '/wishlist' ? 'text-[#C8A46A]' : 'text-[#5E584F] hover:text-[#1B1A17]'
          }`}
        >
          <Heart className={`w-5 h-5 ${wishlist.length > 0 ? 'fill-[#C8A46A] text-[#C8A46A]' : ''}`} strokeWidth={1.75} />
          {wishlist.length > 0 && (
            <span className="absolute top-1 right-2 bg-[#1B1A17] text-[#FFFDF9] text-[9px] w-3.5 h-3.5 rounded-full flex items-center justify-center font-bold">
              {wishlist.length}
            </span>
          )}
          <span className="text-[10px] font-medium mt-0.5 tracking-tight">Wishlist</span>
        </Link>

        {/* Cart Trigger */}
        <button
          onClick={() => setIsCartOpen(true)}
          className="relative flex flex-col items-center justify-center p-1.5 text-[#1B1A17]"
        >
          <div className="relative">
            <ShoppingBag className="w-5 h-5" strokeWidth={2} />
            {cartCount > 0 && (
              <span className="absolute -top-1 -right-2 bg-[#C8A46A] text-[#1B1A17] text-[9px] w-4 h-4 rounded-full flex items-center justify-center font-bold shadow-sm">
                {cartCount}
              </span>
            )}
          </div>
          <span className="text-[10px] font-semibold mt-0.5 tracking-tight">Bag</span>
        </button>
      </div>
    </div>
  );
}
