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
        if(pType == PieceType.BISHOP) {
            return bishopMoves(board, myPosition);
        }
        if(pType == PieceType.KNIGHT) {
            return knightMoves(board, myPosition);
        }
        if(pType == PieceType.ROOK) {
            return rookMoves(board, myPosition);
        }
        if(pType == PieceType.QUEEN) {
            return queenMoves(board, myPosition);
        }
        if(pType == PieceType.PAWN) {
            return pawnMoves(board, myPosition);
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
            return board.currBoard[row][col] == null || board.currBoard[row][col].getTeamColor() != color;
        }
        return false;
    }

    //this method creates and returns a new move object, so I don't have to type it so many times.
    public ChessMove createMove(ChessPosition startPosition, int endRow, int endCol, ChessPiece.PieceType promo) {
        ChessPosition endPosition = new ChessPosition(endRow, endCol);
        return new ChessMove(startPosition, endPosition, promo);
    }

    //this method checks if the pawn can move forward within the board to an open space (null)
    public boolean isValidPawnMove(int row, int col, ChessBoard board) {
        if(row >= 1 && col >= 1 && row <= 8 && col <= 8) {
            return board.currBoard[row][col] == null;
        }
        return false;
    }

    //this checks if a pawn can capture diagonally using the special pawn rules of capture. does not check for promotion.
    public boolean isValidPawnCapture(int row, int col, ChessBoard board) {
        if(row >= 1 && col >= 1 && row <= 8 && col <= 8) {
            return board.currBoard[row][col] != null && board.currBoard[row][col].getTeamColor() != color;
        }
        return false;
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
        //later: king cannot be put into check

    }

    //Bishop-----------------------------------------------------------------------------------------------------------
    public Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        Collection<ChessMove> possibleMoves = new ArrayList<>();

        //up and left
        int currRow = startRow;
        int currCol = startCol;

        while(currRow > 1 && currCol > 1) {
            if(isValidMove(currRow -1, currCol-1, board)) {
                possibleMoves.add(createMove(myPosition, currRow-1, currCol-1, null));

                //capture
                if(board.currBoard[currRow - 1][currCol-1] != null && board.currBoard[currRow - 1][currCol-1].getTeamColor() != color) {
                    break;
                }
            }
            else {
                break;
            }
            currRow--;
            currCol--;
        }

        //up and right
        currRow = startRow;
        currCol = startCol;

        while(currRow > 1 && currCol < 8) {
            if(isValidMove(currRow - 1, currCol+1, board)) {
                possibleMoves.add(createMove(myPosition, currRow-1, currCol+1, null));

                //capture
                if(board.currBoard[currRow - 1][currCol+1] != null && board.currBoard[currRow - 1][currCol+1].getTeamColor() != color) {
                    break;
                }
            }
            else {
                break;
            }
            currRow--;
            currCol++;
        }

        //down and right
        currRow = startRow;
        currCol = startCol;

        while(currRow < 8 && currCol < 8) {
            if(isValidMove(currRow + 1, currCol+1, board)) {
                possibleMoves.add(createMove(myPosition, currRow+1, currCol+1, null));

                //capture
                if(board.currBoard[currRow + 1][currCol+1] != null && board.currBoard[currRow + 1][currCol+1].getTeamColor() != color) {
                    break;
                }
            }
            else {
                break;
            }
            currRow++;
            currCol++;
        }

        //down and left
        currRow = startRow;
        currCol = startCol;

        while(currRow < 8 && currCol > 1) {
            if(isValidMove(currRow + 1, currCol-1, board)) {
                possibleMoves.add(createMove(myPosition, currRow+1, currCol-1, null));

                //capture
                if(board.currBoard[currRow + 1][currCol-1] != null && board.currBoard[currRow + 1][currCol-1].getTeamColor() != color) {
                    break;
                }
            }
            else {
                break;
            }
            currRow++;
            currCol--;
        }

        return possibleMoves;
    }

    //Knight-----------------------------------------------------------------------------------------------------------
    public Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {

         /*
        | |x| |x| |
        |x| | | |x|
        | | |n| | |
        |x| | | |x|
        | |x| |x| |

         */

        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        Collection<ChessMove> possibleMoves = new ArrayList<>();

        //top closer moves
        if(startRow >= 2) {
            //left
            if(startCol >= 3 && isValidMove(startRow-1, startCol-2, board)) {
                possibleMoves.add(createMove(myPosition, startRow-1, startCol-2,null));
            }
            //right
            if(startCol <= 6 && isValidMove(startRow-1, startCol+2, board)) {
                possibleMoves.add(createMove(myPosition, startRow-1, startCol+2,null));
            }
        }

        //top further moves
        if(startRow >= 3) {
            //left
            if(startCol >= 2 && isValidMove(startRow-2, startCol-1, board)) {
                possibleMoves.add(createMove(myPosition, startRow-2, startCol-1,null));
            }
            //right
            if(startCol <= 7 && isValidMove(startRow-2, startCol+1, board)) {
                possibleMoves.add(createMove(myPosition, startRow-2, startCol+1,null));
            }
        }

        //bottom closer moves
        if(startRow <= 7) {
            //left
            if(startCol >= 3 && isValidMove(startRow+1, startCol-2, board)) {
                possibleMoves.add(createMove(myPosition, startRow+1, startCol-2,null));
            }
            //right
            if(startCol <= 6 && isValidMove(startRow+1, startCol+2, board)) {
                possibleMoves.add(createMove(myPosition, startRow+1, startCol+2,null));
            }
        }

        //bottom further moves
        if(startRow <= 6) {
            //left
            if(startCol >= 2 && isValidMove(startRow+2, startCol-1, board)) {
                possibleMoves.add(createMove(myPosition, startRow+2, startCol-1,null));
            }
            //right
            if(startCol <= 7 && isValidMove(startRow+2, startCol+1, board)) {
                possibleMoves.add(createMove(myPosition, startRow+2, startCol+1,null));
            }
        }

        return possibleMoves;
    }

    //Rook-----------------------------------------------------------------------------------------------------------
    public Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        Collection<ChessMove> possibleMoves = new ArrayList<>();

        //go up
        int currRow = startRow;
        int currCol = startCol;

        while(currRow > 1) {
            if(isValidMove(currRow -1, currCol, board)) {
                possibleMoves.add(createMove(myPosition, currRow-1, currCol, null));

                //capture
                if(board.currBoard[currRow - 1][currCol] != null && board.currBoard[currRow - 1][currCol].getTeamColor() != color) {
                    break;
                }
            }
            else {
                break;
            }
            currRow--;
        }

        //go down
        currRow = startRow;

        while(currRow < 8) {
            if(isValidMove(currRow +1, currCol, board)) {
                possibleMoves.add(createMove(myPosition, currRow+1, currCol, null));

                //capture
                if(board.currBoard[currRow + 1][currCol] != null && board.currBoard[currRow + 1][currCol].getTeamColor() != color) {
                    break;
                }
            }
            else {
                break;
            }
            currRow++;
        }

        //go right
        currRow = startRow;

        while(currCol < 8) {
            if(isValidMove(currRow, currCol + 1, board)) {
                possibleMoves.add(createMove(myPosition, currRow, currCol + 1, null));

                //capture
                if(board.currBoard[currRow][currCol + 1] != null && board.currBoard[currRow][currCol + 1].getTeamColor() != color) {
                    break;
                }
            }
            else {
                break;
            }
            currCol++;
        }

        //go left
        //go right
        currCol = startCol;

        while(currCol > 1) {
            if(isValidMove(currRow, currCol - 1, board)) {
                possibleMoves.add(createMove(myPosition, currRow, currCol - 1, null));

                //capture
                if(board.currBoard[currRow][currCol - 1] != null && board.currBoard[currRow][currCol - 1].getTeamColor() != color) {
                    break;
                }
            }
            else {
                break;
            }
            currCol--;
        }

        return possibleMoves;
    }

    //Queen (uses the moves from rook and bishop)---------------------------------------------------------------------
    public Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();

        possibleMoves.addAll(rookMoves(board, myPosition));
        possibleMoves.addAll(bishopMoves(board, myPosition));

        return possibleMoves;
    }

    //Pawn (special capture rules) ---------------------------------------------------------------------
    public Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        Collection<ChessMove> possibleMoves = new ArrayList<>();

        //initial two space move-----------------------------------
        //BLACK
        if(color == ChessGame.TeamColor.BLACK && startRow == 7 && isValidPawnMove(startRow-1, startCol, board) && isValidPawnMove(startRow-2, startCol, board)) {
            possibleMoves.add(createMove(myPosition, startRow-2, startCol, null));
        }
        //WHITE
        if(color == ChessGame.TeamColor.WHITE && startRow == 2 && isValidPawnMove(startRow+1, startCol, board) && isValidPawnMove(startRow+2, startCol, board)) {
            possibleMoves.add(createMove(myPosition, startRow+2, startCol, null));
        }

        //regular move (and promotion)------------------------
        //BLACK
        if(color == ChessGame.TeamColor.BLACK && isValidPawnMove(startRow-1, startCol, board)) {
            if(startRow != 2) {
                possibleMoves.add(createMove(myPosition, startRow - 1, startCol, null));
            }
            else {
                possibleMoves.add(createMove(myPosition, startRow - 1, startCol, PieceType.BISHOP));
                possibleMoves.add(createMove(myPosition, startRow - 1, startCol, PieceType.KNIGHT));
                possibleMoves.add(createMove(myPosition, startRow - 1, startCol, PieceType.QUEEN));
                possibleMoves.add(createMove(myPosition, startRow - 1, startCol, PieceType.ROOK));
            }
        }
        //WHITE
        if(color == ChessGame.TeamColor.WHITE && isValidPawnMove(startRow+1, startCol, board)) {
            if(startRow != 7) {
                possibleMoves.add(createMove(myPosition, startRow + 1, startCol, null));
            }
            else {
                possibleMoves.add(createMove(myPosition, startRow + 1, startCol, PieceType.BISHOP));
                possibleMoves.add(createMove(myPosition, startRow + 1, startCol, PieceType.KNIGHT));
                possibleMoves.add(createMove(myPosition, startRow + 1, startCol, PieceType.QUEEN));
                possibleMoves.add(createMove(myPosition, startRow + 1, startCol, PieceType.ROOK));
            }
        }

        //capture (and promotion)----------------------------------------
        //BLACK
        //left
        if(color == ChessGame.TeamColor.BLACK && isValidPawnCapture(startRow-1, startCol-1, board)) {
            if(startRow != 2) {
                possibleMoves.add(createMove(myPosition, startRow - 1, startCol-1, null));
            }
            else {
                possibleMoves.add(createMove(myPosition, startRow - 1, startCol-1, PieceType.BISHOP));
                possibleMoves.add(createMove(myPosition, startRow - 1, startCol-1, PieceType.KNIGHT));
                possibleMoves.add(createMove(myPosition, startRow - 1, startCol-1, PieceType.QUEEN));
                possibleMoves.add(createMove(myPosition, startRow - 1, startCol-1, PieceType.ROOK));
            }
        }
        //right
        if(color == ChessGame.TeamColor.BLACK && isValidPawnCapture(startRow-1, startCol+1, board)) {
            if(startRow != 2) {
                possibleMoves.add(createMove(myPosition, startRow - 1, startCol+1, null));
            }
            else {
                possibleMoves.add(createMove(myPosition, startRow - 1, startCol+1, PieceType.BISHOP));
                possibleMoves.add(createMove(myPosition, startRow - 1, startCol+1, PieceType.KNIGHT));
                possibleMoves.add(createMove(myPosition, startRow - 1, startCol+1, PieceType.QUEEN));
                possibleMoves.add(createMove(myPosition, startRow - 1, startCol+1, PieceType.ROOK));
            }
        }

        //WHITE
        //left
        if(color == ChessGame.TeamColor.WHITE && isValidPawnCapture(startRow+1, startCol-1, board)) {
            if(startRow != 7) {
                possibleMoves.add(createMove(myPosition, startRow + 1, startCol-1, null));
            }
            else {
                possibleMoves.add(createMove(myPosition, startRow + 1, startCol-1, PieceType.BISHOP));
                possibleMoves.add(createMove(myPosition, startRow + 1, startCol-1, PieceType.KNIGHT));
                possibleMoves.add(createMove(myPosition, startRow + 1, startCol-1, PieceType.QUEEN));
                possibleMoves.add(createMove(myPosition, startRow + 1, startCol-1, PieceType.ROOK));
            }
        }
        //right
        if(color == ChessGame.TeamColor.WHITE && isValidPawnCapture(startRow+1, startCol+1, board)) {
            if(startRow != 7) {
                possibleMoves.add(createMove(myPosition, startRow + 1, startCol+1, null));
            }
            else {
                possibleMoves.add(createMove(myPosition, startRow + 1, startCol+1, PieceType.BISHOP));
                possibleMoves.add(createMove(myPosition, startRow + 1, startCol+1, PieceType.KNIGHT));
                possibleMoves.add(createMove(myPosition, startRow + 1, startCol+1, PieceType.QUEEN));
                possibleMoves.add(createMove(myPosition, startRow + 1, startCol+1, PieceType.ROOK));
            }
        }

        return possibleMoves;
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
