package service;

import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import response.ListGamesResponse;
import response.RegisterResponse;

public class GameService {

    public ListGamesResponse listGames(AuthData authToken) throws DataAccessException {
        if (!MemoryAuthDAO.authDataArrayList.contains(authToken)) {
            return new ListGamesResponse(null, false, "Error: Could not register.");
        }
        else {
//            TODO: refill in parameters
            return new ListGamesResponse(null, false, "Error: Could not register.");
        }
    }



}
