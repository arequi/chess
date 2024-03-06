package service;

import response.ClearResponse;
import dataAccess.*;

public class ClearService {

    ClearResponse result;


    /**
     * Creates result after attempting to clear.
     *
     * @return ClearResult
     */

    public ClearResponse clear() throws DataAccessException{
        new MemoryUserDAO().clear();
        new MemoryGameDAO().clear();
        new MemoryAuthDAO().clear();
        result = new ClearResponse(true, "Clear succeeded.");
        return result;
    }
}
