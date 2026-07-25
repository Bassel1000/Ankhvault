use serde::{Deserialize, Serialize};

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct GameItem {
    pub id: String,
    pub title: String,
    pub category: String,
    pub price: f64,
    pub rating: f64,
    pub description: String,
    pub tag: String,
    pub download_url: String,
    pub dev_wallet_sol: Option<String>,
    pub dev_wallet_evm: Option<String>,
}

pub fn get_default_catalog() -> Vec<GameItem> {
    Vec::new()
}
