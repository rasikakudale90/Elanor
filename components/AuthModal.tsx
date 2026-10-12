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

  const handleGoogleAuth = async (googleEmail?: string, googleName?: string) => {
    setLoading(true);
    setFeedback(null);
    try {
      const emailToUse = googleEmail || 'claire.delacroix@gmail.com';
      const nameToUse = googleName || (emailToUse.split('@')[0].charAt(0).toUpperCase() + emailToUse.split('@')[0].slice(1));

      // Synchronize with serverless OAuth endpoint in background
      fetch('/api/auth/google', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          idToken: `google-token-${Date.now()}`,
          profile: {
            email: emailToUse,
            name: nameToUse,
            picture: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80',
          },
        }),
      }).catch((e) => console.warn('[OAUTH NOTICE]:', e));

      loginCustomer({
        id: `usr-google-${Date.now()}`,
        name: nameToUse,
        email: emailToUse,
        tier: 'Bespoke Member',
        token: `jwt-google-${Date.now()}`,
      });

      setFeedback({ type: 'success', text: `Authenticated with Google as ${emailToUse}` });
      setTimeout(() => {
        setIsAuthOpen(false);
        setFeedback(null);
      }, 1000);
    } catch (err: any) {
      setFeedback({ type: 'error', text: err.message || 'Google OAuth failed' });
    } finally {
      setLoading(false);
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
                href="/orders"
                onClick={() => setIsAuthOpen(false)}
                className="w-full py-3 rounded-full bg-[#FAF7F2] border border-[#EADFCF] text-xs font-semibold text-[#1B1A17] hover:bg-[#EADFCF] transition-all text-center"
              >
                Track Orders & Shipments →
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

            {/* Google OAuth 2.0 1-Click Action */}
            <div className="space-y-3">
              <button
                type="button"
                onClick={() => handleGoogleAuth('claire.delacroix@gmail.com', 'Claire Delacroix')}
                disabled={loading}
                className="w-full py-3 px-4 rounded-full border border-[#DCCDBA] bg-[#FFFDF9] hover:bg-[#FAF6F0] hover:border-[#C8A46A] transition-all flex items-center justify-center space-x-3 text-xs font-semibold text-[#1B1A17] shadow-xs cursor-pointer group disabled:opacity-60"
              >
                <svg className="w-4 h-4 shrink-0 transition-transform group-hover:scale-110" viewBox="0 0 24 24">
                  <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" />
                  <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" />
                  <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.06H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.94l2.85-2.22.81-.63z" />
                  <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.06l3.66 2.84c.87-2.6 3.3-4.52 6.16-4.52z" />
                </svg>
                <span>Continue with Google</span>
              </button>

              <div className="relative flex py-1 items-center">
                <div className="flex-grow border-t border-[#EAE1D3]"></div>
                <span className="flex-shrink mx-3 text-[10px] uppercase tracking-wider text-[#8E857A]">
                  or with sanctuary credentials
                </span>
                <div className="flex-grow border-t border-[#EAE1D3]"></div>
              </div>
            </div>

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

            {/* Dedicated Guest Tracking Gateway */}
            <div className="p-3.5 rounded-2xl bg-[#F8F3EB] border border-[#E4C894]/60 flex items-center justify-between text-xs">
              <div className="space-y-0.5 pr-2">
                <p className="font-semibold text-[#1B1A17] flex items-center space-x-1.5">
                  <span className="w-1.5 h-1.5 rounded-full bg-[#C8A46A]" />
                  <span>Track Guest Order</span>
                </p>
                <p className="text-[11px] text-[#8E857A]">
                  No login required. View live BlueDart shipment telemetry.
                </p>
              </div>
              <Link
                href="/track"
                onClick={() => setIsAuthOpen(false)}
                className="px-3 py-1.5 bg-[#1B1A17] text-[#FFFDF9] text-[11px] font-semibold rounded-full hover:bg-[#322F2A] transition-colors shrink-0 flex items-center space-x-1"
              >
                <span>Track Now</span>
                <ArrowRight className="w-3 h-3" />
              </Link>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
