package dataAccessTests;

import chess.ChessGame;
import dataAccess.*;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class gameDAOTests {
    String realGameName;
    String realGameName2;

    GameData realGame;
    GameData newGame;
    String fakeAuth;
    UserData realUser;
    int fakeGameID;
    GameDAO gDao;
    UserDAO uDao;



    @BeforeEach
    public void setUp() throws DataAccessException{
        realUser = new UserData("sgonza", "3545", "@byu");
        fakeAuth = "i'm fake";
        fakeGameID = 102;
        realGameName = "hi";
        realGameName2 = "bye";
        gDao = new SQLGameDAO();
        uDao = new SQLUserDAO();
        gDao.clear();
    }

    @AfterEach
    public void tearDown() throws DataAccessException{
        gDao.clear();
    }
    @Test
    void clearGamesPass() throws DataAccessException{
        realGame = gDao.createGame(realGameName);
        int gameID = realGame.gameID();
        gDao.clear();
        assertNull(gDao.getGame(gameID));
    }

    @Test
    void createGamePass () throws DataAccessException{
        realGame = gDao.createGame(realGameName);
        assertNotNull(gDao.getGame(realGame.gameID()));
    }

    @Test
    void createGameFail () throws DataAccessException{
        assertThrows(DataAccessException.class, ()->gDao.createGame(null));
    }

    @Test
    void listGamesPass () throws DataAccessException{
        gDao.createGame(realGameName);
        assertEquals(1, gDao.listGames().size());
    }

    @Test
    void listGamesFail () throws DataAccessException{
        // no games exist
        assertTrue(gDao.listGames().isEmpty());
    }

    @Test
    void updateGamePass () throws DataAccessException{
        realGame = gDao.createGame(realGameName);
        GameData game = gDao.getGame(realGame.gameID());
        GameData newGame = gDao.createGame(realGameName2);
        gDao.updateGame(game.gameID(), newGame);
        assertNotEquals(game, newGame);
    }

    @Test
    void updateGameFail () throws DataAccessException{
        realGame = gDao.createGame(realGameName);
        ChessGame game = new ChessGame();
        ArrayList<UserData> observers = new ArrayList<>();
        assertThrows(DataAccessException.class, ()->gDao.updateGame(realGame.gameID(), new GameData(realGame.gameID(), null, null, null, game, observers)));
    }

    @Test
    void getGamePass () throws DataAccessException{
        realGame = gDao.createGame(realGameName);
        assertNotNull(gDao.getGame(realGame.gameID()));
    }

    @Test
    void getGameFail () throws DataAccessException{
        realGame = gDao.createGame(realGameName);
        assertNull(gDao.getGame(fakeGameID));
    }
}
