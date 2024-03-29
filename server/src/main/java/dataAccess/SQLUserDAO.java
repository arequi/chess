package dataAccess;

import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUserDAO implements UserDAO{


    @Override
    public void clear() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            configureDatabase();
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM User")) {
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while clearing the user table");
        }
    }

    @Override
    public void createUser(String username, String password, String email) throws DataAccessException {
        String sql = "INSERT INTO User (username, password, email) VALUES(?,?,?)";
        try (var conn = DatabaseManager.getConnection()) {
            DatabaseManager.createDatabase();
            configureDatabase();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                stmt.setString(3, email);

                stmt.executeUpdate();
            }
        }catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while inserting a user into the database");
        }

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        ResultSet rs;
        String sql = "SELECT * FROM User WHERE username = ?;";
        try (var conn = DatabaseManager.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                rs = stmt.executeQuery();
                if (rs.next()) {
                    return new UserData(rs.getString("username"), rs.getString("password"),
                            rs.getString("email"));
                } else {
                    return null;
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding a user in the database");
        }
    }

    void configureDatabase() throws DataAccessException{
        final String statement =
                """
            CREATE TABLE IF NOT EXISTS User (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256),
              PRIMARY KEY (`username`)
            )
            """
        ;
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException("unable to configure User table");
        }
    }
}
