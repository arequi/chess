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

    public ClearResponse clear() {

        try {
            new MemoryUserDAO().clear();
            new MemoryGameDAO().clear();
            new MemoryAuthDAO().clear();
            result = new ClearResponse(true, "Clear succeeded.");
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            result = new ClearResponse(true, "Error: could not clear data.");
            return result;
        }
    }
}
