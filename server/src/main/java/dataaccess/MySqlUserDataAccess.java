package dataaccess;

import com.google.gson.Gson;
//import exception.ResponseException;
import model.GameData;
import model.UserData;
import model.AuthData;



import java.util.ArrayList;
import java.util.Collection;
import java.util.*;
import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

import org.mindrot.jbcrypt.BCrypt;

public class MySqlUserDataAccess implements UserDataAccess{

    public MySqlUserDataAccess() {

    }


    @Override
    public void clearAllUsers() throws DataAccessException {
        var statement = "TRUNCATE user";
        executeUpdate(statement);
    }

    @Override
    public void insertUser(UserData user) throws DataAccessException {
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        var statement = "INSERT INTO user (username, password_hash, email) VALUES (?, ?, ?)";
        executeUpdate(statement, user.username(), hashedPassword, user.email());
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password_hash, email FROM user WHERE username = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String passwordHash = rs.getString("password_hash");
                        String email = rs.getString("email");
                        return new UserData(username, passwordHash, email);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to retrieve user: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<UserData> getAllUsers() throws DataAccessException {
        List<UserData> users = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password_hash, email FROM user";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String username = rs.getString("username");
                        String passwordHash = rs.getString("password_hash");
                        String email = rs.getString("email");
                        users.add(new UserData(username, passwordHash, email));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to retrieve users: " + e.getMessage());
        }
        return users;
    }

    private final String[] createStatements = {
            """
        CREATE TABLE IF NOT EXISTS user (
        username VARCHAR(256) NOT NULL UNIQUE,
        password_hash VARCHAR(256) NOT NULL,
        email VARCHAR(256) DEFAULT NULL
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
    """
    };

    private void configureDatabase() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to configure database: " + ex.getMessage());
        }
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param == null) ps.setNull(i + 1, Types.NULL);
                }
                ps.executeUpdate();
                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Database update failed: " + e.getMessage());
        }
    }
}
