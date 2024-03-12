package dataAccess;

import model.UserData;

import java.sql.SQLException;

import static dataAccess.DatabaseManager.databaseName;

public class SQLUserDAO implements UserDAO{



    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            // execute SQL statements.
            try (var preparedStatement = conn.prepareStatement("DELETE FROM User")) {
                var rs = preparedStatement.executeQuery();
                rs.next();
                System.out.println(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while clearing the user table");
        }
    }

    @Override
    public void createUser(String username, String password, String email) throws DataAccessException {

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }
}
