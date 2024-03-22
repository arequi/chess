package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;

import javax.xml.crypto.Data;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.*;

public class SQLGameDAO implements GameDAO{


    @Override
    public void clear() throws DataAccessException {
        DatabaseManager.createDatabase();
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
        ChessGame game = new ChessGame();
        ArrayList<UserData> observers = new ArrayList<>();
        GameData data = new GameData(gameID, null, null, gameName, game, null);
        String sql = "INSERT INTO Game (gameID, whiteUsername, blackUsername, gameName, game, observers) VALUES(?,?,?,?,?,?)";
        try (var conn = DatabaseManager.getConnection()) {
            DatabaseManager.createDatabase();
            configureDatabase();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, gameID);
                stmt.setString(2, null);
                stmt.setString(3, null);
                stmt.setString(4, gameName);
                stmt.setObject(5, new Gson().toJson(game));
                stmt.setObject(6, new Gson().toJson(observers));

                stmt.executeUpdate();
                return data;
            }
        }catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while creating a game into the database");
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        ResultSet rs;
        String sql = "SELECT * FROM Game WHERE gameID = ?;";
        try (var conn = DatabaseManager.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, gameID);
                rs = stmt.executeQuery();
                if (rs.next()) {
                    String observerString = rs.getString("observers");
                    Type listOfObservers = new TypeToken<ArrayList<UserData>>() {}.getType();
                    var serializer = new Gson();

                    ArrayList<UserData> observers;
                    if (observerString.isEmpty()) {
                        observers = null;
                    }
                    else {
                        observers = serializer.fromJson(observerString, listOfObservers);
                    }
                    ChessGame game = serializer.fromJson((String) rs.getObject("game"), ChessGame.class);
                    return new GameData(rs.getInt("gameID"), rs.getString("whiteUsername"),
                            rs.getString("blackUsername"), rs.getString("gameName"), game, observers);
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
                    String observerString = rs.getString("observers");
                    Type listOfObservers = new TypeToken<ArrayList<UserData>>() {}.getType();
                    var serializer = new Gson();
                    ArrayList<UserData> observers = serializer.fromJson(String.valueOf(observerString), listOfObservers);
                    ChessGame game = serializer.fromJson((String) rs.getObject("game"), ChessGame.class);
                    gameArray.add(new GameData(rs.getInt("gameID"), rs.getString("whiteUsername"),
                            rs.getString("blackUsername"), rs.getString("gameName"), game, observers));
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
        ChessGame game = updatedGame.game();
        String whiteUsername = updatedGame.whiteUsername();
        String blackUsername = updatedGame.blackUsername();
        ArrayList<UserData> observers = updatedGame.observers();
        String sql = "UPDATE Game " +
                "SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ?," +
                "observers = ?" +
                "WHERE gameID = ?;";
        try (var conn = DatabaseManager.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, whiteUsername);
                stmt.setString(2, blackUsername);
                stmt.setString(3, gameName);
                stmt.setObject(4, new Gson().toJson(game));
                stmt.setObject(5, new Gson().toJson(observers));
                stmt.setInt(6, gameID);

                stmt.executeUpdate();
            }
        }catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while inserting a game into the database");
        }
    }

    void configureDatabase() throws DataAccessException{
        final String statement =
                """
            CREATE TABLE IF NOT EXISTS Game (
              `gameID` int NOT NULL,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `gameName` varchar(256) NOT NULL,
              `game` text NOT NULL,
              `observers` Blob,
              PRIMARY KEY (`gameID`)
            )
            """;
        try (var conn = DatabaseManager.getConnection()) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
        } catch (SQLException ex) {
            throw new DataAccessException("unable to configure Game table");
        }
    }

}
