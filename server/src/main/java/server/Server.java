package server;

import com.google.gson.Gson;
import spark.*;
import handler.*;
import service.*;

public class Server {

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
        Spark.delete("/db", (req, res) -> (new ClearHandler()).handle(new Gson()));
//        Spark.post("/user", (req, res) -> (new RegisterHandler().handle(new Gson())));
//        Spark.post("/session", this::login);
//        Spark.delete("/session", this::logout);
//        Spark.get("/game", this::listGames);
//        Spark.post("/game", this::createGame);
//        Spark.put("/game", this::joinGame);
    }


    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
