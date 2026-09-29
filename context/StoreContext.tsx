'use client';

import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { Product, PRODUCTS } from '@/data/products';

export interface CartItem {
  product: Product;
  quantity: number;
}

export interface SampleItem {
  id: string;
  name: string;
  size: string;
  category: string;
  image: string;
}

export const COMPLIMENTARY_SAMPLES: SampleItem[] = [
  {
    id: 'sample-saffron',
    name: 'Sérum Éclat Mini Ritual',
    size: '5 ml',
    category: 'Radiance',
    image: '/images/skin.jfif'
  },
  {
    id: 'sample-velvet',
    name: 'Crème Renaissance Deluxe',
    size: '7 ml',
    category: 'Barrier Repair',
    image: '/images/skin1.jfif'
  },
  {
    id: 'sample-nocturne',
    name: 'Élixir Nocturne Dropper',
    size: '3 ml',
    category: 'Anti-Aging',
    image: '/images/skin2.jfif'
  }
];

interface StoreContextType {
  cart: CartItem[];
  addToCart: (product: Product, quantity?: number) => void;
  removeFromCart: (productId: string) => void;
  updateQuantity: (productId: string, delta: number) => void;
  clearCart: () => void;
  cartSubtotal: number;
  cartCount: number;
  freeShippingThreshold: number;
  freeShippingProgress: number;
  isFreeShipping: boolean;
  selectedSample: string | null;
  setSelectedSample: (sampleId: string | null) => void;
  isGiftWrap: boolean;
  setIsGiftWrap: (val: boolean) => void;
  giftNote: string;
  setGiftNote: (val: string) => void;

  // Cart Drawer
  isCartOpen: boolean;
  setIsCartOpen: (open: boolean) => void;

  // Wishlist
  wishlist: string[];
  toggleWishlist: (productId: string) => void;
  isInWishlist: (productId: string) => boolean;

  // Compare
  compareList: string[];
  addToCompare: (productId: string) => boolean;
  removeFromCompare: (productId: string) => void;
  clearCompare: () => void;

  // Search Modal
  isSearchOpen: boolean;
  setIsSearchOpen: (open: boolean) => void;

  // Quick View Modal
  quickViewProduct: Product | null;
  setQuickViewProduct: (product: Product | null) => void;
}

const StoreContext = createContext<StoreContextType | undefined>(undefined);

export function StoreProvider({ children }: { children: ReactNode }) {
  const [cart, setCart] = useState<CartItem[]>([]);
  const [wishlist, setWishlist] = useState<string[]>([]);
  const [compareList, setCompareList] = useState<string[]>([]);
  const [selectedSample, setSelectedSample] = useState<string | null>('sample-saffron');
  const [isGiftWrap, setIsGiftWrap] = useState<boolean>(false);
  const [giftNote, setGiftNote] = useState<string>('');

  const [isCartOpen, setIsCartOpen] = useState(false);
  const [isSearchOpen, setIsSearchOpen] = useState(false);
  const [quickViewProduct, setQuickViewProduct] = useState<Product | null>(null);

  // Initialize with some default items for preview / prototype vitality
  useEffect(() => {
    try {
      const savedCart = localStorage.getItem('elanor_cart');
      const savedWishlist = localStorage.getItem('elanor_wishlist');
      if (savedCart) {
        setCart(JSON.parse(savedCart));
      } else {
        // Starter luxury cart
        setCart([
          { product: PRODUCTS[0], quantity: 1 },
          { product: PRODUCTS[1], quantity: 1 }
        ]);
      }
      if (savedWishlist) {
        setWishlist(JSON.parse(savedWishlist));
      } else {
        setWishlist(['elanor-nocturne-elixir', 'elanor-regard-sculptant']);
      }
    } catch {
      // fallback
    }
  }, []);

  // Sync with localStorage
  useEffect(() => {
    try {
      localStorage.setItem('elanor_cart', JSON.stringify(cart));
    } catch {}
  }, [cart]);

  useEffect(() => {
    try {
      localStorage.setItem('elanor_wishlist', JSON.stringify(wishlist));
    } catch {}
  }, [wishlist]);

  const addToCart = (product: Product, quantity: number = 1) => {
    setCart((prev) => {
      const existing = prev.find((item) => item.product.id === product.id);
      if (existing) {
        return prev.map((item) =>
          item.product.id === product.id
            ? { ...item, quantity: item.quantity + quantity }
            : item
        );
      }
      return [...prev, { product, quantity }];
    });
    setIsCartOpen(true);
  };

  const removeFromCart = (productId: string) => {
    setCart((prev) => prev.filter((item) => item.product.id !== productId));
  };

  const updateQuantity = (productId: string, delta: number) => {
    setCart((prev) =>
      prev
        .map((item) => {
          if (item.product.id === productId) {
            const newQty = item.quantity + delta;
            return newQty > 0 ? { ...item, quantity: newQty } : null;
          }
          return item;
        })
        .filter(Boolean) as CartItem[]
    );
  };

  const clearCart = () => setCart([]);

  const cartSubtotal = cart.reduce(
    (total, item) => total + item.product.price * item.quantity,
    0
  );

  const cartCount = cart.reduce((count, item) => count + item.quantity, 0);

  const freeShippingThreshold = 200;
  const freeShippingProgress = Math.min(
    100,
    Math.round((cartSubtotal / freeShippingThreshold) * 100)
  );
  const isFreeShipping = cartSubtotal >= freeShippingThreshold;

  const toggleWishlist = (productId: string) => {
    setWishlist((prev) =>
      prev.includes(productId)
        ? prev.filter((id) => id !== productId)
        : [...prev, productId]
    );
  };

  const isInWishlist = (productId: string) => wishlist.includes(productId);

  const addToCompare = (productId: string) => {
    if (compareList.length >= 3) {
      alert('You can compare a maximum of 3 Élanor elixirs simultaneously.');
      return false;
    }
    if (!compareList.includes(productId)) {
      setCompareList((prev) => [...prev, productId]);
    }
    return true;
  };

  const removeFromCompare = (productId: string) => {
    setCompareList((prev) => prev.filter((id) => id !== productId));
  };

  const clearCompare = () => setCompareList([]);

  return (
    <StoreContext.Provider
      value={{
        cart,
        addToCart,
        removeFromCart,
        updateQuantity,
        clearCart,
        cartSubtotal,
        cartCount,
        freeShippingThreshold,
        freeShippingProgress,
        isFreeShipping,
        selectedSample,
        setSelectedSample,
        isGiftWrap,
        setIsGiftWrap,
        giftNote,
        setGiftNote,
        isCartOpen,
        setIsCartOpen,
        wishlist,
        toggleWishlist,
        isInWishlist,
        compareList,
        addToCompare,
        removeFromCompare,
        clearCompare,
        isSearchOpen,
        setIsSearchOpen,
        quickViewProduct,
        setQuickViewProduct,
      }}
    >
      {children}
    </StoreContext.Provider>
  );
}

export function useStore() {
  const context = useContext(StoreContext);
  if (!context) {
    throw new Error('useStore must be used within a StoreProvider');
  }
  return context;
}
