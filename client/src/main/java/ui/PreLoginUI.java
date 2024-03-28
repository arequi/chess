package ui;

import java.net.URI;
import java.net.URL;
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
//        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
//                case "signin" -> signIn(params);
//                case "rescue" -> rescuePet(params);
//                case "list" -> listPets();
//                case "signout" -> signOut();
//                case "adopt" -> adoptPet(params);
//                case "adoptall" -> adoptAllPets();
//                case "quit" -> "quit";
                default -> displayHelp();
            };
//        }
//        catch (ResponseException ex) {
//            return ex.getMessage();
//        }
    }

    public String displayHelp () {
        String helpString =
                "register <USERNAME> <PASSWORD> <EMAIL>" + SET_TEXT_COLOR_MAGENTA + " - to create an account"
                + SET_TEXT_COLOR_BLUE + "\n login <USERNAME> <PASSWORD>" + SET_TEXT_COLOR_MAGENTA + " - to play chess"
                + SET_TEXT_COLOR_BLUE + "\n quit" +  SET_TEXT_COLOR_MAGENTA + " - playing chess"
                + SET_TEXT_COLOR_BLUE + "\n help" + SET_TEXT_COLOR_MAGENTA + " - with possible commands";

        return helpString;

    }

    public void quitProgram () {

    }

    public void login () {
        // set state to logged in

    }

    public void register () {

    }


}
