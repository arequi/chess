package handler;

import com.google.gson.Gson;
import model.UserData;
import request.ClearRequest;
import response.ClearResponse;
import response.RegisterResponse;
import service.ClearService;
import spark.Request;
import spark.Response;

public class ClearHandler {

    public Object handle() {
        ClearService service = new ClearService();
        ClearResponse clearResponse = service.clear();
        return new Gson().toJson(clearResponse);
    }

}
