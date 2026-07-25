import React, { useState } from 'react';
import { Lock, User, Mail, ShieldAlert } from 'lucide-react';
import ankhvaultLogo from '../assets/ankhvault-logo.png';

interface AuthModalProps {
  isOpen: boolean;
  onLoginSuccess: (username: string) => void;
  onAuthenticate: (username: string, password: string) => Promise<boolean>;
  onRegister: (username: string, email: string, password: string) => Promise<boolean>;
}

export const AuthModal: React.FC<AuthModalProps> = ({
  isOpen,
  onLoginSuccess,
  onAuthenticate,
  onRegister,
}) => {
  const [isRegisterMode, setIsRegisterMode] = useState(false);
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [errorMsg, setErrorMsg] = useState('');
  const [loading, setLoading] = useState(false);

  if (!isOpen) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!username.trim() || !password.trim()) {
      setErrorMsg('Please fill in all required fields');
      return;
    }

    setErrorMsg('');
    setLoading(true);

    try {
      if (isRegisterMode) {
        if (!email.trim()) {
          setErrorMsg('Please enter your email address');
          setLoading(false);
          return;
        }
        const success = await onRegister(username.trim(), email.trim(), password);
        if (success) {
          onLoginSuccess(username.trim());
        } else {
          setErrorMsg('Username already exists or registration failed');
        }
      } else {
        const success = await onAuthenticate(username.trim(), password);
        if (success) {
          onLoginSuccess(username.trim());
        } else {
          setErrorMsg('Invalid username or password');
        }
      }
    } catch (err: any) {
      setErrorMsg(err?.toString() || 'Authentication error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ position: 'fixed', inset: 0, backgroundColor: 'rgba(5, 7, 10, 0.92)', backdropFilter: 'blur(16px)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 200 }}>
      <div className="glass-panel animate-fade-in" style={{ width: '420px', padding: '36px', border: '1px solid var(--accent-gold)' }}>
        {/* Brand Emblem */}
        <div style={{ textAlign: 'center', marginBottom: '24px' }}>
          <img 
            src={ankhvaultLogo} 
            alt="Ankhvault Logo" 
            style={{ width: '64px', height: '64px', margin: '0 auto 12px auto', filter: 'drop-shadow(0 0 12px rgba(212, 175, 55, 0.5))', objectFit: 'contain' }} 
          />
          <h2 style={{ fontFamily: 'var(--font-cinzel)', color: 'var(--accent-gold)', fontSize: '1.6rem', fontWeight: 700 }}>
            {isRegisterMode ? 'CREATE VAULT ACCOUNT' : 'ANKHVAULT ACCESS'}
          </h2>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.8rem', marginTop: '4px' }}>
            Enterprise Encrypted Vault Gateway by Duat Studio
          </p>
        </div>

        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <div>
            <label style={{ display: 'block', color: 'var(--text-secondary)', fontSize: '0.8rem', fontWeight: 600, marginBottom: '6px' }}>
              Username
            </label>
            <div style={{ position: 'relative' }}>
              <User size={16} style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} />
              <input
                type="text"
                placeholder="Enter username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                style={{ width: '100%', padding: '10px 12px 10px 36px', backgroundColor: 'rgba(11, 14, 20, 0.8)', border: '1px solid var(--border-stroke)', borderRadius: '8px', color: 'var(--text-primary)', outline: 'none' }}
              />
            </div>
          </div>

          {isRegisterMode && (
            <div>
              <label style={{ display: 'block', color: 'var(--text-secondary)', fontSize: '0.8rem', fontWeight: 600, marginBottom: '6px' }}>
                Email Address
              </label>
              <div style={{ position: 'relative' }}>
                <Mail size={16} style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} />
                <input
                  type="email"
                  placeholder="Enter email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  style={{ width: '100%', padding: '10px 12px 10px 36px', backgroundColor: 'rgba(11, 14, 20, 0.8)', border: '1px solid var(--border-stroke)', borderRadius: '8px', color: 'var(--text-primary)', outline: 'none' }}
                />
              </div>
            </div>
          )}

          <div>
            <label style={{ display: 'block', color: 'var(--text-secondary)', fontSize: '0.8rem', fontWeight: 600, marginBottom: '6px' }}>
              Password
            </label>
            <div style={{ position: 'relative' }}>
              <Lock size={16} style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} />
              <input
                type="password"
                placeholder="Enter password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                style={{ width: '100%', padding: '10px 12px 10px 36px', backgroundColor: 'rgba(11, 14, 20, 0.8)', border: '1px solid var(--border-stroke)', borderRadius: '8px', color: 'var(--text-primary)', outline: 'none' }}
              />
            </div>
          </div>

          {errorMsg && (
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px', color: '#ef4444', fontSize: '0.82rem' }}>
              <ShieldAlert size={16} />
              {errorMsg}
            </div>
          )}

          <button className="btn-gold" type="submit" disabled={loading} style={{ width: '100%', justifyContent: 'center', padding: '12px', marginTop: '8px', fontSize: '0.95rem' }}>
            {loading ? 'Processing...' : isRegisterMode ? 'Register Account' : 'Authenticate & Unlock'}
          </button>
        </form>

        <div style={{ textAlign: 'center', marginTop: '20px', fontSize: '0.82rem', color: 'var(--text-muted)' }}>
          {isRegisterMode ? 'Already have an account?' : "Don't have a vault account?"}{' '}
          <button
            type="button"
            onClick={() => { setIsRegisterMode(!isRegisterMode); setErrorMsg(''); }}
            style={{ background: 'none', border: 'none', color: 'var(--accent-gold)', fontWeight: 700, cursor: 'pointer', textDecoration: 'underline' }}
          >
            {isRegisterMode ? 'Sign In' : 'Register Now'}
          </button>
        </div>
      </div>
    </div>
  );
};
