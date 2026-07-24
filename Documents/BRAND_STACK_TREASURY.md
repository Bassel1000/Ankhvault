# 🏛️ Duat Studio & Ankhvault Documentation

Comprehensive specification for **Ankhvault** Brand Identity, Technology Stack, and Official Web3 Treasury Vaults.

---

## 🎨 1. Brand Identity & Design System

Ankhvault features a custom dual-palette design system tailored for high-DPI modern displays and GPU-accelerated dark glassmorphism.

### Brand Levels & Palette Matrix

| Brand Level | Primary Accent | Secondary Accent | Background & Base Palette | Brand Vibe & Aesthetics |
| :--- | :--- | :--- | :--- | :--- |
| **Duat Studio** *(Company)* | **Antique Gold (`#C9A227`)** | **Nile Teal (`#0E7C86`)** | Obsidian (`#0B0E11`), Night Indigo (`#1B1F3B`), Papyrus (`#F0E6D2`) | Mythic, ancient-futuristic, vault guardian |
| **Ankhvault** *(Product)* | **Electric Gold (`#D4AF37`)** | **Scarab Turquoise (`#17919E`)** | Charcoal Navy (`#0B0E14`), Emerald (`#10B981`), Sandstone (`#EDE6D6`) | High-tech vault key, modern gaming client, glassmorphism |

### UI Design Tokens & Tokens (`src/styles/glassmorphism.css`)
- **Root Background**: `#0b0e14`
- **Elevated Translucent Panels**: `rgba(17, 22, 31, 0.75)` with `backdrop-filter: blur(16px)`
- **Interactive Card Hover**: `rgba(30, 38, 54, 0.85)` with gold ambient glow (`0 12px 40px rgba(212, 175, 55, 0.25)`)
- **Border Stroke**: `rgba(212, 175, 55, 0.18)`
- **Typography**: `Cinzel` (Headers & Brand Title) + `Inter` (UI Body & Controls)

---

## ⚡ 2. Technology Stack

### Desktop Client Application
- **Core Engine & System Interop**: **Rust + Tauri 2.0**
  - Ultra-lightweight footprint (~40–70 MB RAM while gaming).
  - Native OS process execution (`std::process::Command`), sandboxing, and executable permission control.
- **Frontend User Interface**: **React 18 + Vite 6 + TypeScript**
  - High-performance, GPU-accelerated component architecture.
  - Live catalog filtering, reactive shopping cart, dynamic installing progress states.
- **Vault Security & Local Persistence (`src-tauri/src/db.rs`)**:
  - **AES-256-GCM Binary Data Encryption**: Database contents (`ankh_database.db`) are encrypted with 256-bit AES encryption.
  - **SHA-256 Salted Password Hashing**: Unique 128-bit cryptographic salt generated per user account.
- **Game Installation Engine (`src-tauri/src/installer.rs`)**:
  - Multithreaded HTTPS package downloader with progress event streaming (`download-progress`).
  - Automated ZIP archive extraction (`zip` crate) into `%LOCALAPPDATA%/Ankhvault/Games/`.

### Cloud Backend API & Web Portal
- **Server Runtime**: **Node.js (>=18.0.0)** & **Express.js** ([server.js](file:///D:/Duat%20Studios/server.js))
- **CORS Support**: Cross-origin REST API endpoints for store catalog, developer portal, and build metadata.

---

## 💳 3. Web3 Treasury Vaults & Payment Gateway

Ankhvault implements a **USD Stablecoin x402 Payment Gateway** providing 1:1 USD settlement for premium game catalog purchases.

### Supported Payment Tokens
- **USDT (Tether USD)**
- **USDC (USD Coin)**

### Official Duat Studio Platform Treasury Vaults
- **Solana Network (SPL Tokens)**:
  `2JxDavSJ9de1twMxDxdqz1sV3vkpYVDpPbx8qTy6q2cS`
- **Base / EVM Network (ERC-20 Tokens)**:
  `0x5efe4d14d5f406bb748fab14a3e2cda06e51a986`

### Verification Engine (`src-tauri/src/web3.rs`)
- On-chain transaction signature verification via Solana Mainnet RPC (`https://api.mainnet-beta.solana.com`) and Base L2 RPC (`https://mainnet.base.org`).
