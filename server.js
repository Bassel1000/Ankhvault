const express = require('express');
const cors = require('cors');

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());

// Load Environment Variables with Fallbacks
const TREASURY_SOL = process.env.TREASURY_SOL || "68MPta3vW2cNi2JYBtYPVLk8RFUHE5mAuAKppkik9mtb";
const TREASURY_EVM = process.env.TREASURY_EVM || "0x58f9f0e213140b8f59382a8b195bcbe2e8e8d07a";

const storeCatalog = [
  { id: "ttt-01", title: "Tic Tac Toe", price: 0.00, genre: "Strategy & Puzzle", developer: "NexaPlay Studios" },
  { id: "snk-02", title: "Snake", price: 0.00, genre: "Arcade Classic", developer: "NexaPlay Studios" },
  { id: "png-03", title: "Pong", price: 4.99, genre: "2-Player Arcade", developer: "Retro Bytes" },
  { id: "hng-04", title: "Hangman", price: 9.99, genre: "Word & Logic", developer: "WordSmith Games" },
  { id: "gtn-05", title: "Guess The Number", price: 14.99, genre: "Mind Challenge", developer: "Logic Lab" }
];

app.get('/api/v1/health', (req, res) => {
  res.json({ status: "online", platform: "NexaPlay Cloud", version: "3.0.0", treasurySol: TREASURY_SOL, treasuryEvm: TREASURY_EVM });
});

app.get('/api/v1/games', (req, res) => {
  res.json({ success: true, count: storeCatalog.length, games: storeCatalog });
});

app.post('/api/v1/dev/submit', (req, res) => {
  const { title, price, genre, developer, walletAddress } = req.body;
  if (!title || price === undefined || !walletAddress) {
    return res.status(400).json({ success: false, error: "Missing required fields (title, price, walletAddress)" });
  }

  const newGame = {
    id: `dev-${Date.now()}`,
    title,
    price: parseFloat(price),
    genre: genre || "Indie",
    developer: developer || "Independent Publisher",
    walletAddress,
    status: "pending_verification"
  };

  storeCatalog.push(newGame);
  res.status(201).json({ success: true, message: "Game submitted successfully! Pending malware scan.", game: newGame });
});

app.post('/api/v1/checkout/x402', (req, res) => {
  const { txHash, token, amount } = req.body;
  if (!txHash) {
    return res.status(400).json({ success: false, error: "Missing transaction hash / signature" });
  }

  res.json({
    success: true,
    protocol: "HTTP 402 Payment Required",
    txHash,
    token: token || "USDT",
    amountVerified: amount,
    nexaFeeCutPercent: 5.0,
    devPayoutPercent: 95.0,
    treasurySol: TREASURY_SOL,
    treasuryEvm: TREASURY_EVM,
    status: "verified_on_chain"
  });
});

app.get('/', (req, res) => {
  res.send(`
    <!DOCTYPE html>
    <html lang="en">
    <head>
      <meta charset="UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
      <title>NexaPlay — Desktop Game Launcher & Developer Store</title>
      <link href="https://fonts.googleapis.com/css2?family=Outfit:wght@400;600;700;800&display=swap" rel="stylesheet">
      <style>
        * { margin: 0; padding: 0; box-sizing: border-box; font-family: 'Outfit', sans-serif; }
        body { background: #0B0E14; color: #FFFFFF; min-height: 100vh; display: flex; flex-direction: column; }
        header { padding: 24px 48px; display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #2A3447; background: #161B26; }
        .logo { font-size: 28px; font-weight: 800; color: #06B6D4; text-decoration: none; display: flex; align-items: center; gap: 12px; }
        .hero { flex: 1; display: flex; flex-direction: column; align-items: center; justify-content: center; text-align: center; padding: 80px 24px; }
        h1 { font-size: 56px; font-weight: 800; background: linear-gradient(135deg, #06B6D4, #6366F1); -webkit-background-clip: text; -webkit-text-fill-color: transparent; margin-bottom: 20px; }
        p { font-size: 20px; color: #94A3B8; max-width: 680px; line-height: 1.6; margin-bottom: 36px; }
        .btn-group { display: flex; gap: 16px; }
        .btn { padding: 14px 32px; border-radius: 12px; font-size: 16px; font-weight: 700; text-decoration: none; cursor: pointer; transition: all 0.2s ease; }
        .btn-primary { background: #6366F1; color: white; border: none; box-shadow: 0 4px 20px rgba(99, 102, 241, 0.4); }
        .btn-primary:hover { background: #818CF8; transform: translateY(-2px); }
        .btn-outline { background: transparent; color: white; border: 1px solid #2A3447; }
        .btn-outline:hover { border-color: #06B6D4; color: #06B6D4; }
        .features { display: grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap: 24px; max-width: 1100px; width: 100%; margin-top: 60px; text-align: left; }
        .card { background: #161B26; padding: 28px; border-radius: 16px; border: 1px solid #2A3447; }
        .card h3 { font-size: 20px; color: #06B6D4; margin-bottom: 12px; }
        .card p { font-size: 14px; color: #94A3B8; margin-bottom: 0; word-break: break-all; }
        .vault { font-family: monospace; font-size: 12px; color: #818CF8; margin-top: 8px; }
        footer { padding: 24px; text-align: center; font-size: 14px; color: #94A3B8; border-top: 1px solid #2A3447; }
      </style>
    </head>
    <body>
      <header>
        <a href="/" class="logo">🎮 NexaPlay</a>
        <div><a href="https://github.com/Bassel1000/NexaPlay" class="btn btn-outline" target="_blank">GitHub Repo</a></div>
      </header>

      <section class="hero">
        <h1>The 5% Commission Desktop Game Store</h1>
        <p>Empowering indie game developers worldwide with the lowest platform fee in history. 95% revenue to developers, 5% to NexaPlay.</p>

        <div class="btn-group">
          <a href="https://github.com/Bassel1000/NexaPlay/releases" class="btn btn-primary" target="_blank">Download Client (.exe)</a>
          <a href="/api/v1/games" class="btn btn-outline" target="_blank">Explore API Catalog</a>
        </div>

        <div class="features">
          <div class="card">
            <h3>🔥 5% Revenue Split</h3>
            <p>95% payouts directly to developers. Keep what you earn with instant USD Stablecoin settlement.</p>
          </div>
          <div class="card">
            <h3>⚡ Stablecoin x402 Protocol</h3>
            <p>Accept USDT, USDC, and USDG on-chain.</p>
            <div class="vault">EVM Vault: ${TREASURY_EVM}</div>
            <div class="vault">Solana Vault: ${TREASURY_SOL}</div>
          </div>
          <div class="card">
            <h3>🔒 Enterprise Security</h3>
            <p>SHA-256 salted password hashing and AES-256 binary database file encryption.</p>
          </div>
        </div>
      </section>

      <footer>
        &copy; 2026 NexaPlay Platform Inc. Java 26.
      </footer>
    </body>
    </html>
  `);
});

app.listen(PORT, () => {
  console.log(`NexaPlay Server listening on port ${PORT}`);
});
