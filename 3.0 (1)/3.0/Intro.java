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
    private JButton playAI;
    private JButton play1v1;

    public Intro() {
        setTitle("Intro Choice Maker");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        initializeIntroPanel();

        setFocusable(true);
        requestFocus();
        setVisible(true);
    }

    private void initializeIntroPanel() {
        clearConsole();
        introPanel = new JPanel();
        introPanel.setLayout(new GridLayout(3, 1));
        introPanel.setPreferredSize(new Dimension(800, 800));

        JLabel titleLabel = new JLabel("Choose an Option");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        introPanel.add(titleLabel);

        playAI = new JButton("Play an AI");
        play1v1 = new JButton("Play 1v1 Local");

        playAI.addActionListener(e -> startGameAgainstComputer());
        play1v1.addActionListener(e -> start2v2ManualGame());

        introPanel.add(playAI);
        introPanel.add(play1v1);

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

    public static void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Intro::new);
    }
}
