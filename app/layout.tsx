import type { Metadata, Viewport } from 'next';
import { Cormorant_Garamond, Manrope } from 'next/font/google';
import './globals.css';
import { StoreProvider } from '@/context/StoreContext';
import SmoothScroll from '@/components/SmoothScroll';
import Navbar from '@/components/Navbar';
import CartDrawer from '@/components/CartDrawer';
import SearchModal from '@/components/SearchModal';
import QuickViewModal from '@/components/QuickViewModal';
import AuthModal from '@/components/AuthModal';
import MobileBottomNav from '@/components/MobileBottomNav';
import Footer from '@/components/Footer';

const cormorant = Cormorant_Garamond({
  subsets: ['latin'],
  variable: '--font-cormorant',
  weight: ['400', '500', '600', '700'],
  display: 'swap',
});

const manrope = Manrope({
  subsets: ['latin'],
  variable: '--font-manrope',
  weight: ['300', '400', '500', '600', '700'],
  display: 'swap',
});

export const viewport: Viewport = {
  width: 'device-width',
  initialScale: 1,
  maximumScale: 5,
};

export const metadata: Metadata = {
  title: 'Élanor — Haute Botanique & Clinical Skincare',
  description: 'Pixel-perfect luxury skincare blending rare botanical extracts with clinical bio-ferments for luminous, resilient skin.',
  keywords: ['Élanor', 'luxury skincare', 'botanical elixir', 'ceramides', 'retinal', 'serum', 'Paris haute botanique'],
  openGraph: {
    title: 'Élanor — Haute Botanique & Clinical Skincare',
    description: 'Bespoke botanical rituals and clinical cellular longevity.',
    type: 'website',
  },
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en" className={`${cormorant.variable} ${manrope.variable}`}>
      <body className="min-h-screen flex flex-col bg-[#F8F3EB] text-[#1B1A17] antialiased pb-16 lg:pb-0">
        <StoreProvider>
          <SmoothScroll>
            <Navbar />
            <main className="flex-1">
              {children}
            </main>
            <Footer />
            <MobileBottomNav />
            <CartDrawer />
            <SearchModal />
            <QuickViewModal />
            <AuthModal />
          </SmoothScroll>
        </StoreProvider>
      </body>
    </html>
  );
}
