package dataAccess;

import model.AuthData;

import java.util.Collection;
import java.util.HashSet;

public class MemoryAuthDAO implements AuthDAO{
    final private Collection<AuthData> authDataCollection = new HashSet<>();
    @Override
    public void clear() throws DataAccessException {
        authDataCollection.clear();
    }

    @Override
    public void createAuth() throws DataAccessException {

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }
}
