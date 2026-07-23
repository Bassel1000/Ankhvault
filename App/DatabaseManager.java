import java.io.*;
import java.util.*;

public class DatabaseManager {
    private static DatabaseManager instance;
    private final File dbFile;
    private final Map<String, UserData> users = new HashMap<>();

    public static class UserData {
        public String username;
        public String email;
        public String password;
        public Set<String> library = new HashSet<>();
        public Set<String> cart = new HashSet<>();

        public UserData(String username, String email, String password) {
            this.username = username;
            this.email = email;
            this.password = password;
        }
    }

    private DatabaseManager() {
        File dir = new File("App");
        if (!dir.exists()) dir = new File(".");
        dbFile = new File(dir, "nexa_database.txt");
        loadDatabase();

        // Seed default demo user if not present
        if (!users.containsKey("demo")) {
            registerUser("demo", "demo@nexaplay.io", "demo");
        }
        if (!users.containsKey("bassel")) {
            registerUser("bassel", "bassel@nexaplay.io", "12345678");
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public synchronized boolean registerUser(String username, String email, String password) {
        if (users.containsKey(username.toLowerCase())) {
            return false;
        }
        UserData data = new UserData(username, email, password);
        users.put(username.toLowerCase(), data);
        saveDatabase();
        return true;
    }

    public synchronized boolean authenticateUser(String username, String password) {
        UserData data = users.get(username.toLowerCase());
        return data != null && data.password.equals(password);
    }

    public synchronized UserData getUserData(String username) {
        return users.get(username.toLowerCase());
    }

    public synchronized Set<String> getUserLibrary(String username) {
        UserData data = getUserData(username);
        return data != null ? new HashSet<>(data.library) : new HashSet<>();
    }

    public synchronized boolean isGameOwned(String username, String gameTitle) {
        UserData data = getUserData(username);
        return data != null && data.library.contains(gameTitle);
    }

    public synchronized void addToUserLibrary(String username, String gameTitle) {
        UserData data = getUserData(username);
        if (data != null) {
            data.library.add(gameTitle);
            data.cart.remove(gameTitle);
            saveDatabase();
        }
    }

    public synchronized Set<String> getUserCart(String username) {
        UserData data = getUserData(username);
        return data != null ? new HashSet<>(data.cart) : new HashSet<>();
    }

    public synchronized boolean isGameInCart(String username, String gameTitle) {
        UserData data = getUserData(username);
        return data != null && data.cart.contains(gameTitle);
    }

    public synchronized void addToUserCart(String username, String gameTitle) {
        UserData data = getUserData(username);
        if (data != null && !data.library.contains(gameTitle)) {
            data.cart.add(gameTitle);
            saveDatabase();
        }
    }

    public synchronized void removeFromUserCart(String username, String gameTitle) {
        UserData data = getUserData(username);
        if (data != null) {
            data.cart.remove(gameTitle);
            saveDatabase();
        }
    }

    public synchronized void clearUserCart(String username) {
        UserData data = getUserData(username);
        if (data != null) {
            data.cart.clear();
            saveDatabase();
        }
    }

    private void loadDatabase() {
        if (!dbFile.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(dbFile))) {
            String line;
            UserData current = null;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("USER:")) {
                    String[] parts = line.substring(5).split("\\|");
                    if (parts.length >= 3) {
                        current = new UserData(parts[0], parts[1], parts[2]);
                        users.put(parts[0].toLowerCase(), current);
                    }
                } else if (current != null && line.startsWith("LIB:")) {
                    String[] items = line.substring(4).split("\\|");
                    for (String it : items) {
                        if (!it.isEmpty()) current.library.add(it);
                    }
                } else if (current != null && line.startsWith("CART:")) {
                    String[] items = line.substring(5).split("\\|");
                    for (String it : items) {
                        if (!it.isEmpty()) current.cart.add(it);
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println("Error loading database: " + ex.getMessage());
        }
    }

    private void saveDatabase() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(dbFile))) {
            for (UserData u : users.values()) {
                writer.println("USER:" + u.username + "|" + u.email + "|" + u.password);
                writer.println("LIB:" + String.join("|", u.library));
                writer.println("CART:" + String.join("|", u.cart));
                writer.println();
            }
        } catch (Exception ex) {
            System.err.println("Error saving database: " + ex.getMessage());
        }
    }
}
