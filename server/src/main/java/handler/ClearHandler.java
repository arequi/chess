package handler;

import dataAccess.DataAccessException;
import server.webSocket.ConnectionManager;
import service.ClearService;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import static server.webSocket.ConnectionManager.connections;
import static server.webSocket.ConnectionManager.removeList;

public class ClearHandler {
    ClearService clearService = new ClearService();

    public ClearHandler() {
    }

    public Object handle(Request req, Response res) throws DataAccessException{
        connections = new ConcurrentHashMap<>();
        removeList = new ArrayList<>();
        clearService.clear();
        res.status(200);
        return "{}";
    }
}
