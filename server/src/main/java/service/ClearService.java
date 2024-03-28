package service;

import model.response.ClearResponse;
import dataAccess.*;

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
        result = new ClearResponse("Clear succeeded.");
        return result;
    }
}
