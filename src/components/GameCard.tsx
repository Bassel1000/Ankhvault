import React from 'react';
import { ShoppingCart, Check, Play, Download, Star } from 'lucide-react';

export interface GameItem {
  id: string;
  title: string;
  category: string;
  price: number;
  rating: number;
  description: string;
  tag: string;
  download_url: string;
}

interface GameCardProps {
  game: GameItem;
  isOwned: boolean;
  isInCart: boolean;
  isInstalled: boolean;
  installProgress?: { percentage: number; status: string } | null;
  onAddToCart: (gameId: string) => void;
  onDownloadAndInstall: (gameId: string, url: string) => void;
  onLaunchGame: (gameId: string) => void;
}

export const GameCard: React.FC<GameCardProps> = ({
  game,
  isOwned,
  isInCart,
  isInstalled,
  installProgress,
  onAddToCart,
  onDownloadAndInstall,
  onLaunchGame,
}) => {
  const isInstalling = installProgress && installProgress.percentage < 100;

  return (
    <div className="glass-panel glass-panel-interactive" style={{ padding: '20px', display: 'flex', flexDirection: 'column', height: '100%', position: 'relative' }}>
      {/* Category Tag Badge */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
        <span style={{ backgroundColor: 'rgba(23, 145, 158, 0.2)', color: 'var(--accent-turquoise)', fontSize: '0.72rem', fontWeight: 700, padding: '4px 10px', borderRadius: '6px', border: '1px solid rgba(23, 145, 158, 0.4)' }}>
          {game.tag}
        </span>
        <div style={{ display: 'flex', alignItems: 'center', gap: '4px', color: '#fbbf24', fontSize: '0.85rem', fontWeight: 600 }}>
          <Star size={14} fill="#fbbf24" />
          {game.rating.toFixed(1)}
        </div>
      </div>

      {/* Game Title & Category */}
      <h3 style={{ fontFamily: 'var(--font-cinzel)', color: 'var(--accent-gold)', fontSize: '1.2rem', fontWeight: 700, marginBottom: '4px' }}>
        {game.title}
      </h3>
      <p style={{ color: 'var(--text-muted)', fontSize: '0.8rem', fontWeight: 500, marginBottom: '12px' }}>
        {game.category}
      </p>

      {/* Description */}
      <p style={{ color: 'var(--text-secondary)', fontSize: '0.85rem', lineHeight: '1.45', marginBottom: '20px', flexGrow: 1 }}>
        {game.description}
      </p>

      {/* Pricing & Actions */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: 'auto', paddingTop: '14px', borderTop: '1px solid var(--border-stroke)' }}>
        <div>
          <span style={{ fontSize: '0.72rem', color: 'var(--text-muted)', display: 'block' }}>Price</span>
          <span style={{ fontSize: '1.15rem', color: game.price === 0 ? 'var(--accent-emerald)' : 'var(--text-primary)', fontWeight: 700 }}>
            {game.price === 0 ? 'Free' : `$${game.price.toFixed(2)}`}
          </span>
        </div>

        {/* Action Button States */}
        {isOwned ? (
          isInstalled ? (
            <button className="btn-gold" onClick={() => onLaunchGame(game.id)}>
              <Play size={16} fill="#0b0e14" />
              Play
            </button>
          ) : isInstalling ? (
            <div style={{ width: '120px' }}>
              <div style={{ fontSize: '0.72rem', color: 'var(--accent-gold)', marginBottom: '4px', textAlign: 'right' }}>
                {installProgress.percentage}%
              </div>
              <div style={{ width: '100%', height: '6px', backgroundColor: 'rgba(255, 255, 255, 0.1)', borderRadius: '3px', overflow: 'hidden' }}>
                <div style={{ width: `${installProgress.percentage}%`, height: '100%', backgroundColor: 'var(--accent-gold)', transition: 'width 0.3s ease' }} />
              </div>
            </div>
          ) : (
            <button className="btn-turquoise" onClick={() => onDownloadAndInstall(game.id, game.download_url)}>
              <Download size={16} />
              Install
            </button>
          )
        ) : isInCart ? (
          <button className="btn-outline" disabled style={{ opacity: 0.8, cursor: 'default', color: 'var(--accent-gold)' }}>
            <Check size={16} />
            In Cart
          </button>
        ) : (
          <button className={game.price === 0 ? "btn-turquoise" : "btn-gold"} onClick={() => onAddToCart(game.id)}>
            {game.price === 0 ? (
              <>
                <Download size={16} />
                Get Free Game
              </>
            ) : (
              <>
                <ShoppingCart size={16} />
                Add to Cart
              </>
            )}
          </button>
        )}
      </div>
    </div>
  );
};
