package service;

import model.response.ClearResponse;
import dataAccess.*;
import server.webSocket.WebSocketHandler;

import java.util.ArrayList;
import java.util.TreeMap;

public class ClearService {

    ClearResponse result;


    /**
     * Creates result after attempting to clear.
     *
     * @return ClearResult
     */

    public ClearResponse clear() throws DataAccessException{
        new SQLUserDAO().clear();
        new SQLAuthDAO().clear();
        new SQLGameDAO().clear();
        WebSocketHandler.gameGroups = new TreeMap<>();
        WebSocketHandler.observerAuths = new ArrayList<>();
        WebSocketHandler.gameIDs = new TreeMap<>();
        result = new ClearResponse("Clear succeeded.");
        return result;
    }
}
