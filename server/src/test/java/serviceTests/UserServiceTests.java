package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryUserDAO;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.RegisterRequest;
import response.ClearResponse;
import response.LoginResponse;
import response.RegisterResponse;
import service.ClearService;
import service.UserService;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static server.Server.userDataArrayList;

public class UserServiceTests {
    UserData realUser;
    UserData fakeUser;
    String fakeAuth;
    @BeforeEach
    public void setUp() throws DataAccessException{
        realUser = new UserData("sarahg3545", "nicole", "sgonza@byu");
        fakeUser = new UserData("not real", "bleh", "@byu");
        fakeAuth = "im a fake authtoken...";
    }


    @Test
    void registerPass() throws DataAccessException {
        ClearService clearService = new ClearService();
        ClearResponse clearResult = clearService.clear();
        ArrayList<UserData> data = userDataArrayList;
        assertEquals(data.size(), 0);
        assertTrue(clearResult.success());
        UserData user = new UserData("sarahg3545", "bscdsoiuco", "sgona22@byu.edu");
        RegisterResponse result = new UserService().register(user);
        assertTrue(result.success());
    }


    @Test
    void registerFail() throws DataAccessException {
        new ClearService().clear();
        UserData user = new UserData("sarahg3545", "bscdsoiuco", "sgona22@byu.edu");
        assertTrue((new UserService().register(user)).success());
        assertFalse(new UserService().register(user).success());
    }
    @Test
    void loginPass() throws DataAccessException {
        new ClearService().clear();
        new MemoryUserDAO().createUser(realUser.username(), realUser.password(), realUser.email());
        assertTrue(new UserService().login(realUser).success());

    }
    @Test
    void loginFail() throws DataAccessException {
        new ClearService().clear();
        assertFalse(new UserService().login(fakeUser).success());
    }

    @Test
    void logoutPass() throws DataAccessException{
        new ClearService().clear();
        String auth = new UserService().register(realUser).authToken();
        assertTrue(new UserService().logout(auth).success());
    }
    @Test
    void logoutFail() throws DataAccessException{
        new ClearService().clear();
        assertFalse(new UserService().logout(fakeAuth).success());
    }


}
