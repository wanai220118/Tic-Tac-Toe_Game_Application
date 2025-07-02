package main;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

public class WelcomePage extends JFrame {

    public WelcomePage() {
        setTitle("Tic-Tac-Toe");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel title = new JLabel("Welcome to Tic-Tac-Toe");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JButton playButton = new JButton("Play");
        JButton settingsButton = new JButton("Settings");

        playButton.addActionListener(e -> {
            dispose();

            if (!GameSettings.isSinglePlayer) {
                PlayerNames.playerX = JOptionPane.showInputDialog(null, "Enter name for Player X:", "Player X");
                if (PlayerNames.playerX == null || PlayerNames.playerX.isBlank()) PlayerNames.playerX = "Player X";

                PlayerNames.playerO = JOptionPane.showInputDialog(null, "Enter name for Player O:", "Player O");
                if (PlayerNames.playerO == null || PlayerNames.playerO.isBlank()) PlayerNames.playerO = "Player O";
            }

            new GameBoard();
        });

        settingsButton.addActionListener(e -> {
            dispose();
            new SettingsPage();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 1, 10, 10));
        buttonPanel.add(playButton);
        buttonPanel.add(settingsButton);

        getContentPane().setLayout(new BorderLayout(20, 20));
        getContentPane().add(title, BorderLayout.NORTH);
        getContentPane().add(buttonPanel, BorderLayout.CENTER);

        setVisible(true);
    }
}
