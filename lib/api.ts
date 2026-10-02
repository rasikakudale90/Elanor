/**
 * Élanor Haute Botanique API Client
 * Seamlessly connects Next.js frontend with Spring Boot 3.3 backend
 */

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api/v1';

export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T;
  timestamp: string;
}

export interface AuthTokens {
  accessToken: string;
  refreshToken?: string;
  tokenType: string;
  expiresIn: number;
  userId: string;
}

export interface CustomerProfileDto {
  id: string;
  firstName: string;
  lastName?: string;
  email: string;
  phone?: string;
}

export interface AddressDto {
  id: string;
  fullName: string;
  phone: string;
  addressLine1: string;
  addressLine2?: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
  isDefault: boolean;
}

export interface ProductDto {
  id: string;
  name: string;
  shortDescription?: string;
  description?: string;
  slug: string;
  basePrice: number;
  status: string;
  categoryName?: string;
  coverImageUrl?: string;
  variants?: ProductVariantDto[];
}

export interface ProductVariantDto {
  id: string;
  sku: string;
  name: string;
  price: number;
  compareAtPrice?: number;
  availableStock?: number;
  active: boolean;
}

export interface CartItemDto {
  id: string;
  variantId: string;
  productName: string;
  variantName: string;
  unitPrice: number;
  quantity: number;
  totalPrice: number;
}

export interface CartDto {
  id: string;
  subtotal: number;
  discount: number;
  shipping: number;
  total: number;
  couponCode?: string;
  items: CartItemDto[];
}

export interface ShippingQuoteDto {
  shippingCharge: number;
  freeShippingThreshold: number;
  eligibleForFreeShipping: boolean;
  amountNeededForFreeShipping: number;
  estimatedDeliveryMinDays: number;
  estimatedDeliveryMaxDays: number;
  estimatedDeliveryDescription: string;
}

export interface AiConsultationRequest {
  skinType: string;
  primaryConcerns: string[];
  climate?: string;
  ritualDepth?: string;
  preferredTexture?: string;
  ageGroup?: number;
}

export interface AiConsultationResponse {
  consultationId: string;
  vitalityScores: {
    hydration: number;
    barrierIntegrity: number;
    cellularRadiance: number;
    dermalReactivity: number;
  };
  morningRitual: Array<{
    step: number;
    ritualName: string;
    productName: string;
    volume: string;
    applicationMethod: string;
    formulationBenefit: string;
    variantId?: string;
    price: number;
  }>;
  eveningRitual: Array<{
    step: number;
    ritualName: string;
    productName: string;
    volume: string;
    applicationMethod: string;
    formulationBenefit: string;
    variantId?: string;
    price: number;
  }>;
  bundledDiscountPercentage: number;
  totalRitualPrice: number;
  bundledPrice: number;
}

// Token & Guest ID management
export const tokenStorage = {
  getAuthToken: (): string | null => {
    if (typeof window === 'undefined') return null;
    return localStorage.getItem('elanor_access_token');
  },
  setAuthToken: (token: string) => {
    if (typeof window !== 'undefined') localStorage.setItem('elanor_access_token', token);
  },
  clearAuthToken: () => {
    if (typeof window !== 'undefined') localStorage.removeItem('elanor_access_token');
  },
  getGuestToken: (): string => {
    if (typeof window === 'undefined') return 'guest-token-default';
    let token = localStorage.getItem('elanor_guest_token');
    if (!token) {
      token = 'guest_' + Math.random().toString(36).substring(2) + '_' + Date.now();
      localStorage.setItem('elanor_guest_token', token);
    }
    return token;
  }
};

async function request<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
  const token = tokenStorage.getAuthToken();
  const guestToken = tokenStorage.getGuestToken();

  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    'X-Guest-Token': guestToken,
    ...(options.headers as Record<string, string>),
  };

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const response = await fetch(`${API_BASE_URL}${endpoint}`, {
    ...options,
    headers,
  });

  const body = await response.json().catch(() => null);

  if (!response.ok) {
    const message = body?.message || `HTTP error! status: ${response.status}`;
    throw new Error(message);
  }

  return body?.data !== undefined ? body.data : body;
}

export const elanorApi = {
  // 1. Auth & Profiles
  auth: {
    register: (payload: { firstName: string; lastName?: string; email: string; password: string; phone?: string }) =>
      request<AuthTokens>('/auth/register', { method: 'POST', body: JSON.stringify(payload) }),
    login: (payload: { email: string; password: string }) =>
      request<AuthTokens>('/auth/login', { method: 'POST', body: JSON.stringify(payload) }),
    getProfile: () => request<CustomerProfileDto>('/customers/me'),
    getAddresses: () => request<AddressDto[]>('/customers/me/addresses'),
    addAddress: (payload: Omit<AddressDto, 'id'>) =>
      request<AddressDto>('/customers/me/addresses', { method: 'POST', body: JSON.stringify(payload) }),
  },

  // 2. Catalog & Search
  catalog: {
    getProducts: (page = 0, size = 20) => request<{ content: ProductDto[] }>(`/products?page=${page}&size=${size}`),
    getProductById: (id: string) => request<ProductDto>(`/products/${id}`),
    search: (query: string, categoryId?: string, minPrice?: number, maxPrice?: number) => {
      const params = new URLSearchParams({ q: query });
      if (categoryId) params.append('categoryId', categoryId);
      if (minPrice) params.append('minPrice', minPrice.toString());
      if (maxPrice) params.append('maxPrice', maxPrice.toString());
      return request<{ content: ProductDto[] }>(`/search?${params.toString()}`);
    }
  },

  // 3. Cart & Wishlist
  cart: {
    get: () => request<CartDto>('/cart'),
    addItem: (variantId: string, quantity = 1) =>
      request<CartDto>('/cart/items', { method: 'POST', body: JSON.stringify({ variantId, quantity }) }),
    updateItem: (variantId: string, quantity: number) =>
      request<CartDto>(`/cart/items/${variantId}`, { method: 'PATCH', body: JSON.stringify({ quantity }) }),
    removeItem: (variantId: string) =>
      request<CartDto>(`/cart/items/${variantId}`, { method: 'DELETE' }),
    applyCoupon: (couponCode: string) =>
      request<CartDto>('/cart/coupon', { method: 'POST', body: JSON.stringify({ couponCode }) }),
    removeCoupon: () => request<CartDto>('/cart/coupon', { method: 'DELETE' }),
  },

  wishlist: {
    get: () => request<{ items: Array<{ variantId: string }> }>('/wishlist'),
    add: (variantId: string) =>
      request<void>('/wishlist/items', { method: 'POST', body: JSON.stringify({ variantId }) }),
    remove: (variantId: string) =>
      request<void>(`/wishlist/items/${variantId}`, { method: 'DELETE' }),
  },

  // 4. Checkout & Orders
  checkout: {
    getQuote: (addressId?: string, postalCode?: string) =>
      request<ShippingQuoteDto>('/checkout/quote', { method: 'POST', body: JSON.stringify({ addressId, postalCode }) }),
    createOrder: (payload: { addressId: string; notes?: string; paymentMethod?: string }) =>
      request<{ order: { id: string; orderNumber: string; status: string } }>('/checkout/order', {
        method: 'POST',
        body: JSON.stringify(payload),
      }),
  },

  // 5. AI Concierge
  ai: {
    consult: (payload: AiConsultationRequest) =>
      request<AiConsultationResponse>('/ai/consult', { method: 'POST', body: JSON.stringify(payload) }),
    chat: (message: string, context?: Record<string, unknown>) =>
      request<{ reply: string; suggestedProducts: ProductDto[] }>('/ai/chat', {
        method: 'POST',
        body: JSON.stringify({ message, context }),
      }),
  },

  // 6. Tracking
  tracking: {
    getTimeline: (trackingNumber: string) =>
      request<{ trackingNumber: string; carrierName: string; status: string; events: Array<{ status: string; location: string; description: string; timestamp: string }> }>(
        `/shipments/track/${trackingNumber}`
      ),
  }
};
