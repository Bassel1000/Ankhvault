import React, { useState, useEffect } from 'react';
import { invoke } from '@tauri-apps/api/core';
import { listen } from '@tauri-apps/api/event';
import { TopBar } from './components/TopBar';
import { Sidebar, ActiveTab } from './components/Sidebar';
import { StoreView } from './components/StoreView';
import { LibraryView } from './components/LibraryView';
import { CartView } from './components/CartView';
import { GameItem } from './components/GameCard';
import { MoonPayCheckoutModal } from './components/MoonPayCheckoutModal';
import { AuthModal } from './components/AuthModal';
import { AccountModal } from './components/AccountModal';
import { UpdateModal } from './components/UpdateModal';
import './styles/glassmorphism.css';

import {
  fetchCatalogFromSupabase,
  fetchUserLibrarySupabase,
  syncUserLibrarySupabase,
  registerUserProfileSupabase,
  updateUsernameSupabase,
  deleteUserProfileSupabase,
} from './lib/supabase';

export const App: React.FC = () => {
  const [currentUser, setCurrentUser] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState<ActiveTab>('store');
  const [searchQuery, setSearchQuery] = useState('');
  
  const [catalog, setCatalog] = useState<GameItem[]>([]);
  const [userLibrary, setUserLibrary] = useState<string[]>([]);
  const [userCart, setUserCart] = useState<string[]>([]);
  const [installedGames, setInstalledGames] = useState<Record<string, boolean>>({});
  const [installProgressMap, setInstallProgressMap] = useState<Record<string, { percentage: number; status: string }>>({});
  
  const [isCheckoutOpen, setIsCheckoutOpen] = useState(false);
  const [isAccountModalOpen, setIsAccountModalOpen] = useState(false);
  const [isUpdateModalOpen, setIsUpdateModalOpen] = useState(false);
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

  const [customGamesDir, setCustomGamesDir] = useState<string>(
    localStorage.getItem('ankhvault_games_dir') || ''
  );

  // Check installed games
  useEffect(() => {
    if (!isTauriAvailable()) return;

    catalog.forEach((game) => {
      invoke<boolean>('check_game_installed', { gameId: game.id, customBaseDir: customGamesDir || null })
        .then((installed) => {
          setInstalledGames((prev) => ({ ...prev, [game.id]: installed }));
        })
        .catch(() => {});
    });
  }, [catalog, userLibrary, customGamesDir]);

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
    return u.trim().length > 0 && p.trim().length > 0;
  };

  const handleRegister = async (u: string, e: string, p: string): Promise<boolean> => {
    let success = true;
    if (isTauriAvailable()) {
      success = await invoke<boolean>('register_user', { username: u, email: e, password: p });
    }
    if (success) {
      // Sync user profile to Supabase database so accounts persist!
      await registerUserProfileSupabase(u, e);
    }
    return success;
  };

  const handleLoginSuccess = (username: string) => {
    setCurrentUser(username);
    showToast(`Welcome back to Ankhvault, ${username}!`);
  };

  const handleLogout = () => {
    setCurrentUser(null);
    setUserCart([]);
    setUserLibrary([]);
    setIsAccountModalOpen(false);
    showToast('Logged out successfully.');
  };

  // Account Management Handlers
  const handleChangeUsername = async (newUsername: string): Promise<{ success: boolean; message: string }> => {
    if (!currentUser) return { success: false, message: 'Not logged in' };

    let success = true;
    if (isTauriAvailable()) {
      success = await invoke<boolean>('change_username', { oldUsername: currentUser, newUsername });
    }

    if (!success) {
      return { success: false, message: 'Username is already taken' };
    }

    // Sync username update to Supabase Cloud
    await updateUsernameSupabase(currentUser, newUsername);
    setCurrentUser(newUsername);
    showToast(`Username changed to ${newUsername}!`);
    return { success: true, message: 'Username updated' };
  };

  const handleChangePassword = async (newPassword: string): Promise<{ success: boolean; message: string }> => {
    if (!currentUser) return { success: false, message: 'Not logged in' };

    let success = true;
    if (isTauriAvailable()) {
      success = await invoke<boolean>('change_password', { username: currentUser, newPassword });
    }

    if (success) {
      showToast('Password updated successfully!');
      return { success: true, message: 'Password updated' };
    }
    return { success: false, message: 'Password update failed' };
  };

  const handleDeleteAccount = async () => {
    if (!currentUser) return;

    if (isTauriAvailable()) {
      await invoke('delete_user', { username: currentUser });
    }

    // Delete user profile & library from Supabase Cloud
    await deleteUserProfileSupabase(currentUser);

    showToast('Account permanently deleted.');
    handleLogout();
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
        await invoke('download_and_install_game', {
          gameId,
          downloadUrl: url,
          customBaseDir: customGamesDir || null,
        });
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
        await invoke('launch_game_executable', { gameId, customBaseDir: customGamesDir || null });
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
            onOpenAccountModal={() => setIsAccountModalOpen(true)}
            onOpenUpdateModal={() => setIsUpdateModalOpen(true)}
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

          {/* MoonPay Commerce Checkout Modal */}
          <MoonPayCheckoutModal
            isOpen={isCheckoutOpen}
            onClose={() => setIsCheckoutOpen(false)}
            totalAmount={cartTotal}
            devWalletSol={catalog.find((g) => userCart.includes(g.id))?.dev_wallet_sol}
            devWalletEvm={catalog.find((g) => userCart.includes(g.id))?.dev_wallet_evm}
            onPaymentSuccess={handlePaymentSuccess}
          />

          {/* Account Management Settings Modal */}
          <AccountModal
            isOpen={isAccountModalOpen}
            onClose={() => setIsAccountModalOpen(false)}
            currentUsername={currentUser}
            onChangeUsername={handleChangeUsername}
            onChangePassword={handleChangePassword}
            onDeleteAccount={handleDeleteAccount}
          />

          {/* In-App Auto-Update Modal */}
          <UpdateModal
            isOpen={isUpdateModalOpen}
            onClose={() => setIsUpdateModalOpen(false)}
          />
        </>
      )}
    </div>
  );
};
