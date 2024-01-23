package chess;

import java.util.Arrays;

import static chess.ChessPiece.PieceType.ROOK;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    ChessPiece[][] currBoard = new ChessPiece[9][9];


    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        int newRow = position.getRow();
        int newCol = position.getColumn();

        if(currBoard[newRow][newCol] == null) {
            currBoard[newRow][newCol] = piece;
        }
        else {
            System.out.println("CANNOT ADD PIECE");
        }

    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        int testRow = position.getRow();
        int testCol = position.getColumn();

        if(currBoard[testRow][testCol] != null) {
            return currBoard[testRow][testCol];
        }
        else {
            return null;
        }
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        //clear out board
        for(ChessPiece[] row : currBoard) {
            for(ChessPiece spot : row) {
                spot = null;
            }
        }

        //refill board
        for(int i = 1; i < 9; i++) {
            for(int j = 0; j < 9; j++) {
                ChessPiece newPiece;

                //FIRST AND LAST COLS-------------------------
                if(j == 1 || j == 8) {

                    //ROOKS
                    if(i == 1) {
                        newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ROOK);
                        currBoard[i][j] = newPiece;
                        System.out.print("r");

                    }
                    else if(i == 8) {
                        newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ROOK);
                        currBoard[i][j] = newPiece;
                        System.out.print("R");
                    }

                    //PAWNS
                    else if(i == 2) {
                        newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
                        currBoard[i][j] = newPiece;
                        System.out.print("p");
                    }
                    else if(i == 7) {
                        newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
                        currBoard[i][j] = newPiece;
                        System.out.print("P");
                    }

                    //BLANK SPACE
                    else {
                        System.out.print(" ");
                    }
                }

                //COLS 2 and 7
                if(j==2 || j ==7) {
                    //KNIGHTS
                    if(i == 1) {
                        newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
                        currBoard[i][j] = newPiece;
                        System.out.print("n");

                    }
                    else if(i == 8) {
                        newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
                        currBoard[i][j] = newPiece;
                        System.out.print("N");
                    }

                    //PAWNS
                    else if(i == 2) {
                        newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
                        currBoard[i][j] = newPiece;
                        System.out.print("p");
                    }
                    else if(i == 7) {
                        newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
                        currBoard[i][j] = newPiece;
                        System.out.print("P");
                    }

                    //BLANK SPACE
                    else {
                        System.out.print(" ");
                    }
                }

                //COLS 3 and 6
                if(j==3 || j ==6) {
                    //BISHOPS
                    if (i == 1) {
                        newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
                        currBoard[i][j] = newPiece;
                        System.out.print("b");

                    } else if (i == 8) {
                        newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
                        currBoard[i][j] = newPiece;
                        System.out.print("B");
                    }

                    //PAWNS
                    else if (i == 2) {
                        newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
                        currBoard[i][j] = newPiece;
                        System.out.print("p");
                    } else if (i == 7) {
                        newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
                        currBoard[i][j] = newPiece;
                        System.out.print("P");
                    }

                    //BLANK SPACE
                    else {
                        System.out.print(" ");
                    }
                }

                //COL 4
                if(j==4) {
                    //QUEENS
                    if (i == 1) {
                        newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
                        currBoard[i][j] = newPiece;
                        System.out.print("q");

                    } else if (i == 8) {
                        newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
                        currBoard[i][j] = newPiece;
                        System.out.print("Q");
                    }

                    //PAWNS
                    else if (i == 2) {
                        newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
                        currBoard[i][j] = newPiece;
                        System.out.print("p");
                    } else if (i == 7) {
                        newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
                        currBoard[i][j] = newPiece;
                        System.out.print("P");
                    }

                    //BLANK SPACE
                    else {
                        System.out.print(" ");
                    }
                }

                //COL 5
                if(j==5) {
                    //KINGS
                    if (i == 1) {
                        newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
                        currBoard[i][j] = newPiece;
                        System.out.print("k");

                    } else if (i == 8) {
                        newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
                        currBoard[i][j] = newPiece;
                        System.out.print("K");
                    }

                    //PAWNS
                    else if (i == 2) {
                        newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
                        currBoard[i][j] = newPiece;
                        System.out.print("p");
                    } else if (i == 7) {
                        newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
                        currBoard[i][j] = newPiece;
                        System.out.print("P");
                    }

                    //BLANK SPACE
                    else {
                        System.out.print(" ");
                    }
                }

                //Print seperation
                System.out.print("|");



            }
            System.out.println();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChessBoard that)) return false;
        return Arrays.equals(currBoard, that.currBoard);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(currBoard);
    }
}
