package clientTests;

import dataAccess.DataAccessException;
import model.response.*;
import org.junit.jupiter.api.*;
import server.Server;
import service.ClearService;
import ui.ResponseException;
import ui.ServerFacade;

import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;
import static ui.ServerFacade.gameIDs;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() throws DataAccessException {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() throws DataAccessException {
        server.stop();
    }

    @AfterEach
    public void after () throws DataAccessException {
        new ClearService().clear();
        gameIDs = new TreeMap<>();
    }

    @BeforeEach
    public void before () throws DataAccessException {
        new ClearService().clear();
    }

    @Test
    public void registerPass() throws ResponseException {
        String username = "sarahg";
        String password = "3545";
        String email = "@byu";
        RegisterResponse response = serverFacade.register(username, password, email);
        assertNull(response.message());
        username = "myuser";
        password = "mypw";
        response = serverFacade.register(username, password);
        assertNull(response.message());
    }

    @Test
    public void registerFail () throws ResponseException{
        String username = "sarahg";
        String password = "3545";
        String email = "@byu";
        serverFacade.register(username, password, email);
        assertThrows(Exception.class, ()-> serverFacade.register(username, password, email));
    }

    @Test
    public void loginPass() throws ResponseException {
        String username = "sarahg";
        String password = "3545";
        String email = "@byu";
        RegisterResponse response = serverFacade.register(username, password, email);
        assertNull(response.message());
        username = "myuser";
        password = "mypw";
        response = serverFacade.register(username, password);
        assertNull(response.message());
    }

    @Test
    public void loginFail() throws ResponseException {
        String username = "sarahg";
        String password = "3545";
        String email = "@byu";
        RegisterResponse response = serverFacade.register(username, password, email);
        assertNull(response.message());
        username = "myuser";
        password = "mypw";
        response = serverFacade.register(username, password);
        assertNull(response.message());
    }

    @Test
    public void logoutPass() throws ResponseException {
        String username = "sarahg";
        String password = "3545";
        String email = "@byu";
        RegisterResponse regResponse = serverFacade.register(username, password, email);
        LogoutResponse response = serverFacade.logout();
        assertNull(response.message());
    }

    @Test
    public void logoutFail() throws ResponseException {
        assertThrows(Exception.class, ()-> serverFacade.logout());
    }

    @Test
    public void createGamePass() throws ResponseException {
        String username = "sarahg";
        String password = "3545";
        String email = "@byu";
        serverFacade.register(username, password, email);
        CreateGameResponse response = serverFacade.createGame("realgameName");
        assertNull(response.message());
    }

    @Test
    public void createGameFail() throws ResponseException {
        assertThrows(Exception.class, ()-> serverFacade.createGame("im game name"));
    }

    @Test
    public void listGamesPass() throws ResponseException {
        String username = "sarahg";
        String password = "3545";
        String email = "@byu";
        serverFacade.register(username, password, email);
        serverFacade.createGame("realGameName");
        ListGamesResponse response = serverFacade.listGames();
        assertNull(response.message());
    }

    @Test
    public void listGamesFail() throws ResponseException {
        assertThrows(Exception.class, ()-> serverFacade.listGames());
    }

    @Test
    public void joinGamePass() throws ResponseException {
        String username = "sarahg";
        String password = "3545";
        String email = "@byu";
        serverFacade.register(username, password, email);
        serverFacade.createGame("realGameName");
        String playerColor = "BLACK";
        JoinGameResponse response = serverFacade.joinGame(String.valueOf(1), playerColor);
        System.out.println("response(gameID): " + response.gameID());
        System.out.println("gameIDs map: " + gameIDs);
        assertNull(response.message());
    }

    @Test
    public void joinGameFail() throws ResponseException {
        String playerColor = "BLACK";
        assertThrows(Exception.class, ()-> serverFacade.joinGame(String.valueOf(1)), playerColor);
    }

    @Test
    public void observeGamePass() throws ResponseException {
        String username = "sarahg";
        String password = "3545";
        String email = "@byu";
        serverFacade.register(username, password, email);
        serverFacade.createGame("realGameName");
        JoinGameResponse response = serverFacade.observeGame(String.valueOf(1));
        assertNull(response.message());
    }

    @Test
    public void observeGameFail() throws ResponseException {
        int gameID = 1213;
        assertThrows(Exception.class, ()-> serverFacade.joinGame(String.valueOf(gameID)));
    }

}
