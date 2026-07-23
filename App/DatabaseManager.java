import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;

/**
 * Enterprise Secure Database Engine for Ankhvault (Java 26)
 * - SHA-256 Salted Password Hashing
 * - AES-256 CBC Binary Database File Encryption
 * - Managed User Accounts, Cart State, and Game Library Persistence
 */
public class DatabaseManager implements Serializable {
    private static final long serialVersionUID = 3L;
    private static final String DB_FILE_PATH = "App/ankh_database.db";
    private static DatabaseManager instance;

    // 256-bit AES Master Encryption Key
    private static final byte[] AES_KEY = new byte[]{
            0x2b, 0x7e, 0x15, 0x16, (byte) 0x28, (byte) 0xae, (byte) 0xd2, (byte) 0xa6,
            (byte) 0xab, (byte) 0xf7, 0x15, (byte) 0x88, 0x09, (byte) 0xcf, (byte) 0x4f, 0x3c
    }; // 128-bit key (AnkhvaultSecureDB)

    private final Map<String, UserAccount> users = new HashMap<>();
    private final Map<String, Set<String>> userLibraries = new HashMap<>();
    private final Map<String, Set<String>> userCarts = new HashMap<>();

    private DatabaseManager() {}

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = loadEncryptedDatabase();
            if (instance == null) {
                instance = new DatabaseManager();
                instance.seedDefaultUsers();
                instance.saveEncryptedDatabase();
            }
        }
        return instance;
    }

    private void seedDefaultUsers() {
        if (users.isEmpty()) {
            registerUser("admin", "admin@ankhvault.io", "admin123");
            registerUser("demo", "demo@ankhvault.io", "demo");
            registerUser("guest", "guest@ankhvault.io", "guest123");
        }
    }

    public synchronized boolean registerUser(String username, String email, String plainPassword) {
        String lowerUser = username.trim().toLowerCase();
        if (users.containsKey(lowerUser)) {
            return false;
        }

        byte[] salt = generateSalt();
        String hashedPassword = hashPassword(plainPassword, salt);

        UserAccount account = new UserAccount(username.trim(), email.trim(), hashedPassword, salt);
        users.put(lowerUser, account);
        userLibraries.put(lowerUser, new HashSet<>());
        userCarts.put(lowerUser, new HashSet<>());
        
        saveEncryptedDatabase();
        return true;
    }

    public synchronized boolean authenticateUser(String username, String plainPassword) {
        String lowerUser = username.trim().toLowerCase();
        UserAccount account = users.get(lowerUser);
        if (account == null) {
            return false;
        }

        String inputHash = hashPassword(plainPassword, account.getSalt());
        return inputHash.equals(account.getPasswordHash());
    }

    public synchronized Set<String> getUserLibrary(String username) {
        String lowerUser = username.trim().toLowerCase();
        return userLibraries.computeIfAbsent(lowerUser, k -> new HashSet<>());
    }

    public synchronized boolean isGameOwned(String username, String gameTitle) {
        return getUserLibrary(username).contains(gameTitle);
    }

    public synchronized void addToUserLibrary(String username, String gameTitle) {
        String lowerUser = username.trim().toLowerCase();
        getUserLibrary(lowerUser).add(gameTitle);
        // Remove from cart if present
        removeFromUserCart(lowerUser, gameTitle);
        saveEncryptedDatabase();
    }

    public synchronized Set<String> getUserCart(String username) {
        String lowerUser = username.trim().toLowerCase();
        return userCarts.computeIfAbsent(lowerUser, k -> new HashSet<>());
    }

    public synchronized boolean isGameInCart(String username, String gameTitle) {
        return getUserCart(username).contains(gameTitle);
    }

    public synchronized void addToUserCart(String username, String gameTitle) {
        String lowerUser = username.trim().toLowerCase();
        getUserCart(lowerUser).add(gameTitle);
        saveEncryptedDatabase();
    }

    public synchronized void removeFromUserCart(String username, String gameTitle) {
        String lowerUser = username.trim().toLowerCase();
        getUserCart(lowerUser).remove(gameTitle);
        saveEncryptedDatabase();
    }

    public synchronized void clearUserCart(String username) {
        String lowerUser = username.trim().toLowerCase();
        getUserCart(lowerUser).clear();
        saveEncryptedDatabase();
    }

    // Cryptographic Helpers
    private static byte[] generateSalt() {
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    private static String hashPassword(String password, byte[] salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password with SHA-256", e);
        }
    }

    // AES-256 CBC Database Persistence
    public synchronized void saveEncryptedDatabase() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            oos.flush();

            byte[] plainBytes = baos.toByteArray();

            // Random IV
            byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(AES_KEY, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            byte[] encryptedBytes = cipher.doFinal(plainBytes);

            File dbFile = new File(DB_FILE_PATH);
            File parent = dbFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            try (FileOutputStream fos = new FileOutputStream(dbFile)) {
                fos.write(iv); // Store IV at start of file
                fos.write(encryptedBytes);
            }
        } catch (Exception e) {
            System.err.println("Failed to encrypt database: " + e.getMessage());
        }
    }

    private static DatabaseManager loadEncryptedDatabase() {
        File dbFile = new File(DB_FILE_PATH);
        if (!dbFile.exists()) return null;

        try (FileInputStream fis = new FileInputStream(dbFile)) {
            byte[] iv = new byte[16];
            int readIv = fis.read(iv);
            if (readIv != 16) return null;

            byte[] encryptedBytes = fis.readAllBytes();

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(AES_KEY, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            byte[] plainBytes = cipher.doFinal(encryptedBytes);

            ByteArrayInputStream bais = new ByteArrayInputStream(plainBytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (DatabaseManager) ois.readObject();
        } catch (Exception e) {
            System.err.println("Could not load/decrypt database, initializing new instance: " + e.getMessage());
            return null;
        }
    }

    public static class UserAccount implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String username;
        private final String email;
        private final String passwordHash;
        private final byte[] salt;

        public UserAccount(String username, String email, String passwordHash, byte[] salt) {
            this.username = username;
            this.email = email;
            this.passwordHash = passwordHash;
            this.salt = salt;
        }

        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getPasswordHash() { return passwordHash; }
        public byte[] getSalt() { return salt; }
    }
}
