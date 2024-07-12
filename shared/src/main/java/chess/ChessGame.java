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
            return currPiece.pieceMoves(gameBoard, startPosition, false);
        }

    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {


        //if its not a valid chess move
        if(!validMoves(move.getStartPosition()).contains(move)) {
            throw new InvalidMoveException();
        }

        ChessPiece temp = gameBoard.getPiece(move.getStartPosition());
        int startCol = move.getStartPosition().currCol;
        int startRow = move.getStartPosition().currRow;

        //if its on the wrong turn
        if(temp.color != currColor) {
            throw new InvalidMoveException();
        }

        //TODO: if king is in check, move must get out of check otherwise throw exception
        if(isInCheck(currColor) && moveResultsInCheck(move)) {
            throw new InvalidMoveException();
        }

        //set old spot to null
        gameBoard.currBoard[startRow][startCol] = null;

        //promote piece if needed
        if(temp.pieceType != move.promo && move.promo != null) {
            temp.setPieceType(move.promo);
        }

        int endCol = move.getEndPosition().currCol;
        int endRow = move.getEndPosition().currRow;
        gameBoard.currBoard[endRow][endCol] = temp;

        //if it was valid move, change who's turn it is
        if(currColor == TeamColor.WHITE) {
            setTeamTurn(TeamColor.BLACK);
        }
        else {
            setTeamTurn(TeamColor.WHITE);
        }

    }


    public boolean moveResultsInCheck(ChessMove move) {

        ChessBoard testBoard = createFakeBoard(gameBoard);
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
            for (int j = 0; j < toCopy.currBoard.length; j++) {
                ChessPosition currPosition = new ChessPosition(i, j);
                ChessPiece currPiece = gameBoard.getPiece(currPosition);

                toReturn.addPiece(currPosition, currPiece);
            }
        }

        return toReturn;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKingPosition(teamColor, gameBoard);

        // If king position is not found or there is no opponent piece threatening the king, not in check
        return kingPosition != null && isKingThreatened(kingPosition, teamColor, gameBoard);
    }

    public boolean isTestInCheck(TeamColor teamColor, ChessBoard testBoard) {
        ChessPosition kingPosition = findKingPosition(teamColor, testBoard);

        // If king position is not found or there is no opponent piece threatening the king, not in check
        return kingPosition != null && isKingThreatened(kingPosition, teamColor, testBoard);
    }

    private ChessPosition findKingPosition(TeamColor teamColor, ChessBoard testBoard) {
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

    private boolean isKingThreatened(ChessPosition kingPosition, TeamColor teamColor, ChessBoard testBoard) {
        TeamColor opponentColor;
        if(teamColor == TeamColor.WHITE){
            opponentColor = TeamColor.BLACK;
        }
        else {
            opponentColor = TeamColor.WHITE;
        }

        for (int i = 0; i < testBoard.currBoard.length; i++) {
            for (int j = 0; j < testBoard.currBoard.length; j++) {
                ChessPosition currentPosition = new ChessPosition(i, j);
                ChessPiece currentPiece = testBoard.getPiece(currentPosition);

                if (currentPiece != null && currentPiece.getTeamColor() == opponentColor) {
                    Collection<ChessMove> opponentMoves = currentPiece.pieceMoves(testBoard, currentPosition, false);
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

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false; // King is not in check, so not in checkmate
        }

        ChessPosition kingPosition = findKingPosition(teamColor, gameBoard);
        Collection<ChessMove> kingMoves = validMoves(kingPosition);

        // Check if the king can move to escape check
        for (ChessMove move : kingMoves) {
            if (isKingThreatened(move.getEndPosition(), teamColor, gameBoard)) {
                return true;
            }
            //check for pawns
            if(gameBoard.currBoard[move.getEndPosition().getRow()-1][move.getEndPosition().getColumn()-1] != null &&
                    gameBoard.currBoard[move.getEndPosition().getRow()-1][move.getEndPosition().getColumn()+1] != null &&
                    gameBoard.currBoard[move.getEndPosition().getRow()+1][move.getEndPosition().getColumn()-1] != null &&
                    gameBoard.currBoard[move.getEndPosition().getRow()+1][move.getEndPosition().getColumn()+1] != null) {

                if (gameBoard.currBoard[move.getEndPosition().getRow() - 1][move.getEndPosition().getColumn() - 1].pieceType == ChessPiece.PieceType.PAWN ||
                        gameBoard.currBoard[move.getEndPosition().getRow() - 1][move.getEndPosition().getColumn() + 1].pieceType == ChessPiece.PieceType.PAWN ||
                        gameBoard.currBoard[move.getEndPosition().getRow() + 1][move.getEndPosition().getColumn() - 1].pieceType == ChessPiece.PieceType.PAWN ||
                        gameBoard.currBoard[move.getEndPosition().getRow() + 1][move.getEndPosition().getColumn() + 1].pieceType == ChessPiece.PieceType.PAWN) {
                    return true;
                }
            }

        }

        // Check if any other piece can capture the threatening piece or block the check
        for (int i = 0; i < gameBoard.currBoard.length; i++) {
            for (int j = 0; j < gameBoard.currBoard[i].length; j++) {
                ChessPosition currentPosition = new ChessPosition(i, j);
                ChessPiece currentPiece = gameBoard.getPiece(currentPosition);

                if (currentPiece != null && currentPiece.getTeamColor() == teamColor) {
                    Collection<ChessMove> validMoves = validMoves(currentPosition);

                    // Check if any move by this piece can capture the threatening piece or block the check
                    for (ChessMove move : validMoves) {
                        if (!moveResultsInCheck(move)) {
                            return false; // Piece can capture or block the check, not checkmate
                        }
                    }
                }
            }
        }

        // No escape moves for king and no other pieces can capture or block, so it's checkmate
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

        if (isInCheck(teamColor)) {
            return false; // King is in check, so not in stalemate
        }

        //can any piece that's not the king move
        for (int i = 0; i < gameBoard.currBoard.length; i++) {
            for (int j = 0; j < gameBoard.currBoard.length; j++) {
                ChessPosition currentPosition = new ChessPosition(i, j);
                ChessPiece currentPiece = gameBoard.getPiece(currentPosition);

                if (currentPiece != null && currentPiece.getTeamColor() == teamColor && currentPiece.pieceType != ChessPiece.PieceType.KING) {
                    Collection<ChessMove> validMoves = currentPiece.pieceMoves(gameBoard, currentPosition, false);
                    if (!validMoves.isEmpty()) {
                        return false; //something can move

                    }
                }
            }
        }

        ChessPosition kingPosition = findKingPosition(teamColor, gameBoard);
        Collection<ChessMove> kingMoves = validMoves(kingPosition);

        // Check if the king can move and not be in check
        for (ChessMove move : kingMoves) {
            if (isKingThreatened(move.getEndPosition(), teamColor, gameBoard)) {
                return true;
            }
            //check for pawns
            if(gameBoard.currBoard[move.getEndPosition().getRow()-1][move.getEndPosition().getColumn()-1] != null &&
                    gameBoard.currBoard[move.getEndPosition().getRow()-1][move.getEndPosition().getColumn()+1] != null &&
                    gameBoard.currBoard[move.getEndPosition().getRow()+1][move.getEndPosition().getColumn()-1] != null &&
                    gameBoard.currBoard[move.getEndPosition().getRow()+1][move.getEndPosition().getColumn()+1] != null) {

                if (gameBoard.currBoard[move.getEndPosition().getRow() - 1][move.getEndPosition().getColumn() - 1].pieceType == ChessPiece.PieceType.PAWN ||
                        gameBoard.currBoard[move.getEndPosition().getRow() - 1][move.getEndPosition().getColumn() + 1].pieceType == ChessPiece.PieceType.PAWN ||
                        gameBoard.currBoard[move.getEndPosition().getRow() + 1][move.getEndPosition().getColumn() - 1].pieceType == ChessPiece.PieceType.PAWN ||
                        gameBoard.currBoard[move.getEndPosition().getRow() + 1][move.getEndPosition().getColumn() + 1].pieceType == ChessPiece.PieceType.PAWN) {
                    return true;
                }
            }

        }


        // No escape moves for king and no other pieces
        return true;
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