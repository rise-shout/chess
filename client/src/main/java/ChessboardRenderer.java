import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import ui.EscapeSequences;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class ChessboardRenderer {

    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 3;

    private static final String EMPTY = "   ";
    public static void drawBoard(ChessGame currGame, String playerColor) {

        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);

        drawBasicBoard(out, playerColor, currGame.getBoard());




    }

    private static void drawBasicBoard(PrintStream out, String viewPoint, ChessBoard board) {
        boolean greenSquare = true;
        int oppositeNum = 9;

        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES+2; boardRow++) {
            greenSquare = !greenSquare;
            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES+2; boardCol++) {

                if(boardRow ==  0 && boardCol == 0) {
                    setBlack(out);
                    out.print("  ");
                }
                if(boardRow ==  9 && boardCol == 0) {
                    setBlack(out);
                    out.print("  ");
                }

                //letters on top and bottom
                if(boardRow == 0 || boardRow == BOARD_SIZE_IN_SQUARES+1 || boardCol == 0 || boardCol > BOARD_SIZE_IN_SQUARES) {
                    setBlack(out);

                    if(viewPoint.equals("WHITE")) {
                        out.print(" ");


                        if((boardRow == 0 || boardRow == BOARD_SIZE_IN_SQUARES+1) && boardCol > 0 && boardCol <= BOARD_SIZE_IN_SQUARES) {
                            out.print((char)(96 + boardCol));
                        }
                        out.print(" ");
                    }
                    else {
                        out.print(" ");
                        if((boardRow == 0 || boardRow == BOARD_SIZE_IN_SQUARES+1) && boardCol > 0 && boardCol <= BOARD_SIZE_IN_SQUARES) {
                            out.print((char)(105 - boardCol));
                        }
                        out.print(" ");
                    }

                    //numbers on the sides
                    if(viewPoint.equals("WHITE")) {
                        if ((boardCol == 0 || boardCol == 9) && boardRow != 0 && boardRow != 9) {
                            out.print(oppositeNum);
                            out.print(" ");
                        }

                    }
                    else {
                        if ((boardCol == 0 || boardCol == 9) && boardRow != 0 && boardRow != 9) {
                            out.print(boardRow);
                            out.print(" ");

                        }

                    }




                }


                else {

                    //alternate colors
                    greenSquare = !greenSquare;

                    if (greenSquare) {
                        setGreen(out);
                    } else {
                        setLightGrey(out);
                    }

                    //set up the chess pieces

                    if (board.currBoard[boardRow][boardCol] != null) {
                        out.print(" ");
                        out.print(getChar(board.currBoard[boardRow][boardCol]));
                        out.print(" ");
                    } else {
                        out.print("   ");
                    }
                }

                if(boardRow ==  9 && boardCol > 7) {
                    out.print(" ");
                }
                if(boardRow ==  0 && boardCol > 7) {
                    out.print(" ");
                }

            }
            oppositeNum--;
            out.print(EscapeSequences.RESET_BG_COLOR);
            out.println();
        }
    }

    private static void setLightGrey(PrintStream out) {
        out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
        out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
    }

    private static void setGreen(PrintStream out) {
        out.print(EscapeSequences.SET_BG_COLOR_DARK_GREEN);
        out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
    }

    private static void setBlack(PrintStream out) {
        out.print(EscapeSequences.SET_BG_COLOR_BLACK);
        out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
    }

    private static String getChar(ChessPiece piece) {
        ChessPiece.PieceType pType = piece.getPieceType();

        if(pType == ChessPiece.PieceType.KING) {
            return "K";
        }
        if(pType == ChessPiece.PieceType.BISHOP) {
            return "b";
        }
        if(pType == ChessPiece.PieceType.KNIGHT) {
            return "k";
        }
        if(pType == ChessPiece.PieceType.ROOK) {
            return "r";
        }
        if(pType == ChessPiece.PieceType.QUEEN) {
            return "Q";
        }
        if(pType == ChessPiece.PieceType.PAWN) {
            return "p";
        }

        return "X";
    }
}

