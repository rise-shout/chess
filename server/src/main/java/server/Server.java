package server;

import dataaccess.*;

import server.DatabaseController;
import server.GameController;
import server.UserController;
import service.DatabaseService;
import service.GameService;
import service.UserService;
import spark.Spark;

public class Server {

    /**
     * Starts the server on the given port. Defaults to SQL storage if the port is '0', otherwise uses in-memory storage.
     */

    public int run(int desiredPort) {
        System.out.println("Setting Spark port...");
        Spark.port(desiredPort);

        System.out.println("Setting static files location...");
        Spark.staticFiles.location("web");

        System.out.println("Initializing DAOs...");
        // Determine which data access implementation to use
        GameDataAccess gameDAO;
        UserDataAccess userDAO;
        AuthTokenDAO authTokenDAO = AuthTokenDAO.getInstance();


        String storageType = System.getProperty("storageType", "sql"); // Default to 'sql' if not set

        // Choose which DAO to use based on storageType
        userDAO = new MySqlUserDataAccess(); // Assuming MySqlUserDataAccess is similar to MySqlGameDataAccess
        if ("sql".equalsIgnoreCase(storageType)) gameDAO = new MySqlGameDataAccess();
        else {
            // Use in-memory or another storage implementation (make sure to implement these)
            gameDAO = new GameDAO();
            userDAO = new UserDAO();
        }


        UserService userService = new UserService(userDAO, authTokenDAO);
        GameService gameService = new GameService(gameDAO, authTokenDAO);

        UserController userController = new UserController(userService);
        GameController gameController = new GameController(gameService);

        DatabaseController dbController = new DatabaseController(new DatabaseService(userDAO, gameDAO, authTokenDAO));

        System.out.println("Registering routes...");
        // Register routes
        Spark.delete("/db", dbController.clearDatabase);
        Spark.post("/user", userController.register);
        Spark.post("/session", userController.login);
        Spark.delete("/session", userController.logout);
        Spark.get("/game", gameController.listGames);
        Spark.post("/game", gameController.createGame);
        Spark.put("/game", gameController.joinGame);

        System.out.println("Starting Spark...");
        Spark.init();
        Spark.awaitInitialization();
        System.out.println("Spark started on port: " + Spark.port());
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        System.out.println("SERVER HAS BEEN STOPPED, NOW WAITING");

        Spark.awaitStop(); // Wait for Spark to stop completely
        try {
            Thread.sleep(100); // Add a small delay to ensure cleanup
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}