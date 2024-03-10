package handler;

import com.google.gson.Gson;
import model.UserData;
import request.LoginRequest;
import response.LoginResponse;
import response.RegisterResponse;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;
import dataAccess.DataAccessException;


public class LoginHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
        try {
            Gson gson = new Gson();
            UserService service = new UserService();
            LoginRequest request = gson.fromJson(req.body(), LoginRequest.class);
            UserData user = new UserData(request.username(), request.password(), null);
            LoginResponse response = service.login(user);
            return new Gson().toJson(response);
        } catch (DataAccessException e) {
            res.status(401);
            LoginResponse loginResponse = new LoginResponse(null, null,
                    false, "Error: Username or password incorrect.");
            DataAccessException exceptionData = new DataAccessException(e.getMessage());
            System.out.println(exceptionData.getMessage());
            return new Gson().toJson(loginResponse);
        }
    }
}
