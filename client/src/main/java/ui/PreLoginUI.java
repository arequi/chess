package ui;

import model.response.LoginResponse;
import model.response.RegisterResponse;

import java.net.URI;
import java.net.URL;
import java.sql.PreparedStatement;
import java.util.Arrays;

import static ui.EscapeSequences.*;

public class PreLoginUI {

    private final String serverUrl;
    private final ServerFacade server;
    private State state = State.LOGGED_OUT;

    public PreLoginUI(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
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
        if (params.length >= 1) {
            if (response.message() == null) {
                return "Successfully logged in";
            }
        }
        throw new ResponseException(401, "Incorrect username or password. Try again");
    }

    public String register (String... params) throws ResponseException {
        RegisterResponse response = server.register(params);
        if (params.length >= 1) {
            if (response.message() == null) {
                return "Successfully registered.";
            }
        }
        if (response.message().equals("Error: already taken")) {
            throw new ResponseException(403, "Username is already registered.");
        }
        else if (response.message().equals("Error: bad request")) {
            throw new ResponseException(400, "Expected: <username> <password> <email>");
        }
        else {
            throw new ResponseException(500, "Error unknown.");
        }
    }


}
