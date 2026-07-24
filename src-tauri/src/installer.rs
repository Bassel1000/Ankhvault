use std::fs::{self, File};
use std::io::Write;
use std::path::{Path, PathBuf};
use std::process::Command;
use tauri::{AppHandle, Emitter};

pub fn get_install_directory(game_id: &str) -> PathBuf {
    let clean_id = game_id.to_lowercase().replace(|c: char| !c.is_alphanumeric(), "_");
    dirs::data_local_dir()
        .unwrap_or_else(|| PathBuf::from("."))
        .join("Ankhvault")
        .join("Games")
        .join(clean_id)
}

pub fn is_executable_file(path: &Path) -> bool {
    let name = path.file_name().and_then(|n| n.to_str()).unwrap_or("").to_lowercase();
    if cfg!(target_os = "windows") {
        name.ends_with(".exe") || name.ends_with(".bat") || name.ends_with(".cmd")
    } else {
        name.ends_with(".sh") || name.ends_with(".appimage") || name.ends_with(".x86_64") || !name.contains('.')
    }
}

pub fn find_executable(dir: &Path) -> Option<PathBuf> {
    if !dir.exists() {
        return None;
    }
    if let Ok(entries) = fs::read_dir(dir) {
        for entry in entries.flatten() {
            let path = entry.path();
            if path.is_file() && is_executable_file(&path) {
                return Some(path);
            } else if path.is_dir() {
                if let Some(sub_exe) = find_executable(&path) {
                    return Some(sub_exe);
                }
            }
        }
    }
    None
}

pub fn check_game_installed(game_id: &str) -> bool {
    let dir = get_install_directory(game_id);
    find_executable(&dir).is_some()
}

pub fn launch_game_executable(game_id: &str) -> Result<bool, String> {
    let dir = get_install_directory(game_id);
    let exe = find_executable(&dir).ok_or_else(|| format!("Game '{}' executable not found in {:?}", game_id, dir))?;

    #[cfg(target_os = "windows")]
    {
        Command::new(&exe)
            .current_dir(exe.parent().unwrap_or(&dir))
            .spawn()
            .map_err(|e| format!("Failed to launch process: {}", e))?;
    }

    #[cfg(not(target_os = "windows"))]
    {
        use std::os::unix::fs::PermissionsExt;
        if let Ok(metadata) = fs::metadata(&exe) {
            let mut perms = metadata.permissions();
            perms.set_mode(0o755);
            fs::set_permissions(&exe, perms).ok();
        }

        if exe.extension().and_then(|s| s.to_str()) == Some("sh") {
            Command::new("bash")
                .arg(&exe)
                .current_dir(exe.parent().unwrap_or(&dir))
                .spawn()
                .map_err(|e| format!("Failed to launch script: {}", e))?;
        } else {
            Command::new(&exe)
                .current_dir(exe.parent().unwrap_or(&dir))
                .spawn()
                .map_err(|e| format!("Failed to launch binary: {}", e))?;
        }
    }

    Ok(true)
}

pub async fn download_and_install_game(
    app: AppHandle,
    game_id: String,
    download_url: String,
) -> Result<String, String> {
    let install_dir = get_install_directory(&game_id);
    fs::create_dir_all(&install_dir).map_err(|e| e.to_string())?;

    // Emit progress event
    app.emit("download-progress", serde_json::json!({
        "gameId": game_id,
        "percentage": 10,
        "status": "Connecting to Cloud CDN..."
    })).ok();

    let client = reqwest::Client::new();
    let res = client.get(&download_url).send().await.map_err(|e| e.to_string())?;

    let _total_size = res.content_length().unwrap_or(0);
    let zip_path = install_dir.join(format!("{}.zip", game_id));
    let mut file = File::create(&zip_path).map_err(|e| e.to_string())?;

    app.emit("download-progress", serde_json::json!({
        "gameId": game_id,
        "percentage": 30,
        "status": "Downloading Game Build Package..."
    })).ok();

    // Download content
    let bytes = res.bytes().await.map_err(|e| e.to_string())?;
    file.write_all(&bytes).map_err(|e| e.to_string())?;

    app.emit("download-progress", serde_json::json!({
        "gameId": game_id,
        "percentage": 75,
        "status": "Extracting Game Package..."
    })).ok();

    // Extract ZIP archive
    unzip_archive(&zip_path, &install_dir)?;
    fs::remove_file(&zip_path).ok();

    // Default placeholder executable if download url was mock
    let default_exe = install_dir.join(format!("{}.exe", game_id));
    if !default_exe.exists() {
        let mut mock_file = File::create(&default_exe).map_err(|e| e.to_string())?;
        writeln!(mock_file, "@echo off\necho Launching {} from Ankhvault Launcher...\npause", game_id).ok();
    }

    app.emit("download-progress", serde_json::json!({
        "gameId": game_id,
        "percentage": 100,
        "status": "Installation Complete!"
    })).ok();

    Ok(format!("Game '{}' successfully installed!", game_id))
}

fn unzip_archive(zip_file: &Path, dest_dir: &Path) -> Result<(), String> {
    let file = File::open(zip_file).map_err(|e| e.to_string())?;
    let mut archive = zip::ZipArchive::new(file).map_err(|e| e.to_string())?;

    for i in 0..archive.len() {
        let mut file = archive.by_index(i).map_err(|e| e.to_string())?;
        let outpath = match file.enclosed_name() {
            Some(path) => dest_dir.join(path),
            None => continue,
        };

        if file.name().ends_with('/') {
            fs::create_dir_all(&outpath).map_err(|e| e.to_string())?;
        } else {
            if let Some(p) = outpath.parent() {
                if !p.exists() {
                    fs::create_dir_all(p).map_err(|e| e.to_string())?;
                }
            }
            let mut outfile = File::create(&outpath).map_err(|e| e.to_string())?;
            std::io::copy(&mut file, &mut outfile).map_err(|e| e.to_string())?;
        }
    }
    Ok(())
}
