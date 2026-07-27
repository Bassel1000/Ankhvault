use aes_gcm::{
    aead::{Aead, KeyInit},
    Aes256Gcm, Nonce,
};
use base64::{engine::general_purpose::STANDARD as BASE64, Engine as _};
use rand::RngCore;
use serde::{Deserialize, Serialize};
use sha2::{Digest, Sha256};
use std::collections::{HashMap, HashSet};
use std::fs::{self, File};
use std::io::{Read, Write};
use std::path::{Path, PathBuf};
use std::sync::{Arc, Mutex};

// ---------------------------------------------------------------------------
// Encryption key management
// ---------------------------------------------------------------------------

/// Name of the per-device key file stored alongside the database.
const KEY_FILE_NAME: &str = "ankh.key";

/// Legacy hardcoded key used by installations prior to v1.1.0.
/// Kept ONLY for migrating existing databases on first launch after upgrade.
/// It is never used for new encryption operations.
const LEGACY_KEY: &[u8; 32] = b"AnkhvaultEnterpriseDBKey256Bit!!";

/// Load the 32-byte encryption key from `db_dir/ankh.key`, generating and
/// persisting a fresh random key if the file does not yet exist.
fn load_or_create_key(db_dir: &Path) -> [u8; 32] {
    let key_path = db_dir.join(KEY_FILE_NAME);
    if key_path.exists() {
        if let Ok(mut f) = File::open(&key_path) {
            let mut key = [0u8; 32];
            if f.read_exact(&mut key).is_ok() {
                return key;
            }
        }
    }
    generate_and_persist_key(&key_path)
}

fn generate_and_persist_key(key_path: &Path) -> [u8; 32] {
    let mut key = [0u8; 32];
    rand::thread_rng().fill_bytes(&mut key);
    if let Ok(mut f) = File::create(key_path) {
        f.write_all(&key).ok();
    }
    key
}

/// Attempt to decrypt `db_path` using `key`. Returns `Ok(DatabaseState)` on
/// success, `Err(String)` on any failure.
fn try_decrypt_with_key(db_path: &Path, key: &[u8; 32]) -> Result<DatabaseState, String> {
    let mut file = File::open(db_path).map_err(|e| e.to_string())?;
    let mut buffer = Vec::new();
    file.read_to_end(&mut buffer).map_err(|e| e.to_string())?;
    if buffer.len() < 12 {
        return Err("Database file too small or corrupt".to_string());
    }
    let (nonce_bytes, ciphertext) = buffer.split_at(12);
    let cipher = Aes256Gcm::new(key.into());
    let nonce = Nonce::from_slice(nonce_bytes);
    let plaintext = cipher
        .decrypt(nonce, ciphertext)
        .map_err(|e| format!("Decryption error: {:?}", e))?;
    serde_json::from_slice(&plaintext).map_err(|e| e.to_string())
}

// ---------------------------------------------------------------------------
// Data types
// ---------------------------------------------------------------------------

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct UserAccount {
    pub username: String,
    pub email: String,
    pub password_hash: String,
    pub salt: String, // Base64-encoded salt
}

#[derive(Debug, Clone, Serialize, Deserialize, Default)]
pub struct DatabaseState {
    pub users: HashMap<String, UserAccount>,
    pub user_libraries: HashMap<String, HashSet<String>>,
    pub user_carts: HashMap<String, HashSet<String>>,
}

// ---------------------------------------------------------------------------
// DatabaseManager
// ---------------------------------------------------------------------------

#[derive(Clone)]
pub struct DatabaseManager {
    state: Arc<Mutex<DatabaseState>>,
    db_file_path: PathBuf,
    /// Per-device AES-256-GCM encryption key loaded from ankh.key.
    encryption_key: [u8; 32],
}

impl DatabaseManager {
    pub fn new() -> Self {
        let db_dir = dirs::data_local_dir()
            .unwrap_or_else(|| PathBuf::from("."))
            .join("Ankhvault");
        fs::create_dir_all(&db_dir).ok();

        let db_file_path = db_dir.join("ankh_database.db");

        // Load (or generate) the per-device key. This will create ankh.key on
        // first run or any time the file is missing.
        let encryption_key = load_or_create_key(&db_dir);

        let mut db = DatabaseManager {
            state: Arc::new(Mutex::new(DatabaseState::default())),
            db_file_path: db_file_path.clone(),
            encryption_key,
        };

        if db_file_path.exists() {
            // First, attempt to decrypt with the per-device key (normal path).
            if let Ok(loaded) = try_decrypt_with_key(&db_file_path, &db.encryption_key) {
                *db.state.lock().unwrap() = loaded;
            } else {
                // Key mismatch — this is a pre-v1.1.0 database using the legacy
                // hardcoded key. Migrate it: decrypt with the old key and
                // immediately re-encrypt with the new per-device key.
                if let Ok(loaded) = try_decrypt_with_key(&db_file_path, LEGACY_KEY) {
                    *db.state.lock().unwrap() = loaded;
                    db.save_encrypted_database().ok();
                } else {
                    // Database unreadable; start fresh rather than crash.
                    db.seed_defaults();
                    db.save_encrypted_database().ok();
                }
            }
        } else {
            db.seed_defaults();
            db.save_encrypted_database().ok();
        }

        db
    }

    fn seed_defaults(&mut self) {
        // Dynamic initialization on first run — no hardcoded default accounts.
    }

    // ---------------------------------------------------------------------------
    // Password utilities
    // ---------------------------------------------------------------------------

    pub fn generate_salt() -> Vec<u8> {
        let mut salt = vec![0u8; 16];
        rand::thread_rng().fill_bytes(&mut salt);
        salt
    }

    pub fn hash_password(password: &str, salt: &[u8]) -> String {
        let mut hasher = Sha256::new();
        hasher.update(salt);
        hasher.update(password.as_bytes());
        BASE64.encode(hasher.finalize())
    }

    // ---------------------------------------------------------------------------
    // User management
    // ---------------------------------------------------------------------------

    pub fn register_user(&self, username: &str, email: &str, plain_password: &str) -> Result<bool, String> {
        let lower_user = username.trim().to_lowercase();
        let mut state = self.state.lock().map_err(|e| e.to_string())?;

        if state.users.contains_key(&lower_user) {
            return Ok(false);
        }

        let salt = Self::generate_salt();
        let password_hash = Self::hash_password(plain_password, &salt);

        let account = UserAccount {
            username: username.trim().to_string(),
            email: email.trim().to_string(),
            password_hash,
            salt: BASE64.encode(&salt),
        };

        state.users.insert(lower_user.clone(), account);
        state.user_libraries.entry(lower_user.clone()).or_default();
        state.user_carts.entry(lower_user).or_default();

        drop(state);
        self.save_encrypted_database()?;
        Ok(true)
    }

    pub fn authenticate_user(&self, username: &str, plain_password: &str) -> Result<bool, String> {
        let lower_user = username.trim().to_lowercase();
        let state = self.state.lock().map_err(|e| e.to_string())?;

        if let Some(account) = state.users.get(&lower_user) {
            let salt_bytes = BASE64.decode(&account.salt).map_err(|e| e.to_string())?;
            let computed_hash = Self::hash_password(plain_password, &salt_bytes);
            Ok(computed_hash == account.password_hash)
        } else {
            Ok(false)
        }
    }

    pub fn change_username(&self, old_username: &str, new_username: &str) -> Result<bool, String> {
        let old_lower = old_username.trim().to_lowercase();
        let new_lower = new_username.trim().to_lowercase();

        if old_lower == new_lower {
            return Ok(true);
        }

        let mut state = self.state.lock().map_err(|e| e.to_string())?;
        if state.users.contains_key(&new_lower) {
            return Ok(false);
        }

        if let Some(mut account) = state.users.remove(&old_lower) {
            account.username = new_username.trim().to_string();
            state.users.insert(new_lower.clone(), account);

            if let Some(lib) = state.user_libraries.remove(&old_lower) {
                state.user_libraries.insert(new_lower.clone(), lib);
            }
            if let Some(cart) = state.user_carts.remove(&old_lower) {
                state.user_carts.insert(new_lower, cart);
            }

            drop(state);
            self.save_encrypted_database()?;
            Ok(true)
        } else {
            Ok(false)
        }
    }

    pub fn change_password(&self, username: &str, new_plain_password: &str) -> Result<bool, String> {
        let lower_user = username.trim().to_lowercase();
        let mut state = self.state.lock().map_err(|e| e.to_string())?;

        if let Some(account) = state.users.get_mut(&lower_user) {
            let salt = Self::generate_salt();
            account.password_hash = Self::hash_password(new_plain_password, &salt);
            account.salt = BASE64.encode(&salt);
            drop(state);
            self.save_encrypted_database()?;
            Ok(true)
        } else {
            Ok(false)
        }
    }

    pub fn delete_user(&self, username: &str) -> Result<bool, String> {
        let lower_user = username.trim().to_lowercase();
        let mut state = self.state.lock().map_err(|e| e.to_string())?;

        state.users.remove(&lower_user);
        state.user_libraries.remove(&lower_user);
        state.user_carts.remove(&lower_user);

        drop(state);
        self.save_encrypted_database()?;
        Ok(true)
    }

    // ---------------------------------------------------------------------------
    // Library & cart
    // ---------------------------------------------------------------------------

    pub fn get_user_library(&self, username: &str) -> Vec<String> {
        let lower_user = username.trim().to_lowercase();
        let state = self.state.lock().unwrap();
        state
            .user_libraries
            .get(&lower_user)
            .map(|set| set.iter().cloned().collect())
            .unwrap_or_default()
    }

    pub fn get_user_cart(&self, username: &str) -> Vec<String> {
        let lower_user = username.trim().to_lowercase();
        let state = self.state.lock().unwrap();
        state
            .user_carts
            .get(&lower_user)
            .map(|set| set.iter().cloned().collect())
            .unwrap_or_default()
    }

    pub fn add_to_cart(&self, username: &str, game_id: &str) -> Result<(), String> {
        let lower_user = username.trim().to_lowercase();
        let mut state = self.state.lock().map_err(|e| e.to_string())?;
        state.user_carts.entry(lower_user).or_default().insert(game_id.to_string());
        drop(state);
        self.save_encrypted_database()
    }

    pub fn remove_from_cart(&self, username: &str, game_id: &str) -> Result<(), String> {
        let lower_user = username.trim().to_lowercase();
        let mut state = self.state.lock().map_err(|e| e.to_string())?;
        if let Some(cart) = state.user_carts.get_mut(&lower_user) {
            cart.remove(game_id);
        }
        drop(state);
        self.save_encrypted_database()
    }

    pub fn clear_cart(&self, username: &str) -> Result<(), String> {
        let lower_user = username.trim().to_lowercase();
        let mut state = self.state.lock().map_err(|e| e.to_string())?;
        if let Some(cart) = state.user_carts.get_mut(&lower_user) {
            cart.clear();
        }
        drop(state);
        self.save_encrypted_database()
    }

    pub fn add_to_library(&self, username: &str, game_ids: &[String]) -> Result<(), String> {
        let lower_user = username.trim().to_lowercase();
        let mut state = self.state.lock().map_err(|e| e.to_string())?;
        let lib = state.user_libraries.entry(lower_user.clone()).or_default();
        for id in game_ids {
            lib.insert(id.clone());
        }
        if let Some(cart) = state.user_carts.get_mut(&lower_user) {
            for id in game_ids {
                cart.remove(id);
            }
        }
        drop(state);
        self.save_encrypted_database()
    }

    // ---------------------------------------------------------------------------
    // Encrypted persistence (AES-256-GCM)
    // ---------------------------------------------------------------------------

    fn save_encrypted_database(&self) -> Result<(), String> {
        let state = self.state.lock().map_err(|e| e.to_string())?;
        let json_bytes = serde_json::to_vec(&*state).map_err(|e| e.to_string())?;

        let cipher = Aes256Gcm::new(&self.encryption_key.into());
        let mut nonce_bytes = [0u8; 12];
        rand::thread_rng().fill_bytes(&mut nonce_bytes);
        let nonce = Nonce::from_slice(&nonce_bytes);

        let ciphertext = cipher
            .encrypt(nonce, json_bytes.as_ref())
            .map_err(|e| format!("Encryption error: {:?}", e))?;

        let mut file = File::create(&self.db_file_path).map_err(|e| e.to_string())?;
        file.write_all(&nonce_bytes).map_err(|e| e.to_string())?;
        file.write_all(&ciphertext).map_err(|e| e.to_string())?;
        Ok(())
    }
}
