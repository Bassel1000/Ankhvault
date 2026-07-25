import React from 'react';
import { GameItem } from './GameCard';
import { Trash2, ShoppingCart, ShieldCheck } from 'lucide-react';

interface CartViewProps {
  catalog: GameItem[];
  userCart: string[];
  onRemoveFromCart: (gameId: string) => void;
  onClearCart: () => void;
  onCheckout: () => void;
}

export const CartView: React.FC<CartViewProps> = ({
  catalog,
  userCart,
  onRemoveFromCart,
  onClearCart,
  onCheckout,
}) => {
  const cartGames = catalog.filter((game) => userCart.includes(game.id));
  const subtotal = cartGames.reduce((acc, game) => acc + game.price, 0);

  return (
    <div style={{ padding: '28px', overflowY: 'auto', height: '100%', maxWidth: '900px' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
        <div>
          <h2 style={{ fontFamily: 'var(--font-cinzel)', color: 'var(--accent-gold)', fontSize: '1.6rem', fontWeight: 700 }}>
            SHOPPING CART ({cartGames.length})
          </h2>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.88rem' }}>
            Review your selected titles before proceeding to MoonPay Commerce checkout.
          </p>
        </div>

        {cartGames.length > 0 && (
          <button className="btn-outline" onClick={onClearCart} style={{ fontSize: '0.8rem', padding: '6px 12px' }}>
            Clear Cart
          </button>
        )}
      </div>

      {cartGames.length === 0 ? (
        <div className="glass-panel" style={{ padding: '64px 32px', textAlign: 'center', margin: '40px 0' }}>
          <ShoppingCart size={48} color="var(--accent-gold)" style={{ opacity: 0.6, marginBottom: '16px' }} />
          <h3 style={{ fontFamily: 'var(--font-cinzel)', color: 'var(--text-primary)', fontSize: '1.3rem', marginBottom: '8px' }}>
            Your Cart is Empty
          </h3>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.88rem' }}>
            Add premium or free games from the store catalog to your cart.
          </p>
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
          {/* Cart Item List */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            {cartGames.map((game) => (
              <div key={game.id} className="glass-panel" style={{ padding: '16px 20px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                  <h4 style={{ fontFamily: 'var(--font-cinzel)', color: 'var(--accent-gold)', fontSize: '1.1rem', fontWeight: 700 }}>
                    {game.title}
                  </h4>
                  <span style={{ color: 'var(--text-muted)', fontSize: '0.8rem' }}>{game.category}</span>
                </div>

                <div style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
                  <span style={{ fontSize: '1.1rem', color: game.price === 0 ? 'var(--accent-emerald)' : 'var(--text-primary)', fontWeight: 700 }}>
                    {game.price === 0 ? 'Free' : `$${game.price.toFixed(2)}`}
                  </span>

                  <button
                    onClick={() => onRemoveFromCart(game.id)}
                    style={{ background: 'none', border: 'none', color: '#ef4444', cursor: 'pointer', padding: '4px' }}
                    title="Remove from cart"
                  >
                    <Trash2 size={18} />
                  </button>
                </div>
              </div>
            ))}
          </div>

          {/* Subtotal & MoonPay Checkout Card */}
          <div className="glass-panel" style={{ padding: '24px', backgroundColor: 'rgba(212, 175, 55, 0.08)', border: '1px solid var(--accent-gold)' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
              <span style={{ color: 'var(--text-secondary)', fontSize: '1rem', fontWeight: 600 }}>Total Subtotal</span>
              <span style={{ color: 'var(--accent-gold)', fontSize: '1.6rem', fontWeight: 800 }}>
                ${subtotal.toFixed(2)} USD
              </span>
            </div>

            <div style={{ display: 'flex', alignItems: 'center', gap: '8px', color: 'var(--text-muted)', fontSize: '0.8rem', marginBottom: '20px' }}>
              <ShieldCheck size={16} color="var(--accent-turquoise)" />
              {subtotal === 0
                ? "Free games require zero payment verification."
                : "Payments settled via MoonPay Commerce (Cards, Apple Pay, Google Pay, USDT, USDC)"}
            </div>

            <button
              className={subtotal === 0 ? "btn-turquoise glow-pulse" : "btn-gold glow-pulse"}
              onClick={onCheckout}
              style={{ width: '100%', justifyContent: 'center', padding: '14px', fontSize: '1.05rem' }}
            >
              {subtotal === 0 ? "Claim Free Games" : "Proceed to MoonPay Commerce Checkout"}
            </button>
          </div>
        </div>
      )}
    </div>
  );
};
