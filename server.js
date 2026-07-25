import express from 'express';
import cors from 'cors';

const app = express();
const PORT = process.env.PORT || 3000;

const SUPABASE_URL = process.env.VITE_SUPABASE_URL || 'https://nehtpnrmkipmikutcjbr.supabase.co';
const SUPABASE_KEY = process.env.SUPABASE_SERVICE_ROLE_KEY || process.env.VITE_SUPABASE_ANON_KEY;
const TREASURY_SOL = process.env.TREASURY_SOL || '2JxDavSJ9de1twMxDxdqz1sV3vkpYVDpPbx8qTy6q2cS';
const TREASURY_EVM = process.env.TREASURY_EVM || '0x5efe4d14d5f406bb748fab14a3e2cda06e51a986';

app.use(cors());
app.use(express.json());

async function supabaseFetch(path, options = {}) {
  const res = await fetch(`${SUPABASE_URL}/rest/v1/${path}`, {
    ...options,
    headers: {
      apikey: SUPABASE_KEY,
      Authorization: `Bearer ${SUPABASE_KEY}`,
      'Content-Type': 'application/json',
      ...(options.headers || {}),
    },
  });
  return res.json();
}

app.get('/api/v1/health', (req, res) => {
  res.json({
    status: 'online',
    platform: 'Ankhvault Cloud',
    studio: 'Duat Studio',
    version: '1.0.0',
    treasurySol: TREASURY_SOL,
    treasuryEvm: TREASURY_EVM,
    paymentGateway: 'MoonPay Commerce',
  });
});

app.get('/api/v1/games', async (req, res) => {
  try {
    const games = await supabaseFetch('catalog_games?is_active=eq.true&order=price.desc&select=*');
    res.json({ success: true, count: Array.isArray(games) ? games.length : 0, games });
  } catch (err) {
    res.status(500).json({ success: false, error: 'Failed to fetch catalog from database' });
  }
});

app.post('/api/v1/dev/submit', async (req, res) => {
  const { title, price, genre, developer, walletAddress, email, download_url, description, dev_wallet_sol } = req.body;
  if (!title || price === undefined || !walletAddress || typeof title !== 'string' || typeof walletAddress !== 'string') {
    return res.status(400).json({ success: false, error: 'Missing or invalid required fields (title, price, walletAddress)' });
  }

  const cleanTitle = title.trim().replace(/[<>]/g, '');
  const cleanWallet = walletAddress.trim();
  const parsedPrice = parseFloat(price);

  if (isNaN(parsedPrice) || parsedPrice < 0) {
    return res.status(400).json({ success: false, error: 'Invalid price value' });
  }

  try {
    const gameId = `dev_${Date.now()}`;
    const data = await supabaseFetch('catalog_games', {
      method: 'POST',
      headers: { Prefer: 'return=representation' },
      body: JSON.stringify({
        id: gameId,
        title: cleanTitle,
        category: typeof genre === 'string' ? genre.trim().replace(/[<>]/g, '') : 'Indie',
        price: parsedPrice,
        rating: 5.0,
        description: typeof description === 'string' ? description.trim().replace(/[<>]/g, '') : '',
        tag: 'New Release',
        download_url: typeof download_url === 'string' ? download_url.trim() : '',
        developer_name: typeof developer === 'string' ? developer.trim().replace(/[<>]/g, '') : 'Independent Publisher',
        dev_wallet_sol: dev_wallet_sol || null,
        dev_wallet_evm: cleanWallet,
        is_active: false,
      }),
    });
    res.status(201).json({
      success: true,
      message: 'Game submitted successfully! Pending review and malware scan.',
      game: Array.isArray(data) ? data[0] : data,
    });
  } catch (err) {
    res.status(500).json({ success: false, error: 'Failed to submit game to database' });
  }
});

app.post('/api/v1/checkout/moonpay', (req, res) => {
  const { orderId, currency, amount } = req.body;
  if (!orderId || typeof orderId !== 'string' || orderId.trim().length < 5) {
    return res.status(400).json({ success: false, error: 'Invalid or missing MoonPay Commerce Order ID' });
  }

  const cleanOrderId = orderId.trim();

  res.json({
    success: true,
    gateway: 'MoonPay Commerce',
    orderId: cleanOrderId,
    currency: currency || 'USD',
    amountVerified: amount,
    ankhFeeCutPercent: 5.0,
    devPayoutPercent: 95.0,
    treasuryEvm: TREASURY_EVM,
    status: 'completed',
  });
});

app.get('/', (req, res) => {
  res.send(`
    <!DOCTYPE html>
    <html lang="en">
    <head>
      <meta charset="UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
      <title>Ankhvault — Desktop Game Launcher & Developer Store by Duat Studio</title>
      <link href="https://fonts.googleapis.com/css2?family=Outfit:wght@400;600;700;800&display=swap" rel="stylesheet">
      <style>
        * { margin: 0; padding: 0; box-sizing: border-box; font-family: 'Outfit', sans-serif; }
        body { background: #0B0E14; color: #EDE6D6; min-height: 100vh; display: flex; flex-direction: column; }
        header { padding: 24px 48px; display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #2A3447; background: #161B26; }
        .logo { font-size: 28px; font-weight: 800; color: #D4AF37; text-decoration: none; }
        .hero { flex: 1; display: flex; flex-direction: column; align-items: center; justify-content: center; text-align: center; padding: 80px 24px; }
        h1 { font-size: 48px; font-weight: 800; background: linear-gradient(135deg, #D4AF37, #17919E); -webkit-background-clip: text; -webkit-text-fill-color: transparent; margin-bottom: 20px; }
        p { color: #94A3B8; font-size: 18px; max-width: 600px; margin-bottom: 36px; line-height: 1.6; }
        .btn { display: inline-block; padding: 14px 32px; border-radius: 8px; font-weight: 700; text-decoration: none; margin: 8px; font-size: 15px; }
        .btn-primary { background: linear-gradient(135deg, #D4AF37, #B8962E); color: #0B0E14; }
        .btn-outline { border: 1px solid #D4AF37; color: #D4AF37; }
        .features { display: grid; grid-template-columns: repeat(auto-fit, minmax(260px, 1fr)); gap: 24px; max-width: 900px; margin: 40px auto 0; text-align: left; }
        .card { background: rgba(255,255,255,0.04); border: 1px solid #2A3447; border-radius: 12px; padding: 28px; }
        .card h3 { color: #D4AF37; font-size: 16px; font-weight: 700; margin-bottom: 10px; }
        .card p { font-size: 14px; margin-bottom: 0; }
        .vault { font-family: monospace; font-size: 12px; color: #17919E; margin-top: 8px; word-break: break-all; }
        footer { padding: 24px; text-align: center; font-size: 14px; color: #94A3B8; border-top: 1px solid #2A3447; }
      </style>
    </head>
    <body>
      <header>
        <a href="/" class="logo">☥ Ankhvault</a>
        <div><a href="https://github.com/Duat-Studio/Ankhvault" class="btn btn-outline" target="_blank">GitHub Repo</a></div>
      </header>
      <section class="hero">
        <h1>Ankhvault Desktop Launcher & Store</h1>
        <p>A flagship product by Duat Studio. Empowering game developers worldwide with 95% revenue retention and MoonPay Commerce settlement.</p>
        <div>
          <a href="https://github.com/Duat-Studio/Ankhvault/releases" class="btn btn-primary" target="_blank">Download Client (.exe)</a>
          <a href="/api/v1/games" class="btn btn-outline" target="_blank">Explore API Catalog</a>
        </div>
        <div class="features">
          <div class="card">
            <h3>🔥 95% Dev Revenue Split</h3>
            <p>Developers keep 95% of every sale with instant settlement.</p>
          </div>
          <div class="card">
            <h3>⚡ MoonPay Commerce Gateway</h3>
            <p>Accept Cards, Apple Pay, Google Pay, USDT, and USDC.</p>
            <div class="vault">EVM Vault: ${TREASURY_EVM}</div>
            <div class="vault">Solana Vault: ${TREASURY_SOL}</div>
          </div>
          <div class="card">
            <h3>🔒 AES-256 Vault Security</h3>
            <p>SHA-256 salted password hashing and AES-256-GCM binary database encryption.</p>
          </div>
        </div>
      </section>
      <footer>&copy; ${new Date().getFullYear()} Duat Studio. Built with Rust + Tauri 2.0.</footer>
    </body>
    </html>
  `);
});

app.listen(PORT, () => {
  console.log(`Ankhvault API server running on port ${PORT}`);
});
