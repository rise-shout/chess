package server;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.GameData;
import service.*;
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

            // Validate the auth token
            if (authToken == null || authToken.isEmpty()) {
                res.status(401);
                return gson.toJson(new ErrorResult("Error: unauthorized CAUGHT EARLY"));
            }

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


    public Route joinGame = (Request req, Response res) -> {
        try {
            String authToken = req.headers("authorization");

            // Validate the auth token
            if (authToken == null || authToken.isEmpty()) {
                res.status(401);
                return gson.toJson(new ErrorResult("Error: unauthorized"));
            }

            // Deserialize the request body into a JoinGameRequest
            JoinGameRequest joinGameRequest = gson.fromJson(req.body(), JoinGameRequest.class);

            // Validate the request
            if (joinGameRequest == null ||  joinGameRequest.playerColor() == null
                    || joinGameRequest.playerColor().isEmpty() || joinGameRequest.gameID() <= 0) {
                res.status(400);
                return gson.toJson(new ErrorResult("Error: bad request"));
            }

            // Call the service to join the game
            ChessGame currentGame = gameService.joinGame(authToken, joinGameRequest.gameID(), joinGameRequest.playerColor());

            // Return a successful response
            res.status(200);
            return gson.toJson(new JoinGameResult(currentGame)); // Empty JSON response

        } catch (DataAccessException e) {
            if (e.getMessage().contains("unauthorized")) {
                res.status(401);
                return gson.toJson(new ErrorResult(e.getMessage()));
            } else if (e.getMessage().contains("bad request")) {
                res.status(400);
                return gson.toJson(new ErrorResult(e.getMessage()));
            } else if (e.getMessage().contains("already taken")) {
                res.status(403);
                return gson.toJson(new ErrorResult(e.getMessage()));
            } else {
                res.status(500);
                return gson.toJson(new ErrorResult("Error: " + e.getMessage()));
            }
        }
    };
}