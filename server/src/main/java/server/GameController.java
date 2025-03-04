package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.GameData;
import server.GameListResult;
import service.ErrorResult;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.List;

public class GameController {
    private final GameService gameService = new GameService();
    private final Gson gson = new Gson();

    public GameController() {

    }

    public Route listGames = (Request req, Response res) -> {
        try {
            // Extract the auth token from the header
            String authToken = req.headers("authorization");

            // Validate the auth token
            if (authToken == null || authToken.isEmpty()) {
                res.status(401);
                return gson.toJson(new ErrorResult("Error: unauthorized"));
            }

            // Call the service to list games
            List<GameData> games = gameService.listGames(authToken);

            // Return a successful response
            res.status(200);
            return gson.toJson(new GameListResult(games));

        } catch (DataAccessException e) {
            if (e.getMessage().contains("unauthorized")) {
                res.status(401);
                return gson.toJson(new ErrorResult(e.getMessage()));
            }
            res.status(500);
            return gson.toJson(new ErrorResult("Error: " + e.getMessage()));
        }
    };
}