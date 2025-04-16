package chessclient;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import ui.EscapeSequences;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class ChessboardRenderer {

    private static final int BOARD_SIZE_IN_SQUARES = 8;

    public static void drawBoard(ChessGame currGame, String playerColor) {

        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);

        boardDrawer(out, playerColor, currGame.getBoard());

        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);

    }

    public static ChessPiece[][] flipArrayHorizontal(ChessPiece[][] original) {
        ChessPiece[][] board = new ChessPiece[original.length-1][original.length-1];

        for(int i = 1; i < original.length; i++) {
            for(int j = 1; j < original.length; j++) {
                board[i-1][j-1] = original[i][j];
            }
        }

        int last = board.length - 1;
        for (int i = last / 2; i >= 0; i--) {
            ChessPiece[] temp = board[i];
            board[i] = board[last - i];
            board[last - i] = temp;
        }

        ChessPiece[][] returnBoard = new ChessPiece[original.length][original.length];
        for(int i = 0; i < original.length; i++) {
            for(int j = 0; j < original.length; j++) {
                if(i != 0 && j != 0) {
                    returnBoard[i][j] = board[i-1][j-1];
                }
            }
        }

        return returnBoard;
    }

    public static ChessPiece[][] flipArrayVertical(ChessPiece[][] original) {

        ChessPiece[][] board = new ChessPiece[original.length-1][original.length-1];

        for(int i = 1; i < original.length; i++) {
            for(int j = 1; j < original.length; j++) {
                board[i-1][j-1] = original[i][j];
            }
        }

        int cols = board[0].length;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < cols / 2; j++) {
                ChessPiece temp = board[i][j];
                board[i][j] = board[i][cols - 1 - j];
                board[i][cols - 1 - j] = temp;
            }
        }


        ChessPiece[][] returnBoard = new ChessPiece[original.length][original.length];
        for(int i = 0; i < original.length; i++) {
            for(int j = 0; j < original.length; j++) {
                if(i != 0 && j != 0) {
                    returnBoard[i][j] = board[i-1][j-1];
                }
            }
        }

        return returnBoard;
    }

    public static void boardDrawer(PrintStream out, String viewPoint, ChessBoard origBoard) {
        ChessPiece[][] flippedBoard = new ChessPiece[9][9];

        if(viewPoint.equals("WHITE")) {
            //need to flip the array horizontally for viewing
            //Not changing currBoard AT ALL
            flippedBoard = flipArrayHorizontal(origBoard.currBoard);
        }
        else {
            flippedBoard = flipArrayVertical(origBoard.currBoard);
        }

        boolean greenSquare = true;
        int oppositeNum = 9;

        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES+2; boardRow++) {
            greenSquare = !greenSquare;
            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES+2; boardCol++) {

                //draw borders
                if(boardRow ==  0 && boardCol == 0) {
                    setBlack(out);
                    out.print("  ");
                }
                if(boardRow ==  9 && boardCol == 0) {
                    setBlack(out);
                    out.print("  ");
                }

                if(boardRow == 0 || boardRow == BOARD_SIZE_IN_SQUARES+1 || boardCol == 0 || boardCol > BOARD_SIZE_IN_SQUARES) {
                    setBlack(out);

                    //print letters
                    if((boardRow == 0 || boardRow == BOARD_SIZE_IN_SQUARES+1) && boardCol > 0 && boardCol <= BOARD_SIZE_IN_SQUARES) {
                        out.print(" ");
                        if(viewPoint.equals("WHITE")) {
                            out.print((char) (96 + boardCol));
                        }
                        else {
                            out.print((char) (105 - boardCol));
                        }
                        out.print(" ");
                    }

                    //print numbers
                    if ((boardCol == 0 || boardCol == 9) && boardRow != 0 && boardRow != 9) {
                        if(viewPoint.equals("WHITE")) {
                            out.print(oppositeNum);
                        }
                        else{
                            out.print(boardRow);
                        }
                        out.print(" ");
                    }

                }

                //draw squares and pieces
                else {
                    //alternate colors
                    greenSquare = !greenSquare;
                    if (greenSquare) {
                        setGreen(out);
                    } else {
                        setLightGrey(out);
                    }

                    //draw a piece instead of a square
                    if (flippedBoard[boardRow][boardCol] != null) {
                        out.print(" ");
                        if (flippedBoard[boardRow][boardCol].getTeamColor() == ChessGame.TeamColor.WHITE) {
                            setTextWhite(out);
                        }
                        else {
                            setTextBlack(out);
                        }
                        out.print(getChar(flippedBoard[boardRow][boardCol]));
                        out.print(" ");
                    }
                    else {
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
                    //out.print(EscapeSequences.RESET_TEXT_COLOR);
                    if (board.currBoard[boardRow][boardCol] != null && board.currBoard[9-boardRow][boardCol] != null) {
                        if(viewPoint.equals("WHITE")) {
                            out.print(" ");
                            if (board.currBoard[9-boardRow][boardCol].getTeamColor() == ChessGame.TeamColor.WHITE) {
                                setTextWhite(out);
                            } else {
                                setTextBlack(out);
                            }
                            out.print(getChar(board.currBoard[9-boardRow][boardCol]));
                            out.print(" ");
                        }
                        else {
                            out.print(" ");
                            if (board.currBoard[9-boardRow][boardCol] != null && board.currBoard[boardRow][9-boardCol].getTeamColor() == ChessGame.TeamColor.WHITE) {
                                setTextWhite(out);
                            } else {
                                setTextBlack(out);
                            }
                            out.print(getChar(board.currBoard[boardRow][9-boardCol]));
                            out.print(" ");
                        }
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
    }

    private static void setGreen(PrintStream out) {
        out.print(EscapeSequences.SET_BG_COLOR_DARK_GREEN);
    }

    private static void setTextWhite(PrintStream out) {
        out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
    }

    private static void setTextBlack(PrintStream out) {
        out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
    }

    private static void setBlack(PrintStream out) {
        out.print(EscapeSequences.SET_BG_COLOR_BLACK);
        out.print(EscapeSequences.SET_TEXT_COLOR_BLUE);
    }

    private static String getChar(ChessPiece piece) {
        ChessPiece.PieceType pType = piece.getPieceType();

        if(pType == ChessPiece.PieceType.KING) {
            return "K";
        }
        if(pType == ChessPiece.PieceType.BISHOP) {
            return "B";
        }
        if(pType == ChessPiece.PieceType.KNIGHT) {
            return "N";
        }
        if(pType == ChessPiece.PieceType.ROOK) {
            return "R";
        }
        if(pType == ChessPiece.PieceType.QUEEN) {
            return "Q";
        }
        if(pType == ChessPiece.PieceType.PAWN) {
            return "P";
        }

        return "X";
    }
}

