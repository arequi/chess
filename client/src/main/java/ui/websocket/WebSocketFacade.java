package ui.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import ui.Repl;
import ui.ResponseException;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.JoinObserverCommand;
import webSocketMessages.userCommands.JoinPlayerCommand;
import webSocketMessages.userCommands.UserGameCommand;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");
            this.notificationHandler = notificationHandler;
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    try {
                        ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                        notificationHandler.notify(serverMessage);
                    } catch (Exception ex){
                        notificationHandler.notify(new ErrorMessage(ServerMessage.ServerMessageType.ERROR, ex.getMessage()));
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
    }

    public void joinPlayer(String authToken, Integer gameID, ChessGame.TeamColor playerColor) throws ResponseException {
        try {
            var command = new JoinPlayerCommand(authToken, gameID, playerColor);
            send(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void joinObserver(String authToken, Integer gameID) throws ResponseException {
        try {
            var command = new JoinObserverCommand("hi", gameID);
            send(new Gson().toJson(command));
            this.session.close();
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void makeMove(String authToken) throws ResponseException {
        try {
            var command = new UserGameCommand(authToken);
            send(new Gson().toJson(command));
            this.session.close();
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void leave(String authToken) throws ResponseException {
        try {
            var command = new UserGameCommand(authToken);
            send(new Gson().toJson(command));
            this.session.close();
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void resign(String authToken) throws ResponseException {
        try {
            var command = new UserGameCommand(authToken);
            send(new Gson().toJson(command));
            this.session.close();
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}