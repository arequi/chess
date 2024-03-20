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
        fakeUsername = "notAUsername";
        realUsername = "sarahggg";
        password = "66381263";
        secondRealUsername = "hawaii321";
        // and a new event with random data
        bestUser = new UserData(realUsername, password, "sng@gmail.com");
        secondBestUser = new UserData(secondRealUsername, "ktu", "76859@gmail.com");
        uDao = new SQLUserDAO();
        //Let's clear the database as well so any lingering data doesn't affect our tests
        uDao.clear();
    }
    @Test
    void clearUserPass() throws DataAccessException{
        new SQLUserDAO().createUser("sarahg3545", "bscdsoiuco", "sgona22@byu.edu");
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
