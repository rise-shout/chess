

import service.LoginRequest;
import service.LoginResult;

import java.util.Scanner;

public class ChessClient {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("Welcome to the Chess Client!");

        while (running) {
            System.out.println("\nPrelogin Menu:");
            System.out.println("1. Help");
            System.out.println("2. Register");
            System.out.println("3. Login");
            System.out.println("4. Quit");
            System.out.print("Enter your choice: ");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    System.out.println("\nHelp: Available commands are:");
                    System.out.println("- Register: Register a new account.");
                    System.out.println("- Login: Log in with an existing account.");
                    System.out.println("- Quit: Exit the program.");
                    break;
                case "2":
                    System.out.println("\nRegister functionality will be implemented soon.");
                    break;
                case "3":
                    System.out.println("\nEnter username: ");
                    String username = scanner.nextLine();
                    System.out.println("Enter password: ");
                    String password = scanner.nextLine();

                    try {
                        LoginRequest loginRequest = new LoginRequest(username, password);
                        ServerFacade serverFacade = new ServerFacade("http://localhost:8080"); // Replace with your server URL
                        LoginResult result = serverFacade.login(loginRequest);
                        System.out.println("Login successful! Welcome, " + result.username());
                    } catch (Exception e) {
                        System.out.println("Login failed: " + e.getMessage());
                    }
                    break;
                case "4":
                    System.out.println("\nGoodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("\nInvalid choice. Please try again.");
            }
        }

        scanner.close();
    }
}
