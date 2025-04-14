

import service.LoginRequest;
import service.LoginResult;
import service.RegisterRequest;
import service.RegisterResult;

import java.util.Scanner;

public class ChessClient {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        boolean loggedIn = false;
        String loggedInUsername = null;

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
                        registerUser(scanner);
                        break;
                    case "3":
                        LoginResult loginResult = loginUser(scanner);
                        loggedIn = true;
                        assert loginResult != null;
                        loggedInUsername = loginResult.username();
                        break;
                    case "4":
                        System.out.println("\nGoodbye!");
                        running = false;
                        break;
                    default:
                        System.out.println("\nInvalid choice.");
                }
            }
            else {
                // Post-login menu
                System.out.println("\n~You are currently logged in as " + loggedInUsername + ".~\nOptions:");
                System.out.println("\t1. Help");
                System.out.println("\t2. Start a Game");
                System.out.println("\t3. Logout");
                System.out.println("\t4. Quit");
                System.out.print("What would you like to do: ");

                String input = scanner.nextLine().trim();

                switch (input) {
                    case "1":
                        System.out.println("\nHelp: Available commands are:");
                        System.out.println("\t- Start a Game: Begin a new chess game.");
                        System.out.println("\t- Logout: Log out of your account.");
                        System.out.println("\t- Quit: Exit the program.");
                        break;
                    case "2":
                        System.out.println("\nStarting a new game... (Functionality not implemented yet).");
                        break;
                    case "3":
                        System.out.println("\nLogging out...");
                        loggedIn = false;
                        loggedInUsername = null;
                        break;
                    case "4":
                        System.out.println("\nGoodbye!");
                        running = false;
                        break;
                    default:
                        System.out.println("\nInvalid choice.");
                }
            }
        }

        scanner.close();
    }

    private static LoginResult loginUser(Scanner scanner) {
        System.out.println("\nEnter username: ");
        String username = scanner.nextLine();
        System.out.println("Enter password: ");
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
    private static void registerUser(Scanner scanner) {
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
            } else {
                System.out.println("Registration failed. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("Registration failed.");
        }
    }
}
