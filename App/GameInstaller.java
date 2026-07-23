import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Cross-Platform Game Download, Installation & Launch Engine for Ankhvault
 * Supports Windows 11, Linux (Ubuntu/Debian/Arch/Fedora), and macOS.
 */
public class GameInstaller {
    public interface DownloadProgressCallback {
        void onProgress(int percentage, String status);
        void onComplete(File installedExe);
        void onError(String errorMessage);
    }

    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    public static boolean isLinux() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("nux") || os.contains("nix");
    }

    public static File getInstallDirectory(String gameId) {
        String base;
        if (isWindows()) {
            base = System.getenv("LOCALAPPDATA");
            if (base == null || base.isEmpty()) {
                base = System.getProperty("user.home");
            }
        } else {
            String xdgData = System.getenv("XDG_DATA_HOME");
            if (xdgData != null && !xdgData.isEmpty()) {
                base = xdgData;
            } else {
                base = System.getProperty("user.home") + "/.local/share";
            }
        }
        File baseDir = new File(base, "Ankhvault/Games/" + gameId.toLowerCase().replaceAll("[^a-z0-9]", "_"));
        if (!baseDir.exists()) baseDir.mkdirs();
        return baseDir;
    }

    public static File findExecutable(File installDir) {
        if (installDir == null || !installDir.exists()) return null;
        File[] files = installDir.listFiles();
        if (files == null) return null;

        for (File f : files) {
            if (f.isFile() && isExecutableFile(f)) {
                return f;
            }
        }
        for (File f : files) {
            if (f.isDirectory()) {
                File subExe = findExecutable(f);
                if (subExe != null) return subExe;
            }
        }
        return null;
    }

    public static boolean isExecutableFile(File f) {
        String name = f.getName().toLowerCase();
        if (isWindows()) {
            return name.endsWith(".exe") || name.endsWith(".bat") || name.endsWith(".cmd");
        } else {
            return name.endsWith(".sh") || name.endsWith(".appimage") || name.endsWith(".bin") ||
                   name.endsWith(".x86_64") || name.endsWith(".x86") || f.canExecute() || !name.contains(".");
        }
    }

    public static boolean isGameInstalled(String gameId) {
        File dir = getInstallDirectory(gameId);
        File exe = findExecutable(dir);
        return exe != null && exe.exists();
    }

    public static boolean launchExecutable(File executable) {
        if (executable == null || !executable.exists()) return false;
        try {
            if (!isWindows()) {
                executable.setExecutable(true, false);
            }

            ProcessBuilder pb;
            if (isLinux() && executable.getName().endsWith(".sh")) {
                pb = new ProcessBuilder("bash", executable.getAbsolutePath());
            } else {
                pb = new ProcessBuilder(executable.getAbsolutePath());
            }

            pb.directory(executable.getParentFile());
            pb.start();
            return true;
        } catch (Exception e) {
            System.err.println("Failed to launch game executable: " + e.getMessage());
            return false;
        }
    }

    public static void downloadAndInstallGame(String gameId, String downloadUrlStr, DownloadProgressCallback callback) {
        new Thread(() -> {
            try {
                callback.onProgress(5, "Connecting to Cloud CDN...");
                File installDir = getInstallDirectory(gameId);

                URL url = new URL(downloadUrlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("User-Agent", "Ankhvault-Launcher/3.0 (" + System.getProperty("os.name") + ")");
                
                int fileLength = conn.getContentLength();
                InputStream input = conn.getInputStream();

                callback.onProgress(20, "Downloading Game Package...");
                File zipFile = new File(installDir, gameId.replaceAll("[^a-zA-Z0-9]", "_") + ".zip");
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

                    if (!isWindows()) {
                        file.setExecutable(true, false);
                    }

                    if (isExecutableFile(file)) {
                        if (mainExe == null) mainExe = file;
                    }
                }
                zis.closeEntry();
            }
        }
        return mainExe != null ? mainExe : destDir;
    }
}
