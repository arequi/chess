package handler;

import dataAccess.DataAccessException;
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
        return "{}";
    }
}
