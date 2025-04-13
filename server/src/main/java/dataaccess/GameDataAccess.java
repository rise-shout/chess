package dataaccess;

//import exception.ResponseException;
import model.GameData;

import java.util.List;

public interface GameDataAccess {

    int insertGame(GameData game) throws DataAccessException;
    GameData getGameById(int gameId) throws DataAccessException;
    void updateGame(GameData updatedGame) throws DataAccessException;
    List<GameData> getAllGames() throws DataAccessException;
    void clearAllGames() throws DataAccessException;

}