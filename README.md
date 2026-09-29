# Élanor — Haute Botanique & Clinical Skincare

> **Pure Beauty. Naturally.**  
> Pixel-perfect luxury skincare e-commerce prototype blending rare wild-harvested botanicals with clinical cellular longevity.

---

## 🌟 Overview

**Élanor** is an ultra-premium, quiet-luxury skincare e-commerce web platform engineered with **Next.js 15 (App Router)**, **TypeScript**, **Tailwind CSS v4**, **GSAP 3**, and **Lenis**.

### Key Features
- **Editorial Brand Aesthetic**: Cream/beige quiet-luxury design tokens (`#F8F3EB`, `#C8A46A`, `#7D9075`), Cormorant Garamond serif headers, and Manrope sans typography.
- **GSAP & Lenis Motion**: Physics-based smooth inertia scrolling, hero animation reveals, floating bottle physics on marble pedestals, and micro-interactions.
- **Full 12-Screen Commerce Flow**:
  - **Home (`/`)**: Hero reveal, curated collections, concern spotlight, AI routine preview, clinical proof.
  - **Curated Shop (`/shop`)**: Multi-facet sidebar filters (category, concern, active ingredient, price/rating sort, mobile filter drawer).
  - **Product Details (`/product/[id]`)**: High-res zoom gallery, clinical trial metrics, application ritual tabs, full INCI breakdown, sticky mobile purchase bar.
  - **AI Routine Builder (`/routine-builder`)**: 4-step diagnostic quiz (skin phenotype, primary concern, climate exposure, ritual pace) with animated computation and 1-click bundle discount.
  - **Ingredient Explorer (`/ingredients`)**: Searchable botanical and bio-fermented active directory with origins and bio-synergies.
  - **Formula Comparator (`/compare`)**: Side-by-side comparison matrix for up to 3 formulations.
  - **Shop by Concern (`/concern/[slug]`)**: Dedicated concern protocols (Radiance, Anti-Aging, Barrier Repair, Hydration, Calming, Clarifying).
  - **Maison Philosophy (`/brands`)**: Paris Atelier manifesto, Grasse harvesting, and biophotonic Miron violet glass preservation.
  - **Sacred Wishlist (`/wishlist`)**: Saved items with 1-click "Move All to Sacred Bag".
  - **Slide-Over Cart Drawer (`CartDrawer.tsx`)**: $200 free shipping progress meter, deluxe discovery sample picker, gold rigid gift packaging toggle.
  - **Full Cart Page (`/cart`)**: Detailed item quantities, complimentary sample selector, personalized calligraphy gift note.
  - **3-Step Luxury Checkout (`/checkout`)**: Sanctuary address, packaging selection, simulated payment authorization, celebratory order confirmation screen.
- **Mobile-First Responsiveness**: 5-tab mobile bottom navigation (`MobileBottomNav.tsx`), 44px+ touch targets, and sticky mobile purchase bar.

---

## 🛠️ Tech Stack

- **Framework**: [Next.js 15 (App Router)](https://nextjs.org/)
- **Language**: [TypeScript](https://www.typescriptlang.org/)
- **Styling**: [Tailwind CSS v4](https://tailwindcss.com/) + Custom Glassmorphism & Tokens
- **Animation**: [GSAP 3](https://greensock.com/gsap/) & [ScrollTrigger](https://greensock.com/scrolltrigger/)
- **Smooth Scroll**: [Lenis (Studio Freight)](https://github.com/darkroomengineering/lenis)
- **Icons**: [Lucide React](https://lucide.dev/)

---

## 🚀 Getting Started

### Prerequisites
- Node.js 18+ or 20+
- npm / yarn / pnpm

### Installation

1. Clone the repository:
```bash
git clone https://github.com/rasikakudale90/Elanor.git
cd Elanor
```

2. Install dependencies:
```bash
npm install
```

3. Run the development server:
```bash
npm run dev
```

4. Open [http://localhost:3000](http://localhost:3000) in your browser.

---

## 📁 Project Structure

```
├── app/                  # Next.js App Router (Pages, Layout, Globals)
│   ├── layout.tsx        # Root layout with fonts, Lenis, and drawers
│   ├── page.tsx          # Home page with GSAP hero & brand visual
│   ├── shop/             # Curated shop with multi-facet filters
│   ├── product/[id]/     # Product details with rituals & clinical proof
│   ├── routine-builder/  # AI Routine Diagnostic Quiz
│   ├── ingredients/      # Phytochemical ingredient glossary
│   ├── compare/          # Side-by-side formula comparator
│   ├── concern/[slug]/   # Targeted concern landing pages
│   ├── brands/           # Maison Élanor story & atelier ethics
│   ├── wishlist/         # Sacred wishlist with 1-tap move to bag
│   ├── cart/             # Full cart with sample & gift selectors
│   └── checkout/         # 3-step luxury checkout & mock receipt
├── components/           # Reusable UI (Navbar, ProductCard, Drawers, Modals)
├── context/              # StoreContext (Cart, Wishlist, Compare, Samples)
├── data/                 # Typed product, ingredient, and concern datasets
├── public/images/        # High-resolution brand and editorial photography
├── progress.md           # Milestone & development progress tracking
└── technical-depth.md    # Production engineering and architecture roadmap
```

---

## 📜 License

Private Prototype for Maison Élanor.
