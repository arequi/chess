package handler;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import request.CreateGameRequest;
import request.RegisterRequest;
import response.CreateGameResponse;
import response.ListGamesResponse;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

public class CreateGameHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
        try {
            Gson gson = new Gson();
            String auth = req.headers("authorization");
            GameService service = new GameService();
            CreateGameRequest createGameRequest = gson.fromJson(req.body(), CreateGameRequest.class);
            CreateGameResponse createGameResponse = service.CreateGame(createGameRequest.gameName(), auth);
            res.status(200);
            return new Gson().toJson(createGameResponse);
        } catch (DataAccessException e) {
            CreateGameResponse createGameResponse = new CreateGameResponse( null,
                    false, "Error: Unable to create game");
            DataAccessException exceptionData = new DataAccessException(e.getMessage());
            System.out.println(exceptionData.getMessage());
            if (e.getMessage().equals("Error: unauthorized")) {
                res.status(401);
            }
            if (e.getMessage().equals("Error: bad request")) {
                res.status(400);
            }
            return new Gson().toJson(createGameResponse);
        }
    }
}
