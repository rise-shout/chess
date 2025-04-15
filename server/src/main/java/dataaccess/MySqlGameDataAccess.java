package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.util.ArrayList;
import java.util.*;
import java.sql.*;

import static dataaccess.MySqlUserDataAccess.updateDoer;


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
        var gameState = new Gson().toJson(new ChessGame()); // Serialize game state
        return executeUpdate(statement, game.whiteUsername(),
                game.blackUsername(), game.gameName(), gameState);
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
        String gameState = rs.getString("game_state");
        return new GameData(id, whiteUsername, blackUsername, gameName);
    }

    private ChessGame getChessGame (ResultSet rs) throws SQLException {
        // Retrieve the game_state column as a string
        String gameState = rs.getString("game_state");

        // Check if gameState is not null
        if (gameState == null || gameState.isEmpty()) {
            throw new SQLException("Game state is null or empty for the current row.");
        }

        // Use Gson to deserialize the JSON string into a ChessGame object
        try {
            return new Gson().fromJson(gameState, ChessGame.class);
        } catch (Exception e) {
            throw new SQLException("Failed to deserialize game state: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateGame(GameData updatedGame) throws DataAccessException {
        if(gameExists(updatedGame.gameName())) {
            doUpdate(updatedGame);
        }
        else {
            throw new DataAccessException("Game not found");
        }
    }

    public void updateGame(GameData updatedGame, GameData oldGame) throws DataAccessException {
        if(gameExists(oldGame.gameName())) {
            doUpdate(updatedGame);
        }
        else {
            throw new DataAccessException("Game not found");
        }
    }

    public void doUpdate(GameData updatedGame) throws DataAccessException {
        var statement = "UPDATE game SET player_white=?, player_black=?, game_name=?, game_state=? WHERE id=?";
        var gameState = new Gson().toJson(updatedGame);
        executeUpdate(statement, updatedGame.whiteUsername(), updatedGame.blackUsername(),
                updatedGame.gameName(), gameState, updatedGame.gameID());
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
        String statement = "TRUNCATE game";
        executeUpdate(statement);
    }

    @Override
    public ChessGame getChessGame(int gameId) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT game_state FROM game WHERE id=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameId);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String gameState = rs.getString("game_state");
                        if (gameState != null && !gameState.isEmpty()) {
                            return new Gson().fromJson(gameState, ChessGame.class);
                        } else {
                            throw new DataAccessException("Game state is null or empty for game ID: " + gameId);
                        }
                    } else {
                        throw new DataAccessException("No game found with ID: " + gameId);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving chess game: " + e.getMessage());
        }
    }

    private final String[] createStatements = {
            """
    CREATE TABLE IF NOT EXISTS game (
      id INT AUTO_INCREMENT PRIMARY KEY,
      player_white VARCHAR(256) DEFAULT NULL,
      player_black VARCHAR(256) DEFAULT NULL,
      game_name VARCHAR(256) NOT NULL UNIQUE,
      game_state TEXT DEFAULT NULL
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
    """
    };

    private void configureDatabase() throws DataAccessException {
        MySqlUserDataAccess.databaseMaker(createStatements);
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {

        return updateDoer(statement, params);
    }

    public boolean gameExists(String gameName) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT COUNT(*) FROM game WHERE game_name=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, gameName);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        // If COUNT(*) > 0, the game already exists
                        return rs.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to check if game exists: " + e.getMessage());
        }
        return false; // Game doesn't exist
    }


}