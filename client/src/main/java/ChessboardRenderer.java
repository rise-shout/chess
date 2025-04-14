public class ChessboardRenderer {
    public static void drawBoard(boolean isWhitePerspective) {
        char[][] board = new char[8][8];
        initializeBoard(board);
        if (!isWhitePerspective) {
            reverseBoard(board);
        }
        displayBoard(board);
    }

    private static void initializeBoard(char[][] board) {
        // Fill the board with initial positions of chess pieces.
    }

    private static void reverseBoard(char[][] board) {
        // Reverse the board for black perspective.
    }

    private static void displayBoard(char[][] board) {
        // Print board with alternating square colors and labels.
    }
}

