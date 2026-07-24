import React, { useState } from 'react';
import { X, Copy, CheckCircle2, ShieldCheck, ExternalLink, Loader2 } from 'lucide-react';

interface CryptoCheckoutModalProps {
  isOpen: boolean;
  onClose: () => void;
  totalAmount: number;
  onPaymentSuccess: () => void;
  onVerifyTransaction: (txHash: string, token: string, amount: number) => Promise<{ success: boolean; message: string }>;
}

export const CryptoCheckoutModal: React.FC<CryptoCheckoutModalProps> = ({
  isOpen,
  onClose,
  totalAmount,
  onPaymentSuccess,
  onVerifyTransaction,
}) => {
  const [selectedToken, setSelectedToken] = useState<'USDT' | 'USDC'>('USDT');
  const [txHash, setTxHash] = useState('');
  const [isVerifying, setIsVerifying] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');
  const [copiedSol, setCopiedSol] = useState(false);
  const [copiedEvm, setCopiedEvm] = useState(false);

  const TREASURY_SOL = '2JxDavSJ9de1twMxDxdqz1sV3vkpYVDpPbx8qTy6q2cS';
  const TREASURY_EVM = '0x5efe4d14d5f406bb748fab14a3e2cda06e51a986';

  if (!isOpen) return null;

  const copyToClipboard = (text: string, isSol: boolean) => {
    navigator.clipboard.writeText(text);
    if (isSol) {
      setCopiedSol(true);
      setTimeout(() => setCopiedSol(false), 2000);
    } else {
      setCopiedEvm(true);
      setTimeout(() => setCopiedEvm(false), 2000);
    }
  };

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
    <div style={{ position: 'fixed', inset: 0, backgroundColor: 'rgba(5, 7, 10, 0.85)', backdropFilter: 'blur(10px)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 100 }}>
      <div className="glass-panel animate-fade-in" style={{ width: '540px', padding: '32px', position: 'relative', border: '1px solid var(--accent-gold)' }}>
        {/* Close Button */}
        <button onClick={onClose} style={{ position: 'absolute', right: '20px', top: '20px', background: 'none', border: 'none', color: 'var(--text-muted)', cursor: 'pointer' }}>
          <X size={20} />
        </button>

        {/* Title */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '8px' }}>
          <ShieldCheck size={26} color="var(--accent-gold)" />
          <h2 style={{ fontFamily: 'var(--font-cinzel)', color: 'var(--accent-gold)', fontSize: '1.4rem', fontWeight: 700 }}>
            USD Stablecoin x402 Checkout
          </h2>
        </div>
        <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem', marginBottom: '24px' }}>
          Instant 1:1 USD Settlement Gateway by Duat Studio Platform
        </p>

        {/* Amount Banner */}
        <div className="glass-panel" style={{ padding: '16px 20px', backgroundColor: 'rgba(212, 175, 55, 0.08)', borderRadius: '10px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
          <div>
            <span style={{ color: 'var(--text-muted)', fontSize: '0.78rem', display: 'block' }}>Total Due</span>
            <span style={{ color: 'var(--accent-gold)', fontSize: '1.5rem', fontWeight: 700 }}>
              ${totalAmount.toFixed(2)} USD
            </span>
          </div>
          <span style={{ fontSize: '0.78rem', color: 'var(--accent-turquoise)', backgroundColor: 'rgba(23, 145, 158, 0.2)', padding: '4px 10px', borderRadius: '6px', border: '1px solid rgba(23, 145, 158, 0.3)' }}>
            Protocol x402 Enabled
          </span>
        </div>

        {/* Token Selection Tabs (USDT / USDC only) */}
        <div style={{ marginBottom: '20px' }}>
          <label style={{ display: 'block', color: 'var(--text-secondary)', fontSize: '0.82rem', fontWeight: 600, marginBottom: '8px' }}>
            Select Preferred Payment Token
          </label>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
            {(['USDT', 'USDC'] as const).map((token) => (
              <button
                key={token}
                onClick={() => setSelectedToken(token)}
                style={{
                  padding: '12px',
                  borderRadius: '8px',
                  border: selectedToken === token ? '1px solid var(--accent-gold)' : '1px solid var(--border-stroke)',
                  backgroundColor: selectedToken === token ? 'rgba(212, 175, 55, 0.15)' : 'rgba(11, 14, 20, 0.5)',
                  color: selectedToken === token ? 'var(--accent-gold)' : 'var(--text-secondary)',
                  fontWeight: 700,
                  fontSize: '0.95rem',
                  cursor: 'pointer',
                  transition: 'all 0.2s ease',
                }}
              >
                {token}
              </button>
            ))}
          </div>
        </div>

        {/* Official Treasury Deposit Addresses */}
        <div style={{ marginBottom: '24px' }}>
          <label style={{ display: 'block', color: 'var(--text-secondary)', fontSize: '0.82rem', fontWeight: 600, marginBottom: '8px' }}>
            Deposit Addresses ({selectedToken})
          </label>

          {/* Solana Address */}
          <div className="glass-panel" style={{ padding: '12px', marginBottom: '10px', backgroundColor: 'rgba(11, 14, 20, 0.6)' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.75rem', color: 'var(--text-muted)', marginBottom: '4px' }}>
              <span>Solana Network (SPL)</span>
              {copiedSol && <span style={{ color: 'var(--accent-emerald)' }}>Copied!</span>}
            </div>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: '8px' }}>
              <code style={{ fontSize: '0.8rem', color: 'var(--text-primary)', wordBreak: 'break-all' }}>{TREASURY_SOL}</code>
              <button onClick={() => copyToClipboard(TREASURY_SOL, true)} className="btn-outline" style={{ padding: '4px 8px' }}>
                {copiedSol ? <CheckCircle2 size={14} color="var(--accent-emerald)" /> : <Copy size={14} />}
              </button>
            </div>
          </div>

          {/* EVM Base Address */}
          <div className="glass-panel" style={{ padding: '12px', backgroundColor: 'rgba(11, 14, 20, 0.6)' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.75rem', color: 'var(--text-muted)', marginBottom: '4px' }}>
              <span>Base / EVM Network (ERC-20)</span>
              {copiedEvm && <span style={{ color: 'var(--accent-emerald)' }}>Copied!</span>}
            </div>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: '8px' }}>
              <code style={{ fontSize: '0.8rem', color: 'var(--text-primary)', wordBreak: 'break-all' }}>{TREASURY_EVM}</code>
              <button onClick={() => copyToClipboard(TREASURY_EVM, false)} className="btn-outline" style={{ padding: '4px 8px' }}>
                {copiedEvm ? <CheckCircle2 size={14} color="var(--accent-emerald)" /> : <Copy size={14} />}
              </button>
            </div>
          </div>
        </div>

        {/* Tx Hash Signature Input */}
        <div style={{ marginBottom: '24px' }}>
          <label style={{ display: 'block', color: 'var(--text-secondary)', fontSize: '0.82rem', fontWeight: 600, marginBottom: '8px' }}>
            Transaction Hash / Signature
          </label>
          <input
            type="text"
            placeholder="Paste your 0x... or Solana signature"
            value={txHash}
            onChange={(e) => setTxHash(e.target.value)}
            style={{
              width: '100%',
              padding: '12px 14px',
              backgroundColor: 'rgba(11, 14, 20, 0.85)',
              border: '1px solid var(--border-stroke)',
              borderRadius: '8px',
              color: 'var(--text-primary)',
              fontSize: '0.88rem',
              outline: 'none',
            }}
          />
        </div>

        {errorMsg && (
          <p style={{ color: '#ef4444', fontSize: '0.82rem', marginBottom: '16px' }}>
            {errorMsg}
          </p>
        )}

        {/* Action Button */}
        <button
          className="btn-gold glow-pulse"
          onClick={handleVerify}
          disabled={isVerifying}
          style={{ width: '100%', justifyContent: 'center', padding: '14px', fontSize: '1rem' }}
        >
          {isVerifying ? (
            <>
              <Loader2 size={18} className="animate-spin" />
              Verifying x402 Signature...
            </>
          ) : (
            'Verify x402 On-Chain Payment'
          )}
        </button>
      </div>
    </div>
  );
};
