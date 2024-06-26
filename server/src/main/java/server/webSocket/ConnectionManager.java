package server.webSocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.ServerMessage;

import java.io.IOException;
import java.lang.invoke.ConstantBootstraps;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import static server.webSocket.WebSocketHandler.gameGroups;
import static server.webSocket.WebSocketHandler.observerAuths;

public class ConnectionManager {
    public static ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
    public static ArrayList<Connection> removeList = new ArrayList<>();

    public void add(String authToken, Session session) {
        var connection = new Connection(authToken, session);
        connections.put(authToken, connection);
    }
    public Connection getConnection(String authToken, Session session) {
        if (connections.containsKey(authToken)) {
            if (connections.get(authToken).session.equals(session)) {
                return connections.get(authToken);
            }
        }
        return null;
    }

    public void remove(String authToken) {
        connections.remove(authToken);
    }

    public void broadcast(String excludeAuthToken, ServerMessage serverMessage) throws IOException {
        if (serverMessage.getServerMessageType().equals(ServerMessage.ServerMessageType.LOAD_GAME)) {
            for (var c : connections.values()) {
                if (c.session.isOpen()) {
                    if (c.authToken.equals(excludeAuthToken)) {
                        c.send(new Gson().toJson(serverMessage));
                    }
                } else {
                    removeList.add(c);
                }
            }
        }
        else if (serverMessage.getServerMessageType().equals(ServerMessage.ServerMessageType.NOTIFICATION)) {
            for (var c : connections.values()) {
                if (c.session.isOpen()) {
                    if (!c.authToken.equals(excludeAuthToken)) {
                        c.send(new Gson().toJson(serverMessage));
                    }
                } else {
                    removeList.add(c);
                }
            }
        }
            // Clean up any connections that were left open.
            for (var c : removeList) {
                connections.remove(c.authToken);
            }
        }

    public void broadcastMakeMoveLoadGame(String authToken, LoadGameMessage loadGameMessage) throws IOException {
        String playerColor = loadGameMessage.getPlayerColor();
        ChessGame game = loadGameMessage.getGame();
        LoadGameMessage loadGameToMe;
        LoadGameMessage loadGameToOpponent;
        LoadGameMessage loadGameToObservers;
        if (playerColor.equalsIgnoreCase("White")) {
            loadGameToMe = new LoadGameMessage(game, "white");
            loadGameToOpponent = new LoadGameMessage(game, "black");
        }
        else {
            loadGameToMe = new LoadGameMessage(game, "black");
            loadGameToOpponent = new LoadGameMessage(game, "white");
        }
        loadGameToObservers = new LoadGameMessage(game, "white");
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (c.authToken.equals(authToken)) {
                    c.send(new Gson().toJson(loadGameToMe));
                }
                else if (observerAuths.contains(c.authToken)) {
                    c.send(new Gson().toJson(loadGameToObservers));
                }
                else {
                    c.send(new Gson().toJson(loadGameToOpponent));
                }
            } else {
                removeList.add(c);
            }
        }
        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.authToken);
        }
    }

        public void sendError(String authToken, ServerMessage errorMessage) {
            try {
                for (var c : connections.values()) {
                    if (c.session.isOpen()) {
                        if (c.authToken.equals(authToken)) {
                            c.send(new Gson().toJson(errorMessage));
                            break;
                        }
                    } else {
                        removeList.add(c);
                    }
                }
                // Clean up any connections that were left open.
                for (var c : removeList) {
                    connections.remove(c.authToken);
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
        }
    }

        public void sendResignMessage(ServerMessage errorMessage) {
            try {
                for (var c : connections.values()) {
                    if (c.session.isOpen()) {
                        c.send(new Gson().toJson(errorMessage));
                    } else {
                        removeList.add(c);
                    }
                }
                // Clean up any connections that were left open.
                for (var c : removeList) {
                    connections.remove(c.authToken);
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
