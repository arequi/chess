package serviceTests;

import chess.ChessGame;
import dataAccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import response.CreateGameResponse;
import response.RegisterResponse;
import service.ClearService;
import service.GameService;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {

    GameData realGame;
    GameData fakeGame;
    String realAuth;
    UserData realUser;

    @BeforeEach
    public void setup() throws DataAccessException {
        ChessGame game = new ChessGame();
        ChessGame otherGame = new ChessGame();
        realUser = new UserData("sgonza", "3545", "@byu");
        realGame = new GameData(1234, "whiteTeam", "blackTeam", "realGame", game);
        fakeGame = new GameData(5678, "whiteTeam", "blackTeam", "fakeGame", otherGame);
    }

    @Test
    void ListGamesPass() throws DataAccessException{
        new ClearService().clear();
        new GameService().CreateGame("myGame", realAuth);
//        assertTrue((new GameService().listGames()).success());
    }

    @Test
    void ListGamesFail() throws DataAccessException{
        new ClearService().clear();
//        AuthData auth = new MemoryAuthDAO().getAuth()
//        assertFalse(new GameService().listGames().success());
    }

    @Test
    void CreateGamePass() throws DataAccessException{
        new ClearService().clear();
        RegisterResponse result = new UserService().register(realUser);
        realAuth = result.authToken();
        assertTrue(new MemoryGameDAO().listGames().isEmpty());
        new GameService().CreateGame("myGame", realAuth);
        assertNotNull(new MemoryGameDAO().listGames());
        assertTrue(new GameService().CreateGame("myGame", realAuth).success());
    }

    @Test
    void CreateGameFail() throws DataAccessException{
        new ClearService().clear();
        realAuth = "fakeAuth";
        assertTrue(new MemoryGameDAO().listGames().isEmpty());
        new GameService().CreateGame("myGame", realAuth);
        assertNotNull(new MemoryGameDAO().listGames());
        assertFalse(new GameService().CreateGame("myGame", realAuth).success());
    }

    @Test
    void JoinGamePass() throws DataAccessException{

    }

    @Test
    void JoinGameFail() throws DataAccessException{

    }
}
