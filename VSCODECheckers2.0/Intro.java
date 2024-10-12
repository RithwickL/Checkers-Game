import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class Intro extends JFrame {
    private JPanel introPanel;
    private JButton playAgainstComputerButton;
    private JButton play2v2Button;

    public Intro() {
        setTitle("Checkers Intro");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        initializeIntroPanel();

        setFocusable(true);
        requestFocus();
        setVisible(true);
    }

    private void initializeIntroPanel() {
        introPanel = new JPanel();
        introPanel.setLayout(new GridLayout(3, 1));
        introPanel.setPreferredSize(new Dimension(400, 200));

        JLabel titleLabel = new JLabel("Welcome to Checkers!");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        introPanel.add(titleLabel);

        playAgainstComputerButton = new JButton("Play Against Computer");
        play2v2Button = new JButton("2v2 Manual");

        playAgainstComputerButton.addActionListener(e -> startGameAgainstComputer());
        play2v2Button.addActionListener(e -> start2v2ManualGame());

        introPanel.add(playAgainstComputerButton);
        introPanel.add(play2v2Button);

        add(introPanel, BorderLayout.CENTER);
    }

    private void startGameAgainstComputer() {
        new CheckersGame(true);
        dispose();
    }

    private void start2v2ManualGame() {
        new CheckersGame(false);
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Intro::new);
    }
}
