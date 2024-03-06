package handler;

import com.google.gson.Gson;
import model.UserData;
import request.LoginRequest;
import request.RegisterRequest;
import response.LoginResponse;
import response.RegisterResponse;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoginHandler implements Route {

    @Override
    public Object handle(Request req, Response res) throws Exception {
        Gson gson = new Gson();
        UserService service = new UserService();
        // serialize and send in UserData
        LoginRequest request = gson.fromJson(req.body(), LoginRequest.class);
        UserData user = new UserData(request.username(), request.password(), null);
        LoginResponse response = service.login(user);
        // deserialize response and return json object?
        return new Gson().toJson(response);
    }
}
