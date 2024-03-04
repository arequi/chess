package handler;

import com.google.gson.Gson;
import response.ClearResponse;
import service.ClearService;

public class ClearHandler {

    public ClearResponse handle(Gson gson) {

        ClearService service = new ClearService();

        return service.clear();
    }

}
