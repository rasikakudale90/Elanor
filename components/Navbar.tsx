'use client';

import React, { useState, useEffect } from 'react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useStore } from '@/context/StoreContext';
import { CONCERNS } from '@/data/products';
import { Search, ShoppingBag, Heart, Sparkles, SlidersHorizontal, Menu, X } from 'lucide-react';

export default function Navbar() {
  const pathname = usePathname();
  const { cartCount, wishlist, setIsCartOpen, setIsSearchOpen, compareList } = useStore();
  const [isScrolled, setIsScrolled] = useState(false);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [shopDropdownOpen, setShopDropdownOpen] = useState(false);

  useEffect(() => {
    const handleScroll = () => {
      setIsScrolled(window.scrollY > 20);
    };
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  // Close mobile menu on path change
  useEffect(() => {
    setMobileMenuOpen(false);
    setShopDropdownOpen(false);
  }, [pathname]);

  return (
    <>
      {/* Top Banner */}
      <div className="bg-[#1B1A17] text-[#FDFBF8] text-xs tracking-widest uppercase py-2 px-4 text-center font-medium flex items-center justify-center space-x-3 z-50 relative">
        <span className="inline-block w-1.5 h-1.5 rounded-full bg-[#C8A46A] animate-pulse"></span>
        <span>Complimentary Bespoke Discovery Sample & Gold Gift Packaging on Orders Over $200</span>
        <span className="inline-block w-1.5 h-1.5 rounded-full bg-[#C8A46A] animate-pulse"></span>
      </div>

      {/* Main Glass Navbar */}
      <header
        className={`sticky top-0 z-40 transition-all duration-300 ${
          isScrolled
            ? 'glass-nav py-3 shadow-soft'
            : 'bg-[#F8F3EB]/90 backdrop-blur-md py-5 border-b border-[#EADFCF]/60'
        }`}
      >
        <div className="max-w-[1440px] mx-auto px-6 sm:px-10 flex items-center justify-between">
          {/* Left: Navigation Links */}
          <nav className="hidden lg:flex items-center space-x-8 text-sm tracking-wide text-[#5E584F]">
            <div
              className="relative group py-2"
              onMouseEnter={() => setShopDropdownOpen(true)}
              onMouseLeave={() => setShopDropdownOpen(false)}
            >
              <Link
                href="/shop"
                className={`transition-colors duration-200 hover:text-[#1B1A17] flex items-center space-x-1 ${
                  pathname === '/shop' ? 'text-[#1B1A17] font-medium' : ''
                }`}
              >
                <span>Curated Shop</span>
                <span className="text-[10px] text-[#C8A46A]">▾</span>
              </Link>

              {/* Mega Dropdown */}
              {shopDropdownOpen && (
                <div className="absolute top-full -left-4 w-[540px] glass-dropdown rounded-2xl p-6 shadow-card transition-all z-50 border border-[#E2D4C3]">
                  <div className="grid grid-cols-2 gap-6">
                    <div>
                      <p className="text-xs font-semibold text-[#8E857A] uppercase tracking-wider mb-3">
                        Shop By Concern
                      </p>
                      <ul className="space-y-2 text-sm">
                        {CONCERNS.map((c) => (
                          <li key={c.slug}>
                            <Link
                              href={`/concern/${c.slug}`}
                              className="text-[#1B1A17] hover:text-[#C8A46A] flex items-center justify-between transition-colors py-1 group/item"
                            >
                              <span>{c.name}</span>
                              <span className="text-xs text-[#8E857A] opacity-0 group-hover/item:opacity-100 transition-opacity">
                                →
                              </span>
                            </Link>
                          </li>
                        ))}
                      </ul>
                    </div>

                    <div className="border-l border-[#EADFCF] pl-6">
                      <p className="text-xs font-semibold text-[#8E857A] uppercase tracking-wider mb-3">
                        Categories
                      </p>
                      <ul className="space-y-2 text-sm text-[#1B1A17]">
                        <li>
                          <Link href="/shop?category=Serums" className="hover:text-[#C8A46A] block py-1">
                            Botanical Serums
                          </Link>
                        </li>
                        <li>
                          <Link href="/shop?category=Creams" className="hover:text-[#C8A46A] block py-1">
                            Restorative Creams
                          </Link>
                        </li>
                        <li>
                          <Link href="/shop?category=Elixirs" className="hover:text-[#C8A46A] block py-1">
                            Nocturnal Elixirs & Mists
                          </Link>
                        </li>
                        <li>
                          <Link href="/shop?category=Eye%20Care" className="hover:text-[#C8A46A] block py-1">
                            Sculpting Eye Treatments
                          </Link>
                        </li>
                      </ul>
                      <div className="mt-4 pt-3 border-t border-[#EADFCF]">
                        <Link
                          href="/ai-skin-concierge"
                          className="text-xs font-medium text-[#7D9075] flex items-center space-x-1.5 hover:underline"
                        >
                          <Sparkles className="w-3.5 h-3.5" />
                          <span>Meet AI Skin Concierge</span>
                        </Link>
                      </div>
                    </div>
                  </div>
                </div>
              )}
            </div>

            <Link
              href="/ai-skin-concierge"
              className={`flex items-center space-x-1.5 transition-colors duration-200 hover:text-[#1B1A17] ${
                pathname === '/ai-skin-concierge' ? 'text-[#1B1A17] font-semibold' : ''
              }`}
            >
              <Sparkles className="w-3.5 h-3.5 text-[#C8A46A]" />
              <span>Skin Concierge</span>
            </Link>

            <Link
              href="/ingredients"
              className={`transition-colors duration-200 hover:text-[#1B1A17] ${
                pathname === '/ingredients' ? 'text-[#1B1A17] font-medium' : ''
              }`}
            >
              Ingredient Explorer
            </Link>

            <Link
              href="/compare"
              className={`flex items-center space-x-1.5 transition-colors duration-200 hover:text-[#1B1A17] ${
                pathname === '/compare' ? 'text-[#1B1A17] font-medium' : ''
              }`}
            >
              <SlidersHorizontal className="w-3.5 h-3.5 text-[#8E857A]" />
              <span>Compare</span>
              {compareList.length > 0 && (
                <span className="text-[10px] bg-[#EFE3D3] text-[#1B1A17] font-semibold px-1.5 py-0.5 rounded-full">
                  {compareList.length}
                </span>
              )}
            </Link>

            <Link
              href="/brands"
              className={`transition-colors duration-200 hover:text-[#1B1A17] ${
                pathname === '/brands' ? 'text-[#1B1A17] font-medium' : ''
              }`}
            >
              Maison Élanor
            </Link>
          </nav>

          {/* Center: Brand Identity */}
          <div className="flex flex-col items-center">
            <Link href="/" className="group text-center flex flex-col items-center py-1">
              <span className="font-serif-luxury text-3xl sm:text-4xl tracking-[0.06em] text-[#1B1A17] group-hover:text-[#C8A46A] transition-colors leading-tight inline-block">
                Élanor
              </span>
              <span className="block text-[9px] tracking-[0.28em] uppercase text-[#8E857A] -mt-0.5">
                Pure Beauty • Naturally
              </span>
            </Link>
          </div>

          {/* Right: Actions */}
          <div className="flex items-center space-x-5 sm:space-x-6 text-[#1B1A17]">
            {/* Search Trigger */}
            <button
              onClick={() => setIsSearchOpen(true)}
              aria-label="Search Catalog"
              className="p-1.5 hover:text-[#C8A46A] transition-colors duration-200 flex items-center space-x-1.5"
            >
              <Search className="w-5 h-5" strokeWidth={1.75} />
              <span className="hidden xl:inline text-xs text-[#8E857A] tracking-wider">Search</span>
            </button>

            {/* Wishlist Link */}
            <Link
              href="/wishlist"
              aria-label="Wishlist"
              className="p-1.5 hover:text-[#C8A46A] transition-colors duration-200 relative"
            >
              <Heart
                className={`w-5 h-5 ${wishlist.length > 0 ? 'fill-[#C8A46A] text-[#C8A46A]' : ''}`}
                strokeWidth={1.75}
              />
              {wishlist.length > 0 && (
                <span className="absolute -top-1 -right-1 bg-[#1B1A17] text-[#FDFBF8] text-[10px] w-4 h-4 rounded-full flex items-center justify-center font-medium">
                  {wishlist.length}
                </span>
              )}
            </Link>

            {/* Cart Drawer Trigger */}
            <button
              onClick={() => setIsCartOpen(true)}
              aria-label="View Shopping Bag"
              className="p-2 bg-[#1B1A17] text-[#FFFDF9] rounded-full hover:bg-[#322F2A] transition-all duration-200 flex items-center space-x-2 pl-3.5 pr-4 shadow-sm group"
            >
              <ShoppingBag className="w-4 h-4 group-hover:scale-110 transition-transform" strokeWidth={2} />
              <span className="text-xs font-medium tracking-wide">
                {cartCount > 0 ? `${cartCount} items` : 'Bag'}
              </span>
            </button>

            {/* Mobile Menu Toggle */}
            <button
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
              className="lg:hidden p-1.5 text-[#1B1A17]"
              aria-label="Toggle Navigation Menu"
            >
              {mobileMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
            </button>
          </div>
        </div>

        {/* Mobile Navigation Drawer */}
        {mobileMenuOpen && (
          <div className="lg:hidden bg-[#F8F3EB] border-b border-[#E2D4C3] px-6 py-6 space-y-4 animate-in fade-in slide-in-from-top-4 duration-300">
            <Link
              href="/shop"
              className="block text-base font-medium text-[#1B1A17] py-2 border-b border-[#EAE1D3]"
            >
              Curated Collection (Shop All)
            </Link>
            <div className="pl-3 space-y-2 border-l-2 border-[#C8A46A]/50 my-2">
              <p className="text-xs uppercase tracking-wider text-[#8E857A]">Shop By Concern</p>
              {CONCERNS.slice(0, 4).map((c) => (
                <Link
                  key={c.slug}
                  href={`/concern/${c.slug}`}
                  className="block text-sm text-[#5E584F] hover:text-[#1B1A17]"
                >
                  {c.name}
                </Link>
              ))}
            </div>
            <Link
              href="/routine-builder"
              className="block text-base font-medium text-[#1B1A17] py-2 border-b border-[#EAE1D3] flex items-center justify-between"
            >
              <span>AI Routine Diagnostic</span>
              <Sparkles className="w-4 h-4 text-[#C8A46A]" />
            </Link>
            <Link
              href="/ingredients"
              className="block text-base font-medium text-[#1B1A17] py-2 border-b border-[#EAE1D3]"
            >
              Ingredient Explorer & Synergy
            </Link>
            <Link
              href="/compare"
              className="block text-base font-medium text-[#1B1A17] py-2 border-b border-[#EAE1D3]"
            >
              Product Formula Comparator
            </Link>
            <Link
              href="/brands"
              className="block text-base font-medium text-[#1B1A17] py-2"
            >
              Maison Élanor Philosophy
            </Link>
          </div>
        )}
      </header>
    </>
  );
}
