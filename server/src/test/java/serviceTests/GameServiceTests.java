package serviceTests;

import chess.ChessGame;
import dataAccess.*;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.response.CreateGameResponse;
import model.response.JoinGameResponse;
import model.response.RegisterResponse;
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
        new ClearService().clear();
        ChessGame game = new ChessGame();
        ChessGame otherGame = new ChessGame();
        realUser = new UserData("sgonza", "3545", "@byu");
        realGame = new GameData(1234, "whiteTeam", "blackTeam", "realGame", game, null);
        realGame2 = new GameData(3545, null, "blackTeam", "realGame2", game, null);
        fakeGame = new GameData(5678, "whiteTeam", "blackTeam", "fakeGame", otherGame, null);
        fakeAuth = "i'm fake";
        fakeGameID = 102;
    }

    @Test
    void ListGamesPass() throws DataAccessException{
        RegisterResponse result = new UserService().register(realUser);
        realAuth = result.authToken();
        new GameService().createGame("myGame", realAuth);
        ArrayList<GameData> games = new GameService().listGames(realAuth).games();
        assertEquals(1, games.size());
        new GameService().createGame("myGame2", realAuth);
        ArrayList<GameData> games2 = new GameService().listGames(realAuth).games();
        assertEquals(2, games2.size());
    }

    @Test
    void ListGamesFail() throws DataAccessException{
        RegisterResponse result = new UserService().register(realUser);
        realAuth = result.authToken();
        new GameService().createGame("myGame", realAuth);
        ArrayList<GameData> games = new GameService().listGames(realAuth).games();
        assertEquals(1, games.size());
        assertThrows(DataAccessException.class, ()->new GameService().listGames(fakeAuth));
    }

    @Test
    void CreateGamePass() throws DataAccessException{
        RegisterResponse result = new UserService().register(realUser);
        realAuth = result.authToken();
        assertTrue(new MemoryGameDAO().listGames().isEmpty());
        new GameService().createGame("myGame", realAuth);
        assertNotNull(new MemoryGameDAO().listGames());
    }

    @Test
    void CreateGameFail() throws DataAccessException{
        RegisterResponse result = new UserService().register(realUser);
        realAuth = result.authToken();
        assertTrue(new MemoryGameDAO().listGames().isEmpty());
        new GameService().createGame("myGame", realAuth);
        assertNotNull(new MemoryGameDAO().listGames());
        assertThrows(DataAccessException.class, ()-> new GameService().createGame("fakeGame", fakeAuth));
    }

    @Test
    void JoinGamePass() throws DataAccessException{
        RegisterResponse result = new UserService().register(realUser);
        realAuth = result.authToken();
        CreateGameResponse newGame = new GameService().createGame("mygame", realAuth);
        JoinGameResponse joinGameResponse = new GameService().joinGame(newGame.gameID(), "WHITE", realAuth);
        assertEquals(new MemoryGameDAO().getGame(joinGameResponse.gameID()).whiteUsername(), realUser.username());
    }

    @Test
    void JoinGameFail() throws DataAccessException{
        RegisterResponse result = new UserService().register(realUser);
        realAuth = result.authToken();
        new GameService().createGame("mygame", realAuth);
        assertThrows(DataAccessException.class, ()->new GameService().joinGame(fakeGameID, "WHOTE", realAuth));
    }
}
