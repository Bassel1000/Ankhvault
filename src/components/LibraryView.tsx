import React from 'react';
import { GameCard, GameItem } from './GameCard';
import { Library, Store } from 'lucide-react';

interface LibraryViewProps {
  catalog: GameItem[];
  userLibrary: string[];
  installedGames: Record<string, boolean>;
  installProgressMap: Record<string, { percentage: number; status: string }>;
  onGoToStore: () => void;
  onDownloadAndInstall: (gameId: string, url: string) => void;
  onLaunchGame: (gameId: string) => void;
}

export const LibraryView: React.FC<LibraryViewProps> = ({
  catalog,
  userLibrary,
  installedGames,
  installProgressMap,
  onGoToStore,
  onDownloadAndInstall,
  onLaunchGame,
}) => {
  const ownedGames = catalog.filter((game) => userLibrary.includes(game.id));

  return (
    <div style={{ padding: '28px', overflowY: 'auto', height: '100%' }}>
      <div style={{ marginBottom: '24px' }}>
        <h2 style={{ fontFamily: 'var(--font-cinzel)', color: 'var(--accent-gold)', fontSize: '1.6rem', fontWeight: 700 }}>
          MY GAME LIBRARY ({ownedGames.length})
        </h2>
        <p style={{ color: 'var(--text-muted)', fontSize: '0.88rem' }}>
          Installed and purchased titles tied to your encrypted vault account.
        </p>
      </div>

      {ownedGames.length === 0 ? (
        <div className="glass-panel" style={{ padding: '64px 32px', textAlign: 'center', maxWidth: '500px', margin: '40px auto' }}>
          <Library size={48} color="var(--accent-gold)" style={{ opacity: 0.6, marginBottom: '16px' }} />
          <h3 style={{ fontFamily: 'var(--font-cinzel)', color: 'var(--text-primary)', fontSize: '1.3rem', marginBottom: '8px' }}>
            Your Library is Empty
          </h3>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.88rem', marginBottom: '24px' }}>
            Browse the Ankhvault Store catalog to acquire flagship titles and free games.
          </p>
          <button className="btn-gold" onClick={onGoToStore} style={{ margin: '0 auto' }}>
            <Store size={18} />
            Explore Store Catalog
          </button>
        </div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '24px' }}>
          {ownedGames.map((game) => (
            <GameCard
              key={game.id}
              game={game}
              isOwned={true}
              isInCart={false}
              isInstalled={!!installedGames[game.id]}
              installProgress={installProgressMap[game.id]}
              onAddToCart={() => {}}
              onDownloadAndInstall={onDownloadAndInstall}
              onLaunchGame={onLaunchGame}
            />
          ))}
        </div>
      )}
    </div>
  );
};
