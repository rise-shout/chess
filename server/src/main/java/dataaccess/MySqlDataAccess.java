package dataaccess;

import com.google.gson.Gson;
//import exception.ResponseException;
import model.GameData;
import model.UserData;
import model.AuthData;

import java.util.ArrayList;
import java.util.Collection;
import java.sql.*;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;


public class MySqlDataAccess implements GameDataAccess {


    @Override
    public int insertGame(GameData game) throws DataAccessException {
        return 0;
    }

    @Override
    public GameData getGameById(int gameId) throws DataAccessException {
        return null;
    }

    @Override
    public void updateGame(GameData updatedGame) throws DataAccessException {

    }

    @Override
    public List<GameData> getAllGames() throws DataAccessException {
        return null;
    }

    @Override
    public void clearAllGames() throws DataAccessException {

    }
}