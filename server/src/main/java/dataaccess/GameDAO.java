package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class GameDAO implements GameDataAccess {
    // In-memory database simulation
    private static List<GameData> gameList;
    private static AtomicInteger gameIdGenerator;

    public GameDAO() {
        gameList = new ArrayList<>();
        gameIdGenerator = new AtomicInteger(1); // Start game IDs at 1
    }

    // Method to add a game and generate a unique game ID


    public int insertGame(GameData game) throws DataAccessException {
        try {
            int gameId = gameIdGenerator.getAndIncrement();
            GameData newGame = new GameData(gameId, game.whiteUsername(), game.blackUsername(), game.gameName());
            gameList.add(newGame);
            return gameId;
        } catch (Exception e) {
            throw new DataAccessException("Failed to insert game: " + e.getMessage());
        }
    }


    public GameData getGameById(int gameId) throws DataAccessException {
        try {
            return gameList.stream()
                    .filter(game -> game.gameID() == gameId)
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            throw new DataAccessException("Failed to retrieve game: " + e.getMessage());
        }
    }

    public void updateGame(GameData updatedGame) throws DataAccessException {
        try {
            for (int i = 0; i < gameList.size(); i++) {
                if (gameList.get(i).gameID() == updatedGame.gameID()) {
                    gameList.set(i, updatedGame);
                    return;
                }
            }
            throw new DataAccessException("Game not found");
        } catch (Exception e) {
            throw new DataAccessException("Failed to update game: " + e.getMessage());
        }
    }

    // Method to retrieve all games (for reference)
    public List<GameData> getAllGames() throws DataAccessException {
        try {
            return new ArrayList<>(gameList);
        } catch (Exception e) {
            throw new DataAccessException("Failed to retrieve games: " + e.getMessage());
        }
    }

    // Method to clear all games (for reference)
    public void clearAllGames() throws DataAccessException {
        try {
            gameList.clear();
        } catch (Exception e) {
            throw new DataAccessException("Failed to clear all games: " + e.getMessage());
        }
    }

    @Override
    public ChessGame getChessGame(int gameID) throws DataAccessException {
        return null;
    }
}