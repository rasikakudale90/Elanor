# Lumière Design Tokens v1.0

> Single Source of Truth for Google Antigravity

This document contains the locked design tokens for your premium luxury skincare e-commerce project.

## Design Philosophy
- Quiet Luxury
- Editorial Beauty
- Natural Elegance
- Premium Skincare
- Dribbble-inspired luxury aesthetic (original implementation)
- Next.js + Tailwind CSS v4 + GSAP + Lenis

## Color Tokens

### Primary Palette
| Token | Hex | Usage |
|---|---|---|
| bg-primary | #F8F3EB | Main background |
| bg-secondary | #F2EBE2 | Alternate sections |
| bg-card | #FFFDF9 | Product cards |
| bg-glass | rgba(255,253,249,.72) | Glass surfaces |
| surface | #EFE3D3 | Elevated surfaces |

### Brand Colors
| Token | Hex |
|---|---|
| brand-gold | #C8A46A |
| brand-gold-light | #E4C894 |
| brand-sage | #7D9075 |
| brand-olive | #55624E |

### Text Colors
| Token | Hex |
|---|---|
| text-primary | #1B1A17 |
| text-secondary | #5E584F |
| text-muted | #8E857A |
| text-light | #FDFBF8 |

### Functional Colors
| Token | Hex |
|---|---|
| Success | #4D7A5C |
| Warning | #C99432 |
| Error | #B84A4A |
| Info | #6F8FAF |

## Gradient Tokens

### Hero
```css
linear-gradient(180deg,#FDFBF8 0%,#F7F0E7 48%,#EFE3D3 100%)
```

### Luxury Glow
```css
radial-gradient(circle,rgba(255,246,225,.85) 0%,rgba(255,246,225,0) 70%)
```

### Botanical
```css
linear-gradient(135deg,#F8F3EB 0%,#EEF2E8 100%)
```

## Typography
- Headings: Cormorant Garamond
- Body/UI: Manrope
- Numbers: Manrope

### Font Scale
| Token | Size | Weight |
|---|---|---|
| Hero XL | 88px | 500 |
| Hero | 72px | 500 |
| H1 | 56px | 500 |
| H2 | 42px | 500 |
| H3 | 30px | 500 |
| H4 | 22px | 500 |
| Body L | 18px | 400 |
| Body | 16px | 400 |
| Small | 14px | 400 |
| Caption | 12px | 400 |

Letter spacing:
- Hero: -0.04em
- Heading: -0.03em
- Body: 0
- Navigation: 0.04em

Line height:
- Hero: 0.95
- Heading: 1.05
- Body: 1.6

## Layout System
- 8px grid
- Max width: 1440px
- Content width: 1280px
- Desktop padding: 48px
- Tablet: 32px
- Mobile: 20px
- Section spacing: 120px

## Spacing Tokens
| Token | Value |
|---|---|
| xs | 8px |
| sm | 16px |
| md | 24px |
| lg | 32px |
| xl | 48px |
| 2xl | 64px |
| 3xl | 96px |

## Border Radius
| Token | Value |
|---|---|
| xs | 8px |
| sm | 14px |
| md | 20px |
| lg | 28px |
| xl | 36px |
| Pill | 999px |

## Shadow Tokens

Soft:
```css
0 10px 35px rgba(0,0,0,.06)
```

Card:
```css
0 14px 42px rgba(28,20,12,.08)
```

Floating Product:
```css
0 35px 90px rgba(41,27,12,.12)
```

Glass:
```css
0 8px 30px rgba(255,255,255,.4)
```

## Glassmorphism
```css
background: rgba(255,253,249,.72);
backdrop-filter: blur(24px);
border: 1px solid rgba(255,255,255,.45);
```

Used for:
- Search Bar
- Floating Navbar
- AI Cards

## Component Tokens

### Primary Button
- Background: #1B1A17
- Text: White
- Radius: 999px
- Height: 56px
- Horizontal Padding: 28px

Hover:
- Background: #2B2823
- Lift: translateY(-2px)

### Secondary Button
- Transparent
- Border: 1px solid #DCCDBA
- Hover: #F2EBE2

### Product Card
- Radius: 24px
- Background: #FFFDF9
- Padding: 20px
- Image Ratio: 4:5
- Card Shadow

Hover:
- Lift: -10px
- Scale: 1.02

### Category Card
- Radius: 28px
- Soft Beige Background
- Botanical Corner Decoration

### Search Bar
- Height: 52px
- Radius: 999px
- Glass Background

## Icon System
- Monoline
- Rounded Ends
- 1.75px Stroke
- Color: #2E2A24

Required Icons:
- Search
- User
- Wishlist
- Cart
- Leaf
- Bottle
- Hair
- Fragrance
- Brush

## Motion Tokens

### Lenis
- Duration: 1.2
- Smooth Wheel: Enabled
- Smooth Touch: Enabled

### GSAP Defaults
| Motion | Duration |
|---|---|
| Fade | 0.8s |
| Hero Reveal | 1.4s |
| Stagger | 0.08s |
| Hover | 0.35s |
| Floating Loop | 5–7s |

### ScrollTrigger
- Start: top 85%
- End: bottom 20%

## Photography Direction

### Product Photography
- Marble Pedestal
- Warm Natural Light
- Soft Reflections
- White Flowers
- Botanical Leaves
- Beige Backdrop
- Editorial Composition

### Background Direction
- Cream Fabric Folds
- Stone Textures
- Soft Arches
- Large Negative Space

### Never Use
- Neon Colors
- Plastic-looking Renders
- Harsh Shadows
- Oversaturated Lighting

## Page Hero Rules
| Page | Hero Style |
|---|---|
| Home | Floating Serum |
| Shop by Concern | Model Portrait |
| Product Listing | Editorial Banner |
| Product Details | Large Product Render |
| Ingredient Explorer | Cream Swirl |
| AI Routine Builder | Product Still Life |
| Brands | Premium Packaging |

## Micro-Interactions
| Element | Interaction |
|---|---|
| Button | Lift + Glow |
| Product Card | Lift + Shadow |
| Wishlist | Heart Fill |
| Search | Expand Smoothly |
| Navbar | Glass Blur |
| Hero Bottle | Floating Animation |
| Leaves | Slow Drift |

## Strict Implementation Rules
1. No inline styles.
2. Tailwind CSS v4 tokens only.
3. Cormorant Garamond only for headings.
4. Manrope for all UI/body text.
5. Follow 8px spacing grid.
6. Preserve generous whitespace.
7. Every page must feel editorial.
8. Products must sit on marble or stone.
9. Botanical accents must remain consistent.
10. GSAP + Lenis for all premium motion.
11. Never use harsh shadows.
12. Maintain identical visual language across every page.

## Responsive Breakpoints
- 390px
- 768px
- 1024px
- 1440px
- 1920px

## Locked Tech Stack
- Next.js (App Router)
- TypeScript
- Tailwind CSS v4
- GSAP
- GSAP ScrollTrigger
- Lenis

## Prototype Screens Covered
- Home
- Shop by Concern
- Product Listing
- Product Details
- AI Routine Builder
- Ingredient Explorer
- Product Comparison
- Trusted Brands
- Search Results
- Wishlist
- Cart
- Checkout
- Footer & Newsletter

## Final Design Promise

Every future screen must look like it belongs to the same premium luxury skincare brand with consistent colors, typography, spacing, photography, shadows, and motion.
