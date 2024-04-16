package ui;

import com.google.gson.Gson;
import model.AuthData;
import model.request.CreateGameRequest;
import model.request.JoinGameRequest;
import model.request.LoginRequest;
import model.request.RegisterRequest;
import model.response.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;


public class ServerFacade {
    private final String serverUrl;
    private static AuthData authToken;

    public static Map<String, String> authTokens;

    public ServerFacade(String url) {
        serverUrl = url;
        authTokens = new TreeMap<>();
    }


    public RegisterResponse register(String... params) throws ResponseException {
        String username = params[0];
        String password = params[1];
        String email;
        if (params.length == 3) {email = params[2];}
        else {email = null;}
        RegisterRequest request = new RegisterRequest(username, password, email);
        RegisterResponse response = this.makeRequest("POST", "/user", request, RegisterResponse.class);
        authToken = new AuthData(response.authToken(), response.username());
        authTokens.put(authToken.authToken(), "Player " + authTokens.size()+1);
        if (response.message() == null) {
            Repl.state = State.LOGGED_IN;
        }
        return response;
    }

    public LoginResponse login(String... params) throws ResponseException {
        String username = params[0];
        String password = params[1];
        LoginRequest request = new LoginRequest(username, password);
        LoginResponse response = this.makeRequest("POST", "/session", request, LoginResponse.class);
        authToken = new AuthData(response.authToken(), response.username());
        authTokens.put(authToken.authToken(), "Player " + authTokens.size()+1);
        if (response.message() == null) {
            Repl.state = State.LOGGED_IN;
        }
        return response;
    }

    public LogoutResponse logout () throws ResponseException {
        LogoutResponse response = this.makeRequest("DELETE", "/session", null, LogoutResponse.class);
        if (response.message() == null) {
           Repl.state = State.LOGGED_OUT;
        }
        authTokens.remove(authToken.authToken());
        return response;
    }

    public CreateGameResponse createGame (String gameName) throws ResponseException{
        CreateGameRequest request = new CreateGameRequest(gameName);
        return this.makeRequest("POST", "/game", request, CreateGameResponse.class);
    }

    public ListGamesResponse listGames () throws ResponseException {
        return this.makeRequest("GET", "/game", null, ListGamesResponse.class);
    }

    public JoinGameResponse joinGame (Integer gameNum, String playerColor) throws ResponseException {
        JoinGameRequest request = new JoinGameRequest(gameNum, playerColor);
        return this.makeRequest("PUT", "/game", request, JoinGameResponse.class);
    }

    public JoinGameResponse observeGame (Integer gameNum) throws ResponseException {
        JoinGameRequest request = new JoinGameRequest(gameNum, null);
        return this.makeRequest("PUT", "/game", request, JoinGameResponse.class);
    }

    public <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            if (!(method.equals("GET") && path.equals("/game")) && !(method.equals("DELETE") && path.equals("/session"))) {
                http.setDoOutput(true);
            }
            if (!(method.equals("POST") && path.equals("/user")) && !(method.equals("POST") && path.equals("/session"))) {
                http.addRequestProperty("Authorization", authToken.authToken());
            }
            writeBody(request, http);

            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException("failure: " + status);
        }
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
