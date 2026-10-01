import Link from 'next/link';
import { ArrowLeft } from 'lucide-react';

export default function NotFound() {
  return (
    <div className="min-h-[70vh] flex flex-col items-center justify-center text-center px-6 py-20 bg-[#F8F3EB]">
      <span className="text-xs uppercase tracking-[0.2em] font-semibold text-[#7D9075] mb-2">
        Page Not Found • 404
      </span>
      <h1 className="font-serif-luxury text-4xl sm:text-5xl text-[#1B1A17] mb-4">
        The formulation you seek cannot be found.
      </h1>
      <p className="text-xs sm:text-sm text-[#5E584F] max-w-md mb-8">
        The requested pathway does not exist in the Élanor sanctuary. Return to our curated botanical shop.
      </p>
      <Link
        href="/shop"
        className="px-8 py-3.5 bg-[#1B1A17] text-[#FFFDF9] rounded-full text-xs uppercase tracking-widest font-semibold hover:bg-[#322F2A] transition-all flex items-center space-x-2"
      >
        <ArrowLeft className="w-4 h-4" />
        <span>Return to Curated Shop</span>
      </Link>
    </div>
  );
}
