package serviceTests;

import chess.ChessGame;
import dataAccess.GameDAO;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import dataAccess.DataAccessException;

public class GameServiceTests {

    @BeforeEach
    public void setup() {
        ChessGame game = new ChessGame();
        ChessGame othergame = new ChessGame();
        GameData realGame = new GameData(1234, "whiteTeam", "blackTeam", "realGame", game);
        GameData fakeGame = new GameData(5678, "whiteTeam", "blackTeam", "fakeGame", othergame);
    }

    @Test
    void ListGames() throws DataAccessException{
    }

    @Test
    void CreateGame() throws DataAccessException{

    }

    @Test
    void JoinGame() throws DataAccessException{

    }
}
