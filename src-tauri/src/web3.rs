use serde::{Deserialize, Serialize};

pub const TREASURY_WALLET_SOL: &str = "2JxDavSJ9de1twMxDxdqz1sV3vkpYVDpPbx8qTy6q2cS";
pub const TREASURY_WALLET_EVM: &str = "0x5efe4d14d5f406bb748fab14a3e2cda06e51a986";

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct VerificationResult {
    pub success: bool,
    pub message: String,
    pub tx_hash: String,
    pub amount_verified: f64,
}

pub async fn verify_x402_demo_transaction(
    tx_hash: &str,
    _token_symbol: &str,
    expected_amount_usd: f64,
) -> VerificationResult {
    let clean_hash = tx_hash.trim();

    let final_hash = if clean_hash.is_empty() {
        format!("demo_x402_{}", std::time::SystemTime::now()
            .duration_since(std::time::UNIX_EPOCH)
            .unwrap_or_default()
            .as_millis())
    } else {
        clean_hash.to_string()
    };

    VerificationResult {
        success: true,
        message: format!("x402 Demo Payment '{}' Verified Successfully!", final_hash),
        tx_hash: final_hash,
        amount_verified: expected_amount_usd,
    }
}
