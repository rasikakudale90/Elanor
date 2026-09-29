'use client';

import React from 'react';
import Link from 'next/link';
import Image from 'next/image';
import { useStore } from '@/context/StoreContext';
import { PRODUCTS } from '@/data/products';
import { SlidersHorizontal, Trash2, ShoppingBag, Plus, Star, CheckCircle2 } from 'lucide-react';

export default function ComparePage() {
  const { compareList, removeFromCompare, clearCompare, addToCart, addToCompare } = useStore();

  const comparedProducts = PRODUCTS.filter((p) => compareList.includes(p.id));
  const availableToAdd = PRODUCTS.filter((p) => !compareList.includes(p.id));

  return (
    <div className="bg-[#F8F3EB] min-h-screen py-12 sm:py-16">
      <div className="max-w-[1440px] mx-auto px-6 sm:px-10 space-y-12">
        {/* Header */}
        <div className="flex flex-col md:flex-row md:items-end justify-between border-b border-[#EADFCF] pb-8 gap-4">
          <div>
            <span className="text-xs uppercase tracking-widest text-[#7D9075] font-semibold flex items-center gap-2">
              <SlidersHorizontal className="w-3.5 h-3.5" />
              Side-by-Side Formula Analysis
            </span>
            <h1 className="font-serif-luxury text-4xl sm:text-5xl text-[#1B1A17] mt-1">
              Product Comparator Matrix
            </h1>
            <p className="text-xs sm:text-sm text-[#5E584F] mt-2 max-w-xl">
              Compare cellular active concentrations, sensory textures, skin compatibility, and clinical trial results across up to 3 Élanor formulations.
            </p>
          </div>

          {comparedProducts.length > 0 && (
            <button
              onClick={clearCompare}
              className="text-xs text-[#8E857A] hover:text-[#B84A4A] underline font-medium self-start md:self-end"
            >
              Clear Comparison
            </button>
          )}
        </div>

        {comparedProducts.length === 0 ? (
          <div className="text-center py-20 bg-[#FFFDF9] rounded-3xl border border-[#EFE3D3] p-10 space-y-6 max-w-xl mx-auto shadow-sm">
            <SlidersHorizontal className="w-12 h-12 text-[#C8A46A] mx-auto" />
            <h2 className="font-serif-luxury text-3xl text-[#1B1A17]">No formulations in comparison</h2>
            <p className="text-xs sm:text-sm text-[#5E584F]">
              Browse the shop and tap &ldquo;Compare&rdquo; on any product card, or choose from our featured recommendations below.
            </p>
            <div className="flex flex-wrap justify-center gap-2 pt-2">
              {PRODUCTS.slice(0, 3).map((p) => (
                <button
                  key={p.id}
                  onClick={() => addToCompare(p.id)}
                  className="px-4 py-2 rounded-full bg-[#F2EBE2] hover:bg-[#EFE3D3] text-xs font-medium text-[#1B1A17] flex items-center space-x-1.5"
                >
                  <Plus className="w-3.5 h-3.5" />
                  <span>+ {p.name}</span>
                </button>
              ))}
            </div>
          </div>
        ) : (
          <div className="bg-[#FFFDF9] rounded-3xl border border-[#EFE3D3] shadow-card overflow-x-auto">
            <table className="w-full text-left border-collapse min-w-[700px]">
              <thead>
                <tr className="border-b border-[#EADFCF]">
                  <th className="p-6 text-xs uppercase tracking-wider font-semibold text-[#8E857A] w-1/4 bg-[#F8F3EB]/60">
                    Formulation
                  </th>
                  {comparedProducts.map((p) => (
                    <th key={p.id} className="p-6 w-1/4 align-top">
                      <div className="space-y-3">
                        <div className="relative aspect-square w-full rounded-2xl overflow-hidden bg-[#F2EBE2] border border-[#EADFCF]">
                          <Image src={p.image} alt={p.name} fill className="object-cover" />
                          <button
                            onClick={() => removeFromCompare(p.id)}
                            className="absolute top-2 right-2 p-1.5 rounded-full bg-[#FFFDF9]/90 text-[#8E857A] hover:text-[#B84A4A] shadow-sm"
                            title="Remove"
                          >
                            <Trash2 className="w-3.5 h-3.5" />
                          </button>
                        </div>
                        <div>
                          <span className="text-[10px] uppercase font-semibold text-[#7D9075]">{p.category}</span>
                          <Link href={`/product/${p.id}`} className="hover:text-[#C8A46A]">
                            <h4 className="font-serif-luxury text-lg font-semibold text-[#1B1A17]">{p.name}</h4>
                          </Link>
                          <p className="font-serif-luxury text-xl font-bold text-[#1B1A17] mt-1">${p.price}</p>
                        </div>
                        <button
                          onClick={() => addToCart(p, 1)}
                          className="w-full py-2.5 bg-[#1B1A17] text-[#FFFDF9] rounded-full text-xs uppercase tracking-widest font-semibold hover:bg-[#322F2A] flex items-center justify-center space-x-1.5 shadow-sm"
                        >
                          <ShoppingBag className="w-3.5 h-3.5" />
                          <span>Add to Bag</span>
                        </button>
                      </div>
                    </th>
                  ))}
                  {comparedProducts.length < 3 && (
                    <th className="p-6 w-1/4 align-middle text-center bg-[#FDFBF8]/50 border-l border-[#EAE1D3]">
                      <p className="text-xs text-[#8E857A] mb-3">Add another product to compare:</p>
                      <select
                        onChange={(e) => {
                          if (e.target.value) addToCompare(e.target.value);
                        }}
                        defaultValue=""
                        className="w-full text-xs p-2.5 rounded-xl border border-[#DCCDBA] bg-[#FFFDF9] text-[#1B1A17]"
                      >
                        <option value="" disabled>Select formulation...</option>
                        {availableToAdd.map((p) => (
                          <option key={p.id} value={p.id}>{p.name} (${p.price})</option>
                        ))}
                      </select>
                    </th>
                  )}
                </tr>
              </thead>
              <tbody className="divide-y divide-[#EAE1D3] text-xs text-[#5E584F]">
                <tr>
                  <td className="p-6 font-semibold text-[#1B1A17] bg-[#F8F3EB]/40">Primary Concern</td>
                  {comparedProducts.map((p) => (
                    <td key={p.id} className="p-6 font-medium text-[#7D9075]">{p.concern}</td>
                  ))}
                  {comparedProducts.length < 3 && <td className="p-6 bg-[#FDFBF8]/50" />}
                </tr>
                <tr>
                  <td className="p-6 font-semibold text-[#1B1A17] bg-[#F8F3EB]/40">Sensory Texture</td>
                  {comparedProducts.map((p) => (
                    <td key={p.id} className="p-6">{p.texture}</td>
                  ))}
                  {comparedProducts.length < 3 && <td className="p-6 bg-[#FDFBF8]/50" />}
                </tr>
                <tr>
                  <td className="p-6 font-semibold text-[#1B1A17] bg-[#F8F3EB]/40">Key Active Bio-Molecules</td>
                  {comparedProducts.map((p) => (
                    <td key={p.id} className="p-6 space-y-1">
                      {p.keyActives.map((act) => (
                        <span key={act} className="inline-block px-2 py-0.5 rounded bg-[#F2EBE2] text-[#1B1A17] text-[10px] mr-1 mb-1 font-medium">
                          {act}
                        </span>
                      ))}
                    </td>
                  ))}
                  {comparedProducts.length < 3 && <td className="p-6 bg-[#FDFBF8]/50" />}
                </tr>
                <tr>
                  <td className="p-6 font-semibold text-[#1B1A17] bg-[#F8F3EB]/40">Clinical Measurement</td>
                  {comparedProducts.map((p) => (
                    <td key={p.id} className="p-6 font-medium text-[#1B1A17]">
                      {p.clinicalResults[0] ? (
                        <span><strong>{p.clinicalResults[0].metric}</strong> {p.clinicalResults[0].description}</span>
                      ) : '—'}
                    </td>
                  ))}
                  {comparedProducts.length < 3 && <td className="p-6 bg-[#FDFBF8]/50" />}
                </tr>
                <tr>
                  <td className="p-6 font-semibold text-[#1B1A17] bg-[#F8F3EB]/40">Volume & Vessel</td>
                  {comparedProducts.map((p) => (
                    <td key={p.id} className="p-6">{p.volume} • Miron Violet Glass</td>
                  ))}
                  {comparedProducts.length < 3 && <td className="p-6 bg-[#FDFBF8]/50" />}
                </tr>
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
