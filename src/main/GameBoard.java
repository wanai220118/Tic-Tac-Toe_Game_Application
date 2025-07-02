package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import main.BotAI;

public class GameBoard extends JFrame {
    private JButton[][] buttons = new JButton[3][3];
    private boolean isXTurn = true; // X = Player 1, O = Player 2 or Bot
    private boolean isSinglePlayer = GameSettings.isSinglePlayer;
    private MusicPlayer musicPlayer = new MusicPlayer();
    private JButton muteButton;
    // Match Info
    private JLabel timerLabel, spotsLabel, humanWinLabel, botWinLabel, turnLabel;
    private int spotsTaken = 0;
    private int humanWins = 0;
    private int botWins = 0;
    private Timer timer;
    private int elapsedTime = 0;
    private JButton backButton;
    private int playerXWins = 0;
    private int playerOWins = 0;
    private JLabel confettiOverlay;
    private JButton restartButton;
    private String botDifficulty = "Easy";
    private String playerXSymbol = "❌";
    private String playerOSymbol = "⭕";

    private java.util.List<Point> getWinningCells(String symbol) {
        int size = buttons.length;
        java.util.List<Point> cells = new ArrayList<>();

        // Rows
        for (int i = 0; i < size; i++) {
            boolean win = true;
            for (int j = 0; j < size; j++) {
                if (!buttons[i][j].getText().equals(symbol)) {
                    win = false;
                    break;
                }
            }
            if (win) {
                for (int j = 0; j < size; j++) cells.add(new Point(i, j));
                return cells;
            }
        }

        // Columns
        for (int j = 0; j < size; j++) {
            boolean win = true;
            for (int i = 0; i < size; i++) {
                if (!buttons[i][j].getText().equals(symbol)) {
                    win = false;
                    break;
                }
            }
            if (win) {
                for (int i = 0; i < size; i++) cells.add(new Point(i, j));
                return cells;
            }
        }

        // Diagonal
        boolean diagWin = true;
        for (int i = 0; i < size; i++) {
            if (!buttons[i][i].getText().equals(symbol)) {
                diagWin = false;
                break;
            }
        }
        if (diagWin) {
            for (int i = 0; i < size; i++) cells.add(new Point(i, i));
            return cells;
        }

        // Anti-diagonal
        boolean antiDiagWin = true;
        for (int i = 0; i < size; i++) {
            if (!buttons[i][size - 1 - i].getText().equals(symbol)) {
                antiDiagWin = false;
                break;
            }
        }
        if (antiDiagWin) {
            for (int i = 0; i < size; i++) cells.add(new Point(i, size - 1 - i));
            return cells;
        }

        return null;
    }

    public GameBoard() {
        setTitle("Tic-Tac-Toe Game");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                    GameBoard.this,
                    "Are you sure you want to exit the game?",
                    "Exit Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );

                if (choice == JOptionPane.YES_OPTION) {
                    if (timer != null) timer.stop();
                    musicPlayer.stopMusic();
                    dispose();
                    System.exit(0);
                }
            }
        });
        
        if (isSinglePlayer) {
            String[] options = {"Easy", "Medium", "Hard"};
            botDifficulty = (String) JOptionPane.showInputDialog(
                this,
                "Select Bot Difficulty:",
                "Difficulty Setting",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
            );
            if (botDifficulty == null) botDifficulty = "Easy";
        }
        
        if (isSinglePlayer) {
            playerXSymbol = (String) JOptionPane.showInputDialog(
                this,
                "Choose your symbol:",
                "Player Symbol",
                JOptionPane.PLAIN_MESSAGE,
                null,
                new String[]{"❌", "🔥", "⭐", "🐱", "🍕", "🎮"},
                "❌"
            );
            if (playerXSymbol == null || playerXSymbol.isEmpty()) playerXSymbol = "❌";
            playerOSymbol = "🤖";
        } else {
            playerXSymbol = (String) JOptionPane.showInputDialog(
                this,
                "Player X, choose your symbol:",
                "Player X Symbol",
                JOptionPane.PLAIN_MESSAGE,
                null,
                new String[]{"❌", "🔥", "⭐", "🐱", "🍕", "🎮"},
                "❌"
            );
            if (playerXSymbol == null || playerXSymbol.isEmpty()) playerXSymbol = "❌";

            playerOSymbol = (String) JOptionPane.showInputDialog(
                this,
                "Player O, choose your symbol:",
                "Player O Symbol",
                JOptionPane.PLAIN_MESSAGE,
                null,
                new String[]{"⭕", "🌊", "💎", "🐶", "🍔", "👾"},
                "⭕"
            );
            if (playerOSymbol == null || playerOSymbol.isEmpty()) playerOSymbol = "⭕";
        }

        setLocationRelativeTo(null);

        int size = GameSettings.boardSize;
        buttons = new JButton[size][size];
        JPanel boardPanel = new JPanel(new GridLayout(size, size));

        Font emojiFont = new Font("Segoe UI Emoji", Font.PLAIN, 40);

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                JButton btn = new JButton("");
                btn.setFont(emojiFont);                
                final int r = row;
                final int c = col;

                btn.addActionListener(e -> handleMove(r, c));

                buttons[row][col] = btn;
                boardPanel.add(btn);
            }
        }

        add(boardPanel);
        
     // Start background music in a separate thread
        new Thread(() -> musicPlayer.playMusic("background.wav", true)).start();

     // 1. Create top panel
        JPanel topPanel = new JPanel(new BorderLayout());

        // 2. Create mute button
        muteButton = new JButton("🔊");
        muteButton.addActionListener(e -> toggleMusic());

        // 3. Create back button
        backButton = new JButton("🔙 Menu");
        backButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to return to the main menu?\nYour current game progress will be lost.",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) {
                if (timer != null) timer.stop();
                musicPlayer.stopMusic();
                dispose(); // Close current game window
                new WelcomePage(); // Open welcome screen only after confirmation
            }
        });
        
     // 3. Create restart button
        restartButton = new JButton("🔄 Restart");
        restartButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Restart the current game?",
                "Confirm Restart",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            if (choice == JOptionPane.YES_OPTION) {
                resetBoard();
            }
        });

        // 4. Add both to panel
        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(muteButton, BorderLayout.EAST);
        topPanel.add(restartButton, BorderLayout.CENTER);

        // 5. Add top panel to frame
        add(topPanel, BorderLayout.NORTH);
        
     // Match Info Panel
        timerLabel = new JLabel("Time: 0s");
        spotsLabel = new JLabel("Spots: 0");
        humanWinLabel = new JLabel("👤 Wins: 0");
        botWinLabel = new JLabel("🤖 Wins: 0");
        turnLabel = new JLabel("Your Turn");

        JPanel infoPanel = new JPanel(new GridLayout(1, 5));
        infoPanel.add(timerLabel);
        infoPanel.add(spotsLabel);
        infoPanel.add(humanWinLabel);
        infoPanel.add(botWinLabel);
        infoPanel.add(turnLabel);

        add(infoPanel, BorderLayout.SOUTH);
        
        // Apply visibility settings from GameSettings
        if (!GameSettings.showTimer) timerLabel.setVisible(false);
        if (!GameSettings.showBoardInfo) spotsLabel.setVisible(false);
        if (!GameSettings.showPlayerCounter) {
            humanWinLabel.setVisible(false);
            botWinLabel.setVisible(false);
        }

        // Start Timer
        startTimer();

        setVisible(true);
        
        confettiOverlay = new JLabel(new ImageIcon(getClass().getResource("/confetti.gif")));
        confettiOverlay.setBounds(0, 0, getWidth(), getHeight());
        confettiOverlay.setVisible(false);
        getLayeredPane().add(confettiOverlay, JLayeredPane.POPUP_LAYER);

    }
    
    private void startTimer() {
        timer = new Timer(1000, e -> {
            elapsedTime++;
            timerLabel.setText("Time: " + elapsedTime + "s");
        });
        timer.start();
    }

    private void handleMove(int row, int col) {
        if (!buttons[row][col].getText().equals("")) return;
        
//        playSound("click.wav");
        
        if (isXTurn) {
            playSound("click.wav");
        } else {
            playSound("o_click.wav");
        }

        buttons[row][col].setText(isXTurn ? playerXSymbol : playerOSymbol);
        spotsTaken++;
        spotsLabel.setText("Spots: " + spotsTaken);

        turnLabel.setText(isXTurn ? "Bot Turn" : "Your Turn");

        String currentSymbol = isXTurn ? playerXSymbol : playerOSymbol;
        java.util.List<Point> winningCells = getWinningCells(currentSymbol);
        if (winningCells != null) {
            highlightWinningCells(winningCells);

            String winner;
            if (GameSettings.isSinglePlayer) {
                if (isXTurn) {
                    humanWins++;
                    humanWinLabel.setText("👤 Wins: " + humanWins);
                    winner = "You (X)";
                } else {
                    botWins++;
                    botWinLabel.setText("🤖 Wins: " + botWins);
                    winner = "Bot (O)";
                }
            } else {
                if (isXTurn) {
                    playerXWins++;
                    humanWinLabel.setText("👤 " + PlayerNames.playerX + " (X): " + playerXWins);
                    winner = PlayerNames.playerX + " (X)";
                } else {
                    playerOWins++;
                    botWinLabel.setText("👥 " + PlayerNames.playerO + " (O): " + playerOWins);
                    winner = PlayerNames.playerO + " (O)";
                }
            }

            if (GameSettings.isSinglePlayer) {
                showConfettiAnimation();
            }

            showPostGamePrompt(winner + " wins!", winner);
            return;
        }

        if (isBoardFull()) {
        	showPostGamePrompt("It's a draw!", "Draw");
            return;
        }

        isXTurn = !isXTurn;

        if (!GameSettings.isSinglePlayer) {
            turnLabel.setText(isXTurn ? PlayerNames.playerX + "'s Turn" : PlayerNames.playerO + "'s Turn");
        } else {
            turnLabel.setText(isXTurn ? "Your Turn" : "Bot Turn");
        }

        if (isSinglePlayer && !isXTurn) {
            botMove();
        }
    }

    private void botMove() {
        String[][] board = new String[buttons.length][buttons.length];

        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons.length; j++) {
                board[i][j] = buttons[i][j].getText();
            }
        }

        BotAI.Difficulty difficulty;
        switch (botDifficulty.toLowerCase()) {
            case "hard":
                difficulty = BotAI.Difficulty.HARD;
                break;
            case "medium":
                difficulty = BotAI.Difficulty.MEDIUM;
                break;
            default:
                difficulty = BotAI.Difficulty.EASY;
        }

        Point move = BotAI.getMove(board, difficulty);
        if (move == null) return;

        buttons[move.x][move.y].setText(playerOSymbol);
        playSound("o_click.wav");
        spotsTaken++;
        spotsLabel.setText("Spots: " + spotsTaken);

        java.util.List<Point> winningCells = getWinningCells(playerOSymbol);
        if (winningCells != null) {
            highlightWinningCells(winningCells);
            botWins++;
            botWinLabel.setText("🤖 Wins: " + botWins);
            showConfettiAnimation();
            showPostGamePrompt(PlayerNames.playerO + " " + playerOSymbol + " wins!", PlayerNames.playerO);
            return;
        }

        if (isBoardFull()) {
        	showPostGamePrompt("It's a draw!", "Draw");
            return;
        }

        isXTurn = true;
        turnLabel.setText("Your Turn");
    }

    private boolean checkWin(String symbol) {
        int size = buttons.length;

        // Check rows
        for (int i = 0; i < size; i++) {
            boolean rowWin = true;
            for (int j = 0; j < size; j++) {
                if (!buttons[i][j].getText().equals(symbol)) {
                    rowWin = false;
                    break;
                }
            }
            if (rowWin) return true;
        }

        // Check columns
        for (int j = 0; j < size; j++) {
            boolean colWin = true;
            for (int i = 0; i < size; i++) {
                if (!buttons[i][j].getText().equals(symbol)) {
                    colWin = false;
                    break;
                }
            }
            if (colWin) return true;
        }

        // Check main diagonal
        boolean diagWin = true;
        for (int i = 0; i < size; i++) {
            if (!buttons[i][i].getText().equals(symbol)) {
                diagWin = false;
                break;
            }
        }
        if (diagWin) return true;

        // Check anti-diagonal
        boolean antiDiagWin = true;
        for (int i = 0; i < size; i++) {
            if (!buttons[i][size - 1 - i].getText().equals(symbol)) {
                antiDiagWin = false;
                break;
            }
        }
        return antiDiagWin;
    }

    private boolean isBoardFull() {
        for (JButton[] row : buttons)
            for (JButton btn : row)
                if (btn.getText().equals("")) return false;
        return true;
    }

    private void resetBoard() {
        for (JButton[] row : buttons) {
            for (JButton btn : row) {
                btn.setText("");
                btn.setBackground(null); // reset background
            }
        }

        isXTurn = true;
        spotsTaken = 0;
        elapsedTime = 0;
        spotsLabel.setText("Spots: 0");
        timerLabel.setText("Time: 0s");
        turnLabel.setText(GameSettings.isSinglePlayer ? "Your Turn" : PlayerNames.playerX + "'s Turn");
    }
    
    private void toggleMusic() {
        if (musicPlayer.isPlaying()) {
            musicPlayer.stopMusic();
            muteButton.setText("🔇");
        } else {
            musicPlayer.resumeMusic();
            muteButton.setText("🔊");
        }
    }
    
    private void showPostGamePrompt(String resultMessage, String winner) {
        String fullMessage;
        Icon gif = null;

        if (GameSettings.isSinglePlayer) {
            if (resultMessage.toLowerCase().contains("you")) {
                fullMessage = "🎉 " + resultMessage + "\n\nYou nailed it!";
                playSound("win.wav");
            } else if (resultMessage.toLowerCase().contains("bot")) {
                fullMessage = "💀 " + resultMessage + "\n\nWant revenge?";
                playSound("lose.wav");
            } else {
                fullMessage = "😐 " + resultMessage + "\n\nTry again?";
                playSound("draw.wav");
            }
        } else {
            if (resultMessage.toLowerCase().contains("draw")) {
                fullMessage = "😐 " + resultMessage + "\n\nTry again?";
                playSound("draw.wav");
            } else {
                fullMessage = "🎯 " + resultMessage + "\n\nRematch?";
                playSound("win.wav");
            }
        }

        JLabel label = new JLabel("<html><div style='text-align: center;'>" +
                fullMessage.replaceAll("\n", "<br>") + "</div></html>", gif, JLabel.CENTER);
        label.setHorizontalTextPosition(JLabel.CENTER);
        label.setVerticalTextPosition(JLabel.BOTTOM);
        label.setFont(new Font("Arial", Font.PLAIN, 16));

        Object[] options = {"🧾 Summary", "🔁 Play Again", "🏠 Return to Menu"};
        int option = JOptionPane.showOptionDialog(
                this,
                label,
                "🎊 Game Over",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[1]  // default is "Play Again"
        );

        if (option == 0) { // 🧾 Summary
            showMatchSummaryDialog(resultMessage);
            // Reopen the prompt after showing summary
            showPostGamePrompt(resultMessage, winner);
        } else if (option == 1) { // 🔁 Play Again
            resetBoard();
        } else if (option == 2) { // 🏠 Return to Menu
            showFinalSessionSummary();
            if (timer != null) timer.stop();
            musicPlayer.stopMusic();
            dispose();
            new WelcomePage();
        }
        
        //database
        if (!GameSettings.isSinglePlayer) {
            DatabaseManager.saveMatch(
                "Multi",
                winner,
                PlayerNames.playerX,
                PlayerNames.playerO,
                playerXWins,
                playerOWins,
                spotsTaken,
                elapsedTime
            );
        } else {
            DatabaseManager.saveMatch(
                "Single",
                winner,
                "You",
                "Bot",
                humanWins,
                botWins,
                spotsTaken,
                elapsedTime
            );
        }

    }
    
    private void playSound(String fileName) {
        new Thread(() -> {
            try {
                musicPlayer.playOneShot(fileName); // Assumes playOneShot() exists in MusicPlayer
            } catch (Exception e) {
                System.out.println("Failed to play sound: " + fileName);
            }
        }).start();
    }

    private void playWinAnimationAndSound(String winner) {
        String fullMessage;
        Icon gif = null;

        if (GameSettings.isSinglePlayer) {
            if (winner.toLowerCase().contains("you")) {
                fullMessage = "🎉 " + winner + " Wins!\n\nYou nailed it! Play again?";
                gif = new ImageIcon(getClass().getResource("/confetti.gif"));
                playSound("win.wav");
            } else if (winner.toLowerCase().contains("bot")) {
                fullMessage = "💀 " + winner + " Wins!\n\nWant revenge?";
                playSound("lose.wav");
            } else {
                fullMessage = "😐 " + winner + " Wins!";
                playSound("draw.wav");
            }
        } else {
            fullMessage = "🎯 " + winner + " Wins!\n\nRematch?";
            playSound("win.wav");
        }

        JLabel label = new JLabel("<html><div style='text-align: center;'>" + fullMessage.replaceAll("\n", "<br>") + "</div></html>", gif, JLabel.CENTER);
        label.setHorizontalTextPosition(JLabel.CENTER);
        label.setVerticalTextPosition(JLabel.BOTTOM);
        label.setFont(new Font("Arial", Font.PLAIN, 16));

        int option = JOptionPane.showOptionDialog(
            this,
            label,
            "🎊 Game Over",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,
            new String[]{"🔁 Play Again", "🏠 Return to Menu"},
            "🔁 Play Again"
        );

        if (option == JOptionPane.YES_OPTION) {
            resetBoard();
        } else {
            if (timer != null) timer.stop();
            musicPlayer.stopMusic();
            dispose();
            new WelcomePage();
        }
    }
    
    private void highlightWinningCells(java.util.List<Point> cells) {
        for (Point p : cells) {
            buttons[p.x][p.y].setBackground(Color.YELLOW);
        }
    }

    private void showConfettiAnimation() {
        confettiOverlay.setVisible(true);
        new Timer(3000, e -> confettiOverlay.setVisible(false)).start(); // Hide after 3 seconds
    }
    
    private void showMatchSummaryDialog(String winner) {
        String message;
        if (GameSettings.isSinglePlayer) {
            message = String.format("""
                    🎯 Match Summary:
                    - Winner: %s
                    - You: %d wins
                    - Bot: %d wins
                    - Total Spots Used: %d
                    - Time Elapsed: %ds
                    """, winner, humanWins, botWins, spotsTaken, elapsedTime);
        } else {
            message = String.format("""
                    🎯 Match Summary:
                    - Winner: %s
                    - %s (X): %d wins
                    - %s (O): %d wins
                    - Total Spots Used: %d
                    - Time Elapsed: %ds
                    """, winner, PlayerNames.playerX, playerXWins,
                    PlayerNames.playerO, playerOWins, spotsTaken, elapsedTime);
        }

        JOptionPane.showMessageDialog(this, message, "📋 Match Summary", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showFinalSessionSummary() {
        String finalWinner;
        if (GameSettings.isSinglePlayer) {
            if (humanWins > botWins) {
                finalWinner = "🎉 You are the final winner!";
            } else if (botWins > humanWins) {
                finalWinner = "🤖 Bot wins the session!";
            } else {
                finalWinner = "😐 It's a tie!";
            }
        } else {
            if (playerXWins > playerOWins) {
                finalWinner = "🎉 " + PlayerNames.playerX + " (X) is the final winner!";
            } else if (playerOWins > playerXWins) {
                finalWinner = "🎉 " + PlayerNames.playerO + " (O) is the final winner!";
            } else {
                finalWinner = "😐 It's a tie between " + PlayerNames.playerX + " and " + PlayerNames.playerO + "!";
            }
        }

        String message = """
                📋 Final Session Summary:
                
                %s
                
                Total Rounds Played: %d
                Total Spots Used: %d
                Total Time: %ds
                """.formatted(finalWinner, humanWins + botWins + playerXWins + playerOWins, spotsTaken, elapsedTime);

        JOptionPane.showMessageDialog(this, message, "🏁 Session Ended", JOptionPane.INFORMATION_MESSAGE);
    }

}
