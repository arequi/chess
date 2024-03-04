package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.*;

import static java.lang.Math.random;

public class MemoryGameDAO implements GameDAO{
    static public ArrayList<GameData> gameDataArrayList = new ArrayList<>();
    @Override
    public void clear() throws DataAccessException {
        gameDataArrayList.clear();
    }

    @Override
    public void createGame(String gameName) throws DataAccessException {
        Random rand = new Random();
        int gameID = rand.nextInt(100);
        ChessGame newGame = new ChessGame();
        GameData data = new GameData(gameID, null, null, gameName, newGame);
        gameDataArrayList.add(data);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        Iterator<GameData> itr = gameDataArrayList.iterator();
        GameData data;
        while(itr.hasNext()) {
            data = itr.next();
            if (data.gameID() == gameID) {
                return data;
            }
        }
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return gameDataArrayList;
    }

    @Override
    public void updateGame(int gameID, GameData updatedGame) throws DataAccessException {
        Iterator<GameData> itr = gameDataArrayList.iterator();
        GameData data;
        while (itr.hasNext()) {
            data = itr.next();
            if (data.gameID() == gameID) {
                gameDataArrayList.remove(data);
                gameDataArrayList.add(updatedGame);
            }
        }
    }
}
