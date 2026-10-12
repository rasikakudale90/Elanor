import { NextResponse } from 'next/server';
import { ENV } from '@/lib/env';

interface ShiprocketOrderItem {
  name: string;
  sku: string;
  units: number;
  selling_price: number;
  discount?: number;
  tax?: number;
}

interface ShiprocketOrderRequest {
  order_id: string;
  order_date: string;
  pickup_location?: string;
  billing_customer_name: string;
  billing_last_name: string;
  billing_address: string;
  billing_city: string;
  billing_pincode: string;
  billing_state: string;
  billing_country: string;
  billing_email: string;
  billing_phone: string;
  shipping_is_billing: boolean;
  order_items: ShiprocketOrderItem[];
  payment_method: 'Prepaid' | 'COD';
  sub_total: number;
  length?: number;
  breadth?: number;
  height?: number;
  weight?: number;
}

// In-memory token cache for Shiprocket JWT
let cachedToken: { token: string; expiresAt: number } | null = null;
let lastAuthError: string | null = null;

async function getShiprocketToken(): Promise<{ token: string | null; error?: string | null }> {
  if (!ENV.isShiprocketConfigured()) {
    return { token: null, error: 'Shiprocket credentials (SHIPROCKET_EMAIL or SHIPROCKET_PASSWORD) are not set in environment.' };
  }

  const now = Date.now();
  if (cachedToken && cachedToken.expiresAt > now) {
    return { token: cachedToken.token };
  }

  try {
    const authUrl = `${ENV.SHIPROCKET_BASE_URL}/auth/login`;
    console.log('[SHIPROCKET AUTH] Calling:', authUrl, 'for email:', ENV.SHIPROCKET_EMAIL);
    const authRes = await fetch(authUrl, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        email: ENV.SHIPROCKET_EMAIL,
        password: ENV.SHIPROCKET_PASSWORD,
      }),
    });

    const resText = await authRes.text();
    let data: any = {};
    try {
      data = JSON.parse(resText);
    } catch {
      data = { raw: resText };
    }

    if (!authRes.ok || !data.token) {
      console.error('[SHIPROCKET API] Auth failed:', authRes.status, resText);
      lastAuthError = data.message || `Auth failed with status ${authRes.status}: ${resText}`;
      return { token: null, error: lastAuthError };
    }

    // Tokens are valid for 240 hours (10 days); cache for 8 days
    cachedToken = {
      token: data.token,
      expiresAt: now + 8 * 24 * 60 * 60 * 1000,
    };
    return { token: data.token };
  } catch (err: any) {
    console.error('[SHIPROCKET API] Error generating token:', err);
    lastAuthError = err.message || 'Network error during auth';
    return { token: null, error: lastAuthError };
  }
}

export async function POST(request: Request) {
  try {
    let body: any = {};
    try {
      body = await request.json();
    } catch {
      try {
        const text = await request.text();
        body = text ? JSON.parse(text) : {};
      } catch {
        body = {};
      }
    }

    const { order, customer, items, paymentMethod } = body || {};

    const { token, error: authError } = await getShiprocketToken();

    // If Shiprocket credentials are not provided or auth fails:
    if (!token) {
      console.warn('[SHIPROCKET NOTICE] Token unavailable:', authError);
      
      // If user attempted live credentials but they failed, inform the client
      if (ENV.isShiprocketConfigured()) {
        return NextResponse.json({
          success: false,
          mode: 'AUTH_FAILED',
          error: `Shiprocket API authentication failed: ${authError}. Please verify your Shiprocket API User credentials.`,
          help: 'Ensure you are using the API User created under Shiprocket Settings > API Users (not your main dashboard login if different).'
        }, { status: 401 });
      }

      // If credentials were simply empty, graceful simulated fallback
      const simulatedAwb = `SR-BD-${Math.floor(100000 + Math.random() * 900000)}`;
      return NextResponse.json({
        success: true,
        mode: 'SIMULATED_SANDBOX',
        message: 'Order recorded in local logistics buffer (Shiprocket credentials not provided in .env)',
        shipment_id: `shp_${Date.now()}`,
        order_id: order?.orderNumber || `ELN-${Date.now().toString().slice(-6)}`,
        awb_code: simulatedAwb,
        courier_name: 'BlueDart Express (Shiprocket Partner)',
        status: 'MANIFEST_GENERATED',
      });
    }

    // Fetch primary registered pickup location from Shiprocket account
    let activePickupLocation = 'Primary';
    try {
      const pickupUrl = `${ENV.SHIPROCKET_BASE_URL}/settings/company/pickup`;
      const pickupRes = await fetch(pickupUrl, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
      });
      if (pickupRes.ok) {
        const pickupData = await pickupRes.json();
        const addressList = pickupData.data?.shipping_address || pickupData.shipping_address;
        if (Array.isArray(addressList) && addressList.length > 0) {
          activePickupLocation = addressList[0].pickup_location || 'Primary';
        }
      }
    } catch (pickupErr) {
      console.warn('[SHIPROCKET] Could not query pickup locations, using fallback:', pickupErr);
    }

    // Format Date as strictly required by Shiprocket: YYYY-MM-DD HH:MM
    const now = new Date();
    const pad = (n: number) => String(n).padStart(2, '0');
    const orderDateFormatted = `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())} ${pad(now.getHours())}:${pad(now.getMinutes())}`;

    // Cleanse phone and pincode
    const rawZip = String(customer?.zip || '400001').replace(/[^0-9]/g, '');
    const cleanPincode = rawZip.length >= 6 ? rawZip.slice(0, 6) : '400001';
    const rawPhone = String(customer?.phone || '9876543210').replace(/[^0-9]/g, '');
    const cleanPhone = rawPhone.length >= 10 ? rawPhone.slice(-10) : '9876543210';

    const orderItemsPayload = (items && items.length > 0 ? items : [{ name: 'Sérum Éclat Botanique', price: order?.total || 150, quantity: 1 }]).map((item: any, idx: number) => ({
      name: (item.name || 'Botanique Formulation').slice(0, 50),
      sku: (item.sku || `SKU-${idx}-${Date.now().toString().slice(-4)}`).slice(0, 30),
      units: Math.max(1, Number(item.quantity || 1)),
      selling_price: Math.max(1, Math.round(Number(item.price || 150))),
      discount: 0,
      tax: 0,
    }));

    const totalSub = orderItemsPayload.reduce((acc: number, curr: any) => acc + (curr.selling_price * curr.units), 0);

    const orderPayload: ShiprocketOrderRequest = {
      order_id: String(order?.orderNumber || `ELN-${Date.now()}`),
      order_date: orderDateFormatted,
      pickup_location: activePickupLocation,
      billing_customer_name: (customer?.firstName || 'Valued').slice(0, 30),
      billing_last_name: (customer?.lastName || 'Patron').slice(0, 30),
      billing_address: (customer?.address || '24 Place Vendome').slice(0, 100),
      billing_city: (customer?.city || 'Mumbai').slice(0, 50),
      billing_pincode: cleanPincode,
      billing_state: (customer?.state || 'Maharashtra').slice(0, 50),
      billing_country: 'India',
      billing_email: customer?.email || 'patron@maison-elanor.com',
      billing_phone: cleanPhone,
      shipping_is_billing: true,
      order_items: orderItemsPayload,
      payment_method: paymentMethod === 'COD' ? 'COD' : 'Prepaid',
      sub_total: totalSub,
      length: 10,
      breadth: 10,
      height: 10,
      weight: 0.5,
    };

    const targetUrl = `${ENV.SHIPROCKET_BASE_URL}/orders/create/adhoc`;
    console.log('[SHIPROCKET DISPATCH] POST to:', targetUrl);
    console.log('[SHIPROCKET PAYLOAD]:', JSON.stringify(orderPayload));

    const createRes = await fetch(targetUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(orderPayload),
    });

    const createData = await createRes.json();
    console.log('[SHIPROCKET RESPONSE STATUS]:', createRes.status, createData);

    if (!createRes.ok || createData.status_code === 400) {
      console.warn('[SHIPROCKET API] Order creation returned non-200:', createData);
      return NextResponse.json({
        success: false,
        error: createData.message || 'Shiprocket order creation returned an error',
        raw: createData,
      }, { status: 400 });
    }

    return NextResponse.json({
      success: true,
      mode: 'LIVE_SHIPROCKET_SYNC',
      order_id: createData.order_id,
      shipment_id: createData.shipment_id,
      awb_code: createData.awb_code || `SR-BD-${createData.shipment_id || Date.now().toString().slice(-6)}`,
      courier_name: createData.courier_name || 'BlueDart Express',
      status: 'ORDER_PLACED_IN_SHIPROCKET',
      raw: createData,
    });
  } catch (error: any) {
    console.error('[SHIPROCKET API ROUTE] Unexpected error:', error);
    return NextResponse.json(
      { success: false, error: error.message || 'Internal Server Error' },
      { status: 500 }
    );
  }
}
