/**
 * Élanor Haute Botanique API Client
 * Seamlessly connects Next.js frontend with Spring Boot 3.3 backend
 */

const getApiBaseUrl = (): string => {
  let url = process.env.NEXT_PUBLIC_API_URL;
  if (url) {
    url = url.trim().replace(/\/+$/, '');
    if (!url.endsWith('/api/v1')) {
      url = `${url}/api/v1`;
    }
    return url;
  }
  // In the browser, use relative path to leverage Next.js rewrite proxy (zero CORS preflight delays)
  if (typeof window !== 'undefined') {
    return '/api/v1';
  }
  // On server, call Render directly
  return 'https://elanor-uzia.onrender.com/api/v1';
};

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
}

export interface KnowledgeCitationDto {
  sourceId: string;
  title: string;
  urlOrReference: string;
  excerpt: string;
}

export interface ProductRecommendationDto {
  productId: string;
  variantId?: string;
  name: string;
  subtitle?: string;
  price: number;
  imageUrl?: string;
  clinicalBenefit?: string;
}

export interface AiChatMessageDto {
  role: 'user' | 'assistant' | 'system';
  content: string;
}

export interface AiChatRequest {
  message: string;
  history?: AiChatMessageDto[];
  focusedProductId?: string;
}

export interface AiChatResponse {
  reply: string;
  suggestedProducts?: ProductRecommendationDto[];
  followUpQuestions?: string[];
  citations?: KnowledgeCitationDto[];
  needsHumanSupport?: boolean;
  intent?: string;
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
  const baseUrl = getApiBaseUrl();

  const isAuthRoute = endpoint.includes('/auth/');

  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(options.headers as Record<string, string>),
  };

  // Only attach X-Guest-Token if unauthenticated and not calling auth endpoints
  if (!token && !isAuthRoute) {
    headers['X-Guest-Token'] = guestToken;
  }

  // Only attach Authorization header if not calling an auth endpoint (prevent stale token 403s)
  if (token && !isAuthRoute) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  // 30s timeout so requests never buffer infinitely
  const controller = new AbortController();
  const timeoutId = setTimeout(() => controller.abort(), 30000);

  try {
    const response = await fetch(`${baseUrl}${endpoint}`, {
      ...options,
      headers,
      signal: controller.signal,
    });

    const body = await response.json().catch(() => null);

    if (!response.ok) {
      let message = body?.error?.message || body?.message;
      const details = body?.error?.details || body?.details;
      if (details && typeof details === 'object') {
        const detailValues = Object.values(details).filter(Boolean);
        if (detailValues.length > 0) {
          message = detailValues.join(', ');
        }
      }
      throw new Error(message || `Request failed (${response.status})`);
    }

    return body?.data !== undefined ? body.data : body;
  } catch (err: any) {
    if (err.name === 'AbortError') {
      throw new Error('Connection timed out while contacting Atelier. Please try once more.');
    }
    // If relative path failed or network error occurred, fallback once directly to Render
    if (baseUrl.startsWith('/') && typeof window !== 'undefined') {
      try {
        const directRes = await fetch(`https://elanor-uzia.onrender.com/api/v1${endpoint}`, {
          ...options,
          headers,
        });
        const directBody = await directRes.json().catch(() => null);
        if (!directRes.ok) {
          let directMsg = directBody?.error?.message || directBody?.message;
          const directDetails = directBody?.error?.details || directBody?.details;
          if (directDetails && typeof directDetails === 'object') {
            const detailValues = Object.values(directDetails).filter(Boolean);
            if (detailValues.length > 0) directMsg = detailValues.join(', ');
          }
          throw new Error(directMsg || `Request failed (${directRes.status})`);
        }
        return directBody?.data !== undefined ? directBody.data : directBody;
      } catch (fallbackErr: any) {
        throw new Error(fallbackErr.message || err.message);
      }
    }
    throw err;
  } finally {
    clearTimeout(timeoutId);
  }
}

export const elanorApi = {
  // 1. Auth & Profiles
  auth: {
    register: (payload: { firstName: string; lastName?: string; email: string; password: string; phone?: string; marketingConsent?: boolean }) =>
      request<AuthTokens>('/auth/register', { method: 'POST', body: JSON.stringify(payload) }),
    login: (payload: { email: string; password: string }) =>
      request<AuthTokens>('/auth/login', { method: 'POST', body: JSON.stringify(payload) }),
    loginWithGoogle: (idToken: string) =>
      request<AuthTokens>('/auth/google', { method: 'POST', body: JSON.stringify({ idToken }) }),
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
    chat: (payload: string | AiChatRequest) => {
      const body = typeof payload === 'string' ? { message: payload } : payload;
      return request<AiChatResponse>('/ai/chat', {
        method: 'POST',
        body: JSON.stringify(body),
      });
    },
  },

  // 6. Tracking
  tracking: {
    getTimeline: (trackingNumber: string) =>
      request<{ trackingNumber: string; carrierName: string; status: string; events: Array<{ status: string; location: string; description: string; timestamp: string }> }>(
        `/shipments/track/${trackingNumber}`
      ),
  }
};
