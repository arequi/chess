package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.*;
import dataAccess.SQLUserDAO;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class userDAOTests {

    private UserData bestUser;
    private UserData secondBestUser;
    private UserDAO uDao;
    private String fakeUsername;
    private String realUsername;
    private String secondRealUsername;
    private String secondRealPassword;
    private String secondRealEmail;
    private String realPassword;
    private String realEmail;


    @BeforeEach
    public void setUp() throws DataAccessException {
        // Here we can set up any classes or variables we will need for each test
        fakeUsername = "notAUsername";
        realUsername = "sarahggg";
        realPassword = "66381263";
        realEmail = "sng@gmail.com";
        secondRealUsername = "hawaii321";
        secondRealPassword = "ktu";
        secondRealEmail = "76859@gmail.com";
        // and a new event with random data
        bestUser = new UserData(realUsername, realPassword, realEmail);
        secondBestUser = new UserData(secondRealUsername, secondRealPassword, secondRealEmail);
        uDao = new SQLUserDAO();
        //Let's clear the database as well so any lingering data doesn't affect our tests
        uDao.clear();
    }

    @AfterEach
    public void takeDown() throws DataAccessException{
        // clear database
        uDao.clear();
    }
    @Test
    void clearUserPass() throws DataAccessException{
        uDao.createUser(realUsername, realPassword, realEmail);
        uDao.clear();
        assertNull(uDao.getUser(realUsername));
    }

    @Test
    void createUserPass () throws DataAccessException{
        uDao.createUser(realUsername, realPassword, realEmail);
        assertNotNull(uDao.getUser(realUsername));
    }

    @Test
    void createUserFail () throws DataAccessException{
        assertThrows(DataAccessException.class, ()->uDao.createUser(realUsername, null, realEmail));
    }

    @Test
    void getUserPass () throws DataAccessException{
        uDao.createUser(realUsername, realPassword, realEmail);
        assertNotNull(uDao.getUser(realUsername));
    }

    @Test
    void getUserFail () throws DataAccessException{
        uDao.createUser(realUsername, realPassword, realEmail);
        uDao.clear();
        assertNull(uDao.getUser(realUsername));
    }
}
