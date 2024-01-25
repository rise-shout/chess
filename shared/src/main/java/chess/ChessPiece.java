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
        else if(pieceType == PieceType.PAWN) {
            return pawnMoves(board, myPosition);
        }
        else if(pieceType == PieceType.QUEEN) {
            return queenMoves(board, myPosition);
        }
        else {
            return rookMoves(board, myPosition);
        }
    }

    private boolean isValidMove(int row, int col, ChessBoard board) {
        return row >= 0 && row <= 8 && col >= 0 && col <= 8 && (board.currBoard[row][col] == null || board.currBoard[row][col].getTeamColor() != getTeamColor());
    }
    private boolean isValidMovePawn(int row, int col, ChessBoard board) {
        return row >= 1 && row <= 8 && col >= 0 && col <= 8 && (board.currBoard[row][col] == null);
    }
    private boolean isValidCapturePawn(int row, int col, ChessBoard board) {
        return row >= 1 && row <= 8 && col >= 0 && col <= 8 && board.currBoard[row][col] != null && board.currBoard[row][col].getTeamColor() != getTeamColor();
    }
    public Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();

        int currRow = myPosition.currRow;
        int currCol = myPosition.currCol;

        // Top Left Moves----------------------------
        int testRow = currRow;
        int testCol = currCol;
        while (testRow - 1 >= 1 && testCol -1 >= 1) {
            if (isValidMove(testRow - 1, testCol -1, board)) {
                ChessPosition newPosition = new ChessPosition(testRow - 1, testCol -1);
                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                possibleMoves.add(newMove);
                testRow--;
                testCol--;
                if (board.currBoard[testRow][testCol] != null && board.currBoard[testRow][testCol].getTeamColor() != getTeamColor()) {
                    break;
                }
            } else {
                break;
            }
        }

        // Top Right Moves -----
        testRow = currRow;
        testCol = currCol;
        while (testRow - 1 >= 1 && testCol + 1<= 8) {
            if (isValidMove(testRow - 1, testCol +1, board)) {
                ChessPosition newPosition = new ChessPosition(testRow - 1, testCol +1);
                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                possibleMoves.add(newMove);
                testRow--;
                testCol++;
                if (board.currBoard[testRow][testCol] != null && board.currBoard[testRow][testCol].getTeamColor() != getTeamColor()) {
                    break;
                }
            } else {
                break;
            }
        }

        //Bottom Right Moves
        testRow = currRow;
        testCol = currCol;
        while (testRow + 1 <= 8 && testCol + 1<= 8) {
            if (isValidMove(testRow + 1, testCol +1, board)) {
                ChessPosition newPosition = new ChessPosition(testRow + 1, testCol +1);
                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                possibleMoves.add(newMove);
                testRow++;
                testCol++;
                if (board.currBoard[testRow][testCol] != null && board.currBoard[testRow][testCol].getTeamColor() != getTeamColor()) {
                    break;
                }
            } else {
                break;
            }
        }

        //Bottom Left Moves
        testRow = currRow;
        testCol = currCol;
        while (testRow + 1 <= 8 && testCol -1 >= 1) {
            if (isValidMove(testRow + 1, testCol -1, board)) {
                ChessPosition newPosition = new ChessPosition(testRow + 1, testCol -1);
                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                possibleMoves.add(newMove);
                testRow++;
                testCol--;
                if (board.currBoard[testRow][testCol] != null && board.currBoard[testRow][testCol].getTeamColor() != getTeamColor()) {
                    break;
                }
            } else {
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

    public Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();

        int currRow = myPosition.currRow;
        int currCol = myPosition.currCol;

        //Check for initial move
        if(currRow == 2 || currRow == 7) {
            if (getTeamColor() == ChessGame.TeamColor.WHITE) {
                if (isValidMovePawn(currRow + 2, currCol, board) && isValidMovePawn(currRow + 1, currCol, board)) {
                    ChessPosition newPosition = new ChessPosition(currRow + 2, currCol);
                    ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                    possibleMoves.add(newMove);
                }

            }

            else {
                if (getTeamColor() == ChessGame.TeamColor.BLACK) {

                    if(isValidMovePawn(currRow -2, currCol, board) && isValidMovePawn(currRow -1, currCol, board)) {
                        ChessPosition newPosition = new ChessPosition(currRow - 2, currCol);
                        ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                        possibleMoves.add(newMove);
                    }
                }
            }
        }

        //Check for regular move

        if (getTeamColor() == ChessGame.TeamColor.WHITE) {
            if (isValidMovePawn(currRow + 1, currCol, board)) {
                    ChessPosition newPosition = new ChessPosition(currRow + 1, currCol);
                if (currRow == 7) {
                    possibleMoves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                    possibleMoves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                    possibleMoves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
                    possibleMoves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                }
                else {
                    possibleMoves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        }

        if (getTeamColor() == ChessGame.TeamColor.BLACK) {
            if(isValidMovePawn(currRow -1, currCol, board)) {
                ChessPosition newPosition = new ChessPosition(currRow - 1, currCol);
                if (currRow == 2) {
                    possibleMoves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                    possibleMoves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                    possibleMoves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
                    possibleMoves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                }
                else {
                    possibleMoves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        }

        //Check for capture
        if (getTeamColor() == ChessGame.TeamColor.WHITE) {
            if (isValidCapturePawn(currRow + 1, currCol + 1, board)) {
                ChessPosition newPosition = new ChessPosition(currRow + 1, currCol + 1);
                if (currRow == 7) {
                    possibleMoves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                    possibleMoves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                    possibleMoves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
                    possibleMoves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                }
                else {
                    possibleMoves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
            if (isValidCapturePawn(currRow + 1, currCol - 1, board)) {
                ChessPosition newPosition = new ChessPosition(currRow + 1, currCol - 1);
                if (currRow == 7) {
                    possibleMoves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                    possibleMoves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                    possibleMoves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
                    possibleMoves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                }
                else {
                    possibleMoves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        }

        if (getTeamColor() == ChessGame.TeamColor.BLACK) {
            if(isValidCapturePawn(currRow -1, currCol + 1, board)) {
                ChessPosition newPosition = new ChessPosition(currRow - 1, currCol + 1);
                if (currRow == 2) {
                    possibleMoves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                    possibleMoves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                    possibleMoves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
                    possibleMoves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                }
                else {
                    possibleMoves.add(new ChessMove(myPosition, newPosition, null));
                }

            }
            if(isValidCapturePawn(currRow -1, currCol - 1, board)) {
                ChessPosition newPosition = new ChessPosition(currRow - 1, currCol - 1);
                if (currRow == 2) {
                    possibleMoves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                    possibleMoves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                    possibleMoves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
                    possibleMoves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                }
                else {
                    possibleMoves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        }

        return possibleMoves;
    }
    public Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();

        // Combine rook and bishop moves for the queen

        // Rook-like moves (horizontal and vertical)
        possibleMoves.addAll(rookMoves(board, myPosition));

        // Bishop-like moves (diagonal)
        possibleMoves.addAll(bishopMoves(board, myPosition));

        return possibleMoves;
    }
    public Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();

        int currRow = myPosition.currRow;
        int currCol = myPosition.currCol;

        // Top Moves----------------------------
        int testRow = currRow;
        while(testRow - 1 >= 1) {
            if(isValidMove(testRow-1, currCol, board)) {
                ChessPosition newPosition = new ChessPosition(testRow - 1, currCol);
                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                possibleMoves.add(newMove);
                testRow--;
                if(board.currBoard[testRow][currCol] != null && board.currBoard[testRow][currCol].getTeamColor() != getTeamColor()){
                    break;
                }
            }
            else {
                break;
            }
        }

        // Bottom Moves----------------------------
        testRow = currRow;
        while(testRow + 1 <= 8) {
            if(isValidMove(testRow+1, currCol, board)) {
                ChessPosition newPosition = new ChessPosition(testRow + 1, currCol);
                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                possibleMoves.add(newMove);
                testRow++;
                if(board.currBoard[testRow][currCol] != null && board.currBoard[testRow][currCol].getTeamColor() != getTeamColor()){
                    break;
                }
            }
            else {
                break;
            }
        }

        // Right Moves----------------------------
        int testCol = currCol;
        while(testCol + 1 <= 8) {
            if(isValidMove(currRow, testCol+1, board)) {
                ChessPosition newPosition = new ChessPosition(currRow, testCol + 1);
                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                possibleMoves.add(newMove);
                testCol++;
                if(board.currBoard[currRow][testCol] != null && board.currBoard[currRow][testCol].getTeamColor() != getTeamColor()){
                    break;
                }
            }
            else {
                break;
            }
        }

        // Left Moves----------------------------
        testCol = currCol;
        while(testCol - 1 >= 1) {
            if(isValidMove(currRow, testCol -1, board)) {
                ChessPosition newPosition = new ChessPosition(currRow, testCol - 1);
                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                possibleMoves.add(newMove);
                testCol--;
                if(board.currBoard[currRow][testCol] != null && board.currBoard[currRow][testCol].getTeamColor() != getTeamColor()){
                    break;
                }
            }
            else {
                break;
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
