package handler;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import model.UserData;
import model.request.RegisterRequest;
import model.response.RegisterResponse;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class RegisterHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
        try {
            Gson gson = new Gson();
            UserService service = new UserService();
            RegisterRequest registerRequest = gson.fromJson(req.body(), RegisterRequest.class);
            UserData user = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
            RegisterResponse registerResponse = service.register(user);
            res.status(200);
            return new Gson().toJson(registerResponse);
        } catch (DataAccessException e) {
            RegisterResponse registerResponse = new RegisterResponse(null, null, "Error: Person already registered.");
            DataAccessException exceptionData = new DataAccessException(e.getMessage());
            System.out.println(exceptionData.getMessage());
            if (e.getMessage().equals( "Error: already taken")) {
                res.status(403);
            }
            if (e.getMessage().equals("Error: bad request")) {
                res.status(400);
            }
            return new Gson().toJson(registerResponse);
        }
    }
}
