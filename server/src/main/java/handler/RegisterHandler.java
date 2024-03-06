package handler;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import model.UserData;
import request.RegisterRequest;
import response.RegisterResponse;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class RegisterHandler implements Route {

    @Override
    public Object handle(Request request, Response response) throws DataAccessException {
        Gson gson = new Gson();
        UserService service = new UserService();
        // serialize and send in UserData
        RegisterRequest registerRequest = gson.fromJson(request.body(), RegisterRequest.class);
        UserData user = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        RegisterResponse registerResponse = service.register(user);
        // deserialize response and return json object?
         return new Gson().toJson(registerResponse);
    }
}
