package main;

public class GameSettings {
    public static boolean isSinglePlayer = true;
    public static int boardSize = 3;

    public static boolean showTimer = true;
    public static boolean showBoardInfo = true;
    public static boolean showPlayerCounter = true;

    public static BotAI.Difficulty difficulty = BotAI.Difficulty.EASY;
    
    public static void resetToDefault() {
        isSinglePlayer = true;
        boardSize = 3;
        showTimer = true;
        showBoardInfo = true;
        showPlayerCounter = true;
    }
}