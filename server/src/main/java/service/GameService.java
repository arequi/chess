package service;

import chess.ChessGame;
import dataAccess.*;
import model.GameData;
import model.UserData;
import model.response.CreateGameResponse;
import model.response.JoinGameResponse;
import model.response.ListGamesResponse;

import java.util.ArrayList;


public class GameService {

    public ListGamesResponse listGames(String auth) throws DataAccessException {
        if (new SQLAuthDAO().getAuth(auth) == null) {
            new ListGamesResponse(null, "invalid authToken");
            throw new DataAccessException("Error: unauthorized");
        }
        return new ListGamesResponse(new SQLGameDAO().listGames(), null);
    }

    public CreateGameResponse createGame(String gameName, String auth) throws DataAccessException {
        if (new SQLAuthDAO().getAuth(auth) == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        if (gameName == null) {
            throw new DataAccessException("Error: bad request");
        }
        GameData newGame = new SQLGameDAO().createGame(gameName);
        return new CreateGameResponse(newGame.gameID(), null);
    }

    public JoinGameResponse joinGame(Integer gameID, String playerColor, String auth) throws DataAccessException {
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
        ArrayList<UserData> observers = gameData.observers();
        if (playerColor == null) {
            UserData observingUser = new SQLUserDAO().getUser(username);
            observers.add(observingUser);
            GameData updatedObserverGame = new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(),
                    gameData.gameName(), currentGame, observers);
            new SQLGameDAO().updateGame(gameID, updatedObserverGame);
            return new JoinGameResponse(gameID, null);
        }
        if (playerColor.equalsIgnoreCase((ChessGame.TeamColor.WHITE).name())) {
            if (new SQLGameDAO().getGame(gameID).whiteUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
            GameData updatedWhiteGame = new GameData(gameID, username, gameData.blackUsername(), gameData.gameName(), currentGame, observers);
            new SQLGameDAO().updateGame(gameID, updatedWhiteGame);
            return new JoinGameResponse(gameID, null);
        }
        else if (playerColor.equalsIgnoreCase(ChessGame.TeamColor.BLACK.name())) {
            if (new SQLGameDAO().getGame(gameID).blackUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
            GameData updatedBlackGame = new GameData(gameID, gameData.whiteUsername(), username, gameData.gameName(), currentGame, observers);
            new SQLGameDAO().updateGame(gameID, updatedBlackGame);
            return new JoinGameResponse(gameID, null);
        }
        else {
            throw new DataAccessException("Error: bad request");
        }
    }


}
