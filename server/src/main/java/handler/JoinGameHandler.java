package handler;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import request.CreateGameRequest;
import request.JoinGameRequest;
import response.CreateGameResponse;
import response.JoinGameResponse;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

public class JoinGameHandler implements Route {
    @Override
    public Object handle(Request req, Response res) {
        try {
            Gson gson = new Gson();
            String auth = req.headers("authorization");
            GameService service = new GameService();
            JoinGameRequest joinGameRequest = gson.fromJson(req.body(), JoinGameRequest.class);
            JoinGameResponse joinGameResponse = service.JoinGame(joinGameRequest.playerColor(), joinGameRequest.gameID(), auth);
            res.status(200);
            return new Gson().toJson(joinGameResponse);
        } catch (DataAccessException e) {
            JoinGameResponse joinGameResponse = new JoinGameResponse( 0, "Error: Unable to create game");
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
