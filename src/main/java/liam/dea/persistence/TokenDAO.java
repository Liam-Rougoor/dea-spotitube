package liam.dea.persistence;

import liam.dea.dataobjects.Login;

public interface TokenDAO {
    String getUserWithToken(String token);

    //String getTokenOfUser(String username);

    String createNewTokenForUser(String username);

    boolean tokenIsValid(String token);

    Login getLogin(String user);
}
