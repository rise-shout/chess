import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import ui.EscapeSequences;

public class ChessboardRenderer {
    public static void drawBoard(ChessBoard board, String perspective) {
        String[][] boardToDisplay = new String[8][8];

        // Populate the board with pieces based on the perspective
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                if (piece == null) {
                    boardToDisplay[row - 1][col - 1] = EscapeSequences.EMPTY;
                } else {
                    boardToDisplay[row - 1][col - 1] = getPieceSymbol(piece);
                }
            }
        }

        // Rotate board if the perspective is black
        if ("black".equalsIgnoreCase(perspective)) {
            boardToDisplay = rotateBoard(boardToDisplay);
        }

        // Clear the screen before drawing the board
        System.out.print(EscapeSequences.ERASE_SCREEN);

        // Print the chessboard
        for (int row = 7; row >= 0; row--) {
            for (int col = 0; col < 8; col++) {
                System.out.print(boardToDisplay[row][col]);
            }
            System.out.println("I'M HERE!!");
        }
    }

    private static String getPieceSymbol(ChessPiece piece) {
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            switch (piece.getPieceType()) {
                case PAWN: return EscapeSequences.WHITE_PAWN;
                case ROOK: return EscapeSequences.WHITE_ROOK;
                case KNIGHT: return EscapeSequences.WHITE_KNIGHT;
                case BISHOP: return EscapeSequences.WHITE_BISHOP;
                case QUEEN: return EscapeSequences.WHITE_QUEEN;
                case KING: return EscapeSequences.WHITE_KING;
                default: return EscapeSequences.EMPTY;
            }
        } else {
            switch (piece.getPieceType()) {
                case PAWN: return EscapeSequences.BLACK_PAWN;
                case ROOK: return EscapeSequences.BLACK_ROOK;
                case KNIGHT: return EscapeSequences.BLACK_KNIGHT;
                case BISHOP: return EscapeSequences.BLACK_BISHOP;
                case QUEEN: return EscapeSequences.BLACK_QUEEN;
                case KING: return EscapeSequences.BLACK_KING;
                default: return EscapeSequences.EMPTY;
            }
        }
    }

    // Rotates the board for the black player's perspective
    private static String[][] rotateBoard(String[][] board) {
        String[][] rotatedBoard = new String[8][8];

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                rotatedBoard[row][col] = board[7 - row][7 - col];
            }
        }
        return rotatedBoard;
    }
}

