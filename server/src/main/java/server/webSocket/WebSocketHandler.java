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
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

import java.io.IOException;
import java.lang.ref.PhantomReference;
import java.util.Collection;


@WebSocket
public class WebSocketHandler {


    private final ConnectionManager connections = new ConnectionManager();
    public static Collection<ChessGame> createdGames;
    private final GameDAO gameDAO = new SQLGameDAO();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        Connection conn = connections.getConnection(command.getAuthString(), session);
        if (conn == null) {
            if (new SQLAuthDAO().getAuth(command.getAuthString()) != null) {
                connections.add(command.getAuthString(), session);
            }
            else {
                ServerMessage serverMessage = new Error("error: invalid authToken");
                ConnectionManager.sendError(command.getAuthString(), serverMessage);
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
            ServerMessage serverMessage = new Error("error: user not found.");
            ConnectionManager.sendError(command.getAuthString(), serverMessage);
        }
     }

    public void joinPlayer(String message, Connection conn) throws Exception {
        JoinPlayer joinPlayer = new Gson().fromJson(message, JoinPlayer.class);
        String color = joinPlayer.getPlayerColor().name();
        Integer gameID = joinPlayer.getGameID();
        if (gameDAO.getGame(gameID) == null) {
            ServerMessage serverMessage = new Error("error: bad gameID");
            ConnectionManager.sendError(conn.authToken, serverMessage);
        }
        else {
            GameData gameData = gameDAO.getGame(gameID);
            ChessGame game = gameData.game();
//            if (game.getTeamTurn() != joinPlayer.getPlayerColor()) {
//                ServerMessage serverMessage = new Error("error: team is already taken");
//                ConnectionManager.sendError(conn.authToken, serverMessage);
//            }
            String authToken = conn.authToken;
            var serverMessage = new LoadGame(game);
            connections.broadcast(authToken, serverMessage);
            AuthData authData = new SQLAuthDAO().getAuth(authToken);
            String username = authData.username();
            var serverNotification = new Notification(username + " has joined the game as " + color);
            connections.broadcast(authToken, serverNotification);
        }
    }

    private void joinObserver(String message, Connection conn) throws IOException, DataAccessException {
        JoinObserver joinObserver = new Gson().fromJson(message, JoinObserver.class);
        Integer gameID = joinObserver.getGameID();
        if (gameDAO.getGame(gameID) == null) {
            ServerMessage serverMessage = new Error("error: bad gameID");
            ConnectionManager.sendError(conn.authToken, serverMessage);
        }
        GameData gameData = gameDAO.getGame(gameID);
        ChessGame game = gameData.game();
        ChessBoard board;
        if (game.getBoard() == null) {
            board = new ChessBoard();
            board.resetBoard();
            game.setBoard(board);
            game.setTeamTurn(ChessGame.TeamColor.WHITE);
        }
        var serverMessage = new LoadGame(game);
        String authToken = conn.authToken;
        connections.broadcast(authToken, serverMessage);
        AuthData authData = new SQLAuthDAO().getAuth(authToken);
        String username = authData.username();
        var serverNotification = new Notification(username + " is observing the game");
        connections.broadcast(authToken, serverNotification);
    }

    public void makeMove(String message, Connection conn) throws IOException, DataAccessException, InvalidMoveException {
        MakeMove makeMove = new Gson().fromJson(message, MakeMove.class);
        Integer gameID = makeMove.getGameID();
        ChessGame game = gameDAO.getGame(gameID).game();
        ChessMove move = makeMove.getMove();
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        if (game.getTeamTurn().equals(ChessGame.TeamColor.WHITE)) {
            // mirror move
            startPosition = new ChessPosition(8-startPosition.getRow()+1, 8-startPosition.getColumn()+1);
        }
        String startString = getPositionString(startPosition.getRow(), startPosition.getColumn());
        String endString = getPositionString(endPosition.getRow(), endPosition.getColumn());
        String authToken = conn.authToken;
        AuthData authData = new SQLAuthDAO().getAuth(authToken);
        String username = authData.username();
        game.makeMove(move);
        var serverMessage = new LoadGame(game);
        connections.broadcastMakeMoveLoadGame(serverMessage);
        var serverNotification = new Notification(username + "moved from " + startString + " to " + endString);
        connections.broadcast(authToken, serverNotification);
    }

    public void leaveGame(String message, Connection conn) throws IOException, DataAccessException {
        Leave leave = new Gson().fromJson(message, Leave.class);
        Integer gameID = leave.getGameID();
        String authToken = conn.authToken;
        connections.remove(authToken);
        AuthData authData = new SQLAuthDAO().getAuth(authToken);
        String username = authData.username();
        var serverMessage = new Notification(username + " has left the game");
        connections.broadcast(authToken, serverMessage);
    }

    public void resignGame(String message, Connection conn) throws IOException, DataAccessException {
        Resign resign = new Gson().fromJson(message, Resign.class);
        Integer gameID = resign.getGameID();
        ChessGame game = gameDAO.getGame(gameID).game();
        game.setTeamTurn(null);
        String authToken = conn.authToken;
        AuthData authData = new SQLAuthDAO().getAuth(authToken);
        String username = authData.username();
        var serverMessage = new Notification(username + " has resigned");
        connections.broadcast(authToken, serverMessage);
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