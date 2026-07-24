pub mod catalog;
pub mod db;
pub mod installer;
pub mod web3;

use catalog::{get_default_catalog, GameItem};
use db::DatabaseManager;
use tauri::{AppHandle, State};
use web3::VerificationResult;

struct AppState {
    db: DatabaseManager,
}

#[tauri::command]
fn register_user(state: State<'_, AppState>, username: String, email: String, password: String) -> Result<bool, String> {
    state.db.register_user(&username, &email, &password)
}

#[tauri::command]
fn authenticate_user(state: State<'_, AppState>, username: String, password: String) -> Result<bool, String> {
    state.db.authenticate_user(&username, &password)
}

#[tauri::command]
fn get_catalog() -> Vec<GameItem> {
    get_default_catalog()
}

#[tauri::command]
fn get_user_library(state: State<'_, AppState>, username: String) -> Vec<String> {
    state.db.get_user_library(&username)
}

#[tauri::command]
fn get_user_cart(state: State<'_, AppState>, username: String) -> Vec<String> {
    state.db.get_user_cart(&username)
}

#[tauri::command]
fn add_to_cart(state: State<'_, AppState>, username: String, game_id: String) -> Result<(), String> {
    state.db.add_to_cart(&username, &game_id)
}

#[tauri::command]
fn remove_from_cart(state: State<'_, AppState>, username: String, game_id: String) -> Result<(), String> {
    state.db.remove_from_cart(&username, &game_id)
}

#[tauri::command]
fn clear_cart(state: State<'_, AppState>, username: String) -> Result<(), String> {
    state.db.clear_cart(&username)
}

#[tauri::command]
fn add_to_library(state: State<'_, AppState>, username: String, game_ids: Vec<String>) -> Result<(), String> {
    state.db.add_to_library(&username, &game_ids)
}

#[tauri::command]
async fn verify_x402_payment(tx_hash: String, token_symbol: String, amount_usd: f64) -> VerificationResult {
    web3::verify_on_chain_transaction(&tx_hash, &token_symbol, amount_usd).await
}

#[tauri::command]
fn check_game_installed(game_id: String, custom_base_dir: Option<String>) -> bool {
    installer::check_game_installed(&game_id, custom_base_dir.as_deref())
}

#[tauri::command]
fn launch_game_executable(game_id: String, custom_base_dir: Option<String>) -> Result<bool, String> {
    installer::launch_game_executable(&game_id, custom_base_dir.as_deref())
}

#[tauri::command]
async fn download_and_install_game(
    app: AppHandle,
    game_id: String,
    download_url: String,
    custom_base_dir: Option<String>,
) -> Result<String, String> {
    installer::download_and_install_game(app, game_id, download_url, custom_base_dir).await
}

#[cfg_attr(mobile, tauri::mobile_entry_point)]
pub fn run() {
    let db = DatabaseManager::new();

    tauri::Builder::default()
        .plugin(tauri_plugin_shell::init())
        .manage(AppState { db })
        .invoke_handler(tauri::generate_handler![
            register_user,
            authenticate_user,
            get_catalog,
            get_user_library,
            get_user_cart,
            add_to_cart,
            remove_from_cart,
            clear_cart,
            add_to_library,
            verify_x402_payment,
            check_game_installed,
            launch_game_executable,
            download_and_install_game
        ])
        .run(tauri::generate_context!())
        .expect("error while running Ankhvault Tauri application");
}
