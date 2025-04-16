package chessclient;

import chess.ChessGame;


import chess.ChessMove;
import chess.ChessPosition;
import model.*;
import websocket.WebSocketFacade;
import websocket.NotificationHandler;
import javax.websocket.*;


import java.util.Scanner;
import java.util.*;
public class ChessClient {

    public static ServerFacade serverFacade;
    public static String loggedInUsername = null;
    private static WebSocketFacade ws;
    private static NotificationHandler notificationHandler = null;
    public static String userAuthToken = null;
    public static ChessGame currGame = null;
    public static String currColor = null;
    public Session session;
    private static String serverUrl;

    public ChessClient(String serverUrl, NotificationHandler notificationHandler) {
        serverFacade = new ServerFacade(serverUrl);
        this.notificationHandler = notificationHandler;
        this.serverUrl = serverUrl;
    }



    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        boolean loggedIn = false;
        boolean inGame = false;

        if(serverFacade == null) {
            serverFacade = new ServerFacade("http://localhost:8080");
        }
        while (running) {
            if(!loggedIn) {
                System.out.println("""
                        ~You are currently logged out.~
                        Options:
                        \t1. Help
                        \t2. Register
                        \t3. Login
                        \t4. Quit
                        What would you like to do:\s""");
                String input = scanner.nextLine().trim();
                switch (input) {
                    case "1":
                        System.out.println("\nHelp: Available commands are:");
                        System.out.println("\t- Register: Register a new account.");
                        System.out.println("\t- Login: Log in with an existing account.");
                        System.out.println("\t- Quit: Exit the program.");
                        break;
                    case "2":
                        try {
                            AuthData newUser = registerUser(scanner);
                            loggedIn = true;
                            assert newUser != null;
                            loggedInUsername = newUser.username();
                            userAuthToken = newUser.authToken();

                            //System.out.println(newUser);
                        }catch (Exception e) {
                            loggedIn = false;
                            loggedInUsername = null;
                            userAuthToken = null;
                            System.out.println("Registration failed.");
                        }
                        break;
                    case "3":
                        try {
                            AuthData loginResult = loginUser(scanner);
                            if (loginResult != null) {
                                loggedIn = true;
                                loggedInUsername = loginResult.username();
                                userAuthToken = loginResult.authToken();
                                //System.out.println(loginResult);
                            }
                        } catch (Exception e) {
                            System.out.println("Unable to log in");
                        }
                        break;
                    case "4":
                        System.out.println("\nGoodbye!");
                        running = false;
                        break;
                    default:
                        System.out.println("\nInvalid choice.");
                }
            }
            else if(!inGame){
                // Post-login menu
                System.out.println("\n~You are currently logged in as " + loggedInUsername + ".~\nOptions:");
                System.out.println("""
                        \t1. Help
                        \t2. Logout
                        \t3. List existing games
                        \t4. Create a new game
                        \t5. Play an existing game
                        \t6. Observe a game
                        What would you like to do:\s""");
                String input = scanner.nextLine().trim();
                switch (input) {
                    case "1":
                        System.out.println("\nHelp: Available commands are:\n\t- Logout: Log out of your account.");
                        System.out.println("""
                                \t- List existing games: List all games that have been created in the server.
                                \t- Create a new game: Adds a new, blank game to the list of existing games.
                                \t- Play an existing game: Pick an existing game to join as a player and choose your color.
                                \t- Observe a game: Pick an existing game to watch, but not join as a player.""");
                        break;
                    case "2":
                        System.out.println("\nLogging out...\n");
                        serverFacade.logout(userAuthToken);
                        loggedIn = false;
                        loggedInUsername = null;
                        userAuthToken = null;
                        break;
                    case "3":
                        try {
                            listGames();
                        } catch (Exception ex) {
                            System.out.println("No games available");
                        }
                        break;
                    case "4":
                        createNewGame(scanner);
                        break;
                    case "5":
                        inGame = true;
                        try {
                            playGame(scanner, userAuthToken, loggedInUsername);
                        } catch (Exception e) {
                            System.out.println("Unable to join game");
                            inGame = false;
                        }
                        break;
                    case "6":
                        watchGame(scanner, userAuthToken);
                        break;
                    default:
                        System.out.println("\nInvalid choice.");
                }
            }
            else if(inGame) {
                System.out.println("\n~Playing Game~\nOptions:");
                System.out.println("""
                        \t1. Help
                        \t2. Update the board
                        \t3. Leave the game
                        \t4. Make a move
                        \t5. Resign
                        \t6. Highlight legal moves
                        What would you like to do:\s""");
                String input = scanner.nextLine().trim().toUpperCase();
                switch (input) {
                    case "1":
                        System.out.println("\nHelp: Available commands are:" +
                                "\n\t- Update the board: Redraws the chessboard with any updates" +
                                "\t- Leave the game: You leave the game" +
                                "\t- Make a move: Move one of your chess pieces on your turn" +
                                "\t- Resign: forfeit the game" +
                                "\t- Highlight legal moves: show legal moves for a specific chess piece");
                        break;
                    case "2":
                        updateBoard();
                        break;
                    case "3":
                        System.out.println("Leaving game...");
                        //Need to actually remove from game?
                        inGame = false;
                        currColor = null;
                        currGame = null;
                        break;
                    case "4":
                        makeMove(scanner);
                        break;
                }

            }
        }
        scanner.close();
    }

    private static void makeMove(Scanner scanner) {

        try{
            System.out.print("Enter the current location of the piece you would like to move (ex:a1): ");

            if(currGame.getTeamTurn() != ChessGame.TeamColor.BLACK && currColor.equals("BLACK")) {
                throw new Exception("Not your turn");
            }
            else if(currGame.getTeamTurn() != ChessGame.TeamColor.WHITE && currColor.equals("WHITE")) {
                throw new Exception("Not your turn");
            }

            String start;
            start = scanner.nextLine().trim();

            // Convert column (letter) to a number (1 to 8)
            int actualCol = start.charAt(0) - 'a' + 1;

            // Convert row (number as char) to an integer (1 to 8)
            int actualRow = start.charAt(1) - '0';

            ChessPosition startPos = new ChessPosition(actualRow, actualCol);

            System.out.print("Enter where you would like to move your " + currGame.getBoard().currBoard[actualRow][actualCol].getPieceType() + " (ex:a1): ");
            String end = scanner.nextLine().trim();

            // Convert column (letter) to a number (1 to 8)
            actualCol = end.charAt(0) - 'a' + 1;

            // Convert row (number as char) to an integer (1 to 8)
            actualRow = end.charAt(1) - '0';

            //System.out.println("ROW: " + actualRow + "\nCOL: " + actualCol);
            ChessPosition endPos = new ChessPosition(actualRow, actualCol);

            //making move!
            ChessMove newMove = new ChessMove(startPos,endPos,null);
            currGame.makeMove(newMove);
            //update the current game using the server


        }catch (Exception e) {
            System.out.println("unable to make the move sorry :/");
            System.out.println(e.getMessage());
        }
    }

    private static void updateBoard() {
        ChessboardRenderer.drawBoard(currGame, currColor);
    }

    private static void playGame(Scanner scanner, String userAuthToken, String loggedInUsername) throws Exception  {
        // First, list the games
        List<GameData> allGames = listGames();

        // Get the game number and color from the user
        System.out.print("\nEnter the number of the game you want to join: ");
        int gameNumber = Integer.parseInt(scanner.nextLine().trim());

        String color;

        assert allGames != null;
        if(allGames.get(gameNumber-1).blackUsername() != null && allGames.get(gameNumber-1).blackUsername().equals(loggedInUsername)) {
            System.out.println("You have joined this game previously as the black player, but left the game. Please start a new game.");
            throw new Exception();
        }
        else if(allGames.get(gameNumber-1).whiteUsername() != null && allGames.get(gameNumber-1).whiteUsername().equals(loggedInUsername)) {
            System.out.println("You have joined this game previously as the white player, but left the game. Please start a new game.");
            throw new Exception();
        }
        else {
            System.out.print("Enter the color you want to play (WHITE or BLACK): ");
            color = scanner.nextLine().trim().toUpperCase();
        }



                ServerFacade serverFacade = new ServerFacade("http://localhost:8080");

                // Join the selected game with the specified color
                currGame = serverFacade.joinGame(userAuthToken, gameNumber, color);
                currColor = color;
                System.out.println("Successfully joined the game as " + color + ".");

                //do websocket stuff
                ws = new WebSocketFacade(serverFacade.serverUrl, notificationHandler);
                ws.playerJoinGame(loggedInUsername,userAuthToken,gameNumber);

                // After joining the game, display the board

                ChessboardRenderer.drawBoard(currGame, currColor);

    }

    private static void watchGame(Scanner scanner, String userAuthToken) {


        // Get the game number and color from the user
        System.out.print("\nEnter the number of the game you want to watch: ");
        int gameNumber = Integer.parseInt(scanner.nextLine().trim());

        try {

            System.out.println("Enjoy the game!");

            // After joining the game, display the board
            //NOTE: THIS IS A GENERIC BOARD, NOT THE ACTUAL GAME BOARD
            ChessboardRenderer.drawBoard(new ChessGame(), "WHITE");  // Display the board from the correct perspective

        } catch (Exception e) {
            System.out.println("Unable to watch selected game.");
        }
    }

    private static void createNewGame(Scanner scanner) {
        System.out.print("Enter a unique game name: ");
        String gameName = scanner.nextLine().trim();

        try {
            ServerFacade serverFacade = new ServerFacade("http://localhost:8080");
            GameData game = new GameData(0,null,null,gameName);
            //System.out.println("Auth Token: " + userAuthToken);
            serverFacade.createGame(game, loggedInUsername, userAuthToken);
            System.out.println("Game created successfully!");
        } catch (Exception e) {
            System.out.println("Unable to create game");
            System.out.println(e.getMessage());
        }
    }

    private static List<GameData> listGames() throws Exception  {
        ServerFacade serverFacade = new ServerFacade("http://localhost:8080");
        List<GameData> games = serverFacade.listGames(userAuthToken);


        try {
            if (games == null || games.isEmpty()) {
                System.out.println("\nNo games available on the server.");
                return null;
            } else {
                System.out.println("\nExisting games:");
                for (int i = 0; i < games.size(); i++) {
                    GameData game = games.get(i);
                    System.out.println((i + 1) + ". " + " Game Name: " + game.gameName());
                    String whiteUser = game.whiteUsername();
                    if(whiteUser == null){
                        whiteUser = "Not Joined (Empty)";
                    }
                    String blackUser = game.blackUsername();
                    if(blackUser == null){
                        blackUser = "Not Joined (Empty)";
                    }
                    System.out.println("\tWhite Player: " + whiteUser + "\tBlack Player: " + blackUser);
                }
                return games;
            }
        } catch (Exception e) {
            System.out.println("Unable to list games");
        }
        return null;
    }

    private static AuthData loginUser(Scanner scanner) {
        System.out.print("\nEnter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            UserData user = new UserData(username, password, null);

            AuthData result = serverFacade.login(user);
            System.out.println("Login successful! Welcome, " + result.username());

            //make websocket connection?



            return result;
        } catch (Exception e) {
            System.out.println("Login failed, incorrect username or password\n");
            return null;
        }
    }

    // Method to handle user registration
    private static AuthData registerUser(Scanner scanner) {
        System.out.println("\nRegistering a new user:");

        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        // Create RegisterRequest object
        UserData user = new UserData(username, password, email);

        try {
            // Initialize the chessclient.ServerFacade
            //ServerFacade serverFacade = new ServerFacade("http://localhost:8080");
            // Call the register method on the serverFacade
            AuthData result = serverFacade.register(user);

            // Check if registration was successful
            if (result != null) {
                System.out.println("Registration successful! Welcome, " + result.username());
                return result;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
    }
}
