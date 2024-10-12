import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.ArrayList;

public class CheckersGame123 extends JFrame {
    public JButton[][] checkersBoard = new JButton[8][8];
    public CheckersPiece[][] checkersPieces = new CheckersPiece[8][8];
    private boolean isPlayerTurn = true;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private boolean isAIPlayer; // Flag to indicate if the AI is playing
    private static final int AI_DELAY_MS = 500; // Adjust the delay for AI moves

    public CheckersGame(boolean isAIPlayer) {
        setTitle("Checkers Game");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        this.isAIPlayer = isAIPlayer;

        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(8, 8));
        gamePanel.setPreferredSize(new Dimension(800, 800));

        initializeCheckersBoard(gamePanel);
        initializeCheckersPieces();
        addMouseListeners();

        add(gamePanel);

        setFocusable(true);
        requestFocus();

        setVisible(true);

        if (isAIPlayer) {
            // AI is playing, start the AI's turn
            handleAITurn();
        }
    }
    private void initializeCheckersBoard(JPanel gamePanel) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                checkersBoard[i][j] = new JButton();
                checkersBoard[i][j].setPreferredSize(new Dimension(100, 100));

                // Set background colors based on positions
                if ((i + j) % 2 == 0) {
                    checkersBoard[i][j].setBackground(Color.WHITE);
                } else {
                    checkersBoard[i][j].setBackground(Color.GRAY);
                }

                checkersBoard[i][j].setOpaque(true);
                checkersBoard[i][j].setContentAreaFilled(true);

                gamePanel.add(checkersBoard[i][j]);
            }
        }

        // Add key listener for toggling full-screen mode (Ctrl + F)
        gamePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK), "toggleFullScreen");
        gamePanel.getActionMap().put("toggleFullScreen", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleFullScreen();
            }
        });
    }
    private void initializeCheckersPieces() {
        placePiece(0, 0, Color.BLACK,false);
        placePiece(0, 2, Color.BLACK,false);
        placePiece(0, 4, Color.BLACK,false);
        placePiece(0, 6, Color.BLACK,false);

        placePiece(1, 1, Color.BLACK,false);
        placePiece(1, 3, Color.BLACK,false);
        placePiece(1, 5, Color.BLACK,false);
        placePiece(1, 7, Color.BLACK,false);

        placePiece(2, 0, Color.BLACK,false);
        placePiece(2, 2, Color.BLACK,false);
        placePiece(2, 4, Color.BLACK,false);
        placePiece(2, 6, Color.BLACK,false);

        placePiece(5, 1, Color.RED,false);
        placePiece(5, 3, Color.RED,false);
        placePiece(5, 5, Color.RED,false);
        placePiece(5, 7, Color.RED,false);

        placePiece(6, 0, Color.RED,false);
        placePiece(6, 2, Color.RED,false);
        placePiece(6, 4, Color.RED,false);
        placePiece(6, 6, Color.RED,false);

        placePiece(7, 1, Color.RED,false);
        placePiece(7, 3, Color.RED,false);
        placePiece(7, 5, Color.RED,false);
        placePiece(7, 7, Color.RED,false);
    }

    private void addMouseListeners() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                final int row = i;
                final int col = j;

                checkersBoard[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        handleMove(row, col);
                    }
                });
            }
        }
    }
   private void handleAITurn() {
    Timer timer = new Timer(AI_DELAY_MS, e -> {
        Move aiMove = chooseRandomAIMove();
		 System.out.println("AI Move: " + aiMove);
        if (aiMove != null) {
            moveCheckersPiece(aiMove.getFromRow(), aiMove.getFromCol(), aiMove.getToRow(), aiMove.getToCol());
            updateCheckersBoard();

            if (Math.abs(aiMove.getToRow() - aiMove.getFromRow()) == 2
                    && Math.abs(aiMove.getToCol() - aiMove.getFromCol()) == 2) {
                if (canJumpAgain(aiMove.getToRow(), aiMove.getToCol())) {
                    // The AI can make another jump, handle it
                    handleAITurn();
                    return;
                }
            }

            // Switch turns
            isPlayerTurn = !isPlayerTurn;

            if (!isPlayerTurn && isAIPlayer) {
                // If it's still the AI's turn, continue with the AI's moves
                handleAITurn();
            }
        }
    });

    timer.setRepeats(false);
    timer.start();
}

  private Move chooseRandomAIMove() {
    List<Move> allMoves = new ArrayList<>();

    // Iterate through all pieces on the board
    for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
            if (checkersPieces[i][j] != null && checkersPieces[i][j].getColor() == Color.BLACK) {
                // Get available moves for the current piece
                List<Move> movesForPiece = getAllAvailableMoves(i, j);
                allMoves.addAll(movesForPiece);

                System.out.println("Piece at " + i + ", " + j + " has moves: " + movesForPiece);
            }
        }
    }

    if (!allMoves.isEmpty()) {
        // Randomly select a move from the list of all available moves
        Move randomMove = allMoves.get((int) (Math.random() * allMoves.size()));
        System.out.println("Selected AI Move: " + randomMove);
        return new Move(randomMove.getFromRow(), randomMove.getFromCol(), randomMove.getToRow(), randomMove.getToCol());
    }

    return null;
}




 private List<Move> getAllAvailableMoves(int fromRow, int fromCol) {
    List<Move> availableMoves = new ArrayList<>();

    int[][] moveOffsets = {
            {-1, -1}, {-1, 1}
    };

    for (int[] offset : moveOffsets) {
        int toRow = fromRow + offset[0];
        int toCol = fromCol + offset[1];

        // Check if the move is within bounds
        if (toRow >= 0 && toRow < 8 && toCol >= 0 && toCol < 8) {
            System.out.println("Checking move: " + fromRow + "," + fromCol + " to " + toRow + "," + toCol);

            if (isValidMove(fromRow, fromCol, toRow, toCol)) {
                System.out.println("Move is valid.");
                availableMoves.add(new Move(fromRow, fromCol, toRow, toCol));
            } else {
                System.out.println("Move is not valid.");
            }
        } else {
            System.out.println("Move is out of bounds.");
        }
    }

    return availableMoves;
}





public void placePiece(int row, int col, Color color, boolean isKing) {
    if (isKing) {
        checkersPieces[row][col] = new KingPiece(color, row);
    } else {
        checkersPieces[row][col] = new RegularPiece(color, row);
    }

    checkersBoard[row][col].setIcon(createCircleIcon(color, isKing));
}


public void handleMove(int row, int col) {
    if (selectedRow == -1 && selectedCol == -1) {
        // No piece selected yet, check if the clicked position has a piece
        if (checkersPieces[row][col] != null) {
            if ((isPlayerTurn && checkersPieces[row][col].getColor() == Color.RED) ||
                (!isPlayerTurn && checkersPieces[row][col].getColor() == Color.BLACK)) {
                selectedRow = row;
                selectedCol = col;
                System.out.println("Selected piece at row " + row + ", col " + col);
            } else {
                System.out.println("It's not your turn to move this piece");
            }
        }
    } else {
        // A piece is already selected
        if (selectedRow == row && selectedCol == col) {
            // Clicked on the same piece twice, deselect it
            selectedRow = -1;
            selectedCol = -1;
            System.out.println("Deselected piece");
        } else {
            // Move the selected piece to the clicked position
            if (isValidMove(selectedRow, selectedCol, row, col)) {
                moveCheckersPiece(selectedRow, selectedCol, row, col);
                updateCheckersBoard();

                // Check for additional jumps only if capturing an opponent's piece
                if (Math.abs(row - selectedRow) == 2 && Math.abs(col - selectedCol) == 2) {
                    if (canJumpAgain(row, col)) {
                        // Allow the player to make consecutive jumps
                        System.out.println("You can jump again!");
                        return;
                    }
                }

                // Switch turns only after the initial jump
                isPlayerTurn = !isPlayerTurn;
                System.out.println("Moved piece to row " + row + ", col " + col);

                // Deselect the piece after the move is completed
                selectedRow = -1;
                selectedCol = -1;
            } else {
                System.out.println("Invalid move");
                // Keep the deselection here if the move is not valid
                selectedRow = -1;
                selectedCol = -1;
            }
        }
    }
}






    private boolean canJumpAgain(int row, int col) {
        // Check if the piece at the new location can make another jump
        int[][] possibleJumps = {
                {row - 2, col - 2, row - 1, col - 1},
                {row - 2, col + 2, row - 1, col + 1},
                {row + 2, col - 2, row + 1, col - 1},
                {row + 2, col + 2, row + 1, col + 1}
        };

        for (int[] jump : possibleJumps) {
            int toRow = jump[0];
            int toCol = jump[1];
            int jumpedRow = jump[2];
            int jumpedCol = jump[3];

            if (isValidMove(row, col, toRow, toCol) && checkersPieces[jumpedRow][jumpedCol] != null) {
                return true;
            }
        }

        return false;
    }

  public boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
    if (fromRow >= 0 && fromRow < 8 && fromCol >= 0 && fromCol < 8 &&
            toRow >= 0 && toRow < 8 && toCol >= 0 && toCol < 8 &&
            checkersPieces[fromRow][fromCol] != null && checkersPieces[toRow][toCol] == null) {

        if ((isPlayerTurn && checkersPieces[fromRow][fromCol].getColor() == Color.RED) ||
                (!isPlayerTurn && checkersPieces[fromRow][fromCol].getColor() == Color.BLACK)) {

            int rowDiff = toRow - fromRow;
            int colDiff = Math.abs(toCol - fromCol);

            // Check if it's a valid move for normal pieces
            if (!checkersPieces[fromRow][fromCol].isKing()) {
                if ((isPlayerTurn && rowDiff > 0) || (!isPlayerTurn && rowDiff < 0)) {
                    return false; // Normal pieces can only move forward
                }
            }

            if (Math.abs(rowDiff) == 1 && colDiff == 1) {
                return true;
            } else if (Math.abs(rowDiff) == 2 && colDiff == 2) {
                int capturedRow = (fromRow + toRow) / 2;
                int capturedCol = (fromCol + toCol) / 2;
                return checkersPieces[capturedRow][capturedCol] != null &&
                        checkersPieces[capturedRow][capturedCol].getColor() != checkersPieces[fromRow][fromCol].getColor();
            }

            // Check if it's a valid move for kings
            if (checkersPieces[fromRow][fromCol].isKing()) {
                return Math.abs(rowDiff) == 1 && colDiff == 1; // Allow kings to move in both directions
            }
        }
    }

    return false;
}




public void moveCheckersPiece(int fromRow, int fromCol, int toRow, int toCol) {
    if (checkersPieces[fromRow][fromCol] != null) {
        checkersPieces[toRow][toCol] = checkersPieces[fromRow][fromCol];
        checkersPieces[fromRow][fromCol] = null;

        int jumpedRow = (fromRow + toRow) / 2;
        int jumpedCol = (fromCol + toCol) / 2;
        if (Math.abs(toRow - fromRow) == 2 && Math.abs(toCol - fromCol) == 2) {
            checkersPieces[jumpedRow][jumpedCol] = null;
        }
    }
}





   public void updateCheckersBoard() {
    for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
            if (checkersPieces[i][j] != null) {
                // Update the piece icon with correct king status
                placePiece(i, j, checkersPieces[i][j].getColor(), checkersPieces[i][j].isKing());
            } else {
                checkersBoard[i][j].setIcon(null);
            }
        }
    }
}


    private ImageIcon createCircleIcon(Color color, boolean isKing) {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(color);
        g2d.fillOval(10, 10, 80, 80);

        if (isKing) {
            g2d.setColor(Color.YELLOW);
            g2d.fillRect(40, 0, 20, 20);
            g2d.fillPolygon(new int[]{40, 30, 50}, new int[]{0, 20, 20}, 3);
        }

        g2d.dispose();
        return new ImageIcon(image);
    }
private void toggleFullScreen() {
        if (getExtendedState() == JFrame.MAXIMIZED_BOTH) {
            setExtendedState(JFrame.NORMAL);
        } else {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
    }
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CheckersGame(true)); // Set to true for AI player
    }

}
//Ctr F to make full screen, must do at the beggenig before playing the game.import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CheckersIntroScreen extends JFrame {
    private JPanel introPanel;
    private JButton playAgainstComputerButton;
    private JButton play2v2Button;

    public CheckersIntroScreen() {
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
        new CheckersGame(true); // Start the game against the computer
        dispose(); // Close the intro screen
    }

    private void start2v2ManualGame() {
        new CheckersGame(false); // Start the 2v2 manual game
        dispose(); // Close the intro screen
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CheckersIntroScreen::new);
    }
}
public class Move {
    private int fromRow;
    private int fromCol;
    private int toRow;
    private int toCol;

    public Move(int fromRow, int fromCol, int toRow, int toCol) {
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
}
import java.awt.Color;

public class KingPiece extends CheckersPiece {
    public KingPiece(Color color, int row) {
        super(color, row);
        setKing(true);
    }
}
import java.awt.Color;

public class RegularPiece extends CheckersPiece {
    public RegularPiece(Color color, int row) {
        super(color, row);
    }
}
import java.awt.Color;

class CheckersPiece {
    private Color color;
    private boolean isKing;
    private int row;  // New field to store the row

    public CheckersPiece(Color color, int row) {
        this.color = color;
        this.isKing = (color == Color.RED && row == 0) || (color == Color.BLACK && row == 7);
        this.row = row;
    }

    public Color getColor() {
        return color;
    }

    public boolean isKing() {
        return isKing;
    }

    public void setKing(boolean king) {
        isKing = king;
    }

    public int getRow() {
        return row;
    }
}
