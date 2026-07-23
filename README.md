<div align="center">

  <img src="assets/Ankhvault%20Logo.png" alt="Ankhvault Logo" width="180" />
  
  # ☥ Ankhvault
  ### Next-Generation Desktop Gaming Launcher & Store
  *A flagship product by **Duat Studio***

  [![Java](https://img.shields.io/badge/Java-26-orange.svg)](https://adoptium.net/)
  [![UI](https://img.shields.io/badge/UI-Pure%20Java%20Swing-blue.svg)]()
  [![Security](https://img.shields.io/badge/Security-AES--256%20%2B%20SHA--256-green.svg)]()
  [![Web3](https://img.shields.io/badge/Payments-USDT%20%7C%20USDC%20%7C%20USDG-purple.svg)]()
  [![Company](https://img.shields.io/badge/Studio-Duat%20Studio-gold.svg)]()
  [![License](https://img.shields.io/badge/License-MIT-green.svg)]()

</div>

---

## 🏛️ About Ankhvault

**Ankhvault** (by **Duat Studio**) is a state-of-the-art desktop gaming launcher, store, and game platform built with **Java 26** and **Pure Java Swing**. Designed with a mythic, quiet, guardian-of-the-vault aesthetic, Ankhvault combines ultra-modern dark glassmorphism styling with high-performance vector graphics, an **AES-256 encrypted persistent database**, a **USD Stablecoin Crypto x402 Web3 Payment Gateway**, and 5 custom built-in mini-games.

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

- ⚡ **Java 26 Platform Performance**:
  - **Generational ZGC**: Sub-millisecond GC pause times (<1ms) ensuring smooth 60 FPS repaints across all client windows, games, and UI animations.
  - **Compact Object Headers**: Up to 50% lower heap memory overhead when managing store cards, asset caches, and inventory grids.
  - **Modern Language & Vector Interop**: Flexible constructor bodies, pattern matching, and enhanced Java2D antialiased rendering.

- 🔒 **Enterprise-Grade Vault Security (`DatabaseManager.java`)**:
  - **SHA-256 Cryptographic Salted Hashing**: User credentials are cryptographically secured. Each account receives a unique 128-bit salt and SHA-256 hash.
  - **AES-256 CBC Binary Database Encryption**: Database contents (`nexa_database.db` / `ankh_database.db`) are fully encrypted using 256-bit AES encryption with random Initialization Vectors (IV).

- 🎨 **Pure Java Custom Design Engine**:
  - **Antialiased Graphics**: Global 2D rendering hints for sharp vector curves, smooth text, and responsive UI components.
  - **Modern Glassmorphism**: High-contrast dark theme with electric gold accents, scarab turquoise highlights, and custom dark scrollbars (`ModernScrollBarUI`).
  - **Custom Component Architecture**: `ModernButton` (hover/press glow), `NavItem` (sidebar tabs with active gold indicator bars), `ResponsiveWrapGrid` (responsive store layout), and `ToastManager` (overlay notifications).

- 🛒 **Store Cart & USD Stablecoin x402 Payment Gateway**:
  - **Game Catalog Pricing**: Free-to-play titles (`$0.00`) and premium games (`$4.99`, `$9.99`, `$14.99`).
  - **Live Store Cart**: Interactive cart tab (`🛒 Cart (X)`) with dynamic subtotal calculations and badge counter.
  - **USD Stablecoin x402 Web3 Checkout**: Built-in payment modal supporting **USDT (Tether)**, **USDC (USD Coin)**, and **USDG (Global Dollar)** with 1:1 USD settlement and instant wallet address copying.

- 🚀 **Production Game Downloader & Launch Engine (`GameInstaller.java`)**:
  - **Automated Package Extraction**: Downloads compressed game build archives over HTTPS/CDN directly into `%LOCALAPPDATA%/Ankhvault/Games/`.
  - **Native Process Execution**: Discovers executable binaries (`.exe` / `.bat`) and boots game processes with isolated working directories via `ProcessBuilder`.
  - **Store Catalog**: Features flagship titles including *Shadow of Kemet*, *Nomad Racer*, *Aegis Protocol*, *Chronicles of Duat*, and *Neon Odyssey*.

---

## 🛠️ Build & Running Instructions

### Requirements
- **Java JDK 26** or later (e.g., Eclipse Adoptium Temurin JDK 26).

### 1. Compile the Project
Open a terminal in the `Ankhvault` project root directory and compile all Java source files into `./bin`:

```bash
# Windows PowerShell / Command Prompt
& "C:\Program Files\Eclipse Adoptium\jdk-26.0.1.8-hotspot\bin\javac.exe" -d ./bin App/*.java App/Snake/code/*.java
```

### 2. Launch the Application
Run the `Main` class to launch the Ankhvault launcher:

```bash
# Windows PowerShell
& "C:\Program Files\Eclipse Adoptium\jdk-26.0.1.8-hotspot\bin\java.exe" -cp ./bin Main
```

*(Default sign-in credentials: `admin` / `admin123`, `demo` / `demo`, `guest` / `guest123`, or register a new account through the launcher UI!)*

---

## 🏢 Organization

**Duat Studio**  
*Building next-generation gaming experiences and secure client software.*

---

## 📜 License
Distributed under the **MIT License**. See `LICENSE` for details.
