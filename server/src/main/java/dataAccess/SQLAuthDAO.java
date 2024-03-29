package dataAccess;

import model.AuthData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLAuthDAO implements AuthDAO {

    @Override
    public void clear() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            String sql ="DELETE FROM Auth";
            configureDatabase();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while clearing the auth table");
        }
    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        String sql = "INSERT INTO Auth (authToken, username) VALUES(?,?)";
        try (var conn = DatabaseManager.getConnection()) {
            DatabaseManager.createDatabase();
            configureDatabase();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                String authToken = UUID.randomUUID().toString();
                stmt.setString(1, authToken);
                stmt.setString(2, username);
                stmt.executeUpdate();
                return new AuthData(authToken, username);
            }
        }catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while creating an auth into the database");
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        ResultSet rs;
        String sql = "SELECT * FROM Auth WHERE authToken = ?;";
        try (var conn = DatabaseManager.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, authToken);
                rs = stmt.executeQuery();
                if (rs.next()) {
                    return new AuthData(rs.getString("authToken"), rs.getString("username"));
                } else {
                    return null;
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while getting an authToken from the database");
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String sql = "DELETE FROM Auth WHERE authToken = ?;";
        try (var conn = DatabaseManager.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, authToken);
                stmt.executeUpdate();
            }
        }catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while deleting an authToken from the database");
        }
    }

    void configureDatabase() throws DataAccessException{
        final String statement =
                """
            CREATE TABLE IF NOT EXISTS  Auth (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`)
            )
            """;
            try (var conn = DatabaseManager.getConnection()) {
                    try (var preparedStatement = conn.prepareStatement(statement)) {
                        preparedStatement.executeUpdate();
                    }
            } catch (SQLException ex) {
                throw new DataAccessException("unable to configure auth table");
            }

    }

}
