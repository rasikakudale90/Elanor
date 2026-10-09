/**
 * Centralized Type-Safe Environment Configuration
 * Reads all public and server-side environment variables with validation and defaults.
 */

export const ENV = {
  // Public Frontend Environment Variables
  RAZORPAY_KEY_ID: process.env.NEXT_PUBLIC_RAZORPAY_KEY_ID || 'rzp_test_51a0Hk6b62D7X9',
  API_BASE_URL: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api/v1',
  SITE_URL: process.env.NEXT_PUBLIC_SITE_URL || 'http://localhost:3000',

  // Server-Side Only Secrets (Never exposed to the browser)
  RAZORPAY_KEY_SECRET: process.env.RAZORPAY_KEY_SECRET || '',
  RAZORPAY_WEBHOOK_SECRET: process.env.RAZORPAY_WEBHOOK_SECRET || '',

  // Shiprocket Logistics Credentials
  SHIPROCKET_EMAIL: process.env.SHIPROCKET_EMAIL || '',
  SHIPROCKET_PASSWORD: process.env.SHIPROCKET_PASSWORD || '',
  SHIPROCKET_BASE_URL: process.env.SHIPROCKET_BASE_URL || 'https://apiv2.shiprocket.in/v2/console',
  SHIPROCKET_WEBHOOK_TOKEN: process.env.SHIPROCKET_WEBHOOK_TOKEN || 'sr_webhook_secret_elanor',

  // Helpers
  isShiprocketConfigured(): boolean {
    return Boolean(this.SHIPROCKET_EMAIL && this.SHIPROCKET_PASSWORD);
  },

  isRazorpaySecretConfigured(): boolean {
    return Boolean(this.RAZORPAY_KEY_SECRET);
  }
} as const;
