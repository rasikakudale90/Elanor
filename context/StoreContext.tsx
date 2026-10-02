'use client';

import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { Product, PRODUCTS } from '@/data/products';
export type { Product };

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

export interface CouponItem {
  code: string;
  type: 'PERCENTAGE' | 'FIXED';
  value: number;
  minOrder: number;
  description: string;
}

export const AVAILABLE_COUPONS: CouponItem[] = [
  {
    code: 'HAUTE20',
    type: 'PERCENTAGE',
    value: 20,
    minOrder: 100,
    description: '20% Off Maison Haute Formulations',
  },
  {
    code: 'CONCIERGE15',
    type: 'PERCENTAGE',
    value: 15,
    minOrder: 120,
    description: '15% Off AI Skin Concierge Routine',
  },
  {
    code: 'GOLD10',
    type: 'PERCENTAGE',
    value: 10,
    minOrder: 50,
    description: '10% Off Complimentary Golden Gift',
  },
  {
    code: 'WELCOME50',
    type: 'FIXED',
    value: 50,
    minOrder: 200,
    description: '$50 Off Orders Over $200',
  },
];

export interface CustomerUser {
  id: string;
  name: string;
  email: string;
  phone?: string;
  tier: 'Bespoke Member' | 'Gold Atelier VIP' | 'First Sanctuary Guest';
  token?: string;
}

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

  // Coupon Engine
  appliedCoupon: CouponItem | null;
  couponDiscount: number;
  applyCoupon: (code: string) => { success: boolean; message: string };
  removeCoupon: () => void;

  // Customer Auth
  customerUser: CustomerUser | null;
  isAuthOpen: boolean;
  setIsAuthOpen: (open: boolean) => void;
  loginCustomer: (user: CustomerUser) => void;
  logoutCustomer: () => void;

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

  // Dynamic Catalog Engine
  products: Product[];
  addProduct: (product: Product) => void;
  updateProduct: (product: Product) => void;
  deleteProduct: (productId: string) => void;
  getProductById: (idOrSlug: string) => Product | undefined;
  refreshProducts: () => Promise<void>;

  // Quick View Modal
  quickViewProduct: Product | null;
  setQuickViewProduct: (product: Product | null) => void;
}

const StoreContext = createContext<StoreContextType | undefined>(undefined);

export function StoreProvider({ children }: { children: ReactNode }) {
  const [products, setProducts] = useState<Product[]>(PRODUCTS);
  const [cart, setCart] = useState<CartItem[]>([]);
  const [wishlist, setWishlist] = useState<string[]>([]);
  const [compareList, setCompareList] = useState<string[]>([]);
  const [selectedSample, setSelectedSample] = useState<string | null>('sample-saffron');
  const [isGiftWrap, setIsGiftWrap] = useState<boolean>(false);
  const [giftNote, setGiftNote] = useState<string>('');
  const [appliedCoupon, setAppliedCoupon] = useState<CouponItem | null>(null);

  // Customer Auth
  const [customerUser, setCustomerUser] = useState<CustomerUser | null>(null);
  const [isAuthOpen, setIsAuthOpen] = useState(false);

  const [isCartOpen, setIsCartOpen] = useState(false);
  const [isSearchOpen, setIsSearchOpen] = useState(false);
  const [quickViewProduct, setQuickViewProduct] = useState<Product | null>(null);

  // Initialize with saved items, customer session, and dynamic products
  useEffect(() => {
    try {
      const savedProducts = localStorage.getItem('elanor_custom_products');
      const savedCart = localStorage.getItem('elanor_cart');
      const savedWishlist = localStorage.getItem('elanor_wishlist');
      const savedUser = localStorage.getItem('elanor_customer');
      const savedCoupon = localStorage.getItem('elanor_applied_coupon');

      if (savedProducts) {
        const parsedProds = JSON.parse(savedProducts);
        if (Array.isArray(parsedProds) && parsedProds.length > 0) {
          const seenIds = new Set<string>();
          const merged: Product[] = [];
          
          parsedProds.forEach((p) => {
            if (p && p.id && !seenIds.has(p.id)) {
              seenIds.add(p.id);
              merged.push(p);
            }
          });

          PRODUCTS.forEach((p) => {
            if (!seenIds.has(p.id)) {
              seenIds.add(p.id);
              merged.push(p);
            }
          });

          setProducts(merged);
        }
      }

      if (savedCart) {
        const parsed = JSON.parse(savedCart);
        if (Array.isArray(parsed)) {
          const valid = parsed.filter((item) => item && item.product && item.product.id && typeof item.product.price === 'number');
          setCart(valid.length > 0 ? valid : [
            { product: PRODUCTS[0], quantity: 1 },
            { product: PRODUCTS[1], quantity: 1 }
          ]);
        }
      } else {
        setCart([
          { product: PRODUCTS[0], quantity: 1 },
          { product: PRODUCTS[1], quantity: 1 }
        ]);
      }
      if (savedWishlist) {
        const parsed = JSON.parse(savedWishlist);
        if (Array.isArray(parsed)) setWishlist(parsed);
      } else {
        setWishlist(['elanor-nocturne-elixir', 'elanor-regard-sculptant']);
      }
      if (savedUser) {
        const parsed = JSON.parse(savedUser);
        if (parsed && typeof parsed === 'object') setCustomerUser(parsed);
      }
      if (savedCoupon) {
        const parsed = JSON.parse(savedCoupon);
        if (parsed && parsed.code) setAppliedCoupon(parsed);
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
    if (!product || !product.id) return;
    setCart((prev) => {
      const existing = prev.find((item) => item?.product?.id === product.id);
      if (existing) {
        return prev.map((item) =>
          item?.product?.id === product.id
            ? { ...item, quantity: item.quantity + quantity }
            : item
        );
      }
      return [...prev, { product, quantity }];
    });
    setIsCartOpen(true);
  };

  const removeFromCart = (productId: string) => {
    setCart((prev) => prev.filter((item) => item?.product?.id !== productId));
  };

  const updateQuantity = (productId: string, delta: number) => {
    setCart((prev) =>
      prev
        .map((item) => {
          if (item?.product?.id === productId) {
            const newQty = (item.quantity || 1) + delta;
            return newQty > 0 ? { ...item, quantity: newQty } : null;
          }
          return item;
        })
        .filter(Boolean) as CartItem[]
    );
  };

  const clearCart = () => setCart([]);

  const cartSubtotal = (cart || []).reduce(
    (total, item) => total + (item?.product?.price || 0) * (item?.quantity || 1),
    0
  );

  const cartCount = (cart || []).reduce((count, item) => count + (item?.quantity || 0), 0);

  const freeShippingThreshold = 200;
  const freeShippingProgress = Math.min(
    100,
    Math.round((cartSubtotal / freeShippingThreshold) * 100)
  );
  const isFreeShipping = cartSubtotal >= freeShippingThreshold;

  // Coupon calculations
  const couponDiscount = appliedCoupon
    ? appliedCoupon.type === 'PERCENTAGE'
      ? Math.round((cartSubtotal * appliedCoupon.value) / 100)
      : Math.min(cartSubtotal, appliedCoupon.value)
    : 0;

  const applyCoupon = (code: string) => {
    const formatted = code.trim().toUpperCase();
    const found = AVAILABLE_COUPONS.find((c) => c.code === formatted);

    if (!found) {
      return { success: false, message: `Invalid promotional code "${formatted}".` };
    }

    if (cartSubtotal < found.minOrder) {
      return {
        success: false,
        message: `Code "${formatted}" requires a minimum bag order of $${found.minOrder}.`
      };
    }

    setAppliedCoupon(found);
    try {
      localStorage.setItem('elanor_applied_coupon', JSON.stringify(found));
    } catch {}
    return {
      success: true,
      message: `Maison Code "${formatted}" applied successfully!`
    };
  };

  const removeCoupon = () => {
    setAppliedCoupon(null);
    try {
      localStorage.removeItem('elanor_applied_coupon');
    } catch {}
  };

  const loginCustomer = (user: CustomerUser) => {
    setCustomerUser(user);
    try {
      localStorage.setItem('elanor_customer', JSON.stringify(user));
    } catch {}
  };

  const logoutCustomer = () => {
    setCustomerUser(null);
    try {
      localStorage.removeItem('elanor_customer');
      localStorage.removeItem('elanor_access_token');
    } catch {}
  };

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

  // Dynamic Products CRUD and Sync
  const addProduct = (newProduct: Product) => {
    setProducts((prev) => {
      const updated = [newProduct, ...prev.filter((p) => p.id !== newProduct.id)];
      try {
        localStorage.setItem('elanor_custom_products', JSON.stringify(updated));
        window.dispatchEvent(new CustomEvent('elanor_products_updated', { detail: updated }));
      } catch {}
      return updated;
    });
  };

  const updateProduct = (updatedProduct: Product) => {
    setProducts((prev) => {
      const updated = prev.map((p) => (p.id === updatedProduct.id ? updatedProduct : p));
      try {
        localStorage.setItem('elanor_custom_products', JSON.stringify(updated));
        window.dispatchEvent(new CustomEvent('elanor_products_updated', { detail: updated }));
      } catch {}
      return updated;
    });
  };

  const deleteProduct = (productId: string) => {
    setProducts((prev) => {
      const updated = prev.filter((p) => p.id !== productId);
      try {
        localStorage.setItem('elanor_custom_products', JSON.stringify(updated));
        window.dispatchEvent(new CustomEvent('elanor_products_updated', { detail: updated }));
      } catch {}
      return updated;
    });
  };

  const getProductById = (idOrSlug: string): Product | undefined => {
    if (!idOrSlug) return undefined;
    return products.find(
      (p) =>
        p.id.toLowerCase() === idOrSlug.toLowerCase() ||
        p.name.toLowerCase().replace(/[^a-z0-9]+/g, '-') === idOrSlug.toLowerCase()
    );
  };

  const refreshProducts = async () => {
    try {
      const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api/v1'}/products?size=50`);
      if (res.ok) {
        const json = await res.json();
        const content = json.data?.content || json.content || json.data;
        if (Array.isArray(content) && content.length > 0) {
          const mapped: Product[] = content.map((item: any, idx: number) => ({
            id: item.id || `elanor-prod-${idx}`,
            name: item.name,
            frenchSubtitle: item.shortDescription || 'Haute Botanique de Précision',
            category: (item.categoryName || 'Serums') as Product['category'],
            concern: 'Radiance',
            price: item.basePrice || (item.variants?.[0]?.price) || 150,
            rating: 4.95,
            reviewsCount: 42,
            tagline: item.shortDescription || item.name,
            description: item.description || item.shortDescription || '',
            volume: item.variants?.[0]?.name || '50 ml / 1.7 fl. oz.',
            texture: 'Sublime botanical emulsion',
            skinTypes: ['All Skin Types', 'Sensitive'],
            keyActives: ['Rare Botanical Extracts', 'Bio-Ferments'],
            benefits: ['Cellular revitalization', 'Deep dermal resilience'],
            usageRitual: 'Apply morning and evening onto cleansed skin.',
            clinicalResults: [{ metric: '96%', description: 'felt immediate hydration and glow' }],
            image: item.coverImageUrl || `/images/skin${(idx % 9) + 1}.png`,
            stock: item.variants?.[0]?.availableStock || 30
          }));
          setProducts(mapped);
          localStorage.setItem('elanor_custom_products', JSON.stringify(mapped));
        }
      }
    } catch {}
  };

  return (
    <StoreContext.Provider
      value={{
        products,
        addProduct,
        updateProduct,
        deleteProduct,
        getProductById,
        refreshProducts,
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
        appliedCoupon,
        couponDiscount,
        applyCoupon,
        removeCoupon,
        customerUser,
        isAuthOpen,
        setIsAuthOpen,
        loginCustomer,
        logoutCustomer,
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
