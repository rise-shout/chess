package server;

import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        DatabaseController dbController = new DatabaseController();
        UserController userController = new UserController();
        //GameController gameController = new GameController();

        // Register your endpoints and handle exceptions here.
        // Register routes
        Spark.delete("/db", dbController.clearDatabase);
        Spark.post("/user", userController.register);

        //This line initializes the server and can be removed once you have a functioning endpoint
        System.out.println(Spark.routes());

        //Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}