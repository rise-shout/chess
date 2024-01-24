package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    ChessGame.TeamColor color;
    PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
       color = pieceColor;
       pieceType = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if(pieceType == PieceType.BISHOP) {
            return bishopMoves(board, myPosition);
        }
        else if(pieceType == PieceType.KING) {
            return kingMoves(board, myPosition);
        }
        else if(pieceType == PieceType.KNIGHT) {
            return knightMoves(board, myPosition);
        }

        Collection<ChessMove> temp = null;
        return temp;
    }

    private boolean isValidMove(int row, int col, ChessBoard board) {
        return row >= 0 && row <= 7 && col >= 0 && col <= 8 && (board.currBoard[row][col] == null || board.currBoard[row][col].getTeamColor() != getTeamColor());
    }
    public Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();

        int currRow = myPosition.currRow;
        int currCol = myPosition.currCol;

        // Check up-left
        int i = currRow;
        int j = currCol;

        while (i > 0 && j > 0) {
            i--;
            j--;

            if (board.currBoard[i][j] == null) {
                ChessPosition newPosition = new ChessPosition(i, j);
                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                possibleMoves.add(newMove);
            } else {
                ChessPosition newPosition = new ChessPosition(i, j);
                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                possibleMoves.add(newMove);
                break;
            }
        }

        // Check up-right
        i = currRow;
        j = currCol;

        while (i > 0 && j < 7) {
            i--;
            j++;

            if (board.currBoard[i][j] == null) {
                ChessPosition newPosition = new ChessPosition(i, j);
                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                possibleMoves.add(newMove);
            } else {
                ChessPosition newPosition = new ChessPosition(i, j);
                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                possibleMoves.add(newMove);
                break;
            }
        }

        // Check down-left
        i = currRow;
        j = currCol;

        while (i < 7 && j > 0) {
            i++;
            j--;

            if (board.currBoard[i][j] == null) {
                ChessPosition newPosition = new ChessPosition(i, j);
                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                possibleMoves.add(newMove);
            } else {
                ChessPosition newPosition = new ChessPosition(i, j);
                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                possibleMoves.add(newMove);
                break;
            }
        }

        // Check down-right
        i = currRow;
        j = currCol;

        while (i < 7 && j < 7) {
            i++;
            j++;

            if (board.currBoard[i][j] == null) {
                ChessPosition newPosition = new ChessPosition(i, j);
                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                possibleMoves.add(newMove);
            } else {
                ChessPosition newPosition = new ChessPosition(i, j);
                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                possibleMoves.add(newMove);
                break;
            }
        }

        return possibleMoves;
    }

    public Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();

        int currRow = myPosition.currRow;
        int currCol = myPosition.currCol;

        //top left, top, top right
        if(currRow - 1 >= 1) {
            if(currCol - 1 >=0 && isValidMove(currRow-1, currCol-1, board)) {
                ChessPosition newPosition = new ChessPosition(currRow - 1, currCol - 1);
                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                possibleMoves.add(newMove);
            }
            if(currCol + 1 <= 7 && isValidMove(currRow-1, currCol+1, board)) {
                ChessPosition newPosition = new ChessPosition(currRow - 1, currCol + 1);
                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                possibleMoves.add(newMove);
            }

            if(isValidMove(currRow-1, currCol, board)) {
                ChessPosition newPosition = new ChessPosition(currRow - 1, currCol);
                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                possibleMoves.add(newMove);
            }
        }

        // Left, right
        if (currCol - 1 >= 0 && isValidMove(currRow, currCol-1, board)) {
            ChessPosition newPositionLeft = new ChessPosition(currRow, currCol - 1);
            ChessMove newMoveLeft = new ChessMove(myPosition, newPositionLeft, null);
            possibleMoves.add(newMoveLeft);

        }
        if (currCol + 1 <= 7 && isValidMove(currRow, currCol+1, board)) {
            ChessPosition newPositionRight = new ChessPosition(currRow, currCol + 1);
            ChessMove newMoveRight = new ChessMove(myPosition, newPositionRight, null);
            possibleMoves.add(newMoveRight);

        }

        // Bottom left, bottom, bottom right
        if (currRow + 1 <= 7) {
            if (currCol - 1 >= 0 && isValidMove(currRow+1, currCol-1, board)) {
                ChessPosition newPosition = new ChessPosition(currRow + 1, currCol - 1);
                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                possibleMoves.add(newMove);

            }

            if(isValidMove(currRow+1, currCol, board)) {
                ChessPosition newPositionBottom = new ChessPosition(currRow + 1, currCol);
                ChessMove newMoveBottom = new ChessMove(myPosition, newPositionBottom, null);
                possibleMoves.add(newMoveBottom);
            }

            if (currCol + 1 <= 7 && isValidMove(currRow+1, currCol+1, board)) {
                ChessPosition newPosition = new ChessPosition(currRow + 1, currCol + 1);
                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                possibleMoves.add(newMove);

            }
        }

        return possibleMoves;

    }

    public Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();

        int currRow = myPosition.currRow;
        int currCol = myPosition.currCol;

        // Top Moves----------------------------
        // Left
        if (currRow - 1 >= 1 && currCol - 2 >= 1 && isValidMove(currRow - 1, currCol - 2, board)) {
            ChessPosition newPosition = new ChessPosition(currRow - 1, currCol - 2);
            ChessMove newMove = new ChessMove(myPosition, newPosition, null);
            possibleMoves.add(newMove);
        }
        if (currRow - 2 >= 1 && currCol - 1 >= 1 && isValidMove(currRow - 2, currCol - 1, board)) {
            ChessPosition newPosition = new ChessPosition(currRow - 2, currCol - 1);
            ChessMove newMove = new ChessMove(myPosition, newPosition, null);
            possibleMoves.add(newMove);
        }

        // Right
        if (currRow - 1 >= 1 && currCol + 2 <= 8 && isValidMove(currRow - 1, currCol + 2, board)) {
            ChessPosition newPosition = new ChessPosition(currRow - 1, currCol + 2);
            ChessMove newMove = new ChessMove(myPosition, newPosition, null);
            possibleMoves.add(newMove);
        }
        if (currRow - 2 >= 1 && currCol + 1 <= 8 && isValidMove(currRow - 2, currCol + 1, board)) {
            ChessPosition newPosition = new ChessPosition(currRow - 2, currCol + 1);
            ChessMove newMove = new ChessMove(myPosition, newPosition, null);
            possibleMoves.add(newMove);
        }

        // Bottom Moves----------------------------
        // Left
        if (currRow + 1 <= 8 && currCol - 2 >= 1 && isValidMove(currRow + 1, currCol - 2, board)) {
            ChessPosition newPosition = new ChessPosition(currRow + 1, currCol - 2);
            ChessMove newMove = new ChessMove(myPosition, newPosition, null);
            possibleMoves.add(newMove);
        }
        if (currRow + 2 <= 8 && currCol - 1 >= 1 && isValidMove(currRow + 2, currCol - 1, board)) {
            ChessPosition newPosition = new ChessPosition(currRow + 2, currCol - 1);
            ChessMove newMove = new ChessMove(myPosition, newPosition, null);
            possibleMoves.add(newMove);
        }

        // Right
        if (currRow + 1 <= 8 && currCol + 2 <= 8 && isValidMove(currRow + 1, currCol + 2, board)) {
            ChessPosition newPosition = new ChessPosition(currRow + 1, currCol + 2);
            ChessMove newMove = new ChessMove(myPosition, newPosition, null);
            possibleMoves.add(newMove);
        }
        if (currRow + 2 <= 8 && currCol + 1 <= 8 && isValidMove(currRow + 2, currCol + 1, board)) {
            ChessPosition newPosition = new ChessPosition(currRow + 2, currCol + 1);
            ChessMove newMove = new ChessMove(myPosition, newPosition, null);
            possibleMoves.add(newMove);
        }

        return possibleMoves;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChessPiece that)) return false;
        return color == that.color && pieceType == that.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, pieceType);
    }
}
