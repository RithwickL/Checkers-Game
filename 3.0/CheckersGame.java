import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

/**
 * The {@code CheckersGame} class represents a graphical Checkers game with
 * optional AI player.
 * Players can make moves by clicking on the board, and an AI player can be
 * activated for single-player mode. The game provides a GUI interface and
 * handles the game logic, player turns, and win conditions.
 *
 * <p>
 * <strong>Usage:</strong>
 *
 * <pre>{@code
 * // Example: Creating a new Checkers game with an AI player
 * CheckersGame checkersGame = new CheckersGame(true);
 * }</pre>
 *
 * <p>
 * <strong>Note:</strong>
 * The class includes methods for handling player moves, AI turns, updating the
 * graphical board, and checking win or stalemate conditions. The game supports
 * regular and king checker pieces. Users can toggle full-screen mode using the
 * Ctrl + F shortcut.
 *
 * <p>
 * <strong>Implementation Details:</strong>
 * The code is organized with methods for initializing the board, handling
 * player moves, managing AI turns, and updating the GUI. The game supports both
 * regular and king checker pieces, and the AI player makes random moves with a
 * delay. The {@code CheckersGame} class extends JFrame for creating the
 * graphical interface.
 *
 * @author Rithwick Lakshmanan
 * @version 3.0
 * @see CheckerPiece
 * @see RegularPiece
 * @see KingPiece
 * @see MoveBlack
 * @see MoveRed
 * @see AI
 */

// Main Class
public class CheckersGame extends JFrame {
    public JButton[][] boardButtons = new JButton[8][8];
    public CheckerPiece[][] checkerPieces = new CheckerPiece[8][8];
    private boolean isPlayerTurn = true;
    private int selectedRow = -1;
    private int selectedCol = -1;
    public int Redtie = 0;
    public int Blacktie = 0;
    private boolean AIActive;
    private static final int AI_DELAY_MS = 1000;
    private int selectedSquareRow = -1;
    private int selectedSquareCol = -1;
    Color myBrown = new Color(92, 64, 51);
    Color myLightBrown = new Color(196, 164, 132);

    // If the Boolean is true the AI will play, but is its false its two players
    public CheckersGame(boolean isAIPlayer) {
        setTitle("Checkers Game");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(8, 8));
        gamePanel.setPreferredSize(new Dimension(800, 800));

        initializeBoard(gamePanel);
        initializeCheckerPieces();
        addMouseListeners();

        add(gamePanel);

        setFocusable(true);
        requestFocus();

        setVisible(true);

        if (!isPlayerTurn && AIActive) {
            handleAITurn();
        }
    }

    // Makes the Board with the alternating colors
    private void initializeBoard(JPanel gamePanel) {
        clearConsole();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                boardButtons[i][j] = new JButton();
                boardButtons[i][j].setPreferredSize(new Dimension(100, 100));

                if ((i + j) % 2 == 0) {
                    boardButtons[i][j].setBackground(myLightBrown);
                } else {
                    boardButtons[i][j].setBackground(myBrown);
                }

                boardButtons[i][j].setOpaque(true);
                boardButtons[i][j].setContentAreaFilled(true);

                gamePanel.add(boardButtons[i][j]);
            }
        }

        gamePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK), "toggleFullScreen");
        gamePanel.getActionMap().put("toggleFullScreen", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleFullScreen();
            }
        });
    }

    // Adds all the checkers Pieces
    private void initializeCheckerPieces() {
        clearConsole();

        for (int row = 0; row < 3; row++) {
            for (int col = row % 2; col < 8; col += 2) {
                placePiece(row, col, Color.BLACK, false);
            }
        }

        for (int row = 5; row < 8; row++) {
            for (int col = row % 2; col < 8; col += 2) {
                placePiece(row, col, Color.RED, false);
            }
        }
    }

    // Code to detect Mouse Clicks
    private void addMouseListeners() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                final int row = i;
                final int col = j;

                boardButtons[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        handleMove(row, col, col, col);
                    }
                });
            }
        }
    }

    private void checkForWin() {
        // Check if all pieces of a color are eliminated
        boolean player1PiecesRemaining = false;
        boolean player2PiecesRemaining = false;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (checkerPieces[i][j] != null) {
                    if (checkerPieces[i][j].getColor() == Color.RED) {
                        player1PiecesRemaining = true;
                    } else if (checkerPieces[i][j].getColor() == Color.BLACK) {
                        player2PiecesRemaining = true;
                    }
                }
            }
        }
        Redtie = 0;
        Blacktie = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (checkerPieces[i][j] != null) {
                    if (checkerPieces[i][j].getColor() == Color.RED) {
                        Redtie = Redtie + 1;
                        System.out.println("Redtie: " + Redtie);
                    } else if (checkerPieces[i][j].getColor() == Color.BLACK) {
                        Blacktie = Blacktie + 1;
                        System.out.println("Blacktie: " + Blacktie);

                    }
                }
            }
        }

        // Display win screen or stalemate screen
        if (!player1PiecesRemaining) {
            JOptionPane.showMessageDialog(this,
                    "Player 2 / AI wins! Also wanna hear a joke: Why was the equal sign so humble? It knew it wasn't less than or greater than anyone else!",
                    "Game Over", JOptionPane.INFORMATION_MESSAGE);
            endGame();
        } else if (!player2PiecesRemaining) {
            JOptionPane.showMessageDialog(this,
                    "Player 1 wins! Also wanna hear a joke: Why was the equal sign so humble? It knew it wasn't less than or greater than anyone else!",
                    "Game Over", JOptionPane.INFORMATION_MESSAGE);
            endGame();
        } else if (Redtie == 1 && Blacktie == 1) {
            JOptionPane.showMessageDialog(this,
                    "It's a tie! Also wanna hear a joke: Why was the equal sign so humble? It knew it wasn't less than or greater than anyone else! ",
                    "Game Over",
                    JOptionPane.INFORMATION_MESSAGE);
            endGame();
        }
    }

    // Method to end the game
    private void endGame() {
        System.exit(0);
    }

    // Code to handle AI Moves on the board with normal Function like jumps
    // Modify the handleAITurn method
    private void handleAITurn() {
        Timer timer = new Timer(AI_DELAY_MS, e -> {
            MoveBlack aiMove;

            // Make a specific move with a front-row black piece
            if (Blacktie == 12) {
                aiMove = new MoveBlack(7, 0, 5, 2); // Example move from (7, 0) to (5, 2)
            } else {
                // If not a specific move, choose a random move
                aiMove = chooseRandomAIMove();
            }

            if (aiMove != null) {
                System.out.println("AI Move: " + aiMove);
                updateBoard();

                if (Math.abs(aiMove.getToRow() - aiMove.getFromRow()) == 2
                        && Math.abs(aiMove.getToCol() - aiMove.getFromCol()) == 2) {
                    if (canJumpAgain(aiMove.getToRow(), aiMove.getToCol())) {
                        handleAITurn();
                        return;
                    }
                }

                isPlayerTurn = true; // Now it's the player's turn

            }
        });

        timer.setRepeats(false);
        timer.start();
    }

    // Choose a random AI move for black pieces
    private MoveBlack chooseRandomAIMove() {
        // List to store all available moves for black pieces
        List<MoveBlack> availableMoves = new ArrayList<>();

        // Iterate through all black pieces
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (checkerPieces[i][j] != null && checkerPieces[i][j].getColor() == Color.BLACK) {
                    int[][] moveOffsets = {
                            { 1, -1 }, { 1, 1 }, { 2, -2 }, { 2, 2 },
                            { -1, -1 }, { -1, 1 }, { -2, -2 }, { -2, 2 }
                    };

                    // Check available moves for the current black piece
                    for (int k = 0; k < moveOffsets.length; k++) {
                        int toRow = i + moveOffsets[k][0];
                        int toCol = j + moveOffsets[k][1];

                        if (isValidMove(i, j, toRow, toCol)) {
                            availableMoves.add(new MoveBlack(i, j, toRow, toCol));
                        }
                    }
                }
            }
        }

        if (!availableMoves.isEmpty()) {
            // Randomly select a move from the list of available moves
            return availableMoves.get(new Random().nextInt(availableMoves.size()));
        }

        System.out.println("No available AI moves for black pieces.");
        System.out.println("Number of available AI moves: 1");

        return null;
    }

    private void handleAIMove(MoveBlack aiMove) {
        // Perform the chosen move using moveCheckerPiece
        moveCheckerPiece(aiMove.getFromRow(), aiMove.getFromCol(), aiMove.getToRow(), aiMove.getToCol());
        updateBoard();

        if (Math.abs(aiMove.getToRow() - aiMove.getFromRow()) == 2
                && Math.abs(aiMove.getToCol() - aiMove.getFromCol()) == 2) {
            if (canJumpAgain(aiMove.getToRow(), aiMove.getToCol())) {
                handleAIMove(chooseRandomAIMove());
                return;
            }
        }

        isPlayerTurn = !isPlayerTurn;

        if (!isPlayerTurn && AIActive) {
            handleAIMove(chooseRandomAIMove());
        }
    }

    // Puts the pieces on the board on board updates
    public void placePiece(int row, int col, Color color, boolean isKing) {
        if (isKing) {
            checkerPieces[row][col] = new KingPiece(color, row);
        } else {
            checkerPieces[row][col] = new RegularPiece(color, row);
        }

        boardButtons[row][col].setIcon(createCircleIcon(color, isKing));
    }

    // Handles Players movements
    public void handleMove(int row, int col, int toRow, int toCol) {
        if (selectedRow == -1 && selectedCol == -1) {
            // Player selects a piece to move
            if ((isPlayerTurn && checkerPieces[row][col] != null && checkerPieces[row][col].getColor() == Color.RED) ||
                    (!isPlayerTurn && !AIActive && checkerPieces[row][col] != null &&
                            checkerPieces[row][col].getColor() == Color.BLACK)) {

                selectedRow = row;
                selectedCol = col;
                selectedSquareRow = row;
                selectedSquareCol = col;
                boardButtons[selectedRow][selectedCol].setBackground(Color.YELLOW);
                System.out.println("Selected Piece: (" + selectedRow + ", " + selectedCol + ")");
            }
        } else {
            // Player makes a move
            if (selectedRow == row && selectedCol == col) {
                // Player cancels the selection
                boardButtons[selectedSquareRow][selectedSquareCol].setBackground(
                        (selectedSquareRow + selectedSquareCol) % 2 == 0 ? myLightBrown : myBrown);
                selectedRow = -1;
                selectedCol = -1;
                selectedSquareRow = -1;
                selectedSquareCol = -1;

            } else {
                if (isValidMove(selectedRow, selectedCol, row, col)) {
                    moveCheckerPiece(selectedRow, selectedCol, row, col);
                    updateBoard();

                    // Check for additional jumps
                    if (Math.abs(row - selectedRow) == 2 && Math.abs(col - selectedCol) == 2) {
                        // Player can jump again
                        if (canJumpAgain(row, col)) {
                            handleAITurn(); // Trigger AI turn
                            return;
                        }
                    }

                    isPlayerTurn = !isPlayerTurn;

                    boardButtons[selectedSquareRow][selectedSquareCol]
                            .setBackground((selectedSquareRow + selectedSquareCol) % 2 == 0 ? myLightBrown : myBrown);
                    selectedRow = -1;
                    selectedCol = -1;
                    selectedSquareRow = -1;
                    selectedSquareCol = -1;

                    System.out
                            .println("Move: (" + selectedRow + ", " + selectedCol + ") to (" + row + ", " + col + ")");

                    // After player move, trigger AI turn
                    if (!isPlayerTurn && AIActive) {
                        handleAITurn();
                    }
                } else {
                    // Invalid move, cancel selection
                    boardButtons[selectedSquareRow][selectedSquareCol]
                            .setBackground((selectedSquareRow + selectedSquareCol) % 2 == 0 ? myLightBrown : myBrown);
                    selectedRow = -1;
                    selectedCol = -1;
                    selectedSquareRow = -1;
                    selectedSquareCol = -1;
                }
            }
        }
        checkForWin();
    }

    // A boolean that checks if the player or AI can jump another piece again
    private boolean canJumpAgain(int row, int col) {
        int[][] possibleJumps = {
                { row - 2, col - 2, row - 1, col - 1 },
                { row - 2, col + 2, row - 1, col + 1 },
                { row + 2, col - 2, row + 1, col - 1 },
                { row + 2, col + 2, row + 1, col + 1 }
        };

        for (int[] jump : possibleJumps) {
            int toRow = jump[0];
            int toCol = jump[1];
            int jumpedRow = jump[2];
            int jumpedCol = jump[3];

            if (isValidMove(row, col, toRow, toCol) && checkerPieces[jumpedRow][jumpedCol] != null) {
                return true;
            }
        }

        return false;
    }

    // Checkes if the move is allowed in checkers
    public boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (fromRow >= 0 && fromRow < 8 && fromCol >= 0 && fromCol < 8 &&
                toRow >= 0 && toRow < 8 && toCol >= 0 && toCol < 8 &&
                checkerPieces[fromRow][fromCol] != null && checkerPieces[toRow][toCol] == null) {
            System.out.println("Checking move validity: " + fromRow + ", " + fromCol + " to " + toRow + ", " + toCol);

            if ((isPlayerTurn && checkerPieces[fromRow][fromCol].getColor() == Color.RED) ||
                    (!isPlayerTurn && checkerPieces[fromRow][fromCol].getColor() == Color.BLACK)) {

                int rowDiff = toRow - fromRow;
                int colDiff = Math.abs(toCol - fromCol);

                if (!checkerPieces[fromRow][fromCol].isKing()) {
                    // Check if regular pieces are moving forward
                    if ((isPlayerTurn && rowDiff > 0) || (!isPlayerTurn && rowDiff < 0)) {
                        if ((toRow == 0 && checkerPieces[fromRow][fromCol].getColor() == Color.BLACK) ||
                                (toRow == 7 && checkerPieces[fromRow][fromCol].getColor() == Color.RED)) {
                            if (Math.abs(rowDiff) == 1 && colDiff == 1) {
                                // Only make it a king if it's a regular move to the last row of the
                                // opposite color
                                checkerPieces[fromRow][fromCol] = new KingPiece(
                                        checkerPieces[fromRow][fromCol].getColor(),
                                        fromRow);
                                boardButtons[fromRow][fromCol]
                                        .setIcon(createCircleIcon(checkerPieces[fromRow][fromCol].getColor(), true));
                                return true;
                            } else {
                                return false;
                            }
                        } else {
                            // Regular pieces should not capture backward
                            return false;
                        }
                    }
                }

                if (Math.abs(rowDiff) == 1 && colDiff == 1) {
                    return true;
                } else if (Math.abs(rowDiff) == 2 && colDiff == 2) {
                    int capturedRow = (fromRow + toRow) / 2;
                    int capturedCol = (fromCol + toCol) / 2;
                    return checkerPieces[capturedRow][capturedCol] != null &&
                            checkerPieces[capturedRow][capturedCol].getColor() != checkerPieces[fromRow][fromCol]
                                    .getColor();
                }

                if (checkerPieces[fromRow][fromCol].isKing()) {
                    return Math.abs(rowDiff) == 1 && colDiff == 1;
                }
            }
        }

        return false;
    }

    // Moves the checker piece from one square to another based on input
    public void moveCheckerPiece(int fromRow, int fromCol, int toRow, int toCol) {
        if (checkerPieces[fromRow][fromCol] != null) {
            checkerPieces[toRow][toCol] = checkerPieces[fromRow][fromCol];
            checkerPieces[fromRow][fromCol] = null;

            if ((toRow == 0 && checkerPieces[toRow][toCol].getColor() == Color.RED) ||
                    (toRow == 7 && checkerPieces[toRow][toCol].getColor() == Color.BLACK)) {
                checkerPieces[toRow][toCol] = new KingPiece(checkerPieces[toRow][toCol].getColor(), toRow);
            }

            int jumpedRow = (fromRow + toRow) / 2;
            int jumpedCol = (fromCol + toCol) / 2;
            if (Math.abs(toRow - fromRow) == 2 && Math.abs(toCol - fromCol) == 2) {
                checkerPieces[jumpedRow][jumpedCol] = null;
            }
        }
    }

    // Updates the board everytime a piece moves graphically
    public void updateBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (checkerPieces[i][j] != null) {
                    placePiece(i, j, checkerPieces[i][j].getColor(), checkerPieces[i][j].isKing());
                } else {
                    boardButtons[i][j].setIcon(null);
                }
            }
        }
    }

    // Makes the circle Black and Red piece on the board to interact with the user
    private ImageIcon createCircleIcon(Color color, boolean isKing) {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(color);
        g2d.fillOval(10, 10, 80, 80);

        if (isKing) {
            g2d.setColor(Color.YELLOW);

            int[] xPoints = { 45, 55, 70, 55, 65, 45, 25, 35, 10, 25 };
            int[] yPoints = { 25, 40, 40, 50, 65, 55, 65, 50, 40, 40 };
            g2d.fillPolygon(xPoints, yPoints, 10);
        }

        g2d.dispose();
        return new ImageIcon(image);
    }

    // If Crt + F is used then it becomes full screen based on the size of screen
    // played on
    private void toggleFullScreen() {
        if (getExtendedState() == JFrame.MAXIMIZED_BOTH) {
            setExtendedState(JFrame.NORMAL);
        } else {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
    }

    // Cleans previous debugging cluter on the console everytime a new game is ran
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
}

// Is used to return the color and row/colum of the piece
class CheckerPiece {
    private Color color;

    public CheckerPiece(Color color, int row) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public boolean isKing() {
        return false;
    }
}

// is used to make normal pieces
class RegularPiece extends CheckerPiece {
    public RegularPiece(Color color, int row) {
        super(color, row);
    }
}

// is used to make new king pieces
class KingPiece extends CheckerPiece {
    public KingPiece(Color color, int row) {
        super(color, row);
    }

    @Override
    public boolean isKing() {
        return true;
    }
}

// is used to make the red pieces move forward or backward if king
class MoveRed {
    private int fromRow;
    private int fromCol;
    private int toRow;
    private int toCol;

    public MoveRed(int fromRow, int fromCol, int toRow, int toCol) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
    }

    public int getFromRow() {
        return fromRow;
    }

    public int getFromCol() {
        return fromCol;
    }

    public int getToRow() {
        return toRow;
    }

    public int getToCol() {
        return toCol;
    }

    @Override
    public String toString() {
        return "(" + fromRow + ", " + fromCol + ") to (" + toRow + ", " + toCol + ")";
    }
}
// is used to make the black pieces move forward or backward if king

class MoveBlack {
    private int fromRow;
    private int fromCol;
    private int toRow;
    private int toCol;

    public MoveBlack(int fromRow, int fromCol, int toRow, int toCol) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
    }

    public int getFromRow() {
        return fromRow;
    }

    public int getFromCol() {
        return fromCol;
    }

    public int getToRow() {
        return toRow;
    }

    public int getToCol() {
        return toCol;
    }

    @Override
    public String toString() {
        return "(" + fromRow + ", " + fromCol + ") to (" + toRow + ", " + toCol + ")";
    }
}