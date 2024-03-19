package service;

import dataAccess.*;
import model.AuthData;
import model.UserData;
import response.LoginResponse;
import response.LogoutResponse;
import response.RegisterResponse;

import java.sql.Connection;


public class UserService {
    Connection conn;

    public RegisterResponse register(UserData user) throws DataAccessException {
        if (user.username() == null || user.password() == null) {
            throw new DataAccessException("Error: bad request");
        }
         else if (new SQLUserDAO(conn).getUser(user.username()) != null) {
             throw new DataAccessException("Error: already taken");
        }
        AuthData authToken = new SQLAuthDAO(conn).createAuth(user.username());
        new SQLUserDAO(conn).createUser(user.username(), user.password(), user.email());
        String authString = authToken.authToken();
        return new RegisterResponse(user.username(), authString, null);
    }

    public LoginResponse login(UserData user) throws DataAccessException {
        if (new SQLUserDAO(conn).getUser(user.username()) == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        UserData sqlUser = new SQLUserDAO(conn).getUser(user.username());
        if (sqlUser.password().equals(user.password())) {
            AuthData authToken = new SQLAuthDAO(conn).createAuth(user.username());
            String authString = authToken.authToken();
            return new LoginResponse(user.username(), authString, null);
        }
        else {
            throw new DataAccessException("Error: unauthorized");
        }
    }
    public LogoutResponse logout(String auth) throws DataAccessException {
        if (new SQLAuthDAO(conn).getAuth(auth) == null) {
            new LogoutResponse("Could not log out. User does not exist");
            throw new DataAccessException("Error: unauthorized");
        }
            new SQLAuthDAO(conn).deleteAuth(auth);
            return new LogoutResponse(null);
    }

}
