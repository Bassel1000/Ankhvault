import javax.swing.SwingUtilities;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Boot Database Manager
            DatabaseManager.getInstance();

            // Open Login Screen as Main Application Entry Point
            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            Rectangle bounds = env.getMaximumWindowBounds();
            new Login(bounds.width, bounds.height).setVisible(true);
        });
    }
}