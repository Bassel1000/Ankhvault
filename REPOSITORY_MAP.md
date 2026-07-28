# ☥ Duat Studios Workspace & Repository Structure

This workspace contains two distinct projects with their respective GitHub repositories:

---

## 📱 1. Ankhvault (Desktop Launcher & Store)
- **Scope**: Root directory (`/`)
- **GitHub Repository**: [https://github.com/Duat-Studio/Ankhvault](https://github.com/Duat-Studio/Ankhvault)
- **Description**: Desktop gaming client, store, installer engine, and encrypted vault built with Rust, Tauri 2.0, and React CSS Glassmorphism.
- **Key Directories & Files**:
  - `src-tauri/` — Rust backend (database encryption, native downloader, OS process execution).
  - `src/` — React frontend UI components & styles.
  - `assets/` — Brand identity assets & logos (`Ankhvault Logo`, `Duat Studio Logo`).
  - `server.js` — Node.js mock API / gateway server.
  - `README.md` — Ankhvault product documentation.

---

## 🌐 2. Duat Studio Website (Official Web Portal)
- **Scope**: Nested directory (`/Duat_Studio_Website/`)
- **GitHub Repository**: [https://github.com/Duat-Studio/Duat-Studio](https://github.com/Duat-Studio/Duat-Studio)
- **Description**: Landing page, client download portal, indie developer submission portal, live game catalog, and x402 Web3 payment gateway (demo).
- **Key Directories & Files**:
  - `Duat_Studio_Website/src/` — Web portal components & developer submission forms.
  - `Duat_Studio_Website/public/` — Web assets & logos.
  - `Duat_Studio_Website/README.md` — Web portal documentation.

---

> [!NOTE]
> `Duat_Studio_Website/` is git-ignored by the root `Ankhvault` repository so both Git repositories operate cleanly and independently.
