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
        if (user.username() == null || user.password() == null) {
            throw new DataAccessException("Error: bad request");
        }
         else if (new MemoryUserDAO().getUser(user.username()) != null) {
             throw new DataAccessException("Error: already taken");
        }
        AuthData authToken = new MemoryAuthDAO().createAuth(user.username());
        new MemoryUserDAO().createUser(user.username(), user.password(), user.email());
        String authString = authToken.authToken();
        return new RegisterResponse(user.username(), authString, true, null);
    }

    public LoginResponse login(UserData user) throws DataAccessException {
        if (new MemoryUserDAO().getUser(user.username()) == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        UserData memoryUser = new MemoryUserDAO().getUser(user.username());
        if (memoryUser.password().equals(user.password())) {
            AuthData authToken = new MemoryAuthDAO().createAuth(user.username());
            String authString = authToken.authToken();
            return new LoginResponse(user.username(), authString, true, null);
        }
        else {
            throw new DataAccessException("Error: unauthorized");
        }
    }
    public LogoutResponse logout(String auth) throws DataAccessException {
        if (new MemoryAuthDAO().getAuth(auth) == null) {
            new LogoutResponse(false, "Could not log out. User does not exist");
            //TODO: maybe replace with next line
            throw new DataAccessException("Error: unauthorized");
        }
            new MemoryAuthDAO().deleteAuth(auth);
            return new LogoutResponse(true, null);
    }

}
