package server.webSocket;

import chess.*;
import com.google.gson.Gson;
import dataAccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.userCommands.*;

import java.io.IOException;
import java.util.SortedMap;


@WebSocket
public class WebSocketHandler {

    public final ConnectionManager connections = new ConnectionManager();
    private final GameDAO gameDAO = new SQLGameDAO();
    private final AuthDAO authDAO = new SQLAuthDAO();
    public static SortedMap<Integer, Integer> gameIDs;

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        Connection conn = connections.getConnection(command.getAuthString(), session);
        if (conn == null) {
            if (authDAO.getAuth(command.getAuthString()) != null) {
                connections.add(command.getAuthString(), session);
            }
            else {
                connections.add(command.getAuthString(), session);
                ErrorMessage errorMessage = new ErrorMessage("error: invalid authToken");
                connections.sendError(command.getAuthString(), errorMessage);
                return;
            }
        }
        conn = connections.getConnection(command.getAuthString(), session);
        if (conn != null) {
            switch (command.getCommandType()) {
                case JOIN_PLAYER -> joinPlayer(message, conn);
                case JOIN_OBSERVER -> joinObserver(message, conn);
                case MAKE_MOVE -> makeMove(message, conn);
                case LEAVE -> leaveGame(message, conn);
                case RESIGN -> resignGame(message, conn);
            }
        }
        else {
            ErrorMessage errorMessage = new ErrorMessage("error: user not found.");
            connections.sendError(command.getAuthString(), errorMessage);
        }
     }

    public void joinPlayer(String message, Connection conn) {
        try {
            JoinPlayer joinPlayer = new Gson().fromJson(message, JoinPlayer.class);
            String color = joinPlayer.getPlayerColor().name();
            Integer gameNum = joinPlayer.getGameID();
            String authToken = conn.authToken;
            if (gameIDs.get(gameNum) == null) {
                ErrorMessage errorMessage = new ErrorMessage("error: bad gameID");
                connections.sendError(authToken, errorMessage);
                return;
            }
            Integer gameID = gameIDs.get(gameNum);
            GameData gameData = gameDAO.getGame(gameID);
            ChessGame game = gameData.game();
            game.setGameOverResign(false);
            gameDAO.updateGame(gameID, gameData);
            if (authDAO.getAuth(authToken) == null) {
                ErrorMessage errorMessage = new ErrorMessage("error: invalid authToken");
                connections.sendError(authToken, errorMessage);
                return;
            }
            AuthData authData = authDAO.getAuth(authToken);
            String username = authData.username();
            if (gameData.blackUsername() == null && gameData.whiteUsername() == null) {
                ErrorMessage errorMessage = new ErrorMessage("error: empty game!");
                connections.sendError(authToken, errorMessage);
                return;
            }
            if (color.equalsIgnoreCase("white") && !gameData.whiteUsername().equals(username)) {
                ErrorMessage errorMessage = new ErrorMessage("error: team already taken!");
                connections.sendError(authToken, errorMessage);
                return;
            }
            if (color.equalsIgnoreCase("black") && !gameData.blackUsername().equals(username)) {
                ErrorMessage errorMessage = new ErrorMessage("error: team already taken!");
                connections.sendError(authToken, errorMessage);
                return;
            }
            var serverMessage = new LoadGameMessage(game, color);
            connections.broadcast(authToken, serverMessage);
            var serverNotification = new NotificationMessage(username + " has joined the game as " + color);
            connections.broadcast(authToken, serverNotification);
        }
        catch (DataAccessException e) {
            ErrorMessage errorMessage = new ErrorMessage("error accessing database");
            connections.sendError(conn.authToken, errorMessage);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    private void joinObserver(String message, Connection conn) {
        try {
            JoinObserver joinObserver = new Gson().fromJson(message, JoinObserver.class);
            Integer gameNum = joinObserver.getGameID();
            if (gameIDs.get(gameNum) == null) {
                ErrorMessage errorMessage = new ErrorMessage("error: bad gameID");
                connections.sendError(conn.authToken, errorMessage);
                return;
            }
            Integer gameID = gameIDs.get(gameNum);
            GameData gameData = gameDAO.getGame(gameID);
            ChessGame game = gameData.game();
            var serverMessage = new LoadGameMessage(game, null);
            String authToken = conn.authToken;
            connections.broadcast(authToken, serverMessage);
            if (authDAO.getAuth(authToken) == null) {
                ErrorMessage errorMessage = new ErrorMessage("error: invalid authToken");
                connections.sendError(authToken, errorMessage);
                return;
            }
            AuthData authData = authDAO.getAuth(authToken);
            String username = authData.username();
            var serverNotification = new NotificationMessage(username + " is observing the game");
            connections.broadcast(authToken, serverNotification);
        }
        catch (DataAccessException e) {
            ErrorMessage errorMessage = new ErrorMessage("error accessing database");
            connections.sendError(conn.authToken, errorMessage);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public void makeMove(String message, Connection conn) throws IOException, DataAccessException, InvalidMoveException {
        try {
            MakeMove makeMove = new Gson().fromJson(message, MakeMove.class);
            Integer gameNum = makeMove.getGameID();
            Integer gameID = gameIDs.get(gameNum);
            GameData gameData = gameDAO.getGame(gameID);
            ChessGame game = gameData.game();
            ChessMove move = makeMove.getMove();
            String authToken = conn.authToken;
            AuthData authData = authDAO.getAuth(authToken);
            String username = authData.username();
            if (game.isInCheckmate(ChessGame.TeamColor.WHITE) || game.isInCheckmate(ChessGame.TeamColor.BLACK)) {
                ErrorMessage errorMessage = new ErrorMessage("error: game is over by checkmate");
                connections.sendError(authToken, errorMessage);
                return;
            }
            if (game.isInStalemate(ChessGame.TeamColor.WHITE) || game.isInStalemate(ChessGame.TeamColor.BLACK)) {
                ErrorMessage errorMessage = new ErrorMessage("error: game is over by stalemate");
                connections.sendError(authToken, errorMessage);
                return;
            }
            if (game.getGameOverResign()) {
                ErrorMessage errorMessage = new ErrorMessage("error: game is over by stalemate");
                connections.sendError(authToken, errorMessage);
                return;
            }
            if (gameData.whiteUsername().equals(username) && !game.getTeamTurn().equals(ChessGame.TeamColor.WHITE)) {
                ErrorMessage errorMessage = new ErrorMessage("error: not your turn!");
                connections.sendError(authToken, errorMessage);
                return;
            }
            if (gameData.blackUsername().equals(username) && !game.getTeamTurn().equals(ChessGame.TeamColor.BLACK)) {
                ErrorMessage errorMessage = new ErrorMessage("error: not your turn!");
                connections.sendError(authToken, errorMessage);
                return;
            }
            UserData userData = new SQLUserDAO().getUser(username);
            if (gameData.observers().contains(userData)) {
                ErrorMessage errorMessage = new ErrorMessage("error: observers can't make moves!");
                connections.sendError(authToken, errorMessage);
                return;
            }
            ChessPosition startPosition = move.getStartPosition();
            ChessPosition endPosition = move.getEndPosition();
            if (game.getTeamTurn().equals(ChessGame.TeamColor.WHITE)) {
                startPosition = new ChessPosition(8 - startPosition.getRow() + 1, 8 - startPosition.getColumn() + 1);
            }
            String startString = getPositionString(startPosition.getRow(), startPosition.getColumn());
            String endString = getPositionString(endPosition.getRow(), endPosition.getColumn());
            game.makeMove(move);
            gameDAO.updateGame(gameID, gameData);
            LoadGameMessage loadGameMessage;
            if (gameData.blackUsername().equalsIgnoreCase(username)) {
                loadGameMessage = new LoadGameMessage(game, "black");
            } else if (gameData.whiteUsername().equalsIgnoreCase(username)) {
                loadGameMessage = new LoadGameMessage(game, "white");
            } else {
                loadGameMessage = new LoadGameMessage(game, null);
            }
            connections.broadcastMakeMoveLoadGame(loadGameMessage);
            var serverNotification = new NotificationMessage(username + "moved from " + startString + " to " + endString);
            connections.broadcast(authToken, serverNotification);
        } catch (InvalidMoveException e) {
            ErrorMessage errorMessage = new ErrorMessage("error: invalid move");
            connections.sendError(conn.authToken, errorMessage);
        }
    }

    public void leaveGame(String message, Connection conn) throws IOException, DataAccessException {
        Leave leave = new Gson().fromJson(message, Leave.class);
        Integer gameID = leave.getGameID();
        String authToken = conn.authToken;
        connections.remove(authToken);
        AuthData authData = authDAO.getAuth(authToken);
        String username = authData.username();
        var serverMessage = new NotificationMessage(username + " has left the game");
        connections.broadcast(authToken, serverMessage);
    }

    public void resignGame(String message, Connection conn) throws IOException, DataAccessException {
        Resign resign = new Gson().fromJson(message, Resign.class);
        Integer gameNum = resign.getGameID();
        Integer gameID = gameIDs.get(gameNum);
        GameData gameData = new SQLGameDAO().getGame(gameID);
        ChessGame game = gameData.game();
        String authToken = conn.authToken;
        if (game.getGameOverResign()) {
            ErrorMessage errorMessage = new ErrorMessage("error: game is already over");
            connections.sendError(authToken, errorMessage);
            return;
        }
        game.setGameOverResign(true);
        gameDAO.updateGame(gameID, gameData);
        AuthData authData = authDAO.getAuth(authToken);
        String username = authData.username();
        UserData userData = new SQLUserDAO().getUser(username);
        if (gameData.observers().contains(userData)) {
            ErrorMessage errorMessage = new ErrorMessage("error: observers can't resign");
            connections.sendError(authToken, errorMessage);
            return;
        }
        var serverMessage = new NotificationMessage(username + " has resigned. Game is over");
        connections.sendResignMessage(serverMessage);
    }

    private String getPositionString (int row, int col) {
        String letter = letter(col);
        return letter + row;
    }

    private String letter (int col) {
        if (col == 8) {
            return "a";
        }
        if (col == 7) {
            return "b";
        }
        if (col == 6) {
            return "c";
        }
        if (col == 5) {
            return "d";
        }
        if (col == 4) {
            return "e";
        }
        if (col == 3) {
            return "f";
        }
        if (col == 2) {
            return "g";
        }
        if (col == 1) {
            return "h";
        }
        return null;
    }
}