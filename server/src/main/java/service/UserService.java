package service;

import dataAccess.*;
import model.AuthData;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import model.response.LoginResponse;
import model.response.LogoutResponse;
import model.response.RegisterResponse;


public class UserService {
    public RegisterResponse register(UserData user) throws DataAccessException {
        if (user.username() == null || user.password() == null) {
            throw new DataAccessException("Error: bad request");
        }
         else if (new SQLUserDAO().getUser(user.username()) != null) {
             throw new DataAccessException("Error: already taken");
        }
        AuthData authToken = new SQLAuthDAO().createAuth(user.username());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(user.password());
        new SQLUserDAO().createUser(user.username(), hashedPassword, user.email());
        String authString = authToken.authToken();
        return new RegisterResponse(user.username(), authString, null);
    }

    public LoginResponse login(UserData user) throws DataAccessException {
        if (new SQLUserDAO().getUser(user.username()) == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        UserData sqlUser = new SQLUserDAO().getUser(user.username());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (encoder.matches(user.password(), sqlUser.password())) {
            AuthData authToken = new SQLAuthDAO().createAuth(user.username());
            String authString = authToken.authToken();
            return new LoginResponse(user.username(), authString, null);
        }
        else {
            throw new DataAccessException("Error: unauthorized");
        }
    }
    public LogoutResponse logout(String auth) throws DataAccessException {
        if (new SQLAuthDAO().getAuth(auth) == null) {
            new LogoutResponse("Could not log out. User does not exist");
            throw new DataAccessException("Error: unauthorized");
        }
            new SQLAuthDAO().deleteAuth(auth);
            return new LogoutResponse(null);
    }

}
