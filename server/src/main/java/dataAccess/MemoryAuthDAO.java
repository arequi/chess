package dataAccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import static server.Server.authDataArrayList;

public class MemoryAuthDAO implements AuthDAO{

    AuthData data;

    @Override
    public void clear() throws DataAccessException {
        authDataArrayList.clear();
    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        String auth = UUID.randomUUID().toString();
        AuthData newAuthToken = new AuthData(auth, username);
        authDataArrayList.add(newAuthToken);
        return newAuthToken;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        Iterator<AuthData> itr = authDataArrayList.iterator();
        while (itr.hasNext()) {
            AuthData data = itr.next();
            if (data.authToken().equals(authToken)) {
                return data;
            }
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        Iterator<AuthData> itr = authDataArrayList.iterator();
        while (itr.hasNext()) {
            data = itr.next();
            if (data.authToken().equals(authToken)) {
                authDataArrayList.remove(data);
                break;
            }
        }
    }
}
