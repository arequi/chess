package server;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import spark.*;
import handler.*;
import service.*;

import java.util.ArrayList;
import java.util.logging.Handler;

public class Server {

    static public ArrayList<AuthData> authDataArrayList = new ArrayList<>();
    static public ArrayList<GameData> gameDataArrayList = new ArrayList<>();
    static public ArrayList<UserData> userDataArrayList = new ArrayList<>();

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");
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
//        Spark.get("/game", (req, res) -> (new ListGamesHandler().handle(req, res));
        // CreateGameHandler
//        Spark.post("/game", (req, res) -> (new CreateGameHandler().handle(req, res));
        // JoinGameHandler
//        Spark.put("/game", (req, res) -> (new JoinGameHandler().handle(req, res));
    }


    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
