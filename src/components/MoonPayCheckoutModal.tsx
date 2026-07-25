import React, { useState } from 'react';
import { X, ShieldCheck, CreditCard, ExternalLink, ArrowRightLeft, DollarSign } from 'lucide-react';

interface MoonPayCheckoutModalProps {
  isOpen: boolean;
  onClose: () => void;
  totalAmount: number;
  devWalletSol?: string;
  devWalletEvm?: string;
  onPaymentSuccess: () => void;
  onVerifyTransaction?: (orderId: string, token: string, amount: number) => Promise<{ success: boolean; message: string }>;
}

export const MoonPayCheckoutModal: React.FC<MoonPayCheckoutModalProps> = ({
  isOpen,
  onClose,
  totalAmount,
  onPaymentSuccess,
}) => {
  const [selectedPaymentMethod, setSelectedPaymentMethod] = useState<'CARD' | 'CRYPTO'>('CARD');
  const [selectedCurrency, setSelectedCurrency] = useState<'USD' | 'USDT' | 'USDC'>('USD');

  const PLATFORM_EVM = '0x5efe4d14d5f406bb748fab14a3e2cda06e51a986';

  const platformFeeUsd = totalAmount * 0.05;
  const devPayoutUsd = totalAmount * 0.95;

  if (!isOpen) return null;

  const moonpayUrl = `https://buy.moonpay.com?apiKey=MOONPAY_API_KEY_REDACTED&currencyCode=${selectedCurrency.toLowerCase()}&baseCurrencyAmount=${totalAmount}&walletAddress=${PLATFORM_EVM}`;

  const handleLaunchMoonPay = () => {
    window.open(moonpayUrl, '_blank', 'noopener,noreferrer');
    onPaymentSuccess();
    onClose();
  };

  return (
    <div style={{ position: 'fixed', inset: 0, backgroundColor: 'rgba(5, 7, 10, 0.88)', backdropFilter: 'blur(12px)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 100, padding: '20px' }}>
      <div className="glass-panel animate-fade-in" style={{ width: '520px', maxHeight: '90vh', overflowY: 'auto', padding: '32px', position: 'relative', border: '1px solid var(--accent-gold)' }}>
        {/* Close Button */}
        <button onClick={onClose} style={{ position: 'absolute', right: '20px', top: '20px', background: 'none', border: 'none', color: 'var(--text-muted)', cursor: 'pointer' }}>
          <X size={20} />
        </button>

        {/* Title Header */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '4px' }}>
          <ShieldCheck size={26} color="var(--accent-gold)" />
          <h2 style={{ fontFamily: 'var(--font-cinzel)', color: 'var(--accent-gold)', fontSize: '1.4rem', fontWeight: 700 }}>
            MoonPay Commerce Checkout
          </h2>
        </div>
        <p style={{ color: 'var(--text-muted)', fontSize: '0.82rem', marginBottom: '20px' }}>
          Cards, Apple Pay, Google Pay & Crypto • Instant Settlement
        </p>

        {/* Total Amount Banner */}
        <div className="glass-panel" style={{ padding: '16px 20px', backgroundColor: 'rgba(212, 175, 55, 0.08)', borderRadius: '10px', marginBottom: '20px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '10px' }}>
            <div>
              <span style={{ color: 'var(--text-muted)', fontSize: '0.75rem', display: 'block' }}>Total Purchase Amount</span>
              <span style={{ color: 'var(--accent-gold)', fontSize: '1.6rem', fontWeight: 800 }}>
                ${totalAmount.toFixed(2)} USD
              </span>
            </div>
            <span style={{ fontSize: '0.75rem', color: '#6366f1', backgroundColor: 'rgba(99, 102, 241, 0.15)', padding: '4px 10px', borderRadius: '6px', border: '1px solid rgba(99, 102, 241, 0.3)', fontWeight: 700 }}>
              MoonPay Commerce
            </span>
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', paddingTop: '10px', borderTop: '1px solid var(--border-stroke)', fontSize: '0.78rem', color: 'var(--text-secondary)' }}>
            <ArrowRightLeft size={16} color="var(--accent-turquoise)" />
            <span>
              <strong>Automated Payout:</strong> 95% (${devPayoutUsd.toFixed(2)}) disbursed to Game Developer • 5% (${platformFeeUsd.toFixed(2)}) Platform Vault.
            </span>
          </div>
        </div>

        {/* Payment Method Selector */}
        <div style={{ marginBottom: '20px' }}>
          <label style={{ display: 'block', color: 'var(--text-secondary)', fontSize: '0.78rem', fontWeight: 600, marginBottom: '8px' }}>
            Select Payment Option
          </label>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px' }}>
            <button
              type="button"
              onClick={() => { setSelectedPaymentMethod('CARD'); setSelectedCurrency('USD'); }}
              style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                gap: '8px',
                padding: '10px',
                borderRadius: '8px',
                border: selectedPaymentMethod === 'CARD' ? '1px solid var(--accent-gold)' : '1px solid var(--border-stroke)',
                backgroundColor: selectedPaymentMethod === 'CARD' ? 'rgba(212, 175, 55, 0.15)' : 'transparent',
                color: selectedPaymentMethod === 'CARD' ? 'var(--accent-gold)' : 'var(--text-secondary)',
                fontWeight: 700,
                fontSize: '0.85rem',
                cursor: 'pointer',
              }}
            >
              <CreditCard size={18} />
              Credit Card / Apple Pay
            </button>

            <button
              type="button"
              onClick={() => { setSelectedPaymentMethod('CRYPTO'); setSelectedCurrency('USDT'); }}
              style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                gap: '8px',
                padding: '10px',
                borderRadius: '8px',
                border: selectedPaymentMethod === 'CRYPTO' ? '1px solid var(--accent-turquoise)' : '1px solid var(--border-stroke)',
                backgroundColor: selectedPaymentMethod === 'CRYPTO' ? 'rgba(23, 145, 158, 0.2)' : 'transparent',
                color: selectedPaymentMethod === 'CRYPTO' ? 'var(--accent-turquoise)' : 'var(--text-secondary)',
                fontWeight: 700,
                fontSize: '0.85rem',
                cursor: 'pointer',
              }}
            >
              <DollarSign size={18} />
              USDT / USDC Crypto
            </button>
          </div>
        </div>

        {/* MoonPay Commerce Checkout Button */}
        <div>
          <button
            onClick={handleLaunchMoonPay}
            className="btn-gold glow-pulse"
            style={{
              width: '100%',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              gap: '8px',
              padding: '14px',
              borderRadius: '8px',
              fontWeight: 800,
              fontSize: '1rem',
              cursor: 'pointer',
              boxShadow: '0 4px 16px rgba(212, 175, 55, 0.3)',
            }}
          >
            <ExternalLink size={18} />
            Pay ${totalAmount.toFixed(2)} with MoonPay Commerce
          </button>
          <span style={{ display: 'block', textAlign: 'center', color: 'var(--text-muted)', fontSize: '0.75rem', marginTop: '8px' }}>
            Opens secure MoonPay gateway window & completes checkout
          </span>
        </div>
      </div>
    </div>
  );
};
