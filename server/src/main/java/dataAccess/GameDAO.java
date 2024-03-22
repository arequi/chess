package dataAccess;

import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

public interface GameDAO {
    void clear() throws DataAccessException;
    GameData createGame(String gameName) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    ArrayList<GameData> listGames() throws DataAccessException;
    void updateGame(Integer gameID, GameData updatedGame) throws DataAccessException;


}
