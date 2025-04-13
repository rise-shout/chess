package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.*;
import spark.Request;
import spark.Response;
import spark.Route;

public class UserController {
    private UserService userService;
    private Gson gson;

    public UserController(UserService userService) {
        this.userService = userService;
        this.gson = new Gson();
    }

    public Route register = (Request req, Response res) -> {
        try {
            RegisterRequest registerRequest = gson.fromJson(req.body(), RegisterRequest.class);
            RegisterResult result = userService.register(registerRequest);
            res.status(200);

            // Validate the request (you might want to add more validation here)
            if (registerRequest.username() == null || registerRequest.username().isEmpty() ||
                    registerRequest.password() == null || registerRequest.password().isEmpty() ||
                    registerRequest.email() == null || registerRequest.email().isEmpty()) {
                res.status(400);
                return gson.toJson(new ErrorResult("Error: bad request"));
            }

            return gson.toJson(result);
        } catch (DataAccessException e) {
            if (e.getMessage().contains("bad request")) {
                res.status(400);
            } else if (e.getMessage().contains("already taken")) {
                res.status(403);
            } else {
                res.status(500);
            }
            return gson.toJson(new ErrorResult("Error: " + e.getMessage()));
        }
    };

    public Route login = (Request req, Response res) -> {
        try {
            LoginRequest loginRequest = gson.fromJson(req.body(), LoginRequest.class);
            LoginResult result = userService.login(loginRequest);
            res.status(200);
            return gson.toJson(result);
        } catch (DataAccessException e) {
            if (e.getMessage().contains("unauthorized")) {
                res.status(401);
            } else {
                res.status(500);
            }
            return gson.toJson(new ErrorResult("Error: " + e.getMessage()));
        }
    };

    public Route logout = (Request req, Response res) -> {
        try {
            String authToken = req.headers("authorization");
            userService.logout(authToken);
            res.status(200);
            return "{}";
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