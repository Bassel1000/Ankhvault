public class SwitchPlayer {

    private static String currentPlayer = "X";

    public static void switchPlayer() {
        currentPlayer = currentPlayer.equals("X") ? "O" : "X";
    }

    public static String getCurrentPlayer() {
        return currentPlayer;
    }
}
