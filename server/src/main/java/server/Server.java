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
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Determine which data access implementation to use
        GameDataAccess gameDAO;
        //UserDAO userDAO;
        AuthTokenDAO authTokenDAO = AuthTokenDAO.getInstance();


        String storageType = System.getProperty("storageType", "sql"); // Default to 'sql' if not set

        // Choose which DAO to use based on storageType
        //userDAO = new MySqlUserDataAccess(); // Assuming MySqlUserDataAccess is similar to MySqlGameDataAccess
        if ("sql".equalsIgnoreCase(storageType)) gameDAO = new MySqlGameDataAccess();
        else {
            // Use in-memory or another storage implementation (make sure to implement these)
            gameDAO = new GameDAO();
            //userDAO = new InMemoryUserDataAccess(); // Implement this if necessary
        }



        UserDAO userDAO = new UserDAO();
        //GameDAO gameDAO = new GameDAO();
        //AuthTokenDAO authTokenDAO = AuthTokenDAO.getInstance();

        UserService userService = new UserService(userDAO, authTokenDAO);
        GameService gameService = new GameService(gameDAO, authTokenDAO);

        UserController userController = new UserController(userService);
        GameController gameController = new GameController(gameService);

        DatabaseController dbController = new DatabaseController(new DatabaseService(userDAO, gameDAO, authTokenDAO));

        // Register routes
        Spark.delete("/db", dbController.clearDatabase);
        Spark.post("/user", userController.register);
        Spark.post("/session", userController.login);
        Spark.delete("/session", userController.logout);
        Spark.get("/game", gameController.listGames);
        Spark.post("/game", gameController.createGame);
        Spark.put("/game", gameController.joinGame);

        Spark.init();
        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
    }
}