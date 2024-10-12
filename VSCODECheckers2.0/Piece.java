import java.awt.Color;

class Piece {
    private Color color;
    private boolean isKing;
    private int row;

    public Piece(Color color, int row) {
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
