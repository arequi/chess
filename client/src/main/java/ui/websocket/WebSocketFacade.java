package ui.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import ui.ResponseException;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

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
                        if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
                            LoadGame loadGame = new Gson().fromJson(message, LoadGame.class);
                            notificationHandler.notify(loadGame);
                        }
                        else if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
                            Notification notification = new Gson().fromJson(message, Notification.class);
                            notificationHandler.notify(notification);
                        }
                        else {
                            Error errorMessage = new Gson().fromJson(message, Error.class);
                            notificationHandler.notify(errorMessage);
                        }
                    } catch (Exception ex){
                        notificationHandler.notify(new Error(ex.getMessage()));
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(ex.getMessage());
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
//            GameData gameData =
//            if (playerColor.name().equalsIgnoreCase("white") && gameData.whiteUsername() != null) {
//                connections.sendError("error: white team already taken!", conn.session);
//            }
//            else if (playerColor.name().equalsIgnoreCase("black") && gameData.blackUsername() != null) {
//                connections.sendError("error: black team already taken!", conn.session);
//            }
            var command = new JoinPlayer(authToken, gameID, playerColor);
            send(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(ex.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void joinObserver(String authToken, Integer gameID) throws ResponseException {
        try {
            var command = new JoinObserver(authToken, gameID);
            send(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(ex.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void makeMove(String authToken, Integer gameID, ChessMove move) throws ResponseException {
        try {
            var command = new MakeMove(authToken, gameID, move);
            send(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(ex.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void leave(String authToken, Integer gameID) throws ResponseException {
        try {
            var command = new Leave(authToken, gameID);
            send(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(ex.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void resign(String authToken, Integer gameID) throws ResponseException {
        try {
            var command = new Resign(authToken, gameID);
            send(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(ex.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}