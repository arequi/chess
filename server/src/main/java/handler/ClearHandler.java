package handler;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import response.ClearResponse;
import service.ClearService;
import spark.Request;
import spark.Response;

public class ClearHandler {
    ClearService clearService = new ClearService();

    public ClearHandler() {
    }

    public Object handle(Request req, Response res) throws DataAccessException{
        clearService.clear();
        res.status(200);
        return new Gson().toJson("{}");
    }
}
