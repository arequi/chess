package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryUserDAO;
import org.junit.jupiter.api.Test;
import service.ClearService;

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
