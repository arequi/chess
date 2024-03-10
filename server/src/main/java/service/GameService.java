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
import java.util.Objects;

import static server.Server.authDataArrayList;
import static server.Server.gameDataArrayList;

public class GameService {

    public ListGamesResponse listGames(String auth) throws DataAccessException {
        if (new MemoryAuthDAO().getAuth(auth) == null) {
            new ListGamesResponse(null, "invalid authToken");
            throw new DataAccessException("Error: unauthorized");
        }
        return new ListGamesResponse(gameDataArrayList, null);
    }

    public CreateGameResponse CreateGame(String gameName, String auth) throws DataAccessException {
        if (new MemoryAuthDAO().getAuth(auth) == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        if (gameName == null) {
            throw new DataAccessException("Error: bad request");
        }
        GameData newGame = new MemoryGameDAO().createGame(gameName);
        return new CreateGameResponse(newGame.gameID(), null);
    }

    public JoinGameResponse JoinGame(String playerColor, Integer gameID, String auth) throws DataAccessException {
        if (new MemoryAuthDAO().getAuth(auth) == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        if (gameID == null) {
            throw new DataAccessException("Error: bad request");
        }
        if (new MemoryGameDAO().getGame(gameID) == null) {
            throw new DataAccessException("Error: bad request");
        }
        String username = (new MemoryAuthDAO().getAuth(auth)).username();
        GameData gameData = new MemoryGameDAO().getGame(gameID);
        ChessGame currentGame = gameData.game();
        if (playerColor == null) {
            GameData updatedObserverGame = new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), currentGame);
            new MemoryGameDAO().updateGame(gameID, updatedObserverGame);
            return new JoinGameResponse(gameID, null);
        }
        if (ChessGame.TeamColor.valueOf(playerColor) == ChessGame.TeamColor.WHITE) {
            if (new MemoryGameDAO().getGame(gameID).whiteUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
            GameData updatedWhiteGame = new GameData(gameID, username, gameData.blackUsername(), gameData.gameName(), currentGame);
            new MemoryGameDAO().updateGame(gameID, updatedWhiteGame);
            return new JoinGameResponse(gameID, null);
        }
        else if (ChessGame.TeamColor.valueOf(playerColor) == ChessGame.TeamColor.BLACK) {
            if (new MemoryGameDAO().getGame(gameID).blackUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
            GameData updatedBlackGame = new GameData(gameID, gameData.whiteUsername(), username, gameData.gameName(), currentGame);
            new MemoryGameDAO().updateGame(gameID, updatedBlackGame);
            return new JoinGameResponse(gameID, null);
        }
        else {
            throw new DataAccessException("Error: bad request");
        }
    }


}
