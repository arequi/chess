package handler;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import model.response.ListGamesResponse;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public class ListGamesHandler implements Route  {
    @Override
    public Object handle(Request req, Response res) {
        try {
            String auth = req.headers("authorization");
            GameService service = new GameService();
            ListGamesResponse listGamesResponse = service.listGames(auth);
            res.status(200);
            return new Gson().toJson(Map.of("games", listGamesResponse.games()));
        } catch (DataAccessException e) {
            res.status(401);
            ListGamesResponse listGamesResponse = new ListGamesResponse( null, "Error: Unable to list games");
            DataAccessException exceptionData = new DataAccessException(e.getMessage());
            System.out.println(exceptionData.getMessage());
            return new Gson().toJson(listGamesResponse);
        }
    }
}
