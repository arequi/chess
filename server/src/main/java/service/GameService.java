package service;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import model.AuthData;
import model.GameData;
import response.CreateGameResponse;
import response.JoinGameResponse;
import response.ListGamesResponse;

import java.util.ArrayList;

public class GameService {

    public ListGamesResponse listGames(String auth) throws DataAccessException {
        if (new MemoryAuthDAO().getAuth(auth) == null) {
            return new ListGamesResponse(null, false, "invalid authToken");
        }
        if (MemoryAuthDAO.authDataArrayList.isEmpty()) {
            return new ListGamesResponse(null, false, "no games to list");
        }
        ArrayList<GameData> gameList = MemoryGameDAO.gameDataArrayList;
        return new ListGamesResponse(gameList, true, null);
    }

    public CreateGameResponse CreateGame(String gameName, String auth) throws DataAccessException {
        if (new MemoryAuthDAO().getAuth(auth) == null) {
            return new CreateGameResponse(0, false, "invalid authToken");
        }
        GameData newGame = new MemoryGameDAO().createGame(gameName);
        return new CreateGameResponse(newGame.gameID(), true, null);
    }

    public JoinGameResponse JoinGame(String playerColor, int gameID, String auth) throws DataAccessException {
        if (new MemoryAuthDAO().getAuth(auth) == null) {
            return new JoinGameResponse(gameID, false, "invalid autToken");
        }
        if (new MemoryGameDAO().getGame(gameID) == null) {
            return new JoinGameResponse(0, false, "game doesn't exist. unable to join.");
        }
        String username = (new MemoryAuthDAO().getAuth(auth)).username();
        GameData gameData = new MemoryGameDAO().getGame(gameID);
        ChessGame chessGame = (new MemoryGameDAO().getGame(gameID)).game();
        if (playerColor.equalsIgnoreCase("white")) {
            GameData updatedWhiteGame = new GameData(gameID, username, null, gameData.gameName(), chessGame);
            new MemoryGameDAO().updateGame(gameID, updatedWhiteGame);
            return new JoinGameResponse(gameID, true, null);
        }
        else if (playerColor.equalsIgnoreCase("black")) {
            GameData updatedBlackGame = new GameData(gameID, null, username, gameData.gameName(), chessGame);
            new MemoryGameDAO().updateGame(gameID, updatedBlackGame);
            return new JoinGameResponse(gameID, true, null);
        }
        GameData updatedObserverGame = new GameData(gameID, null, null, gameData.gameName(), chessGame);
        new MemoryGameDAO().updateGame(gameID, updatedObserverGame);
        return new JoinGameResponse(gameID, true, null);
    }


}
