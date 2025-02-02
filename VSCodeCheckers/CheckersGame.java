import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
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

        // Start the game with player 1
        currentPlayer = 1;
        playTurn();

        // Add a component listener to handle resizing
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent evt) {
                // Recalculate button size when the window is resized
                int buttonSize = calculateButtonSize();
                updateButtonSizes(buttonSize);
            }
        });
    }

    private void initializeBoard() {
        clearConsole();
        board = new int[8][8];
        selectedRow = -1;
        selectedCol = -1;

        // Initialize the board with pieces
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 != 0) {
                    board[i][j] = 1; // Player 1's pieces
                }
            }
        }

        for (int i = 5; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 != 0) {
                    board[i][j] = 2; // Player 2's pieces
                }
            }
        }
    }

    private void createGUI() {
        setLayout(new java.awt.BorderLayout());

        JPanel boardPanel = new JPanel(new GridLayout(8, 8));
        boardButtons = new JButton[8][8];

        // Add a component listener to handle resizing
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent evt) {
                // Recalculate button size when the window is resized
                int buttonSize = calculateButtonSize();
                updateButtonSizes(buttonSize);
            }
        });

        // Calculate button size initially
        int buttonSize = calculateButtonSize();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                boardButtons[i][j] = new JButton();
                boardButtons[i][j].setBackground((i + j) % 2 == 0 ? Color.WHITE : Color.BLACK);
                boardButtons[i][j].setPreferredSize(new Dimension(buttonSize, buttonSize));
                boardButtons[i][j].addActionListener(new BoardButtonListener(i, j));
                updateButtonIcon(i, j);
                boardPanel.add(boardButtons[i][j]);
            }
        }

        add(boardPanel, BorderLayout.CENTER);

        resultLabel = new JLabel("", JLabel.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(resultLabel, BorderLayout.SOUTH);
    }

    private void updateButtonIcon(int row, int col) {
        if (board[row][col] == 1) {
            boardButtons[row][col].setIcon(createPieceIcon("Normal_Red.png"));
        } else if (board[row][col] == 2) {
            boardButtons[row][col].setIcon(createPieceIcon("Normal_Black.png"));
        } else {
            boardButtons[row][col].setIcon(null);
        }
    }

    private ImageIcon createPieceIcon(String imagePath) {
        ImageIcon icon = new ImageIcon(imagePath);
        Image image = icon.getImage().getScaledInstance(calculateButtonSize(), calculateButtonSize(),
                Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }

    // Calculate the button size based on the minimum dimension of the JFrame
    private int calculateButtonSize() {
        return Math.min(getWidth() / 8, getHeight() / 8);
    }

    // Method to update button sizes
    private void updateButtonSizes(int buttonSize) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                boardButtons[i][j].setPreferredSize(new Dimension(buttonSize, buttonSize));
            }
        }

        // Revalidate and repaint the JFrame to reflect the changes
        revalidate();
        repaint();
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
            System.out.println("Button Clicked: " + row + ", " + col);
            if (selectedRow == -1 && selectedCol == -1 && isCurrentPlayerPiece(row, col)) {
                // No piece selected, so select the clicked piece
                selectedRow = row;
                selectedCol = col;

                // Highlight the selected piece
                boardButtons[selectedRow][selectedCol].setBackground(Color.YELLOW);
            } else if (selectedRow != -1 && selectedCol != -1 && isValidMove(selectedRow, selectedCol, row, col)) {
                // A piece is already selected, so attempt to move the piece to the clicked
                // position
                // Perform the move
                System.out.println("Moving from: " + selectedRow + ", " + selectedCol + " to " + row + ", " + col);
                board[row][col] = board[selectedRow][selectedCol];
                board[selectedRow][selectedCol] = 0; // Clear the source position

                // Handle capturing (removing) opponent's piece if applicable
                handleCapture(selectedRow, selectedCol, row, col);

                // Check for additional forced jumps
                boolean hasMoreForcedJumps = checkForForcedJumps(row, col);

                // If there are more forced jumps, keep the current player and update the GUI
                if (hasMoreForcedJumps) {
                    // Update the GUI after the move
                    updateBoardGUI();

                    // Highlight the newly selected piece for the next jump
                    selectedRow = row;
                    selectedCol = col;
                    boardButtons[selectedRow][selectedCol].setBackground(Color.YELLOW);
                } else {
                    // Switch the player for the next move
                    currentPlayer = (currentPlayer == 1) ? 2 : 1;

                    // Update the GUI after the move
                    updateBoardGUI();

                    // Reset the selected piece and remove highlight
                    boardButtons[selectedRow][selectedCol]
                            .setBackground((selectedRow + selectedCol) % 2 == 0 ? Color.WHITE : Color.BLACK);
                    selectedRow = -1;
                    selectedCol = -1;

                    // Check if the game is finished
                    if (isGameFinished()) {
                        announceWinner();
                        // Add any further logic for the end of the game here
                    } else {
                        // Continue with the next player's turn
                        playTurn();
                    }
                }
            } else {
                // Deselect the piece if an invalid move is attempted
                selectedRow = -1;
                selectedCol = -1;

                // Remove highlight from the previously selected piece
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        boardButtons[i][j].setBackground((i + j) % 2 == 0 ? Color.WHITE : Color.BLACK);
                    }
                }
            }
        }

        private boolean isCurrentPlayerPiece(int row, int col) {
            return board[row][col] == currentPlayer;
        }

        private boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
            // Check if capturing is valid (only allowed diagonally)
            if (Math.abs(toRow - fromRow) == 2 && Math.abs(toCol - fromCol) == 2) {
                // Determine the position of the captured piece
                int capturedRow = (fromRow + toRow) / 2;
                int capturedCol = (fromCol + toCol) / 2;

                // Check if the captured piece belongs to the opponent
                if (board[capturedRow][capturedCol] == getOpponentPlayer()) {
                    // Valid capturing move
                    board[capturedRow][capturedCol] = 0;
                    System.out.println("Captured opponent's piece at: " + capturedRow + ", " + capturedCol);
                    return true;
                } else {
                    System.out.println("Invalid move: Cannot capture own piece");
                    return false; // Attempting to capture own piece
                }
            }

            // Implement logic to check if the move is valid
            // For simplicity, we'll start with basic bounds checking
            if (toRow < 0 || toRow >= 8 || toCol < 0 || toCol >= 8) {
                System.out.println("Invalid move: Out of bounds");
                return false; // Move is out of bounds
            }

            // Ensure the destination is an empty square
            if (board[toRow][toCol] != 0) {
                System.out.println("Invalid move: Destination is not empty");
                return false; // Destination is not empty
            }

            // Regular pieces move forward only (for player 1, move downward; for player 2,
            // move upward)
            int direction = (currentPlayer == 1) ? 1 : -1;

            // Check for capturing moves
            if (Math.abs(toRow - fromRow) == 2 && Math.abs(toCol - fromCol) == 2) {
                // Check if the capturing move is in the correct direction
                if (direction * (toRow - fromRow) > 0) {
                    return true; // Valid capturing move
                } else {
                    System.out.println("Invalid move: Backward capturing not allowed");
                    return false; // Backward capturing is not allowed
                }
            }

            // Check for regular moves
            if (toRow != fromRow + direction || Math.abs(toCol - fromCol) != 1) {
                System.out.println("Invalid move: Incorrect move for a regular piece");
                return false; // Invalid move for a regular piece
            }

            return true; // Regular move
        }

        private int getOpponentPlayer() {
            return (currentPlayer == 1) ? 2 : 1;
        }

        private void handleCapture(int fromRow, int fromCol, int toRow, int toCol) {
            // Check if capturing is valid (only allowed diagonally)
            if (Math.abs(toRow - fromRow) == 2 && Math.abs(toCol - fromCol) == 2) {
                // Determine the position of the captured piece
                int capturedRow = (fromRow + toRow) / 2;
                int capturedCol = (fromCol + toCol) / 2;

                // Remove the captured piece
                board[capturedRow][capturedCol] = 0;
                System.out.println("Captured opponent's piece at: " + capturedRow + ", " + capturedCol);
            }
        }
    }

    private void updateBoardGUI() {
        // Update the graphical representation of the board after a move
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                updateButtonIcon(i, j);
            }
        }
    }

    private boolean isGameFinished() {
        int player1Count = 0;
        int player2Count = 0;

        // Count the remaining pieces for each player
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == 1) {
                    player1Count++;
                } else if (board[i][j] == 2) {
                    player2Count++;
                }
            }
        }

        // Game ends if all pieces of one player are captured or each player has only
        // one piece left
        return player1Count == 0 || player2Count == 0 || (player1Count == 1 && player2Count == 1);
    }

    private void announceWinner() {
        int player1Count = 0;
        int player2Count = 0;

        // Count the remaining pieces for each player
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == 1) {
                    player1Count++;
                } else if (board[i][j] == 2) {
                    player2Count++;
                }
            }
        }

        // Determine the winner or announce a tie
        if (player1Count > player2Count) {
            resultLabel.setText("Player 1 wins!");
        } else if (player2Count > player1Count) {
            resultLabel.setText("Player 2 wins!");
        } else {
            resultLabel.setText("It's a tie!");
        }

        // Disable board buttons after the game is finished
        disableBoardButtons();
    }

    private void disableBoardButtons() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                boardButtons[i][j].setEnabled(false);
            }
        }
    }

    private boolean checkForForcedJumps() {
        // Iterate through the entire board to find pieces that can perform forced jumps
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == currentPlayer) {
                    // Check for forced jumps starting from this position
                    if (checkForForcedJumps(i, j)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Add this method inside your CheckersGame class
    private boolean checkForForcedJumps(int startRow, int startCol) {
        // Check if forced jumps are available from the given position
        int direction = (currentPlayer == 1) ? 1 : -1;
        boolean forcedJumpAvailable = false;

        // Check for capturing moves diagonally
        int[][] capturingMoves = { { 2, 2 }, { 2, -2 }, { -2, 2 }, { -2, -2 } };

        for (int[] move : capturingMoves) {
            int toRow = startRow + direction * move[0];
            int toCol = startCol + move[1];

            int capturedRow = startRow + direction;
            int capturedCol = startCol + move[1] / 2;

            // Check if the move is within bounds
            if (toRow >= 0 && toRow < 8 && toCol >= 0 && toCol < 8) {
                // Check if the move captures an opponent's piece
                if (board[toRow][toCol] == 0 && capturedRow >= 0 && capturedRow < 8 && capturedCol >= 0
                        && capturedCol < 8 && board[capturedRow][capturedCol] == (currentPlayer == 1 ? 2 : 1)) {
                    forcedJumpAvailable = true;
                    break;
                }
            }
        }

        return forcedJumpAvailable;
    }

    // Replace the existing makeForcedJump method with this inside your CheckersGame
    // class
    private void makeForcedJump(int startRow, int startCol, int toRow, int toCol) {
        // Check if the destination row and column are within bounds
        if (toRow >= 0 && toRow < 8 && toCol >= 0 && toCol < 8) {
            // Perform the forced jump
            board[toRow][toCol] = board[startRow][startCol];
            board[startRow][startCol] = 0;

            // Determine the position of the captured piece
            int capturedRow = (startRow + toRow) / 2;
            int capturedCol = (startCol + toCol) / 2;

            // Remove the captured piece
            board[capturedRow][capturedCol] = 0;
            System.out.println("Forced jump: Captured opponent's piece at " + capturedRow + ", " + capturedCol);

            // Update the GUI after the forced jump
            updateBoardGUI();

            // Check for consecutive forced jumps
            if (checkForForcedJumps(toRow, toCol)) {
                // Continue with the next forced jump
                System.out.println("Consecutive forced jump");
                makeForcedJumpAutomatically(toRow, toCol);
            } else {
                // Switch to the next player for the next move
                currentPlayer = (currentPlayer == 1) ? 2 : 1;
                System.out.println("Switching to Player " + currentPlayer + "'s turn");

                // Reset the selected piece and remove highlight
                boardButtons[startRow][startCol]
                        .setBackground((startRow + startCol) % 2 == 0 ? Color.WHITE : Color.BLACK);
                selectedRow = -1;
                selectedCol = -1;

                // Check if the game is finished
                if (isGameFinished()) {
                    announceWinner();
                } else {
                    // Continue with the next player's turn
                    playTurn();
                }
            }
        } else {
            // Log an error or handle the case where the destination is out of bounds
            System.out.println("Error: Destination is out of bounds");
        }
    }

    private void playTurn() {
        // Check if there are any forced jumps
        boolean hasForcedJumps = checkForForcedJumps(selectedRow, selectedCol);

        if (hasForcedJumps) {
            // Forced jumps exist, make the forced jump automatically
            makeForcedJumpAutomatically(selectedRow, selectedCol);
        } else {
            // No forced jumps, proceed with regular moves
            System.out.println("Player " + currentPlayer + ": Make a move");
        }
    }

    // Add this method inside your CheckersGame class
    private void makeForcedJumpAutomatically(int startRow, int startCol) {
        // Check if forced jumps are available from the given position
        int direction = (currentPlayer == 1) ? 1 : -1;

        // Check for capturing moves diagonally
        int[][] capturingMoves = { { 2, 2 }, { 2, -2 }, { -2, 2 }, { -2, -2 } };

        for (int[] move : capturingMoves) {
            int toRow = startRow + direction * move[0];
            int toCol = startCol + move[1];

            int capturedRow = startRow + direction;
            int capturedCol = startCol + move[1] / 2;

            // Check if the move is within bounds and captures an opponent's piece
            if (toRow >= 0 && toRow < 8 && toCol >= 0 && toCol < 8 &&
                    board[toRow][toCol] == 0 && board[capturedRow][capturedCol] == (currentPlayer == 1 ? 2 : 1)) {
                // Perform the forced jump automatically
                makeForcedJump(startRow, startCol, toRow, toCol);

                // Check if the moved piece reached the upper row
                if (toRow == 0 && board[toRow][toCol] == 1) {
                    // Convert the piece to a red queen
                    board[toRow][toCol] = 3; // Use a different value for queen (e.g., 3)
                    updateButtonIcon(toRow, toCol);
                } else if (toRow == 7 && board[toRow][toCol] == 2) {
                    // Convert the piece to a black queen
                    board[toRow][toCol] = 4; // Use a different value for black queen (e.g., 4)
                    updateButtonIcon(toRow, toCol);
                }

                return; // Stop after making one forced jump
            }
        }
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
        SwingUtilities.invokeLater(() -> new CheckersGame());
    }
}
