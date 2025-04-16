package server;

import dataaccess.*;

import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import server.websocket.WebSocketHandler;
import service.DatabaseService;
import service.GameService;
import service.UserService;
import spark.Spark;

public class Server {
    // final GameService service;



    public int run(int desiredPort) {
        System.out.println("Setting Spark port...");
        Spark.port(desiredPort);

        System.out.println("Setting static files location...");
        Spark.staticFiles.location("web");



        System.out.println("Initializing DAOs...");
        // Determine which data access implementation to use
        GameDataAccess gameDAO;
        UserDataAccess userDAO;
        AuthDataAccess authTokenDAO;


        String storageType = System.getProperty("storageType", "sql"); // Default to 'sql' if not set

        // Choose which DAO to use based on storageType
        if ("sql".equalsIgnoreCase(storageType)) {
            gameDAO = new MySqlGameDataAccess();
            userDAO = new MySqlUserDataAccess();
            authTokenDAO = new MySqlAuthDataAccess();
        } else {
            // Use in-memory or another storage implementation (make sure to implement these)
            gameDAO = new GameDAO();
            userDAO = new UserDAO();
            authTokenDAO = new AuthTokenDAO();
        }


        UserService userService = new UserService(userDAO, authTokenDAO);
        GameService gameService = new GameService(gameDAO, authTokenDAO);

        UserController userController = new UserController(userService);
        GameController gameController = new GameController(gameService);

        DatabaseController dbController = new DatabaseController(new DatabaseService(userDAO, gameDAO, authTokenDAO));

        System.out.println("Registering routes...");

        WebSocketHandler webSocketHandler = new WebSocketHandler(userDAO, authTokenDAO, gameDAO);
        Spark.webSocket("/ws", webSocketHandler);

        // Register routes
        Spark.delete("/db", dbController.clearDatabase);
        Spark.post("/user", userController.register);
        Spark.post("/session", userController.login);
        Spark.delete("/session", userController.logout);
        Spark.get("/game", gameController.listGames);
        Spark.post("/game", gameController.createGame);
        Spark.put("/game", gameController.joinGame);

        //maybe ok?
        //Spark.put("/game/change", gameController.updateGame);

        System.out.println("Starting Spark...");
        Spark.init();
        Spark.awaitInitialization();
        System.out.println("Spark started on port: " + Spark.port());
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        //System.out.println("SERVER HAS BEEN STOPPED, NOW WAITING");

        Spark.awaitStop(); // Wait for Spark to stop completely
        try {
            Thread.sleep(100); // Add a small delay to ensure cleanup
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        session.getRemote().sendString("WebSocket response: " + message);
    }
}