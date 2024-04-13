package server;

import dataAccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import server.webSocket.Connection;
import server.webSocket.WebSocketHandler;
import spark.*;
import handler.*;

import java.util.ArrayList;
public class Server {

    static final public ArrayList<AuthData> authDataArrayList = new ArrayList<>();
    static final public ArrayList<GameData> gameDataArrayList = new ArrayList<>();
    static final public ArrayList<UserData> userDataArrayList = new ArrayList<>();
    private final WebSocketHandler webSocketHandler;

    public Server () {
        webSocketHandler = new WebSocketHandler();
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");
        Spark.webSocket("/connect", webSocketHandler);
        createRoutes();

        Spark.init();
        Spark.awaitInitialization();
        return Spark.port();
    }
    void createRoutes() {
        // Register your endpoints and handle exceptions here.
        // ClearHandler
        Spark.delete("/db", (req, res) -> (new ClearHandler()).handle(req, res));
        // RegisterHandler
        Spark.post("/user", (req, res) -> (new RegisterHandler()).handle(req, res));
        // LoginHandler
        Spark.post("/session", (req, res) -> (new LoginHandler()).handle(req, res));
        // LogoutHandler
        Spark.delete("/session", (req, res) -> (new LogoutHandler()).handle(req, res));
        // ListGamesHandler
        Spark.get("/game", (req, res) -> (new ListGamesHandler()).handle(req, res));
        // CreateGameHandler
        Spark.post("/game", (req, res) -> (new CreateGameHandler()).handle(req, res));
        // JoinGameHandler
        Spark.put("/game", (req, res) -> (new JoinGameHandler()).handle(req, res));
    }


    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
