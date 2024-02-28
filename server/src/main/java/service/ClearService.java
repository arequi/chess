package service;

import response.ClearResponse;
import dataAccess.*;

public class ClearService {


    /**
     * Creates result after attempting to clear.
     *
     * @return ClearResult
     */

    public ClearResponse clear() {
        ClearResponse result;
        try {
            new MemoryUserDAO().clear();
            result = new ClearResponse("Clear succeeded.", true);
            result.setMessage("Clear succeeded.");
            result.setSuccess(true);
            return result;

        } catch (Exception ex) {
            ex.printStackTrace();
            result = new ClearResponse("Error: could not clear data.", false);
            result.setSuccess(false);
            result.setMessage("Could not clear data.");
            return result;
        }

    }


}
