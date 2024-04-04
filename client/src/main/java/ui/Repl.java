package ui;

import java.util.Scanner;
import java.util.TreeMap;

import static ui.EscapeSequences.*;
import static ui.ServerFacade.gameIDs;

public class Repl {
    public static State state;
    private final PreLoginUI preLogin;
    private final PostLoginUI postLogin;

    public Repl(String serverUrl) {
        preLogin = new PreLoginUI(serverUrl);
        postLogin = new PostLoginUI(serverUrl);
    }


    // command line text
    public void run () {
        Scanner scanner = new Scanner(System.in);
        var result = "";
        state = State.LOGGED_OUT;
        while (!result.equals("quit")) {
            printPrompt(); // waiting for user input
            String line = scanner.nextLine(); // take in user input, store in line
            try {
                if (state == State.LOGGED_OUT) {
                    result = preLogin.eval(line);
                }
                else {
                    result = postLogin.eval(line);
                }
                System.out.print(SET_TEXT_COLOR_BLUE + result + "\n");
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + "[" + state + "]" + ">>> " + SET_TEXT_COLOR_GREEN);
    }

}
