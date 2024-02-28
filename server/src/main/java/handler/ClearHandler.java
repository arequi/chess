package handler;

import com.google.gson.Gson;
import response.ClearResponse;
import service.ClearService;

public class ClearHandler {

    public ClearResponse handle(Gson gson) {

        ClearService service = new ClearService();
        ClearResponse response = service.clear();

        return response;
    }

}
