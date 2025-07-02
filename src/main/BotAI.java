package main;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

public class BotAI {

    public enum Difficulty {
        EASY, MEDIUM, HARD
    }

    private static Random rand = new Random();

    public static Point getMove(String[][] board, Difficulty difficulty) {
        switch (difficulty) {
            case EASY:
                return getRandomMove(board);
            case MEDIUM:
                Point winMove = getWinningMove(board, "O");
                if (winMove != null) return winMove;

                Point blockMove = getWinningMove(board, "X");
                if (blockMove != null) return blockMove;

                return getRandomMove(board);
            case HARD:
                return getBestMove(board); // Optional: minimax
            default:
                return getRandomMove(board);
        }
    }

    // Get a random empty spot
    private static Point getRandomMove(String[][] board) {
        ArrayList<Point> empty = new ArrayList<>();
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board.length; j++)
                if (board[i][j].equals("")) empty.add(new Point(i, j));

        return empty.isEmpty() ? null : empty.get(rand.nextInt(empty.size()));
    }

    // Get move that wins for symbol (used for block or win)
    private static Point getWinningMove(String[][] board, String symbol) {
        int size = board.length;

        // Check rows
        for (int i = 0; i < size; i++) {
            int count = 0;
            Point empty = null;
            for (int j = 0; j < size; j++) {
                if (board[i][j].equals(symbol)) count++;
                else if (board[i][j].equals("")) empty = new Point(i, j);
            }
            if (count == size - 1 && empty != null) return empty;
        }

        // Check columns
        for (int j = 0; j < size; j++) {
            int count = 0;
            Point empty = null;
            for (int i = 0; i < size; i++) {
                if (board[i][j].equals(symbol)) count++;
                else if (board[i][j].equals("")) empty = new Point(i, j);
            }
            if (count == size - 1 && empty != null) return empty;
        }

        // Diagonal
        int diagCount = 0;
        Point diagEmpty = null;
        for (int i = 0; i < size; i++) {
            if (board[i][i].equals(symbol)) diagCount++;
            else if (board[i][i].equals("")) diagEmpty = new Point(i, i);
        }
        if (diagCount == size - 1 && diagEmpty != null) return diagEmpty;

        // Anti-diagonal
        int antiDiagCount = 0;
        Point antiEmpty = null;
        for (int i = 0; i < size; i++) {
            if (board[i][size - 1 - i].equals(symbol)) antiDiagCount++;
            else if (board[i][size - 1 - i].equals("")) antiEmpty = new Point(i, size - 1 - i);
        }
        if (antiDiagCount == size - 1 && antiEmpty != null) return antiEmpty;

        return null;
    }

    // Placeholder for HARD difficulty (can implement Minimax later)
    private static Point getBestMove(String[][] board) {
        return getRandomMove(board); // Replace with smart logic if needed
    }
}
