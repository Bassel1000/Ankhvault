<div align="center">

  <img src="assets/Ankhvault%20Logo.png" alt="Ankhvault Logo" width="180" />
  
  # ☥ Ankhvault
  ### Next-Generation Desktop Gaming Launcher & Store
  *A flagship product by **Duat Studio***

  [![Rust](https://img.shields.io/badge/Rust-Tauri%202.0-orange.svg)](https://tauri.app/)
  [![UI](https://img.shields.io/badge/UI-React%20%2B%20CSS%20Glassmorphism-blue.svg)]()
  [![Security](https://img.shields.io/badge/Security-AES--256--GCM%20%2B%20SHA--256-green.svg)]()
  [![Web3](https://img.shields.io/badge/Payments-USDT%20%7C%20USDC-purple.svg)]()
  [![Company](https://img.shields.io/badge/Studio-Duat%20Studio-gold.svg)]()
  [![License](https://img.shields.io/badge/License-MIT-green.svg)]()

</div>

---

## 🏛️ About Ankhvault

**Ankhvault** (by **Duat Studio**) is a state-of-the-art desktop gaming launcher, store, and game platform built with **Rust + Tauri 2.0** and **React CSS Glassmorphism**. Designed with a mythic, quiet, guardian-of-the-vault aesthetic, Ankhvault combines ultra-modern dark glassmorphism styling with high-performance GPU-accelerated graphics, an **AES-256-GCM encrypted persistent database**, a **USD Stablecoin Crypto x402 Web3 Payment Gateway**, automated ZIP package extraction, and native game process management.

<div align="center">
  <img src="assets/Duat%20Studio%20Logo_Compressed.png" alt="Duat Studio Logo" width="320" />
</div>

---

## 🎨 Studio & Product Brand Identity

Ankhvault features a custom dual-palette design system tailored for modern high-DPI displays:

| Brand Level | Primary Accent | Core Palette | Brand Vibe |
| :--- | :--- | :--- | :--- |
| **Duat Studio** *(Company)* | **Antique Gold (`#C9A227`)** | Obsidian (`#0B0E11`), Night Indigo (`#1B1F3B`), Nile Teal (`#0E7C86`), Papyrus (`#F0E6D2`) | Mythic, ancient-futuristic, vault guardian |
| **Ankhvault** *(Product)* | **Electric Gold (`#D4AF37`)** | Charcoal Navy (`#0B0E14`), Scarab Turquoise (`#17919E`), Emerald (`#10B981`), Sandstone (`#EDE6D6`) | High-tech vault key, modern gaming client, glassmorphism |

---

## 🌟 Key Features & Enterprise Security

- ⚡ **Rust + Tauri 2.0 Engine Performance**:
  - **Minimal Memory Overhead**: ~40–80 MB RAM footprint while games are running.
  - **Native OS Execution**: High-speed process management (`std::process::Command`), sandboxing, and platform permission management.

- 🔒 **Enterprise Vault Security (`src-tauri/src/db.rs`)**:
  - **SHA-256 Cryptographic Salted Hashing**: User credentials are cryptographically secured. Each account receives a unique 128-bit salt and SHA-256 hash.
  - **AES-256-GCM Binary Database Encryption**: Database contents are encrypted using 256-bit AES-GCM with unique Initialization Vectors (IV).

- 🎨 **GPU-Accelerated Glassmorphism UI**:
  - **Dark Glassmorphism**: High-contrast dark theme with electric gold accents, scarab turquoise highlights, backdrop blur panels, and smooth CSS transitions.

- 🛒 **Store Cart & USD Stablecoin x402 Payment Gateway**:
  - **Game Catalog Pricing**: Free-to-play titles (`$0.00`) and premium games (`$4.99`, `$9.99`, `$14.99`).
  - **USD Stablecoin x402 Web3 Checkout**: Built-in payment modal supporting **USDT (Tether)** and **USDC (USD Coin)** with 1:1 USD settlement.
  - **Official Treasury Vaults**:
    - **Solana**: `2JxDavSJ9de1twMxDxdqz1sV3vkpYVDpPbx8qTy6q2cS`
    - **EVM (Base L2)**: `0x5efe4d14d5f406bb748fab14a3e2cda06e51a986`

- 🚀 **Production Game Downloader & Launch Engine (`src-tauri/src/installer.rs`)**:
  - **Automated Package Extraction**: Downloads compressed game build archives over HTTPS/CDN directly into `%LOCALAPPDATA%/Ankhvault/Games/`.
  - **Native Process Execution**: Discovers executable binaries (`.exe`, `.bat`, `.sh`, `.appimage`) and boots game processes with isolated working directories.

---

## 🛠️ Build & Running Instructions

### Requirements
- **Rust toolchain** (1.75+)
- **Node.js** (v18+) & **npm**

### Launch Development App
Run the Tauri 2.0 app with hot reloading:

```bash
npm run tauri:dev
```

---

## 🏢 Organization

**Duat Studio**  
*Building next-generation gaming experiences and secure client software.*

---

## 📜 License
Distributed under the **MIT License**. See `LICENSE` for details.
