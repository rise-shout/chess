package dataaccess;

//import exception.ResponseException;
import model.GameData;
import model.UserData;
import model.AuthData;

import java.util.Collection;
import java.util.List;

public interface DataAccess {

    int insertGame(GameData game) throws DataAccessException;
    GameData getGameById(int gameId) throws DataAccessException;
    void updateGame(GameData updatedGame) throws DataAccessException;
    List<GameData> getAllGames() throws DataAccessException;
    void clearAllGames() throws DataAccessException;

}