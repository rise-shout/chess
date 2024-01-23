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

        Collection<ChessMove> temp = null;
        return temp;
    }

    public Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<ChessMove>();
        int currRow = myPosition.currRow;
        int currCol = myPosition.currCol;

        //check up
        if(currRow > 1){
            //diagonally to the upper left
            int i = currRow;
            int j = currCol;

            while(i >= 1 && j >= 0) {
                if(board.currBoard[i-1][j-1] == null) {
                    ChessPosition newPosition = new ChessPosition(i-1, j-1);
                    ChessMove newMove = new ChessMove(myPosition, newPosition,null);

                    possibleMoves.add(newMove);

                    i--;
                    j--;
                }
                else {
                    break;
                }

            }

            //diagonally to the upper left
            i = currRow;
            j = currCol;

            while(i >= 1 && j <= 8) {
                if(board.currBoard[i-1][j+1] == null) {
                    ChessPosition newPosition = new ChessPosition(i-1, j+1);
                    ChessMove newMove = new ChessMove(myPosition, newPosition,null);

                    possibleMoves.add(newMove);

                    i--;
                    j++;
                }
                else {
                    break;
                }

            }
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
