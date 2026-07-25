import React, { useState, useEffect } from 'react';
import { Download, RefreshCw, X, CheckCircle, AlertCircle } from 'lucide-react';
import { check, Update } from '@tauri-apps/plugin-updater';
import { relaunch } from '@tauri-apps/plugin-process';

interface UpdateModalProps {
  isOpen: boolean;
  onClose: () => void;
  autoCheckOnMount?: boolean;
}

export const UpdateModal: React.FC<UpdateModalProps> = ({ isOpen, onClose, autoCheckOnMount = false }) => {
  const [checking, setChecking] = useState<boolean>(false);
  const [updateAvailable, setUpdateAvailable] = useState<Update | null>(null);
  const [statusMessage, setStatusMessage] = useState<string>('');
  const [progress, setProgress] = useState<number>(0);
  const [downloading, setDownloading] = useState<boolean>(false);
  const [installed, setInstalled] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  const checkForUpdates = async () => {
    setChecking(true);
    setError(null);
    setStatusMessage('Checking for app updates...');

    try {
      const update = await check();
      if (update && update.available) {
        setUpdateAvailable(update);
        setStatusMessage(`Version ${update.version} is available!`);
      } else {
        setUpdateAvailable(null);
        setStatusMessage('Your launcher is up to date.');
      }
    } catch (err: any) {
      console.warn('Update check failed or running outside Tauri:', err);
      setError(err?.message || 'Unable to connect to release update server.');
      setStatusMessage('');
    } finally {
      setChecking(false);
    }
  };

  useEffect(() => {
    if (isOpen || autoCheckOnMount) {
      checkForUpdates();
    }
  }, [isOpen]);

  const handleInstallUpdate = async () => {
    if (!updateAvailable) return;
    setDownloading(true);
    setError(null);
    setStatusMessage('Downloading & verifying update package...');

    try {
      let downloaded = 0;
      let contentLength = 0;

      await updateAvailable.downloadAndInstall((event) => {
        switch (event.event) {
          case 'Started':
            contentLength = event.data.contentLength || 0;
            break;
          case 'Progress':
            downloaded += event.data.chunkLength;
            if (contentLength > 0) {
              setProgress(Math.round((downloaded / contentLength) * 100));
            }
            break;
          case 'Finished':
            setStatusMessage('Installation complete! Restarting launcher...');
            setInstalled(true);
            break;
        }
      });

      setStatusMessage('Update installed successfully! Restarting...');
      setTimeout(async () => {
        await relaunch();
      }, 1500);
    } catch (err: any) {
      console.error('Failed to install update:', err);
      setError(err?.message || 'Failed to download and install update.');
      setDownloading(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div
      style={{
        position: 'fixed',
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        backgroundColor: 'rgba(5, 7, 10, 0.85)',
        backdropFilter: 'blur(8px)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        zIndex: 1000,
        padding: '20px',
      }}
    >
      <div
        className="glass-panel"
        style={{
          width: '100%',
          maxWidth: '520px',
          padding: '28px',
          borderRadius: '16px',
          position: 'relative',
          border: '1px solid var(--accent-gold)',
          boxShadow: '0 0 40px rgba(212, 175, 55, 0.2)',
        }}
      >
        <button
          onClick={onClose}
          disabled={downloading}
          style={{
            position: 'absolute',
            top: '20px',
            right: '20px',
            background: 'none',
            border: 'none',
            color: 'var(--text-muted)',
            cursor: downloading ? 'not-allowed' : 'pointer',
          }}
        >
          <X size={20} />
        </button>

        <div style={{ display: 'flex', alignItems: 'center', gap: '14px', marginBottom: '20px' }}>
          <div
            style={{
              padding: '10px',
              borderRadius: '12px',
              backgroundColor: 'rgba(212, 175, 55, 0.15)',
              color: 'var(--accent-gold)',
            }}
          >
            <RefreshCw size={24} className={checking ? 'animate-spin' : ''} />
          </div>
          <div>
            <h2 style={{ fontFamily: 'var(--font-cinzel)', color: 'var(--accent-gold)', fontSize: '1.25rem', fontWeight: 700 }}>
              Software Updates
            </h2>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.82rem' }}>
              Ankhvault Launcher Auto-Updater
            </p>
          </div>
        </div>

        {checking && (
          <div style={{ textAlign: 'center', padding: '30px 0' }}>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.95rem' }}>{statusMessage}</p>
          </div>
        )}

        {!checking && error && (
          <div
            style={{
              backgroundColor: 'rgba(239, 68, 68, 0.1)',
              border: '1px solid #ef4444',
              borderRadius: '10px',
              padding: '16px',
              marginBottom: '20px',
              display: 'flex',
              alignItems: 'flex-start',
              gap: '12px',
            }}
          >
            <AlertCircle size={20} color="#ef4444" style={{ flexShrink: 0, marginTop: '2px' }} />
            <div>
              <p style={{ color: '#ef4444', fontWeight: 600, fontSize: '0.9rem', marginBottom: '4px' }}>
                Update Check Notice
              </p>
              <p style={{ color: 'var(--text-secondary)', fontSize: '0.82rem' }}>{error}</p>
            </div>
          </div>
        )}

        {!checking && !updateAvailable && !error && (
          <div style={{ textAlign: 'center', padding: '24px 0' }}>
            <CheckCircle size={48} color="var(--accent-gold)" style={{ margin: '0 auto 14px' }} />
            <h3 style={{ color: 'var(--text-primary)', fontSize: '1.1rem', fontWeight: 700, marginBottom: '6px' }}>
              You are up to date!
            </h3>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>
              You are running the latest build of Ankhvault Launcher.
            </p>
          </div>
        )}

        {!checking && updateAvailable && (
          <div>
            <div
              style={{
                backgroundColor: 'rgba(212, 175, 55, 0.08)',
                border: '1px solid rgba(212, 175, 55, 0.3)',
                borderRadius: '12px',
                padding: '16px',
                marginBottom: '20px',
              }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '10px' }}>
                <span style={{ color: 'var(--accent-gold)', fontWeight: 700, fontSize: '1rem' }}>
                  Version {updateAvailable.version} Available
                </span>
                <span
                  style={{
                    backgroundColor: 'var(--accent-gold)',
                    color: '#0b0e14',
                    fontSize: '0.72rem',
                    fontWeight: 800,
                    padding: '2px 8px',
                    borderRadius: '6px',
                  }}
                >
                  NEW RELEASE
                </span>
              </div>
              {updateAvailable.body && (
                <div
                  style={{
                    color: 'var(--text-secondary)',
                    fontSize: '0.84rem',
                    maxHeight: '120px',
                    overflowY: 'auto',
                    whiteSpace: 'pre-line',
                    lineHeight: '1.5',
                  }}
                >
                  {updateAvailable.body}
                </div>
              )}
            </div>

            {downloading && (
              <div style={{ marginBottom: '20px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.82rem', marginBottom: '6px' }}>
                  <span style={{ color: 'var(--text-secondary)' }}>{statusMessage}</span>
                  <span style={{ color: 'var(--accent-gold)', fontWeight: 700 }}>{progress}%</span>
                </div>
                <div
                  style={{
                    height: '8px',
                    width: '100%',
                    backgroundColor: 'rgba(255, 255, 255, 0.1)',
                    borderRadius: '4px',
                    overflow: 'hidden',
                  }}
                >
                  <div
                    style={{
                      height: '100%',
                      width: `${progress}%`,
                      backgroundColor: 'var(--accent-gold)',
                      transition: 'width 0.3s ease',
                    }}
                  />
                </div>
              </div>
            )}
          </div>
        )}

        <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', marginTop: '20px' }}>
          <button
            onClick={checkForUpdates}
            disabled={checking || downloading}
            className="btn-outline"
            style={{ padding: '9px 16px', fontSize: '0.85rem', display: 'flex', alignItems: 'center', gap: '6px' }}
          >
            <RefreshCw size={14} className={checking ? 'animate-spin' : ''} />
            Re-check
          </button>

          {updateAvailable && !installed && (
            <button
              onClick={handleInstallUpdate}
              disabled={downloading}
              className="btn-primary"
              style={{ padding: '9px 20px', fontSize: '0.85rem', display: 'flex', alignItems: 'center', gap: '8px' }}
            >
              <Download size={16} />
              {downloading ? 'Downloading...' : 'Update & Restart'}
            </button>
          )}

          {(!updateAvailable || installed) && (
            <button onClick={onClose} className="btn-outline" style={{ padding: '9px 20px', fontSize: '0.85rem' }}>
              Close
            </button>
          )}
        </div>
      </div>
    </div>
  );
};
