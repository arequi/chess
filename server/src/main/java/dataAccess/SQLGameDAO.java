package dataAccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import static server.Server.gameDataArrayList;

public class SQLGameDAO implements GameDAO{
    Connection conn;


    public SQLGameDAO(Connection conn) {
        this.conn = conn;
    }

    GameData data;

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Game")) {
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while clearing the game table");
        }
    }

    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        Random rand = new Random();
        Integer gameID = rand.nextInt(10000) + 1;
        ChessGame newGame = new ChessGame();
        GameData data = new GameData(gameID, null, null, gameName, newGame);
        String sql = "INSERT INTO Game (gameID, whiteUsername, blackUsername, gameName, game) VALUES(?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameID);
            stmt.setString(2, null);
            stmt.setString(3,null);
            stmt.setString(4,gameName);
            stmt.setObject(5, newGame);

            stmt.executeUpdate();
            return data;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while inserting a game into the database");
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        ResultSet rs;
        String sql = "SELECT * FROM Game WHERE gameID = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, String.valueOf(gameID));
            rs = stmt.executeQuery();
            if (rs.next()) {
                return new GameData(rs.getInt("gameID"), rs.getString("whiteUsername"),
                        rs.getString("blackUsername"), rs.getString("gameName"), (ChessGame) rs.getObject("game"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding a game in the database");
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        ResultSet rs;

        String sql = "SELECT * FROM Game";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            Collection<GameData> gameCollection = new HashSet<>();
            rs = stmt.executeQuery();
            while (rs.next()) {
                gameCollection.add(new GameData(rs.getInt("gameID"), rs.getString("whiteUsername"),
                        rs.getString("blackUsername"), rs.getString("gameName"), (ChessGame) rs.getObject("game")));
            }
            return gameCollection;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while listing games in the database");
        }
    }

    @Override
    public void updateGame(int gameID, GameData updatedGame) throws DataAccessException{
        String whiteUsername = updatedGame.whiteUsername();
        String blackUsername = updatedGame.blackUsername();
        String gameName = updatedGame.gameName();
        ChessGame newGame = updatedGame.game();
        String sql = "UPDATE Game " +
                "SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ? " +
                "WHERE gameID = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameID);
            stmt.setString(2, whiteUsername);
            stmt.setString(3, blackUsername);
            stmt.setString(4, gameName);
            stmt.setObject(5, newGame);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while inserting a game into the database");
        }
    }
}
