package ui;

import com.sun.nio.sctp.HandlerResult;
import com.sun.nio.sctp.Notification;
import com.sun.nio.sctp.NotificationHandler;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final State state = State.LOGGED_OUT;
    private final PreLoginUI preLogin;
    private final PostLoginUI postLogin;
    private final String loggedOutString = "[LOGGED_OUT]";
    private final String loggedInString = "[LOGGED_IN]";

    public Repl(String serverUrl) {
        preLogin = new PreLoginUI(serverUrl);
        postLogin = new PostLoginUI(serverUrl);
    }


    // command line text
    public void run () {
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt(); // waiting for user input
            String line = scanner.nextLine(); // take in user input, store in line
            try {
                result = preLogin.eval(line);
                System.out.print("\n" + SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
//        if (state == State.LOGGED_OUT) {
//            // TODO: go to prelogin
//            System.out.println(preLogin.displayHelp());
//
//        }
//        else {
//            // TODO: go to postlogin
//        }
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + "[" + state + "]" + ">>> " + SET_TEXT_COLOR_GREEN);
    }

}
