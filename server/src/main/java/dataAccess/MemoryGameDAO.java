package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.*;

import static java.lang.Math.random;
import static server.Server.gameDataArrayList;

public class MemoryGameDAO implements GameDAO{

    GameData data;

    @Override
    public void clear() throws DataAccessException {
        gameDataArrayList.clear();
    }

    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        Random rand = new Random();
        int gameID = rand.nextInt(10000) + 1;
        ChessGame newGame = new ChessGame();
        GameData data = new GameData(gameID, null, null, gameName, newGame);
        gameDataArrayList.add(data);
        return data;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        Iterator<GameData> itr = gameDataArrayList.iterator();
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
    public void updateGame(int gameID, GameData updatedGame) {
        Iterator<GameData> itr = gameDataArrayList.iterator();
        while (itr.hasNext()) {
            data = itr.next();
            if (data.gameID() == gameID) {
                gameDataArrayList.remove(data);
                gameDataArrayList.add(updatedGame);
                break;
            }
        }
    }
}
