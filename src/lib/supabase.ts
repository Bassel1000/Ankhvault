import { createClient } from '@supabase/supabase-js';

const supabaseUrl = import.meta.env.VITE_SUPABASE_URL || 'https://nehtpnrmkipmikutcjbr.supabase.co';
const supabaseAnonKey = import.meta.env.VITE_SUPABASE_ANON_KEY || 'sb_publishable_6eDmlkZxa0YnaWb6KkwRIQ_XtcNVlze';

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
    }));
  } catch (err) {
    console.warn('Supabase catalog fetch fallback:', err);
    return null;
  }
}

export async function syncUserLibrarySupabase(username: string, gameIds: string[]) {
  try {
    const inserts = gameIds.map((game_id) => ({
      username,
      game_id,
    }));
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
    return null;
  }
}

export async function recordX402TransactionSupabase(
  username: string,
  txHash: string,
  tokenSymbol: string,
  amountUsd: number
) {
  try {
    await supabase.from('x402_transactions').insert({
      username,
      tx_hash: txHash,
      token_symbol: tokenSymbol,
      amount_usd: amountUsd,
    });
  } catch (err) {
    console.warn('Supabase transaction record error:', err);
  }
}
