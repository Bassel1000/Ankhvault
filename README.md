# 🎮 NexaPlay 3.0 — Modern Desktop Gaming Launcher & Store

[![Java](https://img.shields.io/badge/Java-26-orange.svg)](https://adoptium.net/)
[![UI](https://img.shields.io/badge/UI-Pure%20Java%20Swing-blue.svg)]()
[![Security](https://img.shields.io/badge/Security-AES--256%20%2B%20SHA--256-green.svg)]()
[![Web3](https://img.shields.io/badge/Payments-Crypto%20x402-purple.svg)]()
[![License](https://img.shields.io/badge/License-MIT-green.svg)]()

**NexaPlay** is a state-of-the-art desktop gaming launcher, store, and game platform built in **Java 26** and **Pure Java Swing**. Inspired by modern gaming client platforms such as Steam, Epic Games Store, and GOG Galaxy, NexaPlay features custom antialiased graphics rendering, Generational ZGC zero-stutter performance, dark glassmorphism aesthetics, a Store Cart and **Crypto x402 Web3 Payment Gateway**, an **AES-256 Encrypted Persistent Database Engine**, and 5 overhauled built-in mini-games.

---

## 🌟 Key Features & Enterprise Security

- ⚡ **Java 26 Platform Performance**:
  - **Generational ZGC**: Near-zero GC pause times (<1ms) for ultra-smooth 60 FPS repaints across all games and animations.
  - **Compact Object Headers**: Up to 50% lower heap RAM usage when managing store cards and game assets.
  - **Modern Language Enhancements**: Flexible constructor bodies, pattern matching, and enhanced graphics interop.

- 🔒 **Enterprise-Grade Database Security (`DatabaseManager.java`)**:
  - **SHA-256 Cryptographic Salted Password Hashing**: Passwords are never stored in plain text. Each user receives a unique 128-bit cryptographic salt and SHA-256 hash.
  - **AES-256 CBC Binary Database Encryption**: Database contents (`nexa_database.db`) are fully encrypted using 128-bit/256-bit AES encryption with random Initialization Vectors (IV). Opening the database file in text editors reveals unreadable encrypted binary payload.

- 🎨 **Pure Java Custom Design Engine**:
  - **Antialiased Graphics**: Global 2D rendering hints for smooth curves, text, and vector graphics.
  - **Modern Aesthetics**: Futuristic dark charcoal/navy palette (`#0B0E14`), electric indigo (`#6366F1`), cyan (`#06B6D4`), and emerald green accents.
  - **Custom Components**: `ModernButton` (hover/press animations), `NavItem` (sidebar tabs with glowing active indicator bars), `ModernScrollBarUI` (minimalist dark scrollbars), `ResponsiveWrapGrid` (auto-calculated responsive card columns), and `ToastManager` (non-blocking notification overlays).

- 🛒 **Store Cart & Crypto x402 Payment System**:
  - **Game Catalog Pricing**: Free games (`$0.00`) and paid titles (`$4.99`, `$9.99`, `$14.99`).
  - **Store Cart**: Cart tab (`🛒 Cart (X)`) with live item counter badge, subtotal calculations, and item management.
  - **Crypto x402 Web3 Gateway**: Interactive checkout modal implementing the **HTTP 402 Payment Required** protocol. Supports **SOL (Solana)**, **ETH (Ethereum)**, **USDT (Tether)**, and **BTC (Bitcoin)** with real-time crypto price conversion, wallet copy, and transaction verification.

- 🕹️ **5 Overhauled Built-In Games**:
  1. **Tic Tac Toe**: Interactive 3x3 grid with turn indicators, custom symbols ('X' / 'O'), and restart logic.
  2. **Snake**: Dark grid arcade game with smooth snake movement, live score tracking, and restart overlays.
  3. **Pong**: 2-player neon arcade pong with paddle controls (`W/S` & `Up/Down`), glowing ball physics, and scoreboard.
  4. **Hangman**: Custom antialiased vector canvas drawing gallows & stick figure, spaced letter slots, and mistake counters.
  5. **Guess The Number**: Brain challenge game with attempt tracking (`5/5`), feedback log (`Too High ⬇️`, `Too Low ⬆️`), and play again actions.

---

## 🛠️ Build & Running Instructions

### Requirements
- **Java JDK 26** or later (e.g. Eclipse Adoptium Temurin JDK 26).

### 1. Compile the Project
Open terminal in project root directory and compile all Java sources into `./bin`:
```bash
# Windows PowerShell / Command Prompt
& "C:\Program Files\Eclipse Adoptium\jdk-26.0.1.8-hotspot\bin\javac.exe" -d ./bin App/*.java App/Snake/code/*.java
```

### 2. Launch the Application
Run `Main` class to start the unified app:
```bash
# Windows PowerShell
& "C:\Program Files\Eclipse Adoptium\jdk-26.0.1.8-hotspot\bin\java.exe" -cp ./bin Main
```

*(Sign in with default credentials `demo` / `demo`, `bassel` / `12345678`, or register a new user account!)*

---

## 📜 License
Distributed under the **MIT License**. See `LICENSE` for details.
