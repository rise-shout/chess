package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    ChessGame.TeamColor color;
    ChessPiece.PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
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
        if(pieceType == PieceType.KING) {
            return kingMoves(board, myPosition);
        }

        return null;
    }

    public boolean isValidMove(int row, int col, ChessBoard board) {
        if(row >= 1 && col >= 1 && row <= 8 && col <= 8) {
            return board.currBoard[row][col] == null || board.currBoard[row][col].getTeamColor() != color;
        }
        return false;
    }

    public ChessMove createMove(ChessPosition startPosition, int endRow, int endCol, ChessPiece.PieceType promo) {
        ChessPosition endPosition = new ChessPosition(endRow, endCol);
        return new ChessMove(startPosition, endPosition, promo);
    }

    public Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        Collection<ChessMove> possibleMoves = new ArrayList<>();

        //top moves
        if(startRow != 1) {
            //top left
            if(startCol != 1 && isValidMove(startRow-1, startCol-1, board)) {
                possibleMoves.add(createMove(myPosition, startRow-1, startCol-1,null));
            }
            //top right
            if(startCol != 8 && isValidMove(startRow-1, startCol+1, board)) {
                possibleMoves.add(createMove(myPosition, startRow-1, startCol+1,null));
            }
            //top center
            if(isValidMove(startRow-1, startCol, board)) {
                possibleMoves.add(createMove(myPosition, startRow-1, startCol,null));
            }
        }

        //bottom moves
        if(startRow != 8) {
            //bottom left
            if(startCol != 1 && isValidMove(startRow+1, startCol-1, board)) {
                possibleMoves.add(createMove(myPosition, startRow+1, startCol-1,null));
            }
            //bottom right
            if(startCol != 8 && isValidMove(startRow+1, startCol+1, board)) {
                possibleMoves.add(createMove(myPosition, startRow+1, startCol+1,null));
            }
            //bottom center
            if(isValidMove(startRow+1, startCol, board)) {
                possibleMoves.add(createMove(myPosition, startRow+1, startCol,null));
            }
        }

        //left and right
        if(startCol != 1 && isValidMove(startRow, startCol-1, board)) {
            possibleMoves.add(createMove(myPosition, startRow, startCol-1,null));
        }
        if(startCol != 8 && isValidMove(startRow, startCol+1, board)) {
            possibleMoves.add(createMove(myPosition, startRow, startCol+1,null));
        }

        return possibleMoves;
    }
}
