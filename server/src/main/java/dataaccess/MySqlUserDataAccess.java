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
        try {
            configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void clearAllUsers() throws DataAccessException {

    }

    @Override
    public void insertUser(UserData user) throws DataAccessException {

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public List<UserData> getAllUsers() throws DataAccessException {
        return null;
    }

    private final String[] createStatements = {
            """
    CREATE TABLE IF NOT EXISTS user (
        username VARCHAR(256) NOT NULL UNIQUE,
        password_hash VARCHAR(256) NOT NULL,
        email VARCHAR(256) DEFAULT NULL,
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
}
