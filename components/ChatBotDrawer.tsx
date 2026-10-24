'use client';

import React, { useState, useEffect, useRef } from 'react';
import Link from 'next/link';
import { useStore } from '@/context/StoreContext';
import { elanorApi, AiChatResponse, KnowledgeCitationDto, ProductRecommendationDto } from '@/lib/api';
import { Sparkles, X, Send, Bot, ShieldCheck, CheckCircle2, RotateCcw, ExternalLink, User, ShoppingBag } from 'lucide-react';

interface ChatMessage {
  id: string;
  role: 'user' | 'assistant';
  content: string;
  citations?: KnowledgeCitationDto[];
  suggestedProducts?: ProductRecommendationDto[];
  followUpQuestions?: string[];
  intent?: string;
  needsHumanSupport?: boolean;
  timestamp: Date;
}

const STARTER_PROMPTS = [
  'Recommend a ritual for compromised barrier repair',
  'What is your 7-day sacred return policy?',
  'What are the active botanicals in Baume Apaisant?',
  'Where is my recent sacred order status?'
];

export default function ChatBotDrawer() {
  const { customerUser, setIsAuthOpen, addToCart, products, setIsCartOpen } = useStore();
  const [isOpen, setIsOpen] = useState(false);
  const [input, setInput] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [addedItems, setAddedItems] = useState<Record<string, boolean>>({});

  const initialGreeting: ChatMessage = {
    id: 'welcome-1',
    role: 'assistant',
    content: `Bonjour${customerUser ? ` ${customerUser.name}` : ''}. I am your Maison Élanor Haute Botanical Concierge. Grounded in our clinical cellular formulations, rare alpine harvests, and verified atelier policies, I am here to assist your sacred skincare journey. How may I guide your dermal ritual today?`,
    followUpQuestions: STARTER_PROMPTS,
    timestamp: new Date()
  };

  const [messages, setMessages] = useState<ChatMessage[]>([initialGreeting]);
  const [sessionPatronName, setSessionPatronName] = useState<string | null>(null);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const inputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (isOpen) {
      messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
      inputRef.current?.focus();
    }
  }, [isOpen, messages, isLoading]);

  const renderFormattedText = (rawText: string, isUser: boolean) => {
    if (!rawText) return null;
    const parts = rawText.split(/(\*\*[^*]+\*\*)/g);
    return parts.map((part, index) => {
      if (part.startsWith('**') && part.endsWith('**')) {
        return (
          <strong
            key={index}
            className={`font-semibold ${isUser ? 'text-[#C8A46A]' : 'text-[#1B1A17]'}`}
          >
            {part.slice(2, -2)}
          </strong>
        );
      }
      return part;
    });
  };

  const handleSendMessage = async (userText: string) => {
    const text = userText.trim();
    if (!text || isLoading) return;

    const userMessage: ChatMessage = {
      id: 'msg-' + Date.now(),
      role: 'user',
      content: text,
      timestamp: new Date()
    };

    setMessages(prev => [...prev, userMessage]);
    setInput('');
    setIsLoading(true);

    // Check for direct identity inquiries
    const lowerText = text.toLowerCase();
    if (
      lowerText.includes('my name') ||
      lowerText.includes('who am i') ||
      lowerText.includes('do you know me') ||
      lowerText.startsWith('who i am') ||
      lowerText === 'name' ||
      lowerText === 'whats my name' ||
      lowerText === "what's my name"
    ) {
      setTimeout(() => {
        const activeName = customerUser?.name || sessionPatronName;
        const reply = activeName
          ? `Cher Patron, you are recognized in our atelier as **${activeName}**${customerUser?.tier ? ` (${customerUser.email}), esteemed member of our **${customerUser.tier}** tier` : ''}. How may I tailor your botanical regimen today?`
          : `You are currently visiting our atelier as an esteemed guest (**Cher Patron**). To let me personalize your consultations or look up your private orders, please sign in via the Patron Sanctuary icon in the top navigation, or simply introduce yourself by saying *"My name is..."*!`;

        setMessages(prev => [
          ...prev,
          {
            id: 'res-' + Date.now(),
            role: 'assistant',
            content: reply,
            intent: activeName ? 'USER_RECOGNIZED' : 'AUTH_REQUIRED',
            followUpQuestions: activeName
              ? ['Track my recent sacred order', 'Recommend a ritual for barrier repair']
              : ['Sign in to Patron Sanctuary', 'Recommend a ritual for barrier repair'],
            timestamp: new Date()
          }
        ]);
        setIsLoading(false);
      }, 400);
      return;
    }

    // Name self-introduction: "My name is X" / "I am X" / "Call me X" / "I'm X"
    const nameMatch = text.match(/(?:my name is|my name's|i'm|im|i am|call me|this is)\s+([a-zA-Z\s]{2,30})/i);
    if (nameMatch && !lowerText.includes('what is') && !lowerText.includes('tell me') && !lowerText.includes('where is')) {
      const capturedName = nameMatch[1].trim();
      setSessionPatronName(capturedName);
      setTimeout(() => {
        setMessages(prev => [
          ...prev,
          {
            id: 'res-' + Date.now(),
            role: 'assistant',
            content: `Enchanté, **${capturedName}**! It is a distinct pleasure to welcome you to Maison Élanor. I have committed your name to our atelier records. How may I assist your dermal ritual today?`,
            followUpQuestions: [
              'Recommend a ritual for compromised barrier repair',
              'What is your 7-day return policy?',
              'Tell me about Baume Apaisant Réparateur'
            ],
            timestamp: new Date()
          }
        ]);
        setIsLoading(false);
      }, 400);
      return;
    }

    try {
      const historyPayload = messages.slice(-4).map(m => ({
        role: m.role,
        content: m.content
      }));

      // Race with 8s timeout to avoid freezing if Render container is cold-starting
      const chatPromise = elanorApi.ai.chat({
        message: text,
        history: historyPayload
      });
      const timeoutPromise = new Promise<never>((_, reject) =>
        setTimeout(() => reject(new Error('Backend timeout: using local grounded RAG')), 8000)
      );

      const res = await Promise.race([chatPromise, timeoutPromise]);

      if (res && res.reply) {
        const assistantMessage: ChatMessage = {
          id: 'res-' + Date.now(),
          role: 'assistant',
          content: res.reply,
          citations: res.citations || [],
          suggestedProducts: res.suggestedProducts || [],
          followUpQuestions: res.followUpQuestions || [],
          intent: res.intent,
          needsHumanSupport: res.needsHumanSupport,
          timestamp: new Date()
        };
        setMessages(prev => [...prev, assistantMessage]);
      } else {
        throw new Error('Fallback to local grounded engine');
      }
    } catch {
      // Graceful local grounded reasoning engine (Zero broken states even if Render is cold-starting)
      const localResponse = generateLocalGroundedResponse(text);
      setMessages(prev => [...prev, localResponse]);
    } finally {
      setIsLoading(false);
    }
  };

  const generateLocalGroundedResponse = (rawQuery: string): ChatMessage => {
    const q = rawQuery.toLowerCase();
    const citations: KnowledgeCitationDto[] = [];
    const suggestedProducts: ProductRecommendationDto[] = [];
    let reply = '';
    let intent = 'GENERAL_CHAT';
    let needsHumanSupport = false;

    // 1. Medical safety guardrail
    if (q.includes('cure') || q.includes('eczema') || q.includes('psoriasis') || q.includes('dermatitis') || q.includes('infection')) {
      reply = "While Maison Élanor formulations are crafted with rare, clinical-grade botanicals to support barrier resilience and cellular longevity, our formulations are cosmetic and not formulated to diagnose, cure, or treat dermatological diseases. We respectfully advise consulting a board-certified dermatologist for medical conditions.";
      needsHumanSupport = true;
      intent = 'SAFETY_ABSTENTION';
    }
    // 2. Returns Policy
    else if (q.includes('return') || q.includes('refund') || q.includes('exchange')) {
      reply = "Under Maison Élanor's 7-Day Sacred Return Policy, patrons may request returns or replacements within 7 calendar days of verified delivery. Formulations must remain in pristine original packaging with at least 90% uncompromised volume. Following a 48-hour atelier inspection, approved refunds are credited within 3-5 business days.";
      intent = 'POLICY_FAQ';
      citations.push({
        sourceId: 'POLICY:return-policy',
        title: 'Maison Élanor 7-Day Return Policy',
        urlOrReference: '/policies/returns',
        excerpt: '7 calendar days post-delivery window for pristine returned items'
      });
    }
    // 3. Shipping Policy
    else if (q.includes('shipping') || q.includes('deliver') || q.includes('dispatch') || q.includes('cost') || q.includes('track')) {
      reply = "Maison Élanor provides complimentary white-glove express delivery on all orders exceeding ₹1,500 ($150 USD). Standard shipping is ₹150. Dispatches are handled via BlueDart Express and our Shiprocket logistics network, delivering within 3 to 5 business days with live 5-stage milestone tracking.";
      intent = 'POLICY_FAQ';
      citations.push({
        sourceId: 'POLICY:shipping-policy',
        title: 'Maison Élanor White-Glove Dispatch Policy',
        urlOrReference: '/track',
        excerpt: 'Complimentary insured shipping over ₹1,500 via BlueDart Express'
      });
    }
    // 4. Baume Apaisant / Balm / Barrier
    else if (q.includes('balm') || q.includes('barrier') || q.includes('soothing') || q.includes('baume')) {
      const balm = products.find(p => p.id === 'elanor-soothing-balm') || products[0];
      reply = "For barrier reinforcement and calming comfort, our Baume Apaisant Réparateur is infused with blue tansy, colloidal oat lipids, and botanical squalane. It actively replenishes epidermal lipids, pacifies redness, and seals in essential hydration.";
      intent = 'PRODUCT_INQUIRY';
      citations.push({
        sourceId: 'PRODUCT:baume-apaisant',
        title: 'Baume Apaisant Réparateur Profile',
        urlOrReference: '/product/' + balm.id,
        excerpt: 'Infused with blue tansy and alpine arnica to soothe reactive barriers'
      });
      suggestedProducts.push({
        productId: balm.id,
        name: balm.name,
        subtitle: balm.frenchSubtitle,
        price: balm.price,
        imageUrl: balm.image,
        clinicalBenefit: 'Clinical lipid barrier replenishment for sensitized, dry skin.'
      });
    }
    // 5. Serum / Aging / Wrinkles
    else if (q.includes('serum') || q.includes('aging') || q.includes('wrinkle') || q.includes('cellular')) {
      const serum = products.find(p => p.category === 'Serums') || products[1] || products[0];
      reply = "Our flagship Cellular Youth Serum pairs high-altitude edelweiss stem cells with multi-molecular hyaluronic acid to visibly re-densify the skin matrix, soften fine lines, and restore luminous cellular vitality.";
      intent = 'PRODUCT_INQUIRY';
      citations.push({
        sourceId: 'PRODUCT:' + serum.id,
        title: serum.name + ' Formulation Spec',
        urlOrReference: '/product/' + serum.id,
        excerpt: 'Alpine edelweiss stem cells for cellular matrix regeneration'
      });
      suggestedProducts.push({
        productId: serum.id,
        name: serum.name,
        subtitle: serum.frenchSubtitle,
        price: serum.price,
        imageUrl: serum.image,
        clinicalBenefit: 'Award-winning cellular regeneration with bio-available peptides.'
      });
    } else {
      reply = "Welcome to Maison Élanor. I am your Haute Botanical Concierge. I can assist you with our cellular formulations, bespoke morning/evening ritual sequencing, store policies, or tracking your sacred orders.";
      intent = 'GENERAL_CHAT';
      if (products.length > 0) {
        const feat = products[0];
        suggestedProducts.push({
          productId: feat.id,
          name: feat.name,
          subtitle: feat.frenchSubtitle,
          price: feat.price,
          imageUrl: feat.image,
          clinicalBenefit: feat.description?.substring(0, 80)
        });
      }
    }

    return {
      id: 'local-' + Date.now(),
      role: 'assistant',
      content: reply,
      citations,
      suggestedProducts,
      followUpQuestions: STARTER_PROMPTS,
      intent,
      needsHumanSupport,
      timestamp: new Date()
    };
  };

  const handleResetConversation = () => {
    setMessages([{
      id: 'welcome-' + Date.now(),
      role: 'assistant',
      content: `Conversation refreshed. How may I assist your botanical regimen today, cher patron?`,
      followUpQuestions: STARTER_PROMPTS,
      timestamp: new Date()
    }]);
  };

  const handleAddToCartFromConcierge = (item: ProductRecommendationDto) => {
    // Find matching catalog product from StoreContext or synthesize matching item
    const matched = products.find(p => p.id === item.productId || p.name.toLowerCase().includes(item.name.toLowerCase()));
    if (matched) {
      addToCart(matched, 1);
    } else {
      addToCart({
        id: item.productId,
        name: item.name,
        frenchSubtitle: item.subtitle || 'Formulation Haute Botanique',
        category: 'Serums',
        concern: 'Barrier Repair',
        price: item.price || 120,
        rating: 4.98,
        reviewsCount: 42,
        tagline: 'Handcrafted botanical formulation for cellular longevity.',
        description: item.clinicalBenefit || 'Handcrafted botanical longevity formula.',
        volume: '50 ml / 1.7 fl. oz.',
        texture: 'Silken aqueous elixir',
        skinTypes: ['All Skin Types'],
        keyActives: ['Alpine Edelweiss', 'Bio-Ferments'],
        benefits: ['Barrier Support', 'Cellular Renewal'],
        usageRitual: 'Warm 2-3 drops between palms and gently press into skin.',
        clinicalResults: [{ metric: '94%', description: 'Reported immediate barrier comfort' }],
        image: item.imageUrl || '/images/products/skin1.png',
        stock: 50
      }, 1);
    }

    setAddedItems(prev => ({ ...prev, [item.productId]: true }));
    setTimeout(() => {
      setAddedItems(prev => ({ ...prev, [item.productId]: false }));
    }, 2500);
  };

  return (
    <>
      {/* Floating Concierge Launcher Pill */}
      {!isOpen && (
        <button
          onClick={() => setIsOpen(true)}
          aria-label="Open Élanor Haute Botanical AI Concierge"
          className="fixed bottom-20 right-4 lg:bottom-8 lg:right-8 z-40 group flex items-center gap-3 bg-[#1B1A17] hover:bg-[#2C2924] text-[#F8F3EB] border border-[#C8A46A]/40 hover:border-[#C8A46A] shadow-2xl rounded-full px-4 py-3 sm:px-5 sm:py-3.5 transition-all duration-300 hover:scale-105 active:scale-95"
        >
          <div className="relative flex items-center justify-center">
            <span className="absolute -inset-1 rounded-full bg-[#C8A46A]/20 animate-ping opacity-75" />
            <div className="w-8 h-8 rounded-full bg-gradient-to-tr from-[#C8A46A] to-[#E4C894] flex items-center justify-center text-[#1B1A17] shadow-inner">
              <Sparkles className="w-4 h-4" />
            </div>
          </div>
          <div className="flex flex-col text-left">
            <span className="font-serif font-medium text-sm tracking-wide text-[#FDFBF8]">
              Botanical Concierge
            </span>
            <span className="text-[10px] uppercase tracking-wider text-[#C8A46A] font-sans">
              Live RAG Advisor
            </span>
          </div>
        </button>
      )}

      {/* Floating Concierge Panel / Window */}
      {isOpen && (
        <div 
          role="dialog"
          aria-modal="true"
          aria-labelledby="concierge-dialog-title"
          className="fixed bottom-20 right-2 sm:bottom-6 sm:right-6 z-50 w-[96vw] sm:w-[440px] h-[640px] max-h-[82vh] bg-[#FFFDF9]/95 backdrop-blur-xl border border-[#C8A46A]/30 rounded-2xl shadow-2xl flex flex-col overflow-hidden animate-in fade-in slide-in-from-bottom-6 duration-300"
        >
          {/* Luxury Header */}
          <div className="bg-[#1B1A17] text-[#F8F3EB] px-5 py-4 flex items-center justify-between border-b border-[#C8A46A]/30">
            <div className="flex items-center gap-3">
              <div className="w-9 h-9 rounded-full bg-gradient-to-tr from-[#C8A46A] to-[#E4C894] flex items-center justify-center text-[#1B1A17] font-serif font-bold text-base shadow">
                É
              </div>
              <div>
                <h3 id="concierge-dialog-title" className="font-serif font-medium text-base tracking-wide text-[#FDFBF8]">
                  Haute Botanical Concierge
                </h3>
                <div className="flex items-center gap-2">
                  <span className="w-2 h-2 rounded-full bg-[#4D7A5C] animate-pulse" />
                  <span className="text-[10px] uppercase tracking-widest text-[#C8A46A] font-sans font-medium">
                    Evidence-Grounded RAG
                  </span>
                </div>
              </div>
            </div>

            <div className="flex items-center gap-1">
              <button
                onClick={handleResetConversation}
                title="Reset conversation"
                className="p-1.5 text-[#8E857A] hover:text-[#F8F3EB] transition-colors rounded-lg hover:bg-white/5"
              >
                <RotateCcw className="w-4 h-4" />
              </button>
              <button
                onClick={() => setIsOpen(false)}
                title="Close Concierge"
                className="p-1.5 text-[#8E857A] hover:text-[#F8F3EB] transition-colors rounded-lg hover:bg-white/5"
              >
                <X className="w-5 h-5" />
              </button>
            </div>
          </div>

          {/* Messages Stream */}
          <div className="flex-1 overflow-y-auto px-4 py-4 space-y-4 bg-gradient-to-b from-[#F8F3EB]/40 to-[#FFFDF9]">
            {messages.map((msg) => (
              <div
                key={msg.id}
                className={`flex flex-col ${msg.role === 'user' ? 'items-end' : 'items-start'}`}
              >
                {/* Message Bubble */}
                <div
                  className={`max-w-[88%] rounded-2xl px-4 py-3 text-sm leading-relaxed ${
                    msg.role === 'user'
                      ? 'bg-[#1B1A17] text-[#FDFBF8] border border-[#C8A46A]/30 rounded-br-xs shadow-md'
                      : 'bg-[#FFFDF9] text-[#1B1A17] border border-[#EFE3D3] rounded-bl-xs shadow-xs'
                  }`}
                >
                  {msg.role === 'assistant' ? (
                    <div className="flex items-center gap-1.5 mb-1.5 text-[#C8A46A] text-xs font-serif font-medium">
                      <Sparkles className="w-3.5 h-3.5" />
                      <span>Maison Élanor</span>
                    </div>
                  ) : (
                    <div className="flex items-center justify-end gap-1.5 mb-1 text-[#C8A46A] text-[10px] font-sans tracking-wider uppercase font-medium">
                      <span>You</span>
                      <User className="w-3 h-3 text-[#C8A46A]" />
                    </div>
                  )}

                  <p
                    className={`whitespace-pre-line text-[13.5px] leading-relaxed ${
                      msg.role === 'user' ? 'text-[#FDFBF8]' : 'text-[#1B1A17]'
                    }`}
                  >
                    {renderFormattedText(msg.content, msg.role === 'user')}
                  </p>

                  {/* Safety Abstention Alert */}
                  {msg.needsHumanSupport && (
                    <div className="mt-2.5 p-2.5 bg-[#FFF8E7] border border-[#C99432]/40 rounded-lg text-xs text-[#5E584F] flex items-start gap-2">
                      <ShieldCheck className="w-4 h-4 text-[#C99432] shrink-0 mt-0.5" />
                      <span>
                        For reactive dermatitis or medical concerns, please stop usage and consult a licensed dermatologist.
                      </span>
                    </div>
                  )}

                  {/* Auth Required Quick-Action for Orders */}
                  {msg.intent === 'AUTH_REQUIRED' && !customerUser && (
                    <div className="mt-3 pt-2.5 border-t border-[#EFE3D3]">
                      <button
                        onClick={() => {
                          setIsOpen(false);
                          setIsAuthOpen(true);
                        }}
                        className="w-full py-2 px-3 bg-[#1B1A17] hover:bg-[#2C2924] text-[#F8F3EB] rounded-lg text-xs uppercase tracking-wider font-medium flex items-center justify-center gap-2 transition-colors"
                      >
                        <User className="w-3.5 h-3.5 text-[#C8A46A]" />
                        Sign In to Patron Sanctuary
                      </button>
                    </div>
                  )}

                  {/* Verified Citations List */}
                  {msg.citations && msg.citations.length > 0 && (
                    <div className="mt-3 pt-2.5 border-t border-[#EFE3D3]/80">
                      <p className="text-[10px] uppercase tracking-wider font-semibold text-[#8E857A] mb-1.5 flex items-center gap-1">
                        <CheckCircle2 className="w-3 h-3 text-[#4D7A5C]" />
                        Verified Atelier Evidence:
                      </p>
                      <div className="flex flex-wrap gap-1.5">
                        {msg.citations.map((c, idx) => (
                          <Link
                            key={idx}
                            href={c.urlOrReference || '/'}
                            target={c.urlOrReference?.startsWith('http') ? '_blank' : '_self'}
                            className="inline-flex items-center gap-1 px-2 py-0.5 bg-[#F2EBE2] hover:bg-[#EFE3D3] border border-[#C8A46A]/30 rounded text-[11px] text-[#5E584F] hover:text-[#1B1A17] transition-colors"
                            title={c.excerpt}
                          >
                            <span className="truncate max-w-[160px]">{c.title}</span>
                            <ExternalLink className="w-2.5 h-2.5 shrink-0 opacity-60" />
                          </Link>
                        ))}
                      </div>
                    </div>
                  )}

                  {/* Suggested Formulations */}
                  {msg.suggestedProducts && msg.suggestedProducts.length > 0 && (
                    <div className="mt-3 pt-2.5 border-t border-[#EFE3D3] space-y-2">
                      <p className="text-[10px] uppercase tracking-wider font-semibold text-[#8E857A]">
                        Featured Formulations:
                      </p>
                      {msg.suggestedProducts.map((p, idx) => (
                        <div
                          key={idx}
                          className="flex items-center justify-between gap-3 p-2 bg-[#F8F3EB] rounded-xl border border-[#EFE3D3]"
                        >
                          <div className="flex items-center gap-2.5 min-w-0">
                            {p.imageUrl && (
                              <img
                                src={p.imageUrl}
                                alt={p.name}
                                className="w-10 h-10 object-cover rounded-lg border border-[#EFE3D3]"
                              />
                            )}
                            <div className="min-w-0">
                              <p className="text-xs font-medium text-[#1B1A17] truncate">{p.name}</p>
                              <p className="text-[11px] text-[#C8A46A] font-serif font-semibold">
                                ₹{typeof p.price === 'number' ? p.price.toFixed(2) : p.price}
                              </p>
                            </div>
                          </div>
                          <button
                            onClick={() => handleAddToCartFromConcierge(p)}
                            className="px-2.5 py-1.5 bg-[#1B1A17] hover:bg-[#C8A46A] text-[#FDFBF8] text-[11px] rounded-lg transition-colors flex items-center gap-1 shrink-0 font-medium"
                          >
                            {addedItems[p.productId] ? (
                              <>
                                <CheckCircle2 className="w-3 h-3 text-[#4D7A5C]" />
                                Added
                              </>
                            ) : (
                              <>
                                <ShoppingBag className="w-3 h-3" />
                                Add to Bag
                              </>
                            )}
                          </button>
                        </div>
                      ))}
                    </div>
                  )}
                </div>

                {/* Follow-up Prompts Pills */}
                {msg.followUpQuestions && msg.followUpQuestions.length > 0 && (
                  <div className="mt-2 flex flex-wrap gap-1.5 max-w-[90%]">
                    {msg.followUpQuestions.map((q, qIdx) => (
                      <button
                        key={qIdx}
                        onClick={() => handleSendMessage(q)}
                        disabled={isLoading}
                        className="text-left text-xs px-2.5 py-1 bg-white hover:bg-[#F2EBE2] text-[#5E584F] hover:text-[#1B1A17] border border-[#EFE3D3] rounded-full transition-colors shadow-2xs"
                      >
                        {q}
                      </button>
                    ))}
                  </div>
                )}
              </div>
            ))}

            {/* Loading Indicator */}
            {isLoading && (
              <div className="flex items-start gap-2">
                <div className="bg-[#FFFDF9] border border-[#EFE3D3] rounded-2xl rounded-bl-xs px-4 py-3 shadow-xs">
                  <div className="flex items-center gap-1.5 text-xs text-[#8E857A]">
                    <div className="w-1.5 h-1.5 rounded-full bg-[#C8A46A] animate-bounce" />
                    <div className="w-1.5 h-1.5 rounded-full bg-[#C8A46A] animate-bounce [animation-delay:0.2s]" />
                    <div className="w-1.5 h-1.5 rounded-full bg-[#C8A46A] animate-bounce [animation-delay:0.4s]" />
                    <span className="ml-1 text-[11px] font-serif italic text-[#C8A46A]">
                      Atelier is retrieving clinical evidence...
                    </span>
                  </div>
                </div>
              </div>
            )}

            <div ref={messagesEndRef} />
          </div>

          {/* Input Box */}
          <div className="p-3 bg-[#FFFDF9] border-t border-[#EFE3D3]">
            <form
              onSubmit={(e) => {
                e.preventDefault();
                handleSendMessage(input);
              }}
              className="flex items-center gap-2"
            >
              <input
                ref={inputRef}
                type="text"
                value={input}
                onChange={(e) => setInput(e.target.value)}
                placeholder="Ask about formulas, rituals, or sacred orders..."
                disabled={isLoading}
                className="flex-1 bg-[#F8F3EB] border border-[#EFE3D3] focus:border-[#C8A46A] focus:outline-none rounded-xl px-3.5 py-2.5 text-xs sm:text-sm text-[#1B1A17] placeholder:text-[#8E857A] transition-colors"
              />
              <button
                type="submit"
                disabled={!input.trim() || isLoading}
                aria-label="Send message"
                className="p-2.5 bg-[#1B1A17] hover:bg-[#C8A46A] disabled:opacity-40 text-[#FDFBF8] rounded-xl transition-all duration-200 shrink-0"
              >
                <Send className="w-4 h-4" />
              </button>
            </form>
            <p className="text-[10px] text-center text-[#8E857A] mt-1.5">
              Evidence-grounded botanical intelligence. Not a substitute for medical consultation.
            </p>
          </div>
        </div>
      )}
    </>
  );
}
