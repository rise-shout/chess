package dataaccess;

import com.google.gson.Gson;
import model.AuthData;
import java.util.ArrayList;
import java.util.*;
import java.sql.*;

import java.util.Map;

import static dataaccess.MySqlUserDataAccess.updateDoer;

public class MySqlAuthDataAccess implements AuthDataAccess {

    public MySqlAuthDataAccess()  {
        try {
            configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public void insertAuth(AuthData authData) throws DataAccessException {
        var statement = "INSERT INTO auth (username, auth_token) VALUES (?, ?)";
        executeUpdate(statement, authData.username(), authData.authToken());
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        var query = "SELECT username, auth_token FROM auth WHERE auth_token = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, authToken);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new AuthData(rs.getString("auth_token"), rs.getString("username"));
                } else {
                    return null; // Token not found
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to retrieve users: " + e.getMessage());
        }
    }

    @Override
    public String getAuthToken(String username) throws DataAccessException {
        var query = "SELECT auth_token FROM auth WHERE username = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("auth_token");
                } else {
                    return null; // No token found for the given username
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error querying auth token by username" + e.getMessage());
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        var statement = "DELETE FROM auth WHERE auth_token = ?";
        executeUpdate(statement, authToken);

    }

    @Override
    public Map<String, AuthData> getAllAuthTokens() throws DataAccessException {
        var query = "SELECT username, auth_token FROM auth";
        Map<String, AuthData> authDataMap = new HashMap<>();

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String authToken = rs.getString("auth_token");
                String username = rs.getString("username");
                authDataMap.put(authToken, new AuthData(authToken, username));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to retrieve users: " + e.getMessage());
        }

        return authDataMap;
    }

    @Override
    public void setAuthTokens(Map<String, AuthData> newTokens) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            // Clear the table first
            var deleteAllStatement = "DELETE FROM auth";
            try (PreparedStatement deleteStmt = connection.prepareStatement(deleteAllStatement)) {
                deleteStmt.executeUpdate();
            }

            // Insert the new tokens
            var insertStatement = "INSERT INTO auth (username, auth_token) VALUES (?, ?)";
            try (PreparedStatement insertStmt = connection.prepareStatement(insertStatement)) {
                for (AuthData authData : newTokens.values()) {
                    insertStmt.setString(1, authData.username());
                    insertStmt.setString(2, authData.authToken());
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to retrieve users: " + e.getMessage());
        }

    }

    private void configureDatabase() throws DataAccessException {
        MySqlUserDataAccess.databaseMaker(createStatements);
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {

        return updateDoer(statement, params);
    }

    private final String[] createStatements = {
            """
    CREATE TABLE IF NOT EXISTS auth (
      username VARCHAR(256) NOT NULL,
      auth_token VARCHAR(256) NOT NULL
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
    """
    };

    public void clearAllTokens() throws DataAccessException {
        String statement = "TRUNCATE auth";
        executeUpdate(statement);
    }
}
