package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryUserDAO;
import model.UserData;
import org.junit.jupiter.api.Test;
import response.ClearResponse;
import service.ClearService;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTests {

    @Test
    public void clearPass() throws DataAccessException {
        new MemoryUserDAO().createUser("sarahg3545", "bscdsoiuco", "sgona22@byu.edu");
        ArrayList<UserData> data = MemoryUserDAO.userDataArrayList;
        assertNotNull(data);
        ClearService clearService = new ClearService();
        ClearResponse result = clearService.clear();
        assertEquals(result.success(), true);
    }

    @Test
    public void clearFail() throws DataAccessException {

    }

}
