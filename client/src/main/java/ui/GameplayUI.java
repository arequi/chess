package ui;

import model.response.LoginResponse;
import model.response.RegisterResponse;
import ui.websocket.NotificationHandler;

import java.util.Arrays;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static ui.EscapeSequences.SET_TEXT_COLOR_MAGENTA;

public class GameplayUI {
    private final ServerFacade server;

    public GameplayUI(String serverUrl, NotificationHandler notificationHandler) {
        server = new ServerFacade(serverUrl);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "highlight" -> highlight(params);
                case "redraw" -> redraw(params);
                case "move" -> move();
                case "resign" -> resign();
                case "leave" -> leave();
                default -> displayHelp();
            };
        }
        catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String displayHelp () {
        String helpString =
                " highlight" + SET_TEXT_COLOR_MAGENTA + " - legal moves"
                        + SET_TEXT_COLOR_BLUE + "\n redraw" + SET_TEXT_COLOR_MAGENTA + " - chessboard"
                        + SET_TEXT_COLOR_BLUE + "\n move" +  SET_TEXT_COLOR_MAGENTA + " - a chess piece"
                        + SET_TEXT_COLOR_BLUE + "\n resign" + SET_TEXT_COLOR_MAGENTA + " - from game"
                        + SET_TEXT_COLOR_BLUE + "\n leave" +  SET_TEXT_COLOR_MAGENTA + " - game"
                        + SET_TEXT_COLOR_BLUE + "\n help" + SET_TEXT_COLOR_MAGENTA + " - with possible commands";

        return helpString;
    }

    public String highlight (String... params) throws ResponseException {
        LoginResponse response = server.login(params);
        if (params.length >= 1) {
            if (response.message() == null) {
                return "Successfully logged in";
            }
        }
        throw new ResponseException(401, "Incorrect username or password. Try again");
    }

    public String redraw (String... params) throws ResponseException {
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

    public String move (String... params) throws ResponseException {
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

    public String resign (String... params) throws ResponseException {
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

    public String leave (String... params) throws ResponseException {
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
