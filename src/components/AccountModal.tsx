import React, { useState } from 'react';
import { X, User, KeyRound, Trash2, ShieldAlert, CheckCircle2, Loader2, Edit3 } from 'lucide-react';
import ankhvaultLogo from '../assets/ankhvault-logo.png';

interface AccountModalProps {
  isOpen: boolean;
  onClose: () => void;
  currentUsername: string;
  onChangeUsername: (newUsername: string) => Promise<{ success: boolean; message: string }>;
  onChangePassword: (newPassword: string) => Promise<{ success: boolean; message: string }>;
  onDeleteAccount: () => Promise<void>;
}

export const AccountModal: React.FC<AccountModalProps> = ({
  isOpen,
  onClose,
  currentUsername,
  onChangeUsername,
  onChangePassword,
  onDeleteAccount,
}) => {
  const [activeTab, setActiveTab] = useState<'profile' | 'password' | 'danger'>('profile');
  const [newUsername, setNewUsername] = useState(currentUsername);
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');

  const [loading, setLoading] = useState(false);
  const [msg, setMsg] = useState<{ text: string; type: 'error' | 'success' } | null>(null);
  const [showConfirmDelete, setShowConfirmDelete] = useState(false);

  if (!isOpen) return null;

  const handleUpdateUsername = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newUsername.trim() || newUsername.trim() === currentUsername) {
      setMsg({ text: 'Please enter a new username', type: 'error' });
      return;
    }

    setLoading(true);
    setMsg(null);

    try {
      const res = await onChangeUsername(newUsername.trim());
      if (res.success) {
        setMsg({ text: 'Username updated successfully!', type: 'success' });
      } else {
        setMsg({ text: res.message || 'Username already taken', type: 'error' });
      }
    } catch (err: any) {
      setMsg({ text: err?.toString() || 'Error updating username', type: 'error' });
    } finally {
      setLoading(false);
    }
  };

  const handleUpdatePassword = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newPassword || newPassword.length < 4) {
      setMsg({ text: 'Password must be at least 4 characters', type: 'error' });
      return;
    }
    if (newPassword !== confirmPassword) {
      setMsg({ text: 'Passwords do not match', type: 'error' });
      return;
    }

    setLoading(true);
    setMsg(null);

    try {
      const res = await onChangePassword(newPassword);
      if (res.success) {
        setMsg({ text: 'Password reset successfully!', type: 'success' });
        setNewPassword('');
        setConfirmPassword('');
      } else {
        setMsg({ text: res.message || 'Failed to update password', type: 'error' });
      }
    } catch (err: any) {
      setMsg({ text: err?.toString() || 'Error changing password', type: 'error' });
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    setLoading(true);
    try {
      await onDeleteAccount();
      onClose();
    } catch (err: any) {
      setMsg({ text: 'Failed to delete account', type: 'error' });
      setLoading(false);
    }
  };

  return (
    <div style={{ position: 'fixed', inset: 0, backgroundColor: 'rgba(5, 7, 10, 0.9)', backdropFilter: 'blur(16px)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 220, padding: '20px' }}>
      <div className="glass-panel animate-fade-in" style={{ width: '480px', padding: '32px', position: 'relative', border: '1px solid var(--accent-gold)' }}>
        {/* Close Button */}
        <button onClick={onClose} style={{ position: 'absolute', right: '20px', top: '20px', background: 'none', border: 'none', color: 'var(--text-muted)', cursor: 'pointer' }}>
          <X size={20} />
        </button>

        {/* Modal Header */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '14px', marginBottom: '24px' }}>
          <img src={ankhvaultLogo} alt="Ankhvault Logo" style={{ width: '48px', height: '48px', objectFit: 'contain', filter: 'drop-shadow(0 0 10px rgba(212, 175, 55, 0.5))' }} />
          <div>
            <h2 style={{ fontFamily: 'var(--font-cinzel)', color: 'var(--accent-gold)', fontSize: '1.4rem', fontWeight: 700 }}>
              ACCOUNT MANAGEMENT
            </h2>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.8rem' }}>
              Vault Identity & Cloud Sync Settings
            </p>
          </div>
        </div>

        {/* Settings Tab Selector */}
        <div style={{ display: 'flex', gap: '8px', marginBottom: '24px', borderBottom: '1px solid var(--border-stroke)', paddingBottom: '12px' }}>
          <button
            type="button"
            onClick={() => { setActiveTab('profile'); setMsg(null); }}
            style={{
              padding: '6px 14px',
              borderRadius: '6px',
              border: activeTab === 'profile' ? '1px solid var(--accent-gold)' : '1px solid transparent',
              backgroundColor: activeTab === 'profile' ? 'rgba(212, 175, 55, 0.15)' : 'transparent',
              color: activeTab === 'profile' ? 'var(--accent-gold)' : 'var(--text-secondary)',
              fontWeight: 600,
              fontSize: '0.82rem',
              cursor: 'pointer',
              display: 'flex',
              alignItems: 'center',
              gap: '6px',
            }}
          >
            <User size={15} />
            Username Profile
          </button>

          <button
            type="button"
            onClick={() => { setActiveTab('password'); setMsg(null); }}
            style={{
              padding: '6px 14px',
              borderRadius: '6px',
              border: activeTab === 'password' ? '1px solid var(--accent-gold)' : '1px solid transparent',
              backgroundColor: activeTab === 'password' ? 'rgba(212, 175, 55, 0.15)' : 'transparent',
              color: activeTab === 'password' ? 'var(--accent-gold)' : 'var(--text-secondary)',
              fontWeight: 600,
              fontSize: '0.82rem',
              cursor: 'pointer',
              display: 'flex',
              alignItems: 'center',
              gap: '6px',
            }}
          >
            <KeyRound size={15} />
            Reset Password
          </button>

          <button
            type="button"
            onClick={() => { setActiveTab('danger'); setMsg(null); }}
            style={{
              padding: '6px 14px',
              borderRadius: '6px',
              border: activeTab === 'danger' ? '1px solid #ef4444' : '1px solid transparent',
              backgroundColor: activeTab === 'danger' ? 'rgba(239, 68, 68, 0.15)' : 'transparent',
              color: activeTab === 'danger' ? '#ef4444' : 'var(--text-muted)',
              fontWeight: 600,
              fontSize: '0.82rem',
              cursor: 'pointer',
              display: 'flex',
              alignItems: 'center',
              gap: '6px',
              marginLeft: 'auto',
            }}
          >
            <Trash2 size={15} />
            Delete Account
          </button>
        </div>

        {/* Message Banner */}
        {msg && (
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', padding: '10px 14px', borderRadius: '8px', marginBottom: '16px', fontSize: '0.85rem', backgroundColor: msg.type === 'error' ? 'rgba(239, 68, 68, 0.15)' : 'rgba(16, 185, 129, 0.15)', border: msg.type === 'error' ? '1px solid rgba(239, 68, 68, 0.3)' : '1px solid rgba(16, 185, 129, 0.3)', color: msg.type === 'error' ? '#ef4444' : 'var(--accent-emerald)' }}>
            {msg.type === 'error' ? <ShieldAlert size={16} /> : <CheckCircle2 size={16} />}
            {msg.text}
          </div>
        )}

        {/* Tab 1: Username Profile */}
        {activeTab === 'profile' && (
          <form onSubmit={handleUpdateUsername} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            <div>
              <label style={{ display: 'block', color: 'var(--text-secondary)', fontSize: '0.8rem', fontWeight: 600, marginBottom: '6px' }}>
                Change Username
              </label>
              <div style={{ position: 'relative' }}>
                <Edit3 size={16} style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} />
                <input
                  type="text"
                  value={newUsername}
                  onChange={(e) => setNewUsername(e.target.value)}
                  placeholder="New Username"
                  style={{ width: '100%', padding: '10px 12px 10px 36px', backgroundColor: 'rgba(11, 14, 20, 0.85)', border: '1px solid var(--border-stroke)', borderRadius: '8px', color: 'var(--text-primary)', outline: 'none' }}
                />
              </div>
              <span style={{ fontSize: '0.74rem', color: 'var(--text-muted)', marginTop: '4px', display: 'block' }}>
                Updates your handle across local vault and cloud games library.
              </span>
            </div>

            <button className="btn-gold" type="submit" disabled={loading} style={{ justifyContent: 'center', padding: '12px', fontSize: '0.9rem' }}>
              {loading ? <Loader2 size={18} className="animate-spin" /> : 'Save New Username'}
            </button>
          </form>
        )}

        {/* Tab 2: Password Reset */}
        {activeTab === 'password' && (
          <form onSubmit={handleUpdatePassword} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            <div>
              <label style={{ display: 'block', color: 'var(--text-secondary)', fontSize: '0.8rem', fontWeight: 600, marginBottom: '6px' }}>
                New Password
              </label>
              <input
                type="password"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                placeholder="Enter new password"
                style={{ width: '100%', padding: '10px 12px', backgroundColor: 'rgba(11, 14, 20, 0.85)', border: '1px solid var(--border-stroke)', borderRadius: '8px', color: 'var(--text-primary)', outline: 'none' }}
              />
            </div>

            <div>
              <label style={{ display: 'block', color: 'var(--text-secondary)', fontSize: '0.8rem', fontWeight: 600, marginBottom: '6px' }}>
                Confirm New Password
              </label>
              <input
                type="password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                placeholder="Re-enter new password"
                style={{ width: '100%', padding: '10px 12px', backgroundColor: 'rgba(11, 14, 20, 0.85)', border: '1px solid var(--border-stroke)', borderRadius: '8px', color: 'var(--text-primary)', outline: 'none' }}
              />
            </div>

            <button className="btn-gold" type="submit" disabled={loading} style={{ justifyContent: 'center', padding: '12px', fontSize: '0.9rem' }}>
              {loading ? <Loader2 size={18} className="animate-spin" /> : 'Update Password'}
            </button>
          </form>
        )}

        {/* Tab 3: Danger Zone (Delete Account) */}
        {activeTab === 'danger' && (
          <div style={{ textAlign: 'center' }}>
            <div style={{ width: '56px', height: '56px', borderRadius: '50%', backgroundColor: 'rgba(239, 68, 68, 0.15)', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 16px auto' }}>
              <ShieldAlert size={28} color="#ef4444" />
            </div>

            <h3 style={{ color: '#ef4444', fontSize: '1.2rem', fontWeight: 700, marginBottom: '8px' }}>
              PERMANENT ACCOUNT DELETION
            </h3>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.86rem', lineHeight: 1.5, marginBottom: '24px' }}>
              Deleting your account will permanently erase your local encrypted database profile and your Supabase cloud library records for <strong>{currentUsername}</strong>.
            </p>

            {!showConfirmDelete ? (
              <button
                type="button"
                onClick={() => setShowConfirmDelete(true)}
                style={{ width: '100%', padding: '12px', backgroundColor: 'rgba(239, 68, 68, 0.2)', border: '1px solid #ef4444', color: '#ef4444', fontWeight: 700, borderRadius: '8px', cursor: 'pointer' }}
              >
                I want to delete my account
              </button>
            ) : (
              <div style={{ display: 'flex', gap: '12px' }}>
                <button
                  type="button"
                  onClick={() => setShowConfirmDelete(false)}
                  className="btn-outline"
                  style={{ flex: 1, padding: '12px' }}
                >
                  Cancel
                </button>

                <button
                  type="button"
                  onClick={handleDelete}
                  disabled={loading}
                  style={{ flex: 1, padding: '12px', backgroundColor: '#ef4444', border: 'none', color: '#fff', fontWeight: 700, borderRadius: '8px', cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '6px' }}
                >
                  {loading ? <Loader2 size={18} className="animate-spin" /> : 'Confirm Permanent Deletion'}
                </button>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
};
