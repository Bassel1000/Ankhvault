use serde::{Deserialize, Serialize};

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct GameItem {
    pub id: String,
    pub title: String,
    pub category: String,
    pub price: f64,
    pub rating: f64,
    pub description: String,
    pub tag: String,
    pub download_url: String,
}

pub fn get_default_catalog() -> Vec<GameItem> {
    vec![
        GameItem {
            id: "shadow_kemet".to_string(),
            title: "Shadow of Kemet".to_string(),
            category: "Action / RPG".to_string(),
            price: 14.99,
            rating: 4.9,
            description: "Unravel ancient temple mysteries and slay mythical guardians in ancient Egypt.".to_string(),
            tag: "Flagship Title".to_string(),
            download_url: "https://github.com/duatstudio/downloads/releases/download/v1.0/shadow_kemet.zip".to_string(),
        },
        GameItem {
            id: "nomad_racer".to_string(),
            title: "Nomad Racer".to_string(),
            category: "Cyberpunk Racing".to_string(),
            price: 9.99,
            rating: 4.7,
            description: "High-octane neon drag racing across futuristic desert highways.".to_string(),
            tag: "Best Seller".to_string(),
            download_url: "https://github.com/duatstudio/downloads/releases/download/v1.0/nomad_racer.zip".to_string(),
        },
        GameItem {
            id: "aegis_protocol".to_string(),
            title: "Aegis Protocol".to_string(),
            category: "Tactical Shooter".to_string(),
            price: 0.00,
            rating: 4.8,
            description: "Competitive squad tactics and cybernetic combat in a dystopian city.".to_string(),
            tag: "Free to Play".to_string(),
            download_url: "https://github.com/duatstudio/downloads/releases/download/v1.0/aegis_protocol.zip".to_string(),
        },
        GameItem {
            id: "chronicles_duat".to_string(),
            title: "Chronicles of Duat".to_string(),
            category: "Strategy / Deckbuilder".to_string(),
            price: 4.99,
            rating: 4.6,
            description: "Master ritual spells and summon celestial pharaohs in turn-based combat.".to_string(),
            tag: "New Release".to_string(),
            download_url: "https://github.com/duatstudio/downloads/releases/download/v1.0/chronicles_duat.zip".to_string(),
        },
        GameItem {
            id: "neon_odyssey".to_string(),
            title: "Neon Odyssey".to_string(),
            category: "Space Simulator".to_string(),
            price: 14.99,
            rating: 4.9,
            description: "Explore uncharted galaxy sectors, mine rare plasma ores, and command dreadnoughts.".to_string(),
            tag: "Featured".to_string(),
            download_url: "https://github.com/duatstudio/downloads/releases/download/v1.0/neon_odyssey.zip".to_string(),
        },
    ]
}
