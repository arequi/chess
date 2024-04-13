package ui;

import model.response.LoginResponse;
import model.response.RegisterResponse;
import ui.websocket.NotificationHandler;

import java.util.Arrays;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static ui.EscapeSequences.SET_TEXT_COLOR_MAGENTA;
import static ui.PostLoginUI.displayBoard;

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
                case "move" -> move(params);
                case "resign" -> resign(params);
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
                        + SET_TEXT_COLOR_BLUE + "\n redraw <WHITE|BLACK|OBSERVER>" + SET_TEXT_COLOR_MAGENTA + " - chessboard"
                        + SET_TEXT_COLOR_BLUE + "\n move" +  SET_TEXT_COLOR_MAGENTA + " - a chess piece"
                        + SET_TEXT_COLOR_BLUE + "\n resign" + SET_TEXT_COLOR_MAGENTA + " - from game"
                        + SET_TEXT_COLOR_BLUE + "\n leave" +  SET_TEXT_COLOR_MAGENTA + " - game"
                        + SET_TEXT_COLOR_BLUE + "\n help" + SET_TEXT_COLOR_MAGENTA + " - with possible commands";

        return helpString;
    }

    public String highlight (String... params) throws ResponseException {
        return "Successfully Highlighted moves";
    }

    public String redraw (String... params) throws ResponseException {
        if (params[0] == null) {
            throw new ResponseException(400, "Error, Expected: <WHITE|BLACK|OBSERVER>");
        }
        String color = params[0];
        if (color.equalsIgnoreCase("black")) {
            displayBoard("black");
        }
        else if (color.equalsIgnoreCase("white") || color.equalsIgnoreCase("observer")) {
            displayBoard("white");
        }
        else {
            throw new ResponseException(400, "Error, Expected: <WHITE|BLACK|OBSERVER>");
        }
        return "Current board displayed";
    }

    public String move (String... params) throws ResponseException {
        return "Successfully made a move";
    }

    public String resign (String... params) throws ResponseException {
        Repl.state = State.LOGGED_IN;
        return "Successfully resigned";
    }

    public String leave () throws ResponseException {
        Repl.state = State.LOGGED_IN;
        return "Successfully Left Game";
    }


}
