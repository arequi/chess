package service;

import chess.ChessGame;
import dataAccess.*;
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
        if (new SQLAuthDAO().getAuth(auth) == null) {
            new ListGamesResponse(null, "invalid authToken");
            throw new DataAccessException("Error: unauthorized");
        }
        return new ListGamesResponse(new SQLGameDAO().listGames(), null);
    }

    public CreateGameResponse CreateGame(String gameName, String auth) throws DataAccessException {
        if (new SQLAuthDAO().getAuth(auth) == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        if (gameName == null) {
            throw new DataAccessException("Error: bad request");
        }
        GameData newGame = new SQLGameDAO().createGame(gameName);
        return new CreateGameResponse(newGame.gameID(), null);
    }

    public JoinGameResponse JoinGame(String playerColor, Integer gameID, String auth) throws DataAccessException {
        if (new SQLAuthDAO().getAuth(auth) == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        if (gameID == null) {
            throw new DataAccessException("Error: bad request");
        }
        if (new SQLGameDAO().getGame(gameID) == null) {
            throw new DataAccessException("Error: bad request");
        }
        String username = (new SQLAuthDAO().getAuth(auth)).username();
        GameData gameData = new SQLGameDAO().getGame(gameID);
        ChessGame currentGame = gameData.game();
        if (playerColor == null) {
            GameData updatedObserverGame = new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(),
                    gameData.gameName(), currentGame);
            new SQLGameDAO().updateGame(gameID, updatedObserverGame);
            return new JoinGameResponse(gameID, null);
        }
        if (ChessGame.TeamColor.valueOf(playerColor) == ChessGame.TeamColor.WHITE) {
            if (new SQLGameDAO().getGame(gameID).whiteUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
            GameData updatedWhiteGame = new GameData(gameID, username, gameData.blackUsername(), gameData.gameName(), currentGame);
            new SQLGameDAO().updateGame(gameID, updatedWhiteGame);
            return new JoinGameResponse(gameID, null);
        }
        else if (ChessGame.TeamColor.valueOf(playerColor) == ChessGame.TeamColor.BLACK) {
            if (new SQLGameDAO().getGame(gameID).blackUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
            GameData updatedBlackGame = new GameData(gameID, gameData.whiteUsername(), username, gameData.gameName(), currentGame);
            new SQLGameDAO().updateGame(gameID, updatedBlackGame);
            return new JoinGameResponse(gameID, null);
        }
        else {
            throw new DataAccessException("Error: bad request");
        }
    }


}
