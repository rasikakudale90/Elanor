'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import { useStore, CustomerUser } from '@/context/StoreContext';
import {
  X,
  Sparkles,
  Lock,
  Mail,
  User,
  ShieldCheck,
  CheckCircle2,
  ArrowRight,
  LogOut,
  ShoppingBag,
  Heart,
  KeyRound
} from 'lucide-react';

export default function AuthModal() {
  const { isAuthOpen, setIsAuthOpen, customerUser, loginCustomer, logoutCustomer, wishlist, cartCount } = useStore();

  const [mode, setMode] = useState<'LOGIN' | 'REGISTER' | 'OTP'>('LOGIN');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [otpPhone, setOtpPhone] = useState('');
  const [otpCode, setOtpCode] = useState('');
  const [otpSent, setOtpSent] = useState(false);
  const [loading, setLoading] = useState(false);
  const [feedback, setFeedback] = useState<{ type: 'error' | 'success'; text: string } | null>(null);

  if (!isAuthOpen) return null;

  const handleDemoQuickLogin = (demoName: string, demoEmail: string, tier: CustomerUser['tier']) => {
    setLoading(true);
    setTimeout(() => {
      loginCustomer({
        id: `usr-${Date.now()}`,
        name: demoName,
        email: demoEmail,
        tier: tier,
        token: `mock-jwt-token-${Date.now()}`
      });
      setLoading(false);
      setFeedback({ type: 'success', text: `Welcome back to your Sanctuary, ${demoName}.` });
      setTimeout(() => {
        setIsAuthOpen(false);
        setFeedback(null);
      }, 1200);
    }, 600);
  };

  const handleEmailAuth = (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setFeedback(null);

    setTimeout(() => {
      if (mode === 'LOGIN') {
        if (!email || !password) {
          setFeedback({ type: 'error', text: 'Please provide both email and password.' });
          setLoading(false);
          return;
        }
        const namePart = email.split('@')[0].replace(/[._-]/g, ' ');
        const formattedName = namePart.charAt(0).toUpperCase() + namePart.slice(1);
        loginCustomer({
          id: `usr-${Date.now()}`,
          name: formattedName || 'Maison Patron',
          email: email,
          tier: 'Bespoke Member',
          token: `jwt-${Date.now()}`
        });
        setFeedback({ type: 'success', text: 'Authentication successful. Welcome to Maison Élanor.' });
        setTimeout(() => {
          setIsAuthOpen(false);
          setFeedback(null);
        }, 1000);
      } else {
        if (!email || !password || !firstName) {
          setFeedback({ type: 'error', text: 'Please fill in all mandatory fields.' });
          setLoading(false);
          return;
        }
        loginCustomer({
          id: `usr-${Date.now()}`,
          name: `${firstName} ${lastName}`.trim(),
          email: email,
          tier: 'First Sanctuary Guest',
          token: `jwt-${Date.now()}`
        });
        setFeedback({ type: 'success', text: 'Account registered. Your 10% welcome gift code is GOLD10!' });
        setTimeout(() => {
          setIsAuthOpen(false);
          setFeedback(null);
        }, 1200);
      }
      setLoading(false);
    }, 700);
  };

  const handlePhoneOtpSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!otpSent) {
      if (!otpPhone) {
        setFeedback({ type: 'error', text: 'Please enter a valid mobile number.' });
        return;
      }
      setOtpSent(true);
      setFeedback({ type: 'success', text: '6-digit SMS code dispatched to ' + otpPhone });
    } else {
      if (!otpCode || otpCode.length < 4) {
        setFeedback({ type: 'error', text: 'Please enter the 6-digit SMS code.' });
        return;
      }
      loginCustomer({
        id: `usr-${Date.now()}`,
        name: 'SMS Verified Patron',
        email: `${otpPhone}@sms-guest.elanor.com`,
        phone: otpPhone,
        tier: 'Bespoke Member',
        token: `jwt-${Date.now()}`
      });
      setFeedback({ type: 'success', text: 'Phone verified successfully.' });
      setTimeout(() => {
        setIsAuthOpen(false);
        setFeedback(null);
        setOtpSent(false);
      }, 1000);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-[#1B1A17]/60 backdrop-blur-md animate-in fade-in duration-300">
      <div
        className="relative w-full max-w-md bg-[#FFFDF9] rounded-3xl border border-[#EFE3D3] p-8 shadow-floating overflow-hidden animate-in zoom-in-95 duration-300"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Subtle Luxury Aura */}
        <div className="absolute top-0 right-0 w-48 h-48 bg-[#C8A46A]/10 rounded-full blur-2xl pointer-events-none" />

        {/* Close Button */}
        <button
          onClick={() => {
            setIsAuthOpen(false);
            setFeedback(null);
          }}
          className="absolute top-6 right-6 p-2 rounded-full text-[#8E857A] hover:text-[#1B1A17] hover:bg-[#F2EBE2] transition-colors cursor-pointer"
          aria-label="Close"
        >
          <X className="w-5 h-5" />
        </button>

        {customerUser ? (
          /* LOGGED IN PROFILE VIEW */
          <div className="space-y-6 text-center">
            <div className="w-16 h-16 rounded-full bg-[#1B1A17] text-[#C8A46A] border-2 border-[#C8A46A] mx-auto flex items-center justify-center text-xl font-serif-luxury font-bold shadow-md">
              {(customerUser.name || 'Patron').charAt(0)}
            </div>

            <div className="space-y-1">
              <span className="px-3 py-1 rounded-full text-[10px] uppercase tracking-widest font-bold bg-[#C8A46A]/20 text-[#8C6B34]">
                {customerUser.tier}
              </span>
              <h3 className="font-serif-luxury text-2xl font-semibold text-[#1B1A17] pt-1">
                {customerUser.name}
              </h3>
              <p className="text-xs text-[#8E857A] font-mono">{customerUser.email}</p>
            </div>

            <div className="grid grid-cols-2 gap-3 pt-2 text-left text-xs">
              <Link
                href="/wishlist"
                onClick={() => setIsAuthOpen(false)}
                className="p-3 rounded-2xl bg-[#F8F3EB] border border-[#EADFCF] flex items-center justify-between hover:bg-[#F2EBE2] transition-colors"
              >
                <div className="flex items-center space-x-2 text-[#5E584F]">
                  <Heart className="w-4 h-4 text-[#C8A46A]" />
                  <span>Wishlist</span>
                </div>
                <span className="font-bold text-[#1B1A17]">{wishlist.length}</span>
              </Link>

              <Link
                href="/cart"
                onClick={() => setIsAuthOpen(false)}
                className="p-3 rounded-2xl bg-[#F8F3EB] border border-[#EADFCF] flex items-center justify-between hover:bg-[#F2EBE2] transition-colors"
              >
                <div className="flex items-center space-x-2 text-[#5E584F]">
                  <ShoppingBag className="w-4 h-4 text-[#7D9075]" />
                  <span>Shopping Bag</span>
                </div>
                <span className="font-bold text-[#1B1A17]">{cartCount}</span>
              </Link>
            </div>

            <div className="p-4 rounded-2xl bg-[#262420] text-[#FFFDF9] text-left space-y-2">
              <div className="flex items-center space-x-2 text-[#C8A46A] text-xs font-semibold uppercase tracking-wider">
                <Sparkles className="w-3.5 h-3.5" />
                <span>Patron Privileges Active</span>
              </div>
              <p className="text-[11px] text-[#A59D90]">
                Enjoy complimentary gift box packaging, personal routine history, and confidential access to rare harvests.
              </p>
            </div>

            <div className="pt-2 flex flex-col space-y-2.5">
              <Link
                href="/admin"
                onClick={() => setIsAuthOpen(false)}
                className="w-full py-3 rounded-full bg-[#FAF7F2] border border-[#EADFCF] text-xs font-semibold text-[#1B1A17] hover:bg-[#EADFCF] transition-all"
              >
                Access Maison Admin Portal →
              </Link>
              <button
                onClick={logoutCustomer}
                className="w-full py-3 rounded-full bg-[#1B1A17] text-[#FFFDF9] text-xs uppercase tracking-widest font-semibold hover:bg-[#322F2A] transition-all flex items-center justify-center space-x-2 cursor-pointer"
              >
                <LogOut className="w-3.5 h-3.5" />
                <span>Sign Out of Sanctuary</span>
              </button>
            </div>
          </div>
        ) : (
          /* AUTHENTICATION FORM VIEW */
          <div className="space-y-6">
            <div className="text-center space-y-1">
              <span className="text-[10px] uppercase tracking-[0.25em] text-[#C8A46A] font-semibold">
                Maison Élanor
              </span>
              <h2 className="font-serif-luxury text-2xl sm:text-3xl text-[#1B1A17]">
                {mode === 'LOGIN' && 'Sign In to Sanctuary'}
                {mode === 'REGISTER' && 'Create Patron Account'}
                {mode === 'OTP' && 'Passwordless Mobile OTP'}
              </h2>
              <p className="text-xs text-[#8E857A]">
                Access sacred routines, private formulations & rapid checkout.
              </p>
            </div>

            {/* Mode Switcher Tabs */}
            <div className="flex rounded-full bg-[#F2EBE2] p-1 text-xs font-medium">
              <button
                onClick={() => {
                  setMode('LOGIN');
                  setFeedback(null);
                }}
                className={`flex-1 py-1.5 rounded-full transition-all cursor-pointer ${
                  mode === 'LOGIN' ? 'bg-[#1B1A17] text-[#FFFDF9] font-semibold shadow-sm' : 'text-[#5E584F]'
                }`}
              >
                Sign In
              </button>
              <button
                onClick={() => {
                  setMode('REGISTER');
                  setFeedback(null);
                }}
                className={`flex-1 py-1.5 rounded-full transition-all cursor-pointer ${
                  mode === 'REGISTER' ? 'bg-[#1B1A17] text-[#FFFDF9] font-semibold shadow-sm' : 'text-[#5E584F]'
                }`}
              >
                Register
              </button>
              <button
                onClick={() => {
                  setMode('OTP');
                  setFeedback(null);
                }}
                className={`flex-1 py-1.5 rounded-full transition-all cursor-pointer ${
                  mode === 'OTP' ? 'bg-[#1B1A17] text-[#FFFDF9] font-semibold shadow-sm' : 'text-[#5E584F]'
                }`}
              >
                Phone OTP
              </button>
            </div>

            {/* Feedback Alert */}
            {feedback && (
              <div
                className={`p-3 rounded-2xl text-xs flex items-center space-x-2 ${
                  feedback.type === 'error'
                    ? 'bg-[#FBEBE7] text-[#A8381D] border border-[#E58066]/40'
                    : 'bg-[#EEF2E8] text-[#4D6545] border border-[#7D9075]/40'
                }`}
              >
                <CheckCircle2 className="w-4 h-4 shrink-0" />
                <span>{feedback.text}</span>
              </div>
            )}

            {/* Form */}
            {mode === 'OTP' ? (
              <form onSubmit={handlePhoneOtpSubmit} className="space-y-4">
                <div>
                  <label className="block text-[11px] font-semibold text-[#5E584F] uppercase tracking-wider mb-1">
                    Mobile Phone Number
                  </label>
                  <input
                    type="tel"
                    placeholder="+91 98765 43210 or +1 555-0199"
                    value={otpPhone}
                    onChange={(e) => setOtpPhone(e.target.value)}
                    required
                    className="w-full text-xs p-3 rounded-xl border border-[#DCCDBA] bg-[#FDFBF8] focus:outline-none focus:ring-1 focus:ring-[#C8A46A]"
                  />
                </div>
                {otpSent && (
                  <div>
                    <label className="block text-[11px] font-semibold text-[#5E584F] uppercase tracking-wider mb-1">
                      Enter 6-Digit SMS Verification Code
                    </label>
                    <input
                      type="text"
                      maxLength={6}
                      placeholder="e.g. 849201"
                      value={otpCode}
                      onChange={(e) => setOtpCode(e.target.value)}
                      required
                      className="w-full text-xs p-3 rounded-xl border border-[#DCCDBA] bg-[#FDFBF8] font-mono tracking-widest text-center text-sm focus:outline-none focus:ring-1 focus:ring-[#C8A46A]"
                    />
                  </div>
                )}
                <button
                  type="submit"
                  disabled={loading}
                  className="w-full py-3.5 bg-[#1B1A17] text-[#FFFDF9] rounded-full text-xs uppercase tracking-widest font-semibold hover:bg-[#322F2A] transition-all flex items-center justify-center space-x-2 shadow-md cursor-pointer disabled:opacity-60"
                >
                  <span>{otpSent ? 'Verify Code & Sign In' : 'Send One-Time Passcode'}</span>
                  <ArrowRight className="w-3.5 h-3.5" />
                </button>
              </form>
            ) : (
              <form onSubmit={handleEmailAuth} className="space-y-4">
                {mode === 'REGISTER' && (
                  <div className="grid grid-cols-2 gap-3">
                    <div>
                      <label className="block text-[11px] font-semibold text-[#5E584F] uppercase tracking-wider mb-1">
                        First Name
                      </label>
                      <input
                        type="text"
                        placeholder="Claire"
                        value={firstName}
                        onChange={(e) => setFirstName(e.target.value)}
                        required
                        className="w-full text-xs p-3 rounded-xl border border-[#DCCDBA] bg-[#FDFBF8] focus:outline-none focus:ring-1 focus:ring-[#C8A46A]"
                      />
                    </div>
                    <div>
                      <label className="block text-[11px] font-semibold text-[#5E584F] uppercase tracking-wider mb-1">
                        Last Name
                      </label>
                      <input
                        type="text"
                        placeholder="Delacroix"
                        value={lastName}
                        onChange={(e) => setLastName(e.target.value)}
                        className="w-full text-xs p-3 rounded-xl border border-[#DCCDBA] bg-[#FDFBF8] focus:outline-none focus:ring-1 focus:ring-[#C8A46A]"
                      />
                    </div>
                  </div>
                )}

                <div>
                  <label className="block text-[11px] font-semibold text-[#5E584F] uppercase tracking-wider mb-1">
                    Email Address
                  </label>
                  <div className="relative">
                    <Mail className="w-4 h-4 text-[#8E857A] absolute left-3.5 top-1/2 -translate-y-1/2" />
                    <input
                      type="email"
                      placeholder="patron@maison-elanor.com"
                      value={email}
                      onChange={(e) => setEmail(e.target.value)}
                      required
                      className="w-full text-xs pl-10 pr-4 py-3 rounded-xl border border-[#DCCDBA] bg-[#FDFBF8] focus:outline-none focus:ring-1 focus:ring-[#C8A46A]"
                    />
                  </div>
                </div>

                <div>
                  <label className="block text-[11px] font-semibold text-[#5E584F] uppercase tracking-wider mb-1">
                    Password
                  </label>
                  <div className="relative">
                    <Lock className="w-4 h-4 text-[#8E857A] absolute left-3.5 top-1/2 -translate-y-1/2" />
                    <input
                      type="password"
                      placeholder="••••••••"
                      value={password}
                      onChange={(e) => setPassword(e.target.value)}
                      required
                      className="w-full text-xs pl-10 pr-4 py-3 rounded-xl border border-[#DCCDBA] bg-[#FDFBF8] focus:outline-none focus:ring-1 focus:ring-[#C8A46A]"
                    />
                  </div>
                </div>

                <button
                  type="submit"
                  disabled={loading}
                  className="w-full py-3.5 bg-[#1B1A17] text-[#FFFDF9] rounded-full text-xs uppercase tracking-widest font-semibold hover:bg-[#322F2A] transition-all flex items-center justify-center space-x-2 shadow-md cursor-pointer disabled:opacity-60"
                >
                  {loading ? (
                    <span>Consulting Atelier...</span>
                  ) : (
                    <>
                      <span>{mode === 'LOGIN' ? 'Sign In' : 'Create Account'}</span>
                      <ArrowRight className="w-3.5 h-3.5" />
                    </>
                  )}
                </button>
              </form>
            )}

            {/* Fast Quick-Demo Login Pills */}
            <div className="pt-2 border-t border-[#EAE1D3] space-y-2.5">
              <p className="text-[10px] text-center uppercase tracking-wider text-[#8E857A]">
                Instant 1-Click Demo Login
              </p>
              <div className="flex flex-col sm:flex-row gap-2">
                <button
                  type="button"
                  onClick={() =>
                    handleDemoQuickLogin(
                      'Genevieve Moreau',
                      'genevieve@paris-botanique.fr',
                      'Gold Atelier VIP'
                    )
                  }
                  className="flex-1 py-2 px-3 rounded-xl border border-[#C8A46A]/60 bg-[#FBF7EE] text-[11px] font-semibold text-[#8C6B34] hover:bg-[#F5EEDD] transition-colors cursor-pointer"
                >
                  👑 VIP: Genevieve Moreau
                </button>
                <button
                  type="button"
                  onClick={() =>
                    handleDemoQuickLogin(
                      'Claire Delacroix',
                      'claire.delacroix@haute.com',
                      'Bespoke Member'
                    )
                  }
                  className="flex-1 py-2 px-3 rounded-xl border border-[#DCCDBA] bg-[#FAF7F2] text-[11px] font-semibold text-[#5E584F] hover:bg-[#EADFCF] transition-colors cursor-pointer"
                >
                  ✨ Member: Claire Delacroix
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
