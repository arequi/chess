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
    public Object handle(Request request, Response response) {
        try {
            Gson gson = new Gson();
            UserService service = new UserService();
            RegisterRequest registerRequest = gson.fromJson(request.body(), RegisterRequest.class);
            UserData user = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
            RegisterResponse registerResponse = service.register(user);
            return new Gson().toJson(registerResponse);
        } catch (DataAccessException e) {
            new RegisterResponse(null, null, false, "Error: Could not register.");
            DataAccessException exceptionData = new DataAccessException(e.getMessage());
            System.out.println(exceptionData.getMessage());
        }
        return null;
    }
}
