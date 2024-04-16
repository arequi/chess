package server.webSocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.ServerMessage;

import java.io.IOException;
import java.lang.invoke.ConstantBootstraps;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public static ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

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
        var removeList = new ArrayList<Connection>();
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

    public void broadcastMakeMoveLoadGame(ServerMessage serverMessage) throws IOException {
        var removeList = new ArrayList<Connection>();
        if (serverMessage.getServerMessageType().equals(ServerMessage.ServerMessageType.LOAD_GAME)) {
            for (var c : connections.values()) {
                if (c.session.isOpen()) {
                    c.send(new Gson().toJson(serverMessage));
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

        public void sendError(String authToken, ServerMessage errorMessage) {
            try {
                var removeList = new ArrayList<Connection>();
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
            } catch (IOException e) {
                System.out.println(e.getMessage());
        }
    }
    }
