# 🎮 NexaPlay 3.0 — Modern Desktop Gaming Launcher & Store

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://adoptium.net/)
[![UI](https://img.shields.io/badge/UI-Pure%20Java%20Swing-blue.svg)]()
[![Security](https://img.shields.io/badge/Security-AES--256%20%2B%20SHA--256-green.svg)]()
[![Web3](https://img.shields.io/badge/Payments-Crypto%20x402-purple.svg)]()
[![License](https://img.shields.io/badge/License-MIT-green.svg)]()

**NexaPlay** is a state-of-the-art desktop gaming launcher, store, and game platform built in **Pure Java Swing**. Inspired by modern gaming client platforms such as Steam, Epic Games Store, and GOG Galaxy, NexaPlay features custom antialiased graphics rendering, dark glassmorphism aesthetics, a Store Cart and **Crypto x402 Web3 Payment Gateway**, an **AES-256 Encrypted Persistent Database Engine**, and 5 overhauled built-in mini-games.

---

## 🌟 Key Features & Enterprise Security

- 🔒 **Enterprise-Grade Database Security (`DatabaseManager.java`)**:
  - **SHA-256 Cryptographic Salted Password Hashing**: Passwords are never stored in plain text. Each user receives a unique 128-bit cryptographic salt and SHA-256 hash.
  - **AES-256 CBC Binary Database Encryption**: Database contents (`nexa_database.db`) are fully encrypted using 128-bit/256-bit AES encryption with random Initialization Vectors (IV). Opening the database file in text editors reveals unreadable encrypted binary payload.
  - **Automated Plaintext Cleanup**: Insecure legacy `.txt` database files are automatically deleted.

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

## 📐 Project UML Architecture & Diagrams

### 1. Complete Class Diagram

```mermaid
classDiagram
    class Main {
        +main(String[] args)
    }

    class DatabaseManager {
        -static DatabaseManager instance
        -File dbFile
        -Map~String, UserData~ users
        -byte[] AES_KEY_BYTES
        +getInstance() DatabaseManager
        +registerUser(username, email, password) bool
        +authenticateUser(username, password) bool
        +hashPassword(password, salt) String
        +getUserLibrary(username) Set~String~
        +addToUserLibrary(username, gameTitle)
        +getUserCart(username) Set~String~
        +addToUserCart(username, gameTitle)
        +removeFromUserCart(username, gameTitle)
        +clearUserCart(username)
        -loadDatabase()
        -saveDatabase()
    }

    class Theme {
        +Color BG_ROOT
        +Color BG_ELEVATED
        +Color ACCENT
        +Color ACCENT_CYAN
        +setupGraphics(Graphics2D g2)
        +createRoundedIcon(path, w, h, radius) ImageIcon
        +resolveImageFile(path) File
    }

    class ModernButton {
        -Style style
        -int cornerRadius
        -boolean isHovered
        -boolean isPressed
        +paintComponent(Graphics g)
    }

    class ModernScrollBarUI {
        -int THUMB_SIZE
        +paintThumb(Graphics g, JComponent c, Rectangle bounds)
        +paintTrack(Graphics g, JComponent c, Rectangle bounds)
    }

    class NavItem {
        -boolean active
        -String label
        +setActive(boolean active)
    }

    class Login {
        -JTextField usernameField
        -JPasswordField passwordField
        +attemptLogin(width, height)
    }

    class Register {
        -JTextField emailField
        -JTextField usernameField
        -JPasswordField passwordField
        +attemptRegister(width, height)
    }

    class Window {
        -String currentUsername
        -ResponsiveWrapGrid storeGrid
        -ResponsiveWrapGrid libraryGrid
        -CartPanel cartPanel
        +createTopBar() JPanel
        +createSidebar() JPanel
        +launchGame(String title)
    }

    class GameCard {
        -GameItem item
        -String username
        +updateActionButtons(JPanel actions)
    }

    class GameItem {
        -String id
        -String title
        -double price
        -String logoPath
        +getFormattedPrice() String
    }

    class CartPanel {
        -String username
        -List~GameItem~ currentCartItems
        +refreshCart()
    }

    class CryptoCheckoutDialog {
        -String username
        -String selectedToken
        +verifyCryptoPayment()
        +completePurchase()
    }

    Main --> Login : launches
    Login --> Register : switches
    Login --> Window : authenticates
    Window --> DatabaseManager : uses
    Window --> CartPanel : contains
    Window --> GameCard : renders
    GameCard --> GameItem : holds
    CartPanel --> CryptoCheckoutDialog : triggers
    CryptoCheckoutDialog --> DatabaseManager : unlocks games
    Window --> Theme : styles
    ModernButton --> Theme : uses
```

---

### 2. Secure User Authentication & AES-256 Crypto x402 Flow

```mermaid
sequenceDiagram
    autonumber
    actor User
    participant Main
    participant Auth as Login / Register
    participant DB as DatabaseManager (AES-256 + SHA-256)
    participant App as NexaPlay Launcher
    participant Cart as CartPanel
    participant x402 as CryptoCheckoutDialog

    User->>Main: Launch Application
    Main->>DB: Load & Decrypt AES-256 DB (nexa_database.db)
    Main->>Auth: Open Login Window
    User->>Auth: Enter Credentials
    Auth->>DB: Validate SHA-256 Salted Password Hash
    DB-->>Auth: Success (Authenticated)
    Auth->>App: Launch NexaPlay (User Session)
    User->>App: Add Games to Cart & Open "🛒 Cart"
    User->>Cart: Click "Proceed to Checkout"
    Cart->>x402: Open Crypto x402 Web3 Gateway
    User->>x402: Select Token (SOL/ETH/USDT/BTC) & Click "Verify x402 Payment"
    x402->>x402: Verify HTTP 402 Payment Required Block
    x402->>DB: Add Games to Library & Encrypt File (AES-256)
    DB-->>x402: Encrypted Payload Written
    x402-->>App: Toast Success & Games Unlocked in Library
```

---

## 📁 Repository Structure

```
NexaPlay Project 2.0/
├── .gitignore               # Ignored build binaries and temporary logs
├── README.md                # Project documentation and architecture UML
├── App/                     # Source Code Root
│   ├── Main.java            # Unified Application Entry Point
│   ├── Login.java           # Centered Glassmorphic Sign In Window
│   ├── Register.java        # Account Registration Window
│   ├── NexaPlay.java        # Main Launcher & Store Shell Window
│   ├── Theme.java           # Design System Tokens & Antialiasing Helpers
│   ├── ModernButton.java    # Custom Rounded Animated Button Component
│   ├── ModernScrollBarUI.java # Minimalist Dark Scrollbar UI
│   ├── NavItem.java         # Sidebar Tab Navigation Item
│   ├── GameCard.java        # Store Game Card with Hover Elevation Lift
│   ├── GameItem.java        # Store Catalog Game Item Data Model
│   ├── CartPanel.java       # E-Commerce Cart View & Order Summary
│   ├── CryptoCheckoutDialog.java # Web3 HTTP 402 Payment Required Dialog
│   ├── DatabaseManager.java # AES-256 Encrypted & SHA-256 Hashed Database
│   ├── ToastManager.java    # Floating Non-Blocking Toast Overlays
│   ├── ResponsiveWrapGrid.java # Auto-Calculated Responsive Grid Layout
│   ├── TicTacToeGame.java   # Overhauled Tic Tac Toe Game
│   ├── GuessTheNumberGame.java # Overhauled Guess The Number Game
│   ├── HangmanGame.java     # Overhauled Vector Canvas Hangman Game
│   ├── PongGame.java        # Overhauled Arcade Neon Pong Game
│   └── Snake/               # Snake Game Module
│       ├── code/
│       │   ├── Snake.java   # Snake Game Frame
│       │   └── Board.java   # Overhauled Dark Grid Snake Engine
│       └── resources/       # Snake Graphics Assets
└── bin/                     # Compiled JDK 21 Class Files
```

---

## 🛠️ Build & Running Instructions

### Requirements
- **Java JDK 21** or later (e.g. Eclipse Adoptium Temurin JDK 21).

### 1. Compile the Project
Open terminal in project root directory and compile all Java sources into `./bin`:
```bash
# Windows PowerShell / Command Prompt
javac -d ./bin App/*.java App/Snake/code/*.java
```

### 2. Launch the Application
Run `Main` class to start the unified app:
```bash
# Windows PowerShell
java -cp ./bin Main
```

*(Sign in with default credentials `demo` / `demo`, `bassel` / `12345678`, or register a new user account!)*

---

## 📜 License
Distributed under the **MIT License**. See `LICENSE` for details.
