package server.webSocket;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;



@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            // LOAD_GAME to root client and NOTIFICATION to everyone else
            case JOIN_PLAYER, JOIN_OBSERVER -> loadGame(command.getAuthString(), session);
            // server verifies move validity
            // game boards updated & game updated in database
            // LOAD_GAME to everyone
            // NOTIFICATION to everyone except root client
            case MAKE_MOVE -> notification(command.getAuthString());
            // update game to remove root client & update game in database
            // NOTIFICATION to everyone else that root client left
            case LEAVE -> notification(command.getAuthString());
            // game over in server & update game in database
            // NOTIFICATION to everyone
            case RESIGN -> notification(command.getAuthString());
        }
    }

    public void loadGame(String authToken, Session session) throws IOException {
        // load game message + notification message
        // exactly the same for both join player and join observer
        // TODO: maybe call notification method in here?
        // TODO: figure out if we need to make a connection?
        connections.add(authToken, session);
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        // TODO: figure out if we need to broadcast; if not, what do we do?
        connections.broadcast(authToken, serverMessage);
    }

    private void error(String visitorName) throws IOException {
        connections.remove(visitorName);
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
        connections.broadcast(visitorName, serverMessage);
    }

    public void notification(String userName)throws IOException {
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        connections.broadcast("", serverMessage);
    }
}