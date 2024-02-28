package dataAccess;

import model.GameData;

import java.util.Collection;
import java.util.HashSet;

public class MemoryGameDAO implements GameDAO{
    final private Collection<GameData> gameDataCollection = new HashSet<>();
    @Override
    public void clear() throws DataAccessException {
        gameDataCollection.clear();
    }

    @Override
    public void createGame(GameData gameData) throws DataAccessException {

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return null;
    }

    @Override
    public void updateGame(int gameID) throws DataAccessException {

    }
}
