package chessclient;

import chess.ChessGame;


import model.*;
import server.ServerFacade;


import java.util.Scanner;
import java.util.*;
public class ChessClient {

    public static ServerFacade serverFacade;
    public static String loggedInUsername = null;
    public static String userAuthToken = null;

        public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        boolean loggedIn = false;
        boolean inGame = false;
        System.out.println("Welcome to the Chess Client!");
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
                System.out.println("Currently in game. Press X to return to menu.");
                String input = scanner.nextLine().trim().toUpperCase();
                switch (input) {
                    case "X":
                        inGame = false;
                        break;
                }

            }
        }
        scanner.close();
    }

    private static void playGame(Scanner scanner, String userAuthToken, String loggedInUsername) throws Exception  {
        // First, list the games
        List<GameData> allGames = listGames();

        // Get the game number and color from the user
        System.out.print("\nEnter the number of the game you want to join: ");
        int gameNumber = Integer.parseInt(scanner.nextLine().trim());

        String color;
        boolean resumeGame = false;

        assert allGames != null;
        if(allGames.get(gameNumber-1).blackUsername() != null && allGames.get(gameNumber-1).blackUsername().equals(loggedInUsername)) {
            System.out.println("You have joined this game previously as the black player. Resuming game...");
            color = "BLACK";
            resumeGame = true;
        }
        else if(allGames.get(gameNumber-1).whiteUsername() != null && allGames.get(gameNumber-1).whiteUsername().equals(loggedInUsername)) {
            System.out.println("You have joined this game previously as the white player. Resuming game...");
            color = "WHITE";
            resumeGame = true;
        }
        else {
            System.out.print("Enter the color you want to play (WHITE or BLACK): ");
            color = scanner.nextLine().trim().toUpperCase();
        }


            if(!resumeGame) {
                ServerFacade serverFacade = new ServerFacade("http://localhost:8080");

                // Join the selected game with the specified color
                serverFacade.joinGame(userAuthToken, gameNumber, color);
                System.out.println("Successfully joined the game as " + color + ".");
            }

            // After joining the game, display the board
            //NOTE: THIS IS A GENERIC BOARD, NOT THE ACTUAL GAME BOARD
            ChessboardRenderer.drawBoard(new ChessGame(), color);  // Display the board from the correct perspective


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
            int gameId = serverFacade.createGame(game, loggedInUsername, userAuthToken);
            System.out.println("Game created successfully with ID: " + gameId);
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
            return result;
        } catch (Exception e) {
            System.out.println("Login failed, incorrect username or password");
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
            // Initialize the server.ServerFacade
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
}
