import React from 'react';
import { Search, User, LogOut, Settings } from 'lucide-react';
import ankhvaultLogo from '../assets/ankhvault-logo.png';

interface TopBarProps {
  currentUsername: string;
  searchQuery: string;
  setSearchQuery: (query: string) => void;
  onLogout: () => void;
  onOpenAccountModal: () => void;
  onOpenUpdateModal?: () => void;
}

export const TopBar: React.FC<TopBarProps> = ({
  currentUsername,
  searchQuery,
  setSearchQuery,
  onLogout,
  onOpenAccountModal,
  onOpenUpdateModal,
}) => {
  return (
    <header className="glass-panel" style={{ borderRadius: 0, borderTop: 'none', borderLeft: 'none', borderRight: 'none', padding: '14px 28px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', zIndex: 50 }}>
      {/* Brand Header */}
      <div style={{ display: 'flex', alignItems: 'center', gap: '14px' }}>
        <img 
          src={ankhvaultLogo} 
          alt="Ankhvault Logo" 
          style={{ width: '42px', height: '42px', filter: 'drop-shadow(0 0 8px rgba(212, 175, 55, 0.4))', objectFit: 'contain' }} 
        />
        <div>
          <h1 style={{ fontFamily: 'var(--font-cinzel)', color: 'var(--accent-gold)', fontSize: '1.4rem', fontWeight: 700, letterSpacing: '0.05em' }}>
            ANKHVAULT
          </h1>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.75rem', fontWeight: 500 }}>
            Next-Gen Game Launcher & Store (Duat Studio)
          </p>
        </div>
      </div>

      {/* Right Action Items */}
      <div style={{ display: 'flex', alignItems: 'center', gap: '18px' }}>
        {/* Live Catalog Search */}
        <div style={{ position: 'relative', width: '280px' }}>
          <Search size={18} style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} />
          <input
            type="text"
            placeholder="Search catalog titles..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            style={{
              width: '100%',
              padding: '9px 14px 9px 38px',
              backgroundColor: 'rgba(11, 14, 20, 0.75)',
              border: '1px solid var(--border-stroke)',
              borderRadius: '8px',
              color: 'var(--text-primary)',
              fontSize: '0.88rem',
              outline: 'none',
            }}
          />
        </div>

        {/* Check for Updates Button */}
        {onOpenUpdateModal && (
          <button
            onClick={onOpenUpdateModal}
            className="btn-outline"
            style={{ padding: '8px 12px', fontSize: '0.82rem', display: 'flex', alignItems: 'center', gap: '6px' }}
            title="Check for Launcher Updates"
          >
            <Settings size={14} color="var(--accent-gold)" />
            Updates
          </button>
        )}

        {/* User Account Badge (Click to open Account Management) */}
        <div
          onClick={onOpenAccountModal}
          className="glass-panel glass-panel-interactive"
          style={{ padding: '6px 14px', display: 'flex', alignItems: 'center', gap: '8px', borderColor: 'var(--accent-gold)', cursor: 'pointer' }}
          title="Manage Account Settings & Profile"
        >
          <User size={16} color="var(--accent-gold)" />
          <span style={{ color: 'var(--text-primary)', fontWeight: 600, fontSize: '0.9rem' }}>
            {currentUsername}
          </span>
        </div>

        {/* Logout Button */}
        <button className="btn-outline" onClick={onLogout} style={{ padding: '8px 14px', fontSize: '0.85rem' }}>
          <LogOut size={16} />
          Logout
        </button>
      </div>
    </header>
  );
};
