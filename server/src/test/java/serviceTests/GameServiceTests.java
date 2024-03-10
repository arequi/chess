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
import response.JoinGameResponse;
import response.RegisterResponse;
import service.ClearService;
import service.GameService;
import service.UserService;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {

    GameData realGame;
    GameData realGame2;

    GameData fakeGame;
    String realAuth;
    String fakeAuth;
    UserData realUser;
    int fakeGameID;

    @BeforeEach
    public void setup() throws DataAccessException {
        ChessGame game = new ChessGame();
        ChessGame otherGame = new ChessGame();
        realUser = new UserData("sgonza", "3545", "@byu");
        realGame = new GameData(1234, "whiteTeam", "blackTeam", "realGame", game);
        realGame2 = new GameData(3545, null, "blackTeam", "realGame2", game);
        fakeGame = new GameData(5678, "whiteTeam", "blackTeam", "fakeGame", otherGame);
        fakeAuth = "i'm fake";
        fakeGameID = 102;
    }

    @Test
    void ListGamesPass() throws DataAccessException{
        new ClearService().clear();
        RegisterResponse result = new UserService().register(realUser);
        realAuth = result.authToken();
        new GameService().CreateGame("myGame", realAuth);
        ArrayList<GameData> games = new GameService().listGames(realAuth).games();
        assertEquals(1, games.size());
        new GameService().CreateGame("myGame2", realAuth);
        ArrayList<GameData> games2 = new GameService().listGames(realAuth).games();
        assertEquals(2, games2.size());
    }

    @Test
    void ListGamesFail() throws DataAccessException{
        new ClearService().clear();
        RegisterResponse result = new UserService().register(realUser);
        realAuth = result.authToken();
        new GameService().CreateGame("myGame", realAuth);
        ArrayList<GameData> games = new GameService().listGames(realAuth).games();
        assertEquals(1, games.size());
        assertFalse(new GameService().listGames(fakeAuth).success());
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
        new ClearService().clear();
        RegisterResponse result = new UserService().register(realUser);
        realAuth = result.authToken();
        CreateGameResponse newGame = new GameService().CreateGame("mygame", realAuth);
        JoinGameResponse joinGameResponse = new GameService().JoinGame("WHITE", newGame.gameID(), realAuth);
        assertEquals(new MemoryGameDAO().getGame(joinGameResponse.gameID()).whiteUsername(), realUser.username());
    }

    @Test
    void JoinGameFail() throws DataAccessException{
        // if gameID is invalid or authToken is invalid
        new ClearService().clear();
        RegisterResponse result = new UserService().register(realUser);
        realAuth = result.authToken();
        CreateGameResponse newGameResponse = new GameService().CreateGame("mygame", realAuth);
        assertFalse(new GameService().JoinGame("WHITE", fakeGameID, realAuth).success());
    }
}
