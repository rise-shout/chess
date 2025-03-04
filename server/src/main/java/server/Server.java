package server;

import dataaccess.AuthTokenDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import server.DatabaseController;
import server.GameController;
import server.UserController;
import service.DatabaseService;
import service.GameService;
import service.UserService;
import spark.Spark;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        UserDAO userDAO = new UserDAO();
        GameDAO gameDAO = new GameDAO();
        AuthTokenDAO authTokenDAO = AuthTokenDAO.getInstance();

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
        //Spark.put("/game", gameController.joinGame);

        Spark.init();
        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
    }
}