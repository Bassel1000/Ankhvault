<div align="center">

  <a href="https://github.com/Duat-Studio">
    <img src="assets/Duat%20Studio%20Logo_Compressed.png" alt="Duat Studio Logo" width="340" />
  </a>
  <br /><br />
  <img src="assets/Ankhvault%20Logo.png" alt="Ankhvault Logo" width="160" />
  
  # ☥ Ankhvault
  ### Next-Generation Desktop Gaming Launcher & Store
  *A flagship product by **Duat Studio***

  [![Rust](https://img.shields.io/badge/Rust-Tauri%202.0-orange.svg)](https://tauri.app/)
  [![UI](https://img.shields.io/badge/UI-React%20%2B%20CSS%20Glassmorphism-blue.svg)]()
  [![Security](https://img.shields.io/badge/Security-AES--256--GCM%20%2B%20SHA--256-green.svg)]()
  [![Payments](https://img.shields.io/badge/Payments-x402%20Protocol%20(Demo)-indigo.svg)]()
  [![Company](https://img.shields.io/badge/Studio-Duat%20Studio-gold.svg)]()
  [![License](https://img.shields.io/badge/License-MIT-green.svg)]()

</div>

---

## 🏛️ About Ankhvault

**Ankhvault** (by **Duat Studio**) is a state-of-the-art desktop gaming launcher, store, and game platform built with **Rust + Tauri 2.0** and **React CSS Glassmorphism**. Designed with a mythic, quiet, guardian-of-the-vault aesthetic, Ankhvault combines ultra-modern dark glassmorphism styling with high-performance GPU-accelerated graphics, an **AES-256-GCM encrypted persistent database**, an **x402 Payment Protocol Gateway (Demo)** (supporting USDT and USDC simulated Web3 settlement), automated ZIP package extraction, and native game process management.

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
  - **SHA-256 Cryptographic Salted Hashing**: User credentials are cryptographically secured with unique 128-bit salts and SHA-256 hashes.
  - **AES-256-GCM Binary Database Encryption**: Database contents are encrypted using 256-bit AES-GCM with unique Initialization Vectors (IV).

- 🎨 **GPU-Accelerated Glassmorphism UI**:
  - **Dark Glassmorphism**: High-contrast dark theme with electric gold accents, scarab turquoise highlights, backdrop blur panels, and smooth CSS transitions.

- 💳 **Store Cart & x402 Web3 Payment Protocol (Demo)**:
  - **Game Catalog Pricing**: Free-to-play titles (`$0.00`) and premium games (`$4.99`, `$9.99`, `$14.99`).
  - **x402 Demo Checkout**: Built-in checkout widget supporting **USDT** and **USDC** simulated Web3 payment settlement.
  - **Automated Payouts**: 95% revenue split directly to developers.

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

<div align="center">
  <img src="assets/Duat%20Studio%20Logo_Compressed.png" alt="Duat Studio Logo" width="260" />
  <br /><br />
  <b>Duat Studio</b><br />
  <i>Building next-generation gaming experiences and secure client software.</i>
</div>

---

## 📜 License
Distributed under the **MIT License**. See `LICENSE` for details.
