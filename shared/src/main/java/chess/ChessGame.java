package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    TeamColor teamTurnColor = TeamColor.WHITE;
    ChessBoard gameBoard = new ChessBoard();

    public ChessGame() {
        //possibly need to call reset board
        gameBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurnColor;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurnColor = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        //find what piece is at that location
        ChessPiece currPiece = gameBoard.currBoard[startPosition.getRow()][startPosition.getColumn()];

        //create list of all potential moves before check/checkmate tests
        Collection<ChessMove> allPossibleMoves = currPiece.pieceMoves(gameBoard, startPosition);

        //create a new list of valid moves
        Collection<ChessMove> correctedMoves = new ArrayList<>();

        //test each move to see if it results in a check
        for(ChessMove testMove : allPossibleMoves) {

            //create temp (fake) board
            ChessBoard testBoard = new ChessBoard();
            for(int i = 0; i < gameBoard.currBoard.length; i++) {
                System.arraycopy(gameBoard.currBoard[i], 0, testBoard.currBoard[i], 0, gameBoard.currBoard.length);
            }

            //make the test move
            //get the old position
            int startRow = testMove.getStartPosition().getRow();
            int startCol = testMove.getStartPosition().getColumn();

            //set it to the new position
            int endRow = testMove.getEndPosition().getRow();
            int endCol = testMove.getEndPosition().getColumn();

            ChessPiece piece = testBoard.currBoard[startRow][startCol];
            testBoard.currBoard[endRow][endCol] = piece;
            testBoard.currBoard[startRow][startCol] = null;

            if(!isInCheck(currPiece.getTeamColor(), testBoard)) {
                correctedMoves.add(testMove);
            }

        }

        return correctedMoves;

    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

        //get the old position and new position
        int startRow = move.getStartPosition().getRow();
        int startCol = move.getStartPosition().getColumn();


        int endRow = move.getEndPosition().getRow();
        int endCol = move.getEndPosition().getColumn();


        //check if move is valid
        ChessPiece piece = gameBoard.currBoard[startRow][startCol];

        if(piece == null) {
            throw new InvalidMoveException();
        }

        if(piece.getTeamColor() != teamTurnColor) {
            throw new InvalidMoveException();
        }

        Collection<ChessMove> validPieceMoves = validMoves(new ChessPosition(startRow, startCol));

        if(!validPieceMoves.contains(move)) {
            throw new InvalidMoveException();
        }

        //check if it needs to be promoted
        if(move.getPromotionPiece() != null) {
            piece.setPieceType(move.getPromotionPiece());
        }

        //set it to the new position

        gameBoard.currBoard[endRow][endCol] = piece;
        gameBoard.currBoard[startRow][startCol] = null;



        //change turn
        if (teamTurnColor == TeamColor.WHITE) {
            teamTurnColor = TeamColor.BLACK;
        }
        else {
            teamTurnColor = TeamColor.WHITE;
        }
    }
    /**
     * Locates and returns the king's position (custom method)
     *
     * @param teamColor which team to find the king for
     * @return ChessPosition of the king
     */

    public ChessPosition findKingPosition(TeamColor teamColor) {
        return getChessPosition(teamColor, gameBoard);
    }

    private ChessPosition getChessPosition(TeamColor teamColor, ChessBoard gameBoard) {
        for(int i = 1; i < gameBoard.currBoard.length; i++) {
            for(int j = 1; j < gameBoard.currBoard.length; j++) {
                ChessPiece currPiece = gameBoard.currBoard[i][j];
                if(currPiece != null && currPiece.getTeamColor() == teamColor
                        && currPiece.getPieceType() == ChessPiece.PieceType.KING) {
                    return new ChessPosition(i,j);
                }
            }
        }
        return null; //no king on board
    }

    public ChessPosition findKingPosition(TeamColor teamColor, ChessBoard testBoard) {
        return getChessPosition(teamColor, testBoard);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */



    public boolean isInCheck(TeamColor teamColor) {


        ChessPosition kingPosition = findKingPosition(teamColor);

        return findOverlap(teamColor, kingPosition, gameBoard);
    }

    private boolean findOverlap(TeamColor teamColor, ChessPosition kingPosition, ChessBoard gameBoard) {
        for (int i = 1; i < gameBoard.currBoard.length; i++) {
            for (int j = 1; j < gameBoard.currBoard.length; j++) {
                ChessPiece currPiece = gameBoard.currBoard[i][j];
                if (currPiece != null && currPiece.getTeamColor() != teamColor) {

                    Collection<ChessMove> opponentMoves;
                    opponentMoves = currPiece.pieceMoves(gameBoard,new ChessPosition(i,j));

                    for(ChessMove testMove : opponentMoves) {
                        if((testMove.getEndPosition().getRow() == kingPosition.getRow())
                                && (testMove.getEndPosition().getColumn() == kingPosition.getColumn())) {
                            return true;
                        }
                    }
                }
            }
        }

        return false; //no moves overlap with the current king position
    }

    public boolean isInCheck(TeamColor teamColor, ChessBoard testBoard) {

        ChessPosition kingPosition = findKingPosition(teamColor, testBoard);

        return findOverlap(teamColor, kingPosition, testBoard);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        //if not in check, not in checkmate
        if(!isInCheck(teamColor)) {
            return false;
        }

        //run through all valid moves of the same team. If any end in removing check or checkmate, return false
        return canFixIt(teamColor);
    }

    private boolean canFixIt(TeamColor teamColor) {
        for (int i = 1; i < gameBoard.currBoard.length; i++) {
            for (int j = 1; j < gameBoard.currBoard.length; j++) {

                ChessPiece currPiece = gameBoard.currBoard[i][j];

                if (currPiece != null && currPiece.getTeamColor() == teamColor) {
                    Collection<ChessMove> allyMoves;
                    allyMoves = validMoves(new ChessPosition(i,j));

                    if(!allyMoves.isEmpty()) {
                        //System.out.println("POSSIBLE MOVES"); //there are moves to remove from check
                        return false;

                    }


                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(isInCheck(teamColor)) {
            return false;
        }

        //run through all valid moves of the same team. If any end in removing check or checkmate, return false
        return canFixIt(teamColor);

    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChessGame chessGame)) {
            return false;
        }
        return teamTurnColor == chessGame.teamTurnColor && Objects.equals(gameBoard, chessGame.gameBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurnColor, gameBoard);
    }
}
