'use client';

import React, { useState } from 'react';
import IntroScreen from './components/IntroScreen';
import ConsultationFlow from './components/ConsultationFlow';
import AnalysisScreen from './components/AnalysisScreen';
import ProfileAndRitualResult from './components/ProfileAndRitualResult';
import { ConsultationAnswers, ConciergeResult } from './types';
import { evaluateConsultation } from './services/conciergeService';

type ConciergeState = 'intro' | 'consultation' | 'analyzing' | 'results';

const INITIAL_ANSWERS: ConsultationAnswers = {
  skinType: 'Combination',
  concerns: ['Radiance', 'Barrier Restoration'],
  skinContext: 'Air-Conditioned / Indoor',
  ritualPace: 'Balanced',
  texturePreference: 'Lightweight',
};

export default function AiSkinConciergePage() {
  const [viewState, setViewState] = useState<ConciergeState>('intro');
  const [step, setStep] = useState<number>(1);
  const [answers, setAnswers] = useState<ConsultationAnswers>(INITIAL_ANSWERS);
  const [result, setResult] = useState<ConciergeResult | null>(null);

  const handleUpdateAnswers = (updated: Partial<ConsultationAnswers>) => {
    setAnswers((prev) => ({ ...prev, ...updated }));
  };

  const handleStartConsultation = () => {
    setStep(1);
    setViewState('consultation');
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleNextStep = () => {
    if (step < 4) {
      setStep((prev) => prev + 1);
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  };

  const handlePrevStep = () => {
    if (step > 1) {
      setStep((prev) => prev - 1);
      window.scrollTo({ top: 0, behavior: 'smooth' });
    } else {
      setViewState('intro');
    }
  };

  const handleSubmitConsultation = () => {
    setViewState('analyzing');
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleAnalysisComplete = () => {
    const calculatedResult = evaluateConsultation(answers);
    setResult(calculatedResult);
    setViewState('results');
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleRestart = () => {
    setAnswers(INITIAL_ANSWERS);
    setStep(1);
    setViewState('intro');
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  return (
    <main className="min-h-screen bg-[#F8F3EB] text-[#1B1A17] selection:bg-[#E4C894] selection:text-[#1B1A17]">
      {viewState === 'intro' && (
        <IntroScreen onStart={handleStartConsultation} />
      )}

      {viewState === 'consultation' && (
        <ConsultationFlow
          step={step}
          answers={answers}
          onUpdateAnswers={handleUpdateAnswers}
          onNext={handleNextStep}
          onPrev={handlePrevStep}
          onSubmit={handleSubmitConsultation}
        />
      )}

      {viewState === 'analyzing' && (
        <AnalysisScreen onComplete={handleAnalysisComplete} />
      )}

      {viewState === 'results' && result && (
        <ProfileAndRitualResult
          result={result}
          onRestart={handleRestart}
        />
      )}
    </main>
  );
}
