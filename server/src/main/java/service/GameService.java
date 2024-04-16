package service;

import chess.ChessGame;
import dataAccess.*;
import model.GameData;
import model.UserData;
import model.response.CreateGameResponse;
import model.response.JoinGameResponse;
import model.response.ListGamesResponse;
import server.webSocket.WebSocketHandler;

import java.util.ArrayList;
import java.util.TreeMap;

import static server.webSocket.ConnectionManager.connections;
import static server.webSocket.WebSocketHandler.gameIDs;


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
        CreateGameResponse response = new CreateGameResponse(newGame.gameID(), null);
        int gameID = response.gameID();
        int gameNum;
        if (gameIDs == null) {
            gameIDs = new TreeMap<>();
            gameNum = 1;
        }
        else {
            gameNum = gameIDs.size()+1;
        }
        response = new CreateGameResponse(gameNum, null);
        gameIDs.put(gameNum, gameID);
        return response;
    }

    public JoinGameResponse joinGame(Integer gameNum, String playerColor, String auth) throws DataAccessException {
        if (new SQLAuthDAO().getAuth(auth) == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        if (gameNum == null) {
            throw new DataAccessException("Error: bad request");
        }
        if (gameIDs.isEmpty()) {
            throw new DataAccessException("error: bad request");
        }
        Integer gameID = gameIDs.get(gameNum);
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
