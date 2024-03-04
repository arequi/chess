package service;

import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import response.LoginResponse;
import response.LogoutResponse;
import response.RegisterResponse;


public class UserService {

    public RegisterResponse register(UserData user) throws DataAccessException {
         if (new MemoryUserDAO().getUser(user.username()) != null) {
            return new RegisterResponse(null, null, false, "Error: Could not register.");
        }
        AuthData authToken = new MemoryAuthDAO().createAuth(user.username());
        new MemoryUserDAO().createUser(user.username(), user.password(), user.email());
        String authString = authToken.authToken();
        return new RegisterResponse(user.username(), authString, true, null);
    }

    public LoginResponse login(UserData user) throws DataAccessException {
        if (new MemoryUserDAO().getUser(user.username()) == null) {
            return new LoginResponse(null, null, false, "Error: Could not login.");
        }
        UserData memoryUser = new MemoryUserDAO().getUser(user.username());
        if (memoryUser.password().equals(user.password())) {
            AuthData authToken = new MemoryAuthDAO().createAuth(user.username());
            String authString = authToken.authToken();
            return new LoginResponse(user.username(), authString, true, null);
        }
        else {
            return new LoginResponse(null, null, false, "Error: Could not login.");
        }
    }
    public LogoutResponse logout(UserData user) throws DataAccessException {
        if (new MemoryUserDAO().getUser(user.username()) == null) {
            return new LogoutResponse(false, "Could not log out. User does not exist");
        }
        else {
            String auth = new MemoryAuthDAO().findAuthByUser(user.username());
            new MemoryAuthDAO().deleteAuth(auth);
            return new LogoutResponse(true, null);
        }
    }

}
