package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    TeamColor currColor;
    Collection<ChessMove> possibleMoves;
    ChessBoard gameBoard = new ChessBoard();

    public ChessGame() {
        currColor = TeamColor.WHITE;

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {

        return currColor;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currColor = team;
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
        ChessPiece currPiece = gameBoard.getPiece(startPosition);
        if(currPiece == null) {
            return null;
        }
        else {
            return currPiece.pieceMoves(gameBoard, startPosition);
        }

    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece temp = gameBoard.getPiece(move.getStartPosition());
        int startCol = move.getStartPosition().currCol;
        int startRow = move.getStartPosition().currRow;
        gameBoard.currBoard[startRow][startCol] = null;

        //promote piece if needed
        if(temp.pieceType != move.promo && move.promo != null) {
            temp.setPieceType(move.promo);
        }

        int endCol = move.getEndPosition().currCol;
        int endRow = move.getEndPosition().currRow;
        gameBoard.currBoard[endRow][endCol] = temp;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        for(int i = 0; i < board.currBoard[0].length; i++) {
            for(int j = 0; j < board.currBoard.length; j++) {
                ChessPosition currPosition = new ChessPosition(i, j);
                 if(board.getPiece(currPosition) != null) {
                     gameBoard.addPiece(currPosition, board.getPiece(currPosition));
                 }
            }
        }
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
        if (this == o) return true;
        if (!(o instanceof ChessGame chessGame)) return false;
        return currColor == chessGame.currColor && Objects.equals(possibleMoves, chessGame.possibleMoves) && Objects.equals(gameBoard, chessGame.gameBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currColor, possibleMoves, gameBoard);
    }
}
