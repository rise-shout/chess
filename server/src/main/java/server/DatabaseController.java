package server;

import dataaccess.DataAccessException;
import service.DatabaseService;
import spark.Request;
import spark.Response;
import spark.Route;

public class DatabaseController {
    private DatabaseService databaseService = null;

    public DatabaseController(DatabaseService databaseService) {
        this.databaseService = new DatabaseService();
    }

    public Route clearDatabase = (Request req, Response res) -> {
        try {
            databaseService.clearDatabase();
            res.status(200);
            return "{}"; // Empty JSON response
        } catch (DataAccessException e) {
            res.status(500);
            return "{\"message\": \"Error: " + e.getMessage() + "\"}";
        }
    };
}
