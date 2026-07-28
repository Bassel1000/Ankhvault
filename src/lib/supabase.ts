import { createClient } from '@supabase/supabase-js';

const DEFAULT_SUPABASE_URL = 'https://nehtpnrmkipmikutcjbr.supabase.co';
const DEFAULT_SUPABASE_KEY = 'sb_publishable_wPGdeEtsDk0c9MpB-FUcrg_a0kZWuVz';

const supabaseUrl = import.meta.env.VITE_SUPABASE_URL || DEFAULT_SUPABASE_URL;
const supabaseAnonKey = import.meta.env.VITE_SUPABASE_ANON_KEY || DEFAULT_SUPABASE_KEY;

if (!supabaseUrl || !supabaseAnonKey) {
  throw new Error(
    'Missing Supabase environment variables.\n' +
    'Copy .env.example to .env and fill in your VITE_SUPABASE_URL and VITE_SUPABASE_ANON_KEY values.'
  );
}

export const supabase = createClient(supabaseUrl, supabaseAnonKey);

export interface DbGame {
  id: string;
  title: string;
  category: string;
  price: number;
  rating: number;
  description: string;
  tag: string;
  download_url: string;
  developer_name?: string;
}

export async function fetchCatalogFromSupabase(): Promise<DbGame[] | null> {
  try {
    const { data, error } = await supabase
      .from('catalog_games')
      .select('*')
      .eq('is_active', true)
      .order('price', { ascending: false });

    if (error || !data || data.length === 0) return null;
    return data.map((item) => ({
      id: item.id,
      title: item.title,
      category: item.category,
      price: Number(item.price),
      rating: Number(item.rating),
      description: item.description,
      tag: item.tag,
      download_url: item.download_url,
      developer_name: item.developer_name || undefined,
    }));
  } catch (err) {
    console.warn('Supabase catalog fetch error:', err);
    return null;
  }
}

export async function registerUserProfileSupabase(username: string, email: string, role: 'player' | 'developer' = 'player') {
  try {
    await supabase.from('user_profiles').upsert(
      {
        username,
        email,
        role,
        updated_at: new Date().toISOString(),
      },
      { onConflict: 'username' }
    );
  } catch (err) {
    console.warn('Supabase user profile register sync error:', err);
  }
}

export async function updateUsernameSupabase(oldUsername: string, newUsername: string): Promise<boolean> {
  try {
    const { data: existing } = await supabase
      .from('user_profiles')
      .select('username')
      .eq('username', newUsername)
      .maybeSingle();

    if (existing) {
      return false;
    }

    await supabase
      .from('user_profiles')
      .update({ username: newUsername, updated_at: new Date().toISOString() })
      .eq('username', oldUsername);

    await supabase
      .from('user_libraries')
      .update({ username: newUsername })
      .eq('username', oldUsername);

    return true;
  } catch (err) {
    console.warn('Supabase update username error:', err);
    return false;
  }
}

export async function deleteUserProfileSupabase(username: string) {
  try {
    await supabase.from('user_libraries').delete().eq('username', username);
    await supabase.from('user_profiles').delete().eq('username', username);
  } catch (err) {
    console.warn('Supabase delete user error:', err);
  }
}

export async function registerDeveloperProfileSupabase(
  developerName: string,
  email: string
) {
  try {
    await supabase.from('developer_profiles').upsert(
      {
        developer_name: developerName,
        email,
      },
      { onConflict: 'developer_name' }
    );
  } catch (err) {
    console.warn('Developer profile registration error:', err);
  }
}

export async function syncUserLibrarySupabase(username: string, gameIds: string[]) {
  try {
    const inserts = gameIds.map((game_id) => ({ username, game_id }));
    await supabase.from('user_libraries').upsert(inserts, { onConflict: 'username,game_id' });
  } catch (err) {
    console.warn('Supabase library sync error:', err);
  }
}

export async function fetchUserLibrarySupabase(username: string): Promise<string[] | null> {
  try {
    const { data, error } = await supabase
      .from('user_libraries')
      .select('game_id')
      .eq('username', username);

    if (error || !data) return null;
    return data.map((row) => row.game_id);
  } catch (err) {
    console.warn('Supabase fetch library error:', err);
    return null;
  }
}

export async function recordX402TransactionSupabase(
  username: string,
  txHash: string | undefined,
  tokenSymbol: string,
  amountUsd: number
) {
  try {
    await supabase.from('transactions').insert([
      {
        username,
        tx_hash: txHash || `demo_${Date.now()}`,
        token_symbol: tokenSymbol,
        amount_usd: amountUsd,
        created_at: new Date().toISOString(),
      },
    ]);
  } catch (err) {
    console.warn('Supabase record transaction error:', err);
  }
}
