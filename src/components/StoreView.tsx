import React from 'react';
import { GameCard, GameItem } from './GameCard';

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
          DUAT STUDIO FEATURED FLAGSHIP
        </span>
        <h2 style={{ fontFamily: 'var(--font-cinzel)', color: 'var(--text-primary)', fontSize: '2.2rem', fontWeight: 900, marginTop: '4px', marginBottom: '8px' }}>
          SHADOW OF KEMET
        </h2>
        <p style={{ color: 'var(--text-secondary)', maxWidth: '600px', fontSize: '0.95rem', lineHeight: '1.5', marginBottom: '16px' }}>
          Unravel ancient temple mysteries, awaken guardian relics, and slay mythical monsters in an epic dark fantasy adventure powered by next-gen vector graphics.
        </p>
      </div>

      {/* Store Section Title */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
        <h3 style={{ fontFamily: 'var(--font-cinzel)', color: 'var(--accent-gold)', fontSize: '1.3rem', fontWeight: 700 }}>
          EXPLORE CATALOG ({filteredCatalog.length})
        </h3>
      </div>

      {/* Store Grid */}
      {filteredCatalog.length === 0 ? (
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
