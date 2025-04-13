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


public class MySqlGameDataAccess implements GameDataAccess {

    public MySqlGameDataAccess()  {
        try {
            configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public int insertGame(GameData game) throws DataAccessException {

        var statement = "INSERT INTO game (player_white, player_black, game_name, game_state) VALUES (?, ?, ?, ?)";
        var gameState = new Gson().toJson(game); // Serialize game state
        return executeUpdate(statement, game.whiteUsername(), game.blackUsername(), game.gameName(), gameState);
    }

    @Override
    public GameData getGameById(int gameId) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, player_white, player_black, game_name, game_state FROM game WHERE id=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameId);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Unable to retrieve game: " + e.getMessage());
        }
        return null;
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String whiteUsername = rs.getString("player_white");
        String blackUsername = rs.getString("player_black");
        String gameName = rs.getString("game_name");
        String gameStateJson = rs.getString("game_state");
        GameData game = new Gson().fromJson(gameStateJson, GameData.class); // Deserialize game state
        return new GameData(id, whiteUsername, blackUsername, gameName);
    }

    @Override
    public void updateGame(GameData updatedGame) throws DataAccessException {
        var statement = "UPDATE game SET player_white=?, player_black=?, game_name=?, game_state=? WHERE id=?";
        var gameState = new Gson().toJson(updatedGame);
        executeUpdate(statement, updatedGame.whiteUsername(), updatedGame.blackUsername(), updatedGame.gameName(), gameState, updatedGame.gameID());
    }

    @Override
    public List<GameData> getAllGames() throws DataAccessException {
        var result = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, player_white, player_black, game_name, game_state FROM game";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Unable to retrieve games: " + e.getMessage());
        }
        return result;
    }

    @Override
    public void clearAllGames() throws DataAccessException {
        var statement = "TRUNCATE game";
        executeUpdate(statement);
    }

    private final String[] createStatements = {
            """
    CREATE TABLE IF NOT EXISTS game (
      id INT AUTO_INCREMENT PRIMARY KEY,
      player_white VARCHAR(256) DEFAULT NULL,
      player_black VARCHAR(256) DEFAULT NULL,
      game_name VARCHAR(256) NOT NULL,
      game_state TEXT DEFAULT NULL
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