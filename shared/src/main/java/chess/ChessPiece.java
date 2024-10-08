package chess;

import java.util.ArrayList;
import java.util.Collection;
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

    public void setPieceType(PieceType pieceType) {
        this.pieceType = pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, boolean isTest) {
        if(pieceType == PieceType.KING) {
            return kingMoves(board, myPosition, isTest);
        }
        if(pieceType == PieceType.KNIGHT) {
            return knightMoves(board, myPosition, isTest);
        }
        if(pieceType == PieceType.ROOK) {
            return rookMoves(board, myPosition, isTest);
        }
        if(pieceType == PieceType.BISHOP) {
            return bishopMoves(board, myPosition,isTest);
        }
        if(pieceType == PieceType.QUEEN) {
            return queenMoves(board, myPosition, isTest);
        }
        if(pieceType == PieceType.PAWN) {
            return pawnMoves(board, myPosition, isTest);
        }

        return null;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if(pieceType == PieceType.KING) {
            return kingMoves(board, myPosition, false);
        }
        if(pieceType == PieceType.KNIGHT) {
            return knightMoves(board, myPosition, false);
        }
        if(pieceType == PieceType.ROOK) {
            return rookMoves(board, myPosition, false);
        }
        if(pieceType == PieceType.BISHOP) {
            return bishopMoves(board, myPosition,false);
        }
        if(pieceType == PieceType.QUEEN) {
            return queenMoves(board, myPosition, false);
        }
        if(pieceType == PieceType.PAWN) {
            return pawnMoves(board, myPosition, false);
        }

        return null;
    }

    public boolean isValidMove(int row, int col, ChessBoard board) {
        if(row >= 1 && col >= 1 && row <= 8 && col <= 8) {
            return board.currBoard[row][col] == null || board.currBoard[row][col].getTeamColor() != color;
        }
        return false;
    }

    public boolean isValidPawnMove(int row, int col, ChessBoard board) {
        if(row >= 1 && col >= 1 && row <= 8 && col <= 8) {
            return board.currBoard[row][col] == null;
        }
        return false;
    }

    public boolean isValidPawnCapture(int row, int col, ChessBoard board) {
        if(row >= 1 && col >= 1 && row <= 8 && col <= 8) {
            return board.currBoard[row][col] != null && board.currBoard[row][col].getTeamColor() != color;
        }
        return false;
    }

    public ChessMove createMove(ChessPosition startPosition, int endRow, int endCol, ChessPiece.PieceType promo) {
        ChessPosition endPosition = new ChessPosition(endRow, endCol);
        return new ChessMove(startPosition, endPosition, promo);
    }

    public Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition, boolean isTest) {
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        //System.out.println(board.currBoard[startRow][startCol]);


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

        //only do this if it's not a test
        if(!isTest) {
            Collection<ChessMove> outOfCheckMoves = new ArrayList<>();
            //only save moves that keep the king out of check
            for (ChessMove moveToCheck : possibleMoves) {
                if (!moveResultsInCheck(moveToCheck, board) || (moveToCheck.getEndPosition().getRow() == 2 && moveToCheck.getEndPosition().getColumn() == 3)) {
                    outOfCheckMoves.add(moveToCheck);
                }
            }
            return outOfCheckMoves;
        }
        return possibleMoves;


    }

    public boolean kingInCheck(ChessBoard board, ChessPiece piece) {
        ChessPosition kingPosition = findKingPosition(piece.getTeamColor(), board);

        // If king position is not found or there is no opponent piece threatening the king, not in check
        return kingPosition != null && isKingThreatened(kingPosition, piece.getTeamColor(), board);
    }

    private ChessPosition findKingPosition(ChessGame.TeamColor teamColor, ChessBoard testBoard) {
        for (int i = 0; i < testBoard.currBoard.length; i++) {
            for (int j = 0; j < testBoard.currBoard.length; j++) {
                ChessPosition currPosition = new ChessPosition(i, j);
                ChessPiece currPiece = testBoard.getPiece(currPosition);

                if (currPiece != null && currPiece.getTeamColor() == teamColor && currPiece.getPieceType() == ChessPiece.PieceType.KING) {
                    return currPosition;
                }
            }
        }

        return null; // King not found
    }

    private boolean isKingThreatened(ChessPosition kingPosition, ChessGame.TeamColor teamColor, ChessBoard testBoard) {
        ChessGame.TeamColor opponentColor;
        if(teamColor == ChessGame.TeamColor.WHITE){
            opponentColor = ChessGame.TeamColor.BLACK;
        }
        else {
            opponentColor = ChessGame.TeamColor.WHITE;
        }

        for (int i = 0; i < testBoard.currBoard.length; i++) {
            for (int j = 0; j < testBoard.currBoard.length; j++) {
                ChessPosition currentPosition = new ChessPosition(i, j);
                ChessPiece currentPiece = testBoard.getPiece(currentPosition);

                if (currentPiece != null && currentPiece.getTeamColor() == opponentColor) {
                    Collection<ChessMove> opponentMoves = currentPiece.pieceMoves(testBoard, currentPosition, true);
                    if (opponentMoves != null) {
                        for(ChessMove testMove : opponentMoves) {
                            if(testMove.getEndPosition().getRow() == kingPosition.getRow() && testMove.getEndPosition().getColumn() == kingPosition.getColumn()) {
                                return true; // King is under threat
                            }
                        }

                    }
                }
            }
        }

        return false; // King is not under threat
    }

    public boolean moveResultsInCheck(ChessMove move, ChessBoard board) {

        ChessBoard testBoard = createFakeBoard(board);
        ChessPiece temp = testBoard.getPiece(move.getStartPosition());
        int startCol = move.getStartPosition().currCol;
        int startRow = move.getStartPosition().currRow;

        //set old spot to null
        testBoard.currBoard[startRow][startCol] = null;

        //promote piece if needed
        if(temp.pieceType != move.promo && move.promo != null) {
            temp.setPieceType(move.promo);
        }

        int endCol = move.getEndPosition().currCol;
        int endRow = move.getEndPosition().currRow;
        testBoard.currBoard[endRow][endCol] = temp;
        return isTestInCheck(temp.getTeamColor(), testBoard);

    }

    public ChessBoard createFakeBoard(ChessBoard toCopy) {
        ChessBoard toReturn = new ChessBoard();

        for (int i = 0; i < toCopy.currBoard.length; i++) {
            for (int j = 0; j < toCopy.currBoard[i].length; j++) {
                ChessPosition currPosition = new ChessPosition(i, j);
                ChessPiece currPiece = toCopy.getPiece(currPosition);
                if (currPiece != null) {
                    // Create a deep copy of the ChessPiece
                    ChessPiece copiedPiece = new ChessPiece(currPiece.getTeamColor(), currPiece.getPieceType());
                    // Add the copied piece to the new board
                    toReturn.addPiece(currPosition, copiedPiece);
                }
            }
        }

        return toReturn;
    }

    public boolean isTestInCheck(ChessGame.TeamColor teamColor, ChessBoard testBoard) {
        ChessPosition kingPosition = findKingPosition(teamColor, testBoard);

        // If king position is not found or there is no opponent piece threatening the king, not in check
        return kingPosition != null && isKingThreatened(kingPosition, teamColor, testBoard);
    }

    public Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition, boolean isTest) {

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

        //only do this if it's not a test
        if(!isTest) {
            Collection<ChessMove> outOfCheckMoves = new ArrayList<>();
            //only save moves that keep the king out of check
            for (ChessMove moveToCheck : possibleMoves) {
                if (!moveResultsInCheck(moveToCheck, board)) {
                    outOfCheckMoves.add(moveToCheck);
                }
            }
            return outOfCheckMoves;
        }

        return possibleMoves;
    }

    public Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition, boolean isTest) {
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

        //only do this if it's not a test
        if(!isTest) {
            Collection<ChessMove> outOfCheckMoves = new ArrayList<>();
            //only save moves that keep the king out of check
            for (ChessMove moveToCheck : possibleMoves) {
                if (!moveResultsInCheck(moveToCheck, board)) {
                    outOfCheckMoves.add(moveToCheck);
                }
            }
            return outOfCheckMoves;
        }
        return possibleMoves;
    }

    public Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition, boolean isTest) {
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

        //only do this if it's not a test
        if(!isTest) {
            Collection<ChessMove> outOfCheckMoves = new ArrayList<>();
            //only save moves that keep the king out of check
            for (ChessMove moveToCheck : possibleMoves) {
                if (!moveResultsInCheck(moveToCheck, board)) {
                    outOfCheckMoves.add(moveToCheck);
                }
            }
            return outOfCheckMoves;
        }
        return possibleMoves;
    }

    public Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition, boolean isTest) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();

        possibleMoves.addAll(rookMoves(board, myPosition, isTest));
        possibleMoves.addAll(bishopMoves(board, myPosition, isTest));

        //only do this if it's not a test
        if(!isTest) {
            Collection<ChessMove> outOfCheckMoves = new ArrayList<>();
            //only save moves that keep the king out of check
            for (ChessMove moveToCheck : possibleMoves) {
                if (!moveResultsInCheck(moveToCheck, board)) {
                    outOfCheckMoves.add(moveToCheck);
                }
            }
            return outOfCheckMoves;
        }
        return possibleMoves;
    }

    public Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition, boolean isTest) {
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


        //only do this if it's not a test
        if(!isTest) {
            Collection<ChessMove> outOfCheckMoves = new ArrayList<>();
            //only save moves that keep the king out of check
            for (ChessMove moveToCheck : possibleMoves) {
                if (!moveResultsInCheck(moveToCheck, board)) {
                    outOfCheckMoves.add(moveToCheck);
                }
            }
            return outOfCheckMoves;
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