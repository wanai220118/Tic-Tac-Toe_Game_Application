package main;

import javax.swing.*;
import java.awt.*;

public class SettingsPage extends JFrame {
    private JComboBox<String> gameModeBox;
    private JComboBox<String> boardSizeBox;
    private JCheckBox timerCheck, boardInfoCheck, playerCounterCheck;

    public SettingsPage() {
        setTitle("Settings");
        setSize(400, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Settings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(Box.createVerticalStrut(10));
        add(titleLabel);
        add(Box.createVerticalStrut(10));

        // General
        JPanel generalCard = new JPanel(new GridLayout(2, 2, 10, 10));
        generalCard.setBorder(BorderFactory.createTitledBorder("General"));

        generalCard.add(new JLabel("Gamemode:"));
        gameModeBox = new JComboBox<>(new String[]{"Singleplayer", "Multiplayer"});
        generalCard.add(gameModeBox);

        generalCard.add(new JLabel("Board Size:"));
        boardSizeBox = new JComboBox<>(new String[]{"Default (3x3)", "4x4", "5x5", "6x6"});
        generalCard.add(boardSizeBox);

        add(generalCard);
        add(Box.createVerticalStrut(10));

        // Match Info
        JPanel matchCard = new JPanel(new GridLayout(3, 1));
        matchCard.setBorder(BorderFactory.createTitledBorder("Match Info"));
        
        timerCheck = new JCheckBox("Match Timer");
        boardInfoCheck = new JCheckBox("Board Info");
        playerCounterCheck = new JCheckBox("Player Counter");

        matchCard.add(timerCheck);
        matchCard.add(boardInfoCheck);
        matchCard.add(playerCounterCheck);

        add(matchCard);
        add(Box.createVerticalStrut(20));

        // Buttons
        JButton applyBtn = new JButton("Apply");
        JButton resetBtn = new JButton("Reset to Default");

        applyBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        resetBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        applyBtn.addActionListener(e -> {
            applySettings();
            dispose();
            new WelcomePage(); // back to main menu first
        });

        resetBtn.addActionListener(e -> {
            GameSettings.resetToDefault();
            loadSettings();
        });

        add(applyBtn);
        add(Box.createVerticalStrut(10));
        add(resetBtn);
        add(Box.createVerticalStrut(20));

        loadSettings();
        setVisible(true);
    }

    private void loadSettings() {
        gameModeBox.setSelectedIndex(GameSettings.isSinglePlayer ? 0 : 1);
        boardSizeBox.setSelectedIndex(GameSettings.boardSize - 3);

        timerCheck.setSelected(GameSettings.showTimer);
        timerCheck.setText("Match Timer: " + (GameSettings.showTimer ? "On" : "Off"));

        boardInfoCheck.setSelected(GameSettings.showBoardInfo);
        boardInfoCheck.setText("Board Info: " + (GameSettings.showBoardInfo ? "On" : "Off"));

        playerCounterCheck.setSelected(GameSettings.showPlayerCounter);
        playerCounterCheck.setText("Player Counter: " + (GameSettings.showPlayerCounter ? "On" : "Off"));
    }

    private void applySettings() {
        GameSettings.isSinglePlayer = gameModeBox.getSelectedIndex() == 0;
        GameSettings.boardSize = boardSizeBox.getSelectedIndex() + 3;

        GameSettings.showTimer = timerCheck.isSelected();
        GameSettings.showBoardInfo = boardInfoCheck.isSelected();
        GameSettings.showPlayerCounter = playerCounterCheck.isSelected();
    }
}
