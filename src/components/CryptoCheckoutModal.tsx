import React, { useState } from 'react';
import { X, Copy, CheckCircle2, ShieldCheck, QrCode, Loader2, ArrowRightLeft } from 'lucide-react';
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
  const [copied, setCopied] = useState(false);

  // Official Platform Treasury Vaults (Single Customer Deposit Address)
  const PLATFORM_SOL = '2JxDavSJ9de1twMxDxdqz1sV3vkpYVDpPbx8qTy6q2cS';
  const PLATFORM_EVM = '0x5efe4d14d5f406bb748fab14a3e2cda06e51a986';

  // Developer Target Wallets (Automated 95% Payout Destination)
  const DEV_SOL = devWalletSol || PLATFORM_SOL;
  const DEV_EVM = devWalletEvm || PLATFORM_EVM;

  // 95% / 5% Revenue Split Calculations
  const platformFeeUsd = totalAmount * 0.05;
  const devPayoutUsd = totalAmount * 0.95;

  if (!isOpen) return null;

  const activeAddress = selectedNetwork === 'SOL' ? PLATFORM_SOL : PLATFORM_EVM;
  const activeDevWallet = selectedNetwork === 'SOL' ? DEV_SOL : DEV_EVM;

  const copyToClipboard = (text: string) => {
    navigator.clipboard.writeText(text);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
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
    <div style={{ position: 'fixed', inset: 0, backgroundColor: 'rgba(5, 7, 10, 0.88)', backdropFilter: 'blur(12px)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 100, padding: '20px' }}>
      <div className="glass-panel animate-fade-in" style={{ width: '540px', maxHeight: '90vh', overflowY: 'auto', padding: '32px', position: 'relative', border: '1px solid var(--accent-gold)' }}>
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
          Single 1-Step Payment • Automated 95% Developer Split Settlement
        </p>

        {/* Total Amount Banner */}
        <div className="glass-panel" style={{ padding: '16px 20px', backgroundColor: 'rgba(212, 175, 55, 0.08)', borderRadius: '10px', marginBottom: '20px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '10px' }}>
            <div>
              <span style={{ color: 'var(--text-muted)', fontSize: '0.75rem', display: 'block' }}>Total Due (1 Single Payment)</span>
              <span style={{ color: 'var(--accent-gold)', fontSize: '1.6rem', fontWeight: 800 }}>
                ${totalAmount.toFixed(2)} {selectedToken}
              </span>
            </div>
            <span style={{ fontSize: '0.75rem', color: 'var(--accent-turquoise)', backgroundColor: 'rgba(23, 145, 158, 0.2)', padding: '4px 10px', borderRadius: '6px', border: '1px solid rgba(23, 145, 158, 0.3)' }}>
              Protocol x402 Enabled
            </span>
          </div>

          {/* Automated Revenue Split Status */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', paddingTop: '10px', borderTop: '1px solid var(--border-stroke)', fontSize: '0.78rem', color: 'var(--text-secondary)' }}>
            <ArrowRightLeft size={16} color="var(--accent-turquoise)" />
            <span>
              <strong>Automated Payout:</strong> 95% (${devPayoutUsd.toFixed(2)}) automatically disbursed to Game Developer • 5% (${platformFeeUsd.toFixed(2)}) Platform Vault.
            </span>
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

        {/* SINGLE Payment Deposit Address & SINGLE Mobile QR Code */}
        <div style={{ marginBottom: '20px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
            <label style={{ color: 'var(--text-secondary)', fontSize: '0.82rem', fontWeight: 600 }}>
              Deposit Address & Mobile QR Code
            </label>
            {copied && <span style={{ color: 'var(--accent-emerald)', fontSize: '0.78rem', fontWeight: 700 }}>Address Copied!</span>}
          </div>

          <div className="glass-panel" style={{ padding: '16px', backgroundColor: 'rgba(11, 14, 20, 0.65)', border: '1px solid var(--border-active)' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
              {showQrCode && (
                <div style={{ backgroundColor: '#ffffff', padding: '8px', borderRadius: '8px', flexShrink: 0 }}>
                  <QRCodeSVG value={activeAddress} size={110} level="M" />
                </div>
              )}

              <div style={{ flexGrow: 1, minWidth: 0 }}>
                <span style={{ color: 'var(--text-muted)', fontSize: '0.75rem', display: 'block', marginBottom: '4px' }}>
                  Send exactly <strong>${totalAmount.toFixed(2)} {selectedToken}</strong> to:
                </span>
                <code style={{ fontSize: '0.82rem', color: 'var(--text-primary)', wordBreak: 'break-all', display: 'block', marginBottom: '10px' }}>
                  {activeAddress}
                </code>
                <button onClick={() => copyToClipboard(activeAddress)} className="btn-outline" style={{ padding: '6px 12px', fontSize: '0.78rem' }}>
                  {copied ? <CheckCircle2 size={14} color="var(--accent-emerald)" /> : <Copy size={14} />}
                  Copy Address
                </button>
              </div>
            </div>
          </div>
        </div>

        {/* Tx Hash Signature Input */}
        <div style={{ marginBottom: '20px' }}>
          <label style={{ display: 'block', color: 'var(--text-secondary)', fontSize: '0.78rem', fontWeight: 600, marginBottom: '6px' }}>
            Transaction Signature / Hash
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
              Verifying x402 On-Chain Settlement...
            </>
          ) : (
            'Verify x402 Payment & Unlock Games'
          )}
        </button>
      </div>
    </div>
  );
};
