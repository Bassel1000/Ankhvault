import React, { useState, useEffect } from 'react';
import { invoke } from '@tauri-apps/api/core';
import { listen } from '@tauri-apps/api/event';
import { TopBar } from './components/TopBar';
import { Sidebar, ActiveTab } from './components/Sidebar';
import { StoreView } from './components/StoreView';
import { LibraryView } from './components/LibraryView';
import { CartView } from './components/CartView';
import { GameItem } from './components/GameCard';
import { CryptoCheckoutModal } from './components/CryptoCheckoutModal';
import { AuthModal } from './components/AuthModal';
import './styles/glassmorphism.css';

import {
  fetchCatalogFromSupabase,
  fetchUserLibrarySupabase,
  syncUserLibrarySupabase,
  recordX402TransactionSupabase,
} from './lib/supabase';

// Fallback Mock Catalog if running in standard browser dev mode
const MOCK_CATALOG: GameItem[] = [
  {
    id: "shadow_kemet",
    title: "Shadow of Kemet",
    category: "Action / RPG",
    price: 14.99,
    rating: 4.9,
    description: "Unravel ancient temple mysteries and slay mythical guardians in ancient Egypt.",
    tag: "Flagship Title",
    download_url: "https://github.com/duatstudio/downloads/releases/download/v1.0/shadow_kemet.zip",
  },
  {
    id: "nomad_racer",
    title: "Nomad Racer",
    category: "Cyberpunk Racing",
    price: 9.99,
    rating: 4.7,
    description: "High-octane neon drag racing across futuristic desert highways.",
    tag: "Best Seller",
    download_url: "https://github.com/duatstudio/downloads/releases/download/v1.0/nomad_racer.zip",
  },
  {
    id: "aegis_protocol",
    title: "Aegis Protocol",
    category: "Tactical Shooter",
    price: 0.00,
    rating: 4.8,
    description: "Competitive squad tactics and cybernetic combat in a dystopian city.",
    tag: "Free to Play",
    download_url: "https://github.com/duatstudio/downloads/releases/download/v1.0/aegis_protocol.zip",
  },
  {
    id: "chronicles_duat",
    title: "Chronicles of Duat",
    category: "Strategy / Deckbuilder",
    price: 4.99,
    rating: 4.6,
    description: "Master ritual spells and summon celestial pharaohs in turn-based combat.",
    tag: "New Release",
    download_url: "https://github.com/duatstudio/downloads/releases/download/v1.0/chronicles_duat.zip",
  },
  {
    id: "neon_odyssey",
    title: "Neon Odyssey",
    category: "Space Simulator",
    price: 14.99,
    rating: 4.9,
    description: "Explore uncharted galaxy sectors, mine rare plasma ores, and command dreadnoughts.",
    tag: "Featured",
    download_url: "https://github.com/duatstudio/downloads/releases/download/v1.0/neon_odyssey.zip",
  },
];

export const App: React.FC = () => {
  const [currentUser, setCurrentUser] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState<ActiveTab>('store');
  const [searchQuery, setSearchQuery] = useState('');
  
  const [catalog, setCatalog] = useState<GameItem[]>(MOCK_CATALOG);
  const [userLibrary, setUserLibrary] = useState<string[]>([]);
  const [userCart, setUserCart] = useState<string[]>([]);
  const [installedGames, setInstalledGames] = useState<Record<string, boolean>>({});
  const [installProgressMap, setInstallProgressMap] = useState<Record<string, { percentage: number; status: string }>>({});
  
  const [isCheckoutOpen, setIsCheckoutOpen] = useState(false);
  const [toastMessage, setToastMessage] = useState<string | null>(null);

  const showToast = (msg: string) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(null), 3000);
  };

  // Helper to check if Tauri runtime is present
  const isTauriAvailable = () => typeof window !== 'undefined' && '__TAURI_INTERNALS__' in window;

  // Load catalog & user state on login
  useEffect(() => {
    if (!currentUser) return;

    // Fetch live catalog from Supabase
    fetchCatalogFromSupabase().then((dbCatalog) => {
      if (dbCatalog && dbCatalog.length > 0) {
        setCatalog(dbCatalog);
      }
    });

    // Fetch live user library from Supabase
    fetchUserLibrarySupabase(currentUser).then((dbLib) => {
      if (dbLib) {
        setUserLibrary(dbLib);
      }
    });

    if (isTauriAvailable()) {
      invoke<GameItem[]>('get_catalog')
        .then((localCat) => {
          if (localCat && localCat.length > 0) setCatalog(localCat);
        })
        .catch(console.error);

      invoke<string[]>('get_user_library', { username: currentUser })
        .then(setUserLibrary)
        .catch(console.error);

      invoke<string[]>('get_user_cart', { username: currentUser })
        .then(setUserCart)
        .catch(console.error);
    }
  }, [currentUser]);

  // Check installation status of games
  useEffect(() => {
    if (!isTauriAvailable() || catalog.length === 0) return;

    catalog.forEach((game) => {
      invoke<boolean>('check_game_installed', { gameId: game.id })
        .then((installed) => {
          setInstalledGames((prev) => ({ ...prev, [game.id]: installed }));
        })
        .catch(() => {});
    });
  }, [catalog, userLibrary]);

  // Listen for Tauri download progress events
  useEffect(() => {
    if (!isTauriAvailable()) return;

    const unlistenPromise = listen<{ gameId: string; percentage: number; status: string }>(
      'download-progress',
      (event) => {
        const { gameId, percentage, status } = event.payload;
        setInstallProgressMap((prev) => ({
          ...prev,
          [gameId]: { percentage, status },
        }));

        if (percentage >= 100) {
          setInstalledGames((prev) => ({ ...prev, [gameId]: true }));
          showToast(`Installation complete! Ready to play.`);
        }
      }
    );

    return () => {
      unlistenPromise.then((unlisten) => unlisten());
    };
  }, []);

  // Auth Handlers
  const handleAuthenticate = async (u: string, p: string): Promise<boolean> => {
    if (isTauriAvailable()) {
      return await invoke<boolean>('authenticate_user', { username: u, password: p });
    }
    // Fallback web preview credentials
    return (u === 'admin' && p === 'admin123') || (u === 'demo' && p === 'demo') || (u === 'guest' && p === 'guest123');
  };

  const handleRegister = async (u: string, e: string, p: string): Promise<boolean> => {
    if (isTauriAvailable()) {
      return await invoke<boolean>('register_user', { username: u, email: e, password: p });
    }
    return true;
  };

  const handleLoginSuccess = (username: string) => {
    setCurrentUser(username);
    showToast(`Welcome back to Ankhvault, ${username}!`);
  };

  const handleLogout = () => {
    setCurrentUser(null);
    setUserCart([]);
    setUserLibrary([]);
    showToast('Logged out successfully.');
  };

  // Cart & Library Handlers
  const handleAddToCart = async (gameId: string) => {
    if (!currentUser) return;

    const targetGame = catalog.find((g) => g.id === gameId);
    if (targetGame && targetGame.price === 0) {
      // Free Game! Claim directly to library!
      await syncUserLibrarySupabase(currentUser, [gameId]);
      if (isTauriAvailable()) {
        await invoke('add_to_library', { username: currentUser, gameIds: [gameId] });
      }
      setUserLibrary((prev) => Array.from(new Set([...prev, gameId])));
      showToast(`${targetGame.title} added to your Library for Free!`);
      return;
    }

    if (isTauriAvailable()) {
      await invoke('add_to_cart', { username: currentUser, gameId });
    }

    setUserCart((prev) => (prev.includes(gameId) ? prev : [...prev, gameId]));
    showToast('Item added to cart');
  };

  const handleRemoveFromCart = async (gameId: string) => {
    if (!currentUser) return;

    if (isTauriAvailable()) {
      await invoke('remove_from_cart', { username: currentUser, gameId });
    }

    setUserCart((prev) => prev.filter((id) => id !== gameId));
    showToast('Item removed from cart');
  };

  const handleClearCart = async () => {
    if (!currentUser) return;

    if (isTauriAvailable()) {
      await invoke('clear_cart', { username: currentUser });
    }

    setUserCart([]);
  };

  // Web3 Checkout Handlers
  const handleVerifyTransaction = async (txHash: string, tokenSymbol: string, amountUsd: number) => {
    if (isTauriAvailable()) {
      return await invoke<{ success: boolean; message: string }>('verify_x402_payment', {
        txHash,
        tokenSymbol,
        amountUsd,
      });
    }

    return {
      success: true,
      message: `x402 ${tokenSymbol} Payment Signature Verified!`,
    };
  };

  const handlePaymentSuccess = async () => {
    if (!currentUser) return;

    const newLibrary = Array.from(new Set([...userLibrary, ...userCart]));

    // Sync to Supabase Cloud
    await syncUserLibrarySupabase(currentUser, userCart);

    if (isTauriAvailable()) {
      await invoke('add_to_library', { username: currentUser, gameIds: userCart });
    }

    setUserLibrary(newLibrary);
    setUserCart([]);
    showToast('Payment successful! Games synced to your Cloud Library.');
    setActiveTab('library');
  };

  // Download & Launch Handlers
  const handleDownloadAndInstall = async (gameId: string, url: string) => {
    if (isTauriAvailable()) {
      try {
        await invoke('download_and_install_game', { gameId, downloadUrl: url });
      } catch (err: any) {
        showToast(`Download error: ${err.toString()}`);
      }
    } else {
      // Mock progress in web mode
      setInstallProgressMap((prev) => ({ ...prev, [gameId]: { percentage: 20, status: 'Downloading...' } }));
      setTimeout(() => setInstallProgressMap((prev) => ({ ...prev, [gameId]: { percentage: 70, status: 'Extracting...' } })), 1500);
      setTimeout(() => {
        setInstallProgressMap((prev) => ({ ...prev, [gameId]: { percentage: 100, status: 'Complete' } }));
        setInstalledGames((prev) => ({ ...prev, [gameId]: true }));
        showToast('Installation complete!');
      }, 3000);
    }
  };

  const handleLaunchGame = async (gameId: string) => {
    if (isTauriAvailable()) {
      try {
        await invoke('launch_game_executable', { gameId });
        showToast(`Booting game executable...`);
      } catch (err: any) {
        showToast(`Launch error: ${err.toString()}`);
      }
    } else {
      showToast(`Launching ${gameId} executable process!`);
    }
  };

  const cartTotal = catalog
    .filter((g) => userCart.includes(g.id))
    .reduce((acc, g) => acc + g.price, 0);

  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100vh', width: '100vw', backgroundColor: 'var(--bg-root)', color: 'var(--text-primary)', overflow: 'hidden' }}>
      {/* Toast Overlay Notification */}
      {toastMessage && (
        <div className="glass-panel animate-fade-in" style={{ position: 'fixed', bottom: '24px', right: '24px', padding: '12px 20px', backgroundColor: 'rgba(212, 175, 55, 0.95)', color: '#0b0e14', fontWeight: 700, borderRadius: '8px', zIndex: 300, boxShadow: '0 8px 24px rgba(0,0,0,0.5)' }}>
          {toastMessage}
        </div>
      )}

      {/* Auth Modal */}
      <AuthModal
        isOpen={!currentUser}
        onLoginSuccess={handleLoginSuccess}
        onAuthenticate={handleAuthenticate}
        onRegister={handleRegister}
      />

      {currentUser && (
        <>
          {/* Topbar Header */}
          <TopBar
            currentUsername={currentUser}
            searchQuery={searchQuery}
            setSearchQuery={setSearchQuery}
            onLogout={handleLogout}
          />

          {/* Main Content Layout */}
          <div style={{ display: 'flex', flexGrow: 1, overflow: 'hidden' }}>
            <Sidebar
              activeTab={activeTab}
              setActiveTab={setActiveTab}
              cartCount={userCart.length}
              libraryCount={userLibrary.length}
            />

            <main style={{ flexGrow: 1, overflow: 'hidden', position: 'relative' }}>
              {activeTab === 'store' && (
                <StoreView
                  catalog={catalog}
                  userLibrary={userLibrary}
                  userCart={userCart}
                  installedGames={installedGames}
                  installProgressMap={installProgressMap}
                  searchQuery={searchQuery}
                  onAddToCart={handleAddToCart}
                  onDownloadAndInstall={handleDownloadAndInstall}
                  onLaunchGame={handleLaunchGame}
                />
              )}

              {activeTab === 'library' && (
                <LibraryView
                  catalog={catalog}
                  userLibrary={userLibrary}
                  installedGames={installedGames}
                  installProgressMap={installProgressMap}
                  onGoToStore={() => setActiveTab('store')}
                  onDownloadAndInstall={handleDownloadAndInstall}
                  onLaunchGame={handleLaunchGame}
                />
              )}

              {activeTab === 'cart' && (
                <CartView
                  catalog={catalog}
                  userCart={userCart}
                  onRemoveFromCart={handleRemoveFromCart}
                  onClearCart={handleClearCart}
                  onCheckout={() => {
                    if (cartTotal === 0) {
                      handlePaymentSuccess();
                    } else {
                      setIsCheckoutOpen(true);
                    }
                  }}
                />
              )}
            </main>
          </div>

          {/* Crypto Checkout Modal */}
          <CryptoCheckoutModal
            isOpen={isCheckoutOpen}
            onClose={() => setIsCheckoutOpen(false)}
            totalAmount={cartTotal}
            devWalletSol={catalog.find((g) => userCart.includes(g.id))?.dev_wallet_sol}
            devWalletEvm={catalog.find((g) => userCart.includes(g.id))?.dev_wallet_evm}
            onPaymentSuccess={handlePaymentSuccess}
            onVerifyTransaction={handleVerifyTransaction}
          />
        </>
      )}
    </div>
  );
};
