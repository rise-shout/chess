package chess;

import java.util.Collection;
import java.util.Objects;
import java.util.ArrayList;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    ChessGame.TeamColor color;
    ChessPiece.PieceType pType;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        color = pieceColor;
        pType = type;
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
        return pType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if(pType == PieceType.KING) {
            return kingMoves(board, myPosition);
        }

        return null;
    }

    //helper methods-------------------------------------------------------------------------------------------------
    /*
    This method checks if a move is valid, i.e. if it's within the boundaries of the game board. It then checks that the
    spot is either null (empty) or occupied by an enemy piece (resulting in a capture)
     */
    public boolean isValidMove(int row, int col, ChessBoard board) {

        if(row >= 1 && col >= 1 && row <= 8 && col <= 8) {
            //System.out.println("Here's whats inside: " + board.currBoard[row][col]);
            if (board.currBoard[row][col] == null) {
                //System.out.println("Empty!");

                return true;
            }
            else if( board.currBoard[row][col].getTeamColor() != color) {
                //System.out.println("Capture!");
                return true;
            }
        }
        return false;
    }

    //this method creates and returns a new move object, so I don't have to type it so many times.
    public ChessMove createMove(ChessPosition startPosition, int endRow, int endCol, ChessPiece.PieceType promo) {
        ChessPosition endPosition = new ChessPosition(endRow, endCol);
        return new ChessMove(startPosition, endPosition, promo);
    }

    //King-----------------------------------------------------------------------------------------------------------
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
        //todo later: king cannot be put into check

    }





    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChessPiece that)) {
            return false;
        }
        return color == that.color && pType == that.pType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, pType);
    }
}
