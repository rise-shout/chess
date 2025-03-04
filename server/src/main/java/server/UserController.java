package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.*;
import spark.Request;
import spark.Response;
import spark.Route;

public class UserController {
    private final UserService userService = new UserService();
    private final Gson gson = new Gson();;

    public UserController() {


    }

    public Route register = (Request req, Response res) -> {
        try {
            // Deserialize the request body into a RegisterRequest
            RegisterRequest registerRequest = gson.fromJson(req.body(), RegisterRequest.class);

            // Validate the request (you might want to add more validation here)
            if (registerRequest.username().isEmpty() || registerRequest.password().isEmpty() || registerRequest.email().isEmpty()) {
                res.status(400);
                return gson.toJson(new ErrorResult("Error: bad request"));
            }

            // Call the service to register the user
            RegisterResult result = userService.register(registerRequest);

            // Return a successful response
            res.status(200);
            return gson.toJson(result);

        } catch (DataAccessException e) {
            if (e.getMessage().contains("already taken")) {
                res.status(403);
                return gson.toJson(new ErrorResult(e.getMessage()));
            }
            res.status(500);
            return gson.toJson(new ErrorResult("Error: " + e.getMessage()));
        }
    };

    public Route login = (Request req, Response res) -> {
        try {
            // Deserialize the request body into a LoginRequest
            LoginRequest loginRequest = gson.fromJson(req.body(), LoginRequest.class);

            // Call the service to log in the user
            LoginResult result = userService.login(loginRequest);

            // Return a successful response
            res.status(200);
            return gson.toJson(result);

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
