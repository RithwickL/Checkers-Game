import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class CheckersGame extends JFrame {
    private JButton[][] boardButtons;
    private int[][] board;
    private int currentPlayer;
    private int selectedRow;
    private int selectedCol;
    private JLabel resultLabel;

    public CheckersGame() {
        setTitle("Checkers Game");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initializeBoard();
        createGUI();

        setLocationRelativeTo(null);
        setVisible(true);
        resultLabel = new JLabel("", JLabel.CENTER);
        resultLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
        add(resultLabel);
        currentPlayer = 1;
        playTurn();
    }

    private void initializeBoard() {
        clearConsole();
        board = new int[8][8];
        selectedRow = -1;
        selectedCol = -1;

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 8; j++)
                if ((i + j) % 2 != 0)
                    board[i][j] = 1;

        for (int i = 5; i < 8; i++)
            for (int j = 0; j < 8; j++)
                if ((i + j) % 2 != 0)
                    board[i][j] = 2;
    }

    private void createGUI() {
        setLayout(new java.awt.BorderLayout());
        JPanel boardPanel = new JPanel(new java.awt.GridLayout(8, 8));
        boardButtons = new JButton[8][8];

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                boardButtons[i][j] = new JButton();
                boardButtons[i][j].setBackground((i + j) % 2 == 0 ? Color.WHITE : Color.BLACK);
                boardButtons[i][j].addActionListener(new BoardButtonListener(i, j));
                updateButtonIcon(i, j);
                boardPanel.add(boardButtons[i][j]);
            }

        add(boardPanel, java.awt.BorderLayout.CENTER);
        resultLabel = new JLabel("", JLabel.CENTER);
        resultLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
        add(resultLabel, java.awt.BorderLayout.SOUTH);
    }

    private void updateButtonIcon(int row, int col) {
        if (board[row][col] == 1)
            boardButtons[row][col].setIcon(createPieceIcon("Normal_Red.png"));
        else if (board[row][col] == 2)
            boardButtons[row][col].setIcon(createPieceIcon("Normal_Black.png"));
        else if (board[row][col] == 3)
            boardButtons[row][col].setIcon(createPieceIcon("Queen_Red.png"));
        else if (board[row][col] == 4)
            boardButtons[row][col].setIcon(createPieceIcon("Queen_Black.png"));
        else
            boardButtons[row][col].setIcon(null);
    }

    private ImageIcon createPieceIcon(String imagePath) {
        ImageIcon icon = new ImageIcon(imagePath);
        Image image = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }

    private class BoardButtonListener implements ActionListener {
        private int row;
        private int col;

        public BoardButtonListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Additional check for forced jumps before the player's move
            boolean hasForcedJumps = checkForForcedJumps(row, col);

            if (hasForcedJumps) {
                makeForcedJumpAutomatically(row, col);
            } else {
                handleRegularMove(row, col);
            }
        }

        private void handleRegularMove(int row, int col) {
            if (selectedRow == -1 && selectedCol == -1 && isCurrentPlayerPiece(row, col)) {
                selectedRow = row;
                selectedCol = col;
                boardButtons[selectedRow][selectedCol].setBackground(Color.YELLOW);
            } else if (selectedRow != -1 && selectedCol != -1 && isValidMove(selectedRow, selectedCol, row, col)) {
                System.out.println("Moving from: " + selectedRow + ", " + selectedCol + " to " + row + ", " + col);
                board[row][col] = board[selectedRow][selectedCol];
                board[selectedRow][selectedCol] = 0;
                handleCapture(selectedRow, selectedCol, row, col);

                boolean hasMoreForcedJumps = checkForForcedJumps(row, col);

                if (hasMoreForcedJumps) {
                    updateBoardGUI();
                    selectedRow = row;
                    selectedCol = col;
                    boardButtons[selectedRow][selectedCol].setBackground(Color.YELLOW);
                } else {
                    currentPlayer = (currentPlayer == 1) ? 2 : 1;
                    updateBoardGUI();
                    boardButtons[selectedRow][selectedCol]
                            .setBackground((selectedRow + selectedCol) % 2 == 0 ? Color.WHITE : Color.BLACK);
                    selectedRow = -1;
                    selectedCol = -1;

                    if (isGameFinished())
                        announceWinner();
                    else
                        playTurn();
                }
            } else {
                selectedRow = -1;
                selectedCol = -1;

                for (int i = 0; i < 8; i++)
                    for (int j = 0; j < 8; j++)
                        boardButtons[i][j].setBackground((i + j) % 2 == 0 ? Color.WHITE : Color.BLACK);
            }
        }

        private boolean isCurrentPlayerPiece(int row, int col) {
            return board[row][col] == currentPlayer || (currentPlayer == 1 && board[row][col] == 3)
                    || (currentPlayer == 2 && board[row][col] == 4);
        }

        private boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
            if (Math.abs(toRow - fromRow) == 2 && Math.abs(toCol - fromCol) == 2) {
                int capturedRow = (fromRow + toRow) / 2;
                int capturedCol = (fromCol + toCol) / 2;

                if (board[capturedRow][capturedCol] == getOpponentPlayer()) {
                    board[capturedRow][capturedCol] = 0;
                    return true;
                } else {
                    return false;
                }
            }

            if (toRow < 0 || toRow >= 8 || toCol < 0 || toCol >= 8)
                return false;
            if (board[toRow][toCol] != 0)
                return false;

            int direction = (currentPlayer == 1) ? 1 : -1;

            if (Math.abs(toRow - fromRow) == 2 && Math.abs(toCol - fromCol) == 2) {
                if (direction * (toRow - fromRow) > 0)
                    return true;
                else
                    return false;
            }

            if (toRow != fromRow + direction || Math.abs(toCol - fromCol) != 1)
                return false;

            return true;
        }

        private int getOpponentPlayer() {
            return (currentPlayer == 1) ? 2 : 1;
        }

        private void handleCapture(int fromRow, int fromCol, int toRow, int toCol) {
            if (Math.abs(toRow - fromRow) == 2 && Math.abs(toCol - fromCol) == 2) {
                int capturedRow = (fromRow + toRow) / 2;
                int capturedCol = (fromCol + toCol) / 2;

                board[capturedRow][capturedCol] = 0;
            }
        }
    }

    private void updateBoardGUI() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                updateButtonIcon(i, j);
    }

    private boolean isGameFinished() {
        int player1Count = 0;
        int player2Count = 0;

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                if (board[i][j] == 1 || board[i][j] == 3)
                    player1Count++;
                else if (board[i][j] == 2 || board[i][j] == 4)
                    player2Count++;

        return player1Count == 0 || player2Count == 0 || (player1Count == 1 && player2Count == 1);
    }

    private void announceWinner() {
        int player1Count = 0;
        int player2Count = 0;

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                if (board[i][j] == 1 || board[i][j] == 3)
                    player1Count++;
                else if (board[i][j] == 2 || board[i][j] == 4)
                    player2Count++;

        if (player1Count > player2Count)
            resultLabel.setText("Player 1 wins!");
        else if (player2Count > player1Count)
            resultLabel.setText("Player 2 wins!");
        else
            resultLabel.setText("It's a tie!");

        disableBoardButtons();
    }

    private void disableBoardButtons() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                boardButtons[i][j].setEnabled(false);
    }

    private boolean checkForForcedJumps() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                if (board[i][j] == currentPlayer || (currentPlayer == 1 && board[i][j] == 3)
                        || (currentPlayer == 2 && board[i][j] == 4))
                    if (checkForForcedJumps(i, j))
                        return true;
        return false;
    }

    private boolean checkForForcedJumps(int startRow, int startCol) {
        int direction = (currentPlayer == 1) ? 1 : -1;

        int[][] capturingMoves = { { 2, 2 }, { 2, -2 }, { -2, 2 }, { -2, -2 } };

        for (int[] move : capturingMoves) {
            int toRow = startRow + direction * move[0];
            int toCol = startCol + move[1];

            int capturedRow = startRow + direction;
            int capturedCol = startCol + move[1] / 2;

            if (toRow >= 0 && toRow < 8 && toCol >= 0 && toCol < 8)
                if (board[toRow][toCol] == 0 && capturedRow >= 0 && capturedRow < 8 && capturedCol >= 0
                        && capturedCol < 8
                        && (board[capturedRow][capturedCol] == 2 || board[capturedRow][capturedCol] == 4))
                    return true;
        }
        return false;
    }

    private void makeForcedJump(int startRow, int startCol, int toRow, int toCol) {
        if (toRow >= 0 && toRow < 8 && toCol >= 0 && toCol < 8) {
            board[toRow][toCol] = board[startRow][startCol];
            board[startRow][startCol] = 0;

            int capturedRow = (startRow + toRow) / 2;
            int capturedCol = (startCol + toCol) / 2;

            board[capturedRow][capturedCol] = 0;

            updateBoardGUI();

            if (checkForForcedJumps(toRow, toCol))
                makeForcedJumpAutomatically(toRow, toCol);
            else {
                currentPlayer = (currentPlayer == 1) ? 2 : 1;

                if (toRow == 0 && board[toRow][toCol] == 1) {
                    board[toRow][toCol] = 3;
                    updateButtonIcon(toRow, toCol);
                } else if (toRow == 7 && board[toRow][toCol] == 2) {
                    board[toRow][toCol] = 4;
                    updateButtonIcon(toRow, toCol);
                }

                boardButtons[startRow][startCol]
                        .setBackground((startRow + startCol) % 2 == 0 ? Color.WHITE : Color.BLACK);
                selectedRow = -1;
                selectedCol = -1;

                if (isGameFinished())
                    announceWinner();
                else
                    playTurn();
            }
        } else
            System.out.println("Error: Destination is out of bounds");
    }

    private void playTurn() {
        boolean hasForcedJumps = checkForForcedJumps(selectedRow, selectedCol);

        if (hasForcedJumps)
            makeForcedJumpAutomatically(selectedRow, selectedCol);
        else {
            System.out.println("Player " + currentPlayer + ": Make a move");

            if ((currentPlayer == 1 && selectedRow == 7) || (currentPlayer == 2 && selectedRow == 0)) {
                // Check for promotion
                if (selectedRow >= 0 && selectedRow < 8 && selectedCol >= 0 && selectedCol < 8) {
                    board[selectedRow][selectedCol] = (currentPlayer == 1) ? 3 : 4; // Promote to queen
                    updateButtonIcon(selectedRow, selectedCol);
                } else {
                    System.out.println("Error: Invalid promotion indices");
                }
            }

            currentPlayer = (currentPlayer == 1) ? 2 : 1;

            // Check indices before updating button color
            if (selectedRow >= 0 && selectedRow < 8 && selectedCol >= 0 && selectedCol < 8) {
                boardButtons[selectedRow][selectedCol]
                        .setBackground((selectedRow + selectedCol) % 2 == 0 ? Color.WHITE : Color.BLACK);
            } else {
                System.out.println("Error: Invalid indices for button background");
            }

            selectedRow = -1;
            selectedCol = -1;

            if (isGameFinished())
                announceWinner();
        }
    }

    private void makeForcedJumpAutomatically(int startRow, int startCol) {
        int direction = (currentPlayer == 1) ? 1 : -1;

        int[][] capturingMoves = { { 2, 2 }, { 2, -2 }, { -2, 2 }, { -2, -2 } };

        for (int[] move : capturingMoves) {
            int toRow = startRow + direction * move[0];
            int toCol = startCol + move[1];

            int capturedRow = startRow + direction;
            int capturedCol = startCol + move[1] / 2;

            if (toRow >= 0 && toRow < 8 && toCol >= 0 && toCol < 8 &&
                    board[toRow][toCol] == 0
                    && (board[capturedRow][capturedCol] == 2 || board[capturedRow][capturedCol] == 4)) {
                makeForcedJump(startRow, startCol, toRow, toCol);

                // Check for promotion after the forced jump
                if (toRow == 0 && board[toRow][toCol] == 1) {
                    board[toRow][toCol] = 3; // Promote to queen
                    updateButtonIcon(toRow, toCol);
                } else if (toRow == 7 && board[toRow][toCol] == 2) {
                    board[toRow][toCol] = 4; // Promote to queen
                    updateButtonIcon(toRow, toCol);
                }

                currentPlayer = (currentPlayer == 1) ? 2 : 1;

                boardButtons[startRow][startCol]
                        .setBackground((startRow + startCol) % 2 == 0 ? Color.WHITE : Color.BLACK);
                selectedRow = -1;
                selectedCol = -1;

                if (isGameFinished())
                    announceWinner();
                else
                    playTurn();

                return;
            }
        }

        // If no valid forced jumps, revert to regular turn
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
        boardButtons[startRow][startCol]
                .setBackground((startRow + startCol) % 2 == 0 ? Color.WHITE : Color.BLACK);
        selectedRow = -1;
        selectedCol = -1;
        playTurn();
    }

    public static void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CheckersGame());
    }
}
