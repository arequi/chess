package dataAccess;

import model.UserData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class MemoryUserDAO implements UserDAO{
    final private ArrayList<UserData> userDataArrayList = new ArrayList<>();
    public void clear() throws DataAccessException{
        userDataArrayList.clear();
    }
    @Override
    public void createUser(UserData userData) throws DataAccessException {
        userDataArrayList.add(userData);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        Iterator<UserData> itr = userDataArrayList.iterator();
        UserData user;
        while (itr.hasNext()) {
            user = itr.next();
            if (user.username().equals(username)) {
                return user;
            }
        }
        return null;
    }


}
