package handler;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import model.request.JoinGameRequest;
import model.response.JoinGameResponse;
import server.webSocket.WebSocketHandler;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

import java.io.IOException;

public class JoinGameHandler implements Route {
//    private final WebSocketHandler webSocketHandler;

//    public JoinGameHandler(WebSocketHandler webSocketHandler) {
//        this.webSocketHandler = webSocketHandler;
//    }
    @Override
    public Object handle(Request req, Response res) {
        try {
            Gson gson = new Gson();
            String auth = req.headers("authorization");
            GameService service = new GameService();
            JoinGameRequest joinGameRequest = gson.fromJson(req.body(), JoinGameRequest.class);
            JoinGameResponse joinGameResponse = service.JoinGame(joinGameRequest.gameID(), joinGameRequest.playerColor(), auth);
            // TODO: figure out what we need to pass in to websocketHandler methods
//            webSocketHandler.loadGame(auth, session);
            res.status(200);
            return new Gson().toJson(joinGameResponse);
        } catch (DataAccessException e) {
            JoinGameResponse joinGameResponse = new JoinGameResponse( 0, "Error: Unable to join game");
            DataAccessException exceptionData = new DataAccessException(e.getMessage());
            System.out.println(exceptionData.getMessage());
            if (e.getMessage().equals("Error: unauthorized")) {
                res.status(401);
            }
            if (e.getMessage().equals("Error: bad request")) {
                res.status(400);
            }
            if (e.getMessage().equals("Error: already taken")) {
                res.status(403);
            }
            return new Gson().toJson(joinGameResponse);
        }
    }
}
