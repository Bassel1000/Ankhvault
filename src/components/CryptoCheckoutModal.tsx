import React, { useState } from 'react';
import { X, Copy, CheckCircle2, ShieldCheck, QrCode, Loader2, DollarSign } from 'lucide-react';
import { QRCodeSVG } from 'qrcode.react';

interface CryptoCheckoutModalProps {
  isOpen: boolean;
  onClose: () => void;
  totalAmount: number;
  devWalletSol?: string;
  devWalletEvm?: string;
  onPaymentSuccess: () => void;
  onVerifyTransaction: (txHash: string, token: string, amount: number) => Promise<{ success: boolean; message: string }>;
}

export const CryptoCheckoutModal: React.FC<CryptoCheckoutModalProps> = ({
  isOpen,
  onClose,
  totalAmount,
  devWalletSol,
  devWalletEvm,
  onPaymentSuccess,
  onVerifyTransaction,
}) => {
  const [selectedToken, setSelectedToken] = useState<'USDT' | 'USDC'>('USDT');
  const [selectedNetwork, setSelectedNetwork] = useState<'SOL' | 'EVM'>('SOL');
  const [showQrCode, setShowQrCode] = useState(true);
  const [txHash, setTxHash] = useState('');
  const [isVerifying, setIsVerifying] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');
  const [copiedText, setCopiedText] = useState<string | null>(null);

  // Platform Treasury Wallets (5% Platform Fee)
  const PLATFORM_SOL = '2JxDavSJ9de1twMxDxdqz1sV3vkpYVDpPbx8qTy6q2cS';
  const PLATFORM_EVM = '0x5efe4d14d5f406bb748fab14a3e2cda06e51a986';

  // Developer Wallets (95% Dev Payout)
  const DEV_SOL = devWalletSol || PLATFORM_SOL;
  const DEV_EVM = devWalletEvm || PLATFORM_EVM;

  // 95% / 5% Payout Calculations
  const platformFeeUsd = totalAmount * 0.05;
  const devPayoutUsd = totalAmount * 0.95;

  if (!isOpen) return null;

  const copyToClipboard = (text: string, label: string) => {
    navigator.clipboard.writeText(text);
    setCopiedText(label);
    setTimeout(() => setCopiedText(null), 2000);
  };

  const activePlatformAddress = selectedNetwork === 'SOL' ? PLATFORM_SOL : PLATFORM_EVM;
  const activeDevAddress = selectedNetwork === 'SOL' ? DEV_SOL : DEV_EVM;

  const handleVerify = async () => {
    if (!txHash.trim()) {
      setErrorMsg('Please enter your transaction signature / hash');
      return;
    }

    setIsVerifying(true);
    setErrorMsg('');

    try {
      const res = await onVerifyTransaction(txHash.trim(), selectedToken, totalAmount);
      if (res.success) {
        onPaymentSuccess();
        onClose();
      } else {
        setErrorMsg(res.message || 'Verification failed. Please check your transaction hash.');
      }
    } catch (err: any) {
      setErrorMsg(err?.toString() || 'Error verifying x402 signature');
    } finally {
      setIsVerifying(false);
    }
  };

  return (
    <div style={{ position: 'fixed', inset: 0, backgroundColor: 'rgba(5, 7, 10, 0.88)', backdropFilter: 'blur(12px)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 100, padding: '20px' }}>
      <div className="glass-panel animate-fade-in" style={{ width: '580px', maxHeight: '90vh', overflowY: 'auto', padding: '32px', position: 'relative', border: '1px solid var(--accent-gold)' }}>
        {/* Close Button */}
        <button onClick={onClose} style={{ position: 'absolute', right: '20px', top: '20px', background: 'none', border: 'none', color: 'var(--text-muted)', cursor: 'pointer' }}>
          <X size={20} />
        </button>

        {/* Title */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '4px' }}>
          <ShieldCheck size={26} color="var(--accent-gold)" />
          <h2 style={{ fontFamily: 'var(--font-cinzel)', color: 'var(--accent-gold)', fontSize: '1.4rem', fontWeight: 700 }}>
            USD Stablecoin x402 Checkout
          </h2>
        </div>
        <p style={{ color: 'var(--text-muted)', fontSize: '0.82rem', marginBottom: '20px' }}>
          Instant 1:1 Settlement • 95% Developer Payout / 5% Platform Split
        </p>

        {/* Total Amount & Revenue Split Summary Banner */}
        <div className="glass-panel" style={{ padding: '16px 20px', backgroundColor: 'rgba(212, 175, 55, 0.08)', borderRadius: '10px', marginBottom: '20px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
            <div>
              <span style={{ color: 'var(--text-muted)', fontSize: '0.75rem', display: 'block' }}>Total Due</span>
              <span style={{ color: 'var(--accent-gold)', fontSize: '1.5rem', fontWeight: 800 }}>
                ${totalAmount.toFixed(2)} USD
              </span>
            </div>
            <span style={{ fontSize: '0.75rem', color: 'var(--accent-turquoise)', backgroundColor: 'rgba(23, 145, 158, 0.2)', padding: '4px 10px', borderRadius: '6px', border: '1px solid rgba(23, 145, 158, 0.3)' }}>
              Protocol x402 Enabled
            </span>
          </div>

          {/* 95% / 5% Revenue Breakdown Details */}
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px', paddingTop: '10px', borderTop: '1px solid var(--border-stroke)', fontSize: '0.78rem' }}>
            <div style={{ backgroundColor: 'rgba(16, 185, 129, 0.1)', padding: '8px 12px', borderRadius: '6px', border: '1px solid rgba(16, 185, 129, 0.3)' }}>
              <span style={{ color: 'var(--accent-emerald)', fontWeight: 700, display: 'block' }}>🎮 Dev Share (95%)</span>
              <span style={{ color: 'var(--text-primary)', fontWeight: 800, fontSize: '0.95rem' }}>${devPayoutUsd.toFixed(2)} {selectedToken}</span>
            </div>
            <div style={{ backgroundColor: 'rgba(212, 175, 55, 0.1)', padding: '8px 12px', borderRadius: '6px', border: '1px solid rgba(212, 175, 55, 0.3)' }}>
              <span style={{ color: 'var(--accent-gold)', fontWeight: 700, display: 'block' }}>🏛️ Platform Fee (5%)</span>
              <span style={{ color: 'var(--text-primary)', fontWeight: 800, fontSize: '0.95rem' }}>${platformFeeUsd.toFixed(2)} {selectedToken}</span>
            </div>
          </div>
        </div>

        {/* Network & Token Selectors */}
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px', marginBottom: '20px' }}>
          <div>
            <label style={{ display: 'block', color: 'var(--text-secondary)', fontSize: '0.78rem', fontWeight: 600, marginBottom: '6px' }}>
              Payment Network
            </label>
            <div style={{ display: 'flex', gap: '6px' }}>
              <button
                type="button"
                onClick={() => setSelectedNetwork('SOL')}
                style={{
                  flex: 1,
                  padding: '8px',
                  borderRadius: '6px',
                  border: selectedNetwork === 'SOL' ? '1px solid var(--accent-gold)' : '1px solid var(--border-stroke)',
                  backgroundColor: selectedNetwork === 'SOL' ? 'rgba(212, 175, 55, 0.15)' : 'transparent',
                  color: selectedNetwork === 'SOL' ? 'var(--accent-gold)' : 'var(--text-secondary)',
                  fontWeight: 700,
                  fontSize: '0.85rem',
                  cursor: 'pointer',
                }}
              >
                Solana (SPL)
              </button>
              <button
                type="button"
                onClick={() => setSelectedNetwork('EVM')}
                style={{
                  flex: 1,
                  padding: '8px',
                  borderRadius: '6px',
                  border: selectedNetwork === 'EVM' ? '1px solid var(--accent-gold)' : '1px solid var(--border-stroke)',
                  backgroundColor: selectedNetwork === 'EVM' ? 'rgba(212, 175, 55, 0.15)' : 'transparent',
                  color: selectedNetwork === 'EVM' ? 'var(--accent-gold)' : 'var(--text-secondary)',
                  fontWeight: 700,
                  fontSize: '0.85rem',
                  cursor: 'pointer',
                }}
              >
                Base (EVM)
              </button>
            </div>
          </div>

          <div>
            <label style={{ display: 'block', color: 'var(--text-secondary)', fontSize: '0.78rem', fontWeight: 600, marginBottom: '6px' }}>
              Payment Token
            </label>
            <div style={{ display: 'flex', gap: '6px' }}>
              {(['USDT', 'USDC'] as const).map((token) => (
                <button
                  key={token}
                  type="button"
                  onClick={() => setSelectedToken(token)}
                  style={{
                    flex: 1,
                    padding: '8px',
                    borderRadius: '6px',
                    border: selectedToken === token ? '1px solid var(--accent-turquoise)' : '1px solid var(--border-stroke)',
                    backgroundColor: selectedToken === token ? 'rgba(23, 145, 158, 0.2)' : 'transparent',
                    color: selectedToken === token ? 'var(--accent-turquoise)' : 'var(--text-secondary)',
                    fontWeight: 700,
                    fontSize: '0.85rem',
                    cursor: 'pointer',
                  }}
                >
                  {token}
                </button>
              ))}
            </div>
          </div>
        </div>

        {/* Display Addresses & QR Code Generator */}
        <div style={{ marginBottom: '20px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '10px' }}>
            <label style={{ color: 'var(--text-secondary)', fontSize: '0.82rem', fontWeight: 600 }}>
              Deposit Addresses & Mobile QR Codes ({selectedToken} on {selectedNetwork === 'SOL' ? 'Solana' : 'Base EVM'})
            </label>

            <button
              type="button"
              onClick={() => setShowQrCode(!showQrCode)}
              className="btn-outline"
              style={{ padding: '4px 10px', fontSize: '0.75rem', display: 'flex', alignItems: 'center', gap: '4px' }}
            >
              <QrCode size={14} />
              {showQrCode ? 'Hide QR Codes' : 'Show QR Codes'}
            </button>
          </div>

          {/* 1. Developer Payout Wallet (95%) */}
          <div className="glass-panel" style={{ padding: '14px', marginBottom: '12px', backgroundColor: 'rgba(16, 185, 129, 0.05)', border: '1px solid rgba(16, 185, 129, 0.25)' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '6px' }}>
              <span style={{ color: 'var(--accent-emerald)', fontSize: '0.78rem', fontWeight: 700 }}>
                1. Developer Payout Wallet (95% = ${devPayoutUsd.toFixed(2)} {selectedToken})
              </span>
              {copiedText === 'dev' && <span style={{ color: 'var(--accent-emerald)', fontSize: '0.75rem' }}>Copied!</span>}
            </div>

            <div style={{ display: 'flex', alignItems: 'center', gap: '14px' }}>
              {showQrCode && (
                <div style={{ backgroundColor: '#ffffff', padding: '6px', borderRadius: '8px', display: 'inline-block' }}>
                  <QRCodeSVG value={activeDevAddress} size={84} level="M" />
                </div>
              )}

              <div style={{ flexGrow: 1, minWidth: 0 }}>
                <code style={{ fontSize: '0.78rem', color: 'var(--text-primary)', wordBreak: 'break-all', display: 'block', marginBottom: '8px' }}>
                  {activeDevAddress}
                </code>
                <button onClick={() => copyToClipboard(activeDevAddress, 'dev')} className="btn-outline" style={{ padding: '4px 10px', fontSize: '0.75rem' }}>
                  {copiedText === 'dev' ? <CheckCircle2 size={14} color="var(--accent-emerald)" /> : <Copy size={14} />}
                  Copy Dev Address
                </button>
              </div>
            </div>
          </div>

          {/* 2. Platform Treasury Fee Wallet (5%) */}
          <div className="glass-panel" style={{ padding: '14px', backgroundColor: 'rgba(212, 175, 55, 0.05)', border: '1px solid rgba(212, 175, 55, 0.25)' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '6px' }}>
              <span style={{ color: 'var(--accent-gold)', fontSize: '0.78rem', fontWeight: 700 }}>
                2. Platform Treasury Fee (5% = ${platformFeeUsd.toFixed(2)} {selectedToken})
              </span>
              {copiedText === 'platform' && <span style={{ color: 'var(--accent-emerald)', fontSize: '0.75rem' }}>Copied!</span>}
            </div>

            <div style={{ display: 'flex', alignItems: 'center', gap: '14px' }}>
              {showQrCode && (
                <div style={{ backgroundColor: '#ffffff', padding: '6px', borderRadius: '8px', display: 'inline-block' }}>
                  <QRCodeSVG value={activePlatformAddress} size={84} level="M" />
                </div>
              )}

              <div style={{ flexGrow: 1, minWidth: 0 }}>
                <code style={{ fontSize: '0.78rem', color: 'var(--text-primary)', wordBreak: 'break-all', display: 'block', marginBottom: '8px' }}>
                  {activePlatformAddress}
                </code>
                <button onClick={() => copyToClipboard(activePlatformAddress, 'platform')} className="btn-outline" style={{ padding: '4px 10px', fontSize: '0.75rem' }}>
                  {copiedText === 'platform' ? <CheckCircle2 size={14} color="var(--accent-emerald)" /> : <Copy size={14} />}
                  Copy Platform Address
                </button>
              </div>
            </div>
          </div>
        </div>

        {/* Tx Hash Signature Input */}
        <div style={{ marginBottom: '20px' }}>
          <label style={{ display: 'block', color: 'var(--text-secondary)', fontSize: '0.78rem', fontWeight: 600, marginBottom: '6px' }}>
            Transaction Hash / Signature
          </label>
          <input
            type="text"
            placeholder="Paste your 0x... or Solana transaction signature"
            value={txHash}
            onChange={(e) => setTxHash(e.target.value)}
            style={{
              width: '100%',
              padding: '10px 12px',
              backgroundColor: 'rgba(11, 14, 20, 0.85)',
              border: '1px solid var(--border-stroke)',
              borderRadius: '8px',
              color: 'var(--text-primary)',
              fontSize: '0.85rem',
              outline: 'none',
            }}
          />
        </div>

        {errorMsg && (
          <p style={{ color: '#ef4444', fontSize: '0.82rem', marginBottom: '14px' }}>
            {errorMsg}
          </p>
        )}

        {/* Action Button */}
        <button
          className="btn-gold glow-pulse"
          onClick={handleVerify}
          disabled={isVerifying}
          style={{ width: '100%', justifyContent: 'center', padding: '12px', fontSize: '1rem' }}
        >
          {isVerifying ? (
            <>
              <Loader2 size={18} className="animate-spin" />
              Verifying x402 Signature...
            </>
          ) : (
            'Verify x402 On-Chain Settlement'
          )}
        </button>
      </div>
    </div>
  );
};
