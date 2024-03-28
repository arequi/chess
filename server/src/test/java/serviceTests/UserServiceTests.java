package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryUserDAO;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ClearService;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static server.Server.authDataArrayList;
import static server.Server.userDataArrayList;

public class UserServiceTests {
    UserData realUser;
    UserData fakeUser;
    String fakeAuth;
    @BeforeEach
    public void setUp() throws DataAccessException{
        new ClearService().clear();
        realUser = new UserData("sarahg3545", "nicole", "sgonza@byu");
        fakeUser = new UserData("not real", "bleh", "@byu");
        fakeAuth = "im a fake authtoken...";
    }


    @Test
    void registerPass() throws DataAccessException {
        assertEquals(userDataArrayList.size(), 0);
        UserData user = new UserData("sarahg3545", "bscdsoiuco", "sgona22@byu.edu");
        new UserService().register(user);
        assertFalse(userDataArrayList.isEmpty());
    }


    @Test
    void registerFail() throws DataAccessException {
        UserData user = new UserData("sarahg3545", "bscdsoiuco", "sgona22@byu.edu");
        new UserService().register(user);
        assertThrows(DataAccessException.class, ()-> new UserService().register(user));
    }
    @Test
    void loginPass() throws DataAccessException {
        new MemoryUserDAO().createUser(realUser.username(), realUser.password(), realUser.email());
        assertNotNull(new UserService().login(realUser).authToken());

    }
    @Test
    void loginFail() throws DataAccessException {
        assertThrows(DataAccessException.class, ()-> new UserService().login(fakeUser));
    }

    @Test
    void logoutPass() throws DataAccessException{
        String auth = new UserService().register(realUser).authToken();
        new UserService().logout(auth);
        assertTrue(authDataArrayList.isEmpty());
    }
    @Test
    void logoutFail() throws DataAccessException{
        assertThrows(DataAccessException.class, ()-> new UserService().logout(fakeAuth));
    }


}
