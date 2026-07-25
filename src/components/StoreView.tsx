import React from 'react';
import { GameCard, GameItem } from './GameCard';
import { Sparkles, Gamepad2 } from 'lucide-react';

interface StoreViewProps {
  catalog: GameItem[];
  userLibrary: string[];
  userCart: string[];
  installedGames: Record<string, boolean>;
  installProgressMap: Record<string, { percentage: number; status: string }>;
  searchQuery: string;
  onAddToCart: (gameId: string) => void;
  onDownloadAndInstall: (gameId: string, url: string) => void;
  onLaunchGame: (gameId: string) => void;
}

export const StoreView: React.FC<StoreViewProps> = ({
  catalog,
  userLibrary,
  userCart,
  installedGames,
  installProgressMap,
  searchQuery,
  onAddToCart,
  onDownloadAndInstall,
  onLaunchGame,
}) => {
  const filteredCatalog = catalog.filter(
    (game) =>
      game.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
      game.category.toLowerCase().includes(searchQuery.toLowerCase()) ||
      game.description.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div style={{ padding: '28px', overflowY: 'auto', height: '100%' }}>
      {/* Featured Header Banner */}
      <div className="glass-panel" style={{ padding: '32px', marginBottom: '32px', background: 'linear-gradient(135deg, rgba(212, 175, 55, 0.15) 0%, rgba(23, 145, 158, 0.1) 100%)', border: '1px solid var(--border-active)' }}>
        <span style={{ color: 'var(--accent-gold)', fontSize: '0.78rem', fontWeight: 700, letterSpacing: '0.1em', textTransform: 'uppercase' }}>
          DUAT STUDIO • ANKHVAULT STORE
        </span>
        <h2 style={{ fontFamily: 'var(--font-cinzel)', color: 'var(--text-primary)', fontSize: '2.2rem', fontWeight: 900, marginTop: '4px', marginBottom: '8px' }}>
          ANKHVAULT GAMING CATALOG
        </h2>
        <p style={{ color: 'var(--text-secondary)', maxWidth: '600px', fontSize: '0.95rem', lineHeight: '1.5', marginBottom: '0' }}>
          Discover indie titles from creators worldwide. 95% of gross revenue goes directly to game developers.
        </p>
      </div>

      {/* Store Section Title */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
        <h3 style={{ fontFamily: 'var(--font-cinzel)', color: 'var(--accent-gold)', fontSize: '1.3rem', fontWeight: 700 }}>
          EXPLORE CATALOG ({filteredCatalog.length})
        </h3>
      </div>

      {/* Store Grid or Empty "Stay Tuned" Placeholder */}
      {catalog.length === 0 ? (
        <div className="glass-panel" style={{ padding: '60px 32px', textAlign: 'center', margin: '20px auto', maxWidth: '640px', border: '1px solid var(--accent-gold)', backgroundColor: 'rgba(11, 14, 20, 0.7)' }}>
          <div style={{ width: '64px', height: '64px', borderRadius: '50%', backgroundColor: 'rgba(212, 175, 55, 0.15)', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 20px auto' }}>
            <Sparkles size={32} color="var(--accent-gold)" />
          </div>
          <h2 style={{ fontFamily: 'var(--font-cinzel)', color: 'var(--accent-gold)', fontSize: '1.8rem', fontWeight: 800, marginBottom: '12px' }}>
            STAY TUNED! STORE OPENING SOON
          </h2>
          <p style={{ color: 'var(--text-secondary)', fontSize: '0.95rem', lineHeight: 1.6, marginBottom: '20px' }}>
            No titles have been uploaded yet. As game creators submit their games via the Duat Studio Developer Portal, published titles will appear right here in the store catalog.
          </p>
          <div style={{ display: 'inline-flex', alignItems: 'center', gap: '8px', color: 'var(--accent-turquoise)', fontSize: '0.85rem', fontWeight: 600, backgroundColor: 'rgba(23, 145, 158, 0.15)', padding: '6px 16px', borderRadius: '20px', border: '1px solid rgba(23, 145, 158, 0.3)' }}>
            <Gamepad2 size={16} />
            <span>Developer Self-Service Upload Active</span>
          </div>
        </div>
      ) : filteredCatalog.length === 0 ? (
        <div className="glass-panel" style={{ padding: '48px', textAlign: 'center', color: 'var(--text-muted)' }}>
          No games found matching "{searchQuery}"
        </div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '24px' }}>
          {filteredCatalog.map((game) => (
            <GameCard
              key={game.id}
              game={game}
              isOwned={userLibrary.includes(game.id)}
              isInCart={userCart.includes(game.id)}
              isInstalled={!!installedGames[game.id]}
              installProgress={installProgressMap[game.id]}
              onAddToCart={onAddToCart}
              onDownloadAndInstall={onDownloadAndInstall}
              onLaunchGame={onLaunchGame}
            />
          ))}
        </div>
      )}
    </div>
  );
};
