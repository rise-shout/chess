package dataaccess;


import model.AuthData;
import java.sql.*;

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
    public void deleteAuth(String authToken) throws DataAccessException {
        var statement = "DELETE FROM auth WHERE auth_token = ?";
        executeUpdate(statement, authToken);

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
