import React from 'react';
import { Store, Library, ShoppingCart } from 'lucide-react';

export type ActiveTab = 'store' | 'library' | 'cart';

interface SidebarProps {
  activeTab: ActiveTab;
  setActiveTab: (tab: ActiveTab) => void;
  cartCount: number;
  libraryCount: number;
}

export const Sidebar: React.FC<SidebarProps> = ({
  activeTab,
  setActiveTab,
  cartCount,
  libraryCount,
}) => {
  const navItems = [
    { id: 'store' as ActiveTab, label: 'Store Catalog', icon: Store, badge: null },
    { id: 'library' as ActiveTab, label: 'My Library', icon: Library, badge: libraryCount > 0 ? libraryCount : null },
    { id: 'cart' as ActiveTab, label: 'Cart', icon: ShoppingCart, badge: cartCount > 0 ? cartCount : null },
  ];

  return (
    <aside className="glass-panel" style={{ width: '240px', borderRadius: 0, borderTop: 'none', borderBottom: 'none', borderLeft: 'none', padding: '24px 14px', display: 'flex', flexDirection: 'column', gap: '8px' }}>
      <p style={{ color: 'var(--text-muted)', fontSize: '0.75rem', fontWeight: 700, letterSpacing: '0.08em', padding: '0 12px 12px 12px' }}>
        MAIN NAVIGATION
      </p>

      {navItems.map((item) => {
        const Icon = item.icon;
        const isActive = activeTab === item.id;

        return (
          <button
            key={item.id}
            onClick={() => setActiveTab(item.id)}
            style={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between',
              width: '100%',
              padding: '12px 16px',
              borderRadius: '8px',
              border: isActive ? '1px solid var(--accent-gold)' : '1px solid transparent',
              backgroundColor: isActive ? 'rgba(212, 175, 55, 0.12)' : 'transparent',
              color: isActive ? 'var(--accent-gold)' : 'var(--text-secondary)',
              fontWeight: isActive ? 700 : 500,
              fontSize: '0.92rem',
              cursor: 'pointer',
              transition: 'all 0.2s ease',
              textAlign: 'left',
            }}
          >
            <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
              <Icon size={18} color={isActive ? 'var(--accent-gold)' : 'var(--text-muted)'} />
              {item.label}
            </div>

            {item.badge !== null && (
              <span style={{
                backgroundColor: isActive ? 'var(--accent-gold)' : 'rgba(212, 175, 55, 0.2)',
                color: isActive ? '#0b0e14' : 'var(--accent-gold)',
                fontSize: '0.75rem',
                fontWeight: 700,
                padding: '2px 8px',
                borderRadius: '10px',
              }}>
                {item.badge}
              </span>
            )}
          </button>
        );
      })}

      <div style={{ marginTop: 'auto', padding: '16px 12px', borderTop: '1px solid var(--border-stroke)' }}>
        <p style={{ color: 'var(--text-muted)', fontSize: '0.72rem', lineHeight: '1.4' }}>
          Ankhvault Launcher v3.0<br />
          Duat Studio &copy; 2026
        </p>
      </div>
    </aside>
  );
};
