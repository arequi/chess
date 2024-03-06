package handler;

import response.LogoutResponse;
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
            new LogoutResponse(false, "could not log out.");
            DataAccessException exceptionData = new DataAccessException(e.getMessage());
            System.out.println(exceptionData.getMessage());
        }
        return null;
    }
}

