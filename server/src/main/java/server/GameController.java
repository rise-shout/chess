package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.GameData;
import server.GameListResult;
import service.ErrorResult;
import service.GameRequest;
import service.GameResponse;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.List;

public class GameController {
    private GameService gameService;
    private Gson gson;

    public GameController(GameService gameService) {
        this.gameService = gameService;
        this.gson = new Gson();
    }

    public Route createGame = (Request req, Response res) -> {
        try {
            String authToken = req.headers("authorization");
            GameRequest gameRequest = gson.fromJson(req.body(), GameRequest.class);
            int gameId = gameService.createGame(authToken, gameRequest.gameName());
            res.status(200);
            return gson.toJson(new GameResponse(gameId));
        } catch (DataAccessException e) {
            if (e.getMessage().contains("unauthorized")) {
                res.status(401);
            } else if (e.getMessage().contains("bad request")) {
                res.status(400);
            } else {
                res.status(500);
            }
            return gson.toJson(new ErrorResult("Error: " + e.getMessage()));
        }
    };

    public Route listGames = (Request req, Response res) -> {
        try {
            String authToken = req.headers("authorization");
            List<GameData> games = gameService.listGames(authToken);
            res.status(200);
            return gson.toJson(new GameListResult(games));
        } catch (DataAccessException e) {
            if (e.getMessage().contains("unauthorized")) {
                res.status(401);
            } else {
                res.status(500);
            }
            return gson.toJson(new ErrorResult("Error: " + e.getMessage()));
        }
    };
}