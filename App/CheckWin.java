import javax.swing.*;
public class CheckWin {

    public static boolean check(JButton[][] buttons) {
        // Check rows
        for (int i = 0; i < 3; i++) {
            if (checkLine(buttons[i][0], buttons[i][1], buttons[i][2])) {
                return true;
            }
        }

        // Check columns
        for (int i = 0; i < 3; i++) {
            if (checkLine(buttons[0][i], buttons[1][i], buttons[2][i])) {
                return true;
            }
        }

        // Check diagonals
        if (checkLine(buttons[0][0], buttons[1][1], buttons[2][2]) || checkLine(buttons[0][2], buttons[1][1], buttons[2][0])) {
            return true;
        }

        return false;
    }

    private static boolean checkLine(JButton a, JButton b, JButton c) {
        return !a.getText().equals("") && a.getText().equals(b.getText()) && a.getText().equals(c.getText());
    }
}
