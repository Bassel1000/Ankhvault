import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Real Game Download & Installation Engine
 * Downloads game build packages over HTTP/HTTPS and unzips them to LocalAppData/Ankhvault/Games/<GameId>/
 */
public class GameInstaller {
    public interface DownloadProgressCallback {
        void onProgress(int percentage, String status);
        void onComplete(File installedExe);
        void onError(String errorMessage);
    }

    public static File getInstallDirectory(String gameId) {
        String localAppData = System.getenv("LOCALAPPDATA");
        if (localAppData == null || localAppData.isEmpty()) {
            localAppData = System.getProperty("user.home");
        }
        File baseDir = new File(localAppData, "Ankhvault/Games/" + gameId.toLowerCase());
        if (!baseDir.exists()) baseDir.mkdirs();
        return baseDir;
    }

    public static void downloadAndInstallGame(String gameId, String downloadUrlStr, DownloadProgressCallback callback) {
        new Thread(() -> {
            try {
                callback.onProgress(5, "Connecting to Cloud CDN...");
                File installDir = getInstallDirectory(gameId);

                URL url = new URL(downloadUrlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("User-Agent", "Ankhvault-Launcher/3.0");
                
                int fileLength = conn.getContentLength();
                InputStream input = conn.getInputStream();

                callback.onProgress(20, "Downloading Game Package...");
                File zipFile = new File(installDir, gameId + ".zip");
                FileOutputStream output = new FileOutputStream(zipFile);

                byte[] data = new byte[8192];
                long total = 0;
                int count;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    output.write(data, 0, count);
                    if (fileLength > 0) {
                        int pct = 20 + (int) ((total * 50) / fileLength);
                        callback.onProgress(pct, "Downloading... (" + pct + "%)");
                    }
                }

                output.flush();
                output.close();
                input.close();

                // Unpack Game Zip
                callback.onProgress(75, "Extracting Game Package...");
                File mainExecutable = unzipPackage(zipFile, installDir);
                zipFile.delete(); // Clean up zip archive

                callback.onProgress(100, "Installation Complete!");
                callback.onComplete(mainExecutable);

            } catch (Exception ex) {
                callback.onError("Installation failed: " + ex.getMessage());
            }
        }).start();
    }

    private static File unzipPackage(File zipFile, File destDir) throws IOException {
        File mainExe = null;
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File file = new File(destDir, entry.getName());
                if (entry.isDirectory()) {
                    file.mkdirs();
                } else {
                    file.getParentFile().mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        byte[] buffer = new byte[8192];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                    if (file.getName().toLowerCase().endsWith(".exe") || file.getName().toLowerCase().endsWith(".bat")) {
                        if (mainExe == null) mainExe = file;
                    }
                }
                zis.closeEntry();
            }
        }
        return mainExe != null ? mainExe : destDir;
    }
}
