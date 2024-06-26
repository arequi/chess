package ui;

import model.response.LoginResponse;
import model.response.RegisterResponse;
import ui.websocket.NotificationHandler;

import java.util.Arrays;

import static ui.EscapeSequences.*;

public class PreLoginUI {

    private final ServerFacade server;
    public static String authToken;

    public PreLoginUI(String serverUrl, NotificationHandler notificationHandler) {
        server = new ServerFacade(serverUrl);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                default -> displayHelp();
            };
        }
        catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String displayHelp () {
        String helpString =
                " register <USERNAME> <PASSWORD> <EMAIL>" + SET_TEXT_COLOR_MAGENTA + " - to create an account"
                + SET_TEXT_COLOR_BLUE + "\n login <USERNAME> <PASSWORD>" + SET_TEXT_COLOR_MAGENTA + " - to play chess"
                + SET_TEXT_COLOR_BLUE + "\n quit" +  SET_TEXT_COLOR_MAGENTA + " - playing chess"
                + SET_TEXT_COLOR_BLUE + "\n help" + SET_TEXT_COLOR_MAGENTA + " - with possible commands";

        return helpString;

    }

    public String login (String... params) throws ResponseException {
        LoginResponse response = server.login(params);
        authToken = response.authToken();
        if (params.length >= 1) {
            if (response.message() == null) {
                return "Successfully logged in";
            }
        }
        throw new ResponseException("Incorrect username or password. Try again");
    }

    public String register (String... params) throws ResponseException {
        RegisterResponse response = server.register(params);
        authToken = response.authToken();
        if (params.length >= 1) {
            if (response.message() == null) {
                return "Successfully registered.";
            }
        }
        if (response.message().equals("Error: already taken")) {
            throw new ResponseException("Username is already registered.");
        }
        else if (response.message().equals("Error: bad request")) {
            throw new ResponseException("Expected: <username> <password> <email>");
        }
        else {
            throw new ResponseException("Error unknown.");
        }
    }


}
