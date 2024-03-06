package handler;

import com.google.gson.Gson;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import response.LoginResponse;
import response.LogoutResponse;
import response.RegisterResponse;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;
import dataAccess.DataAccessException;

public class LogoutHandler implements Route {

    @Override
    public Object handle(Request req, Response res) throws DataAccessException{
        try {
            String auth = req.headers("authorization");
            UserService service = new UserService();
            service.logout(auth);
            res.status(200);
            return "{}";
        } catch (DataAccessException e) {
//            return error response

        }
//        Gson gson = new Gson();
//        UserService service = new UserService();
//        // serialize and send in UserData
//        String auth = req.headers("authorization");
//        String username = new
//        UserData user = new UserData(request.username(), request.password(), null);
//        LogoutResponse response = service.logout(user);
//        // deserialize response and return json object?
//        return new Gson().toJson(response);
        return null;
    }
}

