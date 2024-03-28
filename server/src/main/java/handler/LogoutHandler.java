package handler;

import com.google.gson.Gson;
import model.response.LogoutResponse;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;
import dataAccess.DataAccessException;

public class LogoutHandler implements Route {

    @Override
    public Object handle(Request req, Response res){
        try {
            String auth = req.headers("authorization");
            UserService service = new UserService();
            service.logout(auth);
            res.status(200);
            return "{}";
        } catch (DataAccessException e) {
            DataAccessException exceptionData = new DataAccessException(e.getMessage());
            System.out.println(exceptionData.getMessage());
            LogoutResponse logoutResponse = new LogoutResponse("Error: could not log out.");
            res.status(401);
            return new Gson().toJson(logoutResponse);
        }
    }
}

