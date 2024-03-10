package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryUserDAO;
import model.UserData;
import org.junit.jupiter.api.Test;
import response.ClearResponse;
import service.ClearService;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static server.Server.userDataArrayList;

public class ClearServiceTests {

    @Test
    public void clearPass() throws DataAccessException {
        new MemoryUserDAO().createUser("sarahg3545", "bscdsoiuco", "sgona22@byu.edu");
        assertNotNull(userDataArrayList);
        new ClearService().clear();
        assertTrue(userDataArrayList.isEmpty());
    }

}
