package dataAccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SQLGameDAO implements GameDAO{
    Connection conn;


    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            configureDatabase();
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
        try (var conn = DatabaseManager.getConnection()) {
            configureDatabase();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, gameID);
                stmt.setString(2, null);
                stmt.setString(3, null);
                stmt.setString(4, gameName);
                stmt.setObject(5, newGame);

                stmt.executeUpdate();
                return data;
            }
        }catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while inserting a game into the database");
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        ResultSet rs;
        String sql = "SELECT * FROM Game WHERE gameID = ?;";
        try (var conn = DatabaseManager.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, String.valueOf(gameID));
                rs = stmt.executeQuery();
                if (rs.next()) {
                    return new GameData(rs.getInt("gameID"), rs.getString("whiteUsername"),
                            rs.getString("blackUsername"), rs.getString("gameName"), (ChessGame) rs.getObject("game"));
                } else {
                    return null;
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding a game in the database");
        }
    }

    @Override
    public ArrayList<GameData> listGames() throws DataAccessException {
        ResultSet rs;

        String sql = "SELECT * FROM Game";
        try (var conn = DatabaseManager.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ArrayList<GameData> gameArray = new ArrayList<>();
                rs = stmt.executeQuery();
                while (rs.next()) {
                    gameArray.add(new GameData(rs.getInt("gameID"), rs.getString("whiteUsername"),
                            rs.getString("blackUsername"), rs.getString("gameName"), (ChessGame) rs.getObject("game")));
                }
                return gameArray;
            }
        }catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while listing games in the database");
        }
    }

    @Override
    public void updateGame(int gameID, GameData updatedGame) throws DataAccessException{
        String gameName = updatedGame.gameName();
        ChessGame newGame = updatedGame.game();
//        UserData observer = ;
        String sql = "UPDATE Game " +
                "SET gameName = ?, game = ?, " +
//                "observer = ?" +
                "WHERE gameID = ?;";
        try (var conn = DatabaseManager.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, gameID);
                stmt.setString(2, gameName);
                stmt.setObject(3, newGame);
//                stmt.setObject(4, observer);

                stmt.executeUpdate();
            }
        }catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while inserting a game into the database");
        }
    }

    void configureDatabase() throws DataAccessException{
        final String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS Game (
              `gameID` int NOT NULL,
              `whiteUsername` varchar(256) DEFAULT NULL,
              `blackUsername` varchar(256) DEFAULT NULL,
              `gameName` varchar(256) NOT NULL,
              `game` blob NOT NULL, 
              PRIMARY KEY (`gameID`),
            )
            """
        };
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("unable to configure Game table");
        }
    }

}
