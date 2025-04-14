

import chess.ChessBoard;
import chess.ChessGame;
import model.AuthData;
import model.GameData;
import service.LoginRequest;
import service.LoginResult;
import service.RegisterRequest;
import service.RegisterResult;

import java.util.Scanner;
import java.util.*;
public class ChessClient {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        boolean loggedIn = false;
        boolean inGame = false;
        String loggedInUsername = null;
        String userAuthToken = null;

        System.out.println("Welcome to the Chess Client!");

        while (running) {
            if(!loggedIn) {
                System.out.println("\n~You are currently logged out.~\nOptions:");
                System.out.println("\t1. Help");
                System.out.println("\t2. Register");
                System.out.println("\t3. Login");
                System.out.println("\t4. Quit");
                System.out.print("What would you like to do: ");

                String input = scanner.nextLine().trim();

                switch (input) {
                    case "1":
                        System.out.println("\nHelp: Available commands are:");
                        System.out.println("\t- Register: Register a new account.");
                        System.out.println("\t- Login: Log in with an existing account.");
                        System.out.println("\t- Quit: Exit the program.");
                        break;
                    case "2":
                        RegisterResult registerResult = registerUser(scanner);
                        loggedIn = true;
                        assert registerResult != null;
                        loggedInUsername = registerResult.username();
                        userAuthToken = registerResult.authToken();
                        break;
                    case "3":
                        LoginResult loginResult = loginUser(scanner);
                        loggedIn = true;
                        assert loginResult != null;
                        loggedInUsername = loginResult.username();
                        userAuthToken = loginResult.authToken();
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
                System.out.println("\t1. Help");
                System.out.println("\t2. Logout");
                System.out.println("\t3. List existing games");
                System.out.println("\t4. Create a new game");
                System.out.println("\t5. Play an existing game");
                System.out.println("\t6. Observe a game");
                System.out.print("What would you like to do: ");

                String input = scanner.nextLine().trim();

                switch (input) {
                    case "1":
                        System.out.println("\nHelp: Available commands are:");
                        System.out.println("\t- Logout: Log out of your account.");
                        System.out.println("\t- List existing games: List all games that have been created in the server.");
                        System.out.println("\t- Create a new game: Adds a new, blank game to the list of existing games.");
                        System.out.println("\t- Play an existing game: Pick an existing game to join as a player and choose your color.");
                        System.out.println("\t- Observe a game: Pick an existing game to watch, but not join as a player.");
                        break;
                    case "2":
                        System.out.println("\nLogging out...");
                        loggedIn = false;
                        loggedInUsername = null;
                        break;
                    case "3":
                        listGames(userAuthToken);
                        break;
                    case "4":
                        createNewGame(scanner, userAuthToken);
                        break;
                    case "5":
                        inGame = true;
                        try {
                            playGame(scanner, userAuthToken, loggedInUsername);
                        } catch (Exception e) {
                            inGame = false;
                        }
                        break;
                    case "6":
                        System.out.println("\nPick a game to observe... (Functionality not implemented yet).");
                        break;
                    default:
                        System.out.println("\nInvalid choice.");
                }
            }
        }

        scanner.close();
    }

    private static void playGame(Scanner scanner, String userAuthToken, String loggedInUsername) {
        // First, list the games
        List<GameData> allGames = listGames(userAuthToken);

        // Get the game number and color from the user
        System.out.print("\nEnter the number of the game you want to join: ");
        int gameNumber = Integer.parseInt(scanner.nextLine().trim());

        String color;
        boolean resumeGame = false;

        if(allGames.get(gameNumber-1).blackUsername().equals(loggedInUsername)) {
            System.out.println("You have joined this game previously as the black player. Resuming game...");
            color = "BLACK";
            resumeGame = true;
        }
        else if(allGames.get(gameNumber-1).whiteUsername().equals(loggedInUsername)) {
            System.out.println("You have joined this game previously as the white player. Resuming game...");
            color = "WHITE";
            resumeGame = true;
        }
        else {
            System.out.print("Enter the color you want to play (WHITE or BLACK): ");
            color = scanner.nextLine().trim().toUpperCase();
        }

        try {
            if(!resumeGame) {
                ServerFacade serverFacade = new ServerFacade("http://localhost:8080");

                // Join the selected game with the specified color
                serverFacade.joinGame(userAuthToken, gameNumber, color);
                System.out.println("Successfully joined the game as " + color + ".");
            }

            // After joining the game, display the board
            //NOTE: THIS IS A GENERIC BOARD, NOT THE ACTUAL GAME BOARD
            ChessboardRenderer.drawBoard(new ChessBoard(), color);  // Display the board from the correct perspective

        } catch (Exception e) {
            System.out.println("Error joining the game: " + e.getMessage());
        }
    }

    private static void createNewGame(Scanner scanner, String userAuthToken) {
        System.out.print("Enter a unique game name: ");
        String gameName = scanner.nextLine().trim();

        try {
            ServerFacade serverFacade = new ServerFacade("http://localhost:8080");
            int gameId = serverFacade.createGame(userAuthToken, gameName);
            System.out.println("Game created successfully with ID: " + gameId);
        } catch (Exception e) {
            System.out.println("Error creating game: " + e.getMessage());
        }
    }

    private static List<GameData> listGames(String authToken) {
        try {
            ServerFacade serverFacade = new ServerFacade("http://localhost:8080");
            List<GameData> games = serverFacade.listGames(authToken); // Pass the auth token

            if (games.isEmpty()) {
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
            System.out.println("Error retrieving games: " + e.getMessage());
        }
        return null;
    }

    private static LoginResult loginUser(Scanner scanner) {
        System.out.print("\nEnter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            LoginRequest loginRequest = new LoginRequest(username, password);
            ServerFacade serverFacade = new ServerFacade("http://localhost:8080");
            LoginResult result = serverFacade.login(loginRequest);
            System.out.println("Login successful! Welcome, " + result.username());
            return result;
        } catch (Exception e) {
            System.out.println("Login failed, incorrect username or password");
            return null;
        }
    }

    // Method to handle user registration
    private static RegisterResult registerUser(Scanner scanner) {
        System.out.println("\nRegistering a new user:");

        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        // Create RegisterRequest object
        RegisterRequest registerRequest = new RegisterRequest(username, password, email);

        try {
            // Initialize the ServerFacade
            ServerFacade serverFacade = new ServerFacade("http://localhost:8080");
            // Call the register method on the serverFacade
            RegisterResult result = serverFacade.register(registerRequest);

            // Check if registration was successful
            if (result != null) {
                System.out.println("Registration successful! Welcome, " + result.username());
                return result;
            } else {
                System.out.println("Registration failed. Please try again.");
                return null;
            }
        } catch (Exception e) {
            System.out.println("Registration failed.");
            return null;
        }
    }
}
