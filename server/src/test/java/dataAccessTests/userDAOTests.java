package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import dataAccess.SQLUserDAO;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ClearService;

import java.sql.Connection;

import static dataAccess.DatabaseManager.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static server.Server.userDataArrayList;

public class userDAOTests {

    private DatabaseManager db;
    private UserData bestUser;
    private UserData secondBestUser;
    private SQLUserDAO uDao;
    private String fakeUsername;
    private String realUsername;
    private String secondRealUsername;
    private String password;
    private String personID;



    @BeforeEach
    public void setUp() throws DataAccessException {
        // Here we can set up any classes or variables we will need for each test
        // lets create a new instance of the Database class
        db = new DatabaseManager();
        fakeUsername = "notAUsername";
        realUsername = "sarahggg";
        password = "66381263";
        secondRealUsername = "hawaii321";
        // and a new event with random data
        bestUser = new UserData(realUsername, password, "sng@gmail.com");
        secondBestUser = new UserData(secondRealUsername, "ktu", "76859@gmail.com");
        // Here, we'll open the connection in preparation for the test case to use it
        Connection conn = db.databaseName;
        //Then we pass that connection to the EventDAO, so it can access the database.
        uDao = new SQLUserDAO(conn);
        //Let's clear the database as well so any lingering data doesn't affect our tests
        uDao.clear();
    }
    @Test
    void clearUserPass() {
        new SQLUserDAO(conn).createUser("sarahg3545", "bscdsoiuco", "sgona22@byu.edu");
        assertNotNull(userDataArrayList);
        new ClearService().clear();
        assertTrue(userDataArrayList.isEmpty());
    }

    @Test
    void createUserPass () {

    }

    @Test
    void createUserFail () {

    }

    @Test
    void getUserPass () {

    }

    @Test
    void getUserFail () {

    }


    @AfterEach
    public void tearDown() {

    }
}
