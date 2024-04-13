package server.webSocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.SQLAuthDAO;
import dataAccess.SQLUserDAO;
import model.AuthData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;



@WebSocket
public class WebSocketHandler {


    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        Connection conn = connections.getConnection(command.getAuthString(), session);
        if (conn != null) {
            switch (command.getCommandType()) {
                // LOAD_GAME to root client and NOTIFICATION to everyone else
                case JOIN_PLAYER -> joinPlayer(command, conn, session);
                case JOIN_OBSERVER -> joinObserver(command.getAuthString());
                // server verifies move validity
                // game boards updated & game updated in database
                // LOAD_GAME to everyone
                // NOTIFICATION to everyone except root client
                case MAKE_MOVE -> makeMove(command.getAuthString());
                // update game to remove root client & update game in database
                // NOTIFICATION to everyone else that root client left
                case LEAVE -> leaveGame(command.getAuthString());
                // game over in server & update game in database
                // NOTIFICATION to everyone
                case RESIGN -> resignGame(command.getAuthString());
            }
        }
        else {
            if (new SQLAuthDAO().getAuth(command.getAuthString()) != null) {
                connections.add(command.getAuthString(), session);
            }
            else {
                connections.sendError("error: user not found.", session);
            }
        }

    }

    public void joinPlayer(UserGameCommand command, Connection conn, Session session) throws Exception {
        // load game message + notification message
        // exactly the same for both join player and join observer
        ChessGame game = new ChessGame();
        var serverMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        connections.broadcast(conn.authToken, serverMessage);
        var serverNotification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        connections.broadcast(conn.authToken, serverNotification);
    }

    private void joinObserver(String visitorName) throws IOException {
        connections.remove(visitorName);
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
        connections.broadcast(visitorName, serverMessage);
    }

    public void makeMove(String authToken)throws IOException {
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        connections.broadcast("", serverMessage);
    }

    public void leaveGame(String authToken)throws IOException {
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        connections.broadcast(authToken, serverMessage);
    }

    public void resignGame(String userName)throws IOException {
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        connections.broadcast("", serverMessage);
    }

    public void broadcastMessage(String userName)throws IOException {
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        connections.broadcast("", serverMessage);
    }

    public void sendMessage(String userName)throws IOException {
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        connections.broadcast("", serverMessage);
    }
}