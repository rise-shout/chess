package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    int currRow;
    int currCol;

    public ChessPosition(int row, int col) {
        currRow = row;
        currCol = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return currRow;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return currCol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChessPosition that)) return false;
        return currRow == that.currRow && currCol == that.currCol;
    }

    @Override
    public int hashCode() {
        return Objects.hash(currRow, currCol);
    }
}